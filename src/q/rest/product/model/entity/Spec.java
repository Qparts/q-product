package q.rest.product.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name="prd_specification")
public class Spec implements Serializable {

    @Id
    @SequenceGenerator(name = "prd_specification_id_seq_gen", sequenceName = "prd_specification_id_seq", initialValue=1000, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prd_specification_id_seq_gen")
    @Column(name="id", updatable = false)
    private int id;
    @Column(name="spec_name")
    private String name;
    @Column(name="spec_name_ar")
    private String nameAr;
    @Column(name="created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @Column(name="created_by")
    private int createdBy;
    @Transient
    @JsonIgnore
    private List<ProductSpec> productSpecs;

    public List<ProductSpec> getProductSpecs() {
        return productSpecs;
    }

    public void setProductSpecs(List<ProductSpec> productSpecs) {
        this.productSpecs = productSpecs;
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

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Spec spec = (Spec) o;
        return id == spec.id &&
                createdBy == spec.createdBy &&
                Objects.equals(name, spec.name) &&
                Objects.equals(nameAr, spec.nameAr) &&
                Objects.equals(created, spec.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, nameAr, created, createdBy);
    }
}

