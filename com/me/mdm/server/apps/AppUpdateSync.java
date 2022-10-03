package com.me.mdm.server.apps;

import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.server.apps.ios.ContentMetaDataAppDetails;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.server.apps.ios.ContentMetaDataAPIHandler;
import java.util.List;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.Row;
import java.util.Iterator;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import java.util.HashMap;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import com.adventnet.persistence.DataObject;

public class AppUpdateSync
{
    Long customerId;
    int platformType;
    DataObject pkgToAppDO;
    DataObject latestAppGroupDO;
    private JSONObject resultJson;
    JSONArray updateAppGroupList;
    final Logger logger;
    
    public JSONArray getUpdateAppGroupList() {
        return this.updateAppGroupList;
    }
    
    public AppUpdateSync(final Long customerId, final int platformType) {
        this.pkgToAppDO = null;
        this.latestAppGroupDO = null;
        this.logger = Logger.getLogger("MDMConfigLogger");
        this.customerId = customerId;
        this.platformType = platformType;
        this.initializeResultJson();
    }
    
    public JSONObject syncStoreIds(final ArrayList<String> storeIds) {
        this.logger.log(Level.INFO, "AppUpdateSync: syncStoreIds() begins.... ");
        try {
            this.logger.log(Level.INFO, "syncStoreIds: {0}", new String[] { storeIds.toString() });
            this.setStoreAppsPkgToAppDO(new Criteria(new Column("MdPackageToAppData", "STORE_ID"), (Object)storeIds.toArray(), 8));
            this.syncApps();
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "AppUpdateSync: Exception while syncStoreIds() ", e);
        }
        this.logger.log(Level.INFO, "AppUpdateSync: syncStoreIds() ends.... ");
        return this.resultJson;
    }
    
    public JSONObject syncNonPortalApps() {
        this.logger.log(Level.INFO, "AppUpdateSync: syncNonPortalApps() begins.... ");
        try {
            this.setStoreAppsPkgToAppDO(new Criteria(new Column("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), (Object)false, 0));
            this.syncApps();
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "AppUpdateSync: Exception while syncNonPortalApps() ", e);
        }
        this.logger.log(Level.INFO, "AppUpdateSync: syncNonPortalApps() ends.... ");
        return this.resultJson;
    }
    
    private JSONObject syncApps() {
        try {
            final HashMap updateAppsHashMap = this.getAppUpdateDetails();
            final JSONArray appDetailsArray = new JSONArray();
            if (!updateAppsHashMap.isEmpty()) {
                final Iterator iterator = updateAppsHashMap.keySet().iterator();
                Long userId = null;
                while (iterator.hasNext()) {
                    final Integer adamId = iterator.next();
                    final HashMap singleAppDetails = updateAppsHashMap.get(adamId);
                    singleAppDetails.put("COUNTRY_CODE", this.getCountryCode());
                    final Row pkgToAppDataRow = this.pkgToAppDO.getRow("MdPackageToAppData", new Criteria(Column.getColumn("MdPackageToAppData", "STORE_ID"), (Object)adamId.toString(), 0));
                    final Long packageID = (Long)pkgToAppDataRow.get("PACKAGE_ID");
                    final Row pkgRow = this.pkgToAppDO.getRow("MdPackage", new Criteria(Column.getColumn("MdPackage", "PACKAGE_ID"), (Object)packageID, 0));
                    userId = (Long)pkgRow.get("PACKAGE_ADDED_BY");
                    final JSONObject appJSON = MDMAppMgmtHandler.getInstance().createiOSAppJson(singleAppDetails, this.customerId, userId, false);
                    appDetailsArray.put((Object)appJSON);
                }
                if (appDetailsArray.length() > 0) {
                    for (int i = 0; i < appDetailsArray.length(); ++i) {
                        final JSONObject appJSON2 = appDetailsArray.getJSONObject(i);
                        JSONObject packagePolicyJSON = appJSON2.optJSONObject("PackagePolicyForm");
                        if (packagePolicyJSON == null) {
                            packagePolicyJSON = new JSONObject();
                        }
                        final JSONObject packageToAppDataJSON = appJSON2.getJSONObject("MdPackageToAppDataFrom");
                        final String storeID = packageToAppDataJSON.get("STORE_ID").toString();
                        try {
                            final Row pkgToAppDataRow2 = this.pkgToAppDO.getRow("MdPackageToAppData", new Criteria(Column.getColumn("MdPackageToAppData", "STORE_ID"), (Object)storeID, 0));
                            final Long packageID2 = (Long)pkgToAppDataRow2.get("PACKAGE_ID");
                            final JSONObject policyJSON = AppsUtil.getInstance().getPackagePolicy(packageID2);
                            final Boolean preventBackup = policyJSON.optBoolean("prevent_backup", (boolean)Boolean.TRUE);
                            final Boolean removeAppWithProfile = policyJSON.optBoolean("remove_app_with_profile", (boolean)Boolean.TRUE);
                            final int oldSupportedDevices = (int)pkgToAppDataRow2.get("SUPPORTED_DEVICES");
                            final int newSupportedDevices = packageToAppDataJSON.getInt("SUPPORTED_DEVICES");
                            packageToAppDataJSON.put("SUPPORTED_DEVICES", newSupportedDevices | oldSupportedDevices);
                            packagePolicyJSON.put("REMOVE_APP_WITH_PROFILE", (Object)removeAppWithProfile);
                            packagePolicyJSON.put("PREVENT_BACKUP", (Object)preventBackup);
                            appJSON2.put("MdPackageToAppDataFrom", (Object)packageToAppDataJSON);
                            appJSON2.put("PackagePolicyForm", (Object)packagePolicyJSON);
                            appJSON2.put("doNotRestore", (Object)Boolean.TRUE);
                            MDMAppMgmtHandler.getInstance().addOrUpdatePackageInRepository(appJSON2);
                            if (appJSON2.has("updatedAppGroupId") && appJSON2.get("updatedAppGroupId") != null) {
                                this.updateAppGroupList.put((Object)Long.valueOf(appJSON2.get("updatedAppGroupId").toString()));
                            }
                        }
                        catch (final Exception e) {
                            this.logger.log(Level.SEVERE, "App with storeID : {0} failed to update in App repository. Exception {1}", new Object[] { storeID, e });
                        }
                    }
                }
            }
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "AppUpdateSync: Exception while syncApps() ", e2);
        }
        return this.resultJson;
    }
    
    public HashMap getAppUpdateDetails() {
        final List<String> adamIdList = this.getAdamIdForNonPortalApps();
        final List<List> storeList = MDMUtil.getInstance().splitListIntoSubLists(adamIdList, 300);
        final String countryCode = this.getCountryCode();
        HashMap updatedAppDetails = new HashMap();
        for (final List subAdamIdList : storeList) {
            final ContentMetaDataAPIHandler contentMetaDataAPIHandler = new ContentMetaDataAPIHandler();
            final HashMap appDetailsResponse = contentMetaDataAPIHandler.getNonVPPAppDetails(subAdamIdList, countryCode);
            updatedAppDetails = this.getUpdateAvailableApps(subAdamIdList, appDetailsResponse, updatedAppDetails);
        }
        return updatedAppDetails;
    }
    
    public List getAdamIdForNonPortalApps() {
        final List<String> adamIdList = new ArrayList<String>();
        try {
            final Iterator<Row> iterator = this.pkgToAppDO.getRows("MdPackageToAppGroup");
            while (iterator.hasNext()) {
                try {
                    final Row row = iterator.next();
                    final Long pkgId = (Long)row.get("PACKAGE_ID");
                    final String adamId = (String)this.pkgToAppDO.getRow("MdPackageToAppData", new Criteria(new Column("MdPackageToAppData", "PACKAGE_ID"), (Object)pkgId, 0)).get("STORE_ID");
                    adamIdList.add(adamId);
                }
                catch (final Exception ex) {
                    this.logger.log(Level.SEVERE, "AppUpdateSync: Exception while iterating rows to get adamID ", ex);
                }
            }
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "AppUpdateSync: Exception while getAdamIdForNonPortalApps() ", ex2);
        }
        return adamIdList;
    }
    
    public String getCountryCode() {
        String countryCode = AppsUtil.getInstance().getAppStoreRegionValue();
        if (MDMStringUtils.isEmpty(countryCode)) {
            countryCode = "US";
        }
        return countryCode;
    }
    
    public HashMap getUpdateAvailableApps(final List adamIdList, final HashMap appDetails, final HashMap newUpdatesHashMap) {
        try {
            for (int i = 0; i < adamIdList.size(); ++i) {
                final String adamId = adamIdList.get(i);
                JSONObject appVersionJSON = new JSONObject();
                if (appDetails.containsKey(Integer.parseInt(adamId))) {
                    final HashMap singleAppDetails = new HashMap();
                    final ContentMetaDataAppDetails appDetailsObject = appDetails.get(Integer.parseInt(adamId));
                    appVersionJSON = this.getAppVersionDetailsJSON(appDetailsObject);
                    final Long appGroupId = (Long)this.pkgToAppDO.getRow("MdPackageToAppData", new Criteria(new Column("MdPackageToAppData", "STORE_ID"), (Object)adamId, 0)).get("APP_GROUP_ID");
                    singleAppDetails.put("APP_GROUP_ID", appGroupId);
                    singleAppDetails.put("appDetailsObject", appDetailsObject);
                    if (AppsUtil.getInstance().isNewVersion(appGroupId, appVersionJSON, this.customerId, this.platformType)) {
                        newUpdatesHashMap.put(Integer.parseInt(adamId), singleAppDetails);
                    }
                    this.logger.log(Level.INFO, "AppUpdateSync: getUpdateAvailableApps() New update appGroupIDs={0}", appGroupId);
                }
                else {
                    this.logger.log(Level.SEVERE, "AppUpdateSync: getNewUpdateApps() App Response not getting for adam id" + adamId);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "AppUpdateSync: Exception while getUpdateAvailableApps() ", e);
        }
        return newUpdatesHashMap;
    }
    
    private JSONObject getAppVersionDetailsJSON(final ContentMetaDataAppDetails appDetailsObject) {
        final JSONObject versionDetailsJSON = new JSONObject();
        versionDetailsJSON.put("EXTERNAL_APP_VERSION_ID", (Object)appDetailsObject.getExternalAppVersionID());
        versionDetailsJSON.put("APP_VERSION", (Object)appDetailsObject.getAppVersion());
        return versionDetailsJSON;
    }
    
    public void setStoreAppsPkgToAppDO(final Criteria customCriteria) throws DataAccessException {
        final SelectQuery sq = MDMAppMgmtHandler.getInstance().getMDPackageAppDataQuery();
        final Criteria customerC = new Criteria(new Column("MdPackage", "CUSTOMER_ID"), (Object)this.customerId, 0);
        final Criteria platformC = new Criteria(new Column("MdPackage", "PLATFORM_TYPE"), (Object)this.platformType, 0);
        final Criteria pkgTypeC = new Criteria(new Column("MdPackageToAppGroup", "PACKAGE_TYPE"), (Object)new Integer[] { 0, 1 }, 8);
        Criteria c = null;
        if (sq.getCriteria() != null) {
            c = sq.getCriteria();
        }
        if (c == null) {
            c = customerC.and(platformC).and(pkgTypeC);
        }
        else {
            c = c.and(customerC.and(platformC).and(pkgTypeC));
        }
        if (customCriteria != null) {
            c = c.and(customCriteria);
        }
        sq.setCriteria(c);
        sq.addSelectColumn(Column.getColumn("MdPackage", "*"));
        sq.addSelectColumn(Column.getColumn("MdPackageToAppData", "*"));
        sq.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "*"));
        this.pkgToAppDO = MDMUtil.getPersistence().get(sq);
    }
    
    private void initializeResultJson() {
        this.resultJson = new JSONObject();
        this.updateAppGroupList = new JSONArray();
    }
    
    private String getAppStoreRegion(final Long userId) {
        if (userId != null) {
            return AppsUtil.getInstance().getAppStoreRegionValue(userId);
        }
        return "US";
    }
    
    public DataObject getpkgToAppDO() {
        return this.pkgToAppDO;
    }
    
    private Long getPackageFromAdamId(final String trackId) {
        Long packageId = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackage"));
            selectQuery.addJoin(new Join("MdPackage", "MdPackageToAppData", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
            final Criteria custCriteria = new Criteria(new Column("MdPackage", "CUSTOMER_ID"), (Object)this.customerId, 0);
            final Criteria storeId = new Criteria(new Column("MdPackageToAppData", "STORE_ID"), (Object)trackId, 0);
            final Criteria platFormCriteria = new Criteria(new Column("MdPackage", "PLATFORM_TYPE"), (Object)1, 0);
            selectQuery.addSelectColumn(new Column("MdPackage", "PACKAGE_ID"));
            selectQuery.addSortColumn(new SortColumn(Column.getColumn("MdPackage", "PACKAGE_ID"), (boolean)Boolean.TRUE));
            selectQuery.setCriteria(custCriteria.and(storeId).and(platFormCriteria));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("MdPackage");
                packageId = (Long)row.get("PACKAGE_ID");
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "issue on fetching package id from store id", (Throwable)e);
        }
        return packageId;
    }
}
