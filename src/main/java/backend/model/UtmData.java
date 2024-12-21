package backend.model;
import jakarta.persistence.*;

@Entity
@Table(name = "utm_data")
public class UtmData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "utm_source")
    private String utmSource;

    @Column(name = "utm_medium")
    private String utmMedium;

    @Column(name = "utm_campaign")
    private String utmCampaign;

    @Column(name = "utm_content")
    private String utmContent;

    @Column(name = "utm_term")
    private String utmTerm;

    // Конструктор по умолчанию
    public UtmData() {
    }

    // Конструктор с параметрами
    public UtmData(String utmSource, String utmMedium, String utmCampaign, String utmContent, String utmTerm) {
        this.utmSource = utmSource;
        this.utmMedium = utmMedium;
        this.utmCampaign = utmCampaign;
        this.utmContent = utmContent;
        this.utmTerm = utmTerm;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUtmSource() {
        return utmSource;
    }

    public void setUtmSource(String utmSource) {
        this.utmSource = utmSource;
    }

    public String getUtmMedium() {
        return utmMedium;
    }

    public void setUtmMedium(String utmMedium) {
        this.utmMedium = utmMedium;
    }

    public String getUtmCampaign() {
        return utmCampaign;
    }

    public void setUtmCampaign(String utmCampaign) {
        this.utmCampaign = utmCampaign;
    }

    public String getUtmContent() {
        return utmContent;
    }

    public void setUtmContent(String utmContent) {
        this.utmContent = utmContent;
    }

    public String getUtmTerm() {
        return utmTerm;
    }

    public void setUtmTerm(String utmTerm) {
        this.utmTerm = utmTerm;
    }

    // Переопределение toString() для удобства логирования
    @Override
    public String toString() {
        return "UtmData{" +
                "id=" + id +
                ", utmSource='" + utmSource + '\'' +
                ", utmMedium='" + utmMedium + '\'' +
                ", utmCampaign='" + utmCampaign + '\'' +
                ", utmContent='" + utmContent + '\'' +
                ", utmTerm='" + utmTerm + '\'' +
                '}';
    }

    // Переопределение equals() и hashCode() для сравнения объектов
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UtmData utmData = (UtmData) o;

        if (!id.equals(utmData.id)) return false;
        if (!utmSource.equals(utmData.utmSource)) return false;
        if (!utmMedium.equals(utmData.utmMedium)) return false;
        if (!utmCampaign.equals(utmData.utmCampaign)) return false;
        if (!utmContent.equals(utmData.utmContent)) return false;
        return utmTerm.equals(utmData.utmTerm);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + utmSource.hashCode();
        result = 31 * result + utmMedium.hashCode();
        result = 31 * result + utmCampaign.hashCode();
        result = 31 * result + utmContent.hashCode();
        result = 31 * result + utmTerm.hashCode();
        return result;
    }
}