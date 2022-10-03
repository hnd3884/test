package com.me.mdm.server.apps;

import java.util.Set;
import com.me.mdm.server.tracker.mics.MICSAppRepositoryFeatureController;
import java.util.HashSet;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.ds.query.DMDataSetWrapper;
import java.util.Collection;
import com.me.mdm.server.apps.ios.vpp.VPPTokenDataHandler;
import com.me.mdm.server.windows.apps.WpAppSettingsHandler;
import org.json.JSONArray;
import org.apache.commons.lang3.StringUtils;
import com.adventnet.ds.query.UpdateQuery;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.adventnet.ds.query.UnionQuery;
import com.me.devicemanagement.framework.server.customgroup.CustomGroupUtil;
import java.sql.SQLException;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.UnionQueryImpl;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import com.adventnet.sym.server.mdm.apps.AppLicenseHandler;
import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import org.json.JSONException;
import java.util.Map;
import java.util.List;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import java.util.HashMap;
import java.util.ArrayList;
import org.json.JSONObject;
import java.util.logging.Logger;

public class AppTrashModeHandler
{
    public static final String RESTORE_COUNT = "APP_RESTORE_COUNT";
    public static final String ADD_AGAIN_COUNT = "APP_ADDED_AGAIN_COUNT";
    public static final String DELETE_PERMANENT_COUNT = "DELETE_PERMANENT_COUNT";
    public static Logger logger;
    private static AppTrashActionInterface appTrashAction;
    
    public AppTrashModeHandler() {
        AppTrashModeHandler.appTrashAction = new BaseAppTrashModeAction();
    }
    
