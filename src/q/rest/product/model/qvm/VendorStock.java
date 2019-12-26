package q.rest.product.model.qvm;

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
    @Column(name="city_id")
    private int cityId;
    @Column(name="quantity")
    private int quantity;
    @Column(name = "retail_price")
    private double retailPrice;
    @Column(name = "wholesale_price")
    private double wholesalesPrice;
    @Column(name="created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @Column(name="created_by")
    private int createdBy;
    @Column(name="created_by_vendor")
    private int createdByVendor;
    @Column(name = "part_number")
    private String partNumber;
    @Column(name="brand_name")
    private String brandName;

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(double retailPrice) {
        this.retailPrice = retailPrice;
    }

    public double getWholesalesPrice() {
        return wholesalesPrice;
    }

    public void setWholesalesPrice(double wholesalesPrice) {
        this.wholesalesPrice = wholesalesPrice;
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
