package q.rest.product.model.qstock;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name="prd_stk_product_shelf")
@IdClass(StockProductShelf.StockProductShelfPK.class)
public class StockProductShelf implements Serializable {
    @Id
    private long productId;
    @Id
    private int branchId;
    @Id
    private int companyId;
    private String shelf;

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
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
        protected long productId;
        protected int companyId;

        public StockProductShelfPK() {}


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StockProductShelfPK that = (StockProductShelfPK) o;
            return branchId == that.branchId &&
                    productId == that.productId &&
                    companyId == that.companyId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(branchId, productId, companyId);
        }
    }
}
