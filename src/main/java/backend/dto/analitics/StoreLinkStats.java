package backend.dto.analitics;

public class StoreLinkStats {
    private String linkId; // Идентификатор ссылки
    private long clicks; // Количество переходов

    // Конструктор
    public StoreLinkStats(String linkId, long clicks) {
        this.linkId = linkId;
        this.clicks = clicks;
    }

    // Геттеры и сеттеры
    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    public long getClicks() {
        return clicks;
    }

    public void setClicks(long clicks) {
        this.clicks = clicks;
    }
}
