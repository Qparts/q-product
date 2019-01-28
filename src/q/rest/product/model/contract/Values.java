package q.rest.product.model.contract;

public class Values {

    private String value;
    private String valueAr;

    public Values(String value, String valueAr) {
        this.value = value;
        this.valueAr = valueAr;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValueAr() {
        return valueAr;
    }

    public void setValueAr(String valueAr) {
        this.valueAr = valueAr;
    }
}
