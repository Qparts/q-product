package q.rest.product.model.contract;

import q.rest.product.model.entity.stock.VendorSpecialOfferStock;
import q.rest.product.model.entity.stock.VendorStock;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class UploadStock implements Serializable {
    private List<VendorStock> vendorStocks;
    private List<VendorSpecialOfferStock> specialOfferStocks;
    private int vendorId;
    private int createdByVendor;
    private int createdBy;
    private int branchId;
    private Date date;
    private boolean overridePrevious;

    public boolean isOverridePrevious() {
        return overridePrevious;
    }

    public void setOverridePrevious(boolean overridePrevious) {
        this.overridePrevious = overridePrevious;
    }

    public List<VendorSpecialOfferStock> getSpecialOfferStocks() {
        return specialOfferStocks;
    }

    public void setSpecialOfferStocks(List<VendorSpecialOfferStock> specialOfferStocks) {
        this.specialOfferStocks = specialOfferStocks;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<VendorStock> getVendorStocks() {
        return vendorStocks;
    }

    public void setVendorStocks(List<VendorStock> vendorStocks) {
        this.vendorStocks = vendorStocks;
    }

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public int getCreatedByVendor() {
        return createdByVendor;
    }

    public void setCreatedByVendor(int createdByVendor) {
        this.createdByVendor = createdByVendor;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }
}
