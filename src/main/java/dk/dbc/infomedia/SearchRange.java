/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt or at https://opensource.dbc.dk/licenses/gpl-3.0/
 */

package dk.dbc.infomedia;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class SearchRange {

    @JsonProperty("SearchFrom")
    private String searchFrom;

    @JsonProperty("SearchTo")
    private String searchTo;

    public SearchRange() {

    }

    public SearchRange(Instant searchFrom, Instant searchTo) {
        this.searchFrom = searchFrom.toString();
        this.searchTo = searchTo.toString();
    }

    public String getSearchFrom() {
        return searchFrom;
    }

    public void setSearchFrom(String searchFrom) {
        this.searchFrom = searchFrom;
    }

    public String getSearchTo() {
        return searchTo;
    }

    public void setSearchTo(String searchTo) {
        this.searchTo = searchTo;
    }

    @Override
    public String toString() {
        return "SearchRange{" +
                "searchFrom='" + searchFrom + '\'' +
                ", searchTo='" + searchTo + '\'' +
                '}';
    }
}
