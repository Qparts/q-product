package q.rest.product.model.qvm;

public class SearchBranch {
    private int qBranchId;
    private int qCityId;
    private String branchName;
    private String branchNameAr;
    private String branchId;
    private String cityName;
    private String cityNameAr;

    public SearchBranch() {
    }

    public SearchBranch(String branchName, String branchId, String cityName) {
        this.branchName = branchName;
        this.branchId = branchId;
        this.cityName = cityName;
    }

    public String getBranchNameAr() {
        return branchNameAr;
    }

    public void setBranchNameAr(String branchNameAr) {
        this.branchNameAr = branchNameAr;
    }

    public String getCityNameAr() {
        return cityNameAr;
    }

    public void setCityNameAr(String cityNameAr) {
        this.cityNameAr = cityNameAr;
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

    public int getqBranchId() {
        return qBranchId;
    }

    public void setqBranchId(int qBranchId) {
        this.qBranchId = qBranchId;
    }

    public int getqCityId() {
        return qCityId;
    }

    public void setqCityId(int qCityId) {
        this.qCityId = qCityId;
    }
}
