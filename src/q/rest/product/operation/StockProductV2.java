package q.rest.product.operation;

import org.jboss.logging.Logger;
import q.rest.product.dao.DaoApi;
import q.rest.product.filter.annotation.SubscriberJwt;
import q.rest.product.helper.AppConstants;
import q.rest.product.helper.Attacher;
import q.rest.product.helper.Helper;
import q.rest.product.model.product.full.Brand;
import q.rest.product.model.product.full.BrandClass;
import q.rest.product.model.qstock.*;
import q.rest.product.model.qstock.views.StockProductView;
import q.rest.product.model.qstock.views.StockPurchaseSummary;
import q.rest.product.model.qstock.views.StockSalesSummary;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.*;
import java.util.*;

@Path("/api/v4/stock/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class StockProductV2 {

    @EJB
    private DaoApi daoApi;

    private static final Logger logger = Logger.getLogger(StockProductV2.class);


    @SubscriberJwt
    @POST
    @Path("search-product")
    public Response searchProduct(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, Map<String, Object> map) {
        String query = (String) map.get("query");
        logger.info("search-product");
        Set<StockProductView> products = daoApi.searchProduct(query, Helper.getCompanyFromJWT(header));
        logger.info("search-product:done");
        return Response.status(200).entity(products).build();
    }

    @SubscriberJwt
    @GET
    @Path("product/{id}")
    public Response findProductById(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, @PathParam(value = "id") long id) {
        int companyId = Helper.getCompanyFromJWT(header);
        logger.info("product/" + id);
        var product = daoApi.findProduct(id, companyId);
        logger.info("product/" + id + ": done");
        return Response.status(200).entity(product).build();
    }

    @SubscriberJwt
    @POST
    @Path("find-product")
    public Response findProduct(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, Map<String, Object> map) {
        String partNumber = (String) map.get("productNumber");
        int brandId = (int) map.get("brandId");
        int companyId = Helper.getCompanyFromJWT(header);
        logger.info("find-product");
        StockProductView product = daoApi.findStockProductView(companyId, partNumber, brandId);
        if (product == null) {
            return Response.status(200).build();
        }

        if (product.getPolicyId() != null)
            return Response.status(409).build();
        logger.info("find-product: done");
        return Response.status(200).entity(product).build();
    }

    @SubscriberJwt
    @POST
    @Path("brand")
    public Response createBrand(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, Brand brand) {
        int companyId = Helper.getCompanyFromJWT(header);
        brand.setName(brand.getName().trim());
        brand.setNameAr(brand.getNameAr().trim());
        brand.setCreatedBy(companyId);
        logger.info("create brand");
        if (!daoApi.isBrandAvailable(brand.getClassId(), brand.getName(), brand.getNameAr()))
            return Response.status(409).build();
        daoApi.createBrand(brand);
        logger.info("creating brand done");
        return Response.status(201).build();
    }

    @SubscriberJwt
    @GET
    @Path("brands")
    public Response getAllBrand(@HeaderParam(HttpHeaders.AUTHORIZATION) String header) {
        int companyId = Helper.getCompanyFromJWT(header);
        logger.info("get brands ");
        List<Brand> brands = daoApi.getBrands(companyId);
        logger.info("get brands done");
        return Response.status(200).entity(brands).build();
    }

    @SubscriberJwt
    @GET
    @Path("brand-classes")
    public Response getBrandClasses(){
        logger.info("get brand classes");
        List<BrandClass> brandClasses = daoApi.getBrandClasses();
        logger.info("get brand classes done");
        return Response.status(200).entity(brandClasses).build();
    }


    @SubscriberJwt
    @POST
    @Path("search-brand")
    public Response searchBrand(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, Map<String, String> map) {
        int companyId = Helper.getCompanyFromJWT(header);
        String name = map.get("query");
        logger.info("search brands");
        List<Brand> brands = daoApi.searchBrands(companyId, name);
        logger.info("search brands done");
        return Response.status(200).entity(brands).build();
    }

    @SubscriberJwt
    @PUT
    @Path("product-policy")
    public Response updateProductSetting(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, Map<String,Number> map) {
        int companyId = Helper.getCompanyFromJWT(header);
        long productId = map.get("productId").longValue();
        int policyId = map.get("policyId").intValue();
        logger.info("update product policy");
        StockProductView view = daoApi.updatePolicy(companyId, productId, policyId);
        view.setPolicyId(policyId);
        logger.info("update product policy done");
        return Response.status(200).entity(view).build();
    }

    @SubscriberJwt
    @PUT
    @Path("product-shelf")
    public Response updateProductShelf(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, Map<String,Object> map){
        int companyId = Helper.getCompanyFromJWT(header);
        long productId = ((Number) map.get("productId")).longValue();
        int branchId = ((Number) map.get("branchId")).intValue();
        String newShelf = (String) map.get("shelf");
        logger.info("update shelf");
        daoApi.updateShelf(companyId, productId, branchId, newShelf);
        StockProductView view = daoApi.findProduct(productId, companyId);
        logger.info("update shelf done");
        return Response.status(200).entity(view).build();
    }

    @SubscriberJwt
    @POST
    @Path("product")
    public Response createProduct(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, StockCreateProduct scp) {
        int companyId = Helper.getCompanyFromJWT(header);
        var productView = daoApi.findStockProductView(companyId, scp.getProductNumber(), scp.getBrandId());
        long productId = 0;
        logger.info("create product");
        if (productView == null) {
            StockProduct stockProduct = daoApi.createStockProduct(scp.getProductNumber(), scp.getBrandId(), scp.getName(), scp.getNameAr(), scp.getReferencePrice(), companyId);
            productId = stockProduct.getId();
        } else {
            productId = productView.getProductId();
            var check = daoApi.getStockProductSetting(productId, companyId);
            if (!check.isEmpty())
                return Response.status(409).entity("product already added").build();
        }
        StockProductSetting companyProduct = daoApi.createStockProductSetting(scp, productId, companyId);
        logger.info("create product done");
        return Response.status(200).entity(companyProduct).build();
    }

    //not needed?
    @SubscriberJwt
    @GET
    @Path("product/number/{number}/brand/{brand}")
    public Response findCompanyProduct(@PathParam(value = "number") String number, @PathParam(value = "brand") int brandId) {
        String productNumber = Helper.undecorate(number);
        logger.info("find product");
        StockProduct sp = daoApi.findProduct(productNumber, brandId);
        if (sp == null) return Response.status(404).entity("product not found").build();
        logger.info("find product done");
        return Response.status(200).entity(sp).build();
    }

    @SubscriberJwt
    @PUT
    @Path("price-policy")
    public Response updatePricePolicy(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, StockPricePolicy policy){
        int companyId = Helper.getCompanyFromJWT(header);
        policy.setCompanyId(companyId);
        logger.info("update price policy");
        daoApi.updatePricePolicy(policy);
        logger.info("update price policy done");
        return Response.status(200).build();
    }


    @SubscriberJwt
    @POST
    @Path("price-policy")
    public Response createPricePolicy(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, StockPricePolicy policy) {
        int companyId = Helper.getCompanyFromJWT(header);
        policy.setCompanyId(companyId);
        daoApi.createNewPolicy(policy);
        logger.info("create price policy");
        List<StockPricePolicy> policies = daoApi.getPolicies(companyId);
        if (policies.size() == 1) {
            //create default
            Map<String, Integer> map = new HashMap<>();
            map.put("policyId", policy.getId());
            Response r = this.putSecuredRequest(AppConstants.POST_DEFAULT_POLICIES, map, header);
            r.close();
        }
        logger.info("create price policy done");
        return Response.status(200).build();
    }

    @SubscriberJwt
    @GET
    @Path("price-policies")
    public Response getPolicies(@HeaderParam(HttpHeaders.AUTHORIZATION) String header) {
        int companyId = Helper.getCompanyFromJWT(header);
        logger.info("get price policies");
        List<StockPricePolicy> policies = daoApi.getPolicies(companyId);
        logger.info("get price policies done");
        return Response.status(200).entity(policies).build();
    }


    @SubscriberJwt
    @POST
    @Path("purchase")
    public Response createPurchaseOrder(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, StockPurchase po) {
        po.setCompanyId(Helper.getCompanyFromJWT(header));
        if(po.getCreated() == null)
            po.setCreated(new Date());
        po.setPaymentMethod(po.getTransactionType() == 'C' ? po.getPaymentMethod() : null);
        logger.info("create purchase");
        int purchaseID = daoApi.createPurchase(po);
        po.setId(purchaseID);
        if (po.getTransactionType() == 'T') daoApi.createPurchaseCredit(po);
        updateStock(po);
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("id", purchaseID);
        logger.info("create purchase done");
        return Response.status(200).entity(map).build();
    }

    @SubscriberJwt
    @POST
    @Path("sales")
    public Response createSales(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, StockSales sales) {
        sales.setCompanyId(Helper.getCompanyFromJWT(header));
        sales.setCreated(new Date());
        sales.setPaymentMethod(sales.getTransactionType() == 'C' ? sales.getPaymentMethod() : null);
        logger.info("create sales");
        for (StockSalesItem item : sales.getItems()) {
            StockLive live = daoApi.findBranchStockLive(sales.getCompanyId(), item.getStockProduct().getId(), sales.getBranchId());
            if(live != null) {
                item.setUnitCost(live.getAverageCost());
                item.setLive(live);
            }
        }
        if (!verifyQuantities(sales)) return Response.status(400).build();
        int salesId = daoApi.createSales(sales);
        sales.setId(salesId);
        if (sales.getTransactionType() == 'T') daoApi.createSalesCredit(sales);

        updateStock(sales);
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("id", sales.getId());
        logger.info("create sales done");
        return Response.status(200).entity(map).build();
    }

    @SubscriberJwt
    @GET
    @Path("pending-items")
    public Response pendingItems(@HeaderParam(HttpHeaders.AUTHORIZATION) String header) {
        int companyId = Helper.getCompanyFromJWT(header);
        logger.info("get pending items");
        List<StockSalesItemView> views = daoApi.getPendingItems(companyId);
        logger.info("get pending items done");
        return Response.status(200).entity(views).build();
    }

    @SubscriberJwt
    @PUT
    @Path("pending-item")
    public Response updatePendingItem(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, Map<String,Integer> map){
        int companyId = Helper.getCompanyFromJWT(header);
        int salesItemId = map.get("salesItemId");
        int branchId = map.get("branchId");
        logger.info("update pending items");
        StockSalesItemView salesItem = daoApi.getPendingItem(companyId, salesItemId);
        if(salesItem == null) {
            return Response.status(400).entity("Sales Item not found").build();
        }

        if(salesItem.getStockProduct().getLiveStock() != null) {
            List<StockLive> lives = salesItem.getStockProduct().getLiveStock();
            StockLive stockLive = null;
            for(var live : lives) {
                if(live.getBranchId() == branchId) {
                    stockLive = live;
                }
            }
            if(stockLive == null) {
                return Response.status(400).entity("Quantity Not available").build();
            }

            if(stockLive.getQuantity() >= salesItem.getPendingQuantity() ) {
                int salesItemQuantity = salesItem.getQuantity();
                int salesItemPendingQuantity = salesItem.getPendingQuantity();

                double totalSalesCost = salesItemQuantity * salesItem.getUnitCost();
                double totalPendingCost = salesItemPendingQuantity * stockLive.getAverageCost();
                int totalSalesItemQuantity = salesItemQuantity + salesItemPendingQuantity;
                double newAverage = (totalSalesCost + totalPendingCost) / totalSalesItemQuantity;
                int newQuantity = salesItemQuantity + salesItemPendingQuantity;
                salesItem.setUnitCost(newAverage);
                salesItem.setQuantity(newQuantity);
                salesItem.setPendingQuantity(0);
                daoApi.updateSalesItem(salesItem);

                stockLive.setQuantity(stockLive.getQuantity() - salesItemPendingQuantity);
                if (stockLive.getQuantity() == 0)
                    daoApi.deleteLive(stockLive);
                else
                    daoApi.updateLive(stockLive);
            }
        }
        logger.info("update pending items done");
        return Response.status(200).build();
    }

    //update stock after sales return
    private void updateStock(int companyId, StockSales sales, StockReturnSales salesReturn) {
        for (var item : salesReturn.getItems()) {
            long productId = sales.getStockProductIdFromSalesItem(item.getSalesItem().getId());
            List<StockLive> lives = daoApi.getStockLive(companyId, productId);
            if (lives.isEmpty())
                daoApi.createNewStockLive(companyId, salesReturn.getBranchId(), productId, item.getSalesItem().getUnitCost(), item.getQuantity());
            else {
                double averageCost = Helper.calculateAveragePrice(lives, item.getSalesItem().getUnitCost(), item.getQuantity());
                daoApi.updateAveragePrice(lives, averageCost);
                daoApi.updateExistingStockLive(companyId, salesReturn.getBranchId(), lives, productId, item.getQuantity(), averageCost);
            }
        }
    }

    //update stock after purchase order
    private void updateStock(StockPurchase po) {
        for (var item : po.getItems()) {
            List<StockLive> lives = daoApi.getProductLiveStock(po.getCompanyId(), item.getStockProduct().getId());
            if (lives.isEmpty())
                daoApi.createNewStockLive(po.getCompanyId(), po.getBranchId(), item.getStockProduct().getId(), item.getUnitPrice(), item.getQuantity());
            else {
                double averageCost = Helper.calculateAveragePrice(lives, item.getUnitPrice(), item.getQuantity());
                daoApi.updateAveragePrice(lives, averageCost);
                daoApi.updateExistingStockLive(po.getCompanyId(), po.getBranchId(), lives, item.getStockProduct().getId(), item.getQuantity(), averageCost);
            }
        }
    }


    private void updateStock(StockSales sales) {
        //if pending do not update stock
        for (var item : sales.getItems()) {
            if(item.getLive() != null) {
                StockLive live = item.getLive();
                int newQuantity = item.getPendingQuantity() == 0 ? live.getQuantity() - item.getQuantity() : live.getQuantity() - (item.getQuantity() - item.getPendingQuantity());
                live.setQuantity(newQuantity);
                if (live.getQuantity() == 0)
                    daoApi.deleteLive(live);
                else
                    daoApi.updateLive(live);
            }
        }
    }


    @SubscriberJwt
    @POST
    @Path("search-sales")
    public Response searchSales(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, Map<String, String> map) {
        logger.info("search sales");
        String query = map.get("query");
        int companyId = Helper.getCompanyFromJWT(header);
        List<Integer> customerIds = getCustomerIdsFromQuery(map, header);
        List<StockSalesView> sales = daoApi.searchSales(query, companyId, customerIds);
        Attacher.attachCustomer(sales, header);
        logger.info("search sales done");
        return Response.status(200).entity(sales).build();
    }


    @SubscriberJwt
    @POST
    @Path("search-purchase")
    public Response searchPurchase(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, Map<String, String> map) {
        logger.info("search puchase");
        String query = map.get("query");
        int companyId = Helper.getCompanyFromJWT(header);
        List<Integer> supplierIds = getSupplierIdsFromQuery(map, header);
        List<StockPurchaseView> purchases = daoApi.searchPurchase(query, companyId, supplierIds);
        Attacher.attachSupplier(purchases, header);
        logger.info("search purchase done");
        return Response.status(200).entity(purchases).build();
    }

    @SubscriberJwt
    @POST
    @Path("search-quotation")
    public Response searchQuotation(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, Map<String, String> map) {
        logger.info("search quotation");
        String query = map.get("query");
        int companyId = Helper.getCompanyFromJWT(header);
        List<Integer> customerIds = getCustomerIdsFromQuery(map, header);
        List<StockQuotationView> quotations = daoApi.searchQuotation(query, companyId, customerIds);
        Attacher.attachCustomerQ(quotations, header);
        logger.info("search quotation done");
        return Response.status(200).entity(quotations).build();
    }


    @SubscriberJwt
    @GET
    @Path("sales-credit-balance")
    public Response getSalesCreditBalance(@HeaderParam(HttpHeaders.AUTHORIZATION) String header) {
        logger.info("get sales credit balance");
        int companyId = Helper.getCompanyFromJWT(header);
        List<Map<String, Object>> creditBalance = daoApi.getSalesCreditBalance(companyId, header);
        logger.info("get sales credit balance done");
        return Response.status(200).entity(creditBalance).build();
    }


    @SubscriberJwt
    @GET
    @Path("purchase-credit-balance")
    public Response getPurchaseCreditBalance(@HeaderParam(HttpHeaders.AUTHORIZATION) String header) {
        logger.info("get purchase credit balance");
        int companyId = Helper.getCompanyFromJWT(header);
        List<Map<String, Object>> creditBalance = daoApi.getPurchaseCreditBalance(companyId, header);
        logger.info("get purchase credit balance done");
        return Response.status(200).entity(creditBalance).build();
    }


    @SubscriberJwt
    @GET
    @Path("branches-sales/{date}")
    public Response getAllBranchSales(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, @PathParam(value = "date") long dateLong) {
        logger.info("get branch sales by date");
        int companyId = Helper.getCompanyFromJWT(header);
        List<Map<String, Object>> list = daoApi.getBranchSales(companyId, dateLong);
        logger.info("get branch sales by date done");
        return Response.status(200).entity(list).build();
    }

    @SubscriberJwt
    @GET
    @Path("purchase/{id}")
    public Response getPurchase(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, @PathParam(value = "id") int id) {
        logger.info("get purchase by id");
        StockPurchaseView purchase = daoApi.findPurchase2(id, Helper.getCompanyFromJWT(header));
        Attacher.attachSupplier(purchase, header);
        logger.info("get purchase by id done");
        return Response.status(200).entity(purchase).build();
    }


    @SubscriberJwt
    @GET
    @Path("sales/{id}")
    public Response getSales(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, @PathParam(value = "id") int id) {
        logger.info("get sales by id");
        StockSalesView sales = daoApi.findSales2(id, Helper.getCompanyFromJWT(header));
        Attacher.attachCustomer(sales, header);
        logger.info("get sales by id done");
        return Response.status(200).entity(sales).build();
    }


    @SubscriberJwt
    @GET
    @Path("quotation/{id}")
    public Response getQuotation(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, @PathParam(value = "id") int id) {
        logger.info("get quotation by id ");
        StockQuotationView quotation = daoApi.findQuotation2(id, Helper.getCompanyFromJWT(header));
        Attacher.attachCustomer(quotation, header);
        logger.info("get quotation by id done");
        return Response.status(200).entity(quotation).build();
    }


    @SubscriberJwt
    @GET
    @Path("sales-return/{id}")
    public Response getSalesReturn(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, @PathParam(value = "id") int id) {
        logger.info("get sales return by id");
        int companyId = Helper.getCompanyFromJWT(header);
        StockReturnSalesStandAlone salesReturn = daoApi.getSalesReturn(id, companyId);
        StockSales sales = daoApi.findSales(salesReturn.getSalesId(), companyId);
        Attacher.attachCustomer(sales, header);
        salesReturn.setCustomer(sales.getCustomer());
        salesReturn.setTaxRate(sales.getTaxRate());
        salesReturn.setCustomerId(sales.getCustomerId());
        logger.info("get sales return by id done");
        return Response.status(200).entity(salesReturn).build();
    }


    @SubscriberJwt
    @GET
    @Path("sales-report/from/{from}/to/{to}")
    public Response getSalesReport(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, @PathParam(value = "from") long fromLong, @PathParam(value = "to") long toLong) {
        logger.info("get sales report from to");
        int companyId = Helper.getCompanyFromJWT(header);
        Date from = new Date(fromLong);
        Date to = new Date(toLong);
        List<StockSalesSummary> summaries = daoApi.getDailySalesSummary(from, to, companyId);//ok
        List<Map<String, Object>> topCustomers = daoApi.getTopCustomers(from, to, companyId, header);//ok
        List<Map<String, Object>> topBrands = daoApi.getTopBrands(from, to, companyId, 'S');
        Map<String, Object> map = new HashMap<>();
        map.put("daysSummary", summaries);
        map.put("topCustomers", topCustomers);
        map.put("topBrands", topBrands);
        logger.info("get sales report from to");
        return Response.status(200).entity(map).build();
    }


    @SubscriberJwt
    @GET
    @Path("sales-report/{year}/{month}")
    public Response getSalesReport(@HeaderParam(HttpHeaders.AUTHORIZATION) String header,
                                   @PathParam(value = "year") int year,
                                   @PathParam(value = "month") int month) {
        logger.info("get sales report by year and month");
        Date from = Helper.getFromDate(month, year);
        Date to = Helper.getToDate(month, year);//month = 1 - 12
        int companyId = Helper.getCompanyFromJWT(header);

        List<StockSalesSummary> summaries = daoApi.getDailySalesSummary(from, to, companyId);//ok
        List<Map<String, Object>> topCustomers = daoApi.getTopCustomers(from, to, companyId, header);//ok
        List<Map<String, Object>> topBrands = daoApi.getTopBrands(from, to, companyId, 'S');
        Map<String, Object> map = new HashMap<>();
        map.put("daysSummary", summaries);
        map.put("topCustomers", topCustomers);
        map.put("topBrands", topBrands);
        logger.info("get sales report by year and month done");
        return Response.status(200).entity(map).build();
    }


    @SubscriberJwt
    @GET
    @Path("products-report/from/{from}/to/{to}")
    public Response getProductsReport(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, @PathParam(value = "from") long fromLong, @PathParam(value = "to") long toLong) {
        logger.info("get products report from to");
        int companyId = Helper.getCompanyFromJWT(header);
        Date from = new Date(fromLong);
        Date to = new Date(toLong);
        List<Map<String, Object>> mostProfitableProducts = daoApi.getTopProductsProfitability(from, to, companyId);
        List<Map<String, Object>> mostProfitableBrands = daoApi.getTopBrandsProfitability(from, to, companyId);
        List<Map<String, Object>> mostMovingProducts = daoApi.getTopProductsMovements(from, to, companyId);
        Map<String, Object> map = new HashMap<>();
        map.put("mostProfitableProducts", mostProfitableProducts);
        map.put("mostProfitableBrands", mostProfitableBrands);
        map.put("mostMovingProducts", mostMovingProducts);
        logger.info("get products reprot from to done");
        return Response.status(200).entity(map).build();
    }

    @SubscriberJwt
    @GET
    @Path("product-details-report/{productId}")
    public Response getProductDetailsReport(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, @PathParam(value = "productId") long productId){
        logger.info("get product details report by id");
        int companyId = Helper.getCompanyFromJWT(header);
        var product =  daoApi.findProduct(productId, companyId);
        List<Map<String, Object>> salesPastYear = daoApi.getProductYearSales(productId, companyId);
        List<Map<String, Object>> purchasesPastYear = daoApi.getProductYearPurchase(productId, companyId);
        List<Map<String, Object>> latestPurchases = daoApi.getLatestPurchaseOrders(productId, companyId, header);
        List<Map<String, Object>> latestSales = daoApi.getLatestSalesOrders(productId, companyId, header);
        Map<String,Object> map  = new HashMap<>();
        map.put("product", product);
        map.put("salesTwelveMonths", salesPastYear);
        map.put("purchaseTwelveMonths", purchasesPastYear);
        map.put("latestPurchases", latestPurchases);
        map.put("latestSales", latestSales);
        logger.info("get product details report by id done");
        return Response.status(200).entity(map).build();
    }


    @SubscriberJwt
    @GET
    @Path("purchase-report/{year}/{month}")
    public Response getPurchaseReport(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, @PathParam(value = "year") int year, @PathParam(value = "month") int month) {
        logger.info("get purchase report by year and month");
        Date from = Helper.getFromDate(month, year);
        Date to = Helper.getToDate(month, year);//month = 1 - 12
        int companyId = Helper.getCompanyFromJWT(header);
        List<StockPurchaseSummary> summaries = daoApi.getDailyPurchaseSummary(from, to, companyId);
        List<Map<String, Object>> topSuppliers = daoApi.getTopSuppliers(from, to, companyId, header);
        List<Map<String, Object>> topBrands = daoApi.getTopBrands(from, to, companyId, 'P');
        Map<String, Object> map = new HashMap<>();
        map.put("daysSummary", summaries);
        map.put("topSuppliers", topSuppliers);
        map.put("topBrands", topBrands);
        logger.info("get purchase report by year and date");
        return Response.status(200).entity(map).build();
    }


    @SubscriberJwt
    @GET
    @Path("purchase-report/from/{from}/to/{to}")
    public Response getPurchaseReportFromTo(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, @PathParam(value = "from") long fromLong, @PathParam(value = "to") long toLong) {
        logger.info("get sales report from to");
        int companyId = Helper.getCompanyFromJWT(header);
        Date from = new Date(fromLong);
        Date to = new Date(toLong);
        List<StockPurchaseSummary> summaries = daoApi.getDailyPurchaseSummary(from, to, companyId);
        List<Map<String, Object>> topSuppliers = daoApi.getTopSuppliers(from, to, companyId, header);
        List<Map<String, Object>> topBrands = daoApi.getTopBrands(from, to, companyId, 'P');
        Map<String, Object> map = new HashMap<>();
        map.put("daysSummary", summaries);
        map.put("topSuppliers", topSuppliers);
        map.put("topBrands", topBrands);
        logger.info("get purchase report by year and date");
        return Response.status(200).entity(map).build();
    }

    @SubscriberJwt
    @GET
    @Path("branches-sales-summary")
    public Response getAllBranchSales2(@HeaderParam(HttpHeaders.AUTHORIZATION) String header) {
        logger.info("get branch sales summary");
        int companyId = Helper.getCompanyFromJWT(header);
        List<Integer> branchIds = getBranchIds(header);
        List<BranchSales> branchSales = daoApi.getLiveBranchSales(branchIds, companyId);
        logger.info("get branch sales summary done");
        return Response.status(200).entity(branchSales).build();
    }


    @SubscriberJwt
    @GET
    @Path("daily-sales/from/{from}/to/{to}")
    public Response getDailySales(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, @PathParam(value = "from") long fromLong, @PathParam(value = "to") long toLong) {
        logger.info("get daily sales from to");
        int companyId = Helper.getCompanyFromJWT(header);
        List<Map<String, Object>> dailySales = daoApi.getDailySales(companyId, fromLong, toLong);
        logger.info("get daily sales from to done");
        return Response.status(200).entity(dailySales).build();
    }

    @SubscriberJwt
    @GET
    @Path("daily-purchase/from/{from}/to/{to}")
    public Response getDailyPurchase(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, @PathParam(value = "from") long fromLong, @PathParam(value = "to") long toLong) {
        logger.info("get daily purchase from to");
        int companyId = Helper.getCompanyFromJWT(header);
        List<Map<String, Object>> dailyPurchase = daoApi.getDailyPurchase(companyId, fromLong, toLong);
        logger.info("get daily purchase from to done");
        return Response.status(200).entity(dailyPurchase).build();
    }

    private List<Integer> getBranchIds(String header) {
        Response r = getSecuredRequest(AppConstants.GET_BRANCHES_IDS, header);
        Map<String, ArrayList<Integer>> map = r.readEntity(Map.class);
        return map.get("branchIds");
    }


    @SubscriberJwt
    @GET
    @Path("monthly-sales/year/{year}/month/{month}/length/{length}")
    public Response getPreviousMonthlySales(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, @PathParam(value = "year") int year, @PathParam(value = "month") int month, @PathParam(value = "length") int length) {
        logger.info("get monthly sales by year and month and length");
        int companyId = Helper.getCompanyFromJWT(header);
        List<Map<String, Object>> monthlySales = daoApi.getMonthlySales(companyId, year, month, length);
        logger.info("get monthly sales by year and month and length done");
        return Response.status(200).entity(monthlySales).build();
    }


    @SubscriberJwt
    @GET
    @Path("stock-value")
    public Response getStockValue(@HeaderParam(HttpHeaders.AUTHORIZATION) String header) {
        logger.info("get stock value");
        int companyId = Helper.getCompanyFromJWT(header);
        double total = daoApi.getStockValue(companyId);
        Map<String, Object> map = new HashMap<>();
        map.put("stockValue", total);
        logger.info("get stock value done");
        return Response.status(200).entity(map).build();
    }


    @SubscriberJwt
    @POST
    @Path("credit-payment/{type}")
    public Response createCreditPayment(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, @PathParam(value = "type") String typePath, Map<String, Object> map) {
        logger.info("create credit payment by type");
        int companyId = Helper.getCompanyFromJWT(header);
        String type;
        if (typePath.equals("purchase")) type = "purchase";
        else if (typePath.equals("sales")) type = "sales";
        else return Response.status(404).entity(map).build();

        String reference = (String) map.get("reference");
        int contactId = (int) (type.equals("purchase") ? map.get("supplierId") : map.get("customerId"));
        double amount = ((Number) map.get("amount")).doubleValue();
        String paymentMethod = (String) map.get("paymentMethod");
        long paymentDateLong = (long) map.get("paymentDate");
        Date paymentDate = new Date(paymentDateLong);
        if (type.equals("purchase")) {
            //check if amount is valid
            daoApi.createPurchaseCreditPayment(amount, reference, paymentMethod.charAt(0), contactId, companyId, paymentDate);
        } else {
            //check if amount is valid
            daoApi.createSalesCreditPayment(amount, reference, paymentMethod.charAt(0), contactId, companyId, paymentDate);
        }
        logger.info("create credit payment by type done");
        return Response.status(200).build();
    }

    @SubscriberJwt
    @POST
    @Path("purchase-return")
    public Response createPurchaseReturn(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, StockReturnPurchase purchaseReturn) {
        logger.info("create purchase return");
        int companyId = Helper.getCompanyFromJWT(header);
        StockPurchase purchase = daoApi.findPurchase(purchaseReturn.getPurchaseId(), companyId);
        if (purchase.getCompanyId() != Helper.getCompanyFromJWT(header))
            return Response.status(400).entity("Invalid purchase order").build();

        purchaseReturn.setCreated(new Date());
        purchaseReturn.setPaymentMethod(purchaseReturn.getTransactionType() == 'C' ? purchaseReturn.getPaymentMethod() : null);
        if (!verifyQuantities(purchaseReturn)) return Response.status(400).build();
        //unit average cost to items
        populateAverageCosts(purchaseReturn, purchase);
        daoApi.createPurchaseReturn(purchaseReturn);
        daoApi.createPurchaseReturnCredit(purchaseReturn, purchase);
        updateStock(companyId, purchase, purchaseReturn);
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("id", purchaseReturn.getId());
        logger.info("create purchase return done");
        return Response.status(200).entity(purchaseReturn).build();
    }

    @SubscriberJwt
    @POST
    @Path("sales-return")
    public Response createSalesReturn(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, StockReturnSales salesReturn) {
        logger.info("create sales return");
        int companyId = Helper.getCompanyFromJWT(header);
        StockSales sales = daoApi.findSales(salesReturn.getSalesId(), companyId);
        if (sales.getCompanyId() != Helper.getCompanyFromJWT(header))
            return Response.status(400).entity("Invalid sales order").build();

        salesReturn.setCreated(new Date());
        salesReturn.setPaymentMethod(salesReturn.getTransactionType() == 'C' ? salesReturn.getPaymentMethod() : null);
        if (!verifyQuantities(salesReturn)) return Response.status(400).build();

        daoApi.createSalesReturn(salesReturn);
        daoApi.createSalesReturnCredit(salesReturn, sales);

        updateStock(companyId, sales, salesReturn);
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("id", salesReturn.getId());
        logger.info("create sales return done");
        return Response.status(200).entity(map).build();
    }

    private void populateAverageCosts(StockReturnPurchase purchaseReturn, StockPurchase purchase){
        for (var item : purchaseReturn.getItems()) {
            double averageCost = daoApi.getAverageCost(purchase, purchase.getCompanyId(), item);
            item.setUnitAverageCost(averageCost);
        }
    }



    @SubscriberJwt
    @POST
    @Path("quotation")
    public Response createQuotation(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, StockQuotation quotation) {
        logger.info("create quotation ");
        quotation.setCompanyId(Helper.getCompanyFromJWT(header));
        quotation.setCreated(new Date());
        quotation.setPaymentMethod(quotation.getTransactionType() == 'C' ? quotation.getPaymentMethod() : null);
        int quotationId = daoApi.createQuotation(quotation);
        quotation.setId(quotationId);
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("id", quotation.getId());
        logger.info("create quotation done");
        return Response.status(200).entity(map).build();
    }





    private void updateStock(int companyId, StockPurchase purchase, StockReturnPurchase purchaseReturn) {
        for (var item : purchaseReturn.getItems()) {
            long productId = purchase.getStockProductId(item.getPurchaseItem().getId());
            int branchId = purchaseReturn.getBranchId();
            int quantity = item.getQuantity();
            daoApi.deductBranchStock(companyId, branchId, productId, quantity);
        }
    }

    private boolean verifyQuantities(StockSales sales) {
        for (var item : sales.getItems()) {
            if(item.getLive() == null) {
                //this should be all pending
                if(item.getQuantity() != item.getPendingQuantity()){
                    return false;
                }
            } else {
                if (item.getPendingQuantity() == 0 && item.getLive().getQuantity() < item.getQuantity()) {
                    return false;
                } else if(item.getPendingQuantity() > 0){
                    if (item.getLive().getQuantity() + item.getPendingQuantity() != item.getQuantity()){
                        return false;
                    }
                }
            }

        }
        return true;
    }

    private boolean verifyQuantities(StockReturnPurchase purchaseReturn) {
        // TODO: 04/02/2021 We have to cheeck if the returnn quantity is valid, check against quantities in purchase, live stock, and other purchase returns
        return true;
    }

    private boolean verifyQuantities(StockReturnSales salesReturn) {
        // TODO: 20/01/2021  We have to check if the return is valid, check against quantities in sales, and in other sales returns
        return true;
    }

    private List<Integer> getCustomerIdsFromQuery(Map<String,String> map, String header){
        Response r = this.postSecuredRequest(AppConstants.SEARCH_CUSTOMER_IDS, map, header);
        if(r.getStatus() == 200){
            Map<String,Object> newMap = r.readEntity(Map.class);
            return (ArrayList<Integer>) newMap.get("customerIds");
        }
        return new ArrayList<>();
    }

    private List<Integer> getSupplierIdsFromQuery(Map<String,String> map, String header){
        Response r = this.postSecuredRequest(AppConstants.SEARCH_SUPPLIER_IDS, map, header);
        if(r.getStatus() == 200){
            Map<String,Object> newMap = r.readEntity(Map.class);
            return (ArrayList<Integer>) newMap.get("supplierIds");
        }
        return new ArrayList<>();
    }


    public <T> Response getSecuredRequest(String link, String header) {
        Invocation.Builder b = ClientBuilder.newClient().target(link).request();
        return b.header(HttpHeaders.AUTHORIZATION, header).get();
    }


    public <T> Response postSecuredRequest(String link, T t, String authHeader) {
        Invocation.Builder b = ClientBuilder.newClient().target(link).request();
        b.header(HttpHeaders.AUTHORIZATION, authHeader);
        return b.post(Entity.entity(t, "application/json"));
    }

    public <T> Response putSecuredRequest(String link, T t, String authHeader) {
        Invocation.Builder b = ClientBuilder.newClient().target(link).request();
        b.header(HttpHeaders.AUTHORIZATION, authHeader);
        return b.put(Entity.entity(t, "application/json"));
    }

}
