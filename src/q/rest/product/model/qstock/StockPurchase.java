package q.rest.product.model.qstock;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Table(name="prd_stk_purchase_order")
@Entity
public class StockPurchase implements Serializable {
    @Id
    @SequenceGenerator(name = "prd_stk_purchase_order_id_seq_gen", sequenceName = "prd_stk_purchase_order_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prd_stk_purchase_order_id_seq_gen")
    private int id;
    @JsonIgnore
    private int companyId;
    private int supplierId;
    @Transient
    private Object supplier;
    private Date created;
    private double deliveryCost;
    private char transactionType;//C = cash, T = credit
    private Character paymentMethod;//S = span, C = Cash, T = transfer, O = online, Q = cheque, NULL if transaction is credit
    private String reference;
    private double taxRate;
    private int branchId;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "purchase_order_id")
    private List<StockPurchaseItem> items;


    @JsonIgnore
    public double getTotalAmount() {
        double total = deliveryCost;
        for (var item : items ) {
            total += (item.getQuantity() * item.getUnitPrice()) + (item.getQuantity() * item.getUnitPrice() * taxRate);
        }
        return total;
    }


    @JsonIgnore
    public void attachSupplier(List<Map> suppliers) {
        try {
            for (var map : suppliers) {
                int id = (int) map.get("id");
                if (id == this.supplierId) {
                    this.supplier = map;
                    break;
                }
            }
        } catch (Exception ignore) {
        }
    }


    @JsonIgnore
    public void attachSupplier(Map<String, Object> supplier) {
        this.supplier = supplier;
    }


    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public Character getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(Character paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public int getId() {
        return id;
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

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public double getDeliveryCost() {
        return deliveryCost;
    }

    public void setDeliveryCost(double deliveryCost) {
        this.deliveryCost = deliveryCost;
    }

    public char getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(char transactionType) {
        this.transactionType = transactionType;
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

    public List<StockPurchaseItem> getItems() {
        return items;
    }

    public void setItems(List<StockPurchaseItem> items) {
        this.items = items;
    }

    public Object getSupplier() {
        return supplier;
    }

    public void setSupplier(Object supplier) {
        this.supplier = supplier;
    }
}
