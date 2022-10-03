package com.adventnet.sym.server.mdm.iosnativeapp;

import java.util.Hashtable;
import com.adventnet.sym.server.mdm.ios.payload.PayloadHandler;
import com.adventnet.ds.query.DMDataSetWrapper;
import java.util.Random;
import java.math.BigInteger;
import java.security.SecureRandom;
import com.me.mdm.server.apps.AppTrashModeHandler;
import org.json.JSONException;
import com.me.mdm.server.apps.AppFacade;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import com.me.mdm.server.apps.multiversion.AppVersionDBUtil;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.adventnet.sym.server.mdm.apps.AppInstallationStatusHandler;
import java.util.Set;
import java.util.Map;
import com.adventnet.sym.server.mdm.queue.CollectionAssociationQueue.AssociationQueueHandler;
import com.adventnet.sym.server.mdm.util.MDMCommonConstants;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import com.adventnet.ds.query.Criteria;
import java.util.HashMap;
import com.me.mdm.server.alerts.MDMAlertConstants;
import java.util.Properties;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.alerts.AlertMailGeneratorUtil;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import org.json.JSONObject;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.message.MDMMessageHandler;
import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import java.util.logging.Logger;

public class IosNativeAppHandler
{
    private static IosNativeAppHandler appHandler;
    public Logger logger;
    private Logger configLogger;
    
    public IosNativeAppHandler() {
        this.logger = Logger.getLogger("MDMEnrollment");
        this.configLogger = Logger.getLogger("MDMConfigLogger");
    }
    
    public static IosNativeAppHandler getInstance() {
        if (IosNativeAppHandler.appHandler == null) {
            IosNativeAppHandler.appHandler = new IosNativeAppHandler();
        }
        return IosNativeAppHandler.appHandler;
    }
    
