package com.me.mdm.core.user;

import java.util.Hashtable;
import com.me.mdm.server.status.GroupCollectionStatusSummary;
import com.me.mdm.server.status.ManagedUserCollectionStatusSummary;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Iterator;
import java.util.List;
import com.adventnet.persistence.DataAccessException;
import java.util.Arrays;
import com.me.mdm.server.common.MDMEventConstant;
import com.me.mdm.server.config.ProfileAssociateHandler;
import java.util.Properties;
import java.util.HashMap;
import com.me.mdm.server.deployment.MDMResourceToProfileDeploymentConfigHandler;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.me.mdm.server.deployment.DeplymentConfigHandler;
import com.me.mdm.server.profiles.ProfileDistributionListHandler;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.core.ManagedDeviceListener;

public class UserDeviceAppAssociationListener extends ManagedDeviceListener
{
    Logger logger;
    
    public UserDeviceAppAssociationListener() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public void deviceManaged(final DeviceEvent deviceEvent) {
        this.logger.log(Level.INFO, "UserDeviceAppAssociationListener::DeviceManaged");
        final Long deviceId = deviceEvent.resourceID;
        final Long userId = ManagedUserHandler.getInstance().getManagedUserDetailsForDevice(deviceId).get("MANAGED_USER_ID");
        final ProfileDistributionListHandler handler = ProfileDistributionListHandler.getDistributionProfileListHandler(0);
        final List addedProfileManagedUserList = handler.getAddedProfileForManagedUser(userId, 2);
        final HashMap profileCollectionMap = handler.getProfileToCollnMapForManagedUserListener(addedProfileManagedUserList);
        final DeplymentConfigHandler depConfigHandler = new DeplymentConfigHandler();
        final Properties defaultAppSettings = AppsUtil.getInstance().getAppSettings(deviceEvent.customerID);
        final List deviceList = new ArrayList();
        deviceList.add(deviceId);
        try {
            new MDMResourceToProfileDeploymentConfigHandler().copyDeploymentConfig(deviceList, userId);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception when copying deployment policies for device managed", e);
        }
        for (final Object profileObj : addedProfileManagedUserList) {
            try {
                final HashMap profileMap = (HashMap)profileObj;
                final HashMap profileProperties = new HashMap();
                final Long profileId = profileMap.get("profileId");
                final HashMap curProfileProps = new HashMap();
                final Long userID = profileMap.get("associatedByUser");
                final String associatedUsername = profileMap.get("associatedByUserName");
                curProfileProps.put("associatedByUserName", associatedUsername);
                curProfileProps.put("associatedByUser", userID);
                profileProperties.put(profileId, curProfileProps);
                final JSONObject depConfigData = depConfigHandler.getEffectiveDeploymentConfig(deviceId, profileId);
                final Properties properties = new Properties();
                if (depConfigData == null) {
                    ((Hashtable<String, Object>)properties).put("isSilentInstall", ((Hashtable<K, Object>)defaultAppSettings).get("isSilentInstall"));
                }
                else {
                    try {
                        final JSONArray policies = depConfigData.getJSONArray("DeploymentPolicy");
                        Boolean forceInstall = false;
                        Boolean sendEnrollmentRequest = false;
                        for (int i = 0; i < policies.length(); ++i) {
                            final JSONObject policy = policies.getJSONObject(i);
                            final int type = policy.getInt("DEPLOYMENT_POLICY_TYPE_ID");
                            if (type == 301) {
                                forceInstall = policy.getJSONObject("PolicyDetails").getBoolean("FORCE_APP_INSTALL");
                                sendEnrollmentRequest = policy.getJSONObject("PolicyDetails").getBoolean("SEND_ENROLLMENT_REQUEST");
                            }
                        }
                        ((Hashtable<String, Boolean>)properties).put("isSilentInstall", forceInstall);
                        ((Hashtable<String, Boolean>)properties).put("sendEnrollmentRequest", sendEnrollmentRequest);
                    }
                    catch (final Exception ex) {
                        this.logger.log(Level.SEVERE, null, ex);
                    }
                }
                final HashMap tempProfileCollnList = new HashMap();
                tempProfileCollnList.put(profileId, profileCollectionMap.get(profileId));
                this.logger.log(Level.INFO, "Going to assign app for device: collectionList: {0} resource: {1}", new Object[] { tempProfileCollnList, deviceId });
                ((Hashtable<String, HashMap>)properties).put("profileCollectionMap", tempProfileCollnList);
                ((Hashtable<String, String>)properties).put("commandName", "InstallApplication");
                ((Hashtable<String, Long>)properties).put("loggedOnUser", ProfileAssociateHandler.getInstance().getAssociatedByForProfile(profileId, userId));
                ((Hashtable<String, Boolean>)properties).put("isAppConfig", true);
                ((Hashtable<String, Long>)properties).put("customerId", deviceEvent.customerID);
                ((Hashtable<String, String>)properties).put("loggedOnUserName", MDMEventConstant.DC_SYSTEM_USER);
                ((Hashtable<String, Boolean>)properties).put("isNotify", false);
                ((Hashtable<String, Boolean>)properties).put("profileOrigin", true);
                ((Hashtable<String, HashMap>)properties).put("profileProperties", profileProperties);
                ((Hashtable<String, List<Long>>)properties).put("resourceList", Arrays.asList(deviceId));
                com.adventnet.sym.server.mdm.config.ProfileAssociateHandler.getInstance().associateCollectionForResource(properties);
            }
            catch (final DataAccessException ex2) {
                this.logger.log(Level.SEVERE, "DataAccessException", (Throwable)ex2);
            }
            catch (final Exception ex3) {
                this.logger.log(Level.SEVERE, "Exception", ex3);
            }
        }
    }
    
