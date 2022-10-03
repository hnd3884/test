package com.me.mdm.server.apps;

import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import java.util.List;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.util.HashMap;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SortColumn;
import java.util.logging.Level;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import java.util.ArrayList;
import com.me.mdm.server.apps.config.AppConfigPolicyDBHandler;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONArray;
import com.me.mdm.api.paging.PagingUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class PickListAppsHandler
{
    private Logger logger;
    private static final int SORT_APP_NAME_ASCENDING = 1;
    private static final int SORT_APP_NAME_DESCENDING = 2;
    
    public PickListAppsHandler() {
        this.logger = Logger.getLogger("InventoryLog");
    }
    
    public JSONObject getPickListAppsList(final JSONObject optionalParams, final Long customerId, final PagingUtil pagingUtil) throws Exception {
        final JSONArray dataList = new JSONArray();
        final JSONObject response = new JSONObject();
        try {
            SelectQuery countQuery = null;
            boolean configurable;
            boolean system;
            boolean installed;
            boolean managed = installed = (system = (configurable = false));
            final String scope = optionalParams.optString("app_scope");
            final Integer scope_id = optionalParams.optInt("app_scope_id");
            final boolean paging = optionalParams.getBoolean("paging");
            final int platformType = optionalParams.getInt("platform");
            if (paging && platformType == 3) {
                throw new APIHTTPException("COM0031", new Object[0]);
            }
            if (scope != null && scope.length() != 0) {
                final String[] split;
                final String[] scopes = split = scope.split(",");
                for (final String s2 : split) {
                    final String s = s2;
                    switch (s2) {
                        case "system":
                        case "scanned_system": {
                            system = true;
                            break;
                        }
                        case "installed": {
                            installed = true;
                            break;
                        }
                        case "managed": {
                            managed = true;
                            break;
                        }
                        case "configurable_apps": {
                            managed = true;
                            configurable = true;
                            break;
                        }
                    }
                }
            }
            else if (scope_id != null && scope_id >= 1 && scope_id <= 5) {
                switch (scope_id) {
                    case 3:
                    case 4: {
                        system = true;
                        break;
                    }
                    case 2: {
                        installed = true;
                        break;
                    }
                    case 1: {
                        managed = true;
                        break;
                    }
                    case 5: {
                        configurable = true;
                        managed = true;
                        break;
                    }
                }
            }
            else {
                managed = (installed = (system = true));
            }
            final String searchString = optionalParams.optString("search");
            final int sortParam = optionalParams.getInt("sort");
            final Criteria appNamecriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "GROUP_DISPLAY_NAME"), (Object)searchString, 12, false);
            final Criteria appNameProfileCrit = new Criteria(Column.getColumn("Profile", "PROFILE_NAME"), (Object)searchString, 12, false);
            final Criteria bundleIdentifierCrit = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)searchString, 12, false);
            Criteria platformCrit;
            if (platformType != 4) {
                platformCrit = new Criteria(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)platformType, 0);
            }
            else {
                platformCrit = new Criteria(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)new Object[] { 2, 4 }, 8);
            }
            final Criteria repoAppCriteria = new Criteria(Column.getColumn("MdPackageToAppGroup", "APP_GROUP_ID"), (Object)null, 1);
            Criteria criteria = platformCrit.and(bundleIdentifierCrit.or(appNamecriteria.or(appNameProfileCrit)));
            final Criteria customerCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
            criteria = criteria.and(customerCriteria);
            final Criteria configCriteria = new Criteria(new Column("AppConfigTemplate", "APP_ID"), (Object)null, 1);
            if (managed && !system && !installed) {
                criteria = criteria.and(repoAppCriteria);
            }
            if (platformType == 2 && configurable) {
                criteria = criteria.and(configCriteria);
                if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AllowMultipleAppConfiguration")) {
                    final List oemApps = AppConfigPolicyDBHandler.getInstance().getOEMApps(Boolean.TRUE, Boolean.TRUE);
                    final Criteria identifiercriteria = new Criteria(new Column("MdAppGroupDetails", "IDENTIFIER"), (Object)oemApps.toArray(), 8, true);
                    criteria = criteria.and(identifiercriteria);
                }
            }
            final List<String> sysAppAddedIdentifier = new ArrayList<String>();
            if (platformType == 1) {
                final Criteria sysAppNamecriteria = new Criteria(Column.getColumn("IOSSystemApps", "APP_NAME"), (Object)searchString, 12, false);
                final Criteria sysAppBundleIdentifierCrit = new Criteria(Column.getColumn("IOSSystemApps", "IDENTIFIER"), (Object)searchString, 12, false);
                final DataObject systemAppDO = AppsUtil.getInstance().getIOSSystemApps(sysAppNamecriteria.or(sysAppBundleIdentifierCrit));
                final Iterator iterator = systemAppDO.getRows("IOSSystemApps");
                while (iterator.hasNext()) {
                    final Row systemAppRow = iterator.next();
                    final Object identifierObj = systemAppRow.get("IDENTIFIER");
                    final String identifier = (String)identifierObj;
                    if (!sysAppAddedIdentifier.contains(identifier)) {
                        sysAppAddedIdentifier.add(identifier);
                    }
                }
                if (!managed && !installed && system) {
                    final Criteria systemAppCriteria = new Criteria(new Column("MdAppGroupDetails", "IDENTIFIER"), (Object)sysAppAddedIdentifier.toArray(), 8);
                    criteria = criteria.and(systemAppCriteria);
                }
                else if (!system) {
                    final Criteria systemAppCriteria = new Criteria(new Column("MdAppGroupDetails", "IDENTIFIER"), (Object)sysAppAddedIdentifier.toArray(), 9);
                    criteria = criteria.and(systemAppCriteria);
                }
            }
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
            final Join appToGroupJoin = new Join("MdAppGroupDetails", "MdAppToGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
            final Join appJoin = new Join("MdAppToGroupRel", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2);
            final Join configTemplateJoin = new Join("MdAppDetails", "AppConfigTemplate", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 1);
            final Join appToClnJoin = new Join("MdAppDetails", "MdAppToCollection", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 1);
            final Join recProfColnJoin = new Join("MdAppToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 1);
            final Join profileJoin = new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 1);
            final Join recentpkgToAppJoin = new Join("MdAppToCollection", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 1);
            final Join pkgToAppGroupJoin = new Join("AppGroupToCollection", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1);
            final Join pkgToAppDataJoin = new Join("MdPackageToAppGroup", "MdPackageToAppData", new String[] { "PACKAGE_ID", "APP_GROUP_ID" }, new String[] { "PACKAGE_ID", "APP_GROUP_ID" }, 1);
            final Join windowsAppDetails = new Join("MdAppDetails", "WindowsAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 1);
            final Join windowsSystemApps = new Join("MdAppGroupDetails", "WindowsSystemApps", new String[] { "IDENTIFIER" }, new String[] { "PACKAGE_FAMILY_NAME" }, 1);
            selectQuery.addJoin(appToGroupJoin);
            selectQuery.addJoin(appJoin);
            selectQuery.addJoin(appToClnJoin);
            selectQuery.addJoin(recProfColnJoin);
            selectQuery.addJoin(profileJoin);
            selectQuery.addJoin(recentpkgToAppJoin);
            selectQuery.addJoin(pkgToAppGroupJoin);
            selectQuery.addJoin(pkgToAppDataJoin);
            selectQuery.addJoin(windowsAppDetails);
            selectQuery.addJoin(windowsSystemApps);
            selectQuery.addJoin(configTemplateJoin);
            final Boolean isallowedApps = optionalParams.optBoolean("is_allowed_apps", true);
            if (isallowedApps != null && isallowedApps) {
                final Join appControlJoin = new Join("MdAppGroupDetails", "BlacklistAppToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1);
                selectQuery.addJoin(appControlJoin);
                final Criteria whiteListCri = new Criteria(Column.getColumn("BlacklistAppToCollection", "GLOBAL_BLACKLIST"), (Object)true, 1);
                final Criteria whiteListCri2 = new Criteria(Column.getColumn("BlacklistAppToCollection", "GLOBAL_BLACKLIST"), (Object)null, 0);
                criteria = criteria.and(whiteListCri.or(whiteListCri2));
            }
            boolean toShowAppIcon = false;
            try {
                final Boolean sIsAppPurchasedFromPortal = optionalParams.optBoolean("is_app_purchased_from_portal");
                if (sIsAppPurchasedFromPortal) {
                    final Criteria appPurchasedFromPortalCrit = new Criteria(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), (Object)true, 0);
                    criteria = criteria.and(appPurchasedFromPortalCrit);
                    toShowAppIcon = true;
                }
            }
            catch (final Exception ex) {
                this.logger.log(Level.INFO, "App Not purchased from portal");
            }
            selectQuery.setCriteria(criteria);
            selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"));
            selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"));
            selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "GROUP_DISPLAY_NAME"));
            selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "IS_MODERN_APP"));
            final Column isManaged = new Column("MdPackageToAppGroup", "PACKAGE_TYPE").maximum();
            isManaged.setColumnAlias("PACKAGE_TYPE");
            final Column profileColumn = new Column("Profile", "PROFILE_NAME").maximum();
            profileColumn.setColumnAlias("PROFILE_NAME");
            final Column displayImgLoc = new Column("MdPackageToAppData", "DISPLAY_IMAGE_LOC").maximum();
            displayImgLoc.setColumnAlias("DISPLAY_IMAGE_LOC");
            selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_TYPE"));
            final Column aumidAppDetials = new Column("WindowsAppDetails", "AUMID").maximum();
            aumidAppDetials.setColumnAlias("aumidAppDetails");
            final Column ppidAppDetails = new Column("WindowsAppDetails", "PHONE_PRODUCT_ID").maximum();
            ppidAppDetails.setColumnAlias("ppidAppDetails");
            final Column aumidSystemApp = new Column("WindowsSystemApps", "AUMID").maximum();
            aumidSystemApp.setColumnAlias("aumidSystemApp");
            final Column ppidSystemApp = new Column("WindowsSystemApps", "PHONE_PRODUCT_ID").maximum();
            ppidSystemApp.setColumnAlias("ppidSystemApp");
            final Column appIdColumn = new Column("MdAppDetails", "APP_ID").maximum();
            appIdColumn.setColumnAlias("APP_ID");
            final Column packageIdColumn = new Column("MdPackageToAppGroup", "PACKAGE_ID").maximum();
            packageIdColumn.setColumnAlias("PACKAGE_ID");
            final List list = new ArrayList();
            SortColumn sortColumn = null;
            sortColumn = new SortColumn(profileColumn, true);
            switch (sortParam) {
                case 1: {
                    sortColumn = new SortColumn(new Column("MdAppGroupDetails", "GROUP_DISPLAY_NAME"), true);
                    break;
                }
                case 2: {
                    sortColumn = new SortColumn(new Column("MdAppGroupDetails", "GROUP_DISPLAY_NAME"), false);
                    break;
                }
            }
            list.add(sortColumn);
            Range range = null;
            if (paging) {
                range = new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit());
                countQuery = (SelectQuery)selectQuery.clone();
            }
            else {
                range = new Range(1, 750);
            }
            selectQuery.addSortColumns(list);
            selectQuery.setRange(range);
            selectQuery.setGroupByClause(new GroupByClause(selectQuery.getSelectColumns()));
            selectQuery.addSelectColumn(aumidAppDetials);
            selectQuery.addSelectColumn(ppidAppDetails);
            selectQuery.addSelectColumn(aumidSystemApp);
            selectQuery.addSelectColumn(ppidSystemApp);
            selectQuery.addSelectColumn(profileColumn);
            selectQuery.addSelectColumn(displayImgLoc);
            selectQuery.addSelectColumn(isManaged);
            selectQuery.addSelectColumn(appIdColumn);
            selectQuery.addSelectColumn(packageIdColumn);
            final DerivedTable derivedTable = new DerivedTable("APPDETAILS", (Query)selectQuery);
            final SelectQuery appDetailSelectQuery = (SelectQuery)new SelectQueryImpl((Table)derivedTable);
            appDetailSelectQuery.addJoin(new Join((Table)derivedTable, Table.getTable("MdAppGroupDetails"), new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            appDetailSelectQuery.addJoin(new Join(Table.getTable("MdAppGroupDetails"), Table.getTable("MdPackageToAppGroup"), new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
            appDetailSelectQuery.addSelectColumn(Column.getColumn(derivedTable.getTableAlias(), "*"));
            appDetailSelectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"));
            final SortColumn sortColumn2 = new SortColumn(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), false);
            final List sortlist = new ArrayList();
            if (platformType == 3) {
                sortlist.add(sortColumn2);
            }
            appDetailSelectQuery.addSortColumns(sortlist);
            try {
                final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)appDetailSelectQuery);
                final List<String> addedIdentifier = new ArrayList<String>();
                final HashMap hm = new HashMap();
                hm.put("IS_SERVER", true);
                hm.put("IS_AUTHTOKEN", false);
                if (platformType == 3 && system) {
                    final SelectQuery systemAppSelectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("WindowsSystemApps"));
                    final Criteria sysAppNamecriteria2 = new Criteria(Column.getColumn("WindowsSystemApps", "APP_NAME"), (Object)searchString, 12, false);
                    final Criteria sysAppBundleIdentifierCrit2 = new Criteria(Column.getColumn("WindowsSystemApps", "PACKAGE_FAMILY_NAME"), (Object)searchString, 12, false);
                    systemAppSelectQuery.setCriteria(sysAppNamecriteria2.or(sysAppBundleIdentifierCrit2));
                    systemAppSelectQuery.addSelectColumn(Column.getColumn("WindowsSystemApps", "*"));
                    final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)systemAppSelectQuery);
                    while (dmDataSetWrapper.next()) {
                        final JSONObject appData = new JSONObject();
                        final Object appId = dmDataSetWrapper.getValue("APP_ID");
                        final Object appName = dmDataSetWrapper.getValue("APP_NAME");
                        final Object identifierObj2 = dmDataSetWrapper.getValue("PACKAGE_FAMILY_NAME");
                        final Object aumid = dmDataSetWrapper.getValue("AUMID");
                        final Object phoneProductID = dmDataSetWrapper.getValue("PHONE_PRODUCT_ID");
                        final String identifier2 = (String)identifierObj2;
                        if (!addedIdentifier.contains(identifier2)) {
                            appData.put("app_id", (Object)appId);
                            appData.put("IDENTIFIER".toLowerCase(), (Object)identifier2);
                            appData.put("app_name", appName);
                            appData.put("aum_id", aumid);
                            appData.put("PHONE_PRODUCT_ID".toLowerCase(), phoneProductID);
                            appData.put("app_scope", (Object)"system");
                            appData.put("app_scope_id", 3);
                            appData.put("platform", 3);
                            dataList.put((Object)appData);
                            addedIdentifier.add(identifier2);
                        }
                    }
                }
                while (ds.next()) {
                    final JSONObject appData2 = new JSONObject();
                    final Object appGroupObj = ds.getValue("APP_GROUP_ID");
                    final Object identifierObj3 = ds.getValue("IDENTIFIER");
                    final Object groupDisplyNameObj = ds.getValue("GROUP_DISPLAY_NAME");
                    final Object profileNameObj = ds.getValue("PROFILE_NAME");
                    final Object appType = ds.getValue("APP_TYPE");
                    final Object winAUMID1 = ds.getValue("aumidAppDetails");
                    final Object winAUMID2 = ds.getValue("aumidSystemApp");
                    final Object winPID1 = ds.getValue("ppidAppDetails");
                    final Object winPID2 = ds.getValue("ppidSystemApp");
                    final Object isManaged2 = ds.getValue("PACKAGE_TYPE");
                    final Object modernApp = ds.getValue("IS_MODERN_APP");
                    final Object isPurchased = ds.getValue("IS_PURCHASED_FROM_PORTAL");
                    final Object packageId = ds.getValue("PACKAGE_ID");
                    final String identifier3 = (String)identifierObj3;
                    final String appName2 = (String)((profileNameObj != null) ? profileNameObj : ((String)groupDisplyNameObj));
                    appData2.put("package_id", (packageId != null) ? packageId : Long.valueOf(-1L));
                    if (!addedIdentifier.contains(identifier3) && !sysAppAddedIdentifier.contains(identifier3)) {
                        appData2.put("app_id", (Object)String.valueOf(appGroupObj));
                        appData2.put("IDENTIFIER".toLowerCase(), (Object)identifier3);
                        appData2.put("app_name", (Object)appName2);
                        appData2.put("APP_TYPE".toLowerCase(), appType);
                        Object displayImg = ds.getValue("DISPLAY_IMAGE_LOC");
                        final int platform = (int)ds.getValue("PLATFORM_TYPE");
                        appData2.put("platform", platform);
                        hm.put("path", displayImg);
                        if (displayImg != null) {
                            displayImg = MDMApiFactoryProvider.getMDMAuthTokenUtilAPI().getURLWithAuthToken(hm);
                        }
                        if (platformType == 3) {
                            if (winAUMID1 != null || winAUMID2 != null) {
                                appData2.put("aum_id", (winAUMID1 != null) ? winAUMID1 : winAUMID2);
                            }
                            if (winPID1 != null || winPID2 != null) {
                                appData2.put("PHONE_PRODUCT_ID", (winPID1 != null) ? winPID1 : winPID2);
                            }
                            appData2.put("is_msi", !(boolean)modernApp);
                            if ((isManaged2 != null && ((Integer)isManaged2).equals(2)) || (isPurchased != null && (boolean)isPurchased)) {
                                appData2.put("app_scope", (Object)"managed");
                                appData2.put("app_scope_id", 1);
                            }
                            else {
                                appData2.put("app_scope", (Object)"installed");
                                appData2.put("app_scope_id", 2);
                            }
                        }
                        else if (isManaged2 != null && (isManaged2.equals(2) || isManaged2.equals(0) || isManaged2.equals(1))) {
                            appData2.put("app_scope", (Object)"managed");
                            appData2.put("app_scope_id", 1);
                        }
                        else {
                            appData2.put("app_scope", (Object)"installed");
                            appData2.put("app_scope_id", 2);
                        }
                        appData2.put("DISPLAY_IMAGE_LOC".toLowerCase(), (displayImg != null) ? displayImg : "");
                        appData2.put("show_app_icon", toShowAppIcon);
                        if ((appData2.getString("app_scope").equals("managed") && managed) || (appData2.getString("app_scope").equals("installed") && installed)) {
                            dataList.put((Object)appData2);
                        }
                        addedIdentifier.add(identifier3);
                    }
                    else {
                        if (!sysAppAddedIdentifier.contains(identifier3)) {
                            continue;
                        }
                        appData2.put("app_id", (Object)String.valueOf(appGroupObj));
                        appData2.put("IDENTIFIER".toLowerCase(), (Object)identifier3);
                        appData2.put("app_name", (Object)appName2);
                        appData2.put("show_app_icon", false);
                        appData2.put("app_scope", (Object)"system");
                        appData2.put("app_scope_id", 3);
                        appData2.put("platform", platformType);
                        dataList.put((Object)appData2);
                    }
                }
                if (paging) {
                    final Column countColumn = new Column("MdAppGroupDetails", "APP_GROUP_ID");
                    while (countQuery.getSelectColumns().size() > 0) {
                        countQuery.removeSelectColumn(0);
                    }
                    while (countQuery.getSortColumns().size() > 0) {
                        countQuery.removeSortColumn(0);
                    }
                    final int count = DBUtil.getRecordCount(countQuery, countColumn.getTableAlias(), countColumn.getColumnName());
                    if (count > 0) {
                        final JSONObject meta = new JSONObject();
                        meta.put("total_record_count", count);
                        response.put("metadata", (Object)meta);
                        final JSONObject pagingJSON = pagingUtil.getPagingJSON(count);
                        if (pagingJSON != null) {
                            response.put("paging", (Object)pagingJSON);
                        }
                    }
                }
            }
            catch (final Exception ex2) {
                this.logger.log(Level.WARNING, "Exception occurred while executing query on getSearchApps", ex2);
            }
            response.put("apps", (Object)dataList);
            return (dataList.length() > 0) ? response : null;
        }
        catch (final Exception ex3) {
            this.logger.log(Level.WARNING, "Exception occurred while searching apps", ex3);
            throw ex3;
        }
    }
}
