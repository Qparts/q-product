package q.rest.product.model.contract.v3;

import java.util.Date;
import java.util.List;

public class QStockUploadHolder {
    private List<SabaModel> uploadPart;
    private int companyId;
    private int branchId;
    private Date date;

    public List<SabaModel> getUploadPart() {
        return uploadPart;
    }

    public void setUploadPart(List<SabaModel> uploadPart) {
        this.uploadPart = uploadPart;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
