package q.rest.product.operation;

import q.rest.product.dao.DAO;
import q.rest.product.filter.SecuredUser;
import q.rest.product.helper.Helper;
import q.rest.product.model.contract.ProductCreation;
import q.rest.product.model.entity.*;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;

@Path("/internal/api/v2/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductInternalApiV2 {

    @EJB
    private DAO dao;

    @SecuredUser
    @PUT
    @Path("category")
    public Response createCategory(Category category){
        try{
            Helper.trimNames(category);
            if(categoryExists(category)){
                return Response.status(409).build();
            }
            dao.update(category);
            return Response.status(201).build();
        }catch (Exception ex){
            return Response.status(500).build();
        }
    }

    @SecuredUser
    @POST
    @Path("category")
    public Response createCategory(@HeaderParam("Authorization") String authHeader, Category category) {
        try {
            if(categoryExists(category)){
                return Response.status(409).build();
            }
            Helper.trimNames(category);
            category.setCreated(new Date());
            dao.persist(category);
            createCategoryTags(category);
            createCategorySpecs(category);
            return Response.status(201).build();
        } catch (Exception ex) {
            ex.printStackTrace();
            return Response.status(500).build();
        }
    }


    @SecuredUser
    @GET
    @Path("categories/structured")
    public Response getCategoriesStructured(){
        try{
            List<Category> rootCategories = dao.getCondition(Category.class, "root", true);
            for(Category category : rootCategories){
                this.addChildren(category);
                this.addTags(category);
                this.addSpecs(category);
            }
            return Response.status(200).entity(rootCategories).build();
        }catch (Exception e){
            return Response.status(500).build();
        }
    }


    @SecuredUser
    @GET
    @Path("categories")
    public Response getCategoriesUnStructured(){
        try{
            List<Category> categories = dao.get(Category.class);
            for(Category category : categories){
                this.addTags(category);
                this.addSpecs(category);
            }
            return Response.status(200).entity(categories).build();
        }catch (Exception e){
            return Response.status(500).build();
        }
    }


    @SecuredUser
    @POST
    @Path("product")
    public Response createProduct(@HeaderParam("Authorization") String header, ProductCreation holder){
        try{
            Product product = holder.getProduct();
            if(productExists(product)){
                return Response.status(409).build();
            }
            product.setCreated(new Date());
            String undecorated = Helper.undecorate(product.getProductNumber());
            product.setProductNumber(undecorated);
            dao.persist(product);
            this.createProductTags(product.getId(), holder.getTags());
            this.createProductSpecs(product.getId(), holder.getProductSpecs());
            this.createProductCategories(product.getId(), holder.getCategories());
            this.createProductPrice(product.getId(), holder.getProductPrice());
          //  async.writeProductImage(header, holder.getImageString(), product.getId());
            return Response.status(200).entity(product.getId()).build();
        }catch (Exception ex){
            return Response.status(500).build();
        }
    }

    @SecuredUser
    @POST
    @Path("spec")
    public Response createSpec(Spec spec){
        try{
            if(specExists(spec)){
                return Response.status(409).build();
            }
            spec.setName(spec.getName().trim());
            spec.setNameAr(spec.getNameAr().trim());
            spec.setCreated(new Date());
            dao.persist(spec);
            return Response.status(201).build();
        }catch(Exception ex){
            return Response.status(500).build();
        }
    }

    @SecuredUser
    @GET
    @Path("specs")
    public Response getAllSpecs(){
        try{
            List<Spec> specs = dao.get(Spec.class);
            return Response.status(200).entity(specs).build();
        }catch (Exception ex){
            return Response.status(500).build();
        }
    }

    @SecuredUser
    @POST
    @Path("brand")
    public Response createBrand(@HeaderParam("Authorization") String authHeader, Brand brand){
        try{
            if(brandExists(brand)){
                return Response.status(409).build();
            }
            brand.setName(brand.getName().trim());
            brand.setNameAr(brand.getNameAr().trim());
            brand.setCreated(new Date());
            brand.setStatus('A');
            dao.persist(brand);
            return Response.status(200).entity(brand.getId()).build();
        }catch(Exception ex){
            return Response.status(500).build();
        }
    }

    @SecuredUser
    @GET
    @Path("brands")
    public Response getAllBrands(){
        try{
            List<Brand> brands = dao.get(Brand.class);
            return Response.status(200).entity(brands).build();
        }catch (Exception ex){
            return Response.status(500).build();
        }
    }

    private boolean categoryExists(Category category){
        String sql = "select b from Category b where (lower(b.name) = :value0 or lower(b.nameAr) = :value1) and b.id != :value2";

        List<Category> categoryList = dao.getJPQLParams(Category.class, sql, category.getName().toLowerCase().trim(), category.getNameAr().toLowerCase().trim(), category.getId());
        return !categoryList.isEmpty();
    }

    private boolean specExists(Spec spec){
        String sql = "select b from Spec b where lower(b.name) = :value0 or b.nameAr = :value1";
        List<Spec> categoryList = dao.getJPQLParams(Spec.class, sql, spec.getName().toLowerCase().trim(), spec.getNameAr().toLowerCase().trim());
        return !categoryList.isEmpty();
    }

    private boolean brandExists(Brand brand){
        String sql = "select b from Brand b where lower(b.name) = :value0 or b.nameAr = :value1";
        List<Brand> brands = dao.getJPQLParams(Brand.class, sql, brand.getName().toLowerCase().trim(), brand.getNameAr().toLowerCase().trim());
        return !brands.isEmpty();
    }

    private boolean productExists(Product product){
        String sql = "select b from Product b where b.productNumber = :value0 and b.brand.id =:value1";
        List<Product> check = dao.getJPQLParams(Product.class, sql , product.getProductNumber(), product.getBrand().getId());
        return !check.isEmpty();
    }



    private void createCategoryTags(Category category){
        if(category.getTags() != null) {
            for (String tag : category.getTags()) {
                CategoryTag ct = new CategoryTag(Helper.properTag(tag), category.getId());
                dao.persist(ct);
            }
        }
    }

    private void createProductTags(long productId, List<String> tags){
        if(tags != null) {
            for (String tag : tags) {
                ProductTag pt = new ProductTag(Helper.properTag(tag), productId);
                dao.persist(pt);
            }
        }
    }

    private void createCategorySpecs(Category category){
        if(category.getDefaultSpecs() != null) {
            for (Spec spec : category.getDefaultSpecs()) {
                CategorySpec cs = new CategorySpec(spec.getId(), category.getId());
                dao.persist(cs);
            }
        }
    }

    private void createProductCategories(long productId, List<Category> categories){
        if(categories != null) {
            for (Category category: categories) {
                ProductCategory pc = new ProductCategory(productId, category.getId());
                dao.persist(pc);
            }
        }
    }

    private void createProductPrice(long productId, ProductPrice productPrice){
        if(productPrice != null){
            inactivateVendorProductPrices(productId, productPrice.getVendorId());
            productPrice.setProductId(productId);
            productPrice.setVendorVatPercentage(0.05);
            productPrice.setStatus('A');
            productPrice.setCreated(new Date());
            dao.persist(productPrice);
        }
    }


    //for release 1
    private void inactivateAllProductPrices(long productId){
        String sql = "select b from ProductPrice b where b.productId = :value0 and b.status = :value1";
        List<ProductPrice> productPrices = dao.getJPQLParams(ProductPrice.class, sql, productId, 'A');
        for(ProductPrice pp : productPrices){
            pp.setStatus('R');//replaced
            dao.update(pp);
        }
    }

    //for future
    private void inactivateVendorProductPrices(long productId, int vendorId){
        String sql = "select b from ProductPrice b where b.productId = :value0 and b.status = :value1 and b.vendorId = :value2";
        List<ProductPrice> productPrices = dao.getJPQLParams(ProductPrice.class, sql, productId, 'A', vendorId);
        for(ProductPrice pp : productPrices){
            pp.setStatus('R');//replaced
            dao.persist(pp);
        }
    }

    private void createProductSpecs(long productId, List<ProductSpec> productSpecs) throws Exception {
        if (productSpecs != null) {
            for (ProductSpec productSpec : productSpecs) {
                productSpec.setCreated(new Date());
                productSpec.setProductId(productId);
                if (productSpec.getValueAr() == null || productSpec.getValueAr().length() == 0) {
                    productSpec.setValueAr(productSpec.getValue());
                }
                dao.persist(productSpec);
            }
        }
    }

    private void addTags(Category category){
        String sql = "select b.tag from CategoryTag b where b.categoryId = :value0";
        List<String> tags = dao.getJPQLParams(String.class, sql, category.getId());
        category.setTags(tags);
    }

    private void addSpecs(Category category){
        String sql = "select c from Spec c where c.id in (select b.specId from CategorySpec b where b.categoryId = :value0)";
        List<Spec> specs = dao.getJPQLParams(Spec.class, sql, category.getId());
        category.setDefaultSpecs(specs);
    }


    private void addChildren(Category category) {
        List<Category> children = dao.getCondition(Category.class, "parentId", category.getId());
        category.setChildren(children);
        this.addTags(category);
        this.addSpecs(category);
        for(Category child : children){
            addChildren(child);
        }
    }

}
