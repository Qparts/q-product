package q.rest.product.model.qvm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UploadStock implements Serializable {
    private List<VendorStock> vendorStocks;
    private int vendorId;
    private int createdByVendor;
    private int createdBy;
    private int branchId;

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
