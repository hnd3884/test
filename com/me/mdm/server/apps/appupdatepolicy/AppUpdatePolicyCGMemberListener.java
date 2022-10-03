package com.me.mdm.server.apps.appupdatepolicy;

import java.util.Iterator;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.sym.server.mdm.config.ProfileDistributionCGMemberListener;

public class AppUpdatePolicyCGMemberListener extends ProfileDistributionCGMemberListener
{
    public int getProfileType() {
        return 12;
    }
    
    public String getCommandName(final Boolean isMemberAdded) {
        return isMemberAdded ? "InstallAppUpdatePolicy" : "RemoveAppUpdatePolicy";
    }
    
    @Override
    public void invokePostProfileAssociationListener(final int resType, final List resourceList, final List collectionList) throws Exception {
        List userList = new ArrayList();
        List groupList = new ArrayList();
        List deviceList = new ArrayList();
        switch (resType) {
            case 101: {
                groupList = resourceList;
                userList = MDMGroupHandler.getMemberIdListForGroups(resourceList, 2);
                deviceList = ManagedUserHandler.getInstance().getManagedDevicesListForManagedUsers(userList);
                break;
            }
            case 2: {
                userList = resourceList;
                deviceList = ManagedUserHandler.getInstance().getManagedDevicesListForManagedUsers(resourceList);
                break;
            }
            case 120: {
                deviceList = resourceList;
                break;
            }
        }
        this.profileDistributionLog.log(Level.INFO, "invokePostProfileAssociationListener for app update policy called for resType {0} groups {1} users {2} devices {3} collection {4}", new Object[] { resType, groupList, userList, deviceList, collectionList });
        for (final Object collection : collectionList) {
            switch (resType) {
                case 101: {
                    AppUpdatesToResourceHandler.getInstance(2).invokePostPolicyAssociationListeners(userList, (Long)collection);
                    AppUpdatesToResourceHandler.getInstance(101).invokePostPolicyAssociationListeners(groupList, (Long)collection);
                    AppUpdatesToResourceHandler.getInstance(120).invokePostPolicyAssociationListeners(deviceList, (Long)collection);
                    continue;
                }
                case 2: {
                    AppUpdatesToResourceHandler.getInstance(2).invokePostPolicyAssociationListeners(userList, (Long)collection);
                    AppUpdatesToResourceHandler.getInstance(120).invokePostPolicyAssociationListeners(deviceList, (Long)collection);
                    continue;
                }
                case 120: {
                    AppUpdatesToResourceHandler.getInstance(120).invokePostPolicyAssociationListeners(deviceList, (Long)collection);
                    continue;
                }
            }
        }
    }
    
    @Override
    public void invokePostProfileDisAssociationListener(final int resType, final List resourceList, final List collectionList) throws Exception {
        List userList = new ArrayList();
        List groupList = new ArrayList();
        List deviceList = new ArrayList();
        switch (resType) {
            case 101: {
                groupList = resourceList;
                userList = MDMGroupHandler.getMemberIdListForGroups(resourceList, 2);
                deviceList = ManagedUserHandler.getInstance().getManagedDevicesListForManagedUsers(userList);
                break;
            }
            case 2: {
                userList = resourceList;
                deviceList = ManagedUserHandler.getInstance().getManagedDevicesListForManagedUsers(resourceList);
                break;
            }
            case 120: {
                deviceList = resourceList;
                break;
            }
        }
        this.profileDistributionLog.log(Level.INFO, "invokePostProfileDisAssociationListener for app update policy called for resType {0} groups {1} users {2} devices {3} collection {4}", new Object[] { resType, groupList, userList, deviceList, collectionList });
        for (final Object collection : collectionList) {
            switch (resType) {
                case 101: {
                    AppUpdatesToResourceHandler.getInstance(2).invokePostPolicyDisassociationListeners(userList, (Long)collection);
                    AppUpdatesToResourceHandler.getInstance(101).invokePostPolicyDisassociationListeners(groupList, (Long)collection);
                    AppUpdatesToResourceHandler.getInstance(120).invokePostPolicyDisassociationListeners(deviceList, (Long)collection);
                    continue;
                }
                case 2: {
                    AppUpdatesToResourceHandler.getInstance(2).invokePostPolicyDisassociationListeners(userList, (Long)collection);
                    AppUpdatesToResourceHandler.getInstance(120).invokePostPolicyDisassociationListeners(deviceList, (Long)collection);
                    continue;
                }
                case 120: {
                    AppUpdatesToResourceHandler.getInstance(120).invokePostPolicyDisassociationListeners(deviceList, (Long)collection);
                    continue;
                }
            }
        }
    }
}
