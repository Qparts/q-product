package q.rest.product.model.contract;

import q.rest.product.model.contract.v3.product.PbProduct;

import java.util.ArrayList;
import java.util.List;

public class SearchResult {

    private int resultSize;
    private List<PbProduct> products;
    private List<SearchFilter> filterObjects;

    public SearchResult(){
        products = new ArrayList<>();
        filterObjects = new ArrayList<>();
    }


    public int getResultSize() {
        return resultSize;
    }

    public void setResultSize(int resultSize) {
        this.resultSize = resultSize;
    }

    public List<PbProduct> getProducts() {
        return products;
    }

    public void setProducts(List<PbProduct> products) {
        this.products = products;
    }

    public List<SearchFilter> getFilterObjects() {
        return filterObjects;
    }

    public void setFilterObjects(List<SearchFilter> filterObjects) {
        this.filterObjects = filterObjects;
    }
}
