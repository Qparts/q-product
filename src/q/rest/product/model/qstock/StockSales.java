package q.rest.product.model.qstock;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Table(name="prd_stk_sales_order")
@Entity
public class StockSales implements Serializable {
    @Id
    @SequenceGenerator(name = "prd_stk_sales_order_id_seq_gen", sequenceName = "prd_stk_sales_order_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prd_stk_sales_order_id_seq_gen")
    private int id;
    @JsonIgnore
    private int companyId;
    private int customerId;
    private Date created;
    private double deliveryCharge;
    private char transactionType;//C = cash, T = credit
    private Character paymentMethod;//S = span, C = Cash, T = transfer, O = online, Q = cheque, NULL if transaction is credit
    private String reference;
    private double taxRate;
    private int branchId;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "sales_order_id")
    private List<StockSalesItem> items;

    public int getId() {
        return id;
    }

    @JsonIgnore
    public double getTotalAmount() {
        double total = deliveryCharge;
        for (var item : items ) {
            total += (item.getQuantity() * item.getUnitPrice()) + (item.getQuantity() * item.getUnitPrice() * taxRate);
        }
        return total;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public double getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(double deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
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

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public double getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(double taxRate) {
        this.taxRate = taxRate;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public List<StockSalesItem> getItems() {
        return items;
    }

    public void setItems(List<StockSalesItem> items) {
        this.items = items;
    }
}
