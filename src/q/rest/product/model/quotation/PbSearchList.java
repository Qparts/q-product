package q.rest.product.model.quotation;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Table(name="prd_search_list")
@Entity
public class PbSearchList implements Serializable{
    @Id
    private long id;
    @JsonIgnore
    private int companyId;
    @JsonIgnore
    private int targetCompanyId;
    @JsonIgnore
    private char status;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @Transient
    private Object company;
    @Transient
    private List<PbSearchListItem> items = new ArrayList<>();

    public Object getCompany() {
        return company;
    }

    public void setCompany(Object company) {
        this.company = company;
    }

    public List<PbSearchListItem> getItems() {
        return items;
    }

    public void setItems(List<PbSearchListItem> items) {
        this.items = items;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getTargetCompanyId() {
        return targetCompanyId;
    }

    public void setTargetCompanyId(int targetCompanyId) {
        this.targetCompanyId = targetCompanyId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public char getStatus() {
        return status;
    }

    public void setStatus(char status) {
        this.status = status;
    }
}
