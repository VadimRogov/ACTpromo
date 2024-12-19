package backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_activity")
public class UserActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_ip", nullable = false)
    private String userIp; // IP-адрес пользователя

    @Column(name = "session_id", nullable = true)
    private String sessionId; // Идентификатор сессии

    @Column(name = "page_url", nullable = true)
    private String pageUrl; // URL страницы

    @Column(name = "event_type", nullable = false)
    private EventType eventType; // Тип события (например, "enter", "click", "exit")

    @Column(name = "event_details", nullable = true)
    private String eventDetails; // Дополнительные данные о событии

    @Column(name = "referer", nullable = true)
    private String referer; // Источник трафика (откуда пользователь перешел)

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp; // Время события
    @Column(name = "count_event")
    private Long countEvent;

    public UserActivity() {}

    public UserActivity(String userIp, String sessionId, String pageUrl, EventType eventType, String eventDetails, String referer, LocalDateTime timestamp, Long countEvent) {
        this.userIp = userIp;
        this.sessionId = sessionId;
        this.pageUrl = pageUrl;
        this.eventType = eventType;
        this.eventDetails = eventDetails;
        this.referer = referer;
        this.timestamp = timestamp;
        this.countEvent = countEvent;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getEventDetails() {
        return eventDetails;
    }

    public void setEventDetails(String eventDetails) {
        this.eventDetails = eventDetails;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Long getCountEvent() {
        return countEvent;
    }

    public void setCountEvent(Long countEvent) {
        this.countEvent = countEvent;
    }
}
