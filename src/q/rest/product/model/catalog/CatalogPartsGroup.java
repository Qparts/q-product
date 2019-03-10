package q.rest.product.model.catalog;

import java.util.List;

public class CatalogPartsGroup {
    private String number;
    private String positionNumber;
    private String name;
    private String description;
    private List<CatalogPartsList> parts;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPositionNumber() {
        return positionNumber;
    }

    public void setPositionNumber(String positionNumber) {
        this.positionNumber = positionNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<CatalogPartsList> getParts() {
        return parts;
    }

    public void setParts(List<CatalogPartsList> parts) {
        this.parts = parts;
    }
}
