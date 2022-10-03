package com.me.mdm.apps.handler;

import java.util.Hashtable;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import java.util.Properties;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import org.json.JSONArray;
import java.util.List;
import java.util.ArrayList;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.agent.handlers.MacMDMAgentHandler;
import com.me.mdm.uem.mac.MacDCAgentHandler;
import java.util.HashMap;
import java.util.logging.Logger;

public class AppsAutoDeployment
{
    private static AppsAutoDeployment appHandler;
    public static final String AGENT_TYPE = "AGENT_TYPE";
    public Logger logger;
    public static final HashMap<String, Integer> AUTO_DEPLOY_AGENT_MAP;
    
    public AppsAutoDeployment() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    public static final Integer getAgentIDFromIdentifier(final String identifier) {
        return AppsAutoDeployment.AUTO_DEPLOY_AGENT_MAP.containsKey(identifier) ? AppsAutoDeployment.AUTO_DEPLOY_AGENT_MAP.get(identifier) : Integer.valueOf(-1);
    }
    
    public static AppsAutoDeployment getInstance() {
        if (AppsAutoDeployment.appHandler == null) {
            AppsAutoDeployment.appHandler = new AppsAutoDeployment();
        }
        return AppsAutoDeployment.appHandler;
    }
    
    public AppAutoDeploymentHandler getAgentHandler(final int agentType) throws APIHTTPException {
        switch (agentType) {
            case 1: {
                return new MacDCAgentHandler();
            }
            case 2: {
                return new MacMDMAgentHandler();
            }
            default: {
                this.logger.log(Level.SEVERE, "ERROR....No such Agent");
                throw new APIHTTPException("COM0008", new Object[0]);
            }
        }
    }
    
