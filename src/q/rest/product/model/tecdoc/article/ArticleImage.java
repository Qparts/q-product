package q.rest.product.model.tecdoc.article;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ArticleImage {
    private String imageURL50;
    private String imageURL100;
    private String imageURL200;
    private String imageURL400;
    private String imageURL800;
    private String fileName;
    private String typeDescription;
    private String headerDescription;

    public String getImageURL50() {
        return imageURL50;
    }

    public void setImageURL50(String imageURL50) {
        this.imageURL50 = imageURL50;
    }

    public String getImageURL100() {
        return imageURL100;
    }

    public void setImageURL100(String imageURL100) {
        this.imageURL100 = imageURL100;
    }

    public String getImageURL200() {
        return imageURL200;
    }

    public void setImageURL200(String imageURL200) {
        this.imageURL200 = imageURL200;
    }

    public String getImageURL400() {
        return imageURL400;
    }

    public void setImageURL400(String imageURL400) {
        this.imageURL400 = imageURL400;
    }

    public String getImageURL800() {
        return imageURL800;
    }

    public void setImageURL800(String imageURL800) {
        this.imageURL800 = imageURL800;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTypeDescription() {
        return typeDescription;
    }

    public void setTypeDescription(String typeDescription) {
        this.typeDescription = typeDescription;
    }

    public String getHeaderDescription() {
        return headerDescription;
    }

    public void setHeaderDescription(String headerDescription) {
        this.headerDescription = headerDescription;
    }
}
