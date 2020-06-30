package q.rest.product.operation;


import q.rest.product.dao.DAO;
import q.rest.product.filter.annotation.SubscriberJwt;
import q.rest.product.filter.annotation.UserJwt;
import q.rest.product.filter.annotation.UserSubscriberJwt;
import q.rest.product.helper.Helper;
import q.rest.product.model.contract.*;
import q.rest.product.model.contract.v3.PullStockRequest;
import q.rest.product.model.contract.v3.UploadHolder;
import q.rest.product.model.entity.ProductSpec;
import q.rest.product.model.entity.v3.stock.CompanyProduct;
import q.rest.product.model.entity.v3.stock.CompanyOfferUploadRequest;
import q.rest.product.model.entity.v3.stock.CompanyUploadRequest;
import q.rest.product.model.entity.v3.stock.DataPullHistory;

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

    //new
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


    /*
    //old
    @UserJwt
    @GET
    @Path("vendor-uploads/stock")
    public Response getVendorUploads() {
        try {
            List<VendorUploadRequest> uploads = dao.getOrderByOriented(VendorUploadRequest.class, "created", "desc");
            return Response.status(200).entity(uploads).build();
        } catch (Exception ex) {
            return Response.status(500).build();
        }
    }
     */


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

    //old
    @Asynchronous
    private void updateSpecialOfferStockAsync(UploadHolder holder) {
        try {
            CompanyOfferUploadRequest req = dao.find(CompanyOfferUploadRequest.class, holder.getOfferId());
            for (var offerVar : holder.getOfferVars()) {
                offerVar.setPartNumber(Helper.undecorate(offerVar.getPartNumber()));
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
            String sql = "delete from prd_company_stock_offer where company_product_id in (select b.id from prd_company_product where company_id = " + holder.getCompanyId() + ") and created < '" + h.getDateFormat(holder.getDate()) + "'";
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


    //new
    @UserSubscriberJwt
    @POST
    @Path("search-company-products")
    public Response searchCompanyProduct(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, Map<String, Object> sr) {
        final String query = (String) sr.get("query");
        List<CompanyProduct> companyProducts = searchCompanyProducts(query);
        async.saveSearch(header, sr);
        return Response.ok().entity(companyProducts).build();
    }


    private List<CompanyProduct> searchCompanyProducts(String query) {
        try {
            String undecorated = "%" + Helper.undecorate(query) + "%";
            String sql = "select b from CompanyProduct b where (b.partNumber like :value0 or b.alternativeNumber like :value0) and (b.id in (" +
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


    @UserJwt
    @POST
    @Path("pull-stock")
    public Response pullStock(PullStockRequest psr) {
        validateDataPull(psr.getCompanyId());
        String header = "Bearer " + psr.getSecret();
        //get count
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
            List<String> links = Helper.getPullDataLinks(count, psr.getAllStockEndPoint());
            async.callPullData(links, header, psr, dph);
            return Response.status(200).entity(links).build();
        }
        return Response.status(404).build();
    }


    @UserSubscriberJwt
    @POST
    @Path("search-parts")
    public Response searchParts(Map<String, String> map) {
        try {
            String query = map.get("query");
            String partNumber = "%" + Helper.undecorate(query) + "%";
            String jpql = "select b from PublicProduct b where b.productNumber like :value0 and b.status =:value1";
            List<PublicProduct> publicProducts = dao.getJPQLParams(PublicProduct.class, jpql, partNumber, 'A');
            for (PublicProduct publicProduct : publicProducts) {
                initPublicProduct(publicProduct);
            }
            return Response.status(200).entity(publicProducts).build();
        } catch (Exception ex) {
            return Response.status(500).build();
        }
    }


    private void initPublicProduct(PublicProduct product) {
        try {
            initPublicProductNoVariant(product);
            initVariants(product);
        } catch (Exception ex) {
            product = null;
        }
    }


    private void initPublicProductNoVariant(PublicProduct product) {
        product.setSpecs(getPublicSpecs(product.getId()));
        product.setSalesPrice(getAveragedSalesPrice(product.getId()));
        product.setReviews(getPublicReviews(product.getId()));
        product.initImageLink();
        product.getBrand().initImageLink();
        product.setVariants(new ArrayList<>());
    }

    private void initVariants(PublicProduct product) {
        //get variants
        String sql = "select distinct b from PublicProduct b where b.status =:value0 and b.id in (" +
                "select c.productId from Variant c where c.variantId =:value1) or b.id in " +
                "(select d.variantId from Variant d where d.productId =:value1)";
        List<PublicProduct> variants = dao.getJPQLParams(PublicProduct.class, sql, 'A', product.getId());
        for (PublicProduct variant : variants) {
            initPublicProductNoVariant(variant);
        }
        product.setVariants(variants);

    }


    private List<PublicReview> getPublicReviews(long productId) {
        String jpql = "select b from PublicReview b where b.productId = :value0 and b.status =:value1 order by b.created desc";
        List<PublicReview> reviews = dao.getJPQLParams(PublicReview.class, jpql, productId, 'A');
        return reviews;
    }

    private double getAveragedSalesPrice(long productId) {
        String sql = "select avg(b.price + (b.price * b.salesPercentage)) from ProductPrice b where b.productId = :value0 and b.status = :value1";
        Number n = dao.findJPQLParams(Number.class, sql, productId, 'A');
        return n.doubleValue();
    }

    private List<PublicSpec> getPublicSpecs(long productId) {
        List<PublicSpec> publicSpecs = new ArrayList<>();
        String sql = "select b from ProductSpec b where b.productId =:value0 and b.status =:value1 order by b.spec.id";
        List<ProductSpec> productSpecs = dao.getJPQLParams(ProductSpec.class, sql, productId, 'A');
        productSpecs.forEach(ps -> {
            publicSpecs.add(ps.getPublicSpec());
        });
        return publicSpecs;
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




    public void throwError(int code) {
        throwError(code, null);
    }

    public void throwError(int code, String msg) {
        throw new WebApplicationException(
                Response.status(code).entity(msg).build()
        );
    }

}