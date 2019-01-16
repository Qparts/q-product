package q.rest.product.model.contract;

import java.io.Serializable;

public class PublicSpec implements Serializable {

    private String specKey;
    private String specKeyAr;
    private String specValue;
    private String specValueAr;

    public String getSpecKey() {
        return specKey;
    }

    public void setSpecKey(String specKey) {
        this.specKey = specKey;
    }

    public String getSpecKeyAr() {
        return specKeyAr;
    }

    public void setSpecKeyAr(String specKeyAr) {
        this.specKeyAr = specKeyAr;
    }

    public String getSpecValue() {
        return specValue;
    }

    public void setSpecValue(String specValue) {
        this.specValue = specValue;
    }

    public String getSpecValueAr() {
        return specValueAr;
    }

    public void setSpecValueAr(String specValueAr) {
        this.specValueAr = specValueAr;
    }
}
