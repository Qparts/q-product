package q.rest.product.model.entity.v3.reduced;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Where;
import q.rest.product.model.contract.subscriber.BranchReduced;
import q.rest.product.model.contract.v3.OfferHolder;
import q.rest.product.model.contract.v3.StockHolder;
import q.rest.product.model.contract.v3.UploadHolder;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="prd_company_stock")
public class CompanyStockReduced {
    @Id
    @JsonIgnore
    private long id;
    @Column(name="company_product_id")
    @JsonIgnore
    private long companyProductId;
    @JsonIgnore
    private int branchId;
    @JsonIgnore
    private int cityId;
    private int quantity;
    @JsonIgnore
    private boolean offerOnly;
    @Transient
    private BranchReduced branch;

    public BranchReduced getBranch() {
        return branch;
    }

    public void setBranch(BranchReduced branch) {
        this.branch = branch;
    }

    public boolean isOfferOnly() {
        return offerOnly;
    }

    public void setOfferOnly(boolean offerOnly) {
        this.offerOnly = offerOnly;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCompanyProductId() {
        return companyProductId;
    }

    public void setCompanyProductId(long companyProductId) {
        this.companyProductId = companyProductId;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
