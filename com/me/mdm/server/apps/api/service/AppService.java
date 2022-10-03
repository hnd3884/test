package com.me.mdm.server.apps.api.service;

import java.util.Hashtable;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.mdm.server.msp.sync.SyncConfigurationListeners;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import java.util.Arrays;
import com.me.mdm.api.model.BaseAPIModel;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.sym.server.mdm.apps.ios.IOSModifiedEnterpriseAppsUtil;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.google.gson.JsonArray;
import com.me.mdm.server.apps.AppStatusRefreshHandler;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import com.me.mdm.server.onelinelogger.MDMOneLineLogger;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.mdm.server.apps.AppTrashModeHandler;
import java.util.logging.Level;
import com.me.mdm.server.apps.AppFacade;
import com.google.gson.Gson;
import com.me.mdm.server.apps.multiversion.AppVersionDBUtil;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import java.util.Properties;
import com.me.mdm.api.error.APIHTTPException;
import java.util.Collection;
import com.me.mdm.server.device.DeviceFacade;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import com.me.mdm.server.apps.api.model.AppAssociateDetailsModel;
import java.util.logging.Logger;

public class AppService
{
    protected static Logger logger;
    
    public void associateAppsToDevices(final AppAssociateDetailsModel appAssociateDetailsModel) throws Exception {
        final JSONObject secLog = new JSONObject();
        String remarks = "associate-failed";
        try {
            final Long deviceId = appAssociateDetailsModel.getDeviceId();
            final Long businessStoreID = appAssociateDetailsModel.getBusinessStoreID();
            JSONArray appDetailsArray = new JSONArray();
            List<Long> resourceList;
            if (deviceId != null) {
                resourceList = new ArrayList<Long>();
                resourceList.add(deviceId);
            }
            else {
                resourceList = appAssociateDetailsModel.getDeviceIds();
            }
            secLog.put((Object)"DEVICE_IDs", (Object)resourceList);
            final HashMap<Integer, ArrayList> platformDeviceMap = new DeviceFacade().validateIfDevicesExists(resourceList, appAssociateDetailsModel.getCustomerId());
            if (platformDeviceMap.size() > 1) {
                throw new APIHTTPException("COM0015", new Object[] { "Devices are not with the unique platform type" });
            }
            Properties pkgToBusinessStoreProps = new Properties();
            final Integer platformType = platformDeviceMap.keySet().iterator().next();
            final Long packageId = appAssociateDetailsModel.getAppId();
            Map<Long, Set<Long>> releaseLabelToPackageId = new HashMap<Long, Set<Long>>();
            if (packageId != null) {
                final Set<Long> packageIds = new HashSet<Long>();
                Long releaseLabelId = appAssociateDetailsModel.getLabelId();
                if (releaseLabelId.equals(null)) {
                    releaseLabelId = AppVersionDBUtil.getInstance().getProductionAppReleaseLabelIDForCustomer(appAssociateDetailsModel.getCustomerId());
                }
                packageIds.add(packageId);
                releaseLabelToPackageId.put(releaseLabelId, packageIds);
                final org.json.JSONObject appDetail = new org.json.JSONObject();
                appDetail.put("app_id", (Object)packageId);
                if (businessStoreID != null && businessStoreID != -1L) {
                    appDetail.put("businessstore_id", (Object)businessStoreID);
                }
                appDetailsArray.put((Object)appDetail);
            }
            else {
                final String json = new Gson().toJson((Object)appAssociateDetailsModel.getAppDetails());
                appDetailsArray = new JSONArray(json);
                releaseLabelToPackageId = new AppFacade().convertAppDetailsArrayToHashMap(appDetailsArray);
            }
            secLog.put((Object)"LABEL_TO_PACKAGE_IDs", (Object)releaseLabelToPackageId);
            pkgToBusinessStoreProps = new AppFacade().checkAndSetBusinessStoreIDsInRequest(appDetailsArray, appAssociateDetailsModel.getCustomerId());
            final Properties appToBusinessStoreProps = new AppFacade().getProfileToBusinessStoreMap(pkgToBusinessStoreProps, appAssociateDetailsModel.getCustomerId());
            final Map profileCollectionMap = new AppFacade().validateAndGetAppDetails(releaseLabelToPackageId, appAssociateDetailsModel.getCustomerId(), platformType);
            AppService.logger.log(Level.INFO, "associate the app to device, device ids:{0} and releaseLabel->AppIdListMap:{1}", new Object[] { resourceList, releaseLabelToPackageId });
            final Collection<Set<Long>> appSets = releaseLabelToPackageId.values();
            final List<Long> packagesCheckForTrash = new ArrayList<Long>();
            for (final Set<Long> apps : appSets) {
                packagesCheckForTrash.addAll(apps);
            }
            new AppTrashModeHandler().validateIfPackageInTrash(packagesCheckForTrash);
            final Properties properties = new Properties();
            ((Hashtable<String, Long>)properties).put("customerId", appAssociateDetailsModel.getCustomerId());
            final ObjectMapper objectMapper = new ObjectMapper();
            final org.json.JSONObject propObj = new org.json.JSONObject(objectMapper.writeValueAsString((Object)appAssociateDetailsModel));
            new AppFacade().setDeploymentConfigProperties(properties, propObj);
            ((Hashtable<String, Boolean>)properties).put("isAppConfig", true);
            ((Hashtable<String, Map>)properties).put("profileCollectionMap", profileCollectionMap);
            ((Hashtable<String, Boolean>)properties).put("profileOrigin", false);
            ((Hashtable<String, Integer>)properties).put("profileOriginInt", 120);
            ((Hashtable<String, List<Long>>)properties).put("resourceList", resourceList);
            ((Hashtable<String, Long>)properties).put("loggedOnUser", appAssociateDetailsModel.getUserId());
            ((Hashtable<String, String>)properties).put("loggedOnUserName", DMUserHandler.getDCUser(appAssociateDetailsModel.getLogInId()));
            ((Hashtable<String, Boolean>)properties).put("isGroup", Boolean.FALSE);
            if (Boolean.parseBoolean(properties.getProperty("isAppDowngrade"))) {
                secLog.put((Object)"IS_DOWNGRADE", (Object)true);
            }
            if (!appToBusinessStoreProps.isEmpty()) {
                ((Hashtable<String, Properties>)properties).put("profileToBusinessStore", appToBusinessStoreProps);
            }
            new AppFacade().updateDepPolicyAndAssociateAppToDevices(properties);
            remarks = "associate-success";
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "ASSOCIATE_APP", secLog);
        }
    }
    
    public void disassociateAppsfromDevices(final AppAssociateDetailsModel appAssociateDetailsModel) throws Exception {
        final JSONObject secLog = new JSONObject();
        String remarks = "dissociate-failed";
        try {
            final Long packageId = appAssociateDetailsModel.getAppId();
            List<Long> packageIds;
            if (packageId != null) {
                packageIds = new ArrayList<Long>();
                packageIds.add(packageId);
            }
            else {
                packageIds = appAssociateDetailsModel.getAppIds();
            }
            secLog.put((Object)"PACKAGE_IDs", (Object)packageIds);
            final Long deviceId = appAssociateDetailsModel.getDeviceId();
            List<Long> resourceList;
            if (deviceId != null) {
                resourceList = new ArrayList<Long>();
                resourceList.add(deviceId);
            }
            else {
                resourceList = appAssociateDetailsModel.getDeviceIds();
            }
            secLog.put((Object)"DEVICE_IDs", (Object)resourceList);
            AppService.logger.log(Level.INFO, "dissassociate the app from device device ids:{0} and app ids:{1}", new Object[] { resourceList, packageIds });
            new DeviceFacade().validateIfDevicesExists(resourceList, appAssociateDetailsModel.getCustomerId());
            final Map<Long, Long> profileCollectionMap = new AppFacade().validateAndAppDetailsForDevice(packageIds, resourceList, appAssociateDetailsModel.getCustomerId());
            final Properties properties = new Properties();
            ((Hashtable<String, Boolean>)properties).put("isAppConfig", true);
            ((Hashtable<String, Long>)properties).put("customerId", appAssociateDetailsModel.getCustomerId());
            ((Hashtable<String, Map<Long, Long>>)properties).put("profileCollectionMap", profileCollectionMap);
            ((Hashtable<String, Boolean>)properties).put("profileOrigin", false);
            ((Hashtable<String, Integer>)properties).put("profileOriginInt", 120);
            ((Hashtable<String, List<Long>>)properties).put("resourceList", resourceList);
            ((Hashtable<String, Long>)properties).put("loggedOnUser", appAssociateDetailsModel.getUserId());
            ((Hashtable<String, String>)properties).put("loggedOnUserName", DMUserHandler.getDCUser(appAssociateDetailsModel.getLogInId()));
            ProfileAssociateHandler.getInstance().disAssociateCollectionForResource(properties);
            remarks = "dissociate-success";
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "DISSOCIATE_APP", secLog);
        }
    }
    
    public void refreshAppStatusForDevice(final AppAssociateDetailsModel appAssociateDetailsModel) throws Exception {
        final Long customerId = appAssociateDetailsModel.getCustomerId();
        final Long deviceId = appAssociateDetailsModel.getDeviceId();
        final Integer status = appAssociateDetailsModel.getStatus();
        final JSONArray deviceids = new JSONArray();
        deviceids.put((Object)deviceId);
        final JsonArray appGroupIds = new Gson().toJsonTree((Object)appAssociateDetailsModel.getAppIds()).getAsJsonArray();
        final org.json.JSONObject appRefreshJSON = new org.json.JSONObject();
        appRefreshJSON.put("CUSTOMER_ID", (Object)customerId);
        appRefreshJSON.put("APP_IDS", (Object)appGroupIds);
        appRefreshJSON.put("DEVICE_IDS", (Object)deviceids);
        if (status != null) {
            appRefreshJSON.put("STATUS", (Object)status);
        }
        new AppStatusRefreshHandler().refreshAppStatusForResource(appRefreshJSON);
    }
    
    private void validateAppsForMovingToAllCustomers(final Long packageId) throws Exception {
        if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("SyncConfigurationsForAllCustomers") || !CustomerInfoUtil.getInstance().isMSP()) {
            throw new APIHTTPException("COM0015", new Object[0]);
        }
        final SelectQuery packageQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackage"));
        packageQuery.addJoin(new Join("MdPackage", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        packageQuery.addJoin(new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        packageQuery.addSelectColumn(Column.getColumn("MdPackage", "*"));
        packageQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "*"));
        packageQuery.setCriteria(new Criteria(Column.getColumn("MdPackage", "PACKAGE_ID"), (Object)packageId, 0));
        final DataObject dataObject = DataAccess.get(packageQuery);
        if (dataObject.isEmpty()) {
            throw new APIHTTPException("COM0008", new Object[] { "app_id" });
        }
        final Row packageRow = dataObject.getFirstRow("MdPackage");
        final Row mdAppGroupRow = dataObject.getFirstRow("MdAppGroupDetails");
        final int appSharedScope = (int)packageRow.get("APP_SHARED_SCOPE");
        final String identifier = (String)mdAppGroupRow.get("IDENTIFIER");
        final int platform = (int)packageRow.get("PLATFORM_TYPE");
        if (platform == 1 && MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AllowSameBundleIDStoreAndEnterpriseAppForIOS") && identifier.equalsIgnoreCase(IOSModifiedEnterpriseAppsUtil.getCustomBundleIDForEnterpriseApp(identifier))) {
            AppService.logger.log(Level.SEVERE, "This is a iOS modified enterprise app.Hence, it cannot be moved to all customers");
            throw new APIHTTPException("APP0039", new Object[0]);
        }
        final Boolean isForAllCustomers = appSharedScope == 1;
        if (isForAllCustomers) {
            AppService.logger.log(Level.SEVERE, "App already in global scope");
            throw new APIHTTPException("COM0015", new Object[0]);
        }
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackage"));
        selectQuery.addJoin(new Join("MdPackage", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppGroupDetails", "CustomerInfo", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 2));
        final Criteria appGroupCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)identifier, 0);
        final Criteria platformCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)platform, 0);
        selectQuery.setCriteria(appGroupCriteria.and(platformCriteria));
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
        while (dmDataSetWrapper.next()) {
            final int packageType = (int)dmDataSetWrapper.getValue("PACKAGE_TYPE");
            if (packageType != 2) {
                AppService.logger.log(Level.SEVERE, "App already present in customer {0} type {1}", new Object[] { dmDataSetWrapper.getValue("CUSTOMER_ID"), packageType });
                throw new APIHTTPException("APP0033", new Object[0]);
            }
        }
    }
    
    public void moveAppToAllCustomers(final BaseAPIModel apiModel, final Long appId) throws Exception {
        final Long customerId = apiModel.getCustomerId();
        this.validateAppsForMovingToAllCustomers(appId);
        AppsUtil.updateAppSharedScope(new ArrayList(Arrays.asList(appId)), 1);
        final org.json.JSONObject queueJSON = new org.json.JSONObject();
        queueJSON.put("CUSTOMER_ID", (Object)customerId);
        queueJSON.put("LOGIN_ID", (Object)apiModel.getLogInId());
        queueJSON.put("LAST_MODIFIED_BY", (Object)apiModel.getUserId());
        queueJSON.put("PROFILE_TYPE", 2);
        final int platformType = (int)DBUtil.getValueFromDB("MdPackage", "PACKAGE_ID", (Object)appId, "PLATFORM_TYPE");
        queueJSON.put("PLATFORM_TYPE", platformType);
        final String identifier = AppsUtil.getInstance().getAppIdentifierFromPackageId(appId);
        queueJSON.put("IDENTIFIER", (Object)identifier);
        queueJSON.put("app_id", (Object)appId);
        SyncConfigurationListeners.invokeListeners(queueJSON, 205);
        final int eventLogConstants = 2038;
        final String eventLogRemarks = "mdm.appmgmt.app.moved";
        final String eventLogRemarksArgs = AppsUtil.getInstance().getAppProfileName(identifier, customerId, platformType);
        MDMEventLogHandler.getInstance().MDMEventLogEntry(eventLogConstants, null, apiModel.getUserName(), eventLogRemarks, eventLogRemarksArgs, customerId);
    }
    
    static {
        AppService.logger = Logger.getLogger("MDMApiLogger");
    }
}
