package q.rest.product.model.contract;

import com.fasterxml.jackson.annotation.JsonIgnore;
import q.rest.product.helper.AppConstants;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

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
    @Column(name="product_desc_ar")
    private String descAr;
    @Column(name="details")
    private String details;
    @Column(name="details_ar")
    private String detailsAr;
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
    @Transient
    private List<PublicProduct> variants;

    public List<PublicProduct> getVariants() {
        return variants;
    }

    public void setVariants(List<PublicProduct> variants) {
        this.variants = variants;
    }

    @JsonIgnore
    public void initImageLink(){
        this.image = AppConstants.getProductImage(id);
    }

    public String getDescAr() {
        return descAr;
    }

    public void setDescAr(String descAr) {
        this.descAr = descAr;
    }

    public String getDetailsAr() {
        return detailsAr;
    }

    public void setDetailsAr(String detailsAr) {
        this.detailsAr = detailsAr;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PublicProduct that = (PublicProduct) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
