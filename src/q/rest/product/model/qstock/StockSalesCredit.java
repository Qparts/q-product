package q.rest.product.model.qstock;

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
    private int salesOrderId;
    private double amount;
    @Temporal(TemporalType.TIMESTAMP)
    private Date creditDate;

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
