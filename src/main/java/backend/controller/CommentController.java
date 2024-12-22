package backend.controller;

import backend.model.Comment;
import backend.service.CommentService;
import backend.utils.JwtUtil;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Tag(name = "CommentController", description = "Контроллер для управления комментариями")
@Controller
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;
    private final JwtUtil jwtUtil;

    public CommentController(CommentService commentService, JwtUtil jwtUtil) {
        this.commentService = commentService;
        this.jwtUtil = jwtUtil;
    }


    @Operation(summary = "Получить все комментарии")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Комментарии получены",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Comment.class)))),
            @ApiResponse(
                    responseCode = "403",
                    description = "Доступ к ресурсу запрещен",
                    content = @Content),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content)
    })
    @GetMapping
    public ResponseEntity<?> getAllComments() {
        try {
            return new ResponseEntity<>(commentService.getAllComments(), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при получении комментариев: " + e.getMessage());
        }
    }

    @Operation(summary = "Получить комментарий по ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Комментарий получен",
                    content = @Content(schema = @Schema(implementation = Comment.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Комментарий не найден",
                    content = @Content),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getCommentById(@PathVariable Long id) {
        try {
            Comment comment = commentService.getCommentById(id);
            if (comment == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Комментарий с ID " + id + " не найден");
            }
            return ResponseEntity.ok(comment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при получении комментария: " + e.getMessage());
        }
    }

    @Operation(summary = "Добавить комментарий")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Комментарий добавлен",
                    content = @Content(schema = @Schema(implementation = Comment.class))),
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
                    description = "Доступ к ресурсу запрещен",
                    content = @Content),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<?> addComment(
            @RequestHeader("Authorization") @Parameter(description = "Токен авторизации", required = true) String authorizationHeader,
            @RequestBody Comment comment) {

        try {
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
            if (comment == null || comment.getContent() == null || comment.getContent().isEmpty()
            || comment.getAuthor() == null || comment.getAuthor().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Некорректные параметры запроса");
            }

            Comment savedComment = commentService.addComment(comment);
            return new ResponseEntity<>(savedComment, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при добавлении комментария: " + e.getMessage());
        }
    }

    @Operation(summary = "Удалить комментарий")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Комментарий удален",
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
                    description = "Комментарий не найден",
                    content = @Content),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCommentById(
            @RequestHeader("Authorization") @Parameter(description = "Токен авторизации", required = true) String authorizationHeader,
            @PathVariable Long id) {

        try {
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

            // Проверка существования комментария
            if (!commentService.isExist(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Комментарий с ID " + id + " не найден");
            }

            commentService.deleteCommentById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при удалении комментария: " + e.getMessage());
        }
    }
}
