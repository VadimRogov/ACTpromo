package backend.service;

import backend.service.UtmDataService;
import backend.model.UtmData;
import backend.repository.UtmDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UtmDataService {

    private UtmDataRepository utmDataRepository;

    public UtmDataService(UtmDataRepository utmDataRepository) {
        this.utmDataRepository = utmDataRepository;
    }

    // Метод для сохранения UTM-меток (вызывается из другого места, например, фильтра или слушателя)
    public void saveUtmData(String utmSource, String utmMedium, String utmCampaign, String utmContent, String utmTerm) {
        UtmData utmData = new UtmData(utmSource, utmMedium, utmCampaign, utmContent, utmTerm);
        utmDataRepository.save(utmData);
    }

    // Метод для получения всех UTM-меток
    public List<UtmData> getAllUtmData() {
        return utmDataRepository.findAll();
    }
}
