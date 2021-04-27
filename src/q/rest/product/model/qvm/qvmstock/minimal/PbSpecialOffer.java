package q.rest.product.model.qvm.qvmstock.minimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Table(name="prd_company_special_offer_upload_request")
@Entity
public class PbSpecialOffer {
    @Id
    private int id;
    private String offerName;
    private String offerNameAr;
    private String notes;
    private int companyId;
    @JsonIgnore
    private char status;//R = requested, C = compelted, E = error
    private int branchId;
    private int numberOfItems;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="offer_start_date")
    private Date startDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="offer_end_date")
    private Date endDate;
    @Transient
    private List<PbCompanyProduct> products = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public char getStatus() {
        return status;
    }

    public void setStatus(char status) {
        this.status = status;
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

    public List<PbCompanyProduct> getProducts() {
        return products;
    }

    public void setProducts(List<PbCompanyProduct> products) {
        this.products = products;
    }
}
