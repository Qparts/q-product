package q.rest.product.model.contract.v3;

import q.rest.product.model.entity.VinSearch;

import java.util.List;

public class SummaryReport {
    private int totalVinSearches;
    private int vinSearchesToday;
    private double stockValue;
    private double offersValue;
    private List<VinSearch> topVins;

    public int getTotalVinSearches() {
        return totalVinSearches;
    }

    public void setTotalVinSearches(int totalVinSearches) {
        this.totalVinSearches = totalVinSearches;
    }

    public double getStockValue() {
        return stockValue;
    }

    public void setStockValue(double stockValue) {
        this.stockValue = stockValue;
    }

    public double getOffersValue() {
        return offersValue;
    }

    public void setOffersValue(double offersValue) {
        this.offersValue = offersValue;
    }

    public List<VinSearch> getTopVins() {
        return topVins;
    }

    public void setTopVins(List<VinSearch> topVins) {
        this.topVins = topVins;
    }

    public int getVinSearchesToday() {
        return vinSearchesToday;
    }

    public void setVinSearchesToday(int vinSearchesToday) {
        this.vinSearchesToday = vinSearchesToday;
    }
}
