package q.rest.product.model.qvm;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name="prd_vendor_stock_upload_request")
@Entity
public class VendorUploadRequest implements Serializable {

    @Id
    @SequenceGenerator(name = "prd_vendor_stock_upload_request_id_seq_gen", sequenceName = "prd_vendor_stock_upload_request_id_seq", initialValue=1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "prd_vendor_stock_upload_request_id_seq_gen")
    @Column(name="id")
    private int id;
    @Column(name="vendor_id")
    private int vendorId;
    @Column(name="created_by")
    private int createdBy;
    @Column(name="upload_source")
    private char uploadSource;
    @Column(name="status")
    private char status;//R = requested, C = compelted, E = error
    @Column(name="created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @Column(name="completed")
    @Temporal(TemporalType.TIMESTAMP)
    private Date completed;
    @Column(name = "error_message")
    private String errorMessage;
    @Column(name="error_message_ar")
    private String errorMessageAr;
    @Column(name="branch_id")
    private int branchId;
    @Column(name="number_of_items")
    private int numberOfItems;
    @Column(name="extension")
    private String extension;
    @Column(name="mime_type")
    private String mimeType;
    @Column(name="approved_by")
    private int approvedBy;

    public int getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(int approvedBy) {
        this.approvedBy = approvedBy;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public int getNumberOfItems() {
        return numberOfItems;
    }

    public void setNumberOfItems(int numberOfItems) {
        this.numberOfItems = numberOfItems;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public char getUploadSource() {
        return uploadSource;
    }

    public void setUploadSource(char uploadSource) {
        this.uploadSource = uploadSource;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessageAr() {
        return errorMessageAr;
    }

    public void setErrorMessageAr(String errorMessageAr) {
        this.errorMessageAr = errorMessageAr;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public char getStatus() {
        return status;
    }

    public void setStatus(char status) {
        this.status = status;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getCompleted() {
        return completed;
    }

    public void setCompleted(Date completed) {
        this.completed = completed;
    }
}
