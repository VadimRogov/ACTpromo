package backend.controller;

import backend.model.Book;
import backend.model.Image;
import backend.model.ImageType;
import backend.service.ImageService;
import backend.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "ImageController", description = "Контроллер сохранения и получения изображений")
@Controller
@RequestMapping("/api/images")
public class ImageController {
    private ImageService imageService;
    private JwtUtil jwtUtil;

    public ImageController(ImageService imageService, JwtUtil jwtUtil) {
        this.imageService = imageService;
        this.jwtUtil = jwtUtil;
    }

    @Operation(summary = "Получение изображения по его id")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешный запрос",
                    content = @Content(mediaType = MediaType.IMAGE_PNG_VALUE)),
            @ApiResponse(
                    responseCode = "404",
                    description = "Изображение не найдено",
                    content = @Content),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content)
    })
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> getImage(
            @Parameter(description = "ID изображения", required = true) @PathVariable("id") Long id) {
        try {

            // Получаем изображение из базы данных
            MultipartFile imageFile = imageService.getImageById(id);

            // Проверка существования изображения
            if (imageFile == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Изображение с ID " + id + " не найдено");
            }

            // Возвращаем изображение в ответе
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(imageFile.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + imageFile.getOriginalFilename() + "\"")
                    .body(imageFile.getBytes());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Изображение с ID " + id + " не найдено");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при чтении изображения: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Не удалось получить изображение: " + e.getMessage());
        }
    }



    @Operation(summary = "Сохранение изображения")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Изображение успешно сохранено",
                    content = @Content(schema = @Schema(implementation = Image.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректные параметры запроса",
                    content = @Content),
            @ApiResponse(
                    responseCode = "401",
                    description = "Недействительный или отсутствующий токен авторизации",
                    content = @Content),
            @ApiResponse(
                    responseCode = "403",
                    description = "Доступ к запрошенному ресурсу запрещен",
                    content = @Content),
            @ApiResponse(
                    responseCode = "404",
                    description = "Книга с указанным ID не найдена",
                    content = @Content),
            @ApiResponse(
                    responseCode = "415",
                    description = "Неподдерживаемый формат файла",
                    content = @Content),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content)
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createImage(
            @RequestHeader("Authorization") @Parameter(description = "Токен авторизации", required = true) String authorizationHeader,
            @Parameter(description = "Id книги") @RequestParam("bookId") Long bookId,
            @Parameter(description = "Название файла") @RequestParam("fileName") String fileName,
            @Parameter(description = "Тип изображения") @RequestParam("imageType") ImageType imageType,
            @Parameter(description = "Файл изображения") @RequestParam("file") MultipartFile file) {

        // Проверка токена авторизации
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Недействительный или отсутствующий токен авторизации");
        }

        String token = authorizationHeader.substring(7);
        String username = jwtUtil.getUsernameFromToken(token);

        // Проверка роли пользователя
        if (!"admin".equals(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Доступ запрещен");
        }

        // Проверка корректности параметров
        if (bookId == null || fileName == null || imageType == null || file == null || file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Некорректные параметры запроса");
        }

        // Проверка поддерживаемого формата файла
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("Неподдерживаемый формат файла");
        }

        try {
            // Проверка существования книги
            if (!imageService.isExistBook(bookId)) {
                throw new EntityNotFoundException("Книга с ID " + bookId + " не найдена");
            }

            // Сохранение изображения
            Image savedImage = imageService.createImage(bookId, fileName, imageType, file);
            return ResponseEntity.ok(savedImage);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Не удалось сохранить изображение: " + e.getMessage());
        }
    }


    @Operation(summary = "Удаление изображения")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Изображение удалено",
                    content = @Content),
            @ApiResponse(
                    responseCode = "401",
                    description = "Недействительный или отсутствующий токен авторизации",
                    content = @Content),
            @ApiResponse(
                    responseCode = "403",
                    description = "Доступ к ресурсу запрещен",
                    content = @Content),
            @ApiResponse(
                    responseCode = "404",
                    description = "Изображение с указанным ID не найдено",
                    content = @Content),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteImage(
            @RequestHeader("Authorization") @Parameter(description = "Токен авторизации", required = true) String authorizationHeader,
            @PathVariable Long id) {

        // Проверка токена авторизации
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Недействительный или отсутствующий токен авторизации");
        }

        String token = authorizationHeader.substring(7);
        String username = jwtUtil.getUsernameFromToken(token);

        // Проверка роли пользователя
        if (!"admin".equals(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Доступ запрещен");
        }

        try {
            // Удаление изображения
            imageService.deleteById(id);
            return ResponseEntity.ok().body("Изображение успешно удалено");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Изображение с ID " + id + " не найдено");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Не удалось удалить изображение: " + e.getMessage());
        }
    }
}
