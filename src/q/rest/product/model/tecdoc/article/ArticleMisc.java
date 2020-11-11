package q.rest.product.model.tecdoc.article;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ArticleMisc {
    private int articleStatusId;
    private String articleStatusDescription;
    private long articleStatusValidFromDate;
    private int quantityPerPackage;
    private boolean isSelfServicePacking;
    private boolean hasMandatoryMaterialCertification;
    private boolean isRemanufacturedPart;
    private boolean isAccessory;

    public int getArticleStatusId() {
        return articleStatusId;
    }

    public void setArticleStatusId(int articleStatusId) {
        this.articleStatusId = articleStatusId;
    }

    public String getArticleStatusDescription() {
        return articleStatusDescription;
    }

    public void setArticleStatusDescription(String articleStatusDescription) {
        this.articleStatusDescription = articleStatusDescription;
    }

    public long getArticleStatusValidFromDate() {
        return articleStatusValidFromDate;
    }

    public void setArticleStatusValidFromDate(long articleStatusValidFromDate) {
        this.articleStatusValidFromDate = articleStatusValidFromDate;
    }

    public int getQuantityPerPackage() {
        return quantityPerPackage;
    }

    public void setQuantityPerPackage(int quantityPerPackage) {
        this.quantityPerPackage = quantityPerPackage;
    }

    public boolean isSelfServicePacking() {
        return isSelfServicePacking;
    }

    public void setSelfServicePacking(boolean selfServicePacking) {
        isSelfServicePacking = selfServicePacking;
    }

    public boolean isHasMandatoryMaterialCertification() {
        return hasMandatoryMaterialCertification;
    }

    public void setHasMandatoryMaterialCertification(boolean hasMandatoryMaterialCertification) {
        this.hasMandatoryMaterialCertification = hasMandatoryMaterialCertification;
    }

    public boolean isRemanufacturedPart() {
        return isRemanufacturedPart;
    }

    public void setRemanufacturedPart(boolean remanufacturedPart) {
        isRemanufacturedPart = remanufacturedPart;
    }

    public boolean isAccessory() {
        return isAccessory;
    }

    public void setAccessory(boolean accessory) {
        isAccessory = accessory;
    }
}
