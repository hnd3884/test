package com.me.mdm.server.announcement.handler;

import java.util.Hashtable;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.server.status.ManagedUserCollectionStatusSummary;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import org.json.JSONException;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.me.mdm.server.profiles.ProfileDistributionListHandler;
import com.me.devicemanagement.framework.server.queue.DCQueue;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.uem.announcement.AnnouncementHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.queue.CollectionAssociationQueue.AssociationQueueHandler;
import java.util.Properties;
import java.util.Iterator;
import java.util.HashMap;
import java.util.List;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.ArrayList;
import com.adventnet.persistence.WritableDataObject;
import org.json.JSONArray;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.profiles.BaseProfileAssociationDataHandler;

public class AnnouncementAssociationHandler extends BaseProfileAssociationDataHandler
{
    public AnnouncementAssociationHandler() {
        String chunkSizeStr = null;
        try {
            chunkSizeStr = MDMUtil.getInstance().getMDMApplicationProperties().getProperty("profile_chunk_size");
            this.chunckSize = ((chunkSizeStr == null) ? 500 : Integer.parseInt(chunkSizeStr));
            final String profileChunkSizeStr = MDMUtil.getInstance().getMDMApplicationProperties().getProperty("profile_len_size");
            this.profileLenSize = ((profileChunkSizeStr == null) ? 5 : Integer.parseInt(profileChunkSizeStr));
        }
        catch (final Exception e) {
            this.chunckSize = 500;
            this.profileLenSize = 5;
        }
    }
    
    public static AnnouncementAssociationHandler getNewInstance() {
        return new AnnouncementAssociationHandler();
    }
    
