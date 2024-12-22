package backend.controller;

import backend.dto.analitics.*;
import backend.model.TimeOnSiteSummary;
import backend.service.AnalyticsService;
import backend.utils.JwtUtil;
import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Tag(name = "AnalyticsController", description = "Контроллер для предоставления аналитики")
@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {
    private final AnalyticsService analyticsService;
    private final JwtUtil jwtUtil;

    public AnalyticsController(AnalyticsService analyticsService, JwtUtil jwtUtil) {
        this.analyticsService = analyticsService;
        this.jwtUtil = jwtUtil;
    }


    @Operation(summary = "Получить количество уникальных посетителей",
            description = "Возвращает количество уникальных посетителей сайта")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Данные успешно получены",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(value = "{\"count\": 123}"))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Недействительный или отсутствующий токен авторизации",
                    content = @Content),
            @ApiResponse(
                    responseCode = "403",
                    description = "Доступ запрещен",
                    content = @Content),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content)
    })
    @GetMapping("/unique-visitors")
    public ResponseEntity<Map<String, Long>> getUniqueVisitorsCount(
            @RequestHeader("Authorization") @Parameter(description = "Токен авторизации", required = true) String authorizationHeader) {

        try {
            String token = authorizationHeader.substring(7); // Убираем "Bearer "
            String username = jwtUtil.getUsernameFromToken(token);

            if (!"admin".equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            Long count = analyticsService.getUniqueVisitorsCount();
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    @Operation(summary = "Получить источники трафика", description = "Возвращает список источников трафика")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Данные успешно получены",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TrafficSourceStats.class)))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Недействительный или отсутствующий токен авторизации",
                    content = @Content),
            @ApiResponse(
                    responseCode = "403",
                    description = "Доступ запрещен",
                    content = @Content),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content)
    })
    @GetMapping("/traffic-sources")
    public ResponseEntity<List<TrafficSourceStats>> getTrafficSources(
            @RequestHeader("Authorization") @Parameter(description = "Токен авторизации", required = true) String authorizationHeader) {

        try {
            String token = authorizationHeader.substring(7); // Убираем "Bearer "
            String username = jwtUtil.getUsernameFromToken(token);

            if (!"admin".equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            return ResponseEntity.ok(analyticsService.getTrafficSources());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    @Operation(summary = "Получить время, проведенное на сайте", description = "Возвращает статистику по времени, проведенному на сайте")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Данные успешно получены",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TimeOnSiteStats.class)))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Недействительный или отсутствующий токен авторизации",
                    content = @Content),
            @ApiResponse(
                    responseCode = "403",
                    description = "Доступ запрещен",
                    content = @Content),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content)
    })
    @GetMapping("/time-on-site")
    public ResponseEntity<TimeOnSiteSummary> getTimeOnSite(
            @RequestHeader("Authorization") @Parameter(description = "Токен авторизации", required = true) String authorizationHeader) {

        try {
            String token = authorizationHeader.substring(7); // Убираем "Bearer "
            String username = jwtUtil.getUsernameFromToken(token);

            if (!"admin".equals(username)) {
                System.out.println("Пользователю отказано в доступе: " + username);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // Получаем данные о времени на сайте
            TimeOnSiteSummary timeOnSiteSummary = analyticsService.getTimeOnSite();

            // Возвращаем результат
            return ResponseEntity.ok(timeOnSiteSummary);
        } catch (Exception e) {
            System.out.println("Ошибка при обработке запроса: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Получить популярные страницы", description = "Возвращает список популярных страниц")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Данные успешно получены",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = PageStats.class)))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Недействительный или отсутствующий токен авторизации",
                    content = @Content),
            @ApiResponse(
                    responseCode = "403",
                    description = "Доступ запрещен",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/popular-pages")
    public ResponseEntity<List<PageStats>> getPopularPages(
            @RequestHeader("Authorization") @Parameter(description = "Токен авторизации", required = true) String authorizationHeader) {

        try {
            String token = authorizationHeader.substring(7); // Убираем "Bearer "
            String username = jwtUtil.getUsernameFromToken(token);

            if (!"admin".equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            return ResponseEntity.ok(analyticsService.getPopularPages());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @Operation(summary = "Получить статистику по переходам на основной магазин",
            description = "Возвращает статистику по переходам на основной магазин (MAIN_SHOP)")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Данные успешно получены",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = StoreLinkStats.class)))),
            @ApiResponse(responseCode = "401",
                    description = "Недействительный или отсутствующий токен авторизации",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Доступ запрещен",
                    content = @Content),
            @ApiResponse(responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content)
    })
    @GetMapping("/store-link-stats")
    public ResponseEntity<List<StoreLinkStats>> getStoreLinkStats(
            @RequestHeader("Authorization") @Parameter(description = "Токен авторизации", required = true) String authorizationHeader) {

        try {
            // Извлекаем токен из заголовка
            String token = authorizationHeader.substring(7); // Убираем "Bearer "
            String username = jwtUtil.getUsernameFromToken(token);

            // Проверяем права доступа
            if (!"admin".equals(username)) {
                System.out.println("Пользователю отказано в доступе: " + username);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // Получаем данные о переходах на основной магазин
            List<StoreLinkStats> storeLinkStats = analyticsService.getStoreLinkStats();

            // Возвращаем результат
            return ResponseEntity.ok(storeLinkStats);
        } catch (Exception e) {
            System.out.println("Ошибка при обработке запроса: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Получить взаимодействия с интерактивными элементами", description = "Возвращает статистику по взаимодействиям с интерактивными элементами")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Данные успешно получены",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = InteractiveElementStats.class)))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Недействительный или отсутствующий токен авторизации",
                    content = @Content),
            @ApiResponse(
                    responseCode = "403",
                    description = "Доступ запрещен",
                    content = @Content),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content)
    })
    @GetMapping("/interactive-elements")
    public ResponseEntity<List<InteractiveElementStats>> getInteractiveElementInteractions(
            @RequestHeader("Authorization") String authorizationHeader) {

        try {
            String token = authorizationHeader.substring(7); // Убираем "Bearer "
            String username = jwtUtil.getUsernameFromToken(token);

            if (!"admin".equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            List<InteractiveElementStats> stats = analyticsService.getInteractiveElementInteractions();
            return ResponseEntity.ok(stats);
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonList(new InteractiveElementStats("Несанкционированный доступ", 0L)));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonList(new InteractiveElementStats("Ошибка", 0L)));
        }
    }
}