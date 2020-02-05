package q.rest.product.model.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
    @Table(name="prd_vin_not_found")
public class VinNotFound implements Serializable {


    @Id
    @SequenceGenerator(name = "prd_vin_not_found_id_seq_gen", sequenceName = "prd_vin_not_found_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prd_vin_not_found_id_seq_gen")
    @Column(name="id")
    private long id;
    @Column(name="vin")
    private String vin;
    @Column(name="cat_id")
    private String catId;
    @Column(name="created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
