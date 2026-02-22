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

        // ✅ Step 1: Add CORS headers to EVERY response
        exchange.getResponse().getHeaders().add("Access-Control-Allow-Origin", "http://localhost:4200");
        exchange.getResponse().getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponse().getHeaders().add("Access-Control-Allow-Headers", "Authorization, Content-Type");
        exchange.getResponse().getHeaders().add("Access-Control-Allow-Credentials", "true");

        // ✅ Step 2: Let preflight OPTIONS requests pass through immediately
        if (exchange.getRequest().getMethod().name().equals("OPTIONS")) {
            exchange.getResponse().setStatusCode(HttpStatus.OK);
            return exchange.getResponse().setComplete();
        }

        String path = exchange.getRequest().getPath().toString();

        // Public endpoints
        if (path.contains("/auth") ||
                (path.contains("/restaurants") && exchange.getRequest().getMethod().name().equals("GET"))) {
            return chain.filter(exchange);
        }

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

        if (path.contains("/users") || path.contains("/orders/status")) {
            if (!"ROLE_ADMIN".equals(role)) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }
        }

        if (path.contains("/restaurants") && exchange.getRequest().getMethod().name().equals("POST")) {
            if (!"ROLE_RESTAURANT_ADMIN".equals(role)) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }
        }

        if (path.contains("/cart")) {
            if (!"ROLE_USER".equals(role)) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }
        }

        return chain.filter(exchange);
    }
}