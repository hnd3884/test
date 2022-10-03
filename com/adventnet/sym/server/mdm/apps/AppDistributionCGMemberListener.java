package com.adventnet.sym.server.mdm.apps;

import java.util.Hashtable;
import java.util.Set;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.mdm.server.config.ProfileAssociateHandler;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import com.me.mdm.server.common.MDMEventConstant;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Iterator;
import java.util.Properties;
import java.util.List;
import com.me.mdm.server.deployment.DeplymentConfigHandler;
import java.util.HashMap;
import com.me.mdm.server.deployment.MDMResourceToProfileDeploymentConfigHandler;
import com.me.mdm.server.profiles.ProfileDistributionListHandler;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.group.MDMGroupMemberEvent;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.group.MDMGroupMemberListener;

public class AppDistributionCGMemberListener implements MDMGroupMemberListener
{
    public Logger profileDistributionLog;
    
    public AppDistributionCGMemberListener() {
        this.profileDistributionLog = Logger.getLogger("MDMProfileDistributionLog");
    }
    
    @Override
    public void groupMemberAdded(final MDMGroupMemberEvent groupEvent) {
        try {
            this.profileDistributionLog.log(Level.INFO, "Group members {0} added to group {1}", new Object[] { groupEvent.memberIds, groupEvent.groupID });
            final boolean flatDistribution = !MDMFeatureParamsHandler.getInstance().isFeatureEnabled("FlatUserGroupDistribution");
            if (flatDistribution && MDMGroupHandler.getInstance().isInCycle(groupEvent.groupID)) {
                return;
            }
            this.profileDistributionLog.log(Level.INFO, "Apps Group memebers {0} added to group {1}", new Object[] { groupEvent.groupID, Arrays.asList(groupEvent.memberIds) });
            final List resourceList = new ArrayList(Arrays.asList(groupEvent.memberIds));
            final ProfileDistributionListHandler handler = ProfileDistributionListHandler.getDistributionProfileListHandler(0);
            final List addedProfileGroupList = handler.getAddedProfileGroupList(groupEvent.groupID, 2);
            if (addedProfileGroupList.isEmpty()) {
                return;
            }
            final HashMap profileCollectionMap = handler.getProfileToCollnMapForGroupListener(addedProfileGroupList);
            final Properties defaultAppSettings = AppsUtil.getInstance().getAppSettings(groupEvent.customerId);
            final Boolean defaultSilentInstall = ((Hashtable<K, Boolean>)defaultAppSettings).get("isSilentInstall");
            final MDMResourceToProfileDeploymentConfigHandler mdmResourceToProfileDeploymentConfigHandler = new MDMResourceToProfileDeploymentConfigHandler();
            mdmResourceToProfileDeploymentConfigHandler.handleDeployConfigForNewGroupMembers(resourceList, groupEvent.groupID, groupEvent.groupType);
            final List profileList = new ArrayList();
            profileList.addAll(profileCollectionMap.keySet());
            final HashMap silentAndReqProfileColln = new HashMap();
            final HashMap notSilentAndReqProfileColln = new HashMap();
            final HashMap silentAndNoReqProfileColln = new HashMap();
            final HashMap notSilentAndNoReqProfileColln = new HashMap();
            final HashMap silentAndReqProfileProperties = new HashMap();
            final HashMap notSilentAndReqProfileProperties = new HashMap();
            final HashMap silentAndNoReqProfileProperties = new HashMap();
            final HashMap notSilentAndNoReqProfileProperties = new HashMap();
            for (final Object profileObj : addedProfileGroupList) {
                try {
                    final HashMap profileMap = (HashMap)profileObj;
                    final Long profileId = profileMap.get("profileId");
                    final Long userID = profileMap.get("associatedByUser");
                    final String associatedUsername = profileMap.get("associatedByUserName");
                    final HashMap curProfileProps = new HashMap();
                    curProfileProps.put("associatedByUserName", associatedUsername);
                    curProfileProps.put("associatedByUser", userID);
                    Boolean isSilentInstall = defaultSilentInstall;
                    Boolean sendEnrollmentRequest = false;
                    final JSONObject depConfigData = new DeplymentConfigHandler().getEffectiveDeploymentConfig(groupEvent.groupID, profileId);
                    if (depConfigData != null) {
                        try {
                            final JSONArray policies = depConfigData.getJSONArray("DeploymentPolicy");
                            for (int i = 0; i < policies.length(); ++i) {
                                final JSONObject policy = policies.getJSONObject(i);
                                final int type = policy.getInt("DEPLOYMENT_POLICY_TYPE_ID");
                                if (type == 301) {
                                    isSilentInstall = policy.getJSONObject("PolicyDetails").getBoolean("FORCE_APP_INSTALL");
                                    sendEnrollmentRequest = policy.getJSONObject("PolicyDetails").getBoolean("SEND_ENROLLMENT_REQUEST");
                                }
                            }
                        }
                        catch (final Exception ex) {
                            this.profileDistributionLog.log(Level.SEVERE, "Exception in fetching deployment config", ex);
                        }
                    }
                    if (isSilentInstall && sendEnrollmentRequest) {
                        silentAndReqProfileColln.put(profileId, profileCollectionMap.get(profileId));
                        silentAndReqProfileProperties.put(profileId, curProfileProps);
                    }
                    else if (isSilentInstall && !sendEnrollmentRequest) {
                        silentAndNoReqProfileColln.put(profileId, profileCollectionMap.get(profileId));
                        silentAndNoReqProfileProperties.put(profileId, curProfileProps);
                    }
                    else if (!isSilentInstall && sendEnrollmentRequest) {
                        notSilentAndReqProfileColln.put(profileId, profileCollectionMap.get(profileId));
                        notSilentAndReqProfileProperties.put(profileId, curProfileProps);
                    }
                    else {
                        notSilentAndNoReqProfileColln.put(profileId, profileCollectionMap.get(profileId));
                        notSilentAndNoReqProfileProperties.put(profileId, curProfileProps);
                    }
                }
                catch (final Exception ex2) {
                    this.profileDistributionLog.log(Level.SEVERE, "Exception when batching app distribution according to deployment config", ex2);
                }
            }
            this.associateAppForNewGroupMembers(silentAndReqProfileColln, silentAndReqProfileProperties, resourceList, flatDistribution, groupEvent, true, true);
            this.associateAppForNewGroupMembers(silentAndNoReqProfileColln, silentAndNoReqProfileProperties, resourceList, flatDistribution, groupEvent, true, false);
            this.associateAppForNewGroupMembers(notSilentAndReqProfileColln, notSilentAndReqProfileProperties, resourceList, flatDistribution, groupEvent, false, true);
            this.associateAppForNewGroupMembers(notSilentAndNoReqProfileColln, notSilentAndNoReqProfileProperties, resourceList, flatDistribution, groupEvent, false, false);
        }
        catch (final Exception ex3) {
            this.profileDistributionLog.log(Level.SEVERE, "Exeception occured while processing the app while adding a member to the group", ex3);
        }
    }
    
