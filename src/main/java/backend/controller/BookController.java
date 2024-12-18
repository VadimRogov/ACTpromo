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
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Tag(name = "BookController", description = "Контроллер получения, сохранения и удаления книг")
@Controller
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;
    private final JwtUtil jwtUtil;

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
                    content = @Content)
    })
    @GetMapping
    public ResponseEntity<?>getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @Operation(summary = "Получение книги по ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Книга по ID получена",
                    content = @Content(schema = @Schema(implementation = Book.class))),
            @ApiResponse(
                    responseCode = "403",
                    description = "Доступ к запрошенному ресурсу запрещен",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getBookById(@PathVariable Long id) {
        return bookService.getBookById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Сохранение изображения")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Книга успешно сохранена",
                    content = @Content(schema = @Schema(implementation = Book.class))),
            @ApiResponse(
                    responseCode = "403",
                    description = "Доступ к запрошенному ресурсу запрещен",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<Book> addBook(
            @RequestHeader("Authorization") @Parameter(description = "Токен авторизации", required = true) String authorizationHeader,
            @RequestBody Book book) {

        String token = authorizationHeader.substring(7); // Убираем "Bearer "
        String username = jwtUtil.getUsernameFromToken(token);

        if ("admin".equals(username)) {
            return ResponseEntity.ok(bookService.addBook(book));
        } else {
            return ResponseEntity.status(403).build();
        }
    }

    @Operation(summary = "Книга удалена")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Книга удалена",
                    content = @Content),
            @ApiResponse(
                    responseCode = "403",
                    description = "Доступ к запрошенному ресурсу запрещен",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(
            @RequestHeader("Authorization") @Parameter(description = "Токен авторизации", required = true) String authorizationHeader,
            @PathVariable Long id) {

        String token = authorizationHeader.substring(7);
        String username = jwtUtil.getUsernameFromToken(token);

        if ("admin".equals(username)) {
            try {
                bookService.deleteBook(id);
                return ResponseEntity.ok().build();
            } catch (EntityNotFoundException e) {
                return ResponseEntity.notFound().build();
            } catch (Exception e) {
                return ResponseEntity.status(500).body("An error occurred: " + e.getMessage());
            }
        }else return ResponseEntity.status(403).build();
    }
}
