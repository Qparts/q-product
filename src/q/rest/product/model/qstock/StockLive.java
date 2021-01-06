package q.rest.product.model.qstock;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="prd_stk_live_stock")
public class StockLive implements Serializable {
    @Id
    @SequenceGenerator(name = "prd_stk_product_id_seq_gen", sequenceName = "prd_stk_product_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prd_stk_product_id_seq_gen")
    private long id;
    @JsonIgnore
    @Column(name = "stock_product_id")
    private long stockProductId;
    private int branchId;
    private int quantity;
    private double averagedCost;
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date lastUpdated;

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getStockProductId() {
        return stockProductId;
    }

    public void setStockProductId(long stockProductId) {
        this.stockProductId = stockProductId;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public double getAveragedCost() {
        return averagedCost;
    }

    public void setAveragedCost(double averagedCost) {
        this.averagedCost = averagedCost;
    }
}
