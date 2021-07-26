package q.rest.product.dao;

import org.jboss.logging.Logger;
import q.rest.product.helper.AppConstants;
import q.rest.product.helper.Attacher;
import q.rest.product.helper.Helper;
import q.rest.product.model.product.full.Brand;
import q.rest.product.model.product.full.BrandClass;
import q.rest.product.model.qstock.*;
import q.rest.product.model.qstock.views.StockProductView;
import q.rest.product.model.qstock.views.StockPurchaseSummary;
import q.rest.product.model.qstock.views.StockSalesSummary;
import q.rest.product.operation.StockProductV2;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.time.Year;
import java.time.YearMonth;
import java.util.*;

@Stateless
public class DaoApi {

    @EJB
    private DAO dao;
    private Helper helper = new Helper();
    private static final Logger logger = Logger.getLogger(StockProductV2.class);


    public boolean isProductInLiveStock(long productId) {
        List<StockLive> lives = dao.getCondition(StockLive.class, "stockProductId", productId);
        return lives.isEmpty();
    }


    public List<StockLive> getProductLiveStock(int companyId, long productId) {
        return dao.getTwoConditions(StockLive.class, "productId", "companyId", productId, companyId);
    }


    public boolean isBrandAvailable(int classId, String name, String nameAr) {
        String sql = "select b from Brand b where b.classId =:value0 and ((lower(b.name) = lower(:value1) or lower(b.nameAr) = lower(:value2)))";
        List<StockBrand> check = dao.getJPQLParams(StockBrand.class, sql, classId, name, nameAr);
        return check.isEmpty();
    }
//
//    public boolean isStockProductAvailable(String productNumber, int brandId) {
//        String sql = "select b from StockProduct b where b.productNumber =:value0 and b.brandId = :value1";
//        List<StockProduct> products = dao.getJPQLParams(StockProduct.class, sql, productNumber, brandId);
//        return products.isEmpty();
//    }


    public List<StockProductSetting> getStockProductSetting(long productId, int companyId) {
        String sql = "select b from StockProductSetting b where b.companyId =:value0 and b.productId =:value1";
        return dao.getJPQLParams(StockProductSetting.class, sql, companyId, productId);
    }

    public StockProduct createStockProduct(String productNumber, int brandId, String name, String nameAr ,double referencePrice, int companyId) {
        StockProduct product = new StockProduct();
        product.setProductNumber(Helper.undecorate(productNumber));
        product.setCreated(new Date());
        product.setBrandId(brandId);
        product.setName(name);
        product.setNameAr(nameAr);
        product.setStatus('P');
        product.setCreatedBy(companyId);
        product.setReferencePrice(referencePrice);
        //manual insert
        return dao.persistAndReturn(product);
    }

    public StockProductSetting createStockProductSetting(StockCreateProduct create, long productId, int companyId) {
        StockProductSetting scp = new StockProductSetting();
        scp.setProductId(productId);
        scp.setCompanyId(companyId);
        scp.setPolicyId(create.getPolicyId());
        scp.setShortageFlag(scp.getShortageFlag());
        scp.setNotes(scp.getNotes());
        dao.persist(scp);

        for (var kv : create.getShelves()) {
            StockProductShelf shelf = new StockProductShelf();
            shelf.setBranchId(kv.getBranchId());
            shelf.setShelf(kv.getShelfLocation());
            shelf.setProductId(productId);
            shelf.setCompanyId(companyId);
            dao.persist(shelf);
        }
        return scp;
    }

    public void createBrand(Brand brand) {
        brand.setCreated(new Date());
        brand.setStatus('P');
        dao.persist(brand);
    }

    public List<Brand> getBrands(int companyId) {
        String sql = "select b from Brand b where b.status = 'A' or (b.status = 'P' and b.createdBy = :value0) order by b.name";
        return dao.getJPQLParams(Brand.class, sql, companyId);
    }

    public List<BrandClass> getBrandClasses(){
        return dao.getOrderBy(BrandClass.class, "id");
    }

    public List<StockProductView> searchProduct(String query, int companyId) {
        String numberLike = "'" + Helper.undecorate(query) + "%'";
        String sql = "select * from (" +
                " select *, row_number() over (PARTITION BY product_id order by company_id desc) as n" +
                " from prd_view_stock_product" +
                " where company_id in (0,"+ companyId +")) z where n < 2 " +
                "and ((z.status = 'P' and z.created_by_company = " + companyId + ") or z.status = 'A')" +
                " and z.product_number like "+ numberLike;

        logger.info("products sql : "+sql);

        List<StockProductView> views = dao.getNative(StockProductView.class, sql);

        attachLiveStock(views, companyId);
        attachShelves(views, companyId);
        return views;
    }

    private void attachLiveStock(StockProductView productView, int companyId) {
        if (productView != null) {
            String sql = "select b from StockLive b where b.productId =:value0 and b.companyId = :value1";
            List<StockLive> lives = dao.getJPQLParams(StockLive.class, sql, productView.getProductId(), companyId);
            productView.setLiveStock(lives);
        }
    }

    private void attachShelves(StockProductView productView, int companyId){
        if(productView != null) {
            String sql = "select b from StockProductShelf b where b.productId =:value0 and b.companyId =:value1";
            List<StockProductShelf> shelves = dao.getJPQLParams(StockProductShelf.class, sql, productView.getProductId(), companyId);
            productView.setShelves(shelves);
        }
    }

    private void attachShelves(List<StockProductView> views, int companyId){
        for (var productView : views) {
            attachShelves(productView, companyId);
        }
    }

    private void attachLiveStock(List<StockProductView> views, int companyId) {
        for (var productView : views) {
            attachLiveStock(productView, companyId);
        }
    }

