package com.me.mdm.server.apps.appupdatepolicy;

import java.util.Iterator;
import com.adventnet.sym.server.mdm.config.ProfileHandler;
import java.util.Collection;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import java.util.logging.Level;
import java.util.List;
import java.util.logging.Logger;

public class AppUpdatePolicyPostDisAssociationListener
{
    public static AppUpdatePolicyPostDisAssociationListener appUpdatePolicyPostDisAssociationListener;
    private static Logger logger;
    
    public static AppUpdatePolicyPostDisAssociationListener getInstance() {
        if (AppUpdatePolicyPostDisAssociationListener.appUpdatePolicyPostDisAssociationListener == null) {
            AppUpdatePolicyPostDisAssociationListener.appUpdatePolicyPostDisAssociationListener = new AppUpdatePolicyPostDisAssociationListener();
        }
        return AppUpdatePolicyPostDisAssociationListener.appUpdatePolicyPostDisAssociationListener;
    }
    
    public void invokePostPolicyDisAssociationListener(final List groupIds, final List<Long> profileIds) throws Exception {
        AppUpdatePolicyPostDisAssociationListener.logger.log(Level.INFO, "invokePostPolicyDisAssociationListener called for groupIds {0} profileIds {1}", new Object[] { groupIds, profileIds });
        for (final Long profileId : profileIds) {
            final List userIds = MDMGroupHandler.getMemberIdListForGroups(groupIds, 2);
            final List deviceIds = MDMGroupHandler.getMemberIdListForGroups(groupIds, 120);
            final List managedDevices = ManagedUserHandler.getInstance().getManagedDevicesListForManagedUsers(userIds);
            deviceIds.addAll(managedDevices);
            final Long collectionId = ProfileHandler.getRecentProfileCollectionID(profileId);
            AppUpdatesToResourceHandler.getInstance(101).invokePostPolicyDisassociationListeners(groupIds, collectionId);
            AppUpdatesToResourceHandler.getInstance(2).invokePostPolicyDisassociationListeners(userIds, collectionId);
            AppUpdatesToResourceHandler.getInstance(120).invokePostPolicyDisassociationListeners(deviceIds, collectionId);
        }
    }
    
    static {
        AppUpdatePolicyPostDisAssociationListener.appUpdatePolicyPostDisAssociationListener = null;
        AppUpdatePolicyPostDisAssociationListener.logger = Logger.getLogger("MDMProfileDistributionLog");
    }
}
