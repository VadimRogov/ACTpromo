package backend.dto.analitics;

public class TimeOnSiteStats {
    private String sessionId; // Идентификатор сессии
    private long timeOnSite; // Время на сайте в секундах

    public TimeOnSiteStats() {}

    public TimeOnSiteStats(String sessionId, long timeOnSite) {
        this.sessionId = sessionId;
        this.timeOnSite = timeOnSite;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public long getTimeOnSite() {
        return timeOnSite;
    }

    public void setTimeOnSite(long timeOnSite) {
        this.timeOnSite = timeOnSite;
    }
}
