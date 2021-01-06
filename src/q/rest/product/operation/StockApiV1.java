package q.rest.product.operation;

import q.rest.product.dao.DAO;
import q.rest.product.filter.annotation.SubscriberJwt;
import q.rest.product.helper.Helper;
import q.rest.product.model.entity.Stock;
import q.rest.product.model.qstock.*;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
        return Response.status(200).build();
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
        return Response.status(200).build();
    }

    private void updateStock(StockPurchase po) {
        for (var item : po.getItems()) {
            List<StockLive> lives = dao.getCondition(StockLive.class, "stockProductId", item.getStockProduct().getId());
            if (lives.isEmpty())
                createNewStockLive(po.getBranchId(), item, item.getUnitPrice());
            else
                updateExistingStockLive(po.getBranchId(), lives, item);
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

    private void updateExistingStockLive(int branchId, List<StockLive> lives, StockPurchaseItem item) {
        double averageCost = Helper.calculateAveragePrice(lives,item);
        updateAveragePrice(lives, averageCost);
        List<StockLive> branchLive = dao.getTwoConditions(StockLive.class, "stockProductId", "branchId", item.getStockProduct().getId(), branchId);
        if (branchLive.isEmpty()) {
            createNewStockLive(branchId, item, averageCost);
        } else {
            branchLive.get(0).setQuantity(item.getQuantity() + branchLive.get(0).getQuantity());
            dao.update(branchLive.get(0));
        }
    }


    private void updateAveragePrice(List<StockLive> lives, double averageCost) {
        for (var live : lives) {
            live.setAveragedCost(Helper.round(averageCost));
            live.setLastUpdated(new Date());
            dao.update(live);
        }
    }

    private void createNewStockLive(int branchId, StockPurchaseItem item, double averageCost) {
        StockLive sl = new StockLive();
        sl.setBranchId(branchId);
        sl.setQuantity(item.getQuantity());
        sl.setLastUpdated(new Date());
        sl.setStockProductId(item.getStockProduct().getId());
        sl.setAveragedCost(Helper.round(averageCost));
        dao.persist(sl);
    }

}
