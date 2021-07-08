package q.rest.product.model.product.full;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import q.rest.product.model.contract.v3.product.PbProduct;
import q.rest.product.model.product.market.ProductSupply;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Table(name="prd_product")
@Entity
public class Product implements Serializable {
    @Id
    @SequenceGenerator(name = "prd_product_id_seq_gen", sequenceName = "prd_product_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prd_product_id_seq_gen")
    private long id;//ok
    private String productNumber;//ok
    @Column(name="product_desc")
    private String productDesc;//ok
    @Column(name="product_desc_ar")
    private String productDescAr;//ok
    private String details;//ok
    private String detailsAr;//ok
    @JoinColumn(name="brand_id", insertable = false, updatable = false)
    @ManyToOne
    private Brand brand;
    private Date created;
    private int createdBy;
    private char status;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="prd_product_category", joinColumns = @JoinColumn(name="product_id"), inverseJoinColumns = @JoinColumn(name="category_id"))
    @OrderBy(value = "id")
    private Set<Category> categories = new HashSet<>();
    @OrderBy(value = "tag")
    @ElementCollection(targetClass=String.class)
    @CollectionTable(name = "prd_product_tag", joinColumns = @JoinColumn(name = "product_id"))
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<String> tag = new HashSet<>();
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<ProductSpec> specs = new HashSet<>();
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<ProductSupply> marketSupply;
    @Column(name="reference_price")
    private double referencePrice;

    @JsonIgnore
    public PbProduct getPublicProduct(List<Spec> specs){
        return new PbProduct(this, specs);
    }


    public List<ProductSupply> getMarketSupply() {
        return marketSupply;
    }

    public void setMarketSupply(List<ProductSupply> marketSupply) {
        this.marketSupply = marketSupply;
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

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public String getProductDescAr() {
        return productDescAr;
    }

    public void setProductDescAr(String productDescAr) {
        this.productDescAr = productDescAr;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDetailsAr() {
        return detailsAr;
    }

    public void setDetailsAr(String detailsAr) {
        this.detailsAr = detailsAr;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public char getStatus() {
        return status;
    }

    public void setStatus(char status) {
        this.status = status;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    public Set<String> getTags() {
        return tag;
    }

    public void setTags(Set<String> tags) {
        this.tag = tags;
    }

    public Set<ProductSpec> getSpecs() {
        return specs;
    }

    public void setSpecs(Set<ProductSpec> specs) {
        this.specs = specs;
    }

    public double getReferencePrice() {
        return referencePrice;
    }

    public void setReferencePrice(double referencePrice) {
        this.referencePrice = referencePrice;
    }
}
