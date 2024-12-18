package backend.dto.analitics;

public class InteractiveElementStats {
    private String elementId; // Идентификатор элемента
    private int interactions; // Количество взаимодействий

    public InteractiveElementStats() {}

    public InteractiveElementStats(String elementId, int interactions) {
        this.elementId = elementId;
        this.interactions = interactions;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public int getInteractions() {
        return interactions;
    }

    public void setInteractions(int interactions) {
        this.interactions = interactions;
    }
}
