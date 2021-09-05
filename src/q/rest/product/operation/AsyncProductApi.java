package q.rest.product.operation;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import q.rest.product.dao.DAO;
import q.rest.product.helper.AppConstants;
import q.rest.product.helper.Helper;
import q.rest.product.helper.KeyConstant;
import q.rest.product.model.contract.v3.Branch;
import q.rest.product.model.contract.v3.PullStockRequest;
import q.rest.product.model.VinSearch;
import q.rest.product.model.quotation.SearchList;
import q.rest.product.model.quotation.SearchListItem;
import q.rest.product.model.qvm.qvmstock.CompanyProduct;
import q.rest.product.model.qvm.qvmstock.CompanyStock;
import q.rest.product.model.qvm.qvmstock.CompanyStockOffer;
import q.rest.product.model.qvm.qvmstock.DataPullHistory;
import q.rest.product.model.qvm.QvmObject;
import q.rest.product.model.qvm.qvmstock.minimal.PbCompanyProduct;
import q.rest.product.model.qvm.qvmstock.minimal.PbCompanyStockOffer;
import q.rest.product.model.search.SearchObject;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Stateless
public class AsyncProductApi {

    @EJB
    private DAO dao;

    @Asynchronous
    public void saveSearch2(String header, SearchObject searchObject, boolean found) {
        if (searchObject.isNewSearch()) {
            int companyId = Helper.getCompanyFromJWT(header);
            int subscriberId = Helper.getSubscriberFromJWT(header);
            Map<String, Object> map = new HashMap<>();
            map.put("query", searchObject.getQuery());
            map.put("companyId", companyId);
            map.put("subscriberId", subscriberId);
            map.put("found", found);
            Response r = this.postSecuredRequest(AppConstants.POST_SAVE_SEARCH_KEYWORD, map, header);
            r.close();
        }
    }

    @Asynchronous
    public void addToSearchList(String header, List<PbCompanyProduct> searchResult) {
        int companyId = Helper.getCompanyFromJWT(header);
        int subscriberId = Helper.getSubscriberFromJWT(header);
        for (var result : searchResult) {
            String sql = "select b from SearchList b where b.companyId =:value0 and b.targetCompanyId =:value1 and cast(b.created as date) =:value2";
            List<SearchList> existingSearchList = dao.getJPQLParams(SearchList.class, sql, companyId, result.getCompanyId(), new Date());
            double offerPrice = getOfferPrice(result);
            Long linkedProductId = getLinkedProductIdToSearch(result.getPartNumber(), result.getBrandName());
            if (existingSearchList.isEmpty()) {
                SearchList list = new SearchList(companyId, subscriberId, result.getCompanyId());
                dao.persist(list);
                existingSearchList.add(list);
            }
            var listId = existingSearchList.get(0).getId();
            var searchListItem = new SearchListItem(listId, result, offerPrice, linkedProductId);
            dao.persist(searchListItem);
        }
    }

    //temporary for company product in ProductQvmApiV3
    @Asynchronous
    public void addToSearchListOld(String header, List<CompanyProduct> searchResult) {
        int companyId = Helper.getCompanyFromJWT(header);
        int subscriberId = Helper.getSubscriberFromJWT(header);
        for (var result : searchResult) {
            try {
                String sql = "select b from SearchList b where b.companyId =:value0 and b.targetCompanyId =:value1 and cast(b.created as date) =:value2";
                List<SearchList> existingSearchList = dao.getJPQLParams(SearchList.class, sql, companyId, result.getCompanyId(), new Date());
                double offerPrice = getOfferPrice(result);
                Long linkedProductId = getLinkedProductIdToSearch(result.getPartNumber(), result.getBrandName());
                if (existingSearchList.isEmpty()) {
                    SearchList list = new SearchList(companyId, subscriberId, result.getCompanyId());
                    dao.persist(list);
                    existingSearchList.add(list);
                }
                var listId = existingSearchList.get(0).getId();
                var searchListItem = new SearchListItem(listId, result, offerPrice, linkedProductId);
                dao.persist(searchListItem);
            } catch (Exception ex) {
                System.out.println("an error occured in saving search list for old api");
            }
        }
    }

    private Long getLinkedProductIdToSearch(String partNumber, String brand) {
        partNumber = "'" + Helper.undecorate(partNumber) + "'";
        brand = "'" + brand.toUpperCase().trim() + "'";
        String sql = "select pr.id from prd_product pr join prd_brand br on pr.brand_id = br.id" +
                " where pr.product_number = " + partNumber + " and upper(br.name) = " + brand;
        System.out.println(sql);
        List<Number> found = dao.getNative(sql);
        if (!found.isEmpty()) {
            return found.get(0).longValue();
        }
        return null;
    }

    private double getOfferPrice(PbCompanyProduct companyProduct) {
        Iterator<PbCompanyStockOffer> it = companyProduct.getOffers().iterator();
        double offerPrice = 0;
        while (it.hasNext()) {
            var offer = it.next();
            offerPrice = offer.getOfferPrice();
        }
        return offerPrice;
    }

    private double getOfferPrice(CompanyProduct companyProduct) {
        Iterator<CompanyStockOffer> it = companyProduct.getOffers().iterator();
        double offerPrice = 0;
        while (it.hasNext()) {
            var offer = it.next();
            offerPrice = offer.getOfferPrice();
        }
        return offerPrice;
    }


    @Asynchronous
    public void saveReplacementSearch(String header, String query, boolean found) {
        //get company from header
        Map<String, Object> map = new HashMap<>();
        try {
            int[] ints = this.readClaims(header);
            map.put("query", query);
            map.put("found", found);
            map.put("companyId", ints[0]);
            map.put("subscriberId", ints[1]);
            Response r = this.postSecuredRequest(AppConstants.POST_SAVE_REPLACEMENTS_KEYWORD, map, header);
            r.close();
        } catch (Exception ignore) {

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
                        System.out.println("calling url " + url);
                        Response r = getSecuredRequest(url, header);
                        System.out.println("response: " + r.getStatus());
                        if (r.getStatus() == 200) {
                            List<QvmObject> rs = r.readEntity(new GenericType<List<QvmObject>>() {
                            });
                            updateStock(rs, psr, dph);
                        } else r.close();
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
                    "and company_product_id in (select d.id from prd_company_product d where d.company_id = " + dph.getCompanyId() + ")";
            dao.updateNative(sql);
            dph.setStatus('C');
        } catch (Exception ex) {
            dph.setStatus('F');
        } finally {
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
    public void saveVinSearch(String vin, String catalogId, String header, boolean found) {
        try {
            VinSearch vinSearch = new VinSearch();
            vinSearch.setCatalogId(catalogId);
            vinSearch.setVin(vin);
            vinSearch.setCreated(new Date());
            int[] claims = readClaims(header);
            vinSearch.setCompanyId(claims[0]);
            vinSearch.setSubscriberId(claims[1]);
            vinSearch.setFound(found);
            dao.persist(vinSearch);
        } catch (Exception ignore) {
            System.out.println("an exception occured!!!");
        }
    }

    public <T> Response postSecuredRequest(String link, T t, String authHeader) {
        Invocation.Builder b = ClientBuilder.newClient().target(link).request();
        b.header(HttpHeaders.AUTHORIZATION, authHeader);
        Response r = b.post(Entity.entity(t, "application/json"));
        return r;
    }

    public int[] readClaims(String header) throws Exception {
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