    @Override
    public void associateProfileForGroup(final JSONObject requestJSON) throws Exception {
        try {
            final JSONArray groupJSONArray = requestJSON.getJSONArray("resource_list");
            final JSONArray announcementList = (JSONArray)requestJSON.get("announcement_list");
            final JSONArray profile_announcement_map = (JSONArray)requestJSON.get("profile_announcement_map");
            requestJSON.put("isGroup", false);
            for (int j = 0; j < announcementList.length(); ++j) {
                final Long announcementID = (Long)announcementList.get(j);
                final JSONObject json = this.getCollnFromAnnouncement(profile_announcement_map, announcementID);
                requestJSON.put("profile_id", json.get("PROFILE_ID"));
                requestJSON.put("collection_id", json.get("COLLECTION_ID"));
                this.finalDO = (DataObject)new WritableDataObject();
                this.getExistingGroupProfileDO(requestJSON);
                this.updateGrouptoProfileDetails(requestJSON);
                MDMUtil.getPersistence().update(this.finalDO);
            }
            this.addEventLogEntry(requestJSON, 75002);
            final List userGroupList = new ArrayList();
            final List deviceGroupList = new ArrayList();
            for (int i = 0; i < groupJSONArray.length(); ++i) {
                final Long resourceId = JSONUtil.optLongForUVH(groupJSONArray, i, -1L);
                final HashMap groupDetails = MDMGroupHandler.getInstance().getGroupDetails(resourceId);
                final int groupType = groupDetails.get("GROUP_TYPE");
                switch (groupType) {
                    case 7: {
                        userGroupList.add(resourceId);
                        break;
                    }
                    case 6: {
                        deviceGroupList.add(resourceId);
                        break;
                    }
                    default: {
                        this.logger.log(Level.SEVERE, " -- associateProfileForGroup()    >   Invalid group type");
                        break;
                    }
                }
            }
            if (!userGroupList.isEmpty()) {
                final List resourceList = MDMGroupHandler.getMemberIdListForGroups(userGroupList, 2);
                final HashSet uniqueResources = new HashSet(resourceList);
                final JSONArray resourceJSONArray = new JSONArray();
                for (final Object value : uniqueResources) {
                    resourceJSONArray.put((Object)value);
                }
                if (resourceJSONArray.length() != 0) {
                    requestJSON.put("resource_list", (Object)resourceJSONArray);
                    requestJSON.put("resource_type", 2);
                    this.associateProfileToMDMResource(requestJSON);
                }
            }
            if (!deviceGroupList.isEmpty()) {
                final List resourceList = MDMGroupHandler.getMemberIdListForGroups(deviceGroupList, 120);
                if (!CustomerInfoUtil.isDC()) {
                    resourceList.addAll(MDMGroupHandler.getMemberIdListForGroups(deviceGroupList, 121));
                }
                final HashSet uniqueResources = new HashSet(resourceList);
                final JSONArray resourceJSONArray = new JSONArray();
                for (final Object value : uniqueResources) {
                    resourceJSONArray.put((Object)value);
                }
                if (resourceJSONArray.length() != 0) {
                    requestJSON.put("resource_list", (Object)resourceJSONArray);
                    this.associateProfileForDevice(requestJSON);
                }
            }
            for (int k = 0; k < announcementList.length(); ++k) {
                final Long announcementID2 = (Long)announcementList.get(k);
                final JSONObject json2 = this.getCollnFromAnnouncement(profile_announcement_map, announcementID2);
                requestJSON.put("profile_id", json2.get("PROFILE_ID"));
                requestJSON.put("collection_id", json2.get("COLLECTION_ID"));
                requestJSON.put("resource_list", (Object)groupJSONArray);
                this.updateGroupCollectionStatusSummary(requestJSON);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- associateProfileForGroup() >   Error   ", e);
            throw e;
        }
    }
    
    private void addTaskToQueue(final JSONObject baseJsonObject, final HashMap deviceMap) {
        try {
            final Long customerId = baseJsonObject.getLong("customer_id");
            final HashMap profileToPlatformMap = new HashMap();
            final List profileList = JSONUtil.getInstance().convertLongJSONArrayTOList((JSONArray)baseJsonObject.get("profile_list"));
            final List collectionList = JSONUtil.getInstance().convertLongJSONArrayTOList((JSONArray)baseJsonObject.get("collection_list"));
            profileToPlatformMap.put(1, profileList);
            profileToPlatformMap.put(2, profileList);
            profileToPlatformMap.put(3, new ArrayList());
            profileToPlatformMap.put(4, new ArrayList());
            final HashMap collectionToPlatformMap = new HashMap();
            collectionToPlatformMap.put(1, collectionList);
            collectionToPlatformMap.put(2, collectionList);
            collectionToPlatformMap.put(3, new ArrayList());
            collectionToPlatformMap.put(4, new ArrayList());
            final Properties taskProps = new Properties();
            ((Hashtable<String, String>)taskProps).put("commandName", "SyncAnnouncement");
            ((Hashtable<String, Long>)taskProps).put("customerId", customerId);
            ((Hashtable<String, Integer>)taskProps).put("commandType", 1);
            ((Hashtable<String, HashMap>)taskProps).put("deviceMap", deviceMap);
            ((Hashtable<String, String>)taskProps).put("UserId", String.valueOf(baseJsonObject.optLong("user_id")));
            ((Hashtable<String, HashMap>)taskProps).put("collectionToPlatformMap", collectionToPlatformMap);
            ((Hashtable<String, HashMap>)taskProps).put("profileToPlatformMap", profileToPlatformMap);
            ((Hashtable<String, JSONObject>)taskProps).put("baseJsonObject", baseJsonObject);
            final HashMap taskInfoMap = new HashMap();
            taskInfoMap.put("taskName", "AssignDeviceCommandTask");
            taskInfoMap.put("schedulerTime", System.currentTimeMillis());
            taskInfoMap.put("poolName", "mdmPool");
            AssociationQueueHandler.getInstance().executeTask(taskInfoMap, taskProps);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in send notification", e);
        }
    }
    
    @Override
    public void associateProfileForDevice(final JSONObject baseJsonObject) throws Exception {
        final JSONArray resourceList = baseJsonObject.getJSONArray("resource_list");
        final HashMap deviceMap = ManagedDeviceHandler.getInstance().getPlatformBasedMemberId(JSONUtil.getInstance().convertLongJSONArrayTOList(resourceList));
        deviceMap.remove(3);
        deviceMap.remove(4);
        final ArrayList resourceListArray = new ArrayList();
        for (final Object platform : deviceMap.keySet()) {
            resourceListArray.addAll(deviceMap.get(platform));
        }
        baseJsonObject.put("resource_list", (Object)JSONUtil.getInstance().convertListToJSONArray(resourceListArray));
        final Long customerId = (Long)baseJsonObject.get("customer_id");
        try {
            baseJsonObject.put("isGroup", false);
            final JSONArray announcementList = (JSONArray)baseJsonObject.get("announcement_list");
            final JSONArray profile_announcement_map = (JSONArray)baseJsonObject.get("profile_announcement_map");
            for (int j = 0; j < announcementList.length(); ++j) {
                final Long announcementID = (Long)announcementList.get(j);
                final JSONObject json = this.getCollnFromAnnouncement(profile_announcement_map, announcementID);
                baseJsonObject.put("profile_id", json.get("PROFILE_ID"));
                baseJsonObject.put("collection_id", json.get("COLLECTION_ID"));
                final Long userId = baseJsonObject.optLong("user_id");
                if (userId == 0L) {
                    baseJsonObject.put("user_id", json.optLong("user_id"));
                }
                this.getExistingDeviceProfileDO(baseJsonObject);
                this.finalDO = (DataObject)new WritableDataObject();
                this.addOrUpdateRecentProfileForResource(baseJsonObject);
                this.addOrUpdateResourceProfileHistory(baseJsonObject);
                baseJsonObject.put("marked_for_delete", (Object)Boolean.FALSE);
                baseJsonObject.put("status", 12);
                baseJsonObject.put("IS_ANNOUNCEMENT", true);
                this.addOrUpdateCollnToResources(baseJsonObject);
                if (!this.finalDO.isEmpty()) {
                    MDMUtil.getPersistence().update(this.finalDO);
                }
                if (!this.existingResourceProfileDO.isEmpty()) {
                    MDMUtil.getPersistence().update(this.existingResourceProfileDO);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- associateOrDisassociateComplianceToDevices() >   Error   ", e);
            throw e;
        }
        this.addTaskToQueue(baseJsonObject, deviceMap);
        this.addEventLogEntry(baseJsonObject, 75002);
    }
    
    private void addEventLogForDeviceDistribution(final List<Long> resourceList, final JSONObject distributionJson) throws Exception {
        try {
            final Long customerId = distributionJson.getLong("customer_id");
            final JSONArray announcementArray = distributionJson.getJSONArray("announcement_list");
            final List<Long> announcementIds = JSONUtil.getInstance().convertLongJSONArrayTOList(announcementArray);
            final HashMap<Long, String> announcementMap = AnnouncementHandler.newInstance().getAnnouncementNames(announcementIds);
            final List<Object> remarkArgs = new ArrayList<Object>();
            final HashMap<Long, String> resourceMap = ManagedDeviceHandler.getInstance().getDeviceNames(resourceList);
            for (final Long announcementId : announcementIds) {
                final String announcementName = announcementMap.get(announcementId);
                for (final Long resourceId : resourceList) {
                    final String resourceName = resourceMap.get(resourceId);
                    final Object remark = announcementName + "@@@" + resourceName;
                    remarkArgs.add(remark);
                }
            }
            final Long userID = distributionJson.getLong("user_id");
            final String sUserName = DMUserHandler.getDCUser(DMUserHandler.getLoginIdForUserId(userID));
            MDMEventLogHandler.getInstance().addEvent(75002, resourceList, sUserName, "mdm.actionlog.announcement.device_distribution", remarkArgs, customerId, System.currentTimeMillis());
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in  addEventLogForDeviceDistribution {0}", ex);
        }
    }
    
    private JSONObject getCollnFromAnnouncement(final JSONArray jsonArray, final Long announcementId) throws Exception {
        JSONObject json = new JSONObject();
        for (int j = 0; j < jsonArray.length(); ++j) {
            json = (JSONObject)jsonArray.get(j);
            if (Long.parseLong(json.get("ANNOUNCEMENT_ID").toString()) == announcementId) {
                return json;
            }
        }
        return null;
    }
    
    private JSONArray putResListForAnnouncement(final JSONArray jsonArray, final HashMap excludeHashMap, final List fullResList, final Boolean isGroup) throws Exception {
        final JSONArray newJSON = new JSONArray();
        JSONObject json = new JSONObject();
        for (int j = 0; j < jsonArray.length(); ++j) {
            json = (JSONObject)jsonArray.get(j);
            final Long profileId = (Long)json.get("PROFILE_ID");
            List resourceList = fullResList;
            if (isGroup != null && isGroup) {
                final JSONArray groupListArray = (JSONArray)json.get("resource_list");
                resourceList = MDMGroupHandler.getMemberIdListForGroups(JSONUtil.getInstance().convertLongJSONArrayTOList(groupListArray), 120);
                final HashSet uniqueResources = new HashSet(resourceList);
                resourceList = new ArrayList(uniqueResources);
            }
            final List excludeResList = excludeHashMap.get(profileId);
            if (excludeResList != null) {
                resourceList.removeAll(excludeResList);
            }
            json.put("resource_list", (Collection)resourceList);
            newJSON.put((Object)json);
        }
        return newJSON;
    }
    
    private HashMap convertAnnouncementArrayToMap(final JSONArray jsonArray) throws Exception {
        JSONObject json = new JSONObject();
        final HashMap map = new HashMap();
        for (int j = 0; j < jsonArray.length(); ++j) {
            json = (JSONObject)jsonArray.get(j);
            map.put(json.get("PROFILE_ID").toString(), json.get("COLLECTION_ID"));
        }
        return map;
    }
    
    private void addEventLogEntry(final JSONObject requestJSON, final Integer eventType) {
        try {
            final Long customerId = requestJSON.getLong("customer_id");
            final JSONObject qData = new JSONObject();
            qData.put("EventTimeStamp", (Object)new Long(System.currentTimeMillis()));
            qData.put("baseJsonObject", (Object)requestJSON);
            qData.put("eventType", (Object)eventType);
            final DCQueueData queueData = new DCQueueData();
            queueData.fileName = customerId + "_" + SyMUtil.getCurrentTimeInMillis() + "_announcement_audit_qdata.txt";
            queueData.queueData = qData.toString();
            queueData.postTime = SyMUtil.getCurrentTimeInMillis();
            queueData.queueDataType = 202;
            final DCQueue dcQueue = DCQueueHandler.getQueue("mdm-audit-log");
            dcQueue.addToQueue(queueData);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in  addEventLogEntryFordisassociateProfile {0}", ex);
        }
    }
    
    @Override
    public void disassociateProfileForGroup(JSONObject requestJSON) throws Exception {
        try {
            final ArrayList groupList = new ArrayList();
            final JSONArray announcementList = (JSONArray)requestJSON.get("announcement_list");
            final Boolean is_delete = requestJSON.optBoolean("is_delete", false);
            final JSONArray profile_announcement_map = (JSONArray)requestJSON.get("profile_announcement_map");
            requestJSON.put("isGroup", true);
            super.getExistingGroupProfileDO(requestJSON);
            for (int j = 0; j < announcementList.length(); ++j) {
                final Long announcementID = (Long)announcementList.get(j);
                final JSONObject json = this.getCollnFromAnnouncement(profile_announcement_map, announcementID);
                requestJSON.put("profile_id", json.get("PROFILE_ID"));
                requestJSON.put("collection_id", json.get("COLLECTION_ID"));
                final JSONArray resArray = (JSONArray)json.opt("resource_list");
                if (resArray == null) {
                    break;
                }
                requestJSON.put("resource_list", (Object)resArray);
                groupList.addAll(JSONUtil.getInstance().convertLongJSONArrayTOList(resArray));
                requestJSON.put("status", 12);
                requestJSON.put("marked_for_delete", (Object)Boolean.TRUE);
                this.finalDO = (DataObject)new WritableDataObject();
                super.updateGrouptoProfileDetails(requestJSON);
                MDMUtil.getPersistence().update(this.finalDO);
                this.updateGroupCollectionStatusSummary(requestJSON);
                this.deleteRecentProfileForGroup(requestJSON);
                this.addEventLogEntry(requestJSON, 75003);
            }
            final List userGroupList = new ArrayList();
            final List deviceGroupList = new ArrayList();
            if (!is_delete) {
                for (int i = 0; i < groupList.size(); ++i) {
                    final Long resourceId = groupList.get(i);
                    final HashMap groupDetails = MDMGroupHandler.getInstance().getGroupDetails(resourceId);
                    final int groupType = groupDetails.get("GROUP_TYPE");
                    switch (groupType) {
                        case 7: {
                            userGroupList.add(resourceId);
                            break;
                        }
                        case 6: {
                            deviceGroupList.add(resourceId);
                            break;
                        }
                        default: {
                            this.logger.log(Level.SEVERE, " -- associateProfileForGroup()    >   Invalid group type");
                            break;
                        }
                    }
                }
            }
            if (!userGroupList.isEmpty()) {
                final List resourceList = MDMGroupHandler.getMemberIdListForGroups(userGroupList, 2);
                final HashSet uniqueResources = new HashSet(resourceList);
                final ProfileDistributionListHandler handler = ProfileDistributionListHandler.getDistributionProfileListHandler(0);
                final List tempResourceList = new ArrayList(uniqueResources);
                final HashMap profileCollnMap = this.convertAnnouncementArrayToMap(profile_announcement_map);
                final HashMap excludeMap = handler.getGroupDeviceExcludeProfileMap(tempResourceList, profileCollnMap, userGroupList);
                requestJSON.put("profile_announcement_map", (Object)this.putResListForAnnouncement((JSONArray)requestJSON.get("profile_announcement_map"), excludeMap, tempResourceList, true));
                requestJSON.put("resource_type", 2);
                this.disassociateProfileForDevice(requestJSON);
            }
            if (!deviceGroupList.isEmpty()) {
                final List resourceList = MDMGroupHandler.getMemberIdListForGroups(deviceGroupList, 120);
                final HashSet uniqueResources = new HashSet(resourceList);
                final ProfileDistributionListHandler handler = ProfileDistributionListHandler.getDistributionProfileListHandler(0);
                final List tempResourceList = new ArrayList(uniqueResources);
                requestJSON = this.getExclusionMap(requestJSON, tempResourceList, deviceGroupList, true);
                this.disassociateProfileForDevice(requestJSON);
            }
            for (int k = 0; k < announcementList.length(); ++k) {
                final Long announcementID2 = (Long)announcementList.get(k);
                final JSONObject json2 = this.getCollnFromAnnouncement(profile_announcement_map, announcementID2);
                requestJSON.put("profile_id", json2.get("PROFILE_ID"));
                requestJSON.put("collection_id", json2.get("COLLECTION_ID"));
                requestJSON.put("resource_list", (Object)JSONUtil.getInstance().convertListToJSONArray(groupList));
                this.updateGroupCollectionStatusSummary(requestJSON);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- disassociateProfileForGroup() >   Error   ", e);
            throw e;
        }
    }
    
    public JSONObject getExclusionMap(final JSONObject requestJSON, final List resList, final List groupList, final Boolean isGroup) throws Exception {
        final JSONArray profile_announcement_map = (JSONArray)requestJSON.get("profile_announcement_map");
        final HashMap profileCollnMap = this.convertAnnouncementArrayToMap(profile_announcement_map);
        final ProfileDistributionListHandler handler = ProfileDistributionListHandler.getDistributionProfileListHandler(0);
        final HashMap excludeMap = handler.getGroupDeviceExcludeProfileMap(resList, profileCollnMap, groupList);
        requestJSON.put("profile_announcement_map", (Object)this.putResListForAnnouncement((JSONArray)requestJSON.get("profile_announcement_map"), excludeMap, resList, isGroup));
        return requestJSON;
    }
    
    @Override
    public void disassociateProfileForDevice(final JSONObject baseJsonObject) throws Exception {
        final ArrayList androidNotificationList = new ArrayList();
        final ArrayList iosNotificationList = new ArrayList();
        final ArrayList resourceList = new ArrayList();
        try {
            baseJsonObject.put("isGroup", false);
            final JSONArray announcementList = (JSONArray)baseJsonObject.get("announcement_list");
            final JSONArray profile_announcement_map = (JSONArray)baseJsonObject.get("profile_announcement_map");
            for (int j = 0; j < announcementList.length(); ++j) {
                final Long announcementID = (Long)announcementList.get(j);
                final JSONObject json = this.getCollnFromAnnouncement(profile_announcement_map, announcementID);
                baseJsonObject.put("profile_id", json.get("PROFILE_ID"));
                final JSONArray resList = (JSONArray)json.opt("resource_list");
                if (resList == null) {
                    break;
                }
                baseJsonObject.put("resource_list", (Object)resList);
                final List<Long> addedList = JSONUtil.getInstance().convertLongJSONArrayTOList(resList);
                resourceList.addAll(addedList);
                baseJsonObject.put("collection_id", json.get("COLLECTION_ID"));
                final Long profileId = baseJsonObject.getLong("profile_id");
                final Long collectionId = baseJsonObject.getLong("collection_id");
                baseJsonObject.put("marked_for_delete", (Object)Boolean.TRUE);
                baseJsonObject.put("status", 12);
                this.finalDO = (DataObject)new WritableDataObject();
                super.getExistingDeviceProfileDO(baseJsonObject);
                super.addOrUpdateRecentProfileForResource(baseJsonObject);
                super.addOrUpdateResourceProfileHistory(baseJsonObject);
                super.addOrUpdateCollnToResources(baseJsonObject);
                this.handleDirectProfileRemoval(baseJsonObject);
                MDMUtil.getPersistence().update(this.finalDO);
                if (!this.existingResourceProfileDO.isEmpty()) {
                    MDMUtil.getPersistence().update(this.existingResourceProfileDO);
                }
                this.addEventLogEntry(baseJsonObject, 75003);
                this.logger.log(Level.INFO, "Disassociating profile for device.ProfileId:{0} collectionId:{1} resourceList{2}", new Object[] { profileId, collectionId, resourceList });
            }
            final HashMap deviceMap = ManagedDeviceHandler.getInstance().getPlatformBasedMemberId(resourceList);
            deviceMap.put(1, new HashSet());
            this.addTaskToQueue(baseJsonObject, deviceMap);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- disassociateComplianceToDevices() >   Error   ", e);
            throw e;
        }
    }
    
    private void addEventLoggerForDeviceRemoval(final List<Long> resourceList, final JSONObject distributionJson) throws Exception {
        try {
            final Long customerId = distributionJson.getLong("customer_id");
            final JSONArray announcementArray = distributionJson.getJSONArray("announcement_list");
            final List<Long> announcementIds = JSONUtil.getInstance().convertLongJSONArrayTOList(announcementArray);
            final HashMap<Long, String> announcementMap = AnnouncementHandler.newInstance().getAnnouncementNames(announcementIds);
            final List<Object> remarkArgs = new ArrayList<Object>();
            final Long userID = distributionJson.getLong("user_id");
            final String sUserName = DMUserHandler.getDCUser(DMUserHandler.getLoginIdForUserId(userID));
            final JSONArray profile_announcement_map = (JSONArray)distributionJson.get("profile_announcement_map");
            final HashMap<Long, String> resourceMap = ManagedDeviceHandler.getInstance().getDeviceNames(resourceList);
            for (final Long announcementId : announcementIds) {
                final String announcementName = announcementMap.get(announcementId);
                final JSONObject json = this.getCollnFromAnnouncement(profile_announcement_map, announcementId);
                final JSONArray resArray = json.optJSONArray("resource_list");
                if (resArray == null) {
                    break;
                }
                final List<Long> announcementResourceIds = JSONUtil.getInstance().convertLongJSONArrayTOList(resArray);
                for (final Long resourceId : announcementResourceIds) {
                    final String resourceName = resourceMap.get(resourceId);
                    final Object remark = announcementName + "@@@" + resourceName;
                    remarkArgs.add(remark);
                }
                MDMEventLogHandler.getInstance().addEvent(75003, resourceList, sUserName, "mdm.actionlog.announcement.device_disassociated", remarkArgs, customerId, System.currentTimeMillis());
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in  addEventLoggerForDeviceRemoval {0} ", ex);
        }
    }
    
    @Override
    public void handleDirectProfileRemoval(final JSONObject requestJSON) throws JSONException {
        try {
            final Long collectionId = JSONUtil.optLongForUVH(requestJSON, "collection_id", Long.valueOf(-1L));
            final JSONArray resourceList = requestJSON.getJSONArray("resource_list");
            final List directRemovalResourceListForCollection = new ArrayList();
            for (int i = 0; i < resourceList.length(); ++i) {
                final Long resourceId = resourceList.getLong(i);
                directRemovalResourceListForCollection.add(resourceId);
            }
            final List collectionList = new ArrayList();
            collectionList.add(collectionId);
            List deleteCommandList = new ArrayList();
            deleteCommandList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionList, "Announcement");
            DeviceCommandRepository.getInstance().clearCommandsFromCacheForResources(deleteCommandList, directRemovalResourceListForCollection, 2);
            DeviceCommandRepository.getInstance().deleteResourcesCommands(deleteCommandList, directRemovalResourceListForCollection, 2);
            DeviceCommandRepository.getInstance().clearCommandsFromCacheForResources(deleteCommandList, directRemovalResourceListForCollection, 1);
            DeviceCommandRepository.getInstance().deleteResourcesCommands(deleteCommandList, directRemovalResourceListForCollection, 1);
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, " -- handleDirectProfileRemoval() >   Error   ", (Throwable)e);
            throw e;
        }
    }
    
    @Override
    public void disassociateProfileToManagedUser(final JSONObject baseJsonObject) throws Exception {
        try {
            final Long profileId = baseJsonObject.getLong("profile_id");
            final Long collectionId = baseJsonObject.getLong("collection_id");
            final Long userID = baseJsonObject.getLong("user_id");
            final Long customerId = baseJsonObject.getLong("customer_id");
            baseJsonObject.put("status", 6);
            baseJsonObject.put("marked_for_delete", (Object)Boolean.TRUE);
            super.getExistingMDMResourceProfileDO(baseJsonObject);
            this.finalDO = (DataObject)new WritableDataObject();
            super.updateMDMResourceToProfileDetails(baseJsonObject);
            MDMUtil.getPersistence().update(this.finalDO);
            super.deleteRecentProfileForMDMResource(baseJsonObject);
            final List<Object> remarkArgs = new ArrayList<Object>();
            final JSONArray userJSONArray = baseJsonObject.getJSONArray("resource_list");
            final List userList = new ArrayList();
            final HashMap userMap = new HashMap();
            for (int i = 0; i < userJSONArray.length(); ++i) {
                final Long userId = JSONUtil.optLongForUVH(userJSONArray, i, -1L);
                userList.add(userId);
                final HashMap userDetails = ManagedUserHandler.getInstance().getManagedUserDetails(userId);
                userMap.put(userId, userDetails);
                final Long announcementId = baseJsonObject.getLong("announcement_id");
                final JSONObject announcementInfo = new AnnouncementHandler().getAnnouncementInfo(announcementId);
                final Object remark = announcementInfo.getString("ANNOUNCEMENT_NAME".toLowerCase()) + "@@@" + userDetails.get("NAME");
                remarkArgs.add(remark);
            }
            final String sEventLogRemarks = "mdm.actionlog.announcement.user_disassociated";
            final String sUserName = DMUserHandler.getDCUser(DMUserHandler.getLoginIdForUserId(userID));
            MDMEventLogHandler.getInstance().addEvent(75003, new ArrayList(), sUserName, sEventLogRemarks, remarkArgs, customerId, System.currentTimeMillis());
            final List<Long> resourceList = ManagedUserHandler.getInstance().getRBDAManagedDevicesListForManagedUsers(userList, 2);
            final JSONArray resourceJSONArray = new JSONArray();
            for (final Long resourceId : resourceList) {
                resourceJSONArray.put((Object)resourceId);
            }
            baseJsonObject.put("resource_list", (Object)resourceJSONArray);
            this.disassociateProfileForDevice(baseJsonObject);
            ManagedUserCollectionStatusSummary.getInstance().updateManagedUserCollectionStatusSummary(resourceList, collectionId);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- associateProfileToManagedUser() >   Error   ", e);
            throw e;
        }
    }
    
    @Override
    public void associateProfileToManagedUser(final JSONObject baseJsonObject) throws Exception {
        try {
            final Long profileId = baseJsonObject.getLong("profile_id");
            final Long collectionId = baseJsonObject.getLong("collection_id");
            final Long userID = baseJsonObject.getLong("user_id");
            final Long customerId = baseJsonObject.getLong("customer_id");
            super.getExistingMDMResourceProfileDO(baseJsonObject);
            this.finalDO = (DataObject)new WritableDataObject();
            super.updateMDMResourceToProfileDetails(baseJsonObject);
            MDMUtil.getPersistence().update(this.finalDO);
            final JSONArray userJSONArray = baseJsonObject.getJSONArray("resource_list");
            final List userList = new ArrayList();
            final HashMap userMap = new HashMap();
            final List<Object> remarkArgs = new ArrayList<Object>();
            for (int i = 0; i < userJSONArray.length(); ++i) {
                final Long userId = JSONUtil.optLongForUVH(userJSONArray, i, -1L);
                userList.add(userId);
                final HashMap userDetails = ManagedUserHandler.getInstance().getManagedUserDetails(userId);
                userMap.put(userId, userDetails);
                final Long announcementId = baseJsonObject.getLong("announcement_id");
                final JSONObject announcementInfo = new AnnouncementHandler().getAnnouncementInfo(announcementId);
                final Object remark = announcementInfo.getString("ANNOUNCEMENT_NAME".toLowerCase()) + "@@@" + userDetails.get("NAME");
                remarkArgs.add(remark);
            }
            final String sEventLogRemarks = "mdm.actionlog.announcement.user_distribution";
            final String sUserName = DMUserHandler.getDCUser(DMUserHandler.getLoginIdForUserId(userID));
            MDMEventLogHandler.getInstance().addEvent(75002, new ArrayList(), sUserName, sEventLogRemarks, remarkArgs, customerId, System.currentTimeMillis());
            final List<Long> resourceList = ManagedUserHandler.getInstance().getRBDAManagedDevicesListForManagedUsers(userList, 2);
            final JSONArray resourceJSONArray = new JSONArray();
            for (final Long resourceId : resourceList) {
                resourceJSONArray.put((Object)resourceId);
            }
            baseJsonObject.put("resource_list", (Object)resourceJSONArray);
            this.associateProfileForDevice(baseJsonObject);
            ManagedUserCollectionStatusSummary.getInstance().updateManagedUserCollectionStatusSummary(userList, collectionId);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- associateProfileToManagedUser() >   Error   ", e);
            throw e;
        }
    }
    
    @Override
    public void associateProfileToDirectoryUser(final JSONObject complianceJSON) throws Exception {
        throw new Exception("Why do you need this!! May be later :P");
    }
    
    @Override
    public void disassociateProfileToDirectoryUser(final JSONObject complianceJSON) throws Exception {
        throw new Exception("Why do you need this!! May be later :P");
    }
    
    @Override
    public void associateProfileToMDMResource(final JSONObject complianceJSON) throws Exception {
        throw new Exception("Why do you need this!! May be later :P");
    }
    
    @Override
    public void disassociateProfileToMDMResource(final JSONObject complianceJSON) throws Exception {
        throw new Exception("Why do you need this!! May be later :P");
    }
    
    public JSONArray getApplicableAnnouncementForGroupAssocation(final long groupId) throws Exception {
        final JSONArray pJSON = new JSONArray();
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Announcement"));
        sQuery.addJoin(new Join("Announcement", "AnnouncementDetail", new String[] { "ANNOUNCEMENT_ID" }, new String[] { "ANNOUNCEMENT_ID" }, 2));
        sQuery.addJoin(new Join("Announcement", "AnnouncementConfigData", new String[] { "ANNOUNCEMENT_ID" }, new String[] { "ANNOUNCEMENT_ID" }, 2));
        sQuery.addJoin(new Join("AnnouncementConfigData", "ConfigDataItem", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        sQuery.addJoin(new Join("ConfigDataItem", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        sQuery.addJoin(new Join("ConfigData", "CfgDataToCollection", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        sQuery.addJoin(new Join("CfgDataToCollection", "Collection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        sQuery.addJoin(new Join("CfgDataToCollection", "CollnToCustomerRel", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        sQuery.addJoin(new Join("CfgDataToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        sQuery.addJoin(new Join("ProfileToCollection", "RecentProfileForGroup", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        sQuery.addJoin(new Join("RecentProfileForGroup", "GroupToProfileHistory", new String[] { "COLLECTION_ID", "GROUP_ID" }, new String[] { "COLLECTION_ID", "GROUP_ID" }, 2));
        final Criteria cGroup = new Criteria(new Column("RecentProfileForGroup", "GROUP_ID"), (Object)groupId, 0);
        final Criteria cMa = new Criteria(new Column("RecentProfileForGroup", "MARKED_FOR_DELETE"), (Object)false, 0);
        sQuery.setCriteria(cGroup.and(cMa));
        sQuery.addSelectColumn(new Column("AnnouncementConfigData", "*"));
        sQuery.addSelectColumn(new Column("CfgDataToCollection", "*"));
        sQuery.addSelectColumn(new Column("ConfigData", "*"));
        sQuery.addSelectColumn(new Column("ConfigDataItem", "*"));
        sQuery.addSelectColumn(new Column("GroupToProfileHistory", "*"));
        sQuery.addSelectColumn(new Column("ProfileToCollection", "COLLECTION_ID"));
        sQuery.addSelectColumn(new Column("ProfileToCollection", "PROFILE_ID"));
        try {
            final DataObject prDO = MDMUtil.getPersistence().get(sQuery);
            if (!prDO.isEmpty()) {
                final Iterator item = prDO.getRows("ProfileToCollection");
                while (item.hasNext()) {
                    final Row proRow = item.next();
                    final JSONObject json = new JSONObject();
                    json.put("PROFILE_ID", (Object)proRow.get("PROFILE_ID"));
                    final Long collectionId = (Long)proRow.get("COLLECTION_ID");
                    json.put("COLLECTION_ID", (Object)collectionId);
                    final Long configDataId = (Long)prDO.getValue("ConfigData", "CONFIG_DATA_ID", new Criteria(new Column("CfgDataToCollection", "COLLECTION_ID"), (Object)proRow.get("COLLECTION_ID"), 0));
                    final Long configDataItemId = (Long)prDO.getValue("ConfigDataItem", "CONFIG_DATA_ITEM_ID", new Criteria(new Column("ConfigData", "CONFIG_DATA_ID"), (Object)configDataId, 0));
                    final Long announcementId = (Long)prDO.getValue("AnnouncementConfigData", "ANNOUNCEMENT_ID", new Criteria(new Column("ConfigDataItem", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0));
                    json.put("ANNOUNCEMENT_ID", (Object)announcementId);
                    final Criteria groupCri = new Criteria(new Column("GroupToProfileHistory", "GROUP_ID"), (Object)groupId, 0);
                    final Criteria collectionCri = new Criteria(new Column("GroupToProfileHistory", "COLLECTION_ID"), (Object)collectionId, 0);
                    json.put("user_id", prDO.getValue("GroupToProfileHistory", "ASSOCIATED_BY", groupCri.and(collectionCri)));
                    pJSON.put((Object)json);
                }
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "getApplicableAnnouncementForGroupAssocation", (Throwable)e);
        }
        return pJSON;
    }
    
    public JSONArray getApplicableAnnouncementForGroupDisassociation(final long groupId) throws Exception {
        final JSONArray pJSON = new JSONArray();
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Announcement"));
        sQuery.addJoin(new Join("Announcement", "AnnouncementDetail", new String[] { "ANNOUNCEMENT_ID" }, new String[] { "ANNOUNCEMENT_ID" }, 2));
        sQuery.addJoin(new Join("Announcement", "AnnouncementConfigData", new String[] { "ANNOUNCEMENT_ID" }, new String[] { "ANNOUNCEMENT_ID" }, 2));
        sQuery.addJoin(new Join("AnnouncementConfigData", "ConfigDataItem", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        sQuery.addJoin(new Join("ConfigDataItem", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        sQuery.addJoin(new Join("ConfigData", "CfgDataToCollection", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        sQuery.addJoin(new Join("CfgDataToCollection", "Collection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        sQuery.addJoin(new Join("CfgDataToCollection", "CollnToCustomerRel", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        sQuery.addJoin(new Join("CfgDataToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        sQuery.addJoin(new Join("ProfileToCollection", "RecentProfileForGroup", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        final Criteria cGroup = new Criteria(new Column("RecentProfileForGroup", "GROUP_ID"), (Object)groupId, 0);
        final Criteria cMa = new Criteria(new Column("RecentProfileForGroup", "MARKED_FOR_DELETE"), (Object)false, 0);
        sQuery.setCriteria(cGroup.and(cMa));
        sQuery.addSelectColumn(new Column("AnnouncementConfigData", "*"));
        sQuery.addSelectColumn(new Column("CfgDataToCollection", "*"));
        sQuery.addSelectColumn(new Column("ConfigData", "*"));
        sQuery.addSelectColumn(new Column("ConfigDataItem", "*"));
        sQuery.addSelectColumn(new Column("ProfileToCollection", "COLLECTION_ID"));
        sQuery.addSelectColumn(new Column("ProfileToCollection", "PROFILE_ID"));
        try {
            final DataObject prDO = MDMUtil.getPersistence().get(sQuery);
            if (!prDO.isEmpty()) {
                final Iterator item = prDO.getRows("ProfileToCollection");
                while (item.hasNext()) {
                    final Row proRow = item.next();
                    final JSONObject json = new JSONObject();
                    json.put("PROFILE_ID", (Object)proRow.get("PROFILE_ID"));
                    json.put("COLLECTION_ID", (Object)proRow.get("COLLECTION_ID"));
                    final Long configDataId = (Long)prDO.getValue("ConfigData", "CONFIG_DATA_ID", new Criteria(new Column("CfgDataToCollection", "COLLECTION_ID"), (Object)proRow.get("COLLECTION_ID"), 0));
                    final Long configDataItemId = (Long)prDO.getValue("ConfigDataItem", "CONFIG_DATA_ITEM_ID", new Criteria(new Column("ConfigData", "CONFIG_DATA_ID"), (Object)configDataId, 0));
                    final Long announcementId = (Long)prDO.getValue("AnnouncementConfigData", "ANNOUNCEMENT_ID", new Criteria(new Column("ConfigDataItem", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0));
                    json.put("ANNOUNCEMENT_ID", (Object)announcementId);
                    pJSON.put((Object)json);
                }
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "getApplicableAnnouncementForGroupAssocation", (Throwable)e);
        }
        return pJSON;
    }
}