    public void addIOSNativeAgent(final long customerId, final long userId) {
        try {
            final boolean isAppExists = AppsUtil.getInstance().isAppExistsInPackage("com.manageengine.mdm.iosagent", 1, customerId);
            if (!isAppExists) {
                MDMUtil.getUserTransaction().begin();
                final JSONObject jsonObject = MDMAppMgmtHandler.getInstance().createiOSNativeAgentJSON(customerId, userId);
                MDMAppMgmtHandler.getInstance().addOrUpdatePackageInRepository(jsonObject);
                MDMUtil.getUserTransaction().commit();
                MDMMessageHandler.getInstance().messageAction("NO_APP_ADDED", customerId);
            }
        }
        catch (final Exception e) {
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final Exception ex) {
                this.logger.log(Level.SEVERE, "Exception while rollback of native app addition", ex);
            }
            this.logger.log(Level.WARNING, " Exception in isIOSNativeAgentEnable ", e);
        }
    }
    
    public void sendIosNativeEnrollmentMail(final long resourceId) {
        try {
            final String authPassword = this.generateEnrollmentId(resourceId);
            final Long customerID = CustomerInfoUtil.getInstance().getCustomerIDForResID(Long.valueOf(resourceId));
            final String deviceUDID = ManagedDeviceHandler.getInstance().getUDIDFromResourceID(resourceId);
            final HashMap userInfoMap = ManagedUserHandler.getInstance().getManagedUserDetailsForDevice(deviceUDID);
            final String userName = userInfoMap.get("NAME");
            final String userEmail = userInfoMap.get("EMAIL_ADDRESS");
            final AlertMailGeneratorUtil mailGenerator = new AlertMailGeneratorUtil(this.logger);
            final Properties serverProps = ApiFactoryProvider.getServerSettingsAPI().getNATConfigurationProperties();
            final Properties enrolAuthlMailProperties = new Properties();
            ((Hashtable<String, String>)enrolAuthlMailProperties).put("$user_name$", userName);
            ((Hashtable<String, Object>)enrolAuthlMailProperties).put("$server_name$", ((Hashtable<K, Object>)serverProps).get("NAT_ADDRESS"));
            ((Hashtable<String, Object>)enrolAuthlMailProperties).put("$server_port$", ((Hashtable<K, Object>)serverProps).get("NAT_HTTPS_PORT"));
            ((Hashtable<String, String>)enrolAuthlMailProperties).put("$enrollment_id$", authPassword);
            ((Hashtable<String, String>)enrolAuthlMailProperties).put("$user_emailid$", userEmail);
            mailGenerator.sendMail(MDMAlertConstants.ENROLLMENT_AUTHENTICATION_TEMPLATE, "MDM", customerID, enrolAuthlMailProperties);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while sending native app enrollment mail", e);
        }
    }
    
    public ArrayList getiOSNativeAgentResourceList(final Criteria criteria) {
        final ArrayList managedUserIDList = new ArrayList();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedDevice"));
            final Join resJoin = new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            final Join iOSNativeJoin = new Join("ManagedDevice", "IOSNativeAppStatus", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            selectQuery.addJoin(resJoin);
            selectQuery.addJoin(iOSNativeJoin);
            if (criteria != null) {
                selectQuery.setCriteria(criteria);
            }
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "*"));
            selectQuery.addSelectColumn(Column.getColumn("IOSNativeAppStatus", "*"));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("ManagedDevice");
                while (iterator.hasNext()) {
                    final Row managedUserRow = iterator.next();
                    managedUserIDList.add(managedUserRow.get("RESOURCE_ID"));
                }
            }
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
        return managedUserIDList;
    }
    
    public List getManagedIOSDeviceList(final Long customerID) {
        List iOSDevicesList = new ArrayList();
        try {
            final List modelList = new ArrayList();
            modelList.add(1);
            modelList.add(2);
            modelList.add(0);
            iOSDevicesList = ManagedDeviceHandler.getInstance().getSuccessfullyEnrolledDevicesForPlatformModels(1, modelList, customerID);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getIOSDeviceIDList", e);
        }
        return iOSDevicesList;
    }
    
    public ArrayList getNativeAgentDeviceIDList(final Criteria criteria) {
        final ArrayList managedUserIDList = new ArrayList();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("IOSNativeAppStatus"));
            final Join resJoin = new Join("IOSNativeAppStatus", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            selectQuery.addJoin(resJoin);
            if (criteria != null) {
                selectQuery.setCriteria(criteria);
            }
            selectQuery.addSelectColumn(Column.getColumn("IOSNativeAppStatus", "*"));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("IOSNativeAppStatus");
                while (iterator.hasNext()) {
                    final Row managedUserRow = iterator.next();
                    managedUserIDList.add(managedUserRow.get("RESOURCE_ID"));
                }
            }
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
        return managedUserIDList;
    }
    
    public void silentlyInstallIOSNativeAgent(final List resourceList, final Long customerId, final Boolean isAppUpgrade) {
        final Long appId = MDMAppMgmtHandler.getInstance().getIOSNativeAgentAppId(customerId);
        final Long collnId = MDMUtil.getInstance().getCollectionIDfromAppID(appId);
        final List collectionList = new ArrayList();
        collectionList.add(collnId);
        final List profileList = new ArrayList();
        final HashMap profileMap = MDMUtil.getInstance().getProfileDetailsForCollectionId(collnId);
        final Long profileId = profileMap.get("PROFILE_ID");
        final Long createdby = profileMap.get("CREATED_BY");
        profileList.add(profileId);
        final Map<Long, Long> profileCollnMap = new HashMap<Long, Long>();
        profileCollnMap.put(profileId, collnId);
        final Properties taskProps = new Properties();
        final HashMap deviceMap = new HashMap();
        final Set resSet = new HashSet();
        resSet.addAll(resourceList);
        deviceMap.put(1, resSet);
        ((Hashtable<String, HashMap>)taskProps).put("deviceMap", deviceMap);
        final HashMap collectionToPlatformMap = new HashMap();
        collectionToPlatformMap.put(1, collectionList);
        ((Hashtable<String, HashMap>)taskProps).put("collectionToPlatformMap", collectionToPlatformMap);
        final HashMap profileToPlatformMap = new HashMap();
        profileToPlatformMap.put(1, profileList);
        ((Hashtable<String, HashMap>)taskProps).put("profileToPlatformMap", profileToPlatformMap);
        ((Hashtable<String, Boolean>)taskProps).put("isGroup", Boolean.FALSE);
        ((Hashtable<String, List>)taskProps).put("resourceList", resourceList);
        ((Hashtable<String, List>)taskProps).put("collectionList", collectionList);
        ((Hashtable<String, Map<Long, Long>>)taskProps).put("profileCollnMap", profileCollnMap);
        ((Hashtable<String, Boolean>)taskProps).put("isAppConfig", true);
        ((Hashtable<String, Long>)taskProps).put("UserId", createdby);
        ((Hashtable<String, Long>)taskProps).put("customerId", customerId);
        ((Hashtable<String, Boolean>)taskProps).put("isAppUpgrade", isAppUpgrade);
        ((Hashtable<String, Integer>)taskProps).put("toBeAssociatedAppSource", MDMCommonConstants.ASSOCIATED_APP_SOURCE_BY_USER);
        ((Hashtable<String, Boolean>)taskProps).put("isSilentInstall", true);
        ((Hashtable<String, Boolean>)taskProps).put("isNotify", false);
        ((Hashtable<String, String>)taskProps).put("commandName", "InstallApplication");
        ((Hashtable<String, Integer>)taskProps).put("commandType", 1);
        final HashMap taskInfoMap = new HashMap();
        taskInfoMap.put("taskName", "AssignDeviceCommandTask");
        taskInfoMap.put("schedulerTime", System.currentTimeMillis());
        taskInfoMap.put("poolName", "mdmPool");
        try {
            AssociationQueueHandler.getInstance().executeTask(taskInfoMap, taskProps);
        }
        catch (final Exception ex) {
            this.configLogger.log(Level.SEVERE, "Exception occurred while distributing app/profile to device ", ex);
        }
    }
    
    public void distributeAndInstallIOSNativeAgent(final List resourceList, final Long customerID) throws Exception {
        this.distributeIOSAgentToDevices(resourceList, customerID, Boolean.FALSE);
    }
    
    public void distributeIOSAgentToDevices(final List resourceList, final Long customerID, final Boolean isAppUpgrade) {
        try {
            final List resEligibleForDistribution = new ArrayList(resourceList);
            final Long packageID = AppsUtil.getInstance().getPackageId("com.manageengine.mdm.iosagent", 1, customerID);
            final Long profileID = AppsUtil.getInstance().getProfileIdForPackage(packageID, customerID);
            final Long appGroupID = AppsUtil.getInstance().getAppGroupId(packageID);
            if (!isAppUpgrade) {
                final List appAlreadyAvailableResList = new AppInstallationStatusHandler().getAppInstalledDevices(appGroupID, resourceList);
                if (appAlreadyAvailableResList != null && !appAlreadyAvailableResList.isEmpty()) {
                    resEligibleForDistribution.removeAll(appAlreadyAvailableResList);
                }
            }
            final JSONObject createdUserDetailsJSON = ProfileUtil.getCreatedUserDetailsForProfile(profileID);
            final JSONObject msgBody = new JSONObject();
            final JSONArray appDetailsArray = new JSONArray();
            final Long releaseLabelId = AppVersionDBUtil.getInstance().getProductionAppReleaseLabelIDForCustomer(customerID);
            final JSONObject appDetail = new JSONObject();
            appDetail.put("app_id", (Object)packageID);
            appDetail.put("release_label_id", (Object)releaseLabelId);
            Properties profileToBusinessStore = new Properties();
            final List profileList = new ArrayList();
            profileList.add(profileID);
            profileToBusinessStore = new ProfileAssociateHandler().getPreferredProfileToBusinessStoreMap(profileToBusinessStore, 1, profileList, resEligibleForDistribution);
            if (!profileToBusinessStore.isEmpty()) {
                appDetail.put("businessstore_id", ((Hashtable<K, Object>)profileToBusinessStore).get(profileID));
            }
            appDetailsArray.put((Object)appDetail);
            msgBody.put("device_ids", (Collection)resEligibleForDistribution);
            msgBody.put("app_details", (Object)appDetailsArray);
            msgBody.put("silent_install", (Object)Boolean.TRUE);
            msgBody.put("notify_user_via_email", (Object)Boolean.FALSE);
            msgBody.put("isAppUpgrade", (Object)isAppUpgrade);
            final JSONObject filters = new JSONObject();
            filters.put("customer_id", (Object)customerID);
            filters.put("user_name", (Object)createdUserDetailsJSON.getString("FIRST_NAME"));
            filters.put("user_id", createdUserDetailsJSON.getLong("USER_ID"));
            filters.put("login_id", (Object)DMUserHandler.getLoginIdForUserId(Long.valueOf(createdUserDetailsJSON.getLong("USER_ID"))));
            final JSONObject message = new JSONObject();
            message.put("msg_body", (Object)msgBody);
            final JSONObject msgHeader = new JSONObject();
            msgHeader.put("filters", (Object)filters);
            msgHeader.put("resource_identifier", (Object)new JSONObject());
            message.put("msg_header", (Object)msgHeader);
            new AppFacade().associateAppsToDevices(message);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, " Exception in distributeIOSAgentToDevices", ex);
        }
    }
    
    public Properties getIOSNativeAppProfileDetails(final Long customerId, final List resourceList) throws JSONException {
        final Long appId = MDMAppMgmtHandler.getInstance().getIOSNativeAgentAppId(customerId);
        final HashMap profileCollectionMap = MDMUtil.getInstance().getProfiletoCollectionMap(appId);
        this.logger.log(Level.INFO, "distributeAndInstallIOSNativeAgent: Going to assign app for devices: collectionList: {0} resourceList: {1}", new Object[] { profileCollectionMap, resourceList });
        final Properties properties = new Properties();
        final JSONObject createdUserDetailsJSON = ProfileUtil.getCreatedUserDetailsForProfile(profileCollectionMap.keySet().iterator().next());
        ((Hashtable<String, HashMap>)properties).put("profileCollectionMap", profileCollectionMap);
        ((Hashtable<String, String>)properties).put("commandName", "InstallApplication");
        ((Hashtable<String, Boolean>)properties).put("isAppConfig", true);
        ((Hashtable<String, List>)properties).put("resourceList", resourceList);
        ((Hashtable<String, Integer>)properties).put("platformtype", 1);
        ((Hashtable<String, Long>)properties).put("customerId", customerId);
        ((Hashtable<String, Object>)properties).put("loggedOnUserName", createdUserDetailsJSON.get("FIRST_NAME"));
        ((Hashtable<String, Object>)properties).put("loggedOnUser", createdUserDetailsJSON.get("USER_ID"));
        ((Hashtable<String, Object>)properties).put("UserId", createdUserDetailsJSON.get("USER_ID"));
        ((Hashtable<String, Boolean>)properties).put("isSilentInstall", true);
        ((Hashtable<String, Boolean>)properties).put("isNotify", false);
        this.logger.log(Level.INFO, "getIOSNativeAppProfileDetails :: Going to assign app for devices: collectionList: {0} resourceList: {1}", new Object[] { ((Hashtable<K, Object>)properties).get("profileCollectionMap"), resourceList });
        return properties;
    }
    
    private void distributeIOSNativeAgentwithEnrolledDevices(final Long appId, final Long customerId) {
        try {
            final List resourceList = this.getManagedIOSDeviceList(customerId);
            if (resourceList.isEmpty()) {
                this.logger.log(Level.INFO, "No managed iOS devices available to distribute ios agent");
                return;
            }
            this.distributeIOSAgentToDevices(resourceList, customerId, Boolean.FALSE);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, " Exception in distributeIOSNativeAgentwithEnrolledDevices ", ex);
        }
    }
    
    public void handleiOSNativeAgent(final JSONObject jsData) {
        final Long customerId = CustomerInfoUtil.getInstance().getCustomerId();
        final boolean isNativeAgentEnable = (boolean)jsData.optBoolean("IS_NATIVE_APP_ENABLE");
        final boolean isAppExists = AppsUtil.getInstance().isAppExistsInPackage("com.manageengine.mdm.iosagent", 1, customerId);
        boolean isAppInTrash = false;
        this.logger.log(Level.INFO, "handleiOSNativeAgent::Is native IOS app already exists {0} Is native app enabled {1}", new Object[] { isAppExists, isNativeAgentEnable });
        if (isNativeAgentEnable) {
            if (!isAppExists) {
                try {
                    final Long userId = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
                    final JSONObject jsonObject = MDMAppMgmtHandler.getInstance().createiOSNativeAgentJSON(customerId, userId);
                    MDMAppMgmtHandler.getInstance().addOrUpdatePackageInRepository(jsonObject);
                    MDMMessageHandler.getInstance().messageAction("NO_APP_ADDED", customerId);
                }
                catch (final Exception ex) {
                    this.logger.log(Level.WARNING, " Exception in HandleiOSNativeAgent {0}", ex);
                }
            }
            isAppInTrash = new AppTrashModeHandler().isAppInTrash("com.manageengine.mdm.iosagent", customerId);
            this.logger.log(Level.INFO, "handleiOSNativeAgent:: Is App in Trash {0}", isAppInTrash);
            if (!isAppInTrash) {
                final Long appId = MDMAppMgmtHandler.getInstance().getIOSNativeAgentAppId(customerId);
                this.logger.log(Level.INFO, "handleiOSNativeAgent::Native ios app distributed for app is {0}", appId);
                this.distributeIOSNativeAgentwithEnrolledDevices(appId, customerId);
                this.logger.log(Level.INFO, "handleiOSNativeAgent::Native ios app details is distributed successfully ");
            }
        }
        else if (!isNativeAgentEnable && isAppExists) {
            this.logger.log(Level.INFO, "handleiOSNativeAgent::Native app details is disabled for the distributed devices");
            final Long appGroupId = MDMAppMgmtHandler.getInstance().getIOSNativeAgentAppGroupId(customerId);
            AppsUtil.getInstance().deleteAppResourceRelFromAppGroupId(appGroupId);
        }
    }
    
    public boolean isIOSNativeAgentInstalled(final Long deviceId) {
        boolean isIOSNativeInstalled = false;
        try {
            final Criteria deviceCri = new Criteria(Column.getColumn("IOSNativeAppStatus", "RESOURCE_ID"), (Object)deviceId, 0);
            final DataObject dObj = MDMUtil.getPersistence().get("IOSNativeAppStatus", deviceCri);
            if (!dObj.isEmpty()) {
                final int nativeInstallStatus = (int)dObj.getValue("IOSNativeAppStatus", "INSTALLATION_STATUS", deviceCri);
                if (nativeInstallStatus == 1) {
                    isIOSNativeInstalled = true;
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, " Exception in isIOSNativeAgentInstalled {0} ", ex);
        }
        return isIOSNativeInstalled;
    }
    
    public void addorUpdateIOSAgentInstallationStatus(final Long deviceId, final int installationStatus) {
        this.addorUpdateIOSAgentInstallationStatus(deviceId, installationStatus, true);
    }
    
    public void addorUpdateIOSAgentInstallationStatus(final Long deviceId, final int installationStatus, final boolean updateLastContact) {
        try {
            final Criteria deviceCri = new Criteria(Column.getColumn("IOSNativeAppStatus", "RESOURCE_ID"), (Object)deviceId, 0);
            final DataObject dObj = MDMUtil.getPersistence().get("IOSNativeAppStatus", deviceCri);
            Row iOSNativeRow = null;
            if (dObj.isEmpty()) {
                iOSNativeRow = new Row("IOSNativeAppStatus");
                iOSNativeRow.set("RESOURCE_ID", (Object)deviceId);
                iOSNativeRow.set("INSTALLATION_STATUS", (Object)installationStatus);
                iOSNativeRow.set("INSTALLED_TIME", (Object)System.currentTimeMillis());
                iOSNativeRow.set("LAST_CONTACTED_TIME", (Object)System.currentTimeMillis());
                dObj.addRow(iOSNativeRow);
                MDMUtil.getPersistence().add(dObj);
            }
            else {
                iOSNativeRow = dObj.getRow("IOSNativeAppStatus", deviceCri);
                iOSNativeRow.set("INSTALLATION_STATUS", (Object)installationStatus);
                if (updateLastContact) {
                    iOSNativeRow.set("LAST_CONTACTED_TIME", (Object)System.currentTimeMillis());
                }
                dObj.updateRow(iOSNativeRow);
                MDMUtil.getPersistence().update(dObj);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, " Exception in addorupdateIOSAgentInstallationStatus ", ex);
        }
    }
    
    public String generateEnrollmentId(final Long deviceId) {
        String authPassword = null;
        try {
            final SecureRandom random = new SecureRandom();
            authPassword = new BigInteger(32, random).toString(16);
            final Criteria authCri = new Criteria(Column.getColumn("IOSNativeAppAuthentication", "MANAGED_DEVICE_ID"), (Object)deviceId, 0);
            final DataObject authDO = MDMUtil.getPersistence().get("IOSNativeAppAuthentication", authCri);
            Row authRow = null;
            if (authDO.isEmpty()) {
                authRow = new Row("IOSNativeAppAuthentication");
                authRow.set("MANAGED_DEVICE_ID", (Object)deviceId);
                authRow.set("AUTH_CODE", (Object)authPassword);
                authDO.addRow(authRow);
                MDMUtil.getPersistence().add(authDO);
            }
            else {
                authRow = authDO.getFirstRow("IOSNativeAppAuthentication");
                authRow.set("AUTH_CODE", (Object)authPassword);
                authDO.updateRow(authRow);
                MDMUtil.getPersistence().update(authDO);
            }
            authPassword = (String)authDO.getFirstValue("IOSNativeAppAuthentication", "AUTH_CODE");
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, " Exception in generateEnrollmentId ", ex);
        }
        return authPassword;
    }
    
    private ArrayList getAllMEMDMApp() throws Exception {
        final ArrayList arrCollectionIDs = new ArrayList();
        final String baseTableName = "ConfigData";
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable(baseTableName));
        query.addJoin(new Join(baseTableName, "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        query.addJoin(new Join("ConfigDataItem", "CfgDataToCollection", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        query.addJoin(new Join("CfgDataToCollection", "CollectionMetaData", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        query.addJoin(new Join("ConfigDataItem", "InstallAppPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        query.addJoin(new Join("InstallAppPolicy", "MdPackage", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        query.addJoin(new Join("MdPackage", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        query.addJoin(new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        query.addSelectColumn(new Column("CollectionMetaData", "COLLECTION_ID"));
        query.addSelectColumn(new Column("CollectionMetaData", "COLLECTION_FILE_PATH"));
        query.setCriteria(new Criteria(new Column("MdAppGroupDetails", "IDENTIFIER"), (Object)"com.manageengine.mdm.iosagent", 0));
        DMDataSetWrapper ds = null;
        try {
            ds = DMDataSetWrapper.executeQuery((Object)query);
            while (ds.next()) {
                final Long collectionId = (Long)ds.getValue("COLLECTION_ID");
                final String collectionFilePath = (String)ds.getValue("COLLECTION_FILE_PATH");
                final HashMap collectionProps = new HashMap();
                collectionProps.put("COLLECTION_ID", collectionId);
                collectionProps.put("COLLECTION_FILE_PATH", collectionFilePath);
                arrCollectionIDs.add(collectionProps);
            }
        }
        catch (final Exception ex2) {
            throw ex2;
        }
        return arrCollectionIDs;
    }
    
    public void regenerateMEMDMAppInstallCommand() {
        try {
            final ArrayList arrCollectionIDsWithDuplicates = new ArrayList();
            arrCollectionIDsWithDuplicates.addAll(this.getAllMEMDMApp());
            this.logger.log(Level.INFO, "regenerateInstallApplication() - arrCollectionIDsWithDuplicates {0}", arrCollectionIDsWithDuplicates);
            final ArrayList arrCollectionIDs = new ArrayList((Collection<? extends E>)new HashSet<Object>(arrCollectionIDsWithDuplicates));
            this.logger.log(Level.INFO, "regenerateInstallApplication() - arrCollectionIDs {0}", arrCollectionIDs);
            final ArrayList regeneratedCollectionIDs = new ArrayList();
            final PayloadHandler payloadHdlr = PayloadHandler.getInstance();
            for (final HashMap collectionProps : arrCollectionIDs) {
                final Long collectionId = collectionProps.get("COLLECTION_ID");
                final String collectionFilePath = collectionProps.get("COLLECTION_FILE_PATH");
                final String profileFileName = ProfileUtil.getInstance().getProfileRepoParentDir() + collectionFilePath;
                payloadHdlr.generateInstallAppProfile(collectionId, profileFileName);
                regeneratedCollectionIDs.add(collectionId);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.INFO, "regenerateMEMDMAppInstallCommand : Exception ", e);
        }
    }
    
    public void addIosAgentAsync(final Long customerId, final Long userId) {
        this.logger.log(Level.INFO, "Inside addIosAgentAsync()");
        try {
            final HashMap taskInfoMap = new HashMap();
            final Properties properties = new Properties();
            ((Hashtable<String, Long>)properties).put("customerId", customerId);
            ((Hashtable<String, Long>)properties).put("userId", userId);
            taskInfoMap.put("taskName", "AddMemdmIosAppTask");
            taskInfoMap.put("schedulerTime", System.currentTimeMillis());
            taskInfoMap.put("poolName", "mdmPool");
            ApiFactoryProvider.getSchedulerAPI().executeAsynchronousWithDelay("com.me.mdm.server.apps.ios.task.AddMemdmIosAppTask", taskInfoMap, properties);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in IosNativeAppHandler addIosAgentAsync()", ex);
        }
    }
    
    static {
        IosNativeAppHandler.appHandler = null;
    }
}
