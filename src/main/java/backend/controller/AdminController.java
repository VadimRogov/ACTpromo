package backend.controller;

import backend.utils.JwtUtil;
import backend.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AdminController", description = "Контроллер для авторизации администратора и смены пароля")
@RestController
@RequestMapping("/api/auth")
public class AdminController {

    private final AdminService adminService;
    private final JwtUtil jwtUtil;

    public AdminController(AdminService adminService, JwtUtil jwtUtil) {
        this.adminService = adminService;
        this.jwtUtil = jwtUtil;
    }

    @Operation(summary = "Вход администратора", description = "Авторизация администратора с использованием имени пользователя и пароля")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешный вход",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Неверные учетные данные",
                    content = @Content),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody @Parameter(description = "Данные для входа", required = true) LoginRequest loginRequest) {

        try {
            String username = loginRequest.getUsername();
            String password = loginRequest.getPassword();

            if (adminService.authenticate(username, password)) {
                String token = jwtUtil.generateToken(username);
                LoginResponse response = new LoginResponse(token);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Неверные учетные данные", "Имя пользователя или пароль неверны"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Ошибка при авторизации", e.getMessage()));
        }
    }

    @Operation(summary = "Смена пароля администратора", description = "Смена пароля администратора с проверкой старого пароля")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Пароль успешно изменен",
                    content = @Content),
            @ApiResponse(
                    responseCode = "403",
                    description = "Старый пароль неверен",
                    content = @Content),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content)
    })
    @PatchMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody @Parameter(description = "Данные для смены пароля", required = true) ChangePasswordRequest changePasswordRequest) {

        try {
            String username = changePasswordRequest.getUsername();
            String oldPassword = changePasswordRequest.getOldPassword();
            String newPassword = changePasswordRequest.getNewPassword();

            if (adminService.changePassword(username, oldPassword, newPassword)) {
                return ResponseEntity.ok("Пароль успешно изменен");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse("Старый пароль неверен", "Проверьте правильность введенного старого пароля"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Ошибка при смене пароля", e.getMessage()));
        }
    }

    // Вспомогательные классы для запросов и ответов
    public static class LoginRequest {
        @Parameter(description = "Имя пользователя", required = true)
        private String username;

        @Parameter(description = "Пароль", required = true)
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class ChangePasswordRequest {
        @Parameter(description = "Имя пользователя", required = true)
        private String username;

        @Parameter(description = "Старый пароль", required = true)
        private String oldPassword;

        @Parameter(description = "Новый пароль", required = true)
        private String newPassword;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getOldPassword() {
            return oldPassword;
        }

        public void setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }

    public static class LoginResponse {
        @Schema(description = "JWT токен", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        private String token;

        public LoginResponse(String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    // Вспомогательный класс для ответа с ошибкой
    public static class ErrorResponse {
        private String message;
        private String details;

        public ErrorResponse(String message, String details) {
            this.message = message;
            this.details = details;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
        }
    }
}