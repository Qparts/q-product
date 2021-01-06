package q.rest.product.model.qstock;

import q.rest.product.model.entity.v3.product.ProductSpec;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table
@IdClass(StockProductShelf.StockProductShelfPK.class)
public class StockProductShelf implements Serializable {
    @Id
    private long stockProductId;
    @Id
    private int branchId;
    private String shelf;

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

    public String getShelf() {
        return shelf;
    }

    public void setShelf(String shelf) {
        this.shelf = shelf;
    }


    public static class StockProductShelfPK implements Serializable{
        protected int branchId;
        protected long stockProductId;

        public StockProductShelfPK() {}

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StockProductShelfPK that = (StockProductShelfPK) o;
            return branchId == that.branchId &&
                    stockProductId == that.stockProductId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(branchId, stockProductId);
        }
    }
}
