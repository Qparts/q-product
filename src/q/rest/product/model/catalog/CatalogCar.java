package q.rest.product.model.catalog;

import java.util.List;

public class CatalogCar {
    private String title;
    private String catalogId;
    private String brand;
    private String carId;
    private String criteria;
    private String modelId;
    private List<CatalogCarParameter> parameters;
    private List<CatalogOptionCodes> optionCodes;

    public List<CatalogOptionCodes> getOptionCodes() {
        return optionCodes;
    }

    public void setOptionCodes(List<CatalogOptionCodes> optionCodes) {
        this.optionCodes = optionCodes;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(String catalogId) {
        this.catalogId = catalogId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    public List<CatalogCarParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<CatalogCarParameter> parameters) {
        this.parameters = parameters;
    }
}
