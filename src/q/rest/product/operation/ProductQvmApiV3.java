package q.rest.product.operation;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
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


    @UserJwt
    @GET
    @Path("last-pulls")
    public Response getCompaniesLastPulls() {
        List<DataPullHistory> pullHistories = daoApi.getLatestPulls();
        return Response.ok().entity(pullHistories).build();
    }


    @SubscriberJwt
    @GET
    @Path("sample-products")
    public Response getSampleProducts(@HeaderParam(HttpHeaders.AUTHORIZATION) String header){
        int companyId = Helper.getCompanyFromJWT(header);
        List<CompanyProduct> products = daoApi.getSampleProducts(companyId);
        return Response.ok().entity(products).build();
    }


    @UserJwt
    @GET
    @Path("vin-search-activity/from/{from}/to/{to}")
    public Response getVinSearchReport(@PathParam(value = "from") long fromLong, @PathParam(value = "to") long toLong) {
        try {
            Helper h = new Helper();
            List<Date> dates = h.getAllDatesBetween(new Date(fromLong), new Date(toLong));
            List<Map<String,Object>> kgs = daoApi.getVinSearchReport(dates);
            return Response.status(200).entity(kgs).build();
        } catch (Exception ex) {
            return Response.status(500).build();
        }
    }

    @UserJwt
    @PUT
    @Path("update-stock")
    public Response updateStock(UploadHolder holder) {
        daoApi.updateStockAsyncOptimized(holder);
        return Response.status(200).build();
    }

    @UserJwt
    @PUT
    @Path("update-q-stock-stock")
    public Response updateQStockStock(QStockUploadHolder holder) {
        daoApi.updateQStockAsyncOptimized(holder);
        return Response.status(200).build();
    }

    @UserJwt
    @PUT
    @Path("update-special-offer-stock")
    public Response updateSpecialOfferStock(UploadHolder uploadHolder) {
        try {
            daoApi.updateSpecialOfferStockAsyncOptimized(uploadHolder);
            return Response.status(200).build();
        } catch (Exception ex) {
            return Response.status(500).build();
        }
    }

    @UserSubscriberJwt
    @Path("upload-special-offer-requests/{companyId}")
    @GET
    public Response getUploadSpecialOfferRequests(@PathParam(value = "companyId") int companyId) {
        try {
            List<CompanyOfferUploadRequest> list = daoApi.getOfferUploadRequest(companyId);
            return Response.status(200).entity(list).build();
        } catch (Exception ex) {
            return Response.status(500).build();
        }
    }

    @UserJwt
    @Path("upload-request")
    @PUT
    public Response updateRequestUpload(CompanyUploadRequest uploadRequest) {
        try {
            daoApi.updateUploadRequest(uploadRequest);
            return Response.status(200).build();
        } catch (Exception ee) {
            return Response.status(500).build();
        }
    }

    @UserJwt
    @GET
    @Path("company-uploads/pending")
    public Response getPendingVendorUploads() {
        try {
            List<CompanyUploadRequest> uploads = daoApi.getPendingStockUploads();
            return Response.ok().entity(uploads).build();
        } catch (Exception ex) {
            return Response.status(500).build();
        }
    }

    @UserJwt
    @GET
    @Path("company-uploads")
    public Response getAllCompanyUploads() {
        try {
            List<CompanyUploadRequest> uploads = daoApi.getAllStockUploads();
            return Response.ok().entity(uploads).build();
        } catch (Exception ex) {
            return Response.status(500).build();
        }
    }

    @UserJwt
    @GET
    @Path("company-uploads/special-offer")
    public Response getAllSpecialOfferUploads() {
        try {
            List<CompanyOfferUploadRequest> uploads = daoApi.getAllOfferUploads();
            return Response.status(200).entity(uploads).build();
        } catch (Exception ex) {
            return Response.status(500).build();
        }
    }


    @SubscriberJwt
    @GET
    @Path("company-uploads/special-offers/live")
    public Response getLiveCompanySpecialOfferUpload() {
        List<CompanyOfferUploadRequest> list = daoApi.getLiveOffers();
        return Response.status(200).entity(list).build();
    }


    @SubscriberJwt
    @Path("special-offer/{offerId}")
    @DELETE
    public Response inactivateSpecialOffer(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, @PathParam(value = "offerId") int id){
        try {
            int companyId = this.getCompanyIdFromHeader(header);

            CompanyOfferUploadRequest offer = daoApi.findOffer(id);
            if(offer.getCompanyId() != companyId) throwError(401);

            daoApi.makeOfferExpired(offer);
            return Response.status(200).build();
        }catch (Exception e){
            return Response.status(500).build();
        }
    }

    @UserJwt
    @Path("upload-special-offer-request")
    @PUT
    public Response updateSpecialOfferRequestUpload(CompanyOfferUploadRequest uploadRequest) {
        try {
            daoApi.makeOfferCompleted(uploadRequest);
            return Response.status(201).build();
        } catch (Exception ee) {
            return Response.status(500).build();
        }
    }

    @UserJwt
    @GET
    @Path("summary-report/company/{id}")
    public Response getCompanySummaryRepoort(@PathParam(value = "id") int companyId) {
        SummaryReport report = daoApi.getCompanyProductSummaryReport(companyId);
        return Response.ok().entity(report).build();
    }


    @UserJwt
    @GET
    @Path("uploads-summary/company/{id}")
    public Response getUploadsSummary(@PathParam(value = "id") int companyId) {
        UploadsSummary us = daoApi.getCompanyUploadsSummary(companyId);
        return Response.ok().entity(us).build();
    }



    @UserJwt
    @GET
    @Path("summary-report")
    public Response getHomeSummary() {
        SummaryReport report = daoApi.getOverallProductSummaryReport();
        return Response.ok().entity(report).build();
    }

    @UserSubscriberJwt
    @Path("special-offer-upload-request")
    @POST
    public Response requestUploadSpecialOffer(CompanyOfferUploadRequest uploadRequest) {
        daoApi.createOfferUploadRequest(uploadRequest);
        return Response.status(200).entity(uploadRequest).build();
    }

    @UserSubscriberJwt
    @Path("upload-request")
    @POST
    public Response requestStockUpload(CompanyUploadRequest uploadRequest) {
        daoApi.createStockUploadRequest(uploadRequest);
        return Response.status(200).entity(uploadRequest).build();
    }

    @UserSubscriberJwt
    @Path("upload-requests/{companyId}")
    @GET
    public Response getUploadRequests(@PathParam(value = "companyId") int companyId) {
        List<CompanyUploadRequest> list = daoApi.getStockUploadRequests(companyId);
        return Response.status(200).entity(list).build();
    }

    @UserSubscriberJwt
    @GET
    @Path("company-uploads/special-offer/{soId}/products/offset/{offset}/max/{max}")
    public Response getSpecialOfferProducts(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, @PathParam(value = "soId") int offerId, @PathParam(value = "offset") int offset, @PathParam(value = "max") int max){
        List<CompanyProduct> so = daoApi.getSpecialOfferProducts(offerId, offset, max);
        async.addToSpecialOfferList(header, offerId, offset, so.size(), max, null);
        return Response.status(200).entity(so).build();
    }

    @UserSubscriberJwt
    @GET
    @Path("company-uploads/special-offer/{soId}/products/offset/{offset}/max/{max}/search/{filter}")
    public Response getCompanySpecialOffer(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, @PathParam(value = "soId") int offerId, @PathParam(value = "offset") int offset, @PathParam(value = "max") int max, @PathParam(value = "filter") String filter){
        Map<String,Object> map = daoApi.getSpecialOfferProductsWithFilter(filter, offerId, offset, max);
        List<CompanyProduct> list = (List<CompanyProduct>) map.get("products");
        async.addToSpecialOfferList(header, offerId, offset, list.size(), max, filter);
        return Response.status(200).entity(map).build();
    }

    @SubscriberJwt
    @POST
    @Path("search-company-products")
    public Response searchCompanyProducts(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, SearchObject searchObject){
        if(searchObject.getQuery() == null || searchObject.getQuery().length() == 0){
            return Response.status(200).entity(new HashMap<>()).build();
        }
        var size = daoApi.searchCompanyProductSize(searchObject);
        var companyProducts = daoApi.searchCompanyProducts(searchObject);
        async.saveSearch2(header, searchObject, size > 0);
        async.addToSearchListOld(header, companyProducts);
        Map<String,Object> map = new HashMap<>();
        map.put("count", size);
        map.put("products", companyProducts);
        return Response.status(200).entity(map).build();
    }

    ////////////////////////////////////////////////////////

    private void validateDataPull(int companyId){
        Date date = Helper.addDays(new Date(), -1);
        String sql = "select b from DataPullHistory b where b.companyId = :value0 and b.created > :value1";
        List<DataPullHistory> dph = dao.getJPQLParams(DataPullHistory.class, sql , companyId, date);
        if(!dph.isEmpty()){
            throwError(409, "data can be pulled only once in 24 hours");
        }
    }

    @GET
    @Path("do-migrate-quotations")
    public Response migrateQuotations(){
        String base = "http://localhost:8081/service-q-quotation/rest/internal/api/v3/migrate-company-quotations/";
        int totalCount = 131797;
//        int totalCount = 103;
        final int N = 500;
        List<Integer> ints = new ArrayList<>();
        int temp = 0;
        while(totalCount > 0){
            ints.add(temp);
            if(totalCount >= N) totalCount -= N;
            else totalCount -= totalCount;
            temp += N;
        }
        List<String> links = new ArrayList<>();
        ints.forEach(g -> links.add(base + "offset/"+ g +"/max/" + N));
        async.migrate(links);
        return Response.status(200).entity(links).build();
    }

    @UserJwt
    @POST
    @Path("pull-stock")
    public Response pullStock(@HeaderParam(HttpHeaders.AUTHORIZATION) String qvmHeader, PullStockRequest psr) {
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
            return Response.status(200).entity(pbProducts).build();
        } catch (Exception ex) {
            return Response.status(500).build();
        }
    }


    @SubscriberJwt
    @GET
    @Path("market-supplies")
    public Response getMarketSupplies(){
        String sql = "select b from MarketProduct b where b.id in " +
                "(select c.productId from ProductSupply c where c.quantity > :value0 and c.status = :value1) " +
                "and b.status = :value2";
        List<MarketProduct> products = dao.getJPQLParams(MarketProduct.class, sql, 0, 'A', 'A');
        return Response.status(200).entity(products).build();
    }

    @SubscriberJwt
    @POST
    @Path("market-order")
    public Response createMarketOrder(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, @Context HttpServletRequest req, MarketOrderRequest marketRequest){
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
        System.out.println(r.getStatus());
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
        int companyId = Helper.getCompanyFromJWT(header );
        int salesId = map.get("salesId");
        int marketOrderId = map.get("marketOrderId");
        String sql = "select b from MarketOrder b where b.companyId =:value0 and b.id = :value1";
        MarketOrder marketOrder = dao.findJPQLParams(MarketOrder.class, sql , companyId, marketOrderId);
        marketOrder.setSalesId(salesId);
        marketOrder.setStatus('P');
        dao.update(marketOrder);
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
