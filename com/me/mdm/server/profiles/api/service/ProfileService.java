package com.me.mdm.server.profiles.api.service;

import java.util.Hashtable;
import java.util.HashSet;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.mdm.server.msp.sync.SyncConfigurationListeners;
import com.adventnet.sym.webclient.mdm.config.ProfileConfigHandler;
import com.me.mdm.api.model.BaseAPIModel;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.server.msp.sync.ConfigurationSyncEngineConstants;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.persistence.DataAccessException;
import com.me.mdm.server.device.DeviceFacade;
import com.me.mdm.server.profiles.api.model.ProfileAssociationtoDeviceModel;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import com.me.mdm.api.APIUtil;
import com.me.mdm.server.profiles.ProfileFacade;
import java.util.HashMap;
import java.util.List;
import com.me.mdm.server.onelinelogger.MDMOneLineLogger;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import java.util.Properties;
import java.util.logging.Level;
import com.me.mdm.server.customgroup.GroupFacade;
import java.util.Collection;
import java.util.ArrayList;
import org.json.simple.JSONObject;
import com.me.mdm.server.profiles.api.model.ProfileAssociationToGroupModel;
import java.util.logging.Logger;

public class ProfileService
{
    protected static Logger logger;
    
    protected Integer getProfileType() {
        return 1;
    }
    
    public void associateProfilesToGroups(final ProfileAssociationToGroupModel profileAssociationToGroupModel) throws APIHTTPException {
        final JSONObject secLog = new JSONObject();
        String remarks = "associate-failed";
        try {
            final Long profileID = profileAssociationToGroupModel.getProfileId();
            final Long customerID = profileAssociationToGroupModel.getCustomerId();
            List<Long> profileList;
            if (profileID != null) {
                profileList = new ArrayList<Long>();
                profileList.add(profileID);
            }
            else {
                profileList = profileAssociationToGroupModel.getProfileIds();
            }
            secLog.put((Object)"PROFILE_IDs", (Object)profileList);
            final HashMap profileCollectionMap = this.validateIfProfilesDistribute(profileList, profileAssociationToGroupModel.getCustomerId(), null);
            final Long groupId = profileAssociationToGroupModel.getGroupId();
            List<Long> groupList;
            if (groupId != null) {
                groupList = new ArrayList<Long>();
                groupList.add(groupId);
            }
            else {
                groupList = profileAssociationToGroupModel.getGroupIds();
            }
            secLog.put((Object)"GROUP_IDs", (Object)groupList);
            new GroupFacade().validateGroupsIfExists(groupList, customerID);
            ProfileService.logger.log(Level.INFO, "associate profiles : [{0}] to groups : [{0}]", new Object[] { profileList, groupList });
            final Properties properties = new Properties();
            ((Hashtable<String, Boolean>)properties).put("isAppConfig", false);
            ((Hashtable<String, Long>)properties).put("customerId", customerID);
            ((Hashtable<String, String>)properties).put("commandName", ProfileUtil.getInstance().getProfileCommand(this.getProfileType(), 1));
            ((Hashtable<String, HashMap>)properties).put("profileCollectionMap", profileCollectionMap);
            ((Hashtable<String, Boolean>)properties).put("profileOrigin", true);
            ((Hashtable<String, Boolean>)properties).put("isGroup", true);
            ((Hashtable<String, List<Long>>)properties).put("resourceList", groupList);
            ((Hashtable<String, Integer>)properties).put("groupType", 6);
            ((Hashtable<String, Long>)properties).put("loggedOnUser", profileAssociationToGroupModel.getUserId());
            ((Hashtable<String, String>)properties).put("loggedOnUserName", DMUserHandler.getDCUser(profileAssociationToGroupModel.getLogInId()));
            ProfileAssociateHandler.getInstance().associateCollectionForGroup(properties);
            remarks = "associate-success";
        }
        catch (final Exception e) {
            ProfileService.logger.log(Level.SEVERE, "exception occurred in associateProfilesToGroups()", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "ASSOCIATE_PROFILE", secLog);
        }
    }
    
