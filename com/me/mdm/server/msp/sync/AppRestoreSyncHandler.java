package com.me.mdm.server.msp.sync;

import org.json.JSONObject;
import java.util.Iterator;
import java.util.List;
import com.me.mdm.server.apps.AppTrashModeHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.queue.DCQueueData;

public class AppRestoreSyncHandler extends AppsRestoreDeleteSyncEngine
{
    AppRestoreSyncHandler(final DCQueueData dcQueueData) {
        super(dcQueueData);
    }
    
    @Override
    public void setParentDO() throws Exception {
    }
    
    @Override
    public void sync() {
        try {
            AppRestoreSyncHandler.logger.log(Level.INFO, "Apps restored from trash from parent customer {0} identifiers -> {1}", new Object[] { this.customerId, this.appIdentifiers });
            this.setParentDO();
            final List<Long> customerList = SyncConfigurationsUtil.getApplicableCustomers(this.customerId);
            for (final Long customerID : customerList) {
                CustomerInfoThreadLocal.setCustomerId(customerID.toString());
                try {
                    final JSONObject childSpecificUVH = this.getChildSpecificUVH(customerID);
                    AppRestoreSyncHandler.logger.log(Level.INFO, "Apps restored from trash for child customer {0} appIds -> {1}", new Object[] { customerID, childSpecificUVH });
                    final List profileIds = childSpecificUVH.getJSONArray("profile_ids").toList();
                    new AppTrashModeHandler().restoreAppFromTrash(profileIds);
                }
                catch (final Exception ex) {
                    AppRestoreSyncHandler.logger.log(Level.SEVERE, "Exception in AppRestoreSyncHandler for customer {0} for props {1}", new Object[] { customerID, ex });
                }
            }
        }
        catch (final Exception ex2) {
            AppRestoreSyncHandler.logger.log(Level.SEVERE, "Exception in AppRestoreSyncHandler {0} {1}", new Object[] { this.appIdentifiers, ex2 });
        }
    }
}
