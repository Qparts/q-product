package q.rest.product.model.qstock;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Table(name = "prd_stk_sales_return")
@Entity
public class StockReturnSalesStandAlone implements Serializable {
    @Id
    private int id;
    @Column(name="sales_id")
    private int salesId;
    private int branchId;
    private double deliveryCharge;
    private char transactionType;
    private Character paymentMethod;
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    private String reference;
    @Transient
    private Object customer;
    @Transient
    private int customerId;
    @Transient
    private double taxRate;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "sales_return_id")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<StockReturnSalesItem> items;

    @JsonIgnore
    public double getTotalAmount(double taxRate) {
        double total = deliveryCharge;
        for (var item : items) {
            total += (item.getQuantity() * item.getSalesItem().getUnitPrice()) + (item.getQuantity() * item.getSalesItem().getUnitPrice() * taxRate);
        }
        return total;
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

    public List<StockReturnSalesItem> getItems() {
        return items;
    }

    public void setItems(List<StockReturnSalesItem> items) {
        this.items = items;
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

    public int getSalesId() {
        return salesId;
    }

    public void setSalesId(int salesId) {
        this.salesId = salesId;
    }

    public Object getCustomer() {
        return customer;
    }

    public void setCustomer(Object customer) {
        this.customer = customer;
    }

    public double getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(double taxRate) {
        this.taxRate = taxRate;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
}
