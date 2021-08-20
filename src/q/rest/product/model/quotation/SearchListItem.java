package q.rest.product.model.quotation;

import q.rest.product.model.qvm.qvmstock.minimal.PbCompanyProduct;

import javax.persistence.*;
import java.util.Date;

@Table(name="prd_search_list_item")
@Entity
public class SearchListItem {
    @Id
    @SequenceGenerator(name = "prd_search_list_item_id_seq_gen", sequenceName = "prd_search_list_item_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prd_search_list_item_id_seq_gen")
    private long id;
    private long searchListId;
    private String productNumber;
    private String brand;
    private Long linkedProductId;
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    private char status;
    private double retailPrice;
    private Double specialOfferPrice;
    private boolean specialOffer;

    public SearchListItem() {
    }

    public SearchListItem(long searchListId, PbCompanyProduct companyProduct, double offerPrice, Long linkedProductId){
        this.searchListId = searchListId;
        this.productNumber = companyProduct.getPartNumber();
        this.brand = companyProduct.getBrandName();
        this.created = new Date();
        this.status = 'N';
        this.retailPrice = companyProduct.getRetailPrice();
        this.specialOffer = !companyProduct.getOffers().isEmpty();
        this.specialOfferPrice = offerPrice;
        this.linkedProductId = linkedProductId;
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
