package q.rest.product.model.quotation;

import q.rest.product.model.qvm.qvmstock.minimal.PbCompanyProduct;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name="prd_search_list")
@Entity
public class SearchList implements Serializable{
    @Id
    @SequenceGenerator(name = "prd_search_list_id_seq_gen", sequenceName = "prd_search_list_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prd_search_list_id_seq_gen")
    private long id;
    private int subscriberId;
    private int companyId;
    private int targetCompanyId;
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    private char status;

    public SearchList(){

    }

    public SearchList(int companyId, int subscriberId, int targetCompanyId){
        this.setCompanyId(companyId);
        this.setTargetCompanyId(targetCompanyId);
        this.setSubscriberId(subscriberId);
        this.setCreated(new Date());
        this.setStatus('N');
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
