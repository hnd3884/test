package com.adventnet.sym.server.mdm.inv;

import java.util.Hashtable;
import java.util.Set;
import com.adventnet.ds.query.DMDataSetWrapper;
import java.util.HashSet;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import com.me.mdm.server.apps.AppTrashModeHandler;
import com.me.mdm.server.apps.IOSAppVersionChecker;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.persistence.ReadOnlyPersistence;
import com.adventnet.sym.server.mdm.util.MDMCommonConstants;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.util.Arrays;
import com.me.devicemanagement.framework.server.queue.DCQueue;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import com.adventnet.sym.server.mdm.queue.MDMDataQueueUtil;
import com.adventnet.sym.server.mdm.queue.QueueControllerHelper;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Map;
import java.util.Collection;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.Collections;
import java.util.Comparator;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.command.CommandUtil;
import com.me.mdm.agent.handlers.MacMDMAgentHandler;
import com.me.mdm.server.notification.NotificationHandler;
import com.adventnet.sym.server.mdm.util.MDMiOSEntrollmentUtil;
import com.adventnet.sym.server.mdm.apps.ios.IOSModifiedEnterpriseAppsUtil;
import com.dd.plist.NSNumber;
import com.dd.plist.NSString;
import com.dd.plist.NSDictionary;
import com.me.mdm.server.privacy.PrivacySettingsHandler;
import com.dd.plist.NSArray;
import org.json.JSONArray;
import java.util.Iterator;
import com.me.mdm.uem.ModernMgmtHandler;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.me.mdm.server.enrollment.MDMAgentUpdateHandler;
import com.adventnet.sym.server.mdm.iosnativeapp.IosNativeAppHandler;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.sym.server.mdm.apps.AppInstallationStatusHandler;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.me.mdm.server.windows.apps.WpCompanyHubAppHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.Properties;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.adventnet.persistence.DataObject;

public class AppDataHandler
{
    private DataObject finalDO;
    private DataObject existAppDO;
    private DataObject existResourceRelDO;
    private boolean isAllowed;
    private int appType;
    private final Logger blwlLogger;
    
    public AppDataHandler() {
        this.finalDO = null;
        this.existAppDO = null;
        this.existResourceRelDO = null;
        this.isAllowed = Boolean.TRUE;
        this.appType = 1;
        this.blwlLogger = Logger.getLogger("MDMAppMgmtLogger");
    }
    