    @Override
    public void userAssigned(final DeviceEvent deviceEvent) {
        try {
            this.logger.log(Level.INFO, "UserDeviceAppAssociationListener::userAssigned");
            final Long oldUserId = (Long)deviceEvent.resourceJSON.get("oldUserId");
            final List resourceList = Arrays.asList(deviceEvent.resourceID);
            final ProfileDistributionListHandler handler = ProfileDistributionListHandler.getDistributionProfileListHandler(0);
            final List addedProfileForManagedUser = handler.getAddedProfileForManagedUser(oldUserId, 2);
            final HashMap oldProfileCollectionMap = handler.getProfileToCollnMapForManagedUserListener(addedProfileForManagedUser);
            final Long userId = ManagedUserHandler.getInstance().getManagedUserDetailsForDevice(deviceEvent.resourceID).get("MANAGED_USER_ID");
            final List addedProfileManagedUserList = handler.getAddedProfileForManagedUser(userId, 2);
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
                ((Hashtable<String, String>)properties).put("commandName", "RemoveApplication");
                ((Hashtable<String, Boolean>)properties).put("isAppConfig", true);
                ((Hashtable<String, Long>)properties).put("customerId", deviceEvent.customerID);
                ((Hashtable<String, String>)properties).put("loggedOnUserName", MDMEventConstant.DC_SYSTEM_USER);
                ((Hashtable<String, List>)properties).put("resourceList", resourceList);
                this.logger.log(Level.INFO, "Going to remove profile for devices: collectionList: {0} resourceList: {1}", new Object[] { oldProfileCollectionMap, resourceList });
                com.adventnet.sym.server.mdm.config.ProfileAssociateHandler.getInstance().disAssociateCollectionForResource(properties);
                final List<Long> groups = MDMGroupHandler.getInstance().getGroupsForResourceId(oldUserId);
                for (final Object profileID2 : oldProfileCollectionMap.keySet()) {
                    ManagedUserCollectionStatusSummary.getInstance().addOrUpdateManagedUserCollectionStatusSummary(oldUserId, oldProfileCollectionMap.get(profileID2));
                    GroupCollectionStatusSummary.getInstance().updateGroupCollectionStatusSummary(groups, oldProfileCollectionMap.get(profileID2));
                }
            }
            final DeplymentConfigHandler depConfigHandler = new DeplymentConfigHandler();
            final Properties defaultAppSettings = AppsUtil.getInstance().getAppSettings(deviceEvent.customerID);
            final List deviceList = new ArrayList();
            deviceList.add(deviceEvent.resourceID);
            try {
                new MDMResourceToProfileDeploymentConfigHandler().copyDeploymentConfig(deviceList, userId);
            }
            catch (final Exception e) {
                this.logger.log(Level.SEVERE, "Exception when copying deployment config for user assignment", e);
            }
            if (!newProfileCollectionMap.isEmpty()) {
                for (final Object profileObj : addedProfileManagedUserList) {
                    try {
                        final HashMap profileMap = (HashMap)profileObj;
                        final Long profileId = profileMap.get("profileId");
                        if (!newProfileCollectionMap.containsKey(profileId)) {
                            continue;
                        }
                        this.logger.log(Level.INFO, "App going to associate in UserDeviceAppAssociationListener::userAssigned:: profileId {0}", profileId);
                        final HashMap profileProperties = new HashMap();
                        final HashMap curProfileProps = new HashMap();
                        final Long associatedByUser = profileMap.get("associatedByUser");
                        final String associatedUsername = profileMap.get("associatedByUserName");
                        curProfileProps.put("associatedByUserName", associatedUsername);
                        curProfileProps.put("associatedByUser", associatedByUser);
                        profileProperties.put(profileId, curProfileProps);
                        final JSONObject depConfigData = depConfigHandler.getEffectiveDeploymentConfig(userId, profileId);
                        final Properties properties2 = new Properties();
                        if (depConfigData == null) {
                            ((Hashtable<String, Object>)properties2).put("isSilentInstall", ((Hashtable<K, Object>)defaultAppSettings).get("isSilentInstall"));
                        }
                        else {
                            try {
                                final JSONArray policies = depConfigData.getJSONArray("DeploymentPolicy");
                                Boolean forceInstall = false;
                                for (int i = 0; i < policies.length(); ++i) {
                                    final JSONObject policy = policies.getJSONObject(i);
                                    final int type = policy.getInt("DEPLOYMENT_POLICY_TYPE_ID");
                                    if (type == 301) {
                                        forceInstall = policy.getJSONObject("PolicyDetails").getBoolean("FORCE_APP_INSTALL");
                                    }
                                }
                                ((Hashtable<String, Boolean>)properties2).put("isSilentInstall", forceInstall);
                            }
                            catch (final Exception ex) {
                                this.logger.log(Level.SEVERE, null, ex);
                            }
                        }
                        final HashMap tempProfileCollnList = new HashMap();
                        tempProfileCollnList.put(profileId, newProfileCollectionMap.get(profileId));
                        this.logger.log(Level.INFO, "Going to assign app for device: collectionList: {0} resource: {1}", new Object[] { tempProfileCollnList, deviceEvent.resourceID });
                        ((Hashtable<String, HashMap>)properties2).put("profileCollectionMap", tempProfileCollnList);
                        ((Hashtable<String, String>)properties2).put("commandName", "InstallApplication");
                        ((Hashtable<String, Long>)properties2).put("loggedOnUser", ProfileAssociateHandler.getInstance().getAssociatedByForProfile(profileId, userId));
                        ((Hashtable<String, Boolean>)properties2).put("isAppConfig", true);
                        ((Hashtable<String, Long>)properties2).put("customerId", deviceEvent.customerID);
                        ((Hashtable<String, String>)properties2).put("loggedOnUserName", MDMEventConstant.DC_SYSTEM_USER);
                        ((Hashtable<String, Boolean>)properties2).put("isNotify", false);
                        ((Hashtable<String, Boolean>)properties2).put("profileOrigin", true);
                        ((Hashtable<String, HashMap>)properties2).put("profileProperties", profileProperties);
                        ((Hashtable<String, List>)properties2).put("resourceList", resourceList);
                        com.adventnet.sym.server.mdm.config.ProfileAssociateHandler.getInstance().associateCollectionForResource(properties2);
                    }
                    catch (final DataAccessException ex2) {
                        this.logger.log(Level.SEVERE, "DataAccessException", (Throwable)ex2);
                    }
                    catch (final Exception ex3) {
                        this.logger.log(Level.SEVERE, "Exception", ex3);
                    }
                }
                final List<Long> groups2 = MDMGroupHandler.getInstance().getGroupsForResourceId(userId);
                for (final Object profileID3 : newProfileCollectionMap.keySet()) {
                    ManagedUserCollectionStatusSummary.getInstance().addOrUpdateManagedUserCollectionStatusSummary(userId, newProfileCollectionMap.get(profileID3));
                    GroupCollectionStatusSummary.getInstance().updateGroupCollectionStatusSummary(groups2, newProfileCollectionMap.get(profileID3));
                }
            }
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception when reassigning user", e2);
        }
    }
}
