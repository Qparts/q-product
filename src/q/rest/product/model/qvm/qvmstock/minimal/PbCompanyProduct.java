package q.rest.product.model.qvm.qvmstock.minimal;

import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="prd_company_product")
public class PbCompanyProduct {
    @Id
    private long id;
    private int companyId;
    private String partNumber;
    private String alternativeNumber;
    private String brandName;
    private double retailPrice;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "company_product_id")
    //@Where(clause = "id not in (select q.company_product_id from prd_company_stock_offer q where now() between q.offer_start_date and q.offer_end_date and q.branch_id = branch_id)")
    private Set<PbCompanyStock> stock = new HashSet<>();
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "company_product_id")
    @Where(clause = "now() between offer_start_date and offer_end_date")
    private Set<PbCompanyStockOffer> offers = new HashSet<>();

    public Set<PbCompanyStock> getStock() {
        return stock;
    }

    public void setStock(Set<PbCompanyStock> stock) {
        this.stock = stock;
    }

    public Set<PbCompanyStockOffer> getOffers() {
        return offers;
    }

    public void setOffers(Set<PbCompanyStockOffer> offers) {
        this.offers = offers;
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

}
