package com.me.mdm.core.user;

import java.util.Hashtable;
import org.json.JSONException;
import com.me.mdm.server.status.GroupCollectionStatusSummary;
import com.me.mdm.server.status.ManagedUserCollectionStatusSummary;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import java.util.Arrays;
import com.me.mdm.server.common.MDMEventConstant;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import java.util.Properties;
import java.util.logging.Level;
import java.util.HashMap;
import com.me.mdm.server.profiles.ProfileDistributionListHandler;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.core.ManagedDeviceListener;

public class UserDeviceProfileAssociationListener extends ManagedDeviceListener
{
    Logger logger;
    int profileType;
    
    public UserDeviceProfileAssociationListener(final int profileType) {
        this.logger = Logger.getLogger("MDMConfigLogger");
        this.profileType = profileType;
    }
    
    @Override
    public void deviceManaged(final DeviceEvent deviceEvent) {
        final Long userId = ManagedUserHandler.getInstance().getManagedUserDetailsForDevice(deviceEvent.resourceID).get("MANAGED_USER_ID");
        final ProfileDistributionListHandler handler = ProfileDistributionListHandler.getDistributionProfileListHandler(0);
        final List addedProfileManagedUserList = handler.getAddedProfileForManagedUser(userId, this.profileType);
        final HashMap profileCollectionMap = handler.getProfileToCollnMapForManagedUserListener(addedProfileManagedUserList);
        final HashMap profileProperties = new HashMap();
        for (final Object profileObj : addedProfileManagedUserList) {
            final HashMap profileMap = (HashMap)profileObj;
            final HashMap curProfileProps = new HashMap();
            final Long profileId = profileMap.get("profileId");
            final Long userID = profileMap.get("associatedByUser");
            final String associatedUsername = profileMap.get("associatedByUserName");
            curProfileProps.put("associatedByUserName", associatedUsername);
            curProfileProps.put("associatedByUser", userID);
            profileProperties.put(profileId, curProfileProps);
        }
        if (!profileCollectionMap.isEmpty()) {
            this.logger.log(Level.INFO, "Going to assign profile for devices: collectionList: {0} resource: {1}", new Object[] { profileCollectionMap, deviceEvent.resourceID });
            final Properties properties = new Properties();
            ((Hashtable<String, HashMap>)properties).put("profileCollectionMap", profileCollectionMap);
            ((Hashtable<String, String>)properties).put("commandName", ProfileUtil.getInstance().getProfileCommand(this.profileType, 1));
            ((Hashtable<String, HashMap>)properties).put("profileProperties", profileProperties);
            ((Hashtable<String, Boolean>)properties).put("isAppConfig", false);
            ((Hashtable<String, Boolean>)properties).put("isSilentInstall", false);
            ((Hashtable<String, Boolean>)properties).put("isNotify", false);
            ((Hashtable<String, Long>)properties).put("customerId", deviceEvent.customerID);
            ((Hashtable<String, String>)properties).put("loggedOnUserName", MDMEventConstant.DC_SYSTEM_USER);
            ((Hashtable<String, Boolean>)properties).put("profileOrigin", false);
            ((Hashtable<String, List<Long>>)properties).put("resourceList", Arrays.asList(deviceEvent.resourceID));
            ProfileAssociateHandler.getInstance().associateCollectionForResource(properties);
        }
    }
    
