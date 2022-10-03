package com.me.mdm.server.device.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MetaDataModel
{
    @JsonProperty("total_record_count")
    private Integer totalCount;
    
    public Integer getTotalCount() {
        return this.totalCount;
    }
    
    public void setTotalCount(final Integer totalCount) {
        this.totalCount = totalCount;
    }
}