    public StockProductView findStockProductView(int companyId, String productNumber, int brandId) {
        String undecorated = "'" + Helper.undecorate(productNumber) + "'";

        String sql = "select * from (" +
                " select *, row_number() over (PARTITION BY product_id order by company_id desc) as n" +
                " from prd_view_stock_product" +
                " where company_id in (0,"+ companyId +")) z where n < 2 " +
                "and ((z.status = 'P' and z.created_by_company = " + companyId + ") or z.status = 'A')" +
                " and z.product_number = "+ undecorated + "and z.brand_id = " + brandId;
        List<StockProductView> views = dao.getNative(StockProductView.class, sql);
        if(!views.isEmpty()){
            attachLiveStock(views.get(0), companyId);
            attachShelves(views.get(0), companyId);
            return views.get(0);
        }
        else return null;
    }

    public StockProduct findProduct(String productNumber, int brandId) {
        String undecorated = Helper.undecorate(productNumber);
        String sql = "select b from StockProduct b where b.productNumber =:value0 and b.brandId = :value1";
        return dao.findJPQLParams(StockProduct.class, sql, undecorated, brandId);
    }

    public StockProductView findProduct(long productId, int companyId) {

        String sql = "select * from (" +
                " select *, row_number() over (PARTITION BY product_id order by company_id desc) as n" +
                " from prd_view_stock_product" +
                " where company_id in (0,"+ companyId +")) z where n < 2 " +
                " and ((z.status = 'P' and z.created_by_company = " + companyId + ") or z.status = 'A')" +
                " and z.product_id = "+ productId;
        List<StockProductView> views = dao.getNative(StockProductView.class, sql);
        if(!views.isEmpty()){
            attachLiveStock(views.get(0), companyId);
            attachShelves(views.get(0), companyId);
            return views.get(0);
        }
        else return null;
    }



    public List<Map<String, Object>> getLatestSalesOrders(long productId, int companyId, String header){
        List<Map<String, Object>> sales = getLatestOrders(productId, companyId, "sales");
        Attacher.attachCustomersMap(sales, header);
        return sales;
    }

    public List<Map<String, Object>> getLatestPurchaseOrders(long productId,  int companyId, String header){
        List<Map<String, Object>> purchases = getLatestOrders(productId,  companyId, "purchase");
        Attacher.attachSuppliersMap(purchases, header);
        return purchases;
    }

    private List<Map<String, Object>> getLatestOrders(long productId, int companyId, String table){
        int limit = 5;
        String beneficiary = table.equals("purchase") ? "supplier" : "customer";
        String sql = "select ord.id as order_id, " +
                "  item.quantity as quantity, " +
                "  item.unit_price as price, " +
                beneficiary+"_id as "+beneficiary+"_id, " +
                " branch_id as branch_id " +
                " from prd_stk_"+ table +"_order_item item left join prd_stk_" + table +"_order ord  on item."+ table +"_order_id= ord.id " +
                " where item.stock_product_id = " + productId +
                " and ord.company_id = " + companyId +
                " order by ord.created desc limit  " + limit;
        List<Object> result = dao.getNative(sql);
        List<Map<String, Object>> list = new ArrayList<>();
        for (Object obj : result) {
            Object[] row = (Object[]) obj;
            Map<String, Object> map = new HashMap<String, Object>();
            int orderId = ((Number) row[0]).intValue();
            int quantity = ((Number) row[1]).intValue();
            double price = ((Number) row[2]).doubleValue();
            int benefeciaryId = ((Number) row[3]).intValue();
            int branchId = ((Number) row[4]).intValue();
            map.put("orderId", orderId);
            map.put("quantity", quantity);
            map.put("price", price);
            map.put(beneficiary+"Id", benefeciaryId);
            map.put("branchId", branchId);
            list.add(map);
        }
        return list;
    }

    public List<Map<String, Object>> getProductYearSales(long productId, int companyId){
        return getProductYearGeneric(productId, companyId, "sales");
    }

    public List<Map<String, Object>> getProductYearPurchase(long productId,  int companyId){
        return getProductYearGeneric(productId,  companyId, "purchase");
    }

    private List<Map<String, Object>> getProductYearGeneric(long productId,  int companyId, String table){
        String sql = " select w.date," +
                "       to_char(w.date, 'Mon') as mon," +
                "       extract(year from w.date) as year," +
                "       coalesce(x.sum,0) as total" +
                " from (" +
                " select date (date_trunc('month', d)) as date " +
                "    from generate_series(" +
                "        current_date - interval '11 months'," +
                "        current_date," +
                "        '1 month'" +
                ") d) w " +
                " left join (" +
                "    select date(date_trunc('month', ord.created)) as date, " +
                "       sum(item.quantity)" +
                " from prd_stk_"+ table +"_order_item item join prd_stk_"+ table +"_order ord  on item."+ table +"_order_id = ord.id" +
                " where stock_product_id = " + productId +
                " and company_id = " + companyId +
                " group by date_trunc('month', ord.created)) x on w.date = x.date";
        List<Object> result = dao.getNative(sql);
        List<Map<String, Object>> list = new ArrayList<>();
        for (Object obj : result) {
            Object[] row = (Object[]) obj;
            Map<String, Object> map = new HashMap<String, Object>();
            Date date = (Date) row[0];
            String monthName = (String) row[1];
            int year = ((Double) row[2]).intValue();
            int total = ((Number) row[3]).intValue();
            map.put("date", date);
            map.put("month", monthName);
            map.put("year", year);
            map.put("total", total);
            list.add(map);
        }
        return list;
    }


    public List<Brand> searchBrands(int companyId, String name) {
        name = "%" + name.toLowerCase() + "%";
        String sql = "select b from Brand b where " +
                " (b.status = 'A' or (b.status = 'P' and b.createdBy =:value0)) and " +
                " lower(b.name) like :value1 or lower(b.nameAr) like :value1";
        return dao.getJPQLParams(Brand.class, sql, companyId, name);
    }


