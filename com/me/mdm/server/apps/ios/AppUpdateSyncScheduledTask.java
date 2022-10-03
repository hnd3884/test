package com.me.mdm.server.apps.ios;

import org.json.JSONArray;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import com.me.mdm.server.apps.autoupdate.AutoAppUpdateHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.sym.server.mdm.apps.AppLicenseMgmtHandler;
import com.adventnet.sym.server.mdm.apps.vpp.VPPAppMgmtHandler;
import com.me.mdm.server.apps.AppUpdateSync;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import java.util.logging.Logger;

public class AppUpdateSyncScheduledTask
{
    private final Logger logger;
    
    public AppUpdateSyncScheduledTask() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    public void execute() {
        try {
            final List<Long> customerList = AppsUtil.getInstance().getCustomersWithoutCustomAppsScheduler();
            for (final Long customerId : customerList) {
                final AppUpdateSync upgradeSync = new AppUpdateSync(customerId, 1);
                upgradeSync.syncStoreIds(VPPAppMgmtHandler.getInstance().getVPPAppsNotPurchasedFromPortal(customerId));
                upgradeSync.syncStoreIds(new AppLicenseMgmtHandler().getIOSRedemptionCodeAppsStoreIDs());
                final JSONArray updatedAppGroupList = upgradeSync.getUpdateAppGroupList();
                if (updatedAppGroupList != null && updatedAppGroupList.length() > 0) {
                    final List<Long> appGroupList = new JSONUtil().convertLongJSONArrayTOList(updatedAppGroupList);
                    AutoAppUpdateHandler.getInstance().handleAutoAppUpdate(customerId, appGroupList);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "AppUpdateSyncScheduledTask: Exception : ", e);
        }
    }
    
    public void execute(final Long customerId) {
        try {
            final AppUpdateSync upgradeSync = new AppUpdateSync(customerId, 1);
            upgradeSync.syncStoreIds(VPPAppMgmtHandler.getInstance().getVPPAppsNotPurchasedFromPortal(customerId));
            upgradeSync.syncStoreIds(new AppLicenseMgmtHandler().getIOSRedemptionCodeAppsStoreIDs());
            final JSONArray updatedAppGroupList = upgradeSync.getUpdateAppGroupList();
            if (updatedAppGroupList != null && updatedAppGroupList.length() > 0) {
                final List<Long> appGroupList = new JSONUtil().convertLongJSONArrayTOList(updatedAppGroupList);
                AutoAppUpdateHandler.getInstance().handleAutoAppUpdate(customerId, appGroupList);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "AppUpdateSyncScheduledTask: Exception : ", e);
        }
    }
}
