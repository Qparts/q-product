package q.rest.product.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import q.rest.product.model.contract.PublicSpec;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Table(name="prd_product_specification")
@Entity
@IdClass(ProductSpec.ProductSpecPK.class)
public class ProductSpec implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@JoinColumn(name="spec_id", referencedColumnName="id")
	@ManyToOne
	private Spec spec;
	@Id
	@Column(name="product_id")
	private long productId;
	@Column(name="value")
	private String value;
	@Column(name="value_ar")
	private String valueAr;
	@Column(name="created")
	private Date created;
	@Column(name="created_by")
	private int createdBy;
	@Column(name="status")
	private char status;

	@JsonIgnore
	public PublicSpec getPublicSpec(){
		PublicSpec ps = new PublicSpec();
		ps.setSpecKey(this.spec.getName());
		ps.setSpecKeyAr(this.spec.getNameAr());
		ps.setSpecValue(this.value);
		ps.setSpecValueAr(this.valueAr);
		return ps;
	}

	public Spec getSpec() {
		return spec;
	}

	public void setSpec(Spec spec) {
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
				createdBy == that.createdBy &&
				status == that.status &&
				Objects.equals(spec, that.spec) &&
				Objects.equals(value, that.value) &&
				Objects.equals(valueAr, that.valueAr) &&
				Objects.equals(created, that.created);
	}

	@Override
	public int hashCode() {
		return Objects.hash(spec, productId, value, valueAr, created, createdBy, status);
	}

	public static class ProductSpecPK implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		protected long spec;
		protected long productId;
		
		public ProductSpecPK() {}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (productId ^ (productId >>> 32));
			result = prime * result + (int) (spec ^ (spec >>> 32));
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
			if (spec != other.spec)
				return false;
			return true;
		}		
		
		
	}
}
