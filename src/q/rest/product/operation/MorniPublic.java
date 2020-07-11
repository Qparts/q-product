package q.rest.product.operation;


import q.rest.product.dao.DAO;
import q.rest.product.filter.annotation.InternalApp;
import q.rest.product.filter.annotation.UserSubscriberJwt;
import q.rest.product.helper.AppConstants;
import q.rest.product.helper.Helper;
import q.rest.product.model.contract.subscriber.BranchReduced;
import q.rest.product.model.contract.subscriber.CompanyReduced;
import q.rest.product.model.entity.v3.reduced.CompanyProductReduced;
import q.rest.product.model.entity.v3.stock.CompanyProduct;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;


@Path("/api/v3/vendor/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MorniPublic {


    @EJB
    private DAO dao;

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

}
