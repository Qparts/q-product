package q.rest.product.model.contract;

import com.fasterxml.jackson.annotation.JsonIgnore;
import q.rest.product.helper.AppConstants;
import q.rest.product.model.entity.ProductReview;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Table(name="prd_product")
@Entity
public class PublicProduct implements Serializable {

    @Id
    @Column(name="id")
    private long id;
    @Column(name="product_number")
    private String productNumber;
    @Column(name="product_desc")
    private String desc;
    @Column(name="details")
    private String details;
    @JoinColumn(name="brand_id")
    @ManyToOne
    private PublicBrand brand;
    @Column(name="status")
    @JsonIgnore
    private char status;
    @Transient
    private List<PublicSpec> specs;
    @Transient
    private double salesPrice;
    @Transient
    private String image;
    @Transient
    private List<PublicReview> reviews;

    @JsonIgnore
    public void initImageLink(){
        this.image = AppConstants.getProductImage(id);
    }

    public String getImage() {
        return image;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public PublicBrand getBrand() {
        return brand;
    }

    public void setBrand(PublicBrand brand) {
        this.brand = brand;
    }

    public char getStatus() {
        return status;
    }

    public void setStatus(char status) {
        this.status = status;
    }

    public List<PublicSpec> getSpecs() {
        return specs;
    }

    public void setSpecs(List<PublicSpec> specs) {
        this.specs = specs;
    }

    public double getSalesPrice() {
        return salesPrice;
    }

    public void setSalesPrice(double salesPrice) {
        this.salesPrice = salesPrice;
    }


    public List<PublicReview> getReviews() {
        return reviews;
    }

    public void setReviews(List<PublicReview> reviews) {
        this.reviews = reviews;
    }
}
