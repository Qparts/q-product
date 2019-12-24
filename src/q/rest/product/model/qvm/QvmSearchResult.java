package q.rest.product.model.qvm;

import q.rest.product.model.contract.ProductHolder;
import q.rest.product.model.contract.PublicProduct;

import java.io.Serializable;
import java.util.List;

public class QvmSearchResult implements Serializable {

    private Integer vendorId;
    private String partNumber;
    private List<PublicProduct> qpartsProducts;
    private String brand;
    private Double retailPrice;
    private Double wholesalesPrice;
    private Boolean available;
    private List<SearchAvailability> availability;

    public List<PublicProduct> getQpartsProducts() {
        return qpartsProducts;
    }

    public void setQpartsProducts(List<PublicProduct> qpartsProducts) {
        this.qpartsProducts = qpartsProducts;
    }

    public Integer getVendorId() {
        return vendorId;
    }

    public void setVendorId(Integer vendorId) {
        this.vendorId = vendorId;
    }

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

    public List<SearchAvailability> getAvailability() {
        return availability;
    }

    public void setAvailability(List<SearchAvailability> availability) {
        this.availability = availability;
    }
}
