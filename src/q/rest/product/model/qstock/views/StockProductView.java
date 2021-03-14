package q.rest.product.model.qstock.views;

import com.fasterxml.jackson.annotation.JsonIgnore;
import q.rest.product.model.qstock.StockLive;
import q.rest.product.model.qstock.StockProduct;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Table(name = "prd_view_stock_product")
@Entity
@IdClass(StockProductView.StockProductViewPK.class)
public class StockProductView {
    @Id
    private long productId;
    @Id
    @JsonIgnore
    private int companyId;
    private String productNumber;
    private String name;
    private String nameAr;
    private int brandId;
    private String brandName;
    private String brandNameAr;
    @JsonIgnore
    private char status;
    private Integer policyId;
    private Integer shortageFlag;
    private String notes;
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumns({
            @JoinColumn(name = "product_id"),
            @JoinColumn(name = "company_id")
    })
    private Set<StockLive> liveStock = new HashSet<>();

    public Set<StockLive> getLiveStock() {
        return liveStock;
    }

    public void setLiveStock(Set<StockLive> liveStock) {
        this.liveStock = liveStock;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
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

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getBrandNameAr() {
        return brandNameAr;
    }

    public void setBrandNameAr(String brandNameAr) {
        this.brandNameAr = brandNameAr;
    }

    public char getStatus() {
        return status;
    }

    public void setStatus(char status) {
        this.status = status;
    }

    public Integer getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Integer policyId) {
        this.policyId = policyId;
    }

    public Integer getShortageFlag() {
        return shortageFlag;
    }

    public void setShortageFlag(Integer shortageFlag) {
        this.shortageFlag = shortageFlag;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }


    public static class StockProductViewPK implements Serializable {
        protected int companyId;
        protected long productId;

        public StockProductViewPK() {}


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StockProductViewPK that = (StockProductViewPK) o;
            return companyId == that.companyId &&
                    productId == that.productId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(companyId, productId);
        }
    }

    @Override
    public String toString() {
        return "StockProductView{" +
                "productId=" + productId +
                ", companyId=" + companyId +
                ", productNumber='" + productNumber + '\'' +
                ", name='" + name + '\'' +
                ", nameAr='" + nameAr + '\'' +
                ", brandId=" + brandId +
                ", brandName='" + brandName + '\'' +
                ", brandNameAr='" + brandNameAr + '\'' +
                ", status=" + status +
                ", policyId=" + policyId +
                ", shortageFlag=" + shortageFlag +
                ", notes='" + notes + '\'' +
                ", liveStock=" + liveStock +
                '}';
    }
}
