package q.rest.product.model.entity.stock;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="prd_vendor_special_offer_stock")
public class VendorSpecialOfferStock implements Serializable {

    @Id
    @SequenceGenerator(name = "vnd_vendor_special_offer_stock_id_seq_gen", sequenceName = "vnd_vendor_special_offer_stock_id_seq", initialValue=1000, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vnd_vendor_special_offer_stock_id_seq_gen")
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
    @Column(name = "special_price")
    private double specialPrice;
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
    @Column(name="offer_start_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date offerStart;
    @Column(name="offer_end_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date offerEnd;
    @Column(name="special_offer_request_id")
    private int specialOfferRequestId;

    public int getSpecialOfferRequestId() {
        return specialOfferRequestId;
    }

    public void setSpecialOfferRequestId(int specialOfferRequestId) {
        this.specialOfferRequestId = specialOfferRequestId;
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

    public double getSpecialPrice() {
        return specialPrice;
    }

    public void setSpecialPrice(double specialPrice) {
        this.specialPrice = specialPrice;
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

    public int getCreatedByVendor() {
        return createdByVendor;
    }

    public void setCreatedByVendor(int createdByVendor) {
        this.createdByVendor = createdByVendor;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public Date getOfferStart() {
        return offerStart;
    }

    public void setOfferStart(Date offerStart) {
        this.offerStart = offerStart;
    }

    public Date getOfferEnd() {
        return offerEnd;
    }

    public void setOfferEnd(Date offerEnd) {
        this.offerEnd = offerEnd;
    }
}
