package backend.controller;

import backend.model.UserActivity;
import backend.service.UserActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "UserActivityController", description = "Контроллер для фиксирования активности на сайте")
@Controller
@RequestMapping("/api/activity")
public class UserActivityController {
    private final UserActivityService userActivityService;

    public UserActivityController(UserActivityService userActivityService) {
        this.userActivityService = userActivityService;
    }

    @Operation(summary = "Сохранение активности")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Активность пользователя успешно сохранена",
                    content = @Content(schema = @Schema(implementation = UserActivity.class))),
            @ApiResponse(
                    responseCode = "403",
                    description = "Доступ к запрошенному ресурсу запрещен",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<?> saveUserActivity(@RequestBody UserActivity userActivity) {
        return ResponseEntity.ok(userActivityService.logActivity(userActivity));
    }

    @Operation(summary = "Получение всех активностей всех пользователей")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешный запрос",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserActivity.class)))),
            @ApiResponse(
                    responseCode = "403",
                    description = "Доступ к запрошенному ресурсу запрещен",
                    content = @Content)
    })
    @GetMapping
    public ResponseEntity<?> getAllUserActivities() {
        return ResponseEntity.ok(userActivityService.getAllUserActivities());
    }

    @Operation(summary = "Получение всех активностей пользователя по IP")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешный запрос",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserActivity.class)))),
            @ApiResponse(
                    responseCode = "403",
                    description = "Доступ к запрошенному ресурсу запрещен",
                    content = @Content)
    })
    @GetMapping("/ip/{ip}")
    public ResponseEntity<List<UserActivity>> getUserActivityByIp(@PathVariable String ip) {
        List<UserActivity> activities = userActivityService.getUserActivityByIp(ip);
        return ResponseEntity.ok(activities);
    }
}
