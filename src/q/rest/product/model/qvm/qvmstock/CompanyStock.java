package q.rest.product.model.qvm.qvmstock;

import q.rest.product.model.contract.v3.OfferHolder;
import q.rest.product.model.contract.v3.StockHolder;
import q.rest.product.model.contract.v3.UploadHolder;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="prd_company_stock")
public class CompanyStock {
    @Id
    @SequenceGenerator(name = "prd_company_stock_id_seq_gen", sequenceName = "prd_company_stock_id_seq", initialValue=1000, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prd_company_stock_id_seq_gen")
    private long id;
    @Column(name="company_product_id")
    private long companyProductId;
    private int branchId;
    private int cityId;
    private int regionId;
    private int countryId;
    private int quantity;
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    private boolean offerOnly;

    public CompanyStock(){

    }

    //for creating regular stock
    public CompanyStock(OfferHolder offerVar, UploadHolder holder){
        offerOnly = true;
        created = holder.getDate();
        branchId = holder.getBranchId();
        quantity = offerVar.getQuantity();
        cityId = holder.getCityId();
        countryId = holder.getCountryId();
        regionId = holder.getRegionId();
    }



    //for creating regular stock
    public CompanyStock(StockHolder stockVar, UploadHolder holder){
        offerOnly = false;
        created = holder.getDate();
        branchId = holder.getBranchId();
        quantity = stockVar.getQuantity();
        cityId = holder.getCityId();
        countryId = holder.getCountryId();
        regionId = holder.getRegionId();
    }

    public boolean isOfferOnly() {
        return offerOnly;
    }

    public void setOfferOnly(boolean offerOnly) {
        this.offerOnly = offerOnly;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
