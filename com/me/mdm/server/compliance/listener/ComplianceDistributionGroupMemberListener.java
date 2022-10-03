package com.me.mdm.server.compliance.listener;

import java.util.Collections;
import com.me.mdm.server.profiles.ProfileDistributionListHandler;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.server.compliance.ComplianceDistributionHandler;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.group.MDMGroupMemberEvent;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.group.MDMGroupMemberListener;

public class ComplianceDistributionGroupMemberListener implements MDMGroupMemberListener
{
    Logger logger;
    
    public ComplianceDistributionGroupMemberListener() {
        this.logger = Logger.getLogger("MDMDeviceComplianceLogger");
    }
    
    @Override
    public void groupMemberAdded(final MDMGroupMemberEvent groupEvent) {
        try {
            final Long groupResourceId = groupEvent.groupID;
            final Long customerId = groupEvent.customerId;
            final Long[] addedMemberList = groupEvent.memberIds;
            final List resourceList = new ArrayList(Arrays.asList(addedMemberList));
            final JSONObject requestJSON = new JSONObject();
            requestJSON.put("group_id", (Object)groupResourceId);
            requestJSON.put("customer_id", (Object)customerId);
            final JSONArray resourceJSONArray = new JSONArray();
            for (final Object resource : resourceList) {
                resourceJSONArray.put((Object)resource);
            }
            final JSONObject groupDetailsJSON = ComplianceDistributionHandler.getInstance().getComplianceProfilesForGroup(requestJSON);
            final JSONArray complianceList = groupDetailsJSON.getJSONArray("compliance_list");
            if (groupEvent.groupType == 7) {
                for (int i = 0; i < complianceList.length(); ++i) {
                    final JSONObject complianceJSON = complianceList.getJSONObject(i);
                    final JSONArray profileJSONArray = new JSONArray();
                    final Long userId = JSONUtil.optLongForUVH(complianceJSON, "user_id", Long.valueOf(-1L));
                    profileJSONArray.put((Object)JSONUtil.optLongForUVH(complianceJSON, "compliance_id", Long.valueOf(-1L)));
                    complianceJSON.put("profile_list", (Object)profileJSONArray);
                    complianceJSON.put("resource_list", (Object)resourceJSONArray);
                    complianceJSON.put("resource_type", 2);
                    complianceJSON.put("user_id", (Object)userId);
                    complianceJSON.put("user_name", (Object)DMUserHandler.getUserNameFromUserID(userId));
                    complianceJSON.put("customer_id", (Object)customerId);
                    ComplianceDistributionHandler.getInstance().associateComplianceToMDMResource(complianceJSON);
                }
            }
            else if (groupEvent.groupType == 6) {
                for (int i = 0; i < complianceList.length(); ++i) {
                    final JSONObject complianceJSON = complianceList.getJSONObject(i);
                    final JSONArray profileJSONArray = new JSONArray();
                    final Long userId = JSONUtil.optLongForUVH(complianceJSON, "user_id", Long.valueOf(-1L));
                    profileJSONArray.put((Object)JSONUtil.optLongForUVH(complianceJSON, "compliance_id", Long.valueOf(-1L)));
                    complianceJSON.put("profile_list", (Object)profileJSONArray);
                    complianceJSON.put("resource_list", (Object)resourceJSONArray);
                    complianceJSON.put("user_id", (Object)userId);
                    complianceJSON.put("user_name", (Object)DMUserHandler.getUserNameFromUserID(userId));
                    complianceJSON.put("customer_id", (Object)customerId);
                    ComplianceDistributionHandler.getInstance().associateComplianceToDevices(complianceJSON);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- groupMemberAdded()    >   Error associating compliance    ", e);
        }
    }
    
    @Override
    public void groupMemberRemoved(final MDMGroupMemberEvent groupEvent) {
        try {
            final Long groupResourceId = groupEvent.groupID;
            final Long customerId = groupEvent.customerId;
            final Long[] removedMemberList = groupEvent.memberIds;
            final List resourceList = new ArrayList(Arrays.asList(removedMemberList));
            final JSONObject requestJSON = new JSONObject();
            requestJSON.put("group_id", (Object)groupResourceId);
            requestJSON.put("customer_id", (Object)customerId);
            final JSONArray resourceJSONArray = new JSONArray();
            for (final Object resource : resourceList) {
                resourceJSONArray.put((Object)resource);
            }
            final JSONObject groupDetailsJSON = ComplianceDistributionHandler.getInstance().getComplianceProfilesForGroup(requestJSON);
            final JSONArray complianceList = groupDetailsJSON.getJSONArray("compliance_list");
            if (groupEvent.groupType == 7) {
                for (int i = 0; i < complianceList.length(); ++i) {
                    final JSONObject complianceJSON = complianceList.getJSONObject(i);
                    final JSONArray profileJSONArray = new JSONArray();
                    final Long userId = JSONUtil.optLongForUVH(complianceJSON, "user_id", Long.valueOf(-1L));
                    final Long complianceId = JSONUtil.optLongForUVH(complianceJSON, "compliance_id", Long.valueOf(-1L));
                    final Long collectionId = JSONUtil.optLongForUVH(complianceJSON, "collection_id", Long.valueOf(-1L));
                    profileJSONArray.put((Object)complianceId);
                    final HashMap profileCollnMap = new HashMap();
                    profileCollnMap.put(complianceId, collectionId);
                    final HashMap excludeMap = ProfileDistributionListHandler.getDistributionProfileListHandler(0).getGroupDeviceExcludeProfileMap(resourceList, profileCollnMap, new ArrayList(Collections.singleton(groupResourceId)));
                    final List excludeList = excludeMap.get(complianceId);
                    final List tempList = new ArrayList(resourceList);
                    if (excludeList != null) {
                        tempList.removeAll(excludeList);
                    }
                    final JSONArray tempResourceJSONArray = new JSONArray();
                    for (final Object value : tempList) {
                        tempResourceJSONArray.put((Object)value);
                    }
                    complianceJSON.put("profile_list", (Object)profileJSONArray);
                    complianceJSON.put("resource_list", (Object)tempResourceJSONArray);
                    complianceJSON.put("resource_type", 2);
                    complianceJSON.put("user_id", (Object)userId);
                    complianceJSON.put("user_name", (Object)DMUserHandler.getUserNameFromUserID(userId));
                    complianceJSON.put("customer_id", (Object)customerId);
                    if (tempResourceJSONArray.length() != 0) {
                        ComplianceDistributionHandler.getInstance().disassociateComplianceToMDMResource(complianceJSON);
                    }
                }
            }
            else if (groupEvent.groupType == 6) {
                for (int i = 0; i < complianceList.length(); ++i) {
                    final JSONObject complianceJSON = complianceList.getJSONObject(i);
                    final JSONArray profileJSONArray = new JSONArray();
                    final Long userId = JSONUtil.optLongForUVH(complianceJSON, "user_id", Long.valueOf(-1L));
                    final Long complianceId = JSONUtil.optLongForUVH(complianceJSON, "compliance_id", Long.valueOf(-1L));
                    final Long collectionId = JSONUtil.optLongForUVH(complianceJSON, "collection_id", Long.valueOf(-1L));
                    profileJSONArray.put((Object)complianceId);
                    final HashMap profileCollnMap = new HashMap();
                    profileCollnMap.put(complianceId, collectionId);
                    final HashMap excludeMap = ProfileDistributionListHandler.getDistributionProfileListHandler(0).getGroupDeviceExcludeProfileMap(resourceList, profileCollnMap, new ArrayList(Collections.singleton(groupResourceId)));
                    final List excludeList = excludeMap.get(complianceId);
                    final List tempList = new ArrayList(resourceList);
                    if (excludeList != null) {
                        tempList.removeAll(excludeList);
                    }
                    final JSONArray tempResourceJSONArray = new JSONArray();
                    for (final Object value : tempList) {
                        tempResourceJSONArray.put((Object)value);
                    }
                    complianceJSON.put("profile_list", (Object)profileJSONArray);
                    complianceJSON.put("resource_list", (Object)tempResourceJSONArray);
                    complianceJSON.put("user_id", (Object)userId);
                    complianceJSON.put("user_name", (Object)DMUserHandler.getUserNameFromUserID(userId));
                    complianceJSON.put("customer_id", (Object)customerId);
                    if (tempResourceJSONArray.length() != 0) {
                        ComplianceDistributionHandler.getInstance().disassociateComplianceToDevices(complianceJSON);
                    }
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- groupMemberRemoved()    >   Error removing compliance    ", e);
        }
    }
}
