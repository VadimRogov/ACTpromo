package backend.service;

import backend.model.Image;
import backend.repository.ImageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImageService {
    private final ImageRepository repository;

    public ImageService(ImageRepository repository) {
        this.repository = repository;
    }

    public List<Image> getAllImages() {
        return repository.findAll();
    }

    public Image getByIdImage(Long id) {
        return repository.findById(id).get();
    }

    public Image save(Image image) {
        return repository.save(image);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
