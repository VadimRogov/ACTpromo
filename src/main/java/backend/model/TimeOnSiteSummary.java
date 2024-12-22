package backend.model;

public class TimeOnSiteSummary {
    private long timeOnSite; // Общее время на сайте
    private long avgTimeOnSite; // Среднее время на сайте (округленное)

    // Конструктор
    public TimeOnSiteSummary(long timeOnSite, long avgTimeOnSite) {
        this.timeOnSite = timeOnSite;
        this.avgTimeOnSite = avgTimeOnSite;
    }

    // Геттеры и сеттеры
    public long getTimeOnSite() {
        return timeOnSite;
    }

    public void setTimeOnSite(long timeOnSite) {
        this.timeOnSite = timeOnSite;
    }

    public long getAvgTimeOnSite() {
        return avgTimeOnSite;
    }

    public void setAvgTimeOnSite(long avgTimeOnSite) {
        this.avgTimeOnSite = avgTimeOnSite;
    }
}
