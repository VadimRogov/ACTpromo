package backend.controller;

import backend.model.UtmData;
import backend.service.UtmDataService;
import jakarta.servlet.http.HttpServletRequest;
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

    @GetMapping("/save")
    public ResponseEntity<String> saveUtmData(HttpServletRequest request) {
        // Извлекаем UTM-метки из URL
        String utmSource = request.getParameter("utm_source");
        String utmMedium = request.getParameter("utm_medium");
        String utmCampaign = request.getParameter("utm_campaign");
        String utmContent = request.getParameter("utm_content");
        String utmTerm = request.getParameter("utm_term");

        // Логирование UTM-меток
        System.out.println("UTM-Source: " + utmSource);
        System.out.println("UTM-Medium: " + utmMedium);
        System.out.println("UTM-Campaign: " + utmCampaign);
        System.out.println("UTM-Content: " + utmContent);
        System.out.println("UTM-Term: " + utmTerm);

        // Сохраняем UTM-метки в базу данных
        try {
            utmDataService.saveUtmData(utmSource, utmMedium, utmCampaign, utmContent, utmTerm);
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
