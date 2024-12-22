package backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "images")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    @JsonIgnore
    private Book book;

    @Column(name = "fileName", nullable = true)
    private String fileName;

    @Enumerated(EnumType.STRING)
    @Column(name = "image_type", nullable = false)
    private ImageType imageType;

    @Lob
    @Column(name = "image_data", nullable = false)
    private byte[] imageData;

    public Image() {}

    public Image(Book book, String fileName, ImageType imageType, byte[] imageData) {
        this.book = book;
        this.fileName = fileName;
        this.imageType = imageType;
        this.imageData = imageData;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) { // Изменено с int на Long
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public ImageType getImageType() {
        return imageType;
    }

    public void setImageType(ImageType imageType) {
        this.imageType = imageType;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    // Метод builder()
    public static Builder builder() {
        return new Builder();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    // Вложенный класс Builder
    public static class Builder {
        private Long id;
        private Book book;
        private String fileName;
        private ImageType imageType;
        private byte[] imageData;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder book(Book book) {
            this.book = book;
            return this;
        }

        public Builder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder imageType(ImageType imageType) {
            this.imageType = imageType;
            return this;
        }

        public Builder imageData(byte[] imageData) {
            this.imageData = imageData;
            return this;
        }

        public Image build() {
            Image image = new Image();
            image.setId(id);
            image.setBook(book);
            image.setFileName(fileName);
            image.setImageType(imageType);
            image.setImageData(imageData);
            return image;
        }
    }
}
