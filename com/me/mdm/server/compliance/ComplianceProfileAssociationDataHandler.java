package com.me.mdm.server.compliance;

import java.util.Hashtable;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Properties;
import org.json.JSONException;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.me.mdm.server.status.ManagedUserCollectionStatusSummary;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import java.util.Set;
import com.adventnet.sym.server.mdm.core.MDMUserHandler;
import com.me.mdm.server.profiles.ProfileDistributionListHandler;
import java.util.Iterator;
import java.util.Collection;
import java.util.HashSet;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import java.util.HashMap;
import java.util.List;
import com.me.mdm.server.notification.NotificationHandler;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.command.DeviceInvCommandHandler;
import com.adventnet.sym.server.mdm.DeviceDetails;
import com.adventnet.persistence.WritableDataObject;
import org.json.JSONArray;
import com.me.mdm.server.compliance.dbutil.ComplianceDBUtil;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.ArrayList;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.profiles.BaseProfileAssociationDataHandler;

public class ComplianceProfileAssociationDataHandler extends BaseProfileAssociationDataHandler
{
    private static ComplianceProfileAssociationDataHandler complianceProfileAssociationDataHandler;
    private Logger logger;
    
    public ComplianceProfileAssociationDataHandler() {
        this.logger = Logger.getLogger("MDMDeviceComplianceLogger");
    }
    
    public static ComplianceProfileAssociationDataHandler getInstance() {
        if (ComplianceProfileAssociationDataHandler.complianceProfileAssociationDataHandler == null) {
            ComplianceProfileAssociationDataHandler.complianceProfileAssociationDataHandler = new ComplianceProfileAssociationDataHandler();
        }
        return ComplianceProfileAssociationDataHandler.complianceProfileAssociationDataHandler;
    }
    
    @Override
    public void associateProfileForDevice(final JSONObject complianceJSON) throws Exception {
        final ArrayList androidNotificationList = new ArrayList();
        final ArrayList iosNotificationList = new ArrayList();
        try {
            MDMUtil.getUserTransaction().begin();
            final JSONArray resourceList = complianceJSON.getJSONArray("resource_list");
            final String userName = String.valueOf(complianceJSON.get("user_name"));
            final Long customerId = JSONUtil.optLongForUVH(complianceJSON, "customer_id", Long.valueOf(-1L));
            final Long userId = JSONUtil.optLongForUVH(complianceJSON, "user_id", Long.valueOf(-1L));
            final Long complianceId = JSONUtil.optLongForUVH(complianceJSON, "compliance_id", Long.valueOf(-1L));
            final JSONObject complianceDetailsJSON = ComplianceDBUtil.getInstance().getComplianceGeneralDetails(complianceJSON);
            final String complianceName = String.valueOf(complianceDetailsJSON.get("compliance_name"));
            final JSONArray profileJSONArray = new JSONArray();
            profileJSONArray.put((Object)JSONUtil.optLongForUVH(complianceJSON, "profile_id", Long.valueOf(-1L)));
            complianceJSON.put("profile_list", (Object)profileJSONArray);
            this.getExistingDeviceProfileDO(complianceJSON);
            this.finalDO = (DataObject)new WritableDataObject();
            this.addOrUpdateRecentProfileForResource(complianceJSON);
            this.addOrUpdateResourceProfileHistory(complianceJSON);
            complianceJSON.put("marked_for_delete", (Object)Boolean.FALSE);
            complianceJSON.put("status", 12);
            this.addOrUpdateCollnToResources(complianceJSON);
            final String remarksArgs = String.valueOf(complianceId) + "@@@";
            MDMUtil.getPersistence().update(this.finalDO);
            final Long collectionId = JSONUtil.optLongForUVH(complianceJSON, "collection_id", Long.valueOf(-1L));
            for (int i = 0; i < resourceList.length(); ++i) {
                final Long resourceId = resourceList.getLong(i);
                final DeviceDetails deviceDetails = new DeviceDetails(resourceId);
                DeviceInvCommandHandler.getInstance().sendCommandToDevice(deviceDetails, "DeviceCompliance;Collection=" + collectionId.toString(), userId);
                final String resourceRemarks = remarksArgs + String.valueOf(resourceList.getLong(i));
                final JSONObject eventLogJSON = new JSONObject();
                eventLogJSON.put("event_id", 72404);
                eventLogJSON.put("customer_id", (Object)customerId);
                eventLogJSON.put("remarks", (Object)"mdm.compliance.associated_device");
                eventLogJSON.put("remarks_args", (Object)(complianceName + "@@@" + deviceDetails.name));
                eventLogJSON.put("user_name", (Object)userName);
                eventLogJSON.put("resource_id", resourceList.get(i));
                ComplianceDBUtil.getInstance().complianceEventLogEntry(eventLogJSON);
                ComplianceDBUtil.getInstance().initiateComplianceDeviceStatus(complianceJSON);
                if (deviceDetails.platform == 2) {
                    androidNotificationList.add(deviceDetails.resourceId);
                }
                else if (deviceDetails.platform == 1) {
                    iosNotificationList.add(deviceDetails.resourceId);
                }
            }
            MDMUtil.getUserTransaction().commit();
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- associateOrDisassociateComplianceToDevices() >   Error   ", e);
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final Exception e2) {
                this.logger.log(Level.SEVERE, "Exception during rollback in associateProfileForDevice", e2);
            }
            throw e;
        }
        try {
            if (!androidNotificationList.isEmpty()) {
                NotificationHandler.getInstance().SendNotification(androidNotificationList, 2);
            }
            if (!iosNotificationList.isEmpty()) {
                NotificationHandler.getInstance().SendNotification(iosNotificationList, 1);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in send notification", e);
        }
    }
    
