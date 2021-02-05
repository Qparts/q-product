package q.rest.product.model.qstock;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Table(name="prd_stk_quotation_order")
@Entity
public class StockQuotation implements Serializable {
    @Id
    @SequenceGenerator(name = "prd_stk_quotation_order_id_seq_gen", sequenceName = "prd_stk_quotation_order_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prd_stk_quotation_order_id_seq_gen")
    private int id;
    @JsonIgnore
    private int companyId;
    private int customerId;
    @Transient
    private Object customer;
    private Date created;
    private double deliveryCharge;
    private char transactionType;//C = cash, T = credit
    private Character paymentMethod;//S = span, C = Cash, T = transfer, O = online, Q = cheque, NULL if transaction is credit
    private String reference;
    private double taxRate;
    private int branchId;
    private double quotationPrice;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "quotation_order_id")
    private List<StockQuotationItem> items;

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


    @JsonIgnore
    public void attachCustomer(Map<String, Object> cst) {
        this.customer = cst;
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

    public List<StockQuotationItem> getItems() {
        return items;
    }

    public void setItems(List<StockQuotationItem> items) {
        this.items = items;
    }

    public double getQuotationPrice() {
        return quotationPrice;
    }

    public void setQuotationPrice(double quotationPrice) {
        this.quotationPrice = quotationPrice;
    }

    public Object getCustomer() {
        return customer;
    }

    public void setCustomer(Object customer) {
        this.customer = customer;
    }
}
