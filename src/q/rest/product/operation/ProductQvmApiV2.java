package q.rest.product.operation;


import q.rest.product.dao.DAO;
import q.rest.product.filter.SecuredVendor;
import q.rest.product.helper.Helper;
import q.rest.product.model.contract.PublicProduct;
import q.rest.product.model.contract.PublicReview;
import q.rest.product.model.contract.PublicSpec;
import q.rest.product.model.contract.SearchResult;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Path("/qvm/api/v2/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductQvmApiV2 {

    @EJB
    private DAO dao;


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
            Date newDate = new Date();
            for(var vs : uploadStock.getVendorStocks()){
                vs.setPartNumber(Helper.undecorate(vs.getPartNumber()));
                String sql = "select b from VendorStock b where b.partNumber = :value0 and b.vendorId = :value1 and b.brandName = :value2 and b.branchId = :value3";
                VendorStock vendorStock = dao.findJPQLParams(VendorStock.class, sql, vs.getPartNumber(), vs.getVendorId(), vs.getBrandName(), vs.getBranchId());
                if(vendorStock != null){
                    vendorStock.setCreated(newDate);
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
                    vs.setCreated(newDate);
                    System.out.println(vs.getPartNumber());
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
                    " and branch_id = " + uploadStock.getBranchId() + " and created < '" +  h.getDateFormat(newDate) +"'";
            dao.updateNative(sql);

        }catch (Exception ex){
            System.out.println("an error occured");
        }
    }





//    @SecuredVendor
    @POST
    @Path("search")
    public Response search(QvmSearchRequest sr) {
        try {
            List<QvmSearchResult> results = new ArrayList<>();
            for (QvmVendorCredentials cred : sr.getVendorCreds()) {
                String endpoint = cred.getEndpointAddress() + "search/" + sr.getQuery();
                String header = "Bearer " + cred.getSecret();
                Response r = getSecuredRequest(endpoint, header);
                if (r.getStatus() == 200) {
                    List<QvmSearchResult> rs = r.readEntity(new GenericType<List<QvmSearchResult>>() {
                    });
                    for (QvmSearchResult result : rs) {
                        result.setSource('L');
                        String partNumber = Helper.undecorate(result.getPartNumber());
                        String jpql = "select b from PublicProduct b where b.productNumber = :value0 and b.status =:value1";
                        List<PublicProduct> publicProducts = dao.getJPQLParams(PublicProduct.class, jpql, partNumber, 'A');
                        for(PublicProduct publicProduct : publicProducts){
                            initPublicProduct(publicProduct);
                        }
                        result.setQpartsProducts(publicProducts);
                        result.setVendorId(cred.getVendorId());
                    }
                    results.addAll(rs);
                }
            }

            //SEARRCH FROM STOCK
            String jpql = "select b from VendorStock b where b.partNumber like :value0";
            List<VendorStock> vendorStocks = dao.getJPQLParams(VendorStock.class, jpql, "%" + Helper.undecorate(sr.getQuery()) +"%");
            for(VendorStock vendorStock : vendorStocks){
                QvmSearchResult searchResult = new QvmSearchResult();
                searchResult.setQpartsProducts(new ArrayList<>());
                PublicProduct product = dao.find(PublicProduct.class, vendorStock.getProductId());
                initPublicProduct(product);
                searchResult.setSource('U');
                searchResult.getQpartsProducts().add(product);
                searchResult.setVendorId(vendorStock.getVendorId());
                searchResult.setAvailable(true);
                searchResult.setBrand(vendorStock.getBrandName());
                searchResult.setPartNumber(vendorStock.getPartNumber());
                searchResult.setRetailPrice(vendorStock.getRetailPrice());
                searchResult.setWholesalesPrice(vendorStock.getWholesalesPrice());
                searchResult.setLastUpdate(vendorStock.getCreated());
                searchResult.setAvailability(new ArrayList<>());
                SearchAvailability sa = new SearchAvailability();
                SearchBranch sb = new SearchBranch();
                sb.setqBranchId(vendorStock.getBranchId());
                sb.setqCityId(vendorStock.getCityId());
                sa.setBranch(sb);
                sa.setQuantity(vendorStock.getQuantity());
                searchResult.getAvailability().add(sa);
                results.add(searchResult);
            }
            return Response.status(200).entity(results).build();
        }catch (Exception ex){
            return Response.status(500).build();
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
