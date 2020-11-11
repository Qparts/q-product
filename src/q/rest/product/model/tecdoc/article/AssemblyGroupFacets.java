package q.rest.product.model.tecdoc.article;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AssemblyGroupFacets {
    private int total;
    private List<AssemblyGroupDetails> counts;

    public List<AssemblyGroupDetails> getChildGroups(int id){
        List<AssemblyGroupDetails> filtered = new ArrayList<>();
        for(AssemblyGroupDetails details : counts){
            if(details.getParentNodeId() == id){
                filtered.add(details);
            }
        }
        return filtered;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<AssemblyGroupDetails> getCounts() {
        return counts;
    }

    public void setCounts(List<AssemblyGroupDetails> counts) {
        this.counts = counts;
    }
}
