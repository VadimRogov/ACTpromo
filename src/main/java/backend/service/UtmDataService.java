package backend.service;

import backend.service.UtmDataService;
import backend.model.UtmData;
import backend.repository.UtmDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UtmDataService {

    @Autowired
    private UtmDataRepository utmDataRepository;

    // Метод для сохранения UTM-меток
    public void saveUtmData(String utmSource, String utmMedium, String utmCampaign, String utmContent, String utmTerm) {
        UtmData utmData = new UtmData(utmSource, utmMedium, utmCampaign, utmContent, utmTerm);
        utmDataRepository.save(utmData);
    }
}
