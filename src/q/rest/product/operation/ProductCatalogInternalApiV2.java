package q.rest.product.operation;

import q.rest.product.dao.DAO;
import q.rest.product.filter.ValidApp;
import q.rest.product.helper.AppConstants;
import q.rest.product.helper.Helper;
import q.rest.product.model.catalog.*;
import q.rest.product.model.contract.ProductHolder;
import q.rest.product.model.entity.*;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.*;
import java.util.Date;
import java.util.List;

@Path("/internal/api/v2/catalog/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductCatalogInternalApiV2 {

    @EJB
    private DAO dao;


    @ValidApp
    @Path("cars")
    @GET
    public Response searchVin(@Context UriInfo info) {
        try{
            String vin = info.getQueryParameters().getFirst("vin");
            String catalogId = info.getQueryParameters().getFirst("catalogid");
            Response r = this.getCatalogSecuredRequest(AppConstants.getCatalogCarsByVin(catalogId, vin));
            System.out.println("status vin search " + r.getStatus());
            if(r.getStatus() != 200){
                System.out.println("vin not found");
                vinNotFound(vin, catalogId);
                return Response.status(404).build();
            }
            List<CatalogCar> catalogCars = r.readEntity(new GenericType<List<CatalogCar>>(){});
            return Response.status(200).entity(catalogCars).build();
        }catch (Exception ex){
            return Response.status(500).build();
        }
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

    @ValidApp
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
            return Response.status(200).entity(catalogGroups).build();

        }catch (Exception e){
            e.printStackTrace();
            return Response.status(500).build();
        }
    }
    @ValidApp
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
            //initProductHolders(catalogPart, makeId);
            return Response.status(200).entity(catalogPart).build();
        }catch (Exception ex){
            return Response.status(500).build();
        }
    }

    private void initProductHolders(CatalogPart catalogPart, int makeId){
        for(CatalogPartsGroup cpg : catalogPart.getPartGroups()){
            for(CatalogPartsList parts : cpg.getParts()){
                ProductHolder holder = getProductHolder(parts.getNumber(), makeId);
//                parts.setProductHolder(holder);
            }
        }
    }

    private ProductHolder getProductHolder(String partNumber, int makeId){
        String undecor = Helper.undecorate(partNumber);
        int brandId = getCatalogBrandId(makeId);
        String jpql = "select b from Product b where b.productNumber = :value0 and b.status =:value1 and b.brand.id = :value2";
        Product product = dao.findJPQLParams(Product.class, jpql, undecor, 'A', brandId);
        ProductHolder holder = null;
        if(product != null){
            holder = new ProductHolder();
            holder.setProduct(product);
            holder.setTags(this.getProductTags(product.getId()));
            holder.setProductPrices(this.getProductPrices(product.getId()));
            holder.setCategories(this.getProductCategories(product.getId()));
            holder.setProductSpecs(this.getProductSpecs(product.getId()));
        }
        return holder;
    }

    public int getCatalogBrandId(int makeId){
        switch (makeId){
            case 1:
            case 2:
                return 2;
            case 3:
                return 9;
            case 4:
                return 15;
            case 5:
            case 6:
            case 7:
        }
        return 0;
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




    private List<String> getProductTags(long productId){
        String sql = "select b.tag from ProductTag b where b.productId = :value0";
        List<String> tags = dao.getJPQLParams(String.class, sql, productId);
        return tags;
    }

    private List<ProductSpec> getProductSpecs(long productId){
        String sql = "select b from ProductSpec b where b.productId = :value0";
        List<ProductSpec> ps = dao.getJPQLParams(ProductSpec.class, sql, productId);
        return ps;
    }

    private List<ProductPrice> getProductPrices(long productId){
        String sql = "select b from ProductPrice b where b.productId = :value0 and b.status = :value1 order by b.created";
        List<ProductPrice> ps = dao.getJPQLParams(ProductPrice.class, sql, productId, 'A');
        return ps;
    }

    private List<Category> getProductCategories(long productId){
        String sql = "select c from Category c where c.id in (select b.categoryId from ProductCategory b where b.productId = :value0)";
        List<Category> categories = dao.getJPQLParams(Category.class, sql, productId);
        return categories;
    }



    private Response getCatalogSecuredRequest(String link) {
        Invocation.Builder b = ClientBuilder.newClient().target(link).request();
        b.header(HttpHeaders.AUTHORIZATION, AppConstants.PARTS_CATALOG_API_KEY);
        Response r = b.get();
        return r;
    }

}
