package q.rest.product.model.catalog;

public class CatalogGroup {

    private String id;
    private String parentId;
    private boolean hasSubgroups;
    private boolean hasParts;
    private String name;
    private String img;
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public boolean isHasSubgroups() {
        return hasSubgroups;
    }

    public void setHasSubgroups(boolean hasSubgroups) {
        this.hasSubgroups = hasSubgroups;
    }

    public boolean isHasParts() {
        return hasParts;
    }

    public void setHasParts(boolean hasParts) {
        this.hasParts = hasParts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
