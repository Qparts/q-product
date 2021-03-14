package q.rest.product.model.qstock;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name="prd_stk_live_stock")
@IdClass(StockLive.StockLivePK.class)
public class StockLive implements Serializable {
    @JsonIgnore
    @Id
    @Column(name = "product_id")
    private long productId;
    @JsonIgnore
    @Id
    @Column(name = "company_id")
    private int companyId;
    @Id
    @Column(name = "branch_id")
    private int branchId;
    private int quantity;
    private double averageCost;

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
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


    public double getAverageCost() {
        return averageCost;
    }

    public void setAverageCost(double averagedCost) {
        this.averageCost = averagedCost;
    }



    public static class StockLivePK implements Serializable{
        protected int companyId;
        protected long productId;
        protected int branchId;

        public StockLivePK() {}

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StockLivePK that = (StockLivePK) o;
            return companyId == that.companyId &&
                    productId == that.productId &&
                    branchId == that.branchId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(companyId, productId, branchId);
        }
    }
}
