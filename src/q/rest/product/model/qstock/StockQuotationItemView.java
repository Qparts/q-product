package q.rest.product.model.qstock;

import com.fasterxml.jackson.annotation.JsonIgnore;
import q.rest.product.model.qstock.views.StockProductView;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="prd_stk_quotation_order_item")
public class StockQuotationItemView implements Serializable {
    @Id
    private int id;
    @Column(name="stock_product_id")
    private long stockProductId;
    @Transient
    private StockProductView stockProduct;
    @JsonIgnore
    @Column(name="quotation_order_id")
    private int quotationOrderId;
    private int quantity;
    private double unitPrice;

    public long getStockProductId() {
        return stockProductId;
    }

    public void setStockProductId(long stockProductId) {
        this.stockProductId = stockProductId;
    }

    public void setStockProduct(StockProductView stockProduct) {
        this.stockProduct = stockProduct;
    }

    public StockProductView getStockProduct() {
        return stockProduct;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuotationOrderId() {
        return quotationOrderId;
    }

    public void setQuotationOrderId(int quotationOrderId) {
        this.quotationOrderId = quotationOrderId;
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
