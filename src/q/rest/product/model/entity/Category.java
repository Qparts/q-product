package q.rest.product.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name="prd_category")
public class Category implements Serializable {

    @Id
    @SequenceGenerator(name = "prd_category_id_seq_gen", sequenceName = "prd_category_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prd_category_id_seq_gen")
    @Column(name="id")
    private int id;
    @Column(name="name")
    private String name;
    @Column(name="name_ar")
    private String nameAr;
    @Column(name="root")
    private boolean root;
    @Column(name="parent_node")
    private Integer parentId;
    @Column(name="status")
    private char status;
    @Column(name="created")
    private Date created;
    @Column(name="created_by")
    private int createdBy;
    @Transient
    private List<String> tags;
    @Transient
    private List<Spec> defaultSpecs;
    @Transient
    private String imageString;
    @Transient
    private List<Category> children;

    public Category(){
        this.tags = new ArrayList<>();
        this.children = new ArrayList<>();
    }

    @JsonIgnore
    public boolean hasChildren(){
        return (children != null && !children.isEmpty());
    }


    public List<Category> getChildren() {
        return children;
    }

    public void setChildren(List<Category> children) {
        this.children = children;
    }

    public List<Spec> getDefaultSpecs() {
        return defaultSpecs;
    }

    public void setDefaultSpecs(List<Spec> defaultSpecs) {
        this.defaultSpecs = defaultSpecs;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

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

    public boolean isRoot() {
        return root;
    }

    public void setRoot(boolean root) {
        this.root = root;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
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

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return id == category.id &&
                root == category.root &&
                status == category.status &&
                createdBy == category.createdBy &&
                Objects.equals(name, category.name) &&
                Objects.equals(nameAr, category.nameAr) &&
                Objects.equals(parentId, category.parentId) &&
                Objects.equals(created, category.created) &&
                Objects.equals(tags, category.tags) &&
                Objects.equals(defaultSpecs, category.defaultSpecs) &&
                Objects.equals(imageString, category.imageString) &&
                Objects.equals(children, category.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, nameAr, root, parentId, status, created, createdBy, tags, defaultSpecs, imageString, children);
    }
}
