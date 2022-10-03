package com.me.mdm.server.apps.appupdatepolicy;

import java.util.Hashtable;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import java.util.Collection;
import java.util.Map;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import java.util.Properties;
import java.util.List;
import com.me.mdm.server.device.api.model.apps.AppUpdatePolicyModel;
import java.util.logging.Logger;

public class AppUpdatePolicyPostModificationListener
{
    public static AppUpdatePolicyPostModificationListener appUpdatePolicyPostModificationListener;
    private Logger profileDistLogger;
    private Logger configLogger;
    
    public AppUpdatePolicyPostModificationListener() {
        this.profileDistLogger = Logger.getLogger("MDMProfileDistributionLogger");
        this.configLogger = Logger.getLogger("MDMConfigLogger");
    }
    
    public static AppUpdatePolicyPostModificationListener getInstance() {
        if (AppUpdatePolicyPostModificationListener.appUpdatePolicyPostModificationListener == null) {
            AppUpdatePolicyPostModificationListener.appUpdatePolicyPostModificationListener = new AppUpdatePolicyPostModificationListener();
        }
        return AppUpdatePolicyPostModificationListener.appUpdatePolicyPostModificationListener;
    }
    
    private void associateCollectionForGroup(final AppUpdatePolicyModel appUpdatePolicyModel, final List groupList) throws Exception {
        final Properties properties = new Properties();
        ((Hashtable<String, Boolean>)properties).put("isAppConfig", false);
        ((Hashtable<String, Long>)properties).put("customerId", appUpdatePolicyModel.getCustomerId());
        ((Hashtable<String, String>)properties).put("commandName", ProfileUtil.getInstance().getProfileCommand(12, 1));
        final Map profileCollectionMap = new HashMap();
        profileCollectionMap.put(appUpdatePolicyModel.getProfileId(), appUpdatePolicyModel.getCollectionId());
        ((Hashtable<String, Map>)properties).put("profileCollectionMap", profileCollectionMap);
        ((Hashtable<String, Boolean>)properties).put("profileOrigin", true);
        ((Hashtable<String, Boolean>)properties).put("isGroup", true);
        ((Hashtable<String, List>)properties).put("resourceList", groupList);
        ((Hashtable<String, Integer>)properties).put("groupType", 6);
        ((Hashtable<String, Long>)properties).put("loggedOnUser", appUpdatePolicyModel.getUserId());
        ((Hashtable<String, String>)properties).put("loggedOnUserName", DMUserHandler.getDCUser(appUpdatePolicyModel.getLogInId()));
        this.profileDistLogger.log(Level.INFO, "Distribution updated app update policy for resource {0} props: {1}", new Object[] { groupList, properties });
        ProfileAssociateHandler.getInstance().associateCollectionForGroup(properties);
        final List<Long> profileList = new ArrayList<Long>();
        profileList.add(appUpdatePolicyModel.getProfileId());
        AppUpdatePostPolicyAssociationListener.getInstance().invokePostPolicyAssociationListener(groupList, profileList);
    }
    
    public void invokePostPolicyModificationListener(final AppUpdatePolicyModel appUpdatePolicyModel, final Long oldCollectionId, final Long newCollectionId) throws Exception {
        this.configLogger.log(Level.INFO, "invokePostPolicyModificationListener called for profile Id {0} old collection id {1} new collection Id {2}", new Object[] { appUpdatePolicyModel.getProfileId(), oldCollectionId, newCollectionId });
        final List<Long> associatedGroupIds = AppUpdatePolicyDBHandler.getInstance().getAssociatedGroupIds(oldCollectionId);
        if (associatedGroupIds != null && !associatedGroupIds.isEmpty()) {
            this.associateCollectionForGroup(appUpdatePolicyModel, associatedGroupIds);
            final List oldAppIds = AppUpdatePolicyDBHandler.getInstance().getListOfAppsInGivenPolicy(oldCollectionId);
            final List newAppIds = AppUpdatePolicyDBHandler.getInstance().getListOfAppsInGivenPolicy(newCollectionId);
            oldAppIds.removeAll(newAppIds);
            this.configLogger.log(Level.INFO, "Apps removed on policy modification: {0}", oldAppIds);
            if (!oldAppIds.isEmpty()) {
                final List userIds = MDMGroupHandler.getMemberIdListForGroups(associatedGroupIds, 2);
                final List deviceIds = MDMGroupHandler.getMemberIdListForGroups(associatedGroupIds, 120);
                final List managedDevices = ManagedUserHandler.getInstance().getManagedDevicesListForManagedUsers(userIds);
                deviceIds.addAll(managedDevices);
                AppUpdatesToResourceHandler.getInstance(101).invokePostPolicyModificationListener(associatedGroupIds, oldAppIds);
                AppUpdatesToResourceHandler.getInstance(2).invokePostPolicyModificationListener(userIds, oldAppIds);
                AppUpdatesToResourceHandler.getInstance(120).invokePostPolicyModificationListener(deviceIds, oldAppIds);
            }
        }
    }
    
    static {
        AppUpdatePolicyPostModificationListener.appUpdatePolicyPostModificationListener = null;
    }
}
