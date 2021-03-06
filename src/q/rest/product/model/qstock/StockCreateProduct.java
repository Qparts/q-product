package q.rest.product.model.qstock;

import java.util.ArrayList;
import java.util.List;

public class StockCreateProduct {
    private String productNumber;
    private int brandId;
    private int policyId;
    private int shortageFlag;
    private String name;
    private String nameAr;
    private String notes;
    List<BranchShelvesKV> shelves = new ArrayList<>();

    public List<BranchShelvesKV> getShelves() {
        return shelves;
    }

    public void setShelves(List<BranchShelvesKV> shelves) {
        this.shelves = shelves;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public int getPolicyId() {
        return policyId;
    }

    public void setPolicyId(int policyId) {
        this.policyId = policyId;
    }

    public int getShortageFlag() {
        return shortageFlag;
    }

    public void setShortageFlag(int shortageFlag) {
        this.shortageFlag = shortageFlag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameAr() {
        return nameAr;
    }

    public void setNameAr(String nameAr) {
        this.nameAr = nameAr;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
