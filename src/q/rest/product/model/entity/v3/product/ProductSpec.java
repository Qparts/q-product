package q.rest.product.model.entity.v3.product;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Table(name="prd_product_specification")
@Entity
@IdClass(ProductSpec.ProductSpecPK.class)
public class ProductSpec implements Serializable {
	
	@Id
	private int specId;
	@Id
	@Column(name = "product_id")
	private long productId;
	private String value;
	private String valueAr;
	private Date created;
	private int createdBy;
	private char status;


	public int getSpecId() {
		return specId;
	}

	public void setSpecId(int specId) {
		this.specId = specId;
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

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public int getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}

	public char getStatus() {
		return status;
	}

	public void setStatus(char status) {
		this.status = status;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ProductSpec that = (ProductSpec) o;
		return productId == that.productId &&
				status == that.status &&
				Objects.equals(specId, that.specId) &&
				Objects.equals(value, that.value) &&
				Objects.equals(valueAr, that.valueAr);
	}

	@Override
	public int hashCode() {
		return Objects.hash(specId, productId, value, valueAr, status);
	}

	public static class ProductSpecPK implements Serializable{
		protected int specId;
		protected long productId;
		
		public ProductSpecPK() {}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (productId ^ (productId >>> 32));
			result = prime * result + (specId ^ (specId >>> 32));
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
			ProductSpecPK other = (ProductSpecPK) obj;
			if (productId != other.productId)
				return false;
			if (specId != other.specId)
				return false;
			return true;
		}		
		
		
	}
}
