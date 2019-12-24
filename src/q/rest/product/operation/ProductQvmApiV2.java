package q.rest.product.operation;


import q.rest.product.dao.DAO;
import q.rest.product.filter.SecuredVendor;
import q.rest.product.helper.Helper;
import q.rest.product.model.contract.PublicProduct;
import q.rest.product.model.contract.PublicReview;
import q.rest.product.model.contract.PublicSpec;
import q.rest.product.model.entity.ProductSpec;
import q.rest.product.model.qvm.QvmSearchRequest;
import q.rest.product.model.qvm.QvmSearchResult;
import q.rest.product.model.qvm.QvmVendorCredentials;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/qvm/api/v2/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductQvmApiV2 {

    @EJB
    private DAO dao;

    @SecuredVendor
    @POST
    @Path("search")
    public Response search(QvmSearchRequest sr) {
        try {
            List<QvmSearchResult> results = new ArrayList<>();
            for (QvmVendorCredentials cred : sr.getVendorCreds()) {
                String endpoint = cred.getEndpointAddress() + "search/" + sr.getQuery();
                String header = "Bearer " + cred.getSecret();
                Response r = getSecuredRequest(endpoint, header);
                if (r.getStatus() == 200) {
                    List<QvmSearchResult> rs = r.readEntity(new GenericType<List<QvmSearchResult>>() {
                    });
                    for (QvmSearchResult result : rs) {
                        String partNumber = Helper.undecorate(result.getPartNumber());
                        String jpql = "select b from PublicProduct b where b.productNumber = :value0 and b.status =:value1";
                        List<PublicProduct> publicProducts = dao.getJPQLParams(PublicProduct.class, jpql, partNumber, 'A');
                        for(PublicProduct publicProduct : publicProducts){
                            initPublicProduct(publicProduct);
                        }
                        result.setQpartsProducts(publicProducts);
                        result.setVendorId(cred.getVendorId());
                    }
                    results.addAll(rs);
                }
            }
            return Response.status(200).entity(results).build();
        }catch (Exception ex){
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


    private void initPublicProductNoVariant(PublicProduct product){
        product.setSpecs(getPublicSpecs(product.getId()));
        product.setSalesPrice(getAveragedSalesPrice(product.getId()));
        product.setReviews(getPublicReviews(product.getId()));
        product.initImageLink();
        product.getBrand().initImageLink();
        product.setVariants(new ArrayList<>());
    }

    private void initVariants(PublicProduct product){
        //get variants
        String sql = "select distinct b from PublicProduct b where b.status =:value0 and b.id in (" +
                "select c.productId from Variant c where c.variantId =:value1) or b.id in " +
                "(select d.variantId from Variant d where d.productId =:value1)";
        List<PublicProduct> variants = dao.getJPQLParams(PublicProduct.class, sql, 'A', product.getId());
        for(PublicProduct variant : variants){
            initPublicProductNoVariant(variant);
        }
        product.setVariants(variants);

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



    public Response getSecuredRequest(String link, String header) {
        Invocation.Builder b = ClientBuilder.newClient().target(link).request();
        b.header(HttpHeaders.AUTHORIZATION, header);
        Response r = b.get();
        return r;
    }
}
