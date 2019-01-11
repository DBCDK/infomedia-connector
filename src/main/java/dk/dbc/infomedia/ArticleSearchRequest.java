package dk.dbc.infomedia;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ArticleSearchRequest {

    @JsonProperty("IqlQuery")
    private String iqlQuery;

    @JsonProperty("PagingParameter")
    private PagingParameter pagingParameter;

    @JsonProperty("SearchRange")
    private SearchRange searchRange;

    // We need to explicitly define a default constructor because we have defined a non-default constructur
    public ArticleSearchRequest() {

    }

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
