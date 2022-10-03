package com.adventnet.sym.server.mdm.config;

import java.util.Hashtable;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.SortColumn;
import java.util.HashSet;
import com.me.mdm.server.deployment.DeplymentConfigHandler;
import java.util.Arrays;
import com.me.mdm.server.profiles.ios.IOSPerAppVPNHandler;
import com.me.mdm.server.apps.config.AppConfigPolicyDBHandler;
import com.me.mdm.server.profiles.kiosk.IOSKioskProfileDataHandler;
import com.me.mdm.server.config.MDMConfigUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.mdm.server.resource.MDMResourceDataProvider;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.ds.query.Join;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataAccess;
import java.util.Set;
import com.me.mdm.server.deployment.MDMResourceToProfileDeploymentConfigHandler;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.queue.DCQueue;
import com.me.mdm.server.tracker.mics.MICSGroupFeatureController;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.me.mdm.server.profiles.ProfileFacade;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.Collection;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.sym.server.mdm.util.MDMCommonConstants;
import java.util.Map;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.status.GroupCollectionStatusSummary;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import java.util.List;
import org.json.JSONException;
import java.util.Properties;
import org.json.JSONArray;
import java.util.ArrayList;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.me.devicemanagement.framework.server.customgroup.CustomGroupUtil;
import java.util.logging.Level;
import java.net.URLEncoder;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.simple.JSONObject;
import java.util.logging.Logger;

public class ProfileAssociateHandler
{
    public static Logger logger;
    public static Logger profileLogger;
    public static Logger profileDistributionLog;
    private static ProfileAssociateHandler handler;
    public static final String MDM_DOMAIN_NAME = "MDM";
    public static final String IS_ANDROID_PROFILE_AVAILABLE = "IS_ANDROID_PROFILE_AVAILABLE";
    public static final String IS_KNOX_PROFILE_AVAILABLE = "IS_KNOX_PROFILE_AVAILABLE";
    private static final String GROUP = "Group";
    private static final String RESOURCE = "Resource";
    
    public static ProfileAssociateHandler getInstance() {
        if (ProfileAssociateHandler.handler == null) {
            ProfileAssociateHandler.handler = new ProfileAssociateHandler();
        }
        return ProfileAssociateHandler.handler;
    }
    
