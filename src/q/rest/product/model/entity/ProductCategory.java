package q.rest.product.model.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "prd_product_category")
@IdClass(ProductCategory.ProductCategoryPK.class)
public class ProductCategory implements Serializable {

	@Id
	@Column(name = "product_id")
	private long productId;

	@Id
	@Column(name = "category_id")
	private int categoryId;


	public ProductCategory(long productId, int categoryId) {
		this.productId = productId;
		this.categoryId = categoryId;
	}


	public long getProductId() {
		return productId;
	}

	public void setProductId(long productId) {
		this.productId = productId;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ProductCategory that = (ProductCategory) o;
		return productId == that.productId &&
				categoryId == that.categoryId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(productId, categoryId);
	}

	public static class ProductCategoryPK implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		protected long productId;
		protected int categoryId;

		public ProductCategoryPK() {
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			ProductCategoryPK that = (ProductCategoryPK) o;
			return productId == that.productId &&
					categoryId == that.categoryId;
		}

		@Override
		public int hashCode() {
			return Objects.hash(productId, categoryId);
		}
	}
}
