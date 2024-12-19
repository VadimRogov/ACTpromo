package backend.dto.analitics;

public class InteractiveElementStats {

    private String typeElement; // Идентификатор элемента
    private Long interactions; // Количество взаимодействий

    public InteractiveElementStats() {}

    public InteractiveElementStats(String typeElement, Long interactions) {
        this.typeElement = typeElement;
        this.interactions = interactions;
    }


    public String getTypeElement() {
        return typeElement;
    }

    public void setTypeElement(String typeElement) {
        this.typeElement = typeElement;
    }

    public Long getInteractions() {
        return interactions;
    }

    public void setInteractions(Long interactions) {
        this.interactions = interactions;
    }
}
