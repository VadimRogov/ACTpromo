package backend.service;

import backend.model.Book;
import backend.model.Image;
import backend.model.ImageType;
import backend.repository.BookRepository;
import backend.repository.ImageRepository;
import backend.utils.CustomMultipartFile;
import jakarta.activation.MimetypesFileTypeMap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

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
                throw new IllegalArgumentException("Файл пустой");
            }
            // Находим книгу по ID
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new IllegalArgumentException("Книга не найдена с ID: " + bookId));

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
            throw new RuntimeException("Не удалось сохранить картинку", e);
        }
    }
    @Transactional
    // Метод для получения изображения по ID
    public MultipartFile getImageById(Long id) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Картинка с ID не найдена: " + id));

        // Создаем MultipartFile из бинарных данных изображения
        return new CustomMultipartFile(
                image.getFileName(), // Используем имя файла из базы данных
                image.getImageData(),
                "image/jpeg"
        );
    }

    @Transactional
    public void deleteById(Long id) {
        imageRepository.deleteById(id);
    }

    public boolean isExist(Long id) {
        return imageRepository.existsById(id);
    }

    public boolean isExistBook (Long id) {
        return bookRepository.existsById(id);
    }
}
