package q.rest.product.model.qvm;

public class SearchBranch {
    private String branchName;
    private String branchId;
    private String cityName;

    public SearchBranch() {
    }

    public SearchBranch(String branchName, String branchId, String cityName) {
        this.branchName = branchName;
        this.branchId = branchId;
        this.cityName = cityName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
