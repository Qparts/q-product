package q.rest.product.model.qvm;

import q.rest.product.model.contract.PublicProduct;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class QvmObject implements Serializable {
    private char source;//L = live integration, U = stock upload, Q = Q Parts
    private Integer vendorId;
    private String partNumber;
    private String brandPartNumber;
    private List<PublicProduct> qpartsProducts;
    private String brand;
    private Double retailPrice;
    private Double wholesalesPrice;
    private Boolean available;
    private Date lastUpdate;
    private List<QvmAvailability> availability;

    public char getSource() {
        return source;
    }

    public void setSource(char source) {
        this.source = source;
    }

    public String getBrandPartNumber() {
        return brandPartNumber;
    }

    public void setBrandPartNumber(String brandPartNumber) {
        this.brandPartNumber = brandPartNumber;
    }

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

    public List<QvmAvailability> getAvailability() {
        return availability;
    }

    public void setAvailability(List<QvmAvailability> availability) {
        this.availability = availability;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
