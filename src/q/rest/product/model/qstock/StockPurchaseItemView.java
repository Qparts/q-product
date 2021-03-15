package q.rest.product.model.qstock;

import com.fasterxml.jackson.annotation.JsonIgnore;
import q.rest.product.model.qstock.views.StockProductView;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="prd_stk_purchase_order_item")
public class StockPurchaseItemView implements Serializable {
    @Id
    private int id;
    @JsonIgnore
    @Column(name="stock_product_id")
    private long stockProductId;
    @Transient
    private StockProductView stockProduct;
    @JsonIgnore
    @Column(name="purchase_order_id")
    private int purchaseOrderId;
    private int quantity;
    private double unitPrice;

    public long getStockProductId() {
        return stockProductId;
    }

    public void setStockProductId(long stockProductId) {
        this.stockProductId = stockProductId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public StockProductView getStockProduct() {
        return stockProduct;
    }

    public void setStockProduct(StockProductView stockProduct) {
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
