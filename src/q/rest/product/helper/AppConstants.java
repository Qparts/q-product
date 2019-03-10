package q.rest.product.helper;

public class AppConstants {

    //SERVICES
    private static final String USER_SERVICE =  SysProps.getValue("userService");
    private static final String CUSTOMER_SERVICE = SysProps.getValue("customerService");

    //AWS
    private static final String AMAZON_S3_PATH = SysProps.getValue("amazonS3Path");
    private static final String PRODUCT_BUCKET_NAME =SysProps.getValue("productBucketName");
    private static final String BRAND_BUCKET_NAME = SysProps.getValue("brandBucketName");

    //PARTS CATALOG
    private static final String PARTS_CATALOG_API = "https://api.parts-catalogs.com/v1/";
    public static final String PARTS_CATALOG_API_KEY = "OEM-API-9BC9464D-D8ED-4D69-8943-32CF9FA0D3F7";

    public static final String CUSTOMER_MATCH_TOKEN = CUSTOMER_SERVICE + "match-token";
    public static final String USER_MATCH_TOKEN = USER_SERVICE + "match-token";


    public static final String getProductImage(long id){
        return AMAZON_S3_PATH + PRODUCT_BUCKET_NAME + "/" + id + ".png";
    }

    public static final String getBrandImage(long id){
        return AMAZON_S3_PATH + BRAND_BUCKET_NAME + "/" +  id + ".png";
    }


    public static final String getCatalogCarsByVin(String catalogId, String vin){
        return PARTS_CATALOG_API + "catalogs/" + catalogId + "/cars-by-vin?vin=" + vin;
    }

    public static final String getCatalogGroups(String catalogId, String carId, String groupId){
        String link = PARTS_CATALOG_API + "catalogs/" + catalogId + "groups2?carId=" + carId;
        if(groupId != null) {
            link+= "&groupId=" + groupId;
        }
        return link;
    }

    public static final String getCatalogParts(String catalogId, String carId, String groupId){
        return PARTS_CATALOG_API + "catalogs/" + catalogId + "/parts2?carId=" + carId + "&groupId=" + groupId;
    }

}
