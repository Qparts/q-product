package q.rest.product.model.product.market;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import q.rest.product.helper.AppConstants;
import q.rest.product.model.product.full.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Table(name="prd_product")
@Entity
public class MarketProduct implements Serializable {
    @Id
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
    @JsonIgnore
    private char status;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<MarketProductSpec> specs = new HashSet<>();
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<ProductSupply> marketSupply;



    @JsonIgnore
    public double getAverageSalesPrice(){
        try {
            double total = 0;
            for (var supply : marketSupply) {
                total += supply.getSalesPrice();
            }
            return total / marketSupply.size();
        }catch (Exception ex) {
            return 0;
        }
    }

    public String getImage(){
        return AppConstants.getProductImage(id);
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

    public char getStatus() {
        return status;
    }

    public void setStatus(char status) {
        this.status = status;
    }


    public Set<MarketProductSpec> getSpecs() {
        return specs;
    }

    public void setSpecs(Set<MarketProductSpec> specs) {
        this.specs = specs;
    }

    @Override
    public String toString() {
        return "MarketProduct{" +
                "id=" + id +
                ", productNumber='" + productNumber + '\'' +
                ", productDesc='" + productDesc + '\'' +
                ", productDescAr='" + productDescAr + '\'' +
                ", details='" + details + '\'' +
                ", detailsAr='" + detailsAr + '\'' +
                ", brand=" + brand +
                ", status=" + status +
                ", specs=" + specs +
                ", marketSupply=" + marketSupply +
                '}';
    }
}
