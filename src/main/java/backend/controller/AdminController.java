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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AdminController", description = "Контроллер для авторизации администратора и смены пароля")
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AdminController {

    private final AdminService adminService;
    private final JwtUtil jwtUtil;

    public AdminController(AdminService adminService, JwtUtil jwtUtil) {
        this.adminService = adminService;
        this.jwtUtil = jwtUtil;
    }

    @Operation(summary = "Вход администратора", description = "Авторизация администратора с использованием имени пользователя и пароля")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешный вход",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "Неверные учетные данные")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody @Parameter(description = "Данные для входа", required = true) LoginRequest loginRequest) {

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        if (adminService.authenticate(username, password)) {
            String token = jwtUtil.generateToken(username);
            LoginResponse response = new LoginResponse(token);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

    @Operation(summary = "Смена пароля администратора", description = "Смена пароля администратора с проверкой старого пароля")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пароль успешно изменен"),
            @ApiResponse(responseCode = "403", description = "Старый пароль неверен")
    })
    @PatchMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody @Parameter(description = "Данные для смены пароля", required = true) ChangePasswordRequest changePasswordRequest) {

        String username = changePasswordRequest.getUsername();
        String oldPassword = changePasswordRequest.getOldPassword();
        String newPassword = changePasswordRequest.getNewPassword();

        if (adminService.changePassword(username, oldPassword, newPassword)) {
            return ResponseEntity.ok("Password changed successfully");
        } else {
            return ResponseEntity.status(403).body("Incorrect old password");
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
}