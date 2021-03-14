package q.rest.product.model.qstock;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name="prd_stk_product_setting")
@IdClass(StockProductSetting.StockProductSettingPK.class)
public class StockProductSetting implements Serializable {

    @Id
    private long productId;
    @Id
    @JsonIgnore
    private int companyId;
    private int policyId;
    private int shortageFlag;
    private String notes;

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

    public int getPolicyId() {
        return policyId;
    }

    public void setPolicyId(int policyId) {
        this.policyId = policyId;
    }

    public int getShortageFlag() {
        return shortageFlag;
    }

    public void setShortageFlag(int shortageFlag) {
        this.shortageFlag = shortageFlag;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public static class StockProductSettingPK implements Serializable{
        protected int companyId;
        protected long productId;

        public StockProductSettingPK() {}

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StockProductSettingPK that = (StockProductSettingPK) o;
            return companyId == that.companyId &&
                    productId == that.productId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(companyId, productId);
        }
    }
}
