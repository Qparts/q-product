package q.rest.product.operation;

import q.rest.product.dao.DAO;
import q.rest.product.filter.SecuredCustomer;
import q.rest.product.filter.SecuredUser;
import q.rest.product.filter.ValidApp;
import q.rest.product.helper.AppConstants;
import q.rest.product.helper.Helper;
import q.rest.product.model.catalog.*;
import q.rest.product.model.contract.PublicProduct;
import q.rest.product.model.contract.PublicReview;
import q.rest.product.model.contract.PublicSpec;
import q.rest.product.model.entity.ProductSpec;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.*;
import java.util.ArrayList;
import java.util.List;

@Path("/api/v2/catalog/test/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductCatalogApiV2Test {

    @EJB
    private DAO dao;


    @ValidApp
    @Path("cars")
    @GET
    public Response searchVin(@Context UriInfo info) {
        try {
            String vin = info.getQueryParameters().getFirst("vin");
            Integer makeId = Integer.parseInt(info.getQueryParameters().getFirst("makeid"));
            //String catalogId = Helper.getCatalogIdFromMakeId(makeId);
            List<CatalogCar> ccs = new ArrayList<>();
            CatalogCar cc = new CatalogCar();
            cc.setBrand("Hyundai");
            cc.setCarId("8b3e0f73eeee535f18ddd0bbe94ac733");
            cc.setCatalogId("hyundai");
            cc.setCriteria("18%2aXW8AN2NE3JH035743");
            cc.setParameters(new ArrayList<>());
            cc.setTitle("Sonata 2500");
            ccs.add(cc);
            CatalogCar cc2 = new CatalogCar();
            cc2.setBrand("Hyundai");
            cc2.setCarId("8b3e0f73eeee535f18ddd0bbe94ac732");
            cc2.setCatalogId("hyundai");
            cc2.setCriteria("18%2aXW8AN2NE3JH035743");
            cc2.setParameters(new ArrayList<>());
            cc2.setTitle("Sonata 2400");
            ccs.add(cc2);

            return Response.status(200).entity(ccs).build();

        } catch (Exception ex) {
            return Response.status(500).build();
        }
    }

    @ValidApp
    @Path("groups")
    @GET
    public Response searchGroups(@Context UriInfo info){
        try{
            String catalogId = Helper.getCatalogIdFromMakeId(Integer.parseInt(info.getQueryParameters().getFirst("makeid")));
            String groupid = info.getQueryParameters().getFirst("groupid");
            String criteria = info.getQueryParameters().getFirst("criteria");
            String carId = info.getQueryParameters().getFirst("carid");

            List<CatalogGroup> catalogGroups = new ArrayList<>();
            if(groupid == null || groupid == ""){
                CatalogGroup cg = new CatalogGroup();
                cg.setDescription("");
                cg.setHasParts(false);
                cg.setHasSubgroups(true);
                cg.setId("MfCfmoBFTg");
                cg.setImg("someImage");
                cg.setName("Body");
                cg.setParentId(null);
                catalogGroups.add(cg);

                CatalogGroup cg2 = new CatalogGroup();
                cg2.setDescription("");
                cg2.setHasParts(false);
                cg2.setHasSubgroups(true);
                cg2.setId("MfCfmoBFT1");
                cg2.setImg("someImage");
                cg2.setName("Engine");
                cg2.setParentId(null);
                catalogGroups.add(cg2);

                CatalogGroup cg3 = new CatalogGroup();
                cg3.setDescription("");
                cg3.setHasParts(false);
                cg3.setHasSubgroups(true);
                cg3.setId("MfCfmoBFT2");
                cg3.setImg("someImage");
                cg3.setName("Electrics");
                cg3.setParentId(null);
                catalogGroups.add(cg3);


                CatalogGroup cg4 = new CatalogGroup();
                cg4.setDescription("");
                cg4.setHasParts(false);
                cg4.setHasSubgroups(true);
                cg4.setId("MfCfmoBFT3");
                cg4.setImg("someImage");
                cg4.setName("Front Axle");
                cg4.setParentId(null);
                catalogGroups.add(cg4);


                CatalogGroup cg5 = new CatalogGroup();
                cg5.setDescription("");
                cg5.setHasParts(false);
                cg5.setHasSubgroups(true);
                cg5.setId("MfCfmoBFT4");
                cg5.setImg("someImage");
                cg5.setName("Rear Axle");
                cg5.setParentId(null);
                catalogGroups.add(cg5);
            }

            else{
                CatalogGroup cg = new CatalogGroup();
                cg.setDescription("");
                cg.setHasParts(true);
                cg.setHasSubgroups(false);
                cg.setId("MvCfmoBFTvCfmoEwOTA5MV8wOTA5MTEx");
                cg.setImg("someImage");
                cg.setName("Air and Footwell heater");
                cg.setParentId(groupid);
                catalogGroups.add(cg);

                CatalogGroup cg2 = new CatalogGroup();
                cg2.setDescription("");
                cg2.setHasParts(false);
                cg2.setHasSubgroups(true);
                cg2.setId("MvCfmoBFTvCfmoEwOTA5MV8wOTA5MTEx1");
                cg2.setImg("someImage");
                cg2.setName("Air Conditioning");
                cg2.setParentId(groupid);
                catalogGroups.add(cg);
            }
            return Response.status(200).entity(catalogGroups).build();

        }catch (Exception e){
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

    /*

    @ValidApp
    @Path("parts")
    @GET
    public Response searchParts(@Context UriInfo info){
        try{
            String catalogId = Helper.getCatalogIdFromMakeId(Integer.parseInt(info.getQueryParameters().getFirst("makeid")));
            String groupid = info.getQueryParameters().getFirst("groupid");
            String criteria = info.getQueryParameters().getFirst("criteria");
            String carId = info.getQueryParameters().getFirst("carid");

            CatalogPart cp = new CatalogPart();
            cp.setImg("some parts simage");
            cp.setImgDescription("description");
            CatalogPartsGroup cpg = new CatalogPartsGroup();
            cpg.setDescription("part desc");
            cpg.setName("Air Guide");
            cpg.setNumber("5E1819655A");
            cpg.setParts("");
            cpg.setPositionNumber("");
            return Response.status(200).entity(catalogGroups).build();

        }catch (Exception e){
            e.printStackTrace();
            return Response.status(500).build();
        }
    }
    */

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

