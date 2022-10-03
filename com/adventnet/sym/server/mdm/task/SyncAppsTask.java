package com.adventnet.sym.server.mdm.task;

import java.util.Iterator;
import java.util.List;
import com.me.mdm.server.windows.apps.WpAppSettingsHandler;
import org.json.JSONObject;
import com.me.mdm.server.chrome.ChromeManagementHandler;
import com.me.mdm.server.apps.android.afw.appmgmt.GooglePlayBusinessAppHandler;
import com.me.mdm.server.apps.businessstore.MDBusinessStoreUtil;
import com.me.mdm.server.apps.businessstore.BusinessStoreSyncConstants;
import com.me.mdm.server.apps.android.afw.GoogleForWorkSettings;
import com.adventnet.sym.server.mdm.apps.ios.IOSAppUtils;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class SyncAppsTask implements SchedulerExecutionInterface
{
    public static Logger logger;
    
    public void executeTask(final Properties props) {
        try {
            SyncAppsTask.logger.log(Level.INFO, "Daily Sync For Apps Started.....");
            final List<Long> customerList = AppsUtil.getInstance().getCustomersWithoutCustomAppsScheduler();
            for (final Long customerId : customerList) {
                new IOSAppUtils().syncAndUpdateIOSApps(customerId);
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(SyncAppsTask.class.getName()).log(Level.SEVERE, "Exception in daily sync of VPP", ex);
        }
        try {
            for (final Long customerId2 : AppsUtil.getInstance().getCustomersWithoutCustomAppsScheduler()) {
                if (GoogleForWorkSettings.isAFWSettingsConfigured(customerId2)) {
                    final Long businessStoreID = MDBusinessStoreUtil.getBusinessStoreID(customerId2, BusinessStoreSyncConstants.BS_SERVICE_AFW);
                    final JSONObject syncStatus = MDBusinessStoreUtil.getBusinessStoreSyncDetails(businessStoreID);
                    final int status = syncStatus.optInt("STORE_SYNC_STATUS");
                    final String psFailureReason = syncStatus.optString("REMARKS");
                    if (status == 4 && psFailureReason != null && psFailureReason.equals("mdm.appmgmt.afw.enterprise_not_found")) {
                        SyncAppsTask.logger.log(Level.SEVERE, "Skipping Playstore scheduler sync for customer: {0} as the associated enterprise is deleted", customerId2);
                    }
                    else {
                        new GooglePlayBusinessAppHandler().syncGooglePlay(customerId2, 2, 2);
                    }
                }
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(SyncAppsTask.class.getName()).log(Level.SEVERE, "Exception in daily sync of PFW", ex);
        }
        try {
            for (final Long customerId2 : AppsUtil.getInstance().getCustomersWithoutCustomAppsScheduler()) {
                if (GoogleForWorkSettings.isGoogleForWorkSettingsConfigured(customerId2, GoogleForWorkSettings.SERVICE_TYPE_CHROME_MGMT)) {
                    SyncAppsTask.logger.log(Level.SEVERE, "Syncing Chrome devices of customer {0}", customerId2);
                    ChromeManagementHandler.syncChromeDevices(customerId2);
                }
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(SyncAppsTask.class.getName()).log(Level.SEVERE, "Exception in daily sync of Chrome", ex);
        }
        try {
            for (final Long customerId2 : AppsUtil.getInstance().getCustomersWithoutCustomAppsScheduler()) {
                final JSONObject bstoreData = new JSONObject();
                WpAppSettingsHandler.getInstance().putBstoreData(bstoreData, customerId2);
                if (bstoreData.has("domain_name")) {
                    WpAppSettingsHandler.getInstance().syncBStoreAppsToRepository(customerId2);
                }
            }
            SyncAppsTask.logger.log(Level.INFO, "Daily Sync For Apps Completed ....");
        }
        catch (final Exception ex) {
            Logger.getLogger(SyncAppsTask.class.getName()).log(Level.SEVERE, "Exception in daily sync of BStore", ex);
        }
    }
    
    static {
        SyncAppsTask.logger = Logger.getLogger("MDMConfigLogger");
    }
}
