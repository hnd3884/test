package com.me.mdm.server.apps.android.afw;

import java.util.Hashtable;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.List;
import com.me.mdm.server.apps.businessstore.MDBusinessStoreUtil;
import com.me.mdm.server.apps.businessstore.BusinessStoreSyncConstants;
import com.me.mdm.server.apps.android.afw.appmgmt.AdvPlayStoreAppDistributionHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class AndroidEMMAPIMigrationTask implements SchedulerExecutionInterface
{
    public Logger logger;
    
    public AndroidEMMAPIMigrationTask() {
        this.logger = Logger.getLogger("MDMBStoreLogger");
    }
    
    public void executeTask(final Properties properties) {
        try {
            final Long customerId = ((Hashtable<K, Long>)properties).get("customerId");
            if (GoogleForWorkSettings.isAFWSettingsConfigured(customerId)) {
                this.logger.log(Level.INFO, "Going to start migrate device emm api for customer {0}", new Object[] { customerId });
                final List<Long> managedDevices = ManagedDeviceHandler.getInstance().getAndroidManagedDevicesForCustomer(customerId);
                final AdvPlayStoreAppDistributionHandler dist = new AdvPlayStoreAppDistributionHandler();
                dist.initialize(customerId, MDBusinessStoreUtil.getBusinessStoreID(customerId, BusinessStoreSyncConstants.BS_SERVICE_AFW));
                dist.migrateToDevicePolicy(managedDevices, customerId);
            }
            else {
                this.logger.log(Level.INFO, "AFW not configured. So skipping the task for customer {0}", new Object[] { customerId });
            }
            MDMUtil.deleteSyMParameter("migrateEMMAPI");
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Cannot migrate devices ", e);
        }
    }
}
