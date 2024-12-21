package backend.dto.analitics;

public class TrafficSourceStats {

    private String source;
    private long visit;

    public TrafficSourceStats() {}

    public TrafficSourceStats(String referer, long visit) {
        this.source = referer;
        this.visit = visit;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public long getVisit() {
        return visit;
    }

    public void setVisit(long visit) {
        this.visit = visit;
    }
}