    @Override
    public void disassociateProfileForDevice(final JSONObject complianceJSON) throws Exception {
        final ArrayList androidNotificationList = new ArrayList();
        final ArrayList iosNotificationList = new ArrayList();
        try {
            MDMUtil.getUserTransaction().begin();
            final JSONArray resourceList = complianceJSON.getJSONArray("resource_list");
            final JSONArray profileJSONArray = new JSONArray();
            final Long userId = JSONUtil.optLongForUVH(complianceJSON, "user_id", Long.valueOf(-1L));
            final Long complianceId = JSONUtil.optLongForUVH(complianceJSON, "compliance_id", Long.valueOf(-1L));
            final Long customerId = JSONUtil.optLongForUVH(complianceJSON, "customer_id", Long.valueOf(-1L));
            final JSONObject complianceDetailsJSON = ComplianceDBUtil.getInstance().getComplianceGeneralDetails(complianceJSON);
            final String complianceName = String.valueOf(complianceDetailsJSON.get("compliance_name"));
            final String userName = String.valueOf(complianceJSON.get("user_name"));
            profileJSONArray.put((Object)JSONUtil.optLongForUVH(complianceJSON, "profile_id", Long.valueOf(-1L)));
            complianceJSON.put("profile_list", (Object)profileJSONArray);
            complianceJSON.put("status", 6);
            complianceJSON.put("marked_for_delete", (Object)Boolean.TRUE);
            this.finalDO = (DataObject)new WritableDataObject();
            super.getExistingDeviceProfileDO(complianceJSON);
            super.addOrUpdateRecentProfileForResource(complianceJSON);
            super.addOrUpdateResourceProfileHistory(complianceJSON);
            super.addOrUpdateCollnToResources(complianceJSON);
            super.deleteRecentProfileForResource(complianceJSON);
            this.handleDirectProfileRemoval(complianceJSON);
            MDMUtil.getPersistence().update(this.finalDO);
            final Long collectionId = JSONUtil.optLongForUVH(complianceJSON, "collection_id", Long.valueOf(-1L));
            final String remarksArgs = String.valueOf(complianceId) + "@@@";
            final JSONObject requestJSON = new JSONObject();
            requestJSON.put("collection_id", (Object)collectionId);
            requestJSON.put("resource_list", (Object)resourceList);
            ComplianceStatusUpdateDataHandler.getInstance().removeComplianceToResourceSummary(requestJSON);
            ComplianceDistributionHandler.getInstance().removePendingCommands(requestJSON);
            for (int i = 0; i < resourceList.length(); ++i) {
                final Long resourceId = JSONUtil.optLongForUVH(resourceList, i, null);
                final DeviceDetails deviceDetails = new DeviceDetails(resourceId);
                DeviceInvCommandHandler.getInstance().sendCommandToDevice(deviceDetails, "RemoveDeviceCompliance;Collection=" + collectionId.toString(), userId);
                final JSONObject eventLogJSON = new JSONObject();
                eventLogJSON.put("event_id", 72405);
                eventLogJSON.put("customer_id", (Object)customerId);
                eventLogJSON.put("remarks", (Object)"mdm.compliance.disassociated_device");
                eventLogJSON.put("remarks_args", (Object)(complianceName + "@@@" + deviceDetails.name));
                eventLogJSON.put("user_name", (Object)userName);
                eventLogJSON.put("resource_id", resourceList.get(i));
                ComplianceDBUtil.getInstance().complianceEventLogEntry(eventLogJSON);
                if (deviceDetails.platform == 2) {
                    androidNotificationList.add(deviceDetails.resourceId);
                }
                else if (deviceDetails.platform == 1) {
                    iosNotificationList.add(deviceDetails.resourceId);
                }
            }
            MDMUtil.getUserTransaction().commit();
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- disassociateComplianceToDevices() >   Error   ", e);
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final Exception e2) {
                this.logger.log(Level.SEVERE, "Exception during rollback in disassociateProfileForDevice", e2);
            }
            throw e;
        }
        try {
            if (!androidNotificationList.isEmpty()) {
                NotificationHandler.getInstance().SendNotification(androidNotificationList, 2);
            }
            if (!iosNotificationList.isEmpty()) {
                NotificationHandler.getInstance().SendNotification(iosNotificationList, 1);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in send notification", e);
        }
    }
    