    public List<StockPurchaseView> searchPurchase(String query, int companyId) {
        String nameLike = "%" + query + "%";
        int id = Helper.convertToInteger(query);
        String sql = "select b from StockPurchaseView b where b.companyId = :value0 and (" +
                " b.id = :value1" +
                " or b.supplierId = :value1" +
                " or lower(b.reference) like :value2)";
        var purchases = dao.getJPQLParams(StockPurchaseView.class, sql, companyId, id, nameLike);
        for (var pur : purchases) {
            for (var item : pur.getItems()) {
                var view = this.findProduct(item.getStockProductId(), companyId);
                item.setStockProduct(view);
            }
        }
        return purchases;
    }



    public void createNewPolicy(StockPricePolicy policy) {
        dao.persist(policy);
    }

    public List<StockPricePolicy> getPolicies(int companyId) {
        String sql = "select b from StockPricePolicy b where b.companyId = :value0 order by b.id ";
        return dao.getJPQLParams(StockPricePolicy.class, sql, companyId);
    }

    public int createPurchase(StockPurchase purchase) {
        dao.persist(purchase);
        return purchase.getId();
    }

    public int createSales(StockSales sales) {
        dao.persist(sales);
        return sales.getId();
    }

    public int createQuotation(StockQuotation quotation) {
        dao.persist(quotation);
        return quotation.getId();
    }


    public int createPurchaseReturn(StockReturnPurchase purchaseReturn) {
        dao.persist(purchaseReturn);
        return purchaseReturn.getId();
    }


    public int createSalesReturn(StockReturnSales salesReturn) {
        dao.persist(salesReturn);
        return salesReturn.getId();
    }

    public StockLive findBranchStockLive(int companyId, long productId, int branchId) {
        String sql = "select b from StockLive b where b.companyId =:value0" +
                " and b.productId =:value1 " +
                " and b.branchId =:value2";
        return dao.findJPQLParams(StockLive.class, sql, companyId, productId, branchId);
    }


    public List<StockLive> getStockLive(int companyId, long productId) {
        return dao.getTwoConditions(StockLive.class, "companyId", "productId", companyId, productId);
    }

    public void createPurchaseCredit(StockPurchase purchase) {
        StockPurchaseCredit credit = new StockPurchaseCredit();
        credit.setAmount(purchase.getTotalAmount());
        credit.setCreditDate(new Date());
        credit.setSupplierId(purchase.getSupplierId());
        credit.setSource('P');
        credit.setCompanyId(purchase.getCompanyId());
        credit.setPurchaseOrderId(purchase.getId());
        dao.persist(credit);
    }


    public void createPurchaseReturnCredit(StockReturnPurchase purchaseReturn, StockPurchase purchase) {
        StockPurchaseCredit credit = new StockPurchaseCredit();
        credit.setAmount(purchaseReturn.getTotalAmount(purchase.getTaxRate()) * -1);
        credit.setCreditDate(new Date());
        credit.setCompanyId(purchase.getCompanyId());
        credit.setSource('R');
        credit.setSupplierId(purchase.getSupplierId());
        credit.setPurchaseOrderId(0);
        credit.setPurchaseReturnId(purchaseReturn.getId());
        dao.persist(credit);
    }


    public void createSalesReturnCredit(StockReturnSales salesReturn, StockSales sales) {
        StockSalesCredit credit = new StockSalesCredit();
        credit.setAmount(salesReturn.getTotalAmount(sales.getTaxRate()) * -1);
        credit.setCompanyId(sales.getCompanyId());
        credit.setCustomerId(sales.getCustomerId());
        credit.setSource('R');
        credit.setCreditDate(new Date());
        credit.setSalesOrderId(0);
        credit.setSalesReturnId(salesReturn.getId());
        dao.persist(credit);
    }

    public void createSalesCredit(StockSales sales) {
        StockSalesCredit credit = new StockSalesCredit();
        credit.setAmount(sales.getTotalAmount());
        credit.setCreditDate(new Date());
        credit.setSalesOrderId(sales.getId());
        credit.setCustomerId(sales.getCustomerId());
        credit.setSource('S');//sales
        credit.setCompanyId(sales.getCompanyId());
        dao.persist(credit);
    }

    public void deleteLive(StockLive live) {
        dao.delete(live);
    }

    public void updateLive(StockLive live) {
        dao.update(live);
    }


    public void updateSalesItem(StockSalesItemView view) {
        dao.update(view);
    }



    public void createNewStockLive(int companyId, int branchId, long productId, double averageCost, int quantity) {
        StockLive sl = new StockLive();
        sl.setBranchId(branchId);
        sl.setQuantity(quantity);
        sl.setCompanyId(companyId);
        sl.setProductId(productId);
        sl.setAverageCost(Helper.round(averageCost));
        dao.persist(sl);
    }


    public void updateExistingStockLive(int companyId, int branchId, List<StockLive> lives, long productId, int quantity, double unitCost) {
        double averageCost = Helper.calculateAveragePrice(lives, unitCost, quantity);
        updateAveragePrice(lives, averageCost);
        String sql = "select b from StockLive b where b.productId =:value0 and b.branchId =:value1 and b.companyId =:value2";
        StockLive branchLive = dao.findJPQLParams(StockLive.class, sql, productId, branchId, companyId);
        if (branchLive == null) {
            createNewStockLive(companyId, branchId, productId, averageCost, quantity);
        } else {
            branchLive.setQuantity(quantity + branchLive.getQuantity());
            dao.update(branchLive);
        }
    }


    private void updateAveragePrice(List<StockLive> lives, double averageCost) {
        for (var live : lives) {
            live.setAverageCost(Helper.round(averageCost));
            dao.update(live);
        }
    }

    public StockReturnSalesStandAlone getSalesReturn(int salesReturnId, int companyId) {
        String sql = "select b from StockReturnSalesStandAlone b where b.id = :value0 and b.salesId in (select c.id from StockSales c where c.companyId = :value1)";
        return dao.findJPQLParams(StockReturnSalesStandAlone.class, sql, salesReturnId, companyId);
    }

