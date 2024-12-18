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
    public ResponseEntity<?> saveUserActivity(@RequestHeader("Authorization")
                                                  @Parameter(description = "Токен авторизации", required = true) String authorizationHeader,
            @RequestBody UserActivity userActivity) {

        String token = authorizationHeader.substring(7); // Убираем "Bearer "
        String username = jwtUtil.getUsernameFromToken(token);

        if ("admin".equals(username)) {
            return ResponseEntity.ok(userActivityService.logActivity(userActivity));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
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
    public ResponseEntity<?> getAllUserActivities(
            @RequestHeader("Authorization") @Parameter(description = "Токен авторизации", required = true) String authorizationHeader) {

        String token = authorizationHeader.substring(7); // Убираем "Bearer "
        String username = jwtUtil.getUsernameFromToken(token);

        if ("admin".equals(username)) {
            return ResponseEntity.ok(userActivityService.getAllUserActivities());
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
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
    public ResponseEntity<List<UserActivity>> getUserActivityByIp(
            @RequestHeader("Authorization") @Parameter(description = "Токен авторизации", required = true) String authorizationHeader,
            @PathVariable String ip) {

        String token = authorizationHeader.substring(7);
        String username = jwtUtil.getUsernameFromToken(token);
        if ("admin".equals(username)) {
            List<UserActivity> activities = userActivityService.getUserActivityByIp(ip);
            return ResponseEntity.ok(activities);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
