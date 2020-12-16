package q.rest.product.model.search;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class SearchObject {
    private String query;
    private int specialOfferId;
    private int subscriberId;
    private int companyId;
    private int max;
    private int offset;
    private String filter;
    private List<SearchLocationFilter> locationFilters = new ArrayList<>();

    @JsonIgnore
    public boolean isNewSearch(){
        return (max == 0 && offset == 0 && (filter == null || filter.equals("")) && locationFilters.isEmpty());
    }

    @JsonIgnore
    public String getLocationFiltersSql(String alias, boolean isNative){
        String regional = getFiltersSql('R', alias, "and", isNative);
        String country = getFiltersSql('C', alias, regional.length() > 0 ? "or" : "and", isNative);
        String city = getFiltersSql('T', alias, regional.length() + country.length() > 0 ? "or" : "and", isNative);
        return regional + country + city;
    }

    @JsonIgnore
    private String getFiltersSql(char type, String alias, String qualifier, boolean isNative){
        String variable = "";
        if(type == 'R') variable = isNative ? "region_id" : "regionId";
        if(type == 'C') variable = isNative ? "country_id" : "countryId";
        if(type == 'T') variable = isNative ? "city_id" : "cityId";
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

    public int getSpecialOfferId() {
        return specialOfferId;
    }

    public void setSpecialOfferId(int specialOfferId) {
        this.specialOfferId = specialOfferId;
    }
}
