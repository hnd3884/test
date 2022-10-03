package com.me.mdm.server.tree.apidatahandler;

import com.adventnet.ds.query.GroupByClause;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.ds.query.Criteria;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.util.logging.Level;
import com.adventnet.ds.query.DMDataSetWrapper;
import java.util.HashMap;
import org.json.JSONArray;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.Range;
import org.json.JSONObject;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import java.util.ArrayList;
import java.util.List;

public class ApiKioskAppListViewDataHandler extends ApiListViewDataHandler
{
    protected List<String> sysAppAddedIdentifier;
    protected boolean installed;
    protected boolean managed;
    protected boolean system;
    protected boolean configurable;
    private static final int SORT_APP_NAME_ASCENDING = 1;
    private static final int SORT_APP_NAME_DESCENDING = 2;
    
    public ApiKioskAppListViewDataHandler() {
        this.sysAppAddedIdentifier = new ArrayList<String>();
    }
    
    @Override
    protected SelectQuery getSelectQuery() {
        final int platformType = this.requestJson.getInt("platform");
        (this.selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"))).addJoin(new Join("MdAppGroupDetails", "MdAppToGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        this.selectQuery.addJoin(new Join("MdAppToGroupRel", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        this.selectQuery.addJoin(new Join("MdAppDetails", "MdAppToCollection", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 1));
        this.selectQuery.addJoin(new Join("MdAppToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 1));
        this.selectQuery.addJoin(new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 1));
        this.selectQuery.addJoin(new Join("MdAppToCollection", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 1));
        this.selectQuery.addJoin(new Join("AppGroupToCollection", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
        this.selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdPackageToAppData", new String[] { "PACKAGE_ID", "APP_GROUP_ID" }, new String[] { "PACKAGE_ID", "APP_GROUP_ID" }, 1));
        this.selectQuery.addJoin(new Join("MdAppDetails", "WindowsAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 1));
        this.selectQuery.addJoin(new Join("MdAppDetails", "AppConfigTemplate", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 1));
        this.selectQuery.addJoin(new Join("MdAppGroupDetails", "WindowsSystemApps", new String[] { "IDENTIFIER" }, new String[] { "PACKAGE_FAMILY_NAME" }, 1));
        this.selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
        this.selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"));
        this.selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"));
        this.selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "GROUP_DISPLAY_NAME"));
        this.selectQuery.addSelectColumn(Column.getColumn("MdAppDetails", "IS_MODERN_APP"));
        this.selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_TYPE"));
        if (platformType == 3) {
            this.selectQuery.addSelectColumn(Column.getColumn("WindowsSystemApps", "APP_NAME"));
        }
        return this.selectQuery;
    }
    
    @Override
    protected JSONObject fetchResultObject() {
        try {
            final int platformType = this.requestJson.getInt("platform");
            final Boolean sIsAppPurchasedFromPortal = this.requestJson.optBoolean("is_app_purchased_from_portal");
            final String searchString = this.requestJson.optString("searchValue");
            final int sortParam = this.requestJson.getInt("sort");
            final Range range = this.selectQuery.getRange();
            this.selectQuery.setRange((Range)null);
            this.addAggregatedColumn(this.selectQuery);
            final DerivedTable derivedTable = new DerivedTable("APPDETAILS", (Query)this.selectQuery);
            final SelectQuery finalSelectQuery = (SelectQuery)new SelectQueryImpl((Table)derivedTable);
            finalSelectQuery.addJoin(new Join((Table)derivedTable, Table.getTable("MdAppGroupDetails"), new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            finalSelectQuery.addJoin(new Join(Table.getTable("MdAppGroupDetails"), Table.getTable("MdPackageToAppGroup"), new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
            finalSelectQuery.addSelectColumn(Column.getColumn(derivedTable.getTableAlias(), "*"));
            finalSelectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"));
            finalSelectQuery.setRange(range);
            final SortColumn sortColumn2 = new SortColumn(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), false);
            final List sortlist = new ArrayList();
            if (platformType == 3) {
                sortlist.add(sortColumn2);
            }
            final Column profileColumn = new Column("APPDETAILS", "PROFILE_NAME");
            SortColumn sortColumn3 = new SortColumn(profileColumn, true);
            switch (sortParam) {
                case 1: {
                    sortColumn3 = new SortColumn(new Column("MdAppGroupDetails", "GROUP_DISPLAY_NAME"), true);
                    break;
                }
                case 2: {
                    sortColumn3 = new SortColumn(new Column("MdAppGroupDetails", "GROUP_DISPLAY_NAME"), false);
                    break;
                }
            }
            sortlist.add(sortColumn3);
            finalSelectQuery.addSortColumns(sortlist);
            final JSONObject resultJson = new JSONObject();
            final JSONArray dataList = new JSONArray();
            final List<String> addedIdentifier = new ArrayList<String>();
            final HashMap hm = new HashMap();
            hm.put("IS_SERVER", true);
            hm.put("IS_AUTHTOKEN", false);
            final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)finalSelectQuery);
            ApiKioskAppListViewDataHandler.logger.log(Level.FINE, "Query formation for picklist App filtered values completed with result");
            while (ds.next()) {
                final JSONObject appData = new JSONObject();
                final Object appGroupObj = ds.getValue("APP_GROUP_ID");
                final Object identifierObj = ds.getValue("IDENTIFIER");
                final Object groupDisplyNameObj = ds.getValue("GROUP_DISPLAY_NAME");
                final Object profileNameObj = ds.getValue("PROFILE_NAME");
                final Object appType = ds.getValue("APP_TYPE");
                final Object winAUMID1 = ds.getValue("aumidAppDetails");
                final Object winAUMID2 = ds.getValue("aumidSystemApp");
                final Object winPID1 = ds.getValue("ppidAppDetails");
                final Object winPID2 = ds.getValue("ppidSystemApp");
                final Object isManaged1 = ds.getValue("PACKAGE_TYPE");
                final Object modernApp = ds.getValue("IS_MODERN_APP");
                final Object isPurchased = ds.getValue("IS_PURCHASED_FROM_PORTAL");
                final Object packageId = ds.getValue("PACKAGE_ID");
                Object isBlacklist = ds.getValue("GLOBAL_BLACKLIST");
                if (isBlacklist == null) {
                    isBlacklist = Boolean.FALSE;
                }
                final String identifier = (String)identifierObj;
                String appName = (String)((profileNameObj != null) ? profileNameObj : ((String)groupDisplyNameObj));
                appData.put("app_id", (Object)String.valueOf(appGroupObj));
                appData.put("app_id", (Object)String.valueOf(appGroupObj));
                appData.put("IDENTIFIER".toLowerCase(), (Object)identifier);
                final int platform = (int)ds.getValue("PLATFORM_TYPE");
                appData.put("platform", platform);
                if (platformType == 3) {
                    if (winAUMID1 != null || winAUMID2 != null) {
                        appData.put("aum_id", (winAUMID1 != null) ? winAUMID1 : winAUMID2);
                    }
                    if (winPID1 != null || winPID2 != null) {
                        appData.put("PHONE_PRODUCT_ID", (winPID1 != null) ? winPID1 : winPID2);
                    }
                    appData.put("is_msi", !(boolean)modernApp);
                    appName = (String)((ds.getValue("APP_NAME") != null) ? ds.getValue("APP_NAME") : appName);
                }
                appData.put("app_name", (Object)appName);
                appData.put("is_global_blacklisted", isBlacklist);
                appData.put("package_id", (packageId != null) ? packageId : Long.valueOf(-1L));
                if (!addedIdentifier.contains(identifier) && !this.sysAppAddedIdentifier.contains(identifier)) {
                    appData.put("APP_TYPE".toLowerCase(), appType);
                    Object displayImg = ds.getValue("DISPLAY_IMAGE_LOC");
                    hm.put("path", displayImg);
                    if (displayImg != null) {
                        displayImg = MDMApiFactoryProvider.getMDMAuthTokenUtilAPI().getURLWithAuthToken(hm);
                    }
                    if (isManaged1 != null && (isManaged1.equals(2) || (isPurchased != null && (boolean)isPurchased) || isManaged1.equals(0) || isManaged1.equals(1))) {
                        appData.put("app_scope", (Object)"managed");
                        appData.put("app_scope_id", 1);
                    }
                    else {
                        appData.put("app_scope", (Object)"installed");
                        appData.put("app_scope_id", 2);
                    }
                    appData.put("DISPLAY_IMAGE_LOC".toLowerCase(), (displayImg != null) ? displayImg : "");
                    appData.put("show_app_icon", (Object)sIsAppPurchasedFromPortal);
                    if ((appData.getString("app_scope").equals("managed") && this.managed) || (appData.getString("app_scope").equals("installed") && this.installed)) {
                        dataList.put((Object)appData);
                    }
                    addedIdentifier.add(identifier);
                }
                else {
                    if (!this.sysAppAddedIdentifier.contains(identifier)) {
                        continue;
                    }
                    appData.put("show_app_icon", false);
                    appData.put("app_scope", (Object)"system");
                    appData.put("app_scope_id", 3);
                    dataList.put((Object)appData);
                }
            }
            resultJson.put("apps", (Object)dataList);
            return resultJson;
        }
        catch (final Exception ex) {
            ApiKioskAppListViewDataHandler.logger.log(Level.INFO, "Exception while fetching details from picklist apps", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    protected SelectQuery setCriteria() {
        final String scope = this.requestJson.optString("app_scope");
        final int platformType = this.requestJson.getInt("platform");
        final String searchString = this.requestJson.optString("searchValue");
        final Long customerId = this.requestJson.getLong("customerId");
        final Boolean isallowedApps = this.requestJson.optBoolean("is_allowed_apps", true);
        final Boolean sIsAppPurchasedFromPortal = this.requestJson.optBoolean("is_app_purchased_from_portal");
        final boolean b = false;
        this.configurable = b;
        this.system = b;
        this.managed = b;
        this.installed = b;
        if (scope != null && scope.length() != 0) {
            final String[] split;
            final String[] scopes = split = scope.split(",");
            for (final String s2 : split) {
                final String s = s2;
                switch (s2) {
                    case "system":
                    case "scanned_system": {
                        this.system = true;
                        break;
                    }
                    case "installed": {
                        this.installed = true;
                        break;
                    }
                    case "managed": {
                        this.managed = true;
                        break;
                    }
                    case "configurable_apps": {
                        this.managed = true;
                        this.configurable = true;
                        break;
                    }
                }
            }
        }
        else {
            final boolean installed = true;
            this.system = installed;
            this.managed = installed;
            this.installed = installed;
        }
        Criteria platformCrit;
        if (platformType != 4) {
            platformCrit = new Criteria(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)platformType, 0);
        }
        else {
            platformCrit = new Criteria(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)new Object[] { 2, 4 }, 8);
        }
        final Criteria appNamecriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "GROUP_DISPLAY_NAME"), (Object)searchString, 12, false);
        final Criteria appNameProfileCrit = new Criteria(Column.getColumn("Profile", "PROFILE_NAME"), (Object)searchString, 12, false);
        final Criteria bundleIdentifierCrit = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)searchString, 12, false);
        Criteria criteria = platformCrit.and(bundleIdentifierCrit.or(appNamecriteria.or(appNameProfileCrit)));
        final Criteria customerCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        criteria = criteria.and(customerCriteria);
        final Criteria repoAppCriteria = new Criteria(Column.getColumn("MdPackageToAppGroup", "APP_GROUP_ID"), (Object)null, 1);
        final Criteria trashCriteria = new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)false, 0);
        if (this.managed && !this.system && !this.installed) {
            criteria = criteria.and(repoAppCriteria).and(trashCriteria);
        }
        final Criteria configCriteria = new Criteria(new Column("AppConfigTemplate", "APP_ID"), (Object)null, 1);
        if (this.configurable == Boolean.TRUE && platformType == 2) {
            criteria = criteria.and(configCriteria);
        }
        if (this.configurable == Boolean.TRUE && platformType == 1) {
            final Criteria bundleIdCriteria = new Criteria(new Column("MdAppGroupDetails", "IDENTIFIER"), (Object)new String[] { "com.manageengine.mdm.iosagent", "com.manageengine.mdm.mac", "com.manageengine.ems" }, 9);
            criteria = criteria.and(bundleIdCriteria);
        }
        final Join appControlJoin = new Join("MdAppGroupDetails", "BlacklistAppToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1);
        this.selectQuery.addJoin(appControlJoin);
        this.selectQuery.addSelectColumn(Column.getColumn("BlacklistAppToCollection", "GLOBAL_BLACKLIST"));
        this.selectQuery.addSelectColumn(Column.getColumn("BlacklistAppToCollection", "COLLECTION_ID"));
        if (isallowedApps != null && isallowedApps) {
            final Criteria whiteListCri = new Criteria(Column.getColumn("BlacklistAppToCollection", "GLOBAL_BLACKLIST"), (Object)true, 1);
            final Criteria whiteListCri2 = new Criteria(Column.getColumn("BlacklistAppToCollection", "GLOBAL_BLACKLIST"), (Object)null, 0);
            criteria = criteria.and(whiteListCri.or(whiteListCri2));
        }
        try {
            if (sIsAppPurchasedFromPortal) {
                final Criteria appPurchasedFromPortalCrit = new Criteria(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), (Object)true, 0);
                criteria = criteria.and(appPurchasedFromPortalCrit);
            }
        }
        catch (final Exception ex) {
            ApiKioskAppListViewDataHandler.logger.log(Level.INFO, "App Not purchased from portal");
        }
        if (platformType == 1 || platformType == 3) {
            this.getIosSystemApps(searchString);
            this.getWindowsSystemApps(searchString);
            if (!this.managed && !this.installed && this.system) {
                final Criteria systemAppCriteria = new Criteria(new Column("MdAppGroupDetails", "IDENTIFIER"), (Object)this.sysAppAddedIdentifier.toArray(), 8);
                criteria = criteria.and(systemAppCriteria);
            }
            else if (!this.system && !this.configurable) {
                final Criteria systemAppCriteria = new Criteria(new Column("MdAppGroupDetails", "IDENTIFIER"), (Object)this.sysAppAddedIdentifier.toArray(), 9);
                criteria = criteria.and(systemAppCriteria);
            }
            else if (this.managed && this.system && !this.installed) {
                final Criteria managedAppCriteria = new Criteria(new Column("Profile", "PROFILE_NAME"), (Object)null, 1);
                final Criteria systemAppCriteria2 = new Criteria(new Column("WindowsSystemApps", "AUMID"), (Object)null, 1);
                criteria = criteria.and(systemAppCriteria2.or(managedAppCriteria));
            }
        }
        this.selectQuery.setCriteria(criteria);
        return this.selectQuery;
    }
    
    private List getIosSystemApps(final String searchString) {
        try {
            final Criteria sysAppNamecriteria = new Criteria(Column.getColumn("IOSSystemApps", "APP_NAME"), (Object)searchString, 12, false);
            final Criteria sysAppBundleIdentifierCrit = new Criteria(Column.getColumn("IOSSystemApps", "IDENTIFIER"), (Object)searchString, 12, false);
            final DataObject systemAppDO = AppsUtil.getInstance().getIOSSystemApps(sysAppNamecriteria.or(sysAppBundleIdentifierCrit));
            final Iterator iterator = systemAppDO.getRows("IOSSystemApps");
            while (iterator.hasNext()) {
                final Row systemAppRow = iterator.next();
                final Object identifierObj = systemAppRow.get("IDENTIFIER");
                final String identifier = (String)identifierObj;
                if (!this.sysAppAddedIdentifier.contains(identifier)) {
                    this.sysAppAddedIdentifier.add(identifier);
                }
            }
        }
        catch (final DataAccessException ex) {
            ApiKioskAppListViewDataHandler.logger.log(Level.INFO, "Exception while fetching ios system apps", (Throwable)ex);
        }
        return this.sysAppAddedIdentifier;
    }
    
    private List getWindowsSystemApps(final String searchString) {
        try {
            final Criteria sysAppNamecriteria = new Criteria(Column.getColumn("WindowsSystemApps", "APP_NAME"), (Object)searchString, 12, false);
            final Criteria sysAppBundleIdentifierCrit = new Criteria(Column.getColumn("WindowsSystemApps", "PACKAGE_FAMILY_NAME"), (Object)searchString, 12, false);
            final DataObject systemAppDO = AppsUtil.getInstance().getWindowSystemApps(sysAppNamecriteria.or(sysAppBundleIdentifierCrit));
            final Iterator iterator = systemAppDO.getRows("WindowsSystemApps");
            while (iterator.hasNext()) {
                final Row systemAppRow = iterator.next();
                final Object identifierObj = systemAppRow.get("PACKAGE_FAMILY_NAME");
                final String identifier = (String)identifierObj;
                if (!this.sysAppAddedIdentifier.contains(identifier)) {
                    this.sysAppAddedIdentifier.add(identifier);
                }
            }
        }
        catch (final DataAccessException ex) {
            ApiKioskAppListViewDataHandler.logger.log(Level.INFO, "Exception while fetching ios system apps", (Throwable)ex);
        }
        return this.sysAppAddedIdentifier;
    }
    
    private SelectQuery addAggregatedColumn(final SelectQuery selectQuery) {
        final int sortParam = this.requestJson.getInt("sort");
        final Column isManaged = new Column("MdPackageToAppGroup", "PACKAGE_TYPE").maximum();
        isManaged.setColumnAlias("PACKAGE_TYPE");
        final Column profileColumn = new Column("Profile", "PROFILE_NAME").maximum();
        profileColumn.setColumnAlias("PROFILE_NAME");
        final Column displayImgLoc = new Column("MdPackageToAppData", "DISPLAY_IMAGE_LOC").maximum();
        displayImgLoc.setColumnAlias("DISPLAY_IMAGE_LOC");
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
        return selectQuery;
    }
}
