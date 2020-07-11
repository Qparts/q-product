package q.rest.product.model.entity.v3.reduced;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Where;
import q.rest.product.model.contract.v3.OfferHolder;
import q.rest.product.model.contract.v3.StockHolder;
import q.rest.product.model.contract.v3.UploadHolder;
import q.rest.product.model.entity.v3.stock.CompanyOfferUploadRequest;
import q.rest.product.model.entity.v3.stock.CompanyStock;
import q.rest.product.model.entity.v3.stock.CompanyStockOffer;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="prd_company_product")
public class CompanyProductReduced {
    @Id
    private long id;
    @JsonIgnore
    private int companyId;
    @Transient
    private Object company;
    private String partNumber;
    private String alternativeNumber;
    private String brandName;
    private double retailPrice;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "company_product_id")
    private Set<CompanyStockReduced> stock = new HashSet<>();


    public Set<CompanyStockReduced> getStock() {
        return stock;
    }

    public void setStock(Set<CompanyStockReduced> stock) {
        this.stock = stock;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getAlternativeNumber() {
        return alternativeNumber;
    }

    public void setAlternativeNumber(String alternativeNumber) {
        this.alternativeNumber = alternativeNumber;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public double getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(double retailPrice) {
        this.retailPrice = retailPrice;
    }

    public Object getCompany() {
        return company;
    }

    public void setCompany(Object company) {
        this.company = company;
    }
}
