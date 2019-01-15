/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt or at https://opensource.dbc.dk/licenses/gpl-3.0/
 */

package dk.dbc.infomedia;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ArticleSearchResult {

    @JsonProperty("Articles")
    private List<Article> articles;

    @JsonProperty("NumFound")
    private int numFound;

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

    public int getNumFound() {
        return numFound;
    }

    public void setNumFound(int numFound) {
        this.numFound = numFound;
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

    @Override
    public String toString() {
        return "ArticleSearchResult{" +
                "articles=" + articles +
                ", numFound=" + numFound +
                ", pagingInfo='" + pagingInfo + '\'' +
                ", articleUsage=" + articleUsage +
                '}';
    }
}
