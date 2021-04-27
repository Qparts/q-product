package q.rest.product.model.product.market;

import com.fasterxml.jackson.annotation.JsonIgnore;
import q.rest.product.model.product.full.ProductSpec;
import q.rest.product.model.product.full.Spec;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "prd_product_specification")
@Entity
@IdClass(MarketProductSpec.MarketProductSpecPK.class)
public class MarketProductSpec {

    @Id
    @JoinColumn(name="spec_id", insertable = false, updatable = false)
    @ManyToOne
    private MarketSpec spec;
    @Id
    @Column(name = "product_id")
    private long productId;
    private String value;
    private String valueAr;
    @JsonIgnore
    private char status;

    public MarketSpec getSpec() {
        return spec;
    }

    public void setSpec(MarketSpec spec) {
        this.spec = spec;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValueAr() {
        return valueAr;
    }

    public void setValueAr(String valueAr) {
        this.valueAr = valueAr;
    }

    public char getStatus() {
        return status;
    }

    public void setStatus(char status) {
        this.status = status;
    }


    public static class MarketProductSpecPK implements Serializable {
        protected int spec;
        protected long productId;

        public MarketProductSpecPK() {}

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (int) (productId ^ (productId >>> 32));
            result = prime * result + (spec ^ (spec >>> 32));
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            MarketProductSpec.MarketProductSpecPK other = (MarketProductSpec.MarketProductSpecPK) obj;
            if (productId != other.productId)
                return false;
            if (spec != other.spec)
                return false;
            return true;
        }


    }
}
