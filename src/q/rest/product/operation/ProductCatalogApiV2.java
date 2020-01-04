package q.rest.product.operation;

import q.rest.product.dao.DAO;
import q.rest.product.filter.SecuredCustomer;
import q.rest.product.filter.SecuredUser;
import q.rest.product.helper.AppConstants;
import q.rest.product.helper.Helper;
import q.rest.product.model.catalog.*;
import q.rest.product.model.contract.PublicProduct;
import q.rest.product.model.contract.PublicReview;
import q.rest.product.model.contract.PublicSpec;
import q.rest.product.model.entity.ProductSpec;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.*;
import java.util.ArrayList;
import java.util.List;

@Path("/api/v2/catalog/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductCatalogApiV2 {

    @EJB
    private DAO dao;

    @SecuredCustomer
    @Path("cars/make/{makeId}/vin/{vin}")
    @GET
    public Response searchVin(@PathParam(value = "vin") String vin, @PathParam(value = "makeId") int makeId) {
        try {
            String catalogId = Helper.getCatalogIdFromMakeId(makeId);
            Response r = this.getCatalogSecuredRequest(AppConstants.getCatalogCarsByVin(catalogId, vin));
            if (r.getStatus() != 200) {
                return Response.status(404).build();
            }
            List<CatalogCar> catalogCars = r.readEntity(new GenericType<List<CatalogCar>>() {
            });
            return Response.status(200).entity(catalogCars).build();

        } catch (Exception ex) {
            return Response.status(500).build();
        }
    }

    @SecuredUser
    @Path("groups/make/{makeId}/car/{carId}")
    @GET
    public Response searchGroups(@Context UriInfo info, @PathParam(value = "carId") String carId, @PathParam(value = "makeId") int makeId ){
        try{
            String catalogId = Helper.getCatalogIdFromMakeId(makeId);
            String groupid = info.getQueryParameters().getFirst("groupid");
            String criteria = info.getQueryParameters().getFirst("criteria");
            if(groupid == null || groupid == ""){
                groupid = null;
            }
            Response r = this.getCatalogSecuredRequest(AppConstants.getCatalogGroups(catalogId, carId, groupid, Helper.getEncodedUrl(criteria)));
            if(r.getStatus() != 200){
                return Response.status(404).build();
            }
            List<CatalogGroup> catalogGroups = r.readEntity(new GenericType<List<CatalogGroup>>(){});
            return Response.status(200).entity(catalogGroups).build();

        }catch (Exception e){
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

    @SecuredUser
    @Path("parts/make/{makeId}/car/{carId}/group/{groupId}/criteria/{criteria}")
    @GET
    public Response searchGroups(@PathParam(value = "groupId") String groupId,
                                 @PathParam(value = "carId") String carId,
                                 @PathParam(value = "makeId") int makeId,
                                 @PathParam(value = "criteria") String criteria){
        try{
            String catalogId = Helper.getCatalogIdFromMakeId(makeId);
            Response r = this.getCatalogSecuredRequest(AppConstants.getCatalogParts(catalogId, carId, groupId, Helper.getEncodedUrl(criteria)));
            if(r.getStatus() != 200){
                return Response.status(404).build();
            }
            CatalogPart catalogPart = r.readEntity(CatalogPart.class);
            initProductHolders(catalogPart, makeId);
            return Response.status(200).entity(catalogPart).build();
        }catch (Exception e){
            return Response.status(500).build();
        }
    }


    private void initProductHolders(CatalogPart catalogPart, int makeId){
        for(CatalogPartsGroup cpg : catalogPart.getPartGroups()){
            for(CatalogPartsList parts : cpg.getParts()){
                PublicProduct pp = getPublicProduct(parts.getNumber(), makeId);
                parts.setProduct(pp);
            }
        }
    }


    private PublicProduct getPublicProduct(String partNumber, int makeId){
        String undecor = Helper.undecorate(partNumber);
        String jpql = "select b from PublicProduct b where b.productNumber = :value0 and b.status =:value1";
        List<PublicProduct> products = dao.getJPQLParams(PublicProduct.class, jpql, undecor, 'A');
        if(!products.isEmpty()) {
            PublicProduct pp = products.get(0);
            initPublicProduct(pp);
        }
        return null;
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



    private Response getCatalogSecuredRequest(String link) {
        Invocation.Builder b = ClientBuilder.newClient().target(link).request();
        b.header(HttpHeaders.AUTHORIZATION, AppConstants.PARTS_CATALOG_API_KEY);
        Response r = b.get();
        return r;
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

