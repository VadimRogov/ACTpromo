package backend.dto.analitics;

public class PageStats {

    private String pageUrl; // URL страницы
    private int views; // Количество просмотров

    public PageStats() {}

    public PageStats(String pageUrl, int views) {
        this.pageUrl = pageUrl;
        this.views = views;
    }



    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }
}
