package q.rest.product.model.qvm;

import java.util.Date;

public class QvmAvailabilityRemote {
    private QvmBranch branch;
    private Integer quantity;
    private Date offerEnd;

    public QvmAvailabilityRemote() {
    }

    public QvmAvailabilityRemote(String name, String id, String cityName, Integer quantity) {
        this.branch = new QvmBranch(name, id, cityName);
        this.quantity = quantity;
    }

    public Date getOfferEnd() {
        return offerEnd;
    }

    public void setOfferEnd(Date offerEnd) {
        this.offerEnd = offerEnd;
    }

    public QvmBranch getBranch() {
        return branch;
    }

    public void setBranch(QvmBranch branch) {
        this.branch = branch;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
