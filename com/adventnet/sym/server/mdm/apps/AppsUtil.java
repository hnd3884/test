package com.adventnet.sym.server.mdm.apps;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessAPI;
import com.me.devicemanagement.framework.server.util.ChecksumProvider;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import java.util.TreeSet;
import com.adventnet.persistence.WritableDataObject;
import com.me.mdm.server.apps.AppVersionChecker;
import com.me.mdm.api.error.APIHTTPException;
import java.util.Set;
import com.adventnet.ds.query.DerivedColumn;
import com.me.idps.core.util.IdpsUtil;
import com.me.devicemanagement.framework.server.scheduler.SchedulerConstants;
import com.me.mdm.api.APIUtil;
import com.me.mdm.server.apps.blacklist.BlacklistAppHandler;
import com.me.mdm.server.apps.appupdatepolicy.AppUpdatesToResourceHandler;
import com.adventnet.sym.server.mdm.util.MDMCommonConstants;
import java.util.Arrays;
import com.me.mdm.server.windows.apps.WpAppSettingsHandler;
import com.adventnet.ds.query.DeleteQuery;
import javax.transaction.SystemException;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.sql.SQLException;
import com.adventnet.ds.query.QueryConstructionException;
import com.me.mdm.server.android.knox.KnoxUtil;
import com.me.mdm.server.notification.NotificationHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.Locale;
import java.util.SortedMap;
import com.adventnet.i18n.I18N;
import java.sql.Connection;
import com.me.devicemanagement.framework.server.customgroup.CustomGroupUtil;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.me.mdm.server.deployment.policy.AppDeploymentPolicyImpl;
import org.json.JSONException;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.ds.query.SortColumn;
import com.me.mdm.server.windows.apps.WpCompanyHubAppHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.persistence.DataAccess;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.ds.query.DataSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.Map;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.persistence.DataAccessException;
import com.me.mdm.server.apps.multiversion.AppVersionDBUtil;
import java.util.List;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.apps.ios.IOSModifiedEnterpriseAppsUtil;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import java.io.IOException;
import com.dd.plist.PropertyListParser;
import com.dd.plist.NSObject;
import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.sym.server.mdm.util.MDMiOSEntrollmentUtil;
import java.util.HashMap;
import java.io.File;
import com.me.mdm.server.apps.constants.AppMgmtConstants;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.logging.Logger;

public class AppsUtil
{
    public static Logger logger;
    public static Logger appMgmtLogger;
    private static AppsUtil appsUtil;
    private ArrayList<Long> androidCategoryMap;
    private ArrayList<Long> iosCategoryMap;
    private ArrayList<Long> windowsCategoryMap;
    private ArrayList<Long> chromeCategoryMap;
    public static final String MDM_API_UTIL_API_TO_CATEGORY_CACHE = "MDM_API_UTIL_API_TO_CATEGORY_CACHE";
    
    public AppsUtil() {
        this.androidCategoryMap = null;
        this.iosCategoryMap = null;
        this.windowsCategoryMap = null;
        this.chromeCategoryMap = null;
    }
    
    public static AppsUtil getInstance() {
        if (AppsUtil.appsUtil == null) {
            AppsUtil.appsUtil = new AppsUtil();
        }
        return AppsUtil.appsUtil;
    }
    
    public JSONArray getAppsDetail(final int packageType, final int platformType) {
        final JSONArray arr = new JSONArray();
        try {
            final Table appgroupdetails = new Table("MdPackageToAppGroup");
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(appgroupdetails);
            query.addJoin(new Join("MdPackageToAppGroup", "MdPackageToAppData", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
            query.addJoin(new Join("MdPackageToAppData", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
            final Column col = new Column("MdPackageToAppData", "*");
            final Column detailscol = new Column("MdAppDetails", "*");
            query.addSelectColumn(col);
            query.addSelectColumn(detailscol);
            Criteria packagetypeCri = null;
            Criteria platformtypeCri = null;
            if (packageType != 0) {
                packagetypeCri = new Criteria(new Column("MdPackageToAppGroup", "PACKAGE_TYPE"), (Object)packageType, 0);
                query.setCriteria(packagetypeCri);
            }
            if (platformType != 0) {
                platformtypeCri = new Criteria(new Column("MdAppDetails", "PLATFORM_TYPE"), (Object)platformType, 0);
                query.setCriteria(packagetypeCri);
            }
            if (packagetypeCri != null && platformtypeCri != null) {
                final Criteria commonCriteria = packagetypeCri.and(platformtypeCri);
                query.setCriteria(commonCriteria);
            }
            final DataObject applistDO = SyMUtil.getPersistence().get(query);
            if (!applistDO.isEmpty()) {
                final Iterator it1 = applistDO.getRows("MdPackageToAppData");
                final Iterator it2 = applistDO.getRows("MdAppDetails");
                while (it1.hasNext() && it2.hasNext()) {
                    final Row row = it1.next();
                    final Row detailsrow = it2.next();
                    final String manifestfileloc = AppMgmtConstants.APP_BASE_PATH + File.separator + row.get("MANIFEST_FILE_URL");
                    final HashMap hm = new HashMap();
                    hm.put("IS_SERVER", false);
                    hm.put("IS_AUTHTOKEN", true);
                    String swPackageUrl = (String)row.get("APP_FILE_LOC");
                    hm.put("path", swPackageUrl);
                    if (swPackageUrl != null && !swPackageUrl.startsWith("http")) {
                        swPackageUrl = MDMiOSEntrollmentUtil.getInstance().getServerBaseURL() + MDMApiFactoryProvider.getMDMAuthTokenUtilAPI().getURLWithAuthTokenAndUDID(hm);
                    }
                    String fullImageLoc = (String)row.get("FULL_IMAGE_LOC");
                    hm.put("path", fullImageLoc);
                    if (fullImageLoc != null && !fullImageLoc.startsWith("http")) {
                        fullImageLoc = MDMiOSEntrollmentUtil.getInstance().getServerBaseURL() + MDMApiFactoryProvider.getMDMAuthTokenUtilAPI().getURLWithAuthToken(hm);
                    }
                    String displayImageLoc = (String)row.get("DISPLAY_IMAGE_LOC");
                    hm.put("path", displayImageLoc);
                    if (displayImageLoc != null && !displayImageLoc.startsWith("http")) {
                        displayImageLoc = MDMiOSEntrollmentUtil.getInstance().getServerBaseURL() + MDMApiFactoryProvider.getMDMAuthTokenUtilAPI().getURLWithAuthToken(hm);
                    }
                    final int supporteddevices = (int)row.get("SUPPORTED_DEVICES");
                    final String bundelIdentifier = (String)detailsrow.get("IDENTIFIER");
                    final String bundleversion = (String)detailsrow.get("APP_VERSION");
                    final String appTitle = (String)detailsrow.get("APP_TITLE");
                    final String customizedAppURL = String.valueOf(row.get("CUSTOMIZED_APP_URL"));
                    final JSONObject jsonObject = new JSONObject();
                    jsonObject.put("FULL_IMAGE_LOC", (Object)((fullImageLoc != null) ? fullImageLoc.replaceAll(" ", "%20").replace("\\", "/") : ""));
                    jsonObject.put("DISPLAY_IMAGE_LOC", (Object)((displayImageLoc != null) ? displayImageLoc.replaceAll(" ", "%20").replace("\\", "/") : ""));
                    jsonObject.put("APP_FILE_LOC", (Object)((swPackageUrl != null) ? swPackageUrl.replaceAll(" ", "%20").replace("\\", "/") : ""));
                    jsonObject.put("IDENTIFIER", (Object)bundelIdentifier);
                    jsonObject.put("APP_VERSION", (Object)bundleversion);
                    jsonObject.put("APP_TITLE", (Object)appTitle);
                    jsonObject.put("MANIFEST_FILE_URL", (Object)manifestfileloc);
                    jsonObject.put("SUPPORTED_DEVICES", supporteddevices);
                    jsonObject.put("CUSTOMIZED_APP_URL", (Object)customizedAppURL);
                    arr.put((Object)jsonObject);
                }
            }
        }
        catch (final Exception ex) {
            AppsUtil.logger.log(Level.INFO, "Exception in getIOSWindowsEnterpirseApps():{0}", ex);
        }
        return arr;
    }
    
    public void regenerateManifestFile() {
        AppsUtil.logger.info("Regenerate manifestFile initiated in RepublishAppProfileTask");
        final JSONArray jsonArray = getInstance().getAppsDetail(2, 1);
        try {
            for (int i = 0; i < jsonArray.length(); ++i) {
                final JSONObject appObj = (JSONObject)jsonArray.get(i);
                if (appObj.getInt("SUPPORTED_DEVICES") == 16) {
                    AppsUtil.logger.log(Level.INFO, "MAC agent found hence ignoring regenerate manifest file");
                }
                else {
                    final String manifestFileUrl = String.valueOf(appObj.get("MANIFEST_FILE_URL"));
                    final String fullImageLoc = String.valueOf(appObj.get("FULL_IMAGE_LOC"));
                    String appLoc = String.valueOf(appObj.get("APP_FILE_LOC"));
                    final String displayImageLoc = String.valueOf(appObj.get("DISPLAY_IMAGE_LOC"));
                    final String bundleIdentifier = String.valueOf(appObj.get("IDENTIFIER"));
                    final String bundleVersion = String.valueOf(appObj.get("APP_VERSION"));
                    final String appTitle = String.valueOf(appObj.get("APP_TITLE"));
                    final String customizedAppURL = String.valueOf(appObj.get("CUSTOMIZED_APP_URL"));
                    if (!MDMStringUtils.isEmpty(customizedAppURL)) {
                        appLoc = customizedAppURL;
                    }
                    MDMRestAPIFactoryProvider.getAppsUtilAPI().generateEnterpriseAppManifestFile(manifestFileUrl, appLoc, fullImageLoc, displayImageLoc, Boolean.TRUE, Boolean.TRUE, bundleIdentifier, bundleVersion, appTitle, appTitle);
                }
            }
        }
        catch (final Exception e) {
            AppsUtil.logger.log(Level.SEVERE, "Exception in regenerateManifestFile()", e);
        }
    }
    
    public void generateEnterpriseAppManifestFile(final String manifestFilePath, final String swPackageUrl, final String fullSizeImageUrl, final String displayImageUrl, final boolean isFullSizeImageNeedShine, final boolean isDisplayImageNeedShile, final String bundleIdentifier, final String bundleVersion, final String subtitle, final String title) throws IOException, Exception {
        final NSDictionary root = new NSDictionary();
        final NSArray arrayDict = new NSArray(1);
        final NSDictionary payload = new NSDictionary();
        payload.put("assets", (NSObject)this.getAssetsArray(swPackageUrl, fullSizeImageUrl, displayImageUrl, isFullSizeImageNeedShine, isDisplayImageNeedShile));
        payload.put("metadata", (NSObject)this.getMetaDataDict(bundleIdentifier, bundleVersion, subtitle, title));
        arrayDict.setValue(0, (Object)payload);
        root.put("items", (NSObject)arrayDict);
        final File file = new File(manifestFilePath);
        PropertyListParser.saveAsXML((NSObject)root, file);
    }
    
    protected NSDictionary getMetaDataDict(String bundleIdentifier, final String bundleVersion, final String subtitle, final String title) {
        if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AllowSameBundleIDStoreAndEnterpriseAppForIOS")) {
            AppsUtil.logger.log(Level.INFO, "ENS Enabled for customer");
            final String tempBundleID = bundleIdentifier;
            bundleIdentifier = IOSModifiedEnterpriseAppsUtil.getOriginalBundleIDOfEnterpriseApp(bundleIdentifier);
            if (!bundleIdentifier.equals(tempBundleID)) {
                AppsUtil.logger.log(Level.INFO, "Changing altered bundleId: {0} to actual bundleId: {1}", new Object[] { tempBundleID, bundleIdentifier });
            }
        }
        final NSDictionary payload = new NSDictionary();
        payload.put("bundle-identifier", (Object)bundleIdentifier);
        payload.put("bundle-version", (Object)bundleVersion);
        payload.put("kind", (Object)"software");
        payload.put("subtitle", (Object)subtitle);
        payload.put("title", (Object)title);
        return payload;
    }
    
    protected NSArray getAssetsArray(final String swPackageUrl, final String fullSizeImageUrl, final String displayImageUrl, final boolean isFullSizeImageNeedShine, final boolean isDisplayImageNeedShile) {
        final NSArray arrayDict = new NSArray(3);
        arrayDict.setValue(0, (Object)this.getSWPackageDict(swPackageUrl));
        arrayDict.setValue(1, (Object)this.getFullSizeImageDict(isFullSizeImageNeedShine, fullSizeImageUrl));
        arrayDict.setValue(2, (Object)this.getDisplayImageDict(isDisplayImageNeedShile, displayImageUrl));
        return arrayDict;
    }
    
    private NSDictionary getSWPackageDict(final String swPackageUrl) {
        final NSDictionary payload = new NSDictionary();
        payload.put("kind", (Object)"software-package");
        payload.put("url", (Object)swPackageUrl);
        return payload;
    }
    
    private NSDictionary getFullSizeImageDict(final boolean isFullSizeImageNeedShine, final String fullSizeImageUrl) {
        final NSDictionary payload = new NSDictionary();
        payload.put("kind", (Object)"full-size-image");
        payload.put("needshine", (Object)isFullSizeImageNeedShine);
        payload.put("url", (Object)fullSizeImageUrl);
        return payload;
    }
    
    private NSDictionary getDisplayImageDict(final boolean isDisplayImageNeedShile, final String displayImageUrl) {
        final NSDictionary payload = new NSDictionary();
        payload.put("kind", (Object)"display-image");
        payload.put("needshine", (Object)isDisplayImageNeedShile);
        payload.put("url", (Object)displayImageUrl);
        return payload;
    }
    
    public Long getAppGroupIdFromMDPackage(final String appIdentifier, final Integer platformType, final Long customerID) {
        AppsUtil.logger.log(Level.INFO, "getAppGroupIdWithLicense: appIdentifier: {0}  customerID: {1} Platform {2}", new Object[] { appIdentifier, customerID, platformType });
        Long appGroupID = -1L;
        try {
            final Criteria cIdentifier = new Criteria(new Column("MdAppGroupDetails", "IDENTIFIER"), (Object)appIdentifier, 0);
            final Criteria cCustomerId = new Criteria(new Column("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria cPlatfromType = new Criteria(new Column("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)platformType, 0);
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("MdAppGroupDetails"));
            query.addJoin(new Join("MdAppGroupDetails", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            query.addSelectColumn(Column.getColumn("MdAppGroupDetails", "*"));
            query.setCriteria(cIdentifier.and(cCustomerId).and(cPlatfromType));
            final DataObject DO = MDMUtil.getPersistence().get(query);
            if (!DO.isEmpty()) {
                final Iterator iter = DO.getRows("MdAppGroupDetails", (Criteria)null);
                final List appgroupList = DBUtil.getColumnValuesAsList(iter, "APP_GROUP_ID");
                final Row row = DO.getFirstRow("MdAppGroupDetails");
                appGroupID = (Long)row.get("APP_GROUP_ID");
                if (appgroupList.size() > 1) {
                    AppsUtil.logger.log(Level.SEVERE, "getAppGroupIdFromMDPackage:More than one MDAPPGROUPDETAILS found {0} -> App GroupIDs:{1} , Platform {2} , Picked App Group ID:{3}", new Object[] { appIdentifier, appgroupList, platformType, appGroupID });
                }
            }
        }
        catch (final Exception ex) {
            AppsUtil.logger.log(Level.SEVERE, "Exception in getAppGroupIdFromMDPackage {0}", ex);
        }
        AppsUtil.logger.log(Level.INFO, "Inside getAppGroupIdFromMDPackage: appGroupID: {0} , Identifier:{1}", new Object[] { appGroupID, appIdentifier });
        return appGroupID;
    }
    
    public Long getAppGroupIDFromIdentifier(final String appIdentifier, final Integer platformType, final Long customerID) {
        AppsUtil.logger.log(Level.INFO, "getAppGroupIDFromIdentifier: appIdentifier: {0}  customerID: {1}", new Object[] { appIdentifier, customerID });
        Long appGroupID = null;
        try {
            final Criteria cIdentifier = new Criteria(new Column("MdAppGroupDetails", "IDENTIFIER"), (Object)appIdentifier, 0);
            final Criteria cCustomerId = new Criteria(new Column("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria cPlatfromType = new Criteria(new Column("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)platformType, 0);
            final DataObject DO = MDMUtil.getPersistence().get("MdAppGroupDetails", cIdentifier.and(cCustomerId).and(cPlatfromType));
            if (!DO.isEmpty()) {
                final Row row = DO.getFirstRow("MdAppGroupDetails");
                appGroupID = (Long)row.get("APP_GROUP_ID");
            }
        }
        catch (final Exception ex) {
            AppsUtil.logger.log(Level.SEVERE, "Exception in getAppGroupIDFromIdentifier {0}", ex);
        }
        AppsUtil.logger.log(Level.INFO, "getAppIDFromIdentifier: appGroupID: {0}", appGroupID);
        return appGroupID;
    }
    
    public Long getAppIDFromIdentifierAndVersion(final String appIdentifier, final String version, final String versionCode, final Integer platformType, final Long customerID) {
        Long appID = null;
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppDetails"));
            query.addSelectColumn(Column.getColumn("MdAppDetails", "APP_ID"));
            final Boolean isCaseSensitive = getInstance().getIsBundleIdCaseSenstive(platformType);
            Criteria criteria = new Criteria(Column.getColumn("MdAppDetails", "PLATFORM_TYPE"), (Object)platformType, 0);
            criteria = criteria.and(new Criteria(Column.getColumn("MdAppDetails", "CUSTOMER_ID"), (Object)customerID, 0));
            criteria = criteria.and(new Criteria(Column.getColumn("MdAppDetails", "IDENTIFIER"), (Object)appIdentifier, 0, (boolean)isCaseSensitive));
            criteria = criteria.and(new Criteria(Column.getColumn("MdAppDetails", "APP_VERSION"), (Object)version, 0));
            criteria = criteria.and(new Criteria(Column.getColumn("MdAppDetails", "APP_NAME_SHORT_VERSION"), (Object)versionCode, 0));
            query.setCriteria(criteria);
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            if (!dataObject.isEmpty()) {
                final Row appRow = dataObject.getFirstRow("MdAppDetails");
                appID = (Long)appRow.get("APP_ID");
            }
        }
        catch (final Exception e) {
            AppsUtil.logger.log(Level.SEVERE, "Exception in getting appID from identifier and version", e);
        }
        return appID;
    }
    
    public Long getCollectionFromIdentifierForCloningAppConfigurationAndPermissions(final String appIdentifier, final int platformType, final Long customerId) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
        final Join appGroupToCollectionJoin = new Join("MdAppGroupDetails", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
        final Join appReleaseLabelJoin = AppVersionDBUtil.getInstance().getAppReleaseLabelJoin();
        final Criteria customer = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria identifier = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)appIdentifier, 0);
        final Criteria platform = new Criteria(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)platformType, 0);
        sQuery.addJoin(appGroupToCollectionJoin);
        sQuery.addJoin(appReleaseLabelJoin);
        sQuery.addSelectColumn(Column.getColumn("AppGroupToCollection", "COLLECTION_ID"));
        sQuery.addSelectColumn(Column.getColumn("AppGroupToCollection", "RELEASE_LABEL_ID"));
        sQuery.setCriteria(customer.and(identifier).and(platform).and(AppVersionDBUtil.getInstance().getApprovedAppVersionCriteria()));
        final DataObject dO = MDMUtil.getPersistence().get(sQuery);
        if (!dO.isEmpty()) {
            final Row row = dO.getFirstRow("AppGroupToCollection");
            return (Long)row.get("COLLECTION_ID");
        }
        return -1L;
    }
    
    public JSONArray getInstalledAppJSONArrayForResource(final Long resourceId, final int filterValue, final String searchValue) throws SyMException {
        AppsUtil.logger.log(Level.FINE, "Entered into getInstalledAppDetailsForResourceID()");
        JSONArray installedAppsJSONArr = null;
        try {
            final Long appId = null;
            final int platformType = ManagedDeviceHandler.getInstance().getPlatformType(resourceId);
            final SelectQuery appListQuery = this.getQueryforResourceAppDetails(resourceId, appId, filterValue, searchValue, platformType);
            installedAppsJSONArr = this.getAppCatalogListJSON(appListQuery, resourceId, appId, platformType);
            AppsUtil.logger.log(Level.FINE, "getInstalledAppJSONArrayForResource : Data Hash for getInstalledAppDetails : {0}", installedAppsJSONArr);
        }
        catch (final Exception exp) {
            AppsUtil.logger.log(Level.WARNING, "Exception in getInstalledAppJSONArrayForResource", exp);
            throw new SyMException(1001, (Throwable)exp);
        }
        return installedAppsJSONArr;
    }
    
    public Object constructStatusSummaryJSON(final JSONObject jsonObj, final Long resourceId) {
        AppsUtil.logger.log(Level.FINE, "Entered into constructStatusSummaryJSON()");
        Object appStatusSummaryJSON = null;
        try {
            final int platformType = ManagedDeviceHandler.getInstance().getPlatformType(resourceId);
            if (platformType == 1) {
                appStatusSummaryJSON = jsonObj;
            }
            else if (platformType == 3) {
                final JSONObject messageResponseJSON = new JSONObject();
                messageResponseJSON.put("MessageResponse", (Object)jsonObj);
                messageResponseJSON.put("MessageType", (Object)"AppCatalogSummary");
                messageResponseJSON.put("Status", (Object)"Acknowledged");
                appStatusSummaryJSON = messageResponseJSON;
            }
            AppsUtil.logger.log(Level.FINE, "constructStatusSummaryJSON : App Status Summary JSON : {0}", appStatusSummaryJSON);
        }
        catch (final Exception exp) {
            AppsUtil.logger.log(Level.WARNING, "Exception in constructStatusSummaryJSON", exp);
        }
        return appStatusSummaryJSON;
    }
    
    public Object constructAppListJSON(final JSONArray installedAppsJSONArr, final Long resourceId) {
        AppsUtil.logger.log(Level.FINE, "Entered into constructAppListJSON()");
        Object appListJSON = null;
        try {
            final int platformType = ManagedDeviceHandler.getInstance().getPlatformType(resourceId);
            if (platformType == 1) {
                appListJSON = installedAppsJSONArr;
            }
            else if (platformType == 3) {
                appListJSON = this.constructWindowsAppListJSON(installedAppsJSONArr, resourceId);
            }
            AppsUtil.logger.log(Level.FINE, "constructAppListJSON : App List JSON : {0}", appListJSON);
        }
        catch (final Exception exp) {
            AppsUtil.logger.log(Level.WARNING, "Exception in constructAppListJSON", exp);
        }
        return appListJSON;
    }
    
    public JSONObject constructWindowsAppListJSON(final JSONArray installedAppsJSONArr, final Long resourceId) {
        AppsUtil.logger.log(Level.FINE, "Entered into constructWindowsAppListJSON()");
        JSONObject messageResponseJSON = null;
        try {
            final Long appCatalogSyncTime = getInstance().getAppCatalogSyncTime(resourceId);
            messageResponseJSON = new JSONObject();
            final JSONObject installAppsJson = new JSONObject();
            installAppsJson.put("LastSyncTime", (Object)appCatalogSyncTime);
            installAppsJson.put("ManagedApps", (Object)installedAppsJSONArr);
            messageResponseJSON.put("MessageResponse", (Object)installAppsJson);
            messageResponseJSON.put("MessageType", (Object)"SyncAppCatalog");
            messageResponseJSON.put("Status", (Object)"Acknowledged");
            AppsUtil.logger.log(Level.FINE, "constructWindowsAppListJSON : Data Hash AppListJSON : {0}", installedAppsJSONArr);
        }
        catch (final Exception exp) {
            AppsUtil.logger.log(Level.WARNING, "Exception in constructWindowsAppListJSON", exp);
        }
        return messageResponseJSON;
    }
    
    public JSONObject getAppJSONObj(final Long appId, final Long collectionId, final String command) throws SyMException {
        AppsUtil.logger.log(Level.FINE, "Entered into getInstalledAppDetailsForResourceID()");
        JSONObject removeAppJSON = null;
        final String searchValue = null;
        try {
            final SelectQuery appListQuery = this.getQueryforAppDetails(appId, searchValue);
            removeAppJSON = this.getAppCatalogJSON(appListQuery, appId, collectionId, command);
            AppsUtil.logger.log(Level.FINE, "getRemoveAppJSONObj : Data Hash for getRemoveAppJSONObj : {0}", removeAppJSON);
        }
        catch (final Exception exp) {
            AppsUtil.logger.log(Level.WARNING, "Exception in getRemoveAppJSONObj", exp);
            throw new SyMException(1001, (Throwable)exp);
        }
        return removeAppJSON;
    }
    
    public JSONObject getAppDetails(final Long resourceId, final Long appGroupId, final int filterValue) {
        JSONObject detailJSON = null;
        DMDataSetWrapper ds = null;
        AppsUtil.logger.log(Level.FINE, "Entered into getInstalledAppDetailsForResourceID()");
        try {
            final SelectQuery appDetailsQuery = this.getQueryforResourceAppDetails(resourceId, appGroupId, filterValue, null, 1);
            ds = DMDataSetWrapper.executeQuery((Object)appDetailsQuery);
            if (ds.next()) {
                detailJSON = this.setIOSAppCatalogJSON(ds, appGroupId);
            }
        }
        catch (final Exception exp) {
            AppsUtil.logger.log(Level.WARNING, "getAppDetailsFromAppFile : DataAccessException while getting Security Info...", exp);
        }
        return detailJSON;
    }
    
    public JSONObject setAppCatalogJSON(final DMDataSetWrapper ds, final Long resourceId, final Map<Long, HashMap> applicableVersions, final Properties defaultAppSettings, final JSONObject appsDeploymentPolicyForResourceID) {
        JSONObject detailJSON = null;
        boolean isInstalled = false;
        AppsUtil.logger.log(Level.FINE, "Entered into setAppCatalogJSON()");
        try {
            detailJSON = new JSONObject();
            final Long collectionId = (Long)ds.getValue("COLLECTION_ID");
            final Long profileId = (Long)ds.getValue("PROFILE_ID");
            final Long appGroupId = (Long)ds.getValue("APP_GROUP_ID");
            detailJSON.put("PLATFORM_TYPE", ds.getValue("PLATFORM_TYPE"));
            detailJSON.put("AppName", ds.getValue("APP_NAME"));
            detailJSON.put("AppVersion", ds.getValue("APP_VERSION"));
            final String versionCodeString = (String)ds.getValue("APP_NAME_SHORT_VERSION");
            if (!MDMStringUtils.isEmpty(versionCodeString)) {
                detailJSON.put("VersionCode", (Object)versionCodeString);
                try {
                    final Integer versionCode = Integer.parseInt(versionCodeString);
                    detailJSON.put("VersionCode", (Object)versionCode);
                }
                catch (final Exception e) {
                    AppsUtil.logger.log(Level.WARNING, "Non integer value in version code {0}", versionCodeString);
                }
            }
            detailJSON.put("PackageName", ds.getValue("IDENTIFIER"));
            detailJSON.put("PackageType", ds.getValue("PACKAGE_TYPE"));
            detailJSON.put("isPortalApp", ds.getValue("IS_PURCHASED_FROM_PORTAL"));
            final String appURL = ds.getValue("APP_FILE_LOC").toString();
            detailJSON.put("AppUrl", (Object)appURL);
            final String customizedAppURL = String.valueOf(ds.getValue("CUSTOMIZED_APP_URL"));
            if (!MDMStringUtils.isEmpty(customizedAppURL)) {
                detailJSON.put("CustomizedURL", (Object)customizedAppURL);
            }
            else {
                detailJSON.put("CustomizedURL", (Object)"");
            }
            detailJSON.put("AbsoluteUrl", (Object)"");
            if (!MDMStringUtils.isEmpty((String)ds.getValue("DISPLAY_IMAGE_LOC"))) {
                detailJSON.put("DisplayImageUrl", ds.getValue("DISPLAY_IMAGE_LOC"));
            }
            detailJSON.put("FullImageLoc", ds.getValue("FULL_IMAGE_LOC"));
            detailJSON.put("SupportedDevices", ds.getValue("SUPPORTED_DEVICES"));
            detailJSON.put("AppCategory", ds.getValue("APP_CATEGORY_NAME"));
            detailJSON.put("CollectionID", (Object)collectionId);
            detailJSON.put("PACKAGE_ID", ds.getValue("PACKAGE_ID"));
            detailJSON.put("APP_ID", ds.getValue("APP_ID"));
            detailJSON.put("APP_GROUP_ID", (Object)appGroupId);
            detailJSON.put("AppDescription", ds.getValue("DESCRIPTION"));
            final Boolean isMarkedForDelete = (Boolean)ds.getValue("MARKED_FOR_DELETE");
            detailJSON.put("IsMarkedForDelete", (Object)isMarkedForDelete);
            MDMRestAPIFactoryProvider.getAppsUtilAPI().addFileDetails(detailJSON, ds);
            final Boolean defaultIsSilentInstall = ((Hashtable<K, Boolean>)defaultAppSettings).get("isSilentInstall");
            Boolean silentInstall;
            Boolean doNotUninstall;
            if (appsDeploymentPolicyForResourceID == null || !appsDeploymentPolicyForResourceID.has(profileId.toString())) {
                silentInstall = defaultIsSilentInstall;
                doNotUninstall = defaultIsSilentInstall;
            }
            else {
                final JSONObject appDeploymentPolicyDetails = appsDeploymentPolicyForResourceID.getJSONObject(profileId.toString());
                silentInstall = appDeploymentPolicyDetails.optBoolean("FORCE_APP_INSTALL", (boolean)defaultIsSilentInstall);
                doNotUninstall = appDeploymentPolicyDetails.optBoolean("DO_NOT_UNINSTALL", (boolean)silentInstall);
            }
            detailJSON.put("isSilentInstall", (Object)silentInstall);
            if (isMarkedForDelete) {
                doNotUninstall = Boolean.FALSE;
            }
            detailJSON.put("DoNotAllowUninstallation", (Object)doNotUninstall);
            final Long installedAppId = (Long)ds.getValue("INSTALLED_APP_ID");
            final boolean skipNotification = ((Hashtable<K, Boolean>)defaultAppSettings).getOrDefault("skipNotification", false);
            final JSONObject jsonObject = detailJSON;
            final String s = "skipNotification";
            boolean b = false;
            Label_0638: {
                if (!skipNotification || installedAppId != null) {
                    if (!silentInstall) {
                        b = false;
                        break Label_0638;
                    }
                }
                b = true;
            }
            jsonObject.put(s, b);
            final Integer scope = (Integer)ds.getValue("SCOPE");
            detailJSON.put("scope", (Object)((scope != null) ? ((scope == 0) ? "device" : "container") : "device"));
            final int appInstallationStatus = (int)ds.getValue("STATUS");
            if (appInstallationStatus == 2) {
                isInstalled = true;
            }
            detailJSON.put("IsInstalled", isInstalled);
            if (applicableVersions != null && applicableVersions.containsKey(appGroupId)) {
                final HashMap versionMap = applicableVersions.get(appGroupId);
                detailJSON.put("applicableVersions", (Collection)new HashSet(versionMap.values()));
                detailJSON.put("applicableVersionCodes", (Collection)versionMap.keySet());
            }
            final String checkSum = (String)ds.getValue("APP_CHECKSUM");
            if (!MDMStringUtils.isEmpty(checkSum)) {
                detailJSON.put("AppCheckSum", ds.getValue("APP_CHECKSUM"));
            }
        }
        catch (final Exception exp) {
            AppsUtil.logger.log(Level.WARNING, "Exception in setAndroidAppCatalogJSON...", exp);
        }
        return detailJSON;
    }
    
    private JSONObject setAndroidRemoveAppJSON(final DataSet ds, final Long collectionId) {
        JSONObject detailJSON = null;
        AppsUtil.logger.log(Level.FINE, "Entered into setAndroidRemoveAppJSON()");
        try {
            detailJSON = new JSONObject();
            detailJSON.put("AppName", ds.getValue("APP_NAME"));
            detailJSON.put("AppVersion", ds.getValue("APP_VERSION"));
            final String versionCode = (String)ds.getValue("APP_NAME_SHORT_VERSION");
            if (!MDMStringUtils.isEmpty(versionCode)) {
                detailJSON.put("VersionCode", (Object)versionCode);
                try {
                    final Integer versionCodeValue = Integer.parseInt(versionCode);
                    detailJSON.put("VersionCode", (Object)versionCodeValue);
                }
                catch (final Exception e) {
                    AppsUtil.logger.log(Level.WARNING, "Non integer value in version code {0}", versionCode);
                }
            }
            detailJSON.put("PackageName", ds.getValue("IDENTIFIER"));
            detailJSON.put("CollectionID", (Object)collectionId);
        }
        catch (final Exception exp) {
            AppsUtil.logger.log(Level.WARNING, "Exception in setAndroidRemoveAppJSON...", exp);
        }
        return detailJSON;
    }
    
    private JSONObject setAndroidInstallAppJSON(final DataSet ds, final long collectionId) {
        JSONObject detailJSON = null;
        AppsUtil.logger.log(Level.FINE, "Entered into setAndroidInstallAppJSON()");
        try {
            detailJSON = new JSONObject();
            detailJSON.put("AppName", ds.getValue("APP_NAME"));
            detailJSON.put("AppVersion", ds.getValue("APP_VERSION"));
            final String versionCode = (String)ds.getValue("APP_NAME_SHORT_VERSION");
            final String customizedAppURL = String.valueOf(ds.getValue("CUSTOMIZED_APP_URL"));
            if (!MDMStringUtils.isEmpty(versionCode)) {
                detailJSON.put("VersionCode", (Object)versionCode);
                try {
                    final Integer versionCodeValue = Integer.parseInt(versionCode);
                    detailJSON.put("VersionCode", (Object)versionCodeValue);
                }
                catch (final Exception e) {
                    AppsUtil.logger.log(Level.WARNING, "Non integer value in version code {0}", versionCode);
                }
            }
            detailJSON.put("PackageName", ds.getValue("IDENTIFIER"));
            final HashMap hm = new HashMap();
            hm.put("path", ds.getValue("APP_FILE_LOC"));
            hm.put("IS_SERVER", false);
            hm.put("IS_AUTHTOKEN", false);
            final String appURL = ApiFactoryProvider.getFileAccessAPI().constructFileURL(hm);
            detailJSON.put("AppUrl", (Object)appURL);
            final String appDSURL = MDMApiFactoryProvider.getUploadDownloadAPI().constructFileURLwithDownloadServer(hm);
            if (customizedAppURL != null && !"null".equalsIgnoreCase(customizedAppURL) && !"".equalsIgnoreCase(customizedAppURL)) {
                detailJSON.put("CustomizedURL", (Object)customizedAppURL);
            }
            else {
                detailJSON.put("CustomizedURL", (Object)"");
            }
            detailJSON.put("AbsoluteUrl", (Object)appDSURL);
            detailJSON.put("PackageType", ds.getValue("PACKAGE_TYPE"));
            detailJSON.put("PlatformType", ds.getValue("PLATFORM_TYPE"));
            detailJSON.put("CollectionID", collectionId);
            final String checkSum = (String)ds.getValue("APP_CHECKSUM");
            if (!MDMStringUtils.isEmpty(checkSum)) {
                detailJSON.put("AppCheckSum", (Object)checkSum);
            }
        }
        catch (final Exception exp) {
            AppsUtil.logger.log(Level.WARNING, "Exception in setAndroidInstallAppJSON...", exp);
        }
        return detailJSON;
    }
    
    public String getAppName(final Long appGroupId) {
        String appName = "";
        try {
            final Criteria appidCri = new Criteria(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroupId, 0);
            final DataObject dObj = DataAccess.get("MdAppGroupDetails", appidCri);
            if (!dObj.isEmpty()) {
                appName = (String)dObj.getValue("MdAppGroupDetails", "GROUP_DISPLAY_NAME", appidCri);
            }
        }
        catch (final Exception exp) {
            AppsUtil.logger.log(Level.WARNING, "Exception in getAppName", exp);
        }
        return appName;
    }
    
    public SelectQuery getQueryforResourceAppDetails(final Long resourceId, final Long appId, final int filterValue, final String searchValue, final int platformType) {
        return this.getQueryforResourceAppDetails(resourceId, appId, filterValue, searchValue, -1, platformType);
    }
    
