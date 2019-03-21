package q.rest.product.operation;

import q.rest.product.dao.DAO;
import q.rest.product.filter.SecuredUser;
import q.rest.product.helper.AppConstants;
import q.rest.product.model.catalog.CatalogCar;
import q.rest.product.model.catalog.CatalogGroup;
import q.rest.product.model.catalog.CatalogPart;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.*;
import java.util.List;

@Path("/internal/api/v2/catalog/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductCatalogInternalApiV2 {

    @EJB
    private DAO dao;


    //@SecuredUser
    @Path("cars/make/{makeId}/vin/{vin}")
    @GET
    public Response searchVin(@PathParam(value = "vin") String vin, @PathParam(value = "makeId") int makeId){
        try{
            String catalogId = this.getCatalogIdFromMakeId(makeId);
            Response r = this.getCatalogSecuredRequest(AppConstants.getCatalogCarsByVin(catalogId, vin));
            if(r.getStatus() != 200){
                return Response.status(404).build();
            }
            List<CatalogCar> catalogCars = r.readEntity(new GenericType<List<CatalogCar>>(){});
            return Response.status(200).entity(catalogCars).build();

        }catch (Exception ex){
            return Response.status(500).build();
        }
    }

  //  @SecuredUser
    @Path("groups/make/{makeId}/car/{carId}")
    @GET
    public Response searchGroups(@Context UriInfo info, @PathParam(value = "carId") String carId, @PathParam(value = "makeId") int makeId ){
        try{
            String catalogId = this.getCatalogIdFromMakeId(makeId);
            String groupid = info.getQueryParameters().getFirst("groupid");
            if(groupid == null || groupid == ""){
                groupid = null;
            }
            Response r = this.getCatalogSecuredRequest(AppConstants.getCatalogGroups(catalogId, carId, groupid));
            if(r.getStatus() != 200){
                return Response.status(404).build();
            }
            List<CatalogGroup> catalogGroups = r.readEntity(new GenericType<List<CatalogGroup>>(){});
            return Response.status(200).entity(catalogGroups).build();

        }catch (Exception e){
            return Response.status(500).build();
        }
    }

//    @SecuredUser
    @Path("parts/make/{makeId}/car/{carId}/group/{groupId}")
    @GET
    public Response searchGroups(@PathParam(value = "groupId") String groupId, @PathParam(value = "carId") String carId, @PathParam(value = "makeId") int makeId ){
        try{
            String catalogId = this.getCatalogIdFromMakeId(makeId);
            Response r = this.getCatalogSecuredRequest(AppConstants.getCatalogParts(catalogId, carId, groupId));
            if(r.getStatus() != 200){
                return Response.status(404).build();
            }
            CatalogPart catalogPart = r.readEntity(CatalogPart.class);
            return Response.status(200).entity(catalogPart).build();
        }catch (Exception e){
            return Response.status(500).build();
        }
    }

    public String getCatalogIdFromMakeId(int makeId){
        switch (makeId){
            case 1:
            case 6:
            case 7:
                return "ford";
            case 2:
                return "toyota";
            case 3:
                return "mazda";
            case 4:
                return "hyundai";
            case 5:
                return "audi";
            case 8:
                return "kia";
            case 9:
                return "nissan";
            case 11:
            case 12:
            case 13:
                return "chevrolet";
            case 14:
                return "honda";
            case 15:
                return "lexus";
            case 17:
                return "mercedes";
            case 18:
                return "bmw";
            case 19:
                return "porsche";
            case 21:
                return "renault";
            case 22:
                return "jeep";
            case 24:
                return "infiniti";
            case 25:
                return "chrysler";
            case 26:
                return "mitsubishi";
            case 28:
                return "suzuki";
            case 29:
                return "dodge";
            case 30:
                return "vw";

        }

        return "hyundai";
    }



    private Response getCatalogSecuredRequest(String link) {
        Invocation.Builder b = ClientBuilder.newClient().target(link).request();
        b.header(HttpHeaders.AUTHORIZATION, AppConstants.PARTS_CATALOG_API_KEY);
        Response r = b.get();
        return r;
    }

}
