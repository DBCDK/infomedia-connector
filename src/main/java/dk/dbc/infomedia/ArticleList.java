/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt or at https://opensource.dbc.dk/licenses/gpl-3.0/
 */

package dk.dbc.infomedia;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ArticleList {

    @JsonProperty("Articles")
    private List<Article> articles;

    @JsonProperty("ArticleUsage")
    private ArticleUsage articleUsage;

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    @Override
    public String toString() {
        return "ArticleList{" +
                "articles=" + articles +
                ", articleUsage=" + articleUsage +
                '}';
    }
}