    public List processWindowsSoftwares(final Long resourceID, final Long customerID, final JSONObject appDataJSON) {
        final List<Properties> arrayList = new ArrayList<Properties>();
        try {
            final Long entryTime = MDMUtil.getCurrentTimeInMillis();
            final JSONObject jsonObject = appDataJSON.getJSONObject("appListJson");
            final int appType = appDataJSON.optInt("appType", 1);
            this.isAllowed = this.getAppControlStatus(customerID);
            final Iterator keys = jsonObject.keys();
            final Long wpNativeAppId = WpCompanyHubAppHandler.getInstance().getWPCompanyHubAppId(customerID);
            final String wpNativeIdentifier = AppsUtil.getInstance().getAppIdentifier(wpNativeAppId);
            Long collectionId = null;
            Long appGroupId = null;
            String remarks = "";
            boolean isWPAgentInstalled = false;
            Boolean installUEMAgent = Boolean.FALSE;
            final MDMUtil mdmUtil = MDMUtil.getInstance();
            final AppInstallationStatusHandler handler = new AppInstallationStatusHandler();
            while (keys.hasNext()) {
                final JSONObject productJSON = jsonObject.getJSONObject((String)keys.next());
                final String identifier = ((String)productJSON.get("Identifier")).toString();
                final String packageIdentifier = productJSON.optString("packageIdentifier", (String)null);
                final String version = ((String)productJSON.get("Version")).trim();
                final String appName = ((String)productJSON.get("Name")).toString();
                final String isModernApp = productJSON.optString("IsModernApp", Boolean.TRUE.toString());
                if (!Boolean.parseBoolean(isModernApp)) {
                    if (packageIdentifier != null && packageIdentifier.equalsIgnoreCase("6AD2231F-FF48-4D59-AC26-405AFAE23DB7") && MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AutoReinstallWinDCAgent")) {
                        installUEMAgent = Boolean.TRUE;
                    }
                    if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("WinMSIAppsSupport")) {
                        continue;
                    }
                }
                final Properties appRepMap = new Properties();
                ((Hashtable<String, String>)appRepMap).put("APP_NAME", appName);
                ((Hashtable<String, String>)appRepMap).put("APP_VERSION", version);
                ((Hashtable<String, String>)appRepMap).put("APP_NAME_SHORT_VERSION", version);
                ((Hashtable<String, String>)appRepMap).put("IDENTIFIER", identifier);
                if (packageIdentifier != null) {
                    ((Hashtable<String, String>)appRepMap).put("packageIdentifier", packageIdentifier);
                }
                ((Hashtable<String, Long>)appRepMap).put("CUSTOMER_ID", customerID);
                ((Hashtable<String, Long>)appRepMap).put("DYNAMIC_SIZE", 0L);
                ((Hashtable<String, Long>)appRepMap).put("BUNDLE_SIZE", 0L);
                ((Hashtable<String, Long>)appRepMap).put("RESOURCE_ID", resourceID);
                ((Hashtable<String, Integer>)appRepMap).put("APP_TYPE", 0);
                ((Hashtable<String, Boolean>)appRepMap).put("NOTIFY_ADMIN", Boolean.TRUE);
                ((Hashtable<String, Integer>)appRepMap).put("PLATFORM_TYPE", 3);
                ((Hashtable<String, String>)appRepMap).put("IS_MODERN_APP", isModernApp);
                ((Hashtable<String, Integer>)appRepMap).put("USER_INSTALLED_APPS", appType);
                arrayList.add(appRepMap);
                if (appType == 1) {
                    if (identifier.equalsIgnoreCase(wpNativeIdentifier) || identifier.equalsIgnoreCase("ZohoCorp.ManageEngineMDM_hfrrf6a1akhx2")) {
                        isWPAgentInstalled = true;
                        IosNativeAppHandler.getInstance().addorUpdateIOSAgentInstallationStatus(resourceID, 1, false);
                        MDMAgentUpdateHandler.getInstance().updateAppAgentVersion(resourceID, version, null);
                        if (wpNativeAppId == null) {
                            continue;
                        }
                        collectionId = mdmUtil.getCollectionIDfromAppID(wpNativeAppId);
                        appGroupId = mdmUtil.getAppGroupIDFromCollection(collectionId);
                        remarks = "dc.db.mdm.collection.Successfully_installed_the_app";
                        MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, String.valueOf(collectionId), 6, remarks);
                        handler.updateAppInstallationDetailsFromDevice(resourceID, appGroupId, wpNativeAppId, 2, remarks, 0);
                    }
                    else {
                        if (!identifier.equalsIgnoreCase("551ab9a7-413b-4b79-8142-74550af0c72e") && !identifier.equalsIgnoreCase("ZohoCorp.ManageEngineMDM_hfrrf6a1akhx2")) {
                            continue;
                        }
                        isWPAgentInstalled = true;
                        IosNativeAppHandler.getInstance().addorUpdateIOSAgentInstallationStatus(resourceID, 1, false);
                        MDMAgentUpdateHandler.getInstance().updateAppAgentVersion(resourceID, version, null);
                    }
                }
            }
            this.processSoftwares(resourceID, arrayList, customerID, 3, 0, appType);
            if (wpNativeAppId != null && !isWPAgentInstalled) {
                collectionId = mdmUtil.getCollectionIDfromAppID(wpNativeAppId);
                appGroupId = mdmUtil.getAppGroupIDFromCollection(collectionId);
                remarks = "dc.db.mdm.apps.status.ManagedButUninstalled";
                handler.updateAppInstallationDetailsFromDevice(resourceID, appGroupId, null, 0, remarks, 0);
                MDMAgentUpdateHandler.getInstance().updateAppAgentVersion(resourceID, "--", null);
            }
            final Long exitTime = MDMUtil.getCurrentTimeInMillis();
            if (!installUEMAgent) {
                AppsUtil.logger.log(Level.INFO, "Going to install DC agent on the machine as it is not present. For resource {0}", resourceID);
                final List resourceList = new ArrayList();
                resourceList.add(resourceID);
                ModernMgmtHandler.getInstance(3).addAgentInstallationCommand(resourceList, customerID);
            }
            AppsUtil.logger.log(Level.INFO, "Time taken to process InstalledApplicationList for windows is: {0}", String.valueOf(exitTime - entryTime));
        }
        catch (final Exception exp) {
            AppsUtil.logger.log(Level.SEVERE, "Exception in processWindowsSoftwares ", exp);
        }
        return arrayList;
    }
    
    public List processAndroidSoftwares(final Long resourceID, final Long customerID, final JSONArray jsonArray) {
        return this.processAndroidSoftwares(resourceID, customerID, jsonArray, 0, 1);
    }
    
    public List processAndroidSoftwares(final Long resourceID, final Long customerID, final JSONArray jsonArray, final int scope, final int appType) {
        final List<Properties> arrayList = new ArrayList<Properties>();
        try {
            final Long entryTime = MDMUtil.getCurrentTimeInMillis();
            this.isAllowed = this.getAppControlStatus(customerID);
            final JSONArray jsonSoftwareArray = jsonArray;
            for (int size = jsonSoftwareArray.length(), i = 0; i < size; ++i) {
                final JSONObject jsonObject = (JSONObject)jsonSoftwareArray.get(i);
                String appName = (String)jsonObject.get("appname");
                if (appName == null) {
                    appName = "--";
                }
                final String identifier = (String)jsonObject.get("packageName");
                final String versionName = ((String)jsonObject.get("VersionName")).trim();
                final String versionCode = jsonObject.optString("VersionCode", "--").trim();
                final Properties appRepMap = new Properties();
                ((Hashtable<String, String>)appRepMap).put("APP_NAME", appName);
                ((Hashtable<String, String>)appRepMap).put("APP_VERSION", versionName);
                ((Hashtable<String, String>)appRepMap).put("APP_NAME_SHORT_VERSION", versionCode);
                ((Hashtable<String, String>)appRepMap).put("IDENTIFIER", identifier);
                ((Hashtable<String, Long>)appRepMap).put("CUSTOMER_ID", customerID);
                ((Hashtable<String, Long>)appRepMap).put("DYNAMIC_SIZE", 0L);
                ((Hashtable<String, Long>)appRepMap).put("BUNDLE_SIZE", 0L);
                ((Hashtable<String, Long>)appRepMap).put("RESOURCE_ID", resourceID);
                ((Hashtable<String, Integer>)appRepMap).put("APP_TYPE", 0);
                ((Hashtable<String, Boolean>)appRepMap).put("NOTIFY_ADMIN", Boolean.TRUE);
                ((Hashtable<String, Integer>)appRepMap).put("PLATFORM_TYPE", 2);
                ((Hashtable<String, Integer>)appRepMap).put("USER_INSTALLED_APPS", appType);
                arrayList.add(appRepMap);
            }
            this.processSoftwares(resourceID, arrayList, customerID, 2, scope, appType);
            final Long exitTime = MDMUtil.getCurrentTimeInMillis();
            AppsUtil.logger.log(Level.INFO, "Time taken to process InstalledApplicationList for android is: {0}", String.valueOf(exitTime - entryTime));
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
        return arrayList;
    }
    
    public List processIOSSoftwares(final Long resourceID, final Long customerID, final NSArray nsaray, final Boolean isFetchAgentOnly) {
        AppsUtil.logger.log(Level.INFO, "Inside processIOSSoftwares..{0}", nsaray.count());
        final List<Properties> arrayList = new ArrayList<Properties>();
        try {
            final HashMap privacyJson = new PrivacySettingsHandler().getPrivacySettingsForMdDevices(resourceID);
            final Boolean doNotFetchUserApp = Integer.parseInt(privacyJson.get("fetch_installed_app").toString()) == 2;
            final Boolean retryAgentInstallReqd = !doNotFetchUserApp || isFetchAgentOnly;
            final Boolean isMacDevice = MDMUtil.getInstance().isMacDevice(resourceID);
            this.isAllowed = this.getAppControlStatus(customerID);
            final NSArray nsarray = nsaray;
            final int length = nsarray.count();
            boolean isNativeAgentInstalled = false;
            Boolean isUEMAgentInstalled = Boolean.FALSE;
            final ArrayList<String> localDuplicatesCheckList = new ArrayList<String>();
            for (int i = 0; i < length; ++i) {
                String appName = "--";
                try {
                    final NSDictionary softwareDict = (NSDictionary)nsarray.objectAtIndex(i);
                    final NSString nsApp = (NSString)softwareDict.objectForKey("Name");
                    if (nsApp != null) {
                        appName = nsApp.toString();
                    }
                    final NSString nsIdentifier = (NSString)softwareDict.objectForKey("Identifier");
                    String identifier = "--";
                    if (nsIdentifier != null) {
                        identifier = nsIdentifier.toString();
                    }
                    String version = null;
                    String sVersion = "--";
                    final NSString nsVersion = (NSString)softwareDict.objectForKey("Version");
                    if (nsVersion != null) {
                        version = (sVersion = nsVersion.toString().trim());
                    }
                    String shortVersion = null;
                    final NSString NsShortVersion = (NSString)softwareDict.objectForKey("ShortVersion");
                    if (NsShortVersion != null) {
                        shortVersion = NsShortVersion.toString().trim();
                    }
                    if (shortVersion == null) {
                        shortVersion = sVersion;
                    }
                    Long dynamicSize = 0L;
                    final NSNumber NSdynamicSize = (NSNumber)softwareDict.objectForKey("DynamicSize");
                    if (NSdynamicSize != null) {
                        dynamicSize = Long.parseLong(NSdynamicSize.toString());
                    }
                    Long bundleSize = -1L;
                    final NSNumber NSbundleSize = (NSNumber)softwareDict.objectForKey("BundleSize");
                    if (NSbundleSize != null) {
                        bundleSize = Long.parseLong(NSbundleSize.toString());
                    }
                    Long externalAppVersionId = null;
                    final NSNumber externalVersionID = (NSNumber)softwareDict.objectForKey("ExternalVersionIdentifier");
                    if (externalVersionID != null) {
                        externalAppVersionId = Long.parseLong(externalVersionID.toString());
                    }
                    Boolean hasUpdateAvailable = false;
                    final NSNumber NSHasUpdateAvailable = (NSNumber)softwareDict.objectForKey("HasUpdateAvailable");
                    if (NSHasUpdateAvailable != null) {
                        hasUpdateAvailable = NSHasUpdateAvailable.boolValue();
                    }
                    if (!identifier.equals("--") && !localDuplicatesCheckList.contains(identifier)) {
                        localDuplicatesCheckList.add(identifier);
                        final Properties appRepMap = new Properties();
                        ((Hashtable<String, String>)appRepMap).put("APP_NAME", appName);
                        ((Hashtable<String, String>)appRepMap).put("IDENTIFIER", identifier);
                        ((Hashtable<String, Long>)appRepMap).put("BUNDLE_SIZE", bundleSize);
                        ((Hashtable<String, Long>)appRepMap).put("RESOURCE_ID", resourceID);
                        ((Hashtable<String, Long>)appRepMap).put("DYNAMIC_SIZE", dynamicSize);
                        ((Hashtable<String, Long>)appRepMap).put("CUSTOMER_ID", customerID);
                        ((Hashtable<String, Integer>)appRepMap).put("APP_TYPE", 0);
                        ((Hashtable<String, Boolean>)appRepMap).put("NOTIFY_ADMIN", Boolean.TRUE);
                        ((Hashtable<String, Integer>)appRepMap).put("PLATFORM_TYPE", 1);
                        ((Hashtable<String, Long>)appRepMap).put("EXTERNAL_APP_VERSION_ID", externalAppVersionId);
                        ((Hashtable<String, Boolean>)appRepMap).put("HAS_UPDATE_AVAILABLE", hasUpdateAvailable);
                        if (externalVersionID != null && externalVersionID.stringValue().equals("0")) {
                            ((Hashtable<String, Integer>)appRepMap).put("PACKAGE_TYPE", 2);
                            ((Hashtable<String, String>)appRepMap).put("APP_VERSION", sVersion);
                            ((Hashtable<String, String>)appRepMap).put("APP_NAME_SHORT_VERSION", shortVersion);
                            AppsUtil.logger.log(Level.INFO, "Enterprise App , changing App Version and App Group version :{0} resourceID:{1}", new Object[] { identifier, resourceID });
                        }
                        else {
                            ((Hashtable<String, String>)appRepMap).put("APP_VERSION", shortVersion);
                            ((Hashtable<String, String>)appRepMap).put("APP_NAME_SHORT_VERSION", sVersion);
                        }
                        if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AllowSameBundleIDStoreAndEnterpriseAppForIOS")) {
                            final Properties appDistributedProps = IOSModifiedEnterpriseAppsUtil.getDistributedAppProps(identifier, customerID, resourceID);
                            final boolean isAppAvailableWithSuffix = ((Hashtable<K, Boolean>)appDistributedProps).get("isAppAvailableWithSuffix");
                            final boolean isActualIdentifierAvailable = ((Hashtable<K, Boolean>)appDistributedProps).get("isActualIdentifierAvailable");
                            if (isAppAvailableWithSuffix) {
                                final String customSuffixIdentifier = IOSModifiedEnterpriseAppsUtil.getCustomBundleIDForEnterpriseApp(identifier);
                                if (isActualIdentifierAvailable) {
                                    AppsUtil.logger.log(Level.INFO, "Enterprise App: {0} Enterprise(CustomSuffixApp) available in App Catalog Of Device: {1} along with store app- {2}. Hence, adding in arrayList for processing InstalledApplicationList", new Object[] { customSuffixIdentifier, resourceID, identifier });
                                    final Properties tempAppRepMap = (Properties)appRepMap.clone();
                                    ((Hashtable<String, String>)tempAppRepMap).put("IDENTIFIER", customSuffixIdentifier);
                                    arrayList.add(tempAppRepMap);
                                }
                                else {
                                    AppsUtil.logger.log(Level.INFO, "Enterprise App: {0}(CustomSuffixApp) is only distributed to the App Catalog of the device: {1}. Hence, adding in arrayList for processing InstalledApplicationList", new Object[] { customSuffixIdentifier, resourceID });
                                    ((Hashtable<String, String>)appRepMap).put("IDENTIFIER", customSuffixIdentifier);
                                }
                            }
                        }
                        arrayList.add(appRepMap);
                        if (identifier.equalsIgnoreCase("com.manageengine.mdm.iosagent") || identifier.equalsIgnoreCase("com.manageengine.mdm.mac")) {
                            isNativeAgentInstalled = true;
                            if (identifier.equalsIgnoreCase("com.manageengine.mdm.mac")) {
                                version = version.replace(".", "");
                            }
                            MDMAgentUpdateHandler.getInstance().updateAppAgentVersion(resourceID, shortVersion, version);
                        }
                        if (identifier.equalsIgnoreCase("dcagentservice")) {
                            isUEMAgentInstalled = Boolean.TRUE;
                        }
                    }
                }
                catch (final Exception e) {
                    AppsUtil.logger.log(Level.SEVERE, "Exception while single item processIOSSoftware() " + appName, e);
                }
            }
            this.processSoftwares(resourceID, arrayList, customerID, 1, 0, 1);
            final boolean isWebClipAppCatalogInstalled = MDMiOSEntrollmentUtil.getInstance().isIOSWebClipAppCatalogInstalled(resourceID);
            final boolean isIOSNativeAppEnrolled = IosNativeAppHandler.getInstance().isIOSNativeAgentInstalled(resourceID);
            if (isMacDevice && retryAgentInstallReqd) {
                final List<Long> resourceList = new ArrayList<Long>() {
                    {
                        this.add(resourceID);
                    }
                };
                if (!isUEMAgentInstalled) {
                    AppsUtil.logger.log(Level.INFO, "Missing macOS UEM agent hence trying to distribute the agent: " + resourceID);
                    ModernMgmtHandler.getInstance(1).addAgentInstallationCommand(resourceList, customerID);
                    NotificationHandler.getInstance().SendNotification(resourceList, 1);
                }
                if (!isNativeAgentInstalled) {
                    AppsUtil.logger.log(Level.INFO, "Missing macOS MDM agent hence trying to distribute the agent: " + resourceID);
                    new MacMDMAgentHandler().checkAndinstallMacAgentForDevice(resourceID, customerID);
                    NotificationHandler.getInstance().SendNotification(resourceList, 1);
                }
            }
            if (!isMacDevice) {
                if (!isNativeAgentInstalled) {
                    IosNativeAppHandler.getInstance().addorUpdateIOSAgentInstallationStatus(resourceID, 0);
                    if (!isWebClipAppCatalogInstalled) {
                        CommandUtil.getInstance().installAppCatalogWebclip(resourceID);
                    }
                }
                else if (!isIOSNativeAppEnrolled && !isWebClipAppCatalogInstalled) {
                    CommandUtil.getInstance().installAppCatalogWebclip(resourceID);
                }
            }
        }
        catch (final Exception ex) {
            AppsUtil.logger.log(Level.SEVERE, "Exception while processIOSSoftware() ", ex);
        }
        return arrayList;
    }
    
    private JSONObject getAppPropertyInJsonFormat(final Properties property) throws Exception {
        final JSONObject appRepMap = new JSONObject();
        appRepMap.put("APP_NAME", ((Hashtable<K, Object>)property).get("APP_NAME"));
        appRepMap.put("APP_VERSION", ((Hashtable<K, Object>)property).get("APP_VERSION"));
        appRepMap.put("APP_NAME_SHORT_VERSION", ((Hashtable<K, Object>)property).get("APP_NAME_SHORT_VERSION"));
        appRepMap.put("IDENTIFIER", ((Hashtable<K, Object>)property).get("IDENTIFIER"));
        appRepMap.put("BUNDLE_SIZE", ((Hashtable<K, Object>)property).get("BUNDLE_SIZE"));
        appRepMap.put("RESOURCE_ID", ((Hashtable<K, Object>)property).get("RESOURCE_ID"));
        appRepMap.put("DYNAMIC_SIZE", ((Hashtable<K, Object>)property).get("DYNAMIC_SIZE"));
        appRepMap.put("EXTERNAL_APP_VERSION_ID", ((Hashtable<K, Object>)property).get("EXTERNAL_APP_VERSION_ID"));
        appRepMap.put("HAS_UPDATE_AVAILABLE", ((Hashtable<K, Object>)property).get("HAS_UPDATE_AVAILABLE"));
        appRepMap.put("CUSTOMER_ID", ((Hashtable<K, Object>)property).get("CUSTOMER_ID"));
        appRepMap.put("APP_TYPE", ((Hashtable<K, Object>)property).get("APP_TYPE"));
        appRepMap.put("NOTIFY_ADMIN", ((Hashtable<K, Object>)property).get("NOTIFY_ADMIN"));
        appRepMap.put("PLATFORM_TYPE", ((Hashtable<K, Object>)property).get("PLATFORM_TYPE"));
        appRepMap.put("SCOPE", ((Hashtable<K, Object>)property).get("SCOPE"));
        if (property.containsKey("USER_INSTALLED_APPS")) {
            appRepMap.put("USER_INSTALLED_APPS", ((Hashtable<K, Object>)property).get("USER_INSTALLED_APPS"));
        }
        if (property.containsKey("IS_MODERN_APP")) {
            appRepMap.put("IS_MODERN_APP", ((Hashtable<K, Object>)property).get("IS_MODERN_APP"));
        }
        if (property.containsKey("PACKAGE_TYPE")) {
            appRepMap.put("PACKAGE_TYPE", ((Hashtable<K, Object>)property).get("PACKAGE_TYPE"));
        }
        return appRepMap;
    }
    
    private DataObject addOrUpdateAppControlStatus(final Properties properties) throws DataAccessException {
        Long appGroupID = ((Hashtable<K, Long>)properties).get("APP_GROUP_ID");
        final DataObject dataObject = MDMUtil.getPersistence().constructDataObject();
        final Row mdAppGroupDetailsRow = ((Hashtable<K, Row>)properties).get("MdAppGroupDetails");
        Row appControlStatusRow = null;
        if (mdAppGroupDetailsRow != null && !mdAppGroupDetailsRow.hasUVGColInPK()) {
            appGroupID = (Long)mdAppGroupDetailsRow.get("APP_GROUP_ID");
        }
        if (appGroupID != null) {
            final Criteria appGroupCriteria = new Criteria(Column.getColumn("MdAppControlStatus", "APP_GROUP_ID"), (Object)appGroupID, 0);
            if (!this.existAppDO.isEmpty()) {
                appControlStatusRow = this.existAppDO.getRow("MdAppControlStatus", appGroupCriteria);
            }
        }
        if (appControlStatusRow == null) {
            appControlStatusRow = new Row("MdAppControlStatus");
            if (appGroupID != null) {
                appControlStatusRow.set("APP_GROUP_ID", (Object)appGroupID);
            }
            else {
                appControlStatusRow.set("APP_GROUP_ID", mdAppGroupDetailsRow.get("APP_GROUP_ID"));
            }
            appControlStatusRow.set("IS_ALLOWED", ((Hashtable<K, Object>)properties).get("IS_ALLOWED"));
            appControlStatusRow.set("UPDATED_AT", (Object)new Long(System.currentTimeMillis()));
            appControlStatusRow.set("UPDATED_BY", (Object)1L);
            dataObject.addRow(appControlStatusRow);
        }
        return dataObject;
    }
    
    private Row addOrUpdateAppControlStatus(final Row mdAppGroupDetailsRow) throws DataAccessException {
        final Properties properties = new Properties();
        ((Hashtable<String, Boolean>)properties).put("IS_ALLOWED", this.appType == 2 || this.isAllowed);
        ((Hashtable<String, Row>)properties).put("MdAppGroupDetails", mdAppGroupDetailsRow);
        final DataObject dataObject = this.addOrUpdateAppControlStatus(properties);
        Row appControlStatusRow = null;
        if (!dataObject.isEmpty()) {
            appControlStatusRow = dataObject.getRow("MdAppControlStatus");
            this.finalDO.merge(dataObject);
        }
        return appControlStatusRow;
    }
    
    private void processSoftwares(final Long resourceID, final List<Properties> softwareList, final Long customerId, final int platform, final int scope, final int appType) throws Exception {
        this.appType = appType;
        Collections.sort(softwareList, Comparator.comparing(appDetail -> appDetail.getProperty("IDENTIFIER")));
        final Map<String, Properties> softwareIdentifierMap = new HashMap<String, Properties>();
        for (final Properties appProp : softwareList) {
            softwareIdentifierMap.put(appProp.getProperty("IDENTIFIER"), appProp);
        }
        final DataObject existingResourceRelApps = this.getAppResourceRelExistingDataObject(resourceID, scope);
        final JSONObject summaryJSON = this.getInventoryAppsSummaryJSONFromDO(existingResourceRelApps, softwareList, platform);
        AppsUtil.logger.log(Level.INFO, " processSoftwares() Summary of Inventory apps for resource: {0} summaryJSON:{1}", new Object[] { resourceID, summaryJSON });
        this.handleDeletedAppRelations(existingResourceRelApps, summaryJSON, scope, appType, platform);
        this.handleNoChangeAppRelations(existingResourceRelApps, summaryJSON, softwareIdentifierMap, platform);
        int startIndex = 0;
        int endIndex = 0;
        final List processingRequiredList = new ArrayList();
        processingRequiredList.addAll(JSONUtil.convertJSONArrayToList(summaryJSON.getJSONArray("ADDED_APPS")));
        processingRequiredList.addAll(JSONUtil.convertJSONArrayToList(summaryJSON.getJSONArray("UPDATED_APPS")));
        final List<Properties> processingRequiredPropertyList = new ArrayList<Properties>();
        if (!processingRequiredList.isEmpty()) {
            for (final Object identifier : processingRequiredList) {
                processingRequiredPropertyList.add(softwareIdentifierMap.get(identifier));
            }
        }
        Collections.sort(processingRequiredPropertyList, Comparator.comparing(appDetail -> appDetail.getProperty("IDENTIFIER")));
        int size = processingRequiredPropertyList.size();
        do {
            startIndex = size - 50;
            endIndex = size - 1;
            if (startIndex < 0) {
                startIndex = 0;
            }
            size = startIndex;
            final List<Properties> subPropertyList = processingRequiredPropertyList.subList(startIndex, endIndex + 1);
            if (subPropertyList != null && !subPropertyList.isEmpty()) {
                final DataObject alreadyExistingAppListDOFromRepository = this.getExistingDOForGivenPlatformSpecificAppVersion(customerId, platform, subPropertyList);
                if (alreadyExistingAppListDOFromRepository != null && alreadyExistingAppListDOFromRepository.containsTable("MdAppDetails")) {
                    AppsUtil.logger.log(Level.INFO, "alreadyExistingAppListDOFromRepository - MDAPPDETAILS rows count: {0} and subPropertyList count: {1}", new Object[] { alreadyExistingAppListDOFromRepository.size("MdAppDetails"), subPropertyList.size() });
                }
                for (final Properties properties : subPropertyList) {
                    final Row appdetailRow = this.getAppDetailRowFromExistingDOForRepoApps(alreadyExistingAppListDOFromRepository, properties, platform);
                    final Boolean isNewApp = appdetailRow == null;
                    if (isNewApp) {
                        this.addNewlyDiscoveredAppToQueue(properties, resourceID, customerId, platform, scope);
                    }
                    else {
                        this.addOrUpdateAppResourceDO(existingResourceRelApps, resourceID, appdetailRow, properties, scope);
                    }
                }
            }
            else {
                AppsUtil.logger.log(Level.INFO, "subPropertyList is Empty");
            }
        } while (size > 0);
        MDMUtil.getPersistence().update(existingResourceRelApps);
        this.deleteBlacklistAppInRes(resourceID);
    }
    
    private DataObject getExistingDOForGivenPlatformSpecificAppVersion(final Long customerID, final int platformType, final List<Properties> bundleIdentifierPropertyList) throws DataAccessException {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
        query.addJoin(new Join("MdAppGroupDetails", "MdAppToGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        query.addJoin(new Join("MdAppToGroupRel", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        query.addSelectColumn(Column.getColumn("MdAppDetails", "*"));
        query.addSelectColumn(Column.getColumn("MdAppToGroupRel", "*"));
        query.addSelectColumn(Column.getColumn("MdAppGroupDetails", "*"));
        final Boolean isCaseSensitive = AppsUtil.getInstance().getIsBundleIdCaseSenstive(platformType);
        Criteria criteria = new Criteria(Column.getColumn("MdAppDetails", "PLATFORM_TYPE"), (Object)platformType, 0);
        criteria = criteria.and(new Criteria(Column.getColumn("MdAppDetails", "CUSTOMER_ID"), (Object)customerID, 0));
        Criteria applistCriteira = null;
        for (final Properties prop : bundleIdentifierPropertyList) {
            Criteria appCriteria = new Criteria(new Column("MdAppGroupDetails", "IDENTIFIER"), (Object)prop.getProperty("IDENTIFIER"), 0, (boolean)isCaseSensitive);
            appCriteria = appCriteria.and(new Criteria(new Column("MdAppDetails", "APP_VERSION"), (Object)prop.getProperty("APP_VERSION"), 0));
            appCriteria = appCriteria.and(new Criteria(new Column("MdAppDetails", "APP_NAME_SHORT_VERSION"), (Object)prop.getProperty("APP_NAME_SHORT_VERSION"), 0));
            applistCriteira = ((applistCriteira == null) ? appCriteria : applistCriteira.or(appCriteria));
        }
        query.setCriteria(criteria.and(applistCriteira));
        final DataObject dataObject = MDMUtil.getPersistence().get(query);
        return dataObject;
    }
    
    private void addOrUpdateAppResourceDO(final DataObject resourceDO, final Long resourceID, final Row mdAppDetailsRow, final Properties properties, final int scope) throws DataAccessException {
        final Integer platformType = ((Hashtable<K, Integer>)properties).get("PLATFORM_TYPE");
        final String identifier = properties.getProperty("IDENTIFIER");
        final DataObject tempDO = this.getParticularAppResourceDO(resourceDO, identifier, platformType);
        final Iterator existingAllVersionsOfAppItr = tempDO.getRows("MdAppDetails");
        List oldVersionAppIDList = new ArrayList();
        if (existingAllVersionsOfAppItr != null && existingAllVersionsOfAppItr.hasNext()) {
            oldVersionAppIDList = DBUtil.getColumnValuesAsList(existingAllVersionsOfAppItr, "APP_ID");
            if (!mdAppDetailsRow.hasUVGColInPK()) {
                final Long appID = (Long)mdAppDetailsRow.get("APP_ID");
                oldVersionAppIDList.remove(appID);
            }
        }
        final Criteria resourceCriteria = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "RESOURCE_ID"), (Object)resourceID, 0);
        final Criteria appCriteria = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "APP_ID"), mdAppDetailsRow.get("APP_ID"), 0);
        final Criteria scopeCriteria = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "SCOPE"), (Object)scope, 0);
        final Criteria existingCri = resourceCriteria.and(appCriteria).and(scopeCriteria);
        if (!oldVersionAppIDList.isEmpty()) {
            final Criteria oldVersionCri = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "APP_ID"), (Object)oldVersionAppIDList.toArray(), 8);
            final Criteria deleteCriteria = oldVersionCri.and(scopeCriteria).and(resourceCriteria);
            tempDO.deleteRows("MdInstalledAppResourceRel", deleteCriteria);
            resourceDO.merge(tempDO);
            AppsUtil.logger.log(Level.INFO, "[AppUpdateCase] Deleting old versions for resourceID:{0} Identifier:{1} oldAppIDs:{2}", new Object[] { resourceID, identifier, oldVersionAppIDList });
        }
        Row appGroupRelRow = resourceDO.getRow("MdInstalledAppResourceRel", existingCri);
        boolean isAdd = false;
        if (mdAppDetailsRow.hasUVGColInPK() || resourceDO.isEmpty() || appGroupRelRow == null) {
            appGroupRelRow = new Row("MdInstalledAppResourceRel");
            appGroupRelRow.set("RESOURCE_ID", (Object)resourceID);
            appGroupRelRow.set("UPDATED_AT", (Object)System.currentTimeMillis());
            appGroupRelRow.set("SCOPE", (Object)scope);
            isAdd = true;
        }
        appGroupRelRow.set("APP_ID", mdAppDetailsRow.get("APP_ID"));
        appGroupRelRow.set("DYNAMIC_SIZE", (Object)((Hashtable<K, Long>)properties).get("DYNAMIC_SIZE"));
        appGroupRelRow.set("USER_INSTALLED_APPS", (Object)(properties.containsKey("USER_INSTALLED_APPS") ? ((Hashtable<K, Integer>)properties).get("USER_INSTALLED_APPS") : 1));
        appGroupRelRow.set("HAS_UPDATE_AVAILABLE", (Object)((Hashtable<K, Boolean>)properties).get("HAS_UPDATE_AVAILABLE"));
        if (isAdd) {
            resourceDO.addRow(appGroupRelRow);
            AppsUtil.logger.log(Level.INFO, "Adding resourceID:{0} Identifier:{1} AppleID:{2}", new Object[] { resourceID, identifier, mdAppDetailsRow.get("APP_ID") });
        }
        else {
            AppsUtil.logger.log(Level.INFO, "Updating resourceID:{0} Identifier:{1} AppleID:{2}", new Object[] { resourceID, identifier, mdAppDetailsRow.get("APP_ID") });
            resourceDO.updateRow(appGroupRelRow);
        }
    }
    
    private Row getAppDetailRowFromExistingDOForRepoApps(final DataObject dataObject, final Properties inputProps, final int platform) throws DataAccessException {
        final String identifier = inputProps.getProperty("IDENTIFIER");
        final String appVersion = inputProps.getProperty("APP_VERSION");
        final String shortAppVersion = inputProps.getProperty("APP_NAME_SHORT_VERSION");
        final Long externalAppVersionId = ((Hashtable<K, Long>)inputProps).get("EXTERNAL_APP_VERSION_ID");
        final List<String> tableList = new ArrayList<String>();
        tableList.add("MdAppDetails");
        tableList.add("MdAppToGroupRel");
        tableList.add("MdAppGroupDetails");
        final Row row = dataObject.getRow("MdAppGroupDetails", new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)identifier, 0, (boolean)AppsUtil.getInstance().getIsBundleIdCaseSenstive(platform)));
        if (row != null) {
            final DataObject particularAppDO = dataObject.getDataObject((List)tableList, row);
            if (particularAppDO != null && particularAppDO.containsTable("MdAppDetails")) {
                if (platform == 1 && externalAppVersionId != null) {
                    final Criteria externalIdCri = new Criteria(Column.getColumn("MdAppDetails", "EXTERNAL_APP_VERSION_ID"), (Object)externalAppVersionId, 0);
                    final Row appDetailsExternalRow = dataObject.getRow("MdAppDetails", externalIdCri);
                    if (appDetailsExternalRow != null) {
                        return appDetailsExternalRow;
                    }
                }
                Criteria versionCri = new Criteria(Column.getColumn("MdAppDetails", "APP_VERSION"), (Object)appVersion, 0);
                versionCri = versionCri.and(new Criteria(Column.getColumn("MdAppDetails", "APP_NAME_SHORT_VERSION"), (Object)shortAppVersion, 0));
                final Row appDetailRow = dataObject.getRow("MdAppDetails", versionCri);
                if (appDetailRow != null) {
                    return appDetailRow;
                }
            }
        }
        return null;
    }
    
    public void addNewlyDiscoveredApp(final Properties properties, final Long resourceID, final Long customerId, final int platform, final int scope) throws DataAccessException {
        try {
            this.finalDO = SyMUtil.getPersistence().constructDataObject();
            AppsUtil.logger.log(Level.INFO, "Newly discovered app added to repository Props->{0}", properties);
            this.existAppDO = null;
            final List<String> subList = new ArrayList<String>();
            subList.add(properties.getProperty("IDENTIFIER"));
            this.getExistDataObject(resourceID, subList.toArray(), customerId, platform, scope);
            final Row mdAppGroupRow = this.addOrUpdateAppGroup(properties);
            if (!mdAppGroupRow.hasUVGColInPK()) {
                this.checkAndModifyPropertiesForiOSEnterpriseApp((Long)mdAppGroupRow.get("APP_GROUP_ID"), properties);
            }
            ((Hashtable<String, String>)properties).put("updateAppDetails", "false");
            final Row mdAppDetailsRow = this.addOrUpdateAppDetails(properties);
            this.addOrUpdateAppGroupRel(mdAppGroupRow, mdAppDetailsRow);
            this.addOrUpdateAppResourceRel(resourceID, mdAppDetailsRow, properties, scope);
            MDMUtil.getPersistence().update(this.finalDO);
        }
        catch (final DataAccessException e) {
            AppsUtil.logger.log(Level.SEVERE, "Unable to add newly discovered app to repository", (Throwable)e);
            throw e;
        }
        catch (final Exception e2) {
            AppsUtil.logger.log(Level.SEVERE, "Unable to add newly discovered app to repository", e2);
            throw e2;
        }
    }
    
    private void addNewlyDiscoveredAppToQueue(final Properties properties, final Long resourceID, final Long customerID, final int platform, final int scope) throws Exception {
        final long postTime = MDMUtil.getCurrentTimeInMillis();
        ((Hashtable<String, Long>)properties).put("RESOURCE_ID", resourceID);
        ((Hashtable<String, Long>)properties).put("CUSTOMER_ID", customerID);
        ((Hashtable<String, Integer>)properties).put("PLATFORM_TYPE", platform);
        ((Hashtable<String, Integer>)properties).put("SCOPE", scope);
        final JSONObject jsonObject = this.getAppPropertyInJsonFormat(properties);
        final String strData = jsonObject.toString();
        final String separator = "\t";
        final String qFileName = "discoveredapps - " + customerID + "-" + resourceID + "-" + postTime + ".txt";
        AppsUtil.logger.log(Level.INFO, "Command Status status update is received and going to add to the queue.");
        final DCQueueData queueData = new DCQueueData();
        queueData.fileName = qFileName;
        queueData.postTime = postTime;
        queueData.queueData = strData;
        queueData.customerID = customerID;
        final Map queueExtnTableData = new HashMap();
        queueExtnTableData.put("CUSTOMER_ID", customerID);
        queueData.queueExtnTableData = queueExtnTableData;
        queueData.queueDataType = 48;
        final String queueName = QueueControllerHelper.getInstance().getQueueName(queueData.queueDataType, (String)queueData.queueData);
        AppsUtil.logger.log(Level.INFO, "QueueName : {0}{1}AddingToQueue{2}{3}{4}{5}{6}{7}", new Object[] { queueName, separator, separator, queueData.fileName, separator, MDMDataQueueUtil.getInstance().getPlatformNameForLogging(queueData.queueDataType), separator, String.valueOf(postTime) });
        final DCQueue queue = DCQueueHandler.getQueue(queueName);
        queue.addToQueue(queueData);
    }
    
    public HashMap processAndroidAppRepositoryData(final Properties properties) {
        final HashMap appMap = new HashMap();
        try {
            ((Hashtable<String, Integer>)properties).put("PLATFORM_TYPE", 2);
            this.getExistDataObjectForAppRep(((Hashtable<K, String>)properties).get("IDENTIFIER"), ((Hashtable<K, Long>)properties).get("CUSTOMER_ID"), 2);
            final String version = ((Hashtable<K, String>)properties).getOrDefault("APP_VERSION", "--").trim();
            final String versionCode = ((Hashtable<K, String>)properties).getOrDefault("APP_NAME_SHORT_VERSION", "--").trim();
            final Criteria identifierCriteria = new Criteria(Column.getColumn("MdAppDetails", "IDENTIFIER"), ((Hashtable<K, Object>)properties).get("IDENTIFIER"), 0);
            final Criteria versionCriteria = new Criteria(Column.getColumn("MdAppDetails", "APP_VERSION"), (Object)version, 0);
            final Criteria versionCodeCriteria = new Criteria(Column.getColumn("MdAppDetails", "APP_NAME_SHORT_VERSION"), (Object)versionCode, 0);
            final Criteria criteria = identifierCriteria.and(versionCriteria).and(versionCodeCriteria);
            if (!this.existAppDO.isEmpty()) {
                final Row mdAppGroupRow = this.existAppDO.getFirstRow("MdAppGroupDetails");
                appMap.put("APP_GROUP_ID", mdAppGroupRow.get("APP_GROUP_ID"));
                Row mdAppDetailsRow = this.existAppDO.getRow("MdAppDetails", criteria);
                if (mdAppDetailsRow != null) {
                    appMap.put("APP_ID", mdAppDetailsRow.get("APP_ID"));
                }
                else {
                    ((Hashtable<String, Object>)properties).put("APP_NAME", mdAppGroupRow.get("GROUP_DISPLAY_NAME"));
                    ((Hashtable<String, String>)properties).put("APP_NAME_SHORT_VERSION", versionCode);
                    ((Hashtable<String, String>)properties).put("APP_VERSION", version);
                    ((Hashtable<String, Integer>)properties).put("APP_TYPE", 1);
                    ((Hashtable<String, Boolean>)properties).put("NOTIFY_ADMIN", Boolean.FALSE);
                    this.finalDO = MDMUtil.getPersistence().constructDataObject();
                    mdAppDetailsRow = this.addOrUpdateAppDetails(properties);
                    this.addOrUpdateAppGroupRel(mdAppGroupRow, mdAppDetailsRow);
                    MDMUtil.getPersistence().update(this.finalDO);
                    mdAppDetailsRow = this.finalDO.getFirstRow("MdAppDetails");
                    appMap.put("APP_ID", mdAppDetailsRow.get("APP_ID"));
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return appMap;
    }
    
    private void getExistDataObject(final Long resourceID, final Object[] identifiers, final Long customerId, final int platform, final int scope) throws DataAccessException {
        this.getAppExistingDataObject(identifiers, customerId, platform, scope);
        this.getAppResourceRelExistingDataObject(resourceID, scope);
    }
    
    private void getAppExistingDataObject(final Object[] identifiers, final Long customerId, final int platform, final int scope) throws DataAccessException {
        final boolean caseSensitiveIdentifier = AppsUtil.getInstance().getIsBundleIdCaseSenstive(platform);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppGroupDetails"));
        final Join appGroupRelJoin = new Join("MdAppGroupDetails", "MdAppToGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1);
        final Join appDetailsJoin = new Join("MdAppToGroupRel", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 1);
        final Join pkgToAppGroupJoin = new Join("MdAppGroupDetails", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1);
        final Join pkgToAppDataJoin = new Join("MdAppDetails", "MdPackageToAppData", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 1);
        selectQuery.addJoin(appGroupRelJoin);
        selectQuery.addJoin(appDetailsJoin);
        selectQuery.addJoin(pkgToAppGroupJoin);
        selectQuery.addJoin(pkgToAppDataJoin);
        final Criteria identifierCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)identifiers, 8, caseSensitiveIdentifier);
        final Criteria customerCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria platformCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)platform, 0);
        selectQuery.setCriteria(identifierCriteria.and(customerCriteria).and(platformCriteria));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "*"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppToGroupRel", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppToGroupRel", "APP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_VERSION"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_NAME_SHORT_VERSION"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppDetails", "IDENTIFIER"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppDetails", "EXTERNAL_APP_VERSION_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "*"));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "PACKAGE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "APP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "SUPPORTED_DEVICES"));
        this.existAppDO = MDMUtil.getPersistence().get(selectQuery);
    }
    
    private DataObject getAppResourceRelExistingDataObject(final Long resourceID, final int scope) throws DataAccessException {
        final SelectQuery selectQueryResourceRel = (SelectQuery)new SelectQueryImpl(new Table("MdInstalledAppResourceRel"));
        selectQueryResourceRel.addJoin(new Join("MdInstalledAppResourceRel", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQueryResourceRel.addJoin(new Join("MdAppDetails", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQueryResourceRel.addJoin(new Join("MdAppToGroupRel", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        final Criteria resourceCriteria = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "RESOURCE_ID"), (Object)resourceID, 0);
        final Criteria scopeCriteria = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "SCOPE"), (Object)scope, 0);
        selectQueryResourceRel.setCriteria(resourceCriteria.and(scopeCriteria));
        selectQueryResourceRel.addSelectColumn(Column.getColumn("MdInstalledAppResourceRel", "*"));
        selectQueryResourceRel.addSelectColumn(Column.getColumn("MdAppDetails", "*"));
        selectQueryResourceRel.addSelectColumn(Column.getColumn("MdAppToGroupRel", "*"));
        selectQueryResourceRel.addSelectColumn(Column.getColumn("MdAppGroupDetails", "*"));
        final DataObject appResourceRel = MDMUtil.getPersistence().get(selectQueryResourceRel);
        this.existResourceRelDO = (DataObject)appResourceRel.clone();
        return appResourceRel;
    }
    
    private void handleDeletedAppRelations(final DataObject dataObject, final JSONObject summaryJSON, final int scope, final int appType, final int platform) throws DataAccessException {
        final JSONArray deletedBundleIDArray = (JSONArray)summaryJSON.get("DELETED_APPS");
        final List deletedBundleIDList = JSONUtil.convertJSONArrayToList(deletedBundleIDArray);
        if (!deletedBundleIDList.isEmpty()) {
            for (final Object toBeDeletedIdentifier : deletedBundleIDList) {
                final DataObject getToBeDeletedDO = this.getParticularAppResourceDO(dataObject, Arrays.asList(String.valueOf(toBeDeletedIdentifier)), platform);
                Criteria criteria = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "SCOPE"), (Object)scope, 0);
                criteria = criteria.and(new Criteria(Column.getColumn("MdInstalledAppResourceRel", "USER_INSTALLED_APPS"), (Object)appType, 0));
                getToBeDeletedDO.deleteRows("MdInstalledAppResourceRel", criteria);
                dataObject.merge(getToBeDeletedDO);
            }
        }
    }
    
    private void handleNoChangeAppRelations(final DataObject dataObject, final JSONObject summaryJSON, final Map<String, Properties> softwareMap, final int platform) throws DataAccessException {
        final JSONArray bundleIDArray = (JSONArray)summaryJSON.get("NOT_CHANGED_APPS");
        final List nochangeIdentifiers = JSONUtil.convertJSONArrayToList(bundleIDArray);
        if (!nochangeIdentifiers.isEmpty()) {
            for (final Object identifier : nochangeIdentifiers) {
                final DataObject tempResDO = this.getParticularAppResourceDO(dataObject, (String)identifier, platform);
                if (tempResDO != null && tempResDO.containsTable("MdInstalledAppResourceRel")) {
                    final Row appGroupRelRow = tempResDO.getRow("MdInstalledAppResourceRel");
                    final Properties prop = softwareMap.get(identifier);
                    appGroupRelRow.set("DYNAMIC_SIZE", (Object)((Hashtable<K, Long>)prop).get("DYNAMIC_SIZE"));
                    appGroupRelRow.set("USER_INSTALLED_APPS", (Object)(prop.containsKey("USER_INSTALLED_APPS") ? ((Hashtable<K, Integer>)prop).get("USER_INSTALLED_APPS") : 1));
                    appGroupRelRow.set("HAS_UPDATE_AVAILABLE", ((Hashtable<K, Object>)prop).get("HAS_UPDATE_AVAILABLE"));
                    final Criteria versionNotAvailableCriteria = new Criteria(new Column("MdAppDetails", "APP_VERSION"), (Object)"--", 0);
                    final Row appDetailsRow = tempResDO.getRow("MdAppDetails", versionNotAvailableCriteria);
                    final String appVersion = ((Hashtable<K, String>)prop).get("APP_VERSION");
                    if (appDetailsRow != null && appVersion != null) {
                        appDetailsRow.set("APP_VERSION", (Object)appVersion);
                        dataObject.updateRow(appDetailsRow);
                    }
                    dataObject.updateRow(appGroupRelRow);
                }
            }
        }
    }
    
    private JSONObject getInventoryAppsSummaryJSONFromDO(final DataObject dataObject, final List<Properties> properties, final int platform) throws DataAccessException {
        final List<String> allIdentifiersList = new ArrayList<String>();
        List<String> deletedIdentifierList = new ArrayList<String>();
        final List<String> updatedIdentifiersList = new ArrayList<String>();
        final List<String> noChangeIdentifierList = new ArrayList<String>();
        final List<String> addedIdentifiersList = new ArrayList<String>();
        for (final Properties singleAppProp : properties) {
            final String inputIdentifier = singleAppProp.getProperty("IDENTIFIER");
            allIdentifiersList.add(inputIdentifier);
            final DataObject appToResourceDO = this.getParticularAppResourceDO(dataObject, inputIdentifier, platform);
            if (appToResourceDO != null && appToResourceDO.containsTable("MdAppDetails")) {
                final Row appIDRow = appToResourceDO.getRow("MdAppDetails");
                if (appIDRow == null) {
                    continue;
                }
                final String existingVersion = (String)appIDRow.get("APP_VERSION");
                final String existingShortVersion = (String)appIDRow.get("APP_NAME_SHORT_VERSION");
                final String inventoryShortVersion = singleAppProp.getProperty("APP_NAME_SHORT_VERSION");
                final String inventoryVersion = singleAppProp.getProperty("APP_VERSION");
                final Long inventoryExternalId = ((Hashtable<K, Long>)singleAppProp).get("EXTERNAL_APP_VERSION_ID");
                final Long existingExternalId = (Long)appIDRow.get("EXTERNAL_APP_VERSION_ID");
                if (platform == 1 && existingExternalId == null && inventoryExternalId != null) {
                    appIDRow.set("EXTERNAL_APP_VERSION_ID", (Object)inventoryExternalId);
                    dataObject.updateRow(appIDRow);
                }
                if (platform == 1 && inventoryExternalId != null && !inventoryExternalId.equals(0L) && existingExternalId != null) {
                    if (!inventoryExternalId.equals(existingExternalId)) {
                        updatedIdentifiersList.add(inputIdentifier);
                    }
                    else {
                        noChangeIdentifierList.add(inputIdentifier);
                    }
                }
                else if (!MDMStringUtils.equals(existingVersion, inventoryVersion, false) || !MDMStringUtils.equals(existingShortVersion, inventoryShortVersion, false)) {
                    updatedIdentifiersList.add(inputIdentifier);
                }
                else {
                    noChangeIdentifierList.add(inputIdentifier);
                }
            }
            else {
                addedIdentifiersList.add(inputIdentifier);
            }
        }
        final Criteria deletedAppsCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)allIdentifiersList.toArray(), 9, (boolean)AppsUtil.getInstance().getIsBundleIdCaseSenstive(platform));
        final Iterator deletedRowsIterator = dataObject.getRows("MdAppGroupDetails", deletedAppsCriteria);
        if (deletedRowsIterator != null) {
            deletedIdentifierList = DBUtil.getColumnValuesAsList(deletedRowsIterator, "IDENTIFIER");
        }
        final JSONObject summaryJSON = new JSONObject();
        summaryJSON.put("ALL_DETECTED_APPS", (Object)new JSONArray((Collection)allIdentifiersList));
        summaryJSON.put("ADDED_APPS", (Object)new JSONArray((Collection)addedIdentifiersList));
        summaryJSON.put("UPDATED_APPS", (Object)new JSONArray((Collection)updatedIdentifiersList));
        summaryJSON.put("DELETED_APPS", (Object)new JSONArray((Collection)deletedIdentifierList));
        summaryJSON.put("NOT_CHANGED_APPS", (Object)new JSONArray((Collection)noChangeIdentifierList));
        return summaryJSON;
    }
    
    private DataObject getParticularAppResourceDO(final DataObject dataObject, final String identifier, final int platform) throws DataAccessException {
        final List<String> identifierList = new ArrayList<String>();
        identifierList.add(identifier);
        final DataObject particularDO = this.getParticularAppResourceDO(dataObject, identifierList, platform);
        return particularDO;
    }
    
    private DataObject getParticularAppResourceDO(final DataObject dataObject, final List<String> identifiers, final int platform) throws DataAccessException {
        final List<String> tableList = new ArrayList<String>();
        tableList.add("MdAppGroupDetails");
        tableList.add("MdAppToGroupRel");
        tableList.add("MdAppDetails");
        tableList.add("MdInstalledAppResourceRel");
        DataObject particularDO = (DataObject)new WritableDataObject();
        final Row appGroupDetailsRow = dataObject.getRow("MdAppGroupDetails", new Criteria(new Column("MdAppGroupDetails", "IDENTIFIER"), (Object)identifiers.toArray(), 8, (boolean)AppsUtil.getInstance().getIsBundleIdCaseSenstive(platform)));
        if (appGroupDetailsRow != null) {
            particularDO = dataObject.getDataObject((List)tableList, appGroupDetailsRow);
        }
        return particularDO;
    }
    
    private void addOrUpdateAppCategoryRel(final Row mdAppGroupRow, final Properties properties) throws DataAccessException {
        final Long appCategoryID = ((Hashtable<K, Long>)properties).get("APP_CATEGORY_ID");
        if (appCategoryID != null && appCategoryID != -1L) {
            DataObject dataObject = MDMUtil.getPersistence().constructDataObject();
            Row appGroupRelRow = null;
            if (mdAppGroupRow.hasUVGColInPK()) {
                appGroupRelRow = new Row("MdAppGroupCategoryRel");
                appGroupRelRow.set("APP_CATEGORY_ID", (Object)appCategoryID);
                appGroupRelRow.set("APP_GROUP_ID", mdAppGroupRow.get("APP_GROUP_ID"));
                dataObject.addRow(appGroupRelRow);
            }
            else {
                final Criteria criteria = new Criteria(Column.getColumn("MdAppGroupCategoryRel", "APP_GROUP_ID"), mdAppGroupRow.get("APP_GROUP_ID"), 0);
                dataObject = MDMUtil.getPersistence().get("MdAppGroupCategoryRel", criteria);
                if (dataObject.isEmpty()) {
                    final Row appCategoryRelRow = new Row("MdAppGroupCategoryRel");
                    appCategoryRelRow.set("APP_GROUP_ID", mdAppGroupRow.get("APP_GROUP_ID"));
                    appCategoryRelRow.set("APP_CATEGORY_ID", (Object)appCategoryID);
                    dataObject.addRow(appCategoryRelRow);
                }
                else {
                    final Row appCategoryRelRow = dataObject.getFirstRow("MdAppGroupCategoryRel");
                    appCategoryRelRow.set("APP_GROUP_ID", mdAppGroupRow.get("APP_GROUP_ID"));
                    appCategoryRelRow.set("APP_CATEGORY_ID", (Object)appCategoryID);
                    dataObject.updateRow(appCategoryRelRow);
                }
            }
            this.finalDO.merge(dataObject);
        }
    }
    
    private void addOrUpdateAppGroupRel(final Row mdAppGroupRow, final Row mdAppDetailsRow) throws DataAccessException {
        Row appGroupRelRow = null;
        if (mdAppGroupRow.hasUVGColInPK() || mdAppDetailsRow.hasUVGColInPK()) {
            appGroupRelRow = new Row("MdAppToGroupRel");
            appGroupRelRow.set("APP_GROUP_ID", mdAppGroupRow.get("APP_GROUP_ID"));
            appGroupRelRow.set("APP_ID", mdAppDetailsRow.get("APP_ID"));
            this.finalDO.addRow(appGroupRelRow);
        }
        else if (!mdAppGroupRow.hasUVGColInPK() && !mdAppDetailsRow.hasUVGColInPK()) {
            final Criteria appGroupCriteria = new Criteria(Column.getColumn("MdAppToGroupRel", "APP_GROUP_ID"), mdAppGroupRow.get("APP_GROUP_ID"), 0);
            final Criteria appCriteria = new Criteria(Column.getColumn("MdAppToGroupRel", "APP_ID"), mdAppDetailsRow.get("APP_ID"), 0);
            final Criteria criteria = appGroupCriteria.and(appCriteria);
            if (!this.existAppDO.isEmpty()) {
                appGroupRelRow = this.existAppDO.getRow("MdAppToGroupRel", criteria);
                if (appGroupRelRow == null) {
                    appGroupRelRow = new Row("MdAppToGroupRel");
                    appGroupRelRow.set("APP_ID", mdAppDetailsRow.get("APP_ID"));
                    appGroupRelRow.set("APP_GROUP_ID", mdAppGroupRow.get("APP_GROUP_ID"));
                    this.finalDO.addRow(appGroupRelRow);
                }
            }
        }
    }
    
    private void addOrUpdateAppResourceRel(final Long resourceID, final Row mdAppDetailsRow, final Properties properties, final int scope) throws DataAccessException {
        this.addOrUpdateAppResourceDO(this.existResourceRelDO, resourceID, mdAppDetailsRow, properties, scope);
        this.finalDO.merge(this.existResourceRelDO);
    }
    
    private Row addOrUpdateAppGroup(final Properties properties) throws DataAccessException {
        final String identifier = ((Hashtable<K, String>)properties).get("IDENTIFIER");
        final int platform = ((Hashtable<K, Integer>)properties).get("PLATFORM_TYPE");
        final Boolean isCaseSensitive = AppsUtil.getInstance().getIsBundleIdCaseSenstive(platform);
        final Criteria criteria;
        final Criteria identifierCriteria = criteria = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)identifier, 0, (boolean)isCaseSensitive);
        final DataObject dataObject = MDMUtil.getPersistence().constructDataObject();
        String appName = MDMStringUtils.trimStringLenght(((Hashtable<K, String>)properties).get("APP_NAME"), 250);
        appName = AppsUtil.getInstance().replaceSplCharsInAppName(appName);
        String isModernApp = String.valueOf(((Hashtable<K, Object>)properties).get("IS_MODERN_APP"));
        if (isModernApp == null || isModernApp.equalsIgnoreCase("null") || isModernApp.trim().isEmpty()) {
            isModernApp = Boolean.TRUE.toString();
        }
        Row mdAppGroupRow;
        if (this.existAppDO.isEmpty()) {
            mdAppGroupRow = new Row("MdAppGroupDetails");
            mdAppGroupRow.set("GROUP_DISPLAY_NAME", (Object)appName);
            mdAppGroupRow.set("IDENTIFIER", ((Hashtable<K, Object>)properties).get("IDENTIFIER"));
            mdAppGroupRow.set("PLATFORM_TYPE", (Object)platform);
            mdAppGroupRow.set("CUSTOMER_ID", ((Hashtable<K, Object>)properties).get("CUSTOMER_ID"));
            mdAppGroupRow.set("APP_TYPE", ((Hashtable<K, Object>)properties).get("APP_TYPE"));
            mdAppGroupRow.set("NOTIFY_ADMIN", ((Hashtable<K, Object>)properties).get("NOTIFY_ADMIN"));
            mdAppGroupRow.set("ADDED_TIME", (Object)new Long(System.currentTimeMillis()));
            mdAppGroupRow.set("IS_MODERN_APP", (Object)Boolean.valueOf(isModernApp));
            dataObject.addRow(mdAppGroupRow);
        }
        else {
            mdAppGroupRow = this.existAppDO.getRow("MdAppGroupDetails", criteria);
            if (mdAppGroupRow == null) {
                mdAppGroupRow = new Row("MdAppGroupDetails");
                mdAppGroupRow.set("GROUP_DISPLAY_NAME", (Object)appName);
                mdAppGroupRow.set("IDENTIFIER", ((Hashtable<K, Object>)properties).get("IDENTIFIER"));
                mdAppGroupRow.set("PLATFORM_TYPE", (Object)platform);
                mdAppGroupRow.set("CUSTOMER_ID", ((Hashtable<K, Object>)properties).get("CUSTOMER_ID"));
                mdAppGroupRow.set("APP_TYPE", ((Hashtable<K, Object>)properties).get("APP_TYPE"));
                mdAppGroupRow.set("NOTIFY_ADMIN", ((Hashtable<K, Object>)properties).get("NOTIFY_ADMIN"));
                mdAppGroupRow.set("ADDED_TIME", (Object)new Long(System.currentTimeMillis()));
                mdAppGroupRow.set("IS_MODERN_APP", (Object)Boolean.valueOf(isModernApp));
                dataObject.addRow(mdAppGroupRow);
            }
        }
        this.finalDO.merge(dataObject);
        return mdAppGroupRow;
    }
    
    private Row addOrUpdateAppDetails(final Properties properties) throws DataAccessException {
        final String identifier = ((Hashtable<K, String>)properties).get("IDENTIFIER");
        final String packageIdentifier = properties.getProperty("packageIdentifier");
        Long externalAppVersionId = ((Hashtable<K, Long>)properties).getOrDefault("EXTERNAL_APP_VERSION_ID", null);
        if (externalAppVersionId != null && externalAppVersionId.equals(0L)) {
            externalAppVersionId = null;
        }
        String identifierString = null;
        if (packageIdentifier == null) {
            identifierString = identifier;
        }
        else {
            identifierString = packageIdentifier;
        }
        final Integer platformType = ((Hashtable<K, Integer>)properties).get("PLATFORM_TYPE");
        final String version = ((Hashtable<K, String>)properties).getOrDefault("APP_VERSION", "--").trim();
        final String versionCode = ((Hashtable<K, String>)properties).getOrDefault("APP_NAME_SHORT_VERSION", "--").trim();
        final Boolean isCaseSensitive = AppsUtil.getInstance().getIsBundleIdCaseSenstive(platformType);
        Criteria identifierCriteria = new Criteria(Column.getColumn("MdAppDetails", "IDENTIFIER"), (Object)identifier, 0, (boolean)isCaseSensitive);
        final Criteria versionCriteria = new Criteria(Column.getColumn("MdAppDetails", "APP_VERSION"), (Object)version, 0, (boolean)isCaseSensitive);
        final Criteria externalIdCriteria = new Criteria(Column.getColumn("MdAppDetails", "EXTERNAL_APP_VERSION_ID"), (Object)externalAppVersionId, 0, (boolean)isCaseSensitive);
        final Criteria versionCodeCriteria = new Criteria(Column.getColumn("MdAppDetails", "APP_NAME_SHORT_VERSION"), (Object)versionCode, 0, (boolean)isCaseSensitive);
        if (packageIdentifier != null) {
            final Criteria packageIdentifierCriteria = new Criteria(Column.getColumn("MdAppDetails", "IDENTIFIER"), (Object)packageIdentifier, 0, (boolean)isCaseSensitive);
            identifierCriteria = identifierCriteria.or(packageIdentifierCriteria);
        }
        Criteria criteria = identifierCriteria;
        if (platformType == 1 && externalAppVersionId != null) {
            criteria = criteria.and(externalIdCriteria.or(versionCriteria));
        }
        else {
            criteria = criteria.and(versionCriteria);
        }
        if (platformType == 2) {
            criteria = criteria.and(versionCodeCriteria);
        }
        String appName = MDMStringUtils.trimStringLenght(((Hashtable<K, String>)properties).get("APP_NAME"), 250);
        appName = AppsUtil.getInstance().replaceSplCharsInAppName(appName);
        final String appTitle = MDMStringUtils.trimStringLenght(((Hashtable<K, String>)properties).get("APP_TITLE"), 200);
        Row mdAppDetailsRow = null;
        if (this.existAppDO.isEmpty()) {
            mdAppDetailsRow = new Row("MdAppDetails");
            mdAppDetailsRow.set("APP_NAME", (Object)appName);
            mdAppDetailsRow.set("APP_TITLE", (Object)appTitle);
            mdAppDetailsRow.set("APP_VERSION", ((Hashtable<K, Object>)properties).get("APP_VERSION"));
            mdAppDetailsRow.set("IDENTIFIER", (Object)identifierString);
            mdAppDetailsRow.set("APP_TYPE", ((Hashtable<K, Object>)properties).get("APP_TYPE"));
            mdAppDetailsRow.set("APP_NAME_SHORT_VERSION", (Object)((Hashtable<K, String>)properties).getOrDefault("APP_NAME_SHORT_VERSION", "--"));
            mdAppDetailsRow.set("PLATFORM_TYPE", (Object)platformType);
            mdAppDetailsRow.set("BUNDLE_SIZE", ((Hashtable<K, Object>)properties).get("BUNDLE_SIZE"));
            mdAppDetailsRow.set("CUSTOMER_ID", (Object)new Long(((Hashtable<K, Object>)properties).get("CUSTOMER_ID").toString()));
            mdAppDetailsRow.set("EXTERNAL_APP_VERSION_ID", (Object)externalAppVersionId);
            this.finalDO.addRow(mdAppDetailsRow);
        }
        else {
            mdAppDetailsRow = this.existAppDO.getRow("MdAppDetails", criteria);
            if (mdAppDetailsRow == null) {
                mdAppDetailsRow = new Row("MdAppDetails");
                mdAppDetailsRow.set("APP_NAME", (Object)appName);
                mdAppDetailsRow.set("APP_TITLE", (Object)appTitle);
                mdAppDetailsRow.set("APP_VERSION", ((Hashtable<K, Object>)properties).get("APP_VERSION"));
                mdAppDetailsRow.set("IDENTIFIER", (Object)identifierString);
                mdAppDetailsRow.set("APP_TYPE", ((Hashtable<K, Object>)properties).get("APP_TYPE"));
                mdAppDetailsRow.set("APP_NAME_SHORT_VERSION", (Object)((Hashtable<K, String>)properties).getOrDefault("APP_NAME_SHORT_VERSION", "--"));
                mdAppDetailsRow.set("PLATFORM_TYPE", (Object)platformType);
                mdAppDetailsRow.set("BUNDLE_SIZE", ((Hashtable<K, Object>)properties).get("BUNDLE_SIZE"));
                mdAppDetailsRow.set("CUSTOMER_ID", (Object)new Long(((Hashtable<K, Object>)properties).get("CUSTOMER_ID").toString()));
                mdAppDetailsRow.set("EXTERNAL_APP_VERSION_ID", (Object)externalAppVersionId);
                this.finalDO.addRow(mdAppDetailsRow);
            }
            else {
                if (!properties.getProperty("updateAppDetails", "true").equalsIgnoreCase("false") || !identifierString.trim().equals(identifier.trim())) {
                    mdAppDetailsRow.set("APP_NAME", (Object)appName);
                    mdAppDetailsRow.set("APP_TITLE", (Object)appTitle);
                    mdAppDetailsRow.set("APP_TYPE", ((Hashtable<K, Object>)properties).get("APP_TYPE"));
                    mdAppDetailsRow.set("IDENTIFIER", (Object)identifierString);
                    mdAppDetailsRow.set("APP_NAME_SHORT_VERSION", ((Hashtable<K, Object>)properties).get("APP_NAME_SHORT_VERSION"));
                    mdAppDetailsRow.set("BUNDLE_SIZE", ((Hashtable<K, Object>)properties).get("BUNDLE_SIZE"));
                    mdAppDetailsRow.set("EXTERNAL_APP_VERSION_ID", (Object)externalAppVersionId);
                    this.finalDO.updateBlindly(mdAppDetailsRow);
                }
                if (platformType == 1 && mdAppDetailsRow != null) {
                    final String existingVersion = (String)mdAppDetailsRow.get("APP_NAME_SHORT_VERSION");
                    if (MDMStringUtils.isEmpty(existingVersion) && !MDMStringUtils.isEmpty(versionCode)) {
                        mdAppDetailsRow.set("APP_NAME_SHORT_VERSION", (Object)versionCode);
                        this.finalDO.updateBlindly(mdAppDetailsRow);
                    }
                }
            }
        }
        return mdAppDetailsRow;
    }
    
    public boolean updateAppControllStatus(final List<Long> groupid, final Boolean isAllowed) {
        Boolean success = Boolean.TRUE;
        final UpdateQuery uQuery = (UpdateQuery)new UpdateQueryImpl("MdAppControlStatus");
        uQuery.setCriteria(new Criteria(new Column("MdAppControlStatus", "APP_GROUP_ID"), (Object)groupid.toArray(), 8));
        uQuery.setUpdateColumn("IS_ALLOWED", (Object)isAllowed);
        try {
            MDMUtil.getPersistence().update(uQuery);
            if (isAllowed) {
                this.deleteBlacklistAppInRes(groupid);
            }
            else {
                this.addorUpdateBlacklistAppInResource(groupid);
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(AppDataHandler.class.getName()).log(Level.SEVERE, null, ex);
            success = Boolean.FALSE;
        }
        return success;
    }
    
    private void deleteBlacklistAppInRes(final List groupid) throws DataAccessException {
        final Criteria cGroup = new Criteria(new Column("MdBlackListAppInResource", "APP_GROUP_ID"), (Object)groupid.toArray(), 8);
        DataAccess.delete(cGroup);
        final Criteria cNotify = new Criteria(new Column("MdAppBlackListNotify", "APP_GROUP_ID"), (Object)groupid.toArray(), 8);
        DataAccess.delete(cNotify);
    }
    
    private void addOrUpdateBlackListedAppsInResource(final Long resourceID, final Row mdAppDetailsRow, final Row appGroupRow, Row appControlStatusRow) throws DataAccessException {
        DataObject blackListDO = MDMUtil.getPersistence().constructDataObject();
        if (appControlStatusRow == null) {
            final Criteria appControlCriteria = new Criteria(Column.getColumn("MdAppControlStatus", "APP_GROUP_ID"), (Object)appGroupRow.get("APP_GROUP_ID"), 0);
            if (this.existAppDO != null && !this.existAppDO.isEmpty()) {
                appControlStatusRow = this.existAppDO.getRow("MdAppControlStatus", appControlCriteria);
            }
            else if (this.existAppDO == null) {
                final DataObject dataObject = MDMUtil.getPersistence().get("MdAppControlStatus", appControlCriteria);
                if (!dataObject.isEmpty()) {
                    appControlStatusRow = dataObject.getRow("MdAppControlStatus");
                }
            }
        }
        if (appGroupRow != null && appControlStatusRow != null) {
            final boolean isAllowedPrivateVariable = (boolean)appControlStatusRow.get("IS_ALLOWED");
            if (appGroupRow.hasUVGColInPK() && !isAllowedPrivateVariable) {
                final Row blackListAppRow = new Row("MdBlackListAppInResource");
                blackListAppRow.set("APP_GROUP_ID", appControlStatusRow.get("APP_GROUP_ID"));
                blackListAppRow.set("RESOURCE_ID", (Object)resourceID);
                blackListAppRow.set("APP_ID", mdAppDetailsRow.get("APP_ID"));
                blackListAppRow.set("DETECTED_AT", (Object)new Long(System.currentTimeMillis()));
                blackListAppRow.set("IS_TAKEN_FOR_NOTIFY", (Object)Boolean.TRUE);
                this.finalDO.addRow(blackListAppRow);
                final Row blacklistNotify = new Row("MdAppBlackListNotify");
                blacklistNotify.set("RESOURCE_ID", (Object)resourceID);
                blacklistNotify.set("APP_GROUP_ID", appControlStatusRow.get("APP_GROUP_ID"));
                blacklistNotify.set("LAST_NOTIFIED_TIME", (Object)0L);
                blacklistNotify.set("NO_OF_NOTIFIED_COUNT", (Object)0);
                blacklistNotify.set("NEXT_NOTIFICATION_TIME", (Object)0L);
                blacklistNotify.set("IS_EXPIRED", (Object)Boolean.FALSE);
                blacklistNotify.set("IS_EXPIRED_TIME", (Object)0L);
                this.finalDO.addRow(blacklistNotify);
                this.blwlLogger.log(Level.INFO, "Blacklist apps detected. Resource ID => {0}", resourceID);
                this.blwlLogger.log(Level.INFO, "Blacklist apps App Group ID : {0}", blackListAppRow.get("APP_GROUP_ID"));
            }
            else if (!isAllowedPrivateVariable && !appGroupRow.hasUVGColInPK()) {
                final Criteria allowedCriteria = new Criteria(Column.getColumn("MdBlackListAppInResource", "APP_GROUP_ID"), (Object)appGroupRow.get("APP_GROUP_ID"), 0);
                final Criteria resourceCriteria = new Criteria(Column.getColumn("MdBlackListAppInResource", "RESOURCE_ID"), (Object)resourceID, 0);
                final Criteria criteria = allowedCriteria.and(resourceCriteria);
                if (this.existResourceRelDO != null && (this.existResourceRelDO.isEmpty() || this.existResourceRelDO.getRow("MdBlackListAppInResource", criteria) == null)) {
                    final Row blackListAppRow2 = new Row("MdBlackListAppInResource");
                    blackListAppRow2.set("APP_GROUP_ID", (Object)appControlStatusRow.get("APP_GROUP_ID"));
                    blackListAppRow2.set("RESOURCE_ID", (Object)resourceID);
                    blackListAppRow2.set("APP_ID", mdAppDetailsRow.get("APP_ID"));
                    blackListAppRow2.set("DETECTED_AT", (Object)new Long(System.currentTimeMillis()));
                    blackListAppRow2.set("IS_TAKEN_FOR_NOTIFY", (Object)Boolean.TRUE);
                    this.finalDO.addRow(blackListAppRow2);
                    final Row blacklistNotify2 = new Row("MdAppBlackListNotify");
                    blacklistNotify2.set("RESOURCE_ID", (Object)resourceID);
                    blacklistNotify2.set("APP_GROUP_ID", appControlStatusRow.get("APP_GROUP_ID"));
                    blacklistNotify2.set("LAST_NOTIFIED_TIME", (Object)0L);
                    blacklistNotify2.set("NO_OF_NOTIFIED_COUNT", (Object)0);
                    blacklistNotify2.set("NEXT_NOTIFICATION_TIME", (Object)0L);
                    blacklistNotify2.set("IS_EXPIRED", (Object)Boolean.FALSE);
                    blacklistNotify2.set("IS_EXPIRED_TIME", (Object)0L);
                    this.finalDO.addRow(blacklistNotify2);
                    this.blwlLogger.log(Level.INFO, "Blacklist apps detected. Resource ID => {0}", resourceID);
                    this.blwlLogger.log(Level.INFO, "Blacklist apps App Group ID : {0}", blackListAppRow2.get("APP_GROUP_ID"));
                }
                else if (this.existResourceRelDO == null) {
                    blackListDO = MDMUtil.getPersistence().get("MdBlackListAppInResource", criteria);
                    if (blackListDO.isEmpty() && this.finalDO.getRow("MdBlackListAppInResource", criteria) == null) {
                        final Row blackListAppRow2 = new Row("MdBlackListAppInResource");
                        blackListAppRow2.set("APP_GROUP_ID", (Object)appControlStatusRow.get("APP_GROUP_ID"));
                        blackListAppRow2.set("RESOURCE_ID", (Object)resourceID);
                        blackListAppRow2.set("APP_ID", mdAppDetailsRow.get("APP_ID"));
                        blackListAppRow2.set("DETECTED_AT", (Object)new Long(System.currentTimeMillis()));
                        blackListAppRow2.set("IS_TAKEN_FOR_NOTIFY", (Object)Boolean.TRUE);
                        blackListAppRow2.set("NOTIFY_ADMIN", (Object)Boolean.FALSE);
                        this.finalDO.addRow(blackListAppRow2);
                        final Row blacklistNotify2 = new Row("MdAppBlackListNotify");
                        blacklistNotify2.set("RESOURCE_ID", (Object)resourceID);
                        blacklistNotify2.set("APP_GROUP_ID", appControlStatusRow.get("APP_GROUP_ID"));
                        blacklistNotify2.set("LAST_NOTIFIED_TIME", (Object)0L);
                        blacklistNotify2.set("NO_OF_NOTIFIED_COUNT", (Object)0);
                        blacklistNotify2.set("NEXT_NOTIFICATION_TIME", (Object)0L);
                        blacklistNotify2.set("IS_EXPIRED", (Object)Boolean.FALSE);
                        blacklistNotify2.set("IS_EXPIRED_TIME", (Object)0L);
                        this.finalDO.addRow(blacklistNotify2);
                        this.blwlLogger.log(Level.INFO, "Blacklist apps detected. Resource ID => {0}", resourceID);
                        this.blwlLogger.log(Level.INFO, "Blacklist apps App Group ID : {0}", blackListAppRow2.get("APP_GROUP_ID"));
                    }
                }
                else {
                    final List tableList = new ArrayList();
                    tableList.add("MdBlackListAppInResource");
                    final DataObject dummyDO = this.getDataObject(this.existResourceRelDO, tableList);
                    this.finalDO.merge(dummyDO);
                    final Row blackListAppRow3 = this.finalDO.getRow("MdBlackListAppInResource", criteria);
                    if (blackListAppRow3 != null && !this.isNotificationExpire(blackListAppRow3)) {
                        this.finalDO.updateRow(blackListAppRow3);
                    }
                }
            }
        }
    }
    
    private boolean isNotificationExpire(final Row blacklistAppInResourceRow) {
        boolean isNotify = true;
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppBlackListNotify"));
        final Join blacklistAppInResJoin = new Join("MdAppBlackListNotify", "MdBlackListAppInResource", new String[] { "RESOURCE_ID", "APP_GROUP_ID" }, new String[] { "RESOURCE_ID", "APP_GROUP_ID" }, 2);
        final Criteria resCriteria = new Criteria(Column.getColumn("MdAppBlackListNotify", "RESOURCE_ID"), blacklistAppInResourceRow.get("RESOURCE_ID"), 0);
        final Criteria appGroupCriteria = new Criteria(Column.getColumn("MdAppBlackListNotify", "APP_GROUP_ID"), blacklistAppInResourceRow.get("APP_GROUP_ID"), 0);
        sQuery.addJoin(blacklistAppInResJoin);
        sQuery.setCriteria(resCriteria.and(appGroupCriteria));
        sQuery.addSelectColumn(Column.getColumn("MdAppBlackListNotify", "*"));
        try {
            final DataObject appDO = MDMUtil.getPersistence().get(sQuery);
            final Iterator appIterator = appDO.getRows("MdAppBlackListNotify");
            while (appIterator.hasNext()) {
                final Row appRow = appIterator.next();
                isNotify = (boolean)appRow.get("IS_EXPIRED");
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return isNotify;
    }
    
    private DataObject getDataObject(final DataObject dataObject, final List tableList) throws DataAccessException {
        final DataObject tempDO = (DataObject)dataObject.clone();
        ((WritableDataObject)tempDO).clearOperations();
        final WritableDataObject returnDataObject = new WritableDataObject();
        if (tableList.size() > 0 && !tempDO.isEmpty()) {
            for (int i = 0; i < tableList.size(); ++i) {
                final String tableName = tableList.get(i);
                final Iterator iterator = tempDO.getRows(tableName);
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    returnDataObject.addRow(row);
                }
            }
        }
        returnDataObject.clearOperations();
        return (DataObject)returnDataObject;
    }
    
    private void deleteBlacklistAppInRes(final Long deviceId) throws DataAccessException {
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("MdInstalledAppResourceRel");
        deleteQuery.addJoin(new Join("MdInstalledAppResourceRel", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        deleteQuery.addJoin(new Join("MdAppToGroupRel", "BlacklistAppToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        final Criteria collnCriteria = new Criteria(Column.getColumn("BlacklistAppToCollection", "COLLECTION_ID"), (Object)Column.getColumn("BlacklistAppCollectionStatus", "COLLECTION_ID"), 0);
        final Criteria resCriteria = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "RESOURCE_ID"), (Object)Column.getColumn("BlacklistAppCollectionStatus", "RESOURCE_ID"), 0);
        final Criteria scopeCriteria = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "SCOPE"), (Object)Column.getColumn("BlacklistAppCollectionStatus", "SCOPE"), 0);
        deleteQuery.addJoin(new Join("BlacklistAppToCollection", "BlacklistAppCollectionStatus", collnCriteria.and(resCriteria).and(scopeCriteria), 2));
        final Criteria deviceCriteria = new Criteria(new Column("MdInstalledAppResourceRel", "RESOURCE_ID"), (Object)deviceId, 0);
        final Criteria successCriteria = new Criteria(new Column("BlacklistAppCollectionStatus", "STATUS"), (Object)4, 0);
        deleteQuery.setCriteria(deviceCriteria.and(successCriteria));
        MDMUtil.getPersistence().delete(deleteQuery);
    }
    
    private void addorUpdateBlacklistAppInResource(final List<Long> groupid) throws Exception {
        this.finalDO = MDMUtil.getPersistence().constructDataObject();
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
        sQuery.addJoin(new Join("MdAppGroupDetails", "MdAppToGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        sQuery.addJoin(new Join("MdAppToGroupRel", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        sQuery.addJoin(new Join("MdAppDetails", "MdInstalledAppResourceRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        sQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "*"));
        sQuery.addSelectColumn(Column.getColumn("MdAppToGroupRel", "*"));
        sQuery.addSelectColumn(Column.getColumn("MdAppDetails", "*"));
        sQuery.addSelectColumn(Column.getColumn("MdInstalledAppResourceRel", "*"));
        sQuery.setCriteria(new Criteria(new Column("MdAppGroupDetails", "APP_GROUP_ID"), (Object)groupid.toArray(), 8));
        final DataObject DO = MDMUtil.getPersistence().get(sQuery);
        if (!DO.isEmpty()) {
            for (final Long appGroupId : groupid) {
                final Criteria cGroupId = new Criteria(new Column("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroupId, 0);
                final Row appGroupRow = DO.getRow("MdAppGroupDetails", cGroupId);
                final Criteria cGroupRel = new Criteria(new Column("MdAppToGroupRel", "APP_GROUP_ID"), (Object)appGroupId, 0);
                final Iterator appRelIter = DO.getRows("MdAppToGroupRel", cGroupRel);
                while (appRelIter.hasNext()) {
                    final Row appRelRow = appRelIter.next();
                    final Long appid = (Long)appRelRow.get("APP_ID");
                    final Criteria cAppid = new Criteria(new Column("MdAppDetails", "APP_ID"), (Object)appid, 0);
                    final Row appRow = DO.getRow("MdAppDetails", cAppid);
                    final Criteria cInstallAppid = new Criteria(new Column("MdInstalledAppResourceRel", "APP_ID"), (Object)appid, 0);
                    final Iterator resRowIter = DO.getRows("MdInstalledAppResourceRel", cInstallAppid);
                    while (resRowIter.hasNext()) {
                        final Row installedApp = resRowIter.next();
                        final Long resId = (Long)installedApp.get("RESOURCE_ID");
                        final Integer n = (Integer)installedApp.get("SCOPE");
                    }
                }
            }
        }
        MDMUtil.getPersistence().update(this.finalDO);
    }
    
    public HashMap processAppRepositoryData(final HashMap appHash) throws Exception {
        this.finalDO = MDMUtil.getPersistence().constructDataObject();
        this.isAllowed = Boolean.TRUE;
        final Properties properties = this.getAppPropertyFromHashMap(appHash);
        AppsUtil.logger.log(Level.INFO, "Beginning to processAppRepositoryData of app:  {0} for customer {1}", new Object[] { ((Hashtable<K, Object>)properties).get("IDENTIFIER"), ((Hashtable<K, Object>)properties).get("CUSTOMER_ID") });
        this.getExistDataObjectForAppRep(((Hashtable<K, String>)properties).get("IDENTIFIER"), ((Hashtable<K, Long>)properties).get("CUSTOMER_ID"), ((Hashtable<K, Integer>)properties).get("PLATFORM_TYPE"));
        Row mdAppGroupRow = this.addOrUpdateAppGroup(properties);
        Row mdAppDetailsRow = this.addOrUpdateAppDetails(properties);
        this.addOrUpdateAppCategoryRel(mdAppGroupRow, properties);
        this.addOrUpdateAppGroupRel(mdAppGroupRow, mdAppDetailsRow);
        MDMUtil.getPersistence().update(this.finalDO);
        mdAppDetailsRow = this.finalDO.getFirstRow("MdAppDetails");
        if (this.finalDO.containsTable("MdAppGroupDetails")) {
            mdAppGroupRow = this.finalDO.getFirstRow("MdAppGroupDetails");
        }
        final Long appId = (Long)mdAppDetailsRow.get("APP_ID");
        final Long appGroupId = (Long)mdAppGroupRow.get("APP_GROUP_ID");
        AppsUtil.logger.log(Level.INFO, "AppRepository details processed. Updated AppDetails: appID: {0} and appGroupID: {1}", new Object[] { appId, appGroupId });
        appHash.put("APP_ID", appId);
        appHash.put("APP_GROUP_ID", appGroupId);
        return appHash;
    }
    
    public void addAppLatestVersion(final Long appGroupID, final Properties latestMetaData, final Long customerID, final int platformType) throws Exception {
        final String bundleId = ((Hashtable<K, String>)latestMetaData).get("IDENTIFIER");
        final String[] identifiers = { bundleId };
        this.getAppExistingDataObject(identifiers, customerID, platformType, 0);
        this.finalDO = MDMUtil.getPersistence().constructDataObject();
        final Row mdAppGroupRow = this.existAppDO.getFirstRow("MdAppGroupDetails");
        final Row mdAppDetailsRow = this.addOrUpdateAppDetails(latestMetaData);
        this.addOrUpdateAppGroupRel(mdAppGroupRow, mdAppDetailsRow);
        this.updatePackageToAppDataRow(mdAppGroupRow, latestMetaData);
        this.updatePackageToAppGroupRow(mdAppGroupRow, latestMetaData);
        MDMUtil.getPersistence().update(this.finalDO);
    }
    
    private void updatePackageToAppDataRow(final Row mdAppGroupRow, final Properties latestMetaData) {
        try {
            final Long appGroupId = (Long)mdAppGroupRow.get("APP_GROUP_ID");
            final int supportedDevices = ((Hashtable<K, Integer>)latestMetaData).get("SUPPORTED_DEVICES");
            final String displayImg = ((Hashtable<K, String>)latestMetaData).get("DISPLAY_IMAGE_LOC");
            final Criteria appGroupCri = new Criteria(new Column("MdPackageToAppData", "APP_GROUP_ID"), (Object)appGroupId, 0);
            final Criteria supportedDeviceCri = new Criteria(new Column("MdPackageToAppData", "SUPPORTED_DEVICES"), (Object)supportedDevices, 7);
            if (!MDMStringUtils.isEmpty(displayImg)) {
                final Iterator iter = this.existAppDO.getRows("MdPackageToAppData", appGroupCri);
                while (iter.hasNext()) {
                    final Row packageToAppDataRow = iter.next();
                    packageToAppDataRow.set("DISPLAY_IMAGE_LOC", (Object)displayImg);
                    this.finalDO.updateBlindly(packageToAppDataRow);
                }
            }
            final Iterator iter2 = this.existAppDO.getRows("MdPackageToAppData", appGroupCri.and(supportedDeviceCri));
            while (iter2.hasNext()) {
                final Row packageToAppDataRow = iter2.next();
                packageToAppDataRow.set("SUPPORTED_DEVICES", (Object)supportedDevices);
                this.finalDO.updateBlindly(packageToAppDataRow);
            }
        }
        catch (final Exception ex) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, "AppDataHandler: Exception while updatePackageToAppDataRow() ", ex);
        }
    }
    
    private void updatePackageToAppGroupRow(final Row mdAppGroupRow, final Properties latestMetaData) {
        try {
            final Long appGroupId = (Long)mdAppGroupRow.get("APP_GROUP_ID");
            final int privateAppType = ((Hashtable<K, Integer>)latestMetaData).get("PRIVATE_APP_TYPE");
            final Criteria appGroupCri = new Criteria(new Column("MdPackageToAppGroup", "APP_GROUP_ID"), (Object)appGroupId, 0);
            final Criteria privateAppTypeCri = new Criteria(new Column("MdPackageToAppGroup", "PRIVATE_APP_TYPE"), (Object)privateAppType, 1);
            final Iterator iter = this.existAppDO.getRows("MdPackageToAppGroup", appGroupCri.and(privateAppTypeCri));
            while (iter.hasNext()) {
                final Row packageToAppGroupRow = iter.next();
                packageToAppGroupRow.set("PRIVATE_APP_TYPE", (Object)privateAppType);
                this.finalDO.updateBlindly(packageToAppGroupRow);
            }
        }
        catch (final Exception ex) {
            Logger.getLogger("MDMConfigLogger").log(Level.SEVERE, "AppDataHandler: Exception while updatePackageToAppDataRow() ", ex);
        }
    }
    
    private void setUpdateForAppGroup(final Long appGroupID) throws DataAccessException {
        AppsUtil.logger.log(Level.INFO, "[APP] [UPDATE] [setAppUpdateForApps] Setting App update for App Group {0}", appGroupID);
        AppsUtil.logger.log(Level.INFO, "[APP] [UPDATE] [setAppUpdateForApps] This app group is probably a store app", appGroupID);
        UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("MdAppCatalogToResourceExtn");
        updateQuery.addJoin(new Join("MdAppCatalogToResourceExtn", "MdAppCatalogToResource", new String[] { "APP_GROUP_ID", "RESOURCE_ID" }, new String[] { "APP_GROUP_ID", "RESOURCE_ID" }, 2));
        updateQuery.addJoin(new Join("MdAppCatalogToResource", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        updateQuery.addJoin(new Join("AppGroupToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        final Criteria profileCriteria = new Criteria(Column.getColumn("ProfileToCollection", "PROFILE_ID"), (Object)Column.getColumn("RecentProfileForResource", "PROFILE_ID"), 0);
        final Criteria resourceCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), 0);
        updateQuery.addJoin(new Join("ProfileToCollection", "RecentProfileForResource", profileCriteria.and(resourceCriteria), 2));
        final Criteria collectionCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "COLLECTION_ID"), (Object)Column.getColumn("CollnToResources", "COLLECTION_ID"), 0);
        final Criteria resourceCriteria2 = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)Column.getColumn("CollnToResources", "RESOURCE_ID"), 0);
        updateQuery.addJoin(new Join("RecentProfileForResource", "CollnToResources", collectionCriteria.and(resourceCriteria2), 2));
        final Criteria appGrpCriteria = new Criteria(Column.getColumn("MdAppCatalogToResourceExtn", "APP_GROUP_ID"), (Object)appGroupID, 0);
        final Criteria statusCriteria = new Criteria(Column.getColumn("CollnToResources", "STATUS"), (Object)12, 1);
        updateQuery.setCriteria(appGrpCriteria.and(statusCriteria));
        updateQuery.setUpdateColumn("IS_UPDATE_AVAILABLE", (Object)true);
        updateQuery.setUpdateColumn("PUBLISHED_APP_SOURCE", (Object)MDMCommonConstants.UNASSIGNED_APP_UPDATE);
        MDMUtil.getPersistence().update(updateQuery);
        updateQuery = (UpdateQuery)new UpdateQueryImpl("MdAppCatalogToGroup");
        updateQuery.setCriteria(new Criteria(Column.getColumn("MdAppCatalogToGroup", "APP_GROUP_ID"), (Object)appGroupID, 0));
        updateQuery.setUpdateColumn("IS_UPDATE_AVAILABLE", (Object)true);
        final UpdateQuery updateQuery2 = (UpdateQuery)new UpdateQueryImpl("MdAppCatalogToResourceExtn");
        updateQuery2.setCriteria(new Criteria(Column.getColumn("MdAppCatalogToResourceExtn", "APP_GROUP_ID"), (Object)appGroupID, 0));
        updateQuery2.setUpdateColumn("PUBLISHED_APP_SOURCE", (Object)MDMCommonConstants.UNASSIGNED_APP_UPDATE);
        MDMUtil.getPersistence().update(updateQuery2);
        MDMUtil.getPersistence().update(updateQuery);
    }
    
    private void getExistDataObjectForAppRep(final String identifiers, final Long customerId, final int platform) throws DataAccessException {
        AppsUtil.logger.log(Level.INFO, "Inside getExistDataObjectForAppRep - getting existing app details of app: {0} for customer{1}", new Object[] { identifiers, customerId });
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppGroupDetails"));
        final Join appGroupRelJoin = new Join("MdAppGroupDetails", "MdAppToGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
        final Join appDetailsJoin = new Join("MdAppToGroupRel", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2);
        selectQuery.addJoin(appGroupRelJoin);
        selectQuery.addJoin(appDetailsJoin);
        final Boolean isCaseSensitive = AppsUtil.getInstance().getIsBundleIdCaseSenstive(platform);
        final Criteria identifierCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)identifiers, 0, (boolean)isCaseSensitive);
        final Criteria customerCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria platformCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)platform, 0);
        selectQuery.setCriteria(identifierCriteria.and(customerCriteria).and(platformCriteria));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "GROUP_DISPLAY_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppToGroupRel", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppToGroupRel", "APP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_VERSION"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_NAME_SHORT_VERSION"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppDetails", "IDENTIFIER"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppDetails", "EXTERNAL_APP_VERSION_ID"));
        this.existAppDO = MDMUtil.getPersistence().get(selectQuery);
        if (this.existAppDO.isEmpty()) {
            AppsUtil.logger.log(Level.INFO, "No existing AppGroupDetails for app: {0}", new Object[] { identifiers });
        }
    }
    
    private Properties getAppPropertyFromHashMap(final HashMap appHash) {
        final Properties appRepMap = new Properties();
        ((Hashtable<String, Object>)appRepMap).put("APP_NAME", appHash.get("APP_NAME"));
        ((Hashtable<String, String>)appRepMap).put("APP_VERSION", appHash.getOrDefault("APP_VERSION", "--"));
        final String appShortVersion = appHash.getOrDefault("APP_NAME_SHORT_VERSION", "--");
        final String appTitle = appHash.get("APP_TITLE");
        final String packageIdentifier = appHash.get("packageIdentifier");
        final Boolean isModernApp = appHash.containsKey("IS_MODERN_APP") ? appHash.get("IS_MODERN_APP") : Boolean.TRUE;
        if (appTitle != null) {
            ((Hashtable<String, String>)appRepMap).put("APP_TITLE", appTitle);
        }
        if (appShortVersion != null) {
            ((Hashtable<String, String>)appRepMap).put("APP_NAME_SHORT_VERSION", appShortVersion);
        }
        else {
            ((Hashtable<String, String>)appRepMap).put("APP_NAME_SHORT_VERSION", "--");
        }
        if (packageIdentifier != null) {
            ((Hashtable<String, String>)appRepMap).put("packageIdentifier", packageIdentifier);
        }
        final String bundleIdentifier = appHash.get("BUNDLE_IDENTIFIER");
        ((Hashtable<String, String>)appRepMap).put("IDENTIFIER", bundleIdentifier.trim());
        final Long bundleSize = appHash.get("BUNDLE_SIZE");
        if (bundleSize != null) {
            ((Hashtable<String, Long>)appRepMap).put("BUNDLE_SIZE", bundleSize);
        }
        else {
            ((Hashtable<String, Long>)appRepMap).put("BUNDLE_SIZE", 0L);
        }
        ((Hashtable<String, Long>)appRepMap).put("APP_CATEGORY_ID", appHash.get("APP_CATEGORY_ID"));
        ((Hashtable<String, Long>)appRepMap).put("CUSTOMER_ID", appHash.get("CUSTOMER_ID"));
        ((Hashtable<String, Integer>)appRepMap).put("APP_TYPE", 1);
        ((Hashtable<String, Integer>)appRepMap).put("PLATFORM_TYPE", appHash.get("PLATFORM_TYPE"));
        ((Hashtable<String, Boolean>)appRepMap).put("NOTIFY_ADMIN", Boolean.FALSE);
        ((Hashtable<String, Boolean>)appRepMap).put("IS_MODERN_APP", isModernApp);
        ((Hashtable<String, Long>)appRepMap).put("EXTERNAL_APP_VERSION_ID", appHash.getOrDefault("EXTERNAL_APP_VERSION_ID", 0L));
        return appRepMap;
    }
    
    private boolean getAppControlStatus(final Long customerID) throws DataAccessException {
        final ReadOnlyPersistence cachedPersistence = MDMUtil.getCachedPersistence();
        final DataObject dataObject = cachedPersistence.get("MdAppBlackListSetting", (Criteria)null);
        if (!dataObject.isEmpty()) {
            final Criteria cCustomerId = new Criteria(new Column("MdAppBlackListSetting", "CUSTOMER_ID"), (Object)customerID, 0);
            final Row settingsRow = dataObject.getRow("MdAppBlackListSetting", cCustomerId);
            return (boolean)settingsRow.get("IS_WHITE_LIST");
        }
        return Boolean.TRUE;
    }
    
    private void UpdateManagedAgentDetails(final Long resourceId) {
        try {
            final Properties properties = new Properties();
            ((Hashtable<String, Long>)properties).put("RESOURCE_ID", resourceId);
            ((Hashtable<String, Long>)properties).put("AGENT_VERSION_CODE", -1L);
            ((Hashtable<String, String>)properties).put("AGENT_VERSION", "--");
            ((Hashtable<String, String>)properties).put("NOTIFIED_AGENT_VERSION", "--");
            ManagedDeviceHandler.getInstance().updateManagedDeviceDetails(properties);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    private void checkAndModifyPropertiesForiOSEnterpriseApp(final Long appGroupId, final Properties properties) {
        try {
            final Criteria appGroupC = new Criteria(Column.getColumn("MdPackageToAppGroup", "APP_GROUP_ID"), (Object)appGroupId, 0);
            final boolean isEnterpriseAppAndAlreadySwaped = properties.containsKey("PACKAGE_TYPE") && ((Hashtable<K, Integer>)properties).get("PACKAGE_TYPE") == 2;
            final Row row = this.existAppDO.getRow("MdPackageToAppGroup", appGroupC);
            if (row != null) {
                final Integer type = (Integer)row.get("PACKAGE_TYPE");
                if (type != null && type == 2) {
                    if (!isEnterpriseAppAndAlreadySwaped) {
                        final String buildNumber = ((Hashtable<K, String>)properties).get("APP_NAME_SHORT_VERSION");
                        ((Hashtable<String, Object>)properties).put("APP_NAME_SHORT_VERSION", ((Hashtable<K, Object>)properties).get("APP_VERSION"));
                        ((Hashtable<String, String>)properties).put("APP_VERSION", buildNumber);
                        AppsUtil.logger.log(Level.INFO, "Enterprise App , swapping AppVersion and AppShortVersion {0}", properties);
                    }
                    else {
                        AppsUtil.logger.log(Level.INFO, "Enterprise App , already swapped AppVersion and AppShortVersion , so not doing it here {0}", properties);
                    }
                }
                else if (isEnterpriseAppAndAlreadySwaped) {
                    AppsUtil.logger.log(Level.WARNING, "App has External Identifier Version 0 but not an enterprise  App as per repository {0}", properties);
                }
            }
        }
        catch (final Exception e) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, "AppDataHandler: Exception while modifyPropertiesForiOSEnterpriseApp() ", e);
        }
    }
    
    public Boolean isAppUpdateAvailable(final Properties prop) {
        Boolean isAppUpdateAvailable = false;
        try {
            final Long latestAppId = ((Hashtable<K, Long>)prop).get("LATEST_APP_ID");
            final Long installedAppId = ((Hashtable<K, Long>)prop).get("INSTALLED_APP_ID");
            final Long publishedAppId = ((Hashtable<K, Long>)prop).get("PUBLISHED_APP_ID");
            final String installedAppVersion = ((Hashtable<K, String>)prop).get("INSTALLED_APP_VERSION");
            final String latestAppVersion = ((Hashtable<K, String>)prop).get("LATEST_APP_VERSION");
            if (latestAppId.compareTo(publishedAppId) != 0) {
                isAppUpdateAvailable = true;
                if (installedAppId != null && latestAppVersion != null) {
                    if (installedAppId.compareTo(latestAppId) != 0) {
                        final IOSAppVersionChecker checkVersion = new IOSAppVersionChecker();
                        final JSONObject latestAppVersionjson = new JSONObject();
                        latestAppVersionjson.put("APP_VERSION", (Object)latestAppVersion);
                        final JSONObject installedAppVersionJson = new JSONObject();
                        installedAppVersionJson.put("APP_VERSION", (Object)installedAppVersion);
                        isAppUpdateAvailable = checkVersion.isAppVersionGreater(latestAppVersionjson, installedAppVersionJson);
                    }
                    else {
                        isAppUpdateAvailable = false;
                    }
                }
            }
        }
        catch (final Exception ex) {
            AppsUtil.logger.log(Level.WARNING, "Exception occoured in isAppUpdateAvailable....{0}", ex);
        }
        return isAppUpdateAvailable;
    }
    
    public int getTheUpdateAvailableAppsCount(final Long customerId, final boolean onlyStoreApps) {
        List updateAvailableApps = null;
        updateAvailableApps = this.getListofAppsWithUpdate(customerId, true, onlyStoreApps, false);
        return updateAvailableApps.size();
    }
    
    public List getListofAppsWithUpdate(final Long customerID) {
        return this.getListofAppsWithUpdate(customerID, true);
    }
    
    public List getListofAppsWithUpdate(final Long customerID, final boolean notify) {
        return this.getListofAppsWithUpdate(customerID, notify, false, false);
    }
    
    public List getListofAppsWithUpdate(final Long customerID, final boolean notify, final boolean onlyStoreApps, final boolean includeScheduledApps) {
        final List appUpdateList = new ArrayList();
        try {
            final List trashedApps = new AppTrashModeHandler().getAppGroupsInTrash(customerID);
            final List managedApps = this.getManagedApps(customerID);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdPackage"));
            final Join packageJoin = new Join("MdPackage", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2);
            final Join resourceJoinJoin = new Join("MdPackageToAppGroup", "MdAppCatalogToResourceExtn", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
            final Join managedJoin = new Join("MdAppCatalogToResourceExtn", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            final Join appGroupJoin = new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
            selectQuery.addJoin(packageJoin);
            selectQuery.addJoin(resourceJoinJoin);
            selectQuery.addJoin(managedJoin);
            selectQuery.addJoin(appGroupJoin);
            selectQuery.addJoin(new Join("MdAppCatalogToResourceExtn", "MdAppCatalogToResource", new String[] { "RESOURCE_ID", "APP_GROUP_ID" }, new String[] { "RESOURCE_ID", "APP_GROUP_ID" }, 2));
            selectQuery.addSelectColumn(Column.getColumn("MdAppCatalogToResourceExtn", "APP_GROUP_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdAppCatalogToResourceExtn", "RESOURCE_ID"));
            final Criteria custIdCri = new Criteria(Column.getColumn("MdPackage", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria sourceCri = new Criteria(Column.getColumn("MdAppCatalogToResourceExtn", "PUBLISHED_APP_SOURCE"), (Object)new Integer[] { MDMCommonConstants.ASSOCIATED_APP_SOURCE_BY_USER, MDMCommonConstants.ASSOCIATED_APP_SOURCE_UNKNOWN }, 8);
            final Criteria updateAvailable = new Criteria(Column.getColumn("MdAppCatalogToResourceExtn", "IS_UPDATE_AVAILABLE"), (Object)true, 0);
            final Criteria appUpdateNotScheduledCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "APPROVED_VERSION_STATUS"), (Object)2, 1);
            final Criteria macAgentCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)"com.manageengine.mdm.mac", 1);
            final List noUpdateList = new ArrayList();
            if (notify) {
                selectQuery.setCriteria(custIdCri.and(sourceCri));
                final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
                final Iterator iterator = dataObject.getRows("MdAppCatalogToResourceExtn");
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final Long noUpdateAppGrpID = (Long)row.get("APP_GROUP_ID");
                    if (!noUpdateList.contains(noUpdateAppGrpID)) {
                        noUpdateList.add(noUpdateAppGrpID);
                    }
                }
            }
            final Criteria managedCri = MDMUtil.getInstance().getSuccessfullyEnrolledCriteria();
            if (notify) {
                final Criteria updateCri = new Criteria(Column.getColumn("MdAppCatalogToResourceExtn", "APP_GROUP_ID"), (Object)noUpdateList.toArray(), 9);
                selectQuery.setCriteria(updateCri.and(custIdCri).and(updateAvailable).and(managedCri).and(macAgentCriteria));
            }
            else {
                selectQuery.setCriteria(custIdCri.and(updateAvailable).and(managedCri).and(macAgentCriteria));
            }
            if (!includeScheduledApps) {
                final Criteria finalCriteria = MDMDBUtil.andCriteria(selectQuery.getCriteria(), appUpdateNotScheduledCriteria);
                selectQuery.setCriteria(finalCriteria);
            }
            final DataObject dataObject2 = MDMUtil.getPersistence().get(selectQuery);
            final Iterator iterator2 = dataObject2.getRows("MdAppCatalogToResourceExtn");
            while (iterator2.hasNext()) {
                final Row row2 = iterator2.next();
                final Long curAppGrpID = (Long)row2.get("APP_GROUP_ID");
                if (!appUpdateList.contains(curAppGrpID)) {
                    appUpdateList.add(curAppGrpID);
                }
            }
            appUpdateList.removeAll(trashedApps);
            appUpdateList.retainAll(managedApps);
            if (onlyStoreApps) {
                appUpdateList.retainAll(this.getStoreApps(customerID));
            }
        }
        catch (final DataAccessException e) {
            AppsUtil.logger.log(Level.WARNING, "error in getting list of apps with updates ", (Throwable)e);
        }
        return appUpdateList;
    }
    
    private List getStoreApps(final Long customerId) throws DataAccessException {
        final List appGrpIDs = new ArrayList();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdPackageToAppGroup"));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "PACKAGE_ID"));
        selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdPackage", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        final Criteria custCriteria = new Criteria(new Column("MdPackage", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria appTypeCriteria = new Criteria(new Column("MdPackageToAppGroup", "PACKAGE_TYPE"), (Object)new Integer[] { 0, 1 }, 8);
        selectQuery.setCriteria(custCriteria.and(appTypeCriteria));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        final Iterator iterator = dataObject.getRows("MdPackageToAppGroup");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final Long appGrpID = (Long)row.get("APP_GROUP_ID");
            if (!appGrpIDs.contains(appGrpID)) {
                appGrpIDs.add(appGrpID);
            }
        }
        return appGrpIDs;
    }
    
    private List getManagedApps(final Long customerId) throws DataAccessException {
        final List appGrpIDs = new ArrayList();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdPackageToAppGroup"));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "PACKAGE_ID"));
        selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdPackage", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        selectQuery.setCriteria(new Criteria(new Column("MdPackage", "CUSTOMER_ID"), (Object)customerId, 0));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        final Iterator iterator = dataObject.getRows("MdPackageToAppGroup");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final Long appGrpID = (Long)row.get("APP_GROUP_ID");
            if (!appGrpIDs.contains(appGrpID)) {
                appGrpIDs.add(appGrpID);
            }
        }
        return appGrpIDs;
    }
    
    public List<Long> getStoreAppPackagesWithUpdate(final Long customerId) {
        final List<Long> packageList = new ArrayList<Long>();
        final List<Long> appgroupIds = this.getYettoUpdateApps(customerId);
        if (appgroupIds != null && !appgroupIds.isEmpty()) {
            try {
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdPackageToAppGroup"));
                selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "APP_GROUP_ID"));
                selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "PACKAGE_ID"));
                selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdPackage", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
                final Criteria customerCriteria = new Criteria(new Column("MdPackage", "CUSTOMER_ID"), (Object)customerId, 0);
                final Criteria appGroupCriteria = new Criteria(new Column("MdPackageToAppGroup", "APP_GROUP_ID"), (Object)appgroupIds.toArray(), 8);
                selectQuery.setCriteria(appGroupCriteria.and(customerCriteria));
                final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
                final Iterator iterator = dataObject.getRows("MdPackageToAppGroup");
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final Long packageId = (Long)row.get("PACKAGE_ID");
                    if (!packageList.contains(packageId)) {
                        packageList.add(packageId);
                    }
                }
            }
            catch (final DataAccessException e) {
                AppsUtil.logger.log(Level.SEVERE, "cannot fetch list of package for app group");
            }
        }
        return packageList;
    }
    
    private List<Long> getYettoUpdateApps(final Long customerId) {
        final List<Long> appGroupList = new ArrayList<Long>();
        try {
            final Set<Long> appSet = new HashSet<Long>();
            SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppCatalogToGroup"));
            selectQuery.addJoin(new Join("MdAppCatalogToGroup", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdPackage", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
            final Criteria custCriteria = new Criteria(new Column("MdPackage", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria appTypeCriteria = new Criteria(new Column("MdPackageToAppGroup", "PACKAGE_TYPE"), (Object)new Integer[] { 0, 1 }, 8);
            final Criteria updateCri = new Criteria(new Column("MdAppCatalogToGroup", "IS_UPDATE_AVAILABLE"), (Object)Boolean.TRUE, 0);
            selectQuery.setCriteria(custCriteria.and(appTypeCriteria).and(updateCri));
            Column column = new Column("MdPackageToAppGroup", "APP_GROUP_ID").distinct();
            column.setColumnAlias("APP_GROUP");
            selectQuery.addSelectColumn(column);
            final DMDataSetWrapper dataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (dataSetWrapper.next()) {
                appSet.add((Long)dataSetWrapper.getValue("APP_GROUP"));
            }
            selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppCatalogToResourceExtn"));
            selectQuery.addJoin(new Join("MdAppCatalogToResourceExtn", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdPackage", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
            selectQuery.addJoin(new Join("MdAppCatalogToResourceExtn", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria resourceupdateCri = new Criteria(new Column("MdAppCatalogToResourceExtn", "IS_UPDATE_AVAILABLE"), (Object)Boolean.TRUE, 0);
            selectQuery.setCriteria(custCriteria.and(appTypeCriteria).and(resourceupdateCri).and(ManagedDeviceHandler.getInstance().getSuccessfullyEnrolledCriteria()));
            column = new Column("MdPackageToAppGroup", "APP_GROUP_ID").distinct();
            column.setColumnAlias("APP_GROUP");
            selectQuery.addSelectColumn(column);
            final DMDataSetWrapper dataSetWrapper2 = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (dataSetWrapper2.next()) {
                appSet.add((Long)dataSetWrapper2.getValue("APP_GROUP"));
            }
            final List trashedApps = new AppTrashModeHandler().getAppGroupsInTrash(customerId);
            appSet.removeAll(trashedApps);
            appGroupList.addAll(appSet);
        }
        catch (final Exception e) {
            AppsUtil.logger.log(Level.SEVERE, "cannot fetch list of app group for update");
        }
        return appGroupList;
    }
    
    public List<Long> getStoreAppsWithUpdate(final Long customerId) {
        return this.getStoreAppsWithUpdate(customerId, null);
    }
    
    public List<Long> getStoreAppsWithUpdate(final Long customerId, final Integer platformType) {
        final List<Long> appGroupList = new ArrayList<Long>();
        final List<Long> appgroupIds = this.getYettoUpdateApps(customerId);
        if (appgroupIds != null && !appgroupIds.isEmpty()) {
            try {
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdPackageToAppGroup"));
                selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "APP_GROUP_ID"));
                selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "PACKAGE_ID"));
                selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdPackage", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
                final Criteria customerCriteria = new Criteria(new Column("MdPackage", "CUSTOMER_ID"), (Object)customerId, 0);
                final Criteria appGroupCriteria = new Criteria(new Column("MdPackageToAppGroup", "APP_GROUP_ID"), (Object)appgroupIds.toArray(), 8);
                final Criteria appTypeCriteria = new Criteria(new Column("MdPackageToAppGroup", "PACKAGE_TYPE"), (Object)new int[] { 0, 1 }, 8);
                selectQuery.setCriteria(appGroupCriteria.and(customerCriteria).and(appTypeCriteria));
                if (platformType != null) {
                    final Criteria platformCriteria = new Criteria(new Column("MdPackage", "PLATFORM_TYPE"), (Object)platformType, 0);
                    selectQuery.setCriteria(selectQuery.getCriteria().and(platformCriteria));
                }
                final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
                final Iterator iterator = dataObject.getRows("MdPackageToAppGroup");
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final Long appGroupId = (Long)row.get("APP_GROUP_ID");
                    if (!appGroupList.contains(appGroupId)) {
                        appGroupList.add(appGroupId);
                    }
                }
            }
            catch (final DataAccessException e) {
                AppsUtil.logger.log(Level.SEVERE, "cannot fetch list of store app group for update");
            }
        }
        return appGroupList;
    }
    
    public Object addAppsOnProfileCloning(final Properties properties, final DataObject cloneConfigDO) throws DataAccessException {
        this.finalDO = MDMUtil.getPersistence().constructDataObject();
        this.isAllowed = Boolean.TRUE;
        this.getExistDataObjectForAppRep(((Hashtable<K, String>)properties).get("IDENTIFIER"), ((Hashtable<K, Long>)properties).get("CUSTOMER_ID"), ((Hashtable<K, Integer>)properties).get("PLATFORM_TYPE"));
        final Row mdAppGroupRow = this.addOrUpdateAppGroup(properties);
        final Row mdAppDetailsRow = this.addOrUpdateAppDetails(properties);
        this.addOrUpdateAppGroupRel(mdAppGroupRow, mdAppDetailsRow);
        cloneConfigDO.merge(this.finalDO);
        return mdAppGroupRow.get("APP_GROUP_ID");
    }
    
    public static class SummaryJSONConstants
    {
        public static final String ADDED_APPS = "ADDED_APPS";
        public static final String DELETED_APPS = "DELETED_APPS";
        public static final String UPDATED_APPS = "UPDATED_APPS";
        public static final String NOT_CHANGED_APPS = "NOT_CHANGED_APPS";
        public static final String ALL_DETECTED_APPS = "ALL_DETECTED_APPS";
    }
}
