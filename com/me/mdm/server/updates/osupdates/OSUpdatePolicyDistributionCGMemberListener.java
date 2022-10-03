package com.me.mdm.server.updates.osupdates;

import java.util.Map;
import java.util.HashMap;
import com.me.mdm.server.profiles.ProfileDistributionListHandler;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import org.json.JSONObject;
import java.util.Collection;
import org.json.JSONArray;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.group.MDMGroupMemberEvent;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.group.MDMGroupMemberListener;

public class OSUpdatePolicyDistributionCGMemberListener implements MDMGroupMemberListener
{
    public Logger logger;
    
    public OSUpdatePolicyDistributionCGMemberListener() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    @Override
    public void groupMemberAdded(final MDMGroupMemberEvent groupEvent) {
        try {
            final List resourceList = Arrays.asList(groupEvent.memberIds);
            final ArrayList<Long> recentOSUpdatePoliciesByGroup = OSUpdatePolicyHandler.getInstance().getRecentOSUpdatePoliciesByGroup(groupEvent.groupID);
            for (final Long profileId : recentOSUpdatePoliciesByGroup) {
                final JSONArray policyArray = new JSONArray().put((Object)profileId);
                final JSONArray deviceArray = new JSONArray((Collection)resourceList);
                final JSONObject distributePoliciesJSON = new JSONObject();
                distributePoliciesJSON.put("PROFILE_IDS", (Object)policyArray);
                distributePoliciesJSON.put("DEVICE_IDS", (Object)deviceArray);
                final Long userId = OSUpdatePolicyHandler.getInstance().getUserIdWhoAssociatedProfileToGroup(groupEvent.groupID, profileId);
                final JSONObject msgHeaderJSON = new JSONObject();
                msgHeaderJSON.put("CUSTOMER_ID", (Object)groupEvent.customerId);
                msgHeaderJSON.put("USER_ID", (Object)userId);
                msgHeaderJSON.put("loggedOnUserName", (Object)DMUserHandler.getDCUser(DMUserHandler.getLoginIdForUserId(userId)));
                OSUpdatePolicyHandler.getInstance().distributeOSUpdatePolicy(msgHeaderJSON, distributePoliciesJSON);
            }
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
    }
    
    @Override
    public void groupMemberRemoved(final MDMGroupMemberEvent groupEvent) {
        try {
            final List resourceList = Arrays.asList(groupEvent.memberIds);
            final ArrayList<Long> recentOSUpdatePoliciesByGroup = OSUpdatePolicyHandler.getInstance().getRecentOSUpdatePoliciesByGroup(groupEvent.groupID);
            for (final Long profileId : recentOSUpdatePoliciesByGroup) {
                final Map profileCollectionMap = OSUpdatePolicyHandler.getInstance().getProfileCollectionIds(Arrays.asList(profileId));
                final ArrayList membersList = new ArrayList(resourceList);
                final ProfileDistributionListHandler handler = ProfileDistributionListHandler.getDistributionProfileListHandler(0);
                final HashMap<Long, List> excludeProfileForGroup = handler.getGroupDeviceExcludeProfileMap(membersList, (HashMap)profileCollectionMap, Arrays.asList(groupEvent.groupID));
                final HashMap<Long, List> excludeProfileForDevice = handler.getDeviceExcludeProfileMap(membersList, (HashMap)profileCollectionMap);
                final List groupExclude = excludeProfileForGroup.get(profileId);
                if (groupExclude != null) {
                    membersList.removeAll(groupExclude);
                }
                final List deviceExclude = excludeProfileForDevice.get(profileId);
                if (deviceExclude != null) {
                    membersList.removeAll(deviceExclude);
                }
                final JSONArray deviceArray = new JSONArray((Collection)membersList);
                final JSONArray policyArray = new JSONArray().put((Object)profileId);
                final JSONObject removeDistributePoliciesJSON = new JSONObject();
                removeDistributePoliciesJSON.put("PROFILE_IDS", (Object)policyArray);
                removeDistributePoliciesJSON.put("DEVICE_IDS", (Object)deviceArray);
                final Long userId = OSUpdatePolicyHandler.getInstance().getUserIdWhoAssociatedProfileToGroup(groupEvent.groupID, profileId);
                final JSONObject msgHeaderJSON = new JSONObject();
                msgHeaderJSON.put("CUSTOMER_ID", (Object)groupEvent.customerId);
                msgHeaderJSON.put("USER_ID", (Object)userId);
                msgHeaderJSON.put("loggedOnUserName", (Object)DMUserHandler.getDCUser(DMUserHandler.getLoginIdForUserId(userId)));
                OSUpdatePolicyHandler.getInstance().removeDistributedOSUpdatePolicy(msgHeaderJSON, removeDistributePoliciesJSON);
            }
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
    }
}
