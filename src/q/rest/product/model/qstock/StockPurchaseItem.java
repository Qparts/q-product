package q.rest.product.model.qstock;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="prd_stk_purchase_order_item")
public class StockPurchaseItem implements Serializable {
    @Id
    @SequenceGenerator(name = "prd_stk_purchase_order_item_id_seq_gen", sequenceName = "prd_stk_purchase_order_item_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prd_stk_purchase_order_item_id_seq_gen")
    private int id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="stock_product_id")
    private StockProduct stockProduct;
    @JsonIgnore
    @Column(name="purchase_order_id")
    private int purchaseOrderId;
    private int quantity;
    private double unitPrice;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public StockProduct getStockProduct() {
        return stockProduct;
    }

    public void setStockProduct(StockProduct stockProduct) {
        this.stockProduct = stockProduct;
    }

    public int getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(int purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
}
