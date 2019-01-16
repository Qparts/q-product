package q.rest.product.model.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "prd_product_tag")
@IdClass(ProductTag.ProductTagPK.class)
public class ProductTag implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="product_id")
	private long productId;
	@Id
	@Column(name = "tag")
	private String tag;


	public ProductTag(String tag, long productId) {
		this.tag = tag;
		this.productId = productId;
	}

	public long getProductId() {
		return productId;
	}

	public void setProductId(long productId) {
		this.productId = productId;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public static class ProductTagPK implements Serializable {
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;
		protected long productId;
		protected String tag;

		public long getProductId() {
			return productId;
		}

		public void setProductId(long productId) {
			this.productId = productId;
		}

		public String getTag() {
			return tag;
		}

		public void setTag(String tag) {
			this.tag = tag;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			ProductTagPK that = (ProductTagPK) o;
			return productId == that.productId &&
					Objects.equals(tag, that.tag);
		}

		@Override
		public int hashCode() {
			return Objects.hash(productId, tag);
		}
	}

}
