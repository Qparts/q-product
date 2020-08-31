package q.rest.product.model.contract;

import q.rest.product.model.entity.v3.stock.CompanyProduct;

import java.util.List;

public class ProductsCountHolder {
    private List<CompanyProduct> products;
    private int count;

    public List<CompanyProduct> getProducts() {
        return products;
    }

    public void setProducts(List<CompanyProduct> products) {
        this.products = products;
    }


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
