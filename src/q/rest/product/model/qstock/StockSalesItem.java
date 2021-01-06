package q.rest.product.model.qstock;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="prd_stk_sales_order_item")
public class StockSalesItem implements Serializable {
    @Id
    @SequenceGenerator(name = "prd_stk_sales_order_item_id_seq_gen", sequenceName = "prd_stk_sales_order_item_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prd_stk_sales_order_item_id_seq_gen")
    private int id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="stock_product_id")
    private StockProduct stockProduct;
    @JsonIgnore
    @Column(name="sales_order_id")
    private int salesOrderId;
    private int quantity;
    private double unitPrice;
    private double unitCost;
    @Transient
    @JsonIgnore
    private StockLive live;

    public StockLive getLive() {
        return live;
    }

    public void setLive(StockLive live) {
        this.live = live;
    }

    public double getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(double unitCost) {
        this.unitCost = unitCost;
    }

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

    public int getSalesOrderId() {
        return salesOrderId;
    }

    public void setSalesOrderId(int salesOrderId) {
        this.salesOrderId = salesOrderId;
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
