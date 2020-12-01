package q.rest.product.operation;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import q.rest.product.dao.DAO;
import q.rest.product.filter.annotation.SubscriberJwt;
import q.rest.product.helper.Helper;
import q.rest.product.helper.KeyConstant;
import q.rest.product.model.contract.v3.product.PbProduct;
import q.rest.product.model.entity.v3.product.Product;
import q.rest.product.model.entity.v3.product.Spec;
import q.rest.product.model.entity.v4.pblic.PbCompanyProduct;
import q.rest.product.model.search.SearchObject;

import javax.ejb.EJB;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/api/v4/main/")
public class ProductApiV4 {

    @EJB
    private DAO dao;


    @EJB
    private AsyncProductApi async;


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