    public void disassociateProfilesToGroups(final ProfileAssociationToGroupModel profileAssociationToGroupModel) throws APIHTTPException {
        final JSONObject secLog = new JSONObject();
        String remarks = "dissociate-failed";
        try {
            final Long profileID = profileAssociationToGroupModel.getProfileId();
            final Long customerID = profileAssociationToGroupModel.getCustomerId();
            final Long groupId = profileAssociationToGroupModel.getGroupId();
            List<Long> groupList;
            if (groupId != null) {
                groupList = new ArrayList<Long>();
                groupList.add(groupId);
            }
            else {
                groupList = profileAssociationToGroupModel.getGroupIds();
            }
            secLog.put((Object)"GROUP_IDs", (Object)groupList);
            final GroupFacade group = new GroupFacade();
            group.validateGroupsIfExists(groupList, customerID);
            List<Long> profileList;
            if (profileID != null) {
                profileList = new ArrayList<Long>();
                profileList.add(profileID);
            }
            else {
                profileList = profileAssociationToGroupModel.getProfileIds();
            }
            secLog.put((Object)"PROFILE_IDs", (Object)profileList);
            final HashMap profileCollectionMap = new HashMap();
            final Map groupProfileMap = new ProfileFacade().getProfileForGroup(groupList, profileList, this.getProfileType(), false);
            if (groupProfileMap.isEmpty() && !profileList.isEmpty()) {
                throw new APIHTTPException("COM0008", new Object[] { APIUtil.getCommaSeperatedString(profileList) });
            }
            final Set<Long> groupIdSet = groupProfileMap.keySet();
            for (final Long groupIdLong : groupIdSet) {
                final Map deviceProfileCollectionMap = groupProfileMap.get(groupIdLong);
                final ArrayList validateProfile = new ArrayList((Collection<? extends E>)profileList);
                final Set<Long> profileIdSet = deviceProfileCollectionMap.keySet();
                validateProfile.removeAll(profileIdSet);
                if (!validateProfile.isEmpty()) {
                    throw new APIHTTPException("COM0008", new Object[] { APIUtil.getCommaSeperatedString(profileList) });
                }
                profileCollectionMap.putAll(deviceProfileCollectionMap);
            }
            ProfileService.logger.log(Level.INFO, "disassociate profiles : [{0}] to groups : [{1}]", new Object[] { profileList, groupList });
            final Properties properties = new Properties();
            ((Hashtable<String, HashMap>)properties).put("profileCollectionMap", profileCollectionMap);
            ((Hashtable<String, Boolean>)properties).put("isAppConfig", false);
            ((Hashtable<String, String>)properties).put("commandName", ProfileUtil.getInstance().getProfileCommand(this.getProfileType(), 0));
            ((Hashtable<String, Long>)properties).put("customerId", profileAssociationToGroupModel.getCustomerId());
            ((Hashtable<String, Boolean>)properties).put("profileOrigin", true);
            ((Hashtable<String, Boolean>)properties).put("isGroup", true);
            ((Hashtable<String, Integer>)properties).put("groupType", 6);
            ((Hashtable<String, List<Long>>)properties).put("resourceList", groupList);
            ((Hashtable<String, Long>)properties).put("loggedOnUser", profileAssociationToGroupModel.getUserId());
            ((Hashtable<String, String>)properties).put("loggedOnUserName", DMUserHandler.getDCUser(profileAssociationToGroupModel.getLogInId()));
            ProfileAssociateHandler.getInstance().disAssociateCollectionForGroup(properties);
            remarks = "dissociate-success";
        }
        catch (final Exception e) {
            ProfileService.logger.log(Level.SEVERE, "exception occurred in disassociateProfilesToGroups()", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "DISSOCIATE_PROFILE", secLog);
        }
    }
    
