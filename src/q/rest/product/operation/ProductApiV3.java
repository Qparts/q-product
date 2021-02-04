package q.rest.product.operation;

import q.rest.product.dao.DAO;
import q.rest.product.filter.annotation.UserJwt;
import q.rest.product.helper.Helper;
import q.rest.product.model.entity.v3.product.*;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Path("/api/v3/main/internal/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductApiV3 {

    @EJB
    private DAO dao;

    @UserJwt
    @POST
    @Path("product")
    public Response addProduct(Product product){
        product.setProductNumber(Helper.undecorate(product.getProductNumber()));
        product.setStatus('A');
        product.setCreated(new Date());
        String sql = "select b from Product b where b.productNumber = :value0 and b.brand.id = :value1";
        List<Product> check = dao.getJPQLParams(Product.class, sql, product.getProductNumber(), product.getBrand().getId());
        if(!check.isEmpty())
            return Response.status(409).build();
        for(var ps : product.getSpecs()){
            ps.setCreated(new Date());
            ps.setStatus('A');
        }
        dao.persist(product);
        return Response.status(200).entity(product).build();
    }

    @UserJwt
    @DELETE
    @Path("product/{id}/tag/{tag}")
    public Response deleteProductTag(@PathParam(value = "id") long id, @PathParam(value = "tag") String tag) {
        Product product = dao.find(Product.class, id);
        product.getTags().remove(tag);
        dao.update(product);
        return Response.ok().build();
    }

    @UserJwt
    @DELETE
    @Path("product/{id}/category/{category}")
    public Response deleteProductCategory(@PathParam(value = "id") long id, @PathParam(value = "category") int categoryId) {
        Product product = dao.find(Product.class, id);
        Category category = dao.find(Category.class, categoryId);
        product.getCategories().remove(category);
        dao.update(product);
        return Response.ok().build();
    }

    @UserJwt
    @DELETE
    @Path("product/{id}/spec/{specId}")
    public Response deleteProductSpec(@PathParam(value = "id") long id, @PathParam(value = "specId") int specId){
        Product product = dao.find(Product.class, id);
        ProductSpec ps = dao.findTwoConditions(ProductSpec.class, "productId" , "specId" , id , specId);
        product.getSpecs().remove(ps);
        dao.update(product);
        return Response.ok().build();
    }


    @UserJwt
    @POST
    @Path("product/spec")
    public Response addCategoryToProduct(ProductSpec ps){
        ps.setCreated(new Date());
        Product product = dao.find(Product.class, ps.getProductId());
        product.getSpecs().add(ps);
        dao.update(product);
        return Response.ok().build();
    }

    @UserJwt
    @POST
    @Path("product/market-supply")
    public Response addMarketSupply(ProductSupply supply){
        supply.setCreated(new Date());
        Product product = dao.find(Product.class, supply.getProductId());
        product.getMarketSupply().add(supply);
        dao.update(product);
        return Response.ok().build();
    }


    @UserJwt
    @POST
    @Path("product/category")
    public Response addCategoryToProduct(Map<String, Object> map){
        long productId = ((Number) map.get("productId")).longValue();
        int categoryId = ((Number) map.get("categoryId")).intValue();
        Product product = dao.find(Product.class, productId);
        Category category = dao.find(Category.class, categoryId);
        product.getCategories().add(category);
        dao.update(product);
        return Response.ok().build();
    }

    @UserJwt
    @POST
    @Path("product/tag")
    public Response addTagToProduct(Map<String, Object> map){
        long productId = ((Number) map.get("productId")).longValue();
        String tag = (String) map.get("tag");
        Product product = dao.find(Product.class, productId);
        product.getTags().add(Helper.properTag(tag));
        dao.update(product);
        return Response.ok().build();
    }

    @UserJwt
    @GET
    @Path("brands")
    public Response getBrands(){
        String sql = "select b from Brand b order by b.name";
        List<Brand> brands = dao.getJPQLParams(Brand.class, sql);
        return Response.ok().entity(brands).build();

    }

    @UserJwt
    @PUT
    @Path("product")
    public Response updateProduct(Map<String, Object> map){
        long productId = ((Number) map.get("productId")).longValue();
        String number = Helper.undecorate((String) map.get("number"));
        String desc = (String) map.get("desc");
        String descAr = (String) map.get("descAr");
        String details = (String) map.get("details");
        String detailsAr = (String) map.get("detailsAr");
        char status = ((String) map.get("status")).charAt(0);
        int brandId = (int) map.get("brandId");

        Product product = dao.find(Product.class, productId);
        //check if number already exist
        if(!product.getProductNumber().equals(number) || product.getBrand().getId() != brandId){
            Product check = dao.findTwoConditions(Product.class, "productNumber", "brand.id", number, brandId);
            if(check != null) return Response.status(409).build();
        }
        product.setProductNumber(number);
        product.setProductDesc(desc);
        product.setProductDescAr(descAr);
        product.setDetails(details);
        product.setDetailsAr(detailsAr);
        if(product.getBrand().getId() != brandId){
            Brand brand = dao.find(Brand.class, brandId);
            product.setBrand(brand);
        }
        product.setStatus(status);
        dao.update(product);
        return Response.ok().build();
    }


    @UserJwt
    @GET
    @Path("product/{id}")
    public Response getProduct(@PathParam(value = "id") long id) {
        Product product = dao.find(Product.class, id);
        return product == null ? Response.status(404).build() : Response.status(200).entity(product).build();
    }

    @UserJwt
    @GET
    @Path("category/{id}")
    public Response getCategory(@PathParam(value = "id") int id) {
        Category category = dao.find(Category.class, id);
        return category == null ? Response.status(404).build() : Response.status(200).entity(category).build();
    }

    @UserJwt
    @GET
    @Path("categories")
    public Response getRootCategories(){
        List<Category> categories = dao.getCondition(Category.class, "root", true);
        return Response.ok().entity(categories).build();
    }

    @UserJwt
    @GET
    @Path("specs")
    public Response getSpecs(){
        List<Spec> specs = dao.get(Spec.class);
        return Response.ok().entity(specs).build();
    }


    @UserJwt
    @GET
    @Path("brand/{id}")
    public Response getBrand(@PathParam(value = "id") int id) {
        Brand brand = dao.find(Brand.class, id);
        return brand == null ? Response.status(404).build() : Response.status(200).entity(brand).build();
    }

    @UserJwt
    @PUT
    @Path("merge")
    public Response merge(Map<String,Integer> map){
        int mainId = map.get("mainId");
        int secId = map.get("secondaryId");
        String sql = "update prd_vin_search set company_id = " + mainId + " where company_id = " + secId;
        dao.updateNative(sql);
        return Response.status(200).build();
    }


}
