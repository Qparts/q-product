package q.rest.product.model.entity.v4.pblic;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="prd_company_stock_offer")
public class PbCompanyStockOffer implements Serializable {
    @Id
    private long id;
    @Column(name="company_product_id")
    private long companyProductId;
    private double offerPrice;
    private Date offerStartDate;
    private Date offerEndDate;
    private int offerRequestId;

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

    public int getOfferRequestId() {
        return offerRequestId;
    }

    public void setOfferRequestId(int offerRequestId) {
        this.offerRequestId = offerRequestId;
    }
}
