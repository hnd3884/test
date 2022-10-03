package com.adventnet.sym.server.mdm.apps.vpp;

import java.util.Hashtable;
import com.me.mdm.server.apps.ios.vpp.VPPLicenseSyncHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class VppSyncLicensesForSinlgeAppTask implements SchedulerExecutionInterface
{
    public Logger logger;
    
    public VppSyncLicensesForSinlgeAppTask() {
        this.logger = Logger.getLogger("MDMVPPAppsMgmtLogger");
    }
    
    public void executeTask(final Properties props) {
        final Long appGroupId = ((Hashtable<K, Long>)props).get("APP_GROUP_ID");
        final String storeId = ((Hashtable<K, String>)props).get("STORE_ID");
        final Long customerId = ((Hashtable<K, Long>)props).get("CUSTOMER_ID");
        final Long userID = ((Hashtable<K, Long>)props).get("USER_ID");
        final Long businessStoreID = ((Hashtable<K, Long>)props).get("BUSINESSSTORE_ID");
        this.logger.log(Level.INFO, "******** Syncing App license from VppSyncLicensesForSinlgeAppTask for appGroupId:{0} START ********", appGroupId);
        final String statusName = "SyncStatus_App=" + appGroupId.toString() + "BusinessStore=" + businessStoreID.toString();
        ApiFactoryProvider.getCacheAccessAPI().putCache(statusName, (Object)"inprogress", 2);
        try {
            final VPPLicenseSyncHandler syncHandler = new VPPLicenseSyncHandler(customerId, userID, appGroupId, storeId, businessStoreID, Boolean.FALSE, Boolean.FALSE);
            syncHandler.syncVppLicenses();
            this.logger.log(Level.INFO, "Syncing App license from VppSyncLicensesForSinlgeAppTask for appGroupId:{0} ENDED SUCCESSFULLY", appGroupId);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in VppSyncLicensesForSinlgeAppTask :{0}", ex);
        }
        finally {
            ApiFactoryProvider.getCacheAccessAPI().removeCache(statusName, 2);
        }
    }
}
