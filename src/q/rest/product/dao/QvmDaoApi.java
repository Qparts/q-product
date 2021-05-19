package q.rest.product.dao;

import q.rest.product.helper.Helper;
import q.rest.product.model.contract.v3.QStockUploadHolder;
import q.rest.product.model.contract.v3.SummaryReport;
import q.rest.product.model.contract.v3.UploadHolder;
import q.rest.product.model.contract.v3.UploadsSummary;
import q.rest.product.model.VinSearch;
import q.rest.product.model.qvm.qvmstock.*;
import q.rest.product.model.search.SearchObject;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.*;

@Stateless
public class QvmDaoApi {
    @EJB
    private DAO dao;
    private Helper helper = new Helper();

    //user only
    public List<DataPullHistory> getLatestPulls(){
        String sql = "select distinct on (company_id) * from prd_data_pull_history order by company_id, created desc";
        return dao.getNative(DataPullHistory.class, sql);
    }

    //subscriber
    public List<CompanyProduct> getSampleProducts(int companyId){
        String sql = "select b from CompanyProduct b where b.companyId = :value0" +
                " and b.id in (" +
                " select c.companyProductId from CompanyStock c where c.offerOnly =:value1)";
        return dao.getJPQLParamsOffsetMax(CompanyProduct.class, sql, 0, 10, companyId, false);
    }

    //user only
    public List<Map<String,Object>> getVinSearchReport(List<Date> dates){
        List<Map<String,Object>> kgs = new ArrayList<>();
        for (Date date : dates) {
            String sql = "select count(*) from VinSearch b where cast(b.created as date) = cast(:value0 as date)";
            Number n = dao.findJPQLParams(Number.class, sql, date);
            Map<String, Object> map = new HashMap<>();
            map.put("count", n.intValue());
            map.put("date", date.getTime());
            kgs.add(map);
        }
        return kgs;
    }


    public List<CompanyOfferUploadRequest> getOfferUploadRequest(int companyId){
        String sql = "select b from CompanyOfferUploadRequest b where b.companyId = :value0 order by b.created desc";
        return dao.getJPQLParams(CompanyOfferUploadRequest.class, sql, companyId);
    }

    public void updateUploadRequest(CompanyUploadRequest uploadRequest){
        uploadRequest.setCompleted(new Date());
        dao.update(uploadRequest);
    }

    //user
    public List<CompanyUploadRequest> getPendingStockUploads(){
        String sql = "select b from CompanyUploadRequest b where b.status = :value0  order by b.created desc";
        return dao.getJPQLParams(CompanyUploadRequest.class, sql, 'R');
    }


    //user
    public List<CompanyUploadRequest> getAllStockUploads(){
        String sql = "select b from CompanyUploadRequest b order by b.created desc";
        return dao.getJPQLParams(CompanyUploadRequest.class, sql);
    }


    //user
    public List<CompanyOfferUploadRequest> getAllOfferUploads(){
        return dao.getOrderByOriented(CompanyOfferUploadRequest.class, "created", "desc");
    }

    public List<CompanyOfferUploadRequest> getLiveOffers(){
        String sql = "select b from CompanyOfferUploadRequest b where :value0 between b.startDate and b.endDate and b.status = :value1 order by b.startDate";
        return dao.getJPQLParams(CompanyOfferUploadRequest.class, sql, new Date(), 'C');
    }


    public int searchCompanyProductSize(SearchObject searchObject){
        try {
            String undecorated = "%" + Helper.undecorate(searchObject.getQuery()) + "%";
            String sql1 = "select count(z.*) from (select p.* from prd_company_product p " +
                    "   join prd_company_stock c on p.id = c.company_product_id " +
                    " where c.offer_only = false" +
                    "  and p.part_number like '"+undecorated+"' and p.id not in (select company_product_id from prd_company_stock_offer where now() between offer_start_date and offer_end_date) " + searchObject.getLocationFiltersSql("c", true) +
                    " union" +
                    " select p.* from prd_company_product p " +
                    "  join prd_company_stock_offer o on p.id = o.company_product_id " +
                    "  join prd_company_stock s on p.id = s.company_product_id " +
                    " where now() between o.offer_start_date and o.offer_end_date" +
                    "  and p.part_number like '"+ undecorated +"' " + searchObject.getLocationFiltersSql("s", true) +
                    ") z " + searchObject.getProductFilterSql();
            return dao.getNativeSingle(Number.class ,sql1).intValue();
        } catch (Exception ex) {
            return 0;
        }
    }

