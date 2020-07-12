package q.rest.product.operation;


import q.rest.product.dao.DAO;
import q.rest.product.filter.annotation.InternalApp;
import q.rest.product.filter.annotation.SubscriberJwt;
import q.rest.product.helper.AppConstants;
import q.rest.product.helper.Helper;
import q.rest.product.model.catalog.CatalogCar;
import q.rest.product.model.catalog.CatalogGroup;
import q.rest.product.model.catalog.CatalogPart;
import q.rest.product.model.contract.subscriber.CompanyReduced;
import q.rest.product.model.entity.v3.reduced.CompanyProductReduced;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.*;
import java.util.*;


@Path("/api/v3/vendor/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MorniPublic {


    @EJB
    private DAO dao;


    @InternalApp
    @Path("cars")
    @GET
    public Response searchVin(@HeaderParam (HttpHeaders.AUTHORIZATION) String header, @Context UriInfo info) {
        try{
            String vin = info.getQueryParameters().getFirst("vin");
            String catalogId = info.getQueryParameters().getFirst("catalogid");
            Response r = this.getCatalogSecuredRequest(AppConstants.getCatalogCarsByVin(catalogId, vin));
            System.out.println("catalog call : " + AppConstants.getCatalogCarsByVin(catalogId, vin));
            System.out.println("status from catalog call " + r.getStatus());
           if(r.getStatus() != 200){
//                async.saveVinSearch(vin, catalogId, header, false);
                return Response.status(404).build();
            }
            List<CatalogCar> catalogCars = r.readEntity(new GenericType<List<CatalogCar>>(){});
        //    if(catalogCars.isEmpty()){
  //              async.saveVinSearch(vin, catalogId, header, false);
          //  }else{
    //            async.saveVinSearch(vin, catalogId, header, true);
            //}
            return Response.status(200).entity(catalogCars).build();
        }catch (Exception ex){
            ex.printStackTrace();
            return Response.status(500).build();
        }
    }


    @InternalApp
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
            //initProductHolders(catalogPart);
            return Response.status(200).entity(catalogPart).build();
        }catch (Exception ex){
            return Response.status(500).build();
        }
    }

    @InternalApp
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

    //new
    @InternalApp
    @POST
    @Path("search-products")
    public Response searchCompanyProduct(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, Map<String, Object> sr) {
        final String query = (String) sr.get("query");
        if(query.length() == 0){
            return Response.status(404).build();
        }
        List<CompanyProductReduced> companyProducts = searchCompanyProducts(query);
        addCompany(companyProducts);
        addCities(companyProducts);
        return Response.ok().entity(companyProducts).build();
    }


    private List<CompanyProductReduced> searchCompanyProducts(String query) {
        try {
            String undecorated = "%" + Helper.undecorate(query) + "%";
            String sql = "select b from CompanyProductReduced b where " +
                    " (b.partNumber like :value0 or b.alternativeNumber like :value0) and (b.id in (" +
                    " select c.companyProductId from CompanyStockReduced c where c.offerOnly =:value1))";
            return dao.getJPQLParams(CompanyProductReduced.class, sql, undecorated, false);
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    private void addCities(List<CompanyProductReduced> products){
        Set<Integer> cityIds = new HashSet<>();
        products.forEach(g-> g.getStock().forEach(h-> cityIds.add(h.getCityId())));
        Map<String,Object> map = new HashMap<>();
        map.put("cityIds", cityIds);
        Response r = this.postSecuredRequest(AppConstants.POST_CITIES_REDUCED, map, "Bearer "+AppConstants.INTERNAL_APP_SECRET);
        if(r.getStatus() == 200){
            List<Map<String,Object>> citiesMap =  r.readEntity(new GenericType<List<Map<String,Object>>>(){});
            for(var pr :products){
                for(var stk : pr.getStock()){
                    if(stk.getQuantity() > 10 ) stk.setQuantity(10);
                    for(Map<String,Object> cityMap : citiesMap){
                        if(stk.getCityId() == (int) cityMap.get("id")){
                            stk.getBranch().setCity(cityMap);
                            break;
                        }
                    }

                }
            }
        }
    }

    private void addCompany(List<CompanyProductReduced> products){
        Set<Integer> companyIds = new HashSet<>();
        products.forEach(g -> companyIds.add(g.getCompanyId()));
        Map<String,Object> map = new HashMap<>();
        map.put("companyIds", companyIds);
        Response r = this.postSecuredRequest(AppConstants.POST_COMPANIES_REDUCED, map, "Bearer "+AppConstants.INTERNAL_APP_SECRET);
        if(r.getStatus() == 200){
            List<CompanyReduced> companiesMap =  r.readEntity(new GenericType<List<CompanyReduced>>(){});
            for(var pr :products){
                for(var comp : companiesMap){
                    if(pr.getCompanyId() == comp.getId()){
                        Map<String,Object> compMap = new HashMap<>();
                        compMap.put("id", comp.getId());
                        compMap.put("name", comp.getName());
                        compMap.put("nameAr", comp.getNameAr());
                        pr.setCompany(compMap);
                        for(var stk : pr.getStock()){
                            stk.setBranch(comp.getBranchFromId(stk.getBranchId()));
                        }
                        break;
                    }
                }
            }
        }
    }


    public <T> Response postSecuredRequest(String link, T t, String authHeader) {
        Invocation.Builder b = ClientBuilder.newClient().target(link).request();
        b.header(HttpHeaders.AUTHORIZATION, authHeader);
        return b.post(Entity.entity(t, "application/json"));
    }


    private Response getCatalogSecuredRequest(String link) {
        Invocation.Builder b = ClientBuilder.newClient().target(link).request();
        b.header(HttpHeaders.AUTHORIZATION, AppConstants.PARTS_CATALOG_API_KEY);
        Response r = b.get();
        return r;
    }

}