    public StockSales findSales(int salesId, int companyId) {
        return dao.findTwoConditions(StockSales.class, "id", "companyId", salesId, companyId);
    }


    public StockSalesView findSales2(int purchaseId, int companyId) {
        StockSalesView sales = dao.findTwoConditions(StockSalesView.class, "id", "companyId", purchaseId, companyId);
        if (sales != null) {
            for (var item : sales.getItems()) {
                var view = this.findProduct(item.getStockProductId(), companyId);
                item.setStockProduct(view);
            }
        }
        return sales;
    }

    public List<StockSalesItemView> getPendingItems(int companyId ){
        String sql = "select b from StockSalesItemView b where b.pendingQuantity > 0 and b.salesOrderId in (select c.id from StockSalesView c where c.companyId = :value0) order by b.id";
        List<StockSalesItemView> items = dao.getJPQLParams(StockSalesItemView.class, sql, companyId);
        for(var item : items){
            var stockProductView = this.findProduct(item.getStockProductId(), companyId);
            item.setStockProduct(stockProductView);
        }
        return items;
    }

    public StockSalesItemView getPendingItem(int companyId, int salesItemId){
        String sql = "select b from StockSalesItemView b where b.pendingQuantity > 0 and b.salesOrderId in (select c.id from StockSalesView c where c.companyId = :value0) and b.id =:value1 order by b.id";
        StockSalesItemView item = dao.findJPQLParams(StockSalesItemView.class, sql, companyId, salesItemId);
        if(item != null) {
            StockProductView stockProductView = this.findProduct(item.getStockProductId(), companyId);
            item.setStockProduct(stockProductView);
            return item;
        }
        return null;
    }

    public List<StockSalesView> searchSales(String query, int companyId) {
        String nameLike = "%" + query + "%";
        int id = Helper.convertToInteger(query);
        String sql = "select b from StockSalesView b where b.companyId = :value0 and (" +
                " b.id = :value1" +
                " or b.customerId = :value1" +
                " or lower(b.reference) like :value2)";
        List<StockSalesView> sales = dao.getJPQLParams(StockSalesView.class, sql, companyId, id, nameLike);
        for (var ss : sales) {
            for (var item : ss.getItems()) {
                var view = this.findProduct(item.getStockProductId(), companyId);
                item.setStockProduct(view);
            }
        }
        return sales;
    }

    public List<StockQuotationView> searchQuotation(String query, int companyId) {
        String nameLike = "%" + query + "%";
        int id = Helper.convertToInteger(query);
        String sql = "select b from StockQuotationView b where b.companyId = :value0 and (" +
                " b.id = :value1" +
                " or b.customerId = :value1" +
                " or lower(b.reference) like :value2)";
        List<StockQuotationView> quotations = dao.getJPQLParams(StockQuotationView.class, sql, companyId, id, nameLike);
        for (var ss : quotations) {
            for (var item : ss.getItems()) {
                var view = this.findProduct(item.getStockProductId(), companyId);
                item.setStockProduct(view);
            }
        }
        return quotations;
    }


    public StockPurchase findPurchase(int purchaseId, int companyId) {
        return dao.findTwoConditions(StockPurchase.class, "id", "companyId", purchaseId, companyId);
    }

    public StockPurchaseView findPurchase2(int purchaseId, int companyId) {
        StockPurchaseView purchase = dao.findTwoConditions(StockPurchaseView.class, "id", "companyId", purchaseId, companyId);
        if (purchase != null) {
            for (var item : purchase.getItems()) {
                var view = this.findProduct(item.getStockProductId(), companyId);
                item.setStockProduct(view);
            }
        }
        return purchase;
    }

    public StockQuotation findQuotation(int quotationId, int companyId) {
        return dao.findTwoConditions(StockQuotation.class, "id", "companyId", quotationId, companyId);
    }

    public StockQuotationView findQuotation2(int purchaseId, int companyId) {
        StockQuotationView q = dao.findTwoConditions(StockQuotationView.class, "id", "companyId", purchaseId, companyId);
        if (q != null) {
            for (var item : q.getItems()) {
                var view = this.findProduct(item.getStockProductId(), companyId);
                item.setStockProduct(view);
            }
        }
        return q;
    }


    public List<Map<String, Object>> getBranchSales(int companyId, long dateLong) {
        String date = "'" + helper.getDateFormat(new Date(dateLong), "YYYY-MM-dd") + "'";
        String sql = "select sal.branch_id as branch_id, ret.branch_id as branch_id_2, total_sales, total_returned from " +
                "    (select s.branch_id, " +
                "       sum((i.unit_price * i.quantity + s.delivery_charge) + (i.unit_price * i.quantity + s.delivery_charge) * s.tax_rate) as total_sales " +
                " from prd_stk_sales_order_item i join prd_stk_sales_order s on i.sales_order_id = s.id " +
                " where s.company_id = " + companyId +
                "  and cast(s.created as date ) = " + date +
                " group by s.branch_id) sal" +
                "        full join" +
                " (select s.branch_id, sum ((si.unit_price * sri.quantity + r.delivery_charge) +  (si.unit_price * sri.quantity + r.delivery_charge) * s.tax_rate) as total_returned" +
                " from prd_stk_sales_return_item sri" +
                "    join prd_stk_sales_return r on sri.sales_return_id = r.id" +
                "    join prd_stk_sales_order s on r.sales_id = s.id" +
                "    join prd_stk_sales_order_item si on sri.sales_item_id = si.id" +
                " where s.company_id = " + companyId +
                " and cast(r.created as date ) = " + date +
                " group by s.branch_id) ret" +
                " on ret.branch_id = sal.branch_id";
        List<Object> result = dao.getNative(sql);
        List<Map<String, Object>> list = new ArrayList<>();
        for (Object obj : result) {
            Object[] row = (Object[]) obj;
            Map<String, Object> map = new HashMap<String, Object>();
            Object branchId = row[0] != null ? row[0] : row[1];
            double totalSales = row[2] != null ? ((Number) row[2]).doubleValue() : 0;
            double totalReturned = row[3] != null ? ((Number) row[3]).doubleValue() : 0;
            map.put("branchId", branchId);
            map.put("sales", totalSales);
            map.put("returned", totalReturned);
            list.add(map);
        }
        return list;
    }

