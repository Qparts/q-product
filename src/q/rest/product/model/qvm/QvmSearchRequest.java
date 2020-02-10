package q.rest.product.model.qvm;

import java.util.List;

public class QvmSearchRequest {
    private String query;
    private boolean attachProduct;
    private int requesterId;
    private char requesterType;//--V = vendor , -U = user
    private List<QvmVendorCredentials> vendorCreds;

    public char getRequesterType() {
        return requesterType;
    }

    public void setRequesterType(char requesterType) {
        this.requesterType = requesterType;
    }

    public int getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(int requesterId) {
        this.requesterId = requesterId;
    }

    public boolean isAttachProduct() {
        return attachProduct;
    }

    public void setAttachProduct(boolean attachProduct) {
        this.attachProduct = attachProduct;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<QvmVendorCredentials> getVendorCreds() {
        return vendorCreds;
    }

    public void setVendorCreds(List<QvmVendorCredentials> vendorCreds) {
        this.vendorCreds = vendorCreds;
    }
}
