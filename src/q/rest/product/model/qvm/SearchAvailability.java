package q.rest.product.model.qvm;

public class SearchAvailability {
    private SearchBranch branch;
    private Integer quantity;

    public SearchAvailability() {
    }

    public SearchAvailability(String name, String id, String cityName, Integer quantity) {
        this.branch = new SearchBranch(name, id, cityName);
        this.quantity = quantity;
    }

    public SearchBranch getBranch() {
        return branch;
    }

    public void setBranch(SearchBranch branch) {
        this.branch = branch;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
