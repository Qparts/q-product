package q.rest.product.model.entity.v3.reduced;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="prd_company_stock_offer")
public class CompanyStockOfferReduced implements Serializable {
    @Id
    private long id;
    @Column(name="company_product_id")
    @JsonIgnore
    private long companyProductId;
    private int quantity;
    private double offerPrice;
    private Date offerStartDate;
    private Date offerEndDate;

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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getOfferPrice() {
        return offerPrice;
    }

    public void setOfferPrice(double offerPrice) {
        this.offerPrice = offerPrice;
    }

    public Date getOfferStartDate() {
        return offerStartDate;
    }

    public void setOfferStartDate(Date offerStartDate) {
        this.offerStartDate = offerStartDate;
    }

    public Date getOfferEndDate() {
        return offerEndDate;
    }

    public void setOfferEndDate(Date offerEndDate) {
        this.offerEndDate = offerEndDate;
    }

}
