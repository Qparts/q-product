package q.rest.product.dao;

import q.rest.product.helper.AppConstants;
import q.rest.product.helper.Helper;
import q.rest.product.model.entity.v3.product.Brand;
import q.rest.product.model.qstock.*;
import q.rest.product.model.qstock.views.StockProductView;
import q.rest.product.model.qstock.views.StockPurchaseSummary;
import q.rest.product.model.qstock.views.StockSalesSummary;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.security.Policy;
import java.time.Year;
import java.time.YearMonth;
import java.util.*;

@Stateless
public class DaoApi {

    @EJB
    private DAO dao;
    private Helper helper = new Helper();

    public boolean isProductInLiveStock(long productId) {
        List<StockLive> lives = dao.getCondition(StockLive.class, "stockProductId", productId);
        return lives.isEmpty();
    }


    public List<StockLive> getProductLiveStock(int companyId, long productId) {
        return dao.getTwoConditions(StockLive.class, "productId", "companyId", productId, companyId);
    }


    public boolean isBrandAvailable(String name, String nameAr) {
        String sql = "select b from Brand b where (lower(b.name) = lower(:value0) or lower(b.nameAr) = lower(:value0))";
        List<StockBrand> check = dao.getJPQLParams(StockBrand.class, sql, name, nameAr);
        return check.isEmpty();
    }

    public boolean isStockProductAvailable(String productNumber, int brandId) {
        String sql = "select b from StockProduct b where b.productNumber =:value0 and b.brandId = :value1";
        List<StockProduct> products = dao.getJPQLParams(StockProduct.class, sql, productNumber, brandId);
        return products.isEmpty();
    }


    public List<StockProductSetting> getStockProductSetting(long productId, int companyId) {
        String sql = "select b from StockProductSetting b where b.companyId =:value0 and b.productId =:value1";
        return dao.getJPQLParams(StockProductSetting.class, sql, companyId, productId);
    }