    public List<Map<String, Object>> getMonthlySales(int companyId, int year, int month, int length) {
        List<YearMonth> yms = Helper.getAllPreviousMonths(year, month, length);
        List<Map<String, Object>> monthlySales = new ArrayList<>();
        for (YearMonth ym : yms) {
            String sql = " select sum(i.unit_price * i.quantity + i.unit_price * i.quantity * s.tax_rate + s.delivery_charge) as total from prd_stk_sales_order_item i join prd_stk_sales_order s on i.sales_order_id = s.id " +
                    " where s.company_id = " + companyId +
                    " and to_char(s.created, 'YYYY-MM') = '" + ym + "'";
            Object o = dao.getNativeSingle(sql);
            double total = o == null ? 0 : ((Number) o).doubleValue();
            Map<String, Object> map = new HashMap<>();
            map.put("total", total);
            map.put("yearMonth", ym.toString());
            map.put("year", ym.getYear());
            map.put("month", ym.getMonth());
            map.put("monthNumber", ym.getMonthValue());
            monthlySales.add(map);
        }
        return monthlySales;
    }

    public List<Map<String, Object>> getDailySales(int companyId, long fromLong, long toLong) {
        List<Date> dates = helper.getAllDatesBetween2(new Date(fromLong), new Date(toLong));
        List<Map<String, Object>> dailySales = new ArrayList<>();
        for (Date date : dates) {
            String sql = "select sum((i.unit_price * i.quantity + s.delivery_charge) + (i.unit_price * i.quantity + s.delivery_charge) * s.tax_rate) as total from prd_stk_sales_order_item i join prd_stk_sales_order s on i.sales_order_id = s.id " +
                    " where s.company_id = " + companyId +
                    " and cast(s.created as date ) = '" + helper.getDateFormat(date, "yyyy-MM-dd") + "'" +
                    " group by cast(s.created as date)";
            Object object = dao.getNativeSingle(sql);
            double totalSales = object == null ? 0 : ((Number) object).doubleValue();
            String sqlReturn = "select sum ((si.unit_price * sri.quantity + r.delivery_charge) +  (si.unit_price * sri.quantity + r.delivery_charge) * s.tax_rate) as total_returned " +
                    " from prd_stk_sales_return_item sri" +
                    "    join prd_stk_sales_return r on sri.sales_return_id = r.id" +
                    "    join prd_stk_sales_order s on r.sales_id = s.id" +
                    "    join prd_stk_sales_order_item si on sri.sales_item_id = si.id" +
                    " where s.company_id = " + companyId +
                    " and cast(r.created as date ) = '" + helper.getDateFormat(date, "yyyy-MM-dd") + "'" +
                    " group by cast(r.created as date)";
            Object o2 = dao.getNativeSingle(sqlReturn);
            double totalReturned = o2 == null ? 0 : ((Number) o2).doubleValue();
            Map<String, Object> map = new HashMap<>();
            map.put("total", totalSales - totalReturned);//to be removed
            map.put("sales", totalSales);
            map.put("returned", totalReturned);
            map.put("date", date);
            dailySales.add(map);
        }
        return dailySales;
    }

    public List<StockSalesSummary> getDailySalesSummary(Date from, Date to, int companyId) {
        List<Date> dates = helper.getAllDatesBetween2(from, to);
        List<StockSalesSummary> summaries = new ArrayList<>();
        for (Date date : dates) {
            String sql = "select b from StockSalesSummary b where b.companyId = :value0 and b.created = :value1";
            StockSalesSummary summary = dao.findJPQLParams(StockSalesSummary.class, sql, companyId, date);
            if (summary == null)
                summary = new StockSalesSummary(date, companyId);
            summaries.add(summary);
        }
        return summaries;
    }


    public List<StockPurchaseSummary> getDailyPurchaseSummary(Date from, Date to, int companyId) {
        List<Date> dates = helper.getAllDatesBetween2(from, to);
        List<StockPurchaseSummary> summaries = new ArrayList<>();
        for (Date date : dates) {
            String sql = "select b from StockPurchaseSummary b where b.companyId = :value0 and b.created = :value1";
            StockPurchaseSummary summary = dao.findJPQLParams(StockPurchaseSummary.class, sql, companyId, date);
            if (summary == null)
                summary = new StockPurchaseSummary(date, companyId);
            summaries.add(summary);
        }
        return summaries;
    }

    public List<Map<String, Object>> getPurchaseCreditBalance(int companyId, String header) {
        String sql = "select supplier_id, sum(amount) as balance from prd_stk_purchase_credit where company_id = " + companyId +
                " group by supplier_id order by balance desc";
        List<Object> result = dao.getNative(sql);
        List<Map<String, Object>> list = new ArrayList<>();
        List<Integer> supplierIds = new ArrayList<>();
        for (Object obj : result) {
            Object[] row = (Object[]) obj;
            Map<String, Object> map = new HashMap<String, Object>();
            int supplierId = ((Number) row[0]).intValue();
            double balance = ((Number) row[1]).doubleValue();
            map.put("supplierId", supplierId);
            map.put("balance", balance);
            supplierIds.add(supplierId);
            list.add(map);
        }
        try {
            List<Map<String, Object>> suppliers = getContactObjects(supplierIds, 'S', header);
            for (var supplier : suppliers) {
                int cid = (int) supplier.get("id");
                for (var top : list) {
                    if ((int) top.get("supplierId") == cid) {
                        top.put("supplier", supplier);
                    }
                }
            }
        } catch (Exception ignore) {
        }
        return list;
    }

