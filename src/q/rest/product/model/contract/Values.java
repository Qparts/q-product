package q.rest.product.model.contract;

public class Values {

    private String value;
    private String valueAr;
    private int id;

    public Values(String value, String valueAr, int id) {
        this.value = value;
        this.valueAr = valueAr;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
