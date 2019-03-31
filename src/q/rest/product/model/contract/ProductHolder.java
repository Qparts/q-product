package q.rest.product.model.contract;

import q.rest.product.model.entity.Category;
import q.rest.product.model.entity.Product;
import q.rest.product.model.entity.ProductPrice;
import q.rest.product.model.entity.ProductSpec;

import java.io.Serializable;
import java.util.List;

public class ProductHolder implements Serializable {

    private Product product;
    private List<Category> categories;
    private List<String> tags;
    private List<ProductSpec> productSpecs;
    private List<ProductPrice> productPrices;


    public List<ProductPrice> getProductPrices() {
        return productPrices;
    }

    public void setProductPrices(List<ProductPrice> productPrices) {
        this.productPrices = productPrices;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<ProductSpec> getProductSpecs() {
        return productSpecs;
    }

    public void setProductSpecs(List<ProductSpec> productSpecs) {
        this.productSpecs = productSpecs;
    }
}
