package com.me.mdm.server.profiles.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.me.mdm.server.device.api.model.MetaDataModel;
import com.me.mdm.api.paging.model.PagingResponse;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceProfilesModel
{
    private List<ProfileModel> profiles;
    private PagingResponse paging;
    private MetaDataModel metadata;
    @JsonProperty("delta-token")
    private String deltaToken;
    
    public List<ProfileModel> getProfiles() {
        return this.profiles;
    }
    
    public void setProfiles(final List<ProfileModel> devices) {
        this.profiles = devices;
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
}
