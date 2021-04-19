package q.rest.product.model.entity.v3.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import q.rest.product.helper.AppConstants;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="prd_brand")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Brand {
    @Id
    @SequenceGenerator(name = "prd_brand_id_seq_gen", sequenceName = "prd_brand_id_seq", initialValue=1000, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prd_brand_id_seq_gen")
    @Column(name="id")
    private int id;
    private String name;
    private String nameAr;
    private int classId;
    @JsonIgnore
    private char status;//A = active, I = inactive , P = pending review
    @JsonIgnore
    private Date created;
    @JsonIgnore
    private int createdBy;//which company created (0 if created in dashboard)
    @JsonIgnore
    private Integer approvedBy;//reviewed by

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public Integer getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Integer approvedBy) {
        this.approvedBy = approvedBy;
    }

    public String getImg() {
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
