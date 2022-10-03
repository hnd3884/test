package com.me.mdm.server.apps.appupdatepolicy;

import java.util.Iterator;
import com.adventnet.sym.server.mdm.config.ProfileHandler;
import java.util.Collection;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import java.util.logging.Level;
import java.util.List;
import java.util.logging.Logger;

public class AppUpdatePostPolicyAssociationListener
{
    private static AppUpdatePostPolicyAssociationListener appUpdatePostPolicyAssociationListener;
    private Logger logger;
    
    public AppUpdatePostPolicyAssociationListener() {
        this.logger = Logger.getLogger("MDMProfileDistributionLog");
    }
    
    public static AppUpdatePostPolicyAssociationListener getInstance() {
        if (AppUpdatePostPolicyAssociationListener.appUpdatePostPolicyAssociationListener == null) {
            AppUpdatePostPolicyAssociationListener.appUpdatePostPolicyAssociationListener = new AppUpdatePostPolicyAssociationListener();
        }
        return AppUpdatePostPolicyAssociationListener.appUpdatePostPolicyAssociationListener;
    }
    
    public void invokePostPolicyAssociationListener(final List groupIds, final List<Long> profileIds) throws Exception {
        this.logger.log(Level.INFO, "invokePostPolicyAssociationListener called for groups {0} profiles {1}", new Object[] { groupIds, profileIds });
        for (final Long profileId : profileIds) {
            final List userIds = MDMGroupHandler.getMemberIdListForGroups(groupIds, 2);
            final List deviceIds = MDMGroupHandler.getMemberIdListForGroups(groupIds, 120);
            final List managedDevices = ManagedUserHandler.getInstance().getManagedDevicesListForManagedUsers(userIds);
            deviceIds.addAll(managedDevices);
            final Long collectionId = ProfileHandler.getRecentProfileCollectionID(profileId);
            AppUpdatesToResourceHandler.getInstance(101).invokePostPolicyAssociationListeners(groupIds, collectionId);
            AppUpdatesToResourceHandler.getInstance(2).invokePostPolicyAssociationListeners(userIds, collectionId);
            AppUpdatesToResourceHandler.getInstance(120).invokePostPolicyAssociationListeners(deviceIds, collectionId);
        }
    }
    
    static {
        AppUpdatePostPolicyAssociationListener.appUpdatePostPolicyAssociationListener = null;
    }
}
