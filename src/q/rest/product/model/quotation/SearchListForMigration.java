package q.rest.product.model.quotation;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SearchListForMigration implements Serializable{
    private long id;
    private int subscriberId;
    private int companyId;
    private int targetCompanyId;
    private long created;
    private char status;
    private List<SearchListItemForMigration> quotationItems = new ArrayList<>();

    public List<SearchListItemForMigration> getQuotationItems() {
        return quotationItems;
    }

    public void setQuotationItems(List<SearchListItemForMigration> quotationItems) {
        this.quotationItems = quotationItems;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(int subscriberId) {
        this.subscriberId = subscriberId;
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

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public char getStatus() {
        return status;
    }

    public void setStatus(char status) {
        this.status = status;
    }
}
