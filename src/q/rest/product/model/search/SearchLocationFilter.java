package q.rest.product.model.search;

public class SearchLocationFilter {
    private char type;// R = region  , C = country, T = city
    private int cityId;
    private int regionId;
    private int countryId;


    public int getId(char type){
        switch (type){
            case 'R': return regionId;
            case 'T': return cityId;
            case 'C': return countryId;
            default: return 0;
        }
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }
}
