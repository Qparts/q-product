package q.rest.product.operation;

import q.rest.product.dao.DAO;
import q.rest.product.filter.annotation.SubscriberJwt;
import q.rest.product.helper.AppConstants;
import q.rest.product.helper.Helper;
import q.rest.product.model.entity.Stock;
import q.rest.product.model.qstock.*;

import javax.annotation.PostConstruct;
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

@Path("/api/v4/stock/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class StockApiV1 {

    @EJB
    private DAO dao;

    @EJB
    private AsyncProductApi async;

    @SubscriberJwt
    @POST
    @Path("brand")
    public Response createBrand(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, StockBrand brand) {
        brand.setName(brand.getName().trim());
        brand.setCompanyId(Helper.getCompanyFromJWT(header));
        String sql = "select b from StockBrand b where b.companyId = :value0 and (lower(b.name) = lower(:value1) or lower(b.nameAr) = lower(:value1))";
        List<StockBrand> check = dao.getJPQLParams(StockBrand.class, sql, brand.getCompanyId(), brand.getName().trim());
        if (!check.isEmpty())
            return Response.status(409).build();
        if (brand.getNameAr() == null)
            brand.setNameAr(brand.getName());
        dao.persist(brand);
        return Response.status(201).build();
    }

    @SubscriberJwt
    @GET
    @Path("brands")
    public Response createBrand(@HeaderParam(HttpHeaders.AUTHORIZATION) String header) {
        int companyId = Helper.getCompanyFromJWT(header);
        String sql = "select b from StockBrand b where b.companyId = :value0 order by id";
        List<StockBrand> brands = dao.getJPQLParams(StockBrand.class, sql, companyId);
        return Response.status(200).entity(brands).build();
    }

    @SubscriberJwt
    @POST
    @Path("search-brand")
    public Response searchBrand(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, Map<String, String> map) {
        String nameLike = "%" + map.get("query").toLowerCase() + "%";
        int id = Helper.convertToInteger(map.get("query"));
        String sql = "select b from StockBrand b where b.companyId = :value0 and (b.id =:value1 or lower(b.name) like :value2 or lower(b.nameAr) like :value2)";
        List<StockBrand> customers = dao.getJPQLParams(StockBrand.class, sql, Helper.getCompanyFromJWT(header), id, nameLike);
        return Response.status(200).entity(customers).build();
    }

    @SubscriberJwt
    @POST
    @Path("product")
    public Response createProduct(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, StockProduct stockProduct) {
        stockProduct.setCompanyId(Helper.getCompanyFromJWT(header));
        stockProduct.setCreated(new Date());
        stockProduct.setProductNumber(Helper.undecorate(stockProduct.getProductNumber()));
        String sql = "select b from StockProduct b where b.companyId = :value0 and b.productNumber = :value1 and b.brand.id = :value2";
        List<StockProduct> check = dao.getJPQLParams(StockProduct.class, sql, stockProduct.getCompanyId(), stockProduct.getProductNumber(), stockProduct.getBrandId());
        if (!check.isEmpty()) return Response.status(409).build();
        dao.persist(stockProduct);
        return Response.ok().build();
    }


    @SubscriberJwt
    @POST
    @Path("search-product")
    public Response searchProduct(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, Map<String, String> map) {
        String nameLike = "%" + map.get("query").toLowerCase() + "%";
        String numberLike = "%" + Helper.undecorate(map.get("query")) + "%";
        long id = Helper.convertToLong(map.get("query"));

        String sql = "select b from StockProduct b where b.companyId = :value0 and " +
                "(b.id =:value1 " +
                "or lower(b.name) like :value2 " +
                "or b.productNumber like :value3)";
        List<StockBrand> customers = dao.getJPQLParams(StockBrand.class, sql, Helper.getCompanyFromJWT(header), id, nameLike, numberLike);
        return Response.status(200).entity(customers).build();
    }

    @SubscriberJwt
    @POST
    @Path("purchase")
    public Response createPurchaseOrder(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, StockPurchase po) {
        po.setCompanyId(Helper.getCompanyFromJWT(header));
        po.setCreated(new Date());
        po.setPaymentMethod(po.getTransactionType() == 'C' ? po.getPaymentMethod() : null);
        dao.persist(po);
        if (po.getTransactionType() == 'T') {
            StockPurchaseCredit credit = new StockPurchaseCredit();
            credit.setAmount(po.getTotalAmount());
            credit.setCreditDate(new Date());
            credit.setPurchaseOrderId(po.getId());
            dao.persist(credit);
        }
        updateStock(po);
        return Response.status(200).build();
    }

    @SubscriberJwt
    @POST
    @Path("search-sales")
    public Response searchSales(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, Map<String, String> map) {
        String nameLike = "%" + map.get("query").toLowerCase() + "%";
        int id = Helper.convertToInteger(map.get("query"));
        String sql = "select b from StockSales b where b.companyId = :value0 and (" +
                " b.id = :value1" +
                " or b.customerId = :value1" +
                " or lower(b.reference) like :value2)";
        List<StockSales> sales = dao.getJPQLParams(StockSales.class, sql, Helper.getCompanyFromJWT(header), id, nameLike);
        attachCustomerObject(sales, header);
        return Response.status(200).entity(sales).build();
    }

    @SubscriberJwt
    @GET
    @Path("sales/{id}")
    public Response getSales(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, @PathParam(value = "id") int id){
        StockSales sales = dao.findTwoConditions(StockSales.class, "id", "companyId", id, Helper.getCompanyFromJWT(header) );
        this.attachCustomerObject(sales, header);
        return Response.status(200).entity(sales).build();
    }


    @SubscriberJwt
    @GET
    @Path("quotation/{id}")
    public Response getQuotation(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, @PathParam(value = "id") int id){
        StockQuotation quotation = dao.findTwoConditions(StockQuotation.class, "id" , "companyId", id, Helper.getCompanyFromJWT(header));
        this.attachCustomerObject(quotation, header);
        return Response.status(200).entity(quotation).build();
    }


    @SubscriberJwt
    @GET
    @Path("sales-return/{id}")
    public Response getSalesReturn(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, @PathParam(value = "id") int id){
        String sql = "select b from StockReturnSalesStandAlone b where b.id = :value0  and b.salesId in (select c.id from StockSales c where c.companyId = :value1)";
        StockReturnSalesStandAlone salesReturn = dao.findJPQLParams(StockReturnSalesStandAlone.class, sql , id, Helper.getCompanyFromJWT(header));
        StockSales sales = dao.find(StockSales.class, salesReturn.getSalesId());
        this.attachCustomerObject(sales, header);
        salesReturn.setCustomer(sales.getCustomer());
        salesReturn.setTaxRate(sales.getTaxRate());
        salesReturn.setCustomerId(sales.getCustomerId());
        return Response.status(200).entity(salesReturn).build();
    }

    @SubscriberJwt
    @GET
    @Path("daily-sales/from/{from}/to/{to}")
    public Response getDailySales(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, @PathParam(value = "from") long fromLong, @PathParam(value = "to") long toLong){
        Helper h = new Helper();
        int companyId = Helper.getCompanyFromJWT(header);
        List<Date> dates = h.getAllDatesBetween2(new Date(fromLong), new Date(toLong));
        List<Map> dailySales = new ArrayList<>();
        for (Date date : dates) {
            String sql = "select sum(i.unit_price * i.quantity + i.unit_price * i.quantity * s.tax_rate + s.delivery_charge) as total from prd_stk_sales_order_item i join prd_stk_sales_order s on i.sales_order_id = s.id " +
                    " where s.company_id = " +companyId +
                    " and cast(s.created as date ) = '" + h.getDateFormat(date, "yyyy-MM-dd") + "'" +
                    " group by cast(s.created as date)";
            Object o = dao.getNativeSingle(sql);
            double total = o == null ? 0 : ((Number)o).doubleValue();
            Map<String, Object> map = new HashMap<>();
            map.put("total", total);
            map.put("date", date);
            dailySales.add(map);
        }
        return Response.status(200).entity(dailySales).build();
    }

    private void attachCustomerObject(List<StockSales> sales, String header){
        StringBuilder ids = new StringBuilder("0");
        for(var s : sales) {
            ids.append(",").append(s.getCustomerId());
        }
        Response r = this.getSecuredRequest(AppConstants.getCustomers(ids.toString()), header);
        if(r.getStatus() == 200){
            List<Map> list = r.readEntity(new GenericType<List<Map>>(){});
            for(var s : sales ){
                s.attachCustomer(list);
            }
        }
    }

    private void attachSupplierObject(List<StockPurchase> purchases, String header){
        System.out.println("attaching supplier object");
        StringBuilder ids = new StringBuilder("0");
        for(var s : purchases) {
            ids.append(",").append(s.getSupplierId());
        }
        Response r = this.getSecuredRequest(AppConstants.getSuppliers(ids.toString()), header);

        if(r.getStatus() == 200){
            List<Map> list = r.readEntity(new GenericType<List<Map>>(){});
            for(var s : purchases ){
                s.attachSupplier(list);
            }
        }
    }


    private void attachCustomerObject(StockSales sales, String header){
        Response r = this.getSecuredRequest(AppConstants.getCustomer(sales.getCustomerId()), header);
        if(r.getStatus() == 200){
            Map<String,Object> map = r.readEntity(new GenericType<Map>(){});
            sales.attachCustomer(map);
        }
    }

    private void attachCustomerObject(StockQuotation quotation, String header){
        Response r = this.getSecuredRequest(AppConstants.getCustomer(quotation.getCustomerId()), header);
        if(r.getStatus() == 200){
            Map<String,Object> map = r.readEntity(new GenericType<Map>(){});
            quotation.attachCustomer(map);
        }
    }

    private void attachSupplierObject(StockPurchase purchase, String header){
        Response r = this.getSecuredRequest(AppConstants.getSupplier(purchase.getSupplierId()), header);
        if(r.getStatus() == 200){
            Map<String,Object> map = r.readEntity(new GenericType<Map>(){});
            purchase.attachSupplier(map);
        }
    }


    @SubscriberJwt
    @GET
    @Path("purchase/{id}")
    public Response getPurchase(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, @PathParam(value = "id") int id){
        StockPurchase purchase = dao.findTwoConditions(StockPurchase.class, "id", "companyId", id, Helper.getCompanyFromJWT(header));
        this.attachSupplierObject(purchase, header);
        return Response.status(200).entity(purchase).build();
    }


    @SubscriberJwt
    @POST
    @Path("search-purchase")
    public Response searchPurchase(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, Map<String, String> map) {
        String nameLike = "%" + map.get("query").toLowerCase() + "%";
        int id = Helper.convertToInteger(map.get("query"));
        String sql = "select b from StockPurchase b where b.companyId = :value0 and (" +
                " b.id = :value1" +
                " or b.supplierId = :value1" +
                " or lower(b.reference) like :value2)";
        List<StockPurchase> purchases = dao.getJPQLParams(StockPurchase.class, sql, Helper.getCompanyFromJWT(header), id, nameLike);
        attachSupplierObject(purchases, header);
        return Response.status(200).entity(purchases).build();
    }


    @SubscriberJwt
    @POST
    @Path("search-quotation")
    public Response searchQuotation(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, Map<String, String> map) {
        String nameLike = "%" + map.get("query").toLowerCase() + "%";
        int id = Helper.convertToInteger(map.get("query"));
        String sql = "select b from StockQuotation b where b.companyId = :value0 and (" +
                " b.id = :value1" +
                " or b.customerId = :value1" +
                " or lower(b.reference) like :value2)";
        List<StockQuotation> quotations = dao.getJPQLParams(StockQuotation.class, sql, Helper.getCompanyFromJWT(header), id, nameLike);
        return Response.status(200).entity(quotations).build();
    }







    @SubscriberJwt
    @POST
    @Path("purchase-return")
    public Response createPurchaseReturn(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, StockReturnPurchase purchaseReturn){
        StockPurchase purchase = dao.find(StockPurchase.class, purchaseReturn.getPurchaseId());
        if (purchase.getCompanyId() != Helper.getCompanyFromJWT(header)) return Response.status(400).entity("Invalid purchase order").build();

        purchaseReturn.setCreated(new Date());
        purchaseReturn.setPaymentMethod(purchaseReturn.getTransactionType() == 'C' ? purchaseReturn.getPaymentMethod() : null);
        if(!verifyQuantities(purchaseReturn)) return Response.status(400).build();
        dao.persist(purchaseReturn);
        if(purchaseReturn.getTransactionType() == 'T'){
            StockPurchaseCredit credit = new StockPurchaseCredit();
            credit.setAmount(purchaseReturn.getTotalAmount(purchase.getTaxRate()) * -1);
            credit.setCreditDate(new Date());
            credit.setPurchaseOrderId(purchaseReturn.getId());
            dao.persist(credit);
        }
        updateStock(purchaseReturn);
        return Response.status(200).build();

    }

    @SubscriberJwt
    @POST
    @Path("sales-return")
    public Response createSalesReturn(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, StockReturnSales salesReturn){
        //make sure that the sales belongs to the caller
        StockSales sales = dao.find(StockSales.class, salesReturn.getSalesId());
        if (sales.getCompanyId() != Helper.getCompanyFromJWT(header)) return Response.status(400).entity("Invalid sales order").build();

        salesReturn.setCreated(new Date());
        salesReturn.setPaymentMethod(salesReturn.getTransactionType() == 'C' ? salesReturn.getPaymentMethod() : null);
        if(!verifyQuantities(salesReturn)) return Response.status(400).build();
        dao.persist(salesReturn);
        if(salesReturn.getTransactionType() == 'T'){
            StockSalesCredit credit = new StockSalesCredit();
            credit.setAmount(salesReturn.getTotalAmount(sales.getTaxRate()) * -1);
            credit.setCreditDate(new Date());
            credit.setSalesOrderId(salesReturn.getId());
            dao.persist(credit);
        }
        updateStock(salesReturn);
        Map<String, Integer> map = new HashMap<String,Integer>();
        map.put("id", salesReturn.getId());
        return Response.status(200).entity(map).build();
    }

    @SubscriberJwt
    @POST
    @Path("sales")
    public Response createSales(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, StockSales sales){
        sales.setCompanyId(Helper.getCompanyFromJWT(header));
        sales.setCreated(new Date());
        sales.setPaymentMethod(sales.getTransactionType() == 'C' ? sales.getPaymentMethod() : null);
        for(StockSalesItem item : sales.getItems()){
            StockLive live = dao.findTwoConditions(StockLive.class, "stockProductId", "branchId",item.getStockProduct().getId(), sales.getBranchId());
            item.setUnitCost(live.getAveragedCost());
            item.setLive(live);
        }
        if(!verifyQuantities(sales)) return Response.status(400).build();
        dao.persist(sales);
        if(sales.getTransactionType() == 'T'){
            StockSalesCredit credit = new StockSalesCredit();
            credit.setAmount(sales.getTotalAmount());
            credit.setCreditDate(new Date());
            credit.setSalesOrderId(sales.getId());
            dao.persist(credit);
        }
        updateStock(sales);
        Map<String,Integer> map = new HashMap<String,Integer>();
        map.put("id", sales.getId());
        return Response.status(200).entity(map).build();
    }

    @SubscriberJwt
    @POST
    @Path("quotation")
    public Response createQuotation(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, StockQuotation quotation ){
        quotation.setCompanyId(Helper.getCompanyFromJWT(header));
        quotation.setCreated(new Date());
        quotation.setPaymentMethod(quotation.getTransactionType() == 'C' ? quotation.getPaymentMethod() : null);
        dao.persist(quotation);
        if(quotation.getQuotationPrice() > 0 && quotation.getTransactionType() == 'T') {
            StockQuotationCredit credit = new StockQuotationCredit();
            credit.setAmount(quotation.getQuotationPrice() + quotation.getQuotationPrice() * quotation.getTaxRate());
            credit.setCreditDate(new Date());
            credit.setQuotationOrderId(quotation.getId());
            dao.persist(quotation);
        }
        Map<String,Integer> map = new HashMap<String,Integer>();
        map.put("id", quotation.getId());
        return Response.status(200).entity(map).build();
    }

    private void updateStock(StockReturnSales salesReturn) {
        for (var item : salesReturn.getItems()) {
            List<StockLive> lives = dao.getCondition(StockLive.class, "stockProductId", item.getSalesItem().getStockProduct().getId());
            if (lives.isEmpty())
                createNewStockLive(salesReturn.getBranchId(), item.getSalesItem().getStockProduct().getId(), item.getSalesItem().getUnitCost(), item.getQuantity());
            else
                updateExistingStockLive(salesReturn.getBranchId(), lives, item.getSalesItem().getStockProduct().getId(), item.getQuantity(), item.getSalesItem().getUnitCost());
        }
    }

    private void updateStock(StockReturnPurchase purchaseReturn) {
        for (var item : purchaseReturn.getItems()) {
            List<StockLive> lives = dao.getTwoConditions(StockLive.class, "stockProductId", "branchId", item.getPurchaseItem().getStockProduct().getId(), purchaseReturn.getId());
            if(!lives.isEmpty()){
                StockLive live = lives.get(0);
                live.setQuantity(live.getQuantity() - item.getQuantity());
                live.setLastUpdated(new Date());
                dao.update(live);
            }
        }
    }

    private void updateStock(StockPurchase po) {
        for (var item : po.getItems()) {
            List<StockLive> lives = dao.getCondition(StockLive.class, "stockProductId", item.getStockProduct().getId());
            if (lives.isEmpty())
                createNewStockLive(po.getBranchId(), item.getStockProduct().getId(), item.getUnitPrice(), item.getQuantity());
            else
                updateExistingStockLive(po.getBranchId(), lives, item.getStockProduct().getId(), item.getQuantity(), item.getUnitPrice());
        }
    }

    private void updateStock(StockSales sales) {
        for (var item : sales.getItems()) {
            StockLive live = item.getLive();
            live.setQuantity(live.getQuantity() - item.getQuantity());
            live.setLastUpdated(new Date());
            if(item.getQuantity() == 0)
                dao.delete(live);
            else
                dao.update(live);
        }
    }

    private boolean verifyQuantities(StockSales sales){
        for (var item : sales.getItems()) {
            if(item.getLive().getQuantity() < item.getQuantity()){
                return false;
            }
        }
        return true;
    }

    private boolean verifyQuantities(StockReturnSales salesReturn){
        // TODO: 20/01/2021  We have to check if the return is valid, check against quantities in sales, and in other sales returns



        return true;
    }


    private boolean verifyQuantities(StockReturnPurchase purchaseReturn){
        // TODO: 04/02/2021 We have to cheeck if the returnn quantity is valid, check against quantities in purchase, live stock, and other purchase returns
        return true;
    }


    private void updateExistingStockLive(int branchId, List<StockLive> lives, long stockProductId,  int quantity, double unitCost) {
        double averageCost = Helper.calculateAveragePrice(lives, unitCost, quantity);
        updateAveragePrice(lives, averageCost);
        List<StockLive> branchLive = dao.getTwoConditions(StockLive.class, "stockProductId", "branchId", stockProductId, branchId);
        if (branchLive.isEmpty()) {
            createNewStockLive(branchId, stockProductId, averageCost, quantity);
        } else {
            branchLive.get(0).setQuantity(quantity + branchLive.get(0).getQuantity());
            dao.update(branchLive.get(0));
        }
    }

    private void createNewStockLive(int branchId, long stockProductId, double averageCost, int quantity){
        StockLive sl = new StockLive();
        sl.setBranchId(branchId);
        sl.setQuantity(quantity);
        sl.setLastUpdated(new Date());
        sl.setStockProductId(stockProductId);
        sl.setAveragedCost(Helper.round(averageCost));
        dao.persist(sl);
    }

    private void updateAveragePrice(List<StockLive> lives, double averageCost) {
        for (var live : lives) {
            live.setAveragedCost(Helper.round(averageCost));
            live.setLastUpdated(new Date());
            dao.update(live);
        }
    }


    public <T> Response postSecuredRequest(String link, T t, String header) {
        Invocation.Builder b = ClientBuilder.newClient().target(link).request();
        b.header(HttpHeaders.AUTHORIZATION, header);
        return b.post(Entity.entity(t, "application/json"));
    }


    public <T> Response getSecuredRequest(String link, String header) {
        Invocation.Builder b = ClientBuilder.newClient().target(link).request();
        return b.header(HttpHeaders.AUTHORIZATION, header).get();
    }

}
