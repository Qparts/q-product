package q.rest.product.model.entity.v3.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import q.rest.product.helper.AppConstants;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="prd_brand_class")
@JsonIgnoreProperties(ignoreUnknown = true)
public class BrandClass {
    @Id
    @SequenceGenerator(name = "prd_brand_class_id_seq_gen", sequenceName = "prd_brand_class_id_seq", initialValue=1000, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prd_brand_class_id_seq_gen")
    @Column(name="id")
    private int id;
    private String name;
    private String nameAr;

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
