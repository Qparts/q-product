package q.rest.product.model.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.*;

@Table(name="prd_category_specification")
@Entity 
@IdClass(CategorySpec.CategorySpecificationPK.class)
public class CategorySpec implements Serializable {

	private static final long serialVersionUID = 1L;	
	@Id
	@Column(name="spec_id")
	private long specId;

	@Id
	@Column(name="category_id")
	private int categoryId;

	public CategorySpec(long specId, int categoryId) {
		this.specId = specId;
		this.categoryId = categoryId;
	}

	public CategorySpec() {
	}

	public long getSpecId() {
		return specId;
	}

	public void setSpecId(int specId) {
		this.specId = specId;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public static class CategorySpecificationPK implements Serializable{

		private static final long serialVersionUID = 1L;
		protected long specId;
		protected int categoryId;

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			CategorySpecificationPK that = (CategorySpecificationPK) o;
			return specId == that.specId &&
					categoryId == that.categoryId;
		}

		@Override
		public int hashCode() {
			return Objects.hash(specId, categoryId);
		}

		public long getSpecId() {
			return specId;
		}

		public void setSpecId(long specId) {
			this.specId = specId;
		}

		public int getCategoryId() {
			return categoryId;
		}

		public void setCategoryId(int categoryId) {
			this.categoryId = categoryId;
		}
	}
}
