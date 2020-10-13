package q.rest.product.helper;

public class AppConstants {

    public static final String INTERNAL_APP_SECRET = "INTERNAL_APP";
    //SERVICES

    private static final String SUBSCRIBER_SERVICE =  SysProps.getValue("subscriberService");
    private static final String APP_BASE = SysProps.getValue("qvmWebAppBase");
    private static final String LOCATION_SERVICE = SysProps.getValue("locationService").replace("/v2/", "/v3/");

    //AWS
    private static final String AMAZON_S3_PATH = SysProps.getValue("amazonS3Path");
    private static final String PRODUCT_BUCKET_NAME =SysProps.getValue("productBucketName");
    private static final String BRAND_BUCKET_NAME = SysProps.getValue("brandBucketName");

    //PARTS CATALOG
    private static final String PARTS_CATALOG_API = "https://api.parts-catalogs.com/v1/";
    public static final String PARTS_CATALOG_API_KEY = SysProps.getValue("tradesoftCatKey");


    //LOCATION SERVICE
    public static final String POST_CITIES_REDUCED = LOCATION_SERVICE + "cities/reduced";

    public static final String CUSTOMER_MATCH_TOKEN =  "match-token";
    public static final String USER_MATCH_TOKEN =  "match-token";
    public static final String VENDOR_MATCH_TOKEN =  "match-token";
    public static final String VENDOR_MATCH_TOKEN_WS =  "match-token/ws";
    public static final String USER_MATCH_TOKEN_WS = "match-token/ws";


    //Subscriber Service
    public static final String POST_SAVE_SEARCH_KEYWORD = SUBSCRIBER_SERVICE + "search-keyword";
    public static final String POST_COMPANIES_REDUCED = SUBSCRIBER_SERVICE + "companies/reduced";

    //VENDOR SERVICE
    public static final String PUT_UPDATE_SEARCH_AVAILABILITY_WITH_BRANCHES =  "search-availability/update-branches";


    public static final String GET_CATALOGS = PARTS_CATALOG_API + "catalogs/";


    public static String getCatalogModels(String catId){
        return PARTS_CATALOG_API + "catalogs/" + catId + "/models";
    }

    public static String getImageReplacedLink(String string){
        try {
            return APP_BASE + "cat-img/" + string.substring("//img.parts-catalogs.com/".length());
        }catch (Exception ex){
            return string;
        }
    }


    public static final String getProductImage(long id){
        return AMAZON_S3_PATH + PRODUCT_BUCKET_NAME + "/" + id + ".png";
    }

    public static final String getBrandImage(long id){
        return AMAZON_S3_PATH + BRAND_BUCKET_NAME + "/" +  id + ".png";
    }


    public static final String getCatalogCarsByVin(String catalogId, String vin){
        return PARTS_CATALOG_API + "catalogs/" + catalogId + "/cars-by-vin?vin=" + vin;
    }

    public static String getCatalogCarsByModel(String catalog, String modelId){
        return PARTS_CATALOG_API + "catalogs/" + catalog + "/cars2?modelId=" + modelId;
    }

    public static String getCatalogCarFiltersByModel(String catalog, String modelId){
        return PARTS_CATALOG_API + "catalogs/" + catalog + "/cars-parameters?modelId=" + modelId;
    }

    public static final String getCatalogGroups(String catalogId, String carId, String groupId, String criteria){
        String link = PARTS_CATALOG_API + "catalogs/" + catalogId + "/groups2?carId=" + carId;
        link += "&criteria=" + criteria;
        if(groupId != null) {
            link+= "&groupId=" + groupId;
        }
        return link;
    }

    public static final String getCatalogParts(String catalogId, String carId, String groupId, String criteria){
        return PARTS_CATALOG_API + "catalogs/" + catalogId + "/parts2?carId=" + carId + "&groupId=" + groupId + "&criteria=" + criteria;
    }

}
