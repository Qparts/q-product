package q.rest.product.model.entity.v3.stock;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name="prd_company_stock_upload_request")
@Entity
public class CompanyUploadRequest implements Serializable {

    @Id
    @SequenceGenerator(name = "prd_company_stock_upload_request_id_seq_gen", sequenceName = "prd_company_stock_upload_request_id_seq", initialValue=1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "prd_company_stock_upload_request_id_seq_gen")
    private int id;
    private int companyId;
    private int createdBy;
    private int createdBySubscriber;
    private char uploadSource;
    private char status;//R = requested, C = compelted, E = error
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @Temporal(TemporalType.TIMESTAMP)
    private Date completed;
    private String errorMessage;
    private String errorMessageAr;
    private int branchId;
    private int numberOfItems;
    private String extension;
    private String mimeType;
    private int approvedBy;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public int getCreatedBySubscriber() {
        return createdBySubscriber;
    }

    public void setCreatedBySubscriber(int createdBySubscriber) {
        this.createdBySubscriber = createdBySubscriber;
    }

    public char getUploadSource() {
        return uploadSource;
    }

    public void setUploadSource(char uploadSource) {
        this.uploadSource = uploadSource;
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

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public int getNumberOfItems() {
        return numberOfItems;
    }

    public void setNumberOfItems(int numberOfItems) {
        this.numberOfItems = numberOfItems;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public int getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(int approvedBy) {
        this.approvedBy = approvedBy;
    }
}
