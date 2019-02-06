package q.rest.product.operation;


import q.rest.product.dao.DAO;
import q.rest.product.filter.ValidApp;
import q.rest.product.helper.Helper;
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
            String pageString = info.getQueryParameters().getFirst("page");
            if(pageString == null){
                pageString = "";
            }

            String sort = info.getQueryParameters().getFirst("sort");
            String brandKey = info.getQueryParameters().getFirst("Brands");
            String categoryString = info.getQueryParameters().getFirst("category");

            int page = 1;
            int categoryId = 0;
            if(pageString != null){
                page = Integer.parseInt(pageString);
            }

            if(categoryString != null){
                categoryId = Integer.parseInt(categoryString);
            }

            int offset = (page -1) * 5;
            int max = page * 5;

            String numbered = Helper.getNumberedQuery(query);
            String tagged = "%"+Helper.properTag(query)+"%";
            String lowered = "%"+ query.trim().toLowerCase() + "%";

            String sqlSize = "select count(b) from PublicProduct b where b.productNumber like :value2 "
                    + "or (lower(b.desc) like :value0 "
                    + "or lower(b.details) like :value0 "
                    + "or b.id in (select c.productId from ProductTag c where c.tag like :value1) "
                    + "or b.id in (select d.productId from ProductSpec d where lower(d.value) like :value0) "
                    + "or b.id in (select e.productId from ProductSpec e where  lower(e.valueAr) like :value0) "
                    + "or lower(b.brand.name) like :value0 "
                    + " or lower(b.brand.nameAr) like :value0 "
                    + "or b.id in (select f.productId from ProductCategory f where f.categoryId in ("
                    + "select g.id from Category g where lower(g.name) like :value0 or lower(g.nameAr) like :value0))) ";

            String sql = "select b from PublicProduct b where b.productNumber like :value2 "
                    + "or (lower(b.desc) like :value0 "
                    + "or lower(b.details) like :value0 "
                    + "or b.id in (select c.productId from ProductTag c where c.tag like :value1) "
                    + "or b.id in (select d.productId from ProductSpec d where lower(d.value) like :value0) "
                    + "or b.id in (select e.productId from ProductSpec e where lower(e.valueAr) like :value0) "
                    + "or lower(b.brand.name) like :value0 or lower(b.brand.nameAr) like :value0 "
                    + "or b.id in (select f.productId from ProductCategory f where f.categoryId in ("
                    + "select g.id from Category g where lower(g.name) like :value0 or lower(g.nameAr) like :value0))) ";

            String sqlBrands = "select distinct b.brand from PublicProduct b where b.productNumber like :value2 "
                    + "or (lower(b.desc) like :value0 "
                    + "or lower(b.details) like :value0 "
                    + "or b.id in (select c.productId from ProductTag c where c.tag like :value1) "
                    + "or b.id in (select d.productId from ProductSpec d where lower(d.value) like :value0) "
                    + "or b.id in (select e.productId from ProductSpec e where lower(e.valueAr) like :value0) "
                    + "or lower(b.brand.name) like :value0 or lower(b.brand.nameAr) like :value0 "
                    + "or b.id in (select f.productId from ProductCategory f where f.categoryId in ("
                    + "select g.id from Category g where lower(g.name) like :value0 or lower(g.nameAr) like :value0))) ";

            List<PublicProduct> products;
            Number searchSize;
            List<PublicBrand> brands;

            if(categoryId > 0){
                sql += " and b.id in ( select h.productId from ProductCategory h where h.categoryId = :value3)";
                sqlSize += " and b.id in ( select h.productId from ProductCategory h where h.categoryId = :value3)";
                sqlBrands += " and b.id in ( select h.productId from ProductCategory h where h.categoryId = :value3)";
                products = dao.getJPQLParamsOffsetMax(PublicProduct.class, sql, offset, max, lowered, tagged, numbered, categoryId);
                searchSize = dao.findJPQLParams(Number.class, sqlSize , lowered, tagged, numbered, categoryId);
                brands = dao.getJPQLParams(PublicBrand.class, sqlBrands , lowered, tagged, numbered, categoryId);
            }else{
                products = dao.getJPQLParamsOffsetMax(PublicProduct.class, sql, offset, max, lowered, tagged, numbered);
                searchSize = dao.findJPQLParams(Number.class, sqlSize, lowered, tagged, numbered);
                brands = dao.getJPQLParams(PublicBrand.class, sqlBrands, lowered, tagged, numbered);
            }

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
            product.setSpecs(getPublicSpecs(product.getId()));
            product.setSalesPrice(getAveragedSalesPrice(product.getId()));
            product.setReviews(getPublicReviews(product.getId()));
            product.initImageLink();
            product.getBrand().initImageLink();
        }catch(Exception ex){
            product = null;
        }
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
