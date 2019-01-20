package q.rest.product.operation;


import q.rest.product.dao.DAO;
import q.rest.product.filter.ValidApp;
import q.rest.product.helper.AppConstants;
import q.rest.product.model.contract.PublicBrand;
import q.rest.product.model.contract.PublicProduct;
import q.rest.product.model.contract.PublicReview;
import q.rest.product.model.contract.PublicSpec;
import q.rest.product.model.entity.Product;
import q.rest.product.model.entity.ProductPrice;
import q.rest.product.model.entity.ProductSpec;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/api/v2/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductApiV2 {


    @EJB
    private DAO dao;

    @ValidApp
    @Path("products/best-sellers")
    @GET
    public Response getBestSellers(){
        try{
            String sql = "select * from prd_product where status = 'A' order by id desc limit 10";
            List<PublicProduct> pbs = dao.getNative(PublicProduct.class, sql);
            for(PublicProduct pb : pbs){
                initPublicProduct(pb);
            }
            return Response.status(200).entity(pbs).build();
        }catch (Exception ex){
            return Response.status(500).build();
        }
    }

    @ValidApp
    @Path("products/offers")
    @GET
    public Response getOffers(){
        try{
            String sql = "select * from prd_product where status = 'A' order by id asc limit 10";
            List<PublicProduct> pbs = dao.getNative(PublicProduct.class, sql);
            for(PublicProduct pb : pbs){
                initPublicProduct(pb);
            }
            return Response.status(200).entity(pbs).build();
        }catch (Exception ex){
            return Response.status(500).build();
        }
    }



    @ValidApp
    @Path("product/{productId}")
    @GET
    public Response getProduct(@PathParam(value = "productId") long productId){
        try{
            String sql = "select b from PublicProduct b where b.id =:value0 and b.status =:value1 and b.id in (" +
                    "select c.productId from ProductPrice c where c.productId =:value0 and c.status =:value1)";
            PublicProduct product = dao.findJPQLParams(PublicProduct.class, sql, productId, 'A');
            if(product == null){
                return Response.status(404).build();
            }
            initPublicProduct(product);
            return Response.status(200).entity(product).build();

        }catch(Exception ex){
            return Response.status(500).build();
        }
    }


    private void initPublicProduct(PublicProduct product){
        product.setSpecs(getPublicSpecs(product.getId()));
        product.setSalesPrice(getAveragedSalesPrice(product.getId()));
        product.setReviews(getPublicReviews(product.getId()));
        product.initImageLink();
        product.getBrand().initImageLink();
    }

    private List<PublicReview> getPublicReviews(long productId){
        String jpql = "select b from PublicReview b where b.productId = :value0 and b.status =:value1 order by b.created desc";
        List<PublicReview> reviews = dao.getJPQLParams(PublicReview.class, jpql, productId, 'A');
        return reviews;
    }

    private double getAveragedSalesPrice(long productId){
        String sql = "select avg(b.price + (b.price * b.salesPercentage)) from ProductPrice b where b.productId = :value0 and b.status = :value1";
        Number n = dao.findJPQLParams(Number.class, sql , productId, 'A');
        return n.doubleValue();
    }

    private List<PublicSpec> getPublicSpecs(long productId){
        List<PublicSpec> publicSpecs = new ArrayList<>();
        String sql = "select b from ProductSpec b where b.productId =:value0 and b.status =:value1 order by b.spec.id";
        List<ProductSpec> productSpecs = dao.getJPQLParams(ProductSpec.class, sql , productId, 'A');
        productSpecs.forEach(ps->{
            publicSpecs.add(ps.getPublicSpec());
        });
        return publicSpecs;
    }


}
