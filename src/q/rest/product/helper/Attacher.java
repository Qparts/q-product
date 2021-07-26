package q.rest.product.helper;

import q.rest.product.helper.AppConstants;
import q.rest.product.model.qstock.*;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

public class Attacher {


    public static void attachCustomer(StockSalesView sales, String header) {
        var customer = getCustomer(sales.getCustomerId(), header);
        sales.attachCustomer(customer);
    }

    public static void attachCustomer(StockQuotationView quotation, String header){
        var customer = getCustomer(quotation.getCustomerId(), header);
        quotation.attachCustomer(customer);
    }

    public static void attachCustomer(StockSales sales, String header) {
        var customer = getCustomer(sales.getCustomerId(), header);
        sales.attachCustomer(customer);
    }

    public static void attachCustomer(List<StockSalesView> sales, String header) {
        StringBuilder ids = new StringBuilder("0");
        for (var s : sales) {
            ids.append(",").append(s.getCustomerId());
        }
        var customers = getCustomers(ids.toString(), header);
        for (var s : sales) {
            s.attachCustomer(customers);
        }
    }

    public static void attachCustomersMap(List<Map<String, Object>> list, String header){
        StringBuilder ids = new StringBuilder("0");
        for(var map : list){
            ids.append(",").append(map.get("customerId"));
        }
        var customers = getCustomers(ids.toString(), header);
        for(var map : list){
            try {
                for (var customersMap : customers) {
                    int id = (int) customersMap.get("id");
                    if (id == (int) map.get("customerId")) {
                        map.put("customer", customersMap);
                        break;
                    }
                }
            } catch (Exception ignore) {
            }
        }
    }

    public static void attachSuppliersMap(List<Map<String, Object>> list, String header){
        StringBuilder ids = new StringBuilder("0");
        for(var map : list){
            ids.append(",").append(map.get("supplierId"));
        }
        var suppliers = getSuppliers(ids.toString(), header);
        for(var map : list){
            try {
                for (var supplierMap : suppliers) {
                    int id = (int) supplierMap.get("id");
                    if (id == (int) map.get("supplierId")) {
                        map.put("supplier", supplierMap);
                        break;
                    }
                }
            } catch (Exception ignore) {
            }
        }
    }

    public static void attachCustomerQ(List<StockQuotationView> quotationViews, String header) {
        StringBuilder ids = new StringBuilder("0");
        for (var s : quotationViews) {
            ids.append(",").append(s.getCustomerId());
        }
        var customers = getCustomers(ids.toString(), header);
        for (var s : quotationViews) {
            s.attachCustomer(customers);
        }
    }


    public static void attachSupplier(StockPurchaseView purchase, String header) {
        var supplier = getSupplier(purchase.getSupplierId(), header);
        purchase.attachSupplier(supplier);
    }

    public static void attachSupplier(List<StockPurchaseView> purchases, String header) {
        StringBuilder ids = new StringBuilder("0");
        for (var s : purchases) {
            ids.append(",").append(s.getSupplierId());
        }
        var suppliers = getSuppliers(ids.toString(), header);
        for (var s : purchases) {
            s.attachSupplier(suppliers);
        }
    }

    private static Map<String, Object> getSupplier(int supplierId, String header) {
        Response r = getSecuredRequest(AppConstants.getSupplier(supplierId), header);
        if (r.getStatus() == 200) {
            return r.readEntity(new GenericType<Map<String,Object>>() {});
        } else r.close();
        return null;
    }

    private static Map<String, Object> getCustomer(int customerId, String header) {
        Response r = getSecuredRequest(AppConstants.getCustomer(customerId), header);
        if (r.getStatus() == 200) {
            return r.readEntity(new GenericType<Map<String,Object>>() {});
        } else r.close();
        return null;
    }

    private static List<Map> getSuppliers(String ids, String header) {
        Response r = getSecuredRequest(AppConstants.getSuppliers(ids), header);
        if (r.getStatus() == 200) {
            return r.readEntity(new GenericType<List<Map>>() {});
        } else r.close();
        return null;
    }

    private static List<Map> getCustomers(String ids, String header) {
        Response r = getSecuredRequest(AppConstants.getCustomers(ids), header);
        if (r.getStatus() == 200) {
            return r.readEntity(new GenericType<List<Map>>() {});
        } else r.close();
        return null;
    }




    private static Response getSecuredRequest(String link, String header) {
        Invocation.Builder b = ClientBuilder.newClient().target(link).request();
        return b.header(HttpHeaders.AUTHORIZATION, header).get();
    }


}
