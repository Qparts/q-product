package q.rest.product.model.product.market;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="prd_market_order_item")
public class MarketOrderItem implements Serializable {
    @Id
    @SequenceGenerator(name = "prd_market_order_item_id_seq_gen", sequenceName = "prd_market_order_item_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prd_market_order_item_id_seq_gen")
    private long id;
    @Column(name = "market_product_id")
    private long marketProductId;
    @Column(name = "order_id")
    private int orderId;
    private int quantity;
    private double salesPrice;
    private double costPrice;

    public double getSalesPrice() {
        return salesPrice;
    }

    public void setSalesPrice(double salesPrice) {
        this.salesPrice = salesPrice;
    }

    public double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(double costPrice) {
        this.costPrice = costPrice;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMarketProductId() {
        return marketProductId;
    }

    public void setMarketProductId(long marketProductId) {
        this.marketProductId = marketProductId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
