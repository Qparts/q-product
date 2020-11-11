package q.rest.product.model.tecdoc.article;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ArticleLinkage {
    private int linkageTargetTypeId;
    private int linkageTargetId;
    private long legacyArticleLinkId;
    private int genericArticleId;
    private String genericArticleDescription;
    private Object linkageCriteria;
    private Object linkageText;

    public int getLinkageTargetTypeId() {
        return linkageTargetTypeId;
    }

    public void setLinkageTargetTypeId(int linkageTargetTypeId) {
        this.linkageTargetTypeId = linkageTargetTypeId;
    }

    public int getLinkageTargetId() {
        return linkageTargetId;
    }

    public void setLinkageTargetId(int linkageTargetId) {
        this.linkageTargetId = linkageTargetId;
    }

    public long getLegacyArticleLinkId() {
        return legacyArticleLinkId;
    }

    public void setLegacyArticleLinkId(long legacyArticleLinkId) {
        this.legacyArticleLinkId = legacyArticleLinkId;
    }

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

    public Object getLinkageCriteria() {
        return linkageCriteria;
    }

    public void setLinkageCriteria(Object linkageCriteria) {
        this.linkageCriteria = linkageCriteria;
    }

    public Object getLinkageText() {
        return linkageText;
    }

    public void setLinkageText(Object linkageText) {
        this.linkageText = linkageText;
    }
}