    public List<Map<String, Object>> getTopBrandsProfitability(Date from, Date to, int companyId) {
        String sql = "select id, name, sum(sales - cost) as profit" +
                "    from prd_view_brand_profitability where company_id = " + companyId +
                "    and created between '" + helper.getDateFormat(from, "yyyy-MM-dd") + "' and '" + helper.getDateFormat(to, "yyyy-MM-dd") + "'" +
                " group by id, name order by sum(sales-cost) desc";
        List<Object> rows = dao.getNative(sql);
        List<Map<String, Object>> list = new ArrayList<>();
        for (Object rowObj : rows) {
            Object[] row = (Object[]) rowObj;
            Map<String, Object> map = new HashMap<String, Object>();
            int brandId = ((Number) row[0]).intValue();
            String brandName = (String) row[1];
            double profit = ((Number) row[2]).doubleValue();
            map.put("id", brandId);
            map.put("brandName", brandName);
            map.put("profit", profit);
            list.add(map);
        }
        return list;
    }

    public List<Map<String, Object>> getTopProductsProfitability(Date from, Date to, int companyId) {
        String sql = "select stock_product_id, sum(sales - sales_cost) as profit" +
                " from prd_view_product_profitability where company_id = " + companyId +
                " and created between '" + helper.getDateFormat(from, "yyyy-MM-dd") + "' and '" + helper.getDateFormat(to, "yyyy-MM-dd") + "'" +
                " group by stock_product_id order by sum(sales-sales_cost) desc";
        List<Object> rows = dao.getNative(sql);
        List<Map<String, Object>> list = new ArrayList<>();
        for (Object rowObj : rows) {
            Object[] row = (Object[]) rowObj;
            Map<String, Object> map = new HashMap<String, Object>();
            int productId = ((Number) row[0]).intValue();
            double profit = ((Number) row[1]).doubleValue();
            StockProduct product = dao.find(StockProduct.class, productId);
            map.put("product", product);
            map.put("profit", profit);
            list.add(map);
        }
        return list;
    }


    public List<Map<String, Object>> getTopProductsMovements(Date from, Date to, int companyId) {
        String sql = "select stock_product_id, items_sold from prd_view_product_movement_high where company_id = " + companyId +
                " and created between '" + helper.getDateFormat(from, "yyyy-MM-dd") + "' and '" + helper.getDateFormat(to, "yyyy-MM-dd") + "'" +
                " group by stock_product_id, items_sold order by items_sold desc";
        List<Object> rows = dao.getNative(sql);
        List<Map<String, Object>> list = new ArrayList<>();
        for (Object rowObj : rows) {
            Object[] row = (Object[]) rowObj;
            Map<String, Object> map = new HashMap<String, Object>();
            int productId = ((Number) row[0]).intValue();
            int itemsSold = ((Number) row[1]).intValue();
            StockProduct product = dao.find(StockProduct.class, productId);
            map.put("product", product);
            map.put("itemsSold", itemsSold);
            list.add(map);
        }
        return list;
    }

    public List<Map<String, Object>> getSalesCreditBalance(int companyId, String header) {
        String sql = "select customer_id, sum(amount) as balance from prd_stk_sales_credit where company_id = " + companyId +
                " group by customer_id order by balance desc";
        List<Object> result = dao.getNative(sql);
        List<Map<String, Object>> list = new ArrayList<>();
        List<Integer> customerIds = new ArrayList<>();
        for (Object obj : result) {
            Object[] row = (Object[]) obj;
            Map<String, Object> map = new HashMap<String, Object>();
            int customerId = ((Number) row[0]).intValue();
            double balance = ((Number) row[1]).doubleValue();
            map.put("customerId", customerId);
            map.put("balance", balance);
            customerIds.add(customerId);
            list.add(map);
        }
        try {
            List<Map<String, Object>> customers = getContactObjects(customerIds, 'C', header);
            for (var customer : customers) {
                int cid = (int) customer.get("id");
                for (var top : list) {
                    if ((int) top.get("customerId") == cid) {
                        top.put("customer", customer);
                    }
                }
            }
        } catch (Exception ignore) {
        }
        return list;
    }


    public List<Map<String, Object>> getTopCustomers(Date from, Date to, int companyId, String header) {
        String sql = "select customer_id, sum(sales + sales_tax - sales_return - sales_return_tax) as total from prd_view_sales_by_customer" +
                " where company_id = " + companyId +
                " and created between '" + helper.getDateFormat(from, "yyyy-MM-dd") + "' and '" + helper.getDateFormat(to, "yyyy-MM-dd") + "'" +
                " group by customer_id order by total desc";
        List<Object> rows = dao.getNative(sql);
        List<Map<String, Object>> topCustomers = new ArrayList<>();
        List<Integer> customerIds = new ArrayList<>();
        for (var rowObj : rows) {
            var row = (Object[]) rowObj;
            int customerId = ((Number) row[0]).intValue();
            double total = ((Number) row[1]).doubleValue();
            if (total > 0) {
                Map<String, Object> tcmap = new HashMap<>();
                tcmap.put("customerId", customerId);
                tcmap.put("sales", total);
                customerIds.add(customerId);
                topCustomers.add(tcmap);
            }
        }
        try {
            List<Map<String, Object>> customers = getContactObjects(customerIds, 'C', header);
            for (var customer : customers) {
                int cid = (int) customer.get("id");
                for (var top : topCustomers) {
                    if ((int) top.get("customerId") == cid) {
                        top.put("customer", customer);
                    }
                }
            }
        } catch (Exception ignore) {
        }
        return topCustomers;
    }


