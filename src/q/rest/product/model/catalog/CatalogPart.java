package q.rest.product.model.catalog;

import java.util.List;

public class CatalogPart {

    private String img;
    private String imgDescription;
    private List<CatalogPartsGroup> partGroups;
    private List<CatalogPartsPosition> positions;

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getImgDescription() {
        return imgDescription;
    }

    public void setImgDescription(String imgDescription) {
        this.imgDescription = imgDescription;
    }

    public List<CatalogPartsGroup> getPartGroups() {
        return partGroups;
    }

    public void setPartGroups(List<CatalogPartsGroup> partGroups) {
        this.partGroups = partGroups;
    }

    public List<CatalogPartsPosition> getPositions() {
        return positions;
    }

    public void setPositions(List<CatalogPartsPosition> positions) {
        this.positions = positions;
    }
}