    public StockProduct createStockProduct(String productNumber, int brandId, String name, String nameAr, int subscriberId) {
        StockProduct product = new StockProduct();
        product.setProductNumber(productNumber);
        product.setCreated(new Date());
        product.setBrandId(brandId);
        product.setName(name);
        product.setNameAr(nameAr);
        product.setStatus('P');
        product.setCreatedBy(subscriberId);
        dao.persist(product);
        return product;
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
            dao.persist(shelf);
        }
        return scp;
    }

    public void createBrand(Brand brand) {
        brand.setCreated(new Date());
        brand.setStatus('P');
        dao.persist(brand);
    }

    public List<Brand> getBrands() {
        return dao.getOrderBy(Brand.class, "name");
    }

    public List<StockProductView> searchProduct(String query, int companyId){
        String numberLike = "%" + query + "%";
        String sql = "select b from StockProductView b where (b.status = 'P' " +
                " and b.companyId = :value0 " +
                " and b.productNumber like :value1) " +
                "or (b.status = 'A' and b.productNumber like :value1)";
        return dao.getJPQLParams(StockProductView.class, sql, companyId, numberLike);
    }


    public StockProductView findStockProduct(String productNumber, int brandId) {
        String undecorated = Helper.undecorate(productNumber);
        String sql = "select b from StockProductView b where b.productNumber =:value0 and b.brandId = :value1";
        return dao.findJPQLParams(StockProductView.class, sql, undecorated, brandId);
    }

    public List<Brand> searchBrands(String name) {
        name = "%" + name.toLowerCase() + "%";
        String sql = "select b from Brand b where lower(b.name) like :value0 or lower(b.nameAr like :value0";
        return dao.getJPQLParams(Brand.class, sql, name);
    }
    public List<StockSales> searchSales(String query, int companyId){
        String nameLike = "%" + query + "%";
        int id = Helper.convertToInteger(query);
        String sql = "select b from StockSales b where b.companyId = :value0 and (" +
                " b.id = :value1" +
                " or b.customerId = :value1" +
                " or lower(b.reference) like :value2)";
        return dao.getJPQLParams(StockSales.class, sql, companyId, id, nameLike);
    }

    public List<StockPurchase> searchPurchase(String query, int companyId){
        String nameLike = "%" + query + "%";
        int id = Helper.convertToInteger(query);
        String sql = "select b from StockPurchase b where b.companyId = :value0 and (" +
                " b.id = :value1" +
                " or b.supplierId = :value1" +
                " or lower(b.reference) like :value2)";
        return dao.getJPQLParams(StockPurchase.class, sql, companyId, id, nameLike);
    }


    public List<StockQuotation> searchQuotation(String query, int companyId){
        String nameLike = "%" + query + "%";
        int id = Helper.convertToInteger(query);
        String sql = "select b from StockQuotation b where b.companyId = :value0 and (" +
                " b.id = :value1" +
                " or b.customerId = :value1" +
                " or lower(b.reference) like :value2)";
        return dao.getJPQLParams(StockQuotation.class, sql, companyId, id, nameLike);
    }

    public void createNewPolicy(StockPricePolicy policy) {
        if (policy.isDefaultPolicy()) {
            String sql = "update prd_stk_policy set default_policy = false where company_id = " + policy.getCompanyId();
            dao.updateNative(sql);
        }
        dao.persist(policy);
    }

    public List<StockPricePolicy> getPolicies(int companyId){
        return dao.getCondition(StockPricePolicy.class, "companyId", companyId);
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
        return dao.getTwoConditions(StockLive.class, "companyId","productId", companyId, productId);
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

    public void deleteLive(StockLive live){
        dao.delete(live);
    }

    public void updateLive(StockLive live){
        dao.update(live);
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

    public StockReturnSalesStandAlone getSalesReturn(int salesReturnId, int companyId){
        String sql = "select b from StockReturnSalesStandAlone b where b.id = :value0 and b.salesId in (select c.id from StockSales c where c.companyId = :value1)";
        return dao.findJPQLParams(StockReturnSalesStandAlone.class, sql , salesReturnId, companyId);
    }

    public StockSales findSales(int salesId, int companyId){
        return dao.findTwoConditions(StockSales.class, "id", "companyId", salesId, companyId);
    }


    public StockPurchase findPurchase(int purchaseId, int companyId){
        return dao.findTwoConditions(StockPurchase.class, "id", "companyId", purchaseId, companyId);
    }

    public StockQuotation findQuotation(int quotationId, int companyId){
        return dao.findTwoConditions(StockQuotation.class, "id", "companyId", quotationId, companyId);
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
            List<Map<String, Object>> suppliers = getContactObjects(supplierIds, 'C', header);
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

    public double getStockValue(int companyId){
        String sql = "select sum(sl.quantity * sl.average_cost) from prd_stk_live_stock sl join prd_stk_product p on sl.product_id = p.id where p.company_id = " + companyId;
        Object o = dao.getNativeSingle(sql);
        return o == null ? 0 : ((Number)o).doubleValue();
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

    public void createPurchaseCreditPayment(double amount, String reference, char paymentMethod, int contactId, int companyId){
        //check if amount is valid
        StockPurchaseCredit pc = new StockPurchaseCredit();
        pc.setSupplierId(contactId);
        pc.setSource('Y');
        pc.setPaymentMethod(paymentMethod);
        pc.setCompanyId(companyId);
        pc.setAmount(amount * -1);
        pc.setReference(reference);
        pc.setCreditDate(new Date());
        dao.persist(pc);
    }

    public void createSalesCreditPayment(double amount, String reference, char paymentMethod, int contactId, int companyId){
        StockSalesCredit sc = new StockSalesCredit();
        sc.setCustomerId(contactId);
        sc.setPaymentMethod(paymentMethod);
        sc.setCompanyId(companyId);
        sc.setSource('Y');
        sc.setAmount(amount * -1);
        sc.setReference(reference);
        sc.setCreditDate(new Date());
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