    public JSONObject moveAppsToTrash(final String profileIDs, final Long customerId) throws JSONException {
        final JSONObject response = new JSONObject();
        try {
            final List<Long> profilesToBeDeleted = new ArrayList<Long>();
            final String[] split;
            final String[] sArrProfileID = split = profileIDs.split(",");
            for (final String sArrProfileID2 : split) {
                Map grpList = new HashMap();
                final Long profileID = Long.parseLong(sArrProfileID2);
                if (this.getAppTrashAction().isDeleteAllowed(profileID, response)) {
                    profilesToBeDeleted.add(profileID);
                    grpList = ProfileUtil.getInstance().getManagedGroupsAssignedForProfile(Long.parseLong(sArrProfileID2));
                    final HashMap grpParams = new HashMap();
                    grpParams.put("CustomerID", customerId);
                    grpParams.put("ProfileID", profileID);
                    grpParams.put("GroupList", grpList);
                    this.getAppTrashAction().performUninstallForGroup(grpParams);
                    grpList = ProfileUtil.getInstance().getManagedDevicesAssignedForProfile(Long.parseLong(sArrProfileID2));
                    if (!grpList.isEmpty()) {
                        final HashMap params = new HashMap();
                        params.put("CustomerID", customerId);
                        params.put("ProfileID", profileID);
                        params.put("ResourceList", grpList);
                        this.getAppTrashAction().performUninstallForResource(params);
                    }
                }
            }
            ProfileUtil.getInstance().markAsDeleted(profilesToBeDeleted, customerId);
            final Long currentlyLoggedInUserLoginId = MDMUtil.getInstance().getCurrentlyLoggedOnUserID();
            final String userName = DMUserHandler.getDCUser(currentlyLoggedInUserLoginId);
            final String appName = (String)DBUtil.getValueFromDB("Profile", "PROFILE_ID", (Object)new Long(sArrProfileID[0]), "PROFILE_NAME");
            String remarks = null;
            String remarksArgs = appName;
            if (sArrProfileID.length == 1) {
                remarks = "mdm.app.trash.single_app_to_trash";
            }
            else {
                remarks = "mdm.app.trash.multi_app_to_trash";
                remarksArgs = remarksArgs + "@@@" + (sArrProfileID.length - 1);
            }
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2037, null, userName, remarks, remarksArgs, customerId);
        }
        catch (final DataAccessException | QueryConstructionException e) {
            AppTrashModeHandler.logger.log(Level.WARNING, "Error while moving app to trash", e);
            response.put("Success", false);
            return response;
        }
        catch (final Exception e) {
            AppTrashModeHandler.logger.log(Level.WARNING, "Error while moving app to trash", e);
        }
        response.put("Success", true);
        return response;
    }
    
    AppTrashActionInterface getAppTrashAction() {
        return AppTrashModeHandler.appTrashAction;
    }
    
    public String deleteMultipleAppFromTrash(final HashMap param) throws Exception {
        String appStatusMessage = "Failed";
        final Long customerId = param.get("CustomerID");
        final List appGroupIds = param.get("appGroupIds");
        final List packageIds = param.get("packageIds");
        final List profileIds = param.get("profileIds");
        if (profileIds != null && packageIds != null && appGroupIds != null) {
            MDMAppMgmtHandler.getInstance().deletePackageDetails(packageIds.toArray(new Long[packageIds.size()]));
            MDMAppMgmtHandler.getInstance().deleteProfileforPackage(profileIds.toArray(new Long[profileIds.size()]), customerId);
            MDMAppMgmtHandler.getInstance().deleteAppRepositoryFiles(customerId, packageIds.toArray(new Long[packageIds.size()]), appGroupIds.toArray(new Long[appGroupIds.size()]));
            MDMAppMgmtHandler.getInstance().deleteAppAssignableDetails(appGroupIds.toArray(new Long[appGroupIds.size()]));
            MDMAppMgmtHandler.getInstance().deleteAppTrackDetails(appGroupIds.toArray(new Long[appGroupIds.size()]));
            MDMAppMgmtHandler.getInstance().deleteUpdateConfFromApp(customerId);
            appStatusMessage = "multiple_delete";
            for (final Object groupId : appGroupIds) {
                new AppLicenseHandler().deleteLicenseDetails((Long)groupId);
            }
            ProfileAssociateHandler.getInstance().updateDeviceProfileSummary();
        }
        final String permanentAppDeleteCnt = CustomerParamsHandler.getInstance().getParameterValue("DELETE_PERMANENT_COUNT", (long)customerId);
        final Long newCount = (permanentAppDeleteCnt != null) ? (Long.parseLong(permanentAppDeleteCnt) + profileIds.size()) : profileIds.size();
        CustomerParamsHandler.getInstance().addOrUpdateParameter("DELETE_PERMANENT_COUNT", newCount.toString(), (long)customerId);
        return appStatusMessage;
    }
    
    public boolean getDeletedAppPackageDetails(final Long appGrpID, final JSONObject jsonObject) {
        boolean isPackageFound = false;
        try {
            final SelectQuery appQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackageToAppGroup"));
            final Join grouppackageJoin = new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
            final Join packageAppJoin = new Join("MdPackageToAppGroup", "MdPackageToAppData", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2);
            final Join appCollectionJoin = new Join("MdPackageToAppData", "MdAppToCollection", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2);
            final Join collectionProfileJoin = new Join("MdAppToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
            final Join profileNameJoin = new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
            final Join profileCustomerJoin = new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
            final Criteria appGrpCriteria = new Criteria(new Column("MdPackageToAppData", "APP_GROUP_ID"), (Object)appGrpID, 0);
            final Criteria trashCriteria = new Criteria(new Column("Profile", "IS_MOVED_TO_TRASH"), (Object)true, 0);
            final Criteria combinecriteria = trashCriteria.and(appGrpCriteria);
            appQuery.addJoin(grouppackageJoin);
            appQuery.addJoin(packageAppJoin);
            appQuery.addJoin(appCollectionJoin);
            appQuery.addJoin(collectionProfileJoin);
            appQuery.addJoin(profileNameJoin);
            appQuery.addJoin(profileCustomerJoin);
            appQuery.setCriteria(combinecriteria);
            appQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "PACKAGE_ID"));
            appQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "APP_GROUP_ID"));
            appQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "APP_ID"));
            appQuery.addSelectColumn(Column.getColumn("MdAppToCollection", "COLLECTION_ID"));
            appQuery.addSelectColumn(Column.getColumn("MdAppToCollection", "APP_ID"));
            appQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
            final DataObject resObj = MDMUtil.getPersistence().get(appQuery);
            if (!resObj.isEmpty()) {
                final Row r = resObj.getFirstRow("MdPackageToAppData");
                final Long packageID = (Long)r.get("PACKAGE_ID");
                if (packageID != null && packageID != -1L) {
                    isPackageFound = true;
                    final Row profRow = resObj.getFirstRow("Profile");
                    jsonObject.put("PACKAGE_ID", (Object)packageID);
                    jsonObject.put("PROFILE_ID", (Object)profRow.get("PROFILE_ID"));
                }
            }
        }
        catch (final Exception ex) {
            AppTrashModeHandler.logger.log(Level.WARNING, "Exception occoured in getAppProfileName....", ex);
        }
        return isPackageFound;
    }
    
    public int getKioskPolicyApps(final List<Long> appGroupIds) throws SQLException {
        int count = 0;
        final SelectQuery androidKioskSelect = (SelectQuery)new SelectQueryImpl(new Table("AndroidKioskPolicyApps"));
        final Join androidJoin = new Join("AndroidKioskPolicyApps", "AndroidKioskPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2);
        androidKioskSelect.addJoin(androidJoin);
        androidKioskSelect.addJoin(new Join("AndroidKioskPolicyApps", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        androidKioskSelect.addJoin(new Join("MdAppGroupDetails", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        androidKioskSelect.addJoin(new Join("MdPackageToAppGroup", "MdPackage", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        androidKioskSelect.addSelectColumn(Column.getColumn("MdAppGroupDetails", "IDENTIFIER").distinct());
        Criteria appCriteria = new Criteria(Column.getColumn("AndroidKioskPolicyApps", "APP_GROUP_ID"), (Object)appGroupIds.toArray(), 8);
        final Criteria allCustomerAppCriteria = new Criteria(Column.getColumn("MdPackage", "APP_SHARED_SCOPE"), (Object)1, 0).and(new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)AppsUtil.getDerivedColumnOfAppIdentifiersForGivenAppGroupIds(appGroupIds), 8));
        appCriteria = appCriteria.or(allCustomerAppCriteria);
        final Criteria policyCriteria = new Criteria(Column.getColumn("AndroidKioskPolicy", "KIOSK_MODE"), (Object)0, 0);
        androidKioskSelect.setCriteria(appCriteria.and(policyCriteria));
        final SelectQuery ioKioskSelect = (SelectQuery)new SelectQueryImpl(new Table("AppLockPolicyApps"));
        final Join iosJoin = new Join("AppLockPolicyApps", "AppLockPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2);
        ioKioskSelect.addJoin(iosJoin);
        ioKioskSelect.addJoin(new Join("AppLockPolicyApps", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        ioKioskSelect.addJoin(new Join("MdAppGroupDetails", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        ioKioskSelect.addJoin(new Join("MdPackageToAppGroup", "MdPackage", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        ioKioskSelect.addSelectColumn(Column.getColumn("MdAppGroupDetails", "IDENTIFIER").distinct());
        Criteria criteria1 = new Criteria(Column.getColumn("AppLockPolicyApps", "APP_GROUP_ID"), (Object)appGroupIds.toArray(), 8);
        criteria1 = criteria1.or(allCustomerAppCriteria);
        final Criteria iospolicyCriteria = new Criteria(Column.getColumn("AppLockPolicy", "KIOSK_MODE"), (Object)1, 0);
        ioKioskSelect.setCriteria(criteria1.and(iospolicyCriteria));
        final UnionQuery unionQuery = (UnionQuery)new UnionQueryImpl((Query)androidKioskSelect, (Query)ioKioskSelect, false);
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection conn = null;
        DataSet ds = null;
        try {
            conn = relapi.getConnection();
            ds = relapi.executeQuery((Query)unionQuery, conn);
            while (ds.next()) {
                ++count;
            }
        }
        catch (final SQLException e) {
            AppTrashModeHandler.logger.log(Level.WARNING, "Exception in determining kiosk policy present", e);
        }
        catch (final QueryConstructionException e2) {
            AppTrashModeHandler.logger.log(Level.WARNING, "Exception in determining kiosk policy present", (Throwable)e2);
        }
        finally {
            CustomGroupUtil.getInstance().closeConnection(conn, ds);
        }
        return count;
    }
    
    public int getAppConfigPolicyApps(final List appGroupIDs, final boolean includeTrashedProfiles) throws DataAccessException, Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
        selectQuery.addJoin(new Join("Profile", "RecentProfileToColln", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("RecentProfileToColln", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        selectQuery.addJoin(new Join("ConfigDataItem", "ManagedAppConfigurationPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedAppConfigurationPolicy", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppGroupDetails", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdPackage", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        final Criteria profileTrashCriteria = new Criteria(new Column("Profile", "IS_MOVED_TO_TRASH"), (Object)false, 0);
        Criteria appCriteria = new Criteria(new Column("ManagedAppConfigurationPolicy", "APP_GROUP_ID"), (Object)appGroupIDs.toArray(), 8);
        final Criteria allCustomerAppCriteria = new Criteria(Column.getColumn("MdPackage", "APP_SHARED_SCOPE"), (Object)1, 0).and(new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)AppsUtil.getDerivedColumnOfAppIdentifiersForGivenAppGroupIds(appGroupIDs), 8));
        appCriteria = appCriteria.or(allCustomerAppCriteria);
        final Criteria criteria = includeTrashedProfiles ? appCriteria : appCriteria.and(profileTrashCriteria);
        selectQuery.setCriteria(criteria);
        final int count = DBUtil.getRecordCount(selectQuery, "MdAppGroupDetails", "IDENTIFIER");
        return count;
    }
    
    public boolean isAccountApp(final Long appID) throws DataAccessException {
        boolean isAccountApp = false;
        final List list = new ArrayList();
        list.add(appID);
        isAccountApp = this.isAccountApp(list, null);
        return isAccountApp;
    }
    
    public boolean isAccountApp(final List appGroupIdList, final Long platformType) throws DataAccessException {
        boolean isAccountApp = false;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppToGroupRel"));
        final Join packageJoin = new Join("MdAppToGroupRel", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
        final Join packageAppJoin = new Join("MdPackageToAppGroup", "MdPackage", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2);
        final Criteria appGroupIDCriteria = new Criteria(new Column("MdAppToGroupRel", "APP_GROUP_ID"), (Object)appGroupIdList.toArray(), 8);
        final Criteria portalCriteria = new Criteria(new Column("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), (Object)true, 0);
        Criteria combinedCriteria = appGroupIDCriteria.and(portalCriteria);
        if (platformType != null) {
            final Criteria platformCriteria = new Criteria(new Column("MdPackage", "PLATFORM_TYPE"), (Object)platformType, 0);
            combinedCriteria = combinedCriteria.and(platformCriteria);
        }
        selectQuery.addJoin(packageJoin);
        selectQuery.addJoin(packageAppJoin);
        selectQuery.setCriteria(combinedCriteria);
        selectQuery.addSelectColumn(new Column("MdAppToGroupRel", "*"));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        isAccountApp = !dataObject.isEmpty();
        return isAccountApp;
    }
    
    public boolean isAppMovedToTrash(final Long profileID) throws Exception {
        boolean isAppMovedToTrash = false;
        isAppMovedToTrash = (boolean)DBUtil.getValueFromDB("Profile", "PROFILE_ID", (Object)profileID, "IS_MOVED_TO_TRASH");
        return isAppMovedToTrash;
    }
    
    private SelectQuery getAppInTrashQuery() {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("AppGroupToCollection"));
        query.addJoin(new Join("AppGroupToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        query.addJoin(new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        query.addJoin(new Join("AppGroupToCollection", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        query.addJoin(new Join("MdAppGroupDetails", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        final Criteria trashModeCriteria = new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)true, 0);
        query.setCriteria(trashModeCriteria);
        return query;
    }
    
    public boolean isAppGroupIdMovedToTrash(final Long appGroupId) {
        try {
            if (appGroupId == null) {
                return false;
            }
            boolean isAppMovedToTrash = false;
            final SelectQuery query = this.getAppInTrashQuery();
            final Criteria appGroupIDCriteria = new Criteria(Column.getColumn("AppGroupToCollection", "APP_GROUP_ID"), (Object)appGroupId, 0);
            query.setCriteria(query.getCriteria().and(appGroupIDCriteria));
            query.addSelectColumn(Column.getColumn("AppGroupToCollection", "APP_GROUP_ID").distinct().count());
            isAppMovedToTrash = (DBUtil.getRecordCount(query) >= 1);
            return isAppMovedToTrash;
        }
        catch (final Exception e) {
            AppTrashModeHandler.logger.log(Level.SEVERE, "error in isAppGroupIdMovedToTrash()", e);
            return false;
        }
    }
    
    public boolean isAppInTrash(final String bundleID, final Long customerId) {
        try {
            if (bundleID == null) {
                return false;
            }
            boolean isAppMovedToTrash = false;
            final SelectQuery query = this.getAppInTrashQuery();
            final Criteria bundleIDCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)bundleID, 0);
            query.setCriteria(query.getCriteria().and(bundleIDCriteria));
            query.addSelectColumn(Column.getColumn("AppGroupToCollection", "APP_GROUP_ID").distinct().count());
            isAppMovedToTrash = (DBUtil.getRecordCount(query) >= 1);
            return isAppMovedToTrash;
        }
        catch (final Exception e) {
            AppTrashModeHandler.logger.log(Level.SEVERE, "error in isAppInTrash()", e);
            return false;
        }
    }
    
    public String isAppAssociated(final HashMap param) throws DataAccessException, JSONException {
        String appStatusMessage = "Success";
        final Long customerId = param.get("CustomerID");
        final List appGroupIds = param.get("appGroupIds");
        final List packageIds = param.get("packageIds");
        final List profileIds = param.get("profileIds");
        final JSONObject status = new JSONObject();
        if (appGroupIds != null && packageIds != null && profileIds != null) {
            final boolean isAppDeleteSafe = MDMAppMgmtHandler.getInstance().isAppDeleteSafe(appGroupIds, Boolean.FALSE);
            final boolean isAppCatalog = AppsUtil.getInstance().checkIfAppCatalog(appGroupIds.toArray(new Long[appGroupIds.size()]));
            boolean isAppCatalogDeleteSafe = true;
            if (isAppCatalog) {
                isAppCatalogDeleteSafe = AppsUtil.getInstance().checkIfAppCatalogDeleteSafe(appGroupIds.toArray(new Long[appGroupIds.size()]), customerId);
            }
            if (!isAppCatalogDeleteSafe) {
                final String appName = AppsUtil.getInstance().getAppCatalogAppName(appGroupIds.toArray(new Long[appGroupIds.size()]));
                status.put("appName", (Object)appName);
                appStatusMessage = "me_mdm_not_safe";
            }
            else if (!isAppDeleteSafe) {
                final boolean isPassiveDeleteSafe = MDMAppMgmtHandler.getInstance().isAppDeleteSafe(appGroupIds, Boolean.TRUE);
                if (!isPassiveDeleteSafe) {
                    appStatusMessage = "profile_delete_not_safe";
                }
                else {
                    appStatusMessage = "profile_passive_delete_safe";
                }
            }
        }
        return appStatusMessage;
    }
    
    public List getSingleAppKioskProfiles(final Long appProfileID) throws DataAccessException {
        final List profileList = new ArrayList();
        profileList.add(appProfileID);
        final List kioskProfileList = this.getSingleAppKioskProfiles(profileList);
        return kioskProfileList;
    }
    
    public List getSingleAppKioskProfiles(final List appProfileIds) throws DataAccessException {
        final List profileIds = new ArrayList();
        final List collnIds = new ArrayList();
        final SelectQuery androidSelectQuery = (SelectQuery)new SelectQueryImpl(new Table("Profile"));
        final Join profileToCollectionJoin = new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
        final Join collnToAppJoin = new Join("ProfileToCollection", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
        final Join apptoGrpJoin = new Join("MdAppToCollection", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2);
        final Join AnroidKioskJoin = new Join("MdAppToGroupRel", "AndroidKioskPolicyApps", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
        final Join AnroidKioskPolicyJoin = new Join("AndroidKioskPolicyApps", "AndroidKioskPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2);
        final Join cfgDataJoin = new Join("AndroidKioskPolicy", "ConfigDataItem", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2);
        final Join cfgdataToColln = new Join("ConfigDataItem", "CfgDataToCollection", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2);
        final Join recentCollectionJoin = new Join("CfgDataToCollection", "RecentProfileToColln", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
        final Criteria criteria = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)appProfileIds.toArray(), 8);
        final Criteria Singlecriteria = new Criteria(Column.getColumn("AndroidKioskPolicy", "KIOSK_MODE"), (Object)0, 0);
        androidSelectQuery.addSelectColumn(Column.getColumn("RecentProfileToColln", "*"));
        androidSelectQuery.setCriteria(criteria.and(Singlecriteria));
        androidSelectQuery.addJoin(profileToCollectionJoin);
        androidSelectQuery.addJoin(collnToAppJoin);
        androidSelectQuery.addJoin(apptoGrpJoin);
        androidSelectQuery.addJoin(AnroidKioskJoin);
        androidSelectQuery.addJoin(AnroidKioskPolicyJoin);
        androidSelectQuery.addJoin(cfgDataJoin);
        androidSelectQuery.addJoin(cfgdataToColln);
        androidSelectQuery.addJoin(recentCollectionJoin);
        final DataObject dataObject = MDMUtil.getPersistence().get(androidSelectQuery);
        final Iterator iterator = dataObject.getRows("RecentProfileToColln");
        while (iterator.hasNext()) {
            collnIds.add(iterator.next().get("COLLECTION_ID"));
        }
        final SelectQuery iOSSelectQuery = (SelectQuery)new SelectQueryImpl(new Table("Profile"));
        final Join IOsKioskJoin = new Join("MdAppToGroupRel", "AppLockPolicyApps", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
        final Join IosKioskPolicyJoin = new Join("AppLockPolicyApps", "AppLockPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2);
        final Join IoscfgDataJoin = new Join("AppLockPolicy", "ConfigDataItem", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2);
        final Criteria SingleIoscriteria = new Criteria(Column.getColumn("AppLockPolicy", "KIOSK_MODE"), (Object)1, 0);
        iOSSelectQuery.addSelectColumn(Column.getColumn("RecentProfileToColln", "*"));
        iOSSelectQuery.setCriteria(criteria.and(SingleIoscriteria));
        iOSSelectQuery.addJoin(profileToCollectionJoin);
        iOSSelectQuery.addJoin(collnToAppJoin);
        iOSSelectQuery.addJoin(apptoGrpJoin);
        iOSSelectQuery.addJoin(IOsKioskJoin);
        iOSSelectQuery.addJoin(IosKioskPolicyJoin);
        iOSSelectQuery.addJoin(IoscfgDataJoin);
        iOSSelectQuery.addJoin(cfgdataToColln);
        iOSSelectQuery.addJoin(recentCollectionJoin);
        final DataObject IosdataObject = MDMUtil.getPersistence().get(iOSSelectQuery);
        final Iterator IosIterator = IosdataObject.getRows("RecentProfileToColln");
        while (IosIterator.hasNext()) {
            collnIds.add(IosIterator.next().get("COLLECTION_ID"));
        }
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ProfileToCollection"));
        selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "*"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("ProfileToCollection", "COLLECTION_ID"), (Object)collnIds.toArray(), 8));
        final DataObject profileDo = MDMUtil.getPersistence().get(selectQuery);
        final Iterator itr = profileDo.getRows("ProfileToCollection");
        while (itr.hasNext()) {
            final Long profile = (Long)itr.next().get("PROFILE_ID");
            if (!profileIds.contains(profile)) {
                profileIds.add(profile);
            }
        }
        return profileIds;
    }
    
    public void restoreAppFromTrash(final List profileIds) throws Exception {
        final UpdateQuery query = (UpdateQuery)new UpdateQueryImpl("Profile");
        final Criteria criteria = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileIds.toArray(), 8);
        query.setCriteria(criteria);
        query.setUpdateColumn("IS_MOVED_TO_TRASH", (Object)Boolean.FALSE);
        query.setUpdateColumn("LAST_MODIFIED_TIME", (Object)System.currentTimeMillis());
        final Long currentlyLoggedInUserLoginId = MDMUtil.getInstance().getCurrentlyLoggedOnUserID();
        query.setUpdateColumn("LAST_MODIFIED_BY", (Object)currentlyLoggedInUserLoginId);
        MDMUtil.getPersistence().update(query);
        final Long customerId = CustomerInfoUtil.getInstance().getCustomerId();
        final String restoreCnt = CustomerParamsHandler.getInstance().getParameterValue("APP_RESTORE_COUNT", (long)customerId);
        final Long newCount = (restoreCnt != null) ? (Long.parseLong(restoreCnt) + profileIds.size()) : profileIds.size();
        CustomerParamsHandler.getInstance().addOrUpdateParameter("APP_RESTORE_COUNT", newCount.toString(), (long)customerId);
        final String userName = DMUserHandler.getDCUser(currentlyLoggedInUserLoginId);
        final String appName = (String)DBUtil.getValueFromDB("Profile", "PROFILE_ID", (Object)new Long(profileIds.get(0)), "PROFILE_NAME");
        String remarks = null;
        String remarksArgs = appName;
        if (profileIds.size() == 1) {
            remarks = "mdm.app.trash.single_app_restore";
        }
        else {
            remarks = "mdm.app.trash.multi_app_restore";
            remarksArgs = remarksArgs + "@@@" + (profileIds.size() - 1);
        }
        MDMEventLogHandler.getInstance().MDMEventLogEntry(2037, null, userName, remarks, remarksArgs, customerId);
    }
    
    public void incrementTrashAppAddCount(final Long customerID) {
        try {
            final String restoreCnt = CustomerParamsHandler.getInstance().getParameterValue("APP_ADDED_AGAIN_COUNT", (long)customerID);
            final Long newCount = (restoreCnt != null) ? (Long.parseLong(restoreCnt) + 1L) : 1L;
            CustomerParamsHandler.getInstance().addOrUpdateParameter("APP_ADDED_AGAIN_COUNT", newCount.toString(), (long)customerID);
        }
        catch (final Exception e) {
            AppTrashModeHandler.logger.log(Level.SEVERE, "Excepetion in changing trash param for tracking ", e);
        }
    }
    
    public boolean hasNonAccountApps(final List appIDs) throws DataAccessException {
        boolean isAccountApp = false;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppToGroupRel"));
        final Join packageJoin = new Join("MdAppToGroupRel", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
        final Join packageAppJoin = new Join("MdPackageToAppGroup", "MdPackage", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2);
        final Criteria appIDCriteria = new Criteria(new Column("MdAppToGroupRel", "APP_GROUP_ID"), (Object)appIDs.toArray(), 8);
        final Criteria portalCriteria = new Criteria(new Column("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), (Object)false, 0);
        final Criteria combinedCriteria = appIDCriteria.and(portalCriteria);
        selectQuery.addJoin(packageJoin);
        selectQuery.addJoin(packageAppJoin);
        selectQuery.setCriteria(combinedCriteria);
        selectQuery.addSelectColumn(new Column("MdAppToGroupRel", "*"));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        isAccountApp = !dataObject.isEmpty();
        return isAccountApp;
    }
    
    public int getAppTrashCount(final Long customerID) throws DataAccessException {
        int count = 0;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Profile"));
        selectQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        final Criteria AppTypeCri = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)2, 0);
        final Criteria trashCriteria = new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)true, 0);
        final Criteria customerCriteria = new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0);
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
        selectQuery.setCriteria(AppTypeCri.and(trashCriteria).and(customerCriteria));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        count = dataObject.size("Profile");
        return count;
    }
    
    public List getAppGroupsInTrash(final Long customerId) throws DataAccessException {
        final List appGrpIDs = new ArrayList();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Profile"));
        final Join profileToCollectionJoin = new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
        final Join collnToAppJoin = new Join("ProfileToCollection", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
        final Join apptoGrpJoin = new Join("MdAppToCollection", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2);
        final Join appgroupJoin = new Join("MdAppToGroupRel", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
        selectQuery.addJoin(profileToCollectionJoin);
        selectQuery.addJoin(collnToAppJoin);
        selectQuery.addJoin(apptoGrpJoin);
        selectQuery.addJoin(appgroupJoin);
        selectQuery.addSelectColumn(Column.getColumn("MdAppToGroupRel", "*"));
        final Criteria AppTypeCri = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)2, 0);
        final Criteria trashCriteria = new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)true, 0);
        final Criteria custCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        selectQuery.setCriteria(AppTypeCri.and(trashCriteria).and(custCriteria));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        final Iterator iterator = dataObject.getRows("MdAppToGroupRel");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final Long appGrpID = (Long)row.get("APP_GROUP_ID");
            if (!appGrpIDs.contains(appGrpID)) {
                appGrpIDs.add(appGrpID);
            }
        }
        return appGrpIDs;
    }
    
    public void moveAppsToTrash(final List appGroupIds, final Long customerID) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AppGroupToCollection"));
        selectQuery.addJoin(new Join("AppGroupToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.setCriteria(new Criteria(Column.getColumn("AppGroupToCollection", "APP_GROUP_ID"), (Object)appGroupIds.toArray(), 8));
        selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "*"));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        final Iterator iterator = dataObject.getRows("ProfileToCollection");
        final List profileList = new ArrayList();
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final Long profileID = (Long)row.get("PROFILE_ID");
            if (!profileList.contains(profileID)) {
                profileList.add(profileID);
            }
        }
        final String profileIDs = StringUtils.join((Iterable)profileList, ',');
        this.moveAppsToTrash(profileIDs, customerID);
    }
    
    public JSONObject allowRestore(final Long customerID, final List appGroupIds) throws Exception {
        Boolean isRestoreAllowed = Boolean.TRUE;
        final JSONObject result = new JSONObject();
        final JSONArray errorMsgArray = new JSONArray();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdPackage"));
        selectQuery.addJoin(new Join("MdPackage", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        final Criteria customerCriteria = new Criteria(Column.getColumn("MdPackage", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria appGroupCriteria = new Criteria(Column.getColumn("MdPackageToAppGroup", "APP_GROUP_ID"), (Object)appGroupIds.toArray(), 8);
        selectQuery.setCriteria(customerCriteria.and(appGroupCriteria));
        selectQuery.addSelectColumn(Column.getColumn("MdPackage", "*"));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "*"));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        final Criteria winPlatformCriteria = new Criteria(Column.getColumn("MdPackage", "PLATFORM_TYPE"), (Object)3, 0);
        final Iterator winIter = dataObject.getRows("MdPackageToAppGroup", winPlatformCriteria);
        while (winIter.hasNext()) {
            final Row winAppRow = winIter.next();
            if ((boolean)winAppRow.get("IS_PURCHASED_FROM_PORTAL") && !WpAppSettingsHandler.getInstance().isBstoreConfigured(customerID)) {
                isRestoreAllowed = Boolean.FALSE;
                errorMsgArray.put((Object)"Windows Bstore not configured");
            }
        }
        final Criteria iosPlatformCriteria = new Criteria(Column.getColumn("MdPackage", "PLATFORM_TYPE"), (Object)1, 0);
        final Iterator iosIter = dataObject.getRows("MdPackageToAppGroup", iosPlatformCriteria);
        if (iosIter.hasNext()) {
            final Boolean isVPPConfigured = VPPTokenDataHandler.getInstance().isVppTokenConfigured(customerID);
            if (!isVPPConfigured) {
                while (iosIter.hasNext()) {
                    final Row iosAppRow = iosIter.next();
                    final Integer packageType = (Integer)iosAppRow.get("PACKAGE_TYPE");
                    if (!(boolean)iosAppRow.get("IS_PURCHASED_FROM_PORTAL") && packageType != 2) {
                        errorMsgArray.put((Object)"Purchase app in VPP");
                        isRestoreAllowed = Boolean.FALSE;
                    }
                    else {
                        if (!(boolean)iosAppRow.get("IS_PAID_APP")) {
                            continue;
                        }
                        errorMsgArray.put((Object)"Paid apps need to be purchased in VPP");
                        isRestoreAllowed = Boolean.FALSE;
                    }
                }
            }
        }
        result.put("errorMsg", (Object)errorMsgArray);
        result.put("allowRestore", (Object)isRestoreAllowed);
        return result;
    }
    
    public List hasOnlyBStoreApps(final Long customerID, final List appGroupIds) throws DataAccessException {
        final List tempAppGroupIdList = new ArrayList(appGroupIds);
        tempAppGroupIdList.removeAll(WpAppSettingsHandler.getInstance().getBStoreAppGroupIds(customerID));
        return tempAppGroupIdList;
    }
    
    public int getAccountAppsInTrash(final int platform, final Long customerID) {
        int count = 0;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdPackageToAppGroup"));
        selectQuery.addJoin(new Join("MdPackageToAppGroup", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("AppGroupToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        final Criteria customerCriteria = new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria accountCriteria = new Criteria(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), (Object)true, 0);
        final Criteria platformCriteria = new Criteria(Column.getColumn("Profile", "PLATFORM_TYPE"), (Object)platform, 0);
        final Criteria trashCriteria = new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)true, 0);
        selectQuery.setCriteria(customerCriteria.and(accountCriteria).and(platformCriteria).and(trashCriteria));
        final Column countcol = new Column("Profile", "PROFILE_ID", "trashCount").distinct().count();
        countcol.setColumnAlias("trashCount");
        selectQuery.addSelectColumn(countcol);
        DMDataSetWrapper ds = null;
        try {
            ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
            if (ds.next()) {
                count = (int)ds.getValue("trashCount");
            }
        }
        catch (final Exception e) {
            AppTrashModeHandler.logger.log(Level.WARNING, "Exception in getingtrashed count", e);
        }
        return count;
    }
    
    public JSONObject checkMoveAppsToTrashFesability(final HashMap params) throws Exception {
        final JSONArray errorArray = new JSONArray();
        final List appGroupIds = params.get("appGroupIds");
        final int kioskCount = new AppTrashModeHandler().getKioskPolicyApps(appGroupIds);
        final int appConfigPolicyApps = this.getAppConfigPolicyApps(appGroupIds, false);
        final String status = new AppTrashModeHandler().isAppAssociated(params);
        if (status.equalsIgnoreCase("me_mdm_not_safe") && appGroupIds.size() == 1) {
            errorArray.put((Object)"me_mdm_not_safe");
        }
        else if (status.equalsIgnoreCase("me_mdm_not_safe")) {
            errorArray.put((Object)"me_mdm_other_apps_not_safe");
        }
        if (appConfigPolicyApps == appGroupIds.size()) {
            errorArray.put((Object)"app_config_not_safe");
        }
        else if (appConfigPolicyApps > 0) {
            errorArray.put((Object)"app_config_other_app_not_safe");
        }
        if (kioskCount == appGroupIds.size()) {
            errorArray.put((Object)"kiosk_not_safe");
        }
        else if (kioskCount > 0) {
            errorArray.put((Object)"kiosk_other_app_not_safe");
        }
        final JSONObject message = new JSONObject();
        if (errorArray.length() > 0) {
            message.put("ErrorMessage", (Object)errorArray);
        }
        return message;
    }
    
    public boolean isAppGroupDeleteSafe(final List appgroupList) {
        boolean isAppGroupDeleteSafe = false;
        try {
            final SelectQuery appGroupDeleteDafe = (SelectQuery)new SelectQueryImpl(Table.getTable("AppGroupToCollection"));
            final Join appgroupprofileJoin = new Join("AppGroupToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
            final Join profileResRelJoin = new Join("ProfileToCollection", "RecentProfileForResource", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
            final Join managedDeviceJoin = new Join("RecentProfileForResource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            final Join profileJoin = new Join("RecentProfileForResource", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
            appGroupDeleteDafe.addJoin(appgroupprofileJoin);
            appGroupDeleteDafe.addJoin(profileResRelJoin);
            appGroupDeleteDafe.addJoin(managedDeviceJoin);
            appGroupDeleteDafe.addJoin(profileJoin);
            appGroupDeleteDafe.addSelectColumn(Column.getColumn("AppGroupToCollection", "*"));
            final Criteria profileCriteria = new Criteria(new Column("AppGroupToCollection", "APP_GROUP_ID"), (Object)appgroupList.toArray(), 8);
            final Criteria platformCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)Column.getColumn("Profile", "PLATFORM_TYPE"), 0);
            appGroupDeleteDafe.setCriteria(profileCriteria.and(ManagedDeviceHandler.getInstance().getSuccessfullyEnrolledCriteria()).and(platformCriteria));
            final DataObject dObj = MDMUtil.getPersistence().get(appGroupDeleteDafe);
            if (dObj.isEmpty()) {
                AppTrashModeHandler.logger.log(Level.INFO, "App group is delete safe.AppGroup:{0}", new Object[] { appgroupList });
                isAppGroupDeleteSafe = true;
            }
        }
        catch (final Exception ex) {
            AppTrashModeHandler.logger.log(Level.WARNING, "Exception in isAppGroupDeleteSafe...", ex);
        }
        return isAppGroupDeleteSafe;
    }
    
    public int getAppGroupsMovedToTrash(final List<Long> appGroupId, final boolean flag) {
        try {
            if (appGroupId == null) {
                return 0;
            }
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("InstallAppPolicy"));
            query.addJoin(new Join("InstallAppPolicy", "ConfigDataItem", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
            query.addJoin(new Join("ConfigDataItem", "CfgDataToCollection", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            query.addJoin(new Join("CfgDataToCollection", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            query.addJoin(new Join("AppGroupToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            query.addJoin(new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            final Criteria appGroupIDCriteria = new Criteria(Column.getColumn("InstallAppPolicy", "APP_GROUP_ID"), (Object)appGroupId.toArray(), 8).and(new Criteria(Column.getColumn("AppGroupToCollection", "APP_GROUP_ID"), (Object)appGroupId.toArray(), 8));
            final Criteria trashModeCriteria = new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)flag, 0);
            final Criteria profileTypeCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)2, 0);
            query.setCriteria(appGroupIDCriteria.and(trashModeCriteria).and(profileTypeCriteria));
            query.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID").distinct().count());
            return DBUtil.getRecordCount(query);
        }
        catch (final Exception e) {
            AppTrashModeHandler.logger.log(Level.SEVERE, "error in isAppGroupIdMovedToTrash()", e);
            return 0;
        }
    }
    
    public void updateForcedAppRemovalStatus(final HashMap params) {
        try {
            final List appGroupList = params.get("appGroupIds");
            final Long customerId = params.get("CustomerID");
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("MdInstalledAppResourceRel");
            updateQuery.addJoin(new Join("MdInstalledAppResourceRel", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
            updateQuery.addJoin(new Join("MdAppDetails", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
            Criteria appGroupIdCriteria = new Criteria(new Column("MdAppToGroupRel", "APP_GROUP_ID"), (Object)appGroupList.toArray(), 8);
            Criteria customerCriteria = new Criteria(new Column("MdAppDetails", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria distributedByMDM = new Criteria(new Column("MdInstalledAppResourceRel", "USER_INSTALLED_APPS"), (Object)1, 0);
            updateQuery.setCriteria(appGroupIdCriteria.and(customerCriteria).and(distributedByMDM));
            updateQuery.setUpdateColumn("USER_INSTALLED_APPS", (Object)1);
            MDMUtil.getPersistence().update(updateQuery);
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("MdAppCatalogToResource");
            deleteQuery.addJoin(new Join("MdAppCatalogToResource", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            appGroupIdCriteria = new Criteria(new Column("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroupList.toArray(), 8);
            customerCriteria = new Criteria(new Column("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
            deleteQuery.setCriteria(appGroupIdCriteria.and(customerCriteria));
            MDMUtil.getPersistence().delete(deleteQuery);
        }
        catch (final Exception e) {
            AppTrashModeHandler.logger.log(Level.WARNING, "error in updateForcedAppRemovalStatus()", e);
        }
    }
    
    public Map<Long, String> getAppNames(final List appGroupList) {
        final Map<Long, String> appNames = new HashMap<Long, String>();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
            selectQuery.addSelectColumn(new Column("MdAppGroupDetails", "APP_GROUP_ID"));
            selectQuery.addSelectColumn(new Column("MdAppGroupDetails", "GROUP_DISPLAY_NAME"));
            selectQuery.setCriteria(new Criteria(new Column("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroupList.toArray(), 8));
            final DataObject dObj = MDMUtil.getPersistence().get(selectQuery);
            if (dObj != null && !dObj.isEmpty()) {
                final Iterator<Row> iterator = dObj.getRows("MdAppGroupDetails");
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    appNames.put((Long)row.get("APP_GROUP_ID"), (String)row.get("GROUP_DISPLAY_NAME"));
                }
            }
        }
        catch (final Exception e) {
            AppTrashModeHandler.logger.log(Level.WARNING, "error in getAppNames", e);
        }
        return appNames;
    }
    
    public void validateIfPackageInTrash(final List<Long> packageIds) {
        final SelectQuery selectQuery = this.getAppInTrashQuery();
        selectQuery.setCriteria(selectQuery.getCriteria().and(new Criteria(new Column("MdPackageToAppGroup", "PACKAGE_ID"), (Object)packageIds.toArray(), 8)));
        selectQuery.addSelectColumn(new Column("MdPackageToAppGroup", "PACKAGE_ID"));
        selectQuery.addSelectColumn(new Column("MdPackageToAppGroup", "APP_GROUP_ID"));
        try {
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                throw new APIHTTPException("COM0015", new Object[] { "Provided App(s) in trash" });
            }
        }
        catch (final DataAccessException e) {
            AppTrashModeHandler.logger.log(Level.WARNING, "Cannot validate if package in trash", (Throwable)e);
        }
    }
    
    public void softDeleteApps(final Long[] packageIds, final Long customerId, final String userName, final Boolean addAuditLog) {
        final Map<Long, String> packageToIdentifier = new HashMap<Long, String>();
        final Set<Long> appGroupSet = new HashSet<Long>();
        final Set<Long> profileSet = new HashSet<Long>();
        final Set<String> profileNameSet = new HashSet<String>();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
            selectQuery.addJoin(new Join("MdAppGroupDetails", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            selectQuery.addJoin(new Join("MdAppGroupDetails", "MdPackageToAppData", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
            selectQuery.addJoin(new Join("MdPackageToAppData", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
            selectQuery.addJoin(new Join("AppGroupToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 1));
            selectQuery.addJoin(new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 1));
            selectQuery.addSelectColumn(new Column("MdAppGroupDetails", "APP_GROUP_ID"));
            selectQuery.addSelectColumn(new Column("MdAppGroupDetails", "IDENTIFIER"));
            selectQuery.addSelectColumn(new Column("MdAppGroupDetails", "PLATFORM_TYPE"));
            selectQuery.addSelectColumn(new Column("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"));
            selectQuery.addSelectColumn(new Column("MdPackageToAppGroup", "PACKAGE_ID"));
            selectQuery.addSelectColumn(new Column("MdPackageToAppGroup", "PACKAGE_TYPE"));
            selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "APP_FILE_LOC"));
            selectQuery.addSelectColumn(new Column("Profile", "PROFILE_ID"));
            selectQuery.addSelectColumn(new Column("Profile", "PROFILE_NAME"));
            selectQuery.setCriteria(new Criteria(new Column("MdPackageToAppGroup", "PACKAGE_ID"), (Object)packageIds, 8));
            final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (dmDataSetWrapper.next()) {
                final Boolean isPortalApp = (Boolean)dmDataSetWrapper.getValue("IS_PURCHASED_FROM_PORTAL");
                if (!isPortalApp) {
                    final Long packageId = (Long)dmDataSetWrapper.getValue("PACKAGE_ID");
                    final String identifier = (String)dmDataSetWrapper.getValue("IDENTIFIER");
                    packageToIdentifier.put(packageId, identifier);
                    final Long appGroupId = (Long)dmDataSetWrapper.getValue("APP_GROUP_ID");
                    final Long profileId = (Long)dmDataSetWrapper.getValue("PROFILE_ID");
                    final String profileName = (String)dmDataSetWrapper.getValue("PROFILE_NAME");
                    appGroupSet.add(appGroupId);
                    profileSet.add(profileId);
                    profileNameSet.add(profileName);
                }
                final Integer platformType = (Integer)dmDataSetWrapper.getValue("PLATFORM_TYPE");
                final Object filePath = dmDataSetWrapper.getValue("APP_FILE_LOC");
                final Integer packageType = (Integer)dmDataSetWrapper.getValue("PACKAGE_TYPE");
                MICSAppRepositoryFeatureController.addTrackingData(platformType, MICSAppRepositoryFeatureController.AppOperation.DELETE_APP, packageType == 2, filePath != null && filePath.toString().endsWith("msi"));
            }
        }
        catch (final Exception e) {
            AppTrashModeHandler.logger.log(Level.SEVERE, "Cannot fetch app identifier", e);
            throw new APIHTTPException("COM0004", new Object[] { e });
        }
        if (!appGroupSet.isEmpty()) {
            try {
                final List<Long> appGroupList = new ArrayList<Long>(appGroupSet);
                final List<Long> profileList = new ArrayList<Long>(profileSet);
                final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("MdAppCatalogToResource");
                deleteQuery.setCriteria(new Criteria(new Column("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)appGroupList.toArray(), 8));
                MDMUtil.getPersistence().delete(deleteQuery);
                final SelectQuery selectQuery2 = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForResource"));
                selectQuery2.addJoin(new Join("RecentProfileForResource", "ProfileToCollection", new String[] { "PROFILE_ID", "COLLECTION_ID" }, new String[] { "PROFILE_ID", "COLLECTION_ID" }, 2));
                selectQuery2.addJoin(new Join("ProfileToCollection", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
                selectQuery2.addJoin(new Join("MdAppToCollection", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
                selectQuery2.addJoin(new Join("MdAppToGroupRel", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
                selectQuery2.addJoin(new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
                selectQuery2.setCriteria(new Criteria(new Column("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroupList.toArray(), 8));
                selectQuery2.addSelectColumn(new Column("RecentProfileForResource", "RESOURCE_ID"));
                selectQuery2.addSelectColumn(new Column("RecentProfileForResource", "PROFILE_ID"));
                selectQuery2.addSelectColumn(new Column("MdAppGroupDetails", "APP_GROUP_ID"));
                selectQuery2.addSelectColumn(new Column("Profile", "PROFILE_NAME"));
                final DMDataSetWrapper dmDataSetWrapper2 = DMDataSetWrapper.executeQuery((Object)selectQuery2);
                final Set<Long> resSet = new HashSet<Long>();
                final Set<Long> profileToResSet = new HashSet<Long>();
                while (dmDataSetWrapper2.next()) {
                    final Long resourceId = (Long)dmDataSetWrapper2.getValue("RESOURCE_ID");
                    final Long profileId2 = (Long)dmDataSetWrapper2.getValue("PROFILE_ID");
                    resSet.add(resourceId);
                    profileToResSet.add(profileId2);
                }
                final List<Long> resList = new ArrayList<Long>(resSet);
                final List<Long> profileToResList = new ArrayList<Long>(profileToResSet);
                final List<Long> packageList = new ArrayList<Long>(packageToIdentifier.keySet());
                if (!resList.isEmpty()) {
                    ProfileAssociateHandler.getInstance().removeCollectionCommandsForSameProfile(resList, profileToResList, "RemoveApplication");
                    ProfileAssociateHandler.getInstance().removeCollectionCommandsForSameProfile(resList, profileToResList, "InstallApplication");
                }
                MDMAppMgmtHandler.getInstance().deletePackageDetails(packageList.toArray(new Long[packageList.size()]));
                MDMAppMgmtHandler.getInstance().deleteProfileforPackage(profileList.toArray(new Long[profileList.size()]), customerId);
                MDMAppMgmtHandler.getInstance().deleteAppRepositoryFiles(customerId, packageList.toArray(new Long[packageList.size()]), appGroupList.toArray(new Long[appGroupList.size()]));
                MDMAppMgmtHandler.getInstance().deleteAppAssignableDetails(appGroupList.toArray(new Long[appGroupList.size()]));
                MDMAppMgmtHandler.getInstance().deleteUpdateConfFromApp(customerId);
                final Iterator<String> iterator = profileNameSet.iterator();
                final String remarks = "mdm.apps.soft_delete";
                String remarksArgs = "";
                if (addAuditLog) {
                    while (iterator.hasNext()) {
                        final String profileName2 = remarksArgs = iterator.next();
                        MDMEventLogHandler.getInstance().MDMEventLogEntry(2036, null, userName, remarks, remarksArgs, customerId);
                    }
                }
                ProfileAssociateHandler.getInstance().updateDeviceProfileSummary();
                final String permanentAppDeleteCnt = CustomerParamsHandler.getInstance().getParameterValue("DELETE_PERMANENT_COUNT", (long)customerId);
                final Long newCount = (permanentAppDeleteCnt != null) ? (Long.parseLong(permanentAppDeleteCnt) + profileList.size()) : profileList.size();
                CustomerParamsHandler.getInstance().addOrUpdateParameter("DELETE_PERMANENT_COUNT", newCount.toString(), (long)customerId);
            }
            catch (final Exception e) {
                AppTrashModeHandler.logger.log(Level.SEVERE, "Exception in softDeleting apps..", e);
            }
        }
    }
    
    static {
        AppTrashModeHandler.logger = Logger.getLogger("MDMConfigLogger");
    }
}
