package q.rest.product.model.qstock.views;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Table(name = "prd_view_purchase_summary")
@Entity
@IdClass(StockPurchaseSummary.StockPurchaseSummaryPK.class)
public class StockPurchaseSummary {
    @Id
    @Column(name = "created")
    @Temporal(TemporalType.DATE)
    private Date created;
    @Id
    @JsonIgnore
    @Column(name = "company_id")
    private int companyId;

    private double cashPurchase;
    private double creditPurchase;

    private double cashPurchaseTax;
    private double creditPurchaseTax;

    private double cashPurchaseReturn;
    private double creditPurchaseReturn;

    private double cashPurchaseReturnAverageCost;
    private double creditPurchaseReturnAverageCost;

    private double cashPurchaseReturnTax;
    private double creditPurchaseReturnTax;



    public StockPurchaseSummary(){}

    public StockPurchaseSummary(Date date, int companyId){
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

    public double getCashPurchase() {
        return cashPurchase;
    }

    public void setCashPurchase(double cashPurchase) {
        this.cashPurchase = cashPurchase;
    }

    public double getCreditPurchase() {
        return creditPurchase;
    }

    public void setCreditPurchase(double creditPurchase) {
        this.creditPurchase = creditPurchase;
    }

    public double getCashPurchaseTax() {
        return cashPurchaseTax;
    }

    public void setCashPurchaseTax(double cashPurchaseTax) {
        this.cashPurchaseTax = cashPurchaseTax;
    }

    public double getCreditPurchaseTax() {
        return creditPurchaseTax;
    }

    public void setCreditPurchaseTax(double creditPurchaseTax) {
        this.creditPurchaseTax = creditPurchaseTax;
    }

    public double getCashPurchaseReturn() {
        return cashPurchaseReturn;
    }

    public void setCashPurchaseReturn(double cashPurchaseReturn) {
        this.cashPurchaseReturn = cashPurchaseReturn;
    }

    public double getCreditPurchaseReturn() {
        return creditPurchaseReturn;
    }

    public void setCreditPurchaseReturn(double creditPurchaseReturn) {
        this.creditPurchaseReturn = creditPurchaseReturn;
    }

    public double getCashPurchaseReturnAverageCost() {
        return cashPurchaseReturnAverageCost;
    }

    public void setCashPurchaseReturnAverageCost(double cashPurchaseReturnAverageCost) {
        this.cashPurchaseReturnAverageCost = cashPurchaseReturnAverageCost;
    }

    public double getCreditPurchaseReturnAverageCost() {
        return creditPurchaseReturnAverageCost;
    }

    public void setCreditPurchaseReturnAverageCost(double creditPurchaseReturnAverageCost) {
        this.creditPurchaseReturnAverageCost = creditPurchaseReturnAverageCost;
    }

    public double getCashPurchaseReturnTax() {
        return cashPurchaseReturnTax;
    }

    public void setCashPurchaseReturnTax(double cashPurchaseReturnTax) {
        this.cashPurchaseReturnTax = cashPurchaseReturnTax;
    }

    public double getCreditPurchaseReturnTax() {
        return creditPurchaseReturnTax;
    }

    public void setCreditPurchaseReturnTax(double creditPurchaseReturnTax) {
        this.creditPurchaseReturnTax = creditPurchaseReturnTax;
    }


    public static class StockPurchaseSummaryPK implements Serializable {
        protected Date created;
        protected int companyId;

        public StockPurchaseSummaryPK() {
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StockPurchaseSummaryPK that = (StockPurchaseSummaryPK) o;
            return companyId == that.companyId &&
                    Objects.equals(created, that.created);
        }

        @Override
        public int hashCode() {
            return Objects.hash(created, companyId);
        }
    }
}
