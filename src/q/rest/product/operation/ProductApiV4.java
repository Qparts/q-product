package q.rest.product.operation;
import org.jboss.logging.Logger;
import q.rest.product.dao.QvmDaoApi;
import q.rest.product.filter.annotation.SubscriberJwt;
import q.rest.product.helper.AppConstants;
import q.rest.product.helper.Helper;
import q.rest.product.model.contract.v3.product.PbProduct;
import q.rest.product.model.qvm.qvmstock.CompanyOfferUploadRequest;
import q.rest.product.model.qvm.qvmstock.CompanyUploadRequest;
import q.rest.product.model.qvm.qvmstock.minimal.PbCompanyProduct;
import q.rest.product.model.search.SearchObject;
import q.rest.product.model.tecdoc.article.ArticleImage;
import q.rest.product.model.tecdoc.article.ArticleResponse;

import javax.ejb.EJB;

import javax.ws.rs.*;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.*;
import java.util.*;

@Path("/api/v4/main/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductApiV4 {

    @EJB
    private QvmDaoApi qvmDaoApi;

    @EJB
    private AsyncProductApi async;

    private static final Logger logger = Logger.getLogger(ProductApiV4.class);


    @SubscriberJwt
    @GET
    @Path("special-offers/live")
    public Response getLiveCompanySpecialOfferUpload(@Context UriInfo info) {
        logger.info("get special offers live");
        var latest = info.getQueryParameters().getFirst("latest") != null;
        var list = qvmDaoApi.getLiveSpecialOffers(latest);
        logger.info("get special offers live done");
        return Response.status(200).entity(list).build();
    }

    @SubscriberJwt
    @GET
    @Path("dashboard-metrics")
    public Response getDashboardMetrics(@HeaderParam(HttpHeaders.AUTHORIZATION) String header){
        logger.info("get dashboard metrics");
        int companyId = Helper.getCompanyFromJWT(header);
        Map<String,Object> map = new HashMap<>();
        var numberOfProducts = qvmDaoApi.getNumberOfItems();
        var numberOfStockProducts = qvmDaoApi.getNumberOfItemsInCompanyStock(companyId);
        var mostSearchedCatalogBrands = qvmDaoApi.getMostSearchedCatalogBrands();
        map.put("mostSearchedCatalogBrands", mostSearchedCatalogBrands);
        map.put("numberOfProducts",numberOfProducts);
        map.put("numberOfStockProducts", numberOfStockProducts);
        //premium
        Response r = async.getSecuredRequest(AppConstants.GET_DASHBOARD_METRICS_ALLOWED, header);
        if(r.getStatus() == 201){
            var monthlySearchCountOnStock = qvmDaoApi.getMonthlySearchCount(companyId);
            var mostSearchedProductsOnStock = qvmDaoApi.getMostSearchedProductsOnStock(companyId);
            var mostActiveCompaniesOnStock = qvmDaoApi.getMostActiveCompaniesOnStock(header, companyId);
            map.put("monthlySearchCountOnStock", monthlySearchCountOnStock);
            map.put("mostSearchedProductsOnStock",mostSearchedProductsOnStock);
            map.put("mostActiveCompaniesOnStock", mostActiveCompaniesOnStock);

        }
        logger.info("get dashboard metrics done");
        return Response.status(200).entity(map).build();
    }



    @SubscriberJwt
    @POST
    @Path("special-offer-products")
    public Response getSpecialOfferProducts(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, SearchObject searchObject){
        logger.info("search special offer products");
        Map<String,Object> map = qvmDaoApi.searchSpecialOfferProducts(searchObject);
        List<PbCompanyProduct> list = (List<PbCompanyProduct>) map.get("products");
        async.addToSpecialOfferList(header, searchObject.getSpecialOfferId(), searchObject.getOffset(), list.size(), searchObject.getMax(), searchObject.getFilter());
        logger.info("search special offer products 2");
        return Response.status(200).entity(map).build();
    }

    @SubscriberJwt
    @POST
    @Path("search-products")
    public Response searchProducts(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, SearchObject searchObject){
        logger.info("search products");
        if(searchObject.getQuery() == null || searchObject.getQuery().length() == 0){
            return Response.status(404).build();
        }
        List<PbProduct> products = qvmDaoApi.searchProducts(searchObject.getQuery());
        logger.info("search products done");
        return Response.status(200).entity(products).build();
    }

    //not yet implemented
    @SubscriberJwt
    @PUT
    @Path("company-product-price")
    public Response updateCompanyProductPrice(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, Map<String,Number> map){
        logger.info("update company product price");
        int companyId = Helper.getCompanyFromJWT(header);
        long companyProductId = map.get("productId").longValue();
        double retailPrice = map.get("retailPrice").doubleValue();
        var companyProduct = qvmDaoApi.updateCompanyProductPrice(companyProductId, companyId, retailPrice);
        logger.info("update company product price done");
        return Response.status(200).entity(companyProduct).build();
    }

    //not yet implemented
    @SubscriberJwt
    @PUT
    @Path("company-product-quantity")
    public Response updateCompanyProductQuantity(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, Map<String,Number> map){
        logger.info("update company product quantity");
        int companyId = Helper.getCompanyFromJWT(header);
        long companyProductId = map.get("productId").longValue();
        int branchId = map.get("branchId").intValue();
        int quantity = map.get("quantity").intValue();
        var companyStock = qvmDaoApi.updateCompanyProductStockQuantity(companyProductId, companyId, branchId, quantity);
        logger.info("update company product quantity done");
        return Response.status(200).entity(companyStock).build();
    }

    @SubscriberJwt
    @POST
    @Path("search-company-products")
    public Response searchCompanyProducts(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, SearchObject searchObject){
        logger.info("search company products");
        if(searchObject.getQuery() == null || searchObject.getQuery().length() == 0){
            return Response.status(200).entity(new HashMap<>()).build();
        }
        var size = qvmDaoApi.searchCompanyProductSize(searchObject);
        var companyProducts = qvmDaoApi.searchCompanyProductsPublic(searchObject);
        async.saveSearch2(header, searchObject, size > 0);
        async.addToSearchList(header, companyProducts);
        Map<String,Object> map = new HashMap<>();
        map.put("searchSize", size);
        map.put("companyProducts", companyProducts);
        logger.info("search company products done");
        return Response.status(200).entity(map).build();
    }


    //not tested
    @SubscriberJwt
    @GET
    @Path("search-lists/year/{year}/month/{month}")
    public Response getTargetVendorQuotations(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, @PathParam(value = "year") int year, @PathParam(value = "month") int month) {
        logger.info("get search list by year and month");
        Date from = Helper.getFromDate(month, year);
        Date to = Helper.getToDate(month, year);
        int companyId = Helper.getCompanyFromJWT(header);
        var searchLists = qvmDaoApi.getSearchList(header, companyId, from , to);
        logger.info("get search list by year and month done");
        return Response.ok().entity(searchLists).build();
    }


    @POST
    @Path("search-replacement-product")
    @SubscriberJwt
    public Response searchReplacement(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, Map<String,String> queryMap){
        logger.info("search replacement products");
        String query = queryMap.get("query");
        Map<String, Object> map = new HashMap<>();
        map.put("articleCountry", "SA");
        map.put("lang", "en");
        map.put("provider", "22423");
        map.put("searchType", 10);
        map.put("searchQuery", query);
        map.put("perPage", 100);
        map.put("page", 1);
        map.put("includeAll", true);

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("getArticles", map);
        Response r = this.postSecuredRequest(requestMap);
        if(r.getStatus() == 200){
            ArticleResponse ar = r.readEntity(ArticleResponse.class);
            boolean found = !ar.getArticles().isEmpty();
            async.saveReplacementSearch(header, query, found);
            ar.getArticles().forEach(art -> art.getImages().forEach(ArticleImage::replaceImages));

            return Response.ok().entity(ar).build();
        }
        else r.close();
        logger.info("search replacement products done");
        return Response.status(404).build();
    }


    public <T> Response postSecuredRequest(T t) {
        Invocation.Builder b = ClientBuilder.newClient().target(AppConstants.TECH_DOC_API_LINK).request();
        b.header("X-Api-Key", AppConstants.TECH_DOC_API_KEY);
        Response r = b.post(Entity.entity(t, "application/json"));
        return r;
    }



    @SubscriberJwt
    @Path("upload-request/stock")
    @POST
    public Response requestStockUpload(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, CompanyUploadRequest uploadRequest) {
        logger.info("upload request / stock");
        int companyId = Helper.getCompanyFromJWT(header);
        int subscriberId = Helper.getSubscriberFromJWT(header);
        uploadRequest.setCreatedBySubscriber(subscriberId);
        uploadRequest.setCompanyId(companyId);
        uploadRequest.setCreated(new Date());
        uploadRequest.setStatus('R');
        uploadRequest.setUploadSource('Q');//from qvm user
        qvmDaoApi.createStockUploadRequest(uploadRequest);
        Map<String,Integer> map = new HashMap<>();
        map.put("id", uploadRequest.getId());
        logger.info("upload request / stock done");
        return Response.status(200).entity(map).build();
    }


    @SubscriberJwt
    @Path("upload-request/special-offer")
    @POST
    public Response requestUploadSpecialOffer(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, CompanyOfferUploadRequest uploadRequest) {
        logger.info("upload request / special offers");
        int companyId = Helper.getCompanyFromJWT(header);
        int subscriberId = Helper.getSubscriberFromJWT(header);
        uploadRequest.setCreatedBySubscriber(subscriberId);
        uploadRequest.setCompanyId(companyId);
        uploadRequest.setCreated(new Date());
        uploadRequest.setStatus('R');
        uploadRequest.setUploadSource('Q');//from qvm user
        uploadRequest.setOfferNameAr(uploadRequest.getOfferName());
        qvmDaoApi.createOfferUploadRequest(uploadRequest);
        Map<String,Integer> map = new HashMap<>();
        map.put("id", uploadRequest.getId());
        logger.info("upload request / special offers done");
        return Response.status(200).entity(map).build();
    }

}
