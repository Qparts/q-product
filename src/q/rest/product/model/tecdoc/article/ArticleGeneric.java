package q.rest.product.model.tecdoc.article;

public class ArticleGeneric {
    private int genericArticleId;
    private String genericArticleDescription;
    private int legacyArticleId;

    public int getGenericArticleId() {
        return genericArticleId;
    }

    public void setGenericArticleId(int genericArticleId) {
        this.genericArticleId = genericArticleId;
    }

    public String getGenericArticleDescription() {
        return genericArticleDescription;
    }

    public void setGenericArticleDescription(String genericArticleDescription) {
        this.genericArticleDescription = genericArticleDescription;
    }

    public int getLegacyArticleId() {
        return legacyArticleId;
    }

    public void setLegacyArticleId(int legacyArticleId) {
        this.legacyArticleId = legacyArticleId;
    }
}
