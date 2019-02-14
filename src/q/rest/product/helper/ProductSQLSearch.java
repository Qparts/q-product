package q.rest.product.helper;

public class ProductSQLSearch {
    private final static String OR = " or ";
    private final static String AND = " and ";
    private final static String NOTHING = "";
    private final String query;
    private String lowered;
    private String tagged;
    private String numbered;
    private final int categoryId;
    private final int max;
    private final int offset;
    private String productSearchSql;
    private String productSearchSizeSql;
    private String brandsSearch;

    public ProductSQLSearch(String query, int categoryId, int max, int offset) {
        if (query != null) {
            this.query = query;
            this.numbered = "'" + Helper.getNumberedQuery(query) + "'";
            this.tagged = "'%" + Helper.properTag(query) + "%'";
            this.lowered = "'%" + query.trim().toLowerCase() + "%'";
        } else {
            this.query = null;
        }
        this.categoryId = categoryId;
        this.max = max;
        this.offset = offset;

        initProductSearch();
        initProductSearchSize();
        initBrandsSearch();
    }

    private String getCommonSql(){
        return productNumber(AND)
                + inCategoryId(AND)
                + inCategoryChildren(OR)
                + likeDesc((categoryId > 0 ? AND : OR) + " (")
                + likeDetails(OR)
                + likeTag(OR)
                + inLikeSpec(OR)
                + inLikeBrand(OR)
                + inLikeCategory(OR) + (query != null ? ")" : "" );
    }

    private void initBrandsSearch() {
        brandsSearch = "select * from prd_brand where id in (select distinct b.brand_id from prd_product b where (b.status = 'A' ";
        brandsSearch += getCommonSql();
        brandsSearch += "))";
    }

    private void initProductSearchSize() {
        productSearchSizeSql = "select count(*) from prd_product b where b.status = 'A' ";
        productSearchSizeSql += getCommonSql();
    }

    private void initProductSearch() {
        productSearchSql = "select * from prd_product b where b.status = 'A' ";
        productSearchSql += getCommonSql();
        productSearchSql += offsetLimit();
    }

    private String offsetLimit() {
        return " offset " + offset + " limit " + max;
    }


    private String productNumber(String connector) {
        return  (query != null) ? connector + " b.product_number like " + numbered : "";
    }

    private String likeTag(String connector) {

        return (query != null) ? connector + " b.id in (select c.product_id from prd_product_tag c where c.tag like " + tagged + ") " : "";
    }

    private String likeDesc(String connector) {
        return query != null ? connector + " lower(b.product_desc) like " + lowered + " " : "";
    }

    private String likeDetails(String connector ) {
        return query != null ? connector + " lower(b.details) like " + lowered + " " : "";
    }


    private String inLikeSpec(String connector) {
        return (query != null) ? connector + " b.id in (select d.product_id from prd_product_specification d where lower(d.value) like " + lowered + " " +
                "or lower(d.value_ar) like " + lowered + ") " : "";
    }

    private String inLikeBrand(String connector) {
        return (query != null) ? connector + " b.brand_id in (select br.id from prd_brand br where lower(br.name) like " + lowered
                + " or lower(br.name_ar) like " + lowered + ") " : "";
    }

    private String inLikeCategory(String connector) {
        return (query != null) ? connector + "  b.id in (select f.product_id from prd_product_category f where f.category_id in ("
                + " select g.id from prd_category g where lower(g.name) like " + lowered + " or lower(g.name_ar) like " + lowered + ")) " : "";
    }

    private String inCategoryId(String connector) {
        return categoryId > 0 ? connector + " (b.id in (select product_id from prd_product_category where category_id = " + categoryId + ") " : "";
    }


    private String inCategoryChildren(String connector) {
        return (categoryId > 0) ?
            connector + " b.id in (select product_id from prd_product_category where category_id in (" +
                    "WITH RECURSIVE nodes AS (" +
                    "    SELECT s1.id, s1.parent_node" +
                    "    FROM prd_category s1 WHERE parent_node = " + categoryId +
                    "        UNION" +
                    "    SELECT s2.id, s2.parent_node" +
                    "    FROM prd_category s2, nodes s1 WHERE s2.parent_node = s1.id" +
                    ")" +
                    "SELECT id FROM nodes)))" : "";
        }


    public String getProductSearchSql() {
        return productSearchSql;
    }

    public String getProductSearchSizeSql() {
        return productSearchSizeSql;
    }

    public String getBrandsSearch() {
        return brandsSearch;
    }
}
