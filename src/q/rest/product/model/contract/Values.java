package q.rest.product.model.contract;

import java.util.Objects;

public class Values {

    private String value;
    private String valueAr;
    private Number id;

    public Values(String value, String valueAr, Number id) {
        this.value = value;
        this.valueAr = valueAr;
        this.id = id;
    }

    public Number getId() {
        return id;
    }

    public void setId(Number id) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Values values = (Values) o;
        return Objects.equals(value, values.value) &&
                Objects.equals(valueAr, values.valueAr) &&
                Objects.equals(id, values.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, valueAr, id);
    }
}
