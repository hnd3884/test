package com.me.mdm.server.device.api.model.apps;

import com.me.mdm.server.device.api.model.MetaDataModel;
import com.me.mdm.api.paging.model.PagingResponse;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppUpdatePolicyListModel
{
    private List<AppUpdatePolicyModel> appUpdatePolicyModels;
    private PagingResponse paging;
    private MetaDataModel metadata;
    
    public List<AppUpdatePolicyModel> getAppUpdatePolicyModels() {
        return this.appUpdatePolicyModels;
    }
    
    public void setAppUpdatePolicyModels(final List<AppUpdatePolicyModel> appUpdatePolicyModels) {
        this.appUpdatePolicyModels = appUpdatePolicyModels;
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
}
