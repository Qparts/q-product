package q.rest.product.model.qstock;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="prd_product")
public class StockProduct implements Serializable {
    @Id
    @SequenceGenerator(name = "prd_product_id_seq_gen", sequenceName = "prd_product_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prd_product_id_seq_gen")
    private long id;
    private String productNumber;
    @Column(name="product_desc")
    private String name;
    @Column(name="product_desc_ar")
    private String nameAr;
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    private int createdBy;
    private Integer approvedBy;
    @Column(name="brand_id")
    private int brandId;
    private char status;//A = Active, I = inactive, P = pending review

//    @OneToMany(fetch = FetchType.EAGER)
//    @JoinColumn(name = "stock_product_id")
//    private Set<StockLive> liveStock = new HashSet<>();


    public Integer getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Integer approvedBy) {
        this.approvedBy = approvedBy;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public String getNameAr() {
        return nameAr;
    }

    public void setNameAr(String nameAr) {
        this.nameAr = nameAr;
    }


    public char getStatus() {
        return status;
    }

    public void setStatus(char status) {
        this.status = status;
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

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
