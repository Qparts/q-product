package q.rest.product.operation;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import q.rest.product.dao.DAO;
import q.rest.product.helper.AppConstants;
import q.rest.product.helper.Helper;
import q.rest.product.helper.KeyConstant;
import q.rest.product.model.contract.v3.Branch;
import q.rest.product.model.contract.v3.PullStockRequest;
import q.rest.product.model.entity.VinSearch;
import q.rest.product.model.entity.v3.stock.CompanyProduct;
import q.rest.product.model.entity.v3.stock.CompanyStock;
import q.rest.product.model.entity.v3.stock.DataPullHistory;
import q.rest.product.model.qvm.QvmObject;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Stateless
public class AsyncProductApi {

    @EJB
    private DAO dao;

    @Asynchronous
    public void saveSearch(String header, Map<String, Object> map) {
        if (map.get("companyId") != null) {
            this.postSecuredRequest(AppConstants.POST_SAVE_SEARCH_KEYWORD, map, header);
        }
    }

    @Asynchronous
    public void callPullData(List<String> links, String header, PullStockRequest psr, DataPullHistory dph) {
        ExecutorService es = Executors.newFixedThreadPool(10);
        for (int i = 0; i < links.size(); i++) {
            final int ii = i;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    String url = links.get(ii);
                    try {
                        Response r = getSecuredRequest(url, header);
                        if (r.getStatus() == 200) {
                            List<QvmObject> rs = r.readEntity(new GenericType<List<QvmObject>>() {
                            });
                            updateStock(rs, psr, dph);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            es.execute(runnable);
        }
        es.shutdown();
        while (!es.isTerminated()) ;
        System.out.println("over");
    }

    private Branch getBranchId(PullStockRequest psr, String qvmBranchId) {
        for (var br : psr.getBranches()) {
            if (br.getClientBranchId().equals(qvmBranchId)) {
                return br;
            }
        }
        return null;
    }


    private synchronized void updateStock(List<QvmObject> qvmObjects, PullStockRequest psr, DataPullHistory dph) {
        try {
            for (var qvmObject : qvmObjects) {
                qvmObject.setPartNumber(Helper.undecorate(qvmObject.getPartNumber()));
                qvmObject.setBrandPartNumber(Helper.undecorate(qvmObject.getBrandPartNumber()));
                if (qvmObject.getAvailability() == null || qvmObject.getAvailability().isEmpty()) continue;
                //get company product
                String sql = "select b from CompanyProduct b where b.partNumber = :value0 and b.companyId = :value1 and b.brandName = :value2";
                CompanyProduct companyProduct = dao.findJPQLParams(CompanyProduct.class, sql, qvmObject.getPartNumber(), psr.getCompanyId(), qvmObject.getBrand());
                if (companyProduct != null) {
                    companyProduct.setRetailPrice(qvmObject.getRetailPrice());
                    companyProduct.setWholesalesPrice(qvmObject.getWholesalesPrice());
                    companyProduct.setCreated(dph.getCreated());
                    companyProduct.setAlternativeNumber(qvmObject.getBrandPartNumber());
                    for (var av : qvmObject.getAvailability()) {
                        Branch branch = getBranchId(psr, av.getBranch().getBranchId());
                        if (branch == null) continue;
                        CompanyStock cs = companyProduct.getStockFromBranchId(branch.getId());
                        if (cs == null) {
                            cs = new CompanyStock();
                            cs.setCountryId(branch.getCountryId());
                            cs.setBranchId(branch.getId());
                            cs.setRegionId(branch.getRegionId());
                            cs.setCityId(branch.getCityId());
                            cs.setCreated(dph.getCreated());
                            cs.setOfferOnly(false);
                            cs.setQuantity(av.getQuantity());
                            companyProduct.getStock().add(cs);
                        } else {
                            cs.setQuantity(av.getQuantity());
                            cs.setOfferOnly(false);
                            cs.setCreated(dph.getCreated());
                        }
                    }
                    dao.update(companyProduct);
                } else {
                    CompanyProduct cp = new CompanyProduct();
                    cp.setBrandName(qvmObject.getBrand());
                    cp.setPartNumber(qvmObject.getPartNumber());
                    cp.setAlternativeNumber(qvmObject.getBrandPartNumber());
                    cp.setCompanyId(psr.getCompanyId());
                    cp.setCreated(dph.getCreated());
                    cp.setRetailPrice(qvmObject.getRetailPrice());
                    cp.setWholesalesPrice(qvmObject.getWholesalesPrice());
                    //stock
                    for (var av : qvmObject.getAvailability()) {
                        Branch branch = getBranchId(psr, av.getBranch().getBranchId());
                        if (branch == null) continue;
                        CompanyStock companyStock = new CompanyStock();
                        companyStock.setCreated(dph.getCreated());
                        companyStock.setOfferOnly(false);
                        companyStock.setQuantity(av.getQuantity());
                        companyStock.setBranchId(branch.getId());
                        companyStock.setCityId(branch.getCityId());
                        companyStock.setRegionId(branch.getRegionId());
                        companyStock.setCountryId(branch.getCountryId());
                        cp.getStock().add(companyStock);
                    }
                    dao.persist(cp);
                }
            }

            Helper h = new Helper();
            String sql = "delete from prd_company_stock where created < '" + h.getDateFormat(dph.getCreated()) + "' " +
                    "and company_product_id in (select d.id from prd_company_product d where d.company_id = " +dph.getCompanyId() +")";
            dao.updateNative(sql);
            dph.setStatus('C');
        }catch (Exception ex){
            dph.setStatus('F');
        }finally {
            dao.update(dph);
        }
    }

    public <T> Response putSecuredRequest(String link, T t, String authHeader) {
        Invocation.Builder b = ClientBuilder.newClient().target(link).request();
        b.header(HttpHeaders.AUTHORIZATION, authHeader);
        Response r = b.put(Entity.entity(t, "application/json"));
        return r;
    }



    @Asynchronous
    public void saveVinSearch(String vin, String catalogId, String header, boolean found){
        try{
            VinSearch vinSearch = new VinSearch();
            vinSearch.setCatalogId(catalogId);
            vinSearch.setVin(vin);
            vinSearch.setCreated(new Date());
            int[] claims = readClaims(header);
            vinSearch.setCompanyId(claims[0]);
            vinSearch.setSubscriberId(claims[1]);
            vinSearch.setFound(found);
            dao.persist(vinSearch);
        }catch (Exception ignore){
            System.out.println("an exception occured!!!");
        }
    }

    public <T> Response postSecuredRequest(String link, T t, String authHeader) {
        Invocation.Builder b = ClientBuilder.newClient().target(link).request();
        b.header(HttpHeaders.AUTHORIZATION, authHeader);
        Response r = b.post(Entity.entity(t, "application/json"));
        return r;
    }

    public int[] readClaims(String header) throws Exception{
        String token = header.substring("Bearer".length()).trim();
        Claims claims = Jwts.parserBuilder().setSigningKey(KeyConstant.PUBLIC_KEY).build().parseClaimsJws(token).getBody();
        int companyId = Integer.parseInt(claims.get("comp").toString());
        int subscriberId = Integer.parseInt(claims.get("sub").toString());
        return new int[]{companyId, subscriberId};
    }


    public Response getSecuredRequest(String link, String header) {
        Invocation.Builder b = ClientBuilder.newClient().target(link).request();
        b.header(HttpHeaders.AUTHORIZATION, header);
        Response r = b.get();
        return r;
    }
}
