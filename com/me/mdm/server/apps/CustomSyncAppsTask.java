package com.me.mdm.server.apps;

import java.util.Hashtable;
import com.me.mdm.server.windows.apps.WpAppSettingsHandler;
import com.me.mdm.server.chrome.ChromeManagementHandler;
import com.me.mdm.server.apps.android.afw.appmgmt.GooglePlayBusinessAppHandler;
import com.me.mdm.server.apps.android.afw.GoogleForWorkSettings;
import com.adventnet.sym.server.mdm.apps.ios.IOSAppUtils;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class CustomSyncAppsTask implements SchedulerExecutionInterface
{
    public static Logger logger;
    
    public void executeTask(final Properties taskProps) {
        final String scheduleName = ((Hashtable<K, String>)taskProps).get("scheduleName");
        Long customerId = ApiFactoryProvider.getSchedulerAPI().getCustomerID(scheduleName);
        Long[] customers = new Long[0];
        try {
            customers = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
        }
        catch (final SyMException e) {
            CustomSyncAppsTask.logger.log(Level.SEVERE, "couldnt fetch customer List from DB reverting to single customer ID", (Throwable)e);
            customers[0] = customerId;
        }
        for (int i = 0; i < customers.length; ++i) {
            customerId = customers[i];
            CustomSyncAppsTask.logger.log(Level.INFO, "Customized Sync For Apps Started for customer {0}", customerId);
            try {
                new IOSAppUtils().syncAndUpdateIOSApps(customerId);
            }
            catch (final Exception e2) {
                CustomSyncAppsTask.logger.log(Level.SEVERE, "Exception in syncing IOS Store for customer : " + customerId, e2);
            }
            try {
                if (GoogleForWorkSettings.isAFWSettingsConfigured(customerId)) {
                    CustomSyncAppsTask.logger.log(Level.INFO, "Syncing Google for Work of customer {0}", customerId);
                    new GooglePlayBusinessAppHandler().syncGooglePlay(customerId, 2, 2);
                }
            }
            catch (final Exception e2) {
                CustomSyncAppsTask.logger.log(Level.SEVERE, "Exception in syncing Google for work for customer : " + customerId, e2);
            }
            try {
                if (GoogleForWorkSettings.isGoogleForWorkSettingsConfigured(customerId, GoogleForWorkSettings.SERVICE_TYPE_CHROME_MGMT)) {
                    CustomSyncAppsTask.logger.log(Level.INFO, "Syncing Chrome devices of customer {0}", customerId);
                    ChromeManagementHandler.syncChromeDevices(customerId);
                }
            }
            catch (final Exception e2) {
                CustomSyncAppsTask.logger.log(Level.SEVERE, "Exception in syncing Chrome devices of customer : " + customerId, e2);
            }
            try {
                if (new WpAppSettingsHandler().isBstoreConfigured(customerId)) {
                    CustomSyncAppsTask.logger.log(Level.INFO, "Syncing Windows apps of customer {0}", customerId);
                    WpAppSettingsHandler.getInstance().syncBStoreAppsToRepository(customerId);
                }
            }
            catch (final Exception e2) {
                CustomSyncAppsTask.logger.log(Level.SEVERE, "Exception in syncing Windows apps of customer : " + customerId, e2);
            }
        }
    }
    
    static {
        CustomSyncAppsTask.logger = Logger.getLogger("MDMConfigLogger");
    }
}
