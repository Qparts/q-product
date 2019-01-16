package q.rest.product.model.contract;

import com.fasterxml.jackson.annotation.JsonIgnore;
import q.rest.product.helper.AppConstants;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "prd_brand")
public class PublicBrand implements Serializable {
    @Id
    @Column(name = "id")
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "name_ar")
    private String nameAr;
    @Column(name = "status")
    @JsonIgnore
    private char status;
    @Transient
    private String image;

    @JsonIgnore
    public void initImageLink(){
        this.image = AppConstants.getBrandImage(id);
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

    public char getStatus() {
        return status;
    }

    public void setStatus(char status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
