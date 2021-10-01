package q.rest.product.model.qvm.qvmstock;

import q.rest.product.model.product.full.ProductSpec;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name="prd_special_offer_brand_tag")
@IdClass(OfferBrandTag.OfferBrandTagPK.class)
public class OfferBrandTag implements Serializable {
    @Id
    @Column(name="tag")
    private String tag;
    @Id
    @Column(name = "offer")
    private int offer;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getOffer() {
        return offer;
    }

    public void setOffer(int offer) {
        this.offer = offer;
    }

    public static class OfferBrandTagPK implements Serializable{
        protected String tag;
        protected int offer;

        public OfferBrandTagPK() {
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            OfferBrandTagPK that = (OfferBrandTagPK) o;
            return offer == that.offer &&
                    Objects.equals(tag, that.tag);
        }

        @Override
        public int hashCode() {
            return Objects.hash(tag, offer);
        }
    }
}
