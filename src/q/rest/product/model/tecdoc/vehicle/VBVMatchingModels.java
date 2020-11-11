package q.rest.product.model.tecdoc.vehicle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties
public class VBVMatchingModels {
    private List<VBVModels> array;

    public List<VBVModels> getArray() {
        return array;
    }

    public void setArray(List<VBVModels> array) {
        this.array = array;
    }
}