    public List<CompanyProduct> searchCompanyProducts(SearchObject searchObject) {
        try {
            int max = searchObject.getMax() == 0 ? 10 : searchObject.getMax();
            String undecorated = "%" + Helper.undecorate(searchObject.getQuery()) + "%";
            String sql1 = "select z.* from (select p.*, 0 as on_offer from prd_company_product p " +
                    "   join prd_company_stock c on p.id = c.company_product_id " +
                    " where c.offer_only = false" +
                    "  and p.part_number like '"+undecorated+"' " +
                    " and p.id not in (select company_product_id from prd_company_stock_offer where now() between offer_start_date and offer_end_date) " +
                    searchObject.getLocationFiltersSql("c", true) +
                    " union" +
                    " select p.*, 1 as on_offer from prd_company_product p " +
                    "  join prd_company_stock_offer o on p.id = o.company_product_id " +
                    " join prd_company_stock s on p.id = s.company_product_id " +
                    " where now() between o.offer_start_date and o.offer_end_date" +
                    "  and p.part_number like '"+ undecorated +"' " +
                    searchObject.getLocationFiltersSql("s", true) +
                    " ) z " +
                    searchObject.getProductFilterSql() +
                    " order by on_offer desc ";
            return dao.getNativeOffsetMax(CompanyProduct.class, sql1, searchObject.getOffset(), max);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<CompanyProduct> getSpecialOfferProducts(int offerId, int offset, int max){
        String sql2 = "select b from CompanyProduct b " +
                " where b.id in (" +
                " select c.companyProductId from CompanyStockOffer c " +
                " where c.offerRequestId = :value0)" +
                " order by b.partNumber";
        return dao.getJPQLParamsOffsetMax(CompanyProduct.class, sql2, offset, max, offerId);
    }

    public Map<String,Object> getSpecialOfferProductsWithFilter(String filter, int offerId, int offset, int max){
        filter = "" + Helper.undecorate(filter) + "%";
        String sql = "select count(*) from CompanyProduct b " +
                " where b.id in (" +
                " select c.companyProductId from CompanyStockOffer c " +
                " where c.offerRequestId = :value0)" +
                " and b.partNumber like :value1";
        Number count = dao.findJPQLParams(Number.class, sql, offerId, filter);
        String sql2 = "select b from CompanyProduct b " +
                " where b.id in (" +
                " select c.companyProductId from CompanyStockOffer c " +
                " where c.offerRequestId = :value0)" +
                " and b.partNumber like :value1" +
                " order by b.partNumber";
        List<CompanyProduct> so = dao.getJPQLParamsOffsetMax(CompanyProduct.class, sql2, offset, max, offerId, filter);
        Map<String,Object> map = new HashMap<>();
        map.put("products", so);
        map.put("count", count);
        return map;
    }

    public void makeOfferExpired(CompanyOfferUploadRequest offer){
        Date expireDate = Helper.addMinutes(new Date(), -5);
        String dateString = helper.getDateFormat(expireDate);
        String sql = "update prd_company_stock_offer set offer_end_date = '" + dateString + "' where offer_request_id = " + offer.getId();
        dao.updateNative(sql);
        offer.setEndDate(expireDate);
        dao.update(offer);
    }

    public CompanyOfferUploadRequest findOffer(int id){
        return dao.find(CompanyOfferUploadRequest.class, id);
    }

    public void makeOfferCompleted(CompanyOfferUploadRequest uploadRequest){
        uploadRequest.setCompleted(new Date());
        dao.update(uploadRequest);
    }

    public SummaryReport getOverallProductSummaryReport(){
        String sql = "select count(*) from VinSearch b where cast(b.created as date) = cast(now() as date)";
        int vinSearchesToday = dao.findJPQLParams(Number.class, sql).intValue();
        sql = "select coalesce(sum(stk.quantity * pcp.retailPrice),0) from CompanyStock stk left join CompanyProduct pcp on stk.companyProductId = pcp.id";
        double stockValue = dao.findJPQLParams(Number.class, sql).doubleValue();
        sql = "select coalesce(sum(b.quantity * b.offerPrice),0) from CompanyStockOffer b where now() between b.offerStartDate and b.offerEndDate";
        double offersValue = dao.findJPQLParams(Number.class, sql).doubleValue();
        sql = "select b from VinSearch b order by b.created desc";
        List<VinSearch> vinSearches = dao.getJPQLParamsMax(VinSearch.class, sql, 50);
        SummaryReport report = new SummaryReport();
        report.setVinSearchesToday(vinSearchesToday);
        report.setStockValue(stockValue);
        report.setOffersValue(offersValue);
        report.setTopVins(vinSearches);
        return report;
    }

    public SummaryReport getCompanyProductSummaryReport(int companyId){
        String sql = "select count(*) from VinSearch b where b.companyId = :value0" ;
        int totalVinSearches = dao.findJPQLParams(Number.class, sql, companyId).intValue();
        sql = "select coalesce(sum(stk.quantity * pcp.retailPrice),0) from CompanyStock stk left join CompanyProduct pcp on stk.companyProductId = pcp.id where pcp.companyId = :value0";
        double stockValue = dao.findJPQLParams(Number.class, sql, companyId).doubleValue();
        sql = "select coalesce(sum(b.quantity * b.offerPrice),0) from CompanyStockOffer b where now() between b.offerStartDate and b.offerEndDate " +
                "and b.companyProductId in (select c.id from CompanyProduct c where c.companyId = :value0)";
        double offersValue = dao.findJPQLParams(Number.class, sql, companyId).doubleValue();
        sql = "select b from VinSearch b where b.companyId = :value0 order by b.created desc";
        List<VinSearch> vinSearches = dao.getJPQLParamsMax(VinSearch.class, sql, 50, companyId);
        SummaryReport report = new SummaryReport();
        report.setOffersValue(offersValue);
        report.setStockValue(stockValue);
        report.setTopVins(vinSearches);
        report.setTotalVinSearches(totalVinSearches);
        return report;
    }

    public UploadsSummary getCompanyUploadsSummary(int companyId){
        String sql = "select b from CompanyUploadRequest b where b.companyId = :value0 order by created desc";
        List<CompanyUploadRequest> stockRequests = dao.getJPQLParams(CompanyUploadRequest.class, sql , companyId);
        sql = "select b from CompanyOfferUploadRequest b where b.companyId = :value0 order by created desc";
        List<CompanyOfferUploadRequest> offerRequests = dao.getJPQLParams(CompanyOfferUploadRequest.class, sql, companyId);
        UploadsSummary us = new UploadsSummary();
        us.setOfferRequests(offerRequests);
        us.setStockRequests(stockRequests);
        return us;
    }

    public void createOfferUploadRequest(CompanyOfferUploadRequest uploadRequest){
        Date date = Helper.addMinutes(new Date(), 5 * -1);
        String jpql = "select b from CompanyOfferUploadRequest b where b.created > :value0 and b.companyId = :value1 and b.uploadSource = :value2";
        List<CompanyOfferUploadRequest> check = dao.getJPQLParams(CompanyOfferUploadRequest.class, jpql, date, uploadRequest.getCompanyId(), uploadRequest.getUploadSource());
        if (check.isEmpty()) {
            uploadRequest.setCreated(new Date());
            dao.persist(uploadRequest);
        }
    }

    public void createStockUploadRequest(CompanyUploadRequest uploadRequest){
        Date date = Helper.addMinutes(new Date(), 5 * -1);
        String jpql = "select b from CompanyUploadRequest b where b.branchId = :value0 and b.created > :value1 and b.companyId = :value2 and b.uploadSource = :value3";
        List<CompanyUploadRequest> check = dao.getJPQLParams(CompanyUploadRequest.class, jpql, uploadRequest.getBranchId(), date, uploadRequest.getCompanyId(), uploadRequest.getUploadSource());
        if (check.isEmpty()) {
            dao.persist(uploadRequest);
        }
    }

    public List<CompanyUploadRequest> getStockUploadRequests(int companyId){
        String sql = "select b from CompanyUploadRequest b where b.companyId = :value0 order by b.created desc";
        return dao.getJPQLParams(CompanyUploadRequest.class, sql, companyId);
    }

    @Asynchronous
    public void updateSpecialOfferStockAsyncOptimized(UploadHolder holder) {
        try {
            CompanyOfferUploadRequest req = dao.find(CompanyOfferUploadRequest.class, holder.getOfferId());
            String date = "'" +helper.getDateFormat(holder.getDate()) +"'";
            String offerStart = "'" +helper.getDateFormat(req.getStartDate()) +"'";
            String offerEnd = "'" +helper.getDateFormat(req.getEndDate()) +"'";
            String sql = "";
            int companyId = holder.getCompanyId();
            int branchId = holder.getBranchId();
            int cityId = holder.getCityId();
            int countryId = holder.getCountryId();
            int regionId = holder.getRegionId();

            for (var offerVar : holder.getOfferVars()){
                String pn = "'" +Helper.undecorate(offerVar.getPartNumber()) + "'";
                String alt = Helper.undecorate(offerVar.getAlternativeNumber());
                String brand = "'" +offerVar.getBrand() + "'";
                int quantity = offerVar.getQuantity();
                double retail = 0;
                double wholesales = 0;
                double offerPrice = offerVar.getOfferPrice();

                if (alt == null || alt.length() == 0) {
                    alt = "null";
                } else {
                    alt = "'" + alt +"'";
                }

                sql += " with ins1 as (" +
                        " insert into prd_company_product (company_id, part_number, alternative_number, product_id, brand_name, retail_price, wholesales_price, created) " +
                        " values ("+companyId+", "+ pn +" , "+ alt +" , 0 , " + brand +" , "+ retail +" , "+ wholesales +" , "+ date +")" +
                        " on conflict on constraint unique_part_number_company_id_brand_name do update set alternative_number = excluded.alternative_number " +
                        " returning id as company_product_id " +
                        "), ins2 as (" +
                        " insert into prd_company_stock (company_product_id, branch_id, city_id, region_id, country_id, quantity, created, offer_only) " +
                        " values ((select company_product_id from ins1), "+ branchId +" , "+ cityId +" , "+ regionId +" , "+ countryId +" , 0 , " + date +" , true)" +
                        " on conflict on constraint unique_stock_product_id_branch_id do update set created = " + date +
                        " ) insert into prd_company_stock_offer (company_product_id, quantity, created, offer_price, offer_start_date, offer_end_date, offer_request_id) " +
                        " values ((select company_product_id from ins1), "+ quantity +", " +date +", "+ offerPrice +", " + offerStart + ",  " + offerEnd +", " +  req.getId()+")" +
                        " on conflict on constraint unique_special_offer_product_id_offer_id do nothing;";
            }//end for loop
            dao.insertNative(sql);
            deletePreviousOffers(holder);
        } catch (Exception ex) {
            System.out.println("an error occurred");
        }
    }

    @Asynchronous
    public void updateStockAsyncOptimized(UploadHolder holder) {
        try {
            String date = "'" +helper.getDateFormat(holder.getDate()) +"'";
            String sql = "";
            int branchId= holder.getBranchId();
            int cityId = holder.getCityId();
            int countryId = holder.getCountryId();
            int regionId = holder.getRegionId();
            int companyId = holder.getCompanyId();
            for (var stockVar : holder.getStockVars()) {
                String pn = "'" +Helper.undecorate(stockVar.getPartNumber()) + "'";
                String brand = "'"+stockVar.getBrand()+"'";
                String alt = Helper.undecorate(stockVar.getAlternativeNumber());
                double retailPrice = stockVar.getRetailPrice();
                double wholesales = stockVar.getWholesalesPrice();
                int quantity =  stockVar.getQuantity();


                if (alt == null || alt.length() == 0) {
                    alt = "null";
                } else {
                    alt = "'" + alt +"'";
                }

                 sql += " with ins1 as (" +
                         " insert into prd_company_product (company_id, part_number, alternative_number, product_id, brand_name, retail_price, wholesales_price, created)" +
                         " values ("+ companyId +", " + pn  +" , " + alt  + " , 0 , "+ brand+" , "+ retailPrice +" , "+wholesales+" , "+date+")" +
                         " on conflict on constraint unique_part_number_company_id_brand_name do update set retail_price = " + retailPrice + " , wholesales_price =" + wholesales + " , created = "+date +
                         " returning id as company_product_id) " +
                         " insert into prd_company_stock (company_product_id, branch_id, city_id, region_id, country_id, quantity, created, offer_only) " +
                         " values ( (select company_product_id from ins1), "+ branchId +" , "+ cityId +" , "+regionId+" , " + countryId + " , "+ quantity +" , "+date+" , false)" +
                         "on conflict on constraint unique_stock_product_id_branch_id do update set created = " + date + "; ";
            }//end for loop
            dao.insertNative(sql);
            deletePreviousStock(holder);
        } catch (Exception ex) {
            System.out.println("an error occured");
        }
    }




    @Asynchronous
    public void updateQStockAsyncOptimized(QStockUploadHolder holder) {
        try {
            String sql = "";
            int branchId = holder.getBranchId();
            int companyId = holder.getCompanyId();
            String date = "'" +helper.getDateFormat(holder.getDate()) +"'";
            for(var part : holder.getUploadPart() ){
                String undecorated = "'" +Helper.undecorate(part.getPartNumber()) + "'";
                String pn = "'" + part.getPartNumber() + "'";
                String brandClass = "'" + part.getBrandClass() + "'";
                String brandName = "'" + part.getBrandName() + "'";
                String descEng = "'" + part.getDecsEng() + "'";
                String descAr = "'" + part.getDecsAr() + "'";
                String classCode = "'" + part.getClassCode() + "'";
                String idCode = "'" + part.getIdCode()+ "'";
                String frCode = "'" + part.getFrCode()+ "'";
                String location = "'" + part.getLocation()+ "'";
                double unitCost = part.getUnitCost();
                double averageCost = part.getAverageCost();
                double sellingPrice = part.getSellingPrice();
                double agentPrice = part.getAgentPrice();
                double wholesalesPrice = part.getWholesalesPrice();
                double openingUnitCost = part.getOpeningUnitCost();
                double openingAverageCost = part.getOpeningAverageCost();
                double openingSellingPrice = part.getOpeningSellingPrice();
                int quantityIn = part.getQuantityIn();
                int quantityOut = part.getQuantityOut();
                int quantityInHand = part.getQuantityInHand();
                int tempQuantityOut = part.getTempQuantityOut();
                int openingQtyIn = part.getOpeningQtyIn();
                int openingBalance = part.getOpeningBalance();
                int tempIn = part.getTempIn();

                sql += "insert into prd_saba_live_stock (company_id, created, branch_id, brand_class, brand_name, part_number, undecorated_part_number, " +
                        " decs_eng, decs_ar, class_code, id_code, fr_code, location, unit_cost, average_cost, selling_price, " +
                        " agent_price, wholesales_price, quantity_in, quantity_out, quantity_in_hand, temp_quantity_out, " +
                        " opening_qty_in, opening_balance, opening_unit_cost, opening_average_cost, opening_selling_price, temp_in) " +
                        " values ("+ companyId + ", "+ date +", "+ branchId +"," + brandClass +", "+ brandName +", "+ pn +", " + undecorated +", " +
                        " " + descEng +", " + descAr +", "+ classCode +", "+ idCode +", " + frCode +", "+ location +", "+ unitCost +", " + averageCost +", " + sellingPrice +", " +
                        " " +agentPrice +", "+ wholesalesPrice +", "+ quantityIn +", "+ quantityOut +", " + quantityInHand +", "+ tempQuantityOut +", " +
                        " "+ openingQtyIn +", "+ openingBalance +", "+openingUnitCost +", "+ openingAverageCost +", "+ openingSellingPrice +", "+ tempIn +") " +
                        "on conflict on constraint prd_saba_live_stock_constraint do update set " +
                        " unit_cost = "+ unitCost +", location = "+ location +", created = " + date + ", " +
                        " average_cost = "+ averageCost + ", selling_price = "+ sellingPrice + ", agent_price = "+ agentPrice + ", " +
                        "  wholesales_price = "+ wholesalesPrice + ", quantity_in = "+ quantityIn +", quantity_out = "+ quantityOut + ", " +
                        "  quantity_in_hand = "+quantityInHand +", temp_quantity_out = "+ tempQuantityOut +", conflict_update = 'OMG!!';";
            }
            System.out.println("inserting batch");
            dao.insertNative(sql);
            String delete = "delete from prd_saba_live_stock where branch_id = " + branchId +
                    " and company_id = " + companyId +
                    " and created < " + date ;
            dao.updateNative(delete);
            System.out.println("done inserting batch");
            System.out.println("==============");
        } catch (Exception ex) {
            System.out.println("an error occured");
        }
    }



    private void deletePreviousStock(UploadHolder holder) {
        //delete anything before new date in the branch
        if (holder.isOverridePrevious()) {
            String sql = "delete from prd_company_stock where branch_id = " + holder.getBranchId() + " and created < '" + helper.getDateFormat(holder.getDate()) + "'";
            dao.updateNative(sql);
        }
    }




    private void deletePreviousOffers(UploadHolder holder) {
        //delete anything before new date in the branch
        if (holder.isOverridePrevious()) {
            Helper h = new Helper();
            String sql = "delete from prd_company_stock_offer " +
                    "where company_product_id in (select b.id from prd_company_product b where b.company_id = " + holder.getCompanyId() + ")" +
                    " and created < '" + h.getDateFormat(holder.getDate()) + "'";
            dao.updateNative(sql);
        }
    }


}
