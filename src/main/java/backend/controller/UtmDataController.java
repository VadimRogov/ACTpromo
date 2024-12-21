package backend.controller;

import backend.service.UtmDataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/utm")
public class UtmDataController {

    private UtmDataService utmDataService;

    public UtmDataController(UtmDataService utmDataService) {
        this.utmDataService = utmDataService;
    }

    @GetMapping("/track-utm")
    public Map<String, String> trackUtm(
            @RequestHeader(value = "UTM-Source", required = false) String utmSource,
            @RequestHeader(value = "UTM-Medium", required = false) String utmMedium,
            @RequestHeader(value = "UTM-Campaign", required = false) String utmCampaign,
            @RequestHeader(value = "UTM-Content", required = false) String utmContent,
            @RequestHeader(value = "UTM-Term", required = false) String utmTerm
    ) {
        // Логирование UTM-меток
        System.out.println("UTM-Source: " + utmSource);
        System.out.println("UTM-Medium: " + utmMedium);
        System.out.println("UTM-Campaign: " + utmCampaign);
        System.out.println("UTM-Content: " + utmContent);
        System.out.println("UTM-Term: " + utmTerm);

        // Сохраняем UTM-метки
        utmDataService.saveUtmData(utmSource, utmMedium, utmCampaign, utmContent, utmTerm);

        // Возвращаем UTM-метки в ответе
        Map<String, String> utmData = new HashMap<>();
        utmData.put("utm_source", utmSource);
        utmData.put("utm_medium", utmMedium);
        utmData.put("utm_campaign", utmCampaign);
        utmData.put("utm_content", utmContent);
        utmData.put("utm_term", utmTerm);

        return utmData;
    }
}
