package q.rest.product.model.qstock;

import com.fasterxml.jackson.annotation.JsonIgnore;
import q.rest.product.model.qstock.views.StockProductView;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name="prd_stk_sales_order_item")
public class StockSalesItemView implements Serializable {
    @Id
    private int id;
    @JsonIgnore
    @Column(name="stock_product_id")
    private long stockProductId;
    @Transient
    private StockProductView stockProduct;
    @JsonIgnore
    @Column(name="sales_order_id")
    private int salesOrderId;
    private int quantity;
    private double unitPrice;
    private double unitCost;

    public long getStockProductId() {
        return stockProductId;
    }

    public void setStockProductId(long stockProductId) {
        this.stockProductId = stockProductId;
    }

    public StockProductView getStockProduct() {
        return stockProduct;
    }

    public void setStockProduct(StockProductView stockProduct) {
        this.stockProduct = stockProduct;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockSalesItemView that = (StockSalesItemView) o;
        return id == that.id &&
                salesOrderId == that.salesOrderId &&
                quantity == that.quantity &&
                Double.compare(that.unitPrice, unitPrice) == 0 &&
                Double.compare(that.unitCost, unitCost) == 0 &&
                Objects.equals(stockProduct, that.stockProduct) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, stockProduct, salesOrderId, quantity, unitPrice, unitCost);
    }
}