    public JSONObject getGroupProfileTreeJSON(final Long groupId, final Long customerId, final int profileType) {
        JSONObject profileJSON = null;
        Connection conn = null;
        DataSet ds = null;
        try {
            JSONObject profilePropJSON = null;
            final SelectQuery profileQuery = ProfileUtil.getInstance().getQueryforProfileCollnGroup(groupId, customerId);
            final Criteria profileTypeCri = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)profileType, 0);
            profileQuery.setCriteria(profileQuery.getCriteria().and(profileTypeCri));
            final RelationalAPI relapi = RelationalAPI.getInstance();
            conn = relapi.getConnection();
            ds = relapi.executeQuery((Query)profileQuery, conn);
            profileJSON = new JSONObject();
            Long profileId = null;
            Long collectionId = null;
            String profileCollnId = null;
            String profileName = "";
            while (ds.next()) {
                profilePropJSON = new JSONObject();
                profileId = (Long)ds.getValue("PROFILE_ID");
                profileName = (String)ds.getValue("PROFILE_NAME");
                profileName = URLEncoder.encode(profileName, "UTF-8");
                collectionId = (Long)ds.getValue("COLLECTION_ID");
                profileCollnId = String.valueOf(profileId).concat("_").concat(String.valueOf(collectionId));
                profilePropJSON.put((Object)"NODE_ID", (Object)profileCollnId);
                profileJSON.put((Object)profileCollnId, (Object)profilePropJSON);
            }
        }
        catch (final Exception e) {
            ProfileAssociateHandler.logger.log(Level.WARNING, "Exception occured in getGroupProfileTreeJSON....", e);
        }
        finally {
            CustomGroupUtil.getInstance().closeConnection(conn, ds);
        }
        return profileJSON;
    }
    
    public ArrayList decodeProfileCollnIds(final String profileCollnIds) {
        final ArrayList profileCollectionList = new ArrayList();
        if (profileCollnIds != null) {
            try {
                final JSONArray profileCollnJSONArr = new JSONArray(profileCollnIds);
                String profileColln = null;
                String[] profileSplit = null;
                for (int i = 0; i < profileCollnJSONArr.length(); ++i) {
                    profileColln = profileCollnJSONArr.optString(i);
                    profileSplit = profileColln.split("_");
                    final Properties properties = new Properties();
                    ((Hashtable<String, Long>)properties).put("PROFILE_ID", Long.valueOf(profileSplit[0]));
                    ((Hashtable<String, Long>)properties).put("COLLECTION_ID", Long.valueOf(profileSplit[1]));
                    profileCollectionList.add(properties);
                }
            }
            catch (final JSONException ex) {
                Logger.getLogger(ProfileAssociateHandler.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
            }
        }
        return profileCollectionList;
    }
    
    public void addOrUpdateGroupTables(final List<Long> groupList, final Long profileID, final Long collectionID) {
        try {
            AppsUtil.getInstance().addOrUpdateAppCatalogToGroup(groupList, collectionID);
            GroupCollectionStatusSummary.getInstance().updateGroupCollectionStatusSummary(groupList, collectionID);
            getInstance().updateGroupProfileSummary();
        }
        catch (final Exception ex) {
            ProfileAssociateHandler.logger.log(Level.SEVERE, "Exception in addOrUpdateGroupTables {0}", ex);
        }
    }
    
    public void associateCollectionForGroup(final Properties properties) {
        ProfileAssociateHandler.profileDistributionLog.log(Level.INFO, "Initiation of Associating the Collection to the group : {0}", properties);
        final List groupList = ((Hashtable<K, List>)properties).get("resourceList");
        final Boolean isAppConfig = ((Hashtable<K, Boolean>)properties).get("isAppConfig");
        final long customerId = ((Hashtable<K, Long>)properties).get("customerId");
        try {
            if (!isAppConfig) {
                this.distributeKioskApp(properties);
            }
            final ProfileAssociateDataHandler handler = new ProfileAssociateDataHandler();
            handler.associateProfileForGroup(properties);
            final HashMap hash = MDMUtil.getInstance().getCurrentlyLoggenOnUserInfo();
            final String sLoggedOnUserName = hash.get("UserName");
            final HashMap profileCollectionMap = ((Hashtable<K, HashMap>)properties).get("profileCollectionMap");
            final org.json.JSONObject qData = new org.json.JSONObject();
            qData.put("ProfileToCollectionMap", (Map)profileCollectionMap);
            qData.put("CollectionApplicableResource", ((Hashtable<K, Object>)properties).get("collectionToApplicableResource"));
            qData.put("profileToBusinessStore", ((Hashtable<K, Object>)properties).get("profileToBusinessStore"));
            qData.put("isGroup", (Object)Boolean.TRUE);
            qData.put("AssociatedUserName", (Object)sLoggedOnUserName);
            if (properties.containsKey("toBeAssociatedAppSource") && Integer.valueOf(String.valueOf(((Hashtable<K, Object>)properties).get("toBeAssociatedAppSource"))) == MDMCommonConstants.ASSOCIATED_APP_SOURCE_BY_AUTO_UPDATE) {
                Long loggedInUser = -1L;
                if (properties.containsKey("loggedOnUser")) {
                    try {
                        loggedInUser = Long.valueOf(properties.getProperty("loggedOnUser"));
                        final String userName = DMUserHandler.getUserNameFromUserID(loggedInUser);
                        qData.put("AssociatedUserName", (Object)userName);
                    }
                    catch (final Exception e) {
                        qData.put("AssociatedUserName", (Object)sLoggedOnUserName);
                    }
                }
            }
            if (properties.get("commandName") != null) {
                qData.put("CommandName", ((Hashtable<K, Object>)properties).get("commandName"));
            }
            qData.put("CustomerID", customerId);
            qData.put("ResourceList", (Collection)groupList);
            qData.put("isAppConfig", (Object)isAppConfig);
            qData.put("EventTimeStamp", (Object)new Long(System.currentTimeMillis()));
            final DCQueueData queueData = new DCQueueData();
            queueData.fileName = this.getAuditLogQueueDataFileName(customerId, isAppConfig);
            queueData.queueData = qData.toString();
            queueData.postTime = MDMUtil.getCurrentTimeInMillis();
            queueData.queueDataType = (isAppConfig ? 201 : 200);
            final DCQueue dcQueue = DCQueueHandler.getQueue("mdm-audit-log");
            dcQueue.addToQueue(queueData);
            if (!isAppConfig) {
                new ProfileFacade().setUpdateCountMessageStatus(customerId);
            }
            if (isAppConfig || (properties.get("commandName") != null && ((Hashtable<K, String>)properties).get("commandName").equalsIgnoreCase("InstallProfile"))) {
                final List<HashMap> groupDetails = MDMGroupHandler.getInstance().getGroupDetails(groupList);
                for (final HashMap groupDetail : groupDetails) {
                    MICSGroupFeatureController.addTrackingData(groupDetail.get("GROUP_TYPE"), MICSGroupFeatureController.GroupOperation.INTEGRATE, "MDM".equalsIgnoreCase(groupDetail.get("DOMAIN_NETBIOS_NAME")));
                }
            }
        }
        catch (final Exception exp) {
            ProfileAssociateHandler.profileDistributionLog.log(Level.SEVERE, "Exception in associateCollectionForGroup", exp);
        }
    }
    
    public void associateCollectionForResource(final Properties properties) {
        final List resourceList = ((Hashtable<K, List>)properties).get("resourceList");
        final Boolean isAppConfig = ((Hashtable<K, Boolean>)properties).get("isAppConfig");
        final long customerId = ((Hashtable<K, Long>)properties).get("customerId");
        final HashMap profileCollectionMap = ((Hashtable<K, HashMap>)properties).get("profileCollectionMap");
        final Boolean isProfileOrigin = ((Hashtable<K, Boolean>)properties).get("profileOrigin");
        final HashMap profileProperties = ((Hashtable<K, HashMap>)properties).get("profileProperties");
        if (isProfileOrigin == null) {
            ((Hashtable<String, Boolean>)properties).put("profileOrigin", false);
        }
        try {
            ProfileAssociateHandler.profileDistributionLog.log(Level.INFO, "associateCollection:Resource ID {0}", resourceList.toString());
            ProfileAssociateHandler.profileDistributionLog.log(Level.INFO, "associateCollection:profileId {0}", profileCollectionMap.toString());
            final Boolean kioskProfileApplied = (properties.get("kioskProfileApplied") != null) ? ((Hashtable<K, Boolean>)properties).get("kioskProfileApplied") : Boolean.FALSE;
            if (properties.get("isGroup") != null && !isAppConfig && !kioskProfileApplied) {
                this.distributeKioskApp(properties);
            }
            final ProfileAssociateDataHandler handler = new ProfileAssociateDataHandler();
            handler.associateProfileForDevice(properties);
            String sLoggedOnUserName = ((Hashtable<K, String>)properties).get("loggedOnUserName");
            if (sLoggedOnUserName == null) {
                final HashMap hash = MDMUtil.getInstance().getCurrentlyLoggenOnUserInfo();
                sLoggedOnUserName = hash.get("UserName");
            }
            final String associatedUsername = sLoggedOnUserName;
            final org.json.JSONObject qData = new org.json.JSONObject();
            if (profileProperties != null) {
                qData.put("ProfileProperties", (Map)profileProperties);
            }
            qData.put("ProfileToCollectionMap", (Map)profileCollectionMap);
            qData.put("CollectionApplicableResource", ((Hashtable<K, Object>)properties).get("collectionToApplicableResource"));
            qData.put("profileToBusinessStore", ((Hashtable<K, Object>)properties).get("profileToBusinessStore"));
            qData.put("isGroup", (Object)Boolean.FALSE);
            qData.put("AssociatedUserName", (Object)associatedUsername);
            if (properties.containsKey("toBeAssociatedAppSource") && Integer.valueOf(String.valueOf(((Hashtable<K, Object>)properties).get("toBeAssociatedAppSource"))) == MDMCommonConstants.ASSOCIATED_APP_SOURCE_BY_AUTO_UPDATE) {
                Long loggedInUser = -1L;
                if (properties.containsKey("loggedOnUser")) {
                    try {
                        loggedInUser = Long.valueOf(properties.getProperty("loggedOnUser"));
                        final String userName = DMUserHandler.getUserNameFromUserID(loggedInUser);
                        qData.put("AssociatedUserName", (Object)userName);
                    }
                    catch (final Exception e) {
                        qData.put("AssociatedUserName", (Object)associatedUsername);
                    }
                }
            }
            if (properties.get("commandName") != null) {
                qData.put("CommandName", ((Hashtable<K, Object>)properties).get("commandName"));
            }
            qData.put("CustomerID", customerId);
            qData.put("ResourceList", (Collection)resourceList);
            qData.put("isAppConfig", (Object)isAppConfig);
            qData.put("EventTimeStamp", (Object)new Long(System.currentTimeMillis()));
            final DCQueueData queueData = new DCQueueData();
            queueData.fileName = this.getAuditLogQueueDataFileName(customerId, isAppConfig);
            queueData.queueData = qData.toString();
            queueData.postTime = MDMUtil.getCurrentTimeInMillis();
            queueData.queueDataType = (isAppConfig ? 201 : 200);
            final DCQueue dcQueue = DCQueueHandler.getQueue("mdm-audit-log");
            dcQueue.addToQueue(queueData);
            if (!isAppConfig) {
                new ProfileFacade().setUpdateCountMessageStatus(customerId);
            }
        }
        catch (final Exception exp) {
            ProfileAssociateHandler.profileDistributionLog.log(Level.SEVERE, "Exception in associateCollectionForResource", exp);
        }
    }
    
    public List disAssociateCollectionForResource(final Properties properties) {
        final List resourceList = ((Hashtable<K, List>)properties).get("resourceList");
        final Boolean isAppConfig = ((Hashtable<K, Boolean>)properties).get("isAppConfig");
        final long customerId = ((Hashtable<K, Long>)properties).get("customerId");
        final Long deploymentSourceID = ((Hashtable<K, Long>)properties).get("deploymentSource");
        String sLoggedOnUserName = ((Hashtable<K, String>)properties).get("loggedOnUserName");
        final HashMap profileCollectionMap = ((Hashtable<K, HashMap>)properties).get("profileCollectionMap");
        final HashMap profileProperties = ((Hashtable<K, HashMap>)properties).get("profileProperties");
        try {
            ProfileAssociateHandler.profileDistributionLog.log(Level.INFO, "disassociateCollection:resourceList {0}", resourceList.toString());
            ProfileAssociateHandler.profileDistributionLog.log(Level.INFO, "disassociateCollection:profileId {0}", profileCollectionMap.toString());
            final ProfileAssociateDataHandler handler = new ProfileAssociateDataHandler();
            handler.disassociateProfileForDevice(properties);
            final Boolean isGroupListener = (properties.get("isGroupListener") == null) ? false : ((Hashtable<K, Boolean>)properties).get("isGroupListener");
            final Integer profileOrigin = ((Hashtable<K, Integer>)properties).get("profileOriginInt");
            final Set profileSet = profileCollectionMap.keySet();
            if (isAppConfig && !isGroupListener && profileOrigin != null && profileOrigin == 120) {
                final List profileList = new ArrayList(profileSet);
                if (deploymentSourceID != null) {
                    new MDMResourceToProfileDeploymentConfigHandler().deleteMDMResourceToDeploymentConfig(deploymentSourceID, resourceList, profileList);
                }
                else {
                    new MDMResourceToProfileDeploymentConfigHandler().deleteMDMResourceToDeploymentConfig(resourceList, profileList);
                }
            }
            for (final Object profileID : profileSet) {
                final Long collectionID = profileCollectionMap.get(profileID);
                if (sLoggedOnUserName == null) {
                    final HashMap hash = MDMUtil.getInstance().getCurrentlyLoggenOnUserInfo();
                    sLoggedOnUserName = hash.get("UserName");
                }
                String associatedUsername = sLoggedOnUserName;
                if (profileProperties != null) {
                    final HashMap props = profileProperties.get(profileID);
                    if (props != null) {
                        final String profileAssociatedUser = props.get("associatedByUserName");
                        if (profileAssociatedUser != null) {
                            associatedUsername = profileAssociatedUser;
                        }
                    }
                }
                final String commandName = properties.getProperty("commandName", null);
                if (commandName == null || (commandName != null && !commandName.equals("RemoveBlacklistAppInDevice") && !commandName.equals("RemoveAppUpdatePolicy"))) {
                    if (isAppConfig) {
                        final String sEventLogRemarksKey = "dc.mdm.actionlog.appmgmt.removal_device_success";
                        this.addProfileActionEventLogEntry(customerId, resourceList, (long)profileID, sEventLogRemarksKey, 2033, associatedUsername, "Resource", new Long(System.currentTimeMillis()));
                    }
                    else {
                        final String sEventLogRemarksKey = "dc.mdm.actionlog.profilemgmt.dis_association_device_success";
                        this.addProfileActionEventLogEntry(customerId, resourceList, (long)profileID, sEventLogRemarksKey, 2021, associatedUsername, "Resource", new Long(System.currentTimeMillis()));
                        new ProfileFacade().setUpdateCountMessageStatus(customerId);
                    }
                }
                ProfileAssociateHandler.profileLogger.log(Level.INFO, "{0}\t\t{1}\t\t{2}\t\t{3}\t\tResourceID: {4}", new Object[] { profileID, collectionID, isAppConfig ? "App" : "Profile", "PROFILE_DIS_ASSOCIATION", resourceList });
            }
        }
        catch (final Exception exp) {
            ProfileAssociateHandler.profileDistributionLog.log(Level.SEVERE, "Exception in disAssociateCollectionForResource", exp);
        }
        return resourceList;
    }
    
    public List disAssociateCollectionForGroup(final Properties properties) {
        final List resourceList = new ArrayList();
        final List groupList = ((Hashtable<K, List>)properties).get("resourceList");
        final Boolean isAppConfig = ((Hashtable<K, Boolean>)properties).get("isAppConfig");
        final long customerId = ((Hashtable<K, Long>)properties).get("customerId");
        final Integer groupType = ((Hashtable<K, Integer>)properties).get("groupType");
        String sLoggedOnUserName = ((Hashtable<K, String>)properties).get("loggedOnUserName");
        final HashMap profileCollectionMap = ((Hashtable<K, HashMap>)properties).get("profileCollectionMap");
        final Set profileSet = profileCollectionMap.keySet();
        final List profileList = new ArrayList(profileSet);
        try {
            ProfileAssociateHandler.logger.log(Level.INFO, "disassociateCollection:groupId {0}", groupList.toString());
            ProfileAssociateHandler.logger.log(Level.INFO, "disassociateCollection:profileId {0}", profileCollectionMap.toString());
            final ProfileAssociateDataHandler handler = new ProfileAssociateDataHandler();
            handler.disassociateProfileForGroup(properties);
            if (isAppConfig) {
                new MDMResourceToProfileDeploymentConfigHandler().deleteMDMResourceToDeploymentConfig(groupList, profileList);
            }
            if (groupType != null && groupType != 7) {
                for (final Object profileID : profileSet) {
                    final Long collectionID = profileCollectionMap.get(profileID);
                    if (sLoggedOnUserName == null) {
                        final HashMap hash = MDMUtil.getInstance().getCurrentlyLoggenOnUserInfo();
                        sLoggedOnUserName = hash.get("UserName");
                    }
                    final String commandName = properties.getProperty("commandName", null);
                    if (commandName == null || (commandName != null && !commandName.equals("RemoveBlacklistAppInDevice") && !commandName.equalsIgnoreCase("RemoveAppUpdatePolicy"))) {
                        if (isAppConfig) {
                            final String sEventLogRemarksKey = "dc.mdm.actionlog.appmgmt.removal_groups_success";
                            this.addProfileActionEventLogEntry(customerId, groupList, (long)profileID, sEventLogRemarksKey, 2033, sLoggedOnUserName, "Group", new Long(System.currentTimeMillis()));
                        }
                        else {
                            final String sEventLogRemarksKey = "dc.mdm.actionlog.profilemgmt.dis_association_groups_success";
                            this.addProfileActionEventLogEntry(customerId, groupList, (long)profileID, sEventLogRemarksKey, 2021, sLoggedOnUserName, "Group", new Long(System.currentTimeMillis()));
                            new ProfileFacade().setUpdateCountMessageStatus(customerId);
                        }
                    }
                    ProfileAssociateHandler.profileLogger.log(Level.INFO, "{0}\t\t{1}\t\t{2}\t\t{3}\t\tGroupID: {4}; Group memeber resourceID: {5}", new Object[] { profileID, collectionID, isAppConfig ? "App" : "Profile", "PROFILE_ASSOCIATION", groupList, resourceList });
                }
            }
        }
        catch (final Exception exp) {
            ProfileAssociateHandler.logger.log(Level.SEVERE, "Exception in disAssociateCollectionForGroup", exp);
        }
        return resourceList;
    }
    
    private String getAuditLogQueueDataFileName(final Long customerID, final Boolean isAppConfig) {
        String fileName = customerID + "_" + MDMUtil.getCurrentTimeInMillis();
        fileName = (isAppConfig ? (fileName + "_apps_audit_qdata.txt") : "_profiles_audit_qdata.txt");
        return fileName;
    }
    
    public void deleteRecentProfileForResourceListCollection(final List resourceList, final Long collectionID) throws DataAccessException {
        final Criteria resourceCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceList.toArray(), 8, (boolean)Boolean.FALSE);
        final Criteria collectionCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "COLLECTION_ID"), (Object)collectionID, 0, (boolean)Boolean.FALSE);
        DataAccess.delete("RecentProfileForResource", collectionCriteria.and(resourceCriteria));
    }
    
    public void deleteRecentProfileForResource(final List resourceList, final Long profileId, final Long collectionId) throws DataAccessException {
        ProfileAssociateHandler.logger.log(Level.INFO, "Inside deleteRecentProfileForResource()");
        final Column resCol = Column.getColumn("RecentProfileForResource", "RESOURCE_ID");
        final Criteria resCri = new Criteria(resCol, (Object)resourceList.toArray(), 8);
        final Column profileCol = Column.getColumn("RecentProfileForResource", "PROFILE_ID");
        final Criteria profileCri = new Criteria(profileCol, (Object)profileId, 0);
        final Column collnCol = Column.getColumn("RecentProfileForResource", "COLLECTION_ID");
        final Criteria collCri = new Criteria(collnCol, (Object)collectionId, 0);
        final Criteria criteria1 = resCri.and(profileCri);
        final Criteria criteria2 = criteria1.and(collCri);
        DataAccess.delete("RecentProfileForResource", criteria2);
    }
    
    public Boolean isCollectionDeleteSafe(final Long resourceID, final Long collectionID) {
        Boolean isCollectionDeleteSafe = Boolean.TRUE;
        final Long profileID = new ProfileHandler().getProfileIDFromCollectionID(collectionID);
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForResource"));
            final Criteria resourceCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceID, 0);
            final Criteria profileCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "PROFILE_ID"), (Object)profileID, 0);
            selectQuery.setCriteria(resourceCriteria.and(profileCriteria));
            selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "PROFILE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "MARKED_FOR_DELETE"));
            final DataObject dao = DataAccess.get(selectQuery);
            if (!dao.isEmpty()) {
                final Row row = dao.getFirstRow("RecentProfileForResource");
                isCollectionDeleteSafe = (Boolean)row.get("MARKED_FOR_DELETE");
            }
        }
        catch (final Exception ex) {
            ProfileAssociateHandler.logger.log(Level.WARNING, "Exception in isCollectionDeleteSafe..", ex);
        }
        return isCollectionDeleteSafe;
    }
    
    public void deleteRecentProfileForResource(final Long resourceID) throws DataAccessException {
        ProfileAssociateHandler.logger.log(Level.INFO, "Inside deleteRecentProfileForResource()");
        final Column resCol = Column.getColumn("RecentProfileForResource", "RESOURCE_ID");
        final Criteria resCri = new Criteria(resCol, (Object)resourceID, 0);
        DataAccess.delete("RecentProfileForResource", resCri);
    }
    
    public void deleteRecentProfileForResource(final Long resourceID, final Long collectionId) throws DataAccessException {
        ProfileAssociateHandler.logger.log(Level.INFO, "Inside deleteRecentProfileForResource()");
        final Column resCol = Column.getColumn("RecentProfileForResource", "RESOURCE_ID");
        final Criteria resCri = new Criteria(resCol, (Object)resourceID, 0);
        final Column collnCol = Column.getColumn("RecentProfileForResource", "COLLECTION_ID");
        final Criteria collCri = new Criteria(collnCol, (Object)collectionId, 0);
        final Criteria criteria1 = resCri.and(collCri);
        DataAccess.delete("RecentProfileForResource", criteria1);
    }
    
    public void deleteRecentProfileForGroup(final List resourceList, final Long profileId, final Long collectionId) throws DataAccessException {
        ProfileAssociateHandler.logger.log(Level.INFO, "Inside deleteRecentProfileForResourceEntry()");
        final Column resCol = Column.getColumn("RecentProfileForGroup", "GROUP_ID");
        final Criteria resCri = new Criteria(resCol, (Object)resourceList.toArray(), 8);
        final Column profileCol = Column.getColumn("RecentProfileForGroup", "PROFILE_ID");
        final Criteria profileCri = new Criteria(profileCol, (Object)profileId, 0);
        final Column collnCol = Column.getColumn("RecentProfileForGroup", "COLLECTION_ID");
        final Criteria collCri = new Criteria(collnCol, (Object)collectionId, 0);
        final Criteria criteria1 = resCri.and(profileCri);
        final Criteria criteria2 = criteria1.and(collCri);
        DataAccess.delete("RecentProfileForGroup", criteria2);
    }
    
    public void deleteResourceToProfileHistory(final Long resourceID) throws DataAccessException {
        ProfileAssociateHandler.logger.log(Level.INFO, "Inside deleteResourceToProfileHistory {0}", resourceID);
        final Column resCol = Column.getColumn("ResourceToProfileHistory", "RESOURCE_ID");
        final Criteria resCri = new Criteria(resCol, (Object)resourceID, 0);
        DataAccess.delete("ResourceToProfileHistory", resCri);
    }
    
    public static List getMemberGroupsId(final List groupList) throws SyMException {
        final List resIdList = new ArrayList();
        try {
            final Criteria criteria = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupList.toArray(), 8);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
            selectQuery.setCriteria(criteria);
            selectQuery.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"));
            final DataObject resultDO = MDMUtil.getPersistence().get(selectQuery);
            Long memberResourceID = null;
            if (!resultDO.isEmpty()) {
                final Iterator memberRows = resultDO.getRows("CustomGroupMemberRel");
                while (memberRows.hasNext()) {
                    final Row memRow = memberRows.next();
                    memberResourceID = (Long)memRow.get("MEMBER_RESOURCE_ID");
                    if (!resIdList.contains(memberResourceID)) {
                        resIdList.add(memberResourceID);
                    }
                }
            }
        }
        catch (final Exception ex) {
            ProfileAssociateHandler.logger.log(Level.SEVERE, "Exception in getMemberGroupsId", ex);
        }
        return resIdList;
    }
    
    public void updateRecentProfileForResourceForDelete(final List resourceList, final long collectionId) {
        try {
            final UpdateQuery uQuery = (UpdateQuery)new UpdateQueryImpl("RecentProfileForResource");
            final Criteria cResource = new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            final Criteria cCollection = new Criteria(new Column("RecentProfileForResource", "COLLECTION_ID"), (Object)collectionId, 8);
            uQuery.setCriteria(cResource.and(cCollection));
            uQuery.setUpdateColumn("MARKED_FOR_DELETE", (Object)true);
            MDMUtil.getPersistence().update(uQuery);
        }
        catch (final Exception e) {
            ProfileAssociateHandler.logger.log(Level.SEVERE, "Exception in updateRecentProfileForResourceForDelete", e);
        }
    }
    
    public void removeAppsForResource(final long resourceID) {
        try {
            ProfileAssociateHandler.logger.log(Level.INFO, "removeAppsForResource :resId {0}", resourceID);
            String commandUUID = null;
            final List collectionIdList = this.getAssociatedAppCollectionIdList(resourceID);
            for (int listSize = collectionIdList.size(), s = 0; s < listSize; ++s) {
                final long collectionId = collectionIdList.get(s);
                commandUUID = "RemoveApplication;Collection=" + Long.toString(collectionId);
                DeviceCommandRepository.getInstance().assignCommandToDevice(commandUUID, resourceID);
            }
        }
        catch (final Exception ex) {
            ProfileAssociateHandler.logger.log(Level.WARNING, "Exception in removeAppsForResource {0}", ex);
        }
    }
    
    private List getAssociatedAppCollectionIdList(final long resourceID) {
        final List collectionIdList = new ArrayList();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForResource"));
            final Join profileJoin = new Join("RecentProfileForResource", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
            selectQuery.addJoin(profileJoin);
            final Criteria resCri = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceID, 0);
            final Criteria AppTypeCri = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)2, 0);
            selectQuery.setCriteria(resCri.and(AppTypeCri));
            selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "*"));
            final DataObject dataObj = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObj.isEmpty()) {
                final Iterator collItr = dataObj.getRows("RecentProfileForResource");
                while (collItr.hasNext()) {
                    final Row collIdRow = collItr.next();
                    collectionIdList.add(collIdRow.get("COLLECTION_ID"));
                }
            }
        }
        catch (final Exception ex) {
            ProfileAssociateHandler.logger.log(Level.WARNING, "Exception in getAssociatedAppCollectionIdList {0}", ex);
        }
        return collectionIdList;
    }
    
    public void addProfileActionEventLogEntry(final long customerId, final List resourceOrGroupList, final long profileID, final String sEventLogRemarksKey, final int eventConstant, final String sUserName, final String inputType, final Long eventTimeStamp) {
        this.addProfileActionEventLogEntry(customerId, resourceOrGroupList, profileID, sEventLogRemarksKey, eventConstant, sUserName, inputType, eventTimeStamp, null);
    }
    
    public void addProfileActionEventLogEntry(final long customerId, final List resourceOrGroupList, final long profileID, final String sEventLogRemarksKey, final int eventConstant, final String sUserName, final String inputType, final Long eventTimeStamp, final Map<String, String> customArgs) {
        try {
            final long startime = System.currentTimeMillis();
            final HashMap profileHash = MDMUtil.getInstance().getProfileDetails(profileID);
            String profileName = profileHash.get("PROFILE_NAME");
            if (customArgs != null) {
                if (customArgs.containsKey("appVersion")) {
                    profileName = profileName + "@@@" + customArgs.get("appVersion");
                }
                if (customArgs.containsKey("releaseLabelName")) {
                    profileName = profileName + "@@@" + customArgs.get("releaseLabelName");
                }
                if (customArgs.containsKey("businessstore_name")) {
                    profileName = profileName + "@@@" + customArgs.get("businessstore_name");
                }
            }
            final List<Object> remarksArgsList = new ArrayList<Object>();
            HashMap resourseVsName;
            if (inputType.equalsIgnoreCase("Resource")) {
                resourseVsName = ManagedDeviceHandler.getInstance().getDeviceNames(resourceOrGroupList);
            }
            else {
                resourseVsName = MDMResourceDataProvider.getResourceNames(resourceOrGroupList);
            }
            for (Object remarksArgs : resourseVsName.keySet()) {
                remarksArgs = profileName + "@@@" + resourseVsName.get(remarksArgs);
                remarksArgsList.add(remarksArgs);
            }
            MDMEventLogHandler.getInstance().addEvent(eventConstant, sUserName, sEventLogRemarksKey, remarksArgsList, customerId, eventTimeStamp);
            ProfileAssociateHandler.logger.log(Level.INFO, "Time Taken For Log Entry - {0}, No.Of Rows - {1}", new Object[] { System.currentTimeMillis() - startime, resourceOrGroupList.size() });
        }
        catch (final Exception ex) {
            ProfileAssociateHandler.logger.log(Level.SEVERE, "Exception in addProfileActionEventLogEntry", ex);
        }
    }
    
    public org.json.JSONObject getAssociatedProfils(final Long customerId, final int profType, final int platformType, final String profileFor) {
        final String treeID = "id";
        final String treeItem = "item";
        final String treeText = "text";
        final String treeUserData = "userdata";
        final String treeUserDataName = "name";
        final String treeUserDataContent = "content";
        final org.json.JSONObject mdmProfileTree = new org.json.JSONObject();
        final JSONArray mdmProfileItem = new JSONArray();
        final org.json.JSONObject androidProfiles = new org.json.JSONObject();
        final org.json.JSONObject knoxProfiles = new org.json.JSONObject();
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection conn = null;
        DataSet ds = null;
        SelectQuery profileQuery = null;
        final ProfileUtil profileUtil = new ProfileUtil();
        if (profileFor.equals("Group")) {
            profileQuery = profileUtil.getQueryforProfileCollnGroup(null, customerId);
        }
        else {
            profileQuery = profileUtil.getQueryforProfileCollnDevice(null, customerId);
        }
        final Criteria profileTypeCri = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)profType, 0);
        final Criteria platformTypeCri = new Criteria(Column.getColumn("Profile", "PLATFORM_TYPE"), (Object)platformType, 0);
        final Criteria DeleteCri = new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)false, 0);
        final Criteria cri = profileQuery.getCriteria().and(profileTypeCri).and(platformTypeCri).and(DeleteCri);
        profileQuery.setCriteria(cri);
        try {
            final JSONArray androidProfileItem = new JSONArray();
            final JSONArray knoxProfileItem = new JSONArray();
            conn = relapi.getConnection();
            ds = relapi.executeQuery((Query)profileQuery, conn);
            while (ds.next()) {
                final Long profileId = (Long)ds.getValue("PROFILE_ID");
                final String profileName = (String)ds.getValue("PROFILE_NAME");
                final int profileType = (int)ds.getValue("PROFILE_TYPE");
                final int latestVer = (int)ds.getValue("PROFILE_VERSION");
                final Long profileCollnId = (Long)ds.getValue("COLLECTION_ID");
                final int scope = (int)ds.getValue("SCOPE");
                final org.json.JSONObject userData = new org.json.JSONObject();
                userData.put("profileType", profileType);
                userData.put("profileCollnId", (Object)profileCollnId);
                userData.put("latestVer", latestVer);
                final org.json.JSONObject userDataContainer = new org.json.JSONObject();
                userDataContainer.put(treeUserDataName, (Object)(profileId + ""));
                userDataContainer.put(treeUserDataContent, (Object)userData);
                final JSONArray userDataAr = new JSONArray();
                userDataAr.put((Object)userDataContainer);
                final org.json.JSONObject node = new org.json.JSONObject();
                node.put(treeID, (Object)(profileId + ""));
                node.put(treeText, (Object)SyMUtil.getInstance().decodeURIComponentEquivalent(profileName + "(" + latestVer + ")"));
                node.put(treeUserData, (Object)userDataAr);
                node.put("style", (Object)"font-family: 'Lato', 'Roboto', sans-serif;font-size: 10px; text-decoration:");
                node.put("imheight", 1);
                node.put("imwidth", 1);
                if (scope == 1) {
                    knoxProfileItem.put((Object)node);
                    ApiFactoryProvider.getCacheAccessAPI().putCache("IS_KNOX_PROFILE_AVAILABLE", (Object)true, 2);
                }
                else {
                    androidProfileItem.put((Object)node);
                    ApiFactoryProvider.getCacheAccessAPI().putCache("IS_ANDROID_PROFILE_AVAILABLE", (Object)true, 2);
                }
            }
            androidProfiles.put(treeID, (Object)"Android");
            androidProfiles.put(treeText, (Object)"Android Profiles");
            androidProfiles.put(treeItem, (Object)androidProfileItem);
            androidProfiles.put("im0", (Object)"../android-profile.png");
            androidProfiles.put("im1", (Object)"../android-profile.png");
            androidProfiles.put("im2", (Object)"../android-profile.png");
            androidProfiles.put("style", (Object)"font-family: 'Lato', 'Roboto', sans-serif;font-size: 12px; font-weight:bold; text-decoration:");
            knoxProfiles.put(treeID, (Object)"Knox");
            knoxProfiles.put(treeText, (Object)"Knox Profiles");
            knoxProfiles.put(treeItem, (Object)knoxProfileItem);
            knoxProfiles.put("im0", (Object)"../knox-profile.png");
            knoxProfiles.put("im1", (Object)"../knox-profile.png");
            knoxProfiles.put("im2", (Object)"../knox-profile.png");
            knoxProfiles.put("style", (Object)"font-family: 'Lato', 'Roboto', sans-serif;font-size: 12px; font-weight:bold; text-decoration:");
            mdmProfileItem.put((Object)androidProfiles);
            mdmProfileItem.put((Object)knoxProfiles);
            mdmProfileTree.put(treeID, 0);
            mdmProfileTree.put(treeItem, (Object)mdmProfileItem);
        }
        catch (final Exception ex) {
            ProfileAssociateHandler.logger.log(Level.WARNING, "Exception occoured in getAssociatedProfilForGroup....", ex);
        }
        finally {
            CustomGroupUtil.getInstance().closeConnection(conn, ds);
        }
        return mdmProfileTree;
    }
    
    public boolean isAndroidProfileAvailable() {
        boolean isAndroidProfilesAvailable = false;
        if (ApiFactoryProvider.getCacheAccessAPI().getCache("IS_ANDROID_PROFILE_AVAILABLE", 2) != null) {
            isAndroidProfilesAvailable = (boolean)ApiFactoryProvider.getCacheAccessAPI().getCache("IS_ANDROID_PROFILE_AVAILABLE", 2);
        }
        return isAndroidProfilesAvailable;
    }
    
    public boolean isKnoxProfileAvailable() {
        boolean isKnoxProfilesAvailable = false;
        if (ApiFactoryProvider.getCacheAccessAPI().getCache("IS_KNOX_PROFILE_AVAILABLE", 2) != null) {
            isKnoxProfilesAvailable = (boolean)ApiFactoryProvider.getCacheAccessAPI().getCache("IS_KNOX_PROFILE_AVAILABLE", 2);
        }
        return isKnoxProfilesAvailable;
    }
    
    public void removeEarlierVersionProfileCommand(final List resourceList, final Map<Long, Long> associatedProfileCollnMap) {
        ProfileAssociateHandler.logger.log(Level.INFO, "** Remove earlier version commands initiated. ResourceList : {0}, ProfileCollectionList : {1}", new Object[] { resourceList, associatedProfileCollnMap });
        final Set<Long> profileIDSet = associatedProfileCollnMap.keySet();
        final Map<Long, List<Long>> profileCollnMap = ProfileHandler.getProfileCollections(profileIDSet);
        final List<List<Long>> collectionList = new ArrayList<List<Long>>(profileCollnMap.values());
        final List<Long> collnMergeList = new ArrayList<Long>();
        for (final List<Long> collections : collectionList) {
            collnMergeList.addAll(collections);
        }
        collnMergeList.removeAll(new ArrayList<Object>(associatedProfileCollnMap.values()));
        if (!collnMergeList.isEmpty()) {
            final DeviceCommandRepository deviceCommandRepository = DeviceCommandRepository.getInstance();
            final List<Long> commandIDList = deviceCommandRepository.getCollectionIdsCommandList(collnMergeList, "InstallProfile");
            deviceCommandRepository.clearDeviceCommand(resourceList, commandIDList);
            ProfileAssociateHandler.logger.log(Level.INFO, "** Removed earlier verion of profile command");
        }
    }
    
    public void removeCollectionCommandsForSameProfile(final Map<Long, List> collectionToApplicableRes, final Map<Long, Long> associatedProfileCollnMap, final boolean ignoreLatestCollectionComamnd, final String commandType) {
        final Set<Long> profileIDSet = associatedProfileCollnMap.keySet();
        for (final Long profileID : profileIDSet) {
            final HashMap<Long, Long> profileMap = new HashMap<Long, Long>();
            final Long collectionId = associatedProfileCollnMap.get(profileID);
            profileMap.put(profileID, collectionId);
            final List<Long> resourceList = new ArrayList<Long>(collectionToApplicableRes.get(collectionId));
            this.removeCollectionCommandsForSameProfile(resourceList, profileMap, ignoreLatestCollectionComamnd, commandType);
        }
    }
    
    public void removeCollectionCommandsForSameProfile(final List resourceList, final Map<Long, Long> associatedProfileCollnMap, final boolean ignoreLatestCollectionComamnd, final String commandType) {
        ProfileAssociateHandler.logger.log(Level.INFO, "** Remove collection commands initiated. ResourceList : {0}, ProfileCollectionList : {1}", new Object[] { resourceList, associatedProfileCollnMap });
        final Set<Long> profileIDSet = associatedProfileCollnMap.keySet();
        final Map<Long, List<Long>> profileCollnMap = ProfileHandler.getProfileCollections(profileIDSet);
        final List<List<Long>> collectionList = new ArrayList<List<Long>>(profileCollnMap.values());
        final List<Long> collnMergeList = new ArrayList<Long>();
        for (final List<Long> collections : collectionList) {
            collnMergeList.addAll(collections);
        }
        if (ignoreLatestCollectionComamnd) {
            collnMergeList.removeAll(new ArrayList<Object>(associatedProfileCollnMap.values()));
        }
        if (!collnMergeList.isEmpty()) {
            final DeviceCommandRepository deviceCommandRepository = DeviceCommandRepository.getInstance();
            final List<Long> commandIDList = deviceCommandRepository.getCollectionIdsCommandList(collnMergeList, commandType);
            deviceCommandRepository.clearDeviceCommand(resourceList, commandIDList);
            ProfileAssociateHandler.logger.log(Level.INFO, "** Removed collection of profile command");
        }
    }
    
    public void removeInstallProfileCommand(final List resourceList, final Map<Long, Long> associatedProfileCollnMap) {
        ProfileAssociateHandler.logger.log(Level.INFO, "** Remove earlier version commands initiated. ResourceList : {0}, ProfileCollectionList : {1}", new Object[] { resourceList, associatedProfileCollnMap });
        final Set<Long> profileIDSet = associatedProfileCollnMap.keySet();
        final Map<Long, List<Long>> profileCollnMap = ProfileHandler.getProfileCollections(profileIDSet);
        final List<List<Long>> collectionList = new ArrayList<List<Long>>(profileCollnMap.values());
        final List<Long> collnMergeList = new ArrayList<Long>();
        for (final List<Long> collections : collectionList) {
            collnMergeList.addAll(collections);
        }
        if (!collnMergeList.isEmpty()) {
            final DeviceCommandRepository deviceCommandRepository = DeviceCommandRepository.getInstance();
            final List<Long> commandIDList = deviceCommandRepository.getCollectionIdsCommandList(collnMergeList, "InstallProfile");
            deviceCommandRepository.clearDeviceCommand(resourceList, commandIDList);
            ProfileAssociateHandler.logger.log(Level.INFO, "** Removed earlier verion of profile command");
        }
    }
    
    public String getInstallAppFromCatalogRemark(final Integer packageType, final int platformType, final Boolean isAppUpgrade) {
        String remarks;
        if (platformType == 2) {
            remarks = "mdm.android.app_installation_by_user";
            if (isAppUpgrade != null && isAppUpgrade) {
                remarks = "mdm.android.app_update_by_user";
            }
        }
        else {
            remarks = "dc.mdm.android.app_installation_from_appcatalog";
            if (isAppUpgrade != null && isAppUpgrade) {
                remarks = "mdm.appmgmt.app_update_from_appcatalog";
            }
        }
        final String helpUrl = AppsUtil.getInstance().getSilentInstallAppHelpUrl(packageType, platformType);
        if (!helpUrl.equals("")) {
            remarks = "dc.mdm.app_installation_from_appcatalog@@@<l>" + helpUrl;
            if (isAppUpgrade != null && isAppUpgrade) {
                remarks = "dc.mdm.app_update_from_appcatalog_with_help@@@<l>" + helpUrl;
            }
        }
        return remarks;
    }
    
    public Long getAssociatedByUser(final Long resID, final Long profileID, final Long collectionID) throws Exception {
        Long userID = null;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ResourceToProfileHistory"));
        selectQuery.addSelectColumn(new Column("ResourceToProfileHistory", "RESOURCE_HISTORY_ID"));
        selectQuery.addSelectColumn(new Column("ResourceToProfileHistory", "ASSOCIATED_BY"));
        final Criteria resCriteria = new Criteria(Column.getColumn("ResourceToProfileHistory", "RESOURCE_ID"), (Object)resID, 0);
        final Criteria collectionCriteria = new Criteria(Column.getColumn("ResourceToProfileHistory", "COLLECTION_ID"), (Object)collectionID, 0);
        selectQuery.setCriteria(resCriteria.and(collectionCriteria));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("ResourceToProfileHistory");
            userID = (Long)row.get("ASSOCIATED_BY");
        }
        return userID;
    }
    
    public boolean isIPCommandAvailableForResource(final Long collectionId, final Long resourceId) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ProfileToCollection"));
            selectQuery.addJoin(new Join("ProfileToCollection", "RecentProfileForResource", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            selectQuery.addJoin(new Join("RecentProfileForResource", "CollnToResources", new String[] { "COLLECTION_ID", "RESOURCE_ID" }, new String[] { "COLLECTION_ID", "RESOURCE_ID" }, 2));
            final Criteria profileCriteria = new Criteria(new Column("ProfileToCollection", "COLLECTION_ID"), (Object)collectionId, 0);
            final Criteria resourceCriteria = new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceId, 0);
            final Criteria notDeleted = new Criteria(new Column("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
            final Criteria notSuccess = new Criteria(new Column("CollnToResources", "STATUS"), (Object)6, 1);
            selectQuery.setCriteria(profileCriteria.and(resourceCriteria).and(notDeleted).and(notSuccess));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                return true;
            }
        }
        catch (final DataAccessException e) {
            ProfileAssociateHandler.logger.log(Level.SEVERE, "Exception in checking the install profile command", (Throwable)e);
        }
        return false;
    }
    
    public Integer getCollectionStatusForResource(final Long resourceID, final Long collectionID) {
        Integer status = 0;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("CollnToResources"));
            selectQuery.addSelectColumn(Column.getColumn("CollnToResources", "*"));
            final Criteria resCriteria = new Criteria(Column.getColumn("CollnToResources", "RESOURCE_ID"), (Object)resourceID, 0);
            final Criteria collnCriteria = new Criteria(Column.getColumn("CollnToResources", "COLLECTION_ID"), (Object)collectionID, 0);
            selectQuery.setCriteria(resCriteria.and(collnCriteria));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            final Row row = dataObject.getFirstRow("CollnToResources");
            status = (Integer)row.get("STATUS");
        }
        catch (final DataAccessException e) {
            ProfileAssociateHandler.logger.log(Level.WARNING, "Eror in getting status", (Throwable)e);
        }
        return status;
    }
    
    public void updateGroupProfileSummary() {
        try {
            ResourceSummaryHandler.getInstance().updateResSummary(101);
        }
        catch (final Exception ex) {
            ProfileAssociateHandler.logger.log(Level.SEVERE, null, ex);
        }
    }
    
    public Map getGroupSummary(final List groupList) {
        try {
            Criteria criteria = null;
            if (groupList != null) {
                criteria = new Criteria(Column.getColumn("CustomGroup", "RESOURCE_ID"), (Object)groupList.toArray(new Long[groupList.size()]), 8);
            }
            return ResourceSummaryHandler.getInstance().getResSummary(101, null, criteria);
        }
        catch (final Exception ex) {
            ProfileAssociateHandler.logger.log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public void updateDeviceProfileSummary() {
        try {
            ResourceSummaryHandler.getInstance().updateResSummary(120);
        }
        catch (final Exception ex) {
            ProfileAssociateHandler.logger.log(Level.SEVERE, null, ex);
        }
    }
    
    public void updateuserProfileSummary() {
        try {
            ResourceSummaryHandler.getInstance().updateResSummary(2);
        }
        catch (final Exception ex) {
            ProfileAssociateHandler.logger.log(Level.SEVERE, null, ex);
        }
    }
    
    public Map getDeviceSummary(final List deviceList) {
        try {
            Criteria criteria = null;
            if (deviceList != null) {
                criteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)deviceList.toArray(new Long[deviceList.size()]), 8);
            }
            return ResourceSummaryHandler.getInstance().getResSummary(120, null, criteria);
        }
        catch (final Exception ex) {
            ProfileAssociateHandler.logger.log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public boolean isProfileDeleteSafe(final List profileList) {
        boolean isProfileDeleteSafe = false;
        try {
            final SelectQuery profileSafeQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ProfileToCollection"));
            final Join profileResRelJoin = new Join("ProfileToCollection", "RecentProfileForResource", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
            final Join managedDeviceJoin = new Join("RecentProfileForResource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            final Join profileJoin = new Join("RecentProfileForResource", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
            profileSafeQuery.addJoin(profileResRelJoin);
            profileSafeQuery.addJoin(managedDeviceJoin);
            profileSafeQuery.addJoin(profileJoin);
            profileSafeQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "*"));
            final Criteria profileCriteria = new Criteria(new Column("ProfileToCollection", "PROFILE_ID"), (Object)profileList.toArray(), 8);
            final Criteria platformCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)Column.getColumn("Profile", "PLATFORM_TYPE"), 0);
            profileSafeQuery.setCriteria(profileCriteria.and(ManagedDeviceHandler.getInstance().getSuccessfullyEnrolledCriteria()).and(platformCriteria));
            final DataObject dObj = MDMUtil.getPersistence().get(profileSafeQuery);
            if (dObj.isEmpty()) {
                ProfileAssociateHandler.logger.log(Level.INFO, "Profile is delete safe.ProfileId:{0}", new Object[] { profileList });
                isProfileDeleteSafe = true;
            }
        }
        catch (final Exception ex) {
            ProfileAssociateHandler.logger.log(Level.WARNING, "Exception in isProfileofAppDeleteSafe...", ex);
        }
        return isProfileDeleteSafe;
    }
    
    public void distributeKioskApp(final Properties properties) {
        ProfileAssociateHandler.profileDistributionLog.log(Level.INFO, "Distribution of Kiosk Apps {0}", properties);
        try {
            ((Hashtable<String, Boolean>)properties).put("kioskProfileApplied", true);
            final Properties appProperties = (Properties)properties.clone();
            final long customerId = ((Hashtable<K, Long>)appProperties).get("customerId");
            final HashMap<Long, Long> profileCollectionMap = ((Hashtable<K, HashMap<Long, Long>>)appProperties).get("profileCollectionMap");
            final DataObject profileDO = ProfileUtil.getInstance().getProfileDO(new ArrayList(profileCollectionMap.keySet()));
            final List<Long> resourceList = ((Hashtable<K, List<Long>>)appProperties).get("resourceList");
            final Map<Long, List<Long>> resourceAppProfileMap = new HashMap<Long, List<Long>>();
            final boolean isGroup = appProperties.get("isGroup") != null && ((Hashtable<K, Boolean>)appProperties).get("isGroup");
            int configID = -1;
            for (final Long profileId : profileCollectionMap.keySet()) {
                final Long collectionId = profileCollectionMap.get(profileId);
                final List configIds = MDMConfigUtil.getConfigIds(collectionId);
                final int platformType = (int)profileDO.getValue("Profile", "PLATFORM_TYPE", new Criteria(new Column("Profile", "PROFILE_ID"), (Object)profileId, 0));
                org.json.JSONObject kioskAppDetails = new org.json.JSONObject();
                boolean autoAppDistribution = false;
                if (configIds.contains(183)) {
                    final IOSKioskProfileDataHandler iosKioskProfileDataHandler = new IOSKioskProfileDataHandler();
                    kioskAppDetails = iosKioskProfileDataHandler.profileDetailsForIOSKioskAutomation(collectionId, customerId);
                    final int kioskMode = kioskAppDetails.optInt("KIOSK_MODE");
                    if ((kioskAppDetails.optBoolean("AUTO_DISTRIBUTE_APPS", (boolean)Boolean.FALSE) && kioskMode == 2) || kioskMode == 1 || kioskMode == 3) {
                        autoAppDistribution = true;
                    }
                    configID = 183;
                }
                else if (configIds.contains(557)) {
                    kioskAppDetails = this.profileDetailsForAndroidKioskAutomation(collectionId);
                    if (kioskAppDetails.optBoolean("AUTO_DISTRIBUTE_APPS", (boolean)Boolean.TRUE)) {
                        autoAppDistribution = true;
                    }
                    configID = 557;
                }
                else if (configIds.contains(611)) {
                    kioskAppDetails = this.getApplicableAppsForWindows(collectionId);
                    if (kioskAppDetails.optBoolean("AUTO_DISTRIBUTE_APPS", (boolean)Boolean.TRUE)) {
                        autoAppDistribution = true;
                    }
                    configID = 611;
                }
                else if (configIds.contains(302) && platformType == 2) {
                    configID = 302;
                    autoAppDistribution = true;
                    kioskAppDetails = AppConfigPolicyDBHandler.getInstance().getApplicableAppDetails(collectionId);
                }
                else if (configIds.contains(521)) {
                    configID = 521;
                    autoAppDistribution = true;
                    kioskAppDetails = new IOSPerAppVPNHandler().getAppGroupIdForCollection(collectionId);
                }
                if (autoAppDistribution) {
                    final JSONArray appGroupIds = (kioskAppDetails.optJSONArray("APP_GROUP_ID") != null) ? kioskAppDetails.optJSONArray("APP_GROUP_ID") : new JSONArray();
                    final HashMap<Long, Long> appProfileCollectionMap = new HashMap<Long, Long>();
                    final List appGroupIdList = new ArrayList();
                    for (int initial = 0; initial < appGroupIds.length(); ++initial) {
                        appGroupIdList.add(appGroupIds.getLong(initial));
                    }
                    final DataObject dObj = AppsUtil.getInstance().getResourceDetailsForApp(appGroupIdList, resourceList, isGroup);
                    final Iterator<Row> profileToCollectionRows = dObj.getRows("ProfileToCollection");
                    while (profileToCollectionRows.hasNext()) {
                        final Row profileToCollRow = profileToCollectionRows.next();
                        if (profileToCollRow != null) {
                            final Long appProfileId = (Long)profileToCollRow.get("PROFILE_ID");
                            final Long collectionID = (Long)profileToCollRow.get("COLLECTION_ID");
                            appProfileCollectionMap.put(appProfileId, collectionID);
                            if (isGroup) {
                                final Criteria criteria = new Criteria(Column.getColumn("RecentProfileForGroup", "PROFILE_ID"), (Object)appProfileId, 0);
                                final Iterator<Row> profileRows = dObj.getRows("RecentProfileForGroup", criteria);
                                while (profileRows.hasNext()) {
                                    final Row recentProfileForGroup = profileRows.next();
                                    final Long groupId = (Long)recentProfileForGroup.get("GROUP_ID");
                                    if (!resourceAppProfileMap.containsKey(groupId)) {
                                        resourceAppProfileMap.put(groupId, new ArrayList<Long>());
                                    }
                                    resourceAppProfileMap.get(groupId).add(appProfileId);
                                }
                            }
                            else {
                                final Criteria criteria = new Criteria(Column.getColumn("RecentProfileForResource", "PROFILE_ID"), (Object)appProfileId, 0);
                                final Iterator<Row> profileRows = dObj.getRows("RecentProfileForResource", criteria);
                                while (profileRows.hasNext()) {
                                    final Row recentProfileForGroup = profileRows.next();
                                    final Long resourceId = (Long)recentProfileForGroup.get("RESOURCE_ID");
                                    if (!resourceAppProfileMap.containsKey(resourceId)) {
                                        resourceAppProfileMap.put(resourceId, new ArrayList<Long>());
                                    }
                                    resourceAppProfileMap.get(resourceId).add(appProfileId);
                                }
                            }
                        }
                    }
                    for (final Long resourceId2 : resourceList) {
                        final Map appProfileCollectionMapValue = new HashMap(appProfileCollectionMap);
                        final Properties kioskAppProperties = (Properties)appProperties.clone();
                        if (resourceAppProfileMap.containsKey(resourceId2)) {
                            final List<Long> appList = resourceAppProfileMap.get(resourceId2);
                            for (final Long appId : appList) {
                                if (appProfileCollectionMapValue.containsKey(appId)) {
                                    appProfileCollectionMapValue.remove(appId);
                                }
                            }
                        }
                        if (!appProfileCollectionMapValue.isEmpty()) {
                            ProfileAssociateHandler.profileDistributionLog.log(Level.INFO, "Resource Id : {0} Kiosk Apps {1}", new Object[] { resourceId2, appProfileCollectionMap });
                            ((Hashtable<String, Map>)kioskAppProperties).put("profileCollectionMap", appProfileCollectionMapValue);
                            ((Hashtable<String, Integer>)kioskAppProperties).put("groupType", MDMGroupHandler.getInstance().getGroupType(resourceId2));
                            ((Hashtable<String, List<Long>>)kioskAppProperties).put("resourceList", Arrays.asList(resourceId2));
                            ((Hashtable<String, Boolean>)kioskAppProperties).put("isAppConfig", true);
                            ((Hashtable<String, Boolean>)kioskAppProperties).put("isSilentInstall", true);
                            ((Hashtable<String, Boolean>)kioskAppProperties).put("isNotify", false);
                            ((Hashtable<String, String>)kioskAppProperties).put("commandName", "InstallApplication");
                            final List profileList = new ArrayList(appProfileCollectionMap.keySet());
                            final List tempResList = new ArrayList();
                            tempResList.add(resourceId2);
                            Properties profileToBusinessStore = new Properties();
                            profileToBusinessStore = this.getPreferredProfileToBusinessStoreMap(profileToBusinessStore, platformType, profileList, tempResList);
                            if (!profileToBusinessStore.isEmpty()) {
                                ((Hashtable<String, Properties>)kioskAppProperties).put("profileToBusinessStore", profileToBusinessStore);
                            }
                            new DeplymentConfigHandler().updateDeploymentSettingsForApp(kioskAppProperties);
                            if (isGroup) {
                                final ProfileAssociateDataHandler handler = new ProfileAssociateDataHandler();
                                handler.associateProfileForGroup(kioskAppProperties);
                            }
                            else {
                                final ProfileAssociateDataHandler handler = new ProfileAssociateDataHandler();
                                handler.associateProfileForDevice(kioskAppProperties);
                            }
                            this.addProfileAppActionEventLogEntry(kioskAppProperties, resourceId2, profileId, configID);
                        }
                    }
                }
            }
        }
        catch (final Exception ex) {
            ProfileAssociateHandler.logger.log(Level.SEVERE, "Exception occurred while distributing the kiosk apps to the resources", ex);
        }
    }
    
    private String getEventLogRemarksForProfileDependentAppDistribution(final int configID, final boolean isGroup) {
        String remarks = "";
        switch (configID) {
            case 183:
            case 557:
            case 611: {
                remarks = (isGroup ? "mdm.actionlog.appmgmt.group_kioskapp_distribution" : "mdm.actionlog.appmgmt.device_kioskapp_distribution");
                break;
            }
            case 302: {
                remarks = (isGroup ? "mdm.actionlog.group_appconfig_distribution" : "mdm.actionlog.device_appconfig_distribution");
                break;
            }
            case 521: {
                remarks = (isGroup ? "mdm.actionlog.group_perappvpn_distribution" : "mdm.actionlog.device_perappvpn_distribution");
                break;
            }
        }
        return remarks;
    }
    
    private void addProfileAppActionEventLogEntry(final Properties properties, final Long resourceId, final Long profileId, final int configID) {
        try {
            final Map profileCollectionMap = ((Hashtable<K, Map>)properties).get("profileCollectionMap");
            final boolean isGroup = ((Hashtable<K, Boolean>)properties).get("isGroup");
            final long customerId = ((Hashtable<K, Long>)properties).get("customerId");
            final HashMap hash = MDMUtil.getInstance().getCurrentlyLoggenOnUserInfo();
            final String sEventLogRemarksKey = this.getEventLogRemarksForProfileDependentAppDistribution(configID, isGroup);
            final String sLoggedOnUserName = hash.get("UserName");
            final int eventConstant = 2033;
            final HashMap profileHash = MDMUtil.getInstance().getProfileDetails(profileId);
            final String profileName = profileHash.get("PROFILE_NAME");
            final String resourceOrGroupName = isGroup ? MDMResourceDataProvider.getResourceName(resourceId) : ManagedDeviceHandler.getInstance().getDeviceName(resourceId);
            for (final Object appProfileId : profileCollectionMap.keySet()) {
                final HashMap appProfileHash = MDMUtil.getInstance().getProfileDetails((Long)appProfileId);
                final String appProfileName = appProfileHash.get("PROFILE_NAME");
                Object remarksArgs;
                if (configID == 302) {
                    remarksArgs = appProfileName + "@@@" + profileName + "@@@" + resourceOrGroupName;
                }
                else {
                    remarksArgs = appProfileName + "@@@" + resourceOrGroupName + "@@@" + profileName;
                }
                MDMEventLogHandler.getInstance().MDMEventLogEntry(eventConstant, null, sLoggedOnUserName, sEventLogRemarksKey, remarksArgs, customerId);
            }
        }
        catch (final Exception ex) {
            ProfileAssociateHandler.logger.log(Level.SEVERE, "Exception in addProfileActionEventLogEntry", ex);
        }
    }
    
    public org.json.JSONObject profileDetailsForAndroidKioskAutomation(final Long collectionID) {
        final org.json.JSONObject applicableApps = new org.json.JSONObject();
        try {
            final DataObject kioskDO = this.getAndroidKioskProfileDetails(collectionID);
            if (!kioskDO.isEmpty()) {
                final Row kioskRow = kioskDO.getFirstRow("AndroidKioskPolicy");
                final List appGroupIDs = new ArrayList();
                if (kioskDO.containsTable("AndroidKioskPolicyApps")) {
                    final Iterator<Row> appRows = kioskDO.getRows("AndroidKioskPolicyApps");
                    while (appRows.hasNext()) {
                        final Row appRow = appRows.next();
                        appGroupIDs.add(appRow.get("APP_GROUP_ID"));
                    }
                }
                if (kioskDO.containsTable("AndroidKioskPolicyBackgroundApps")) {
                    final Iterator<Row> appRows = kioskDO.getRows("AndroidKioskPolicyBackgroundApps");
                    while (appRows.hasNext()) {
                        final Row appRow = appRows.next();
                        appGroupIDs.add(appRow.get("APP_GROUP_ID"));
                    }
                }
                if (!appGroupIDs.isEmpty()) {
                    applicableApps.put("APP_GROUP_ID", (Object)new JSONArray((Collection)appGroupIDs));
                }
                final Integer appType = (Integer)kioskRow.get("KIOSK_MODE");
                final Boolean autoDistributeApps = (Boolean)kioskRow.get("AUTO_DISTRIBUTE_APPS");
                applicableApps.put("KIOSK_MODE", (Object)appType);
                applicableApps.put("AUTO_DISTRIBUTE_APPS", (Object)autoDistributeApps);
            }
        }
        catch (final Exception e) {
            ProfileAssociateHandler.logger.log(Level.SEVERE, "Error while retriving Kiosk App", e);
        }
        return applicableApps;
    }
    
    private org.json.JSONObject getApplicableAppsForWindows(final Long collectionID) {
        final org.json.JSONObject applicableApps = new org.json.JSONObject();
        try {
            final SelectQuery kioskQuery = (SelectQuery)new SelectQueryImpl(new Table("Collection"));
            kioskQuery.addJoin(new Join("Collection", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            kioskQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            kioskQuery.addJoin(new Join("ConfigDataItem", "WindowsLockdownPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
            kioskQuery.addJoin(new Join("WindowsLockdownPolicy", "LockdownPolicy", new String[] { "POLICY_ID" }, new String[] { "POLICY_ID" }, 2));
            kioskQuery.addJoin(new Join("LockdownPolicy", "LockdownRules", new String[] { "POLICY_ID" }, new String[] { "POLICY_ID" }, 2));
            kioskQuery.addJoin(new Join("LockdownRules", "LockdownRuleToApp", new String[] { "RULE_ID" }, new String[] { "RULE_ID" }, 1));
            kioskQuery.addJoin(new Join("LockdownRuleToApp", "LockdownApplications", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 1));
            kioskQuery.addJoin(new Join("LockdownRules", "WindowsLockdownConfig", new String[] { "RULE_ID" }, new String[] { "RULE_ID" }, 1));
            final Criteria criteria = new Criteria(new Column("Collection", "COLLECTION_ID"), (Object)collectionID, 0);
            kioskQuery.setCriteria(criteria);
            kioskQuery.addSelectColumn(Column.getColumn("LockdownApplications", "*"));
            kioskQuery.addSelectColumn(Column.getColumn("WindowsLockdownConfig", "*"));
            DataObject dataObject = MDMUtil.getPersistenceLite().get(kioskQuery);
            final List<String> identifiers = new ArrayList<String>();
            Iterator iterator = dataObject.getRows("LockdownApplications");
            final Row windowsLockDownConfigRow = dataObject.getFirstRow("WindowsLockdownConfig");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final String identifier = (String)row.get("APP_IDENTIFIER");
                identifiers.add(identifier.split("!")[0]);
            }
            if (identifiers.size() > 0) {
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppGroupDetails"));
                selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "*"));
                selectQuery.setCriteria(new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)identifiers.toArray(), 8));
                dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
                final JSONArray jsonArray = new JSONArray();
                iterator = dataObject.getRows("MdAppGroupDetails");
                while (iterator.hasNext()) {
                    final Row row2 = iterator.next();
                    final Long appGroupID = (Long)row2.get("APP_GROUP_ID");
                    jsonArray.put((Object)appGroupID);
                }
                if (jsonArray.length() > 0) {
                    applicableApps.put("APP_GROUP_ID", (Object)jsonArray);
                }
            }
            final Boolean autoDistributeApps = (Boolean)windowsLockDownConfigRow.get("AUTO_DISTRIBUTE_APPS");
            applicableApps.put("AUTO_DISTRIBUTE_APPS", (Object)autoDistributeApps);
        }
        catch (final Exception e) {
            ProfileAssociateHandler.logger.log(Level.SEVERE, "Exception in getting windows kiosk apps", e);
        }
        return applicableApps;
    }
    
    private DataObject getAndroidKioskProfileDetails(final Long collectionID) throws DataAccessException {
        final SelectQuery kioskQuery = (SelectQuery)new SelectQueryImpl(new Table("Collection"));
        kioskQuery.addJoin(new Join("Collection", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        kioskQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        kioskQuery.addJoin(new Join("ConfigDataItem", "AndroidKioskPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        kioskQuery.addJoin(new Join("ConfigDataItem", "AndroidKioskPolicyApps", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
        final Criteria criteria = new Criteria(new Column("Collection", "COLLECTION_ID"), (Object)collectionID, 0);
        kioskQuery.setCriteria(criteria);
        kioskQuery.addSelectColumn(new Column("AndroidKioskPolicy", "CONFIG_DATA_ITEM_ID"));
        kioskQuery.addSelectColumn(new Column("AndroidKioskPolicy", "KIOSK_MODE"));
        kioskQuery.addSelectColumn(new Column("AndroidKioskPolicy", "AUTO_DISTRIBUTE_APPS"));
        kioskQuery.addSelectColumn(new Column("AndroidKioskPolicyApps", "CONFIG_DATA_ITEM_ID"));
        kioskQuery.addSelectColumn(new Column("AndroidKioskPolicyApps", "APP_GROUP_ID"));
        final DataObject kioskDO = MDMUtil.getPersistence().get(kioskQuery);
        final SelectQuery kioskHiddenAppsQuery = (SelectQuery)new SelectQueryImpl(new Table("Collection"));
        kioskHiddenAppsQuery.addJoin(new Join("Collection", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        kioskHiddenAppsQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        kioskHiddenAppsQuery.addJoin(new Join("ConfigDataItem", "AndroidKioskPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        kioskHiddenAppsQuery.addJoin(new Join("ConfigDataItem", "AndroidKioskPolicyBackgroundApps", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
        final Criteria hiddenAppsCriteria = new Criteria(new Column("Collection", "COLLECTION_ID"), (Object)collectionID, 0);
        kioskHiddenAppsQuery.addSelectColumn(new Column("AndroidKioskPolicy", "CONFIG_DATA_ITEM_ID"));
        kioskHiddenAppsQuery.addSelectColumn(new Column("AndroidKioskPolicy", "KIOSK_MODE"));
        kioskHiddenAppsQuery.addSelectColumn(new Column("AndroidKioskPolicy", "AUTO_DISTRIBUTE_APPS"));
        kioskHiddenAppsQuery.addSelectColumn(new Column("AndroidKioskPolicyBackgroundApps", "CONFIG_DATA_ITEM_ID"));
        kioskHiddenAppsQuery.addSelectColumn(new Column("AndroidKioskPolicyBackgroundApps", "APP_GROUP_ID"));
        kioskHiddenAppsQuery.setCriteria(hiddenAppsCriteria);
        final DataObject kioskHiddenDO = MDMUtil.getPersistence().get(kioskHiddenAppsQuery);
        if (!kioskHiddenDO.isEmpty()) {
            kioskDO.merge(kioskHiddenDO);
        }
        return kioskDO;
    }
    
    public void removeCollectionCommandsForDevices(final Long collectionId, final List<Long> resourceList) {
        final DeviceCommandRepository deviceCommandRepository = DeviceCommandRepository.getInstance();
        final List collectionList = new ArrayList();
        collectionList.add(collectionId);
        final List<Long> commandIDList = deviceCommandRepository.getCollectionIdsCommandList(collectionList, "InstallApplication");
        deviceCommandRepository.clearDeviceCommand(resourceList, commandIDList);
    }
    
    public void associateCollectionToAllAssociatedEntities(final Properties properties) throws DataAccessException {
        ProfileAssociateHandler.logger.log(Level.INFO, "Going to distribute all associated devices with the props {0}", properties);
        final List oldCollnList = ((Hashtable<K, List>)properties).get("OldCollectionList");
        final Long customerID = ((Hashtable<K, Long>)properties).get("customerId");
        final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Collection"));
        selectQuery.addJoin(new Join("Collection", "RecentProfileForGroup", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("RecentProfileForGroup", "Resource", new String[] { "GROUP_ID" }, new String[] { "RESOURCE_ID" }, 2));
        final Criteria collnCriteria = new Criteria(Column.getColumn("Collection", "COLLECTION_ID"), (Object)oldCollnList.toArray(), 8);
        final Criteria markedForDeleteGroup = new Criteria(Column.getColumn("RecentProfileForGroup", "MARKED_FOR_DELETE"), (Object)false, 0);
        selectQuery.setCriteria(collnCriteria.and(markedForDeleteGroup).and(customerCriteria));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "GROUP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "PROFILE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "COLLECTION_ID"));
        DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        Iterator iterator = dataObject.getRows("RecentProfileForGroup");
        final List groupList = new ArrayList();
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final Long resourceID = (Long)row.get("GROUP_ID");
            groupList.add(resourceID);
        }
        ProfileAssociateHandler.logger.log(Level.INFO, "The number of group that were found to have the collecion are {0}", groupList.toArray());
        final SelectQuery grupQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedDevice"));
        grupQuery.addJoin(new Join("ManagedDevice", "RecentProfileForResource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        grupQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        final Criteria groupCriteria = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupList.toArray(), 8);
        final Criteria memberCriteria = new Criteria(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)Column.getColumn("ManagedDevice", "RESOURCE_ID"), 0);
        grupQuery.addJoin(new Join("ManagedDevice", "CustomGroupMemberRel", groupCriteria.and(memberCriteria), 1));
        final Criteria managed = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
        final Criteria notNullCriteria = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)null, 0);
        final Criteria collndeviceCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "COLLECTION_ID"), (Object)oldCollnList.toArray(), 8);
        grupQuery.setCriteria(managed.and(notNullCriteria).and(collndeviceCriteria).and(customerCriteria));
        grupQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        dataObject = MDMUtil.getPersistenceLite().get(grupQuery);
        iterator = dataObject.getRows("ManagedDevice");
        final List resourceList = new ArrayList();
        while (iterator.hasNext()) {
            final Row row2 = iterator.next();
            final Long resID = (Long)row2.get("RESOURCE_ID");
            resourceList.add(resID);
        }
        ProfileAssociateHandler.logger.log(Level.INFO, "The number of devices that were found to have the collection without the group are {0}", resourceList.toArray());
        ((Hashtable<String, List>)properties).put("resourceList", groupList);
        ((Hashtable<String, Boolean>)properties).put("profileOrigin", true);
        if (groupList.size() > 0) {
            this.associateCollectionForGroup(properties);
        }
        ProfileAssociateHandler.logger.log(Level.INFO, "Assocation for groups is complete ");
        ((Hashtable<String, List>)properties).put("resourceList", resourceList);
        ((Hashtable<String, Boolean>)properties).put("profileOrigin", false);
        if (resourceList.size() > 0) {
            this.associateCollectionForResource(properties);
        }
        ProfileAssociateHandler.logger.log(Level.INFO, "Assocation for devices is complete ");
    }
    
    public void disassociateCollectionToAllAssociatedEntities(final Properties properties) throws DataAccessException {
        ProfileAssociateHandler.logger.log(Level.INFO, "Going to distribute all dis - associated devices with the props {0}", properties);
        final List oldCollnList = ((Hashtable<K, List>)properties).get("OldCollectionList");
        final Long customerID = ((Hashtable<K, Long>)properties).get("customerId");
        final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Collection"));
        selectQuery.addJoin(new Join("Collection", "RecentProfileForGroup", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("RecentProfileForGroup", "Resource", new String[] { "GROUP_ID" }, new String[] { "RESOURCE_ID" }, 2));
        final Criteria collnCriteria = new Criteria(Column.getColumn("Collection", "COLLECTION_ID"), (Object)oldCollnList.toArray(), 8);
        final Criteria markedForDeleteGroup = new Criteria(Column.getColumn("RecentProfileForGroup", "MARKED_FOR_DELETE"), (Object)false, 0);
        selectQuery.setCriteria(collnCriteria.and(markedForDeleteGroup).and(customerCriteria));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "GROUP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "PROFILE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "COLLECTION_ID"));
        DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        Iterator iterator = dataObject.getRows("RecentProfileForGroup");
        final List groupList = new ArrayList();
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final Long resourceID = (Long)row.get("GROUP_ID");
            groupList.add(resourceID);
        }
        ProfileAssociateHandler.logger.log(Level.INFO, "The number of group that were found to have the collecion are {0}", groupList.toArray());
        final SelectQuery grupQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedDevice"));
        grupQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        final Criteria recentProfileCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)Column.getColumn("ManagedDevice", "RESOURCE_ID"), 0);
        final Criteria collnresCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "COLLECTION_ID"), (Object)oldCollnList.toArray(), 8);
        grupQuery.addJoin(new Join("ManagedDevice", "RecentProfileForResource", recentProfileCriteria.and(collnresCriteria), 2));
        final Criteria groupCriteria = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupList.toArray(), 8);
        final Criteria memberCriteria = new Criteria(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)Column.getColumn("ManagedDevice", "RESOURCE_ID"), 0);
        grupQuery.addJoin(new Join("ManagedDevice", "CustomGroupMemberRel", groupCriteria.and(memberCriteria), 1));
        final Criteria managed = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
        final Criteria notNullCriteria = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)null, 0);
        grupQuery.setCriteria(managed.and(notNullCriteria).and(customerCriteria));
        grupQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        dataObject = MDMUtil.getPersistenceLite().get(grupQuery);
        iterator = dataObject.getRows("ManagedDevice");
        final List resourceList = new ArrayList();
        while (iterator.hasNext()) {
            final Row row2 = iterator.next();
            final Long resID = (Long)row2.get("RESOURCE_ID");
            resourceList.add(resID);
        }
        ProfileAssociateHandler.logger.log(Level.INFO, "The number of devices that were found to have the collection without the group are {0}", resourceList.toArray());
        ((Hashtable<String, List>)properties).put("resourceList", groupList);
        ((Hashtable<String, Boolean>)properties).put("profileOrigin", true);
        if (groupList.size() > 0) {
            this.disAssociateCollectionForGroup(properties);
            ProfileAssociateHandler.logger.log(Level.INFO, "dis - Assocation for groups is complete ");
        }
        if (resourceList.size() > 0) {
            ((Hashtable<String, List>)properties).put("resourceList", resourceList);
            ((Hashtable<String, Boolean>)properties).put("profileOrigin", false);
            this.disAssociateCollectionForResource(properties);
            ProfileAssociateHandler.logger.log(Level.INFO, "dis - Assocation for devices is complete ");
        }
    }
    
    public void associateCollectionToResources(final Long collectionID, final List<Long> resourceIDs, final Long customerID, final Long userID) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ProfileToCollection"));
        selectQuery.addJoin(new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_TYPE"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("ProfileToCollection", "COLLECTION_ID"), (Object)collectionID, 0));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Properties properties = new Properties();
            final Row row = dataObject.getFirstRow("Profile");
            final Long profileID = (Long)row.get("PROFILE_ID");
            final Integer profileType = (Integer)row.get("PROFILE_TYPE");
            ((Hashtable<String, List<Long>>)properties).put("resourceList", resourceIDs);
            ((Hashtable<String, Boolean>)properties).put("profileOrigin", false);
            ((Hashtable<String, Boolean>)properties).put("isSilentInstall", true);
            ((Hashtable<String, Boolean>)properties).put("isNotify", false);
            ((Hashtable<String, Long>)properties).put("customerId", customerID);
            ((Hashtable<String, Long>)properties).put("loggedOnUser", userID);
            final HashMap profileCollectionMap = new HashMap();
            profileCollectionMap.put(profileID, collectionID);
            ((Hashtable<String, HashMap>)properties).put("profileCollectionMap", profileCollectionMap);
            ((Hashtable<String, Boolean>)properties).put("isAppConfig", false);
            ((Hashtable<String, String>)properties).put("commandName", new ProfileUtil().getProfileCommand(profileType, 1));
            this.associateCollectionForResource(properties);
        }
    }
    
    public void disassociateCollectionToResources(final Long collectionID, final List<Long> resourceIDs, final Long customerID, final Long userID) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ProfileToCollection"));
        selectQuery.addJoin(new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_TYPE"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("ProfileToCollection", "COLLECTION_ID"), (Object)collectionID, 0));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Properties properties = new Properties();
            final Row row = dataObject.getFirstRow("Profile");
            final Long profileID = (Long)row.get("PROFILE_ID");
            final Integer profileType = (Integer)row.get("PROFILE_TYPE");
            ((Hashtable<String, List<Long>>)properties).put("resourceList", resourceIDs);
            ((Hashtable<String, Boolean>)properties).put("profileOrigin", false);
            ((Hashtable<String, Boolean>)properties).put("isSilentInstall", true);
            ((Hashtable<String, Boolean>)properties).put("isNotify", false);
            ((Hashtable<String, Long>)properties).put("customerId", customerID);
            ((Hashtable<String, Long>)properties).put("loggedOnUser", userID);
            final HashMap profileCollectionMap = new HashMap();
            profileCollectionMap.put(profileID, collectionID);
            ((Hashtable<String, HashMap>)properties).put("profileCollectionMap", profileCollectionMap);
            ((Hashtable<String, Boolean>)properties).put("isAppConfig", false);
            ((Hashtable<String, String>)properties).put("commandName", new ProfileUtil().getProfileCommand(profileType, 0));
            this.disAssociateCollectionForResource(properties);
        }
    }
    
    public void removeCollectionCommandsForSameProfile(final List resourceList, final List profileList, final String commandType) {
        ProfileAssociateHandler.logger.log(Level.INFO, "** Remove collection commands initiated. ResourceList : {0}, ProfileList : {1}", new Object[] { resourceList, profileList });
        final Set<Long> profileIDSet = new HashSet<Long>(profileList);
        final Map<Long, List<Long>> profileCollnMap = ProfileHandler.getProfileCollections(profileIDSet);
        final List<List<Long>> collectionList = new ArrayList<List<Long>>(profileCollnMap.values());
        final List<Long> collnMergeList = new ArrayList<Long>();
        for (final List<Long> collections : collectionList) {
            collnMergeList.addAll(collections);
        }
        if (!collnMergeList.isEmpty()) {
            final DeviceCommandRepository deviceCommandRepository = DeviceCommandRepository.getInstance();
            final List<Long> commandIDList = deviceCommandRepository.getCollectionIdsCommandList(collnMergeList, commandType);
            deviceCommandRepository.clearDeviceCommand(resourceList, commandIDList);
            ProfileAssociateHandler.logger.log(Level.INFO, "** Removed collection of profile command");
        }
    }
    
    private Properties getProfileBSList(final List profileList) {
        final Properties profileToBSListMap = new Properties();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
            selectQuery.addJoin(new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            selectQuery.addJoin(new Join("ProfileToCollection", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQuery.addJoin(new Join("MdAppToCollection", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
            selectQuery.addJoin(new Join("MdAppToGroupRel", "MdStoreAssetToAppGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            selectQuery.addJoin(new Join("MdStoreAssetToAppGroupRel", "MdBusinessStoreToAssetRel", new String[] { "STORE_ASSET_ID" }, new String[] { "STORE_ASSET_ID" }, 2));
            selectQuery.addJoin(new Join("MdBusinessStoreToAssetRel", "ManagedBusinessStore", new String[] { "BUSINESSSTORE_ID" }, new String[] { "BUSINESSSTORE_ID" }, 2));
            final Criteria iOSPlatformCriteria = new Criteria(Column.getColumn("Profile", "PLATFORM_TYPE"), (Object)1, 0);
            final Criteria profileCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileList.toArray(), 8);
            final Column profileIDColumn = new Column("Profile", "PROFILE_ID");
            final Column businessStoreIDColumn = new Column("MdBusinessStoreToAssetRel", "BUSINESSSTORE_ID");
            final SortColumn sortColumn = new SortColumn(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_ID"), true);
            final ArrayList groupByList = new ArrayList();
            groupByList.add(profileIDColumn);
            groupByList.add(businessStoreIDColumn);
            selectQuery.addSelectColumn(profileIDColumn);
            selectQuery.addSelectColumn(businessStoreIDColumn);
            selectQuery.addSortColumn(sortColumn);
            selectQuery.setCriteria(iOSPlatformCriteria.and(profileCriteria));
            final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (ds.next()) {
                final Long profileID = (Long)ds.getValue("PROFILE_ID");
                final Long businessStoreID = (Long)ds.getValue("BUSINESSSTORE_ID");
                List tempBSList = ((Hashtable<K, List>)profileToBSListMap).get(profileID);
                if (tempBSList == null) {
                    tempBSList = new ArrayList();
                }
                if (!tempBSList.contains(businessStoreID)) {
                    tempBSList.add(businessStoreID);
                }
                ((Hashtable<Long, List>)profileToBSListMap).put(profileID, tempBSList);
            }
        }
        catch (final Exception e) {
            ProfileAssociateHandler.logger.log(Level.SEVERE, "Exception in getProfileBSList", e);
        }
        return profileToBSListMap;
    }
    
    private Properties getProfileBSMapForAppWithOneBS(final Properties profileToBSListMap) {
        final Properties profileToBusinessStoreProps = new Properties();
        if (!profileToBSListMap.isEmpty()) {
            final Properties tempBSListMap = (Properties)profileToBSListMap.clone();
            for (final Long profileID : ((Hashtable<Object, V>)tempBSListMap).keySet()) {
                final List tempBSList = ((Hashtable<K, List>)tempBSListMap).get(profileID);
                if (tempBSList.size() == 1) {
                    ((Hashtable<Long, Object>)profileToBusinessStoreProps).put(profileID, tempBSList.get(0));
                    profileToBSListMap.remove(profileID);
                }
            }
        }
        return profileToBusinessStoreProps;
    }
    
    private Long getBSIdForAppBasedOnExistingAssociation(final Long profileID, final List resourceList) {
        ProfileAssociateHandler.logger.log(Level.INFO, "Kiosk BS App Distribution handling: Checking if profileID: {0} is already available for resources: {1}", new Object[] { profileID, resourceList });
        return this.getPreferredBusinessStoreID(resourceList, profileID, null);
    }
    
    private Long getPreferredBusinessStoreID(final List resourceList, final Long profileID, final List businessStoreList) {
        Long businessStoreID = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MDMResourceToDeploymentConfigs"));
            Criteria criteria = new Criteria(Column.getColumn("MDMResourceToDeploymentConfigs", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            selectQuery.addSelectColumn(Column.getColumn("MDMResourceToDeploymentConfigs", "*"));
            final SortColumn timeSortColumn = new SortColumn(Column.getColumn("MDMResourceToDeploymentConfigs", "ADDED_TIME"), false);
            if (profileID != null) {
                criteria = criteria.and(new Criteria(Column.getColumn("MDMResourceToDeploymentConfigs", "PROFILE_ID"), (Object)profileID, 0));
            }
            if (businessStoreList != null) {
                criteria = criteria.and(new Criteria(Column.getColumn("MDMResourceToDeploymentConfigs", "BUSINESSSTORE_ID"), (Object)businessStoreList.toArray(), 8));
            }
            selectQuery.setCriteria(criteria);
            selectQuery.addSortColumn(timeSortColumn);
            final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
            if (ds.next()) {
                businessStoreID = (Long)ds.getValue("BUSINESSSTORE_ID");
            }
        }
        catch (final Exception e) {
            ProfileAssociateHandler.logger.log(Level.INFO, "Exception in getPreferredBusinessStoreID", e);
        }
        return businessStoreID;
    }
    
    private Long getBsIdForResWithAppsFromAvailableBSIds(final List resourceList, final List businessStoreIDs) {
        ProfileAssociateHandler.logger.log(Level.INFO, "Kiosk BS App Distribution handling: Checking if businessStoreIDs: {0} is already available for resources: {1} for other apps", new Object[] { businessStoreIDs, resourceList });
        return this.getPreferredBusinessStoreID(resourceList, null, businessStoreIDs);
    }
    
    public Properties getPreferredProfileToBusinessStoreMap(Properties profileToBusinessStore, final int platformType, final List profileList, final List resourceList) {
        if (profileToBusinessStore == null) {
            profileToBusinessStore = new Properties();
        }
        final Properties profileToBSListMap = this.getProfileBSList(profileList);
        if (!profileToBSListMap.isEmpty()) {
            profileToBusinessStore = this.getProfileBSMapForAppWithOneBS(profileToBSListMap);
            if (!profileToBSListMap.isEmpty()) {
                ProfileAssociateHandler.logger.log(Level.INFO, "Kiosk BS App Distribution handling: There are some apps present in more than one business store");
                final List tempProfileList = new ArrayList(profileToBSListMap.keySet());
                for (final Long profileID : tempProfileList) {
                    Long businessStoreID = this.getBSIdForAppBasedOnExistingAssociation(profileID, resourceList);
                    if (businessStoreID != null) {
                        ProfileAssociateHandler.logger.log(Level.INFO, "Kiosk BS App Distribution handling: Profile(App) {0} found in one of the resources from businessStoreID {1}", new Object[] { profileID, businessStoreID });
                        ((Hashtable<Long, Long>)profileToBusinessStore).put(profileID, businessStoreID);
                    }
                    else {
                        ProfileAssociateHandler.logger.log(Level.INFO, "Kiosk BS App Distribution handling: Profile(App) {0} not already associated with any resources: {1}.", new Object[] { profileID, resourceList });
                        businessStoreID = this.getBsIdForResWithAppsFromAvailableBSIds(resourceList, ((Hashtable<K, List>)profileToBSListMap).get(profileID));
                        if (businessStoreID != null) {
                            ProfileAssociateHandler.logger.log(Level.INFO, "Kiosk BS App Distribution handling: BusinessStoreID {0} of Profile(App) is found associated with one of the resources {2} for other bs apps", new Object[] { businessStoreID, profileID, resourceList });
                            ((Hashtable<Long, Long>)profileToBusinessStore).put(profileID, businessStoreID);
                        }
                        else {
                            final List businessStoreList = ((Hashtable<K, List>)profileToBSListMap).get(profileID);
                            if (platformType == 1) {
                                businessStoreID = businessStoreList.get(0);
                                ProfileAssociateHandler.logger.log(Level.INFO, "Unable to find preferred businessStoreID for profile {0}. Hence taking the first added businessstore {1}", new Object[] { profileID, businessStoreID });
                            }
                            else {
                                businessStoreID = businessStoreList.get(0);
                            }
                            ((Hashtable<Long, Long>)profileToBusinessStore).put(profileID, businessStoreID);
                        }
                    }
                }
            }
        }
        return profileToBusinessStore;
    }
    
    static {
        ProfileAssociateHandler.logger = Logger.getLogger("MDMConfigLogger");
        ProfileAssociateHandler.profileLogger = Logger.getLogger("MDMProfileConfigLogger");
        ProfileAssociateHandler.profileDistributionLog = Logger.getLogger("MDMProfileDistributionLog");
        ProfileAssociateHandler.handler = null;
    }
}
