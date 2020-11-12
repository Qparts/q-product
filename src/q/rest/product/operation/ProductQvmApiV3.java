package q.rest.product.operation;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import q.rest.product.dao.DAO;
import q.rest.product.filter.annotation.SubscriberJwt;
import q.rest.product.filter.annotation.UserJwt;
import q.rest.product.filter.annotation.UserSubscriberJwt;
import q.rest.product.helper.AppConstants;
import q.rest.product.helper.Helper;
import q.rest.product.helper.KeyConstant;
import q.rest.product.model.contract.*;
import q.rest.product.model.contract.v3.PullStockRequest;
import q.rest.product.model.contract.v3.SummaryReport;
import q.rest.product.model.contract.v3.UploadHolder;
import q.rest.product.model.contract.v3.UploadsSummary;
import q.rest.product.model.contract.v3.product.PbProduct;
import q.rest.product.model.entity.VinSearch;
import q.rest.product.model.entity.v3.product.Product;
import q.rest.product.model.entity.v3.product.Spec;
import q.rest.product.model.entity.v3.stock.*;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
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


    @UserJwt
    @GET
    @Path("vin-search-activity/from/{from}/to/{to}")
    public Response getQuotationsItemsDailyReport(@PathParam(value = "from") long fromLong, @PathParam(value = "to") long toLong) {
        try {
            Helper h = new Helper();
            List<Date> dates = h.getAllDatesBetween(new Date(fromLong), new Date(toLong));
            List<Map> kgs = new ArrayList<>();
            for (Date date : dates) {
                String sql = "select count(*) from VinSearch b where cast(b.created as date) = cast(:value0 as date)";
                Number n = dao.findJPQLParams(Number.class, sql, date);
                Map<String, Object> map = new HashMap<>();
                map.put("count", n.intValue());
                map.put("date", date.getTime());
                kgs.add(map);
            }
            return Response.status(200).entity(kgs).build();
        } catch (Exception ex) {
            return Response.status(500).build();
        }
    }



    @UserJwt
    @PUT
    @Path("update-stock")
    public Response updateStock(UploadHolder holder) {
        updateStockAsync(holder);
        return Response.status(200).build();
    }

    @UserJwt
    @GET
    @Path("last-pulls")
    public Response getCompaniesLastPulls() {
        String sql = "select distinct on (company_id) * from prd_data_pull_history order by company_id, created desc";
        List<DataPullHistory> pullHistories = dao.getNative(DataPullHistory.class, sql);
        return Response.ok().entity(pullHistories).build();
    }


    //new not tested
    @UserJwt
    @PUT
    @Path("update-special-offer-stock")
    public Response updateSpecialOfferStock(UploadHolder uploadHolder) {
        try {
            updateSpecialOfferStockAsync(uploadHolder);
            return Response.status(200).build();
        } catch (Exception ex) {
            return Response.status(500).build();
        }
    }


    //new
    @UserSubscriberJwt
    @Path("upload-special-offer-requests/{companyId}")
    @GET
    public Response getUploadSpecialOfferRequests(@PathParam(value = "companyId") int companyId) {
        try {
            String sql = "select b from CompanyOfferUploadRequest b where b.companyId = :value0 order by b.created desc";
            List<CompanyOfferUploadRequest> list = dao.getJPQLParams(CompanyOfferUploadRequest.class, sql, companyId);
            return Response.status(200).entity(list).build();
        } catch (Exception ex) {
            return Response.status(500).build();
        }
    }

    //new
    @UserJwt
    @Path("upload-request")
    @PUT
    public Response updateRequestUpload(CompanyUploadRequest uploadRequest) {
        try {
            uploadRequest.setCompleted(new Date());
            dao.update(uploadRequest);
            return Response.status(200).build();
        } catch (Exception ee) {
            return Response.status(500).build();
        }
    }


    //new
    @UserJwt
    @GET
    @Path("company-uploads/pending")
    public Response getPendingVendorUploads() {
        try {
            String sql = "select b from CompanyUploadRequest b where b.status = :value0  order by b.created desc";
            List<CompanyUploadRequest> uploads = dao.getJPQLParams(CompanyUploadRequest.class, sql, 'R');
            return Response.ok().entity(uploads).build();
        } catch (Exception ex) {
            return Response.status(500).build();
        }
    }


    //new
    @UserJwt
    @GET
    @Path("company-uploads")
    public Response getAllCompanyUploads() {
        try {
            String sql = "select b from CompanyUploadRequest b order by b.created desc";
            List<CompanyUploadRequest> uploads = dao.getJPQLParams(CompanyUploadRequest.class, sql);
            return Response.ok().entity(uploads).build();
        } catch (Exception ex) {
            return Response.status(500).build();
        }
    }

    //new
    @UserJwt
    @GET
    @Path("company-uploads/special-offer")
    public Response getVendorSpecialOfferUploads() {
        try {
            List<CompanyOfferUploadRequest> uploads = dao.getOrderByOriented(CompanyOfferUploadRequest.class, "created", "desc");
            return Response.status(200).entity(uploads).build();
        } catch (Exception ex) {
            return Response.status(500).build();
        }
    }

    //new
    @SubscriberJwt
    @GET
    @Path("company-uploads/special-offers/live")
    public Response getLiveCompanySpecialOfferUpload() {
        String sql = "select b from CompanyOfferUploadRequest b where :value0 between b.startDate and b.endDate and b.status = :value1 order by b.startDate";
        List<CompanyOfferUploadRequest> list = dao.getJPQLParams(CompanyOfferUploadRequest.class, sql, new Date(), 'C');
        return Response.status(200).entity(list).build();
    }

    //new
    //this is bullshit, its loading all, it shouldn't
    @UserSubscriberJwt
    @GET
    @Path("company-uploads/special-offer/{soId}")
    public Response getVendorSpecialOffer(@PathParam(value = "soId") int id) {
        String sql = "select b from CompanyOfferUploadRequest b where :value0 between b.startDate and b.endDate and b.status = :value1 and b.id = :value2";
        CompanyOfferUploadRequest so = dao.findJPQLParams(CompanyOfferUploadRequest.class, sql, new Date(), 'C', id);
        String sql2 = "select b from CompanyProduct b where b.id in (select c.companyProductId from CompanyStockOffer c where c.offerRequestId = :value0)";
        List<CompanyProduct> stock = dao.getJPQLParams(CompanyProduct.class, sql2, id);
        so.setProducts(stock);
        return Response.status(200).entity(so).build();
    }



    //for lazy
    private List<CompanyProduct> searchCompanyProducts(String query, int offset, int max) {
        try {
            String undecorated = "%" + Helper.undecorate(query) + "%";
            String sql = "select b from CompanyProduct b where " +
                    "(b.partNumber like :value0 or b.alternativeNumber like :value0) and (b.id in (" +
                    " select c.companyProductId from CompanyStock c where c.offerOnly =:value1)" +
                    " or b.id in (select d.companyProductId from CompanyStock d where d.offerOnly = :value2 " +
                    " and b.id in (" +
                    " select e.companyProductId from CompanyStockOffer e where now() between e.offerStartDate and e.offerEndDate" +
                    ")))";
            return dao.getJPQLParamsOffsetMax(CompanyProduct.class, sql, offset, max, undecorated, false, true);
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }


    @UserSubscriberJwt
    @GET
    @Path("company-uploads/special-offer/{soId}/products/offset/{offset}/max/{max}")
    public Response getVendorSepcialOffer(@PathParam(value = "soId") int id, @PathParam(value = "offset") int offset, @PathParam(value = "max") int max){
        String sql2 = "select b from CompanyProduct b " +
                " where b.id in (" +
                " select c.companyProductId from CompanyStockOffer c " +
                " where c.offerRequestId = :value0)" +
                " order by b.partNumber";
        List<CompanyProduct> so = dao.getJPQLParamsOffsetMax(CompanyProduct.class, sql2, offset, max, id);
        return Response.status(200).entity(so).build();
    }


    @UserSubscriberJwt
    @GET
    @Path("company-uploads/special-offer/{soId}/products/offset/{offset}/max/{max}/search/{search}")
    public Response getCompanySpecialOffer(@PathParam(value = "soId") int id, @PathParam(value = "offset") int offset, @PathParam(value = "max") int max, @PathParam(value = "search") String search){
        search = "%" + Helper.undecorate(search) + "%";
        String sql = "select count(*) from CompanyProduct b " +
                " where b.id in (" +
                " select c.companyProductId from CompanyStockOffer c " +
                " where c.offerRequestId = :value0)" +
                " and b.partNumber like :value1";
        Number count = dao.findJPQLParams(Number.class, sql, id, search);
        String sql2 = "select b from CompanyProduct b " +
                " where b.id in (" +
                " select c.companyProductId from CompanyStockOffer c " +
                " where c.offerRequestId = :value0)" +
                " and b.partNumber like :value1" +
                " order by b.partNumber";
        List<CompanyProduct> so = dao.getJPQLParamsOffsetMax(CompanyProduct.class, sql2, offset, max, id, search);
        Map<String,Object> map = new HashMap<>();
        map.put("products", so);
        map.put("count", count);
        return Response.status(200).entity(map).build();
    }

    @SubscriberJwt
    @Path("special-offer/{offerId}")
    @DELETE
    public Response inactivateSpecialOffer(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, @PathParam(value = "offerId") int id){
        try {
            int companyId = this.getCompanyIdFromHeader(header);
            CompanyOfferUploadRequest offer = dao.find(CompanyOfferUploadRequest.class, id);
            if(offer.getCompanyId() != companyId) throwError(401);
            Helper h = new Helper();
            Date expireDate = Helper.addMinutes(new Date(), -5);
            String dateString = h.getDateFormat(expireDate);
            String sql = "update prd_company_stock_offer set offer_end_date = '" + dateString + "' where offer_request_id = " + offer.getId();
            dao.updateNative(sql);
            offer.setEndDate(expireDate);
            dao.update(offer);
            return Response.status(200).build();
        }catch (Exception e){
            return Response.status(500).build();
        }
    }



    //new
    @UserJwt
    @Path("upload-special-offer-request")
    @PUT
    public Response updateSpecialOfferRequestUpload(CompanyOfferUploadRequest uploadRequest) {
        try {
            uploadRequest.setCompleted(new Date());
            dao.update(uploadRequest);
            return Response.status(201).build();
        } catch (Exception ee) {
            return Response.status(500).build();
        }
    }


    @UserJwt
    @GET
    @Path("summary-report/company/{id}")
    public Response getCompanySummaryRepoort(@PathParam(value = "id") int id) {
        String sql = "select count(*) from VinSearch b where b.companyId = :value0" ;
        int totalVinSearches = dao.findJPQLParams(Number.class, sql, id).intValue();
        sql = "select coalesce(sum(stk.quantity * pcp.retailPrice),0) from CompanyStock stk left join CompanyProduct pcp on stk.companyProductId = pcp.id where pcp.companyId = :value0";
        double stockValue = dao.findJPQLParams(Number.class, sql, id).doubleValue();
        sql = "select coalesce(sum(b.quantity * b.offerPrice),0) from CompanyStockOffer b where now() between b.offerStartDate and b.offerEndDate " +
                "and b.companyProductId in (select c.id from CompanyProduct c where c.companyId = :value0)";
        double offersValue = dao.findJPQLParams(Number.class, sql, id).doubleValue();
        sql = "select b from VinSearch b where b.companyId = :value0 order by b.created desc";
        List<VinSearch> vinSearches = dao.getJPQLParamsMax(VinSearch.class, sql, 50, id);
        SummaryReport report = new SummaryReport();
        report.setOffersValue(offersValue);
        report.setStockValue(stockValue);
        report.setTopVins(vinSearches);
        report.setTotalVinSearches(totalVinSearches);
        return Response.ok().entity(report).build();
    }


    @UserJwt
    @GET
    @Path("uploads-summary/company/{id}")
    public Response getUploadsSummary(@PathParam(value = "id") int id) {
        String sql = "select b from CompanyUploadRequest b where b.companyId = :value0 order by created desc";
        List<CompanyUploadRequest> stockRequests = dao.getJPQLParams(CompanyUploadRequest.class, sql , id);
        sql = "select b from CompanyOfferUploadRequest b where b.companyId = :value0 order by created desc";
        List<CompanyOfferUploadRequest> offerRequests = dao.getJPQLParams(CompanyOfferUploadRequest.class, sql, id);
        UploadsSummary us = new UploadsSummary();
        us.setOfferRequests(offerRequests);
        us.setStockRequests(stockRequests);
        return Response.ok().entity(us).build();
    }


    @UserJwt
    @GET
    @Path("summary-report")
    public Response getHomeSummary() {
        String sql = "select count(*) from VinSearch b where cast(b.created as date) = cast(now() as date)";
        int vinSearchesToday = dao.findJPQLParams(Number.class, sql).intValue();
        sql = "select coalesce(sum(stk.quantity * pcp.retailPrice),0) from CompanyStock stk left join CompanyProduct pcp on stk.companyProductId = pcp.id";
        double stockValue = dao.findJPQLParams(Number.class, sql).doubleValue();
        sql = "select coalesce(sum(b.quantity * b.offerPrice),0) from CompanyStockOffer b where now() between b.offerStartDate and b.offerEndDate";
        double offersValue = dao.findJPQLParams(Number.class, sql).doubleValue();
        sql = "select b from VinSearch b order by b.created desc";
        List<VinSearch> vinSearches = dao.getJPQLParamsMax(VinSearch.class, sql, 50);
        SummaryReport report = new SummaryReport();
        report.setVinSearchesToday(vinSearchesToday);
        report.setStockValue(stockValue);
        report.setOffersValue(offersValue);
        report.setTopVins(vinSearches);
        return Response.ok().entity(report).build();
    }

    @UserSubscriberJwt
    @Path("special-offer-upload-request")
    @POST
    public Response requestUploadSpecialOffer(CompanyOfferUploadRequest uploadRequest) {
        Date date = Helper.addMinutes(new Date(), 5 * -1);
        String jpql = "select b from CompanyOfferUploadRequest b where b.created > :value0 and b.companyId = :value1 and b.uploadSource = :value2";
        List<CompanyOfferUploadRequest> check = dao.getJPQLParams(CompanyOfferUploadRequest.class, jpql, date, uploadRequest.getCompanyId(), uploadRequest.getUploadSource());
        if (!check.isEmpty()) throwError(409);
        uploadRequest.setCreated(new Date());
        dao.persist(uploadRequest);
        return Response.status(200).entity(uploadRequest).build();
    }

    private void validateDataPull(int companyId){
        Date date = Helper.addDays(new Date(), -1);
        String sql = "select b from DataPullHistory b where b.companyId = :value0 and b.created > :value1";
        List<DataPullHistory> dph = dao.getJPQLParams(DataPullHistory.class, sql , companyId, date);
        if(!dph.isEmpty()){
            throwError(409, "data can be pulled only once in 24 hours");
        }
    }

    //new
    @UserSubscriberJwt
    @Path("upload-request")
    @POST
    public Response requestUpload(CompanyUploadRequest uploadRequest) {
        Date date = Helper.addMinutes(new Date(), 5 * -1);
        String jpql = "select b from CompanyUploadRequest b where b.branchId = :value0 and b.created > :value1 and b.companyId = :value2 and b.uploadSource = :value3";
        List<CompanyUploadRequest> check = dao.getJPQLParams(CompanyUploadRequest.class, jpql, uploadRequest.getBranchId(), date, uploadRequest.getCompanyId(), uploadRequest.getUploadSource());
        if (!check.isEmpty()) throwError(409);
        dao.persist(uploadRequest);
        return Response.status(200).entity(uploadRequest).build();
    }

    @Asynchronous
    private void updateSpecialOfferStockAsync(UploadHolder holder) {
        try {
            CompanyOfferUploadRequest req = dao.find(CompanyOfferUploadRequest.class, holder.getOfferId());
            for (var offerVar : holder.getOfferVars()) {
                offerVar.setPartNumber(Helper.undecorate(offerVar.getPartNumber()));
                offerVar.setAlternativeNumber(Helper.undecorate(offerVar.getAlternativeNumber()));
                String sql = "select b from CompanyProduct b where b.partNumber = :value0 and b.companyId =:value1 and b.brandName = :value2";
                CompanyProduct cp = dao.findJPQLParams(CompanyProduct.class, sql, offerVar.getPartNumber(), holder.getCompanyId(), offerVar.getBrand());
                if (cp != null) {
                    cp.updateAfterUploadOffer(offerVar, holder, req);
                    dao.update(cp);
                } else {
                    cp = new CompanyProduct(offerVar, holder, req);
                    dao.persist(cp);
                }
            }//end for loop
            deletePreviousOffers(holder);
        } catch (Exception ex) {
            System.out.println("an error occured");
        }
    }

    //new
    @Asynchronous
    private void updateStockAsync(UploadHolder holder) {
        try {
            for (var stockVar : holder.getStockVars()) {
                stockVar.setPartNumber(Helper.undecorate(stockVar.getPartNumber()));
                stockVar.setAlternativeNumber(Helper.undecorate(stockVar.getAlternativeNumber()));
                String sql = "select b from CompanyProduct b where b.partNumber = :value0 and b.companyId =:value1 and b.brandName = :value2";
                CompanyProduct cp = dao.findJPQLParams(CompanyProduct.class, sql, stockVar.getPartNumber(), holder.getCompanyId(), stockVar.getBrand());
                if (cp != null) {
                    cp.updateAfterUploadStock(stockVar, holder);
                    dao.update(cp);
                } else {
                    cp = new CompanyProduct(stockVar, holder);
                    dao.persist(cp);
                }
            }//end for loop
            deletePreviousStock(holder);
        } catch (Exception ex) {
            System.out.println("an error occured");
        }
    }

    private void deletePreviousStock(UploadHolder holder) {
        //delete anything before new date in the branch
        if (holder.isOverridePrevious()) {
            Helper h = new Helper();
            String sql = "delete from prd_company_stock where branch_id = " + holder.getBranchId() + " and created < '" + h.getDateFormat(holder.getDate()) + "'";
            dao.updateNative(sql);
        }
    }


    private void deletePreviousOffers(UploadHolder holder) {
        //delete anything before new date in the branch
        if (holder.isOverridePrevious()) {
            Helper h = new Helper();
            String sql = "delete from prd_company_stock_offer " +
                    "where company_product_id in (select b.id from prd_company_product b where b.company_id = " + holder.getCompanyId() + ")" +
                    " and created < '" + h.getDateFormat(holder.getDate()) + "'";
            dao.updateNative(sql);
        }
    }


    @UserSubscriberJwt
    @Path("upload-requests/{companyId}")
    @GET
    public Response getUploadRequests(@PathParam(value = "companyId") int companyId) {
        String sql = "select b from CompanyUploadRequest b where b.companyId = :value0 order by b.created desc";
        List<CompanyUploadRequest> list = dao.getJPQLParams(CompanyUploadRequest.class, sql, companyId);
        return Response.status(200).entity(list).build();
    }


    //for lazy loading
    @UserSubscriberJwt
    @POST
    @Path("search-company-products-lazy/size")
    public Response searchCompanyProductSize(@HeaderParam(HttpHeaders.AUTHORIZATION) String header,Map<String, Object> sr){
        String query = (String) sr.get("query");
        if(query.length() == 0){
            return Response.status(404).build();
        }
        int size = searchCompanyProductSize(query);
        async.saveSearch(header, sr, size > 0);
        Map<String,Integer> map = new HashMap<>();
        map.put("search-size", size);
        return Response.status(200).entity(map).build();
    }


    @SubscriberJwt
    @POST
    @Path("search-company-products-lazy")
    public Response searchCompanyProductLazy(Map<String, Object> map){
        String query = (String) map.get("query");
        int offset = (int) map.get("offset");
        int max = (int) map.get("max");
        List<CompanyProduct> so = this.searchCompanyProducts(query, offset, max);
        return Response.status(200).entity(so).build();
    }

    @SubscriberJwt
    @POST
    @Path("search-company-products-lazy/filtered")
    public Response searchCompanyProductLazyFiltered(Map<String, Object> map){
        String query = (String) map.get("query");
        int offset = (int) map.get("offset");
        int max = (int) map.get("max");
        String filter = (String) map.get("filter");
        String undecorated = "%" + Helper.undecorate(query) + "%";
        String filterUndecorated = "%" + Helper.undecorate(filter) + "%";
        String sql = "select count(*) from CompanyProduct z where z.id in (select b.id from CompanyProduct b where " +
                "(b.partNumber like :value0 or b.alternativeNumber like :value0) and (b.id in (" +
                " select c.companyProductId from CompanyStock c where c.offerOnly =:value1)" +
                " or b.id in (select d.companyProductId from CompanyStock d where d.offerOnly = :value2 " +
                " and b.id in (" +
                " select e.companyProductId from CompanyStockOffer e where now() between e.offerStartDate and e.offerEndDate" +
                ")))) and z.partNumber like :value3";
        int size = dao.findJPQLParams(Number.class, sql, undecorated, false, true, filterUndecorated).intValue();
        sql = "select z from CompanyProduct z where z.id in (select b.id from CompanyProduct b where " +
                "(b.partNumber like :value0 or b.alternativeNumber like :value0) and (b.id in (" +
                " select c.companyProductId from CompanyStock c where c.offerOnly =:value1)" +
                " or b.id in (select d.companyProductId from CompanyStock d where d.offerOnly = :value2 " +
                " and b.id in (" +
                " select e.companyProductId from CompanyStockOffer e where now() between e.offerStartDate and e.offerEndDate" +
                ")))) and z.partNumber like :value3";
        List<CompanyProduct> so =  dao.getJPQLParamsOffsetMax(CompanyProduct.class, sql, offset, max, undecorated, false, true, filterUndecorated);
        Map<String,Object> mp = new HashMap<>();
        mp.put("products", so);
        mp.put("count", size);
        return Response.status(200).entity(mp).build();
    }


    //new
    @UserSubscriberJwt
    @POST
    @Path("search-company-products")
    public Response searchCompanyProduct(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, Map<String, Object> sr) {
        final String query = (String) sr.get("query");
        if(query.length() == 0){
            return Response.status(404).build();
        }
        List<CompanyProduct> companyProducts = searchCompanyProducts(query);
        async.saveSearch(header, sr, !companyProducts.isEmpty());
        return Response.ok().entity(companyProducts).build();
    }

    //for lazy (size only)
    private int searchCompanyProductSize(String query){
        try {
            String undecorated = "%" + Helper.undecorate(query) + "%";
            String sql = "select count(*) from CompanyProduct b where " +
                    "(b.partNumber like :value0 or b.alternativeNumber like :value0) and (b.id in (" +
                    " select c.companyProductId from CompanyStock c where c.offerOnly =:value1)" +
                    " or b.id in (select d.companyProductId from CompanyStock d where d.offerOnly = :value2 " +
                    " and b.id in (" +
                    " select e.companyProductId from CompanyStockOffer e where now() between e.offerStartDate and e.offerEndDate" +
                    ")))";
            return dao.findJPQLParams(Number.class, sql, undecorated, false, true).intValue();
        } catch (Exception ex) {
            return 0;
        }
    }

    //for eager
    private List<CompanyProduct> searchCompanyProducts(String query) {
        try {
            String undecorated = "%" + Helper.undecorate(query) + "%";
            String sql = "select b from CompanyProduct b where " +
                    "(b.partNumber like :value0 or b.alternativeNumber like :value0) and (b.id in (" +
                    " select c.companyProductId from CompanyStock c where c.offerOnly =:value1)" +
                    " or b.id in (select d.companyProductId from CompanyStock d where d.offerOnly = :value2 " +
                    " and b.id in (" +
                    " select e.companyProductId from CompanyStockOffer e where now() between e.offerStartDate and e.offerEndDate" +
                    ")))";
            return dao.getJPQLParams(CompanyProduct.class, sql, undecorated, false, true);
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    @SubscriberJwt
    @GET
    @Path("sample-products/company/{companyId}")
    public Response getSampleProducts(@PathParam(value = "companyId") int companyId){
        String sql = "select b from CompanyProduct b where b.companyId = :value0" +
                " and b.id in (" +
                " select c.companyProductId from CompanyStock c where c.offerOnly =:value1)";
        List<CompanyProduct> products = dao.getJPQLParamsOffsetMax(CompanyProduct.class, sql, 0, 10, companyId, false);
        return Response.ok().entity(products).build();
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
    public Response searchParts(Map<String, String> map) {
        try {
            String query = map.get("query");
            if(query == null || query.length() == 0){
                return Response.status(404).build();
            }
            String partNumber = "%" + Helper.undecorate(query) + "%";
            String jpql = "select b from Product b where b.productNumber like :value0 and b.status =:value1";
            List<Product> products = dao.getJPQLParams(Product.class, jpql, partNumber, 'A');
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
