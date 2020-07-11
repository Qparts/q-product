package q.rest.product.model.contract.subscriber;


import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashSet;
import java.util.Set;

public class BranchReduced {
    private int id;
    @JsonIgnore
    private int companyId;
    private String name;
    private String nameAr;
    @JsonIgnore
    private int cityId;
    private Object city;
    private Set<CompanyContactReduced> contacts = new HashSet<>();

    public BranchReduced() {
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
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

    public Set<CompanyContactReduced> getContacts() {
        return contacts;
    }

    public void setContacts(Set<CompanyContactReduced> contacts) {
        this.contacts = contacts;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public Object getCity() {
        return city;
    }

    public void setCity(Object city) {
        this.city = city;
    }
}
