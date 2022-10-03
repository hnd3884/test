package com.me.mdm.server.msp.sync;

import java.util.HashMap;
import org.json.JSONObject;
import java.util.Iterator;
import java.util.List;
import com.me.mdm.server.apps.AppTrashModeHandler;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.queue.DCQueueData;

public class AppDeletePermanentlySyncHandler extends AppsRestoreDeleteSyncEngine
{
    AppDeletePermanentlySyncHandler(final DCQueueData dcQueueData) {
        super(dcQueueData);
    }
    
    @Override
    public void setParentDO() throws Exception {
    }
    
    @Override
    public void sync() {
        try {
            AppDeletePermanentlySyncHandler.logger.log(Level.INFO, "App deleted permanently from parent customer {0} identifiers -> {1}", new Object[] { this.customerId, this.appIdentifiers });
            this.setParentDO();
            final List<Long> customerList = SyncConfigurationsUtil.getApplicableCustomers(this.customerId);
            for (final Long customerID : customerList) {
                CustomerInfoThreadLocal.setCustomerId(customerID.toString());
                try {
                    final JSONObject childSpecificUVH = this.getChildSpecificUVH(customerID);
                    AppDeletePermanentlySyncHandler.logger.log(Level.INFO, "App permanent delete for child customer {0} appIds -> {1}", new Object[] { customerID, childSpecificUVH });
                    final List packageIds = childSpecificUVH.getJSONArray("app_ids").toList();
                    final HashMap hashMap = AppsUtil.getInstance().getProfileIDFromPackageIdsForTrash(packageIds, customerID);
                    hashMap.put("packageIds", packageIds);
                    hashMap.put("CustomerID", customerID);
                    new AppTrashModeHandler().deleteMultipleAppFromTrash(hashMap);
                }
                catch (final Exception ex) {
                    AppDeletePermanentlySyncHandler.logger.log(Level.SEVERE, "Exception in AppDeletePermanentlySynHandler for customer {0} for props {1}", new Object[] { customerID, ex });
                }
            }
        }
        catch (final Exception ex2) {
            AppDeletePermanentlySyncHandler.logger.log(Level.SEVERE, "Exception in AppDeletePermanentlySynHandler {0} {1}", new Object[] { this.appIdentifiers, ex2 });
        }
    }
}
