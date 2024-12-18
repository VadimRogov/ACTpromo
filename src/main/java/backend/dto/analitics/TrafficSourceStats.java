package backend.dto.analitics;

public class TrafficSourceStats {

    private String source;
    private int visit;

    public TrafficSourceStats() {}

    public TrafficSourceStats(String referer, int visit) {
        this.source = referer;
        this.visit = visit;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getVisit() {
        return visit;
    }

    public void setVisit(int visit) {
        this.visit = visit;
    }
}