    private void associateAppForNewGroupMembers(final HashMap profileCollnMap, final HashMap profilePropsMap, final List resourceList, final boolean flatDistribution, final MDMGroupMemberEvent groupEvent, final boolean silentInstall, final boolean sendEnrollReq) {
        if (!profileCollnMap.isEmpty() && !profilePropsMap.isEmpty() && !resourceList.isEmpty()) {
            final Properties properties = new Properties();
            final Long customerId = groupEvent.customerId;
            final int groupType = groupEvent.groupType;
            this.profileDistributionLog.log(Level.INFO, "Going to assign app for devices: collectionList: {0} resourceList: {1} with silentInstall {2} and sendEnrollReq {3}", new Object[] { profileCollnMap, resourceList, silentInstall, sendEnrollReq });
            ((Hashtable<String, Boolean>)properties).put("isSilentInstall", silentInstall);
            ((Hashtable<String, Boolean>)properties).put("sendEnrollmentRequest", sendEnrollReq);
            ((Hashtable<String, HashMap>)properties).put("profileCollectionMap", profileCollnMap);
            ((Hashtable<String, String>)properties).put("commandName", "InstallApplication");
            ((Hashtable<String, Boolean>)properties).put("isAppConfig", true);
            ((Hashtable<String, Long>)properties).put("customerId", customerId);
            ((Hashtable<String, String>)properties).put("loggedOnUserName", MDMEventConstant.DC_SYSTEM_USER);
            ((Hashtable<String, Boolean>)properties).put("isNotify", false);
            ((Hashtable<String, Boolean>)properties).put("profileOrigin", true);
            ((Hashtable<String, HashMap>)properties).put("profileProperties", profilePropsMap);
            if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AllowAppDowngradeWhileGroupMovement")) {
                this.profileDistributionLog.log(Level.INFO, "Allow app downgrade while group movement feature enabled");
                ((Hashtable<String, Boolean>)properties).put("isAppDowngrade", Boolean.TRUE);
            }
            if (groupType == 7) {
                final List userResourceList = ManagedUserHandler.getInstance().getUserList(resourceList);
                if (userResourceList != null) {
                    ((Hashtable<String, List>)properties).put("resourceList", userResourceList);
                    ((Hashtable<String, Integer>)properties).put("resourceType", 2);
                    ((Hashtable<String, Integer>)properties).put("profileOriginInt", 101);
                    final List configSourceList = new ArrayList();
                    configSourceList.add(groupEvent.groupID);
                    ((Hashtable<String, List>)properties).put("configSourceList", configSourceList);
                    ProfileAssociateHandler.getInstance().associateCollectionToMDMResource(properties);
                }
                properties.remove("configSourceList");
                if (flatDistribution) {
                    final List groupResourceList = MDMGroupHandler.getInstance().getGroupList(resourceList);
                    if (groupResourceList != null && !groupResourceList.isEmpty()) {
                        ((Hashtable<String, List>)properties).put("resourceList", groupResourceList);
                        com.adventnet.sym.server.mdm.config.ProfileAssociateHandler.getInstance().associateCollectionForGroup(properties);
                    }
                }
            }
            else {
                ((Hashtable<String, List>)properties).put("resourceList", resourceList);
                final List configSourceList2 = new ArrayList();
                configSourceList2.add(groupEvent.groupID);
                ((Hashtable<String, List>)properties).put("configSourceList", configSourceList2);
                com.adventnet.sym.server.mdm.config.ProfileAssociateHandler.getInstance().associateCollectionForResource(properties);
            }
        }
    }
    
    @Override
    public void groupMemberRemoved(final MDMGroupMemberEvent groupEvent) {
        try {
            if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("FlatUserGroupDistribution") && MDMGroupHandler.getInstance().isInCycle(groupEvent.groupID)) {
                return;
            }
            this.profileDistributionLog.log(Level.INFO, "Apps Group memebers {0} removed from group {1}", new Object[] { groupEvent.groupID, Arrays.asList(groupEvent.memberIds) });
            final List resourceList = new ArrayList(Arrays.asList(groupEvent.memberIds));
            final ProfileDistributionListHandler handler = ProfileDistributionListHandler.getDistributionProfileListHandler(0);
            final List addedProfileGroupList = handler.getAddedProfileGroupList(groupEvent.groupID, 2);
            if (addedProfileGroupList.isEmpty()) {
                return;
            }
            final HashMap profileCollnMap = handler.getProfileCollnMapForGroupListener(addedProfileGroupList);
            final HashMap profileProperties = new HashMap();
            for (final Object profileObj : addedProfileGroupList) {
                final HashMap profileMap = (HashMap)profileObj;
                final Long profileId = profileMap.get("profileId");
                final HashMap curProfileProps = new HashMap();
                Long userID = profileMap.get("associatedByUser");
                String associatedUsername = profileMap.get("associatedByUserName");
                try {
                    userID = groupEvent.userId;
                    associatedUsername = DMUserHandler.getUserNameFromUserID(userID);
                }
                catch (final SyMException ex) {
                    userID = profileMap.get("associatedByUser");
                    this.profileDistributionLog.log(Level.INFO, "The user Id from group Event is not valid so proceeding profile association user id {0}", userID);
                }
                curProfileProps.put("associatedByUserName", associatedUsername);
                curProfileProps.put("associatedByUser", userID);
                profileProperties.put(profileId, curProfileProps);
            }
            final Properties properties = new Properties();
            ((Hashtable<String, HashMap>)properties).put("profileCollectionMap", profileCollnMap);
            ((Hashtable<String, HashMap>)properties).put("profileProperties", profileProperties);
            ((Hashtable<String, String>)properties).put("commandName", "RemoveApplication");
            ((Hashtable<String, Boolean>)properties).put("isAppConfig", true);
            ((Hashtable<String, Boolean>)properties).put("isGroupListener", true);
            ((Hashtable<String, Long>)properties).put("customerId", groupEvent.customerId);
            ((Hashtable<String, String>)properties).put("loggedOnUserName", MDMEventConstant.DC_SYSTEM_USER);
            final Set profileSet = profileCollnMap.keySet();
            final List profileList = new ArrayList(profileSet);
            final List sourceList = new ArrayList();
            sourceList.add(groupEvent.groupID);
            this.profileDistributionLog.log(Level.INFO, "Going to remove profile for resource: collectionList: {0} resourceList: {1}", new Object[] { profileCollnMap, resourceList });
            if (groupEvent.groupType == 7) {
                final List userResourceList = ManagedUserHandler.getInstance().getUserList(resourceList);
                if (userResourceList != null) {
                    ((Hashtable<String, List>)properties).put("resourceList", userResourceList);
                    ((Hashtable<String, Integer>)properties).put("resourceType", 2);
                    ((Hashtable<String, Integer>)properties).put("profileOriginInt", 101);
                    final List configSourceList = new ArrayList();
                    configSourceList.add(groupEvent.groupID);
                    ((Hashtable<String, List>)properties).put("configSourceList", configSourceList);
                    ProfileAssociateHandler.getInstance().disassociateCollectionFromMDMResource(properties);
                }
                properties.remove("configSourceList");
                if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("FlatUserGroupDistribution")) {
                    final List groupResourceList = MDMGroupHandler.getInstance().getGroupList(resourceList);
                    if (groupResourceList != null && !groupResourceList.isEmpty()) {
                        com.adventnet.sym.server.mdm.config.ProfileAssociateHandler.getInstance().disAssociateCollectionForGroup(properties);
                    }
                }
            }
            else {
                ((Hashtable<String, List>)properties).put("resourceList", resourceList);
                ((Hashtable<String, Long>)properties).put("groupID", groupEvent.groupID);
                final List configSourceList2 = new ArrayList();
                configSourceList2.add(groupEvent.groupID);
                ((Hashtable<String, List>)properties).put("configSourceList", configSourceList2);
                ((Hashtable<String, Long>)properties).put("deploymentSource", groupEvent.groupID);
                com.adventnet.sym.server.mdm.config.ProfileAssociateHandler.getInstance().disAssociateCollectionForResource(properties);
            }
            new MDMResourceToProfileDeploymentConfigHandler().deleteMDMResourceToDeploymentConfig(sourceList, resourceList, profileList);
        }
        catch (final Exception ex2) {
            this.profileDistributionLog.log(Level.SEVERE, "Exeception occured while processing the app while removing a member to the group", ex2);
        }
    }
}