    @Override
    public void associateProfileForGroup(final JSONObject complianceJSON) throws Exception {
        try {
            MDMUtil.getUserTransaction().begin();
            final JSONArray groupJSONArray = complianceJSON.getJSONArray("resource_list");
            final JSONArray profileJSONArray = new JSONArray();
            final Long profileId = JSONUtil.optLongForUVH(complianceJSON, "compliance_id", Long.valueOf(-1L));
            final Long customerId = JSONUtil.optLongForUVH(complianceJSON, "customer_id", Long.valueOf(-1L));
            final JSONObject complianceDetailsJSON = ComplianceDBUtil.getInstance().getComplianceGeneralDetails(complianceJSON);
            final String complianceName = String.valueOf(complianceDetailsJSON.get("compliance_name"));
            final String userName = String.valueOf(complianceJSON.get("user_name"));
            profileJSONArray.put((Object)JSONUtil.optLongForUVH(complianceJSON, "profile_id", Long.valueOf(-1L)));
            complianceJSON.put("profile_list", (Object)profileJSONArray);
            complianceJSON.put("status", 12);
            complianceJSON.put("marked_for_delete", (Object)Boolean.FALSE);
            this.finalDO = (DataObject)new WritableDataObject();
            this.getExistingGroupProfileDO(complianceJSON);
            this.updateGrouptoProfileDetails(complianceJSON);
            MDMUtil.getPersistence().update(this.finalDO);
            final String remarksArgs = String.valueOf(profileId) + "@@@";
            final List userGroupList = new ArrayList();
            final List deviceGroupList = new ArrayList();
            final HashMap deviceGroupMap = new HashMap();
            final HashMap userGroupMap = new HashMap();
            for (int i = 0; i < groupJSONArray.length(); ++i) {
                final Long resourceId = JSONUtil.optLongForUVH(groupJSONArray, i, -1L);
                final HashMap groupDetails = MDMGroupHandler.getInstance().getGroupDetails(resourceId);
                final int groupType = groupDetails.get("GROUP_TYPE");
                switch (groupType) {
                    case 7: {
                        userGroupList.add(resourceId);
                        userGroupMap.put(resourceId, groupDetails);
                        break;
                    }
                    case 6: {
                        deviceGroupList.add(resourceId);
                        deviceGroupMap.put(resourceId, groupDetails);
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
                    complianceJSON.put("resource_list", (Object)resourceJSONArray);
                    complianceJSON.put("resource_type", 2);
                    ComplianceDistributionHandler.getInstance().associateComplianceToMDMResource(complianceJSON);
                    for (final Object groupId : userGroupList) {
                        final HashMap groupDetails2 = userGroupMap.get((long)groupId);
                        final String groupName = groupDetails2.get("NAME");
                        final JSONObject eventLogJSON = new JSONObject();
                        eventLogJSON.put("event_id", 72408);
                        eventLogJSON.put("customer_id", (Object)customerId);
                        eventLogJSON.put("remarks", (Object)"mdm.compliance.associated_user_group");
                        eventLogJSON.put("remarks_args", (Object)(complianceName + "@@@" + groupName));
                        eventLogJSON.put("user_name", (Object)userName);
                        eventLogJSON.put("resource_id", (long)groupId);
                        ComplianceDBUtil.getInstance().complianceEventLogEntry(eventLogJSON);
                    }
                }
            }
            if (!deviceGroupList.isEmpty()) {
                final List resourceList = MDMGroupHandler.getMemberIdListForGroups(deviceGroupList, 120);
                resourceList.addAll(MDMGroupHandler.getMemberIdListForGroups(deviceGroupList, 121));
                final HashSet uniqueResources = new HashSet(resourceList);
                final JSONArray resourceJSONArray = new JSONArray();
                for (final Object value : uniqueResources) {
                    resourceJSONArray.put((Object)value);
                }
                if (resourceJSONArray.length() != 0) {
                    complianceJSON.put("resource_list", (Object)resourceJSONArray);
                    ComplianceDistributionHandler.getInstance().associateComplianceToDevices(complianceJSON);
                    for (final Object groupId : deviceGroupList) {
                        final HashMap groupDetails2 = deviceGroupMap.get((long)groupId);
                        final String groupName = groupDetails2.get("NAME");
                        final String resourceRemarks = remarksArgs + (long)groupId;
                        final JSONObject eventLogJSON2 = new JSONObject();
                        eventLogJSON2.put("event_id", 72406);
                        eventLogJSON2.put("customer_id", (Object)customerId);
                        eventLogJSON2.put("remarks", (Object)"mdm.compliance.associated_device_group");
                        eventLogJSON2.put("remarks_args", (Object)(complianceName + "@@@" + groupName));
                        eventLogJSON2.put("user_name", (Object)userName);
                        eventLogJSON2.put("resource_id", (long)groupId);
                        ComplianceDBUtil.getInstance().complianceEventLogEntry(eventLogJSON2);
                    }
                }
            }
            MDMUtil.getUserTransaction().commit();
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- associateProfileForGroup() >   Error   ", e);
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final Exception e2) {
                this.logger.log(Level.SEVERE, "Exception in rollback in associateProfileForGroup", e2);
            }
            throw e;
        }
    }
    
