package q.rest.product.helper;

public class AppConstants {

    //SERVICES
    private static final String USER_SERVICE =  SysProps.getValue("userService");
    private static final String CUSTOMER_SERVICE = SysProps.getValue("customerService");
    //AWS
    private static final String AMAZON_S3_PATH = SysProps.getValue("amazonS3Path");
    private static final String PRODUCT_BUCKET_NAME =SysProps.getValue("productBucketName");
    private static final String BRAND_BUCKET_NAME = SysProps.getValue("brandBucketName");

    public static final String CUSTOMER_MATCH_TOKEN = CUSTOMER_SERVICE + "match-token";
    public static final String USER_MATCH_TOKEN = USER_SERVICE + "match-token";


    public static final String getProductImage(long id){
        return AMAZON_S3_PATH + PRODUCT_BUCKET_NAME + "/" + id + ".png";
    }

    public static final String getBrandImage(long id){
        return AMAZON_S3_PATH + BRAND_BUCKET_NAME + "/" +  id + ".png";
    }

}
