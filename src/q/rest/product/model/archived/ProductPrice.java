package q.rest.product.model.archived;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name="prd_product_price")
public class ProductPrice implements Serializable {

    @Id
    @SequenceGenerator(name = "prd_product_price_id_seq_gen", sequenceName = "prd_product_price_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prd_product_price_id_seq_gen")
    @Column(name="id")
    private long id;
    @Column(name="vendor_id")
    private int vendorId;
    @Column(name = "product_id")
    private long productId;
    @Column(name="price")
    private double price;
    @Column(name="created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @Column(name="created_by")
    private int createdBy;
    @Column(name="sales_percentage")
    private double salesPercentage;
    @Column(name="status")
    private char status;
    @Column(name="vendor_vat_percentage")
    private double vendorVatPercentage;

    public double getVendorVatPercentage() {
        return vendorVatPercentage;
    }

    public void setVendorVatPercentage(double vendorVatPercentage) {
        this.vendorVatPercentage = vendorVatPercentage;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
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

    public double getSalesPercentage() {
        return salesPercentage;
    }

    public void setSalesPercentage(double salesPercentage) {
        this.salesPercentage = salesPercentage;
    }

    public char getStatus() {
        return status;
    }

    public void setStatus(char status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductPrice that = (ProductPrice) o;
        return id == that.id &&
                vendorId == that.vendorId &&
                productId == that.productId &&
                Double.compare(that.price, price) == 0 &&
                createdBy == that.createdBy &&
                Double.compare(that.salesPercentage, salesPercentage) == 0 &&
                status == that.status &&
                Objects.equals(created, that.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, vendorId, productId, price, created, createdBy, salesPercentage, status);
    }
}
