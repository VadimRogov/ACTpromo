package backend.controller;

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

import java.util.List;
import java.util.logging.Logger;

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

    private static final Logger logger = Logger.getLogger(ImageController.class.getName());

    /*
    @Operation(summary = "Получение всех изображений")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешный запрос",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(
                    responseCode = "403",
                    description = "Доступ к запрошенному ресурсу запрещен",
                    content = @Content)
    })

    @GetMapping
    @ResponseBody
    public ResponseEntity<?> getAllImages() {
        try {
            // Получаем все изображения из базы данных
            List<MultipartFile> imageFiles = imageService.getByIdImage();

            // Возвращаем список изображений в ответе
            return ResponseEntity.ok(imageFiles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve images: " + e.getMessage());
        }
    }
    */
    @Operation(summary = "Получение изображения по его id")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешный запрос",
                    content = @Content(mediaType = MediaType.IMAGE_PNG_VALUE)),
            @ApiResponse(
                    responseCode = "403",
                    description = "Доступ к запрошенному ресурсу запрещен",
                    content = @Content)
    })
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> getImage(
            @Parameter(description = "ID изображения", required = true) @PathVariable("id") Long id) {
        try {
            // Получаем изображение из базы данных
            MultipartFile imageFile = imageService.getImageById(id);

            // Возвращаем изображение в ответе
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(imageFile.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + imageFile.getOriginalFilename() + "\"")
                    .body(imageFile.getBytes());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Image not found with ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve image: " + e.getMessage());
        }
    }



    @Operation(summary = "Сохранение изображения")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Изображение успешно сохранено",
                    content = @Content(schema = @Schema(implementation = Image.class))),
            @ApiResponse(
                    responseCode = "403",
                    description = "Доступ к запрошенному ресурсу запрещен",
                    content = @Content)
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createImage(
            @Parameter(description = "Id книги") @RequestParam("bookId") Long bookId,
            @Parameter(description = "Название файла") @RequestParam("fileName") String fileName,
            @Parameter(description = "Тип изображения") @RequestParam("imageType") ImageType imageType,
            @Parameter(description = "Файл изображения") @RequestParam("file") MultipartFile file) {
            logger.info("ImageController: createImage");
        return ResponseEntity.ok(imageService.createImage(bookId, fileName, imageType, file));
    }




    @Operation(summary = "Удаление изображения")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Изображение удалено",
                    content = @Content),
            @ApiResponse(
                    responseCode = "403",
                    description = "Доступ к ресурсу запрещен",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteImage(
            @RequestHeader("Authorization") @Parameter(description = "Токен авторизации", required = true) String authorizationHeader,
            @PathVariable Long id) {

        String token = authorizationHeader.substring(7);
        String username = jwtUtil.getUsernameFromToken(token);
        try {
            imageService.deleteById(id);
            return ResponseEntity.ok().build();
        }catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
