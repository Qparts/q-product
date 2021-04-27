package q.rest.product.model.contract;

import q.rest.product.model.archived.ProductPrice;
import q.rest.product.model.archived.Stock;
import q.rest.product.model.product.full.Category;
import q.rest.product.model.product.full.Product;
import q.rest.product.model.product.full.ProductSpec;

import java.io.Serializable;
import java.util.List;

public class ProductHolder implements Serializable {

    private Product product;
    private List<Category> categories;//ok
    private List<String> tags;//ok
    private List<ProductSpec> productSpecs;//ok
    private List<ProductPrice> productPrices;
    private List<Stock> liveStock;

    public List<Stock> getLiveStock() {
        return liveStock;
    }

    public void setLiveStock(List<Stock> liveStock) {
        this.liveStock = liveStock;
    }

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
