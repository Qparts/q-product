package q.rest.product.model.search;

import java.util.ArrayList;
import java.util.List;

public class SearchObject {
    private String query;
    private int subscriberId;
    private int companyId;
    private int max;
    private int offset;
    private String filter;
    private List<SearchLocationFilter> locationFilters = new ArrayList<>();



    public String getLocationFiltersSql(String alias){
        String regional = getFiltersSql('R', alias, "and");
        String country = getFiltersSql('C', alias, regional.length() > 0 ? "or" : "and");
        String city = getFiltersSql('T', alias, regional.length() + country.length() > 0 ? "or" : "and");
        return regional + country + city;
    }

    private String getFiltersSql(char type, String alias, String qualifier){
        String variable = "";
        if(type == 'R') variable = "regionId";
        if(type == 'C') variable = "countryId";
        if(type == 'T') variable = "cityId";
        if(locationFilters.size() > 0) {
            int n = 0;
            StringBuilder sql = new StringBuilder(" " + qualifier + " " + alias + "." + variable + " in (0");
            for (var lf : locationFilters) {
                if (lf.getType() == type) {
                    sql.append(",").append(lf.getId(type));
                    n++;
                }
            }
            sql.append(")");
            if(n == 0) sql = new StringBuilder();
            return sql.toString();
        }
        return "";
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(int subscriberId) {
        this.subscriberId = subscriberId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public List<SearchLocationFilter> getLocationFilters() {
        return locationFilters;
    }

    public void setLocationFilters(List<SearchLocationFilter> locationFilters) {
        this.locationFilters = locationFilters;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }
}
