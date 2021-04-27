package q.rest.product.model.product.market;

import com.fasterxml.jackson.annotation.JsonIgnore;
import q.rest.product.model.product.full.ProductSpec;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name="prd_specification")
public class MarketSpec implements Serializable {

    @Id
    @Column(name="id", updatable = false)
    private int id;
    @Column(name = "spec_name")
    private String name;
    @Column(name = "spec_name_ar")
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

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarketSpec spec = (MarketSpec) o;
        return id == spec.id &&
                Objects.equals(name, spec.name) &&
                Objects.equals(nameAr, spec.nameAr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, nameAr);
    }
}

