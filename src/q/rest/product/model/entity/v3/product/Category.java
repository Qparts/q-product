package q.rest.product.model.entity.v3.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name="prd_category")
public class Category implements Serializable {

    @Id
    @SequenceGenerator(name = "prd_category_id_seq_gen", sequenceName = "prd_category_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prd_category_id_seq_gen")
    private int id;
    private String name;
    private String nameAr;
    private boolean root;
    private Integer parentNode;
    private char status;
    private Date created;
    private int createdBy;
    @OrderBy(value = "tag")
    @ElementCollection(targetClass=String.class)
    @CollectionTable(name = "prd_category_tag", joinColumns = @JoinColumn(name = "category_id"))
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<String> tag;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="prd_category_specification",
            joinColumns = @JoinColumn(name="category_id"),
            inverseJoinColumns = @JoinColumn(name="spec_id"))
    @OrderBy(value = "id")
    private Set<Spec> defaultSpecs = new HashSet<>();
    @OneToMany(mappedBy = "parentNode")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Category> children;

    public Category(){
        this.tag = new ArrayList<>();
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

    public Set<Spec> getDefaultSpecs() {
        return defaultSpecs;
    }

    public void setDefaultSpecs(Set<Spec> defaultSpecs) {
        this.defaultSpecs = defaultSpecs;
    }

    public List<String> getTags() {
        return tag;
    }

    public void setTags(List<String> tags) {
        this.tag = tags;
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


    public Integer getParentNode() {
        return parentNode;
    }

    public void setParentNode(Integer parentNode) {
        this.parentNode = parentNode;
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
                Objects.equals(parentNode, category.parentNode) &&
                Objects.equals(created, category.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, nameAr, root, parentNode, status, created, createdBy);
    }
}
