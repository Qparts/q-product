package q.rest.product.model.contract.v3;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class UploadHolder implements Serializable {
    private List<StockHolder> stockVars;
    private List<OfferHolder> offerVars;
    private int companyId;
    private int branchId;
    private int cityId;
    private int countryId;
    private int regionId;
    private Date date;
    private boolean overridePrevious;
    private int offerId;//for offer


    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    public boolean isOverridePrevious() {
        return overridePrevious;
    }

    public void setOverridePrevious(boolean overridePrevious) {
        this.overridePrevious = overridePrevious;
    }


    public List<StockHolder> getStockVars() {
        return stockVars;
    }

    public void setStockVars(List<StockHolder> stockVars) {
        this.stockVars = stockVars;
    }

    public List<OfferHolder> getOfferVars() {
        return offerVars;
    }

    public void setOfferVars(List<OfferHolder> offerVars) {
        this.offerVars = offerVars;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getOfferId() {
        return offerId;
    }

    public void setOfferId(int offerId) {
        this.offerId = offerId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }
}
