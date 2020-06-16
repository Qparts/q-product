package q.rest.product.operation;

import com.google.gson.Gson;
import q.rest.product.dao.DAO;
import q.rest.product.helper.AppConstants;
import q.rest.product.helper.Helper;
import q.rest.product.model.contract.Branch;
import q.rest.product.model.contract.PublicBrand;
import q.rest.product.model.contract.PullStockRequest;
import q.rest.product.model.entity.Product;
import q.rest.product.model.entity.stock.VendorStock;
import q.rest.product.model.qvm.QvmAvailabilityRemote;
import q.rest.product.model.qvm.QvmBranch;
import q.rest.product.model.qvm.QvmObject;
import q.rest.product.model.qvm.QvmVendorCredentials;
import q.rest.product.operation.socket.AvailabilitySearchUserServer;
import q.rest.product.operation.socket.AvailabilitySearchVendorServer;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Stateless
public class AsyncProductApi {

    @EJB
    private DAO dao;


    @Asynchronous
    public void callVendorAPI(QvmVendorCredentials vendorCredentials, String query, int requesterId, char requesterType, String authHeader){
        String endpoint = vendorCredentials.getEndpointAddress() + query;
        String header = "Bearer " + vendorCredentials.getSecret();
        Response r = getSecuredRequest(endpoint, header);
        int code = r.getStatus();
        Map<String,Object> map = new HashMap<>();
        map.put("vendorId", vendorCredentials.getVendorId());
        if(code == 200){
            List<QvmObject> rs = r.readEntity(new GenericType<List<QvmObject>>() {
            });
            for (QvmObject result : rs) {
                result.setSource('L');
                result.setVendorId(vendorCredentials.getVendorId());
                result.setStatus('C');
            }

            Response r2 = this.putSecuredRequest(AppConstants.PUT_UPDATE_SEARCH_AVAILABILITY_WITH_BRANCHES, rs, authHeader);
            if(r2.getStatus() == 200){
                rs = r2.readEntity(new GenericType<List<QvmObject>>(){});
            }
            map.put("results", rs);
        }
        Gson gson = new Gson();
        String mapString = gson.toJson(map);
        if(requesterType == 'V') {
            AvailabilitySearchVendorServer.sendToVendorUser(mapString, requesterId);
        }else if(requesterType == 'U'){
            AvailabilitySearchUserServer.sendToUser(mapString, requesterId);
        }
    }




    @Asynchronous
    public void callPullData(List<String> links, String header, PullStockRequest psr){
        Date pullDate = new Date();
        ExecutorService es = Executors.newFixedThreadPool(10);
        for(int i = 0; i < links.size(); i++){
            final int ii = i;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    String url = links.get(ii);
                    int code = 200;
                    try {
                        Response r = getSecuredRequest(url, header);
                        code = r.getStatus();
                        if(r.getStatus() == 200){
                            List<QvmObject> rs = r.readEntity(new GenericType<List<QvmObject>>(){});
                            System.out.println("iteration done ");
                            updateStock(rs, psr, pullDate);
                        }
                    } catch (Exception e) {

                    }
                    System.out.println(url + " " + code);
                }
            };
            es.execute(runnable);
        }
        es.shutdown();
        while (!es.isTerminated());
        System.out.println("\nFinished all threads");


    }

    private Branch getBranchId(PullStockRequest psr, String qvmBranchId){
        for(var br : psr.getBranches()){
            if (br.getClientBranchId().equals(qvmBranchId)){
                return br;
            }
        }
        return null;
    }

    private void updateStock(List<QvmObject> qvmObjects, PullStockRequest psr, Date pullDate){
        for(var qvmObject : qvmObjects){
            for(var av : qvmObject.getAvailability()){
                Branch branch = getBranchId(psr, av.getBranch().getBranchId());
                if(branch == null)
                    continue;
                qvmObject.setPartNumber(Helper.undecorate(qvmObject.getPartNumber()));
                String sql = "select b from VendorStock b where b.partNumber = :value0 and b.vendorId = :value1 and b.brandName = :value2 and b.branchId = :value3";
                VendorStock vendorStock = dao.findJPQLParams(VendorStock.class, sql, qvmObject.getPartNumber(), qvmObject.getVendorId(), qvmObject.getBrand(), branch.getId());
                if(vendorStock != null){
                    vendorStock.setCreated(pullDate);
                    vendorStock.setQuantity(av.getQuantity());
                    vendorStock.setRetailPrice(qvmObject.getRetailPrice());
                    vendorStock.setWholesalesPrice(qvmObject.getWholesalesPrice());
                    vendorStock.setCreatedBy(0);
                    vendorStock.setCreatedByVendor(0);
                    if(vendorStock.getProductId() == 0){
                        String jpql = "select b from Product b where b.productNumber = :value0 and lower(b.brand.name) = :value1";
                        Product product = dao.findJPQLParams(Product.class, jpql, qvmObject.getPartNumber(), qvmObject.getBrand().toLowerCase());
                        if(product != null){
                            vendorStock.setProductId(product.getId());
                        }
                    }
                    dao.update(vendorStock);
                }
                else{
                    VendorStock vs = new VendorStock();
                    vs.setCreated(pullDate);
                    vs.setBrandName(qvmObject.getBrand());
                    vs.setVendorId(psr.getVendorId());
                    vs.setBranchId(branch.getId());
                    vs.setCityId(branch.getCityId());
                    vs.setQuantity(av.getQuantity());
                    vs.setRetailPrice(qvmObject.getRetailPrice());
                    vs.setWholesalesPrice(qvmObject.getWholesalesPrice());
                    vs.setCreatedBy(0);
                    vs.setCreatedByVendor(0);
                    vs.setPartNumber(qvmObject.getPartNumber());
                    String jpql = "select b from Product b where b.productNumber = :value0 and lower(b.brand.name) = :value1";
                    Product product = dao.findJPQLParams(Product.class, jpql, vs.getPartNumber(), vs.getBrandName().toLowerCase());
                    if(product != null){
                        vs.setProductId(product.getId());
                        vs.setBrandName(product.getBrand().getName());
                    }
                    dao.persist(vs);
                }
            }

            //delete anything before newdate for the same vendor, same branch
            Helper h = new Helper();
            String sql = "delete from prd_vendor_stock where vendor_id = " + psr.getVendorId() +
                    " and created < '" + h.getDateFormat(pullDate) + "'";
            dao.updateNative(sql);
        }
    }



    public <T> Response putSecuredRequest(String link, T t, String authHeader) {
        Invocation.Builder b = ClientBuilder.newClient().target(link).request();
        b.header(HttpHeaders.AUTHORIZATION, authHeader);
        Response r = b.put(Entity.entity(t, "application/json"));
        return r;
    }



    public Response getSecuredRequest(String link, String header) {
        Invocation.Builder b = ClientBuilder.newClient().target(link).request();
        b.header(HttpHeaders.AUTHORIZATION, header);
        Response r = b.get();
        return r;
    }
}
