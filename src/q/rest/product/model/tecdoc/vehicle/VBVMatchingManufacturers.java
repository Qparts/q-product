package q.rest.product.model.tecdoc.vehicle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties
public class VBVMatchingManufacturers {
    private List<VBVManufacturers> array;

    public List<VBVManufacturers> getArray() {
        return array;
    }

    public void setArray(List<VBVManufacturers> array) {
        this.array = array;
    }
}
