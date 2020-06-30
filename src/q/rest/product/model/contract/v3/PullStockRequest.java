package q.rest.product.model.contract.v3;


import java.util.List;

public class PullStockRequest {
    private int companyId;
    private int createdBy;
    private String allStockEndPoint;
    private String secret;
    private List<Branch> branches;

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
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

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public void setBranches(List<Branch> branches) {
        this.branches = branches;
    }
}
