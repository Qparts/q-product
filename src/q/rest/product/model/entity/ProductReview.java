package q.rest.product.model.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Table(name="prd_product_review")
@Entity
public class ProductReview implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "prd_product_review_id_seq_gen", sequenceName = "prd_product_review_id_seq", initialValue=1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prd_product_review_id_seq_gen")
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
    private char status;//A = approved, P = pending, R = rejected
    @Column(name="reviewed_by")
    private Integer reviewedBy;
    @Column(name="reviewed_on")
    @Temporal(TemporalType.TIMESTAMP)
    private Date reviewedOn;

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

    public Integer getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(Integer reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public Date getReviewedOn() {
        return reviewedOn;
    }

    public void setReviewedOn(Date reviewedOn) {
        this.reviewedOn = reviewedOn;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductReview review = (ProductReview) o;
        return id == review.id &&
                productId == review.productId &&
                status == review.status &&
                Objects.equals(customerId, review.customerId) &&
                Objects.equals(rating, review.rating) &&
                Objects.equals(text, review.text) &&
                Objects.equals(created, review.created) &&
                Objects.equals(reviewedBy, review.reviewedBy) &&
                Objects.equals(reviewedOn, review.reviewedOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customerId, rating, text, productId, created, status, reviewedBy, reviewedOn);
    }
}
