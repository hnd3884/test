package com.me.mdm.server.msp.sync;

import org.json.JSONObject;
import com.me.devicemanagement.framework.server.queue.DCQueueData;

public abstract class ProfilesSyncEngine extends BaseConfigurationsSyncEngine
{
    public String profileIdentifier;
    
    ProfilesSyncEngine(final DCQueueData dcQueueData) {
        super(dcQueueData);
        this.profileIdentifier = this.qData.optString("PROFILE_PAYLOAD_IDENTIFIER");
    }
    
    @Override
    public JSONObject getChildSpecificUVH(final Long customerID) throws Exception {
        final Long childProfileId = SyncConfigurationsUtil.getProfileIdFromProfileIdentifier(this.profileIdentifier, customerID);
        return new JSONObject().put("PROFILE_ID", (Object)childProfileId);
    }
    
    @Override
    public void setParentDO() throws Exception {
        this.parentProfileDO = SyncConfigurationsUtil.getProfileDO(this.profileIdentifier, this.customerId);
    }
    
    @Override
    public abstract void sync();
}
