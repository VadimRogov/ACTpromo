package backend.config;

import backend.service.UtmDataService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class UtmDataFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(UtmDataFilter.class);

    @Autowired
    private UtmDataService utmDataService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        logger.info("UtmDataFilter: Processing request for URL: {}", request.getRequestURL());

        // Пропускаем запросы к Swagger UI, OpenAPI и другим нерелевантным маршрутам
        String path = request.getRequestURI();
        if (isSwaggerPath(path) || isOpenApiPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        // Извлекаем UTM-метки из запроса
        String utmSource = request.getParameter("utm_source");
        String utmMedium = request.getParameter("utm_medium");
        String utmCampaign = request.getParameter("utm_campaign");
        String utmContent = request.getParameter("utm_content");
        String utmTerm = request.getParameter("utm_term");

        // Логируем UTM-метки
        logger.info("UTM-Source: {}", utmSource);
        logger.info("UTM-Medium: {}", utmMedium);
        logger.info("UTM-Campaign: {}", utmCampaign);
        logger.info("UTM-Content: {}", utmContent);
        logger.info("UTM-Term: {}", utmTerm);

        // Сохраняем UTM-метки в базу данных
        utmDataService.saveUtmData(utmSource, utmMedium, utmCampaign, utmContent, utmTerm);

        // Продолжаем выполнение цепочки фильтров
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