package q.rest.product.operation;

import q.rest.product.dao.DAO;
import q.rest.product.filter.annotation.SubscriberJwt;
import q.rest.product.helper.AppConstants;
import q.rest.product.helper.Helper;
import q.rest.product.model.catalog.*;
import q.rest.product.model.contract.PublicProduct;
import q.rest.product.model.entity.VinNotFound;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Path("/api/v3/catalog/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductCatalogApiV3 {

    @EJB
    private DAO dao;


    @EJB
    private AsyncProductApi async;


    @SubscriberJwt
    @Path("catalogs")
    @GET
    public Response getCatalogs(){
        Response r = this.getCatalogSecuredRequest(AppConstants.GET_CATALOGS);
        if(r.getStatus() == 200){
            List<Catalogs> catalogs = r.readEntity(new GenericType<List<Catalogs>>(){});
            return Response.ok().entity(catalogs).build();
        }
        return Response.status(400).build();
    }



    @SubscriberJwt
    @Path("models")
    @GET
    public Response getModels(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, @Context UriInfo info){
        String catalogId = info.getQueryParameters().getFirst("catalogid");
        Response r = this.getCatalogSecuredRequest(AppConstants.getCatalogModels(catalogId));
        if(r.getStatus() == 200){
            List<CatalogModel> models = r.readEntity(new GenericType<List<CatalogModel>>(){});
            return Response.ok().entity(models).build();
        }
        return Response.status(400).build();
    }

    @SubscriberJwt
    @Path("cars-by-model")
    @GET
    public Response getCarsByModel(@Context UriInfo info){
        String catalogId = info.getQueryParameters().getFirst("catalogid");
        String modelId = info.getQueryParameters().getFirst("modelid");
        Response r = this.getCatalogSecuredRequest(AppConstants.getCatalogCarsByModel(catalogId, modelId));
        if(r.getStatus() == 200){
            return Response.ok().entity(r.readEntity(Object.class)).build();
        }
        Map<String,Object> map = r.readEntity(Map.class);
        map.putIfAbsent("requested", AppConstants.getCatalogCarsByModel(catalogId, modelId));
        return Response.status(r.getStatus()).entity(map).build();
    }

    @SubscriberJwt
    @Path("car-filters-by-model")
    @GET
    public Response getCarFiltersByModel(@Context UriInfo info){
        String catalogId = info.getQueryParameters().getFirst("catalogid");
        String modelId = info.getQueryParameters().getFirst("modelid");
        String params = info.getQueryParameters().getFirst("params");
        Response r = this.getCatalogSecuredRequest(AppConstants.getCatalogCarFiltersByModel(catalogId, modelId, params));
        if(r.getStatus() == 200){
            return Response.ok().entity(r.readEntity(Object.class)).build();
        }
        Map<String,Object> map = r.readEntity(Map.class);
        map.putIfAbsent("requested", AppConstants.getCatalogCarFiltersByModel(catalogId, modelId,params));
        return Response.status(r.getStatus()).entity(map).build();
    }


    @SubscriberJwt
    @Path("cars")
    @GET
    public Response searchVin(@HeaderParam (HttpHeaders.AUTHORIZATION) String header, @Context UriInfo info) {
        try{
            String vin = info.getQueryParameters().getFirst("vin");
            String catalogId = info.getQueryParameters().getFirst("catalogid");
            Response r = this.getCatalogSecuredRequest(AppConstants.getCatalogCarsByVin(catalogId, vin));
            if(r.getStatus() != 200){
                async.saveVinSearch(vin, catalogId, header, false);
                return Response.status(404).build();
            }
            List<CatalogCar> catalogCars = r.readEntity(new GenericType<List<CatalogCar>>(){});
            if(catalogCars.isEmpty()){
                async.saveVinSearch(vin, catalogId, header, false);
            }else{
                async.saveVinSearch(vin, catalogId, header, true);
            }
            return Response.status(200).entity(catalogCars).build();
        }catch (Exception ex){
            ex.printStackTrace();
            return Response.status(500).build();
        }
    }


    @SubscriberJwt
    @Path("groups")
    @GET
    public Response searchGroups(@Context UriInfo info){
        try{
            String catalogId = info.getQueryParameters().getFirst("catalogid");
            String groupId = info.getQueryParameters().getFirst("groupid");
            String criteria = info.getQueryParameters().getFirst("criteria");
            String carId = info.getQueryParameters().getFirst("carid");
            if(groupId == null || groupId == ""){
                groupId = null;
            }
            Response r = this.getCatalogSecuredRequest(AppConstants.getCatalogGroups(catalogId, carId, groupId, Helper.getEncodedUrl(criteria)));
            if(r.getStatus() != 200){
                return Response.status(404).build();
            }
            List<CatalogGroup> catalogGroups = r.readEntity(new GenericType<List<CatalogGroup>>(){});
            catalogGroups.forEach(cg -> {
                cg.setImg(AppConstants.getImageReplacedLink(cg.getImg()));
            });
            return Response.status(200).entity(catalogGroups).build();

        }catch (Exception e){
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

    @SubscriberJwt
    @Path("parts")
    @GET
    public Response searchParts(@Context UriInfo info) {
        try {
            String catalogId = info.getQueryParameters().getFirst("catalogid");
            String groupId = info.getQueryParameters().getFirst("groupid");
            String criteria = info.getQueryParameters().getFirst("criteria");
            String carId = info.getQueryParameters().getFirst("carid");
            Response r = this.getCatalogSecuredRequest(AppConstants.getCatalogParts(catalogId, carId, groupId, Helper.getEncodedUrl(criteria)));
            if(r.getStatus() != 200){
                return Response.status(404).build();
            }
            CatalogPart catalogPart = r.readEntity(CatalogPart.class);
            catalogPart.setImg(AppConstants.getImageReplacedLink(catalogPart.getImg()));
            initProductHolders(catalogPart);
            return Response.status(200).entity(catalogPart).build();
        }catch (Exception ex){
            return Response.status(500).build();
        }
    }


    private void initProductHolders(CatalogPart catalogPart){
        for(CatalogPartsGroup cpg : catalogPart.getPartGroups()){
            for(CatalogPartsList parts : cpg.getParts()){
                parts.setProducts(getProducts(parts.getNumber()));
            }
        }
    }

    private List<PublicProduct> getProducts(String partNumber){
        String jpql = "select b from PublicProduct b where b.productNumber = :value0 and b.status =:value1";
        List<PublicProduct> products = dao.getJPQLParams(PublicProduct.class, jpql, Helper.undecorate(partNumber), 'A');
        if(products != null) {
            products.forEach(this::initPublicProduct);
        }
        return products;
    }

    private void initPublicProduct(PublicProduct product){
        try {
            product.setSalesPrice(getAveragedSalesPrice(product.getId()));
            product.initImageLink();
            product.getBrand().initImageLink();
            product.setVariants(new ArrayList<>());
        }catch(Exception ex){
            product = null;
        }
    }

    private double getAveragedSalesPrice(long productId){
        String sql = "select avg(b.price + (b.price * b.salesPercentage)) from ProductPrice b where b.productId = :value0 and b.status = :value1";
        Number n = dao.findJPQLParams(Number.class, sql , productId, 'A');
        return n.doubleValue();
    }


    private void vinNotFound(String vin, String catalogId){
        try{
            VinNotFound vinNotFound = new VinNotFound();
            vinNotFound.setCatId(catalogId);
            vinNotFound.setVin(vin);
            vinNotFound.setCreated(new Date());
            dao.persist(vinNotFound);
        }catch (Exception ignore){
            System.out.println("an exception occured!!!");
        }
    }


    private Response getCatalogSecuredRequest(String link) {
        Invocation.Builder b = ClientBuilder.newClient().target(link).request();
        b.header(HttpHeaders.AUTHORIZATION, AppConstants.PARTS_CATALOG_API_KEY);
        Response r = b.get();
        return r;
    }

}

