package q.rest.product.model.tecdoc.article;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Articles {
    private int dataSupplierId;
    private String articleNumber;
    private int mfrId;
    private String mfrName;
    private ArticleMisc misc;
    private List<ArticleGeneric> genericArticles;
    private Object articleText;
    private String[] gtins;
    private Object tradeNumbers;
    private List<ArticleOEMNumbers> oemNumbers;
    private Object replacesArticles;
    private Object replacedByArticles;
    private List<ArticleCriteria> articleCriteria;
    private List<ArticleLinkage> linkages;
    private Object pdfs;
    private List<ArticleImage> images;
    private Object comparableNumbers;
    private Object links;
    private int totalLinkages;
    private Object prices;

    public int getDataSupplierId() {
        return dataSupplierId;
    }

    public void setDataSupplierId(int dataSupplierId) {
        this.dataSupplierId = dataSupplierId;
    }

    public String getArticleNumber() {
        return articleNumber;
    }

    public void setArticleNumber(String articleNumber) {
        this.articleNumber = articleNumber;
    }

    public int getMfrId() {
        return mfrId;
    }

    public void setMfrId(int mfrId) {
        this.mfrId = mfrId;
    }

    public String getMfrName() {
        return mfrName;
    }

    public void setMfrName(String mfrName) {
        this.mfrName = mfrName;
    }

    public ArticleMisc getMisc() {
        return misc;
    }

    public void setMisc(ArticleMisc misc) {
        this.misc = misc;
    }

    public List<ArticleGeneric> getGenericArticles() {
        return genericArticles;
    }

    public void setGenericArticles(List<ArticleGeneric> genericArticles) {
        this.genericArticles = genericArticles;
    }

    public Object getArticleText() {
        return articleText;
    }

    public void setArticleText(Object articleText) {
        this.articleText = articleText;
    }

    public String[] getGtins() {
        return gtins;
    }

    public void setGtins(String[] gtins) {
        this.gtins = gtins;
    }

    public Object getTradeNumbers() {
        return tradeNumbers;
    }

    public void setTradeNumbers(Object tradeNumbers) {
        this.tradeNumbers = tradeNumbers;
    }

    public List<ArticleOEMNumbers> getOemNumbers() {
        return oemNumbers;
    }

    public void setOemNumbers(List<ArticleOEMNumbers> oemNumbers) {
        this.oemNumbers = oemNumbers;
    }

    public Object getReplacesArticles() {
        return replacesArticles;
    }

    public void setReplacesArticles(Object replacesArticles) {
        this.replacesArticles = replacesArticles;
    }

    public Object getReplacedByArticles() {
        return replacedByArticles;
    }

    public void setReplacedByArticles(Object replacedByArticles) {
        this.replacedByArticles = replacedByArticles;
    }

    public List<ArticleCriteria> getArticleCriteria() {
        return articleCriteria;
    }

    public void setArticleCriteria(List<ArticleCriteria> articleCriteria) {
        this.articleCriteria = articleCriteria;
    }

    public List<ArticleLinkage> getLinkages() {
        return linkages;
    }

    public void setLinkages(List<ArticleLinkage> linkages) {
        this.linkages = linkages;
    }

    public Object getPdfs() {
        return pdfs;
    }

    public void setPdfs(Object pdfs) {
        this.pdfs = pdfs;
    }

    public List<ArticleImage> getImages() {
        return images;
    }

    public void setImages(List<ArticleImage> images) {
        this.images = images;
    }

    public Object getComparableNumbers() {
        return comparableNumbers;
    }

    public void setComparableNumbers(Object comparableNumbers) {
        this.comparableNumbers = comparableNumbers;
    }

    public Object getLinks() {
        return links;
    }

    public void setLinks(Object links) {
        this.links = links;
    }

    public int getTotalLinkages() {
        return totalLinkages;
    }

    public void setTotalLinkages(int totalLinkages) {
        this.totalLinkages = totalLinkages;
    }

    public Object getPrices() {
        return prices;
    }

    public void setPrices(Object prices) {
        this.prices = prices;
    }
}
