package com.me.mdm.server.msp.sync;

import org.json.JSONObject;
import java.util.Iterator;
import java.util.List;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.queue.DCQueueData;

public class ProfileTrashSyncHandler extends ProfilesSyncEngine
{
    ProfileTrashSyncHandler(final DCQueueData dcQueueData) {
        super(dcQueueData);
    }
    
    @Override
    public void sync() {
        try {
            this.setParentDO();
            final List customerList = SyncConfigurationsUtil.getApplicableCustomers(this.customerId);
            ProfileTrashSyncHandler.logger.log(Level.INFO, "Trashing profile {0} for customers {1}", new Object[] { this.parentProfileDO, customerList });
            for (final Long customerId : customerList) {
                ProfileTrashSyncHandler.logger.log(Level.INFO, "Trashing profile for customer {0}", new Object[] { customerId });
                try {
                    final JSONObject childProfileDetails = this.getChildSpecificUVH(customerId);
                    final Long profileId = (Long)childProfileDetails.get("PROFILE_ID");
                    final ProfileUtil profileUtil = new ProfileUtil();
                    profileUtil.moveProfilesToTrash(String.valueOf(profileId), customerId, this.aaaLoginId, this.profileType);
                }
                catch (final Exception ex) {
                    ProfileTrashSyncHandler.logger.log(Level.SEVERE, "Exception in profileTrash for profileId {0}, customerId {1} {2}", new Object[] { this.profileIdentifier, customerId, ex });
                }
            }
        }
        catch (final Exception ex2) {
            ProfileTrashSyncHandler.logger.log(Level.SEVERE, "Exception in profileTrashSyncHandler {0} {1}", new Object[] { this.parentProfileDO, ex2 });
        }
    }
}
