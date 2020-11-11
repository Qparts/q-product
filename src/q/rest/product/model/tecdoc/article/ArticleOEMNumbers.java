package q.rest.product.model.tecdoc.article;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ArticleOEMNumbers {
    private String articleNumber;
    private int mfrId;
    private String mfrName;

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
}
