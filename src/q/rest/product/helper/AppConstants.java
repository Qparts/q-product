package q.rest.product.helper;

import java.util.ArrayList;
import java.util.List;

public class AppConstants {

    public static final String INTERNAL_APP_SECRET = "INTERNAL_APP";
    //SERVICES
    private static final String SUBSCRIBER_SERVICE =  SysProps.getValue("subscriberService");
    private static final String SUBSCRIBER_SERVICE_V2 = SUBSCRIBER_SERVICE.replace("/v1/", "/v2/");
    private static final String APP_BASE = SysProps.getValue("qvmWebAppBase");
    private static final String LOCATION_SERVICE = SysProps.getValue("locationService").replace("/v2/", "/v3/");
    private static final String CUSTOMER_SERVICE = SysProps.getValue("customerService");
    private static final String INVOICE_SERVICE = SysProps.getValue("invoiceService").replace("/v2/", "/v3/");

    //AWS
    private static final String AMAZON_S3_PATH = SysProps.getValue("amazonS3Path");
    private static final String PRODUCT_BUCKET_NAME = SysProps.getValue("productBucketName");
    private static final String BRAND_BUCKET_NAME = SysProps.getValue("brandBucketName");

    //PARTS CATALOG
    private static final String PARTS_CATALOG_API = "https://api.parts-catalogs.com/v1/";
    public static final String PARTS_CATALOG_API_KEY = SysProps.getValue("tradesoftCatKey");

    //TECH DOC
    public static final String TECH_DOC_API_LINK = "https://webservice.tecalliance.services/pegasus-3-0/services/TecdocToCatDLB.jsonEndpoint";
    public static final String TECH_DOC_API_KEY = SysProps.getValue("techDocKey");

    //INVOICE SERVICE
    public final static String POST_PAYMENT_REQUEST = INVOICE_SERVICE + "payment-order";


    //LOCATION SERVICE
    public static final String POST_CITIES_REDUCED = LOCATION_SERVICE + "cities/reduced";

    public static final String CUSTOMER_MATCH_TOKEN =  "match-token";
    public static final String USER_MATCH_TOKEN =  "match-token";
    public static final String VENDOR_MATCH_TOKEN =  "match-token";
    public static final String VENDOR_MATCH_TOKEN_WS =  "match-token/ws";
    public static final String USER_MATCH_TOKEN_WS = "match-token/ws";

    //Subscriber Service
    public static final String POST_SAVE_SEARCH_KEYWORD = SUBSCRIBER_SERVICE + "search-keyword";
    public static final String POST_SAVE_SEARCH_KEYWORD2 = SUBSCRIBER_SERVICE_V2 + "search-keyword";
    public static final String GET_BRANCHES_IDS = SUBSCRIBER_SERVICE_V2 + "branches/ids";
    public static final String POST_SAVE_REPLACEMENTS_KEYWORD = SUBSCRIBER_SERVICE + "replacement-search-keyword";
    public static final String POST_COMPANIES_REDUCED = SUBSCRIBER_SERVICE + "companies/reduced";
    public static final String POST_DEFAULT_POLICIES = SUBSCRIBER_SERVICE_V2 + "default-policy";
    public static final String GET_DASHBOARD_METRICS_ALLOWED = SUBSCRIBER_SERVICE_V2 + "dashboard-metrics-allowed";

    public static String getCompaniesVisibleFromIds(List<Integer> idArray){
        StringBuilder ids = new StringBuilder("0");
        for (int id : idArray) {
            ids.append(",").append(id);
        }
        return SUBSCRIBER_SERVICE_V2 + "companies/" +ids+"/visible";
    }

    public static  String getPullChunkSize(int companyId) {
        return  SUBSCRIBER_SERVICE + "pull-chunk-size/company/" + companyId;
    }


    //CUSTOMER SERVICE
    public static String SEARCH_CUSTOMER_IDS = CUSTOMER_SERVICE + "search-customer-ids";
    public static String SEARCH_SUPPLIER_IDS = CUSTOMER_SERVICE + "search-supplier-ids";

    public static String getCustomers(String ids){
        return CUSTOMER_SERVICE + "customers/" + ids;
    }

    public static String getSuppliers(String ids){
        return CUSTOMER_SERVICE + "suppliers/" + ids;
    }

    public static String getCustomer(int id){
        return CUSTOMER_SERVICE + "customer/" + id;
    }

    public static String getSupplier(int id){
        return CUSTOMER_SERVICE + "supplier/" + id;
    }

    public static String getCatalogImageReplacedLink(String string){
        try {
            return APP_BASE + "cat-img/" + string.substring("//img.parts-catalogs.com/".length());
        }catch (Exception ex){
            return string;
        }
    }

    public static String getTechDocImageReplacedLink(String string){
        try{
            return APP_BASE + "rep-img/" + string.substring("https://digital-assets.tecalliance.services/images/".length());
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


    public static final String GET_CATALOGS = PARTS_CATALOG_API + "catalogs/";


    public static String getCatalogModels(String catId){
        return PARTS_CATALOG_API + "catalogs/" + catId + "/models";
    }

    public static String getCarInfo(String query){
      return PARTS_CATALOG_API + "car/info?q=" + query;
    }


    public static String getCatalogCarsByVin(String catalogId, String vin){
        return PARTS_CATALOG_API + "catalogs/" + catalogId + "/cars-by-vin?vin=" + vin;
    }

    public static String getCatalogCarsByModel(String catalog, String modelId, String params){
        String link =  PARTS_CATALOG_API + "catalogs/" + catalog + "/cars2?modelId=" + modelId;
        if(params != null) link += "&parameter=" +params;
        return link;
    }

    public static String getCatalogCarFiltersByModel(String catalog, String modelId, String params){
        String link =  PARTS_CATALOG_API + "catalogs/" + catalog + "/cars-parameters?modelId=" + modelId;
        if(params != null) link += "&parameter=" +params;
        return link;
    }

    public static final String getCatalogGroups(String catalogId, String carId, String groupId, String criteria){
        String link = PARTS_CATALOG_API + "catalogs/" + catalogId + "/groups2?carId=" + carId;
        if(criteria != null ) link += "&criteria=" + criteria;
        if(groupId != null) link+= "&groupId=" + groupId;
        return link;
    }

    public static final String getCatalogParts(String catalogId, String carId, String groupId, String criteria){
        String link = PARTS_CATALOG_API + "catalogs/" + catalogId + "/parts2?carId=" + carId + "&groupId=" + groupId;
        if(criteria != null) link += "&criteria=" + criteria;
        return link;
    }

}
