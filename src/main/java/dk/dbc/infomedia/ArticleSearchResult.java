package dk.dbc.infomedia;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ArticleSearchResult {

    @JsonProperty("Articles")
    private List<Article> articles;

    @JsonProperty("NumFound")
    private int count;

    @JsonProperty("PagingInfo")
    private String pagingInfo;

    @JsonProperty("ArticleUsage")
    private ArticleUsage articleUsage;

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getPagingInfo() {
        return pagingInfo;
    }

    public void setPagingInfo(String pagingInfo) {
        this.pagingInfo = pagingInfo;
    }

    public ArticleUsage getArticleUsage() {
        return articleUsage;
    }

    public void setArticleUsage(ArticleUsage articleUsage) {
        this.articleUsage = articleUsage;
    }

    public List<String> getArticleIds() {
        List<String> res = new ArrayList<>();

        for (Article article : articles) {
            res.add(article.getArticleId());
        }

        return res;
    }

}
