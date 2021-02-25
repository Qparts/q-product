package q.rest.product.model.qstock;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="prd_stk_purchase_credit")
public class StockPurchaseCredit implements Serializable {
    @Id
    @SequenceGenerator(name = "prd_stk_purchase_credit_id_seq_gen", sequenceName = "prd_stk_purchase_credit_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prd_stk_purchase_credit_id_seq_gen")
    private int id;
    private int purchaseOrderId;
    private int purchaseReturnId;
    private double amount;
    @Temporal(TemporalType.TIMESTAMP)
    private Date creditDate;
    private int supplierId;
    private int companyId;
    private char source;//P = purchase, R = return , Y = payment
    private Character paymentMethod;// only in source = Y -- S = span, C = Cash, T = transfer, O = online, Q = cheque, NULL if transaction is credit
    private String reference;

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Character getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(Character paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public char getSource() {
        return source;
    }

    public void setSource(char source) {
        this.source = source;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getPurchaseReturnId() {
        return purchaseReturnId;
    }

    public void setPurchaseReturnId(int purchaseReturnId) {
        this.purchaseReturnId = purchaseReturnId;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(int purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getCreditDate() {
        return creditDate;
    }

    public void setCreditDate(Date creditDate) {
        this.creditDate = creditDate;
    }
}