    public List<Map<String, Object>> getTopSuppliers(Date from, Date to, int companyId, String header) {
        String sql = "select supplier_id, sum(purchase + purchase_tax - purchase_return - purchase_return_tax) as total from prd_view_purchase_by_supplier" +
                " where company_id = " + companyId +
                " and created between '" + helper.getDateFormat(from, "yyyy-MM-dd") + "' and '" + helper.getDateFormat(to, "yyyy-MM-dd") + "'" +
                " group by supplier_id order by total desc";
        List<Object> rows = dao.getNative(sql);
        List<Map<String, Object>> topSuppliers = new ArrayList<>();
        List<Integer> supplierIds = new ArrayList<>();
        for (var rowObj : rows) {
            var row = (Object[]) rowObj;
            int supplierId = ((Number) row[0]).intValue();
            double total = ((Number) row[1]).doubleValue();
            if (total > 0) {
                Map<String, Object> tcmap = new HashMap<>();
                tcmap.put("supplierId", supplierId);
                tcmap.put("purchase", total);
                supplierIds.add(supplierId);
                topSuppliers.add(tcmap);
            }
        }
        try {
            List<Map<String, Object>> suppliers = getContactObjects(supplierIds, 'S', header);
            for (var supplier : suppliers) {
                int cid = (int) supplier.get("id");
                for (var top : topSuppliers) {
                    if ((int) top.get("supplierId") == cid) {
                        top.put("supplier", supplier);
                    }
                }
            }
        } catch (Exception ignore) {
        }
        return topSuppliers;
    }


    public List<Map<String, Object>> getTopBrands(Date from, Date to, int companyId, char type) {
        String select = type == 'P' ?
                "select brand_id, brand_name, sum(purchase + purchase_tax - purchase_return - purchase_return_tax) as total from prd_view_purchase_by_brand"
                :
                "select brand_id, brand_name, sum(sales + sales_tax - sales_return - sales_return_tax) as total from prd_view_sales_by_brand";
        String sql = select +
                " where company_id = " + companyId +
                " and created between '" + helper.getDateFormat(from, "yyyy-MM-dd") + "' and '" + helper.getDateFormat(to, "yyyy-MM-dd") + "'" +
                " group by brand_id, brand_name order by total desc";
        List<Object> rows = dao.getNative(sql);
        List<Map<String, Object>> topBrands = new ArrayList<>();
        for (var rowObj : rows) {
            var row = (Object[]) rowObj;
            int brandId = ((Number) row[0]).intValue();
            String brandName = (String) row[1];
            double total = ((Number) row[2]).doubleValue();
            if (total > 0) {
                Map<String, Object> map = new HashMap<>();
                map.put("brandId", brandId);
                map.put("brandName", brandName);
                map.put(type == 'P' ? "purchase" : "sales", total);
                topBrands.add(map);
            }
        }
        return topBrands;
    }


    public List<BranchSales> getLiveBranchSales(List<Integer> branches, int companyId) {
        List<BranchSales> branchSales = new ArrayList<>();
        for (var bid : branches) {
            BranchSales bs = new BranchSales();
            bs.setBranchId(bid);
            branchSales.add(bs);
        }
        applyMtd(companyId, branchSales);
        applyYtd(companyId, branchSales);
        applyDaySales(companyId, branchSales);
        return branchSales;
    }

    public double getStockValue(int companyId) {
        String sql = "select sum(quantity * average_cost) from prd_stk_live_stock where company_id = " + companyId;
        Object o = dao.getNativeSingle(sql);
        return o == null ? 0 : ((Number) o).doubleValue();
    }

    private void applyDaySales(int companyId, List<BranchSales> branchSales) {
        String date = "'" + helper.getDateFormat(new Date(), "YYYY-MM-dd") + "'";
        String sql = "select sal.branch_id as branch_id, ret.branch_id as branch_id_2, total_sales, total_returned from " +
                "    (select s.branch_id, " +
                "       sum((i.unit_price * i.quantity + s.delivery_charge) + (i.unit_price * i.quantity + s.delivery_charge) * s.tax_rate) as total_sales " +
                " from prd_stk_sales_order_item i join prd_stk_sales_order s on i.sales_order_id = s.id " +
                " where s.company_id = " + companyId +
                "  and cast(s.created as date ) = " + date +
                " group by s.branch_id) sal" +
                "        full join" +
                " (select s.branch_id, sum ((si.unit_price * sri.quantity + r.delivery_charge) +  (si.unit_price * sri.quantity + r.delivery_charge) * s.tax_rate) as total_returned" +
                " from prd_stk_sales_return_item sri" +
                "    join prd_stk_sales_return r on sri.sales_return_id = r.id" +
                "    join prd_stk_sales_order s on r.sales_id = s.id" +
                "    join prd_stk_sales_order_item si on sri.sales_item_id = si.id" +
                " where s.company_id = " + companyId +
                " and cast(r.created as date ) = " + date +
                " group by s.branch_id) ret" +
                " on ret.branch_id = sal.branch_id";
        List<Object> result = dao.getNative(sql);
        for (Object obj : result) {
            Object[] row = (Object[]) obj;
            int branchId = ((Number) (row[0] != null ? row[0] : row[1])).intValue();
            double totalSales = row[2] != null ? ((Number) row[2]).doubleValue() : 0;
            double totalReturned = row[3] != null ? ((Number) row[3]).doubleValue() : 0;
            for (BranchSales bs : branchSales) {
                if (bs.getBranchId() == branchId) {
                    bs.setDaySales(totalSales);
                    bs.setDayReturns(totalReturned);
                    break;
                }
            }
        }
    }

