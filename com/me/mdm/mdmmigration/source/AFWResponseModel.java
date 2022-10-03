package com.me.mdm.mdmmigration.source;

import com.me.mdm.server.device.api.model.MetaDataModel;
import com.me.mdm.api.paging.model.PagingResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AFWResponseModel
{
    @JsonProperty("data")
    private String data;
    @JsonProperty("tag")
    private String tag;
    @JsonProperty("code")
    private String code;
    private PagingResponse paging;
    private MetaDataModel metadata;
    @JsonProperty("delta-token")
    private String deltaToken;
    
    public String getData() {
        return this.data;
    }
    
    public void setData(final String data) {
        this.data = data;
    }
    
    public String getTag() {
        return this.tag;
    }
    
    public void setTag(final String tag) {
        this.tag = tag;
    }
    
    public PagingResponse getPaging() {
        return this.paging;
    }
    
    public void setPaging(final PagingResponse paging) {
        this.paging = paging;
    }
    
    public MetaDataModel getMetadata() {
        return this.metadata;
    }
    
    public void setMetadata(final MetaDataModel metadata) {
        this.metadata = metadata;
    }
    
    public String getDeltaToken() {
        return this.deltaToken;
    }
    
    public void setDeltaToken(final String deltaToken) {
        this.deltaToken = deltaToken;
    }
    
    public String getCode() {
        return this.code;
    }
    
    public void setCode(final String code) {
        this.code = code;
    }
}