    @Override
    public void disassociateProfileForGroup(final JSONObject complianceJSON) throws Exception {
        try {
            MDMUtil.getUserTransaction().begin();
            final Long profileId = JSONUtil.optLongForUVH(complianceJSON, "compliance_id", Long.valueOf(-1L));
            final String userName = String.valueOf(complianceJSON.get("user_name"));
            final Long customerId = JSONUtil.optLongForUVH(complianceJSON, "customer_id", Long.valueOf(-1L));
            final JSONArray groupJSONArray = complianceJSON.getJSONArray("resource_list");
            final JSONArray profileJSONArray = new JSONArray();
            final JSONObject complianceDetailsJSON = ComplianceDBUtil.getInstance().getComplianceGeneralDetails(complianceJSON);
            final String complianceName = String.valueOf(complianceDetailsJSON.get("compliance_name"));
            profileJSONArray.put((Object)JSONUtil.optLongForUVH(complianceJSON, "profile_id", Long.valueOf(-1L)));
            final Long collectionId = JSONUtil.optLongForUVH(complianceDetailsJSON, "collection_id", Long.valueOf(-1L));
            complianceJSON.put("profile_list", (Object)profileJSONArray);
            complianceJSON.put("status", 12);
            complianceJSON.put("marked_for_delete", (Object)Boolean.TRUE);
            this.finalDO = (DataObject)new WritableDataObject();
            super.getExistingGroupProfileDO(complianceJSON);
            super.updateGrouptoProfileDetails(complianceJSON);
            MDMUtil.getPersistence().update(this.finalDO);
            this.deleteRecentProfileForGroup(complianceJSON);
            final String remarksArgs = String.valueOf(profileId) + "@@@";
            final List userGroupList = new ArrayList();
            final List deviceGroupList = new ArrayList();
            final HashMap deviceGroupMap = new HashMap();
            final HashMap userGroupMap = new HashMap();
            for (int i = 0; i < groupJSONArray.length(); ++i) {
                final Long resourceId = JSONUtil.optLongForUVH(groupJSONArray, i, -1L);
                final HashMap groupDetails = MDMGroupHandler.getInstance().getGroupDetails(resourceId);
                final int groupType = groupDetails.get("GROUP_TYPE");
                switch (groupType) {
                    case 7: {
                        userGroupList.add(resourceId);
                        userGroupMap.put(resourceId, groupDetails);
                        break;
                    }
                    case 6: {
                        deviceGroupList.add(resourceId);
                        deviceGroupMap.put(resourceId, groupDetails);
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
                HashSet uniqueResources = new HashSet(resourceList);
                final ProfileDistributionListHandler handler = ProfileDistributionListHandler.getDistributionProfileListHandler(0);
                final List tempResourceList = new ArrayList(uniqueResources);
                final HashMap profileCollnMap = new HashMap();
                profileCollnMap.put(profileId, collectionId);
                final HashMap excludeMap = handler.getGroupDeviceExcludeProfileMap(tempResourceList, profileCollnMap, userGroupList);
                final List excludeList = excludeMap.get(profileId);
                if (excludeList != null) {
                    tempResourceList.removeAll(excludeList);
                }
                uniqueResources = new HashSet(tempResourceList);
                final JSONArray resourceJSONArray = new JSONArray();
                for (final Object value : uniqueResources) {
                    resourceJSONArray.put((Object)value);
                }
                if (resourceJSONArray.length() != 0) {
                    complianceJSON.put("resource_list", (Object)resourceJSONArray);
                    complianceJSON.put("resource_type", 2);
                    ComplianceDistributionHandler.getInstance().disassociateComplianceToMDMResource(complianceJSON);
                    for (final Object groupId : userGroupList) {
                        final HashMap groupDetails2 = userGroupMap.get((long)groupId);
                        final String groupName = groupDetails2.get("NAME");
                        final String resourceRemarks = remarksArgs + (long)groupId;
                        final JSONObject eventLogJSON = new JSONObject();
                        eventLogJSON.put("event_id", 72409);
                        eventLogJSON.put("customer_id", (Object)customerId);
                        eventLogJSON.put("remarks", (Object)"mdm.compliance.disassociated_user_group");
                        eventLogJSON.put("remarks_args", (Object)(complianceName + "@@@" + groupName));
                        eventLogJSON.put("user_name", (Object)userName);
                        eventLogJSON.put("resource_id", (long)groupId);
                        ComplianceDBUtil.getInstance().complianceEventLogEntry(eventLogJSON);
                    }
                }
            }
            if (!deviceGroupList.isEmpty()) {
                final List resourceList = MDMGroupHandler.getMemberIdListForGroups(deviceGroupList, 120);
                resourceList.addAll(MDMGroupHandler.getMemberIdListForGroups(deviceGroupList, 121));
                HashSet uniqueResources = new HashSet(resourceList);
                final ProfileDistributionListHandler handler = ProfileDistributionListHandler.getDistributionProfileListHandler(0);
                final List tempResourceList = new ArrayList(uniqueResources);
                final HashMap profileCollnMap = new HashMap();
                profileCollnMap.put(profileId, collectionId);
                final HashMap excludeMap = handler.getGroupDeviceExcludeProfileMap(tempResourceList, profileCollnMap, deviceGroupList);
                final List excludeList = excludeMap.get(profileId);
                if (excludeList != null) {
                    tempResourceList.removeAll(excludeList);
                }
                uniqueResources = new HashSet(tempResourceList);
                final JSONArray resourceJSONArray = new JSONArray();
                for (final Object value : uniqueResources) {
                    resourceJSONArray.put((Object)value);
                }
                if (resourceJSONArray.length() != 0) {
                    complianceJSON.put("resource_list", (Object)resourceJSONArray);
                    ComplianceDistributionHandler.getInstance().disassociateComplianceToDevices(complianceJSON);
                    for (final Object groupId : deviceGroupList) {
                        final HashMap groupDetails2 = deviceGroupMap.get((long)groupId);
                        final String groupName = groupDetails2.get("NAME");
                        final JSONObject eventLogJSON2 = new JSONObject();
                        eventLogJSON2.put("event_id", 72407);
                        eventLogJSON2.put("customer_id", (Object)customerId);
                        eventLogJSON2.put("remarks", (Object)"mdm.compliance.disassociated_device_group");
                        eventLogJSON2.put("remarks_args", (Object)(complianceName + "@@@" + groupName));
                        eventLogJSON2.put("user_name", (Object)userName);
                        eventLogJSON2.put("resource_id", (long)groupId);
                        ComplianceDBUtil.getInstance().complianceEventLogEntry(eventLogJSON2);
                    }
                }
            }
            MDMUtil.getUserTransaction().commit();
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- disassociateProfileForGroup() >   Error   ", e);
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final Exception e2) {
                this.logger.log(Level.SEVERE, "Exception in rollback in disassociateProfileForGroup", e2);
            }
            throw e;
        }
    }
    
    @Override
    public void associateProfileToMDMResource(final JSONObject complianceJSON) throws Exception {
        try {
            MDMUtil.getUserTransaction().begin();
            final int resourceType = complianceJSON.getInt("resource_type");
            final Long complianceId = JSONUtil.optLongForUVH(complianceJSON, "compliance_id", Long.valueOf(-1L));
            final JSONObject complianceDetailsJSON = ComplianceDBUtil.getInstance().getComplianceGeneralDetails(complianceJSON);
            final String complianceName = String.valueOf(complianceDetailsJSON.get("compliance_name"));
            complianceJSON.put("compliance_name", (Object)complianceName);
            switch (resourceType) {
                case 2: {
                    final JSONArray userJSONArray = complianceJSON.getJSONArray("resource_list");
                    final List userList = new ArrayList();
                    for (int i = 0; i < userJSONArray.length(); ++i) {
                        userList.add(userJSONArray.getLong(i));
                    }
                    final HashMap<Integer, Set> userMap = new MDMUserHandler().getUserIdsBasedOnType(userList);
                    complianceJSON.put("resource_list", (Object)new JSONArray((Collection)userMap.get(1)));
                    this.associateProfileToManagedUser(complianceJSON);
                    complianceJSON.put("resource_list", (Object)new JSONArray((Collection)userMap.get(2)));
                    this.associateProfileToDirectoryUser(complianceJSON);
                    break;
                }
                default: {
                    this.logger.log(Level.SEVERE, " -- associateProfileToMDMResource()    >   Invalid group type");
                    break;
                }
            }
            MDMUtil.getUserTransaction().commit();
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- associateProfileToMDMResource() >   Error   ", e);
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final Exception e2) {
                this.logger.log(Level.SEVERE, "Exception in rollback in associateProfileToMDMResource", e2);
            }
            throw e;
        }
    }
    
