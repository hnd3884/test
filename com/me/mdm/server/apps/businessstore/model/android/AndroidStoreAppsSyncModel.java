package com.me.mdm.server.apps.businessstore.model.android;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AndroidStoreAppsSyncModel
{
    private List<AndroidStoreAppSyncDetailsModel> apps;
    
    public List<AndroidStoreAppSyncDetailsModel> getApps() {
        return this.apps;
    }
    
    public void setApps(final List<AndroidStoreAppSyncDetailsModel> apps) {
        this.apps = apps;
    }
}
