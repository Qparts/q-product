package q.rest.product.model.catalog;

import java.util.List;

public class CatalogPartsPosition {

    private String number;
    private int[] coordinates;

    public int[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(int[] coordinates) {
        this.coordinates = coordinates;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }


}
