package backend.service;

import backend.model.Book;
import backend.model.Image;
import backend.model.ImageType;
import backend.repository.BookRepository;
import backend.repository.ImageRepository;
import backend.utils.CustomMultipartFile;
import jakarta.activation.MimetypesFileTypeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImageService {
    private final ImageRepository imageRepository;
    private final BookRepository bookRepository;

    public ImageService(ImageRepository imageRepository, BookRepository bookRepository) {
        this.imageRepository = imageRepository;
        this.bookRepository = bookRepository;
    }

    private final MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();

    @Transactional
    public Image createImage(Long bookId, String fileName, ImageType imageType, MultipartFile file) {
        try {
            // Проверяем, что файл не пустой
            if (file.isEmpty()) {
                throw new IllegalArgumentException("File is empty");
            }
            // Находим книгу по ID
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + bookId));

            // Создаем объект Image с использованием Builder
            Image image = Image.builder()
                    .book(book)
                    .fileName(fileName)
                    .imageType(imageType)
                    .imageData(file.getBytes())
                    .build();
            // Сохраняем изображение в базу данных
            return imageRepository.save(image);

        } catch (IOException e) {
            throw new RuntimeException("Failed to save image", e);
        }
    }
    @Transactional
    // Метод для получения изображения по ID
    public MultipartFile getImageById(Long id) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found with ID: " + id));

        // Создаем MultipartFile из бинарных данных изображения
        return new CustomMultipartFile(
                image.getFileName(), // Используем имя файла из базы данных
                image.getImageData(),
                "image/jpeg" // MIME-тип (можно изменить на соответствующий)
        );
    }
    @Transactional
    // Метод для получения всех изображений
    public List<MultipartFile> getAllImages() {
        List<Image> images = imageRepository.findAll();
        List<MultipartFile> result = new ArrayList<>();
        for (Image image : images) {
            result.add(getImageById(image.getId()));
        }
        return result;
    }

    @Transactional
    public List<MultipartFile> getImagesByBookId(Long bookId) {
        List<Image> images = imageRepository.findByBookId(bookId);

        return images.stream()
                .map(image -> {
                    String mimeType = mimeTypesMap.getContentType(image.getFileName()); // Определяем MIME-тип
                    return new CustomMultipartFile(
                            image.getFileName(),
                            image.getImageData(),
                            mimeType
                    );
                })
                .collect(Collectors.toList());
    }

    public Image getByIdImage(Long id) {
        return imageRepository.findById(id).get();
    }

    public void deleteById(Long id) {
        imageRepository.deleteById(id);
    }
}
