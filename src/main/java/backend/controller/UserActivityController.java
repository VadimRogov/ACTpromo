package backend.controller;

import backend.model.UserActivity;
import backend.service.UserActivityService;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "UserActivityController", description = "Контроллер для фиксирования активности на сайте")
@Controller
@RequestMapping("/api/activity")
public class UserActivityController {
    private final UserActivityService userActivityService;
    private final JwtUtil jwtUtil;

    public UserActivityController(UserActivityService userActivityService, JwtUtil jwtUtil) {
        this.userActivityService = userActivityService;
        this.jwtUtil = jwtUtil;
    }

    // Метод для создания JSON-ответа об ошибке
    private Map<String, String> createErrorResponse(String message, String details) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", message);
        if (details != null) {
            errorResponse.put("details", details);
        }
        return errorResponse;
    }

    @Operation(summary = "Сохранение активности")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Активность пользователя успешно сохранена",
                    content = @Content(schema = @Schema(implementation = UserActivity.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректные параметры запроса",
                    content = @Content),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<?> saveUserActivity(@RequestBody List<UserActivity> userActivities) {
        try {
            // Проверка корректности параметров
            if (userActivities == null || userActivities.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("Некорректные параметры запроса", "Список активностей не может быть пустым"));
            }

            // Валидация каждой активности в списке
            for (UserActivity activity : userActivities) {
                if (activity.getUserIp() == null || activity.getEventType() == null ||
                        activity.getEventType().equals("") || activity.getTimestamp() == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(createErrorResponse("Некорректные параметры запроса", "IP, тип события и временная метка не могут быть пустыми"));
                }
            }

            // Сохранение активностей
            List<UserActivity> savedActivities = userActivityService.logActivity(userActivities);
            return ResponseEntity.ok(savedActivities);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Внутренняя ошибка сервера", e.getMessage()));
        }
    }

    @Operation(summary = "Получение всех активностей всех пользователей")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешный запрос",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserActivity.class)))),
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
    @GetMapping
    public ResponseEntity<?> getAllUserActivities(
            @RequestHeader("Authorization") @Parameter(description = "Токен авторизации", required = true) String authorizationHeader) {

        try {
            String token = authorizationHeader.substring(7); // Убираем "Bearer "
            String username = jwtUtil.getUsernameFromToken(token);

            if (!"admin".equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(createErrorResponse("Доступ запрещен", "Только администратор может получить данные"));
            }

            return ResponseEntity.ok(userActivityService.getAllUserActivities());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Внутренняя ошибка сервера", e.getMessage()));
        }
    }

    @Operation(summary = "Получение всех активностей пользователя по IP")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешный запрос",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserActivity.class)))),
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
                    description = "Активности для указанного IP не найдены",
                    content = @Content),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content)
    })
    @GetMapping("/ip/{ip}")
    public ResponseEntity<?> getUserActivityByIp(
            @RequestHeader("Authorization") @Parameter(description = "Токен авторизации", required = true) String authorizationHeader,
            @PathVariable String ip) {

        try {
            String token = authorizationHeader.substring(7);
            String username = jwtUtil.getUsernameFromToken(token);

            if (!"admin".equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(createErrorResponse("Доступ запрещен", "Только администратор может получить данные"));
            }

            List<UserActivity> activities = userActivityService.getUserActivityByIp(ip);
            if (activities.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("Активности не найдены", "Для IP " + ip + " активности отсутствуют"));
            }

            return ResponseEntity.ok(activities);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Внутренняя ошибка сервера", e.getMessage()));
        }
    }
}