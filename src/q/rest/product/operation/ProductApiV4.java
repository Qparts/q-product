package q.rest.product.operation;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import q.rest.product.dao.DAO;
import q.rest.product.filter.annotation.SubscriberJwt;
import q.rest.product.filter.annotation.UserSubscriberJwt;
import q.rest.product.helper.Helper;
import q.rest.product.helper.KeyConstant;
import q.rest.product.model.contract.v3.product.PbProduct;
import q.rest.product.model.entity.v3.product.Product;
import q.rest.product.model.entity.v3.product.Spec;
import q.rest.product.model.entity.v3.stock.CompanyOfferUploadRequest;
import q.rest.product.model.entity.v3.stock.CompanyProduct;
import q.rest.product.model.entity.v4.pblic.PbCompanyProduct;
import q.rest.product.model.entity.v4.pblic.PbSpecialOffer;
import q.rest.product.model.search.SearchObject;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.*;

@Path("/api/v4/main/")
public class ProductApiV4 {

    @EJB
    private DAO dao;


    @EJB
    private AsyncProductApi async;

    @SubscriberJwt
    @GET
    @Path("special-offers/live")
    public Response getLiveCompanySpecialOfferUpload() {
        String sql = "select b from PbSpecialOffer b where :value0 between b.startDate and b.endDate and b.status = :value1 order by b.startDate";
        List<PbSpecialOffer> list = dao.getJPQLParams(PbSpecialOffer.class, sql, new Date(), 'C');
        return Response.status(200).entity(list).build();
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
        return Response.status(200).entity(pbProducts).build();
    }


    //for lazy loading
    @SubscriberJwt
    @POST
    @Path("search-company-products")
    public Response searchCompanyProducts(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, SearchObject searchObject){
        if(searchObject.getQuery() == null || searchObject.getQuery().length() == 0){
            return Response.status(200).entity(new HashMap<>()).build();
        }
        var size = searchCompanyProductSize(searchObject);
        var companyProducts = searchCompanyProducts(searchObject);
        async.saveSearch2(header, searchObject, size > 0);
        Map<String,Object> map = new HashMap<>();
        map.put("searchSize", size);
        map.put("companyProducts", companyProducts);
        return Response.status(200).entity(map).build();
    }

    //for lazy
    private List<PbCompanyProduct> searchCompanyProducts(SearchObject searchObject) {
        try {
            String undecorated = "%" + Helper.undecorate(searchObject.getQuery()) + "%";
            String sql = "select b from PbCompanyProduct b where " +
                    "(b.partNumber like :value0 or b.alternativeNumber like :value0) and (b.id in (" +
                    " select c.companyProductId from PbCompanyStock c where c.offerOnly =:value1 " + searchObject.getLocationFiltersSql("c") + ")" +
                    " or b.id in (select d.companyProductId from PbCompanyStock d where d.offerOnly = :value2 " + searchObject.getLocationFiltersSql("d") +
                    " and b.id in (" +
                    " select e.companyProductId from PbCompanyStockOffer e where now() between e.offerStartDate and e.offerEndDate" +
                    ")))";
            return dao.getJPQLParams(PbCompanyProduct.class, sql, undecorated, false, true);
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    //for lazy (size only)
    private int searchCompanyProductSize(SearchObject searchObject){
        try {
            String undecorated = "%" + Helper.undecorate(searchObject.getQuery()) + "%";
            String sql = "select count(*) from PbCompanyProduct b where " +
                    "(b.partNumber like :value0 or b.alternativeNumber like :value0) and (b.id in (" +
                    " select c.companyProductId from PbCompanyStock c where c.offerOnly =:value1" + searchObject.getLocationFiltersSql("c") +  " )"+
                    " or b.id in (select d.companyProductId from PbCompanyStock d where d.offerOnly = :value2 " + searchObject.getLocationFiltersSql("d") +
                    " and b.id in (" +
                    " select e.companyProductId from PbCompanyStockOffer e where now() between e.offerStartDate and e.offerEndDate" +
                    ")))";
            return dao.findJPQLParams(Number.class, sql, undecorated, false, true).intValue();
        } catch (Exception ex) {
            return 0;
        }
    }
}
