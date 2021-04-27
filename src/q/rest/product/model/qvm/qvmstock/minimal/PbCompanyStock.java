package q.rest.product.model.qvm.qvmstock.minimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name="prd_company_stock")
public class PbCompanyStock {
    @Id
    private long id;
    @Column(name="company_product_id")
    private long companyProductId;
    private int branchId;
    private int cityId;
    private int regionId;
    private int countryId;
    @JsonIgnore
    private boolean offerOnly;

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

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

}
