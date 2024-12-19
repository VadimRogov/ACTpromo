package backend.controller;

import backend.model.Book;
import backend.utils.JwtUtil;
import backend.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Tag(name = "BookController", description = "Контроллер получения, сохранения и удаления книг")
@RestController
@RequestMapping("/api/books")
public class BookController {

    private BookService bookService;
    private JwtUtil jwtUtil;

    public BookController(BookService bookService, JwtUtil jwtUtil) {
        this.bookService = bookService;
        this.jwtUtil = jwtUtil;
    }


    @Operation(summary = "Получение всех книг")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Список всех книг получен",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Book.class)))),
            @ApiResponse(
                    responseCode = "403",
                    description = "Доступ к запрошенному ресурсу запрещен",
                    content = @Content),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content)
    })
    @GetMapping
    public ResponseEntity<?> getAllBooks() {
        try {
            return ResponseEntity.ok(bookService.getAllBooks());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при получении списка книг: " + e.getMessage());
        }
    }

    @Operation(summary = "Получение книги по ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Книга по ID получена",
                    content = @Content(schema = @Schema(implementation = Book.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Книга не найдена",
                    content = @Content),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        try {

            // Получение книги по ID
            Optional<Book> bookOptional = bookService.getBookById(id);

            // Если книга не найдена, возвращаем 404
            if (bookOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // Возвращаем книгу с кодом 200
            return ResponseEntity.ok(bookOptional.get());
        } catch (Exception e) {
            // В случае ошибки возвращаем 500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Сохранение книги")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Книга успешно сохранена",
                    content = @Content(schema = @Schema(implementation = Book.class))),
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
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<Book> addBook(
            @RequestHeader("Authorization") @Parameter(description = "Токен авторизации", required = true) String authorizationHeader,
            @RequestBody Book book) {

        try {
            String token = authorizationHeader.substring(7); // Убираем "Bearer "
            String username = jwtUtil.getUsernameFromToken(token);

            // Проверка токена авторизации
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }



            // Проверка роли пользователя
            if (!"admin".equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // Проверка корректности параметров
            if (book == null || book.getTitle() == null || book.getTitle().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            return ResponseEntity.ok(bookService.addBook(book));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Удаление книги")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Книга удалена",
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
                    description = "Книга не найдена",
                    content = @Content),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(
            @RequestHeader("Authorization") @Parameter(description = "Токен авторизации", required = true) String authorizationHeader,
            @PathVariable Long id) {

        try {
            // Проверка токена авторизации
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String token = authorizationHeader.substring(7);
            String username = jwtUtil.getUsernameFromToken(token);

            // Проверка роли пользователя
            if (!"admin".equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // Проверка существования книги
            if (!bookService.isExistingBook(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Книга с ID " + id + " не найдена");
            }

            bookService.deleteBook(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при удалении книги: " + e.getMessage());
        }
    }
}
