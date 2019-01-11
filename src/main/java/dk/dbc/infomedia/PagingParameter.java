/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt or at https://opensource.dbc.dk/licenses/gpl-3.0/
 */

package dk.dbc.infomedia;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PagingParameter {

    @JsonProperty("StartIndex")
    private int startIndex;

    @JsonProperty("Pagesize")
    private int pagesize;

    public PagingParameter() {
        this.startIndex = 0;
        this.pagesize = 400;
    }

    public PagingParameter(int startIndex, int pagesize) {
        this.startIndex = startIndex;
        this.pagesize = pagesize;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getPagesize() {
        return pagesize;
    }

    public void setPagesize(int pagesize) {
        this.pagesize = pagesize;
    }

    @Override
    public String toString() {
        return "PagingParameter{" +
                "startIndex=" + startIndex +
                ", pagesize=" + pagesize +
                '}';
    }
}
