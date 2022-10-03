package com.me.mdm.server.apps.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppListModel
{
    @JsonProperty("identifiers")
    private List<String> identifierList;
    
    public List<String> getIdentifierList() {
        return this.identifierList;
    }
    
    public void setIdentifierList(final List<String> identifierList) {
        this.identifierList = identifierList;
    }
}
