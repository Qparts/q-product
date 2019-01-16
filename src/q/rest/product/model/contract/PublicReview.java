package q.rest.product.model.contract;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Table(name="prd_product_review")
@Entity
public class PublicReview implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="id")
    private long id;
    @Column(name="customer_id")
    private Long customerId;
    @Column(name="customer_name")
    private String customerName;
    @Column(name="rating")
    private Integer rating;
    @Column(name="text")
    private String text;
    @Column(name="product_id")
    private long productId;
    @Column(name="created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @Column(name="status")
    @JsonIgnore
    private char status;


    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
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
        PublicReview that = (PublicReview) o;
        return id == that.id &&
                productId == that.productId &&
                status == that.status &&
                Objects.equals(customerId, that.customerId) &&
                Objects.equals(rating, that.rating) &&
                Objects.equals(text, that.text) &&
                Objects.equals(created, that.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customerId, rating, text, productId, created, status);
    }
}
