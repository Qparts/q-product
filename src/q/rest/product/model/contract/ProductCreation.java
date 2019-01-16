package q.rest.product.model.contract;

import q.rest.product.model.entity.Category;
import q.rest.product.model.entity.Product;
import q.rest.product.model.entity.ProductPrice;
import q.rest.product.model.entity.ProductSpec;

import java.io.Serializable;
import java.util.List;

public class ProductCreation implements Serializable {

    private Product product;
    private String imageString;
    private List<Category> categories;
    private List<String> tags;
    private List<ProductSpec> productSpecs;
    private ProductPrice productPrice;


    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getImageString() {
        return imageString;
    }

    public void setImageString(String imageString) {
        this.imageString = imageString;
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

    public ProductPrice getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(ProductPrice productPrice) {
        this.productPrice = productPrice;
    }
}
