package q.rest.product.model.entity;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Table(name="prd_product")
@Entity
public class Product implements Serializable {

    @Id
    @SequenceGenerator(name = "prd_product_id_seq_gen", sequenceName = "prd_product_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prd_product_id_seq_gen")
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
    private Brand brand;
    @Column(name="created")
    private Date created;
    @Column(name="created_by")
    private int createdBy;
    @Column(name="status")
    private char status;

    public char getStatus() {
        return status;
    }

    public void setStatus(char status) {
        this.status = status;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id &&
                createdBy == product.createdBy &&
                Objects.equals(productNumber, product.productNumber) &&
                Objects.equals(desc, product.desc) &&
                Objects.equals(details, product.details) &&
                Objects.equals(brand, product.brand) &&
                Objects.equals(created, product.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, productNumber, desc, details, brand, created, createdBy);
    }
}
