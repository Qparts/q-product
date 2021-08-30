package q.rest.product.model.quotation;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;

@Table(name="prd_search_list_item")
@Entity
public class PbSearchListItem {
    @Id
    private long id;
    private String productNumber;
    private String brand;
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    private double retailPrice;
    private Double specialOfferPrice;
    private boolean specialOffer;


    @JsonIgnore
    private long searchListId;
    @JsonIgnore
    private Long linkedProductId;
    @JsonIgnore
    private char status;



    public PbSearchListItem() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSearchListId() {
        return searchListId;
    }

    public void setSearchListId(long searchListId) {
        this.searchListId = searchListId;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Long getLinkedProductId() {
        return linkedProductId;
    }

    public void setLinkedProductId(Long linkedProductId) {
        this.linkedProductId = linkedProductId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public char getStatus() {
        return status;
    }

    public void setStatus(char status) {
        this.status = status;
    }

    public double getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(double retailPrice) {
        this.retailPrice = retailPrice;
    }

    public Double getSpecialOfferPrice() {
        return specialOfferPrice;
    }

    public void setSpecialOfferPrice(Double specialOfferPrice) {
        this.specialOfferPrice = specialOfferPrice;
    }

    public boolean isSpecialOffer() {
        return specialOffer;
    }

    public void setSpecialOffer(boolean specialOffer) {
        this.specialOffer = specialOffer;
    }
}
