package backend.model;

import jakarta.persistence.*;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_activity")
public class UserActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_ip")
    private String userIp; // IP-адрес пользователя

    @Column(name = "session_id")
    private String sessionId; // Идентификатор сессии

    @Column(name = "page_url")
    private String pageUrl; // URL страницы

    @Column(name = "event_type")
    private String eventType; // Тип события (например, "view", "click", "exit")

    @Column(name = "event_details")
    private String eventDetails; // Дополнительные данные о событии

    @Column(name = "referer")
    private String referer; // Источник трафика (откуда пользователь перешел)

    @Column(name = "timestamp")
    private LocalDateTime timestamp; // Время события

    public UserActivity() {}

    public UserActivity(String userIp, String sessionId, String pageUrl, String eventType, String eventDetails, String referer, LocalDateTime timestamp) {
        this.userIp = userIp;
        this.sessionId = sessionId;
        this.pageUrl = pageUrl;
        this.eventType = eventType;
        this.eventDetails = eventDetails;
        this.referer = referer;
        this.timestamp = timestamp;
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

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
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
}
