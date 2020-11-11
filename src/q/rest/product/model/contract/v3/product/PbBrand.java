package q.rest.product.model.contract.v3.product;

import q.rest.product.helper.AppConstants;
import q.rest.product.model.entity.v3.product.Brand;

import javax.persistence.*;
import java.util.Date;

public class PbBrand {
    private int id;
    private String name;
    private String nameAr;


    public PbBrand(Brand brand){
        this.id = brand.getId();
        this.name = brand.getName();
        this.nameAr = brand.getNameAr();
    }


    public String getImage(){
        return AppConstants.getBrandImage(id);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameAr() {
        return nameAr;
    }

    public void setNameAr(String nameAr) {
        this.nameAr = nameAr;
    }
}
