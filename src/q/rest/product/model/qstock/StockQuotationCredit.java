package q.rest.product.model.qstock;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="prd_stk_quotation_credit")
public class StockQuotationCredit implements Serializable {

    @Id
    @SequenceGenerator(name = "prd_stk_quotation_credit_id_seq_gen", sequenceName = "prd_stk_quotation_credit_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prd_stk_quotation_credit_id_seq_gen")
    private int id;
    private int quotationOrderId;
    private double amount;
    @Temporal(TemporalType.TIMESTAMP)
    private Date creditDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuotationOrderId() {
        return quotationOrderId;
    }

    public void setQuotationOrderId(int quotationOrderId) {
        this.quotationOrderId = quotationOrderId;
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
