package com.api_gateway.filter;

import com.api_gateway.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        // ── Step 1: CORS headers ──────────────────────────────────────────────
        exchange.getResponse().getHeaders().add("Access-Control-Allow-Origin",      "http://localhost:4200");
        exchange.getResponse().getHeaders().add("Access-Control-Allow-Methods",     "GET, POST, PUT, PATCH, DELETE, OPTIONS");
        exchange.getResponse().getHeaders().add("Access-Control-Allow-Headers",     "Authorization, Content-Type");
        exchange.getResponse().getHeaders().add("Access-Control-Allow-Credentials", "true");

        // ── Step 2: Preflight ─────────────────────────────────────────────────
        if (exchange.getRequest().getMethod().name().equals("OPTIONS")) {
            exchange.getResponse().setStatusCode(HttpStatus.OK);
            return exchange.getResponse().setComplete();
        }

        String path   = exchange.getRequest().getPath().toString();
        String method = exchange.getRequest().getMethod().name();

        // ── Step 3: Public endpoints ──────────────────────────────────────────
        if (path.contains("/auth") ||
                (path.contains("/restaurants") && method.equals("GET"))) {
            return chain.filter(exchange);
        }

        // ── Step 4: Validate JWT ──────────────────────────────────────────────
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String role = jwtUtil.extractRole(token);

        // ── Step 5: Role-based access rules ───────────────────────────────────

        // ROLE_RESTAURANT_ADMIN — user management
        if (path.contains("/users")) {
            if (!"ROLE_RESTAURANT_ADMIN".equals(role)) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }
        }

        // ROLE_RESTAURANT_ADMIN — order status updates
        // ✅ Fixed: was checking /orders/status which never matched PUT /{orderId}/status
        if (path.matches(".*/orders/\\d+/status.*")) {
            if (!"ROLE_RESTAURANT_ADMIN".equals(role)) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }
        }

        // ROLE_RESTAURANT_ADMIN — create, update, delete restaurants & menu items
        if (path.contains("/restaurants") &&
                (method.equals("POST") || method.equals("PATCH") || method.equals("DELETE"))) {
            if (!"ROLE_RESTAURANT_ADMIN".equals(role)) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }
        }

        // ROLE_USER only — cart operations
        if (path.contains("/cart")) {
            if (!"ROLE_USER".equals(role)) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }
        }

        return chain.filter(exchange);
    }
}