    @Override
    public void userAssigned(final DeviceEvent deviceEvent) {
        try {
            final Long oldUserId = (Long)deviceEvent.resourceJSON.get("oldUserId");
            final List resourceList = Arrays.asList(deviceEvent.resourceID);
            final ProfileDistributionListHandler handler = ProfileDistributionListHandler.getDistributionProfileListHandler(0);
            final List addedProfileForManagedUser = handler.getAddedProfileForManagedUser(oldUserId, this.profileType);
            final HashMap oldProfileCollectionMap = handler.getProfileToCollnMapForManagedUserListener(addedProfileForManagedUser);
            final Long userId = ManagedUserHandler.getInstance().getManagedUserDetailsForDevice(deviceEvent.resourceID).get("MANAGED_USER_ID");
            final List addedProfileManagedUserList = handler.getAddedProfileForManagedUser(userId, this.profileType);
            final HashMap newProfileCollectionMap = handler.getProfileToCollnMapForManagedUserListener(addedProfileManagedUserList);
            final List<Object> removalList = new ArrayList<Object>();
            for (final Object profileID : oldProfileCollectionMap.keySet()) {
                if (newProfileCollectionMap.containsKey(profileID) && newProfileCollectionMap.get(profileID).equals(oldProfileCollectionMap.get(profileID))) {
                    removalList.add(profileID);
                }
            }
            for (final Object profleID : removalList) {
                oldProfileCollectionMap.remove(profleID);
                newProfileCollectionMap.remove(profleID);
            }
            if (!oldProfileCollectionMap.isEmpty()) {
                final Properties properties = new Properties();
                ((Hashtable<String, HashMap>)properties).put("profileCollectionMap", oldProfileCollectionMap);
                ((Hashtable<String, String>)properties).put("commandName", ProfileUtil.getInstance().getProfileCommand(this.profileType, 0));
                ((Hashtable<String, Boolean>)properties).put("isAppConfig", false);
                ((Hashtable<String, Long>)properties).put("customerId", deviceEvent.customerID);
                ((Hashtable<String, String>)properties).put("loggedOnUserName", MDMEventConstant.DC_SYSTEM_USER);
                ((Hashtable<String, List>)properties).put("resourceList", resourceList);
                this.logger.log(Level.INFO, "Going to remove profile for devices: collectionList: {0} resourceList: {1}", new Object[] { oldProfileCollectionMap, resourceList });
                ProfileAssociateHandler.getInstance().disAssociateCollectionForResource(properties);
                final List<Long> groups = MDMGroupHandler.getInstance().getGroupsForResourceId(oldUserId);
                for (final Object profileID2 : oldProfileCollectionMap.keySet()) {
                    ManagedUserCollectionStatusSummary.getInstance().addOrUpdateManagedUserCollectionStatusSummary(oldUserId, oldProfileCollectionMap.get(profileID2));
                    GroupCollectionStatusSummary.getInstance().updateGroupCollectionStatusSummary(groups, oldProfileCollectionMap.get(profileID2));
                }
            }
            if (!newProfileCollectionMap.isEmpty()) {
                this.logger.log(Level.INFO, "Going to assign profile for devices: collectionList: {0} resource: {1}", new Object[] { newProfileCollectionMap, deviceEvent.resourceID });
                final Properties properties = new Properties();
                ((Hashtable<String, HashMap>)properties).put("profileCollectionMap", newProfileCollectionMap);
                ((Hashtable<String, String>)properties).put("commandName", ProfileUtil.getInstance().getProfileCommand(this.profileType, 1));
                ((Hashtable<String, Boolean>)properties).put("isAppConfig", false);
                ((Hashtable<String, Boolean>)properties).put("isSilentInstall", false);
                ((Hashtable<String, Boolean>)properties).put("isNotify", false);
                ((Hashtable<String, Long>)properties).put("customerId", deviceEvent.customerID);
                ((Hashtable<String, String>)properties).put("loggedOnUserName", MDMEventConstant.DC_SYSTEM_USER);
                ((Hashtable<String, Boolean>)properties).put("profileOrigin", false);
                ((Hashtable<String, List>)properties).put("resourceList", resourceList);
                if (deviceEvent.resourceJSON.opt("technicianUserId") != null) {
                    ((Hashtable<String, Object>)properties).put("loggedOnUser", deviceEvent.resourceJSON.opt("technicianUserId"));
                }
                ProfileAssociateHandler.getInstance().associateCollectionForResource(properties);
                final List<Long> groups = MDMGroupHandler.getInstance().getGroupsForResourceId(userId);
                for (final Object profileID2 : newProfileCollectionMap.keySet()) {
                    ManagedUserCollectionStatusSummary.getInstance().addOrUpdateManagedUserCollectionStatusSummary(userId, newProfileCollectionMap.get(profileID2));
                    GroupCollectionStatusSummary.getInstance().updateGroupCollectionStatusSummary(groups, newProfileCollectionMap.get(profileID2));
                }
            }
        }
        catch (final JSONException e) {
            e.printStackTrace();
        }
    }
}
