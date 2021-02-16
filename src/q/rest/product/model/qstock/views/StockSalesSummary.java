package q.rest.product.model.qstock.views;

import com.fasterxml.jackson.annotation.JsonIgnore;
import q.rest.product.model.entity.Variant;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Table(name = "prd_view_Sales_summary")
@Entity
@IdClass(StockSalesSummary.StockSalesSummaryPK.class)
public class StockSalesSummary {
    @Id
    @Column(name = "created")
    @Temporal(TemporalType.DATE)
    private Date created;
    @Id
    @JsonIgnore
    @Column(name = "company_id")
    private int companyId;

    private double cashSales;
    private double creditSales;

    private double cashSalesCost;
    private double creditSalesCost;

    private double cashSalesTax;
    private double creditSalesTax;

    private double cashSalesReturn;
    private double creditSalesReturn;

    private double cashSalesReturnCost;
    private double creditSalesReturnCost;

    private double cashSalesReturnTax;
    private double creditSalesReturnTax;

    public StockSalesSummary(){}

    public StockSalesSummary(Date date, int companyId){
        this.created = date;
        this.companyId = companyId;
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

    public double getCashSales() {
        return cashSales;
    }

    public void setCashSales(double cashSales) {
        this.cashSales = cashSales;
    }

    public double getCreditSales() {
        return creditSales;
    }

    public void setCreditSales(double creditSales) {
        this.creditSales = creditSales;
    }

    public double getCashSalesCost() {
        return cashSalesCost;
    }

    public void setCashSalesCost(double cashSalesCost) {
        this.cashSalesCost = cashSalesCost;
    }

    public double getCreditSalesCost() {
        return creditSalesCost;
    }

    public void setCreditSalesCost(double creditSalesCost) {
        this.creditSalesCost = creditSalesCost;
    }

    public double getCashSalesTax() {
        return cashSalesTax;
    }

    public void setCashSalesTax(double cashSalesTax) {
        this.cashSalesTax = cashSalesTax;
    }

    public double getCreditSalesTax() {
        return creditSalesTax;
    }

    public void setCreditSalesTax(double creditSalesTax) {
        this.creditSalesTax = creditSalesTax;
    }

    public double getCashSalesReturn() {
        return cashSalesReturn;
    }

    public void setCashSalesReturn(double cashSalesReturn) {
        this.cashSalesReturn = cashSalesReturn;
    }

    public double getCreditSalesReturn() {
        return creditSalesReturn;
    }

    public void setCreditSalesReturn(double creditSalesReturn) {
        this.creditSalesReturn = creditSalesReturn;
    }

    public double getCashSalesReturnCost() {
        return cashSalesReturnCost;
    }

    public void setCashSalesReturnCost(double cashSalesReturnCost) {
        this.cashSalesReturnCost = cashSalesReturnCost;
    }

    public double getCreditSalesReturnCost() {
        return creditSalesReturnCost;
    }

    public void setCreditSalesReturnCost(double creditSalesReturnCost) {
        this.creditSalesReturnCost = creditSalesReturnCost;
    }

    public double getCashSalesReturnTax() {
        return cashSalesReturnTax;
    }

    public void setCashSalesReturnTax(double cashSalesReturnTax) {
        this.cashSalesReturnTax = cashSalesReturnTax;
    }

    public double getCreditSalesReturnTax() {
        return creditSalesReturnTax;
    }

    public void setCreditSalesReturnTax(double creditSalesReturnTax) {
        this.creditSalesReturnTax = creditSalesReturnTax;
    }

    public static class StockSalesSummaryPK implements Serializable {
        protected Date created;
        protected int companyId;

        public StockSalesSummaryPK() {
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StockSalesSummaryPK that = (StockSalesSummaryPK) o;
            return companyId == that.companyId &&
                    Objects.equals(created, that.created);
        }

        @Override
        public int hashCode() {
            return Objects.hash(created, companyId);
        }

    }
}
