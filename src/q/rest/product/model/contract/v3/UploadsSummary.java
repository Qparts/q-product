package q.rest.product.model.contract.v3;

import q.rest.product.model.entity.v3.stock.CompanyOfferUploadRequest;
import q.rest.product.model.entity.v3.stock.CompanyUploadRequest;

import java.util.List;

public class UploadsSummary {
    private List<CompanyUploadRequest> stockRequests;
    private List<CompanyOfferUploadRequest> offerRequests;

    public List<CompanyUploadRequest> getStockRequests() {
        return stockRequests;
    }

    public void setStockRequests(List<CompanyUploadRequest> stockRequests) {
        this.stockRequests = stockRequests;
    }

    public List<CompanyOfferUploadRequest> getOfferRequests() {
        return offerRequests;
    }

    public void setOfferRequests(List<CompanyOfferUploadRequest> offerRequests) {
        this.offerRequests = offerRequests;
    }
}