    @Override
    public void associateProfileToDirectoryUser(final JSONObject complianceJSON) throws Exception {
        try {
            final JSONObject complianceDetailsJSON = ComplianceDBUtil.getInstance().getComplianceGeneralDetails(complianceJSON);
            final String complianceName = String.valueOf(complianceDetailsJSON.get("compliance_name"));
            final Long userId = JSONUtil.optLongForUVH(complianceJSON, "user_id", Long.valueOf(-1L));
            final Long profileId = JSONUtil.optLongForUVH(complianceJSON, "compliance_id", Long.valueOf(-1L));
            final Long customerId = JSONUtil.optLongForUVH(complianceJSON, "customer_id", Long.valueOf(-1L));
            final String userName = String.valueOf(complianceJSON.get("user_name"));
            super.getExistingMDMResourceProfileDO(complianceJSON);
            this.finalDO = (DataObject)new WritableDataObject();
            super.updateMDMResourceToProfileDetails(complianceJSON);
            MDMUtil.getPersistence().update(this.finalDO);
            final JSONArray resourceJSONArray = complianceJSON.getJSONArray("resource_list");
            final String remarksArgs = String.valueOf(profileId) + "@@@";
            final Long collectionId = JSONUtil.optLongForUVH(complianceJSON, "collection_id", Long.valueOf(-1L));
            for (int i = 0; i < resourceJSONArray.length(); ++i) {
                final Long resourceId = JSONUtil.optLongForUVH(resourceJSONArray, i, -1L);
                final DeviceDetails deviceDetails = new DeviceDetails();
                deviceDetails.resourceId = resourceId;
                DeviceInvCommandHandler.getInstance().sendCommandToDevice(deviceDetails, "DeviceCompliance;Collection=" + collectionId.toString(), userId);
                final JSONObject eventLogJSON = new JSONObject();
                eventLogJSON.put("event_id", 72418);
                eventLogJSON.put("customer_id", (Object)customerId);
                eventLogJSON.put("remarks", (Object)"mdm.compliance.associated_user");
                eventLogJSON.put("remarks_args", (Object)(complianceName + "@@@" + deviceDetails.name));
                eventLogJSON.put("user_name", (Object)userName);
                eventLogJSON.put("resource_id", resourceJSONArray.get(i));
                ComplianceDBUtil.getInstance().complianceEventLogEntry(eventLogJSON);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- associateProfileToDirectoryUser() >   Error   ", e);
            throw e;
        }
    }
    
    @Override
    public void associateProfileToManagedUser(final JSONObject complianceJSON) throws Exception {
        try {
            final JSONObject complianceDetailsJSON = ComplianceDBUtil.getInstance().getComplianceGeneralDetails(complianceJSON);
            final String complianceName = String.valueOf(complianceDetailsJSON.get("compliance_name"));
            final Long profileId = JSONUtil.optLongForUVH(complianceJSON, "compliance_id", Long.valueOf(-1L));
            final Long customerId = JSONUtil.optLongForUVH(complianceJSON, "customer_id", Long.valueOf(-1L));
            final String userName = String.valueOf(complianceJSON.get("user_name"));
            super.getExistingMDMResourceProfileDO(complianceJSON);
            this.finalDO = (DataObject)new WritableDataObject();
            super.updateMDMResourceToProfileDetails(complianceJSON);
            MDMUtil.getPersistence().update(this.finalDO);
            final JSONArray userJSONArray = complianceJSON.getJSONArray("resource_list");
            final List userList = new ArrayList();
            final HashMap userMap = new HashMap();
            for (int i = 0; i < userJSONArray.length(); ++i) {
                final Long userId = JSONUtil.optLongForUVH(userJSONArray, i, -1L);
                userList.add(userId);
                userMap.put(userId, ManagedUserHandler.getInstance().getManagedUserDetails(userId));
            }
            final List<Long> resourceList = ManagedUserHandler.getInstance().getManagedDevicesListForManagedUsers(userList, 2);
            final JSONArray resourceJSONArray = new JSONArray();
            for (final Long resourceId : resourceList) {
                resourceJSONArray.put((Object)resourceId);
            }
            complianceJSON.put("resource_list", (Object)resourceJSONArray);
            ComplianceDistributionHandler.getInstance().associateComplianceToDevices(complianceJSON);
            final Long collectionId = JSONUtil.optLongForUVH(complianceJSON, "collection_id", Long.valueOf(-1L));
            ManagedUserCollectionStatusSummary.getInstance().updateManagedUserCollectionStatusSummary(userList, collectionId);
            final String remarksArgs = String.valueOf(profileId) + "@@@";
            for (int j = 0; j < userJSONArray.length(); ++j) {
                final Long userId2 = JSONUtil.optLongForUVH(userJSONArray, j, -1L);
                final HashMap userDetails = userMap.get(userId2);
                final JSONObject eventLogJSON = new JSONObject();
                final String managedUserName = userDetails.get("DISPLAY_NAME");
                eventLogJSON.put("event_id", 72418);
                eventLogJSON.put("customer_id", (Object)customerId);
                eventLogJSON.put("remarks", (Object)"mdm.compliance.associated_user");
                eventLogJSON.put("remarks_args", (Object)(complianceName + "@@@" + managedUserName));
                eventLogJSON.put("user_name", (Object)userName);
                eventLogJSON.put("resource_id", userJSONArray.get(j));
                ComplianceDBUtil.getInstance().complianceEventLogEntry(eventLogJSON);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- associateProfileToManagedUser() >   Error   ", e);
            throw e;
        }
    }
    
    @Override
    public void disassociateProfileToMDMResource(final JSONObject complianceJSON) throws Exception {
        try {
            MDMUtil.getUserTransaction().begin();
            final int resourceType = complianceJSON.getInt("resource_type");
            switch (resourceType) {
                case 2: {
                    final JSONArray userJSONArray = complianceJSON.getJSONArray("resource_list");
                    final List userList = new ArrayList();
                    for (int i = 0; i < userJSONArray.length(); ++i) {
                        userList.add(userJSONArray.getLong(i));
                    }
                    final HashMap<Integer, Set> userMap = new MDMUserHandler().getUserIdsBasedOnType(userList);
                    complianceJSON.put("resource_list", (Object)new JSONArray((Collection)userMap.get(1)));
                    this.disassociateProfileToManagedUser(complianceJSON);
                    complianceJSON.put("resource_list", (Object)new JSONArray((Collection)userMap.get(2)));
                    this.disassociateProfileToDirectoryUser(complianceJSON);
                    break;
                }
                default: {
                    this.logger.log(Level.SEVERE, " -- disassociateProfileToMDMResource()    >   Invalid group type");
                    break;
                }
            }
            MDMUtil.getUserTransaction().commit();
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- disassociateProfileToMDMResource() >   Error   ", e);
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final Exception e2) {
                this.logger.log(Level.SEVERE, "Exception in rollback in disassociateProfileToMDMResource", e2);
            }
            throw e;
        }
    }
    
    @Override
    public void disassociateProfileToDirectoryUser(final JSONObject complianceJSON) throws Exception {
        try {
            final JSONObject complianceDetailsJSON = ComplianceDBUtil.getInstance().getComplianceGeneralDetails(complianceJSON);
            final String complianceName = String.valueOf(complianceDetailsJSON.get("compliance_name"));
            final Long userId = JSONUtil.optLongForUVH(complianceJSON, "user_id", Long.valueOf(-1L));
            final Long profileId = JSONUtil.optLongForUVH(complianceJSON, "compliance_id", Long.valueOf(-1L));
            final Long customerId = JSONUtil.optLongForUVH(complianceJSON, "customer_id", Long.valueOf(-1L));
            final String userName = String.valueOf(complianceJSON.get("user_name"));
            super.getExistingMDMResourceProfileDO(complianceJSON);
            this.finalDO = (DataObject)new WritableDataObject();
            super.updateMDMResourceToProfileDetails(complianceJSON);
            MDMUtil.getPersistence().update(this.finalDO);
            super.deleteRecentProfileForMDMResource(complianceJSON);
            final JSONArray resourceJSONArray = complianceJSON.getJSONArray("resource_list");
            final Long collectionId = JSONUtil.optLongForUVH(complianceJSON, "collection_id", Long.valueOf(-1L));
            final String remarksArgs = String.valueOf(profileId) + "@@@";
            for (int i = 0; i < resourceJSONArray.length(); ++i) {
                final Long resourceId = JSONUtil.optLongForUVH(resourceJSONArray, i, -1L);
                final DeviceDetails deviceDetails = new DeviceDetails();
                deviceDetails.resourceId = resourceId;
                DeviceInvCommandHandler.getInstance().sendCommandToDevice(deviceDetails, "RemoveDeviceCompliance;Collection=" + collectionId.toString(), userId);
                final JSONObject eventLogJSON = new JSONObject();
                eventLogJSON.put("event_id", 72419);
                eventLogJSON.put("customer_id", (Object)customerId);
                eventLogJSON.put("remarks", (Object)"mdm.compliance.disassociated_user");
                eventLogJSON.put("remarks_args", (Object)(complianceName + "@@@" + deviceDetails.name));
                eventLogJSON.put("user_name", (Object)userName);
                eventLogJSON.put("resource_id", resourceJSONArray.get(i));
                ComplianceDBUtil.getInstance().complianceEventLogEntry(eventLogJSON);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- disassociateProfileToDirectoryUser() >   Error   ", e);
            throw e;
        }
    }
    
    @Override
    public void disassociateProfileToManagedUser(final JSONObject complianceJSON) throws Exception {
        try {
            final JSONObject complianceDetailsJSON = ComplianceDBUtil.getInstance().getComplianceGeneralDetails(complianceJSON);
            final String complianceName = String.valueOf(complianceDetailsJSON.get("compliance_name"));
            final Long profileId = JSONUtil.optLongForUVH(complianceJSON, "compliance_id", Long.valueOf(-1L));
            final Long customerId = JSONUtil.optLongForUVH(complianceJSON, "customer_id", Long.valueOf(-1L));
            final String userName = String.valueOf(complianceJSON.get("user_name"));
            super.getExistingMDMResourceProfileDO(complianceJSON);
            this.finalDO = (DataObject)new WritableDataObject();
            super.updateMDMResourceToProfileDetails(complianceJSON);
            MDMUtil.getPersistence().update(this.finalDO);
            super.deleteRecentProfileForMDMResource(complianceJSON);
            final JSONArray userJSONArray = complianceJSON.getJSONArray("resource_list");
            final List userList = new ArrayList();
            final HashMap userMap = new HashMap();
            for (int i = 0; i < userJSONArray.length(); ++i) {
                final Long userId = JSONUtil.optLongForUVH(userJSONArray, i, -1L);
                userList.add(userId);
                userMap.put(userId, ManagedUserHandler.getInstance().getManagedUserDetails(userId));
            }
            final List<Long> resourceList = ManagedUserHandler.getInstance().getManagedDevicesListForManagedUsers(userList, 2);
            final JSONArray resourceJSONArray = new JSONArray();
            for (final Long resourceId : resourceList) {
                resourceJSONArray.put((Object)resourceId);
            }
            complianceJSON.put("resource_list", (Object)resourceJSONArray);
            ComplianceDistributionHandler.getInstance().disassociateComplianceToDevices(complianceJSON);
            final Long collectionId = JSONUtil.optLongForUVH(complianceJSON, "collection_id", Long.valueOf(-1L));
            ManagedUserCollectionStatusSummary.getInstance().updateManagedUserCollectionStatusSummary(resourceList, collectionId);
            final String remarksArgs = String.valueOf(profileId) + "@@@";
            for (int j = 0; j < userJSONArray.length(); ++j) {
                final Long userId2 = JSONUtil.optLongForUVH(userJSONArray, j, -1L);
                final HashMap userDetails = userMap.get(userId2);
                final JSONObject eventLogJSON = new JSONObject();
                eventLogJSON.put("event_id", 72419);
                eventLogJSON.put("customer_id", (Object)customerId);
                final String managedUserName = userDetails.get("DISPLAY_NAME");
                eventLogJSON.put("remarks", (Object)"mdm.compliance.disassociated_user");
                eventLogJSON.put("remarks_args", (Object)(complianceName + "@@@" + managedUserName));
                eventLogJSON.put("user_name", (Object)userName);
                eventLogJSON.put("resource_id", userJSONArray.get(j));
                ComplianceDBUtil.getInstance().complianceEventLogEntry(eventLogJSON);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- associateProfileToManagedUser() >   Error   ", e);
            throw e;
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
            deleteCommandList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionList, "DeviceCompliance");
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
    
    public void handleComplianceRemovalForAssociatedResources(final JSONObject requestJSON) throws Exception {
        try {
            final Long complianceId = JSONUtil.optLongForUVH(requestJSON, "compliance_id", Long.valueOf(-1L));
            final Long collectionId = JSONUtil.optLongForUVH(requestJSON, "collection_id", Long.valueOf(-1L));
            final Long customerId = JSONUtil.optLongForUVH(requestJSON, "customer_id", Long.valueOf(-1L));
            final Long userId = JSONUtil.optLongForUVH(requestJSON, "user_id", Long.valueOf(-1L));
            requestJSON.put("marked_for_delete", true);
            final HashMap taskInfoMap = new HashMap();
            taskInfoMap.put("taskName", "ComplianceProfilePublishTask");
            taskInfoMap.put("schedulerTime", MDMUtil.getCurrentTimeInMillis());
            taskInfoMap.put("poolName", "asynchThreadPool");
            final Properties properties = new Properties();
            ((Hashtable<String, Long>)properties).put("compliance_id", complianceId);
            ((Hashtable<String, Long>)properties).put("collection_id", collectionId);
            ((Hashtable<String, Long>)properties).put("customer_id", customerId);
            ((Hashtable<String, Long>)properties).put("user_id", userId);
            ((Hashtable<String, String>)properties).put("compliance_state", "compliance_deleted");
            this.logger.log(Level.INFO, " Beginning to execute task for publish compliance ");
            ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.server.compliance.task.ComplianceProfilePublishTask", taskInfoMap, properties);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- handleComplianceRemovalForAssociatedResources() >   Error   ", e);
            throw e;
        }
    }
    
    static {
        ComplianceProfileAssociationDataHandler.complianceProfileAssociationDataHandler = null;
    }
}
