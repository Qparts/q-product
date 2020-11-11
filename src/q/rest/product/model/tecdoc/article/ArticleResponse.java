package q.rest.product.model.tecdoc.article;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ArticleResponse {
    private int totalMatchingArticles;
    private int maxAllowedPage;
    private int status;
    private List<Articles> articles;
    private AssemblyGroupFacets assemblyGroupFacets;

    public int getTotalMatchingArticles() {
        return totalMatchingArticles;
    }

    public void setTotalMatchingArticles(int totalMatchingArticles) {
        this.totalMatchingArticles = totalMatchingArticles;
    }

    public int getMaxAllowedPage() {
        return maxAllowedPage;
    }

    public void setMaxAllowedPage(int maxAllowedPage) {
        this.maxAllowedPage = maxAllowedPage;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public List<Articles> getArticles() {
        return articles;
    }

    public void setArticles(List<Articles> articles) {
        this.articles = articles;
    }

    public AssemblyGroupFacets getAssemblyGroupFacets() {
        return assemblyGroupFacets;
    }

    public void setAssemblyGroupFacets(AssemblyGroupFacets assemblyGroupFacets) {
        this.assemblyGroupFacets = assemblyGroupFacets;
    }
}
