package q.rest.product.model.qvm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QvmObject implements Serializable {
    private String partNumber;
    private String brand;
    private String brandPartNumber;
    private Double retailPrice;
    private Double wholesalesPrice;
    private Boolean available;
    private List<QvmObjectStock> availability;


    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getBrandPartNumber() {
        return brandPartNumber;
    }

    public void setBrandPartNumber(String brandPartNumber) {
        this.brandPartNumber = brandPartNumber;
    }

    public Double getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(Double retailPrice) {
        this.retailPrice = retailPrice;
    }

    public Double getWholesalesPrice() {
        return wholesalesPrice;
    }

    public void setWholesalesPrice(Double wholesalesPrice) {
        this.wholesalesPrice = wholesalesPrice;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public List<QvmObjectStock> getAvailability() {
        return availability;
    }

    public void setAvailability(List<QvmObjectStock> availability) {
        this.availability = availability;
    }
}
