package q.rest.product.operation;
import org.jboss.logging.Logger;
import q.rest.product.dao.DAO;
import q.rest.product.dao.QvmDaoApi;
import q.rest.product.filter.annotation.SubscriberJwt;
import q.rest.product.filter.annotation.UserSubscriberJwt;
import q.rest.product.helper.AppConstants;
import q.rest.product.helper.Helper;
import q.rest.product.model.contract.v3.product.PbProduct;
import q.rest.product.model.product.full.Product;
import q.rest.product.model.product.full.Spec;
import q.rest.product.model.qvm.qvmstock.CompanyOfferUploadRequest;
import q.rest.product.model.qvm.qvmstock.CompanyUploadRequest;
import q.rest.product.model.qvm.qvmstock.minimal.PbCompanyProduct;
import q.rest.product.model.qvm.qvmstock.minimal.PbSpecialOffer;
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
    private DAO dao;

    @EJB
    private QvmDaoApi daoApi;

    @EJB
    private AsyncProductApi async;

    public static final Logger logger = Logger.getLogger(ProductApiV4.class);

    @SubscriberJwt
    @GET
    @Path("special-offers/live")
    public Response getLiveCompanySpecialOfferUpload(@Context UriInfo info) {
        String sql = "select b from PbSpecialOffer b where :value0 between b.startDate and b.endDate and b.status = :value1 order by b.startDate";
        var list = (info.getQueryParameters().getFirst("latest") == null) ?
                dao.getJPQLParams(PbSpecialOffer.class, sql, new Date(), 'C') :
                dao.getJPQLParamsMax(PbSpecialOffer.class, sql, 3, new Date(), 'C');
        return Response.status(200).entity(list).build();
    }

    @SubscriberJwt
    @GET
    @Path("dashboard-metrics")
    public Response getDashboardMetrics(@HeaderParam(HttpHeaders.AUTHORIZATION) String header){
        int companyId = Helper.getCompanyFromJWT(header);
        Map<String,Object> map = new HashMap<>();
        System.out.println("calling no items");
        var numberOfProducts = daoApi.getNumberOfItems();
        System.out.println("calling num of items in stock");
        var numberOfStockProducts = daoApi.getNumberOfItemsInCompanyStock(companyId);
        System.out.println("calling most searchede cata");
        var mostSearchedCatalogBrands = daoApi.getMostSearchedCatalogBrands();
        System.out.println("ok");
        map.put("mostSearchedCatalogBrands", mostSearchedCatalogBrands);
        map.put("numberOfProducts",numberOfProducts);
        map.put("numberOfStockProducts", numberOfStockProducts);
        return Response.status(200).entity(map).build();
    }



    @UserSubscriberJwt
    @POST
    @Path("special-offer-products")
    public Response getSpecialOfferProducts(SearchObject searchObject){
        PbSpecialOffer so = dao.find(PbSpecialOffer.class, searchObject.getSpecialOfferId());
        List<PbCompanyProduct> productsList;
        int searchSize;
        if(searchObject.getFilter() == null || searchObject.getFilter().trim().equals("")){
            String sql2 = "select b from PbCompanyProduct b " +
                    " where b.id in (" +
                    " select c.companyProductId from PbCompanyStockOffer c " +
                    " where c.offerRequestId = :value0)" +
                    " order by b.partNumber";
            productsList = dao.getJPQLParamsOffsetMax(PbCompanyProduct.class, sql2, searchObject.getOffset(), searchObject.getMax(), so.getId());
            searchSize = so.getNumberOfItems();
        } else {
            String search = "%" + Helper.undecorate(searchObject.getFilter()) + "%";
            String sql = "select count(*) from PbCompanyProduct b " +
                    " where b.id in (" +
                    " select c.companyProductId from PbCompanyStockOffer c " +
                    " where c.offerRequestId = :value0)" +
                    " and b.partNumber like :value1";
            searchSize = dao.findJPQLParams(Number.class, sql, so.getId(), search).intValue();
            String sql2 = "select b from PbCompanyProduct b " +
                    " where b.id in (" +
                    " select c.companyProductId from PbCompanyStockOffer c " +
                    " where c.offerRequestId = :value0)" +
                    " and b.partNumber like :value1" +
                    " order by b.partNumber";
            productsList = dao.getJPQLParamsOffsetMax(PbCompanyProduct.class, sql2, searchObject.getOffset(), searchObject.getMax(), so.getId(), search);
        }
        Map<String,Object> map = new HashMap<>();
        map.put("products", productsList);
        map.put("searchSize", searchSize);
        return Response.status(200).entity(map).build();
    }

    @SubscriberJwt
    @POST
    @Path("search-products")
    public Response searchProducts(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, SearchObject searchObject){

        logger.info("start to get products with "+searchObject.getQuery());

        if(searchObject.getQuery() == null || searchObject.getQuery().length() == 0){
            return Response.status(404).build();
        }
        String partNumber = "%" + Helper.undecorate(searchObject.getQuery()) + "%";
        String jpql = "select b from Product b where b.productNumber like :value0 and b.status =:value1";
        List<Product> products = dao.getJPQLParams(Product.class, jpql, partNumber, 'A');

        List<Spec> specs = dao.get(Spec.class);
        List<PbProduct> pbProducts = new ArrayList<>();
        for (var product : products) {
            pbProducts.add(product.getPublicProduct(specs));
        }

        logger.info("products size "+pbProducts.size());

        return Response.status(200).entity(pbProducts).build();
    }

    @SubscriberJwt
    @POST
    @Path("search-company-products")
    public Response searchCompanyProducts(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, SearchObject searchObject){
        if(searchObject.getQuery() == null || searchObject.getQuery().length() == 0){
            return Response.status(200).entity(new HashMap<>()).build();
        }
        var size = daoApi.searchCompanyProductSize(searchObject);
        var companyProducts = daoApi.searchCompanyProductsPublic(searchObject);
        async.saveSearch2(header, searchObject, size > 0);
        Map<String,Object> map = new HashMap<>();
        map.put("searchSize", size);
        map.put("companyProducts", companyProducts);
        return Response.status(200).entity(map).build();
    }

    @POST
    @Path("search-replacement-product")
    @SubscriberJwt
    public Response searchReplacement(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, Map<String,String> queryMap){
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
        int companyId = Helper.getCompanyFromJWT(header);
        int subscriberId = Helper.getSubscriberFromJWT(header);
        uploadRequest.setCreatedBySubscriber(subscriberId);
        uploadRequest.setCompanyId(companyId);
        uploadRequest.setCreated(new Date());
        uploadRequest.setStatus('R');
        uploadRequest.setUploadSource('Q');//from qvm user
        daoApi.createStockUploadRequest(uploadRequest);
        Map<String,Integer> map = new HashMap<>();
        map.put("id", uploadRequest.getId());
        return Response.status(200).entity(map).build();
    }


    @SubscriberJwt
    @Path("upload-request/special-offer")
    @POST
    public Response requestUploadSpecialOffer(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, CompanyOfferUploadRequest uploadRequest) {
        int companyId = Helper.getCompanyFromJWT(header);
        int subscriberId = Helper.getSubscriberFromJWT(header);
        uploadRequest.setCreatedBySubscriber(subscriberId);
        uploadRequest.setCompanyId(companyId);
        uploadRequest.setCreated(new Date());
        uploadRequest.setStatus('R');
        uploadRequest.setUploadSource('Q');//from qvm user
        uploadRequest.setOfferNameAr(uploadRequest.getOfferName());
        daoApi.createOfferUploadRequest(uploadRequest);
        Map<String,Integer> map = new HashMap<>();
        map.put("id", uploadRequest.getId());
        return Response.status(200).entity(map).build();
    }

}
