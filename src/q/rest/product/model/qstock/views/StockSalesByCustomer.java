package q.rest.product.model.qstock.views;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Table(name = "prd_view_sales_by_customer")
@Entity
@IdClass(StockSalesByCustomer.StockSalesByCustomerPK.class)
public class StockSalesByCustomer {
    @Id
    @Column(name = "created")
    @Temporal(TemporalType.DATE)
    private Date created;
    @Id
    @JsonIgnore
    @Column(name = "company_id")
    private int companyId;
    @Id
    @Column(name = "customer_id")
    private int customerId;

    private double sales;
    private double salesTax;
    private double salesReturn;
    private double salesReturnTax;

    public StockSalesByCustomer(){}

    public StockSalesByCustomer(Date date, int companyId, int customerId){
        this.created = date;
        this.companyId = companyId;
        this.customerId = customerId;
    }


    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
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

    public double getSales() {
        return sales;
    }

    public void setSales(double sales) {
        this.sales = sales;
    }

    public double getSalesTax() {
        return salesTax;
    }

    public void setSalesTax(double salesTax) {
        this.salesTax = salesTax;
    }

    public double getSalesReturn() {
        return salesReturn;
    }

    public void setSalesReturn(double salesReturn) {
        this.salesReturn = salesReturn;
    }

    public double getSalesReturnTax() {
        return salesReturnTax;
    }

    public void setSalesReturnTax(double salesReturnTax) {
        this.salesReturnTax = salesReturnTax;
    }

    public static class StockSalesByCustomerPK implements Serializable {
        protected Date created;
        protected int companyId;
        protected int customerId;

        public StockSalesByCustomerPK() {
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StockSalesByCustomerPK that = (StockSalesByCustomerPK) o;
            return companyId == that.companyId &&
                    customerId == that.customerId &&
                    Objects.equals(created, that.created);
        }

        @Override
        public int hashCode() {
            return Objects.hash(created, companyId, customerId);
        }
    }
}
