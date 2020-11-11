package q.rest.product.model.tecdoc.vehicle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties
public class VBVMatchingVehicles {
    private List<VBVVehicle> array;

    public List<VBVVehicle> getArray() {
        return array;
    }

    public void setArray(List<VBVVehicle> array) {
        this.array = array;
    }
}
