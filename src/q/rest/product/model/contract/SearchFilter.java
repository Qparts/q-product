package q.rest.product.model.contract;

import java.util.HashSet;
import java.util.Set;

public class SearchFilter {

    private int id;
    private String filterTitle;
    private String filterTitleAr;
    private Set<Values> options;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SearchFilter(){
        options = new HashSet<Values>();
    }

    public void addValues(String value, String valueAr, Number id){
        Values values = new Values(value, valueAr, id);
        options.add(values);
    }

    public String getFilterTitle() {
        return filterTitle;
    }

    public void setFilterTitle(String filterTitle) {
        this.filterTitle = filterTitle;
    }

    public Set<Values> getOptions() {
        return options;
    }

    public void setOptions(Set<Values> options) {
        this.options = options;
    }

    public String getFilterTitleAr() {
        return filterTitleAr;
    }

    public void setFilterTitleAr(String filterTitleAr) {
        this.filterTitleAr = filterTitleAr;
    }
}
