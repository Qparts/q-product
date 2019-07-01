package q.rest.product.operation;

import q.rest.product.dao.DAO;
import q.rest.product.filter.SecuredUser;
import q.rest.product.helper.Helper;
import q.rest.product.model.contract.ProductHolder;
import q.rest.product.model.contract.StockDeduct;
import q.rest.product.model.entity.*;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

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
    @Path("categories/make/{makeId}")
    public Response getMakeCategories(@PathParam(value = "makeId") int makeId){
        try{
            String sql = "select b from Category b where b.categoryId in (" +
                    "select c.categoryId from CategoryMake c where c.makeId =:value0)";
            List<Category> categories = dao.getJPQLParams(Category.class, sql , makeId);
            for(Category category : categories){
                this.addTags(category);
                this.addSpecs(category);
            }
            return Response.status(200).entity(categories).build();
        }catch(Exception ex){
            return Response.status(500).build();
        }
    }

    @SecuredUser
    @POST
    @Path("search-product-by-number")
    public Response searchProductByNumber(Map<Object, Object> map){
        try{
            boolean inStock = (boolean) map.get("inStock");
            String number = (String) map.get("number");
            String undecor = "%" + Helper.undecorate(number) + "%";
            String jpql = "select b from Product b where b.productNumber like :value0 and b.status =:value1";
            String stockCond = " and b.id in (select c.productId from Stock c where c.quantity > :value2)";
            String voidCond = " and b.createdBy >= :value2";
            if(inStock){
                jpql += stockCond;
            }else{
                jpql += voidCond;
            }
            List<Product> products = dao.getJPQLParams(Product.class, jpql, undecor, 'A', 0);
            List<ProductHolder> holders = new ArrayList<>();
            for(Product product : products){
                holders.add(getProductHolder(product));
            }
            return Response.status(200).entity(holders).build();
        }catch(Exception ex){
            return Response.status(500).build();
        }
    }

    @SecuredUser
    @POST
    @Path("find-or-create-product")
    public Response findOrCreateProduct(Map<Object,Object> map){
        try{
            String number = (String) map.get("number");
            String name = (String) map.get("name");
            Integer brandId = ((Number) map.get("brandId")).intValue();
            Integer createdBy = ((Number) map.get("createdBy")).intValue();
            String undecor = Helper.undecorate(number);
            String jpql = "select b from Product b where b.productNumber = :value0 and b.brand.id = :value1";
            List<Product> products = dao.getJPQLParams(Product.class, jpql, undecor, brandId);
            ProductHolder holder = new ProductHolder();
            if (products.isEmpty()) {
                Product product = new Product();
                Brand brand = dao.find(Brand.class, brandId);
                product.setCreated(new Date());
                product.setProductNumber(undecor);
                product.setBrand(brand);
                product.setCreatedBy(createdBy);
                product.setDesc(name);
                product.setDetails("");
                product.setStatus('A');
                dao.persist(product);
                createSparePartsCategory(product);
                holder.setProduct(product);
            }
            else{
                holder.setProduct(products.get(0));
            }
            holder.setTags(this.getProductTags(holder.getProduct().getId()));
            holder.setProductPrices(this.getProductPrices(holder.getProduct().getId()));
            holder.setCategories(this.getProductCategories(holder.getProduct().getId()));
            holder.setProductSpecs(this.getProductSpecs(holder.getProduct().getId()));
            return Response.status(200).entity(holder).build();
        }catch (Exception ex){
            ex.printStackTrace();
            return Response.status(500).build();
        }
    }

    @SecuredUser
    @PUT
    @Path("product-price")
    public Response productPrice(ProductPrice pp) {
        try {
            pp.setCreated(new Date());
            pp.setStatus('A');
            String sql = "select b from ProductPrice b where b.productId = :value0 and b.vendorId = :value1 and b.status =:value2";
            List<ProductPrice> pps = dao.getJPQLParams(ProductPrice.class, sql, pp.getProductId(), pp.getVendorId(), 'A');
            if (!pps.isEmpty()) {
                for (ProductPrice oldpp: pps) {
                    oldpp.setStatus('X');// archive old price
                    dao.update(oldpp);
                }
            }
            dao.persist(pp);
            return Response.status(200).entity(pp).build();
        } catch (Exception ex) {
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
    @GET
    @Path("products/newest")
    public Response getLatestProducts(){
        try {
//            String sql = "select b from Product b where b.id != :value0 order by created desc";
  //          List<Product> products = dao.getJPQLParamsOffsetMax(Product.class, sql, 'A', 20, 0L);
            List<Product> products = new ArrayList<>();
            return Response.status(200).entity(products).build();
        }catch (Exception ex){
            return Response.status(500).build();
        }
    }

    @SecuredUser
    @POST
    @Path("products/search")
    public Response searchProduct(Map<String,String> map){
        try {
            String query = map.get("query");
            String numbered = Helper.getNumberedQuery(query);
            String lowered = "%"+ query.trim().toLowerCase() + "%";
            long asId = Helper.getQueryAsId(query);
            String sql = "select b from Product b " +
                    "where lower(b.desc) like :value0 " +
                    "or lower(b.descAr) like :value0 "+
                    "or b.productNumber like :value1 or b.id = :value2 ";
            List<Product> products = dao.getJPQLParamsOffsetMax(Product.class, sql, 0, 20, lowered, numbered, asId);
            return Response.status(200).entity(products).build();
        }catch (Exception ex){
            return Response.status(500).build();
        }
    }

    @SecuredUser
    @GET
    @Path("products/search/{query}/id-only")
    public Response searchProductsIds(@PathParam(value = "query") String query){
        try {
            String numbered = Helper.getNumberedQuery(query);
            String lowered = "%"+ query.trim().toLowerCase() + "%";
            long asId = Helper.getQueryAsId(query);
            String sql = "select b.id from Product b where lower(b.desc) like :value0 " +
                    "or lower(b.productNumber) like :value1 or b.id = :value2";
            List<Long> products = dao.getJPQLParamsOffsetMax(Long.class, sql, 0, 20, lowered, numbered, asId);
            return Response.status(200).entity(products).build();
        }catch (Exception ex){
            return Response.status(500).build();
        }
    }

    @SecuredUser
    @GET
    @Path("product/{id}")
    public Response getProduct(@PathParam(value = "id") long id){
        try{
            Product product = dao.find(Product.class, id);
            if(product == null){
                return Response.status(404).build();
            }
            ProductHolder holder = getProductHolder(product);
            return Response.status(200).entity(holder).build();
        }catch (Exception ex){
            return Response.status(500).build();
        }
    }

    private ProductHolder getProductHolder(Product product){
        ProductHolder holder = new ProductHolder();
        holder.setProduct(product);
        holder.setCategories(getProductCategories(product.getId()));
        holder.setProductPrices(getProductPrices(product.getId()));
        holder.setProductSpecs(getProductSpecs(product.getId()));
        holder.setTags(getProductTags(product.getId()));
        holder.setLiveStock(getLiveStock(product.getId()));
        return holder;
    }

    @SecuredUser
    @POST
    @Path("product")
    public Response createProduct(@HeaderParam("Authorization") String header, ProductHolder holder){
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
            this.createProductPrice(product.getId(), holder.getProductPrices().get(0));
            return Response.status(200).entity(product.getId()).build();
        }catch (Exception ex){
            ex.printStackTrace();
            return Response.status(500).build();
        }
    }

    @SecuredUser
    @POST
    @Path("product-spec")
    public Response createProductSpec(ProductSpec ps){
        try{
            if (ps.getValueAr() == null || ps.getValueAr().length() == 0) {
                ps.setValueAr(ps.getValue());
            }
            var specs = dao.getTwoConditions(ProductSpec.class, "productId", "spec.id", ps.getProductId(), ps.getSpec().getId());
            if(!specs.isEmpty()){
                if(specs.size() == 1){
                    specs.get(0).setValueAr(ps.getValueAr());
                    specs.get(0).setValue(ps.getValue());
                    dao.update(specs.get(0));
                }
                else{
                    return Response.status(409).build();
                }
            }else{
                String sql = "insert into prd_product_specification (spec_id, product_id, value, value_ar, created, created_by, status) " +
                        "values(" + ps.getSpec().getId() +" ,"
                        + ps.getProductId() +", "
                        + "'" + ps.getValue() +"' , "
                        + " '" + ps.getValueAr() +"' , "
                        + " now() , "
                        + ps.getCreatedBy() + ", "
                        + " '" + ps.getStatus() + "')";
                dao.insertNative(sql);
            }
            return Response.status(201).build();
        }catch (Exception ex){
            return Response.status(500).build();
        }
    }

    @SecuredUser
    @PUT
    @Path("product")
    public Response updateProduct(@HeaderParam("Authorization") String header, ProductHolder holder){
        try{
            dao.update(holder.getProduct());
            return Response.status(201).build();
        }catch (Exception ex){
            ex.printStackTrace();
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

    @SecuredUser
    @POST
    @Path("stock/add")
    public Response addStock(List<Stock> stockList){
        try{
            for(Stock stock : stockList){
                String sql = "select b from Stock b where b.productId = :value0 " +
                        " and b.purchaseProductId = :value1 " +
                        " and b.vendorId = :value2 " +
                        " and b.purchaseId = :value3 ";

                Stock checkStock = dao.findJPQLParams(Stock.class, sql , stock.getProductId(), stock.getPurchaseProductId(), stock.getVendorId(), stock.getPurchaseId());
                if(checkStock != null){
                    checkStock.setQuantity(checkStock.getQuantity() + stock.getQuantity());
                    dao.update(checkStock);
                }
                else{
                    stock.setCreated(new Date());
                    dao.persist(stock);
                }
            }
            return Response.status(201).build();
        }catch (Exception ex){
            return Response.status(500).build();
        }
    }


    @SecuredUser
    @PUT
    @Path("stock/deduct/purchase-return")
    public Response deductStockPurchaseReturn(List<Stock> stocks){
        try{
            for(Stock stock : stocks){
                String sql = "select b from Stock b where b.productId = :value0 " +
                        " and b.purchaseProductId = :value1 " +
                        " and b.vendorId = :value2 " +
                        " and b.purchaseId = :value3 ";
                Stock checkStock = dao.findJPQLParams(Stock.class, sql , stock.getProductId(), stock.getPurchaseProductId(), stock.getVendorId(), stock.getPurchaseId());
                if(checkStock != null){
                    checkStock.setQuantity(checkStock.getQuantity() - stock.getQuantity());
                    dao.update(checkStock);
                }
            }
            return Response.status(201).build();
        }catch (Exception ex){
            return Response.status(500).build();
        }
    }


    @SecuredUser
    @POST
    @Path("stock/deduct")
    public Response deductStock(List<StockDeduct> stockDeducts){
        try{
            //check if all stocks are available
            if(!isAllStockAvailable(stockDeducts)){
                return Response.status(409).build();
            }
            for(StockDeduct sd : stockDeducts){
                String sql = "select b from Stock b where b.productId =:value0 and b.quantity > :value1 order by b.created asc";
                List<Stock> stocks = dao.getJPQLParams(Stock.class, sql, sd.getProductId(), 0);
                if(!stocks.isEmpty()){
                    int remaining = sd.getQuantity();
                    int index = 0;
                    while(remaining > 0){
                        try {
                            Stock stock = stocks.get(index);
                            if (stock.getQuantity() <= remaining) {
                                remaining = remaining - stock.getQuantity();
                                Map<String,Object> map = new HashMap<>();
                                map.put("purchaseProductId", stock.getPurchaseProductId());
                                map.put("quantity", stock.getQuantity());
                                sd.getPurchaseProductIds().add(map);
                                stock.setQuantity(0);
                                dao.update(stock);
                            } else {
                                stock.setQuantity(stock.getQuantity() - remaining);
                                Map<String,Object> map = new HashMap<>();
                                map.put("purchaseProductId", stock.getPurchaseProductId());
                                map.put("quantity", remaining);
                                sd.getPurchaseProductIds().add(map);
                                dao.update(stock);
                                remaining = 0;
                            }
                            index++;
                        }catch (ArrayIndexOutOfBoundsException ex){
                            //no more to delete!! create a negative stock
                            remaining = 0;
                        }
                    }
                }else{
                    //no more to delete!! create a negative stock
                }
            }
            return Response.status(200).entity(stockDeducts).build();
        }catch(Exception ex){
            return Response.status(500).build();
        }
    }

    private boolean isAllStockAvailable(List<StockDeduct> stockDeducts){
        boolean success = true;
        var filtered = new ArrayList<StockDeduct>();
        for(StockDeduct sd : stockDeducts){
           if(filtered.contains(sd)){
               success = false;
               break;
           }
           else{
               filtered.add(sd);
           }
        }
        if(success){
            for(StockDeduct sd : filtered){
                String sql = "select sum(b.quantity) from Stock b where b.productId = :value0 and b.quantity > :value1";
                Number n = dao.findJPQLParams(Number.class, sql, sd.getProductId(), 0);
                if(n == null){
                    n = 0;
                }
                if(n.intValue() < sd.getQuantity()){
                    success = false;
                    break;
                }
            }

        }
        return success;
    }

    /*
    @SecuredUser
    @POST
    @Path("stock/deduct")
    public Response deductStock(List<StockDeduct> stockDeducts){
        try{
            for(StockDeduct sd : stockDeducts){
                String sql = "select b from Stock b where b.productId where order by b.created asc";
                List<Stock> stocks = dao.getJPQLParams(Stock.class, sql, sd.getProductId());
                if(!stocks.isEmpty()){
                    int remaining = sd.getQuantity();
                    int index = 0;
                    while(remaining > 0){
                        try {
                            Stock stock = stocks.get(index);
                            if (stock.getQuantity() <= remaining) {
                                remaining = remaining - stock.getQuantity();
                                dao.delete(stock);
                            } else {
                                stock.setQuantity(stock.getQuantity() - remaining);
                                dao.update(stock);
                                remaining = 0;
                            }
                            index++;
                        }catch (ArrayIndexOutOfBoundsException ex){
                            //no more to delete!! create a negative stock
                            createNegativeStock(sd);
                            remaining = 0;
                        }
                    }
                }else{
                    //no more to delete!! create a negative stock
                  createNegativeStock(sd);
                }
            }
            return Response.status(201).build();
        }catch (Exception ex){
            return Response.status(500).build();
        }
    }
    */

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

    private void createSparePartsCategory(Product product){
        try {
            String sql = "select b from Category b where lower(b.name) =:value0";
            Category category = dao.findJPQLParams(Category.class, sql, "Spare Parts".toLowerCase());
            ProductCategory pc = new ProductCategory(product.getId(), category.getId());
            dao.persist(pc);
        }catch(Exception ignore){

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
            Helper h = new Helper();
            for (ProductSpec ps : productSpecs) {
                String date = h.getDateFormat(new Date());
                ps.setProductId(productId);
                if (ps.getValueAr() == null || ps.getValueAr().length() == 0) {
                    ps.setValueAr(ps.getValue());
                }

                String sql = "insert into prd_product_specification (spec_id, product_id, value, value_ar, created, created_by, status) " +
                        "values(" + ps.getSpec().getId() +" ,"
                        + productId +", "
                        + "'" + ps.getValue() +"' , "
                        + " '" + ps.getValueAr() +"' , "
                        + " '" + date + "' , "
                        + ps.getCreatedBy() + ", "
                        + " '" + ps.getStatus() + "')";
                dao.insertNative(sql);
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

    private List<Stock> getLiveStock(long productId){
        String sql = "select b from Stock b where b.productId = :value0 and b.quantity > :value1";
        List<Stock> stocks = dao.getJPQLParams(Stock.class, sql , productId, 0);
        return stocks;
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

}
