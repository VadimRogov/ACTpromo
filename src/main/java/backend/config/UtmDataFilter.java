package backend.config;

import backend.service.UtmDataService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class UtmDataFilter implements Filter {

    private UtmDataService utmDataService;

    public UtmDataFilter(UtmDataService utmDataService) {
        this.utmDataService = utmDataService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // Извлекаем UTM-метки из заголовков
        String utmSource = httpRequest.getHeader("UTM-Source");
        String utmMedium = httpRequest.getHeader("UTM-Medium");
        String utmCampaign = httpRequest.getHeader("UTM-Campaign");
        String utmContent = httpRequest.getHeader("UTM-Content");
        String utmTerm = httpRequest.getHeader("UTM-Term");

        // Логирование UTM-меток
        System.out.println("UTM-Source: " + utmSource);
        System.out.println("UTM-Medium: " + utmMedium);
        System.out.println("UTM-Campaign: " + utmCampaign);
        System.out.println("UTM-Content: " + utmContent);
        System.out.println("UTM-Term: " + utmTerm);

        // Перенаправляем запрос на контроллер
        if (utmSource != null || utmMedium != null || utmCampaign != null) {
            utmDataService.saveUtmData(utmSource, utmMedium, utmCampaign, utmContent, utmTerm);
        }

        // Продолжаем выполнение запроса
        chain.doFilter(request, response);
    }
}