package q.rest.product.model.qvm;

import java.util.Date;

public class QvmObjectStock {
    private QvmBranch branch;
    private Integer quantity;

    public QvmObjectStock() {
    }

    public QvmObjectStock(String name, String id, String cityName, Integer quantity) {
        this.branch = new QvmBranch(name, id, cityName);
        this.quantity = quantity;
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