    private void applyMtd(int companyId, List<BranchSales> branchSales) {
        int year = Year.now().getValue();
        int month = YearMonth.now().getMonthValue();

        String monthStart = " '" + helper.getDateFormat(Helper.getFromDate(month, year), "YYYY-MM-dd") + "' ";
        String monthEnd = " '" + helper.getDateFormat(Helper.getToDate(month, year), "YYYY-MM-dd") + "' ";

        String sql = " select sal.branch_id as branch_id, ret.branch_id as branch_id_2, total_sales, total_returned from" +
                "    (" +
                " select s.branch_id," +
                "       sum((i.unit_price * i.quantity + s.delivery_charge) + (i.unit_price * i.quantity + s.delivery_charge) * s.tax_rate) as total_sales" +
                " from prd_stk_sales_order_item i join prd_stk_sales_order s on i.sales_order_id = s.id" +
                " where s.company_id = " + companyId +
                "  and cast(s.created as date ) between " + monthStart + " and " + monthEnd +
                " group by s.branch_id) sal " +
                " full join" +
                " (select s.branch_id, sum ((si.unit_price * sri.quantity + r.delivery_charge) +  (si.unit_price * sri.quantity + r.delivery_charge) * s.tax_rate) as total_returned " +
                " from prd_stk_sales_return_item sri " +
                "    join prd_stk_sales_return r on sri.sales_return_id = r.id" +
                "    join prd_stk_sales_order s on r.sales_id = s.id" +
                "    join prd_stk_sales_order_item si on sri.sales_item_id = si.id" +
                " where s.company_id = " + companyId +
                " and cast(r.created as date )  between " + monthStart + " and " + monthEnd +
                " group by s.branch_id) ret" +
                " on ret.branch_id = sal.branch_id";
        List<Object> result = dao.getNative(sql);
        for (Object obj : result) {
            Object[] row = (Object[]) obj;
            int branchId = ((Number) (row[0] != null ? row[0] : row[1])).intValue();
            double totalSales = row[2] != null ? ((Number) row[2]).doubleValue() : 0;
            double totalReturned = row[3] != null ? ((Number) row[3]).doubleValue() : 0;
            for (BranchSales bs : branchSales) {
                if (bs.getBranchId() == branchId) {
                    bs.setMtdSales(totalSales);
                    bs.setMtdReturns(totalReturned);
                    break;
                }
            }
        }
    }


    public void applyYtd(int companyId, List<BranchSales> branchSales) {
        int year = Year.now().getValue();
        String yearStart = "'" + helper.getDateFormat(Helper.getFromDate(1, year), "YYYY-MM-dd") + "' ";
        String sql = " select sal.branch_id as branch_id, ret.branch_id as branch_id_2, total_sales, total_returned from" +
                "    (" +
                " select s.branch_id," +
                "       sum((i.unit_price * i.quantity + s.delivery_charge) + (i.unit_price * i.quantity + s.delivery_charge) * s.tax_rate) as total_sales" +
                " from prd_stk_sales_order_item i join prd_stk_sales_order s on i.sales_order_id = s.id" +
                " where s.company_id = " + companyId +
                "  and cast(s.created as date ) >= " + yearStart +
                " group by s.branch_id) sal " +
                " full join" +
                " (select s.branch_id, sum ((si.unit_price * sri.quantity + r.delivery_charge) +  (si.unit_price * sri.quantity + r.delivery_charge) * s.tax_rate) as total_returned " +
                " from prd_stk_sales_return_item sri " +
                "    join prd_stk_sales_return r on sri.sales_return_id = r.id" +
                "    join prd_stk_sales_order s on r.sales_id = s.id" +
                "    join prd_stk_sales_order_item si on sri.sales_item_id = si.id" +
                " where s.company_id = " + companyId +
                " and cast(r.created as date ) >= " + yearStart +
                " group by s.branch_id) ret" +
                " on ret.branch_id = sal.branch_id";
        List<Object> result = dao.getNative(sql);
        for (Object obj : result) {
            Object[] row = (Object[]) obj;
            int branchId = ((Number) (row[0] != null ? row[0] : row[1])).intValue();
            double totalSales = row[2] != null ? ((Number) row[2]).doubleValue() : 0;
            double totalReturned = row[3] != null ? ((Number) row[3]).doubleValue() : 0;
            for (BranchSales bs : branchSales) {
                if (bs.getBranchId() == branchId) {
                    bs.setYtdSales(totalSales);
                    bs.setYtdReturns(totalReturned);
                    break;
                }
            }
        }
    }

    public void createPurchaseCreditPayment(double amount, String reference, char paymentMethod, int contactId, int companyId, Date date) {
        //check if amount is valid
        StockPurchaseCredit pc = new StockPurchaseCredit();
        pc.setSupplierId(contactId);
        pc.setSource('Y');
        pc.setPaymentMethod(paymentMethod);
        pc.setCompanyId(companyId);
        pc.setAmount(amount * -1);
        pc.setReference(reference);
        pc.setCreditDate(date);
        dao.persist(pc);
    }

    public void createSalesCreditPayment(double amount, String reference, char paymentMethod, int contactId, int companyId, Date date) {
        StockSalesCredit sc = new StockSalesCredit();
        sc.setCustomerId(contactId);
        sc.setPaymentMethod(paymentMethod);
        sc.setCompanyId(companyId);
        sc.setSource('Y');
        sc.setAmount(amount * -1);
        sc.setReference(reference);
        sc.setCreditDate(date);
        dao.persist(sc);
    }


    private List<Map<String, Object>> getContactObjects(List<Integer> contactIds, char type, String header) {
        StringBuilder ids = new StringBuilder("0");
        for (var id : contactIds) {
            ids.append(",").append(id);
        }
        String link = "";
        if (type == 'C')
            link = AppConstants.getCustomers(ids.toString());
        else if (type == 'S')
            link = AppConstants.getSuppliers(ids.toString());

        Response r = this.getSecuredRequest(link, header);
        if (r.getStatus() == 200) {
            List<Map<String, Object>> list = r.readEntity(new GenericType<List<Map<String, Object>>>() {
            });
            return list;
        }
        return new ArrayList<>();
    }

    public <T> Response getSecuredRequest(String link, String header) {
        Invocation.Builder b = ClientBuilder.newClient().target(link).request();
        return b.header(HttpHeaders.AUTHORIZATION, header).get();
    }

}
