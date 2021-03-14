package q.rest.product.operation;

import q.rest.product.dao.DaoApi;
import q.rest.product.filter.annotation.SubscriberJwt;
import q.rest.product.helper.AppConstants;
import q.rest.product.helper.Helper;
import q.rest.product.model.entity.v3.product.Brand;
import q.rest.product.model.qstock.*;
import q.rest.product.model.qstock.views.StockProductView;
import q.rest.product.model.qstock.views.StockPurchaseSummary;
import q.rest.product.model.qstock.views.StockSalesSummary;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@Path("/api/v4/stock/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class StockProductV2 {

    @EJB
    private DaoApi daoApi;

    //must create view
    @SubscriberJwt
    @POST
    @Path("search-product")
    public Response searchProduct(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, Map<String, String> map) {
        List<StockProductView> products = daoApi.searchProduct(map.get("query"), Helper.getCompanyFromJWT(header));
        return Response.status(200).entity(products).build();
    }


    @SubscriberJwt
    @GET
    @Path("product/{id}")
    public Response findProductById(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, @PathParam(value = "id") long id){
        int companyId = Helper.getCompanyFromJWT(header);
        var product = daoApi.findProduct(id, companyId);
        return Response.status(200).entity(product).build();
    }

    @SubscriberJwt
    @POST
    @Path("find-product")
    public Response findProduct(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, Map<String, Object> map) {
        String partNumber = (String) map.get("productNumber");
        int brandId = (int) map.get("brandId");
        int companyId = Helper.getCompanyFromJWT(header);

        StockProductView product = daoApi.findStockProductView(companyId, partNumber, brandId);
        if(product == null) {
            return Response.status(200).build();
        }

        if(product.getPolicyId() != null)
            return Response.status(409).build();

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
        if (!daoApi.isBrandAvailable(brand.getName(), brand.getNameAr()))
            return Response.status(409).build();
        daoApi.createBrand(brand);
        return Response.status(201).build();
    }

    @SubscriberJwt
    @GET
    @Path("brands")
    public Response getAllBrand(@HeaderParam(HttpHeaders.AUTHORIZATION) String header) {
        int companyId = Helper.getCompanyFromJWT(header);
        List<Brand> brands = daoApi.getBrands(companyId);
        return Response.status(200).entity(brands).build();
    }


    @SubscriberJwt
    @POST
    @Path("search-brand")
    public Response searchBrand(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, Map<String, String> map) {
        int companyId = Helper.getCompanyFromJWT(header);
        String name = map.get("query");
        List<Brand> brands = daoApi.searchBrands(companyId, name);
        return Response.status(200).entity(brands).build();
    }


    //TEEST
    //500
    @SubscriberJwt
    @POST
    @Path("product")
    public Response createProduct(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, StockCreateProduct scp) {
        int companyId = Helper.getCompanyFromJWT(header);
        var productView = daoApi.findStockProductView(companyId, scp.getProductNumber(), scp.getBrandId());
        long productId = 0;
        if (productView == null) {
            StockProduct stockProduct = daoApi.createStockProduct(scp.getProductNumber(), scp.getBrandId(), scp.getName(), scp.getNameAr(), companyId);
            productId = stockProduct.getId();
        }
        else {
            productId = productView.getProductId();
            var check = daoApi.getStockProductSetting(productId, companyId);
            if (!check.isEmpty())
                return Response.status(409).entity("product already added").build();
        }
        StockProductSetting companyProduct = daoApi.createStockProductSetting(scp, productId, companyId);
        return Response.status(200).entity(companyProduct).build();
    }

    //not needed?
    @SubscriberJwt
    @GET
    @Path("product/number/{number}/brand/{brand}")
    public Response findCompanyProduct(@PathParam(value = "number") String number, @PathParam(value = "brand") int brandId) {
        String productNumber = Helper.undecorate(number);
        StockProduct sp = daoApi.findProduct(productNumber, brandId);
        if (sp == null) return Response.status(404).entity("product not found").build();
        return Response.status(200).entity(sp).build();
    }


    @SubscriberJwt
    @POST
    @Path("price-policy")
    public Response createPricePolicy(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, StockPricePolicy policy) {
        int companyId = Helper.getCompanyFromJWT(header);
        policy.setCompanyId(companyId);
        daoApi.createNewPolicy(policy);
        return Response.status(200).build();
    }

    @SubscriberJwt
    @GET
    @Path("price-policies")
    public Response getPolicies(@HeaderParam(HttpHeaders.AUTHORIZATION) String header){
        int companyId = Helper.getCompanyFromJWT(header);
        List<StockPricePolicy> policies = daoApi.getPolicies(companyId);
        return Response.status(200).entity(policies).build();

    }


    @SubscriberJwt
    @POST
    @Path("purchase")
    public Response createPurchaseOrder(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, StockPurchase po) {
        po.setCompanyId(Helper.getCompanyFromJWT(header));
        po.setCreated(new Date());
        po.setPaymentMethod(po.getTransactionType() == 'C' ? po.getPaymentMethod() : null);
        int purchaseID = daoApi.createPurchase(po);
        po.setId(purchaseID);
        if (po.getTransactionType() == 'T') daoApi.createPurchaseCredit(po);
        updateStock(po);
        return Response.status(200).build();
    }

    @SubscriberJwt
    @POST
    @Path("sales")
    public Response createSales(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, StockSales sales) {
        sales.setCompanyId(Helper.getCompanyFromJWT(header));
        sales.setCreated(new Date());
        sales.setPaymentMethod(sales.getTransactionType() == 'C' ? sales.getPaymentMethod() : null);
        for (StockSalesItem item : sales.getItems()) {
            StockLive live = daoApi.findBranchStockLive(sales.getCompanyId(), item.getStockProduct().getId(), sales.getBranchId());
            item.setUnitCost(live.getAverageCost());
            item.setLive(live);
        }
        if (!verifyQuantities(sales)) return Response.status(400).build();
        int salesId = daoApi.createSales(sales);
        sales.setId(salesId);
        if (sales.getTransactionType() == 'T') daoApi.createSalesCredit(sales);

        updateStock(sales);
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("id", sales.getId());
        return Response.status(200).entity(map).build();
    }


    private void updateStock(StockPurchase po) {
        for (var item : po.getItems()) {
            List<StockLive> lives = daoApi.getProductLiveStock(po.getCompanyId(), item.getStockProduct().getId());
            if (lives.isEmpty())
                daoApi.createNewStockLive(po.getCompanyId(), po.getBranchId(), item.getStockProduct().getId(), item.getUnitPrice(), item.getQuantity());
            else
                daoApi.updateExistingStockLive(po.getCompanyId(), po.getBranchId(), lives, item.getStockProduct().getId(), item.getQuantity(), item.getUnitPrice());
        }
    }


    private void updateStock(StockSales sales) {
        for (var item : sales.getItems()) {
            StockLive live = item.getLive();
            live.setQuantity(live.getQuantity() - item.getQuantity());
            if (item.getQuantity() == 0)
                daoApi.deleteLive(live);
            else
                daoApi.updateLive(live);
        }
    }

    @SubscriberJwt
    @POST
    @Path("search-sales")
    public Response searchSales(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, Map<String, String> map) {
        String query = map.get("query");
        List<StockSales> sales = daoApi.searchSales(query, Helper.getCompanyFromJWT(header));
        attachCustomerObject(sales, header);
        return Response.status(200).entity(sales).build();
    }


    @SubscriberJwt
    @POST
    @Path("search-quotation")
    public Response searchQuotation(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, Map<String, String> map) {
        String query = map.get("query");
        List<StockQuotation> quotations = daoApi.searchQuotation(query, Helper.getCompanyFromJWT(header));
        return Response.status(200).entity(quotations).build();
    }


    @SubscriberJwt
    @POST
    @Path("search-purchase")
    public Response searchPurchase(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, Map<String, String> map) {
        String query = map.get("query");
        List<StockPurchase> purchases = daoApi.searchPurchase(query, Helper.getCompanyFromJWT(header));
        attachSupplierObject(purchases, header);
        return Response.status(200).entity(purchases).build();
    }


    @SubscriberJwt
    @GET
    @Path("sales-credit-balance")
    public Response getSalesCreditBalance(@HeaderParam(HttpHeaders.AUTHORIZATION) String header) {
        int companyId = Helper.getCompanyFromJWT(header);
        List<Map<String, Object>> creditBalance = daoApi.getSalesCreditBalance(companyId, header);
        return Response.status(200).entity(creditBalance).build();
    }


    @SubscriberJwt
    @GET
    @Path("purchase-credit-balance")
    public Response getPurchaseCreditBalance(@HeaderParam(HttpHeaders.AUTHORIZATION) String header) {
        int companyId = Helper.getCompanyFromJWT(header);
        List<Map<String, Object>> creditBalance = daoApi.getPurchaseCreditBalance(companyId, header);
        return Response.status(200).entity(creditBalance).build();
    }


    @SubscriberJwt
    @GET
    @Path("branches-sales/{date}")
    public Response getAllBranchSales(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, @PathParam(value = "date") long dateLong) {
        int companyId = Helper.getCompanyFromJWT(header);
        List<Map<String, Object>> list = daoApi.getBranchSales(companyId, dateLong);
        return Response.status(200).entity(list).build();
    }


    @SubscriberJwt
    @GET
    @Path("sales/{id}")
    public Response getSales(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, @PathParam(value = "id") int id) {
        StockSales sales = daoApi.findSales(id, Helper.getCompanyFromJWT(header));
        this.attachCustomerObject(sales, header);
        return Response.status(200).entity(sales).build();
    }


    @SubscriberJwt
    @GET
    @Path("purchase/{id}")
    public Response getPurchase(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, @PathParam(value = "id") int id) {
        StockPurchase purchase = daoApi.findPurchase(id, Helper.getCompanyFromJWT(header));
        this.attachSupplierObject(purchase, header);
        return Response.status(200).entity(purchase).build();
    }


    @SubscriberJwt
    @GET
    @Path("quotation/{id}")
    public Response getQuotation(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, @PathParam(value = "id") int id) {
        StockQuotation quotation = daoApi.findQuotation(id, Helper.getCompanyFromJWT(header));
        this.attachCustomerObject(quotation, header);
        return Response.status(200).entity(quotation).build();
    }


    @SubscriberJwt
    @GET
    @Path("sales-return/{id}")
    public Response getSalesReturn(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, @PathParam(value = "id") int id) {
        int companyId = Helper.getCompanyFromJWT(header);
        StockReturnSalesStandAlone salesReturn = daoApi.getSalesReturn(id, companyId);
        StockSales sales = daoApi.findSales(salesReturn.getSalesId(), companyId);
        this.attachCustomerObject(sales, header);
        salesReturn.setCustomer(sales.getCustomer());
        salesReturn.setTaxRate(sales.getTaxRate());
        salesReturn.setCustomerId(sales.getCustomerId());
        return Response.status(200).entity(salesReturn).build();
    }


    @SubscriberJwt
    @GET
    @Path("sales-report/{year}/{month}")
    public Response getDailySales2(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, @PathParam(value = "year") int year, @PathParam(value = "month") int month) {
        Date from = Helper.getFromDate(month, year);
        Date to = Helper.getToDate(month, year);//month = 1 - 12
        int companyId = Helper.getCompanyFromJWT(header);

        List<StockSalesSummary> summaries = daoApi.getDailySalesSummary(from, to, companyId);
        List<Map<String, Object>> topCustomers = daoApi.getTopCustomers(from, to, companyId, header);
        List<Map<String, Object>> topBrands = daoApi.getTopBrands(from, to, companyId, 'S');
        Map<String, Object> map = new HashMap<>();
        map.put("daysSummary", summaries);
        map.put("topCustomers", topCustomers);
        map.put("topBrands", topBrands);
        return Response.status(200).entity(map).build();
    }


    @SubscriberJwt
    @GET
    @Path("products-report/from/{from}/to/{to}")
    public Response getProductsReport(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, @PathParam(value = "from") long fromLong, @PathParam(value = "to") long toLong) {
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
        return Response.status(200).entity(map).build();
    }


    @SubscriberJwt
    @GET
    @Path("purchase-report/{year}/{month}")
    public Response getDailyPurchase(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, @PathParam(value = "year") int year, @PathParam(value = "month") int month) {
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
        return Response.status(200).entity(map).build();
    }

    @SubscriberJwt
    @GET
    @Path("branches-sales-summary")
    public Response getAllBranchSales2(@HeaderParam(HttpHeaders.AUTHORIZATION) String header) {
        int companyId = Helper.getCompanyFromJWT(header);
        List<Integer> branchIds = getBranchIds(header);
        List<BranchSales> branchSales = daoApi.getLiveBranchSales(branchIds, companyId);
        return Response.status(200).entity(branchSales).build();
    }


    @SubscriberJwt
    @GET
    @Path("daily-sales/from/{from}/to/{to}")
    public Response getDailySales(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, @PathParam(value = "from") long fromLong, @PathParam(value = "to") long toLong) {
        int companyId = Helper.getCompanyFromJWT(header);
        List<Map<String, Object>> dailySales = daoApi.getDailySales(companyId, fromLong, toLong);
        return Response.status(200).entity(dailySales).build();
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
        int companyId = Helper.getCompanyFromJWT(header);
        List<Map<String, Object>> monthlySales = daoApi.getMonthlySales(companyId, year, month, length);
        return Response.status(200).entity(monthlySales).build();
    }


    @SubscriberJwt
    @GET
    @Path("stock-value")
    public Response getStockValue(@HeaderParam(HttpHeaders.AUTHORIZATION) String header) {
        int companyId = Helper.getCompanyFromJWT(header);
        double total = daoApi.getStockValue(companyId);
        Map<String, Object> map = new HashMap<>();
        map.put("stockValue", total);
        return Response.status(200).entity(map).build();
    }


    @SubscriberJwt
    @POST
    @Path("credit-payment/{type}")
    public Response createCreditPayment(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, @PathParam(value = "type") String typePath, Map<String, Object> map) {
        int companyId = Helper.getCompanyFromJWT(header);
        String type;
        if (typePath.equals("purchase")) type = "purchase";
        else if (typePath.equals("sales")) type = "sales";
        else return Response.status(404).entity(map).build();

        String reference = (String) map.get("reference");
        int contactId = (int) (type.equals("purchase") ? map.get("supplierId") : map.get("customerId"));
        double amount = ((Number) map.get("amount")).doubleValue();
        String paymentMethod = (String) map.get("paymentMethod");

        if (type.equals("purchase")) {
            //check if amount is valid
            daoApi.createPurchaseCreditPayment(amount, reference, paymentMethod.charAt(0), contactId, companyId);
        } else {
            //check if amount is valid
            daoApi.createSalesCreditPayment(amount, reference, paymentMethod.charAt(0), contactId, companyId);
        }
        return Response.status(200).build();
    }


    @SubscriberJwt
    @POST
    @Path("purchase-return")
    public Response createPurchaseReturn(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, StockReturnPurchase purchaseReturn) {
        int companyId = Helper.getCompanyFromJWT(header);
        StockPurchase purchase = daoApi.findPurchase(purchaseReturn.getPurchaseId(), companyId);
        if (purchase.getCompanyId() != Helper.getCompanyFromJWT(header))
            return Response.status(400).entity("Invalid purchase order").build();

        purchaseReturn.setCreated(new Date());
        purchaseReturn.setPaymentMethod(purchaseReturn.getTransactionType() == 'C' ? purchaseReturn.getPaymentMethod() : null);

        if (!verifyQuantities(purchaseReturn)) return Response.status(400).build();
        //unit average cost
        for (var item : purchaseReturn.getItems()) {
            List<StockLive> lives = daoApi.getStockLive(companyId, item.getPurchaseItem().getStockProduct().getId());
            item.setUnitAverageCost(lives.get(0).getAverageCost());
        }
        int purchaseReturnId = daoApi.createPurchaseReturn(purchaseReturn);
        purchaseReturn.setPurchaseId(purchaseReturnId);

        if (purchaseReturn.getTransactionType() == 'T') {
            daoApi.createPurchaseReturnCredit(purchaseReturn, purchase);
        }
        updateStock(companyId, purchaseReturn);
        return Response.status(200).build();

    }


    @SubscriberJwt
    @POST
    @Path("sales-return")
    public Response createSalesReturn(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, StockReturnSales salesReturn) {
        //make sure that the sales belongs to the caller
        int companyId = Helper.getCompanyFromJWT(header);
        StockSales sales = daoApi.findSales(salesReturn.getSalesId(), companyId);
        if (sales.getCompanyId() != Helper.getCompanyFromJWT(header))
            return Response.status(400).entity("Invalid sales order").build();

        salesReturn.setCreated(new Date());
        salesReturn.setPaymentMethod(salesReturn.getTransactionType() == 'C' ? salesReturn.getPaymentMethod() : null);
        if (!verifyQuantities(salesReturn)) return Response.status(400).build();
        int salesReturnId = daoApi.createSalesReturn(salesReturn);
        salesReturn.setId(salesReturnId);

        if (salesReturn.getTransactionType() == 'T') {
            daoApi.createSalesReturnCredit(salesReturn, sales);
        }
        updateStock(companyId, salesReturn);
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("id", salesReturn.getId());
        return Response.status(200).entity(map).build();
    }


    @SubscriberJwt
    @POST
    @Path("quotation")
    public Response createQuotation(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, StockQuotation quotation) {
        quotation.setCompanyId(Helper.getCompanyFromJWT(header));
        quotation.setCreated(new Date());
        quotation.setPaymentMethod(quotation.getTransactionType() == 'C' ? quotation.getPaymentMethod() : null);
        int quotationId = daoApi.createQuotation(quotation);
        quotation.setId(quotationId);
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("id", quotation.getId());
        return Response.status(200).entity(map).build();
    }


    private void updateStock(int companyId, StockReturnSales salesReturn) {
        for (var item : salesReturn.getItems()) {
            List<StockLive> lives = daoApi.getStockLive(companyId, item.getSalesItem().getStockProduct().getId());
            if (lives.isEmpty())
                daoApi.createNewStockLive(companyId, salesReturn.getBranchId(), item.getSalesItem().getStockProduct().getId(), item.getSalesItem().getUnitCost(), item.getQuantity());
            else
                daoApi.updateExistingStockLive(companyId, salesReturn.getBranchId(), lives, item.getSalesItem().getStockProduct().getId(), item.getQuantity(), item.getSalesItem().getUnitCost());
        }
    }


    private void updateStock(int companyId, StockReturnPurchase purchaseReturn) {
        for (var item : purchaseReturn.getItems()) {
            StockLive live = daoApi.findBranchStockLive(companyId, item.getPurchaseItem().getStockProduct().getId(), purchaseReturn.getBranchId());
            if (live != null) {
                live.setQuantity(live.getQuantity() - item.getQuantity());
                if(live.getQuantity() == 0)
                    daoApi.deleteLive(live);
                else
                    daoApi.updateLive(live);
            }
        }
    }

    private boolean verifyQuantities(StockSales sales) {
        for (var item : sales.getItems()) {
            if (item.getLive().getQuantity() < item.getQuantity()) {
                return false;
            }
        }
        return true;
    }


    private void attachCustomerObject(List<StockSales> sales, String header) {
        StringBuilder ids = new StringBuilder("0");
        for (var s : sales) {
            ids.append(",").append(s.getCustomerId());
        }
        Response r = this.getSecuredRequest(AppConstants.getCustomers(ids.toString()), header);
        if (r.getStatus() == 200) {
            List<Map> list = r.readEntity(new GenericType<List<Map>>() {
            });
            for (var s : sales) {
                s.attachCustomer(list);
            }
        }
    }


    private void attachCustomerObject(StockSales sales, String header) {
        Response r = this.getSecuredRequest(AppConstants.getCustomer(sales.getCustomerId()), header);
        if (r.getStatus() == 200) {
            Map<String, Object> map = r.readEntity(new GenericType<Map>() {
            });
            sales.attachCustomer(map);
        }
    }

    private void attachCustomerObject(StockQuotation quotation, String header) {
        Response r = this.getSecuredRequest(AppConstants.getCustomer(quotation.getCustomerId()), header);
        if (r.getStatus() == 200) {
            Map<String, Object> map = r.readEntity(new GenericType<Map>() {
            });
            quotation.attachCustomer(map);
        }
    }

    private void attachSupplierObject(StockPurchase purchase, String header) {
        Response r = this.getSecuredRequest(AppConstants.getSupplier(purchase.getSupplierId()), header);
        if (r.getStatus() == 200) {
            Map<String, Object> map = r.readEntity(new GenericType<Map>() {
            });
            purchase.attachSupplier(map);
        }
    }


    private void attachSupplierObject(List<StockPurchase> purchases, String header) {
        StringBuilder ids = new StringBuilder("0");
        for (var s : purchases) {
            ids.append(",").append(s.getSupplierId());
        }
        Response r = this.getSecuredRequest(AppConstants.getSuppliers(ids.toString()), header);

        if (r.getStatus() == 200) {
            List<Map> list = r.readEntity(new GenericType<List<Map>>() {
            });
            for (var s : purchases) {
                s.attachSupplier(list);
            }
        }
    }


    private boolean verifyQuantities(StockReturnPurchase purchaseReturn) {
        // TODO: 04/02/2021 We have to cheeck if the returnn quantity is valid, check against quantities in purchase, live stock, and other purchase returns
        return true;
    }

    private boolean verifyQuantities(StockReturnSales salesReturn) {
        // TODO: 20/01/2021  We have to check if the return is valid, check against quantities in sales, and in other sales returns
        return true;
    }


    public <T> Response getSecuredRequest(String link, String header) {
        Invocation.Builder b = ClientBuilder.newClient().target(link).request();
        return b.header(HttpHeaders.AUTHORIZATION, header).get();
    }

}
