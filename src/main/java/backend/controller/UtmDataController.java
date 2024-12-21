package backend.controller;

import backend.model.UtmData;
import backend.service.UtmDataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/utm")
public class UtmDataController {

    private UtmDataService utmDataService;

    public UtmDataController(UtmDataService utmDataService) {
        this.utmDataService = utmDataService;
    }

    // Метод для получения всех UTM-меток из базы данных
    @GetMapping("/list")
    public List<UtmData> getAllUtmData() {
        return utmDataService.getAllUtmData();
    }
}
