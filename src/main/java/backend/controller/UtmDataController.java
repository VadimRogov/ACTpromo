package backend.controller;

import backend.model.UtmData;
import backend.service.UtmDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utm")
public class UtmDataController {

    private UtmDataService utmDataService;

    public UtmDataController(UtmDataService utmDataService) {
        this.utmDataService = utmDataService;
    }

    @PostMapping("/save")
    public ResponseEntity<String> saveUtmData(
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

        // Сохраняем UTM-метки в базу данных
        utmDataService.saveUtmData(utmSource, utmMedium, utmCampaign, utmContent, utmTerm);

        return ResponseEntity.ok("UTM-метки успешно сохранены!");
    }

    // Метод для получения всех UTM-меток из базы данных
    @GetMapping("/list")
    public List<UtmData> getAllUtmData() {
        return utmDataService.getAllUtmData();
    }
}
