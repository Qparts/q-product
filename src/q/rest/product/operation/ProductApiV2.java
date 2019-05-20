package q.rest.product.operation;


import q.rest.product.dao.DAO;
import q.rest.product.filter.SecuredCustomer;
import q.rest.product.filter.ValidApp;
import q.rest.product.helper.Helper;
import q.rest.product.helper.ProductSQLSearch;
import q.rest.product.model.contract.*;
import q.rest.product.model.entity.*;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.*;

@Path("/api/v2/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductApiV2 {


    @EJB
    private DAO dao;

    @ValidApp
    @Path("products/best-sellers")
    @GET
    public Response getBestSellers(@Context UriInfo info){
        try{
        //    String categoryId = info.getQueryParameters().getFirst("category");
          //  if(categoryId == null || categoryId == ""){

            //}

//            String sql = "select * from prd_product where status = 'A' ";

  //          if(categoryId != null){
    //            sql += " and id in (select id from prd_product_category where category_id = "+ categoryId + ")";
      //      }

        //    sql += " order by id desc limit 10";

            String sql = "select b from PublicProduct b where b.id in (:value0 , :value1, :value2, :value3, :value4, " +
                    ":value5, :value6, :value7, :value8, :value9)";
            List<PublicProduct> pbs = dao.getJPQLParams(PublicProduct.class, sql, 89536L, 4L, 103297L, 133718L, 90774L, 76068L, 133718L, 57L, 76069L, 5L);
            System.out.println(pbs.size());
            for(PublicProduct pb : pbs){
                initPublicProduct(pb);
            }
            return Response.status(200).entity(pbs).build();


        }catch (Exception ex){
            ex.printStackTrace();
            return Response.status(500).build();
        }
    }


    @ValidApp
    @Path("popular-brands/oil")
    @GET
    public Response getOilPopularBrands(){
        try{
            String sql = "select * from prd_brand a where a.id in (" +
                    " select distinct b.brand_id from prd_product b where b.id in(" +
                    " select product_id from prd_product_category where category_id in (" +
                    " WITH RECURSIVE nodes AS (" +
                    " SELECT s1.id, s1.parent_node" +
                    " FROM prd_category s1 WHERE parent_node = 9" +
                    " UNION" +
                    " SELECT s2.id, s2.parent_node" +
                    " FROM prd_category s2, nodes s1 WHERE s2.parent_node = s1.id" +
                    " )" +
                    " SELECT id FROM nodes" +
                    " )" +
                    " ) or b.id in (" +
                    " select product_id from prd_product_category where category_id = 9" +
                    " )" +
                    ") limit 6";

            List<PublicBrand> brands = dao.getNative(PublicBrand.class, sql);
            initBrandsLinks(brands);
            return Response.status(200).entity(brands).build();
        }catch (Exception ex){
            return Response.status(500).build();
        }
    }

    private void initBrandsLinks(List<PublicBrand> brands){
        for(PublicBrand brand : brands){
            brand.initImageLink();
        }
    }

    @ValidApp
    @Path("products/offers")
    @GET
    public Response getOffers(){
        try{
            String sql = "select * from prd_product where status = 'A' order by id asc limit 10";
            List<PublicProduct> pbs = dao.getNative(PublicProduct.class, sql);
            for(PublicProduct pb : pbs){
                initPublicProduct(pb);
            }
            return Response.status(200).entity(pbs).build();
        }catch (Exception ex){
            return Response.status(500).build();
        }
    }


    @SecuredCustomer
    @Path("review")
    @POST
    public Response writeReview(PublicReview pr){
        try{
            pr.setCreated(new Date());
            pr.setStatus('P');
            dao.persist(pr);
            return Response.status(201).build();
        }catch (Exception ex){
            return Response.status(500).build();
        }
    }



    @ValidApp
    @Path("product/{productId}")
    @GET
    public Response getProduct(@PathParam(value = "productId") long productId){
        try{
            String sql = "select b from PublicProduct b where b.id =:value0 and b.status =:value1 and b.id in (" +
                    "select c.productId from ProductPrice c where c.productId =:value0 and c.status =:value1)";
            PublicProduct product = dao.findJPQLParams(PublicProduct.class, sql, productId, 'A');
            if(product == null){
                return Response.status(404).build();
            }
            initPublicProduct(product);
            return Response.status(200).entity(product).build();

        }catch(Exception ex){
            return Response.status(500).build();
        }
    }



    @ValidApp
    @Path("search/general")
    @GET
    public Response searchProduct(@Context UriInfo info){
        try {
            String query = info.getQueryParameters().getFirst("query");
            if(query == null || query == ""){
                query = null;
            }

            String pageString = info.getQueryParameters().getFirst("page");
            if(pageString == null){
                pageString = "";
            }

            Map<String,Object> map = new HashMap<String, Object>();

            String sort = info.getQueryParameters().getFirst("sort");


            List<String> brandStringValues = info.getQueryParameters().get("Brands");
            List<Integer> brandValues = Helper.extractParams(brandStringValues);

            List<String> viscosityStringValues = info.getQueryParameters().get("Viscosity");
            List<Integer> viscosityProductIds = Helper.extractParams(viscosityStringValues);



            String categoryString = info.getQueryParameters().getFirst("category");

            int page = 1;
            int categoryId = 0;
            if(pageString != null && pageString != ""){
                page = Integer.parseInt(pageString);
            }

            if(categoryString != null && categoryString != ""){
                categoryId = Integer.parseInt(categoryString);
            }

            int offset = (page -1) * 16;
            int max = 16;

            ProductSQLSearch psql = new ProductSQLSearch(query, categoryId, brandValues, viscosityProductIds, max, offset);
            List<PublicProduct> products = dao.getNative(PublicProduct.class, psql.getProductSearchSql());
            Number searchSize = (Number) dao.getNative(psql.getProductSearchSizeSql()).get(0);
            List<PublicBrand> brands = dao.getNative(PublicBrand.class, psql.getBrandsSearch());
            List<Spec> specs = dao.getNative(Spec.class, psql.getSpecsSearch());
            List<ProductSpec> productSpecs = dao.getNative(ProductSpec.class, psql.getProductSpecsSearch());
            SearchResult searchResult = new SearchResult();
            searchResult.setResultSize(searchSize.intValue());

            for (PublicProduct pb : products) {
                initPublicProduct(pb);
                searchResult.getProducts().add(pb);
            }

            int filterIdIndex = 1;
            SearchFilter brandFilter = new SearchFilter();
            brandFilter.setFilterTitle("Brands");
            brandFilter.setFilterTitleAr("الماركة");
            brandFilter.setId(filterIdIndex);
            filterIdIndex++;
            for(PublicBrand brand : brands){
                brandFilter.addValues(brand.getName(), brand.getNameAr(), brand.getId());
            }
            if(brands.size() > 0){
                searchResult.getFilterObjects().add(brandFilter);
            }
            for(var spec : specs){
                SearchFilter specFilter = new SearchFilter();
                specFilter.setFilterTitle(spec.getName());
                specFilter.setFilterTitleAr(spec.getNameAr());
                specFilter.setId(filterIdIndex);
                spec.setProductSpecs(new ArrayList<>());
                for(var productSpec : productSpecs){
                    if(productSpec.getSpec().getId() == spec.getId()){
                        specFilter.addValues(productSpec.getValue(), productSpec.getValueAr(), productSpec.getProductId());
                    }
                }
                if(specFilter.getOptions().size() > 0) {
                    searchResult.getFilterObjects().add(specFilter);
                }
                filterIdIndex++;
            }

            for(var spec : specs){
                SearchFilter specFilter = new SearchFilter();
                specFilter.setFilterTitle(spec.getName());
                specFilter.setFilterTitleAr(spec.getNameAr());
                specFilter.setId(filterIdIndex);
                filterIdIndex++;
            }



            return Response.status(200).entity(searchResult).build();
        }catch(Exception ex){
            ex.printStackTrace();
            return Response.serverError().build();
        }
    }

    private void initSearchFilters(SearchResult searchResult){
    }


    private void initPublicProduct(PublicProduct product){
        try {
            initPublicProductNoVariant(product);
            initVariants(product);
        }catch(Exception ex){
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


}
