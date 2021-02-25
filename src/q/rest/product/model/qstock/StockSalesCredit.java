package q.rest.product.model.qstock;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="prd_stk_sales_credit")
public class StockSalesCredit implements Serializable {
    @Id
    @SequenceGenerator(name = "prd_stk_sales_credit_id_seq_gen", sequenceName = "prd_stk_sales_credit_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prd_stk_sales_credit_id_seq_gen")
    private int id;
    @JsonIgnore
    private int companyId;
    private int salesOrderId;
    private int salesReturnId;
    private double amount;
    @Temporal(TemporalType.TIMESTAMP)
    private Date creditDate;
    private int customerId;
    private char source;//S = sales , R = return, Y = payment received
    private Character paymentMethod;// only in source = Y
    private String reference;

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

    public int getCustomerId() {
        return customerId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getSalesReturnId() {
        return salesReturnId;
    }

    public void setSalesReturnId(int salesReturnId) {
        this.salesReturnId = salesReturnId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public char getSource() {
        return source;
    }

    public void setSource(char source) {
        this.source = source;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSalesOrderId() {
        return salesOrderId;
    }

    public void setSalesOrderId(int salesOrderId) {
        this.salesOrderId = salesOrderId;
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
