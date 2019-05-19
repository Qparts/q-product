package q.rest.product.model.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="prd_vendor_stock")
public class VendorStock implements Serializable {

    @Id
    @SequenceGenerator(name = "prd_vendor_stock_id_seq_gen", sequenceName = "prd_vendor_stock_id_seq", initialValue=1000, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prd_vendor_stock_id_seq_gen")
    @Column(name="id", updatable = false)
    private long id;
    @Column(name="product_id")
    private long productId;
    @Column(name="vendor_id")
    private int vendorId;
    @Column(name="branch_id")
    private int branchId;
    @Column(name="cityId")
    private int cityId;
    @Column(name="quantity_min")
    private int quantityMin;
    @Column(name="quantity_max")
    private int quantityMax;
    @Column(name = "vat_percentage")
    private double vatPercentage;
    @Column(name="created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @Column(name="created_by")
    private int createdBy;
    @Column(name="created_by_vendor")
    private int createdByVendor;

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public int getQuantityMin() {
        return quantityMin;
    }

    public void setQuantityMin(int quantityMin) {
        this.quantityMin = quantityMin;
    }

    public int getQuantityMax() {
        return quantityMax;
    }

    public void setQuantityMax(int quantityMax) {
        this.quantityMax = quantityMax;
    }

    public double getVatPercentage() {
        return vatPercentage;
    }

    public void setVatPercentage(double vatPercentage) {
        this.vatPercentage = vatPercentage;
    }

    public int getCreatedByVendor() {
        return createdByVendor;
    }

    public void setCreatedByVendor(int createdByVendor) {
        this.createdByVendor = createdByVendor;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
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
}
