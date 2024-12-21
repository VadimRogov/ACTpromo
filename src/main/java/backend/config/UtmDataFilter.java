package backend.config;

import backend.service.UtmDataService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class UtmDataFilter extends OncePerRequestFilter {

    @Autowired
    private UtmDataService utmDataService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // Извлекаем UTM-метки из запроса
        String utmSource = request.getParameter("utm_source");
        String utmMedium = request.getParameter("utm_medium");
        String utmCampaign = request.getParameter("utm_campaign");
        String utmContent = request.getParameter("utm_content");
        String utmTerm = request.getParameter("utm_term");

        // Логируем UTM-метки
        logger.info("UTM-Source: " + utmSource);
        logger.info("UTM-Medium: " + utmMedium);
        logger.info("UTM-Campaign: " + utmCampaign);
        logger.info("UTM-Content: " + utmContent);
        logger.info("UTM-Term: " + utmTerm);

        // Сохраняем UTM-метки в базу данных
        utmDataService.saveUtmData(utmSource, utmMedium, utmCampaign, utmContent, utmTerm);

        // Продолжаем выполнение цепочки фильтров
        chain.doFilter(request, response);
    }
}