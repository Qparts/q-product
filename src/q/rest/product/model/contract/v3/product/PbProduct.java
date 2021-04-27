package q.rest.product.model.contract.v3.product;

import q.rest.product.helper.AppConstants;
import q.rest.product.model.product.full.Product;
import q.rest.product.model.product.full.ProductSpec;
import q.rest.product.model.product.market.ProductSupply;
import q.rest.product.model.product.full.Spec;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PbProduct {

    private long id;
    private String productNumber;
    private String desc;
    private String descAr;
    private String details;
    private String detailsAr;
    private PbBrand brand;
    private Set<PbSpec> specs = new HashSet<>();
    private double salesPrice;

    public String getImage(){
        return AppConstants.getProductImage(id);
    }

    public PbProduct(Product product, List<Spec> specs) {
        this.id = product.getId();
        this.desc = product.getProductDesc();
        this.descAr = product.getProductDescAr();
        this.details = product.getDetails();
        this.productNumber = product.getProductNumber();
        this.brand = new PbBrand(product.getBrand());
        this.calculateSalesPrice(product.getMarketSupply());
        this.initSpecs(product.getSpecs(), specs);
    }

    private void initSpecs(Set<ProductSpec> pss, List<Spec> specKeys){
        for(var ps : pss){
            PbSpec pbspec = new PbSpec();
            pbspec.setValue(ps.getValue());
            pbspec.setValueAr(ps.getValueAr());
            for(var spec : specKeys){
                if(spec.getId() == ps.getSpecId()) {
                    pbspec.setKey(spec.getName());
                    pbspec.setKeyAr(spec.getNameAr());
                    break;
                }
            }
            this.specs.add(pbspec);
        }
    }

    private void calculateSalesPrice(List<ProductSupply> supplies){
        if(supplies != null && !supplies.isEmpty()){
            double total = 0;
            for(var s : supplies){
                total += s.getSalesPrice();
            }
            salesPrice = total / supplies.size();
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDescAr() {
        return descAr;
    }

    public void setDescAr(String descAr) {
        this.descAr = descAr;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDetailsAr() {
        return detailsAr;
    }

    public void setDetailsAr(String detailsAr) {
        this.detailsAr = detailsAr;
    }

    public PbBrand getBrand() {
        return brand;
    }

    public void setBrand(PbBrand brand) {
        this.brand = brand;
    }

    public Set<PbSpec> getSpecs() {
        return specs;
    }

    public void setSpecs(Set<PbSpec> specs) {
        this.specs = specs;
    }

    public double getSalesPrice() {
        return salesPrice;
    }

    public void setSalesPrice(double salesPrice) {
        this.salesPrice = salesPrice;
    }
}
