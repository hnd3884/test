package com.adventnet.sym.webclient.mdm.config;

import java.util.Hashtable;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.sym.server.mdm.config.ResourceSummaryHandler;
import com.adventnet.sym.server.mdm.config.ProfileCertificateUtil;
import com.adventnet.ds.query.SortColumn;
import java.util.Properties;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.Set;
import java.util.TreeSet;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.i18n.I18N;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.server.profiles.ProfileException;
import com.me.mdm.server.config.MDMCollectionUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.Arrays;
import java.util.List;
import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import java.util.ArrayList;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.logger.DMSecurityLogger;
import com.me.mdm.server.config.MDMConfigUtil;
import com.me.mdm.webclient.formbean.MDMDefaultFormBean;
import com.me.mdm.server.payload.PayloadException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.HashMap;
import java.util.Map;
import com.adventnet.sym.server.mdm.config.ProfileHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public class ProfileConfigHandler
{
    public static Logger logger;
    private static final String COLLN_STATUS_UPDATE = "STATUS_UPDATE";
    public static Logger profileLogger;
    
    public static void addOrModifyProfileCollection(final JSONObject jsonObject) throws SyMException, Exception {
        final Long profileID = jsonObject.optLong("PROFILE_ID", -1L);
        if (profileID == null || profileID == -1L) {
            ProfileConfigHandler.logger.log(Level.INFO, "+++ Create new profile action initiated +++");
            addProfileCollection(jsonObject);
            ProfileConfigHandler.profileLogger.log(Level.INFO, "{0}\t\t{1}\t\t{2}\t\t{3}\t\t{4};", new Object[] { jsonObject.opt("PROFILE_ID"), jsonObject.opt("COLLECTION_ID"), (jsonObject.optInt("PROFILE_TYPE") == 1) ? "Profile" : "App", jsonObject.opt("PLATFORM_TYPE"), "PROFILE_CREATION" });
        }
        else {
            ProfileConfigHandler.logger.log(Level.INFO, "--- Modify profile action initiated ---");
            modifyProfileCollection(jsonObject);
            ProfileConfigHandler.profileLogger.log(Level.INFO, "{0}\t\t{1}\t\t{2}\t\t{3}\t\t{4};", new Object[] { jsonObject.opt("PROFILE_ID"), jsonObject.opt("COLLECTION_ID"), (jsonObject.optInt("PROFILE_TYPE") == 1) ? "Profile" : "App", jsonObject.opt("PLATFORM_TYPE"), "PROFILE_MODIFICATION" });
        }
    }
    
    public static void addProfileCollection(final JSONObject jsonObject) throws SyMException, Exception {
        Long createdUserID = jsonObject.optLong("CREATED_BY", -1L);
        if (createdUserID == -1L) {
            createdUserID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
        }
        jsonObject.put("CREATED_BY", (Object)createdUserID);
        final Long profileID = ProfileHandler.addOrUpdateProfile(jsonObject);
        jsonObject.put("PROFILE_ID", (Object)profileID);
        final Long collectionID = ProfileHandler.addOrUpdateProfileCollectionDO(jsonObject);
        ProfileConfigHandler.logger.log(Level.SEVERE, "AddOrUpdated ProfileToCollection: ProfileID: {0}, CollectionID: {1}", new Object[] { profileID, collectionID });
        jsonObject.put("COLLECTION_ID", (Object)collectionID);
        jsonObject.put("PROFILE_COLLECTION_STATUS", 1);
    }
    
    public static void modifyProfileCollection(final JSONObject jsonObject) throws SyMException, Exception {
        final Long clonedCollectionID = null;
        if (!jsonObject.has("LAST_MODIFIED_BY")) {
            final Long modifiedUserID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            jsonObject.put("LAST_MODIFIED_BY", (Object)modifiedUserID);
        }
        final Long profileID = ProfileHandler.addOrUpdateProfile(jsonObject);
        Long collectionID = ProfileHandler.getRecentProfileCollectionID(profileID);
        if (collectionID == null) {
            collectionID = ProfileHandler.addOrUpdateProfileCollectionDO(jsonObject);
            jsonObject.put("COLLECTION_ID", (Object)collectionID);
            jsonObject.put("PROFILE_COLLECTION_STATUS", 1);
        }
        else {
            final int collectionStatus = ProfileHandler.getRecentProfileCollectionStatus(profileID);
            jsonObject.put("COLLECTION_ID", (Object)collectionID);
            jsonObject.put("PROFILE_COLLECTION_STATUS", collectionStatus);
        }
        collectionID = (Long)jsonObject.get("COLLECTION_ID");
        final HashMap configuredStatus = getConfiguredStatus(collectionID);
        final JSONObject configuredStatusJSON = new JSONObject((Map)configuredStatus);
        jsonObject.put("configuredStatus", (Object)configuredStatusJSON);
    }
    
    public static void addOrModifyConfiguration(final JSONObject jsonObject) throws Exception {
        Long collectionID = null;
        String configName = null;
        Boolean isAppConfig = Boolean.FALSE;
        Long configDataItemID = null;
        int configID = -1;
        try {
            configName = (String)jsonObject.get("CURRENT_CONFIG");
            collectionID = (Long)jsonObject.get("COLLECTION_ID");
            isAppConfig = (Boolean)jsonObject.get("APP_CONFIG");
            final HashMap profileHash = MDMUtil.getInstance().getProfileDetailsForCollectionId(collectionID);
            final JSONObject configurationJSON = jsonObject.getJSONObject(configName);
            if (isAppConfig) {
                initAppPolicyConfigData(jsonObject);
            }
            configDataItemID = configurationJSON.optLong("CONFIG_DATA_ITEM_ID");
            configID = configurationJSON.optInt("CONFIG_ID");
            validateLicense(configID);
            if (profileHash.get("PROFILE_PAYLOAD_IDENTIFIER") != null) {
                final String payloadIdentifier = profileHash.get("PROFILE_PAYLOAD_IDENTIFIER") + "." + configurationJSON.get("CONFIG_DATA_IDENTIFIER");
                configurationJSON.put("PROFILE_PAYLOAD_IDENTIFIER", (Object)payloadIdentifier);
            }
            if (profileHash.get("PROFILE_NAME") != null) {
                jsonObject.put("PROFILE_NAME", profileHash.get("PROFILE_NAME"));
            }
            jsonObject.put(configName, (Object)configurationJSON);
            if (configDataItemID > 0L) {
                saveModifiedConfiguration(jsonObject);
                jsonObject.put("CONFIG_DATA_ITEM_ID", (Object)configDataItemID);
                ProfileConfigHandler.logger.log(Level.INFO, "Profile payload modified. Collection ID: {0}; Payload Name: {1}; ConfigDataItemID: {2}", new Object[] { collectionID, configName, configDataItemID });
                ProfileConfigHandler.profileLogger.log(Level.INFO, "ProfileId:{0}\t\tCollectionId:{1}\t\t{2}\t\tPlatform:{3}\t\t{4}\t\tConfigDataItemID: {5}; Payload Name: {6}", new Object[] { profileHash.get("PROFILE_ID"), collectionID, isAppConfig ? "App" : "Profile", profileHash.get("PLATFORM_TYPE"), "PAYLOAD_MODIFIED", configDataItemID, configName });
            }
            else {
                configDataItemID = addConfiguration(jsonObject);
                jsonObject.put("CONFIG_DATA_ITEM_ID", (Object)configDataItemID);
                ProfileConfigHandler.logger.log(Level.INFO, "Profile payload created. Collection ID: {0}; Payload Name: {1}; ConfigDataItemID: {2}", new Object[] { collectionID, configName, configDataItemID });
                ProfileConfigHandler.profileLogger.log(Level.INFO, "ProfileId:{0}\t\tCollectionId:{1}\t\t{2}\t\tPlatform:{3}\t\t{4}\t\tConfigDataItemID: {5}; Payload Name: {6}", new Object[] { profileHash.get("PROFILE_ID"), collectionID, isAppConfig ? "App" : "Profile", profileHash.get("PLATFORM_TYPE"), "PAYLOAD_ADDED", configDataItemID, configName });
            }
        }
        catch (final PayloadException ex) {
            throw ex;
        }
        catch (final Exception exp) {
            ProfileConfigHandler.logger.log(Level.SEVERE, "Exception while creating profile...", exp);
            throw exp;
        }
    }
    
    private static Long addConfiguration(final JSONObject jsonObject) throws DataAccessException, SyMException, PayloadException, Exception {
        String configName = null;
        configName = (String)jsonObject.opt("CURRENT_CONFIG");
        final Long collectionID = (Long)jsonObject.get("COLLECTION_ID");
        final JSONObject[] singleConfigForm = { jsonObject.optJSONObject(configName) };
        singleConfigForm[0].put("COLLECTION_ID", (Object)collectionID);
        final String beanName = String.valueOf(singleConfigForm[0].get("BEAN_NAME"));
        final Integer configID = (Integer)singleConfigForm[0].get("CONFIG_ID");
        final MDMDefaultFormBean formClass = (MDMDefaultFormBean)Class.forName(beanName).newInstance();
        DataObject configDO = MDMConfigUtil.getConfigDataDOByCollectionId(configID, collectionID);
        if (configDO.isEmpty()) {
            configDO = ProfileHandler.createConfigDataDO(singleConfigForm[0]);
        }
        configDO = formClass.getDataObject(jsonObject, singleConfigForm, configDO);
        DMSecurityLogger.info(ProfileConfigHandler.logger, "ProfileConfiHandler", "addConfiguration()", "DataObject tables before insert: {0}", (Object)configDO.getTableNames());
        configDO = MDMUtil.getPersistence().update(configDO);
        final Long configDataItemID = (Long)configDO.getFirstValue("ConfigDataItem", "CONFIG_DATA_ITEM_ID");
        return configDataItemID;
    }
    
    private static void validateLicense(final Integer configID) throws APIHTTPException {
        final List<Integer> uemOnlyPayloads = new ArrayList<Integer>() {
            {
                this.add(770);
                this.add(753);
                this.add(767);
            }
        };
        if (uemOnlyPayloads.contains(configID)) {
            final boolean isDcEnterpriseSetup = ProductUrlLoader.getInstance().getValue("productcode").equals("DCEE");
            final boolean isEndpointServiceEnabled = LicenseProvider.getInstance().isEndpointServiceEnabled();
            final String licenseType = LicenseProvider.getInstance().getLicenseType();
            final boolean isTrialCustomer = licenseType.equalsIgnoreCase("T");
            final boolean isFreeCustomer = licenseType.equalsIgnoreCase("F");
            final boolean isUemLicenseSatisfied = isEndpointServiceEnabled || isTrialCustomer || isFreeCustomer;
            if (isDcEnterpriseSetup && !isUemLicenseSatisfied) {
                ProfileConfigHandler.logger.log(Level.INFO, "License not valid for this payload");
                throw new APIHTTPException("ENR00107", new Object[0]);
            }
        }
    }
    
    private static void saveModifiedConfiguration(final JSONObject jsonObject) throws SyMException, Exception {
        final String configName = (String)jsonObject.get("CURRENT_CONFIG");
        final int configID = MDMConfigUtil.getConfigID(configName);
        final Boolean isAppConfig = false;
        final JSONObject[] singleJSONForm = { null };
        if (!isAppConfig) {
            singleJSONForm[0] = jsonObject.getJSONObject(configName);
        }
        else {
            singleJSONForm[0] = jsonObject;
        }
        final String beanName = String.valueOf(singleJSONForm[0].get("BEAN_NAME"));
        final MDMDefaultFormBean formClass = (MDMDefaultFormBean)Class.forName(beanName).newInstance();
        final Long configDataItemID = singleJSONForm[0].getLong("CONFIG_DATA_ITEM_ID");
        DataObject configDO = MDMConfigUtil.getConfigDataItemsDO(configID, configDataItemID);
        configDO = formClass.getModifiedDO(jsonObject, singleJSONForm, configDO);
        MDMUtil.getPersistence().update(configDO);
    }
    
    public static HashMap getConfiguredStatus(final Long collectionID) throws Exception {
        final HashMap configuredStatus = new HashMap();
        final List configDOList = MDMConfigUtil.getConfigurations(collectionID);
        for (int i = 0; i < configDOList.size(); ++i) {
            final DataObject configDOFromDB = configDOList.get(i);
            final String configName = (String)configDOFromDB.getFirstValue("ConfigData", "LABEL");
            configuredStatus.put(configName + "_COUNT", "true");
        }
        return configuredStatus;
    }
    
    public static Boolean deleteProfile(final String sProfileID, final Long customerId) throws Exception {
        Boolean successfullyDeleted = false;
        if (sProfileID != null) {
            final String[] sArrProfileID = sProfileID.split(",");
            successfullyDeleted = deleteProfilePermanently(Arrays.asList(sArrProfileID), customerId);
        }
        return successfullyDeleted;
    }
    
    public static DataObject cloneConfigurations(final Long collectionID, final Long clonedCollectionID) throws SyMException {
        DataObject finalClonedConfigDO = null;
        try {
            final List configDOList = MDMConfigUtil.getConfigurationDataItems(collectionID);
            final Map beanMap = getBeanNameHashMap();
            finalClonedConfigDO = MDMUtil.getPersistence().constructDataObject();
            DataObject cloneConfigDO = null;
            for (int i = 0; i < configDOList.size(); ++i) {
                final DataObject configDOFromDB = configDOList.get(i);
                final Integer configID = (Integer)configDOFromDB.getFirstValue("ConfigData", "CONFIG_ID");
                final String configName = (String)configDOFromDB.getFirstValue("ConfigData", "LABEL");
                String beanName = beanMap.get(configID);
                if (beanName == null) {
                    beanName = "com.me.mdm.webclient.formbean.MDMDefaultFormBean";
                }
                final Criteria criteria = new Criteria(Column.getColumn("ConfigData", "CONFIG_ID"), (Object)new Integer(configID), 0);
                final Object clonedConfigDataID = finalClonedConfigDO.getValue("ConfigData", "CONFIG_DATA_ID", criteria);
                if (clonedConfigDataID == null) {
                    final HashMap configDataMap = new HashMap();
                    configDataMap.put("CONFIG_ID", new Integer(configID));
                    configDataMap.put("CONFIG_NAME", configName);
                    configDataMap.put("COLLECTION_ID", clonedCollectionID);
                    cloneConfigDO = ProfileHandler.createConfigDataDO(configDataMap);
                    if (cloneConfigDO != null) {
                        finalClonedConfigDO.merge(cloneConfigDO);
                    }
                }
                final DataObject clonedProfileDetails = getProfileDetails(clonedCollectionID);
                finalClonedConfigDO.merge(clonedProfileDetails);
                final DataObject parentProfileDetails = getProfileDetails(collectionID);
                configDOFromDB.merge(parentProfileDetails);
                final MDMDefaultFormBean formBean = (MDMDefaultFormBean)Class.forName(beanName).newInstance();
                formBean.cloneConfigDO(configID, configDOFromDB, finalClonedConfigDO);
            }
            MDMUtil.getPersistence().update(finalClonedConfigDO);
        }
        catch (final Exception exp) {
            exp.printStackTrace();
            throw new SyMException(1002, exp.getCause());
        }
        return finalClonedConfigDO;
    }
    
    public static Long cloneConfigurationsOnModification(final JSONObject jsonObject) throws ProfileException {
        Long clonedCollectionID = null;
        try {
            final Long collectionID = (Long)jsonObject.get("COLLECTION_ID");
            final Long customerID = (Long)jsonObject.get("CUSTOMER_ID");
            if (!MDMCollectionUtil.isCustomerEligible(customerID, collectionID)) {
                throw new ProfileException();
            }
            final String currentConfigName = String.valueOf(jsonObject.get("CURRENT_CONFIG"));
            final JSONObject profileDetails = getProfileDetailsForCloning(collectionID, customerID);
            clonedCollectionID = ProfileHandler.addOrUpdateProfileCollectionDO(profileDetails);
            jsonObject.put("COLLECTION_ID", (Object)clonedCollectionID);
            final DataObject clonedCollnDo = cloneConfigurations(collectionID, clonedCollectionID);
            final Row configDataRow = clonedCollnDo.getRow("ConfigData", new Criteria(new Column("ConfigData", "LABEL"), jsonObject.get("CURRENT_CONFIG"), 0));
            if (configDataRow != null) {
                final Long configDataId = (Long)configDataRow.get("CONFIG_DATA_ID");
                jsonObject.put("CONFIG_DATA_ID", (Object)configDataId);
            }
            final JSONObject currentConfigJSON = jsonObject.has(currentConfigName) ? jsonObject.getJSONObject(currentConfigName) : new JSONObject();
            if (clonedCollnDo != null && !clonedCollnDo.isEmpty() && currentConfigJSON.has("CONFIG_DATA_ITEM_ID") && currentConfigJSON.optLong("CONFIG_DATA_ITEM_ID") > 0L) {
                final String payloadIdentifier = MDMConfigUtil.getConfigPayloadIdentifier(currentConfigJSON.get("CONFIG_DATA_ITEM_ID"));
                if (configDataRow != null) {
                    Criteria configDataIdCriteria = new Criteria(new Column("ConfigDataItem", "CONFIG_DATA_ID"), configDataRow.get("CONFIG_DATA_ID"), 0);
                    Object configDataItemId;
                    if (payloadIdentifier == null) {
                        final Row configDateItemRow = clonedCollnDo.getRow("ConfigDataItem", configDataIdCriteria);
                        configDataItemId = configDateItemRow.get("CONFIG_DATA_ITEM_ID");
                    }
                    else {
                        final Criteria configPayloadIdentifierCriteria = new Criteria(Column.getColumn("MdConfigDataItemExtn", "CONFIG_PAYLOAD_IDENTIFIER"), (Object)payloadIdentifier, 0);
                        final Iterator configDateItemExtnRows = clonedCollnDo.getRows("MdConfigDataItemExtn", configPayloadIdentifierCriteria);
                        final List configDataItemIds = DBUtil.getColumnValuesAsList(configDateItemExtnRows, "CONFIG_DATA_ITEM_ID");
                        final Criteria configDataItemIdCriteria = new Criteria(Column.getColumn("ConfigDataItem", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemIds.toArray(), 8);
                        configDataIdCriteria = configDataItemIdCriteria.and(configDataIdCriteria);
                        final Row configDateItemRow2 = clonedCollnDo.getRow("ConfigDataItem", configDataIdCriteria);
                        configDataItemId = configDateItemRow2.get("CONFIG_DATA_ITEM_ID");
                    }
                    jsonObject.put("CONFIG_DATA_ITEM_ID", (long)configDataItemId);
                    currentConfigJSON.put("CONFIG_DATA_ITEM_ID", (long)configDataItemId);
                    jsonObject.put(currentConfigName, (Object)currentConfigJSON);
                }
            }
        }
        catch (final ProfileException ex) {
            throw new ProfileException();
        }
        catch (final Exception ex2) {
            ProfileConfigHandler.logger.log(Level.SEVERE, " Exception while cloning profile during modification", ex2);
        }
        return clonedCollectionID;
    }
    
    public static JSONObject getProfileDetailsForCloning(final Long collectionID, final Long customerID) {
        final JSONObject profileDetails = new JSONObject();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Collection"));
            selectQuery.addJoin(new Join("Collection", "IOSCollectionPayload", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQuery.addJoin(new Join("Collection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQuery.addJoin(new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            selectQuery.setCriteria(new Criteria(new Column("Collection", "COLLECTION_ID"), (Object)collectionID, 0));
            selectQuery.addSelectColumn(new Column("Profile", "*"));
            selectQuery.addSelectColumn(new Column("IOSCollectionPayload", "*"));
            final DataObject collectionsDO = MDMUtil.getPersistence().get(selectQuery);
            final Row profileROw = collectionsDO.getFirstRow("Profile");
            final Row iOSCollnROw = collectionsDO.getFirstRow("IOSCollectionPayload");
            profileDetails.put("PROFILE_NAME", profileROw.get("PROFILE_NAME"));
            profileDetails.put("PROFILE_DESCRIPTION", profileROw.get("PROFILE_DESCRIPTION"));
            profileDetails.put("CUSTOMER_ID", (Object)customerID);
            profileDetails.put("PROFILE_ID", profileROw.get("PROFILE_ID"));
            profileDetails.put("SECURITY_TYPE", iOSCollnROw.get("SECURITY_TYPE"));
            profileDetails.put("PROFILE_PAYLOAD_IDENTIFIER", profileROw.get("PROFILE_PAYLOAD_IDENTIFIER"));
            profileDetails.put("SCOPE", profileROw.get("SCOPE"));
            profileDetails.put("PLATFORM_TYPE", profileROw.get("PLATFORM_TYPE"));
        }
        catch (final Exception ex) {
            ProfileConfigHandler.logger.log(Level.SEVERE, " Exception while cloning profile during modification", ex);
        }
        return profileDetails;
    }
    
    private static JSONObject getStatusMsg(final Long collectionID, final Integer configID, final String configName, final Long configDataItemID, final Boolean successfullyUpdated) {
        JSONObject responseObject = new JSONObject();
        try {
            if (configName != null) {
                if (successfullyUpdated) {
                    final Integer countMsg = 1;
                    responseObject.put("CONFIG_NAME", (Object)configName);
                    responseObject.put("CONFIG_DATA_ITEM_ID", (Object)String.valueOf(configDataItemID));
                    responseObject.put("COUNT_MSG", (Object)I18N.getMsg("dc.mdm.device_mgmt.configured", new Object[0]));
                }
                String successMsg = "mdm.profile.success_msg";
                String failureMsg = "mdm.profile.failure_msg";
                switch (configID) {
                    case 172:
                    case 757: {
                        successMsg = "dc.mdm.enroll.passcode_SUCCESS_MSG";
                        failureMsg = "dc.mdm.enroll.passcode_FAILURE_MSG";
                        break;
                    }
                    case 173: {
                        successMsg = "dc.mdm.device_mgmt.RESTRICTION_SUCCESS_MSG";
                        failureMsg = "dc.mdm.device_mgmt.RESTRICTION_FAILURE_MSG";
                        break;
                    }
                    case 174:
                    case 553: {
                        successMsg = "dc.mdm.device_mgmt.EMAIL_SUCCESS_MSG";
                        failureMsg = "dc.mdm.device_mgmt.EMAIL_FAILURE_MSG";
                        break;
                    }
                    case 175:
                    case 554: {
                        successMsg = "dc.mdm.device_mgmt.ACTIVESYNC_SUCCESS_MSG";
                        failureMsg = "dc.mdm.device_mgmt.ACTIVESYNC_FAILURE_MSG";
                        break;
                    }
                    case 176:
                    case 704:
                    case 766: {
                        successMsg = "dc.mdm.device_mgmt.VPN_SUCCESS_MSG";
                        failureMsg = "dc.mdm.device_mgmt.VPN_FAILURE_MSG";
                        break;
                    }
                    case 521: {
                        successMsg = "mdm.device_mgmt.PER_APP_VPN_SUCCESS_MSG";
                        failureMsg = "mdm.device_mgmt.PER_APP_VPN_FAILURE_MSG";
                        break;
                    }
                    case 177:
                    case 556:
                    case 605:
                    case 701:
                    case 774: {
                        successMsg = "dc.mdm.device_mgmt.WIFI_SUCCESS_MSG";
                        failureMsg = "dc.mdm.device_mgmt.WIFI_FAILURE_MSG";
                        break;
                    }
                    case 178: {
                        successMsg = "dc.mdm.device_mgmt.LDAP_SUCCESS_MSG";
                        failureMsg = "dc.mdm.device_mgmt.LDAP_FAILURE_MSG";
                        break;
                    }
                    case 179: {
                        successMsg = "dc.mdm.device_mgmt.CALDAV_SUCCESS_MSG";
                        failureMsg = "dc.mdm.device_mgmt.CALDAV_FAILURE_MSG";
                        break;
                    }
                    case 180: {
                        successMsg = "dc.mdm.device_mgmt.CALENDAR_SUCCESS_MSG";
                        failureMsg = "dc.mdm.device_mgmt.CALENDAR_FAILURE_MSG";
                        break;
                    }
                    case 181: {
                        successMsg = "dc.mdm.device_mgmt.CARDDAV_SUCCESS_MSG";
                        failureMsg = "dc.mdm.device_mgmt.CARDDAV_FAILURE_MSG";
                        break;
                    }
                    case 182:
                    case 560: {
                        successMsg = "dc.mdm.deviceMgmt.WEBCLIPS_SUCCESS_MSG";
                        failureMsg = "dc.mdm.device_mgmt.WEBCLIPS_FAILURE_MSG";
                        break;
                    }
                    case 188:
                    case 561:
                    case 707: {
                        successMsg = "dc.mdm.enroll.webfilter_SUCCESS_MSG";
                        failureMsg = "dc.mdm.enroll.webfilter_FAILURE_MSG";
                        break;
                    }
                    case 515:
                    case 555:
                    case 607:
                    case 703:
                    case 772: {
                        successMsg = "dc.mdm.device_mgmt.IOS_CERTIFICATE_SUCCESS_MSG";
                        failureMsg = "dc.mdm.device_mgmt.IOS_CERTIFICATE_FAILURE_MSG";
                        break;
                    }
                    case 516:
                    case 566:
                    case 606:
                    case 773: {
                        successMsg = "dc.mdm.enroll.ios_SCEP_SUCESS_MSG";
                        failureMsg = "dc.mdm.enroll.ios_SCEP_FAILURE_MSG";
                        break;
                    }
                    case 185: {
                        successMsg = "dc.mdm.enroll.passcode_SUCCESS_MSG";
                        failureMsg = "dc.mdm.enroll.passcode_FAILURE_MSG";
                        break;
                    }
                    case 186: {
                        successMsg = "dc.mdm.device_mgmt.RESTRICTION_SUCCESS_MSG";
                        failureMsg = "dc.mdm.device_mgmt.RESTRICTION_FAILURE_MSG";
                        break;
                    }
                    case 557:
                    case 705: {
                        successMsg = "dc.mdm.profile.android.kiosk.KIOSK_SUCCESS_MSG";
                        failureMsg = "dc.mdm.profile.android.kiosk.KIOSK_FAILURE_MSG";
                        break;
                    }
                    case 518:
                    case 558: {
                        successMsg = "dc.mdm.profile.android.wallpaper.WALLPAPER_SUCCESS_MSG";
                        failureMsg = "dc.mdm.profile.android.wallpaper.WALLPAPER_FAILURE_MSG";
                        break;
                    }
                    case 183: {
                        successMsg = "dc.mdm.device_mgmt.APPLOCK_SUCCESS_MSG";
                        failureMsg = "dc.mdm.device_mgmt.APPLOCK_FAILURE_MSG";
                        break;
                    }
                    case 184:
                    case 559:
                    case 768: {
                        successMsg = "dc.mdm.device_mgmt.GLOBAL_PROXY_SUCCESS_MSG";
                        failureMsg = "dc.mdm.device_mgmt.GLOBAL_PROXY_FAILURE_MSG";
                        break;
                    }
                    case 187:
                    case 562: {
                        successMsg = "dc.mdm.device_mgmt.APN_SUCCESS_MSG";
                        failureMsg = "dc.mdm.device_mgmt.APN_FAILURE_MSG";
                        break;
                    }
                    case 563: {
                        successMsg = "mdm.deviceMgmt.migration.success";
                        failureMsg = "mdm.deviceMgmt.migration.failure";
                        break;
                    }
                    case 601: {
                        successMsg = "dc.mdm.device_mgmt.WIN_PASSCODE_SUCCESS_MSG";
                        failureMsg = "dc.mdm.device_mgmt.WIN_PASSCODE_FAILURE_MSG";
                        break;
                    }
                    case 602: {
                        successMsg = "dc.mdm.device_mgmt.WIN_EMAIL_SUCCESS_MSG";
                        failureMsg = "dc.mdm.device_mgmt.WIN_EMAIL_FAILURE_MSG";
                        break;
                    }
                    case 603: {
                        successMsg = "dc.mdm.device_mgmt.WIN_EXCHANGE_SYNC_SUCCESS_MSG";
                        failureMsg = "dc.mdm.device_mgmt.WIN_EXCHANGE_SYNC_FAILURE_MSG";
                        break;
                    }
                    case 604: {
                        successMsg = "dc.mdm.device_mgmt.WIN_RESTRICTION_SUCCESS_MSG";
                        failureMsg = "dc.mdm.device_mgmt.WIN_RESTRICTION_FAILURE_MSG";
                        break;
                    }
                    case 517: {
                        successMsg = "dc.mdm.device_mgmt_ios_MANAGED_DOMAIN_SUCCESS_MSG";
                        failureMsg = "dc.mdm.device_mgmt_ios_MANAGED_DOMAIN_FAILURE_MSG";
                        break;
                    }
                    case 519:
                    case 769: {
                        successMsg = "mdm.device_mgmt_ios_Airprint_SUCCESS_MSG";
                        failureMsg = "mdm.device_mgmt_ios_Airprint_FAILURE_MSG";
                        break;
                    }
                    case 520: {
                        successMsg = "mdm.profile.ssoPolicy_Success_Msg";
                        failureMsg = "mdm.profile.ssoPolicy_Failure_Msg";
                        break;
                    }
                    case 564: {
                        successMsg = "dc.mdm.device_mgmt.VPN_SUCCESS_MSG";
                        failureMsg = "dc.mdm.device_mgmt.VPN_FAILURE_MSG";
                        break;
                    }
                    case 770: {
                        successMsg = "mdm.profile.filevault_Success_Msg";
                        failureMsg = "mdm.profile.filevault_Failure_Msg";
                        break;
                    }
                    case 771: {
                        responseObject = (successfullyUpdated ? getReponseMessage("1", I18N.getMsg("mdm.profile.directorybind_Success_Msg", new Object[0]), responseObject) : getReponseMessage("-1", I18N.getMsg("mdm.profile.directory_bind_failure_Message", new Object[0]), responseObject));
                        break;
                    }
                    case 702: {
                        successMsg = "mdm.profile.ethernet.sucess_msg";
                        failureMsg = "mdm.profile.ethernet.failure_msg";
                        break;
                    }
                    case 706:
                    case 710: {
                        successMsg = "dc.mdm.device_mgmt.RESTRICTION_SUCCESS_MSG";
                        failureMsg = "Failed to save Chrome Restrictions configuration";
                        break;
                    }
                    case 709: {
                        successMsg = "mdm.profile.bookmarks.sucess_msg";
                        failureMsg = "mdm.profile.bookmarks.failure_msg";
                        break;
                    }
                    case 708: {
                        successMsg = "mdm.profile.powerMgmt.sucess_msg";
                        failureMsg = "mdm.profile.powerMgmt.failure_msg";
                        break;
                    }
                    case 711: {
                        successMsg = "mdm.profile.verifyAccess.sucess_msg";
                        failureMsg = "mdm.profile.verifyAccess.failure_msg";
                        break;
                    }
                    case 712: {
                        successMsg = "mdm.profile.browser.sucess_msg";
                        failureMsg = "mdm.profile.browser.failure_msg";
                        break;
                    }
                    case 713: {
                        successMsg = "mdm.profile.application.success_msg";
                        failureMsg = "mdm.profile.application.failure_msg";
                        break;
                    }
                    case 714: {
                        successMsg = "mdm.profile.managedguestsession.success_msg";
                        failureMsg = "mdm.profile.managedguestsession.failure_msg";
                        break;
                    }
                    case 608: {
                        successMsg = "dc.mdm.device_mgmt.APPLOCK_SUCCESS_MSG";
                        failureMsg = "dc.mdm.device_mgmt.APPLOCK_FAILURE_MSG";
                        break;
                    }
                    case 522: {
                        successMsg = "mdm.profile.ios.assettag_Success_Msg";
                        failureMsg = "mdm.profile.ios.assettag_Failure_Msg";
                        break;
                    }
                    case 565: {
                        successMsg = "mdm.profile.EFRP_SUCCESS_MSG";
                        failureMsg = "mdm.profile.EFRP_FAILURE_MSG";
                        break;
                    }
                }
                successMsg = I18N.getMsg(successMsg, new Object[0]);
                failureMsg = I18N.getMsg(failureMsg, new Object[0]);
                final String message = successfullyUpdated ? successMsg : failureMsg;
                responseObject = getReponseMessage("1", message, responseObject);
                switch (configID) {
                    case 518:
                    case 558: {
                        getWallpaperInfo(configDataItemID, responseObject);
                        break;
                    }
                }
            }
            responseObject.put("COLLECTION_ID", (Object)String.valueOf(collectionID));
        }
        catch (final Exception e) {
            ProfileConfigHandler.logger.log(Level.SEVERE, "Exception in getting message for profile save", e);
        }
        return responseObject;
    }
    
    private static JSONObject getReponseMessage(final String statusCode, final String msg, final JSONObject props) throws Exception {
        props.put("ERROR_CODE", (Object)statusCode);
        props.put("MESSAGE", (Object)msg);
        return props;
    }
    
    private static JSONObject getWallpaperInfo(final Long configDataItemId, final JSONObject props) {
        try {
            final Row row = DBUtil.getRowFromDB("MDMWallpaperPolicy", "CONFIG_DATA_ITEM_ID", (Object)configDataItemId);
            if (row != null) {
                final String belowHDPIWall = (String)row.get("BELOW_HDPI_WALLPAPER_PATH");
                final String aboveHDPIWall = (String)row.get("ABOVE_HDPI_WALLPAPER_PATH");
                props.put("BELOW_HDPI_WALLPAPER_PATH", (Object)((belowHDPIWall != null) ? belowHDPIWall : ""));
                props.put("ABOVE_HDPI_WALLPAPER_PATH", (Object)((aboveHDPIWall != null) ? aboveHDPIWall : ""));
            }
        }
        catch (final Exception ex) {
            ProfileConfigHandler.logger.log(Level.SEVERE, "Exception in get wallpaper info", ex);
        }
        return props;
    }
    
    private static void setRequiredPayloadAttr(final HttpServletRequest request, final String configName) {
        if (configName.equals("ANDROID_WALLPAPER_POLICY")) {
            final String param = request.getParameter("isDnDSupport");
            request.setAttribute("isDnDSupport", (Object)(param != null && Boolean.valueOf(param)));
        }
        if (configName.equals("IOS_WALLPAPER_POLICY")) {
            final String param = request.getParameter("isDnDSupport");
            request.setAttribute("isDnDSupport", (Object)(param != null && Boolean.valueOf(param)));
        }
    }
    
    public static boolean deleteConfiguration(final Long collectionID, final String configName, final Long customerId) throws ProfileException {
        boolean isDeleted = false;
        if (collectionID != null) {
            if (!MDMCollectionUtil.isCustomerEligible(customerId, collectionID)) {
                throw new ProfileException();
            }
            try {
                final Integer configID = MDMConfigUtil.getConfigID(configName);
                final Long configDataID = getConfigDataID(collectionID, configID);
                MDMConfigUtil.deleteConfiguration(configDataID);
                isDeleted = true;
                final Map<String, Object> profileInfo = new ProfileHandler().getProfileInfoFromCollectionID(collectionID);
                if (!profileInfo.isEmpty()) {
                    ProfileConfigHandler.profileLogger.log(Level.INFO, "{0}\t\t{1}\t\tProfile\t\t{2}\t\tPAYLOAD_REMOVED\t\tConfig Name: {3}; ConfigID:{4}; ConfigDataID: {5}", new Object[] { profileInfo.get("PROFILE_ID"), collectionID, profileInfo.get("PLATFORM_TYPE"), configName, configID, configDataID });
                }
            }
            catch (final Exception ex) {
                ProfileConfigHandler.logger.log(Level.SEVERE, "Exception in deleteConfigDataItem ", ex);
            }
        }
        return isDeleted;
    }
    
    public static void deleteConfiguration(final Object[] profileIDArr, final Long customerId) throws DataAccessException {
        final SelectQuery configQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
        final Join profileToCustomerRel = new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
        final Join profileToCollectionJoin = new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, "Profile", "ProfileToCollection", 2);
        final Join collectionToConfigDataJoin = new Join("ProfileToCollection", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, "ProfileToCollection", "CfgDataToCollection", 2);
        configQuery.addJoin(profileToCollectionJoin);
        configQuery.addJoin(collectionToConfigDataJoin);
        configQuery.addJoin(profileToCustomerRel);
        final Criteria customerCriteria = new Criteria(new Column("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0);
        configQuery.addSelectColumn(Column.getColumn("CfgDataToCollection", "*"));
        configQuery.setCriteria(new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileIDArr, 8, (boolean)Boolean.FALSE).and(customerCriteria));
        final DataObject cfgdataObject = MDMUtil.getPersistence().get(configQuery);
        final Iterator iter = cfgdataObject.getRows("CfgDataToCollection");
        final Set<Long> collectionIdList = new TreeSet<Long>();
        final Set<Long> configDataIdList = new TreeSet<Long>();
        while (iter.hasNext()) {
            final Row cfgDataToCollectionRow = iter.next();
            collectionIdList.add((Long)cfgDataToCollectionRow.get("COLLECTION_ID"));
            configDataIdList.add((Long)cfgDataToCollectionRow.get("CONFIG_DATA_ID"));
        }
        final Criteria cfgDataDelCriteria = new Criteria(Column.getColumn("ConfigData", "CONFIG_DATA_ID"), (Object)configDataIdList.toArray(), 8, (boolean)Boolean.FALSE);
        ProfileConfigHandler.logger.log(Level.INFO, "Deleting from ConfigData table ConfigDataId''s: {0}", configDataIdList);
        MDMUtil.getPersistence().delete(cfgDataDelCriteria);
        final Criteria collectionDelCriteria = new Criteria(Column.getColumn("Collection", "COLLECTION_ID"), (Object)collectionIdList.toArray(), 8, (boolean)Boolean.FALSE);
        ProfileConfigHandler.logger.log(Level.INFO, "Deleting from Collection table CollecionId''s: {0}", collectionIdList);
        MDMUtil.getPersistence().delete(collectionDelCriteria);
    }
    
    public static void publishProfile(final JSONObject jsonObject) throws Exception {
        Long customerID = (Long)jsonObject.opt("CUSTOMER_ID");
        if (customerID == null) {
            customerID = CustomerInfoUtil.getInstance().getCustomerId();
        }
        final Long profileID = (Long)jsonObject.get("PROFILE_ID");
        final List<Long> profileIds = new ArrayList<Long>();
        profileIds.add(profileID);
        if (!ProfileUtil.getInstance().isCustomerEligible(customerID, profileIds)) {
            throw new ProfileException();
        }
        final Long collectionID = (Long)jsonObject.get("COLLECTION_ID");
        final int platformType = (int)jsonObject.get("PLATFORM_TYPE");
        final Boolean isAppPolicy = jsonObject.optBoolean("APP_CONFIG", false);
        ProfileConfigHandler.logger.log(Level.INFO, "Profile going to published. ProfileID : {0}; CollectionID : {1}; Platform : {2}; IsAppConfig : {3}", new Object[] { profileID, collectionID, platformType, isAppPolicy });
        final Properties properties = ProfileHandler.getCollectionProperties(collectionID);
        ((Hashtable<String, Long>)properties).put("CUSTOMER_ID", customerID);
        ((Hashtable<String, Long>)properties).put("PROFILE_ID", profileID);
        if (jsonObject.has("PROFILE_TYPE")) {
            ((Hashtable<String, Integer>)properties).put("PROFILE_TYPE", jsonObject.getInt("PROFILE_TYPE"));
        }
        ((Hashtable<String, Boolean>)properties).put("APP_CONFIG", isAppPolicy);
        ((Hashtable<String, Object>)properties).put("LAST_MODIFIED_BY", jsonObject.get("LAST_MODIFIED_BY"));
        if (isAppPolicy) {
            final Long appId = jsonObject.getLong("APP_ID");
            ((Hashtable<String, Long>)properties).put("APP_ID", appId);
            ((Hashtable<String, Boolean>)properties).put("addConfig", jsonObject.optBoolean("isNewVersionAppDetected", true));
            ((Hashtable<String, Boolean>)properties).put("hasAppConfiguration", jsonObject.optBoolean("HAS_APP_CONFIGURATION", false));
            ((Hashtable<String, Boolean>)properties).put("IS_MOVED_TO_TRASH", jsonObject.optBoolean("IS_MOVED_TO_TRASH", false));
        }
        else {
            final String payloadIdentifier = ProfileHandler.getProfileIdentifierFromProfileID(profileID);
            if (payloadIdentifier != null) {
                ((Hashtable<String, String>)properties).put("PROFILE_PAYLOAD_IDENTIFIER", payloadIdentifier);
            }
        }
        ((Hashtable<String, Integer>)properties).put("PLATFORM_TYPE", platformType);
        final Integer profileType = jsonObject.optInt("PROFILE_TYPE");
        if (profileType != null) {
            ((Hashtable<String, Integer>)properties).put("PROFILE_TYPE", profileType);
        }
        if (jsonObject.has("CREATION_TIME")) {
            ((Hashtable<String, Long>)properties).put("CREATION_TIME", jsonObject.getLong("CREATION_TIME"));
        }
        if (jsonObject.has("LAST_MODIFIED_TIME")) {
            ((Hashtable<String, Long>)properties).put("LAST_MODIFIED_TIME", jsonObject.getLong("LAST_MODIFIED_TIME"));
        }
        final MDMConfigRequestHandler crh = new MDMConfigRequestHandler();
        crh.handleRequest(1, properties);
    }
    
    public static List getConfigDataIds(final Long collectionId, final Integer configID) throws SyMException {
        final List configIdList = new ArrayList();
        try {
            final String baseTableName = "CfgDataToCollection";
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable(baseTableName));
            query.addJoin(new Join(baseTableName, "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            query.addSelectColumn(new Column(baseTableName, "*"));
            final SortColumn sortColumn = new SortColumn("CfgDataToCollection", "ORDER_OF_EXECUTION", true);
            query.addSortColumn(sortColumn);
            final Column col = Column.getColumn("CfgDataToCollection", "COLLECTION_ID");
            Criteria criteria = new Criteria(col, (Object)collectionId, 0);
            if (configID != null) {
                final Column typeCol = Column.getColumn("ConfigData", "CONFIG_ID");
                final Criteria typeCri = new Criteria(typeCol, (Object)configID, 0);
                criteria = criteria.and(typeCri);
            }
            query.setCriteria(criteria);
            final DataObject resultDO = MDMUtil.getPersistence().get(query);
            if (!resultDO.isEmpty()) {
                final Iterator rows = resultDO.getRows(baseTableName);
                while (rows.hasNext()) {
                    final Row row = rows.next();
                    configIdList.add(row.get("CONFIG_DATA_ID"));
                }
            }
        }
        catch (final DataAccessException ex) {
            throw new SyMException(1001, (Throwable)ex);
        }
        catch (final Exception ex2) {
            throw new SyMException(1001, (Throwable)ex2);
        }
        return configIdList;
    }
    
    private static Long getConfigDataID(final Long collectionID, final Integer configID) throws DataAccessException {
        final String baseTableName = "Collection";
        Long configDataID = null;
        if (collectionID != null && configID != null) {
            final Table baseTable = Table.getTable(baseTableName);
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(baseTable);
            query.addJoin(new Join(baseTableName, "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            query.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            query.addSelectColumn(new Column((String)null, "*"));
            final Column col = Column.getColumn("Collection", "COLLECTION_ID");
            Criteria criteria = new Criteria(col, (Object)collectionID, 0);
            final Column typeCol = Column.getColumn("ConfigData", "CONFIG_ID");
            final Criteria typeCri = new Criteria(typeCol, (Object)new Integer(configID), 0);
            criteria = criteria.and(typeCri);
            query.setCriteria(criteria);
            final DataObject resultDO = MDMUtil.getPersistence().get(query);
            if (!resultDO.isEmpty()) {
                configDataID = (Long)resultDO.getFirstValue("ConfigData", "CONFIG_DATA_ID");
            }
        }
        return configDataID;
    }
    
    public static int getConfigDataItemCount(final Long configDataID) throws Exception {
        int itemCount = 0;
        final Criteria criteria = new Criteria(Column.getColumn("ConfigDataItem", "CONFIG_DATA_ID"), (Object)configDataID, 0);
        itemCount = DBUtil.getRecordCount("ConfigDataItem", "CONFIG_DATA_ITEM_ID", criteria);
        return itemCount;
    }
    
    public static List getConfigDataItemIds(final Long configDataID) throws SyMException {
        final List configIdList = new ArrayList();
        try {
            final Criteria criteria = new Criteria(Column.getColumn("ConfigDataItem", "CONFIG_DATA_ID"), (Object)configDataID, 0);
            final DataObject resultDO = MDMUtil.getPersistence().get("ConfigDataItem", criteria);
            if (!resultDO.isEmpty()) {
                final Iterator rows = resultDO.getRows("ConfigDataItem");
                while (rows.hasNext()) {
                    final Row row = rows.next();
                    configIdList.add(row.get("CONFIG_DATA_ITEM_ID"));
                }
            }
        }
        catch (final DataAccessException ex) {
            throw new SyMException(1001, (Throwable)ex);
        }
        catch (final Exception ex2) {
            throw new SyMException(1001, (Throwable)ex2);
        }
        return configIdList;
    }
    
    public static DataObject getConfigDetailsForCollectionId(final Long collectionId) throws SyMException {
        DataObject resultDO = null;
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
            query.addJoin(new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            query.addJoin(new Join("ProfileToCollection", "CollectionStatus", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            query.addJoin(new Join("CollectionStatus", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 1));
            query.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 1));
            query.addJoin(new Join("ConfigData", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 1));
            query.addSelectColumn(Column.getColumn("ConfigData", "*"));
            query.addSelectColumn(Column.getColumn("ConfigDataItem", "*"));
            query.addSelectColumn(Column.getColumn("CollectionStatus", "COLLECTION_ID"));
            query.addSelectColumn(Column.getColumn("CollectionStatus", "PROFILE_COLLECTION_STATUS"));
            query.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
            query.addSelectColumn(Column.getColumn("Profile", "PLATFORM_TYPE"));
            final Criteria collectionIdCriteria = new Criteria(Column.getColumn("ProfileToCollection", "COLLECTION_ID"), (Object)collectionId, 0);
            query.setCriteria(collectionIdCriteria);
            resultDO = MDMUtil.getPersistence().get(query);
        }
        catch (final DataAccessException ex) {
            throw new SyMException(1001, (Throwable)ex);
        }
        catch (final Exception ex2) {
            throw new SyMException(1001, (Throwable)ex2);
        }
        return resultDO;
    }
    
    public static Map getBeanNameHashMap() {
        final Map beanMap = new HashMap();
        beanMap.put(176, "com.adventnet.sym.webclient.mdm.config.VPNPolicyFormBean");
        beanMap.put(766, "com.adventnet.sym.webclient.mdm.config.VPNPolicyFormBean");
        beanMap.put(188, "com.adventnet.sym.webclient.mdm.config.WebContentFilterPolicyFormBean");
        beanMap.put(758, "com.adventnet.sym.webclient.mdm.config.WebContentFilterPolicyFormBean");
        beanMap.put(561, "com.adventnet.sym.webclient.mdm.config.WebContentFilterPolicyFormBean");
        beanMap.put(707, "com.adventnet.sym.webclient.mdm.config.WebContentFilterPolicyFormBean");
        beanMap.put(517, "com.adventnet.sym.webclient.mdm.config.ManagedDomainFormBean");
        beanMap.put(520, "com.me.mdm.webclient.formbean.MDMSSOPolicyFormBean");
        beanMap.put(709, "com.adventnet.sym.webclient.mdm.config.WebContentFilterPolicyFormBean");
        beanMap.put(713, "com.adventnet.sym.webclient.mdm.config.WebContentFilterPolicyFormBean");
        beanMap.put(608, "com.adventnet.sym.webclient.mdm.config.formbean.WindowsKioskPolicyFormBean");
        beanMap.put(522, "com.me.mdm.webclient.formbean.MDMLockScreenMsgFormBean");
        beanMap.put(565, "com.adventnet.sym.webclient.mdm.config.WebContentFilterPolicyFormBean");
        beanMap.put(771, "com.me.mdm.webclient.formbean.DirectoryBindPolicyFormBean");
        beanMap.put(755, "com.me.mdm.webclient.formbean.MacSystemExtensionFormBean");
        beanMap.put(567, "com.me.mdm.webclient.formbean.MDMLockScreenMsgFormBean");
        beanMap.put(762, "com.me.mdm.webclient.formbean.MacLoginWindowItemsFormBean");
        beanMap.put(183, "com.adventnet.sym.webclient.mdm.config.formbean.AppLockPolicyFormBeanExtn");
        beanMap.put(557, "com.adventnet.sym.webclient.mdm.config.formbean.KioskAPIPolicyFormBeanExtn");
        beanMap.put(182, "com.adventnet.sym.webclient.mdm.config.formbean.WebClipsPolicyFormBean");
        beanMap.put(560, "com.adventnet.sym.webclient.mdm.config.formbean.WebClipsPolicyFormBean");
        beanMap.put(714, "com.adventnet.sym.webclient.mdm.config.formbean.ManagedGuestSessionFormBean");
        beanMap.put(528, "com.adventnet.sym.webclient.mdm.config.formbean.AppNotificationPolicyFormBean");
        beanMap.put(775, "com.adventnet.sym.webclient.mdm.config.formbean.AppNotificationPolicyFormBean");
        return beanMap;
    }
    
    private static void setCertificateList(final HttpServletRequest request, final String configName, final Long customerId, final Long configDataID) throws Exception {
        final HashMap certificateMap = ProfileCertificateUtil.getInstance().getCredentialCertificateList(customerId, configName, configDataID);
        if (certificateMap.containsKey("CredentialCertificateInfo")) {
            final ArrayList certificateList = certificateMap.get("CredentialCertificateInfo");
            request.setAttribute("certificateList", (Object)certificateList);
        }
        if (certificateMap.containsKey("SCEPConfigurations")) {
            final ArrayList scepConfigList = certificateMap.get("SCEPConfigurations");
            request.setAttribute("scepConfigList", (Object)scepConfigList);
        }
    }
    
    private static void setProfileScope(final HttpServletRequest request, final Long collectionID) {
        try {
            final HashMap profileDetails = MDMUtil.getInstance().getProfileDetailsForCollectionId(collectionID);
            request.setAttribute("profileDetails", (Object)profileDetails);
            request.setAttribute("scope", profileDetails.get("SCOPE"));
        }
        catch (final Exception ex) {
            ProfileConfigHandler.logger.log(Level.WARNING, "Exception occurred while setting profile", ex);
        }
    }
    
    public static void updateResSummary() {
        try {
            ResourceSummaryHandler.getInstance().updateResSummary(101);
        }
        catch (final Exception e) {
            ProfileConfigHandler.logger.log(Level.SEVERE, "Exception while update summary count ", e);
        }
        try {
            ResourceSummaryHandler.getInstance().updateResSummary(120);
        }
        catch (final Exception e) {
            ProfileConfigHandler.logger.log(Level.SEVERE, "Exception while update summary count ", e);
        }
        try {
            ResourceSummaryHandler.getInstance().updateResSummary(2);
        }
        catch (final Exception e) {
            ProfileConfigHandler.logger.log(Level.SEVERE, "Exception while update summary count ", e);
        }
    }
    
    private static void initAppPolicyConfigData(final JSONObject jsonObject) throws Exception {
        final String configName = jsonObject.optString("CONFIG_NAME", "APP_POLICY");
        final Long collectionID = jsonObject.getLong("COLLECTION_ID");
        Long configDataID = null;
        Long configDataItemID = 0L;
        final Integer configID = MDMConfigUtil.getConfigID(configName);
        configDataID = getConfigDataID(collectionID, configID);
        if (configDataID != null) {
            final List arrayList = getConfigDataItemIds(configDataID);
            if (arrayList.size() == 1) {
                configDataItemID = arrayList.get(0);
            }
        }
        jsonObject.put("CONFIG_DATA_ITEM_ID", (Object)configDataItemID);
        final JSONObject appPolicyForm = (JSONObject)jsonObject.get("APP_POLICY");
        appPolicyForm.put("CONFIG_DATA_ITEM_ID", (Object)configDataItemID);
    }
    
    public String getForwardURL(final String scope, final String defaultForwardURL) {
        String forwardURL = defaultForwardURL;
        if (scope != null) {
            forwardURL = (scope.equalsIgnoreCase("1") ? "KNOX_RESTRICTION_POLICY" : "ANDROID_RESTRICTIONS_POLICY");
        }
        return forwardURL;
    }
    
    public static Boolean deleteProfilePermanently(final List<String> profileId, final Long customerId) {
        Boolean deleteSuccess = true;
        try {
            final HashMap hashMap = AppsUtil.getInstance().getPackageIdsFromProfileIds(profileId);
            final List<Long> packageIds = hashMap.get("PackageIDs");
            final List<Long> appgrpIds = hashMap.get("AppGroupIDs");
            final Long packageCustomerId = hashMap.get("CustomerID");
            if (!packageIds.isEmpty()) {
                MDMAppMgmtHandler.getInstance().deletePackageDetails(packageIds.toArray(new Long[packageIds.size()]));
                MDMAppMgmtHandler.getInstance().deleteAppRepositoryFiles(packageCustomerId, packageIds.toArray(new Long[packageIds.size()]), appgrpIds.toArray(new Long[packageIds.size()]));
                MDMAppMgmtHandler.getInstance().deleteAppAssignableDetails(appgrpIds.toArray(new Long[packageIds.size()]));
                MDMAppMgmtHandler.getInstance().deleteUpdateConfFromApp(customerId);
            }
            deleteConfiguration(profileId.toArray(), customerId);
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("Profile");
            deleteQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            final Criteria deleteCriteria = new Criteria(new Column("Profile", "PROFILE_ID"), (Object)profileId.toArray(), 8).and(new Criteria(new Column("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0));
            deleteQuery.setCriteria(deleteCriteria);
            MDMUtil.getPersistenceLite().delete(deleteQuery);
            deleteSuccess = true;
            ProfileAssociateHandler.getInstance().updateDeviceProfileSummary();
            ProfileAssociateHandler.getInstance().updateGroupProfileSummary();
        }
        catch (final Exception e) {
            deleteSuccess = false;
            Logger.getLogger(ProfileConfigHandler.class.getName()).log(Level.SEVERE, null, e);
        }
        return deleteSuccess;
    }
    
    public static boolean deleteConfigurationDataItem(final Long collectionID, final JSONObject configDetails, final Long customerId) throws ProfileException {
        boolean isDeleted = false;
        if (collectionID != null) {
            if (!MDMCollectionUtil.isCustomerEligible(customerId, collectionID)) {
                throw new ProfileException();
            }
            try {
                final Long configDataItemId = configDetails.optLong("CONFIG_DATA_ITEM_ID");
                final Long configDataId = configDetails.optLong("CONFIG_DATA_ID");
                if (configDataId > 0L) {
                    final int count = getConfigDataItemCount(configDataId);
                    if (count == 1) {
                        MDMConfigUtil.deleteConfiguration(configDataId);
                    }
                    else if (configDataItemId != null) {
                        final Row row = new Row("ConfigDataItem");
                        row.set("CONFIG_DATA_ITEM_ID", (Object)configDataItemId);
                        DataAccess.delete(row);
                    }
                }
                else {
                    ProfileConfigHandler.logger.log(Level.WARNING, "ProfileConfigHandler -> deleteConfigurationDataItem -> configDataItemId  is NULL ");
                }
                isDeleted = true;
                final Map<String, Object> profileInfo = new ProfileHandler().getProfileInfoFromCollectionID(collectionID);
                if (!profileInfo.isEmpty()) {
                    ProfileConfigHandler.profileLogger.log(Level.INFO, "{0}\t\t{1}\t\tProfile\t\t{2}\t\tPAYLOAD_REMOVED\t\t; ConfigDataID:{3}", new Object[] { profileInfo.get("PROFILE_ID"), collectionID, profileInfo.get("PLATFORM_TYPE"), configDataItemId });
                }
            }
            catch (final Exception ex) {
                ProfileConfigHandler.logger.log(Level.SEVERE, "Exception in deleteConfigDataItem ", ex);
            }
        }
        return isDeleted;
    }
    
    public static DataObject getProfileDetails(final Long collectionId) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ProfileToCollection"));
        selectQuery.addJoin(new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        final Criteria cri = new Criteria(Column.getColumn("ProfileToCollection", "COLLECTION_ID"), (Object)collectionId, 0);
        selectQuery.setCriteria(cri);
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_PAYLOAD_IDENTIFIER"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_SHARED_SCOPE"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PLATFORM_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("ProfileToCustomerRel", "*"));
        selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "*"));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        return dataObject;
    }
    
    public static void makeProfileForAllCustomers(final Long profileId) throws DataAccessException {
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("Profile");
        updateQuery.setCriteria(new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileId, 0));
        updateQuery.setUpdateColumn("PROFILE_SHARED_SCOPE", (Object)1);
        MDMUtil.getPersistence().update(updateQuery);
    }
    
    static {
        ProfileConfigHandler.logger = Logger.getLogger("MDMConfigLogger");
        ProfileConfigHandler.profileLogger = Logger.getLogger("MDMProfileConfigLogger");
    }
}