    public SelectQuery getQueryforResourceAppDetails(final Long resourceId, final Long appId, final int filterValue, final String searchValue, final int appType, final int platformType) {
        AppsUtil.logger.log(Level.FINE, "Entered into getQueryforResourceAppDetails()");
        SelectQuery appListQuery = null;
        try {
            final Long customerID = CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceId);
            appListQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackage"));
            final Join packageGroupRel = new Join("MdPackage", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2);
            final Join packageAppDataRel = new Join("MdPackageToAppGroup", "MdPackageToAppData", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2);
            final Join appCollectionJoin = new Join("MdPackageToAppData", "MdAppToCollection", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2);
            Join appCatalogJoin = null;
            if (platformType == 3) {
                appCatalogJoin = new Join("MdPackageToAppData", "MdAppCatalogToResource", new String[] { "APP_ID" }, new String[] { "PUBLISHED_APP_ID" }, 2);
            }
            else {
                appCatalogJoin = new Join("MdPackageToAppData", "MdAppCatalogToResource", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
            }
            final Criteria resourceJoinC = new Criteria(new Column("MdAppCatalogToResource", "RESOURCE_ID"), (Object)new Column("RecentProfileForResource", "RESOURCE_ID"), 0);
            final Criteria collnJoinC = new Criteria(new Column("MdAppToCollection", "COLLECTION_ID"), (Object)new Column("RecentProfileForResource", "COLLECTION_ID"), 0);
            final Join recentProfileCollnJoin = new Join("MdAppCatalogToResource", "RecentProfileForResource", resourceJoinC.and(collnJoinC), 2);
            final Join publishedAppDetailsJoin = new Join("MdAppCatalogToResource", "MdAppDetails", new String[] { "PUBLISHED_APP_ID" }, new String[] { "APP_ID" }, 2);
            final Join categoryRelJoin = new Join("MdPackageToAppData", "MdAppGroupCategoryRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
            final Join categoryJoin = new Join("MdAppGroupCategoryRel", "AppCategory", new String[] { "APP_CATEGORY_ID" }, new String[] { "APP_CATEGORY_ID" }, 2);
            final Join licenseRelJoin = new Join("MdPackageToAppData", "MdLicenseToAppGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1);
            final Join licenseJoin = new Join("MdLicenseToAppGroupRel", "MdLicense", new String[] { "LICENSE_ID" }, new String[] { "LICENSE_ID" }, 1);
            final Join appCatalogScope = new Join("MdAppCatalogToResource", "MdAppCatalogToResourceScope", new String[] { "RESOURCE_ID", "APP_GROUP_ID" }, new String[] { "RESOURCE_ID", "APP_GROUP_ID" }, 1);
            final Join profileJoin = new Join("RecentProfileForResource", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
            appListQuery.addJoin(packageGroupRel);
            appListQuery.addJoin(packageAppDataRel);
            appListQuery.addJoin(appCatalogJoin);
            appListQuery.addJoin(appCollectionJoin);
            appListQuery.addJoin(recentProfileCollnJoin);
            appListQuery.addJoin(publishedAppDetailsJoin);
            appListQuery.addJoin(categoryRelJoin);
            appListQuery.addJoin(categoryJoin);
            appListQuery.addJoin(profileJoin);
            appListQuery.addJoin(licenseRelJoin);
            appListQuery.addJoin(licenseJoin);
            appListQuery.addJoin(appCatalogScope);
            appListQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_NAME"));
            appListQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "PROFILE_ID"));
            appListQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "MARKED_FOR_DELETE"));
            appListQuery.addSelectColumn(Column.getColumn("MdAppDetails", "PLATFORM_TYPE"));
            appListQuery.addSelectColumn(Column.getColumn("MdAppDetails", "IDENTIFIER"));
            appListQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_VERSION"));
            appListQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_NAME_SHORT_VERSION"));
            appListQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_NAME"));
            appListQuery.addSelectColumn(Column.getColumn("MdAppToCollection", "COLLECTION_ID"));
            appListQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "PACKAGE_ID"));
            appListQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "PACKAGE_TYPE"));
            appListQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "APP_GROUP_ID"));
            appListQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"));
            appListQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "APP_FILE_LOC"));
            appListQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "CUSTOMIZED_APP_URL"));
            appListQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "DISPLAY_IMAGE_LOC"));
            appListQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "FULL_IMAGE_LOC"));
            appListQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "DESCRIPTION"));
            appListQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "SUPPORTED_DEVICES"));
            appListQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "STORE_ID"));
            appListQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "APP_CHECKSUM"));
            appListQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "APP_ID"));
            appListQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "APP_GROUP_ID"));
            appListQuery.addSelectColumn(Column.getColumn("AppCategory", "APP_CATEGORY_NAME"));
            appListQuery.addSelectColumn(Column.getColumn("AppCategory", "APP_CATEGORY_LABEL"));
            appListQuery.addSelectColumn(Column.getColumn("MdAppCatalogToResourceScope", "SCOPE"));
            appListQuery.addSelectColumn(Column.getColumn("MdAppCatalogToResource", "STATUS"));
            appListQuery.addSelectColumn(Column.getColumn("MdAppCatalogToResource", "IS_LATEST"));
            appListQuery.addSelectColumn(Column.getColumn("MdAppCatalogToResource", "PUBLISHED_APP_ID"));
            appListQuery.addSelectColumn(Column.getColumn("MdAppCatalogToResource", "INSTALLED_APP_ID"));
            appListQuery.addSelectColumn(Column.getColumn("MdAppCatalogToResource", "UPDATED_AT"));
            appListQuery.addSelectColumn(Column.getColumn("MdLicense", "LICENSED_TYPE"));
            MDMRestAPIFactoryProvider.getAppsUtilAPI().addFileDetailsQuery(appListQuery);
            Criteria cri;
            final Criteria resCri = cri = new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourceId, 0);
            if (customerID != null) {
                final Criteria customerCriteria = new Criteria(Column.getColumn("MdPackage", "CUSTOMER_ID"), (Object)customerID, 0);
                cri = cri.and(customerCriteria);
            }
            if (appId != null) {
                final Criteria appCri = new Criteria(Column.getColumn("MdAppDetails", "APP_ID"), (Object)appId, 0);
                cri = cri.and(appCri);
            }
            if (filterValue != -1) {
                final Criteria filterCri = new Criteria(Column.getColumn("MdAppCatalogToResource", "STATUS"), (Object)filterValue, 0);
                cri = cri.and(filterCri);
            }
            if (searchValue != null) {
                final Criteria searchCri = new Criteria(Column.getColumn("MdAppDetails", "APP_NAME"), (Object)searchValue, 12, false);
                cri = cri.and(searchCri);
            }
            if (appType != -1) {
                final Criteria appTypeCri = new Criteria(Column.getColumn("MdPackageToAppGroup", "PACKAGE_TYPE"), (Object)appType, 0);
                cri = cri.and(appTypeCri);
            }
            final Criteria wpNativeAppRemoveCriteria = new Criteria(Column.getColumn("MdAppDetails", "APP_ID"), (Object)WpCompanyHubAppHandler.getInstance().getWPCompanyHubAppId(customerID), 0).negate();
            cri = cri.and(wpNativeAppRemoveCriteria);
            appListQuery.setCriteria(cri);
            final SortColumn sortApp = new SortColumn(Column.getColumn("MdAppCatalogToResource", "STATUS"), true);
            appListQuery.addSortColumn(sortApp);
        }
        catch (final Exception exp) {
            AppsUtil.logger.log(Level.WARNING, "getQueryforAppDetails : DataAccessException while getting Security Info...", exp);
        }
        return appListQuery;
    }
    
    public SelectQuery getQueryforAppDetails(final Long appId, final String searchValue) {
        AppsUtil.logger.log(Level.FINE, "Entered into getQueryforAppDetails()");
        SelectQuery appListQuery = null;
        try {
            appListQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackage"));
            final Join mdpackageGroupJoin = new Join("MdPackage", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2);
            final Join mdpackageJoin = new Join("MdPackage", "MdPackageToAppData", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2);
            final Join appDetailsJoin = new Join("MdPackageToAppData", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2);
            final Join appGroupDetailsJoin = new Join("MdPackageToAppData", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
            final Join categoryRelJoin = new Join("MdAppGroupDetails", "MdAppGroupCategoryRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
            final Join categoryJoin = new Join("MdAppGroupCategoryRel", "AppCategory", new String[] { "APP_CATEGORY_ID" }, new String[] { "APP_CATEGORY_ID" }, 2);
            appListQuery.addJoin(mdpackageJoin);
            appListQuery.addJoin(mdpackageGroupJoin);
            appListQuery.addJoin(appGroupDetailsJoin);
            appListQuery.addJoin(appDetailsJoin);
            appListQuery.addJoin(categoryRelJoin);
            appListQuery.addJoin(categoryJoin);
            appListQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            Criteria cri = null;
            if (appId != null) {
                final Criteria appCri = cri = new Criteria(Column.getColumn("MdPackageToAppData", "APP_ID"), (Object)appId, 0);
            }
            if (searchValue != null) {
                final Criteria searchCri = new Criteria(Column.getColumn("MdAppGroupDetails", "GROUP_DISPLAY_NAME"), (Object)searchValue, 12, false);
                cri = cri.and(searchCri);
            }
            appListQuery.setCriteria(cri);
        }
        catch (final Exception exp) {
            AppsUtil.logger.log(Level.WARNING, "getQueryforAppDetails : DataAccessException while getting Security Info...", exp);
        }
        return appListQuery;
    }
    
    private HashMap getInstalledAppSummaryForCriteria(final Criteria cri) throws SyMException {
        AppsUtil.logger.log(Level.FINE, "Entered into getInstalledAppSummaryForUDID()");
        final HashMap appSummaryMap = new HashMap();
        try {
            final SelectQuery appSummaryQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            final Join mgDeviceJoin = new Join("ManagedDevice", "MdAppCatalogToResource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            final Join resJoin = new Join("MdAppCatalogToResource", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
            final Join appCategoryRelJoin = new Join("MdAppGroupDetails", "MdAppGroupCategoryRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
            final Join appCategoryJoin = new Join("MdAppGroupCategoryRel", "AppCategory", new String[] { "APP_CATEGORY_ID" }, new String[] { "APP_CATEGORY_ID" }, 2);
            final Join appGroupPackDataJoin = new Join("MdAppGroupDetails", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
            appSummaryQuery.addJoin(mgDeviceJoin);
            appSummaryQuery.addJoin(resJoin);
            appSummaryQuery.addJoin(appCategoryRelJoin);
            appSummaryQuery.addJoin(appCategoryJoin);
            appSummaryQuery.addJoin(appGroupPackDataJoin);
            final Column packageTypeCol = Column.getColumn("MdPackageToAppGroup", "PACKAGE_TYPE");
            appSummaryQuery.addSelectColumn(packageTypeCol);
            Column appCol = Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID");
            appCol = appCol.count();
            appSummaryQuery.addSelectColumn(appCol);
            appSummaryQuery.setCriteria(cri);
            List groupByColumns = new ArrayList();
            groupByColumns.add(packageTypeCol);
            GroupByClause grpByCls = new GroupByClause(groupByColumns);
            appSummaryQuery.setGroupByClause(grpByCls);
            final HashMap packageTypCounteMap = this.setPackageTypeCount(appSummaryQuery);
            appSummaryMap.put("packageTypCounteMap", packageTypCounteMap);
            appSummaryQuery.removeSelectColumn(packageTypeCol);
            appSummaryQuery.removeSelectColumn(appCol);
            final Column installStatusCol = Column.getColumn("MdAppCatalogToResource", "STATUS");
            appSummaryQuery.addSelectColumn(installStatusCol);
            appSummaryQuery.addSelectColumn(appCol);
            groupByColumns = new ArrayList();
            groupByColumns.add(installStatusCol);
            grpByCls = new GroupByClause(groupByColumns);
            appSummaryQuery.setGroupByClause(grpByCls);
            final HashMap installationStatusCountMap = this.setInstallationStatusCount(appSummaryQuery);
            appSummaryMap.put("installationStatusCountMap", installationStatusCountMap);
            appSummaryQuery.removeSelectColumn(installStatusCol);
            appSummaryQuery.removeSelectColumn(appCol);
            final Column categoryColl = Column.getColumn("AppCategory", "APP_CATEGORY_LABEL");
            appSummaryQuery.addSelectColumn(categoryColl);
            appSummaryQuery.addSelectColumn(appCol);
            groupByColumns = new ArrayList();
            groupByColumns.add(categoryColl);
            grpByCls = new GroupByClause(groupByColumns);
            appSummaryQuery.setGroupByClause(grpByCls);
            final HashMap categoryCountMap = this.setcategoryCount(appSummaryQuery);
            appSummaryMap.put("categoryCountMap", categoryCountMap);
            AppsUtil.logger.log(Level.FINE, "getInstalledAppSummaryForUDID : SummaryMap : {0}", categoryCountMap);
        }
        catch (final Exception exp) {
            AppsUtil.logger.log(Level.WARNING, "getInstalledAppSummaryForUDID : DataAccessException while getting Security Info...", exp);
            throw new SyMException(1001, (Throwable)exp);
        }
        return appSummaryMap;
    }
    
    public HashMap getInstalledAppSummaryForResourceID(final Long resourceId) throws SyMException, JSONException {
        final Criteria cri = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceId, 0);
        return this.getInstalledAppSummaryForCriteria(cri);
    }
    
    public HashMap getInstalledAppSummaryForUDID(final String udid) throws SyMException {
        final Criteria cri = new Criteria(Column.getColumn("ManagedDevice", "UDID"), (Object)udid, 0);
        return this.getInstalledAppSummaryForCriteria(cri);
    }
    
    public JSONArray getAppCatalogListJSON(final SelectQuery appListQuery, final Long resourceId, final Long appGroupId, final int platformType) throws SyMException {
        AppsUtil.logger.log(Level.FINE, "Entered into AppsUtil.getListHashFromDO()");
        final JSONArray memberJSONArr = new JSONArray();
        JSONObject detailJSON = null;
        DMDataSetWrapper ds = null;
        try {
            final Long customerId = CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceId);
            final Properties defaultAppSettings = getInstance().getAppSettings(customerId);
            final boolean skipNotification = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("SkipNotifyForNotInstalled");
            ((Hashtable<String, Boolean>)defaultAppSettings).put("skipNotification", skipNotification);
            ds = DMDataSetWrapper.executeQuery((Object)appListQuery);
            final Map applicableVersions = (platformType == 2) ? this.getAllAvailableVersions(resourceId) : null;
            final JSONObject appsDeploymentPolicyForResourceID = new AppDeploymentPolicyImpl().getAppsDeploymentPolicyForResourceId(resourceId);
            while (ds.next()) {
                if (platformType == 1) {
                    detailJSON = this.setIOSAppCatalogJSON(ds, appGroupId);
                }
                else if (platformType == 2 || platformType == 3) {
                    detailJSON = this.setAppCatalogJSON(ds, resourceId, applicableVersions, defaultAppSettings, appsDeploymentPolicyForResourceID);
                }
                memberJSONArr.put((Object)detailJSON);
            }
            AppsUtil.logger.log(Level.FINE, "Finished executing AppsUtil.getListHashFromDO()");
        }
        catch (final Exception exp) {
            AppsUtil.logger.log(Level.WARNING, "Exception in getListHashFromDO...", exp);
        }
        return (platformType == 1) ? memberJSONArr : MDMRestAPIFactoryProvider.getAppsUtilAPI().convertFilePath(memberJSONArr);
    }
    
    private JSONObject getAppCatalogJSON(final SelectQuery appListQuery, final Long appId, final Long collectionId, final String command) throws SyMException {
        AppsUtil.logger.log(Level.FINE, "Entered into AppsUtil.getListHashFromDO()");
        JSONObject detailJSON = null;
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection conn = null;
        DataSet ds = null;
        try {
            conn = relapi.getConnection();
            ds = relapi.executeQuery((Query)appListQuery, conn);
            while (ds.next()) {
                if (command.equalsIgnoreCase("RemoveApplication")) {
                    detailJSON = this.setAndroidRemoveAppJSON(ds, collectionId);
                }
                else {
                    if (!command.equalsIgnoreCase("InstallApplication")) {
                        continue;
                    }
                    detailJSON = this.setAndroidInstallAppJSON(ds, collectionId);
                }
            }
            AppsUtil.logger.log(Level.FINE, "Finished executing AppsUtil.getListHashFromDO()");
        }
        catch (final Exception exp) {
            AppsUtil.logger.log(Level.WARNING, "Exception in getListHashFromDO...", exp);
        }
        finally {
            CustomGroupUtil.getInstance().closeConnection(conn, ds);
        }
        return detailJSON;
    }
    
    public JSONObject setIOSAppCatalogJSON(final DMDataSetWrapper ds, final Long appId) {
        JSONObject detailJSON = null;
        AppsUtil.logger.log(Level.FINE, "Entered into setIOSAppCatalogJSON()");
        try {
            detailJSON = new JSONObject();
            detailJSON.put("PLATFORM_TYPE", ds.getValue("PLATFORM_TYPE"));
            detailJSON.put("APP_ID", ds.getValue("APP_ID"));
            detailJSON.put("APP_NAME", ds.getValue("APP_NAME"));
            detailJSON.put("APP_CATEGORY_NAME", (Object)I18N.getMsg((String)ds.getValue("APP_CATEGORY_LABEL"), new Object[0]));
            String display_image_loc = ds.getValue("DISPLAY_IMAGE_LOC").toString();
            final HashMap hm = new HashMap();
            hm.put("path", display_image_loc);
            hm.put("IS_SERVER", false);
            hm.put("IS_AUTHTOKEN", true);
            display_image_loc = MDMApiFactoryProvider.getMDMAuthTokenUtilAPI().getURLWithAuthToken(hm);
            detailJSON.put("DISPLAY_IMAGE_LOC", (Object)display_image_loc);
            final Long publishedAppId = (Long)ds.getValue("PUBLISHED_APP_ID");
            final Long installedAppId = (Long)ds.getValue("INSTALLED_APP_ID");
            Boolean isUpgrade = false;
            if (installedAppId != null && installedAppId != (long)publishedAppId) {
                isUpgrade = true;
            }
            detailJSON.put("IS_UPGRADE", (Object)isUpgrade);
            String display_image_loc2 = ds.getValue("DISPLAY_IMAGE_LOC").toString();
            hm.put("path", display_image_loc2);
            display_image_loc2 = MDMApiFactoryProvider.getMDMAuthTokenUtilAPI().getURLWithAuthToken(hm);
            detailJSON.put("ICON_URL", (Object)display_image_loc2);
            detailJSON.put("IS_LATEST", ds.getValue("IS_LATEST"));
            detailJSON.put("STATUS", ds.getValue("STATUS"));
            String fullDisplayImageLoc = String.valueOf(ds.getValue("FULL_IMAGE_LOC"));
            if (fullDisplayImageLoc != null) {
                hm.put("path", fullDisplayImageLoc);
                fullDisplayImageLoc = MDMApiFactoryProvider.getMDMAuthTokenUtilAPI().getURLWithAuthToken(hm);
            }
            detailJSON.put("FULL_IMAGE_LOC", (Object)fullDisplayImageLoc);
            detailJSON.put("PACKAGE_ID", ds.getValue("PACKAGE_ID"));
            detailJSON.put("APP_GROUP_ID", ds.getValue("APP_GROUP_ID"));
            detailJSON.put("PACKAGE_TYPE", ds.getValue("PACKAGE_TYPE"));
            detailJSON.put("DESCRIPTION", ds.getValue("DESCRIPTION"));
            MDMRestAPIFactoryProvider.getAppsUtilAPI().addFileDetails(detailJSON, ds);
            final Boolean isPortalPurchasedApp = (Boolean)ds.getValue("IS_PURCHASED_FROM_PORTAL");
            if (isPortalPurchasedApp != null && isPortalPurchasedApp) {
                detailJSON.put("VPP_LICENSE_TYPE", true);
            }
            else {
                detailJSON.put("VPP_LICENSE_TYPE", false);
            }
        }
        catch (final Exception exp) {
            AppsUtil.logger.log(Level.WARNING, "setAppHash : Exception in setIOSAppCatalogJSON", exp);
        }
        return detailJSON;
    }
    
    protected void addFileDetails(final JSONObject detailJSON, final DMDataSetWrapper ds) {
    }
    
    protected void addFileDetailsQuery(final SelectQuery selectQuery) {
    }
    
    public void closeConnection(final Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (final Exception ex2) {
            AppsUtil.logger.log(Level.WARNING, "Exception in closeConnection...", ex2);
        }
    }
    
    public HashMap setPackageTypeCount(final SelectQuery appSummaryQuery) {
        final HashMap packageTypCounteMap = new HashMap();
        try {
            final HashMap summaryMap = MDMUtil.getInstance().executeCountQuery(appSummaryQuery);
            packageTypCounteMap.put("AppleAppStoreFreeAppCount", 0);
            packageTypCounteMap.put("AppleAppStorePaidAppCount", 0);
            packageTypCounteMap.put("EnterpriseAppCount", 0);
            for (final Map.Entry pairs : summaryMap.entrySet()) {
                final int packageType = pairs.getKey();
                final int appCount = pairs.getValue();
                if (packageType == 0) {
                    packageTypCounteMap.put("AppleAppStoreFreeAppCount", appCount);
                }
                else if (packageType == 1) {
                    packageTypCounteMap.put("AppleAppStorePaidAppCount", appCount);
                }
                else {
                    if (packageType != 2) {
                        continue;
                    }
                    packageTypCounteMap.put("EnterpriseAppCount", appCount);
                }
            }
        }
        catch (final Exception ex2) {
            AppsUtil.logger.log(Level.WARNING, "Exception in closeConnection...", ex2);
        }
        return packageTypCounteMap;
    }
    
    public HashMap setInstallationStatusCount(final SelectQuery appSummaryQuery) {
        final HashMap installationStatusCountMap = new HashMap();
        try {
            final HashMap summaryMap = MDMUtil.getInstance().executeCountQuery(appSummaryQuery);
            installationStatusCountMap.put("YettoInstallAppCount", 0);
            installationStatusCountMap.put("InstallingAppCount", 0);
            installationStatusCountMap.put("InstalledAppCount", 0);
            for (final Map.Entry pairs : summaryMap.entrySet()) {
                final int installationStatus = pairs.getKey();
                final int installationStatusCount = pairs.getValue();
                if (installationStatus == 0) {
                    installationStatusCountMap.put("YettoInstallAppCount", installationStatusCount);
                }
                else if (installationStatus == 1) {
                    installationStatusCountMap.put("InstallingAppCount", installationStatusCount);
                }
                else {
                    if (installationStatus != 2) {
                        continue;
                    }
                    installationStatusCountMap.put("InstalledAppCount", installationStatusCount);
                }
            }
        }
        catch (final Exception ex2) {
            AppsUtil.logger.log(Level.WARNING, "Exception in closeConnection...", ex2);
        }
        return installationStatusCountMap;
    }
    
    public HashMap setcategoryCount(final SelectQuery appSummaryQuery) {
        final HashMap categoryCountMap = new HashMap();
        try {
            final HashMap summaryMap = MDMUtil.getInstance().executeCountQuery(appSummaryQuery);
            for (final Map.Entry pairs : summaryMap.entrySet()) {
                final String category = pairs.getKey();
                final int appCount = pairs.getValue();
                if (!category.equalsIgnoreCase("")) {
                    categoryCountMap.put(I18N.getMsg(category, new Object[0]), appCount);
                }
            }
        }
        catch (final Exception ex2) {
            AppsUtil.logger.log(Level.WARNING, "Exception in closeConnection...", ex2);
        }
        return categoryCountMap;
    }
    
    public void updatePublishedAppId(final Long resourceId, final Long appGroupId, final Long publishedAppId) {
        try {
            final Criteria resCri = new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourceId, 0);
            final Criteria appGroupCri = new Criteria(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)appGroupId, 0);
            final Criteria cri = resCri.and(appGroupCri);
            final DataObject dObj = MDMUtil.getPersistence().get("MdAppCatalogToResource", cri);
            if (!dObj.isEmpty()) {
                final Row row = dObj.getRow("MdAppCatalogToResource", cri);
                row.set("PUBLISHED_APP_ID", (Object)publishedAppId);
                dObj.updateRow(row);
                MDMUtil.getPersistence().update(dObj);
            }
        }
        catch (final Exception ex) {
            AppsUtil.logger.log(Level.WARNING, "Exception in updatePublishedAppId", ex);
        }
    }
    
    public boolean isAppExistsInPackage(final String bundleIdentifier, final int platformType, final Long customerId, final Boolean isPortal) {
        final boolean isAppExists = false;
        try {
            final SelectQuery appQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
            final Join appGroupToAppJoin = new Join("MdAppGroupDetails", "MdAppToGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
            final Join appCollnJoin = new Join("MdAppToGroupRel", "MdAppToCollection", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2);
            final Join appGroupToCollectionJoin = new Join("MdAppToCollection", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
            final Join packageJoin = new Join("AppGroupToCollection", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
            final Join appReleaseLabelJoin = new Join("AppGroupToCollection", "AppReleaseLabel", new String[] { "RELEASE_LABEL_ID" }, new String[] { "RELEASE_LABEL_ID" }, 2);
            appQuery.addJoin(appGroupToAppJoin);
            appQuery.addJoin(appCollnJoin);
            appQuery.addJoin(appGroupToCollectionJoin);
            appQuery.addJoin(packageJoin);
            appQuery.addJoin(appReleaseLabelJoin);
            appQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
            final Boolean isCaseSensitive = this.getIsBundleIdCaseSenstive(platformType);
            final Criteria bundleIdentifierCri = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)bundleIdentifier, 0, (boolean)isCaseSensitive);
            final Criteria platformCri = new Criteria(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)platformType, 0);
            final Criteria customerCri = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria collectionIdCri = AppVersionDBUtil.getInstance().getCriteriaForCollectionIdWithMdAppToCollection();
            final Criteria approvedAppVersionCriteria = AppVersionDBUtil.getInstance().getApprovedAppVersionCriteria();
            Criteria cri = bundleIdentifierCri.and(customerCri).and(platformCri).and(collectionIdCri).and(approvedAppVersionCriteria);
            if (isPortal != null) {
                final Criteria portalCri = new Criteria(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), (Object)isPortal, 0);
                cri = cri.and(portalCri);
            }
            appQuery.setCriteria(cri);
            final DataObject appRepDobj = DataAccess.get(appQuery);
            return !appRepDobj.isEmpty();
        }
        catch (final Exception ex) {
            AppsUtil.logger.log(Level.WARNING, "Exception in isAppExists", ex);
            return isAppExists;
        }
    }
    
    public String getAppStoreRegionValue(final Long userID) {
        String region = MDMUtil.getInstance().getMdUserConfigParams(userID, "APP_STORE_SEARCH_COUNTRY");
        if (region.equals("")) {
            region = "US";
        }
        return region;
    }
    
    public boolean isAppExistsInPackage(final String bundleIdentifier, final int platformType, final Long customerId) {
        return this.isAppExistsInPackage(bundleIdentifier, platformType, customerId, null);
    }
    
    public Long getPackageId(final String bundleIdentifier, final int platformType, final Long customerId) {
        try {
            final SelectQuery appQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
            final Join appCusJoin = new Join("MdAppGroupDetails", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
            appQuery.addJoin(appCusJoin);
            appQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            final Boolean isCaseSensitive = this.getIsBundleIdCaseSenstive(platformType);
            final Criteria bundleIdentifierCri = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)bundleIdentifier, 0, (boolean)isCaseSensitive);
            final Criteria platformCri = new Criteria(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)platformType, 0);
            final Criteria customerCri = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria cri = bundleIdentifierCri.and(customerCri).and(platformCri);
            appQuery.setCriteria(cri);
            final DataObject appRepDobj = DataAccess.get(appQuery);
            if (appRepDobj.isEmpty()) {
                return -1L;
            }
            return (Long)appRepDobj.getFirstValue("MdPackageToAppGroup", "PACKAGE_ID");
        }
        catch (final Exception ex) {
            AppsUtil.logger.log(Level.WARNING, "Exception in isAppExists", ex);
            return -1L;
        }
    }
    
    public void addOrUpdateAppStoreRegionValue(final String sCountryName) {
        try {
            final Long userId = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            if (userId != null && sCountryName != null && !sCountryName.equals("")) {
                MDMUtil.getInstance().addOrUpdateMdUserConfigParams(userId, "APP_STORE_SEARCH_COUNTRY", sCountryName);
            }
        }
        catch (final Exception e) {
            AppsUtil.logger.log(Level.SEVERE, "Exception in addOrUpdateAppStoreRegionValue...", e);
        }
    }
    
    public String getAppStoreRegionValue() {
        String sCountryName = "";
        try {
            final Long userId = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            if (userId != null) {
                sCountryName = MDMUtil.getInstance().getMdUserConfigParams(userId, "APP_STORE_SEARCH_COUNTRY");
            }
        }
        catch (final Exception e) {
            AppsUtil.logger.log(Level.SEVERE, "Exception in getting AppStore Region Value...", e);
        }
        return sCountryName;
    }
    
    public SortedMap getCountryNames() {
        SortedMap countryMap = null;
        final Locale[] availLocales = Locale.getAvailableLocales();
        String iso = null;
        String countryCode = null;
        String countryName = null;
        countryMap = new TreeMap();
        for (final Locale locale : availLocales) {
            try {
                iso = locale.getISO3Country();
                countryCode = locale.getCountry();
                countryName = locale.getDisplayCountry();
                if (iso != null && countryCode != null && !"".equals(iso) && !"".equals(countryCode)) {
                    countryMap.put(countryName, countryCode);
                }
            }
            catch (final Exception ex) {
                AppsUtil.logger.log(Level.WARNING, "Error in AppsUtil.getCountryNames() {0}", ex.getMessage());
            }
        }
        return countryMap;
    }
    
    public LinkedHashMap getAppCategoryNames(final int platformType) {
        LinkedHashMap categoryMap = null;
        try {
            final SelectQuery select = (SelectQuery)new SelectQueryImpl(Table.getTable("AppCategory"));
            select.addSelectColumn(new Column("AppCategory", "APP_CATEGORY_ID"));
            select.addSelectColumn(new Column("AppCategory", "APP_CATEGORY_LABEL"));
            final Criteria platformCri = new Criteria(Column.getColumn("AppCategory", "PLATFORM_TYPE"), (Object)platformType, 0);
            select.setCriteria(platformCri);
            select.addSortColumn(new SortColumn("AppCategory", "APP_CATEGORY_ID", true));
            final DataObject dObj = DataAccess.get(select);
            if (!dObj.isEmpty()) {
                categoryMap = new LinkedHashMap();
                final Iterator itr = dObj.getRows("AppCategory");
                while (itr.hasNext()) {
                    final Row row = itr.next();
                    final Object appCategoryId = row.get("APP_CATEGORY_ID");
                    if (appCategoryId != null) {
                        categoryMap.put(I18N.getMsg((String)row.get("APP_CATEGORY_LABEL"), new Object[0]), appCategoryId);
                    }
                }
            }
        }
        catch (final Exception ex) {
            AppsUtil.logger.log(Level.WARNING, "Exception in getAppCategoryNames", ex);
        }
        return categoryMap;
    }
    
    public void addOrUpdateAppCatalogSync(final List resourceList) {
        try {
            if (!resourceList.isEmpty()) {
                Criteria resourceIdCri = null;
                Long resourceId = null;
                final Criteria resourceListCri = new Criteria(Column.getColumn("AppCatalogSync", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
                final DataObject dObj = DataAccess.get("AppCatalogSync", resourceListCri);
                Row appSyncRow = null;
                for (int j = 0; j < resourceList.size(); ++j) {
                    resourceId = resourceList.get(j);
                    resourceIdCri = new Criteria(Column.getColumn("AppCatalogSync", "RESOURCE_ID"), (Object)resourceId, 0);
                    appSyncRow = dObj.getRow("AppCatalogSync", resourceIdCri);
                    if (appSyncRow == null) {
                        appSyncRow = new Row("AppCatalogSync");
                        appSyncRow.set("RESOURCE_ID", (Object)resourceId);
                        appSyncRow.set("SYNC_TIME", (Object)System.currentTimeMillis());
                        dObj.addRow(appSyncRow);
                    }
                    else {
                        appSyncRow = dObj.getRow("AppCatalogSync", resourceIdCri);
                        appSyncRow.set("SYNC_TIME", (Object)System.currentTimeMillis());
                        dObj.updateRow(appSyncRow);
                    }
                }
                MDMUtil.getPersistence().update(dObj);
            }
        }
        catch (final Exception ex) {
            AppsUtil.logger.log(Level.WARNING, "Exception in addOrUpdateAppCatalogSync...", ex);
        }
    }
    
    public Long getAppCatalogSyncTime(final Long resourceId) {
        Long appCatalogSyncTime = -1L;
        try {
            final Criteria resIdCri = new Criteria(Column.getColumn("AppCatalogSync", "RESOURCE_ID"), (Object)resourceId, 0);
            final DataObject dObj = DataAccess.get("AppCatalogSync", resIdCri);
            if (!dObj.isEmpty()) {
                appCatalogSyncTime = (Long)dObj.getValue("AppCatalogSync", "SYNC_TIME", resIdCri);
            }
        }
        catch (final Exception ex) {
            AppsUtil.logger.log(Level.WARNING, "Exception in getAppCatalogSyncTime...", ex);
        }
        return appCatalogSyncTime;
    }
    
    public void updateSyncTimeforApp(final Long appId) {
        final ArrayList resourceList = this.getAppInstalledResourceList(appId);
        if (resourceList != null) {
            this.addOrUpdateAppCatalogSync(resourceList);
        }
    }
    
    public ArrayList getAppInstalledResourceList(final Long appId) {
        ArrayList resourceList = null;
        Long resourceId = null;
        try {
            final Criteria appIdCri = new Criteria(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)appId, 0);
            final DataObject dObj = DataAccess.get("MdAppCatalogToResource", appIdCri);
            if (!dObj.isEmpty()) {
                final Iterator itr = dObj.getRows("MdAppCatalogToResource", appIdCri);
                Row row = null;
                resourceList = new ArrayList();
                while (itr.hasNext()) {
                    row = itr.next();
                    resourceId = (Long)row.get("RESOURCE_ID");
                    resourceList.add(resourceId);
                }
            }
        }
        catch (final Exception ex) {
            AppsUtil.logger.log(Level.WARNING, "Exception in getAppInstalledResourceList...", ex);
        }
        AppsUtil.logger.log(Level.INFO, "List of resourceIDs App is installed: AppGroupID:{0} resourceIDList{1}", new Object[] { appId, resourceList });
        return resourceList;
    }
    
    public Map getManagedDevicesWithVersionOfApp(final Long appProfileId, final List<Long> deviceList) throws DataAccessException {
        final HashMap<Object, Object> map = new HashMap<Object, Object>();
        final SelectQuery installedAppQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ProfileToCollection"));
        final Criteria collnEqlCriteria = new Criteria(Column.getColumn("ProfileToCollection", "COLLECTION_ID"), (Object)Column.getColumn("MdAppToCollection", "COLLECTION_ID"), 0);
        final Criteria profileIdCriteria = new Criteria(Column.getColumn("ProfileToCollection", "PROFILE_ID"), (Object)appProfileId, 0);
        installedAppQuery.addJoin(new Join("ProfileToCollection", "MdAppToCollection", collnEqlCriteria.and(profileIdCriteria), 2));
        final Criteria appIdEqlCriteria = new Criteria(Column.getColumn("MdAppToCollection", "APP_ID"), (Object)Column.getColumn("MdAppCatalogToResource", "PUBLISHED_APP_ID"), 0);
        final Criteria resCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)deviceList.toArray(new Long[0]), 8);
        installedAppQuery.addJoin(new Join("MdAppToCollection", "MdAppCatalogToResource", appIdEqlCriteria.and(resCriteria), 2));
        installedAppQuery.addSelectColumn(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"));
        installedAppQuery.addSelectColumn(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"));
        installedAppQuery.addSelectColumn(Column.getColumn("MdAppCatalogToResource", "PUBLISHED_APP_ID"));
        installedAppQuery.addSelectColumn(Column.getColumn("MdAppToCollection", "COLLECTION_ID"));
        installedAppQuery.addSelectColumn(Column.getColumn("MdAppToCollection", "APP_ID"));
        final DataObject installedAppDO = MDMUtil.getPersistenceLite().get(installedAppQuery);
        if (!installedAppDO.isEmpty()) {
            final Iterator<Row> installedAppRows = installedAppDO.getRows("MdAppCatalogToResource");
            while (installedAppRows.hasNext()) {
                final Row installedAppRow = installedAppRows.next();
                final Long resId = (Long)installedAppRow.get("RESOURCE_ID");
                final Long appId = (Long)installedAppRow.get("PUBLISHED_APP_ID");
                final Row appCollectionRow = installedAppDO.getRow("MdAppToCollection", new Criteria(new Column("MdAppToCollection", "APP_ID"), (Object)appId, 0));
                final Long collectionId = (Long)appCollectionRow.get("COLLECTION_ID");
                map.put(resId, collectionId);
            }
        }
        return map;
    }
    
    public void deleteAppResourceRel(final Long resourceId, final Long appGroupId) {
        try {
            final Criteria resIdCri = new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourceId, 0);
            final Criteria appIdCri = new Criteria(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)appGroupId, 0);
            final Criteria cri = resIdCri.and(appIdCri);
            DataAccess.delete("MdAppCatalogToResource", cri);
        }
        catch (final Exception ex) {
            AppsUtil.logger.log(Level.WARNING, "Exception in deleteAppResourceRel...", ex);
        }
    }
    
    public void revertInstalledAppStatus(final Long appGroupID, final Long resourceID) {
        try {
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("MdAppCatalogToResource");
            final Criteria appGroupCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)appGroupID, 0);
            final Criteria resourceCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourceID, 0);
            updateQuery.setCriteria(appGroupCriteria.and(resourceCriteria));
            updateQuery.setUpdateColumn("INSTALLED_APP_ID", (Object)null);
            updateQuery.setUpdateColumn("STATUS", (Object)0);
            MDMUtil.getPersistence().update(updateQuery);
        }
        catch (final Exception ex) {
            AppsUtil.logger.log(Level.WARNING, "Exception in revertInstalledAppStatus..", ex);
        }
    }
    
    public Long getPublishedAppId(final Long resourceId, final Long appGroupId) {
        Long appId = null;
        try {
            final Criteria resIdCri = new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourceId, 0);
            final Criteria appIdCri = new Criteria(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)appGroupId, 0);
            final Criteria cri = resIdCri.and(appIdCri);
            final DataObject DO = MDMUtil.getPersistence().get("MdAppCatalogToResource", cri);
            if (!DO.isEmpty()) {
                final Row catalogRow = DO.getRow("MdAppCatalogToResource");
                appId = (Long)catalogRow.get("PUBLISHED_APP_ID");
            }
        }
        catch (final Exception ex) {
            AppsUtil.logger.log(Level.WARNING, "Exception in deleteAppResourceRel...", ex);
        }
        return appId;
    }
    
    public Long getPublishedAppId(final Long resourceId, final String bundleIdentifier) {
        Long appId = null;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppCatalogToResource"));
            sQuery.addJoin(new Join("MdAppCatalogToResource", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            final Criteria resIdCri = new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourceId, 0);
            final Criteria bundleIdCri = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)bundleIdentifier, 0);
            final Criteria cri = resIdCri.and(bundleIdCri);
            sQuery.setCriteria(cri);
            sQuery.addSelectColumn(new Column("MdAppCatalogToResource", "*"));
            final DataObject DO = MDMUtil.getPersistence().get(sQuery);
            if (!DO.isEmpty()) {
                final Row catalogRow = DO.getRow("MdAppCatalogToResource");
                appId = (Long)catalogRow.get("PUBLISHED_APP_ID");
            }
        }
        catch (final Exception ex) {
            AppsUtil.logger.log(Level.WARNING, "Exception in getPublishedAppId...", ex);
        }
        return appId;
    }
    
    public void deleteAppResourceRel(final List resourceIdList, final Long appGroupId) {
        try {
            final Criteria resIdCri = new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourceIdList.toArray(), 8);
            final Criteria appIdCri = new Criteria(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)appGroupId, 0);
            final Criteria cri = resIdCri.and(appIdCri);
            DataAccess.delete("MdAppCatalogToResource", cri);
        }
        catch (final Exception ex) {
            AppsUtil.logger.log(Level.WARNING, "Exception in deleteAppResourceRel...", ex);
        }
    }
    
    public void deleteAppResourceRel(final Long resourceId) {
        try {
            final Criteria resIdCri = new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourceId, 0);
            DataAccess.delete("MdAppCatalogToResource", resIdCri);
        }
        catch (final Exception ex) {
            AppsUtil.logger.log(Level.WARNING, "Exception in deleteAppResourceRel...", ex);
        }
    }
    
    public void deleteAppResourceRelFromAppGroupId(final Long appGroupId) {
        try {
            final Criteria appIdCri = new Criteria(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)appGroupId, 0);
            DataAccess.delete("MdAppCatalogToResource", appIdCri);
        }
        catch (final Exception ex) {
            AppsUtil.logger.log(Level.WARNING, "Exception in deleteAppResourceRel...", ex);
        }
    }
    
    private boolean isMarkedForDelete(final Long collectionId, final Long resourceId) {
        boolean isMarkedForDelete = false;
        try {
            final Criteria collnCri = new Criteria(Column.getColumn("RecentProfileForResource", "COLLECTION_ID"), (Object)collectionId, 0);
            final Criteria resIdCri = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceId, 0);
            final Criteria cri = collnCri.and(resIdCri);
            final DataObject dObj = DataAccess.get("RecentProfileForResource", cri);
            if (!dObj.isEmpty()) {
                isMarkedForDelete = (boolean)dObj.getFirstValue("RecentProfileForResource", "MARKED_FOR_DELETE");
            }
        }
        catch (final Exception ex) {
            AppsUtil.logger.log(Level.WARNING, "Exception in isMarkedForDelete...", ex);
        }
        return isMarkedForDelete;
    }
    
    public int getAppPackageType(final Long appGroupID) throws Exception {
        int packageType = -1;
        final Integer pkgType = (Integer)DBUtil.getValueFromDB("MdPackageToAppGroup", "APP_GROUP_ID", (Object)appGroupID, "PACKAGE_TYPE");
        if (pkgType != null) {
            packageType = pkgType;
        }
        return packageType;
    }
    
    public Integer getAppPackageTypeFromCollectionId(final Long collectionId) throws Exception {
        Integer packageType = null;
        final SelectQuery appquery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppToCollection"));
        final Join appToGroupRelJoin = new Join("MdAppToCollection", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2);
        final Join appGroupToPackageJoin = new Join("MdAppToGroupRel", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
        appquery.addJoin(appToGroupRelJoin);
        appquery.addJoin(appGroupToPackageJoin);
        final Criteria resourceIdCri = new Criteria(Column.getColumn("MdAppToCollection", "COLLECTION_ID"), (Object)collectionId, 0);
        appquery.setCriteria(resourceIdCri);
        appquery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "*"));
        final DataObject packageToAppGroupDo = MDMUtil.getPersistence().get(appquery);
        if (!packageToAppGroupDo.isEmpty()) {
            final Row mdPackageToAppGroupRow = packageToAppGroupDo.getRow("MdPackageToAppGroup");
            packageType = (Integer)mdPackageToAppGroupRow.get("PACKAGE_TYPE");
        }
        return packageType;
    }
    
    public Long getAppPackageId(final Long appId) throws Exception {
        final Long packageId = (Long)DBUtil.getValueFromDB("MdPackageToAppData", "APP_ID", (Object)appId, "PACKAGE_ID");
        return packageId;
    }
    
    public String getAppIdentifier(final Long appId) throws Exception {
        final String bundleIdentifier = (String)DBUtil.getValueFromDB("MdAppDetails", "APP_ID", (Object)appId, "IDENTIFIER");
        return bundleIdentifier;
    }
    
    public String getAppMinOSVersion(final Long appId) {
        String minOSVersion = null;
        try {
            minOSVersion = (String)DBUtil.getValueFromDB("MdPackageToAppData", "APP_ID", (Object)appId, "MIN_OS");
        }
        catch (final Exception e) {
            AppsUtil.logger.log(Level.WARNING, "Exception in getminOS...", e);
        }
        return minOSVersion;
    }
    
    public Integer getInstalledAppStatusFromIdentifier(final Long resourceId, final String identifier) throws Exception {
        final SelectQuery appquery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppCatalogToResource"));
        appquery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_ID"));
        appquery.addSelectColumn(Column.getColumn("MdAppDetails", "CUSTOMER_ID"));
        appquery.addSelectColumn(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"));
        appquery.addSelectColumn(Column.getColumn("MdAppCatalogToResource", "INSTALLED_APP_ID"));
        appquery.addSelectColumn(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"));
        appquery.addSelectColumn(Column.getColumn("MdAppCatalogToResource", "STATUS"));
        final Join appCatalogJoin = new Join("MdAppCatalogToResource", "MdAppDetails", new String[] { "INSTALLED_APP_ID" }, new String[] { "APP_ID" }, 2);
        final Criteria resourceIdCri = new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourceId, 0);
        final Criteria identifierCri = new Criteria(Column.getColumn("MdAppDetails", "IDENTIFIER"), (Object)identifier, 0);
        appquery.addJoin(appCatalogJoin);
        appquery.setCriteria(identifierCri);
        final DataObject dObj = MDMUtil.getPersistence().get(appquery);
        final Integer appInstallStatus = (Integer)dObj.getValue("MdAppCatalogToResource", "STATUS", resourceIdCri);
        return appInstallStatus;
    }
    
    public DataObject getAppCatalogDObj(final Long[] appGroupIds) {
        DataObject dObj = null;
        try {
            final Criteria appIdCri = new Criteria(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroupIds, 8);
            final Criteria iOSCri = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)"com.manageengine.mdm.iosagent", 12);
            final Criteria wpCri = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)"d73a6956-c81b-4bcb-ba8d-fe8718735ad7", 12);
            final Criteria windowsCri = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)"ZohoCorp.ManageEngineMDM_hfrrf6a1akhx2", 12);
            final Criteria cri = appIdCri.and(iOSCri.or(wpCri).or(windowsCri));
            dObj = MDMUtil.getPersistence().get("MdAppGroupDetails", cri);
        }
        catch (final Exception ex) {
            AppsUtil.logger.log(Level.WARNING, "Exception occoured in getAppCatalogAppDObj....", ex);
        }
        return dObj;
    }
    
    public boolean checkIfAppCatalog(final Long[] appGroupIds) {
        boolean isAppCatalog = false;
        try {
            final DataObject dObj = this.getAppCatalogDObj(appGroupIds);
            if (!dObj.isEmpty()) {
                isAppCatalog = true;
            }
        }
        catch (final Exception ex) {
            AppsUtil.logger.log(Level.WARNING, "Exception occoured in checkIfAppCatalog....", ex);
        }
        return isAppCatalog;
    }
    
    public boolean checkIfAppCatalogDeleteSafe(final Long[] appGroupIds, final Long customerId) {
        boolean isAppCatalogDeleteSafe = false;
        try {
            final SelectQuery appQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
            final Join grouppackageJoin = new Join("MdAppGroupDetails", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
            final Criteria appIdCri = new Criteria(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroupIds, 8);
            final Criteria iOSNativeAppCri = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)"com.manageengine.mdm.iosagent", 12);
            final Criteria cri = appIdCri.and(iOSNativeAppCri);
            appQuery.addJoin(grouppackageJoin);
            appQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "*"));
            appQuery.setCriteria(cri);
            final DataObject dObj = MDMUtil.getPersistence().get(appQuery);
            if (!dObj.isEmpty()) {
                isAppCatalogDeleteSafe = false;
            }
        }
        catch (final Exception ex) {
            AppsUtil.logger.log(Level.SEVERE, "Exception occoured in checkIfAppCatalogDeleteSafe.... {0}", ex);
        }
        return isAppCatalogDeleteSafe;
    }
    
    public String getAppProfileName(final String bundleIdentifier, final Long customer_id, final Integer platformType) {
        String appName = "";
        try {
            final SelectQuery appQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackageToAppGroup"));
            final Join grouppackageJoin = new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
            final Join packageAppJoin = new Join("MdPackageToAppGroup", "MdPackageToAppData", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2);
            final Join appCollectionJoin = new Join("MdPackageToAppData", "MdAppToCollection", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2);
            final Join collectionProfileJoin = new Join("MdAppToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
            final Join profileNameJoin = new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
            final Join profileCustomerJoin = new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
            final Criteria equalIdentifier = new Criteria(new Column("MdAppGroupDetails", "IDENTIFIER"), (Object)bundleIdentifier, 0, (boolean)Boolean.FALSE);
            final Criteria customeridentifier = new Criteria(new Column("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customer_id, 0);
            final Criteria platformCriteria = new Criteria(new Column("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)platformType, 0);
            final Criteria trashCriteria = new Criteria(new Column("Profile", "IS_MOVED_TO_TRASH"), (Object)false, 0);
            final Criteria combinecriteria = equalIdentifier.and(customeridentifier).and(platformCriteria).and(trashCriteria);
            appQuery.addJoin(grouppackageJoin);
            appQuery.addJoin(packageAppJoin);
            appQuery.addJoin(appCollectionJoin);
            appQuery.addJoin(collectionProfileJoin);
            appQuery.addJoin(profileNameJoin);
            appQuery.addJoin(profileCustomerJoin);
            appQuery.setCriteria(combinecriteria);
            appQuery.addSelectColumn(Column.getColumn("Profile", "*"));
            final DataObject resObj = MDMUtil.getPersistence().get(appQuery);
            if (!resObj.isEmpty()) {
                final Row r = resObj.getFirstRow("Profile");
                appName = (String)r.get("PROFILE_NAME");
            }
            return appName;
        }
        catch (final Exception ex) {
            AppsUtil.logger.log(Level.WARNING, "Exception occoured in getAppProfileName....", ex);
            return appName;
        }
    }
    
    public String getAppCatalogAppName(final Long[] appGroupIds) {
        String appCatalogAppName = "";
        try {
            final DataObject dObj = this.getAppCatalogDObj(appGroupIds);
            if (!dObj.isEmpty()) {
                appCatalogAppName = (String)dObj.getFirstValue("MdAppGroupDetails", "GROUP_DISPLAY_NAME");
            }
        }
        catch (final Exception ex) {
            AppsUtil.logger.log(Level.WARNING, "Exception occoured in getAppCatalog....", ex);
        }
        return appCatalogAppName;
    }
    
    public boolean checkIfWpAppCatalog(final String bundleIdentifier) {
        boolean isWpAppCatalog = false;
        if (bundleIdentifier.trim().equalsIgnoreCase("d73a6956-c81b-4bcb-ba8d-fe8718735ad7")) {
            isWpAppCatalog = true;
        }
        return isWpAppCatalog;
    }
    
    public List getEnterpriseAppsCollection(final List allAppsCollList) throws Exception {
        final List entAppsCollection = new ArrayList();
        for (int j = 0; j < allAppsCollList.size(); ++j) {
            final Long appGroupID = MDMUtil.getInstance().getAppGroupIDFromCollection(allAppsCollList.get(j));
            if (this.getAppPackageType(appGroupID) == 2) {
                entAppsCollection.add(allAppsCollList.get(j));
            }
        }
        return entAppsCollection;
    }
    
    public List getVPPAPPsCollection(final List appsCollList, final Long customerId) {
        List vppAppCollection = new ArrayList();
        vppAppCollection = this.getVppAppsDoFromCollection(appsCollList);
        return vppAppCollection;
    }
    
    private List getVppAppsDoFromCollection(final List collectionList) {
        final List appCollectionList = new ArrayList();
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AppGroupToCollection"));
            sQuery.addJoin(new Join("AppGroupToCollection", "MdLicenseToAppGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            sQuery.addJoin(new Join("MdLicenseToAppGroupRel", "MdLicense", new String[] { "LICENSE_ID" }, new String[] { "LICENSE_ID" }, 2));
            sQuery.addJoin(new Join("AppGroupToCollection", "MDAppAssignableDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            final Criteria appToCollnCriteria = new Criteria(new Column("AppGroupToCollection", "COLLECTION_ID"), (Object)collectionList.toArray(), 8);
            final Criteria vppAppCriteria = new Criteria(Column.getColumn("MdLicense", "LICENSED_TYPE"), (Object)2, 0);
            final Criteria vppDeviceAssignableCriteria = new Criteria(Column.getColumn("MDAppAssignableDetails", "APP_ASSIGNABLE_TYPE"), (Object)2, 0);
            sQuery.setCriteria(appToCollnCriteria.and(vppAppCriteria).and(vppDeviceAssignableCriteria));
            sQuery.addSelectColumn(Column.getColumn("AppGroupToCollection", "*"));
            final DataObject appCollnDo = MDMUtil.getPersistence().get(sQuery);
            if (!appCollnDo.isEmpty()) {
                final Iterator rowIterator = appCollnDo.getRows("AppGroupToCollection");
                Long collectionId = null;
                while (rowIterator.hasNext()) {
                    final Row row = rowIterator.next();
                    collectionId = (Long)row.get("COLLECTION_ID");
                    appCollectionList.add(collectionId);
                }
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return appCollectionList;
    }
    
    public void addOrUpdateAppSettings(final Properties prop) {
        final Long customerId = ((Hashtable<K, Long>)prop).get("customerId");
        try {
            final Criteria cCusId = new Criteria(new Column("MdAppDistributionSettings", "CUSTOMER_ID"), (Object)customerId, 0);
            final DataObject DO = MDMUtil.getPersistence().get("MdAppDistributionSettings", cCusId);
            if (DO.isEmpty()) {
                final Row settingRow = new Row("MdAppDistributionSettings");
                final Boolean isSilentInstall = ((Hashtable<K, Boolean>)prop).get("isSilentInstall");
                if (isSilentInstall != null) {
                    settingRow.set("FORCE_APP_INSTALL", (Object)isSilentInstall);
                }
                final Boolean isNotify = ((Hashtable<K, Boolean>)prop).get("isNotify");
                if (isNotify != null) {
                    settingRow.set("NOTIFY_APP_DISTRIBUTION", (Object)isNotify);
                }
                settingRow.set("CUSTOMER_ID", (Object)customerId);
                DO.addRow(settingRow);
                MDMUtil.getPersistence().add(DO);
            }
            else {
                final Row settingRow = DO.getFirstRow("MdAppDistributionSettings");
                final Boolean isSilentInstall = ((Hashtable<K, Boolean>)prop).get("isSilentInstall");
                if (isSilentInstall != null) {
                    settingRow.set("FORCE_APP_INSTALL", (Object)isSilentInstall);
                }
                final Boolean isNotify = ((Hashtable<K, Boolean>)prop).get("isNotify");
                if (isNotify != null) {
                    settingRow.set("NOTIFY_APP_DISTRIBUTION", (Object)isNotify);
                }
                settingRow.set("CUSTOMER_ID", (Object)customerId);
                DO.updateRow(settingRow);
                MDMUtil.getPersistence().update(DO);
            }
        }
        catch (final Exception ex) {
            AppsUtil.logger.log(Level.SEVERE, "Exception in add or update vpp settings", ex);
        }
    }
    
    public void populateAppSettings(final HttpServletRequest request) {
        try {
            final Long customerID = MSPWebClientUtil.getCustomerID(request);
            final Row appSettings = DBUtil.getRowFromDB("MdAppDistributionSettings", "CUSTOMER_ID", (Object)customerID);
            if (appSettings != null) {
                request.setAttribute("isSilentInstall", appSettings.get("FORCE_APP_INSTALL"));
                request.setAttribute("isNotify", appSettings.get("NOTIFY_APP_DISTRIBUTION"));
            }
            else {
                request.setAttribute("isSilentInstall", (Object)false);
                request.setAttribute("isNotify", (Object)true);
            }
        }
        catch (final Exception ex) {
            AppsUtil.logger.log(Level.SEVERE, null, ex);
        }
    }
    
    public Properties getAppSettings(final long customerId) {
        final Properties prop = new Properties();
        ((Hashtable<String, Boolean>)prop).put("isSilentInstall", false);
        ((Hashtable<String, Boolean>)prop).put("isNotify", false);
        try {
            final Row appSettings = DBUtil.getRowFromDB("MdAppDistributionSettings", "CUSTOMER_ID", (Object)customerId);
            if (appSettings != null) {
                ((Hashtable<String, Object>)prop).put("isSilentInstall", appSettings.get("FORCE_APP_INSTALL"));
                ((Hashtable<String, Object>)prop).put("isNotify", appSettings.get("NOTIFY_APP_DISTRIBUTION"));
            }
        }
        catch (final Exception ex) {
            AppsUtil.logger.log(Level.SEVERE, null, ex);
        }
        return prop;
    }
    
    public Properties getApplicableAppCommandForResources(final Long collectionID, final List resourceList) throws DataAccessException {
        final Properties properties = new Properties();
        final List clonedResourceList = new ArrayList(resourceList);
        final Long appGroupID = MDMUtil.getInstance().getAppGroupIDFromCollection(collectionID);
        final Criteria appGroupIdCri = new Criteria(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)appGroupID, 0);
        final Criteria resIdCri = new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
        final Criteria statusCri = new Criteria(Column.getColumn("MdAppCatalogToResource", "STATUS"), (Object)new Integer(2), 0);
        final Criteria criteria = appGroupIdCri.and(resIdCri).and(statusCri);
        final DataObject dataObject = MDMUtil.getPersistence().get("MdAppCatalogToResource", criteria);
        if (dataObject.isEmpty()) {
            ((Hashtable<String, List>)properties).put("InstallApplication", clonedResourceList);
        }
        else {
            final Iterator iterator = dataObject.getRows("MdAppCatalogToResource");
            final List updatedArrayList = new ArrayList();
            if (iterator.hasNext()) {
                final Row row = iterator.next();
                final Long resourceID = (Long)row.get("RESOURCE_ID");
                if (clonedResourceList.remove(resourceID)) {
                    updatedArrayList.add(resourceID);
                }
            }
            if (!clonedResourceList.isEmpty()) {
                ((Hashtable<String, List>)properties).put("InstallApplication", clonedResourceList);
            }
            if (!updatedArrayList.isEmpty()) {
                ((Hashtable<String, List>)properties).put("UpdateApplication", updatedArrayList);
            }
        }
        return properties;
    }
    
    public Properties getAppleAppCommandForResources(final Boolean isNativeAgent, final Properties commandForResProp, final List finalResourceList) {
        if (isNativeAgent) {
            ((Hashtable<String, List>)commandForResProp).put("ApplicationConfiguration", finalResourceList);
        }
        ((Hashtable<String, List>)commandForResProp).put("InstallApplication", finalResourceList);
        return commandForResProp;
    }
    
    public void resetAppCatalog(final Long resourceId, final int scope) throws Exception {
        final boolean doMigrate = false;
        int appStatusNotEqualTo = 2;
        if (doMigrate) {
            appStatusNotEqualTo = 2;
            DeviceCommandRepository.getInstance().addSecurityCommand(resourceId, "MigrateAppToContainer");
            final List resourceList = new ArrayList();
            resourceList.add(resourceId);
            NotificationHandler.getInstance().SendNotification(resourceList, 2);
        }
        final SelectQuery appCatalogQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppCatalogToResource"));
        final Join packageJoin = new Join("MdAppCatalogToResource", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
        appCatalogQuery.addJoin(packageJoin);
        final Criteria resCriteria = new Criteria(new Column("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourceId, 0);
        final Criteria typeCriteria = new Criteria(new Column("MdPackageToAppGroup", "PACKAGE_TYPE"), (Object)new Integer[] { 0, 1 }, 9);
        final Criteria statusCriteria = new Criteria(new Column("MdAppCatalogToResource", "STATUS"), (Object)appStatusNotEqualTo, 1);
        appCatalogQuery.setCriteria(resCriteria.and(statusCriteria).and(typeCriteria));
        appCatalogQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dO = MDMUtil.getPersistence().get(appCatalogQuery);
        if (!dO.isEmpty()) {
            final Iterator iter = dO.getRows("MdAppCatalogToResource");
            while (iter.hasNext()) {
                final Row row = iter.next();
                row.set("STATUS", (Object)0);
                row.set("REMARKS", (Object)"");
                row.set("UPDATED_AT", (Object)System.currentTimeMillis());
                this.addOrUpdateAppCatalogScopeRel(resourceId, (Long)row.get("APP_GROUP_ID"), this.getScopeForApp(resourceId, (Long)row.get("APP_GROUP_ID")));
                dO.updateRow(row);
            }
        }
        MDMUtil.getPersistence().update(dO);
        final UpdateQuery collnToResQuery = (UpdateQuery)new UpdateQueryImpl("CollnToResources");
        final Criteria collnCriteria = new Criteria(new Column("CollnToResources", "COLLECTION_ID"), (Object)this.getCollectionForResource(resourceId, appStatusNotEqualTo, new int[] { 0, 1 }).toArray(), 8);
        final Criteria resourceCriteria = new Criteria(new Column("CollnToResources", "RESOURCE_ID"), (Object)resourceId, 0);
        collnToResQuery.setUpdateColumn("STATUS", (Object)12);
        collnToResQuery.setUpdateColumn("REMARKS", (Object)"");
        collnToResQuery.setUpdateColumn("AGENT_APPLIED_TIME", (Object)System.currentTimeMillis());
        collnToResQuery.setUpdateColumn("APPLIED_TIME", (Object)System.currentTimeMillis());
        collnToResQuery.setCriteria(resourceCriteria.and(collnCriteria));
        MDMUtil.getPersistence().update(collnToResQuery);
        final List resourceList2 = new ArrayList();
        resourceList2.add(resourceId);
        this.addOrUpdateAppCatalogSync(resourceList2);
    }
    
    public void addOrUpdateAppCatalogScopeRel(final Long resourceId, final Long appGroupId, final Integer scope) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppCatalogToResourceScope"));
        Criteria criteria = new Criteria(new Column("MdAppCatalogToResourceScope", "RESOURCE_ID"), (Object)resourceId, 0);
        criteria = criteria.and(new Criteria(new Column("MdAppCatalogToResourceScope", "APP_GROUP_ID"), (Object)appGroupId, 0));
        sQuery.setCriteria(criteria);
        sQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dO = MDMUtil.getPersistence().get(sQuery);
        if (dO.isEmpty()) {
            final Row row = new Row("MdAppCatalogToResourceScope");
            row.set("RESOURCE_ID", (Object)resourceId);
            row.set("APP_GROUP_ID", (Object)appGroupId);
            row.set("SCOPE", (Object)scope);
            dO.addRow(row);
        }
        else {
            final Row row = dO.getRow("MdAppCatalogToResourceScope");
            row.set("SCOPE", (Object)scope);
            dO.updateRow(row);
        }
        MDMUtil.getPersistence().update(dO);
    }
    
    public void addOrUpdateAppCatalogToGroup(final Long groupResID, final Long appGroupID, final Long publishedAppID) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppCatalogToGroup"));
        Criteria criteria = new Criteria(new Column("MdAppCatalogToGroup", "RESOURCE_ID"), (Object)groupResID, 0);
        criteria = criteria.and(new Criteria(new Column("MdAppCatalogToGroup", "APP_GROUP_ID"), (Object)appGroupID, 0));
        sQuery.setCriteria(criteria);
        sQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dO = MDMUtil.getPersistence().get(sQuery);
        if (dO.isEmpty()) {
            final Row row = new Row("MdAppCatalogToGroup");
            row.set("RESOURCE_ID", (Object)groupResID);
            row.set("APP_GROUP_ID", (Object)appGroupID);
            row.set("PUBLISHED_APP_ID", (Object)publishedAppID);
            row.set("APPROVED_APP_ID", (Object)publishedAppID);
            row.set("APPROVED_VERSION_STATUS", (Object)3);
            row.set("IS_UPDATE_AVAILABLE", (Object)false);
            dO.addRow(row);
        }
        else {
            final Row row = dO.getRow("MdAppCatalogToGroup");
            row.set("PUBLISHED_APP_ID", (Object)publishedAppID);
            row.set("APPROVED_APP_ID", (Object)publishedAppID);
            row.set("APPROVED_VERSION_STATUS", (Object)3);
            row.set("IS_UPDATE_AVAILABLE", (Object)false);
            dO.updateRow(row);
        }
        MDMUtil.getPersistence().update(dO);
    }
    
    public void deleteAppCatalogToGroupRelation(final Long groupRes, final Long appGroupId) {
        try {
            final Criteria resIdCri = new Criteria(Column.getColumn("MdAppCatalogToGroup", "RESOURCE_ID"), (Object)groupRes, 0);
            final Criteria appIdCri = new Criteria(Column.getColumn("MdAppCatalogToGroup", "APP_GROUP_ID"), (Object)appGroupId, 0);
            final Criteria cri = resIdCri.and(appIdCri);
            DataAccess.delete("MdAppCatalogToGroup", cri);
        }
        catch (final Exception ex) {
            AppsUtil.logger.log(Level.WARNING, "Exception in deleteAppCatalogToGroupRelation...", ex);
        }
    }
    
    public void deleteAppCatalogToUserRelation(final List userList, final Long collectionId) {
        try {
            final Long appGroupId = MDMUtil.getInstance().getAppGroupIDFromCollection(collectionId);
            final Criteria resIdCri = new Criteria(Column.getColumn("MdAppCatalogToUser", "RESOURCE_ID"), (Object)userList.toArray(), 8);
            final Criteria appIdCri = new Criteria(Column.getColumn("MdAppCatalogToUser", "APP_GROUP_ID"), (Object)appGroupId, 0);
            final Criteria cri = resIdCri.and(appIdCri);
            DataAccess.delete("MdAppCatalogToUser", cri);
        }
        catch (final Exception ex) {
            AppsUtil.logger.log(Level.WARNING, "Exception in deleteAppCatalogToManagedUserRelation...", ex);
        }
    }
    
    public void handleAppsForContainerRemoved(final Long resourceId) throws DataAccessException {
        final UpdateQuery collnToResQuery = (UpdateQuery)new UpdateQueryImpl("CollnToResources");
        final Criteria collnCriteria = new Criteria(new Column("CollnToResources", "COLLECTION_ID"), (Object)this.getCollectionWithContainerScopeForResource(resourceId).toArray(), 8);
        final Criteria resourceCriteria = new Criteria(new Column("CollnToResources", "RESOURCE_ID"), (Object)resourceId, 0);
        collnToResQuery.setUpdateColumn("STATUS", (Object)new Integer(12));
        collnToResQuery.setUpdateColumn("REMARKS", (Object)"");
        collnToResQuery.setUpdateColumn("APPLIED_TIME", (Object)System.currentTimeMillis());
        collnToResQuery.setUpdateColumn("AGENT_APPLIED_TIME", (Object)System.currentTimeMillis());
        collnToResQuery.setCriteria(resourceCriteria.and(collnCriteria));
        MDMUtil.getPersistence().update(collnToResQuery);
        final SelectQuery appCatalogQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppCatalogToResource"));
        final Join scopeJoin = new Join("MdAppCatalogToResource", "MdAppCatalogToResourceScope", new String[] { "RESOURCE_ID", "APP_GROUP_ID" }, new String[] { "RESOURCE_ID", "APP_GROUP_ID" }, 2);
        final Criteria resCriteria = new Criteria(new Column("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourceId, 0);
        final Criteria scopeCriteria = new Criteria(new Column("MdAppCatalogToResourceScope", "SCOPE"), (Object)1, 0);
        appCatalogQuery.addJoin(scopeJoin);
        appCatalogQuery.setCriteria(resCriteria.and(scopeCriteria));
        appCatalogQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dO = MDMUtil.getPersistence().get(appCatalogQuery);
        if (!dO.isEmpty()) {
            final Iterator iter = dO.getRows("MdAppCatalogToResource");
            while (iter.hasNext()) {
                final Row row = iter.next();
                row.set("STATUS", (Object)0);
                row.set("REMARKS", (Object)"");
                row.set("UPDATED_AT", (Object)System.currentTimeMillis());
                dO.updateRow(row);
            }
            DataAccess.delete("MdAppCatalogToResourceScope", new Criteria(new Column("MdAppCatalogToResourceScope", "RESOURCE_ID"), (Object)resourceId, 0));
        }
        MDMUtil.getPersistence().update(dO);
        final List resourceList = new ArrayList();
        resourceList.add(resourceId);
        this.addOrUpdateAppCatalogSync(resourceList);
    }
    
    private List getCollectionWithContainerScopeForResource(final Long resourceId) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppCatalogToResource"));
        final Criteria resCriteria = new Criteria(new Column("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourceId, 0);
        final Criteria scopeCriteria = new Criteria(new Column("MdAppCatalogToResourceScope", "SCOPE"), (Object)1, 0);
        final Join scopeJoin = new Join("MdAppCatalogToResource", "MdAppCatalogToResourceScope", new String[] { "RESOURCE_ID", "APP_GROUP_ID" }, new String[] { "RESOURCE_ID", "APP_GROUP_ID" }, 2);
        final Join appGroupJoin = new Join("MdAppCatalogToResource", "MdAppToGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
        final Join appCollectionJoin = new Join("MdAppToGroupRel", "MdAppToCollection", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2);
        sQuery.addJoin(appGroupJoin);
        sQuery.addJoin(appCollectionJoin);
        sQuery.addJoin(scopeJoin);
        sQuery.setCriteria(resCriteria.and(scopeCriteria));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dO = MDMUtil.getPersistence().get(sQuery);
        if (!dO.isEmpty()) {
            final List collectionList = new ArrayList();
            final Iterator iter = dO.getRows("MdAppToCollection");
            while (iter.hasNext()) {
                final Row row = iter.next();
                collectionList.add(row.get("COLLECTION_ID"));
            }
            return collectionList;
        }
        return new ArrayList();
    }
    
    public List getCollectionForResource(final Long resourceId, final int appStatusNotEqualTo, final int[] typeNotIn) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppCatalogToResource"));
        final Criteria resCriteria = new Criteria(new Column("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourceId, 0);
        final Join appGroupJoin = new Join("MdAppCatalogToResource", "MdAppToGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
        final Join packageJoin = new Join("MdAppCatalogToResource", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
        final Join appCollectionJoin = new Join("MdAppToGroupRel", "MdAppToCollection", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2);
        sQuery.addJoin(appGroupJoin);
        sQuery.addJoin(appCollectionJoin);
        sQuery.addJoin(packageJoin);
        sQuery.setCriteria(resCriteria);
        if (appStatusNotEqualTo != -1) {
            final Criteria statusCriteria = new Criteria(new Column("MdAppCatalogToResource", "STATUS"), (Object)appStatusNotEqualTo, 1);
            sQuery.setCriteria(sQuery.getCriteria().and(statusCriteria));
        }
        if (typeNotIn != null && typeNotIn.length > 0) {
            final Criteria typeCriteria = new Criteria(new Column("MdPackageToAppGroup", "PACKAGE_TYPE"), (Object)typeNotIn, 9);
            sQuery.setCriteria(sQuery.getCriteria().and(typeCriteria));
        }
        sQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dO = MDMUtil.getPersistence().get(sQuery);
        if (!dO.isEmpty()) {
            final List collectionList = new ArrayList();
            final Iterator iter = dO.getRows("MdAppToCollection");
            while (iter.hasNext()) {
                final Row row = iter.next();
                collectionList.add(row.get("COLLECTION_ID"));
            }
            return collectionList;
        }
        return new ArrayList();
    }
    
    public Long getAppGroupIdFormCommandId(final Long commandId) throws Exception {
        Long appgrpID = null;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdCollectionCommand"));
        final Join collnJoin = new Join("MdCollectionCommand", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
        final Join appGrpJoin = new Join("MdAppToCollection", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2);
        selectQuery.addJoin(collnJoin);
        selectQuery.addJoin(appGrpJoin);
        selectQuery.addSelectColumn(Column.getColumn("MdAppToGroupRel", "*"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("MdCollectionCommand", "COMMAND_ID"), (Object)commandId, 0));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("MdAppToGroupRel");
            appgrpID = (Long)row.get("APP_GROUP_ID");
        }
        return appgrpID;
    }
    
    public int getScopeForApp(final Long resourceId, final Long appGroupId) {
        return this.getScopeForApp(resourceId, appGroupId, -1);
    }
    
    public int getScopeForApp(final Long resourceId, final Long appGroupId, final int errorCode) {
        switch (errorCode) {
            case 15000:
            case 15002: {
                return 0;
            }
            default: {
                try {
                    if (KnoxUtil.getInstance().canApplyKnoxProfile(resourceId)) {
                        final int type = (int)DBUtil.getValueFromDB("MdPackageToAppGroup", "APP_GROUP_ID", (Object)appGroupId, "PACKAGE_TYPE");
                        if (type == 2) {
                            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppCatalogToResource"));
                            final Criteria resCriteria = new Criteria(new Column("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourceId, 0);
                            final Criteria appCriteria = new Criteria(new Column("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)appGroupId, 0);
                            final Criteria statusCriteria = new Criteria(new Column("MdAppCatalogToResource", "STATUS"), (Object)2, 0);
                            sQuery.setCriteria(appCriteria.and(resCriteria).and(statusCriteria));
                            sQuery.addSelectColumn(new Column((String)null, "*"));
                            final DataObject dO = MDMUtil.getPersistence().get(sQuery);
                            if (!dO.isEmpty()) {
                                return this.getScope(resourceId, appGroupId);
                            }
                            final String packageName = this.getAppIdentifier((Long)DBUtil.getValueFromDB("MdAppToGroupRel", "APP_GROUP_ID", (Object)appGroupId, "APP_ID"));
                            final Integer knoxVersion = (Integer)DBUtil.getValueFromDB("ManagedKNOXContainer", "RESOURCE_ID", (Object)resourceId, "KNOX_VERSION");
                            if (!packageName.startsWith("sec_container_1.") && knoxVersion == 1) {
                                return 0;
                            }
                            return 1;
                        }
                    }
                }
                catch (final Exception ex) {
                    Logger.getLogger(AppLicenseMgmtHandler.class.getName()).log(Level.SEVERE, "getScopeForApp", ex);
                }
                return 0;
            }
        }
    }
    
    public int getAppRepositoryAppCount(final Integer platform) {
        int profileCount = -1;
        try {
            Criteria profileCri;
            final Criteria profileTypeCri = profileCri = new Criteria(new Column("Profile", "PROFILE_TYPE"), (Object)2, 0);
            if (platform != null) {
                final Criteria platformCri = new Criteria(new Column("Profile", "PLATFORM_TYPE"), (Object)platform, 0);
                profileCri = profileCri.and(platformCri);
            }
            profileCount = DBUtil.getRecordCount("Profile", "PROFILE_ID", profileCri);
        }
        catch (final Exception ex) {
            AppsUtil.logger.log(Level.SEVERE, "Exception in getProfileCount", ex);
        }
        return profileCount;
    }
    
    public int getAppGroupCount(final int platform) {
        int appCount = -1;
        try {
            final Criteria mdappGroupPlatformCri = new Criteria(new Column("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)platform, 0);
            appCount = DBUtil.getRecordCount("MdAppGroupDetails", "APP_GROUP_ID", mdappGroupPlatformCri);
        }
        catch (final Exception ex) {
            AppsUtil.logger.log(Level.SEVERE, "Exception in getAppRepositoryAppCount", ex);
        }
        return appCount;
    }
    
    private Integer getScope(final Long resourceId, final Long appGroupId) throws DataAccessException {
        Criteria criteria = new Criteria(new Column("MdAppCatalogToResourceScope", "RESOURCE_ID"), (Object)resourceId, 0);
        criteria = criteria.and(new Criteria(new Column("MdAppCatalogToResourceScope", "APP_GROUP_ID"), (Object)appGroupId, 0));
        final DataObject dO = MDMUtil.getPersistence().get("MdAppCatalogToResourceScope", criteria);
        if (dO.isEmpty()) {
            return 0;
        }
        final Integer scope = (Integer)dO.getRow("MdAppCatalogToResourceScope").get("SCOPE");
        return (scope != null) ? scope : 0;
    }
    
    public int getAppRepositoryAppCount(final int platform, final int appType) throws Exception {
        Criteria platformCriteria = new Criteria(new Column("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)platform, 0);
        final Criteria appTypeCri = this.getRepositoryAppTypeCriteria(appType);
        platformCriteria = platformCriteria.and(appTypeCri);
        return this.getAppRepositoryCount(platformCriteria);
    }
    
    public int getAppRepositoryCount(final Criteria c) throws Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("MdPackageToAppGroup"));
        final Join mdAppGroupDetailsJoin = new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
        final Criteria joinCriteria1 = new Criteria(new Column("MdPackageToAppData", "PACKAGE_ID"), (Object)new Column("MdPackageToAppGroup", "PACKAGE_ID"), 0);
        final Criteria joinCriteria2 = new Criteria(new Column("MdPackageToAppData", "APP_GROUP_ID"), (Object)new Column("MdAppGroupDetails", "APP_GROUP_ID"), 0);
        final Join pkgToAppData = new Join("MdPackageToAppGroup", "MdPackageToAppData", joinCriteria1.and(joinCriteria2), 2);
        sQuery.addJoin(mdAppGroupDetailsJoin);
        sQuery.addJoin(pkgToAppData);
        sQuery.setCriteria(c);
        final int appCount = DBUtil.getRecordCount(sQuery, "MdPackageToAppGroup", "PACKAGE_ID");
        return appCount;
    }
    
    public int getIOSEnterpriseAppCount() throws Exception {
        final int platform = 1;
        final int appType = 2;
        Criteria platformCriteria = new Criteria(new Column("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)platform, 0);
        final Criteria appTypeCri = this.getRepositoryAppTypeCriteria(appType);
        final Criteria excludeMacOSCriteria = new Criteria(new Column("MdPackageToAppData", "SUPPORTED_DEVICES"), (Object)16, 1);
        platformCriteria = platformCriteria.and(appTypeCri).and(excludeMacOSCriteria);
        return this.getAppRepositoryCount(platformCriteria);
    }
    
    private Criteria getRepositoryAppTypeCriteria(final int appType) {
        final int androidPlayStoreApp = 0;
        Criteria criteria = null;
        if (appType == androidPlayStoreApp) {
            criteria = new Criteria(new Column("MdPackageToAppGroup", "PACKAGE_TYPE"), (Object)2, 1);
        }
        else {
            criteria = new Criteria(new Column("MdPackageToAppGroup", "PACKAGE_TYPE"), (Object)appType, 0);
        }
        return criteria;
    }
    
    public List<String> getAppNameFromIdentifier(final String[] identifier, final Integer platformType, final Long customerID) {
        final List<String> appNameList = new ArrayList<String>();
        try {
            final Criteria platformCriteria = new Criteria(new Column("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)platformType, 0);
            final Criteria customerIDCriteria = new Criteria(new Column("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria appIdentifierCriteria = new Criteria(new Column("MdAppGroupDetails", "IDENTIFIER"), (Object)identifier, 8);
            final DataObject appDO = MDMUtil.getPersistence().get("MdAppGroupDetails", appIdentifierCriteria.and(platformCriteria).and(customerIDCriteria));
            if (!appDO.isEmpty()) {
                final Iterator appIterator = appDO.getRows("MdAppGroupDetails");
                while (appIterator.hasNext()) {
                    final Row row = appIterator.next();
                    appNameList.add((String)row.get("GROUP_DISPLAY_NAME"));
                }
            }
        }
        catch (final Exception ex) {
            AppsUtil.logger.log(Level.SEVERE, "Exception in getAppNameFromIdentifier {0}", ex);
        }
        return appNameList;
    }
    
    public JSONArray getAppNameFromIdentifierJSON(final String[] identifier, final Integer platformType, final Long customerID) {
        final JSONArray appNameList = new JSONArray();
        try {
            final Criteria platformCriteria = new Criteria(new Column("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)platformType, 0);
            final Criteria customerIDCriteria = new Criteria(new Column("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria appIdentifierCriteria = new Criteria(new Column("MdAppGroupDetails", "IDENTIFIER"), (Object)identifier, 8);
            final DataObject appDO = MDMUtil.getPersistence().get("MdAppGroupDetails", appIdentifierCriteria.and(platformCriteria).and(customerIDCriteria));
            if (!appDO.isEmpty()) {
                final Iterator appIterator = appDO.getRows("MdAppGroupDetails");
                while (appIterator.hasNext()) {
                    final Row row = appIterator.next();
                    final JSONObject appDetail = new JSONObject();
                    appDetail.put("IDENTIFIER", (Object)row.get("IDENTIFIER"));
                    appDetail.put("GROUP_DISPLAY_NAME", (Object)row.get("GROUP_DISPLAY_NAME"));
                    appNameList.put((Object)appDetail);
                }
            }
        }
        catch (final Exception ex) {
            AppsUtil.logger.log(Level.SEVERE, "Exception in getAppNameFromIdentifier {0}", ex);
        }
        return appNameList;
    }
    
    public String getAppNames(final List<String> appNames) {
        final StringBuilder sBuild = new StringBuilder();
        for (final String app : appNames) {
            if (sBuild.length() == 0) {
                sBuild.append(app);
            }
            else {
                sBuild.append(",");
                sBuild.append(app);
            }
        }
        return sBuild.toString();
    }
    
    public Criteria getAppListExcludeCriteria(final Long customerId) {
        Criteria excludeCriteria = null;
        try {
            final List excludeAppGroupIds = new ArrayList();
            final Long wpCompanyHubAppId = WpCompanyHubAppHandler.getInstance().getWPCompanyHubAppId(customerId);
            final Long wpCompanyHubAppGroupId = (Long)DBUtil.getValueFromDB("MdAppToGroupRel", "APP_ID", (Object)wpCompanyHubAppId, "APP_GROUP_ID");
            excludeAppGroupIds.add(wpCompanyHubAppGroupId);
            excludeCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"), (Object)excludeAppGroupIds.toArray(), 9);
        }
        catch (final Exception exp) {
            AppsUtil.logger.log(Level.SEVERE, "Exception thrown in getAppListExcludeCriteria {0}", exp);
        }
        return excludeCriteria;
    }
    
    public Long getPackageId(final Long appGroupId) throws DataAccessException {
        Long appId = null;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackageToAppGroup"));
        selectQuery.setCriteria(new Criteria(new Column("MdPackageToAppGroup", "APP_GROUP_ID"), (Object)appGroupId, 0));
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            appId = (Long)dataObject.getFirstValue("MdPackageToAppGroup", "PACKAGE_ID");
        }
        return (appId == null) ? -1L : appId;
    }
    
    public HashMap<Long, Long> getAppGroupToPackageMap(final List<Long> appGroupIdList) throws DataAccessException {
        final HashMap<Long, Long> appGroupToPackageMap = new HashMap<Long, Long>();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackageToAppGroup"));
        selectQuery.setCriteria(new Criteria(new Column("MdPackageToAppGroup", "APP_GROUP_ID"), (Object)appGroupIdList.toArray(), 8));
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Iterator<Row> iterator = dataObject.getRows("MdPackageToAppGroup");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final Long appGroupId = (Long)row.get("APP_GROUP_ID");
                final Long packageId = (Long)row.get("PACKAGE_ID");
                appGroupToPackageMap.put(appGroupId, packageId);
            }
        }
        return appGroupToPackageMap;
    }
    
    public JSONObject getAppDetailsJson(final Long appId) {
        JSONObject appsMap = null;
        try {
            final Criteria appIdCri = new Criteria(Column.getColumn("MdAppDetails", "APP_ID"), (Object)appId, 0);
            final DataObject dObj = DataAccess.get("MdAppDetails", appIdCri);
            if (!dObj.isEmpty()) {
                final Row appRow = dObj.getFirstRow("MdAppDetails");
                appsMap = new JSONObject();
                appsMap.put("APP_ID", appRow.get("APP_ID"));
                appsMap.put("APP_NAME", appRow.get("APP_NAME"));
                appsMap.put("APP_TYPE", appRow.get("APP_TYPE"));
                appsMap.put("IDENTIFIER", appRow.get("IDENTIFIER"));
                appsMap.put("APP_VERSION", appRow.get("APP_VERSION"));
                appsMap.put("APP_NAME_SHORT_VERSION", appRow.get("APP_NAME_SHORT_VERSION"));
                appsMap.put("PLATFORM_TYPE", appRow.get("PLATFORM_TYPE"));
                appsMap.put("EXTERNAL_APP_VERSION_ID", appRow.get("EXTERNAL_APP_VERSION_ID"));
            }
        }
        catch (final Exception ex) {
            AppsUtil.logger.log(Level.WARNING, "Exception occoured in getAppDetailsFromAppFile....", ex);
        }
        return appsMap;
    }
    
    public JSONObject getInstalledAppDetailsJson(final Long resourceId, final String identifier) {
        JSONObject appdetails = null;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("MdInstalledAppResourceRel"));
            final Join mdAppdetailsJoin = new Join("MdInstalledAppResourceRel", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2);
            final Join mdAppToGroupRelJoin = new Join("MdAppDetails", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2);
            sQuery.addJoin(mdAppdetailsJoin);
            sQuery.addJoin(mdAppToGroupRelJoin);
            final Criteria resIdcri = new Criteria(new Column("MdInstalledAppResourceRel", "RESOURCE_ID"), (Object)resourceId, 0);
            final Criteria identifierCri = new Criteria(new Column("MdAppDetails", "IDENTIFIER"), (Object)identifier, 0);
            sQuery.setCriteria(resIdcri.and(identifierCri));
            sQuery.addSelectColumn(new Column("MdAppDetails", "*"));
            sQuery.addSelectColumn(new Column("MdAppToGroupRel", "*"));
            final DataObject appdetailsDo = MDMUtil.getPersistence().get(sQuery);
            if (!appdetailsDo.isEmpty()) {
                appdetails = new JSONObject();
                final Row row = appdetailsDo.getFirstRow("MdAppDetails");
                appdetails.put("APP_ID", row.get("APP_ID"));
                appdetails.put("APP_NAME", row.get("APP_NAME"));
                appdetails.put("APP_TYPE", row.get("APP_TYPE"));
                appdetails.put("IDENTIFIER", row.get("IDENTIFIER"));
                appdetails.put("APP_VERSION", row.get("APP_VERSION"));
                appdetails.put("APP_NAME_SHORT_VERSION", row.get("APP_NAME_SHORT_VERSION"));
                appdetails.put("PLATFORM_TYPE", row.get("PLATFORM_TYPE"));
                final Row mdAppToGroupRow = appdetailsDo.getFirstRow("MdAppToGroupRel");
                appdetails.put("APP_GROUP_ID", mdAppToGroupRow.get("APP_GROUP_ID"));
            }
        }
        catch (final Exception ex) {
            AppsUtil.logger.log(Level.SEVERE, "Exception in getInstalledAppDetailsJson {0}", ex);
        }
        return appdetails;
    }
    
    public Long[] getProfileIDS(final Long[] packageIDs) throws Exception {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackage"));
        query.addJoin(new Join("MdPackage", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        query.addJoin(new Join("MdPackageToAppGroup", "MdAppToGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        query.addJoin(new Join("MdAppToGroupRel", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        query.addJoin(new Join("MdAppDetails", "MdAppToCollection", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        query.addJoin(new Join("MdAppToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        final Column profileCountColumn = Column.getColumn("ProfileToCollection", "PROFILE_ID").distinct();
        profileCountColumn.setColumnAlias("profileCount");
        query.addSelectColumn(profileCountColumn);
        query.setCriteria(new Criteria(Column.getColumn("MdPackage", "PACKAGE_ID"), (Object)packageIDs, 8));
        final List<Long> distinctValue = new ArrayList<Long>();
        DMDataSetWrapper ds = null;
        try {
            ds = DMDataSetWrapper.executeQuery((Object)query);
            while (ds.next()) {
                final Object value = ds.getValue("profileCount");
                if (value != null && !distinctValue.contains(value)) {
                    distinctValue.add((Long)value);
                }
            }
        }
        catch (final QueryConstructionException | SQLException ex) {
            throw ex;
        }
        return distinctValue.toArray(new Long[distinctValue.size()]);
    }
    
    public void fillMDPackageToAppGroupForm(final JSONObject appJSON, final Boolean isPaidApp, final Boolean isPortalApp) {
        final JSONObject packageToAppGroupForm = appJSON.getJSONObject("MDPackageToAppGroupForm");
        if (isPaidApp != null && isPaidApp) {
            packageToAppGroupForm.put("IS_PAID_APP", (Object)isPaidApp);
        }
        if (isPortalApp != null && isPortalApp) {
            packageToAppGroupForm.put("IS_PURCHASED_FROM_PORTAL", (Object)isPortalApp);
        }
        appJSON.put("MDPackageToAppGroupForm", (Object)packageToAppGroupForm);
    }
    
    public void fillMdAppAssignableDetailsForm(final JSONObject appJSON, final Integer appAssignableType) {
        if (appAssignableType != null) {
            final JSONObject appAssignableDetails = appJSON.getJSONObject("MdAppAssignableDetailsForm");
            appAssignableDetails.put("APP_ASSIGNABLE_TYPE", (Object)appAssignableType);
            appJSON.put("MdAppAssignableDetailsForm", (Object)appAssignableDetails);
        }
    }
    
    public void fillMdPackageToAppDataFrom(final JSONObject appJSON, final Integer supportedDevices, final String storeURL, String description, final String storeID, final String mainfestFileURL, final Long filesize, final String minimumOsVersion) {
        final JSONObject packageToAppDataForm = appJSON.getJSONObject("MdPackageToAppDataFrom");
        if (supportedDevices != null) {
            packageToAppDataForm.put("SUPPORTED_DEVICES", (Object)supportedDevices);
        }
        if (storeURL != null) {
            packageToAppDataForm.put("STORE_URL", (Object)storeURL);
        }
        if (!MDMStringUtils.isEmpty(description)) {
            if (description.length() > 4800) {
                description = description.substring(0, 4795).concat("...");
            }
            packageToAppDataForm.put("DESCRIPTION", (Object)description);
        }
        if (storeID != null) {
            packageToAppDataForm.put("STORE_ID", (Object)storeID);
        }
        if (mainfestFileURL != null) {
            packageToAppDataForm.put("STOREMANIFEST_FILE_URL_id", (Object)mainfestFileURL);
        }
        if (filesize != null) {
            packageToAppDataForm.put("FILE_UPLOAD_SIZE", (Object)filesize);
        }
        if (!MDMStringUtils.isEmpty(minimumOsVersion)) {
            packageToAppDataForm.put("MIN_OS", (Object)minimumOsVersion);
        }
        appJSON.put("MdPackageToAppDataFrom", (Object)packageToAppDataForm);
    }
    
    public DataObject getMDAppCatalogtoResDO(final Long appGroupId, final Criteria criteria) {
        DataObject mdAppCatalogtoResDO = null;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppCatalogToResourceExtn"));
            final Criteria appGroupcriteria = new Criteria(new Column("MdAppCatalogToResourceExtn", "APP_GROUP_ID"), (Object)appGroupId, 0);
            sQuery.setCriteria(appGroupcriteria);
            if (criteria != null) {
                sQuery.setCriteria(sQuery.getCriteria().and(criteria));
            }
            sQuery.addSelectColumn(new Column((String)null, "*"));
            mdAppCatalogtoResDO = MDMUtil.getPersistence().get(sQuery);
            MDMUtil.getPersistence().update(mdAppCatalogtoResDO);
        }
        catch (final Exception ex) {
            AppsUtil.logger.log(Level.SEVERE, "Exception in getMDAppCatalogtoResDO {0}", ex);
        }
        return mdAppCatalogtoResDO;
    }
    
    public DataObject getMDAppCatalogtoResDO(final Long appGroupId) {
        return this.getMDAppCatalogtoResDO(appGroupId, null);
    }
    
    public String getIdentifierFromAppGroupID(final Long appGroupID) throws Exception {
        final String bundleIdentifier = (String)DBUtil.getValueFromDB("MdAppGroupDetails", "APP_GROUP_ID", (Object)appGroupID, "IDENTIFIER");
        return bundleIdentifier;
    }
    
    public Long getAppSupportedArchitecture(final Long appID) {
        Long arch = null;
        try {
            arch = (Long)DBUtil.getValueFromDB("MdPackageToAppData", "APP_ID", (Object)appID, "SUPPORTED_ARCH");
        }
        catch (final Exception e) {
            AppsUtil.logger.log(Level.WARNING, "Exception in getarch...", e);
        }
        return arch;
    }
    
    public int getMSIAppsCount() {
        int msiAppsCount = 0;
        final SelectQuery msiAppsQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
        msiAppsQuery.addJoin(new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        msiAppsQuery.addJoin(new Join("ProfileToCollection", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        msiAppsQuery.addJoin(new Join("MdAppToCollection", "MdPackageToAppData", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        msiAppsQuery.addJoin(new Join("MdAppToCollection", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        msiAppsQuery.setCriteria(new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)2, 0).and(Column.getColumn("Profile", "PLATFORM_TYPE"), (Object)3, 0).and(Column.getColumn("MdPackageToAppData", "APP_FILE_LOC"), (Object)".msi", 11, (boolean)Boolean.FALSE));
        try {
            msiAppsCount = DBUtil.getRecordCount(msiAppsQuery, "Profile", "PROFILE_ID");
        }
        catch (final Exception ex) {
            AppsUtil.logger.log(Level.SEVERE, "Exception while obtaining record count for MSI apps count", ex);
        }
        return msiAppsCount;
    }
    
    public String getSupportedArchCode(final String[] supporttedDevices) {
        int codeVal = 0;
        String code = null;
        Boolean isX64 = Boolean.FALSE;
        Boolean isX65 = Boolean.FALSE;
        Boolean isneutral = Boolean.FALSE;
        Boolean isARM = Boolean.FALSE;
        if (supporttedDevices.length == 0 || (supporttedDevices.length == 1 && supporttedDevices[0] == "")) {
            codeVal = 14;
        }
        else {
            for (int i = 0; i < supporttedDevices.length; ++i) {
                if (supporttedDevices[i].toLowerCase().contains("arm") && !isARM) {
                    codeVal += 2;
                    isARM = Boolean.TRUE;
                }
                if (supporttedDevices[i].toLowerCase().contains("x86") && !isX65) {
                    codeVal += 4;
                    isX65 = Boolean.TRUE;
                }
                if (supporttedDevices[i].toLowerCase().contains("x64") && !isX64) {
                    codeVal += 8;
                    isX64 = Boolean.TRUE;
                }
                if (supporttedDevices[i].toLowerCase().contains("neutral") && !isneutral) {
                    codeVal = 1;
                    isneutral = Boolean.TRUE;
                }
            }
        }
        code = "" + codeVal;
        return code;
    }
    
    public static void addOrUpdateAppToCollectionRelation(final List appID, final Long collectionID) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppToCollection"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppToCollection", "*"));
        final Criteria appCriteria = new Criteria(Column.getColumn("MdAppToCollection", "APP_ID"), (Object)appID.toArray(), 8);
        selectQuery.setCriteria(appCriteria);
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        for (final Long curAppID : appID) {
            final Criteria curAppCriteria = new Criteria(Column.getColumn("MdAppToCollection", "APP_ID"), (Object)curAppID, 0);
            Row row = dataObject.getRow("MdAppToCollection", curAppCriteria);
            if (row == null) {
                row = new Row("MdAppToCollection");
                row.set("APP_ID", (Object)curAppID);
                row.set("COLLECTION_ID", (Object)collectionID);
                dataObject.addRow(row);
            }
            else {
                row.set("COLLECTION_ID", (Object)collectionID);
                dataObject.updateRow(row);
            }
        }
        MDMUtil.getPersistence().update(dataObject);
    }
    
    public static void addOrUpdateAppGrpToCollectionRelation(final Long appGrpID, final Long collectionID, final Long appReleaseLabelId, final Integer appVersionStatus) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AppGroupToCollection"));
        final Column column = new Column("AppGroupToCollection", "*");
        final Criteria criteria = new Criteria(Column.getColumn("AppGroupToCollection", "APP_GROUP_ID"), (Object)appGrpID, 0);
        final Criteria releaseLabelCriteria = new Criteria(Column.getColumn("AppGroupToCollection", "RELEASE_LABEL_ID"), (Object)appReleaseLabelId, 0);
        selectQuery.addSelectColumn(column);
        selectQuery.setCriteria(criteria.and(releaseLabelCriteria));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("AppGroupToCollection");
            row.set("COLLECTION_ID", (Object)collectionID);
            if (appVersionStatus != -1) {
                row.set("APP_VERSION_STATUS", (Object)appVersionStatus);
            }
            dataObject.updateRow(row);
            AppsUtil.logger.log(Level.INFO, "Collection in label id {0} for appGroupId {1} is updated to {2} with version status {3}", new Object[] { appReleaseLabelId, appGrpID, collectionID, appVersionStatus });
        }
        else {
            final Row row = new Row("AppGroupToCollection");
            row.set("APP_GROUP_ID", (Object)appGrpID);
            row.set("COLLECTION_ID", (Object)collectionID);
            row.set("RELEASE_LABEL_ID", (Object)appReleaseLabelId);
            if (appVersionStatus != -1) {
                row.set("APP_VERSION_STATUS", (Object)appVersionStatus);
            }
            dataObject.addRow(row);
            AppsUtil.logger.log(Level.INFO, "Collection id {0} added in label id {1} for appGroupId {1} with version status {3}", new Object[] { collectionID, appReleaseLabelId, appGrpID, appVersionStatus });
        }
        MDMUtil.getPersistence().update(dataObject);
    }
    
    public static void addOrUpdateAppCollectionToReleaseLabelHistory(final JSONObject appCollectionReleaseLabelData) throws Exception {
        final Long collectionId = appCollectionReleaseLabelData.getLong("COLLECTION_ID");
        final Long releaseLabelId = appCollectionReleaseLabelData.getLong("RELEASE_LABEL_ID");
        final Long userId = appCollectionReleaseLabelData.getLong("USER_ID");
        final Long assignedTime = MDMUtil.getCurrentTimeInMillis();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AppCollnToReleaseLabelHistory"));
        final Criteria collnCriteria = new Criteria(Column.getColumn("AppCollnToReleaseLabelHistory", "COLLECTION_ID"), (Object)collectionId, 0);
        final Criteria releaseLabelIdCriteria = new Criteria(Column.getColumn("AppCollnToReleaseLabelHistory", "RELEASE_LABEL_ID"), (Object)releaseLabelId, 0);
        selectQuery.setCriteria(collnCriteria.and(releaseLabelIdCriteria));
        selectQuery.addSelectColumn(Column.getColumn("AppCollnToReleaseLabelHistory", "*"));
        final DataObject dao = MDMUtil.getPersistence().get(selectQuery);
        if (!dao.isEmpty()) {
            AppsUtil.appMgmtLogger.log(Level.INFO, "Already an entry for COLLECTION_ID-{0} and RELEASE_LABEL_ID-{1} is found in the table APPCOLLNTORELEASELABELHISTORY moved by USER_ID-{2} hence updating time {3}, might be the case of app details update", new Object[] { collectionId, releaseLabelId, userId, assignedTime });
        }
        else {
            final Row labelHistoryRow = new Row("AppCollnToReleaseLabelHistory");
            labelHistoryRow.set("COLLECTION_ID", (Object)collectionId);
            labelHistoryRow.set("RELEASE_LABEL_ID", (Object)releaseLabelId);
            labelHistoryRow.set("LABEL_ASSIGNED_TIME", (Object)assignedTime);
            labelHistoryRow.set("LABEL_ASSIGNED_USER", (Object)userId);
            dao.addRow(labelHistoryRow);
            MDMUtil.getPersistence().add(dao);
        }
    }
    
    public JSONObject handleDbChangesForMarkAsStable(final JSONObject appCollectionReleaseLabelData) throws JSONException, DataAccessException {
        final Long packageId = appCollectionReleaseLabelData.getLong("PACKAGE_ID");
        final Long customerId = appCollectionReleaseLabelData.getLong("CUSTOMER_ID");
        final Long userId = appCollectionReleaseLabelData.getLong("USER_ID");
        final Long releaseLabelId = appCollectionReleaseLabelData.getLong("RELEASE_LABEL_ID");
        final Long latestBetaCollectionToBeMarkedAsProduction = appCollectionReleaseLabelData.getLong("COLLECTION_ID");
        final Long appGroupId = appCollectionReleaseLabelData.getLong("APP_GROUP_ID");
        final Long productionReleaseLabelId = AppVersionDBUtil.getInstance().getApprovedReleaseLabelForGivePackage(packageId, customerId);
        final List<Long> releaseLabelsTobeMerged = JSONUtil.getInstance().convertLongJSONArrayTOList(appCollectionReleaseLabelData.getJSONArray("release_labels_to_merge"));
        final Boolean isReleaseLabelsToBeMergedContainsProduction = releaseLabelsTobeMerged.contains(productionReleaseLabelId);
        Long channelToBeRetained = releaseLabelId;
        if (isReleaseLabelsToBeMergedContainsProduction) {
            AppsUtil.logger.log(Level.INFO, "Production channel found in merging channel hence retaining the production channel as target channel");
            channelToBeRetained = productionReleaseLabelId;
            releaseLabelsTobeMerged.add(releaseLabelId);
            releaseLabelsTobeMerged.remove(productionReleaseLabelId);
        }
        AppsUtil.logger.log(Level.INFO, "Channels {0} are being merged with {1}", new Object[] { releaseLabelsTobeMerged, channelToBeRetained });
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("InstallAppPolicy"));
        selectQuery.addJoin(new Join("InstallAppPolicy", "ConfigDataItem", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        selectQuery.addJoin(new Join("ConfigDataItem", "CfgDataToCollection", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        selectQuery.addJoin(new Join("CfgDataToCollection", "AppCollnToReleaseLabelHistory", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(AppVersionDBUtil.getInstance().getJoinForCollectionsLatestAppReleaseLabelFromHistoryTable());
        final Criteria packageIdCriteria = new Criteria(Column.getColumn("InstallAppPolicy", "PACKAGE_ID"), (Object)packageId, 0);
        Criteria releaseLabelCriteria = new Criteria(Column.getColumn("AppCollnToReleaseLabelHistory", "RELEASE_LABEL_ID"), (Object)releaseLabelsTobeMerged.toArray(), 8);
        selectQuery.setCriteria(packageIdCriteria.and(releaseLabelCriteria));
        selectQuery.addSelectColumn(Column.getColumn("AppCollnToReleaseLabelHistory", "COLLN_LABEL_HISTORY_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AppCollnToReleaseLabelHistory", "COLLECTION_ID"));
        final JSONObject statusJson = new JSONObject();
        statusJson.put("status", 200);
        statusJson.put("channel_retained", (Object)channelToBeRetained);
        try {
            MDMUtil.getUserTransaction().begin();
            final DataObject dao = MDMUtil.getPersistence().get(selectQuery);
            final List<Long> collectionIDToBeRemoved = new ArrayList<Long>();
            if (!dao.isEmpty()) {
                final Iterator<Row> appCollnRows = dao.getRows("AppCollnToReleaseLabelHistory");
                while (appCollnRows.hasNext()) {
                    final Row appCollnRow = appCollnRows.next();
                    final Long collectionId = (Long)appCollnRow.get("COLLECTION_ID");
                    final JSONObject collectionReleaseLabelData = new JSONObject();
                    collectionReleaseLabelData.put("COLLECTION_ID", (Object)collectionId);
                    collectionReleaseLabelData.put("RELEASE_LABEL_ID", (Object)channelToBeRetained);
                    collectionReleaseLabelData.put("USER_ID", (Object)userId);
                    addOrUpdateAppCollectionToReleaseLabelHistory(collectionReleaseLabelData);
                    collectionIDToBeRemoved.add(collectionId);
                }
            }
            AppsUtil.logger.log(Level.INFO, "For these  collection ids {0}, an entry in AppCollnToReleaseLabelHistory table has been added as it has been merged with the channel {1} by the user ID {2}", new Object[] { collectionIDToBeRemoved, channelToBeRetained, userId });
            final DeleteQuery appGroupToCollectionBetaRowDelete = (DeleteQuery)new DeleteQueryImpl("AppGroupToCollection");
            final Criteria collectionIdCriteria = new Criteria(Column.getColumn("AppGroupToCollection", "COLLECTION_ID"), (Object)collectionIDToBeRemoved.toArray(), 8);
            appGroupToCollectionBetaRowDelete.setCriteria(collectionIdCriteria);
            MDMUtil.getPersistence().delete(appGroupToCollectionBetaRowDelete);
            AppsUtil.logger.log(Level.INFO, "Successfully deleted the existing AppGroupToCollectionRow for collectionIds-{0}, ReleaseLabelIds-{1}", new Object[] { collectionIDToBeRemoved, releaseLabelsTobeMerged });
            if (isReleaseLabelsToBeMergedContainsProduction) {
                final UpdateQuery appGroupToCollectionProdRowUpdate = (UpdateQuery)new UpdateQueryImpl("AppGroupToCollection");
                final Criteria appGroupIdCriteria = new Criteria(Column.getColumn("AppGroupToCollection", "APP_GROUP_ID"), (Object)appGroupId, 0);
                releaseLabelCriteria = new Criteria(Column.getColumn("AppGroupToCollection", "RELEASE_LABEL_ID"), (Object)productionReleaseLabelId, 0);
                appGroupToCollectionProdRowUpdate.setCriteria(appGroupIdCriteria.and(releaseLabelCriteria));
                appGroupToCollectionProdRowUpdate.setUpdateColumn("COLLECTION_ID", (Object)latestBetaCollectionToBeMarkedAsProduction);
                MDMUtil.getPersistence().update(appGroupToCollectionProdRowUpdate);
                AppsUtil.logger.log(Level.INFO, "Successfully updated the existing AppGroupToCollectionRow for appGroupId-{0}, prodReleaseLabelId-{1} with the collectionId-{2}", new Object[] { appGroupId, productionReleaseLabelId, latestBetaCollectionToBeMarkedAsProduction });
            }
            AppsUtil.logger.log(Level.INFO, "Going to process app update set for appGroupId-{0}", new Object[] { appGroupId });
            this.setAppUpdateForApps(appGroupId, channelToBeRetained, Boolean.FALSE);
            AppsUtil.logger.log(Level.INFO, "Successfully processed app update set for appGroupId-{0}", new Object[] { appGroupId });
            MDMUtil.getUserTransaction().commit();
        }
        catch (final Exception ex) {
            statusJson.put("status", 500);
            statusJson.put("error", (Object)ex.getMessage());
            AppsUtil.logger.log(Level.SEVERE, "Exception in handleDbChangesForMarkAsStable method", ex);
            try {
                MDMUtil.getUserTransaction().rollback();
                statusJson.put("rollback_status", 200);
            }
            catch (final SystemException exp) {
                statusJson.put("rollback_status", 500);
                statusJson.put("rollback_error", (Object)exp.getMessage());
                AppsUtil.logger.log(Level.SEVERE, "Exception in handleDbChangesForMarkAsStable rollback call", (Throwable)exp);
            }
        }
        return statusJson;
    }
    
    public Long getCompatibleAppForResource(final Long collectionID, final Long resourceID) throws DataAccessException {
        final HashMap deviceMap = MDMUtil.getInstance().getMDMDeviceProperties(resourceID);
        return this.getCompatibleAppForResource(collectionID, deviceMap);
    }
    
    public Long getCompatibleAppForResource(final Long collectionID, final HashMap deviceMap) throws DataAccessException {
        Long appID = null;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppToCollection"));
        final Join join = new Join("MdAppToCollection", "MdPackageToAppData", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2);
        selectQuery.addJoin(join);
        final Criteria criteria = new Criteria(Column.getColumn("MdAppToCollection", "COLLECTION_ID"), (Object)collectionID, 0);
        final Criteria compatibilityCriteria = this.getCompatibilityCriteria(deviceMap);
        selectQuery.setCriteria(criteria.and(compatibilityCriteria));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "*"));
        final SortColumn sortColumn = new SortColumn(Column.getColumn("MdPackageToAppData", "SUPPORTED_DEVICES"), true);
        selectQuery.addSortColumn(sortColumn);
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("MdPackageToAppData");
            appID = (Long)row.get("APP_ID");
        }
        return appID;
    }
    
    public Criteria getCompatibilityCriteria(final HashMap deviceMap) {
        Criteria compatibilityCriteria = null;
        final int platformType = deviceMap.get("PLATFORM_TYPE");
        if (platformType == 3) {
            compatibilityCriteria = WpAppSettingsHandler.getInstance().getCriteriaForResource(deviceMap);
        }
        return compatibilityCriteria;
    }
    
    public boolean isResourcesAssociatedToAccountApps(final Long customerID, final int platformType) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdPackage"));
        final Join packageJoin = new Join("MdPackage", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2);
        final Join resourceDataJoin = new Join("MdPackageToAppGroup", "MdAppCatalogToResource", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
        final Join groupDataJoin = new Join("MdPackageToAppGroup", "MdAppCatalogToGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
        selectQuery.addJoin(packageJoin);
        selectQuery.addJoin(resourceDataJoin);
        selectQuery.addJoin(groupDataJoin);
        final Criteria customerCriteria = new Criteria(Column.getColumn("MdPackage", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria platformCriteria = new Criteria(Column.getColumn("MdPackage", "PLATFORM_TYPE"), (Object)platformType, 0);
        final Criteria portalCriteria = new Criteria(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), (Object)true, 0);
        selectQuery.setCriteria(customerCriteria.and(platformCriteria.and(portalCriteria)));
        selectQuery.addSelectColumn(Column.getColumn("MdPackage", "PACKAGE_ID"));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        return !dataObject.isEmpty();
    }
    
    public boolean isAppPurchasedFromPortal(final Long appID) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdPackageToAppGroup"));
        final Join packageJoin = new Join("MdPackageToAppGroup", "MdPackageToAppData", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
        selectQuery.addJoin(packageJoin);
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "PACKAGE_ID"));
        final Criteria appCriteria = new Criteria(Column.getColumn("MdPackageToAppData", "APP_ID"), (Object)appID, 0);
        final Criteria portalCriteria = new Criteria(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), (Object)true, 0);
        selectQuery.setCriteria(portalCriteria.and(appCriteria));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        return !dataObject.isEmpty();
    }
    
    public void setAppUpdateForApps(final Long appGroupId, final Long releaseLabelId, final Boolean isSameVersionUpload) throws DataAccessException {
        this.setAppUpdateForApps(appGroupId, Arrays.asList(releaseLabelId), isSameVersionUpload);
    }
    
    public void setAppUpdateForApps(final Long appGrpID, final List appReleaseLabelIds, final Boolean isSameVersionUpload) throws DataAccessException {
        AppsUtil.logger.log(Level.INFO, "[APP] [UPDATE] [setAppUpdateForApps] Setting App update for App Group {0}--{1}--{2}", new Object[] { appGrpID, appReleaseLabelIds, isSameVersionUpload });
        AppsUtil.logger.log(Level.INFO, "[APP] [UPDATE] [setAppUpdateForApps] This app group is probably a enterprise app", appGrpID);
        final SelectQuery latestSelectQuery = (SelectQuery)new SelectQueryImpl(new Table("AppGroupToCollection"));
        latestSelectQuery.addJoin(new Join("AppGroupToCollection", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        latestSelectQuery.setCriteria(new Criteria(Column.getColumn("AppGroupToCollection", "APP_GROUP_ID"), (Object)appGrpID, 0).and(new Criteria(Column.getColumn("AppGroupToCollection", "RELEASE_LABEL_ID"), (Object)appReleaseLabelIds.toArray(), 8)));
        latestSelectQuery.addSelectColumn(Column.getColumn("MdAppToCollection", "*"));
        final DataObject laltestDataObject = MDMUtil.getPersistence().get(latestSelectQuery);
        final List latestAppIdList = new ArrayList();
        final Iterator itr = laltestDataObject.getRows("MdAppToCollection");
        while (itr.hasNext()) {
            final Row row = itr.next();
            latestAppIdList.add(row.get("APP_ID"));
        }
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("MdAppCatalogToResourceExtn");
        final Join extnJoin = new Join("MdAppCatalogToResourceExtn", "MdAppCatalogToResource", new String[] { "RESOURCE_ID", "APP_GROUP_ID" }, new String[] { "RESOURCE_ID", "APP_GROUP_ID" }, 2);
        final Join mdAppToCollectionJoin = new Join("MdAppCatalogToResource", "MdAppToCollection", new String[] { "APPROVED_APP_ID" }, new String[] { "APP_ID" }, 2);
        final Join appCollnReleaseLabelJoin = new Join("MdAppToCollection", "AppCollnToReleaseLabelHistory", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
        final Join appCollnReleaseLabelToMaxJoin = AppVersionDBUtil.getInstance().getJoinForCollectionsLatestAppReleaseLabelFromHistoryTable();
        updateQuery.addJoin(extnJoin);
        updateQuery.addJoin(mdAppToCollectionJoin);
        updateQuery.addJoin(appCollnReleaseLabelJoin);
        updateQuery.addJoin(appCollnReleaseLabelToMaxJoin);
        final Criteria resCriteria = new Criteria(Column.getColumn("MdAppCatalogToResourceExtn", "APP_GROUP_ID"), (Object)appGrpID, 0);
        final Criteria appCatalogToResCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)appGrpID, 0);
        final Criteria publishedCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "PUBLISHED_APP_ID"), (Object)latestAppIdList.toArray(), 9);
        final Criteria appReleaseLabelCriteria = new Criteria(Column.getColumn("AppCollnToReleaseLabelHistory", "RELEASE_LABEL_ID"), (Object)appReleaseLabelIds.toArray(), 8);
        updateQuery.setCriteria(resCriteria.and(appCatalogToResCriteria).and(appReleaseLabelCriteria));
        if (!isSameVersionUpload) {
            updateQuery.setCriteria(updateQuery.getCriteria().and(publishedCriteria));
        }
        updateQuery.setUpdateColumn("IS_UPDATE_AVAILABLE", (Object)true);
        updateQuery.setUpdateColumn("PUBLISHED_APP_SOURCE", (Object)MDMCommonConstants.UNASSIGNED_APP_UPDATE);
        final UpdateQuery updateQuery2 = (UpdateQuery)new UpdateQueryImpl("MdAppCatalogToResourceExtn");
        updateQuery2.setCriteria(new Criteria(Column.getColumn("MdAppCatalogToResourceExtn", "APP_GROUP_ID"), (Object)appGrpID, 0));
        updateQuery2.setUpdateColumn("PUBLISHED_APP_SOURCE", (Object)MDMCommonConstants.UNASSIGNED_APP_UPDATE);
        MDMUtil.getPersistence().update(updateQuery);
        MDMUtil.getPersistence().update(updateQuery2);
        final UpdateQuery groupUpdateQuery = (UpdateQuery)new UpdateQueryImpl("MdAppCatalogToGroup");
        final Join mdAppGroupToCollectionJoin = new Join("MdAppCatalogToGroup", "MdAppToCollection", new String[] { "APPROVED_APP_ID" }, new String[] { "APP_ID" }, 2);
        groupUpdateQuery.addJoin(mdAppGroupToCollectionJoin);
        groupUpdateQuery.addJoin(appCollnReleaseLabelJoin);
        groupUpdateQuery.addJoin(appCollnReleaseLabelToMaxJoin);
        final Criteria updateGrpCriteria = new Criteria(Column.getColumn("MdAppCatalogToGroup", "APP_GROUP_ID"), (Object)appGrpID, 0);
        final Criteria publishedMdAppCatalogCriteria = new Criteria(Column.getColumn("MdAppCatalogToGroup", "PUBLISHED_APP_ID"), (Object)latestAppIdList.toArray(), 9);
        groupUpdateQuery.setCriteria(updateGrpCriteria.and(appReleaseLabelCriteria));
        if (!isSameVersionUpload) {
            groupUpdateQuery.setCriteria(groupUpdateQuery.getCriteria().and(publishedMdAppCatalogCriteria));
        }
        groupUpdateQuery.setUpdateColumn("IS_UPDATE_AVAILABLE", (Object)true);
        MDMUtil.getPersistence().update(groupUpdateQuery);
        AppsUtil.logger.log(Level.INFO, "[APP] [UPDATE] [setAppUpdateForApps] Setting App update for App Group {0} for AllGroups", new Object[] { appGrpID });
        final UpdateQuery userUpdateQuery = (UpdateQuery)new UpdateQueryImpl("MdAppCatalogToUser");
        final Join mdAppCatalogUserToCollectionJoin = new Join("MdAppCatalogToUser", "MdAppToCollection", new String[] { "APPROVED_APP_ID" }, new String[] { "APP_ID" }, 2);
        userUpdateQuery.addJoin(mdAppCatalogUserToCollectionJoin);
        userUpdateQuery.addJoin(appCollnReleaseLabelJoin);
        userUpdateQuery.addJoin(appCollnReleaseLabelToMaxJoin);
        final Criteria publishedMdAppCatalogToUserCriteria = new Criteria(Column.getColumn("MdAppCatalogToUser", "PUBLISHED_APP_ID"), (Object)latestAppIdList.toArray(), 9);
        final Criteria appGroupUserCriteria = new Criteria(Column.getColumn("MdAppCatalogToUser", "APP_GROUP_ID"), (Object)appGrpID, 0);
        userUpdateQuery.setCriteria(appGroupUserCriteria.and(appReleaseLabelCriteria));
        if (!isSameVersionUpload) {
            userUpdateQuery.setCriteria(userUpdateQuery.getCriteria().and(publishedMdAppCatalogToUserCriteria));
        }
        userUpdateQuery.setUpdateColumn("IS_UPDATE_AVAILABLE", (Object)true);
        MDMUtil.getPersistence().update(userUpdateQuery);
        AppsUtil.logger.log(Level.INFO, "[APP] [UPDATE] [setAppUpdateForApps] Setting App update for App Group {0} for Users", new Object[] { appGrpID });
    }
    
    public void setApprovedAppIdForResource(final Long releaseLabelId, final Long appGroupId, final Long approvedAppId) throws DataAccessException, Exception {
        this.setApprovedAppIdForResource(Arrays.asList(releaseLabelId), appGroupId, approvedAppId);
    }
    
    public void setApprovedAppIdForResource(final List releaseLabelIds, final Long appGroupId, final Long approvedAppId) throws DataAccessException, Exception {
        AppsUtil.logger.log(Level.INFO, "[APP] [UPDATE] [setApprovedAppId] Setting App update for App Group {0}--{1}--{2}", new Object[] { appGroupId, releaseLabelIds, approvedAppId });
        AppsUtil.logger.log(Level.INFO, "Setting app update for custom group");
        AppUpdatesToResourceHandler.getInstance(101).associateAppUpdateForResource(releaseLabelIds, appGroupId, approvedAppId);
        AppsUtil.logger.log(Level.INFO, "Setting app update for user");
        AppUpdatesToResourceHandler.getInstance(2).associateAppUpdateForResource(releaseLabelIds, appGroupId, approvedAppId);
        AppsUtil.logger.log(Level.INFO, "Setting app update for device");
        AppUpdatesToResourceHandler.getInstance(120).associateAppUpdateForResource(releaseLabelIds, appGroupId, approvedAppId);
    }
    
    public void setAppPublishedSource(final Long resID, final Long appGroupID, final int type) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppCatalogToResourceExtn"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppCatalogToResourceExtn", "*"));
        final Criteria appGrpCriteria = new Criteria(Column.getColumn("MdAppCatalogToResourceExtn", "APP_GROUP_ID"), (Object)appGroupID, 0);
        final Criteria resCriteria = new Criteria(Column.getColumn("MdAppCatalogToResourceExtn", "RESOURCE_ID"), (Object)resID, 0);
        selectQuery.setCriteria(resCriteria.and(appGrpCriteria));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("MdAppCatalogToResourceExtn");
            row.set("PUBLISHED_APP_SOURCE", (Object)type);
            dataObject.updateRow(row);
            MDMUtil.getPersistence().update(dataObject);
        }
    }
    
    public List installedAppResourceListFromIdentifier(final List resourceList, final String identifier) {
        final List removedList = new ArrayList();
        for (int i = 0; i < resourceList.size(); ++i) {
            final Long resourceId = resourceList.get(i);
            final JSONObject appJSON = this.getInstalledAppDetailsJson(resourceId, identifier);
            if (appJSON == null || appJSON.length() == 0) {
                removedList.add(resourceId);
            }
        }
        resourceList.removeAll(removedList);
        return removedList;
    }
    
    public String getSilentInstallAppHelpUrl(final Integer packageType, final Integer platformType) {
        String helpUrl = "";
        if (packageType != null && platformType == 1) {
            if (packageType == 2) {
                helpUrl = "$(mdmUrl)/help/app_management/ios_app_management.html?$(traceurl)&pgSrc=$(pageSource)#Enterprise_App_for_iOS";
            }
            else {
                helpUrl = "$(mdmUrl)/how-to/silent-installation-ios-apps.html?$(traceurl)&pgSrc=$(pageSource)";
            }
        }
        return helpUrl;
    }
    
    public HashMap getPackageIdsFromProfileIds(final List profileIDs) {
        final HashMap hashMap = new HashMap();
        final List<Long> packageIds = new ArrayList<Long>();
        final List<Long> appgrpId = new ArrayList<Long>();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Profile"));
        final Join collnJoin = new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
        final Join appJoin = new Join("ProfileToCollection", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
        final Join packageJoin = new Join("MdAppToCollection", "MdPackageToAppData", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2);
        final Join profileToCustomerJoin = new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
        final Join packagetoappgrpJoin = new Join("MdPackageToAppData", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
        final Criteria criteria = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileIDs.toArray(), 8);
        selectQuery.addJoin(collnJoin);
        selectQuery.addJoin(appJoin);
        selectQuery.addJoin(packageJoin);
        selectQuery.addJoin(profileToCustomerJoin);
        selectQuery.addJoin(packagetoappgrpJoin);
        selectQuery.setCriteria(criteria);
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "PACKAGE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "APP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ProfileToCustomerRel", "PROFILE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"));
        final Connection con = null;
        Long customerID = null;
        DMDataSetWrapper dataSet = null;
        try {
            dataSet = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (dataSet.next()) {
                customerID = (Long)dataSet.getValue("CUSTOMER_ID");
                final Boolean isPurchasedFromPortal = (Boolean)dataSet.getValue("IS_PURCHASED_FROM_PORTAL");
                if (!isPurchasedFromPortal) {
                    packageIds.add((Long)dataSet.getValue("PACKAGE_ID"));
                    appgrpId.add((Long)dataSet.getValue("APP_GROUP_ID"));
                }
                else {
                    profileIDs.remove(dataSet.getValue("PROFILE_ID"));
                }
            }
            hashMap.put("PackageIDs", packageIds);
            hashMap.put("AppGroupIDs", appgrpId);
            hashMap.put("CustomerID", customerID);
        }
        catch (final Exception e) {
            AppsUtil.logger.log(Level.SEVERE, "Exception in fetching getPackageIdsFromProfileIds ", e);
        }
        return hashMap;
    }
    
    public void setAppUpdateForResource(final Map<Long, List> collnToApplicableRes, final List<Long> collectionList, final Boolean isUpdateAvialable) {
        for (final Long collectionId : collectionList) {
            final List resourceList = collnToApplicableRes.get(collectionId);
            if (resourceList != null && !resourceList.isEmpty()) {
                final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("MdAppCatalogToResourceExtn");
                updateQuery.addJoin(new Join("MdAppCatalogToResourceExtn", "MdAppToGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
                updateQuery.addJoin(new Join("MdAppToGroupRel", "MdAppToCollection", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
                final Criteria collectionCriteria = new Criteria(Column.getColumn("MdAppToCollection", "COLLECTION_ID"), (Object)collectionId, 0);
                final Criteria resourceCriteria = new Criteria(Column.getColumn("MdAppCatalogToResourceExtn", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
                updateQuery.setCriteria(collectionCriteria.and(resourceCriteria));
                updateQuery.setUpdateColumn("IS_UPDATE_AVAILABLE", (Object)isUpdateAvialable);
                updateQuery.setUpdateColumn("PUBLISHED_APP_SOURCE", (Object)MDMCommonConstants.ASSOCIATED_APP_SOURCE_UNKNOWN);
                try {
                    MDMUtil.getPersistence().update(updateQuery);
                }
                catch (final DataAccessException e) {
                    AppsUtil.logger.log(Level.WARNING, "Exception in Updating isUpdateAvailable", (Throwable)e);
                }
            }
        }
    }
    
    public String getLatestAppVersionForBundleIdentifier(final Long customerID, final String bundleID) {
        String version = null;
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
            query.addJoin(new Join("MdAppGroupDetails", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            query.addJoin(new Join("AppGroupToCollection", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            query.addJoin(new Join("MdAppToCollection", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
            Criteria criteria = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)bundleID, 0);
            criteria = criteria.and(new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerID, 0));
            query.setCriteria(criteria);
            query.addSelectColumn(Column.getColumn("MdAppDetails", "APP_ID"));
            query.addSelectColumn(Column.getColumn("MdAppDetails", "APP_VERSION"));
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            final Row row = dataObject.getFirstRow("MdAppDetails");
            version = (String)row.get("APP_VERSION");
        }
        catch (final Exception e) {
            AppsUtil.logger.log(Level.SEVERE, "Unable to get app version for bundle identifier", e);
        }
        return version;
    }
    
    public void addOrUpdateAppCatalogToGroup(final List groupResList, final Long collectionID) throws Exception {
        final Long appGroupID = MDMUtil.getInstance().getAppGroupIDFromCollection(collectionID);
        final Long appID = MDMUtil.getInstance().getAppIDFromCollection(collectionID);
        for (final Object groupRes : groupResList) {
            getInstance().addOrUpdateAppCatalogToGroup((Long)groupRes, appGroupID, appID);
        }
    }
    
    public void deleteAppCatalogToGroupRelation(final List groupResList, final Long collectionID) throws DataAccessException {
        final Long appGroupID = MDMUtil.getInstance().getAppGroupIDFromCollection(collectionID);
        for (final Object groupRes : groupResList) {
            getInstance().deleteAppCatalogToGroupRelation((Long)groupRes, appGroupID);
        }
    }
    
    public JSONObject getAppDetailsFromCollectionId(final Long collectionId) {
        JSONObject object = null;
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("MdAppToCollection"));
            query.addJoin(new Join("MdAppToCollection", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
            query.addJoin(new Join("MdAppToGroupRel", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            final Criteria criteria = new Criteria(new Column("MdAppToCollection", "COLLECTION_ID"), (Object)collectionId, 0);
            query.setCriteria(criteria);
            query.addSelectColumn(new Column((String)null, "*"));
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            if (!dataObject.isEmpty()) {
                object = new JSONObject();
                final Row appIdRow = dataObject.getFirstRow("MdAppToGroupRel");
                final Long appId = (Long)appIdRow.get("APP_ID");
                final Row appDetailRow = dataObject.getFirstRow("MdAppGroupDetails");
                final Long appGroupId = (Long)appDetailRow.get("APP_GROUP_ID");
                final String appName = (String)appDetailRow.get("GROUP_DISPLAY_NAME");
                final String bundleIdentifier = (String)appDetailRow.get("IDENTIFIER");
                object.put("APP_ID", (Object)appId);
                object.put("APP_GROUP_ID", (Object)appGroupId);
                object.put("IDENTIFIER", (Object)bundleIdentifier);
                object.put("GROUP_DISPLAY_NAME", (Object)appName);
            }
        }
        catch (final Exception e) {
            AppsUtil.logger.log(Level.SEVERE, "Exception while getting appDetails for Collection", e);
        }
        return object;
    }
    
    public Properties isiOSDeviceApplicableForSilentDistribution(final Long appCollectionID, final List resourceList, final Long customerId) {
        final Properties resourceApplicable = new Properties();
        final List appList = new ArrayList();
        appList.add(appCollectionID);
        ((Hashtable<String, List>)resourceApplicable).put("RESOURCELIST", resourceList);
        ((Hashtable<String, List>)resourceApplicable).put("APPSCOLLECTION", appList);
        return resourceApplicable;
    }
    
    public JSONObject getIOSSystemAppDetails(final Long systemAppId) {
        JSONObject systemApp = null;
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("IOSSystemApps"));
            query.setCriteria(new Criteria(new Column("IOSSystemApps", "APP_ID"), (Object)systemAppId, 0));
            query.addSelectColumn(new Column((String)null, "*"));
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            if (!dataObject.isEmpty()) {
                final Row appRow = dataObject.getFirstRow("IOSSystemApps");
                systemApp = appRow.getAsJSON();
            }
        }
        catch (final Exception e) {
            AppsUtil.logger.log(Level.SEVERE, "Exception in getting system apps", e);
        }
        return systemApp;
    }
    
    public void addOrUpdateAppCatalogToUser(final List userList, final Long collectionId) throws Exception {
        final Long appGroupId = MDMUtil.getInstance().getAppGroupIDFromCollection(collectionId);
        final Long appID = MDMUtil.getInstance().getAppIDFromCollection(collectionId);
        final DataObject finalDO = MDMUtil.getPersistence().constructDataObject();
        final List usersSplitList = MDMUtil.getInstance().splitListIntoSubLists(userList, 500);
        for (final List userSubList : usersSplitList) {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppCatalogToUser"));
            final Criteria userCriteria = new Criteria(Column.getColumn("MdAppCatalogToUser", "RESOURCE_ID"), (Object)userSubList.toArray(), 8);
            final Criteria appGroupCriteria = new Criteria(Column.getColumn("MdAppCatalogToUser", "APP_GROUP_ID"), (Object)appGroupId, 0);
            selectQuery.setCriteria(userCriteria.and(appGroupCriteria));
            selectQuery.addSelectColumn(Column.getColumn("MdAppCatalogToUser", "*"));
            final DataObject existingDO = MDMUtil.getPersistence().get(selectQuery);
            for (final Object userId : userSubList) {
                Row resRow = null;
                if (!existingDO.isEmpty()) {
                    final Criteria userIDCriteria = new Criteria(Column.getColumn("MdAppCatalogToUser", "RESOURCE_ID"), (Object)userId, 0);
                    final Criteria appGroupIdCriteria = new Criteria(Column.getColumn("MdAppCatalogToUser", "APP_GROUP_ID"), (Object)appGroupId, 0);
                    resRow = existingDO.getRow("MdAppCatalogToUser", userIDCriteria.and(appGroupIdCriteria));
                }
                if (resRow == null) {
                    resRow = new Row("MdAppCatalogToUser");
                    resRow.set("APP_GROUP_ID", (Object)appGroupId);
                    resRow.set("RESOURCE_ID", userId);
                    resRow.set("PUBLISHED_APP_ID", (Object)appID);
                    resRow.set("APPROVED_APP_ID", (Object)appID);
                    resRow.set("APPROVED_VERSION_STATUS", (Object)3);
                    resRow.set("IS_UPDATE_AVAILABLE", (Object)false);
                    finalDO.addRow(resRow);
                }
                else {
                    resRow.set("IS_UPDATE_AVAILABLE", (Object)false);
                    resRow.set("APPROVED_APP_ID", (Object)appID);
                    resRow.set("PUBLISHED_APP_ID", (Object)appID);
                    resRow.set("APPROVED_VERSION_STATUS", (Object)3);
                    finalDO.updateBlindly(resRow);
                }
            }
        }
        MDMUtil.getPersistence().update(finalDO);
    }
    
    public String getiOSSupportedDevicesString(final int data) {
        String displayValue = "";
        try {
            final ArrayList supportedDevices = new ArrayList();
            if ((data & 0x2) > 0) {
                supportedDevices.add(I18N.getMsg("dc.mdm.actionlog.appmgmt.iphone", new Object[0]));
            }
            if ((data & 0x4) > 0) {
                supportedDevices.add(I18N.getMsg("mdm.os.ipod", new Object[0]));
            }
            if ((data & 0x1) > 0) {
                supportedDevices.add(I18N.getMsg("dc.mdm.actionlog.appmgmt.ipad", new Object[0]));
            }
            if ((data & 0x8) > 0) {
                supportedDevices.add(I18N.getMsg("mdm.os.tvos", new Object[0]));
            }
            if ((data & 0x10) > 0) {
                supportedDevices.add(I18N.getMsg("mdm.os.mac", new Object[0]));
            }
            for (final Object str : supportedDevices) {
                if (displayValue.equalsIgnoreCase("")) {
                    displayValue = str.toString();
                }
                else {
                    displayValue = displayValue.concat(",").concat(str.toString());
                }
            }
        }
        catch (final Exception ex) {
            AppsUtil.logger.log(Level.SEVERE, "Exception in getting system apps {0}", ex);
        }
        return displayValue;
    }
    
    public String replaceSplCharsInAppName(String appName) {
        appName = appName.replaceAll("[<>*\\[\\]/\\\\{}\"]", " ");
        return appName;
    }
    
    public String[] getArchitecture(final long supportedArch) throws Exception {
        final ArrayList<String> architecture = new ArrayList<String>();
        if ((supportedArch & 0x2L) == 0x2L) {
            architecture.add(I18N.getMsg("mdm.common.ARM", new Object[0]));
        }
        if ((supportedArch & 0x4L) == 0x4L) {
            architecture.add(I18N.getMsg("mdm.common.X86", new Object[0]));
        }
        if ((supportedArch & 0x8L) == 0x8L) {
            architecture.add(I18N.getMsg("mdm.common.X64", new Object[0]));
        }
        if (supportedArch == 1L) {
            architecture.add(I18N.getMsg("mdm.common.neutral", new Object[0]));
        }
        String[] arch = new String[architecture.size()];
        arch = architecture.toArray(arch);
        return arch;
    }
    
    public HashMap getProfileIDFromPackageIdsForTrash(final List packageIDS, final Long customerId) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppGroupDetails"));
        selectQuery.addJoin(new Join("MdAppGroupDetails", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("MdPackageToAppGroup", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("AppGroupToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        final Criteria packageCriteria = new Criteria(Column.getColumn("MdPackageToAppGroup", "PACKAGE_ID"), (Object)packageIDS.toArray(), 8);
        final Criteria profileCriteria = new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)Boolean.TRUE, 0);
        final Criteria customerCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        selectQuery.setCriteria(packageCriteria.and(profileCriteria).and(customerCriteria));
        selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "*"));
        selectQuery.addSelectColumn(Column.getColumn("AppGroupToCollection", "*"));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        Iterator iterator = dataObject.getRows("ProfileToCollection");
        final List profileIds = new ArrayList();
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final Long profileid = (Long)row.get("PROFILE_ID");
            if (!profileIds.contains(profileid)) {
                profileIds.add(profileid);
            }
        }
        final List appGroupIds = new ArrayList();
        iterator = dataObject.getRows("AppGroupToCollection");
        while (iterator.hasNext()) {
            final Row row2 = iterator.next();
            final Long appGroupId = (Long)row2.get("APP_GROUP_ID");
            if (!appGroupIds.contains(appGroupId)) {
                appGroupIds.add(appGroupId);
            }
        }
        final HashMap hashMap = new HashMap();
        hashMap.put("profileIds", profileIds);
        hashMap.put("appGroupIds", appGroupIds);
        return hashMap;
    }
    
    public JSONObject getDistributedAppCount(final Long customerId, final List appGroupList) {
        final JSONObject result = new JSONObject();
        try {
            final SelectQuery mdAppCatalogToResQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppCatalogToResource"));
            final Join managedDeviceJoin = new Join("MdAppCatalogToResource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            final Join resourceJoin = new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            mdAppCatalogToResQuery.addJoin(managedDeviceJoin);
            mdAppCatalogToResQuery.addJoin(resourceJoin);
            final Criteria cManagedStatus = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            final Criteria cResource = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
            mdAppCatalogToResQuery.setCriteria(cManagedStatus.and(cResource));
            if (appGroupList != null && !appGroupList.isEmpty()) {
                final Criteria appGroupCri = new Criteria(new Column("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)appGroupList.toArray(), 8);
                mdAppCatalogToResQuery.setCriteria(mdAppCatalogToResQuery.getCriteria().and(appGroupCri));
            }
            final Column appCatalogResCount = Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID").count();
            appCatalogResCount.setColumnAlias("APP_COUNT");
            mdAppCatalogToResQuery.addSelectColumn(appCatalogResCount);
            mdAppCatalogToResQuery.addSelectColumn(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"));
            final List list = new ArrayList();
            final Column groupByCol = Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID");
            list.add(groupByCol);
            final GroupByClause appGroupBy = new GroupByClause(list);
            mdAppCatalogToResQuery.setGroupByClause(appGroupBy);
            final org.json.simple.JSONArray dsJSArray = MDMUtil.executeSelectQuery(mdAppCatalogToResQuery);
            for (int i = 0; i < dsJSArray.size(); ++i) {
                final org.json.simple.JSONObject jsObject = (org.json.simple.JSONObject)dsJSArray.get(i);
                final Long appGroupId = (Long)jsObject.get((Object)"APP_GROUP_ID");
                final Long count = Long.valueOf(jsObject.get((Object)"APP_COUNT").toString());
                result.put(appGroupId.toString(), (Object)count);
            }
        }
        catch (final Exception ex) {
            AppsUtil.logger.log(Level.SEVERE, "Exception in getDistributedAppCount", ex);
        }
        return result;
    }
    
    public Long getAppGroupId(final Long packageId) throws Exception {
        final Long appGroupId = (Long)DBUtil.getValueFromDB("MdPackageToAppGroup", "PACKAGE_ID", (Object)packageId, "APP_GROUP_ID");
        return appGroupId;
    }
    
    public Integer getStoreId(final Long packageId) throws Exception {
        final Integer storeId = Integer.valueOf((String)DBUtil.getValueFromDB("MdPackageToAppData", "PACKAGE_ID", (Object)packageId, "STORE_ID"));
        return storeId;
    }
    
    public List getAppGroupDetails(final List appIds, final Long customerId) throws Exception {
        final List<Long> appList = new ArrayList<Long>();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdPackageToAppGroup"));
        selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdPackage", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        final Criteria appCriteria = new Criteria(Column.getColumn("MdPackageToAppGroup", "PACKAGE_ID"), (Object)appIds.toArray(), 8);
        final Criteria custCriteria = new Criteria(Column.getColumn("MdPackage", "CUSTOMER_ID"), (Object)customerId, 0);
        selectQuery.setCriteria(appCriteria.and(custCriteria));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "PACKAGE_ID"));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        final Iterator iterator = dataObject.getRows("MdPackageToAppGroup");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final Long appID = (Long)row.get("APP_GROUP_ID");
            appList.add(appID);
        }
        return appList;
    }
    
    public String getAppIdentifierFromCollection(final Long collectionID) throws DataAccessException {
        String identifier = "";
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AppGroupToCollection"));
        selectQuery.addJoin(new Join("AppGroupToCollection", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("AppGroupToCollection", "COLLECTION_ID"), (Object)collectionID, 0));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("MdAppGroupDetails");
            identifier = (String)row.get("IDENTIFIER");
        }
        return identifier;
    }
    
    public DataObject getResourceDetailsForApp(final List appGroupIDs, final List resourceList, final boolean isGroup) throws DataAccessException {
        final SelectQuery resourceForAppGroupQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AppGroupToCollection"));
        resourceForAppGroupQuery.addJoin(new Join("AppGroupToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        resourceForAppGroupQuery.addJoin(new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        final Criteria appGrpCriteria = new Criteria(new Column("AppGroupToCollection", "APP_GROUP_ID"), (Object)appGroupIDs.toArray(), 8);
        final Criteria trashCriteria = new Criteria(new Column("Profile", "IS_MOVED_TO_TRASH"), (Object)false, 0);
        final Criteria productionCriteria = AppVersionDBUtil.getInstance().getApprovedAppVersionCriteria();
        resourceForAppGroupQuery.setCriteria(appGrpCriteria.and(trashCriteria).and(productionCriteria));
        resourceForAppGroupQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "PROFILE_ID"));
        resourceForAppGroupQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "COLLECTION_ID"));
        if (isGroup) {
            final Criteria groupCriteria = new Criteria(Column.getColumn("RecentProfileForGroup", "GROUP_ID"), (Object)resourceList.toArray(), 8);
            final Criteria joinCriteria = new Criteria(Column.getColumn("ProfileToCollection", "PROFILE_ID"), (Object)Column.getColumn("RecentProfileForGroup", "PROFILE_ID"), 0);
            resourceForAppGroupQuery.addJoin(new Join("ProfileToCollection", "RecentProfileForGroup", joinCriteria.and(groupCriteria), 1));
            resourceForAppGroupQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "PROFILE_ID"));
            resourceForAppGroupQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "GROUP_ID"));
        }
        else {
            final Criteria resourceCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            final Criteria joinCriteria = new Criteria(Column.getColumn("ProfileToCollection", "PROFILE_ID"), (Object)Column.getColumn("RecentProfileForResource", "PROFILE_ID"), 0);
            resourceForAppGroupQuery.addJoin(new Join("ProfileToCollection", "RecentProfileForResource", resourceCriteria.and(joinCriteria), 1));
            resourceForAppGroupQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "PROFILE_ID"));
            resourceForAppGroupQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"));
        }
        final DataObject dObj = MDMUtil.getPersistence().get(resourceForAppGroupQuery);
        return dObj;
    }
    
    public SelectQuery getResourceDetailForAppQuery(final boolean isGroup) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppGroupDetails"));
        if (isGroup) {
            selectQuery.addJoin(new Join("MdAppGroupDetails", "MdAppCatalogToGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            selectQuery.addJoin(new Join("MdAppCatalogToGroup", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID" }, 2));
            selectQuery.addJoin(new Join("CustomGroupMemberRel", "MdAppCatalogToResource", new String[] { "MEMBER_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        }
        else {
            selectQuery.addJoin(new Join("MdAppGroupDetails", "MdAppCatalogToResource", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        }
        return selectQuery;
    }
    
    public DataObject getResourcesForAppGroup(final boolean isGroup, final List<Long> appGroup, final List<Long> resourceList, final Criteria criteria) throws DataAccessException {
        final SelectQuery selectQuery = this.getResourceDetailForAppQuery(isGroup);
        Criteria resourceCriteria = null;
        if (isGroup) {
            resourceCriteria = new Criteria(new Column("MdAppCatalogToGroup", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            selectQuery.addSelectColumn(new Column("MdAppCatalogToGroup", "RESOURCE_ID"));
            selectQuery.addSelectColumn(new Column("MdAppCatalogToGroup", "APP_GROUP_ID"));
            selectQuery.addSelectColumn(new Column("CustomGroupMemberRel", "*"));
        }
        else {
            resourceCriteria = new Criteria(new Column("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
        }
        Criteria appGroupCriteria = new Criteria(new Column("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroup.toArray(), 8);
        if (!resourceList.isEmpty()) {
            appGroupCriteria = appGroupCriteria.and(resourceCriteria);
        }
        if (criteria != null) {
            appGroupCriteria = appGroupCriteria.and(criteria);
        }
        selectQuery.setCriteria(appGroupCriteria);
        selectQuery.addSelectColumn(new Column("MdAppGroupDetails", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(new Column("MdAppGroupDetails", "PLATFORM_TYPE"));
        selectQuery.addSelectColumn(new Column("MdAppCatalogToResource", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(new Column("MdAppCatalogToResource", "RESOURCE_ID"));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        return dataObject;
    }
    
    public DataObject getAppGroupIdsForResource(final List<Long> appGroup, final List<Long> resourceList, final boolean isGroup, final Criteria criteria) throws DataAccessException {
        final SelectQuery selectQuery = this.getResourceDetailForAppQuery(isGroup);
        if (resourceList.isEmpty()) {
            return null;
        }
        Criteria resourceCriteria = null;
        if (isGroup) {
            resourceCriteria = new Criteria(new Column("MdAppCatalogToGroup", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            selectQuery.addSelectColumn(new Column("MdAppCatalogToGroup", "RESOURCE_ID"));
            selectQuery.addSelectColumn(new Column("MdAppCatalogToGroup", "APP_GROUP_ID"));
            selectQuery.addSelectColumn(new Column("CustomGroupMemberRel", "*"));
        }
        else {
            resourceCriteria = new Criteria(new Column("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
        }
        if (!appGroup.isEmpty()) {
            final Criteria appGroupCriteria = new Criteria(new Column("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroup.toArray(), 8);
            resourceCriteria = resourceCriteria.and(appGroupCriteria);
        }
        if (criteria != null) {
            resourceCriteria = resourceCriteria.and(criteria);
        }
        selectQuery.setCriteria(resourceCriteria);
        selectQuery.addSelectColumn(new Column("MdAppGroupDetails", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(new Column("MdAppGroupDetails", "PLATFORM_TYPE"));
        selectQuery.addSelectColumn(new Column("MdAppCatalogToResource", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(new Column("MdAppCatalogToResource", "RESOURCE_ID"));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        return dataObject;
    }
    
    public void addiOSSystemAppToAppGroup(final Long customerId) {
        try {
            final DataObject dataObject = this.getIOSSystemApps(null);
            if (!dataObject.isEmpty()) {
                final HashMap hashMap = new HashMap();
                final Iterator iterator = dataObject.getRows("IOSSystemApps");
                final List<JSONObject> appList = new ArrayList<JSONObject>();
                while (iterator.hasNext()) {
                    final JSONObject appsJSON = new JSONObject();
                    final Row systemAppRow = iterator.next();
                    final String appIdentifier = (String)systemAppRow.get("IDENTIFIER");
                    final String appName = (String)systemAppRow.get("APP_NAME");
                    appsJSON.put("appname", (Object)appName);
                    appsJSON.put("version", (Object)"--");
                    appsJSON.put("identifier", (Object)appIdentifier);
                    appList.add(appsJSON);
                }
                hashMap.put(1, appList);
                new BlacklistAppHandler().addAndGetAppsInRepository(hashMap, customerId);
            }
        }
        catch (final Exception e) {
            AppsUtil.logger.log(Level.SEVERE, "Exception in adding iOS system apps to app group", e);
        }
    }
    
    public DataObject getIOSSystemApps(final Criteria criteria) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("IOSSystemApps"));
        selectQuery.addSelectColumn(new Column("IOSSystemApps", "*"));
        if (criteria != null) {
            selectQuery.setCriteria(criteria);
        }
        final DataObject dataObject = MDMUtil.getCachedPersistence().get(selectQuery);
        return dataObject;
    }
    
    public DataObject getWindowSystemApps(final Criteria criteria) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("WindowsSystemApps"));
        selectQuery.addSelectColumn(new Column("WindowsSystemApps", "*"));
        if (criteria != null) {
            selectQuery.setCriteria(criteria);
        }
        final DataObject dataObject = MDMUtil.getCachedPersistence().get(selectQuery);
        return dataObject;
    }
    
    public JSONObject getAppPackageLevelDetails(final Long packageId, Long appGroupId) throws Exception {
        if (packageId == null && appGroupId == null) {
            throw new Exception("Either of packageId or appGroupId param must be not null");
        }
        final SelectQuery packageGroupQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackage"));
        packageGroupQuery.addJoin(new Join("MdPackage", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        packageGroupQuery.addJoin(new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        Criteria finalCriteria = null;
        if (packageId != null) {
            finalCriteria = new Criteria(Column.getColumn("MdPackage", "PACKAGE_ID"), (Object)packageId, 0);
        }
        if (appGroupId != null) {
            final Criteria appGroupCriteria = new Criteria(Column.getColumn("MdPackageToAppGroup", "APP_GROUP_ID"), (Object)appGroupId, 0);
            if (finalCriteria == null) {
                finalCriteria = appGroupCriteria;
            }
            else {
                finalCriteria = finalCriteria.and(appGroupCriteria);
            }
        }
        packageGroupQuery.setCriteria(finalCriteria);
        packageGroupQuery.addSelectColumn(Column.getColumn("MdPackage", "PACKAGE_ID"));
        packageGroupQuery.addSelectColumn(Column.getColumn("MdPackage", "PLATFORM_TYPE"));
        packageGroupQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "PACKAGE_ID"));
        packageGroupQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "PACKAGE_TYPE"));
        packageGroupQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
        packageGroupQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"));
        final DataObject dao = MDMUtil.getPersistence().get(packageGroupQuery);
        final JSONObject retJson = new JSONObject();
        if (!dao.isEmpty()) {
            final Row mdPackageRow = dao.getFirstRow("MdPackage");
            final Row mdPackageToAppGroupRow = dao.getFirstRow("MdPackageToAppGroup");
            final Row mdAppGroupRow = dao.getFirstRow("MdAppGroupDetails");
            if (mdPackageRow != null) {
                final Integer platformType = (Integer)mdPackageRow.get("PLATFORM_TYPE");
                retJson.put("PLATFORM_TYPE", (Object)platformType);
            }
            if (mdPackageToAppGroupRow != null) {
                final Integer appType = (Integer)mdPackageToAppGroupRow.get("PACKAGE_TYPE");
                retJson.put("PACKAGE_TYPE", (Object)appType);
            }
            if (mdAppGroupRow != null) {
                appGroupId = (Long)mdAppGroupRow.get("APP_GROUP_ID");
                final String identifier = (String)mdAppGroupRow.get("IDENTIFIER");
                retJson.put("IDENTIFIER", (Object)identifier);
                retJson.put("APP_GROUP_ID", (Object)appGroupId);
            }
        }
        return retJson;
    }
    
    public Boolean getIsBundleIdCaseSenstive(final int platformType) {
        if (platformType == 2) {
            return true;
        }
        return false;
    }
    
    public String getAppIdentifierFromPackageId(final Long packageId) throws DataAccessException {
        return this.getAppIdentifiersFromPackageIds(Arrays.asList(packageId)).getOrDefault(packageId, "");
    }
    
    public Long[] getProfileIDSFromAppGroup(final Long[] appGroupIds) throws Exception {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackage"));
        query.addJoin(new Join("MdPackage", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        query.addJoin(new Join("MdPackageToAppGroup", "MdAppToGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        query.addJoin(new Join("MdAppToGroupRel", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        query.addJoin(new Join("MdAppDetails", "MdAppToCollection", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        query.addJoin(new Join("MdAppToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        final Column profileCountColumn = Column.getColumn("ProfileToCollection", "PROFILE_ID").distinct();
        profileCountColumn.setColumnAlias("profileCount");
        query.addSelectColumn(profileCountColumn);
        query.setCriteria(new Criteria(Column.getColumn("MdPackageToAppGroup", "APP_GROUP_ID"), (Object)appGroupIds, 8));
        final List<Long> distinctValue = new ArrayList<Long>();
        DMDataSetWrapper ds = null;
        try {
            ds = DMDataSetWrapper.executeQuery((Object)query);
            while (ds.next()) {
                final Object value = ds.getValue("profileCount");
                if (value != null && !distinctValue.contains(value)) {
                    distinctValue.add((Long)value);
                }
            }
        }
        catch (final QueryConstructionException | SQLException ex) {
            throw ex;
        }
        return distinctValue.toArray(new Long[distinctValue.size()]);
    }
    
    public JSONObject updateAppsSyncScheduler(final JSONObject message) throws Exception {
        JSONObject schedulerJson = message;
        final Long customerId = message.getLong("customerId");
        final String userName = String.valueOf(message.get("userName"));
        schedulerJson.put("schedule_type", (Object)"Daily");
        schedulerJson.put("daily_interval_type", (Object)"everyDay");
        schedulerJson = APIUtil.getNewInstance().convertAPIJSONtoServerJSON(schedulerJson);
        Long existingTaskId = null;
        String schedulerName = "MDMAppSyncTaskCustomTemplate";
        final String workflowName = "MDMAppSyncTaskCustomTemplate";
        Label_0120: {
            if (!CustomerInfoUtil.getInstance().isMSP()) {
                CustomerInfoUtil.getInstance();
                if (!CustomerInfoUtil.isSAS()) {
                    break Label_0120;
                }
            }
            schedulerName = schedulerName + "__" + customerId;
        }
        ApiFactoryProvider.getSchedulerAPI().setSchedulerState((boolean)SchedulerConstants.ENABLE, schedulerName);
        final String className = "com.me.mdm.server.apps.CustomSyncAppsTask";
        final Long taskId = IdpsUtil.getInstance().getSchedulerCustomizedTask(customerId, 7200);
        if (!taskId.equals(-1L)) {
            existingTaskId = taskId;
        }
        ApiFactoryProvider.getSchedulerAPI().createScheduleFromJson(schedulerJson, String.valueOf(7200), "MDMAppSyncTaskCustomScheduler", schedulerName, workflowName, className, "MDM Apps Sync Scheduler", (String)null, userName, customerId, existingTaskId, Boolean.TRUE);
        return null;
    }
    
    public List<Long> getCustomersWithoutCustomAppsScheduler() {
        final Set<Long> customerSet = new HashSet<Long>();
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomerInfo"));
            sQuery.addSelectColumn(Column.getColumn("CustomerInfo", "CUSTOMER_ID"));
            final SelectQuery selectQuery1 = (SelectQuery)new SelectQueryImpl(Table.getTable("TaskToCustomerRel"));
            selectQuery1.addSelectColumn(Column.getColumn("TaskToCustomerRel", "CUSTOMER_ID"));
            selectQuery1.addJoin(new Join("TaskToCustomerRel", "TaskDetails", new String[] { "TASK_ID" }, new String[] { "TASK_ID" }, 2));
            selectQuery1.setCriteria(new Criteria(Column.getColumn("TaskDetails", "TYPE"), (Object)7200, 0));
            final Column derivedColumn = (Column)new DerivedColumn("derived", selectQuery1);
            final Criteria criteria = new Criteria(Column.getColumn("CustomerInfo", "CUSTOMER_ID"), (Object)derivedColumn, 9);
            sQuery.setCriteria(criteria);
            final DataObject dataObject = MDMUtil.getPersistence().get(sQuery);
            if (!dataObject.isEmpty()) {
                final Iterator<Row> rows = dataObject.getRows("CustomerInfo");
                while (rows.hasNext()) {
                    final Row row = rows.next();
                    final Long customerId = (Long)row.get("CUSTOMER_ID");
                    customerSet.add(customerId);
                }
            }
        }
        catch (final DataAccessException e) {
            AppsUtil.logger.log(Level.SEVERE, "Cannot fetch customer ids for iteration", (Throwable)e);
        }
        return new ArrayList<Long>(customerSet);
    }
    
    public static Integer getPlatformTypeForAppGroupID(final Long appGroupID) throws Exception {
        return (Integer)DBUtil.getValueFromDB("MdAppGroupDetails", "APP_GROUP_ID", (Object)appGroupID, "PLATFORM_TYPE");
    }
    
    public Long getProfileIdForPackage(final Long packageId, final Long customerId) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
        selectQuery.addJoin(new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("ProfileToCollection", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToCollection", "MdPackageToAppData", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        final Criteria packageCriteria = new Criteria(new Column("MdPackageToAppData", "PACKAGE_ID"), (Object)packageId, 0);
        final Criteria customerCriteria = new Criteria(new Column("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0);
        selectQuery.setCriteria(packageCriteria.and(customerCriteria));
        final Column column = new Column("Profile", "PROFILE_ID").distinct();
        column.setColumnAlias("DISTINCT_PROFILE");
        selectQuery.addSelectColumn(column);
        try {
            final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
            if (dmDataSetWrapper.next()) {
                return (Long)dmDataSetWrapper.getValue("DISTINCT_PROFILE");
            }
            AppsUtil.logger.log(Level.INFO, "Unknown package id {0} for customer {1}", new Object[] { packageId, customerId });
            throw new APIHTTPException("COM0008", new Object[] { packageId });
        }
        catch (final Exception e) {
            AppsUtil.logger.log(Level.SEVERE, "Issue on getting profile id from packageId", e);
            throw new APIHTTPException("COM0004", new Object[] { e });
        }
    }
    
    public SelectQuery getQueryForManagedDeviceListWithAppDistributedInMDM() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForResource"));
        selectQuery.addJoin(new Join("RecentProfileForResource", "CollnToResources", new String[] { "COLLECTION_ID", "RESOURCE_ID" }, new String[] { "COLLECTION_ID", "RESOURCE_ID" }, 2));
        Criteria resToProfCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)Column.getColumn("ManagedDevice", "RESOURCE_ID"), 0);
        resToProfCriteria = resToProfCriteria.and(new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0));
        selectQuery.addJoin(new Join("RecentProfileForResource", "ManagedDevice", resToProfCriteria, 2));
        selectQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("CollnToResources", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToCollection", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        Criteria appCatalogToResCrit = new Criteria(Column.getColumn("MdAppToGroupRel", "APP_GROUP_ID"), (Object)Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"), 0);
        appCatalogToResCrit = appCatalogToResCrit.and(new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), 0));
        selectQuery.addJoin(new Join("MdAppToGroupRel", "MdAppCatalogToResource", appCatalogToResCrit, 2));
        return selectQuery;
    }
    
    public Integer getPlatformTypeFromPackageID(final Long packageID) throws Exception {
        return (Integer)DBUtil.getValueFromDB("MdPackage", "PACKAGE_ID", (Object)packageID, "PLATFORM_TYPE");
    }
    
    private JSONObject getAppVersionJSON(final Long collectionID) throws DataAccessException, JSONException {
        final JSONObject appVersionJSON = new JSONObject();
        final SelectQuery appVersionQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppToCollection"));
        final Join appDetailsJoin = new Join("MdAppToCollection", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2);
        appVersionQuery.addJoin(appDetailsJoin);
        final Criteria collectionIDCriteria = new Criteria(Column.getColumn("MdAppToCollection", "COLLECTION_ID"), (Object)collectionID, 0);
        appVersionQuery.setCriteria(collectionIDCriteria);
        appVersionQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_ID"));
        appVersionQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_VERSION"));
        appVersionQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_NAME_SHORT_VERSION"));
        final DataObject appVersionDO = DataAccess.get(appVersionQuery);
        final Row row = appVersionDO.getFirstRow("MdAppDetails");
        appVersionJSON.put("APP_VERSION", row.get("APP_VERSION"));
        appVersionJSON.put("APP_NAME_SHORT_VERSION", row.get("APP_NAME_SHORT_VERSION"));
        return appVersionJSON;
    }
    
    public List<Long> getListOfAppCollnsVersionHigherThanGivenColln(final Long profileID, final Long collectionID, final Boolean considerVersionWithSpecialCharacterAsGreater) throws DataAccessException, Exception {
        final List<Long> listOfCollectionIDs = new ArrayList<Long>();
        final SelectQuery allAppsQuery = getAllAppVersionQuery();
        final Criteria profileIDCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileID, 0);
        allAppsQuery.setCriteria(profileIDCriteria);
        allAppsQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_NAME_SHORT_VERSION"));
        allAppsQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_VERSION"));
        allAppsQuery.addSelectColumn(Column.getColumn("MdAppDetails", "PLATFORM_TYPE"));
        allAppsQuery.addSelectColumn(Column.getColumn("MdAppToCollection", "COLLECTION_ID"));
        final JSONObject appVersionJSON = this.getAppVersionJSON(collectionID);
        final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)allAppsQuery);
        while (ds.next()) {
            final JSONObject json = new JSONObject();
            json.put("APP_VERSION", ds.getValue("APP_VERSION"));
            json.put("APP_NAME_SHORT_VERSION", ds.getValue("APP_NAME_SHORT_VERSION"));
            if (considerVersionWithSpecialCharacterAsGreater) {
                final Boolean isGreater = AppVersionChecker.getInstance((int)ds.getValue("PLATFORM_TYPE")).isAppVersionGreater(json, appVersionJSON);
                if (!isGreater) {
                    continue;
                }
                listOfCollectionIDs.add((Long)ds.getValue("COLLECTION_ID"));
            }
            else {
                final Boolean isGreaterOrEqual = AppVersionChecker.getInstance((int)ds.getValue("PLATFORM_TYPE")).isAppVersionGreaterOrEqual(appVersionJSON, json);
                if (isGreaterOrEqual) {
                    continue;
                }
                listOfCollectionIDs.add((Long)ds.getValue("COLLECTION_ID"));
            }
        }
        return listOfCollectionIDs;
    }
    
    public List<Long> getListOfReleaseLabelsWithAppVersionLowerThanGivenApp(final Long packageId, final Long releaseLabelId, final Boolean considerVersionWithSpecialCharacterAsLower) throws Exception {
        final List<Long> labelIds = new ArrayList<Long>();
        final SelectQuery selectQuery = getAppAllLiveVersionQuery();
        selectQuery.setCriteria(new Criteria(new Column("MdPackage", "PACKAGE_ID"), (Object)packageId, 0));
        selectQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_NAME_SHORT_VERSION"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_VERSION"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppDetails", "PLATFORM_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("AppGroupToCollection", "RELEASE_LABEL_ID"));
        final HashMap appDetailsMap = MDMAppMgmtHandler.getInstance().getAppDetailsMap(packageId, releaseLabelId);
        final JSONObject baseChannelAppVersion = new JSONObject();
        baseChannelAppVersion.put("APP_VERSION", appDetailsMap.get("APP_VERSION"));
        baseChannelAppVersion.put("APP_NAME_SHORT_VERSION", appDetailsMap.get("APP_NAME_SHORT_VERSION"));
        final int platformType = appDetailsMap.get("PLATFORM_TYPE");
        final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
        while (ds.next()) {
            final JSONObject json = new JSONObject();
            json.put("APP_VERSION", ds.getValue("APP_VERSION"));
            json.put("APP_NAME_SHORT_VERSION", ds.getValue("APP_NAME_SHORT_VERSION"));
            if (considerVersionWithSpecialCharacterAsLower) {
                final Boolean isGreater = AppVersionChecker.getInstance(platformType).isAppVersionGreater(baseChannelAppVersion, json);
                if (!isGreater) {
                    continue;
                }
                labelIds.add((Long)ds.getValue("RELEASE_LABEL_ID"));
            }
            else {
                final Boolean isGreaterOrEqual = AppVersionChecker.getInstance(platformType).isAppVersionGreaterOrEqual(json, baseChannelAppVersion);
                if (isGreaterOrEqual) {
                    continue;
                }
                labelIds.add((Long)ds.getValue("RELEASE_LABEL_ID"));
            }
        }
        return labelIds;
    }
    
    public List<Long> getListOfAppIDsWithVersionHigher(final Long packageID, final Long releaseLabelID, final Boolean considerVersionWithSpecialCharacterAsHigher) throws Exception {
        final List<Long> listOfAppIDs = new ArrayList<Long>();
        final HashMap appDetailsMap = MDMAppMgmtHandler.getInstance().getAppDetailsMap(packageID, releaseLabelID);
        final int platformType = appDetailsMap.get("PLATFORM_TYPE");
        final JSONObject baseChannelAppVersion = new JSONObject();
        baseChannelAppVersion.put("APP_VERSION", appDetailsMap.get("APP_VERSION"));
        baseChannelAppVersion.put("APP_NAME_SHORT_VERSION", appDetailsMap.get("APP_NAME_SHORT_VERSION"));
        final SelectQuery allAppsQuery = getAllAppVersionQuery();
        allAppsQuery.setCriteria(new Criteria(Column.getColumn("MdPackage", "PACKAGE_ID"), (Object)packageID, 0));
        allAppsQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_NAME_SHORT_VERSION"));
        allAppsQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_VERSION"));
        allAppsQuery.addSelectColumn(Column.getColumn("MdAppDetails", "PLATFORM_TYPE"));
        allAppsQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_ID"));
        final DataObject dao = DataAccess.get(allAppsQuery);
        if (!dao.isEmpty()) {
            final Iterator<Row> rowIterator = dao.getRows("MdAppDetails");
            while (rowIterator.hasNext()) {
                final Row row = rowIterator.next();
                final JSONObject json = new JSONObject();
                json.put("APP_VERSION", row.get("APP_VERSION"));
                json.put("APP_NAME_SHORT_VERSION", row.get("APP_NAME_SHORT_VERSION"));
                if (considerVersionWithSpecialCharacterAsHigher) {
                    final Boolean isGreater = AppVersionChecker.getInstance(platformType).isAppVersionGreater(json, baseChannelAppVersion);
                    if (!isGreater) {
                        continue;
                    }
                    listOfAppIDs.add((Long)row.get("APP_ID"));
                }
                else {
                    final Boolean isGreaterOrEqual = AppVersionChecker.getInstance(platformType).isAppVersionGreaterOrEqual(baseChannelAppVersion, json);
                    if (isGreaterOrEqual) {
                        continue;
                    }
                    listOfAppIDs.add((Long)row.get("APP_ID"));
                }
            }
        }
        return listOfAppIDs;
    }
    
    public List<Long> getListOfAppIdsWithVersionLower(final Long packageId, final Long releaseLabelId, final Boolean considerVersionWithSpecialCharacterAsLower) throws Exception {
        final List<Long> listOfAppIDs = new ArrayList<Long>();
        final HashMap appDetailsMap = MDMAppMgmtHandler.getInstance().getAppDetailsMap(packageId, releaseLabelId);
        final int platformType = appDetailsMap.get("PLATFORM_TYPE");
        final JSONObject baseChannelAppVersion = new JSONObject();
        baseChannelAppVersion.put("APP_VERSION", appDetailsMap.get("APP_VERSION"));
        baseChannelAppVersion.put("APP_NAME_SHORT_VERSION", appDetailsMap.get("APP_NAME_SHORT_VERSION"));
        final SelectQuery allAppsQuery = getAllAppVersionQuery();
        allAppsQuery.setCriteria(new Criteria(Column.getColumn("MdPackage", "PACKAGE_ID"), (Object)packageId, 0));
        allAppsQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_NAME_SHORT_VERSION"));
        allAppsQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_VERSION"));
        allAppsQuery.addSelectColumn(Column.getColumn("MdAppDetails", "PLATFORM_TYPE"));
        allAppsQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_ID"));
        final DataObject dao = DataAccess.get(allAppsQuery);
        if (!dao.isEmpty()) {
            final Iterator<Row> rowIterator = dao.getRows("MdAppDetails");
            while (rowIterator.hasNext()) {
                final Row row = rowIterator.next();
                final JSONObject json = new JSONObject();
                json.put("APP_VERSION", row.get("APP_VERSION"));
                json.put("APP_NAME_SHORT_VERSION", row.get("APP_NAME_SHORT_VERSION"));
                if (considerVersionWithSpecialCharacterAsLower) {
                    final Boolean isGreater = AppVersionChecker.getInstance(platformType).isAppVersionGreater(baseChannelAppVersion, json);
                    if (!isGreater) {
                        continue;
                    }
                    listOfAppIDs.add((Long)row.get("APP_ID"));
                }
                else {
                    final Boolean isGreaterOrEqual = AppVersionChecker.getInstance(platformType).isAppVersionGreaterOrEqual(json, baseChannelAppVersion);
                    if (isGreaterOrEqual) {
                        continue;
                    }
                    listOfAppIDs.add((Long)row.get("APP_ID"));
                }
            }
        }
        return listOfAppIDs;
    }
    
    public boolean isBusinessStoreApp(final Long collectionID) throws Exception {
        Boolean isStoreApp = Boolean.FALSE;
        final SelectQuery appTypeQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AppGroupToCollection"));
        appTypeQuery.addJoin(new Join("AppGroupToCollection", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        appTypeQuery.addJoin(new Join("MdPackageToAppGroup", "MdPackage", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        appTypeQuery.setCriteria(new Criteria(Column.getColumn("AppGroupToCollection", "COLLECTION_ID"), (Object)collectionID, 0));
        appTypeQuery.setCriteria(appTypeQuery.getCriteria().and(new Criteria(Column.getColumn("MdPackage", "PLATFORM_TYPE"), (Object)3, 0)));
        appTypeQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "PACKAGE_ID"));
        appTypeQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"));
        final DataObject appTypeDo = DataAccess.get(appTypeQuery);
        if (!appTypeDo.isEmpty()) {
            final Row row = appTypeDo.getFirstRow("MdPackageToAppGroup");
            isStoreApp = (Boolean)row.get("IS_PURCHASED_FROM_PORTAL");
        }
        return isStoreApp;
    }
    
    public boolean isNonAccountStoreApp(final Long collectionID) throws Exception {
        final SelectQuery appTypeQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AppGroupToCollection"));
        appTypeQuery.addJoin(new Join("AppGroupToCollection", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        final Criteria collectionIDCriteria = new Criteria(Column.getColumn("AppGroupToCollection", "COLLECTION_ID"), (Object)collectionID, 0);
        final Criteria isPurchasedFromPortalCriteria = new Criteria(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), (Object)Boolean.FALSE, 0);
        final Criteria notEnterpriseTypeCriteria = new Criteria(Column.getColumn("MdPackageToAppGroup", "PACKAGE_TYPE"), (Object)2, 1);
        appTypeQuery.setCriteria(collectionIDCriteria.and(isPurchasedFromPortalCriteria).and(notEnterpriseTypeCriteria));
        appTypeQuery.addSelectColumn(Column.getColumn("AppGroupToCollection", "APP_GROUP_ID"));
        appTypeQuery.addSelectColumn(Column.getColumn("AppGroupToCollection", "COLLECTION_ID"));
        final DataObject appTypeDo = DataAccess.get(appTypeQuery);
        if (appTypeDo.isEmpty()) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
    
    public boolean isEnterpriseApp(final Long collectionId) throws Exception {
        Boolean isEnterpriseApp = Boolean.FALSE;
        final SelectQuery appTypeQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppToCollection"));
        appTypeQuery.addJoin(new Join("MdAppToCollection", "MdPackageToAppData", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        appTypeQuery.addJoin(new Join("MdPackageToAppData", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        appTypeQuery.setCriteria(new Criteria(Column.getColumn("MdAppToCollection", "COLLECTION_ID"), (Object)collectionId, 0));
        appTypeQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "PACKAGE_ID"));
        appTypeQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "PACKAGE_TYPE"));
        final DataObject appTypeDO = DataAccess.get(appTypeQuery);
        if (!appTypeDO.isEmpty()) {
            final Row mdPackageToAppGroupRow = appTypeDO.getFirstRow("MdPackageToAppGroup");
            isEnterpriseApp = (((int)mdPackageToAppGroupRow.get("PACKAGE_TYPE") == 2) ? Boolean.TRUE : Boolean.FALSE);
        }
        return isEnterpriseApp;
    }
    
    private static SelectQuery getAllAppVersionQuery() {
        final SelectQuery allAppsQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppDetails"));
        allAppsQuery.addJoin(new Join("MdAppDetails", "MdAppToCollection", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        allAppsQuery.addJoin(new Join("MdAppToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        allAppsQuery.addJoin(new Join("MdAppDetails", "MdPackageToAppData", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        allAppsQuery.addJoin(new Join("MdPackageToAppData", "MdPackage", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        allAppsQuery.addJoin(new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        return allAppsQuery;
    }
    
    public static SelectQuery getAppAllLiveVersionQuery() {
        final SelectQuery appAllLiveVersionQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
        appAllLiveVersionQuery.addJoin(new Join("MdAppGroupDetails", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        appAllLiveVersionQuery.addJoin(new Join("MdPackageToAppGroup", "MdPackage", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        appAllLiveVersionQuery.addJoin(new Join("MdAppGroupDetails", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        appAllLiveVersionQuery.addJoin(new Join("AppGroupToCollection", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        appAllLiveVersionQuery.addJoin(new Join("MdAppToCollection", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        appAllLiveVersionQuery.addJoin(new Join("AppGroupToCollection", "AppReleaseLabel", new String[] { "RELEASE_LABEL_ID" }, new String[] { "RELEASE_LABEL_ID" }, 2));
        appAllLiveVersionQuery.addJoin(new Join("MdAppToCollection", "MdPackageToAppData", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        appAllLiveVersionQuery.addJoin(new Join("AppGroupToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        appAllLiveVersionQuery.addJoin(new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        return appAllLiveVersionQuery;
    }
    
    public String getAppVersionFromAppID(final Long appID) throws Exception {
        final String appVersion = String.valueOf(DBUtil.getValueFromDB("MdAppDetails", "APP_ID", (Object)appID, "APP_VERSION"));
        return appVersion;
    }
    
    public String getAppVersionFromCollectionID(final Long collectionID) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppDetails"));
        selectQuery.addJoin(new Join("MdAppDetails", "MdAppToCollection", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery.setCriteria(new Criteria(Column.getColumn("MdAppToCollection", "COLLECTION_ID"), (Object)collectionID, 0));
        selectQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_VERSION"));
        final DataObject dao = DataAccess.get(selectQuery);
        final Row appVersionRow = dao.getFirstRow("MdAppDetails");
        final String appVersion = String.valueOf(appVersionRow.get("APP_VERSION"));
        return appVersion;
    }
    
    public void checkIfMDMApp(final List appGroupIds) throws Exception {
        DataObject dObj = null;
        final Criteria appIdCri = new Criteria(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroupIds.toArray(), 8);
        final Criteria iOSCri = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)"com.manageengine.mdm.iosagent", 0);
        final Criteria wpCri = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)"d73a6956-c81b-4bcb-ba8d-fe8718735ad7", 12);
        final Criteria windowsCri = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)"ZohoCorp.ManageEngineMDM_hfrrf6a1akhx2", 12);
        final Criteria macCri = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)"com.manageengine.mdm.mac", 0);
        final Criteria androidCri = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)"com.manageengine.mdm.android", 0);
        final Criteria cri = appIdCri.and(iOSCri.or(wpCri).or(windowsCri).or(macCri).or(androidCri));
        dObj = MDMUtil.getPersistence().get("MdAppGroupDetails", cri);
        if (!dObj.isEmpty()) {
            throw new APIHTTPException("COM0015", new Object[] { I18N.getMsg("mdm.inv.memdm_not_blacklisted", new Object[0]) });
        }
    }
    
    public void validateAppGroupsIfFound(Collection<Long> appset, final Long customerID) throws APIHTTPException {
        try {
            appset = new HashSet<Long>(appset);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
            final Criteria appGroupcriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appset.toArray(), 8);
            final Criteria customerCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerID, 0);
            selectQuery.setCriteria(appGroupcriteria.and(customerCriteria));
            selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
            final DataObject dataObject = DataAccess.get(selectQuery);
            final Iterator<Row> rows = dataObject.getRows("MdAppGroupDetails");
            final ArrayList<Long> apps = new ArrayList<Long>();
            while (rows.hasNext()) {
                apps.add(Long.valueOf(String.valueOf(rows.next().get("APP_GROUP_ID"))));
            }
            appset.removeAll(apps);
            if (appset.size() > 0) {
                throw new APIHTTPException("COM0008", new Object[] { APIUtil.getCommaSeperatedString(appset) });
            }
        }
        catch (final DataAccessException e) {
            AppsUtil.logger.log(Level.SEVERE, "Issue on validating app groups for blacklisting", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONArray getAPIAppCategoryNames(final int platformType) {
        JSONArray jsonArray = null;
        try {
            final Criteria platformCri = new Criteria(Column.getColumn("AppCategory", "PLATFORM_TYPE"), (Object)platformType, 0);
            final DataObject dObj = DataAccess.get("AppCategory", platformCri);
            jsonArray = new JSONArray();
            JSONObject jsonObject = null;
            if (!dObj.isEmpty()) {
                final Iterator itr = dObj.getRows("AppCategory");
                final int i = 1;
                while (itr.hasNext()) {
                    final Row row = itr.next();
                    final Object appCategoryId = row.get("APP_CATEGORY_ID");
                    jsonObject = new JSONObject();
                    if (appCategoryId != null) {
                        jsonObject.put("category_id", (Object)appCategoryId);
                        jsonObject.put("APP_CATEGORY_NAME", (Object)I18N.getMsg((String)row.get("APP_CATEGORY_LABEL"), new Object[0]));
                        jsonObject.put("app_category_key", (Object)row.get("APP_CATEGORY_LABEL"));
                        jsonArray.put((Object)jsonObject);
                    }
                }
            }
        }
        catch (final Exception e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return jsonArray;
    }
    
    public Long getAppCategoryId(final Long mappedId, final int platform) {
        this.initializeCategoryMaps();
        switch (platform) {
            case 1: {
                if (this.iosCategoryMap.contains(mappedId)) {
                    return mappedId;
                }
                break;
            }
            case 2: {
                if (this.androidCategoryMap.contains(mappedId)) {
                    return mappedId;
                }
                break;
            }
            case 3: {
                if (this.windowsCategoryMap.contains(mappedId)) {
                    return mappedId;
                }
                break;
            }
            case 4: {
                if (this.chromeCategoryMap.contains(mappedId)) {
                    return mappedId;
                }
                break;
            }
        }
        return -1L;
    }
    
    private void initializeCategoryMaps() {
        HashMap<Integer, ArrayList<Long>> cacheObject = (HashMap<Integer, ArrayList<Long>>)ApiFactoryProvider.getCacheAccessAPI().getCache("MDM_API_UTIL_API_TO_CATEGORY_CACHE", 2);
        if (cacheObject != null) {
            this.androidCategoryMap = cacheObject.get(2);
            this.iosCategoryMap = cacheObject.get(1);
            this.windowsCategoryMap = cacheObject.get(3);
            this.chromeCategoryMap = cacheObject.get(4);
        }
        else {
            cacheObject = new HashMap<Integer, ArrayList<Long>>();
            this.androidCategoryMap = new ArrayList<Long>();
            this.androidCategoryMap = this.getAppCategoryList(2);
            cacheObject.put(2, this.androidCategoryMap);
            this.iosCategoryMap = new ArrayList<Long>();
            this.iosCategoryMap = this.getAppCategoryList(1);
            cacheObject.put(1, this.iosCategoryMap);
            this.windowsCategoryMap = new ArrayList<Long>();
            this.windowsCategoryMap = this.getAppCategoryList(3);
            cacheObject.put(3, this.windowsCategoryMap);
            this.chromeCategoryMap = new ArrayList<Long>();
            this.chromeCategoryMap = this.getAppCategoryList(4);
            cacheObject.put(4, this.chromeCategoryMap);
            ApiFactoryProvider.getCacheAccessAPI().putCache("MDM_API_UTIL_API_TO_CATEGORY_CACHE", (Object)cacheObject, 2);
        }
    }
    
    public static int getSupportedDevicesValues(final int mappedValue, final int plaform) {
        Label_0199: {
            switch (plaform) {
                case 2: {
                    switch (mappedValue) {
                        case 1: {
                            return 2;
                        }
                        case 2: {
                            return 3;
                        }
                        case 3: {
                            return 1;
                        }
                        case 4: {
                            return 0;
                        }
                        default: {
                            return -1;
                        }
                    }
                    break;
                }
                case 1: {
                    switch (mappedValue) {
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                        case 9:
                        case 10:
                        case 11:
                        case 12:
                        case 13:
                        case 14:
                        case 15:
                        case 16: {
                            return mappedValue;
                        }
                        default: {
                            return -1;
                        }
                    }
                    break;
                }
                case 3: {
                    switch (mappedValue) {
                        case 1: {
                            return 8;
                        }
                        case 2: {
                            return 16;
                        }
                        case 3: {
                            return 24;
                        }
                        case 4: {
                            return -1;
                        }
                        default: {
                            break Label_0199;
                        }
                    }
                    break;
                }
                case 4: {
                    switch (mappedValue) {
                        case 1: {
                            return mappedValue;
                        }
                        default: {
                            return -1;
                        }
                    }
                    break;
                }
                default: {
                    return -1;
                }
            }
        }
    }
    
    public static int getAPISupportedDevicesValues(final int mappedValue, final int plaform) {
        Label_0208: {
            switch (plaform) {
                case 2: {
                    switch (mappedValue) {
                        case 2: {
                            return 1;
                        }
                        case 3: {
                            return 2;
                        }
                        case 1: {
                            return 3;
                        }
                        case 0: {
                            return 4;
                        }
                        default: {
                            return -1;
                        }
                    }
                    break;
                }
                case 1: {
                    switch (mappedValue) {
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                        case 9:
                        case 10:
                        case 11:
                        case 12:
                        case 13:
                        case 14:
                        case 15:
                        case 16: {
                            return mappedValue;
                        }
                        default: {
                            return -1;
                        }
                    }
                    break;
                }
                case 3: {
                    switch (mappedValue) {
                        case 8: {
                            return 1;
                        }
                        case 16: {
                            return 2;
                        }
                        case 24: {
                            return 3;
                        }
                        case 4: {
                            return -1;
                        }
                        default: {
                            break Label_0208;
                        }
                    }
                    break;
                }
                case 4: {
                    switch (mappedValue) {
                        case 1: {
                            return mappedValue;
                        }
                        default: {
                            return -1;
                        }
                    }
                    break;
                }
                default: {
                    return -1;
                }
            }
        }
    }
    
    public ArrayList<Long> getAppCategoryList(final int platformType) {
        ArrayList<Long> categoryMap = null;
        try {
            final SelectQuery select = (SelectQuery)new SelectQueryImpl(Table.getTable("AppCategory"));
            select.addSelectColumn(new Column("AppCategory", "APP_CATEGORY_ID"));
            final Criteria platformCri = new Criteria(Column.getColumn("AppCategory", "PLATFORM_TYPE"), (Object)platformType, 0);
            select.setCriteria(platformCri);
            select.addSortColumn(new SortColumn("AppCategory", "APP_CATEGORY_ID", true));
            final DataObject dObj = DataAccess.get(select);
            if (!dObj.isEmpty()) {
                categoryMap = new ArrayList<Long>();
                final Iterator itr = dObj.getRows("AppCategory");
                while (itr.hasNext()) {
                    final Row row = itr.next();
                    final Long appCategoryId = (Long)row.get("APP_CATEGORY_ID");
                    if (appCategoryId != null) {
                        categoryMap.add(appCategoryId);
                    }
                }
            }
        }
        catch (final Exception ex) {
            AppsUtil.logger.log(Level.WARNING, "Exception in getAppCategoryNames", ex);
        }
        return categoryMap;
    }
    
    public Long getPlatformDefaultCategory(final int platform, final String platformDefault) {
        Long category = null;
        try {
            final SelectQuery select = (SelectQuery)new SelectQueryImpl(Table.getTable("AppCategory"));
            select.addSelectColumn(new Column("AppCategory", "APP_CATEGORY_ID"));
            final Criteria platformCri = new Criteria(Column.getColumn("AppCategory", "PLATFORM_TYPE"), (Object)platform, 0);
            final Criteria platformDef = new Criteria(Column.getColumn("AppCategory", "APP_CATEGORY_NAME"), (Object)platformDefault, 0);
            select.setCriteria(platformCri.and(platformDef));
            final DataObject dObj = DataAccess.get(select);
            if (!dObj.isEmpty()) {
                final Row row = dObj.getFirstRow("AppCategory");
                category = (Long)row.get("APP_CATEGORY_ID");
            }
        }
        catch (final Exception e) {
            AppsUtil.logger.log(Level.WARNING, "Cannot fetch default category");
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return category;
    }
    
    public JSONObject getPackagePolicy(final Long packageId) {
        final JSONObject jsonObject = new JSONObject();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackagePolicy"));
            selectQuery.setCriteria(new Criteria(new Column("MdPackagePolicy", "PACKAGE_ID"), (Object)packageId, 0));
            selectQuery.addSelectColumn(Column.getColumn("MdPackagePolicy", "PACKAGE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdPackagePolicy", "PREVENT_BACKUP"));
            selectQuery.addSelectColumn(Column.getColumn("MdPackagePolicy", "REMOVE_APP_WITH_PROFILE"));
            final DataObject dObj = DataAccess.get(selectQuery);
            if (!dObj.isEmpty()) {
                final Row row = dObj.getRow("MdPackagePolicy");
                jsonObject.put("prevent_backup", (Object)row.get("PREVENT_BACKUP"));
                jsonObject.put("remove_app_with_profile", (Object)row.get("REMOVE_APP_WITH_PROFILE"));
            }
        }
        catch (final Exception e) {
            AppsUtil.logger.log(Level.WARNING, "Exception while fetching package policy");
        }
        return jsonObject;
    }
    
    @Deprecated
    public void setAppUpdatesForDevices(final Long appId, final Long appGroupId, final int platformType, final String version, final String versionCode) {
        AppsUtil.logger.log(Level.INFO, "Going to set app update of app group {0} for lower version distributed devices & groups", appGroupId);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppDetails"));
        selectQuery.addJoin(new Join("MdAppDetails", "MdPackageToAppData", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        final Criteria appGroupCriteria = new Criteria(new Column("MdPackageToAppData", "APP_GROUP_ID"), (Object)appGroupId, 0);
        final Criteria appCriteria = new Criteria(new Column("MdAppDetails", "APP_ID"), (Object)appId, 1);
        selectQuery.setCriteria(appGroupCriteria.and(appCriteria));
        selectQuery.addSelectColumn(new Column("MdAppDetails", "APP_ID"));
        selectQuery.addSelectColumn(new Column("MdAppDetails", "APP_VERSION"));
        selectQuery.addSelectColumn(new Column("MdAppDetails", "APP_NAME_SHORT_VERSION"));
        try {
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            final List<Long> toBeUpdatedList = new ArrayList<Long>();
            final List<Long> clearUpdateList = new ArrayList<Long>();
            if (!dataObject.isEmpty()) {
                final Iterator<Row> rows = dataObject.getRows("MdAppDetails");
                while (rows.hasNext()) {
                    final Row row = rows.next();
                    final String appVersion = (String)row.get("APP_VERSION");
                    final String appShortVersion = (String)row.get("APP_NAME_SHORT_VERSION");
                    final JSONObject existingVersionDetails = new JSONObject();
                    existingVersionDetails.put("APP_VERSION", (Object)appVersion);
                    existingVersionDetails.put("APP_NAME_SHORT_VERSION", (Object)appShortVersion);
                    final JSONObject versionDetails = new JSONObject();
                    versionDetails.put("APP_VERSION", (Object)version);
                    versionDetails.put("APP_NAME_SHORT_VERSION", (Object)versionCode);
                    final Boolean isExistingVersionGreater = AppVersionChecker.getInstance(platformType).isAppVersionGreater(existingVersionDetails, versionDetails);
                    if (!isExistingVersionGreater) {
                        toBeUpdatedList.add((Long)row.get("APP_ID"));
                    }
                    else {
                        clearUpdateList.add((Long)row.get("APP_ID"));
                    }
                }
            }
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("MdAppCatalogToGroup");
            updateQuery.addJoin(new Join("MdAppCatalogToGroup", "MdPackageToAppData", new String[] { "APP_GROUP_ID", "PUBLISHED_APP_ID" }, new String[] { "APP_GROUP_ID", "APP_ID" }, 2));
            updateQuery.addJoin(new Join("MdPackageToAppData", "MdAppToCollection", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
            updateQuery.addJoin(new Join("MdAppToCollection", "RecentProfileForGroup", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            final Criteria notDeletedcriteria = new Criteria(new Column("RecentProfileForGroup", "MARKED_FOR_DELETE"), (Object)Boolean.FALSE, 0);
            final Criteria appIdCriteria = new Criteria(new Column("MdAppCatalogToGroup", "PUBLISHED_APP_ID"), (Object)toBeUpdatedList.toArray(), 8);
            updateQuery.setCriteria(notDeletedcriteria.and(appIdCriteria));
            updateQuery.setUpdateColumn("IS_UPDATE_AVAILABLE", (Object)Boolean.TRUE);
            final UpdateQuery updateQuery2 = (UpdateQuery)new UpdateQueryImpl("MdAppCatalogToResourceExtn");
            updateQuery2.addJoin(new Join("MdAppCatalogToResourceExtn", "MdAppCatalogToResource", new String[] { "APP_GROUP_ID", "RESOURCE_ID" }, new String[] { "APP_GROUP_ID", "RESOURCE_ID" }, 2));
            updateQuery2.addJoin(new Join("MdAppCatalogToResource", "MdPackageToAppData", new String[] { "APP_GROUP_ID", "PUBLISHED_APP_ID" }, new String[] { "APP_GROUP_ID", "APP_ID" }, 2));
            updateQuery2.addJoin(new Join("MdPackageToAppData", "MdAppToCollection", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
            updateQuery2.addJoin(new Join("MdAppToCollection", "RecentProfileForResource", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            final Criteria notDeletedcriteria2 = new Criteria(new Column("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)Boolean.FALSE, 0);
            final Criteria appIdCriteria2 = new Criteria(new Column("MdAppCatalogToResource", "PUBLISHED_APP_ID"), (Object)toBeUpdatedList.toArray(), 8);
            updateQuery2.setCriteria(notDeletedcriteria2.and(appIdCriteria2));
            updateQuery2.setUpdateColumn("IS_UPDATE_AVAILABLE", (Object)Boolean.TRUE);
            updateQuery2.setUpdateColumn("PUBLISHED_APP_SOURCE", (Object)MDMCommonConstants.UNASSIGNED_APP_UPDATE);
            clearUpdateList.add(appId);
            final UpdateQuery updateQuery3 = (UpdateQuery)new UpdateQueryImpl("MdAppCatalogToGroup");
            updateQuery3.addJoin(new Join("MdAppCatalogToGroup", "MdPackageToAppData", new String[] { "APP_GROUP_ID", "PUBLISHED_APP_ID" }, new String[] { "APP_GROUP_ID", "APP_ID" }, 2));
            updateQuery3.addJoin(new Join("MdPackageToAppData", "MdAppToCollection", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
            updateQuery3.addJoin(new Join("MdAppToCollection", "RecentProfileForGroup", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            final Criteria appIdCriteria3 = new Criteria(new Column("MdAppCatalogToGroup", "PUBLISHED_APP_ID"), (Object)clearUpdateList.toArray(), 8);
            updateQuery3.setCriteria(notDeletedcriteria.and(appIdCriteria3));
            updateQuery3.setUpdateColumn("IS_UPDATE_AVAILABLE", (Object)Boolean.FALSE);
            final UpdateQuery updateQuery4 = (UpdateQuery)new UpdateQueryImpl("MdAppCatalogToResourceExtn");
            updateQuery4.addJoin(new Join("MdAppCatalogToResourceExtn", "MdAppCatalogToResource", new String[] { "APP_GROUP_ID", "RESOURCE_ID" }, new String[] { "APP_GROUP_ID", "RESOURCE_ID" }, 2));
            updateQuery4.addJoin(new Join("MdAppCatalogToResource", "MdPackageToAppData", new String[] { "APP_GROUP_ID", "PUBLISHED_APP_ID" }, new String[] { "APP_GROUP_ID", "APP_ID" }, 2));
            updateQuery4.addJoin(new Join("MdPackageToAppData", "MdAppToCollection", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
            updateQuery4.addJoin(new Join("MdAppToCollection", "RecentProfileForResource", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            final Criteria appIdCriteria4 = new Criteria(new Column("MdAppCatalogToResource", "PUBLISHED_APP_ID"), (Object)clearUpdateList.toArray(), 8);
            updateQuery4.setCriteria(notDeletedcriteria2.and(appIdCriteria4));
            updateQuery4.setUpdateColumn("IS_UPDATE_AVAILABLE", (Object)Boolean.FALSE);
            MDMUtil.getPersistence().update(updateQuery);
            MDMUtil.getPersistence().update(updateQuery2);
            MDMUtil.getPersistence().update(updateQuery3);
            MDMUtil.getPersistence().update(updateQuery4);
        }
        catch (final Exception e) {
            AppsUtil.logger.log(Level.WARNING, e, () -> "Couldn't set app update of app group -- " + n + " for devices and groups");
        }
    }
    
    public String getAbsoluteUrlFromCollectionID(final Long collectionId) {
        String absoluteUrl = null;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppToCollection"));
            sQuery.addJoin(new Join("MdAppToCollection", "MdPackageToAppData", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
            sQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "*"));
            sQuery.setCriteria(new Criteria(new Column("MdAppToCollection", "COLLECTION_ID"), (Object)collectionId, 0));
            final DataObject appDo = MDMUtil.getPersistence().get(sQuery);
            if (appDo != null && !appDo.isEmpty()) {
                final Row groupRow = appDo.getFirstRow("MdPackageToAppData");
                final String appUrl = (String)groupRow.get("APP_FILE_LOC");
                final HashMap hm = new HashMap();
                hm.put("path", appUrl);
                hm.put("IS_SERVER", false);
                hm.put("IS_AUTHTOKEN", false);
                absoluteUrl = MDMApiFactoryProvider.getUploadDownloadAPI().constructFileURLwithDownloadServer(hm);
            }
        }
        catch (final Exception e) {
            AppsUtil.logger.log(Level.SEVERE, "Exception while getting absolute url for collection ", e);
        }
        return absoluteUrl;
    }
    
    public JSONObject isAndroidStoreAppConversionAllowed(final String bundleId, final Long customerId) {
        final JSONObject jsonObject = new JSONObject();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackageToAppGroup"));
        selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppGroupDetails", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("AppGroupToCollection", "AppReleaseLabel", new String[] { "RELEASE_LABEL_ID" }, new String[] { "RELEASE_LABEL_ID" }, 2));
        selectQuery.addJoin(new Join("AppGroupToCollection", "AppCollnToReleaseLabelHistory", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(AppVersionDBUtil.getInstance().getJoinForCollectionsLatestAppReleaseLabelFromHistoryTable());
        final Criteria appGroupCriteria = new Criteria(new Column("MdAppGroupDetails", "IDENTIFIER"), (Object)bundleId, 0, (boolean)this.getIsBundleIdCaseSenstive(2));
        final Criteria platformCriteria = new Criteria(new Column("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)2, 0);
        final Criteria customerCriteria = new Criteria(new Column("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        selectQuery.setCriteria(platformCriteria.and(appGroupCriteria).and(customerCriteria));
        selectQuery.addSelectColumn(new Column("AppGroupToCollection", "*"));
        selectQuery.addSelectColumn(new Column("MdPackageToAppGroup", "PACKAGE_ID"));
        selectQuery.addSelectColumn(new Column("MdPackageToAppGroup", "PACKAGE_TYPE"));
        selectQuery.addSelectColumn(new Column("AppReleaseLabel", "RELEASE_LABEL_ID"));
        selectQuery.addSelectColumn(new Column("AppReleaseLabel", "RELEASE_LABEL_TYPE"));
        selectQuery.addSelectColumn(new Column("AppReleaseLabel", "CUSTOMER_ID"));
        selectQuery.addSelectColumn(new Column("AppCollnToReleaseLabelHistory", "*"));
        try {
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            final Criteria nonApprovedAppCriteria = AppVersionDBUtil.getInstance().getNonApprovedAppVersionCriteria();
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("MdPackageToAppGroup");
                final int packageType = (int)row.get("PACKAGE_TYPE");
                if (packageType == 2) {
                    final Iterator iterator = dataObject.getRows("AppGroupToCollection", nonApprovedAppCriteria);
                    if (iterator.hasNext()) {
                        AppsUtil.logger.log(Level.WARNING, "Sync failed for app id {0} as non prod release label is available. Need to mark it as stable to continue sync for that particular app", new Object[] { bundleId });
                        jsonObject.put("NON_PROD_RELEASE_LABEL_AVAILABLE", true);
                        return jsonObject;
                    }
                    final Boolean enterprisePriority = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AllowEnterprisePriority");
                    if (enterprisePriority != null && enterprisePriority) {
                        AppsUtil.logger.log(Level.WARNING, "Enterprise priority mode is set for Android Playstore sync. Hence AFW sync failed for : {0}", bundleId);
                        jsonObject.put("ENTERPRISE_PRIORITYMODE_SET", true);
                    }
                    else {
                        AppsUtil.logger.log(Level.WARNING, "Going to convert enterprise app to store app : {0}", bundleId);
                        final Row appGroupRow = dataObject.getRow("AppGroupToCollection");
                        final Long appGroupId = (Long)appGroupRow.get("APP_GROUP_ID");
                        final Long collectionId = (Long)appGroupRow.get("COLLECTION_ID");
                        final Row appReleaseLabelRow = dataObject.getRow("AppCollnToReleaseLabelHistory");
                        final Long userId = (Long)appReleaseLabelRow.get("LABEL_ASSIGNED_USER");
                        final Long releaseLabelId = (Long)appGroupRow.get("RELEASE_LABEL_ID");
                        AppsUtil.logger.log(Level.WARNING, "Going to convert enterprise app to store app : {0}", bundleId);
                        this.moveEnterpriseAppCollnToDefaultLabelForStoreAppConversion(appGroupId, collectionId, customerId, releaseLabelId, userId);
                        AppsUtil.logger.log(Level.WARNING, "Converted enterprise app {0} to store app App Group Id  {1}", new Object[] { bundleId, appGroupId });
                    }
                }
            }
        }
        catch (final Exception e) {
            AppsUtil.logger.log(Level.WARNING, "Cannot validate existing appgroup", e);
        }
        return jsonObject;
    }
    
    private void moveEnterpriseAppCollnToDefaultLabelForStoreAppConversion(final Long appGroupId, final Long collectionId, final Long customerId, final Long releaseLabelId, final Long userId) throws DataAccessException {
        AppsUtil.logger.log(Level.INFO, "Moving enterprise app {0} from label id {1} to default label id for store app conversion", new Object[] { appGroupId, releaseLabelId });
        final Long defaultLabelId = AppVersionDBUtil.getInstance().getProductionAppReleaseLabelIDForCustomer(customerId);
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("AppGroupToCollection");
        final Criteria collectionCriteria = new Criteria(Column.getColumn("AppGroupToCollection", "COLLECTION_ID"), (Object)collectionId, 0);
        final Criteria appGroupCriteria = new Criteria(Column.getColumn("AppGroupToCollection", "APP_GROUP_ID"), (Object)appGroupId, 0);
        updateQuery.setCriteria(collectionCriteria.and(appGroupCriteria));
        updateQuery.setUpdateColumn("RELEASE_LABEL_ID", (Object)defaultLabelId);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("InstallAppPolicy"));
        selectQuery.addJoin(new Join("InstallAppPolicy", "ConfigDataItem", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        selectQuery.addJoin(new Join("ConfigDataItem", "CfgDataToCollection", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        selectQuery.addJoin(new Join("CfgDataToCollection", "AppCollnToReleaseLabelHistory", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(AppVersionDBUtil.getInstance().getJoinForCollectionsLatestAppReleaseLabelFromHistoryTable());
        final Criteria appGroupCrit = new Criteria(Column.getColumn("InstallAppPolicy", "APP_GROUP_ID"), (Object)appGroupId, 0);
        final Criteria releaseLabelCriteria = new Criteria(Column.getColumn("AppCollnToReleaseLabelHistory", "RELEASE_LABEL_ID"), (Object)releaseLabelId, 0);
        selectQuery.setCriteria(appGroupCrit.and(releaseLabelCriteria));
        selectQuery.addSelectColumn(Column.getColumn("AppCollnToReleaseLabelHistory", "COLLN_LABEL_HISTORY_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AppCollnToReleaseLabelHistory", "COLLECTION_ID"));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        final Iterator<Row> collnIter = dataObject.getRows("AppCollnToReleaseLabelHistory");
        final List<Long> collnIdForLogging = new ArrayList<Long>();
        final DataObject writeDo = (DataObject)new WritableDataObject();
        while (collnIter.hasNext()) {
            final Row existingRow = collnIter.next();
            collnIdForLogging.add((Long)existingRow.get("COLLECTION_ID"));
            final Row newRow = new Row("AppCollnToReleaseLabelHistory");
            newRow.set("COLLECTION_ID", existingRow.get("COLLECTION_ID"));
            newRow.set("RELEASE_LABEL_ID", (Object)defaultLabelId);
            newRow.set("LABEL_ASSIGNED_TIME", (Object)MDMUtil.getCurrentTimeInMillis());
            newRow.set("LABEL_ASSIGNED_USER", (Object)userId);
            writeDo.addRow(newRow);
        }
        MDMUtil.getPersistence().update(updateQuery);
        DataAccess.update(writeDo);
        AppsUtil.logger.log(Level.INFO, "An entry in AppCollnReleaseLabelHistory made for following collnIds with default release label for store app conversion", new Object[] { collnIdForLogging });
    }
    
    public JSONArray convertFilePath(final JSONArray memberJSONArr) {
        return memberJSONArr;
    }
    
    public void addWindowSystemAppToAppGroup(final Long customerId) {
        try {
            final DataObject dataObject = this.getWindowSystemApps(null);
            if (!dataObject.isEmpty()) {
                final HashMap hashMap = new HashMap();
                final Iterator iterator = dataObject.getRows("WindowsSystemApps");
                final Set<String> appIdentifierSet = new HashSet<String>();
                final List<JSONObject> appList = new ArrayList<JSONObject>();
                while (iterator.hasNext()) {
                    final JSONObject appsJSON = new JSONObject();
                    final Row systemAppRow = iterator.next();
                    final String appIdentifier = (String)systemAppRow.get("PACKAGE_FAMILY_NAME");
                    final String appName = (String)systemAppRow.get("APP_NAME");
                    appsJSON.put("appname", (Object)appName);
                    appsJSON.put("version", (Object)"--");
                    appsJSON.put("identifier", (Object)appIdentifier);
                    if (!appIdentifierSet.contains(appIdentifier)) {
                        appIdentifierSet.add(appIdentifier);
                        appList.add(appsJSON);
                    }
                }
                hashMap.put(3, appList);
                new BlacklistAppHandler().addAndGetAppsInRepository(hashMap, customerId);
            }
        }
        catch (final Exception e) {
            AppsUtil.logger.log(Level.SEVERE, "Exception in adding windows system apps to app group", e);
        }
    }
    
    public Criteria showAppCriteria(final Long customerId) {
        final JSONObject appViewData = AppSettingsDataHandler.getInstance().getAppViewSettings(customerId);
        final boolean isShowUserInstalledApp = appViewData.optBoolean("SHOW_USER_INSTALLED_APPS", true);
        final boolean isShowSystemApp = appViewData.optBoolean("SHOW_SYSTEM_APPS", false);
        final boolean isShowManagedApp = appViewData.optBoolean("SHOW_MANAGED_APPS", true);
        Criteria showAppCriteria = null;
        if (isShowUserInstalledApp && !isShowManagedApp && isShowSystemApp) {
            showAppCriteria = new Criteria(new Column("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)null, 0);
        }
        if (isShowUserInstalledApp && !isShowManagedApp && !isShowSystemApp) {
            showAppCriteria = new Criteria(new Column("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)null, 0);
            showAppCriteria = showAppCriteria.and(new Criteria(new Column("MdInstalledAppResourceRel", "USER_INSTALLED_APPS"), (Object)1, 0));
            showAppCriteria = showAppCriteria.and(new Criteria(new Column("MdAppGroupDetails", "APP_TYPE"), (Object)2, 1));
        }
        if (!isShowUserInstalledApp && !isShowManagedApp && isShowSystemApp) {
            showAppCriteria = new Criteria(new Column("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)null, 0);
        }
        if (!isShowUserInstalledApp && isShowManagedApp && !isShowSystemApp) {
            showAppCriteria = new Criteria(new Column("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)null, 1);
            showAppCriteria = showAppCriteria.and(new Criteria(new Column("MdAppGroupDetails", "APP_TYPE"), (Object)2, 1));
        }
        if (isShowUserInstalledApp && isShowManagedApp && isShowSystemApp) {
            showAppCriteria = null;
        }
        if (!isShowUserInstalledApp && !isShowManagedApp && !isShowSystemApp) {
            showAppCriteria = new Criteria(new Column("MdInstalledAppResourceRel", "APP_ID"), (Object)null, 0);
            showAppCriteria = showAppCriteria.and(new Criteria(new Column("MdAppGroupDetails", "APP_TYPE"), (Object)2, 1));
        }
        if (isShowUserInstalledApp && isShowManagedApp && !isShowSystemApp) {
            showAppCriteria = new Criteria(new Column("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)null, 1);
            showAppCriteria = showAppCriteria.or(new Criteria(new Column("MdInstalledAppResourceRel", "USER_INSTALLED_APPS"), (Object)1, 0));
            showAppCriteria = showAppCriteria.and(new Criteria(new Column("MdAppGroupDetails", "APP_TYPE"), (Object)2, 1));
        }
        if (!isShowUserInstalledApp && isShowManagedApp && isShowSystemApp) {
            showAppCriteria = new Criteria(new Column("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)null, 1);
            showAppCriteria = showAppCriteria.or(new Criteria(new Column("MdInstalledAppResourceRel", "USER_INSTALLED_APPS"), (Object)2, 0));
        }
        return showAppCriteria;
    }
    
    public String getInstalledVersionForApp(final Long deviceId, final Long appGroupId) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppCatalogToResource"));
        selectQuery.addJoin(new Join("MdAppCatalogToResource", "MdAppDetails", new String[] { "INSTALLED_APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery.addSelectColumn(new Column("MdAppCatalogToResource", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(new Column("MdAppCatalogToResource", "RESOURCE_ID"));
        selectQuery.addSelectColumn(new Column("MdAppCatalogToResource", "PUBLISHED_APP_ID"));
        selectQuery.addSelectColumn(new Column("MdAppCatalogToResource", "INSTALLED_APP_ID"));
        selectQuery.addSelectColumn(new Column("MdAppDetails", "APP_ID"));
        selectQuery.addSelectColumn(new Column("MdAppDetails", "APP_VERSION"));
        final Criteria resCriteria = new Criteria(new Column("MdAppCatalogToResource", "RESOURCE_ID"), (Object)deviceId, 0);
        final Criteria appGroupCriteria = new Criteria(new Column("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)appGroupId, 0);
        selectQuery.setCriteria(resCriteria.and(appGroupCriteria));
        try {
            final DataObject appObject = MDMUtil.getPersistence().get(selectQuery);
            if (appObject != null && !appObject.isEmpty()) {
                final Row appRow = appObject.getRow("MdAppDetails");
                return (String)appRow.get("APP_VERSION");
            }
        }
        catch (final DataAccessException e) {
            AppsUtil.logger.log(Level.WARNING, "Cannot get installed version", (Throwable)e);
        }
        return "--";
    }
    
    public String getAppLatestVersion(final Long appGroupId, final Long releaseLabel) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AppGroupToCollection"));
        selectQuery.addJoin(new Join("AppGroupToCollection", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToCollection", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        final Criteria appGroupCriteria = new Criteria(new Column("AppGroupToCollection", "APP_GROUP_ID"), (Object)appGroupId, 0);
        final Criteria releaseLabelCriteria = new Criteria(new Column("AppGroupToCollection", "RELEASE_LABEL_ID"), (Object)releaseLabel, 0);
        selectQuery.setCriteria(appGroupCriteria.and(releaseLabelCriteria));
        selectQuery.addSelectColumn(new Column("MdAppDetails", "APP_ID"));
        selectQuery.addSelectColumn(new Column("MdAppDetails", "APP_VERSION"));
        try {
            final DataObject appObject = MDMUtil.getPersistence().get(selectQuery);
            if (appObject != null && !appObject.isEmpty()) {
                final Row appRow = appObject.getRow("MdAppDetails");
                return (String)appRow.get("APP_VERSION");
            }
        }
        catch (final DataAccessException e) {
            AppsUtil.logger.log(Level.WARNING, "Cannot get app latest version", (Throwable)e);
        }
        return "--";
    }
    
    public HashMap getDevicesAssociatedWithApps(final List<Long> appGroupIds, final Long customerId, final int platformType) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppGroupDetails"));
        final Join appGroupToAppRelJoin = new Join("MdAppGroupDetails", "MdAppToGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
        final Join appToCollectionRelJoin = new Join("MdAppToGroupRel", "MdAppToCollection", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2);
        final Join collectionToRecentProfileJoin = new Join("MdAppToCollection", "RecentProfileForResource", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
        final Join managedDeviceJoin = new Join("RecentProfileForResource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        selectQuery.addJoin(appGroupToAppRelJoin);
        selectQuery.addJoin(appToCollectionRelJoin);
        selectQuery.addJoin(collectionToRecentProfileJoin);
        selectQuery.addJoin(managedDeviceJoin);
        final Criteria appGroupCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroupIds.toArray(), 8);
        final Criteria deleteAppsCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
        final Criteria customerCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria managedDeviceCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
        final Criteria platformCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)platformType, 0);
        selectQuery.setCriteria(appGroupCriteria.and(deleteAppsCriteria).and(customerCriteria).and(managedDeviceCriteria).and(platformCriteria));
        selectQuery.addSelectColumn(new Column("MdAppGroupDetails", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(new Column("MdAppGroupDetails", "PLATFORM_TYPE"));
        selectQuery.addSelectColumn(new Column("MdAppGroupDetails", "CUSTOMER_ID"));
        selectQuery.addSelectColumn(new Column("MdAppToGroupRel", "*"));
        selectQuery.addSelectColumn(new Column("MdAppToCollection", "*"));
        selectQuery.addSelectColumn(new Column("RecentProfileForResource", "*"));
        selectQuery.addSelectColumn(new Column("ManagedDevice", "RESOURCE_ID"));
        selectQuery.addSelectColumn(new Column("ManagedDevice", "MANAGED_STATUS"));
        final DataObject appsToDevicesDO = MDMUtil.getPersistence().get(selectQuery);
        final HashMap appsToDevices = new HashMap();
        if (appsToDevicesDO != null) {
            final Iterator<Row> collectionAssociatedWithDevicesItr = appsToDevicesDO.getRows("RecentProfileForResource");
            Collection collectionIdsAssociatedWithDevices = DBUtil.getColumnValuesAsList((Iterator)collectionAssociatedWithDevicesItr, "COLLECTION_ID");
            collectionIdsAssociatedWithDevices = new HashSet(collectionIdsAssociatedWithDevices);
            for (final Object collectionId : collectionIdsAssociatedWithDevices) {
                final Criteria collectionIdCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "COLLECTION_ID"), collectionId, 0);
                final Iterator<Row> appAssociatedDevicesItr = appsToDevicesDO.getRows("RecentProfileForResource", collectionIdCriteria);
                final List deviceIds = DBUtil.getColumnValuesAsList((Iterator)appAssociatedDevicesItr, "RESOURCE_ID");
                final Criteria collectionAppGroupCriteria = new Criteria(Column.getColumn("MdAppToCollection", "COLLECTION_ID"), collectionId, 0);
                final Row appGroupIdRow = appsToDevicesDO.getRow("MdAppToGroupRel", collectionAppGroupCriteria, appToCollectionRelJoin);
                final Long appGroupId = Long.parseLong(appGroupIdRow.get("APP_GROUP_ID").toString());
                if (appsToDevices.containsKey(appGroupId)) {
                    final List updatedDevicesList = appsToDevices.get(appGroupId);
                    deviceIds.addAll(updatedDevicesList);
                }
                appsToDevices.put(appGroupId, deviceIds);
            }
        }
        return appsToDevices;
    }
    
    public void fillLatestApplicableVersions(final Long packageId, final Long appGroupId, final JSONArray applicableVersions) {
        final Map<String, String> versionCodeToName = new HashMap<String, String>();
        for (int i = 0; i < applicableVersions.length(); ++i) {
            final String versionCode = String.valueOf(((JSONObject)applicableVersions.get(i)).get("APP_NAME_SHORT_VERSION"));
            final String versionName = String.valueOf(((JSONObject)applicableVersions.get(i)).get("APP_VERSION"));
            versionCodeToName.put(versionCode, versionName);
        }
        try {
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("BusinessStoreAppVersion");
            final Criteria packageCriteria = new Criteria(new Column("BusinessStoreAppVersion", "PACKAGE_ID"), (Object)packageId, 0);
            final Criteria removableVersionCodeCriteria = new Criteria(new Column("BusinessStoreAppVersion", "APP_NAME_SHORT_VERSION"), (Object)versionCodeToName.keySet().toArray(), 9);
            final Criteria removableVersionCriteria = new Criteria(new Column("BusinessStoreAppVersion", "APP_VERSION"), (Object)versionCodeToName.values().toArray(), 9);
            deleteQuery.setCriteria(packageCriteria.and(removableVersionCriteria.or(removableVersionCodeCriteria)));
            MDMUtil.getPersistence().delete(deleteQuery);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("BusinessStoreAppVersion"));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            selectQuery.setCriteria(packageCriteria);
            DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (dataObject == null || dataObject.isEmpty()) {
                dataObject = (DataObject)new WritableDataObject();
            }
            Criteria appVersionCriteria = null;
            Criteria appVersionCodeCriteria = null;
            final List<String> versionCodeList = new ArrayList<String>(versionCodeToName.keySet());
            for (int j = 0; j < versionCodeList.size(); ++j) {
                final String versionCode2 = versionCodeList.get(j);
                final String versionName2 = versionCodeToName.get(versionCode2);
                appVersionCriteria = new Criteria(new Column("BusinessStoreAppVersion", "APP_VERSION"), (Object)versionName2, 0);
                appVersionCodeCriteria = new Criteria(new Column("BusinessStoreAppVersion", "APP_NAME_SHORT_VERSION"), (Object)versionCode2, 0);
                final Row row = new Row("BusinessStoreAppVersion");
                row.set("PACKAGE_ID", (Object)packageId);
                row.set("APP_GROUP_ID", (Object)appGroupId);
                row.set("APP_NAME_SHORT_VERSION", (Object)versionCode2);
                row.set("APP_VERSION", (Object)versionName2);
                if (dataObject.getRow("BusinessStoreAppVersion", packageCriteria.and(appVersionCriteria.and(appVersionCodeCriteria))) == null) {
                    dataObject.addRow(row);
                }
            }
            MDMUtil.getPersistence().update(dataObject);
        }
        catch (final DataAccessException e) {
            AppsUtil.logger.log(Level.WARNING, "Cannot update latest version for the packageId {0}, Reason :{1}", new Object[] { packageId, e });
        }
    }
    
    public HashMap<Long, HashMap> getAllAvailableVersions(final Long resourceId) {
        return this.getAvailableVersions(resourceId, null, null);
    }
    
    public HashMap<Long, HashMap> getAvailableVersions(final Long resourceId, final Long installedAppGroupId, final Long releaseLabelId) {
        HashMap<Long, HashMap> appGroupToVersions = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("BusinessStoreAppVersion"));
            selectQuery.addJoin(new Join("BusinessStoreAppVersion", "MdAppCatalogToResource", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            selectQuery.addSelectColumn(new Column("BusinessStoreAppVersion", "*"));
            selectQuery.setCriteria(new Criteria(new Column("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourceId, 0));
            if (installedAppGroupId != null) {
                selectQuery.setCriteria(selectQuery.getCriteria().and(new Criteria(new Column("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)installedAppGroupId, 0)));
            }
            if (releaseLabelId != null) {
                selectQuery.setCriteria(selectQuery.getCriteria().and(new Criteria(new Column("BusinessStoreAppVersion", "RELEASE_LABEL_ID"), (Object)releaseLabelId, 0)));
            }
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator<Row> iterator = dataObject.getRows("BusinessStoreAppVersion");
                appGroupToVersions = new HashMap<Long, HashMap>();
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final Long appGroupId = (Long)row.get("APP_GROUP_ID");
                    if (!appGroupToVersions.containsKey(appGroupId)) {
                        appGroupToVersions.put(appGroupId, new HashMap());
                    }
                    final String versionName = (String)row.get("APP_VERSION");
                    final String versionCode = (String)row.get("APP_NAME_SHORT_VERSION");
                    appGroupToVersions.get(appGroupId).put(versionCode, versionName);
                }
            }
        }
        catch (final DataAccessException e) {
            AppsUtil.logger.log(Level.WARNING, "Cannot get available app versions", (Throwable)e);
        }
        return appGroupToVersions;
    }
    
    public void getAvailableVersions(final JSONObject appJSON, final Long appGroupId, final Long releaseLabelId) {
        JSONArray appGroupToVersions = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("BusinessStoreAppVersion"));
            selectQuery.addSelectColumn(new Column("BusinessStoreAppVersion", "*"));
            selectQuery.setCriteria(new Criteria(new Column("BusinessStoreAppVersion", "APP_GROUP_ID"), (Object)appGroupId, 0));
            if (releaseLabelId != null) {
                selectQuery.setCriteria(selectQuery.getCriteria().and(new Criteria(new Column("BusinessStoreAppVersion", "RELEASE_LABEL_ID"), (Object)releaseLabelId, 0)));
            }
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Map<String, Set> versionToCodeMap = new HashMap<String, Set>();
                appGroupToVersions = new JSONArray();
                final Iterator<Row> iterator = dataObject.getRows("BusinessStoreAppVersion");
                int versionsCount = 0;
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final String versionName = (String)row.get("APP_VERSION");
                    final String versionCode = (String)row.get("APP_NAME_SHORT_VERSION");
                    ++versionsCount;
                    if (!versionToCodeMap.containsKey(versionName)) {
                        versionToCodeMap.put(versionName, new TreeSet());
                    }
                    versionToCodeMap.get(versionName).add(versionCode);
                }
                final Set<String> versionSet = new TreeSet<String>(versionToCodeMap.keySet());
                final Iterator<String> iterator2 = versionSet.iterator();
                while (iterator2.hasNext()) {
                    final JSONObject appVersion = new JSONObject();
                    final String versionName2 = iterator2.next();
                    appVersion.put("APP_VERSION", (Object)versionName2);
                    appVersion.put("APP_NAME_SHORT_VERSION", (Object)JSONUtil.getInstance().convertListToJSONArray(new ArrayList(versionToCodeMap.get(versionName2))));
                    appGroupToVersions.put((Object)appVersion);
                }
                if (versionsCount > 1) {
                    appJSON.put("applicable_versions", (Object)appGroupToVersions);
                    appJSON.put("split_apk_count", versionsCount);
                }
            }
        }
        catch (final DataAccessException e) {
            AppsUtil.logger.log(Level.WARNING, "Cannot get available app versions", (Throwable)e);
        }
    }
    
    public JSONObject getAppVersionJSONFromAppId(final Long appID) throws DataAccessException, JSONException {
        final JSONObject appVersionJSON = new JSONObject();
        final SelectQuery appVersionQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppDetails"));
        final Criteria appIdCriteria = new Criteria(Column.getColumn("MdAppDetails", "APP_ID"), (Object)appID, 0);
        appVersionQuery.setCriteria(appIdCriteria);
        appVersionQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_ID"));
        appVersionQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_VERSION"));
        appVersionQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_NAME_SHORT_VERSION"));
        final DataObject appVersionDO = DataAccess.get(appVersionQuery);
        final Row row = appVersionDO.getFirstRow("MdAppDetails");
        appVersionJSON.put("APP_VERSION", row.get("APP_VERSION"));
        appVersionJSON.put("APP_NAME_SHORT_VERSION", row.get("APP_NAME_SHORT_VERSION"));
        return appVersionJSON;
    }
    
    public Map<Long, Map> getAppDetailsFromCollection(final List<Long> collectionList) {
        final Map<Long, Map> appJSON = new HashMap<Long, Map>();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppToCollection"));
        selectQuery.addJoin(new Join("MdAppToCollection", "MdPackageToAppData", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery.addJoin(new Join("MdPackageToAppData", "MdPackageToAppGroup", new String[] { "PACKAGE_ID", "APP_GROUP_ID" }, new String[] { "PACKAGE_ID", "APP_GROUP_ID" }, 2));
        selectQuery.setCriteria(new Criteria(new Column("MdAppToCollection", "COLLECTION_ID"), (Object)collectionList.toArray(), 8));
        selectQuery.addSelectColumn(new Column("MdAppToCollection", "*"));
        selectQuery.addSelectColumn(new Column("MdPackageToAppData", "PACKAGE_ID"));
        selectQuery.addSelectColumn(new Column("MdPackageToAppData", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(new Column("MdPackageToAppData", "APP_ID"));
        selectQuery.addSelectColumn(new Column("MdPackageToAppData", "SUPPORTED_DEVICES"));
        selectQuery.addSelectColumn(new Column("MdPackageToAppGroup", "PACKAGE_TYPE"));
        selectQuery.addSelectColumn(new Column("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"));
        try {
            final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (ds.next()) {
                final Long collectionId = (Long)ds.getValue("COLLECTION_ID");
                if (!appJSON.containsKey(collectionId)) {
                    appJSON.put(collectionId, new HashMap());
                }
                final HashMap appMap = appJSON.get(collectionId);
                appMap.put("COLLECTION_ID", collectionId);
                appMap.put("PACKAGE_ID", ds.getValue("PACKAGE_ID"));
                appMap.put("APP_ID", ds.getValue("APP_ID"));
                appMap.put("APP_GROUP_ID", ds.getValue("APP_GROUP_ID"));
                appMap.put("SUPPORTED_DEVICES", ds.getValue("SUPPORTED_DEVICES"));
                appMap.put("IS_PURCHASED_FROM_PORTAL", ds.getValue("IS_PURCHASED_FROM_PORTAL"));
                appMap.put("PACKAGE_TYPE", ds.getValue("PACKAGE_TYPE"));
            }
        }
        catch (final Exception e) {
            AppsUtil.logger.log(Level.SEVERE, "Cannot fetch app details from collection", e);
        }
        return appJSON;
    }
    
    public List<Map> getAppDetailsListFromCollection(final List<Long> collectionList) {
        final List<Map> appJSON = new ArrayList<Map>();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppToCollection"));
        selectQuery.addJoin(new Join("MdAppToCollection", "MdPackageToAppData", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery.addJoin(new Join("MdPackageToAppData", "MdPackageToAppGroup", new String[] { "PACKAGE_ID", "APP_GROUP_ID" }, new String[] { "PACKAGE_ID", "APP_GROUP_ID" }, 2));
        selectQuery.setCriteria(new Criteria(new Column("MdAppToCollection", "COLLECTION_ID"), (Object)collectionList.toArray(), 8));
        selectQuery.addSelectColumn(new Column("MdAppToCollection", "*"));
        selectQuery.addSelectColumn(new Column("MdPackageToAppData", "PACKAGE_ID"));
        selectQuery.addSelectColumn(new Column("MdPackageToAppData", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(new Column("MdPackageToAppData", "APP_ID"));
        selectQuery.addSelectColumn(new Column("MdPackageToAppData", "SUPPORTED_DEVICES"));
        selectQuery.addSelectColumn(new Column("MdPackageToAppGroup", "PACKAGE_TYPE"));
        selectQuery.addSelectColumn(new Column("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"));
        try {
            final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (ds.next()) {
                final Long collectionId = (Long)ds.getValue("COLLECTION_ID");
                final Map appMap = new HashMap();
                appMap.put("COLLECTION_ID", collectionId);
                appMap.put("PACKAGE_ID", ds.getValue("PACKAGE_ID"));
                appMap.put("APP_ID", ds.getValue("APP_ID"));
                appMap.put("APP_GROUP_ID", ds.getValue("APP_GROUP_ID"));
                appMap.put("SUPPORTED_DEVICES", ds.getValue("SUPPORTED_DEVICES"));
                appMap.put("IS_PURCHASED_FROM_PORTAL", ds.getValue("IS_PURCHASED_FROM_PORTAL"));
                appMap.put("PACKAGE_TYPE", ds.getValue("PACKAGE_TYPE"));
                appJSON.add(appMap);
            }
        }
        catch (final Exception e) {
            AppsUtil.logger.log(Level.SEVERE, "Cannot fetch app details from collection", e);
        }
        return appJSON;
    }
    
    public List<Long> getContainerScopeResources(final List<Long> resourceList, final Long appGroupId) throws DataAccessException {
        List<Long> knoxResources = null;
        Criteria criteria = new Criteria(new Column("MdAppCatalogToResourceScope", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
        criteria = criteria.and(new Criteria(new Column("MdAppCatalogToResourceScope", "APP_GROUP_ID"), (Object)appGroupId, 0));
        criteria = criteria.and(new Criteria(new Column("MdAppCatalogToResourceScope", "SCOPE"), (Object)1, 0));
        final DataObject dO = MDMUtil.getPersistence().get("MdAppCatalogToResourceScope", criteria);
        if (!dO.isEmpty()) {
            knoxResources = new ArrayList<Long>();
            final Iterator<Row> rows = dO.getRows("MdAppCatalogToResourceScope");
            while (rows.hasNext()) {
                final Row row = rows.next();
                knoxResources.add((Long)row.get("RESOURCE_ID"));
            }
        }
        return knoxResources;
    }
    
    public int validatePlayStoreToEnterpriseConversion(final JSONObject requestJSON, int appType, final int requestedAppType, final String identifier, final Long appGroupID) {
        final int platformType = (int)requestJSON.get("platform_type");
        final long customerID = (long)requestJSON.get("customerID");
        if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AllowPlayStoreToEnterprise") && !new AppsUtil().isAppExistsInPackage(identifier, platformType, customerID, true) && requestedAppType == 2 && requestedAppType != appType && platformType == 2 && requestJSON.has("app_file")) {
            appType = 2;
            requestJSON.put("convertPlayStoreToEnterprise", true);
        }
        final Boolean isSameBundleIDStoreAndEnterpriseAppMgmtEnabled = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AllowSameBundleIDStoreAndEnterpriseAppForIOS");
        if (requestedAppType == 2 && requestedAppType != appType && new AppsUtil().isAppExistsInPackage(identifier, platformType, customerID) && (!isSameBundleIDStoreAndEnterpriseAppMgmtEnabled || platformType != 1)) {
            final String appName = requestJSON.optString("app_name");
            throw new APIHTTPException("APP0030", new Object[] { appName });
        }
        return appType;
    }
    
    public Long validateIfAppIsToBeConvertedfromPlaystore(final JSONObject message, final Long releaseLabelId, final Long customerID, final int platformType) throws DataAccessException {
        if (message.optBoolean("convertPlayStoreToEnterprise") && platformType == 2) {
            return AppVersionDBUtil.getInstance().getProductionAppReleaseLabelIDForCustomer(customerID);
        }
        return releaseLabelId;
    }
    
    public JSONArray getPortalAppsAssociatedForDevice(final Long resourceId) {
        final JSONArray jsonArray = new JSONArray();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForResource"));
        selectQuery.addJoin(new Join("RecentProfileForResource", "ProfileToCollection", new String[] { "PROFILE_ID", "COLLECTION_ID" }, new String[] { "PROFILE_ID", "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("ProfileToCollection", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToCollection", "MdPackageToAppData", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        final Criteria appCriteria = new Criteria(new Column("MdPackageToAppData", "APP_GROUP_ID"), (Object)new Column("MdAppCatalogToResource", "APP_GROUP_ID"), 0);
        final Criteria resCriteria = new Criteria(new Column("MdAppCatalogToResource", "RESOURCE_ID"), (Object)new Column("RecentProfileForResource", "RESOURCE_ID"), 0);
        selectQuery.addJoin(new Join("MdPackageToAppData", "MdAppCatalogToResource", appCriteria.and(resCriteria), 2));
        selectQuery.addJoin(new Join("MdAppCatalogToResource", "MdAppCatalogToResourceExtn", new String[] { "RESOURCE_ID", "APP_GROUP_ID" }, new String[] { "RESOURCE_ID", "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppCatalogToResource", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addSelectColumn(new Column("MdAppGroupDetails", "IDENTIFIER"));
        final Column groupByColumn = new Column("MdAppGroupDetails", "IDENTIFIER");
        final List groupByColumns = new ArrayList();
        groupByColumns.add(groupByColumn);
        final GroupByClause grpByCls = new GroupByClause(groupByColumns);
        selectQuery.setGroupByClause(grpByCls);
        final Criteria notDeletedCriteria = new Criteria(new Column("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
        final Criteria resourceCriteria = new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceId, 0);
        final Criteria portalAppCriteria = new Criteria(new Column("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), (Object)true, 0);
        selectQuery.setCriteria(notDeletedCriteria.and(resourceCriteria).and(portalAppCriteria));
        try {
            final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (dmDataSetWrapper.next()) {
                jsonArray.put((Object)dmDataSetWrapper.getValue("IDENTIFIER"));
            }
            AppsUtil.logger.log(Level.INFO, "No of portal apps for device {0} is {1}", new Object[] { resourceId, jsonArray.length() });
        }
        catch (final Exception e) {
            AppsUtil.logger.log(Level.WARNING, "Cannot fetch app identifiers for the device", e);
        }
        return jsonArray;
    }
    
    public boolean isNewVersion(final Long appGroupId, final JSONObject appJson, final Long customerId, final int platformType) {
        boolean isVersionUpdated = false;
        try {
            final SelectQuery appQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackageToAppGroup"));
            appQuery.addJoin(new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            appQuery.addJoin(new Join("MdPackageToAppGroup", "MdPackageToAppData", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
            appQuery.addJoin(new Join("MdPackageToAppData", "MdAppToCollection", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
            appQuery.addJoin(new Join("MdAppToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            appQuery.addJoin(new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            appQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            appQuery.addJoin(new Join("MdAppToCollection", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
            final Criteria appGrpCriteria = new Criteria(new Column("MdPackageToAppData", "APP_GROUP_ID"), (Object)appGroupId, 0);
            final Criteria custcriteria = new Criteria(new Column("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria combinecriteria = custcriteria.and(appGrpCriteria);
            appQuery.setCriteria(combinecriteria);
            appQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "PROFILE_VERSION"));
            appQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_VERSION"));
            appQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_ID"));
            final SortColumn sortColumn = new SortColumn("ProfileToCollection", "PROFILE_VERSION", (boolean)Boolean.FALSE);
            appQuery.addSortColumn(sortColumn);
            final DMDataSetWrapper dataSetWrapper = DMDataSetWrapper.executeQuery((Object)appQuery);
            if (dataSetWrapper.next()) {
                final Long oldAppID = (Long)dataSetWrapper.getValue("APP_ID");
                final JSONObject availableAppJson = getInstance().getAppDetailsJson(oldAppID);
                final AppVersionChecker checker = AppVersionChecker.getInstance(platformType);
                isVersionUpdated = checker.isAppVersionGreater(appJson, availableAppJson);
            }
            AppsUtil.logger.log(Level.INFO, "AppUpdateSync: isNewVersion(): {0} AppGroup={1} Name={2} Version={3}", new Object[] { isVersionUpdated, appGroupId, appJson.optString("APP_NAME"), appJson.optString("APP_VERSION") });
        }
        catch (final NumberFormatException numFormatex) {
            AppsUtil.logger.log(Level.WARNING, "AppUpdateSync: Nubmer format Exception in getAppVersionChecker {0} for AppGroup={1} Name={2} Version={3}", new Object[] { numFormatex.getMessage(), appGroupId, appJson.optString("APP_NAME"), appJson.optString("APP_VERSION") });
        }
        catch (final Exception e) {
            AppsUtil.logger.log(Level.SEVERE, "AppUpdateSync: Exception while isNewVersion() ", e);
        }
        return isVersionUpdated;
    }
    
    public static void validateAndPerformUpdateAllOnAppUpdate(final JSONObject requestJSON, final JSONObject responseJSON) {
        try {
            final Boolean distributeUpdate = requestJSON.optBoolean("distribute_update", (boolean)Boolean.FALSE);
            final Long releaseLabelId = responseJSON.getLong("RELEASE_LABEL_ID");
            final Long customerID = responseJSON.getLong("CUSTOMER_ID");
            final Long appGroupId = responseJSON.getLong("APP_GROUP_ID");
            final Long userID = responseJSON.getLong("PACKAGE_ADDED_BY");
            if (distributeUpdate) {
                AppsUtil.logger.log(Level.INFO, "Performing update all for appgroupid {0}, releaselabelid {1} on enterprise app update", new Object[] { appGroupId, releaseLabelId });
                final Properties properties = new Properties();
                final Boolean isSilentInstall = requestJSON.optBoolean("silent_install");
                final Boolean isNotify = requestJSON.optBoolean("notify_user_via_email");
                ((Hashtable<String, Integer>)properties).put("toBeAssociatedAppSource", MDMCommonConstants.ASSOCIATED_APP_SOURCE_BY_USER);
                ((Hashtable<String, Boolean>)properties).put("isSilentInstall", isSilentInstall);
                ((Hashtable<String, Boolean>)properties).put("isNotify", isNotify);
                ((Hashtable<String, Long>)properties).put("RELEASE_LABEL_ID", releaseLabelId);
                final HashMap resourceMap = MDMAppMgmtHandler.getInstance().performUpdateAllForAppGroup(appGroupId, customerID, userID, properties);
                final List grpList = resourceMap.get("groupList");
                final List deviceList = resourceMap.get("resourceList");
                responseJSON.put("group_list", (Collection)grpList);
                responseJSON.put("device_list", (Collection)deviceList);
            }
        }
        catch (final Exception ex) {
            AppsUtil.logger.log(Level.SEVERE, "Exception distributing updated enterprise app to device and groups", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public boolean isSignatureMismatch(final Long appGroupId, final JSONObject newSignInfo, final int platFormType) {
        boolean isSignatureMismatch = true;
        try {
            final Row row = MDMDBUtil.getRowFromDB("AppGroupSignatureDetails", "APP_GROUP_ID", (Object)appGroupId);
            String newMD5Fingerprint = "";
            String newSHA256Fingerprint = "";
            String newSHA128Fingerprint = "";
            String oldMD5Fingerprint = "";
            String oldSHA256Fingerprint = "";
            String oldSHA128Fingerprint = "";
            if (!MDMStringUtils.isEmpty(newSignInfo.optString("SHA1"))) {
                newSHA128Fingerprint = (String)newSignInfo.get("SHA1");
            }
            if (!MDMStringUtils.isEmpty(newSignInfo.optString("MD5"))) {
                newMD5Fingerprint = (String)newSignInfo.get("MD5");
            }
            if (!MDMStringUtils.isEmpty(newSignInfo.optString("SHA256"))) {
                newSHA256Fingerprint = (String)newSignInfo.get("SHA256");
            }
            if (row != null) {
                oldMD5Fingerprint = (String)row.get("FINGERPRINT_MD5");
                oldSHA128Fingerprint = (String)row.get("FINGERPRINT_SHA128");
                oldSHA256Fingerprint = (String)row.get("FINGERPRINT_SHA256");
            }
            else {
                final String filePath = this.getAppFilePath(appGroupId, null);
                if (!MDMStringUtils.isEmpty(filePath)) {
                    final JSONObject existingSignInfo = EnterpriseAppExtractor.getNewInstance(platFormType).getAppSignatureDetails(filePath);
                    if (existingSignInfo != null) {
                        if (!MDMStringUtils.isEmpty(existingSignInfo.optString("SHA1"))) {
                            oldSHA128Fingerprint = (String)existingSignInfo.get("SHA1");
                        }
                        if (!MDMStringUtils.isEmpty(existingSignInfo.optString("MD5"))) {
                            oldMD5Fingerprint = (String)existingSignInfo.get("MD5");
                        }
                        if (!MDMStringUtils.isEmpty(existingSignInfo.optString("SHA256"))) {
                            oldSHA256Fingerprint = (String)existingSignInfo.get("SHA256");
                        }
                    }
                }
            }
            if (MDMStringUtils.isEmptyOrEquals(oldMD5Fingerprint, newMD5Fingerprint) && MDMStringUtils.isEmptyOrEquals(oldSHA128Fingerprint, newSHA128Fingerprint) && MDMStringUtils.isEmptyOrEquals(oldSHA256Fingerprint, newSHA256Fingerprint)) {
                isSignatureMismatch = false;
            }
        }
        catch (final Exception e) {
            AppsUtil.logger.log(Level.WARNING, "Cannot validate signatures", e);
        }
        return isSignatureMismatch;
    }
    
    protected String getAppFilePath(final Long appGroupId, final Long appId) throws DataAccessException {
        String fullPath = null;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackageToAppData"));
        selectQuery.addJoin(new Join("MdPackageToAppData", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addSelectColumn(new Column("MdPackageToAppData", "*"));
        if (appGroupId != null) {
            selectQuery.setCriteria(new Criteria(new Column("MdPackageToAppData", "APP_GROUP_ID"), (Object)appGroupId, 0));
        }
        else if (appId != null) {
            selectQuery.setCriteria(new Criteria(new Column("MdPackageToAppData", "APP_ID"), (Object)appId, 0));
        }
        selectQuery.setCriteria(selectQuery.getCriteria().and(AppVersionDBUtil.getInstance().getApprovedAppVersionCriteria()));
        selectQuery.addSortColumn(new SortColumn(new Column("MdPackageToAppData", "APP_ID"), true));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row appRow = dataObject.getFirstRow("MdPackageToAppData");
            final String appFilePath = (String)appRow.get("APP_FILE_LOC");
            fullPath = MDMAppMgmtHandler.getInstance().getAppRepositoryBaseFolderPath().concat(appFilePath);
        }
        return fullPath;
    }
    
    public Map<Long, String> getAppsNameFromPackageIds(final List packageIds) {
        final Map<Long, String> appNames = new HashMap<Long, String>();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
        selectQuery.addJoin(new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("ProfileToCollection", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("AppGroupToCollection", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addSelectColumn(new Column("Profile", "PROFILE_ID"));
        selectQuery.addSelectColumn(new Column("Profile", "PROFILE_NAME"));
        selectQuery.setCriteria(new Criteria(new Column("MdPackageToAppGroup", "PACKAGE_ID"), (Object)packageIds.toArray(), 8));
        try {
            final DMDataSetWrapper dataSet = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (dataSet.next()) {
                final Long profileId = (Long)dataSet.getValue("PROFILE_ID");
                if (!appNames.containsKey(profileId)) {
                    final String appName = (String)dataSet.getValue("PROFILE_NAME");
                    appNames.put(profileId, appName);
                }
            }
        }
        catch (final Exception e) {
            AppsUtil.logger.log(Level.SEVERE, "Exception in getAppsNameFromPackageIds()", e);
        }
        return appNames;
    }
    
    public Map<Long, String> getAppIdentifiersFromPackageIds(final List packageIds) {
        final Map<Long, String> packageIdToIdentifier = new HashMap<Long, String>();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackageToAppGroup"));
        selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addSelectColumn(new Column("MdPackageToAppGroup", "PACKAGE_ID"));
        selectQuery.addSelectColumn(new Column("MdAppGroupDetails", "IDENTIFIER"));
        selectQuery.setCriteria(new Criteria(new Column("MdPackageToAppGroup", "PACKAGE_ID"), (Object)packageIds.toArray(), 8));
        try {
            final DMDataSetWrapper dataSet = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (dataSet.next()) {
                final Long packageId = (Long)dataSet.getValue("PACKAGE_ID");
                if (!packageIdToIdentifier.containsKey(packageId)) {
                    final String identifier = (String)dataSet.getValue("IDENTIFIER");
                    packageIdToIdentifier.put(packageId, identifier);
                }
            }
        }
        catch (final Exception e) {
            AppsUtil.logger.log(Level.SEVERE, "Exception in getAppsNameFromPackageIds()", e);
        }
        return packageIdToIdentifier;
    }
    
    public static void updateAppSharedScope(final List appIds, final Integer appSharedScope) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackage"));
        selectQuery.addJoin(new Join("MdPackage", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "*"));
        final Criteria appCriteria = new Criteria(Column.getColumn("MdPackage", "PACKAGE_ID"), (Object)appIds.toArray(), 8);
        selectQuery.setCriteria(appCriteria);
        final DataObject dataObject = DataAccess.get(selectQuery);
        Criteria appIdCriteria = null;
        if (!dataObject.isEmpty()) {
            final Iterator<Row> iterator = dataObject.getRows("MdAppGroupDetails");
            while (iterator.hasNext()) {
                final Row appGroupRow = iterator.next();
                final String identifier = (String)appGroupRow.get("IDENTIFIER");
                final Integer platform = (Integer)appGroupRow.get("PLATFORM_TYPE");
                final Criteria identifierCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)identifier, 0);
                final Criteria platformCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)platform, 0);
                if (appIdCriteria == null) {
                    appIdCriteria = identifierCriteria.and(platformCriteria);
                }
                else {
                    appIdCriteria = appIdCriteria.or(identifierCriteria.and(platformCriteria));
                }
            }
            if (appIdCriteria != null) {
                final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("MdPackage");
                updateQuery.addJoin(new Join("MdPackage", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
                updateQuery.addJoin(new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
                updateQuery.setCriteria(appIdCriteria);
                updateQuery.setUpdateColumn("APP_SHARED_SCOPE", (Object)appSharedScope);
                MDMUtil.getPersistence().update(updateQuery);
            }
        }
    }
    
    public static Column getDerivedColumnOfAppIdentifiersForGivenAppGroupIds(final List appGroupIds) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroupIds.toArray(), 8));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"));
        return (Column)new DerivedColumn("DC", selectQuery);
    }
    
    public void calculateCheckSumForAppFiles(final Long customerId) throws Exception {
        final Long startTime = System.currentTimeMillis();
        try {
            SelectQueryImpl selectQuery = new SelectQueryImpl(Table.getTable("MdPackage"));
            selectQuery.addJoin(new Join("MdPackage", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
            selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdPackageToAppData", new String[] { "PACKAGE_ID", "APP_GROUP_ID" }, new String[] { "PACKAGE_ID", "APP_GROUP_ID" }, 2));
            selectQuery.addJoin(new Join("MdPackageToAppData", "MdAppCatalogToResource", new String[] { "APP_ID", "APP_GROUP_ID" }, new String[] { "PUBLISHED_APP_ID", "APP_GROUP_ID" }, 2));
            selectQuery.addJoin(new Join("MdAppCatalogToResource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "*"));
            final Criteria emptyCheckSumCriteria = new Criteria(new Column("MdPackageToAppData", "APP_CHECKSUM"), (Object)null, 0);
            final Criteria successfullyEnrolledCriteria = ManagedDeviceHandler.getInstance().getSuccessfullyEnrolledCriteria();
            final Criteria enterpriseAppCriteria = new Criteria(new Column("MdPackageToAppGroup", "PACKAGE_TYPE"), (Object)2, 0);
            final Criteria customerCriteria = new Criteria(new Column("MdPackage", "CUSTOMER_ID"), (Object)customerId, 0);
            selectQuery.setCriteria(customerCriteria.and(emptyCheckSumCriteria.and(successfullyEnrolledCriteria.and(enterpriseAppCriteria))));
            this.updateCheckSumForAppFile((SelectQuery)selectQuery);
            selectQuery = new SelectQueryImpl(Table.getTable("MdPackage"));
            selectQuery.addJoin(new Join("MdPackage", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
            selectQuery.addJoin(new Join("MdPackageToAppGroup", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            selectQuery.addJoin(new Join("AppGroupToCollection", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQuery.addJoin(new Join("MdAppToCollection", "MdPackageToAppData", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
            selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "*"));
            selectQuery.setCriteria(customerCriteria.and(emptyCheckSumCriteria.and(enterpriseAppCriteria)));
            this.updateCheckSumForAppFile((SelectQuery)selectQuery);
            AppsUtil.appMgmtLogger.log(Level.WARNING, "Total time taken {0}", System.currentTimeMillis() - startTime);
        }
        catch (final Exception e) {
            AppsUtil.appMgmtLogger.log(Level.WARNING, "Could not update checksum", e);
            throw e;
        }
    }
    
    protected void updateCheckSumForAppFile(final SelectQuery selectQuery) throws Exception {
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            AppsUtil.appMgmtLogger.log(Level.INFO, "Going to update checksum for the app files");
            final Iterator<Row> iterator = dataObject.getRows("MdPackageToAppData");
            while (iterator.hasNext()) {
                final Long perFileTime = System.currentTimeMillis();
                final Row row = iterator.next();
                final String appFilePath = (String)row.get("APP_FILE_LOC");
                final String fullPath = MDMAppMgmtHandler.getInstance().getAppRepositoryBaseFolderPath().concat(appFilePath);
                if (fullPath != null) {
                    final FileAccessAPI fileAccessAPI = ApiFactoryProvider.getFileAccessAPI();
                    if (fileAccessAPI.isFileExists(fullPath)) {
                        final String fileCheckSum = ChecksumProvider.getInstance().GetSHA256CheckSum(fullPath);
                        row.set("APP_CHECKSUM", (Object)fileCheckSum);
                        dataObject.updateRow(row);
                    }
                    AppsUtil.appMgmtLogger.log(Level.INFO, "Time taken for app {0} of size {1} is {2}", new Object[] { row.get("PACKAGE_ID"), row.get("FILE_UPLOAD_SIZE"), System.currentTimeMillis() - perFileTime });
                }
            }
            MDMUtil.getPersistence().update(dataObject);
        }
    }
    
    public JSONObject getAppIDAndLabelForGivenIdentifier(final String identifier, final Integer platform, final Long customerId) throws DataAccessException {
        return this.getAppIDAndLabelForGivenIdentifier(identifier, platform, customerId, null);
    }
    
    public JSONObject getAppIDAndLabelForGivenIdentifier(final String identifier, final Integer platform, final Long customerId, final Criteria additionCriteria) throws DataAccessException {
        final JSONObject response = new JSONObject();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackageToAppGroup"));
        selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppGroupDetails", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("AppGroupToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        final Criteria appApprovedCriteria = AppVersionDBUtil.getInstance().getApprovedAppVersionCriteria();
        final Criteria identifierCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)identifier, 0, (boolean)getInstance().getIsBundleIdCaseSenstive(platform));
        final Criteria platformCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)platform, 0);
        final Criteria customerCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        selectQuery.setCriteria(appApprovedCriteria.and(identifierCriteria).and(platformCriteria).and(customerCriteria));
        if (additionCriteria != null) {
            selectQuery.setCriteria(selectQuery.getCriteria().and(additionCriteria));
        }
        selectQuery.addSelectColumn(new Column("MdPackageToAppGroup", "PACKAGE_ID"));
        selectQuery.addSelectColumn(new Column("AppGroupToCollection", "COLLECTION_ID"));
        selectQuery.addSelectColumn(new Column("AppGroupToCollection", "RELEASE_LABEL_ID"));
        selectQuery.addSelectColumn(new Column("AppGroupToCollection", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(new Column("ProfileToCollection", "PROFILE_ID"));
        selectQuery.addSelectColumn(new Column("ProfileToCollection", "COLLECTION_ID"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row mdPackageRow = dataObject.getFirstRow("MdPackageToAppGroup");
            final Long packageId = (Long)mdPackageRow.get("PACKAGE_ID");
            final Row appGroupCollnRow = dataObject.getFirstRow("AppGroupToCollection");
            final Long labelId = (Long)appGroupCollnRow.get("RELEASE_LABEL_ID");
            final Long collectionID = (Long)appGroupCollnRow.get("COLLECTION_ID");
            final Long appGroupId = (Long)appGroupCollnRow.get("APP_GROUP_ID");
            final Row profileRow = dataObject.getFirstRow("ProfileToCollection");
            final Long profileId = (Long)profileRow.get("PROFILE_ID");
            response.put("PACKAGE_ID", (Object)packageId);
            response.put("RELEASE_LABEL_ID", (Object)labelId);
            response.put("COLLECTION_ID", (Object)collectionID);
            response.put("PROFILE_ID", (Object)profileId);
            response.put("APP_GROUP_ID", (Object)appGroupId);
        }
        else {
            AppsUtil.logger.log(Level.SEVERE, "App not found {0} {1} {2}", new Object[] { identifier, platform, customerId });
        }
        return response;
    }
    
    private SelectQuery getAppGroupToProfileQuery() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
        selectQuery.addJoin(new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("ProfileToCollection", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToCollection", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        return selectQuery;
    }
    
    public Long getProfileIDFromAppGroupID(final Long appGroupID) {
        Long profileID = null;
        try {
            final SelectQuery selectQuery = this.getAppGroupToProfileQuery();
            selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("MdAppToGroupRel", "APP_GROUP_ID"), (Object)appGroupID, 0));
            final DataObject profileDo = MDMUtil.getPersistence().get(selectQuery);
            if (!profileDo.isEmpty()) {
                final Row profileRow = profileDo.getFirstRow("Profile");
                profileID = (Long)profileRow.get("PROFILE_ID");
            }
        }
        catch (final Exception e) {
            AppsUtil.logger.log(Level.SEVERE, "Exception in getProfileIDFromAppGroupID", e);
        }
        return profileID;
    }
    
    public Long getAppGroupIDFromProfileID(final Long profileID) {
        Long appGroupID = null;
        try {
            final SelectQuery selectQuery = this.getAppGroupToProfileQuery();
            selectQuery.addSelectColumn(Column.getColumn("MdAppToGroupRel", "*"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileID, 0));
            final DataObject appGroupDo = MDMUtil.getPersistence().get(selectQuery);
            if (!appGroupDo.isEmpty()) {
                final Row profileRow = appGroupDo.getFirstRow("MdAppToGroupRel");
                appGroupID = (Long)profileRow.get("APP_GROUP_ID");
            }
        }
        catch (final Exception e) {
            AppsUtil.logger.log(Level.SEVERE, "Exception in getAppGroupIDFromProfileID", e);
        }
        return appGroupID;
    }
    
    public static DataObject getAppCatalogAppGroupDetailsDO(final Criteria criteria) {
        DataObject appCatalogDO = (DataObject)new WritableDataObject();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackageToAppGroup"));
            selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            selectQuery.addJoin(new Join("MdAppGroupDetails", "MdAppCatalogToResource", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            selectQuery.setCriteria(criteria);
            selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "*"));
            selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "*"));
            selectQuery.addSelectColumn(Column.getColumn("MdAppCatalogToResource", "*"));
            appCatalogDO = MDMUtil.getPersistence().get(selectQuery);
        }
        catch (final Exception e) {
            AppsUtil.logger.log(Level.SEVERE, "Exception in getAppCatalogAppGroupDetailsDO", e);
        }
        return appCatalogDO;
    }
    
    public SelectQuery getPortalApprovedAppsQuery(final Long customerId, final int platformType, final Long businessStoreID) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackageToAppGroup"));
        final Criteria portalPurchasedCriteria = new Criteria(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), (Object)Boolean.TRUE, 0);
        final Criteria customerCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria platformCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)platformType, 0);
        selectQuery.setCriteria(customerCriteria.and(portalPurchasedCriteria).and(platformCriteria));
        selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        if (businessStoreID != null) {
            selectQuery.addJoin(new Join("MdAppGroupDetails", "MdBusinessStoreToAssetRel", new String[] { "IDENTIFIER" }, new String[] { "ASSET_IDENTIFIER" }, 2));
            final Criteria businessStoreCriteria = new Criteria(new Column("MdBusinessStoreToAssetRel", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
            selectQuery.setCriteria(selectQuery.getCriteria().and(businessStoreCriteria));
        }
        return selectQuery;
    }
    
    public List getPortalApprovedApps(final Long customerId, final int platformType, final Long businessStoreID) throws Exception {
        List list = new ArrayList();
        final SelectQuery sQuery = this.getPortalApprovedAppsQuery(customerId, platformType, businessStoreID);
        sQuery.addSelectColumn(new Column("MdAppGroupDetails", "APP_GROUP_ID"));
        final DataObject dO = DataAccess.get(sQuery);
        if (!dO.isEmpty()) {
            final Iterator it = dO.getRows("MdAppGroupDetails");
            list = DBUtil.getColumnValuesAsList(it, "APP_GROUP_ID");
        }
        return list;
    }
    
    public List getPortalApprovedAppIdentifiers(final Long customerId, final int platformType, final Long businessStoreID) throws Exception {
        List list = new ArrayList();
        final SelectQuery sQuery = this.getPortalApprovedAppsQuery(customerId, platformType, businessStoreID);
        sQuery.addSelectColumn(new Column("MdAppGroupDetails", "APP_GROUP_ID"));
        sQuery.addSelectColumn(new Column("MdAppGroupDetails", "IDENTIFIER"));
        final DataObject dO = DataAccess.get(sQuery);
        if (!dO.isEmpty()) {
            final Iterator it = dO.getRows("MdAppGroupDetails");
            list = DBUtil.getColumnValuesAsList(it, "IDENTIFIER");
        }
        return list;
    }
    
    public Map<Long, JSONObject> getPortalAppDetails(final List<Long> collectionList) {
        final Map<Long, JSONObject> collnToAppDetails = new HashMap<Long, JSONObject>();
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppToCollection"));
        sQuery.addJoin(new Join("MdAppToCollection", "MdPackageToAppData", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        sQuery.addJoin(new Join("MdPackageToAppData", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        sQuery.addJoin(new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        final Criteria portalCriteria = new Criteria(new Column("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), (Object)true, 0);
        final Criteria collnCriteria = new Criteria(new Column("MdAppToCollection", "COLLECTION_ID"), (Object)collectionList.toArray(), 8);
        sQuery.setCriteria(portalCriteria.and(collnCriteria));
        sQuery.addSelectColumn(new Column("MdAppToCollection", "COLLECTION_ID"));
        sQuery.addSelectColumn(new Column("MdAppGroupDetails", "IDENTIFIER"));
        sQuery.addSelectColumn(new Column("MdPackageToAppData", "APP_ID"));
        sQuery.addSelectColumn(new Column("MdPackageToAppData", "APP_GROUP_ID"));
        sQuery.addSelectColumn(new Column("MdPackageToAppGroup", "IS_PAID_APP"));
        try {
            final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)sQuery);
            while (dmDataSetWrapper.next()) {
                final Long collectionId = (Long)dmDataSetWrapper.getValue("COLLECTION_ID");
                final String identifier = (String)dmDataSetWrapper.getValue("IDENTIFIER");
                final Long appId = (Long)dmDataSetWrapper.getValue("APP_ID");
                final Long appGroupId = (Long)dmDataSetWrapper.getValue("APP_GROUP_ID");
                final JSONObject appJSON = new JSONObject();
                appJSON.put("COLLECTION_ID", (Object)collectionId);
                appJSON.put("IDENTIFIER", (Object)identifier);
                appJSON.put("APP_ID", (Object)appId);
                appJSON.put("APP_GROUP_ID", (Object)appGroupId);
                appJSON.put("IS_PAID_APP", (Object)dmDataSetWrapper.getValue("IS_PAID_APP"));
                collnToAppDetails.put(collectionId, appJSON);
            }
        }
        catch (final Exception e) {
            AppsUtil.logger.log(Level.SEVERE, "Couldn't fetch portal app details", e);
        }
        return collnToAppDetails;
    }
    
    public Map<Long, JSONObject> getProfileToAppDetailsForCustomer(final Long customerId, final int platformType, final boolean isPortal) {
        final Map<Long, JSONObject> profileToAppDetails = new HashMap<Long, JSONObject>();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ProfileToCustomerRel"));
        selectQuery.addJoin(new Join("ProfileToCustomerRel", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("ProfileToCollection", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("AppGroupToCollection", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        final Criteria customerCriteria = new Criteria(new Column("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria portalAppCriteria = new Criteria(new Column("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), (Object)isPortal, 0);
        final Criteria notTrashCriteria = new Criteria(new Column("Profile", "IS_MOVED_TO_TRASH"), (Object)false, 0);
        final Criteria platFormTypeCriteria = new Criteria(new Column("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)platformType, 0);
        selectQuery.setCriteria(customerCriteria.and(portalAppCriteria).and(notTrashCriteria).and(platFormTypeCriteria));
        selectQuery.addSelectColumn(new Column("Profile", "PROFILE_ID"));
        selectQuery.addSelectColumn(new Column("MdAppGroupDetails", "IDENTIFIER"));
        try {
            final DMDataSetWrapper dataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (dataSetWrapper.next()) {
                final Long profileId = (Long)dataSetWrapper.getValue("PROFILE_ID");
                final String identifier = (String)dataSetWrapper.getValue("IDENTIFIER");
                if (!profileToAppDetails.containsKey(profileId)) {
                    final JSONObject appDetails = new JSONObject();
                    appDetails.put("PROFILE_ID", (Object)profileId);
                    appDetails.put("IDENTIFIER", (Object)identifier);
                    profileToAppDetails.put(profileId, appDetails);
                }
            }
        }
        catch (final Exception e) {
            AppsUtil.logger.log(Level.SEVERE, "Cannot get portal details ", e);
        }
        return profileToAppDetails;
    }
    
    public static String getValidVersion(final String appVersion) {
        String versionName = appVersion;
        if (appVersion != null && appVersion.contains("[[")) {
            versionName = appVersion.substring(0, appVersion.indexOf("[["));
        }
        return versionName;
    }
    
    static {
        AppsUtil.logger = Logger.getLogger("MDMConfigLogger");
        AppsUtil.appMgmtLogger = Logger.getLogger("MDMAppMgmtLogger");
        AppsUtil.appsUtil = null;
    }
}
