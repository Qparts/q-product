package q.rest.product.model.contract.subscriber;


import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class CompanyReduced {
    private int id;
    private String name;
    private String nameAr;
    private List<BranchReduced> branches = new ArrayList<>();


    @JsonIgnore
    public BranchReduced getBranchFromId(int id){
        for (var branch : branches ){
            if(branch.getId() == id){
                return branch;
            }
        }
        return null;
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

    public List<BranchReduced> getBranches() {
        return branches;
    }

    public void setBranches(List<BranchReduced> branches) {
        this.branches = branches;
    }
}
