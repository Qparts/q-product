package q.rest.product.operation;


import q.rest.product.dao.DAO;
import q.rest.product.filter.SecuredVendor;
import q.rest.product.helper.Helper;
import q.rest.product.model.contract.PublicProduct;
import q.rest.product.model.contract.PublicReview;
import q.rest.product.model.contract.PublicSpec;
import q.rest.product.model.entity.Product;
import q.rest.product.model.entity.ProductSpec;
import q.rest.product.model.qvm.*;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@Path("/qvm/api/v2/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductQvmApiV2 {

    @EJB
    private DAO dao;

    @SecuredVendor
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
            Helper h = new Helper();
            String sql = "delete from prd_vendor_stock where vendor_id = " + uploadStock.getVendorId() +
                    " and branch_id = " + uploadStock.getBranchId() + " and created < '" +  h.getDateFormat(uploadStock.getDate()) +"'";
            dao.updateNative(sql);

        }catch (Exception ex){
            System.out.println("an error occured");
        }
    }


    private void addMissinProduct(List<QvmObject> results, String query){
        //find other products
        Set<PublicProduct> allProductsAdded = new HashSet<>();
        //add from q-parts if already not added
//        for (QvmObject obj : results) {
  //          if (obj.getQpartsProducts() != null) {
    //            allProductsAdded.addAll(obj.getQpartsProducts());
      //      }
        //}
        String partNumber = "%" + Helper.undecorate(query) + "%";
       // StringBuilder sql = new StringBuilder("select b from PublicProduct b where b.productNumber like :value0 and b.status =:value1 and b.id not in (0 ");
        //for(PublicProduct pp : allProductsAdded){
          //  sql.append(",").append(pp.getId());
        //}
        //sql.append(")");
        String sql = "select b from PublicProduct b where b.productNumber like :value0 and b.status =:value1";
        List<PublicProduct> pps = dao.getJPQLParams(PublicProduct.class, sql, partNumber, 'A');
        for(PublicProduct pp : pps){
            QvmObject qvmObject = new QvmObject();
            qvmObject.setAvailability(new ArrayList<>());
            qvmObject.setAvailable(false);
            qvmObject.setBrand(pp.getBrand().getName());
            qvmObject.setVendorId(0);
            qvmObject.setSource('Q');
            qvmObject.setRetailPrice(pp.getSalesPrice());
            qvmObject.setWholesalesPrice(pp.getSalesPrice());
            qvmObject.setPartNumber(pp.getProductNumber());
            initPublicProduct(pp);
            List<PublicProduct> list = new ArrayList<>();
            list.add(pp);
            qvmObject.setQpartsProducts(list);
            results.add(qvmObject);
        }
    }


    @SecuredVendor
    @POST
    @Path("search-availability")
    public Response searchAvailability(QvmSearchRequest sr) {
        try {
            List<QvmObject> results = new ArrayList<>();
            searchLiveAPis(results, sr);
            searchVendorStock(results, sr.getQuery(), sr.isAttachProduct());
            addMissinProduct(results, sr.getQuery());
            return Response.status(200).entity(results).build();
        }catch (Exception ex){
            return Response.status(500).build();
        }
    }

    @SecuredVendor
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

    private void searchLiveAPis(List<QvmObject> results, QvmSearchRequest sr){
        try {
            for (QvmVendorCredentials cred : sr.getVendorCreds()) {
                String endpoint = cred.getEndpointAddress() + "search/" + sr.getQuery();
                String header = "Bearer " + cred.getSecret();
                Response r = getSecuredRequest(endpoint, header);
                if (r.getStatus() == 200) {
                    List<QvmObject> rs = r.readEntity(new GenericType<List<QvmObject>>() {
                    });
                    System.out.println(rs.size());
                    for (QvmObject result : rs) {
                        result.setSource('L');
                        if (sr.isAttachProduct()) {
                            String partNumber = Helper.undecorate(result.getPartNumber());
                            String jpql = "select b from PublicProduct b where b.productNumber = :value0 and b.status =:value1";
                            List<PublicProduct> publicProducts = dao.getJPQLParams(PublicProduct.class, jpql, partNumber, 'A');
                            for (PublicProduct publicProduct : publicProducts) {
                                initPublicProduct(publicProduct);
                            }
                            result.setQpartsProducts(publicProducts);
                        }
                        result.setVendorId(cred.getVendorId());
                    }
                    results.addAll(rs);
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }



    private void searchVendorStock(List<QvmObject> results, String query, boolean attachProduct){
        //SEARRCH FROM STOCK
        String undecorated = "%" + Helper.undecorate(query) +"%";
        String jpql = "select distinct on (part_number, brand_name) * from prd_vendor_stock where part_number like '" + undecorated + "'" ;
//        List<VendorStock> vendorStocks = dao.getJPQLParams(VendorStock.class, jpql, undecorated);
        List<VendorStock> vendorStocks= dao.getNative(VendorStock.class, jpql);
        for(VendorStock vendorStock : vendorStocks){
            QvmObject searchResult = new QvmObject();
            searchResult.setQpartsProducts(new ArrayList<>());
            if(attachProduct) {
                PublicProduct product = dao.find(PublicProduct.class, vendorStock.getProductId());
                initPublicProduct(product);
                searchResult.getQpartsProducts().add(product);
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

            String sql = "select b from VendorStock b where b.partNumber like :value0";
            List<VendorStock> subs = dao.getJPQLParams(VendorStock.class, sql , undecorated);
            for(VendorStock vs : subs){
                QvmAvailability sa = new QvmAvailability();
                QvmBranch sb = new QvmBranch();
                sb.setqBranchId(vs.getBranchId());
                sb.setqCityId(vs.getCityId());
                sa.setBranch(sb);
                sa.setQuantity(vs.getQuantity());
                searchResult.getAvailability().add(sa);
            }
            results.add(searchResult);
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
}
