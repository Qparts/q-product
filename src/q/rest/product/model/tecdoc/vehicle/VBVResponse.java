package q.rest.product.model.tecdoc.vehicle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VBVResponse implements Serializable {

    private VBVData data;
    private int status;

    public VBVData getData() {
        return data;
    }

    public void setData(VBVData data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
