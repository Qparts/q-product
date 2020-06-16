package q.rest.product.model.contract;

import java.util.List;

public class PullStockRequest {
    private int vendorId;
    private String allStockEndPoint;
    private String secret;
    private List<Branch> branches;

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public String getAllStockEndPoint() {
        return allStockEndPoint;
    }

    public void setAllStockEndPoint(String allStockEndPoint) {
        this.allStockEndPoint = allStockEndPoint;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public List<Branch> getBranches() {
        return branches;
    }

    public void setBranches(List<Branch> branches) {
        this.branches = branches;
    }
}
