/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt or at https://opensource.dbc.dk/licenses/gpl-3.0/
 */

package dk.dbc.infomedia;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ArticleSearchRequest {

    @JsonProperty("IqlQuery")
    private String iqlQuery;

    @JsonProperty("PagingParameter")
    private PagingParameter pagingParameter;

    @JsonProperty("SearchRange")
    private SearchRange searchRange;

    public String getIqlQuery() {
        return iqlQuery;
    }

    public void setIqlQuery(String iqlQuery) {
        this.iqlQuery = iqlQuery;
    }

    public PagingParameter getPagingParameter() {
        return pagingParameter;
    }

    public void setPagingParameter(PagingParameter pagingParameter) {
        this.pagingParameter = pagingParameter;
    }

    public SearchRange getSearchRange() {
        return searchRange;
    }

    public void setSearchRange(SearchRange searchRange) {
        this.searchRange = searchRange;
    }

    @Override
    public String toString() {
        return "ArticleSearchRequest{" +
                "iqlQuery='" + iqlQuery + '\'' +
                ", pagingParameter=" + pagingParameter +
                ", searchRange=" + searchRange +
                '}';
    }
}
