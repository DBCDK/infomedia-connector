/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt or at https://opensource.dbc.dk/licenses/gpl-3.0/
 */

package dk.dbc.infomedia;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ArticleUsage {

    @JsonProperty("ArticleUsageCount")
    private int articleUsageCount;

    @JsonProperty("ArticleUsageType")
    private int articleUsageType;

    public int getArticleUsageCount() {
        return articleUsageCount;
    }

    public void setArticleUsageCount(int articleUsageCount) {
        this.articleUsageCount = articleUsageCount;
    }

    public int getArticleUsageType() {
        return articleUsageType;
    }

    public void setArticleUsageType(int articleUsageType) {
        this.articleUsageType = articleUsageType;
    }

    @Override
    public String toString() {
        return "ArticleUsage{" +
                "articleUsageCount=" + articleUsageCount +
                ", articleUsageType=" + articleUsageType +
                '}';
    }
}
