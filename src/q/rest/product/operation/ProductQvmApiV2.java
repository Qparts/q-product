package q.rest.product.operation;


import q.rest.product.dao.DAO;
import q.rest.product.filter.SecuredUser;
import q.rest.product.filter.SecuredUserVendor;
import q.rest.product.filter.SecuredVendor;
import q.rest.product.filter.ValidApp;
import q.rest.product.helper.Helper;
import q.rest.product.model.contract.PublicProduct;
import q.rest.product.model.contract.PublicReview;
import q.rest.product.model.contract.PublicSpec;
import q.rest.product.model.entity.Product;
import q.rest.product.model.entity.ProductSpec;
import q.rest.product.model.entity.stock.VendorSpecialOfferStock;
import q.rest.product.model.entity.stock.VendorSpecialOfferUploadRequest;
import q.rest.product.model.qvm.*;
import q.rest.product.model.contract.UploadStock;
import q.rest.product.model.entity.stock.VendorStock;
import q.rest.product.model.entity.stock.VendorUploadRequest;

import javax.ejb.Asynchronous;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Path("/qvm/api/v2/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductQvmApiV2 {

    @EJB
    private DAO dao;

    @EJB
    private AsyncProductApi async;

    @ValidApp
    @PUT
    @Path("update-stock")
    public Response updateStock(UploadStock uploadStock){
       try{
           updateStockAsync(uploadStock);
           return Response.status(201).build();
       }catch (Exception ex){
           return Response.status(500).build();
       }
   }


    @ValidApp
    @PUT
    @Path("update-special-offer-stock")
    public Response updateSpecialOfferStock(UploadStock uploadStock){
        try{
            updateSpecialOfferStockAsync(uploadStock);
            return Response.status(201).build();
        }catch (Exception ex){
            return Response.status(500).build();
        }
    }



    @SecuredUserVendor
    @Path("upload-requests/{vendorId}")
    @GET
    public Response getUploadRequests(@HeaderParam("Authorization") String header, @PathParam(value = "vendorId") int vendorId){
        try {
            String sql = "select b from VendorUploadRequest b where b.vendorId = :value0 order by b.created desc";
            List<VendorUploadRequest> list = dao.getJPQLParams(VendorUploadRequest.class, sql , vendorId);
            return Response.status(200).entity(list).build();
        }catch (Exception ex){
            return Response.status(500).build();
        }
    }


    @SecuredUserVendor
    @Path("upload-special-offer-requests/{vendorId}")
    @GET
    public Response getUploadSpecialOfferRequests(@HeaderParam("Authorization") String header, @PathParam(value = "vendorId") int vendorId){
        try {
            String sql = "select b from VendorSpecialOfferUploadRequest b where b.vendorId = :value0 order by b.created desc";
            List<VendorSpecialOfferUploadRequest> list = dao.getJPQLParams(VendorSpecialOfferUploadRequest.class, sql , vendorId);
            return Response.status(200).entity(list).build();
        }catch (Exception ex){
            return Response.status(500).build();
        }
    }




    //update upload request
    @ValidApp
    @Path("upload-request")
    @PUT
    public Response updateRequestUpload(VendorUploadRequest uploadRequest){
        try{
            uploadRequest.setCompleted(new Date());
            dao.update(uploadRequest);
            return Response.status(201).build();
        }catch (Exception ee){
            return Response.status(500).build();
        }
    }


    @SecuredUser
    @GET
    @Path("vendor-uploads/pending")
    public Response getPendingVendorUploads(){
        try{
            String sql = "select b from VendorUploadRequests b where b.status = :value0  order by b.created desc";
            List<VendorUploadRequest> uploads = dao.getJPQLParams(VendorUploadRequest.class, sql, 'R');
            return Response.status(200).entity(uploads).build();
        }catch (Exception ex){
            return Response.status(500).build();
        }
    }


    @SecuredUser
    @GET
    @Path("vendor-uploads/stock")
    public Response getVendorUploads(){
        try{
            List<VendorUploadRequest> uploads = dao.getOrderByOriented(VendorUploadRequest.class, "created", "desc");
            return Response.status(200).entity(uploads).build();
        }catch (Exception ex){
            return Response.status(500).build();
        }
    }


    @SecuredUser
    @GET
    @Path("vendor-uploads/special-offer")
    public Response getVendorSpecialOfferUploads(){
        try{
            List<VendorSpecialOfferUploadRequest> uploads = dao.getOrderByOriented(VendorSpecialOfferUploadRequest.class, "created", "desc");
            return Response.status(200).entity(uploads).build();
        }catch (Exception ex){
            return Response.status(500).build();
        }
    }



    @ValidApp
    @Path("upload-special-offer-request")
    @PUT
    public Response updateSpecialOfferRequestUpload(VendorSpecialOfferUploadRequest uploadRequest){
        try{
            uploadRequest.setCompleted(new Date());
            dao.update(uploadRequest);
            return Response.status(201).build();
        }catch (Exception ee){
            return Response.status(500).build();
        }
    }

    @ValidApp
    @Path("special-offer-upload-request")
    @POST
    public Response requestUploadSpecialOffer(VendorSpecialOfferUploadRequest uploadRequest){
        try{
            Date date = Helper.addMinutes(new Date(), 5*-1);
            String jpql = "select b from VendorSpecialOfferUploadRequest b where b.branchId = :value0 and b.created > :value1 and b.vendorId = :value2 and b.uploadSource = :value3";
            List<VendorSpecialOfferUploadRequest> check = dao.getJPQLParams(VendorSpecialOfferUploadRequest.class, jpql, uploadRequest.getBranchId(), date, uploadRequest.getVendorId(), uploadRequest.getUploadSource());
            if(check.isEmpty()){
                uploadRequest.setCreated(new Date());
                dao.persist(uploadRequest);
                return Response.status(200).entity(uploadRequest).build();
            }
            else{
                return Response.status(409).build();
            }

        }catch (Exception ex){
            return Response.status(500).build();
        }
    }

    @SecuredVendor
    @Path("upload-request")
    @POST
    public Response requestUpload(VendorUploadRequest uploadRequest){
        try{
            Date date = Helper.addMinutes(new Date(), 5*-1);
            String jpql = "select b from VendorUploadRequest b where b.branchId = :value0 and b.created > :value1 and b.vendorId = :value2 and b.uploadSource = :value3";
            List<VendorUploadRequest> check = dao.getJPQLParams(VendorUploadRequest.class, jpql, uploadRequest.getBranchId(), date, uploadRequest.getVendorId(), uploadRequest.getUploadSource());
            if(check.isEmpty()){
                dao.persist(uploadRequest);
                return Response.status(200).entity(uploadRequest).build();
            }
            else{
                return Response.status(409).build();
            }
        }catch (Exception ex){
            return Response.status(500).build();
        }
    }


    @Asynchronous
    private void updateSpecialOfferStockAsync(UploadStock uploadStock){
        try{
            for(var vs : uploadStock.getSpecialOfferStocks()){
                vs.setPartNumber(Helper.undecorate(vs.getPartNumber()));
                String sql = "select b from VendorSpecialOfferStock b where b.partNumber = :value0 and b.vendorId = :value1 and b.brandName = :value2 and b.branchId = :value3";
                VendorSpecialOfferStock so = dao.findJPQLParams(VendorSpecialOfferStock.class, sql, vs.getPartNumber(), vs.getVendorId(), vs.getBrandName(), vs.getBranchId());
                if(so != null){
                    so.setCreated(uploadStock.getDate());
                    so.setQuantity(vs.getQuantity());
                    so.setSpecialPrice(vs.getSpecialPrice());
                    so.setCreatedBy(uploadStock.getCreatedBy());
                    so.setOfferEnd(vs.getOfferEnd());
                    so.setOfferStart(vs.getOfferEnd());
                    so.setCreatedByVendor(uploadStock.getCreatedByVendor());
                    if(so.getProductId() == 0){
                        String jpql = "select b from Product b where b.productNumber = :value0 and lower(b.brand.name) = :value1";
                        Product product = dao.findJPQLParams(Product.class, jpql, vs.getPartNumber(), vs.getBrandName().toLowerCase());
                        if(product != null){
                            so.setProductId(product.getId());
                        }
                    }
                    dao.update(so);
                }
                else{
                    vs.setCreated(uploadStock.getDate());
                    String jpql = "select b from Product b where b.productNumber = :value0 and lower(b.brand.name) = :value1";
                    Product product = dao.findJPQLParams(Product.class, jpql, vs.getPartNumber(), vs.getBrandName().toLowerCase());
                    if(product != null){
                        vs.setProductId(product.getId());
                        vs.setBrandName(product.getBrand().getName());
                    }
                    dao.persist(vs);
                }
            }//end for loop
            //delete anything before newdate for the same vendor, same branch
            Helper h = new Helper();
            String sql = "delete from prd_vendor_special_offer_stock where vendor_id = " + uploadStock.getVendorId() +
                    " and branch_id = " + uploadStock.getBranchId() + " and created < '" +  h.getDateFormat(uploadStock.getDate()) +"'";
            dao.updateNative(sql);
        }catch (Exception ex){
            System.out.println("an error occured");
        }
    }



    @Asynchronous
    private void updateStockAsync(UploadStock uploadStock){
        try{
            for(var vs : uploadStock.getVendorStocks()){
                vs.setPartNumber(Helper.undecorate(vs.getPartNumber()));
                String sql = "select b from VendorStock b where b.partNumber = :value0 and b.vendorId = :value1 and b.brandName = :value2 and b.branchId = :value3";
                VendorStock vendorStock = dao.findJPQLParams(VendorStock.class, sql, vs.getPartNumber(), vs.getVendorId(), vs.getBrandName(), vs.getBranchId());
                if(vendorStock != null){
                    vendorStock.setCreated(uploadStock.getDate());
                    vendorStock.setQuantity(vs.getQuantity());
                    vendorStock.setRetailPrice(vs.getRetailPrice());
                    vendorStock.setWholesalesPrice(vs.getWholesalesPrice());
                    vendorStock.setCreatedBy(uploadStock.getCreatedBy());
                    vendorStock.setCreatedByVendor(uploadStock.getCreatedByVendor());
                    if(vendorStock.getProductId() == 0){
                        String jpql = "select b from Product b where b.productNumber = :value0 and lower(b.brand.name) = :value1";
                        Product product = dao.findJPQLParams(Product.class, jpql, vs.getPartNumber(), vs.getBrandName().toLowerCase());
                        if(product != null){
                            vendorStock.setProductId(product.getId());
                        }
                    }
                    dao.update(vendorStock);
                }
                else{
                    vs.setCreated(uploadStock.getDate());
                    String jpql = "select b from Product b where b.productNumber = :value0 and lower(b.brand.name) = :value1";
                    Product product = dao.findJPQLParams(Product.class, jpql, vs.getPartNumber(), vs.getBrandName().toLowerCase());
                    if(product != null){
                        vs.setProductId(product.getId());
                        vs.setBrandName(product.getBrand().getName());
                    }
                    dao.persist(vs);
                }
            }//end for loop

            //delete anything before newdate for the same vendor, same branch
            if(uploadStock.isOverridePrevious()) {
                Helper h = new Helper();
                String sql = "delete from prd_vendor_stock where vendor_id = " + uploadStock.getVendorId() +
                        " and branch_id = " + uploadStock.getBranchId() + " and created < '" + h.getDateFormat(uploadStock.getDate()) + "'";
                dao.updateNative(sql);
            }

        }catch (Exception ex){
            System.out.println("an error occured");
        }
    }


    @SecuredUserVendor
    @POST
    @Path("search-availability")
    public Response searchAvailability(@HeaderParam("Authorization") String header, QvmSearchRequest sr) {
        try {
            List<QvmObject> fromSpecialOffer = searchVendorSpecialOffer(sr.getQuery(), sr.isAttachProduct());
            List<QvmObject> fromApi = searchLiveAPIs2(sr, header);
            List<QvmObject> fromStock = searchVendorStock(sr.getQuery(), sr.isAttachProduct());
            List<QvmObject> results = new ArrayList<>();
            results.addAll(fromSpecialOffer);
            results.addAll(fromApi);
            results.addAll(fromStock);
            return Response.status(200).entity(results).build();
        }catch (Exception ex){
            return Response.status(500).build();
        }
    }

    @SecuredUserVendor
    @POST
    @Path("search-parts")
    public Response searchParts(String query) {
        try {
            String partNumber = "%" + Helper.undecorate(query) + "%";
            String jpql = "select b from PublicProduct b where b.productNumber like :value0 and b.status =:value1";
            List<PublicProduct> publicProducts = dao.getJPQLParams(PublicProduct.class, jpql, partNumber, 'A');
            for(PublicProduct publicProduct : publicProducts){
                initPublicProduct(publicProduct);
            }
            return Response.status(200).entity(publicProducts).build();
        }catch (Exception ex){
            return Response.status(500).build();
        }
    }

    private List<QvmObject> searchLiveAPIs(QvmSearchRequest sr){
        List results = Collections.synchronizedList(new ArrayList<>());
        ExecutorService es = Executors.newFixedThreadPool(sr.getVendorCreds().size());
        for (int i = 0; i < sr.getVendorCreds().size(); i++) {
            final int ii = i;
            Runnable runnable = () -> {
                QvmVendorCredentials vendorCreds = sr.getVendorCreds().get(ii);
                int code = 200;
                try {
                    String endpoint = vendorCreds.getEndpointAddress() + sr.getQuery();
                    String header = "Bearer " + vendorCreds.getSecret();
                    Response r = getSecuredRequest(endpoint, header);
                    code = r.getStatus();
                    if(code == 200){
                        List<QvmObject> rs = r.readEntity(new GenericType<List<QvmObject>>() {
                        });
                        for (QvmObject result : rs) {
                            result.setSource('L');
                            result.setVendorId(vendorCreds.getVendorId());
                        }
                        results.addAll(rs);
                    }
                } catch (Exception ignore) {
                }
            };
            es.execute(runnable);
        }
        es.shutdown();
        while (!es.isTerminated()) ;
        //all threads ended
        return results;
    }



    private List<QvmObject> searchLiveAPIs2(QvmSearchRequest sr, String header) {
        List<QvmObject> results = Collections.synchronizedList(new ArrayList<>());
        for (int i = 0; i < sr.getVendorCreds().size(); i++) {
            QvmVendorCredentials vendorCreds = sr.getVendorCreds().get(i);
            List<QvmObject> qvmObjects = new ArrayList<>();
            QvmObject qvmObject = new QvmObject();
            qvmObject.setStatus('W');
            qvmObject.setVendorId(vendorCreds.getVendorId());
            qvmObject.setSource('L');
            qvmObjects.add(qvmObject);
            results.addAll(qvmObjects);
            async.callVendorAPI(vendorCreds, sr.getQuery(), sr.getRequesterId(), sr.getRequesterType(), header);//async
        }
        System.out.println("returning result");
        return results;
    }




    private List<QvmObject> searchVendorSpecialOffer(String query, boolean attachProduct){
        try{
            List<QvmObject> results = new ArrayList<>();
            //SEARRCH FROM SPECIAL OFFER
            String undecorated = "%" + Helper.undecorate(query) + "%";
            Helper h = new Helper();
            String todayString = h.getDateFormat(new Date(), "yyyy-MM-dd");
            String jpql = "select distinct on (part_number, brand_name) * from prd_vendor_special_offer_stock where part_number like '" + undecorated + "' and '" + todayString +"' between cast(offer_start_date as date) and cast(offer_end_date as date)";
            List<VendorSpecialOfferStock> soStocks = dao.getNative(VendorSpecialOfferStock.class, jpql);
            for (VendorSpecialOfferStock sos : soStocks) {
                QvmObject qvmObject = new QvmObject();
                qvmObject.setQpartsProducts(new ArrayList<>());
                if (attachProduct) {
                    PublicProduct product = dao.find(PublicProduct.class, sos.getProductId());
                    if(product != null ) {
                        initPublicProduct(product);
                        qvmObject.getQpartsProducts().add(product);
                    }
                }
                qvmObject.setSource('S');
                qvmObject.setVendorId(sos.getVendorId());
                qvmObject.setAvailable(true);
                qvmObject.setBrand(sos.getBrandName());
                qvmObject.setPartNumber(sos.getPartNumber());
                qvmObject.setSpecialOfferPrice(sos.getSpecialPrice());
                qvmObject.setLastUpdate(sos.getCreated());
                qvmObject.setAvailability(new ArrayList<>());
                qvmObject.setOfferEnd(sos.getOfferEnd());
                String sql = "select b from VendorSpecialOfferStock b where b.partNumber = :value0 and b.vendorId = :value1 and b.brandName = :value2 and cast(:value3 as date) between cast(b.offerStart as date) and cast(b.offerEnd as date)";
                List<VendorSpecialOfferStock> subs = dao.getJPQLParams(VendorSpecialOfferStock.class, sql, sos.getPartNumber(), sos.getVendorId(), sos.getBrandName(), new Date());
                for (VendorSpecialOfferStock vsos : subs) {
                    QvmAvailabilityRemote sa = new QvmAvailabilityRemote();
                    QvmBranch sb = new QvmBranch();
                    sb.setqBranchId(vsos.getBranchId());
                    sb.setqCityId(vsos.getCityId());
                    sa.setBranch(sb);
                    sa.setQuantity(vsos.getQuantity());
                    sa.setOfferEnd(vsos.getOfferEnd());
                    qvmObject.getAvailability().add(sa);
                }
                results.add(qvmObject);
            }
            return results;
        }catch (Exception ex){
            return new ArrayList<>();
        }
    }


    private List<QvmObject> searchVendorStock(String query, boolean attachProduct){
        try {
            List<QvmObject> results = new ArrayList<>();
            //SEARRCH FROM STOCK
            String undecorated = "%" + Helper.undecorate(query) + "%";
            String jpql = "select distinct on (part_number, brand_name) * from prd_vendor_stock where part_number like '" + undecorated + "'";
            List<VendorStock> vendorStocks = dao.getNative(VendorStock.class, jpql);
            for (VendorStock vendorStock : vendorStocks) {
                QvmObject searchResult = new QvmObject();
                searchResult.setQpartsProducts(new ArrayList<>());
                if (attachProduct) {
                    PublicProduct product = dao.find(PublicProduct.class, vendorStock.getProductId());
                    if(product != null) {
                        initPublicProduct(product);
                        searchResult.getQpartsProducts().add(product);
                    }
                }
                searchResult.setSource('U');
                searchResult.setVendorId(vendorStock.getVendorId());
                searchResult.setAvailable(true);
                searchResult.setBrand(vendorStock.getBrandName());
                searchResult.setPartNumber(vendorStock.getPartNumber());
                searchResult.setRetailPrice(vendorStock.getRetailPrice());
                searchResult.setWholesalesPrice(vendorStock.getWholesalesPrice());
                searchResult.setLastUpdate(vendorStock.getCreated());
                searchResult.setAvailability(new ArrayList<>());

                String sql = "select b from VendorStock b where b.partNumber = :value0 and b.vendorId = :value1 and b.brandName = :value2";
                List<VendorStock> subs = dao.getJPQLParams(VendorStock.class, sql, vendorStock.getPartNumber(), vendorStock.getVendorId(), vendorStock.getBrandName());
                for (VendorStock vs : subs) {
                    QvmAvailabilityRemote sa = new QvmAvailabilityRemote();
                    QvmBranch sb = new QvmBranch();
                    sb.setqBranchId(vs.getBranchId());
                    sb.setqCityId(vs.getCityId());
                    sa.setBranch(sb);
                    sa.setQuantity(vs.getQuantity());
                    searchResult.getAvailability().add(sa);
                }
                results.add(searchResult);
            }
            return results;
        }catch (Exception ex){
            return new ArrayList<>();
        }
    }


    private void initPublicProduct(PublicProduct product) {
        try {
            initPublicProductNoVariant(product);
            initVariants(product);
        } catch (Exception ex) {
            product = null;
        }
    }


    private void initPublicProductNoVariant(PublicProduct product){
        product.setSpecs(getPublicSpecs(product.getId()));
        product.setSalesPrice(getAveragedSalesPrice(product.getId()));
        product.setReviews(getPublicReviews(product.getId()));
        product.initImageLink();
        product.getBrand().initImageLink();
        product.setVariants(new ArrayList<>());
    }

    private void initVariants(PublicProduct product){
        //get variants
        String sql = "select distinct b from PublicProduct b where b.status =:value0 and b.id in (" +
                "select c.productId from Variant c where c.variantId =:value1) or b.id in " +
                "(select d.variantId from Variant d where d.productId =:value1)";
        List<PublicProduct> variants = dao.getJPQLParams(PublicProduct.class, sql, 'A', product.getId());
        for(PublicProduct variant : variants){
            initPublicProductNoVariant(variant);
        }
        product.setVariants(variants);

    }


    private List<PublicReview> getPublicReviews(long productId){
        String jpql = "select b from PublicReview b where b.productId = :value0 and b.status =:value1 order by b.created desc";
        List<PublicReview> reviews = dao.getJPQLParams(PublicReview.class, jpql, productId, 'A');
        return reviews;
    }

    private double getAveragedSalesPrice(long productId){
        String sql = "select avg(b.price + (b.price * b.salesPercentage)) from ProductPrice b where b.productId = :value0 and b.status = :value1";
        Number n = dao.findJPQLParams(Number.class, sql , productId, 'A');
        return n.doubleValue();
    }

    private List<PublicSpec> getPublicSpecs(long productId){
        List<PublicSpec> publicSpecs = new ArrayList<>();
        String sql = "select b from ProductSpec b where b.productId =:value0 and b.status =:value1 order by b.spec.id";
        List<ProductSpec> productSpecs = dao.getJPQLParams(ProductSpec.class, sql , productId, 'A');
        productSpecs.forEach(ps->{
            publicSpecs.add(ps.getPublicSpec());
        });
        return publicSpecs;
    }



    public Response getSecuredRequest(String link, String header) {
        Invocation.Builder b = ClientBuilder.newClient().target(link).request();
        b.header(HttpHeaders.AUTHORIZATION, header);
        Response r = b.get();
        return r;
    }


    public <T> Response postSecuredRequest(String link, T t, String authHeader) {
        Invocation.Builder b = ClientBuilder.newClient().target(link).request();
        b.header(HttpHeaders.AUTHORIZATION, authHeader);
        Response r = b.post(Entity.entity(t, "application/json"));
        return r;
    }



}