    public void addAgentApp(final int agentType, final long customerId) throws Exception {
        try {
            final AppAutoDeploymentHandler handler = this.getAgentHandler(agentType);
            final int platform = handler.getPlatformType();
            final boolean isAppExists = AppsUtil.getInstance().isAppExistsInPackage(handler.getBundleIdentifier(), platform, customerId);
            if (!isAppExists) {
                MDMUtil.getUserTransaction().begin();
                final JSONObject jsonObject = handler.getAgentAppData(customerId);
                MDMAppMgmtHandler.getInstance().addOrUpdatePackageInRepository(jsonObject);
                MDMUtil.getUserTransaction().commit();
                this.logger.log(Level.INFO, "Native agent {0} added", handler.getBundleIdentifier());
            }
        }
        catch (final Exception e) {
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final Exception ex) {
                this.logger.log(Level.SEVERE, "Exception while rollback of native app addition", ex);
            }
            this.logger.log(Level.WARNING, " Exception in adding native agent app ", e);
            throw e;
        }
    }
    
    public Integer getAgentIDFromCollectionID(final Long collectionID) throws Exception {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("AppGroupToCollection"));
        final Join groupJoin = new Join("AppGroupToCollection", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
        query.addJoin(groupJoin);
        final Criteria criteria = new Criteria(Column.getColumn("AppGroupToCollection", "COLLECTION_ID"), (Object)collectionID, 0);
        query.setCriteria(criteria);
        query.addSelectColumn(Column.getColumn("AppGroupToCollection", "APP_GROUP_ID"));
        query.addSelectColumn(Column.getColumn("AppGroupToCollection", "COLLECTION_ID"));
        query.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
        query.addSelectColumn(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"));
        final DataObject dataObject = MDMUtil.getPersistence().get(query);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("MdAppGroupDetails");
            final String bundleIdentifier = (String)row.get("IDENTIFIER");
            return getAgentIDFromIdentifier(bundleIdentifier);
        }
        return -1;
    }
    
    public Long getCollectionIDFromAgentID(final Integer agentID) {
        final Long collectionID = -1L;
        try {
            final String identifier = this.getAgentHandler(agentID).getBundleIdentifier();
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("AppGroupToCollection"));
            final Join groupJoin = new Join("AppGroupToCollection", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
            query.addJoin(groupJoin);
            final Criteria criteria = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)identifier, 0);
            query.setCriteria(criteria);
            query.addSelectColumn(Column.getColumn("AppGroupToCollection", "APP_GROUP_ID"));
            query.addSelectColumn(Column.getColumn("AppGroupToCollection", "COLLECTION_ID"));
            query.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("AppGroupToCollection");
                return (Long)row.get("COLLECTION_ID");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Unable to get collection ID from Agent ID", e);
        }
        return collectionID;
    }
    
    public Integer getAgentIDFromCommandUUID(String commandUUID) {
        try {
            if (!commandUUID.contains("agentID") && !commandUUID.contains("InstallAgentID=")) {
                commandUUID = MDMUtil.getInstance().getCollectionIdFromCommandUUID(commandUUID);
                final int agentID = this.getAgentIDFromCollectionID(Long.parseLong(commandUUID));
                return agentID;
            }
            final Boolean isInstallApplication = commandUUID.contains("InstallAgentID=");
            final String startString = "InstallAgentID=";
            final String configStartString = "Install_AgentConfig?agentID=";
            commandUUID = commandUUID.replaceAll("InstallEnterpriseApplication;InstallAgentID=[0-9]*;Collection=", "");
            commandUUID = commandUUID.replace(configStartString, "");
            commandUUID = commandUUID.replace(startString, "");
            if (isInstallApplication) {
                return this.getAgentIDFromCollectionID(Long.parseLong(commandUUID));
            }
            return Integer.parseInt(commandUUID);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Failed to retrieve agentID from commandUUID");
            return -1;
        }
    }
    
    public void handleNativeAgent(final JSONObject jsData) throws Exception {
        final Long customerId = jsData.getLong("CUSTOMER_ID");
        final int agentType = jsData.optInt("AGENT_TYPE");
        final boolean isNativeAgentEnable = jsData.optBoolean("IS_NATIVE_APP_ENABLE");
        if (isNativeAgentEnable) {
            try {
                this.addAgentApp(agentType, customerId);
            }
            catch (final Exception ex) {
                this.logger.log(Level.WARNING, " Exception in handling native agent", ex);
            }
            List resourceList = null;
            if (jsData.has("RESOURCE_LIST")) {
                resourceList = new ArrayList();
                final JSONArray resourceArray = jsData.optJSONArray("RESOURCE_LIST");
                for (int i = 0; i < resourceArray.length(); ++i) {
                    resourceList.add(resourceArray.get(i));
                }
            }
            this.distributeAgentwithEnrolledDevices(customerId, resourceList, agentType);
        }
        else {
            this.logger.log(Level.WARNING, "Inside handleNativeAgent() : Native Agent is disabled for agentType :{0}", agentType);
        }
    }
    
    public void removeAppFromCatalogForMac(final Integer agentType, final Long resourceID, final Long customerID) throws Exception {
        final AppAutoDeploymentHandler agentHandler = this.getAgentHandler(agentType);
        final String bundleID = agentHandler.getBundleIdentifier();
        final Long appID = MDMAppMgmtHandler.getInstance().getNativeAgentAppId(customerID, 1, bundleID);
        if (appID != null) {
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("MdAppCatalogToResource");
            Criteria criteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "INSTALLED_APP_ID"), (Object)appID, 0);
            criteria = criteria.and(new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourceID, 0));
            deleteQuery.setCriteria(criteria);
            MDMUtil.getPersistence().delete(deleteQuery);
        }
    }
    
    public Properties getAppProfileDetails(final Long customerId, final List resourceList, final int platform, final int agentType, final String request) throws Exception {
        final AppAutoDeploymentHandler agentHandler = this.getAgentHandler(agentType);
        final String bundleID = agentHandler.getBundleIdentifier();
        final Long appID = MDMAppMgmtHandler.getInstance().getNativeAgentAppId(customerId, platform, bundleID);
        final HashMap profileCollectionMap = MDMUtil.getInstance().getProfiletoCollectionMap(appID);
        this.logger.log(Level.INFO, "Going to assign app for devices: collectionList: {0} resourceList: {1}", new Object[] { profileCollectionMap, resourceList });
        final Properties properties = new Properties();
        final JSONObject createdUserDetailsJSON = ProfileUtil.getCreatedUserDetailsForProfile(profileCollectionMap.keySet().iterator().next());
        ((Hashtable<String, HashMap>)properties).put("profileCollectionMap", profileCollectionMap);
        ((Hashtable<String, String>)properties).put("commandName", request);
        ((Hashtable<String, Boolean>)properties).put("isAppConfig", true);
        ((Hashtable<String, List>)properties).put("resourceList", resourceList);
        ((Hashtable<String, Integer>)properties).put("platformtype", platform);
        ((Hashtable<String, Long>)properties).put("customerId", customerId);
        ((Hashtable<String, Object>)properties).put("loggedOnUserName", createdUserDetailsJSON.get("FIRST_NAME"));
        ((Hashtable<String, Object>)properties).put("loggedOnUser", createdUserDetailsJSON.get("USER_ID"));
        ((Hashtable<String, Object>)properties).put("UserId", createdUserDetailsJSON.get("USER_ID"));
        ((Hashtable<String, Boolean>)properties).put("isSilentInstall", true);
        ((Hashtable<String, Boolean>)properties).put("isNotify", false);
        this.logger.log(Level.INFO, "Going to assign app for devices: collectionList: {0} resourceList: {1}", new Object[] { ((Hashtable<K, Object>)properties).get("profileCollectionList"), resourceList });
        return properties;
    }
    
    public void removeAgentFromEnrolledDevices(final Long customerId, List resourceList, final int agentType) throws Exception {
        final AppAutoDeploymentHandler handler = this.getAgentHandler(agentType);
        final int platform = handler.getPlatformType();
        if (resourceList == null) {
            final Criteria agentTypecri = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)platform, 0);
            final Criteria customercri = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria cri = agentTypecri.and(customercri);
            resourceList = ManagedDeviceHandler.getInstance().getManagedDeviceResourceIDs(cri);
        }
        resourceList = handler.filterDevices(resourceList);
        if (resourceList != null && !resourceList.isEmpty()) {
            final Properties properties = this.getAppProfileDetails(customerId, resourceList, platform, agentType, "RemoveProfile");
            ProfileAssociateHandler.getInstance().associateCollectionForResource(properties);
        }
    }
    
    private void distributeAgentwithEnrolledDevices(final Long customerId, List resourceList, final int agentType) throws Exception {
        try {
            final AppAutoDeploymentHandler handler = this.getAgentHandler(agentType);
            final int platform = handler.getPlatformType();
            if (resourceList == null) {
                final Criteria agentTypecri = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)platform, 0);
                final Criteria customercri = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
                final Criteria cri = agentTypecri.and(customercri);
                resourceList = ManagedDeviceHandler.getInstance().getManagedDeviceResourceIDs(cri);
            }
            this.logger.log(Level.INFO, "Inside distributeAgentwithEnrolledDevices PRE-FILTER-DEVICES agentType:{0} ,resourceList:{1}", new Object[] { agentType, resourceList });
            resourceList = handler.filterDevices(resourceList);
            this.logger.log(Level.INFO, "Inside distributeAgentwithEnrolledDevices POST-FILTER-DEVICES agentType:{0} ,resourceList:{1}", new Object[] { agentType, resourceList });
            if (resourceList != null && !resourceList.isEmpty()) {
                final Properties properties = this.getAppProfileDetails(customerId, resourceList, platform, agentType, "InstallApplication");
                ProfileAssociateHandler.getInstance().associateCollectionForResource(properties);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, " Exception in distributing agent to devices ", ex);
            throw ex;
        }
    }
    
    static {
        AppsAutoDeployment.appHandler = null;
        AUTO_DEPLOY_AGENT_MAP = new HashMap<String, Integer>() {
            {
                this.put("com.manageengine.ems", 1);
                this.put("com.manageengine.mdm.mac", 2);
            }
        };
    }
}
