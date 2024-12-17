package backend.config;

import backend.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil = new JwtUtil();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // Проверяем, является ли запрос к Swagger UI или OpenAPI документации
        String path = request.getRequestURI();
        if (isSwaggerPath(path) || isOpenApiPath(path)) {
            chain.doFilter(request, response); // Пропускаем запросы к Swagger и OpenAPI
            return;
        }

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            username = jwtUtil.getUsernameFromToken(jwt);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    username, null, null);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        chain.doFilter(request, response);
    }

    // Метод для проверки, является ли путь Swagger UI
    private boolean isSwaggerPath(String path) {
        return path.startsWith("/backend/swagger-ui") || path.startsWith("/swagger-ui");
    }

    // Метод для проверки, является ли путь OpenAPI документацией
    private boolean isOpenApiPath(String path) {
        return path.startsWith("/v3/api-docs") || path.startsWith("/api-docs");
    }
}