    public void associateProfilesToDevices(final ProfileAssociationtoDeviceModel profileAssociationtoDeviceModel) throws APIHTTPException {
        final JSONObject secLog = new JSONObject();
        String remarks = "associate-failed";
        try {
            final Long profileID = profileAssociationtoDeviceModel.getProfileId();
            final Long customerID = profileAssociationtoDeviceModel.getCustomerId();
            final Long deviceId = profileAssociationtoDeviceModel.getDeviceId();
            List<Long> resourceList;
            if (deviceId != null) {
                resourceList = new ArrayList<Long>();
                resourceList.add(deviceId);
            }
            else {
                resourceList = profileAssociationtoDeviceModel.getDeviceIds();
            }
            secLog.put((Object)"DEVICE_IDs", (Object)resourceList);
            final HashMap<Integer, ArrayList> platformDeviceMap = new DeviceFacade().validateIfDevicesExists(resourceList, customerID);
            if (platformDeviceMap.size() > 1) {
                throw new APIHTTPException("COM0015", new Object[] { "Devices are not with the unique platform type" });
            }
            final Integer platformType = platformDeviceMap.keySet().iterator().next();
            List<Long> profileList;
            if (profileID != null) {
                profileList = new ArrayList<Long>();
                profileList.add(profileID);
            }
            else {
                profileList = profileAssociationtoDeviceModel.getProfileIds();
            }
            secLog.put((Object)"PROFILE_IDs", (Object)profileList);
            final HashMap profileCollectionMap = this.validateIfProfilesDistribute(profileList, customerID, platformType);
            ProfileService.logger.log(Level.INFO, "associate profiles : [{0}] to device : [{0}]", new Object[] { profileList, resourceList });
            final Properties properties = new Properties();
            ((Hashtable<String, HashMap>)properties).put("profileCollectionMap", profileCollectionMap);
            ((Hashtable<String, Boolean>)properties).put("isAppConfig", false);
            ((Hashtable<String, String>)properties).put("commandName", ProfileUtil.getInstance().getProfileCommand(this.getProfileType(), 1));
            ((Hashtable<String, Long>)properties).put("customerId", customerID);
            ((Hashtable<String, Boolean>)properties).put("profileOrigin", false);
            ((Hashtable<String, Boolean>)properties).put("isGroup", false);
            ((Hashtable<String, Integer>)properties).put("profileOriginInt", 120);
            ((Hashtable<String, List<Long>>)properties).put("resourceList", resourceList);
            ((Hashtable<String, Long>)properties).put("loggedOnUser", profileAssociationtoDeviceModel.getUserId());
            ProfileAssociateHandler.getInstance().associateCollectionForResource(properties);
            remarks = "associate-success";
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "ASSOCIATE_PROFILE", secLog);
        }
    }
    
    public void disassociateProfilesToDevices(final ProfileAssociationtoDeviceModel profileAssociationtoDeviceModel) throws APIHTTPException, DataAccessException {
        final JSONObject secLog = new JSONObject();
        String remarks = "dissociate-failed";
        try {
            final Long profileID = profileAssociationtoDeviceModel.getProfileId();
            final Long deviceId = profileAssociationtoDeviceModel.getDeviceId();
            final Long customerId = profileAssociationtoDeviceModel.getCustomerId();
            List<Long> resourceList;
            if (deviceId != null) {
                new DeviceFacade().validateIfDeviceExists(deviceId, customerId);
                resourceList = new ArrayList<Long>();
                resourceList.add(deviceId);
            }
            else {
                resourceList = profileAssociationtoDeviceModel.getDeviceIds();
                new DeviceFacade().validateIfDevicesExists(resourceList, customerId);
            }
            secLog.put((Object)"DEVICE_IDs", (Object)resourceList);
            List<Long> profileList;
            if (profileID != null) {
                profileList = new ArrayList<Long>();
                profileList.add(profileID);
            }
            else {
                profileList = profileAssociationtoDeviceModel.getProfileIds();
            }
            secLog.put((Object)"PROFILE_IDs", (Object)profileList);
            final HashMap profileCollectionMap = new HashMap();
            final Map deviceProfileMap = new ProfileFacade().getProfileForDevice(resourceList, profileList, this.getProfileType());
            final Set<Long> deviceIdSet = deviceProfileMap.keySet();
            for (final Long deviceIdLong : deviceIdSet) {
                final ArrayList validateProfile = new ArrayList((Collection<? extends E>)profileList);
                final Map deviceProfileCollectionMap = deviceProfileMap.get(deviceIdLong);
                final Set<Long> profileIdSet = deviceProfileCollectionMap.keySet();
                validateProfile.removeAll(profileIdSet);
                if (!validateProfile.isEmpty()) {
                    throw new APIHTTPException("COM0008", new Object[] { APIUtil.getCommaSeperatedString(validateProfile) });
                }
                profileCollectionMap.putAll(deviceProfileCollectionMap);
            }
            ProfileService.logger.log(Level.INFO, "disassociate profiles : [{0}] to devices : [{0}]", new Object[] { profileList, resourceList });
            final Properties properties = new Properties();
            ((Hashtable<String, HashMap>)properties).put("profileCollectionMap", profileCollectionMap);
            ((Hashtable<String, Boolean>)properties).put("isAppConfig", false);
            ((Hashtable<String, String>)properties).put("commandName", ProfileUtil.getInstance().getProfileCommand(this.getProfileType(), 0));
            ((Hashtable<String, Boolean>)properties).put("isSilentInstall", false);
            ((Hashtable<String, Boolean>)properties).put("isNotify", false);
            ((Hashtable<String, Long>)properties).put("customerId", customerId);
            ((Hashtable<String, Boolean>)properties).put("profileOrigin", false);
            ((Hashtable<String, Integer>)properties).put("profileOriginInt", 120);
            ((Hashtable<String, List<Long>>)properties).put("resourceList", resourceList);
            ((Hashtable<String, Long>)properties).put("loggedOnUser", profileAssociationtoDeviceModel.getUserId());
            ProfileAssociateHandler.getInstance().disAssociateCollectionForResource(properties);
            remarks = "dissociate-success";
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "DISSOCIATE_PROFILE", secLog);
        }
    }
    
    private void validateProfileForMovingToAllCustomers(final Long profileId) throws DataAccessException {
        if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("SyncConfigurationsForAllCustomers") || !CustomerInfoUtil.getInstance().isMSP()) {
            throw new APIHTTPException("COM0015", new Object[0]);
        }
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
        selectQuery.addJoin(new Join("Profile", "RecentPubProfileToColln", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("RecentPubProfileToColln", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        selectQuery.setCriteria(new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileId, 0));
        selectQuery.addSelectColumn(new Column("Profile", "*"));
        selectQuery.addSelectColumn(new Column("ConfigData", "*"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        final Row profileRow = dataObject.getFirstRow("Profile");
        final int platformType = (int)profileRow.get("PLATFORM_TYPE");
        final int profileType = (int)profileRow.get("PROFILE_TYPE");
        final Integer profileSharedScope = (Integer)profileRow.get("PROFILE_SHARED_SCOPE");
        final Boolean isForAllCustomers = profileSharedScope == 1;
        if (isForAllCustomers) {
            ProfileService.logger.log(Level.SEVERE, "Profile already in global scope");
            throw new APIHTTPException("COM0015", new Object[0]);
        }
        if ((platformType == 1 || platformType == 2) && profileType == 1) {
            final Iterator<Row> iterator = dataObject.getRows("ConfigData");
            while (iterator.hasNext()) {
                final Row configRow = iterator.next();
                final int configId = (int)configRow.get("CONFIG_ID");
                if (ConfigurationSyncEngineConstants.restrictedPayloads.contains(configId)) {
                    throw new APIHTTPException("PAY0015", new Object[0]);
                }
            }
        }
    }
    
    public void moveProfileToAllCustomers(final BaseAPIModel apiModel, final Long profileId) throws Exception {
        final Long customerId = apiModel.getCustomerId();
        final org.json.JSONObject profileDetails = new ProfileFacade().validateAndGetIfProfileExists(profileId, customerId);
        this.validateProfileForMovingToAllCustomers(profileId);
        ProfileConfigHandler.makeProfileForAllCustomers(profileId);
        profileDetails.put("LAST_MODIFIED_BY", (Object)apiModel.getUserId());
        profileDetails.put("LOGIN_ID", (Object)apiModel.getLogInId());
        SyncConfigurationListeners.invokeListeners(profileDetails, 100);
        final String sEventLogRemarks = "dc.mdm.actionlog.profilemgmt.move_to_all_customers";
        final String remarksArgs = (String)profileDetails.get("PROFILE_NAME");
        MDMEventLogHandler.getInstance().MDMEventLogEntry(2021, null, apiModel.getUserName(), sEventLogRemarks, remarksArgs, customerId);
    }
    
    public HashMap validateIfProfilesDistribute(Collection<Long> profileIDs, final Long customerID, final Integer platformType) throws APIHTTPException {
        if (profileIDs.isEmpty()) {
            throw new APIHTTPException("ENR00105", new Object[0]);
        }
        final HashMap<Long, Long> profileCollectionMap = new HashMap<Long, Long>();
        try {
            ProfileService.logger.log(Level.INFO, "validate profile can distribute, profile ids:{0}", profileIDs);
            profileIDs = new HashSet<Long>(profileIDs);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
            selectQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            selectQuery.addJoin(new Join("Profile", "RecentProfileToColln", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            selectQuery.addJoin(new Join("Profile", "RecentPubProfileToColln", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 1));
            selectQuery.addJoin(new Join("RecentProfileToColln", "CollectionStatus", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ProfileToCustomerRel", "PROFILE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"));
            selectQuery.addSelectColumn(Column.getColumn("RecentProfileToColln", "PROFILE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("RecentProfileToColln", "COLLECTION_ID"));
            selectQuery.addSelectColumn(Column.getColumn("RecentPubProfileToColln", "PROFILE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("RecentPubProfileToColln", "COLLECTION_ID"));
            selectQuery.addSelectColumn(Column.getColumn("CollectionStatus", "COLLECTION_ID"));
            selectQuery.addSelectColumn(Column.getColumn("CollectionStatus", "PROFILE_COLLECTION_STATUS"));
            Criteria criteria = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileIDs.toArray(), 8).and(new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0));
            if (platformType != null) {
                final Criteria platformNeutralCriteria = new Criteria(Column.getColumn("Profile", "PLATFORM_TYPE"), (Object)0, 0);
                if (platformType.equals(1)) {
                    criteria = criteria.and(new Criteria(Column.getColumn("Profile", "PLATFORM_TYPE"), (Object)new Integer[] { 7, 6, 1 }, 8).or(platformNeutralCriteria));
                }
                else {
                    criteria = criteria.and(new Criteria(Column.getColumn("Profile", "PLATFORM_TYPE"), (Object)platformType, 0).or(platformNeutralCriteria));
                }
            }
            this.setProfileQueryCriteria(selectQuery, criteria);
            final DataObject dataObject = DataAccess.get(selectQuery);
            final List profileCheckIds = new ArrayList(profileIDs);
            final Iterator<Row> rows = dataObject.getRows("RecentProfileToColln");
            final Set draftProfiles = new HashSet();
            while (rows.hasNext()) {
                final Row profileCollectionRow = rows.next();
                final Long profileId = (Long)profileCollectionRow.get("PROFILE_ID");
                final Long recentCollectionId = (Long)profileCollectionRow.get("COLLECTION_ID");
                final Row recentPubProfileToCooln = dataObject.getRow("RecentPubProfileToColln", new Criteria(Column.getColumn("RecentPubProfileToColln", "PROFILE_ID"), (Object)profileId, 0));
                if (recentPubProfileToCooln == null) {
                    draftProfiles.add(profileId);
                    profileCollectionMap.put(profileId, recentCollectionId);
                }
                else {
                    final Long collectionId = (Long)recentPubProfileToCooln.get("COLLECTION_ID");
                    profileCollectionMap.put(profileId, collectionId);
                }
            }
            final Set availableProfileIds = profileCollectionMap.keySet();
            profileCheckIds.removeAll(availableProfileIds);
            if (profileCheckIds.size() > 0) {
                final String remark = "Profile Id :" + APIUtil.getCommaSeperatedString(profileCheckIds);
                throw new APIHTTPException("COM0008", new Object[] { remark });
            }
            if (draftProfiles.size() > 0) {
                throw new APIHTTPException("COM0015", new Object[] { "Profiles [" + APIUtil.getCommaSeperatedString(draftProfiles) + "] should be in published status" });
            }
        }
        catch (final DataAccessException ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return profileCollectionMap;
    }
    
    private void setProfileQueryCriteria(final SelectQuery selectQuery, final Criteria criteria) {
        Criteria profileTypeCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)this.getProfileType(), 0);
        if (criteria != null) {
            profileTypeCriteria = profileTypeCriteria.and(criteria);
        }
        selectQuery.setCriteria(profileTypeCriteria);
    }
    
    static {
        ProfileService.logger = Logger.getLogger("MDMApiLogger");
    }
}
