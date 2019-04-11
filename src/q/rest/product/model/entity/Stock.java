package q.rest.product.model.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="prd_stock")
public class Stock implements Serializable {

    @Id
    @SequenceGenerator(name = "prd_stock_id_seq_gen", sequenceName = "prd_stock_id_seq", initialValue=1000, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prd_stock_id_seq_gen")
    @Column(name="id", updatable = false)
    private long id;

    @Column(name="product_id")
    private long productId;

    @Column(name="purchase_id")
    private long purchaseId;

    @Column(name="purchase_product_id")
    private long purchaseProductId;

    @Column(name="quantity")
    private int quantity;

    @Column(name="cost_actual")
    private double costActual;

    @Column(name="created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name="created_by")
    private int createdBy;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public long getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(long purchaseId) {
        this.purchaseId = purchaseId;
    }

    public long getPurchaseProductId() {
        return purchaseProductId;
    }

    public void setPurchaseProductId(long purchaseProductId) {
        this.purchaseProductId = purchaseProductId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getCostActual() {
        return costActual;
    }

    public void setCostActual(double costActual) {
        this.costActual = costActual;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }
}
