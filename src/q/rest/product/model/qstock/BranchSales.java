package q.rest.product.model.qstock;

public class BranchSales {
    private int branchId;
    private double daySales;
    private double dayReturns;
    private double mtdSales;
    private double mtdReturns;
    private double ytdSales;
    private double ytdReturns;

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public double getDaySales() {
        return daySales;
    }

    public void setDaySales(double daySales) {
        this.daySales = daySales;
    }

    public double getDayReturns() {
        return dayReturns;
    }

    public void setDayReturns(double dayReturns) {
        this.dayReturns = dayReturns;
    }

    public double getMtdSales() {
        return mtdSales;
    }

    public void setMtdSales(double mtdSales) {
        this.mtdSales = mtdSales;
    }

    public double getMtdReturns() {
        return mtdReturns;
    }

    public void setMtdReturns(double mtdReturns) {
        this.mtdReturns = mtdReturns;
    }

    public double getYtdSales() {
        return ytdSales;
    }

    public void setYtdSales(double ytdSales) {
        this.ytdSales = ytdSales;
    }

    public double getYtdReturns() {
        return ytdReturns;
    }

    public void setYtdReturns(double ytdReturns) {
        this.ytdReturns = ytdReturns;
    }
}
