package backend.service;

import backend.dto.analitics.*;
import backend.model.EventType;
import backend.model.TimeOnSiteSummary;
import backend.model.UserActivity;
import backend.repository.UserActivityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {
    private final UserActivityRepository userActivityRepository;
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsService.class);
    public AnalyticsService(UserActivityRepository userActivityRepository) {
        this.userActivityRepository = userActivityRepository;
    }

    public Long getUniqueVisitorsCount() {
        return userActivityRepository.findAll().stream()
                .map(UserActivity::getUserIp)
                .distinct()
                .count();
    }

    // Метод для вычисления исходного трафика (Метод определяет уникальные источники трафика и число пользователей которые перешли с этого URL)
    public List<TrafficSourceStats> getTrafficSources() {
        return userActivityRepository.findAll().stream()
                .filter(activity -> activity.getReferer() != null && !activity.getReferer().isEmpty()) // Фильтруем только события с заполненным referer
                .collect(Collectors.groupingBy(
                        UserActivity::getReferer,
                        Collectors.summingLong(UserActivity::getCountEvent))) // Группируем по referer и суммируем countEvent

                .entrySet().stream()
                .map(entry -> {
                    TrafficSourceStats stats = new TrafficSourceStats();
                    stats.setSource(entry.getKey()); // Устанавливаем источник трафика
                    stats.setVisit(entry.getValue()); // Устанавливаем общее количество визитов
                    return stats;
                })
                .sorted((a, b) -> Long.compare(b.getVisit(), a.getVisit())) // Сортируем по убыванию количества визитов
                .collect(Collectors.toList());
    }


    // Метод для вычисления общего времени и среднего общего времени проведенного на сайте
    public TimeOnSiteSummary getTimeOnSite() {
        try {
            // Получаем все записи из репозитория
            List<UserActivity> activities = userActivityRepository.findAll();

            // Проверяем, что список не пуст
            if (activities == null || activities.isEmpty()) {
                throw new IllegalStateException("Список активностей пуст.");
            }

            // Группируем события по IP-адресу
            Map<String, List<UserActivity>> activitiesByIp = activities.stream()
                    .collect(Collectors.groupingBy(UserActivity::getUserIp));

            // Вычисляем время на сайте для каждого IP-адреса
            List<TimeOnSiteStats> timeOnSiteStatsList = activitiesByIp.entrySet().stream()
                    .map(entry -> {
                        String ipAddress = entry.getKey();
                        long timeOnSite = calculateTimeOnSiteForIp(entry.getValue());
                        return new TimeOnSiteStats(ipAddress, timeOnSite);
                    })
                    .collect(Collectors.toList());

            // Вычисляем общее время на сайте для всех пользователей
            long totalTimeOnSite = timeOnSiteStatsList.stream()
                    .mapToLong(TimeOnSiteStats::getTimeOnSite)
                    .sum();

            // Вычисляем среднее время на сайте
            long averageTimeOnSite = timeOnSiteStatsList.size() > 0
                    ? Math.round((double) totalTimeOnSite / timeOnSiteStatsList.size()) // Округляем среднее время
                    : 0;

            // Возвращаем результат
            return new TimeOnSiteSummary(totalTimeOnSite, averageTimeOnSite);
        } catch (Exception e) {
            System.out.println("Ошибка при вычислении времени на сайте: " + e.getMessage());
            return new TimeOnSiteSummary(0, 0); // Возвращаем пустой результат в случае ошибки
        }
    }


    // Метод для вычисления времени на сайте для одного IP-адреса
    private long calculateTimeOnSiteForIp(List<UserActivity> activities) {
        // Сортируем события по времени
        activities.sort(Comparator.comparing(UserActivity::getTimestamp));

        // Находим первое событие ENTER
        LocalDateTime firstEnter = activities.stream()
                .filter(activity -> EventType.ENTER.equals(activity.getEventType()))
                .map(UserActivity::getTimestamp)
                .findFirst()
                .orElse(null);

        // Если нет ENTER, время на сайте равно 0
        if (firstEnter == null) {
            System.out.println("Для IP-адреса " + activities.get(0).getUserIp() + " отсутствует событие ENTER.");
            return 0;
        }

        // Находим последнее событие EXIT
        LocalDateTime lastExit = activities.stream()
                .filter(activity -> EventType.EXIT.equals(activity.getEventType()))
                .map(UserActivity::getTimestamp)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        // Если EXIT отсутствует, используем текущее время
        if (lastExit == null) {
            lastExit = LocalDateTime.now(); // Текущее время
            System.out.println("Для IP-адреса " + activities.get(0).getUserIp() + " отсутствует событие EXIT. Используется текущее время: " + lastExit);
        }

        // Проверяем, чтобы время не было отрицательным
        if (lastExit.isBefore(firstEnter)) {
            System.out.println("Ошибка: время последнего события EXIT меньше времени первого события ENTER для IP-адреса " + activities.get(0).getUserIp());
            return 0;
        }

        // Вычисляем время на сайте между первым ENTER и последним EXIT
        return java.time.Duration.between(firstEnter, lastExit).toMillis();
    }


    // Метод для вычисления популярных страниц (метод выводит популярные страницы и число посещений)
    public List<PageStats> getPopularPages() {
        try {
            // Получаем все записи из репозитория
            List<UserActivity> activities = userActivityRepository.findAll();

            // Проверяем, что список не пуст
            if (activities == null || activities.isEmpty()) {
                throw new IllegalStateException("Список активностей пуст.");
            }

            return activities.stream()
                    .filter(activity -> EventType.VIEW.equals(activity.getEventType())) // Фильтруем только события VIEW
                    .collect(Collectors.groupingBy(UserActivity::getPageUrl)) // Группируем по pageUrl
                    .entrySet().stream()
                    .map(entry -> {
                        // Проверяем, что список событий для текущей группы не пуст
                        if (entry.getValue() == null || entry.getValue().isEmpty()) {
                            throw new IllegalStateException("Список событий для страницы " + entry.getKey() + " пуст.");
                        }

                        PageStats stats = new PageStats();
                        stats.setPageUrl(entry.getKey()); // Устанавливаем URL страницы

                        // Считаем сумму countEvent для каждой группы
                        long totalViews = entry.getValue().stream()
                                .mapToLong(UserActivity::getCountEvent)
                                .sum();

                        stats.setViews((int) totalViews); // Устанавливаем общее количество просмотров
                        return stats;
                    })
                    .sorted((a, b) -> b.getViews() - a.getViews()) // Сортируем по убыванию просмотров
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("Ошибка при вычислении популярных страниц: " + e.getMessage());
            return Collections.emptyList(); // Возвращаем пустой список в случае ошибки
        }
    }

    //Метод вычисляет с какой страницы перешли на сайт основного магазина и количество пользователей
    public List<StoreLinkStats> getStoreLinkStats() {
        try {
            // Логирование начала выполнения метода
            logger.info("Начинаем получение статистики по переходам на основной магазин с нашего сайта");

            // Получаем все события с типом MAIN_SHOP из базы данных
            List<UserActivity> mainShopEvents = userActivityRepository.findByEventType(EventType.MAIN_SHOP);

            // Логирование количества найденных событий
            logger.info("Найдено {} событий с типом MAIN_SHOP", mainShopEvents.size());

            // Группируем по page_url и подсчитываем количество переходов
            Map<String, Long> linkClicksMap = mainShopEvents.stream()
                    .collect(Collectors.groupingBy(
                            UserActivity::getPageUrl, // Группировка по page_url
                            Collectors.summingLong(UserActivity::getCountEvent) // Суммируем countEvent
                    ));

            // Логирование результата группировки
            logger.info("Сгруппированные события по page_url: {}", linkClicksMap);

            // Преобразуем результат в список StoreLinkStats
            List<StoreLinkStats> result = linkClicksMap.entrySet().stream()
                    .map(entry -> new StoreLinkStats(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());

            // Логирование успешного завершения
            logger.info("Успешно получена статистика по переходам на основной магазин");

            return result;
        } catch (Exception e) {
            // Логирование ошибки
            logger.error("Ошибка при получении статистики по переходам на основной магазин: {}", e.getMessage(), e);
            return List.of(); // Возвращаем пустой список в случае ошибки
        }
    }



    // Метод для вычисления взаимодействий с интерактивными элементами
    @Transactional(readOnly = true)
    public List<InteractiveElementStats> getInteractiveElementInteractions() {
        try {
            // Список нужных типов событий
            List<EventType> eventTypes = Arrays.asList(
                    EventType.BUTTER_FLY_COUNT,
                    EventType.TREE_COUNT,
                    EventType.CUB_COUNT,
                    EventType.COMMENT_COUNT,
                    EventType.CATALOG_COUNT
            );

            // Выполняем запрос с фильтрацией по нужным типам
            List<Object[]> results = userActivityRepository.findInteractiveElementStats(eventTypes);

            if (results == null || results.isEmpty()) {
                throw new IllegalStateException("Результаты запроса пусты или отсутствуют.");
            }

            return results.stream()
                    .map(result -> {
                        if (result == null || result.length < 2) {
                            throw new IllegalStateException("Некорректный результат запроса: " + Arrays.toString(result));
                        }

                        InteractiveElementStats stats = new InteractiveElementStats();
                        stats.setTypeElement(result[0] != null ? result[0].toString() : "UNKNOWN");
                        stats.setInteractions(result[1] != null ? ((Number) result[1]).longValue() : 0L);
                        return stats;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("Ошибка при обработке интерактивных элементов: " + e.getMessage());
            return Collections.emptyList(); // Возвращаем пустой список в случае ошибки
        }
    }

}
