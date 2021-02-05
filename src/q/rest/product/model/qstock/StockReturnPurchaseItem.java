package q.rest.product.model.qstock;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "prd_stk_purchase_return_item")
@Entity
public class StockReturnPurchaseItem implements Serializable {
    @Id
    @SequenceGenerator(name = "prd_stk_purchase_return_item_id_seq_gen", sequenceName = "prd_stk_purchase_return_item_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prd_stk_purchase_return_item_id_seq_gen")
    private int id;
    @Column(name="purchase_return_id")
    private int purchaseReturnId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="purchase_item_id")
    private StockPurchaseItem purchaseItem;
    private int quantity;
    private double unitAverageCost;

    public int getPurchaseReturnId() {
        return purchaseReturnId;
    }

    public void setPurchaseReturnId(int purchaseReturnId) {
        this.purchaseReturnId = purchaseReturnId;
    }

    public double getUnitAverageCost() {
        return unitAverageCost;
    }

    public void setUnitAverageCost(double unitAverageCost) {
        this.unitAverageCost = unitAverageCost;
    }

    public StockPurchaseItem getPurchaseItem() {
        return purchaseItem;
    }

    public void setPurchaseItem(StockPurchaseItem purchaseItem) {
        this.purchaseItem = purchaseItem;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
