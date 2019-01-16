package q.rest.product.model.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="prd_brand")
public class Brand implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "prd_brand_id_seq_gen", sequenceName = "prd_brand_id_seq", initialValue=1000, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prd_brand_id_seq_gen")
    @Column(name="id")
    private int id;
    @Column(name="name")
    private String name;
    @Column(name="name_ar")
    private String nameAr;
    @Column(name="status")
    private char status;
    @Column(name="created")
    private Date created;
    @Column(name="created_by")
    private int createdBy;
    @Transient
    private String imageString;

    public String getImageString() {
        return imageString;
    }

    public void setImageString(String imageString) {
        this.imageString = imageString;
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

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdby) {
        this.createdBy = createdby;
    }
}
