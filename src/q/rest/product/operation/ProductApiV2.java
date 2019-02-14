package q.rest.product.operation;


import q.rest.product.dao.DAO;
import q.rest.product.filter.ValidApp;
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
import java.util.ArrayList;
import java.util.List;

@Path("/api/v2/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductApiV2 {


    @EJB
    private DAO dao;

    @ValidApp
    @Path("products/best-sellers")
    @GET
    public Response getBestSellers(){
        try{
            String sql = "select * from prd_product where status = 'A' order by id desc limit 10";
            List<PublicProduct> pbs = dao.getNative(PublicProduct.class, sql);
            for(PublicProduct pb : pbs){
                initPublicProduct(pb);
            }
            return Response.status(200).entity(pbs).build();
        }catch (Exception ex){
            return Response.status(500).build();
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

            String sort = info.getQueryParameters().getFirst("sort");
            String brandKey = info.getQueryParameters().getFirst("Brands");
            String categoryString = info.getQueryParameters().getFirst("category");

            int page = 1;
            int categoryId = 0;
            if(pageString != null && pageString != ""){
                page = Integer.parseInt(pageString);
            }

            if(categoryString != null && categoryString != ""){
                categoryId = Integer.parseInt(categoryString);
            }

            int offset = (page -1) * 18;
            int max = page * 18;

            ProductSQLSearch psql = new ProductSQLSearch(query, categoryId, max, offset);

            System.out.println(psql.getProductSearchSql());

            List<PublicProduct> products = dao.getNative(PublicProduct.class, psql.getProductSearchSql());
            Number searchSize = (Number) dao.getNative(psql.getProductSearchSizeSql()).get(0);
            List<PublicBrand> brands = dao.getNative(PublicBrand.class, psql.getBrandsSearch());


            SearchResult searchResult = new SearchResult();
            searchResult.setResultSize(searchSize.intValue());
            for (PublicProduct pb : products) {
                initPublicProduct(pb);
                searchResult.getProducts().add(pb);
            }

            SearchFilter brandFilter = new SearchFilter();
            brandFilter.setFilterTitle("Brands");
            brandFilter.setFilterTitleAr("الماركة");
            for(PublicBrand brand : brands){
                brandFilter.addValues(brand.getName(), brand.getNameAr());
            }
            if(brands.size() > 0){
                searchResult.getFilterObjects().add(brandFilter);
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
