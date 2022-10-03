package com.me.mdm.server.apps.blocklist.model;

import com.me.mdm.server.device.api.model.DeviceDetailsModel;
import com.me.mdm.api.model.GroupDetails;
import java.util.List;
import com.me.mdm.api.paging.model.PagingResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.me.mdm.server.device.api.model.MetaDataModel;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BlocklistResponsePOJO
{
    private MetaDataModel metadata;
    @JsonProperty("delta-token")
    private String deltaToken;
    private PagingResponse paging;
    @JsonProperty("groups")
    List<GroupDetails> groups;
    @JsonProperty("devices")
    List<DeviceDetailsModel> devices;
    @JsonProperty("apps")
    List<BlocklistedAppDetailsByDevice> apps;
    
    public void setGroups(final List<GroupDetails> groupDetails) {
        this.groups = groupDetails;
    }
    
    public List<GroupDetails> getGroups() {
        return this.groups;
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
    
    public PagingResponse getPaging() {
        return this.paging;
    }
    
    public void setPaging(final PagingResponse paging) {
        this.paging = paging;
    }
    
    public List<DeviceDetailsModel> getDevices() {
        return this.devices;
    }
    
    public void setDevices(final List<DeviceDetailsModel> devices) {
        this.devices = devices;
    }
    
    public List<BlocklistedAppDetailsByDevice> getApps() {
        return this.apps;
    }
    
    public void setApps(final List<BlocklistedAppDetailsByDevice> apps) {
        this.apps = apps;
    }
}
