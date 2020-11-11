package q.rest.product.model.catalog;


import q.rest.product.model.contract.v3.product.PbProduct;

import java.util.List;

public class CatalogPartsList {
    private String id;
    private String number;
    private String name;
    private String notice;
    private String description;
    private String positionNumber;
    private String url;
    private List<PbProduct> products;

    public List<PbProduct> getProducts() {
        return products;
    }

    public void setProducts(List<PbProduct> products) {
        this.products = products;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPositionNumber() {
        return positionNumber;
    }

    public void setPositionNumber(String positionNumber) {
        this.positionNumber = positionNumber;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
