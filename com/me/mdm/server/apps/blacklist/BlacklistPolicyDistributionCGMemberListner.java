package com.me.mdm.server.apps.blacklist;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.HashMap;
import com.me.mdm.server.profiles.ProfileDistributionListHandler;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.group.MDMGroupMemberEvent;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.group.MDMGroupMemberListener;

public class BlacklistPolicyDistributionCGMemberListner implements MDMGroupMemberListener
{
    private Logger logger;
    
    public BlacklistPolicyDistributionCGMemberListner() {
        this.logger = Logger.getLogger("MDMAppMgmtLogger");
    }
    
    @Override
    public void groupMemberAdded(final MDMGroupMemberEvent groupEvent) {
        final List resourceList = Arrays.asList(groupEvent.memberIds);
        final ProfileDistributionListHandler handler = ProfileDistributionListHandler.getDistributionProfileListHandler(0);
        final List addedProfileGroupList = handler.getAddedProfileGroupList(groupEvent.groupID, 4);
        final HashMap profileCollectionMap = handler.getProfileToCollnMapForGroupListener(addedProfileGroupList);
        final HashMap profileProperties = new HashMap();
        try {
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
                final HashMap params = new HashMap();
                params.put("profileCollectionMap", profileCollectionMap);
                params.put("profileProperties", profileProperties);
                params.put("resourceIDs", resourceList);
                params.put("CUSTOMER_ID", groupEvent.customerId);
                if (groupEvent.groupType == 7) {
                    new UserBlackListHandler().blacklistAppInResource(params);
                }
                else {
                    new DeviceBlacklistHandler().blacklistAppInResource(params);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Blacklist profile not associated through group policy", e);
        }
    }
    
    @Override
    public void groupMemberRemoved(final MDMGroupMemberEvent groupEvent) {
        final List resourceList = Arrays.asList(groupEvent.memberIds);
        final ProfileDistributionListHandler handler = ProfileDistributionListHandler.getDistributionProfileListHandler(0);
        final List addedProfileGroupList = handler.getAddedProfileGroupList(groupEvent.groupID, 4);
        final HashMap profileCollectionMap = handler.getProfileToCollnMapForGroupListener(addedProfileGroupList);
        final HashMap profileProperties = new HashMap();
        try {
            final HashMap networkBlacklisted = new BlacklistAppHandler().getNetworkLevelBlacklistedApps(groupEvent.customerId, null);
            final List profileList = new ArrayList(profileCollectionMap.keySet());
            profileList.removeAll(new ArrayList(networkBlacklisted.keySet()));
            final HashMap profileCollectionMapWithoutNetwork = new HashMap();
            for (final Long profID : profileList) {
                profileCollectionMapWithoutNetwork.put(profID, profileCollectionMap.get(profID));
            }
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
            if (!profileCollectionMapWithoutNetwork.isEmpty()) {
                final List newProfileList = new ArrayList(profileCollectionMapWithoutNetwork.keySet());
                final Iterator iterator2 = newProfileList.iterator();
                ProfileDistributionListHandler profhandler = null;
                if (groupEvent.groupType == 7) {
                    profhandler = ProfileDistributionListHandler.getDistributionProfileListHandler(1);
                }
                else {
                    profhandler = ProfileDistributionListHandler.getDistributionProfileListHandler(0);
                }
                final List grpList = new ArrayList();
                grpList.add(groupEvent.groupID);
                final HashMap excludeList = profhandler.getGroupDeviceExcludeProfileMap(resourceList, profileCollectionMapWithoutNetwork, grpList);
                while (iterator2.hasNext()) {
                    final Long profileID = iterator2.next();
                    final HashMap profileCollnMap = new HashMap();
                    final List resList = new ArrayList(resourceList);
                    final List curProfileExcluedList = excludeList.get(profileID);
                    if (curProfileExcluedList != null) {
                        resList.removeAll(curProfileExcluedList);
                    }
                    profileCollnMap.put(profileID, profileCollectionMapWithoutNetwork.get(profileID));
                    final HashMap params = new HashMap();
                    params.put("profileCollectionMap", profileCollnMap);
                    params.put("profileProperties", profileProperties);
                    params.put("resourceIDs", resList);
                    params.put("CUSTOMER_ID", groupEvent.customerId);
                    params.put("isGroupListener", true);
                    if (groupEvent.groupType == 7) {
                        params.put("profileOriginInt", 101);
                        new UserBlackListHandler().removeBlacklistAppInResource(params);
                    }
                    else {
                        new DeviceBlacklistHandler().removeBlacklistAppInResource(params);
                    }
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Blacklist profile not removed through group policy", e);
        }
    }
}
