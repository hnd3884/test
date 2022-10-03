package com.me.mdm.server.msp.sync;

import org.json.JSONObject;
import java.util.Iterator;
import java.util.List;
import com.me.mdm.server.apps.AppTrashModeHandler;
import org.apache.commons.lang.StringUtils;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.queue.DCQueueData;

public class AppTrashSyncHandler extends AppsRestoreDeleteSyncEngine
{
    Boolean isSoftDelete;
    
    AppTrashSyncHandler(final DCQueueData dcQueueData) {
        super(dcQueueData);
        this.isSoftDelete = this.qData.optBoolean("softdelete", false);
    }
    
    @Override
    public void setParentDO() throws Exception {
    }
    
    @Override
    public void sync() {
        try {
            AppTrashSyncHandler.logger.log(Level.INFO, "App moved to trash from parent customer {0} identifiers -> {1}", new Object[] { this.customerId, this.appIdentifiers });
            this.setParentDO();
            List<Long> applicableCustomers;
            if (this.childCustomerId == -1L) {
                applicableCustomers = SyncConfigurationsUtil.getApplicableCustomers(this.customerId);
            }
            else {
                final Long[] array;
                applicableCustomers = new ArrayList<Long>(Arrays.asList(array));
                array = new Long[] { this.childCustomerId };
            }
            final List<Long> customerList = applicableCustomers;
            for (final Long customerID : customerList) {
                CustomerInfoThreadLocal.setCustomerId(customerID.toString());
                try {
                    final JSONObject childSpecificUVH = this.getChildSpecificUVH(customerID);
                    AppTrashSyncHandler.logger.log(Level.INFO, "App moved to trash for child customer {0} appIds -> {1}", new Object[] { customerID, childSpecificUVH });
                    final List packageIds = childSpecificUVH.getJSONArray("app_ids").toList();
                    final List profileIds = childSpecificUVH.getJSONArray("profile_ids").toList();
                    final String profileIdsStr = StringUtils.join(profileIds.toArray(), ',');
                    if (this.isSoftDelete) {
                        AppTrashSyncHandler.logger.log(Level.INFO, "Is Soft delete {0}", new Object[] { true });
                        new AppTrashModeHandler().softDeleteApps(packageIds.toArray(new Long[0]), customerID, this.userName, true);
                    }
                    else {
                        new AppTrashModeHandler().moveAppsToTrash(profileIdsStr, customerID);
                    }
                }
                catch (final Exception ex) {
                    AppTrashSyncHandler.logger.log(Level.SEVERE, "Exception in AppTrashSyncHandler for customer {0} for props {1}", new Object[] { customerID, ex });
                }
            }
        }
        catch (final Exception ex2) {
            AppTrashSyncHandler.logger.log(Level.SEVERE, "Exception in AppTrashSyncHandler {0} {1}", new Object[] { this.appIdentifiers, ex2 });
        }
    }
}
