package q.rest.product.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "prd_variant")
@IdClass(Variant.VariantPK.class)
public class Variant implements Serializable {

    @Id
    @Column(name = "product_id")
    private long productId;

    @Id
    @Column(name = "variant_id")
    private long variantId;


    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public long getVariantId() {
        return variantId;
    }

    public void setVariantId(long variantId) {
        this.variantId = variantId;
    }


    public static class VariantPK implements Serializable {
        protected long productId;
        protected int variantId;

        public VariantPK() {
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Variant.VariantPK that = (Variant.VariantPK) o;
            return productId == that.productId &&
                    variantId == that.variantId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(productId, variantId);
        }
    }
}
