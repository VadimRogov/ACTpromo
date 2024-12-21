package backend.controller;

import backend.model.UtmData;
import backend.service.UtmDataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utm")
public class UtmDataController {

    private final UtmDataService utmDataService;

    public UtmDataController(UtmDataService utmDataService) {
        this.utmDataService = utmDataService;
    }

    @PostMapping("/save")
    public ResponseEntity<String> saveUtmData(
            @RequestParam(value = "utm_source", required = false) String utmSource,
            @RequestParam(value = "utm_medium", required = false) String utmMedium,
            @RequestParam(value = "utm_campaign", required = false) String utmCampaign,
            @RequestParam(value = "utm_content", required = false) String utmContent,
            @RequestParam(value = "utm_term", required = false) String utmTerm
    ) {
        // Логирование UTM-меток
        System.out.println("UTM-Source: " + utmSource);
        System.out.println("UTM-Medium: " + utmMedium);
        System.out.println("UTM-Campaign: " + utmCampaign);
        System.out.println("UTM-Content: " + utmContent);
        System.out.println("UTM-Term: " + utmTerm);

        // Сохраняем UTM-метки в базу данных
        try {
            utmDataService.saveUtmData(utmSource,
                    utmMedium,
                    utmCampaign,
                    utmContent,
                    utmTerm);
            return ResponseEntity.ok("UTM-метки успешно сохранены!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при сохранении UTM-меток.");
        }
    }

    // Метод для получения всех UTM-меток из базы данных
    @GetMapping("/list")
    public List<UtmData> getAllUtmData() {
        return utmDataService.getAllUtmData();
    }
}
