package com.me.mdm.api.paging.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PagingResponse
{
    private String next;
    private String previous;
    
    @JsonProperty("next")
    public String getNext() {
        return this.next;
    }
    
    public void setNext(final String next) {
        this.next = next;
    }
    
    @JsonProperty("previous")
    public String getPrevious() {
        return this.previous;
    }
    
    public void setPrevious(final String previous) {
        this.previous = previous;
    }
}
