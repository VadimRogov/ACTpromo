package backend.config;

import backend.service.UtmDataService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class UtmDataFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(UtmDataFilter.class);
    private final UtmDataService utmDataService;

    public UtmDataFilter(UtmDataService utmDataService) {
        this.utmDataService = utmDataService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // Извлекаем UTM-метки из параметров URL
        String utmSource = httpRequest.getParameter("utm_source");
        String utmMedium = httpRequest.getParameter("utm_medium");
        String utmCampaign = httpRequest.getParameter("utm_campaign");
        String utmContent = httpRequest.getParameter("utm_content");
        String utmTerm = httpRequest.getParameter("utm_term");

        // Логирование UTM-меток
        logger.info("UTM-Source: {}", utmSource);
        logger.info("UTM-Medium: {}", utmMedium);
        logger.info("UTM-Campaign: {}", utmCampaign);
        logger.info("UTM-Content: {}", utmContent);
        logger.info("UTM-Term: {}", utmTerm);

        // Сохраняем UTM-метки в базе данных
        if (utmSource != null || utmMedium != null ||
                utmCampaign != null || utmContent != null ||
                utmTerm != null) {
            try {
                utmDataService.saveUtmData(utmSource, utmMedium,
                        utmCampaign,
                        utmContent,
                        utmTerm);
            } catch (Exception e) {
                logger.error("Ошибка при сохранении UTM-данных", e);
            }
        }

        // Продолжаем выполнение запроса
        chain.doFilter(request, response);
    }
}