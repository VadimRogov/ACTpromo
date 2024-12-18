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
@RequestMapping("/api/comment")
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
                    content = @Content)
    })
    @GetMapping
    public ResponseEntity<?> getAllComments() {
        return new ResponseEntity<>(commentService.getAllComments(), HttpStatus.OK);
    }


    @Operation(summary = "Получить комментарий по ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Комментарий получен",
                    content = @Content(schema = @Schema(implementation = Comment.class))),
            @ApiResponse(
                    responseCode = "403",
                    description = "Доступ к ресурсу запрещен",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getCommentById(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.getCommentById(id));
    }

    @Operation(summary = "Добавить комментарий")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Комментарий добавлен",
                    content = @Content(schema = @Schema(implementation = Comment.class))),
            @ApiResponse(
                    responseCode = "403",
                    description = "Доступ к ресурсу запрещен",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<?> addComment(
            @RequestHeader("Authorization") @Parameter(description = "Токен авторизации", required = true) String authorizationHeader,
            @RequestBody Comment comment) {

        String token = authorizationHeader.substring(7);
        String username = jwtUtil.getUsernameFromToken(token);

        return new ResponseEntity<>(commentService.addComment(comment), HttpStatus.CREATED);
    }

    @Operation(summary = "Удалить комментарии")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Комментарий был удален",
                    content = @Content),
            @ApiResponse(
                    responseCode = "403",
                    description = "Доступ к ресурсу запрещен",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCommentById(@PathVariable Long id) {
        commentService.deleteCommentById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
