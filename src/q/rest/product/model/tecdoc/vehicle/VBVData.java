package q.rest.product.model.tecdoc.vehicle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VBVData {
    private VBVMatchingManufacturers matchingManufacturers;
    private VBVMatchingModels matchingModels;
    private VBVMatchingVehicles matchingVehicles;
    private int matchingVehiclesCount;
    private List<VBVDataSource> dataSource;


    @JsonIgnore
    public String getManufacturerById(int id){
        try{
            for (var vr : matchingManufacturers.getArray()){
                if(vr.getManuId() == id){
                    return vr.getManuName();
                }
            }
            throw new NullPointerException();
        }catch (NullPointerException ex){
            return String.valueOf(id);
        }
    }

    @JsonIgnore
    public String getModelById(int id){
        try{
            for (var vr : matchingModels.getArray()){
                if(vr.getManuId() == id){
                    return vr.getModelName();
                }
            }
            throw new NullPointerException();
        }catch (NullPointerException ex){
            return String.valueOf(id);
        }


    }

    public VBVMatchingManufacturers getMatchingManufacturers() {
        return matchingManufacturers;
    }

    public void setMatchingManufacturers(VBVMatchingManufacturers matchingManufacturers) {
        this.matchingManufacturers = matchingManufacturers;
    }

    public VBVMatchingModels getMatchingModels() {
        return matchingModels;
    }

    public void setMatchingModels(VBVMatchingModels matchingModels) {
        this.matchingModels = matchingModels;
    }

    public VBVMatchingVehicles getMatchingVehicles() {
        return matchingVehicles;
    }

    public void setMatchingVehicles(VBVMatchingVehicles matchingVehicles) {
        this.matchingVehicles = matchingVehicles;
    }

    public int getMatchingVehiclesCount() {
        return matchingVehiclesCount;
    }

    public void setMatchingVehiclesCount(int matchingVehiclesCount) {
        this.matchingVehiclesCount = matchingVehiclesCount;
    }

    public List<VBVDataSource> getDataSource() {
        return dataSource;
    }

    public void setDataSource(List<VBVDataSource> dataSource) {
        this.dataSource = dataSource;
    }
}
