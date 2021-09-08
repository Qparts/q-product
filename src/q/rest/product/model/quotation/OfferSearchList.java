package q.rest.product.model.quotation;

import javax.persistence.*;
import java.util.Date;

@Table(name="prd_offer_search_list")
@Entity
public class OfferSearchList {
    @Id
    @SequenceGenerator(name = "prd_offer_search_list_id_seq_gen", sequenceName = "prd_offer_search_list_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prd_offer_search_list_id_seq_gen")
    private long id;
    private int subscriberId;
    private int companyId;
    private int offerId;
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    private int numberOfProducts;
    private int page;
    private String filter;

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

    public int getOfferId() {
        return offerId;
    }

    public void setOfferId(int offerId) {
        this.offerId = offerId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public int getNumberOfProducts() {
        return numberOfProducts;
    }

    public void setNumberOfProducts(int numberOfProducts) {
        this.numberOfProducts = numberOfProducts;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }
}
