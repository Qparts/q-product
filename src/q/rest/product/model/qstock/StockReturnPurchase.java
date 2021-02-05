package q.rest.product.model.qstock;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Table(name = "prd_stk_purchase_return")
@Entity
public class StockReturnPurchase implements Serializable {
    @Id
    @SequenceGenerator(name = "prd_stk_purchase_return_id_seq_gen", sequenceName = "prd_stk_purchase_return_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prd_stk_purchase_return_id_seq_gen")
    private int id;
    @Column(name="purchase_id")
    private int purchaseId;
    private int branchId;
    private double deliveryCharge;//ok
    private char transactionType;//ok
    private Character paymentMethod;//ok
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;//ok
    private String reference;//ok
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "purchase_return_id")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<StockReturnPurchaseItem> items;

    @JsonIgnore
    public double getTotalAmount(double taxRate) {
        double total = deliveryCharge;
        for (var item : items) {
            total += (item.getQuantity() * item.getPurchaseItem().getUnitPrice()) + (item.getQuantity() * item.getPurchaseItem().getUnitPrice() * taxRate);
        }
        return total;
    }

    public int getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(int purchaseId) {
        this.purchaseId = purchaseId;
    }

    public void setItems(List<StockReturnPurchaseItem> items) {
        this.items = items;
    }

    public List<StockReturnPurchaseItem> getItems() {
        return items;
    }

    public double getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(double deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public char getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(char transactionType) {
        this.transactionType = transactionType;
    }

    public Character getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(Character paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
