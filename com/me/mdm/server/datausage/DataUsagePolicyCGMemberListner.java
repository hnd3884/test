package com.me.mdm.server.datausage;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.Iterator;
import java.util.List;
import com.me.mdm.server.config.DynamicValueModifiedHandler;
import com.me.mdm.server.config.ProfileAssociateHandler;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import java.util.Properties;
import java.util.HashMap;
import com.me.mdm.server.profiles.ProfileDistributionListHandler;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.sym.server.mdm.group.MDMGroupMemberEvent;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.group.MDMGroupMemberListener;

public class DataUsagePolicyCGMemberListner implements MDMGroupMemberListener
{
    public Logger profileDistributionLog;
    
    public DataUsagePolicyCGMemberListner() {
        this.profileDistributionLog = Logger.getLogger("MDMProfileDistributionLog");
    }
    
    @Override
    public void groupMemberAdded(final MDMGroupMemberEvent groupEvent) {
        if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("FlatUserGroupDistribution") && MDMGroupHandler.getInstance().isInCycle(groupEvent.groupID)) {
            return;
        }
        this.profileDistributionLog.log(Level.INFO, "Data Profile Group memebers {0} added to group {1}", new Object[] { groupEvent.groupID, Arrays.asList(groupEvent.memberIds) });
        final List resourceList = new ArrayList(Arrays.asList(groupEvent.memberIds));
        final ProfileDistributionListHandler handler = ProfileDistributionListHandler.getDistributionProfileListHandler(0);
        final List addedProfileGroupList = handler.getAddedProfileGroupList(groupEvent.groupID, 8);
        final HashMap profileCollectionMap = handler.getProfileToCollnMapForGroupListener(addedProfileGroupList);
        final HashMap profileProperties = new HashMap();
        for (final Object profileObj : addedProfileGroupList) {
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
            this.profileDistributionLog.log(Level.INFO, "Going to assign data profile for devices: collectionList: {0} resourceList: {1}", new Object[] { profileCollectionMap, resourceList });
            final Properties properties = new Properties();
            ((Hashtable<String, HashMap>)properties).put("profileCollectionMap", profileCollectionMap);
            ((Hashtable<String, String>)properties).put("commandName", "InstallDataProfile");
            ((Hashtable<String, Boolean>)properties).put("isAppConfig", false);
            ((Hashtable<String, Boolean>)properties).put("isSilentInstall", false);
            ((Hashtable<String, Boolean>)properties).put("isNotify", false);
            ((Hashtable<String, Long>)properties).put("customerId", groupEvent.customerId);
            ((Hashtable<String, HashMap>)properties).put("profileProperties", profileProperties);
            ((Hashtable<String, Boolean>)properties).put("profileOrigin", true);
            if (groupEvent.groupType == 7) {
                final List userResourceList = ManagedUserHandler.getInstance().getUserList(resourceList);
                if (userResourceList != null) {
                    ((Hashtable<String, List>)properties).put("resourceList", userResourceList);
                    ((Hashtable<String, Integer>)properties).put("resourceType", 2);
                    ((Hashtable<String, Integer>)properties).put("profileOriginInt", 101);
                    ProfileAssociateHandler.getInstance().associateCollectionToMDMResource(properties);
                }
                if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("FlatUserGroupDistribution")) {
                    final List groupResourceList = MDMGroupHandler.getInstance().getGroupList(resourceList);
                    if (groupResourceList != null && !groupResourceList.isEmpty()) {
                        ((Hashtable<String, List>)properties).put("resourceList", groupResourceList);
                        com.adventnet.sym.server.mdm.config.ProfileAssociateHandler.getInstance().associateCollectionForGroup(properties);
                    }
                }
            }
            else {
                ((Hashtable<String, List>)properties).put("resourceList", resourceList);
                com.adventnet.sym.server.mdm.config.ProfileAssociateHandler.getInstance().associateCollectionForResource(properties);
            }
        }
        final DynamicValueModifiedHandler dynamicValueModifiedHandler = new DynamicValueModifiedHandler();
        dynamicValueModifiedHandler.redistributeProfileToDevice(groupEvent);
    }
    
    @Override
    public void groupMemberRemoved(final MDMGroupMemberEvent groupEvent) {
        if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("FlatUserGroupDistribution") && MDMGroupHandler.getInstance().isInCycle(groupEvent.groupID)) {
            return;
        }
        this.profileDistributionLog.log(Level.INFO, "Profile Group memebers {0} removed from group {1}", new Object[] { groupEvent.groupID, Arrays.asList(groupEvent.memberIds) });
        final List resourceList = new ArrayList(Arrays.asList(groupEvent.memberIds));
        final ProfileDistributionListHandler handler = ProfileDistributionListHandler.getDistributionProfileListHandler(0);
        final List addedProfileGroupList = handler.getAddedProfileGroupList(groupEvent.groupID, 8);
        final HashMap profileCollectionMap = handler.getProfileToCollnMapForGroupListener(addedProfileGroupList);
        final HashMap profileProperties = new HashMap();
        for (final Object profileObj : addedProfileGroupList) {
            final HashMap profileMap = (HashMap)profileObj;
            final HashMap curProfileProps = new HashMap();
            final Long profileId = profileMap.get("profileId");
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
        if (!profileCollectionMap.isEmpty()) {
            final Properties properties = new Properties();
            ((Hashtable<String, HashMap>)properties).put("profileCollectionMap", profileCollectionMap);
            ((Hashtable<String, Boolean>)properties).put("isAppConfig", false);
            ((Hashtable<String, Long>)properties).put("customerId", groupEvent.customerId);
            ((Hashtable<String, HashMap>)properties).put("profileProperties", profileProperties);
            ((Hashtable<String, String>)properties).put("commandName", "RemoveDataProfile");
            ((Hashtable<String, Boolean>)properties).put("isGroupListener", true);
            if (groupEvent.groupType == 7) {
                final List userResourceList = ManagedUserHandler.getInstance().getUserList(resourceList);
                if (userResourceList != null) {
                    ((Hashtable<String, List>)properties).put("resourceList", userResourceList);
                    ((Hashtable<String, Integer>)properties).put("resourceType", 2);
                    ((Hashtable<String, Integer>)properties).put("profileOriginInt", 101);
                    ProfileAssociateHandler.getInstance().disassociateCollectionFromMDMResource(properties);
                }
                if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("FlatUserGroupDistribution")) {
                    final List groupResourceList = MDMGroupHandler.getInstance().getGroupList(resourceList);
                    if (groupResourceList != null && !groupResourceList.isEmpty()) {
                        ((Hashtable<String, List>)properties).put("resourceList", groupResourceList);
                        com.adventnet.sym.server.mdm.config.ProfileAssociateHandler.getInstance().disAssociateCollectionForGroup(properties);
                    }
                }
            }
            else {
                ((Hashtable<String, List>)properties).put("resourceList", resourceList);
                ((Hashtable<String, Long>)properties).put("deploymentSource", groupEvent.groupID);
                this.profileDistributionLog.log(Level.INFO, "Going to remove profile for devices: collectionList: {0} resourceList: {1}", new Object[] { profileCollectionMap, resourceList });
                com.adventnet.sym.server.mdm.config.ProfileAssociateHandler.getInstance().disAssociateCollectionForResource(properties);
            }
        }
        final DynamicValueModifiedHandler dynamicValueModifiedHandler = new DynamicValueModifiedHandler();
        dynamicValueModifiedHandler.redistributeProfileToDevice(groupEvent);
    }
}
