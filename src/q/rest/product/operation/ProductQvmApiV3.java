package q.rest.product.operation;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.jboss.logging.Logger;
import q.rest.product.dao.DAO;
import q.rest.product.dao.QvmDaoApi;
import q.rest.product.filter.annotation.SubscriberJwt;
import q.rest.product.filter.annotation.UserJwt;
import q.rest.product.filter.annotation.UserSubscriberJwt;
import q.rest.product.helper.AppConstants;
import q.rest.product.helper.Helper;
import q.rest.product.helper.KeyConstant;
import q.rest.product.model.contract.v3.*;
import q.rest.product.model.contract.v3.product.PbProduct;
import q.rest.product.model.product.market.*;
import q.rest.product.model.qvm.qvmstock.*;
import q.rest.product.model.product.full.Product;
import q.rest.product.model.product.full.Spec;
import q.rest.product.model.search.SearchObject;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@Path("/api/v3/qvm/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductQvmApiV3 {

    @EJB
    private DAO dao;

    @EJB
    private AsyncProductApi async;

    @EJB
    private QvmDaoApi daoApi;

    private static final Logger logger = Logger.getLogger(ProductQvmApiV3.class);

    @UserJwt
    @GET
    @Path("last-pulls")
    public Response getCompaniesLastPulls() {
        logger.info("get last pulls");
        List<DataPullHistory> pullHistories = daoApi.getLatestPulls();
        logger.info("get last pulls done");
        return Response.ok().entity(pullHistories).build();
    }


    @SubscriberJwt
    @GET
    @Path("sample-products")
    public Response getSampleProducts(@HeaderParam(HttpHeaders.AUTHORIZATION) String header){
        logger.info("get sample products");
        int companyId = Helper.getCompanyFromJWT(header);
        List<CompanyProduct> products = daoApi.getSampleProducts(companyId);
        logger.info("get sample products done");
        return Response.ok().entity(products).build();
    }


    @UserJwt
    @GET
    @Path("vin-search-activity/from/{from}/to/{to}")
    public Response getVinSearchReport(@PathParam(value = "from") long fromLong, @PathParam(value = "to") long toLong) {
        logger.info("get vin search activities from to");
        Helper h = new Helper();
        List<Date> dates = h.getAllDatesBetween(new Date(fromLong), new Date(toLong));
        List<Map<String,Object>> kgs = daoApi.getVinSearchReport(dates);
        logger.info("get vin search activities from to done");
        return Response.status(200).entity(kgs).build();
    }

    @UserJwt
    @PUT
    @Path("update-stock")
    public Response updateStock(UploadHolder holder) {
        logger.info("update stock");
        daoApi.updateStockAsyncOptimized(holder);
        logger.info("update stock done");
        return Response.status(200).build();
    }

    @UserJwt
    @PUT
    @Path("update-q-stock-stock")
    public Response updateQStockStock(QStockUploadHolder holder) {
        logger.info("update q stock stock");
        daoApi.updateQStockAsyncOptimized(holder);
        logger.info("update q stock stock done");
        return Response.status(200).build();
    }

    @UserJwt
    @PUT
    @Path("update-special-offer-stock")
    public Response updateSpecialOfferStock(UploadHolder uploadHolder) {
        logger.info("update special offer stock");
        daoApi.updateSpecialOfferStockAsyncOptimized(uploadHolder);
        logger.info("update specoal offer stock done");
        return Response.status(200).build();
    }

    @UserJwt
    @POST
    @Path("offer-brand-tag")
    public Response createBrandTag(Map<String,Object> map){
        int offerId = ((Number) map.get("offerId")).intValue();
        String brandTag = (String) map.get("brandTag");
        daoApi.createBrandTag(offerId, brandTag);
        return Response.status(200).build();
    }

    @UserJwt
    @DELETE
    @Path("offer-brand-tag/offer/{offerId}/brand-tag/{brandTag}")
    public Response deleteBrandTag(@PathParam(value = "offerId") int offerId, @PathParam(value = "brandTag") String brandTag){
        daoApi.deleteBrandTag(offerId, brandTag);
        return Response.status(200).build();
    }

    @UserSubscriberJwt
    @Path("upload-special-offer-requests/{companyId}")
    @GET
    public Response getUploadSpecialOfferRequests(@PathParam(value = "companyId") int companyId) {
        logger.info("upload special offer request by company id");
        List<CompanyOfferUploadRequest> list = daoApi.getOfferUploadRequest(companyId);
        logger.info("upload special offerr request by company id done");
        return Response.status(200).entity(list).build();
    }

    @UserJwt
    @Path("upload-request")
    @PUT
    public Response updateRequestUpload(CompanyUploadRequest uploadRequest) {
        logger.info("update upload request");
        daoApi.updateUploadRequest(uploadRequest);
        logger.info("update upload request done");
        return Response.status(200).build();
    }

    @UserJwt
    @GET
    @Path("company-uploads/pending")
    public Response getPendingVendorUploads() {
        logger.info("get company uploads pending");
        List<CompanyUploadRequest> uploads = daoApi.getPendingStockUploads();
        logger.info("get company uploads pending done");
        return Response.ok().entity(uploads).build();
    }

    @UserJwt
    @GET
    @Path("company-uploads")
    public Response getAllCompanyUploads() {
        logger.info("get company uploads");
        List<CompanyUploadRequest> uploads = daoApi.getAllStockUploads();
        logger.info("get company uploads done");
        return Response.ok().entity(uploads).build();
    }

    @UserJwt
    @GET
    @Path("company-uploads/special-offer")
    public Response getAllSpecialOfferUploads() {
        logger.info("get company uploads special offer");
        List<CompanyOfferUploadRequest> uploads = daoApi.getAllOfferUploads();
        logger.info("get company uploads special offer done ");
        return Response.status(200).entity(uploads).build();
    }


    @SubscriberJwt
    @GET
    @Path("company-uploads/special-offers/live")
    public Response getLiveCompanySpecialOfferUpload() {
        logger.info("get company uploads special offers live");
        List<CompanyOfferUploadRequest> list = daoApi.getLiveOffers();
        logger.info("get company uploads special offers live done");
        return Response.status(200).entity(list).build();
    }


    @SubscriberJwt
    @Path("special-offer/{offerId}")
    @DELETE
    public Response inactivateSpecialOffer(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, @PathParam(value = "offerId") int id){
        try {
            logger.info("delete special offer by id");
            int companyId = this.getCompanyIdFromHeader(header);

            CompanyOfferUploadRequest offer = daoApi.findOffer(id);
            if(offer.getCompanyId() != companyId) throwError(401);

            daoApi.makeOfferExpired(offer);
            logger.info("delete special offer by id done");
            return Response.status(200).build();
        }catch (Exception e){
            return Response.status(500).build();
        }
    }

    @UserJwt
    @Path("upload-special-offer-request")
    @PUT
    public Response updateSpecialOfferRequestUpload(CompanyOfferUploadRequest uploadRequest) {
        logger.info("update special offer request");
        daoApi.makeOfferCompleted(uploadRequest);
        logger.info("update special offer request done");
        return Response.status(201).build();

    }

    @UserJwt
    @GET
    @Path("summary-report/company/{id}")
    public Response getCompanySummaryRepoort(@PathParam(value = "id") int companyId) {
        logger.info("get summary report by company id");
        SummaryReport report = daoApi.getCompanyProductSummaryReport(companyId);
        logger.info("get summary report by company id done");
        return Response.ok().entity(report).build();
    }


    @UserJwt
    @GET
    @Path("uploads-summary/company/{id}")
    public Response getUploadsSummary(@PathParam(value = "id") int companyId) {
        logger.info("get upload summary by company id");
        UploadsSummary us = daoApi.getCompanyUploadsSummary(companyId);
        logger.info("get upload summary by company id done");
        return Response.ok().entity(us).build();
    }



    @UserJwt
    @GET
    @Path("summary-report")
    public Response getHomeSummary() {
        logger.info("get summary report");
        SummaryReport report = daoApi.getOverallProductSummaryReport();
        logger.info("get summary report done");
        return Response.ok().entity(report).build();
    }

    @UserSubscriberJwt
    @Path("special-offer-upload-request")
    @POST
    public Response requestUploadSpecialOffer(CompanyOfferUploadRequest uploadRequest) {
        logger.info("create special offer upload request");
        daoApi.createOfferUploadRequest(uploadRequest);
        logger.info("create special offer upload request done");
        return Response.status(200).entity(uploadRequest).build();
    }

    @UserSubscriberJwt
    @Path("upload-request")
    @POST
    public Response requestStockUpload(CompanyUploadRequest uploadRequest) {
        logger.info("create upload request");
        daoApi.createStockUploadRequest(uploadRequest);
        logger.info("create upload request done");
        return Response.status(200).entity(uploadRequest).build();
    }

    @UserSubscriberJwt
    @Path("upload-requests/{companyId}")
    @GET
    public Response getUploadRequests(@PathParam(value = "companyId") int companyId) {
        logger.info("get upload requests by company id");
        List<CompanyUploadRequest> list = daoApi.getStockUploadRequests(companyId);
        logger.info("get upload requests by company id done");
        return Response.status(200).entity(list).build();
    }

    @UserSubscriberJwt
    @GET
    @Path("company-uploads/special-offer/{soId}/products/offset/{offset}/max/{max}")
    public Response getSpecialOfferProducts(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, @PathParam(value = "soId") int offerId, @PathParam(value = "offset") int offset, @PathParam(value = "max") int max){
        logger.info("search company uploads special offer id by product offset and max");
        List<CompanyProduct> so = daoApi.getSpecialOfferProducts(offerId, offset, max);
        async.addToSpecialOfferList(header, offerId, offset, so.size(), max, null);
        logger.info("search company uploads special offer id by product offset and max done");
        return Response.status(200).entity(so).build();
    }

    @UserSubscriberJwt
    @GET
    @Path("company-uploads/special-offer/{soId}/products/offset/{offset}/max/{max}/search/{filter}")
    public Response getCompanySpecialOffer(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, @PathParam(value = "soId") int offerId, @PathParam(value = "offset") int offset, @PathParam(value = "max") int max, @PathParam(value = "filter") String filter){
        logger.info("search company uploads special offer id by product offset and max filter");
        Map<String,Object> map = daoApi.getSpecialOfferProductsWithFilter(filter, offerId, offset, max);
        List<CompanyProduct> list = (List<CompanyProduct>) map.get("products");
        async.addToSpecialOfferList(header, offerId, offset, list.size(), max, filter);
        logger.info("search company uploads special offer id by product offset and max filter done");
        return Response.status(200).entity(map).build();
    }

    @SubscriberJwt
    @POST
    @Path("search-company-products")
    public Response searchCompanyProducts(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, SearchObject searchObject){
        logger.info("search company products");
        if(searchObject.getQuery() == null || searchObject.getQuery().length() == 0){
            return Response.status(200).entity(new HashMap<>()).build();
        }
        var size = daoApi.searchCompanyProductSize(searchObject);
        //
        var companyProducts = daoApi.searchCompanyProducts(searchObject);
        //
        async.saveSearch2(header, searchObject, size > 0);
        //
        async.addToSearchListOld(header, companyProducts);

        Map<String,Object> map = new HashMap<>();
        map.put("products", companyProducts);
        map.put("count", size);
        logger.info("search company products done");
        return Response.status(200).entity(map).build();
    }

    private void validateDataPull(int companyId){
        Date date = Helper.addDays(new Date(), -1);
        String sql = "select b from DataPullHistory b where b.companyId = :value0 and b.created > :value1";
        List<DataPullHistory> dph = dao.getJPQLParams(DataPullHistory.class, sql , companyId, date);
        if(!dph.isEmpty()){
            throwError(409, "data can be pulled only once in 24 hours");
        }
    }

    @UserJwt
    @POST
    @Path("pull-stock")
    public Response pullStock(@HeaderParam(HttpHeaders.AUTHORIZATION) String qvmHeader, PullStockRequest psr) {
        logger.info("create pull stock");
        validateDataPull(psr.getCompanyId());
        String header = "Bearer " + psr.getSecret();
        Response r = async.getSecuredRequest(psr.getAllStockEndPoint() + "count", header);
        if (r.getStatus() == 200) {
            Map<String, Integer> countResult = r.readEntity(Map.class);
            int count = countResult.get("count");
            Date pullDate = new Date();
            //create pull first
            DataPullHistory dph = new DataPullHistory();
            dph.setCompanyId(psr.getCompanyId());
            dph.setCreated(pullDate);
            dph.setCreatedBy(psr.getCreatedBy());
            dph.setNumberOfItems(count);
            dph.setStatus('U');
            dao.persist(dph);
            //get links for pull
            int chunk = getChunkSize(psr.getCompanyId(), qvmHeader);
            List<String> links = Helper.getPullDataLinks(count, psr.getAllStockEndPoint(), chunk);
            async.callPullData(links, header, psr, dph);
            return Response.status(200).entity(links).build();
        }
        logger.info("create pull stock done");
        return Response.status(404).entity("error code in calling count : " + r.getStatus()).build();
    }

    private int getChunkSize(int companyId, String header){
        try {
            Response r = async.getSecuredRequest(AppConstants.getPullChunkSize(companyId), header);
            if(r.getStatus() == 200){
                Map<String,Integer> map = r.readEntity(Map.class);
                int chunk = map.get("chunk");
                return chunk > 0 ? chunk : 500;
            }
            return 500;
        }catch (Exception ex){
            return 500;
        }
    }


    @UserSubscriberJwt
    @POST
    @Path("search-parts")
    public Response searchParts(SearchObject searchObject) {
        try {
            logger.info("search parts");
            if(searchObject.getQuery() == null || Helper.undecorate(searchObject.getQuery()).length() < 3){
                return Response.status(404).build();
            }
            String partNumber = "" + Helper.undecorate(searchObject.getQuery()) + "%";
            String jpql = "select b from Product b where b.productNumber like :value0 and b.status =:value1";
            List<Product> products = dao.getJPQLParamsOffsetMax(Product.class, jpql, 0, 10, partNumber, 'A');
            List<Spec> specs = dao.get(Spec.class);
            List<PbProduct> pbProducts = new ArrayList<>();
            for (var product : products) {
                pbProducts.add(product.getPublicProduct(specs));
            }
            logger.info("search parts done");
            return Response.status(200).entity(pbProducts).build();
        } catch (Exception ex) {
            return Response.status(500).build();
        }
    }


    @SubscriberJwt
    @GET
    @Path("market-supplies")
    public Response getMarketSupplies(){
        logger.info("get market supplies");
        String sql = "select b from MarketProduct b where b.id in " +
                "(select c.productId from ProductSupply c where c.quantity > :value0 and c.status = :value1) " +
                "and b.status = :value2";
        List<MarketProduct> products = dao.getJPQLParams(MarketProduct.class, sql, 0, 'A', 'A');
        logger.info("get market supplies done");
        return Response.status(200).entity(products).build();
    }

    @SubscriberJwt
    @POST
    @Path("market-order")
    public Response createMarketOrder(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, @Context HttpServletRequest req, MarketOrderRequest marketRequest){
        logger.info("create market order");
        int companyId = Helper.getCompanyFromJWT(header);
        int subscriberId = Helper.getSubscriberFromJWT(header);
        marketRequest.setClientIp(req.getRemoteAddr());
        marketRequest.setCompanyId(companyId);
        marketRequest.setSubscriberId(subscriberId);
        MarketOrder order = new MarketOrder(marketRequest);
        calculateSalesPrices(order);
        dao.persist(order);
        //calculate base amount
        double itemsAmount = order.getItemsSalesPrice();
        //send order to invoice
        Map<String, Object> paymentRequest = marketRequest.getPaymentRequestObject(order.getId(), itemsAmount);
        Response r = postSecuredRequest(AppConstants.POST_PAYMENT_REQUEST, paymentRequest, header);
        logger.info("create market order done");
        return Response.status(202).entity(r.getEntity()).build();
    }

    private void calculateSalesPrices(MarketOrder order){
        for(var item : order.getItems()) {
            MarketProduct mp = dao.find(MarketProduct.class, item.getMarketProductId());
            item.setSalesPrice(mp.getAverageSalesPrice());
        }
    }

    @SubscriberJwt
    @POST
    @Path("activate-market-order")
    public Response activateMarketOrder(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, Map<String,Integer> map){
        logger.info("activate market order");
        int companyId = Helper.getCompanyFromJWT(header );
        int salesId = map.get("salesId");
        int marketOrderId = map.get("marketOrderId");
        String sql = "select b from MarketOrder b where b.companyId =:value0 and b.id = :value1";
        MarketOrder marketOrder = dao.findJPQLParams(MarketOrder.class, sql , companyId, marketOrderId);
        marketOrder.setSalesId(salesId);
        marketOrder.setStatus('P');
        dao.update(marketOrder);
        logger.info("activate market order done");
        return Response.status(200).build();
    }

    public Response getSecuredRequest(String link, String header) {
        Invocation.Builder b = ClientBuilder.newClient().target(link).request();
        b.header(HttpHeaders.AUTHORIZATION, header);
        Response r = b.get();
        return r;
    }


    public <T> Response postSecuredRequest(String link, T t, String authHeader) {
        Invocation.Builder b = ClientBuilder.newClient().target(link).request();
        b.header(HttpHeaders.AUTHORIZATION, authHeader);
        return b.post(Entity.entity(t, "application/json"));
    }


    public <T> Response putSecuredRequest(String link, T t, String authHeader) {
        Invocation.Builder b = ClientBuilder.newClient().target(link).request();
        b.header(HttpHeaders.AUTHORIZATION, authHeader);
        return b.put(Entity.entity(t, "application/json"));
    }


    public int getCompanyIdFromHeader(String header) throws Exception{
        String token = header.substring("Bearer".length()).trim();
        Claims claims = Jwts.parserBuilder().setSigningKey(KeyConstant.PUBLIC_KEY).build().parseClaimsJws(token).getBody();
        return Integer.parseInt(claims.get("comp").toString());
    }

    public int getSubscriberIdFromHeader(String header) throws Exception{
        String token = header.substring("Bearer".length()).trim();
        Claims claims = Jwts.parserBuilder().setSigningKey(KeyConstant.PUBLIC_KEY).build().parseClaimsJws(token).getBody();
        return Integer.parseInt(claims.get("sub").toString());
    }



    public void throwError(int code) {
        throwError(code, null);
    }

    public void throwError(int code, String msg) {
        throw new WebApplicationException(
                Response.status(code).entity(msg).build()
        );
    }

}
