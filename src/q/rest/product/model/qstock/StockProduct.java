package q.rest.product.model.qstock;

import com.fasterxml.jackson.annotation.JsonIgnore;
import q.rest.product.model.entity.v4.pblic.PbCompanyStock;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="prd_stk_product")
public class StockProduct implements Serializable {
    @Id
    @SequenceGenerator(name = "prd_stk_product_id_seq_gen", sequenceName = "prd_stk_product_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prd_stk_product_id_seq_gen")
    private long id;
    @JsonIgnore
    private int companyId;
    private String name;
    private String productNumber;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "brand_id", insertable = false, updatable = false)
    private StockBrand brand = new StockBrand();
    private double salesPrice;
    private double wholesalesPrice;
    private double specialPrice;
    private int shortageFlag;
    private String notes;
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @Column(name="brand_id")
    private int brandId;
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "stock_product_id")
    private Set<StockLive> liveStock = new HashSet<>();

    public Set<StockLive> getLiveStock() {
        return liveStock;
    }

    public void setLiveStock(Set<StockLive> liveStock) {
        this.liveStock = liveStock;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public StockBrand getBrand() {
        return brand;
    }

    public void setBrand(StockBrand brand) {
        this.brand = brand;
    }

    public double getSalesPrice() {
        return salesPrice;
    }

    public void setSalesPrice(double salesPrice) {
        this.salesPrice = salesPrice;
    }

    public double getWholesalesPrice() {
        return wholesalesPrice;
    }

    public void setWholesalesPrice(double wholesalesPrice) {
        this.wholesalesPrice = wholesalesPrice;
    }

    public double getSpecialPrice() {
        return specialPrice;
    }

    public void setSpecialPrice(double specialPrice) {
        this.specialPrice = specialPrice;
    }

    public int getShortageFlag() {
        return shortageFlag;
    }

    public void setShortageFlag(int shortageFlag) {
        this.shortageFlag = shortageFlag;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
