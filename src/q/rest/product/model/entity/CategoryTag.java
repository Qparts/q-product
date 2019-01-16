package q.rest.product.model.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.*;

@Table(name="prd_category_tag")
@Entity 
@IdClass(CategoryTag.CategoryTagPK.class)
public class CategoryTag implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "tag")
	private String tag;

	@Id
	@Column(name="category_id")
	private int categoryId;

	public CategoryTag() {
	}

	public CategoryTag(String tag, int categoryId) {
		this.tag = tag;
		this.categoryId = categoryId;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
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
		CategoryTag that = (CategoryTag) o;
		return categoryId == that.categoryId &&
				Objects.equals(tag, that.tag);
	}

	@Override
	public int hashCode() {
		return Objects.hash(tag, categoryId);
	}

	public static class CategoryTagPK implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		protected String tag;
		protected int categoryId;

		public String getTag() {
			return tag;
		}

		public void setTag(String tag) {
			this.tag = tag;
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
			CategoryTagPK that = (CategoryTagPK) o;
			return categoryId == that.categoryId &&
					Objects.equals(tag, that.tag);
		}

		@Override
		public int hashCode() {
			return Objects.hash(tag, categoryId);
		}
	}
}
