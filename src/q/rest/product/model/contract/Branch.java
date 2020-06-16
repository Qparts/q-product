package q.rest.product.model.contract;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Branch implements Serializable {
    private int id;
    private String clientBranchId;
    private int cityId;


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getClientBranchId() {
        return clientBranchId;
    }
    public void setClientBranchId(String clientBranchId) {
        this.clientBranchId = clientBranchId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
