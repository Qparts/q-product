package q.rest.product.model.qvm.qvmstock;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Table(name="prd_company_special_offer_upload_request")
@Entity
public class CompanyOfferUploadRequest implements Serializable {
    @Id
    @SequenceGenerator(name = "prd_company_special_offer_upload_request_id_seq_gen", sequenceName = "prd_company_special_offer_upload_request_id_seq", initialValue=1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "prd_company_special_offer_upload_request_id_seq_gen")
    private int id;
    private String offerName;
    private String offerNameAr;
    private String notes;
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
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="offer_start_date")
    private Date startDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="offer_end_date")
    private Date endDate;
    @Transient
    private List<CompanyProduct> products;

    public List<CompanyProduct> getProducts() {
        return products;
    }

    public void setProducts(List<CompanyProduct> products) {
        this.products = products;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

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

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getCreatedBySubscriber() {
        return createdBySubscriber;
    }

    public void setCreatedBySubscriber(int createdBySubscriber) {
        this.createdBySubscriber = createdBySubscriber;
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

    public String getOfferName() {
        return offerName;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }

    public String getOfferNameAr() {
        return offerNameAr;
    }

    public void setOfferNameAr(String offerNameAr) {
        this.offerNameAr = offerNameAr;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
