package com.me.mdm.server.msp.sync;

import java.util.Iterator;
import java.util.List;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.queue.DCQueueData;

public class ProfileDeleteSyncHandler extends ProfilesSyncEngine
{
    ProfileDeleteSyncHandler(final DCQueueData dcQueueData) {
        super(dcQueueData);
    }
    
    @Override
    public void sync() {
        try {
            final List customerList = SyncConfigurationsUtil.getApplicableCustomers(this.customerId);
            ProfileDeleteSyncHandler.logger.log(Level.INFO, "Deleting profile {0} for customers {1}", new Object[] { this.parentProfileDO, customerList });
            for (final Long customerId : customerList) {
                ProfileDeleteSyncHandler.logger.log(Level.INFO, "Deleting profile for customer {0}", new Object[] { customerId });
                try {
                    final JSONObject childProfileDetails = this.getChildSpecificUVH(customerId);
                    final Long profileId = (Long)childProfileDetails.get("PROFILE_ID");
                    final String profileIdStr = String.valueOf(profileId);
                    final ProfileUtil profileUtil = new ProfileUtil();
                    if (profileUtil.isProfileDeleteSafe(profileIdStr)) {
                        final JSONObject requestJSON = new JSONObject();
                        requestJSON.put("CUSTOMER_ID", (Object)customerId);
                        requestJSON.put("LOGIN_ID", (Object)this.aaaLoginId);
                        requestJSON.put("profileIDs", (Object)profileIdStr);
                        profileUtil.deleteProfile(requestJSON);
                    }
                    else {
                        ProfileDeleteSyncHandler.logger.log(Level.SEVERE, "Unable to delete profile {0} for customer {1} as profile is not delete safe", new Object[] { profileId, customerId });
                    }
                }
                catch (final Exception ex) {
                    ProfileDeleteSyncHandler.logger.log(Level.SEVERE, "Exception in profileTrash for profileId {0}, customerId {1} {2}", new Object[] { this.profileIdentifier, customerId, ex });
                }
            }
        }
        catch (final Exception ex2) {
            ProfileDeleteSyncHandler.logger.log(Level.SEVERE, "Exception in ProfileDeleteSyncHandler {0} {1}", new Object[] { this.parentProfileDO, ex2 });
        }
    }
}
