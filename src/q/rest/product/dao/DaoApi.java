package q.rest.product.dao;

import q.rest.product.helper.AppConstants;
import q.rest.product.helper.Helper;
import q.rest.product.model.qstock.BranchSales;
import q.rest.product.model.qstock.StockPurchase;
import q.rest.product.model.qstock.StockSales;
import q.rest.product.model.qstock.views.StockPurchaseSummary;
import q.rest.product.model.qstock.views.StockSalesSummary;

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



    public List<Map<String,Object>> getBranchSales(int companyId, long dateLong){
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
        List<Map<String,Object>> list = new ArrayList<>();
        for(Object obj : result){
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

    public List<Map<String,Object>> getMonthlySales(int companyId, int year, int month, int length){
        List<YearMonth> yms = Helper.getAllPreviousMonths(year, month, length);
        List<Map<String,Object>> monthlySales = new ArrayList<>();
        for (YearMonth ym : yms) {
            String sql = " select sum(i.unit_price * i.quantity + i.unit_price * i.quantity * s.tax_rate + s.delivery_charge) as total from prd_stk_sales_order_item i join prd_stk_sales_order s on i.sales_order_id = s.id " +
                    " where s.company_id = " + companyId +
                    " and to_char(s.created, 'YYYY-MM') = '" + ym + "'";
            Object o = dao.getNativeSingle(sql);
            double total = o == null ? 0 : ((Number)o).doubleValue();
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

    public List<Map<String,Object>> getDailySales(int companyId, long fromLong, long toLong){
        List<Date> dates = helper.getAllDatesBetween2(new Date(fromLong), new Date(toLong));
        List<Map<String,Object>> dailySales = new ArrayList<>();
        for (Date date : dates) {
            String sql = "select sum((i.unit_price * i.quantity + s.delivery_charge) + (i.unit_price * i.quantity + s.delivery_charge) * s.tax_rate) as total from prd_stk_sales_order_item i join prd_stk_sales_order s on i.sales_order_id = s.id " +
                    " where s.company_id = " +companyId +
                    " and cast(s.created as date ) = '" + helper.getDateFormat(date, "yyyy-MM-dd") + "'" +
                    " group by cast(s.created as date)";
            Object object = dao.getNativeSingle(sql);
            double totalSales = object == null ? 0 : ((Number)object).doubleValue();
            String sqlReturn = "select sum ((si.unit_price * sri.quantity + r.delivery_charge) +  (si.unit_price * sri.quantity + r.delivery_charge) * s.tax_rate) as total_returned " +
                    " from prd_stk_sales_return_item sri" +
                    "    join prd_stk_sales_return r on sri.sales_return_id = r.id" +
                    "    join prd_stk_sales_order s on r.sales_id = s.id" +
                    "    join prd_stk_sales_order_item si on sri.sales_item_id = si.id" +
                    " where s.company_id = " + companyId +
                    " and cast(r.created as date ) = '" + helper.getDateFormat(date, "yyyy-MM-dd") + "'" +
                    " group by cast(r.created as date)";
            Object o2 = dao.getNativeSingle(sqlReturn);
            double totalReturned = o2 == null ? 0 : ((Number)o2).doubleValue();
            Map<String, Object> map = new HashMap<>();
            map.put("total", totalSales - totalReturned);//to be removed
            map.put("sales", totalSales);
            map.put("returned", totalReturned);
            map.put("date", date);
            dailySales.add(map);
        }
        return dailySales;
    }

    public List<StockSalesSummary> getDailySalesSummary(Date from, Date to, int companyId){
        List<Date> dates = helper.getAllDatesBetween2(from, to);
        List<StockSalesSummary> summaries = new ArrayList<>();
        for(Date date : dates){
            String sql = "select b from StockSalesSummary b where b.companyId = :value0 and b.created = :value1";
            StockSalesSummary summary = dao.findJPQLParams(StockSalesSummary.class, sql, companyId, date);
            if(summary == null)
                summary = new StockSalesSummary(date, companyId);
            summaries.add(summary);
        }
        return summaries;
    }




    public List<StockPurchaseSummary> getDailyPurchaseSummary(Date from, Date to, int companyId){
        List<Date> dates = helper.getAllDatesBetween2(from, to);
        List<StockPurchaseSummary> summaries = new ArrayList<>();
        for(Date date : dates){
            String sql = "select b from StockPurchaseSummary b where b.companyId = :value0 and b.created = :value1";
            StockPurchaseSummary summary = dao.findJPQLParams(StockPurchaseSummary.class, sql, companyId, date);
            if(summary == null)
                summary = new StockPurchaseSummary(date, companyId);
            summaries.add(summary);
        }
        return summaries;
    }

    public List<Map<String,Object>> getPurchaseCreditBalance(int companyId, String header){
        String sql = "select supplier_id, sum(amount) as balance from prd_stk_purchase_credit where company_id = " + companyId +
        " group by supplier_id order by balance desc";
        List<Object> result = dao.getNative(sql);
        List<Map<String,Object>> list = new ArrayList<>();
        List<Integer> supplierIds = new ArrayList<>();
        for(Object obj : result) {
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
            List<Map<String,Object>> suppliers = getContactObjects(supplierIds, 'C', header);
            for (var supplier : suppliers) {
                int cid = (int) supplier.get("id");
                for (var top : list) {
                    if ((int) top.get("supplierId") == cid) {
                        top.put("supplier", supplier);
                    }
                }
            }
        }catch (Exception ignore){}
        return list;
    }

    public List<Map<String,Object>> getSalesCreditBalance(int companyId, String header){
        String sql = "select customer_id, sum(amount) as balance from prd_stk_sales_credit where company_id = " + companyId +
                " group by customer_id order by balance desc";
        List<Object> result = dao.getNative(sql);
        List<Map<String,Object>> list = new ArrayList<>();
        List<Integer> customerIds = new ArrayList<>();
        for(Object obj : result) {
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
            List<Map<String,Object>> customers = getContactObjects(customerIds, 'C', header);
            for (var customer : customers) {
                int cid = (int) customer.get("id");
                for (var top : list) {
                    if ((int) top.get("customerId") == cid) {
                        top.put("customer", customer);
                    }
                }
            }
        }catch (Exception ignore){}
        return list;
    }

    public List<Map<String,Object>> getTopCustomers(Date from, Date to, int companyId, String header){
        String sql = "select customer_id, sum(sales + sales_tax - sales_return - sales_return_tax) as total from prd_view_sales_by_customer" +
                " where company_id = " + companyId +
                " and created between '" + helper.getDateFormat(from , "yyyy-MM-dd") + "' and '" + helper.getDateFormat(to , "yyyy-MM-dd") + "'" +
                " group by customer_id order by total desc";
        List<Object> rows = dao.getNative(sql);
        List<Map<String,Object>> topCustomers = new ArrayList<>();
        List<Integer> customerIds = new ArrayList<>();
        for(var rowObj : rows){
            var row = (Object[]) rowObj;
            int customerId = ((Number) row[0]).intValue();
            double total = ((Number) row[1]).doubleValue();
            if(total > 0) {
                Map<String,Object> tcmap = new HashMap<>();
                tcmap.put("customerId", customerId);
                tcmap.put("sales", total);
                customerIds.add(customerId);
                topCustomers.add(tcmap);
            }
        }
        try {
            List<Map<String,Object>> customers = getContactObjects(customerIds, 'C', header);
            for (var customer : customers) {
                int cid = (int) customer.get("id");
                for (var top : topCustomers) {
                    if ((int) top.get("customerId") == cid) {
                        top.put("customer", customer);
                    }
                }
            }
        }catch (Exception ignore){}
        return topCustomers;
    }



    public List<Map<String,Object>> getTopSuppliers(Date from, Date to, int companyId, String header){
        String sql = "select supplier_id, sum(purchase + purchase_tax - purchase_return - purchase_return_tax) as total from prd_view_purchase_by_supplier" +
                " where company_id = " + companyId +
                " and created between '" + helper.getDateFormat(from , "yyyy-MM-dd") + "' and '" + helper.getDateFormat(to , "yyyy-MM-dd") + "'" +
                " group by supplier_id order by total desc";
        List<Object> rows = dao.getNative(sql);
        List<Map<String,Object>> topSuppliers = new ArrayList<>();
        List<Integer> supplierIds = new ArrayList<>();
        for(var rowObj : rows){
            var row = (Object[]) rowObj;
            int supplierId = ((Number) row[0]).intValue();
            double total = ((Number) row[1]).doubleValue();
            if(total > 0) {
                Map<String,Object> tcmap = new HashMap<>();
                tcmap.put("supplierId", supplierId);
                tcmap.put("purchase", total);
                supplierIds.add(supplierId);
                topSuppliers.add(tcmap);
            }
        }
        try {
            List<Map<String,Object>> suppliers = getContactObjects(supplierIds, 'S', header);
            for (var supplier : suppliers) {
                int cid = (int) supplier.get("id");
                for (var top : topSuppliers) {
                    if ((int) top.get("supplierId") == cid) {
                        top.put("supplier", supplier);
                    }
                }
            }
        }catch (Exception ignore){}
        return topSuppliers;
    }



    public List<Map<String,Object>> getTopBrands(Date from, Date to, int companyId, char type){
        String select = type == 'P' ?
                "select brand_id, brand_name, sum(purchase + purchase_tax - purchase_return - purchase_return_tax) as total from prd_view_purchase_by_brand"
                :
                "select brand_id, brand_name, sum(sales + sales_tax - sales_return - sales_return_tax) as total from prd_view_sales_by_brand";
        String sql = select +
                " where company_id = " + companyId +
                " and created between '" + helper.getDateFormat(from , "yyyy-MM-dd") + "' and '" + helper.getDateFormat(to , "yyyy-MM-dd") + "'" +
                " group by brand_id, brand_name order by total desc";
        List<Object> rows = dao.getNative(sql);
        List<Map<String,Object>> topBrands = new ArrayList<>();
        for(var rowObj : rows){
            var row = (Object[]) rowObj;
            int brandId = ((Number) row[0]).intValue();
            String brandName = (String) row[1];
            double total = ((Number) row[2]).doubleValue();
            if(total > 0) {
                Map<String, Object> map = new HashMap<>();
                map.put("brandId", brandId);
                map.put("brandName", brandName);
                map.put(type == 'P' ? "purchase" : "sales", total);
                topBrands.add(map);
            }
        }
        return topBrands;
    }



    public List<BranchSales> getLiveBranchSales(Map<String,ArrayList<Integer>> branchIds, int companyId){
        List<Integer> branches =  branchIds.get("branchIds");
        List<BranchSales> branchSales = new ArrayList<>();
        for(var bid : branches){
            BranchSales bs = new BranchSales();
            bs.setBranchId(bid);
            branchSales.add(bs);
        }
        applyMtd(companyId, branchSales);
        applyYtd(companyId, branchSales);
        applyDaySales(companyId, branchSales);
        return branchSales;
    }





    private void applyDaySales(int companyId, List<BranchSales> branchSales){
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
        for(Object obj : result){
            Object[] row = (Object[]) obj;
            int branchId = ((Number) (row[0] != null ? row[0] : row[1])).intValue();
            double totalSales = row[2] != null ? ((Number) row[2]).doubleValue() : 0;
            double totalReturned = row[3] != null ? ((Number) row[3]).doubleValue() : 0;
            for(BranchSales bs : branchSales){
                if(bs.getBranchId() == branchId){
                    bs.setDaySales(totalSales);
                    bs.setDayReturns(totalReturned);
                    break;
                }
            }
        }
    }

    private void applyMtd(int companyId, List<BranchSales> branchSales){
        int year = Year.now().getValue();
        int month = YearMonth.now().getMonthValue();

        String monthStart = " '" + helper.getDateFormat(Helper.getFromDate(month, year) , "YYYY-MM-dd") + "' ";
        String monthEnd = " '" + helper.getDateFormat(Helper.getToDate(month, year) , "YYYY-MM-dd") + "' ";

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
        for(Object obj : result){
            Object[] row = (Object[]) obj;
            int branchId = ((Number) (row[0] != null ? row[0] : row[1])).intValue();
            double totalSales = row[2] != null ? ((Number) row[2]).doubleValue() : 0;
            double totalReturned = row[3] != null ? ((Number) row[3]).doubleValue() : 0;
            for(BranchSales bs : branchSales){
                if(bs.getBranchId() == branchId){
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


    private List<Map<String,Object>> getContactObjects(List<Integer> contactIds, char type, String header){
        StringBuilder ids = new StringBuilder("0");
        for(var id : contactIds) {
            ids.append(",").append(id);
        }
        String link = "";
        if(type == 'C')
            link = AppConstants.getCustomers(ids.toString());
        else if (type == 'S')
            link = AppConstants.getSuppliers(ids.toString());

        Response r = this.getSecuredRequest(link, header);
        if(r.getStatus() == 200){
            List<Map<String,Object>> list = r.readEntity(new GenericType<List<Map<String,Object>>>(){});
            return list;
        }
        return new ArrayList<>();
    }

    public <T> Response getSecuredRequest(String link, String header) {
        Invocation.Builder b = ClientBuilder.newClient().target(link).request();
        return b.header(HttpHeaders.AUTHORIZATION, header).get();
    }

}
