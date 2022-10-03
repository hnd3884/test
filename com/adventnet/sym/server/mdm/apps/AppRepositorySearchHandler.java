package com.adventnet.sym.server.mdm.apps;

import java.util.Hashtable;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.me.devicemanagement.framework.server.customgroup.CustomGroupUtil;
import com.adventnet.db.api.RelationalAPI;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.ds.query.DMDataSetWrapper;
import org.json.JSONObject;
import java.util.Properties;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.HashMap;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SortColumn;
import java.util.logging.Level;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.search.SuggestQueryIfc;

public class AppRepositorySearchHandler implements SuggestQueryIfc
{
    static Logger logger;
    
    public List getSuggestData(final String searchString, final String params) {
        return this.getSearchApps(searchString, params);
    }
    
    private List getSearchApps(String searchString, final String paramsData) {
        final List dataList = new ArrayList();
        try {
            final String[] data = paramsData.trim().split(",");
            String configName = "";
            if (this.isParamContainsKey(paramsData, "currentConfig")) {
                configName = this.getKeyValueFromSearchParam(paramsData, "currentConfig");
            }
            searchString = searchString.trim();
            if ("CHROME_KIOSK_POLICY".equals(configName)) {
                return this.getSearchAppsForChromeKiosk(searchString, paramsData);
            }
            final int platformType = Integer.parseInt(data[0]);
            final Criteria appNamecriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "GROUP_DISPLAY_NAME"), (Object)searchString, 12, false);
            final Criteria appNameProfileCrit = new Criteria(Column.getColumn("Profile", "PROFILE_NAME"), (Object)searchString, 12, false);
            final Criteria bundleIdentifierCrit = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)searchString, 12, false);
            final Criteria platformCrit = new Criteria(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)platformType, 0);
            final Criteria repoAppCriteria = new Criteria(Column.getColumn("MdPackageToAppGroup", "APP_GROUP_ID"), (Object)null, 1);
            Criteria criteria = platformCrit.and(bundleIdentifierCrit.or(appNamecriteria.or(appNameProfileCrit)));
            if (data[1] != null && !data[1].equals("")) {
                final Criteria customerCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)Long.valueOf(data[1]), 0);
                criteria = criteria.and(customerCriteria);
            }
            if (configName.equalsIgnoreCase("IOS_PER_APP_VPN")) {
                criteria = criteria.and(repoAppCriteria);
            }
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
            final Join appToGroupJoin = new Join("MdAppGroupDetails", "MdAppToGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
            final Join appJoin = new Join("MdAppToGroupRel", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2);
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
            final Boolean isallowedApps = Boolean.valueOf(data[2]);
            if (isallowedApps != null && isallowedApps) {
                final Join appControlJoin = new Join("MdAppGroupDetails", "BlacklistAppToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1);
                selectQuery.addJoin(appControlJoin);
                final Criteria whiteListCri = new Criteria(Column.getColumn("BlacklistAppToCollection", "GLOBAL_BLACKLIST"), (Object)true, 1);
                final Criteria whiteListCri2 = new Criteria(Column.getColumn("BlacklistAppToCollection", "GLOBAL_BLACKLIST"), (Object)null, 0);
                criteria = criteria.and(whiteListCri.or(whiteListCri2));
            }
            boolean toShowAppIcon = false;
            try {
                final String sIsAppPurchasedFromPortal = data[3];
                if (sIsAppPurchasedFromPortal != null && sIsAppPurchasedFromPortal.equalsIgnoreCase("true")) {
                    final Criteria appPurchasedFromPortalCrit = new Criteria(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), (Object)true, 0);
                    criteria = criteria.and(appPurchasedFromPortalCrit);
                    toShowAppIcon = true;
                }
            }
            catch (final Exception ex) {
                AppRepositorySearchHandler.logger.log(Level.INFO, "App Not purchased from portal");
            }
            selectQuery.setCriteria(criteria);
            selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"));
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
            final SortColumn sortColumn = new SortColumn(profileColumn, true);
            final List list = new ArrayList();
            list.add(sortColumn);
            selectQuery.addSortColumns(list);
            final Range range = new Range(1, 15);
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
                final List<String> addedIdentifier = new ArrayList<String>();
                final HashMap hm = new HashMap();
                hm.put("IS_SERVER", true);
                hm.put("IS_AUTHTOKEN", false);
                if (platformType == 3) {
                    final SelectQuery systemAppSelectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("WindowsSystemApps"));
                    final Criteria sysAppNamecriteria = new Criteria(Column.getColumn("WindowsSystemApps", "APP_NAME"), (Object)searchString, 12, false);
                    final Criteria sysAppBundleIdentifierCrit = new Criteria(Column.getColumn("WindowsSystemApps", "PACKAGE_FAMILY_NAME"), (Object)searchString, 12, false);
                    systemAppSelectQuery.setCriteria(sysAppNamecriteria.or(sysAppBundleIdentifierCrit));
                    systemAppSelectQuery.addSelectColumn(Column.getColumn("WindowsSystemApps", "*"));
                    final DataObject dataObject = MDMUtil.getPersistence().get(systemAppSelectQuery);
                    if (dataObject != null && !dataObject.isEmpty()) {
                        final Iterator iterator = dataObject.getRows("WindowsSystemApps");
                        while (iterator.hasNext()) {
                            final Row row = iterator.next();
                            final Properties dataProperty = new Properties();
                            final JSONObject appData = new JSONObject();
                            final Object appId = row.get("APP_ID");
                            final Object appName = row.get("APP_NAME");
                            final Object identifierObj = row.get("PACKAGE_FAMILY_NAME");
                            final Object aumid = row.get("AUMID");
                            final Object phoneProductID = row.get("PHONE_PRODUCT_ID");
                            final String identifier = (String)identifierObj;
                            if (!addedIdentifier.contains(identifier)) {
                                appData.put("APP_ID", (Object)appId);
                                appData.put("IDENTIFIER", (Object)identifier);
                                appData.put("GROUP_DISPLAY_NAME", appName);
                                appData.put("AUMID", aumid);
                                appData.put("PHONE_PRODUCT_ID", phoneProductID);
                                appData.put("IsWindowsSystemApp", true);
                                ((Hashtable<String, Object>)dataProperty).put("dataValue", appName);
                                ((Hashtable<String, String>)dataProperty).put("dataId", appData.toString());
                                dataList.add(dataProperty);
                                addedIdentifier.add(identifier);
                            }
                        }
                    }
                }
                final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)appDetailSelectQuery);
                while (ds.next()) {
                    final Properties dataProperty2 = new Properties();
                    final JSONObject appData2 = new JSONObject();
                    final Object appGroupObj = ds.getValue("APP_GROUP_ID");
                    final Object identifierObj2 = ds.getValue("IDENTIFIER");
                    final Object groupDisplyNameObj = ds.getValue("GROUP_DISPLAY_NAME");
                    final Object profileNameObj = ds.getValue("PROFILE_NAME");
                    final Object appType = ds.getValue("APP_TYPE");
                    final Object winAUMID1 = ds.getValue("aumidAppDetails");
                    final Object winAUMID2 = ds.getValue("aumidSystemApp");
                    final Object winPID1 = ds.getValue("ppidAppDetails");
                    final Object winPID2 = ds.getValue("ppidSystemApp");
                    final Object managed = ds.getValue("PACKAGE_TYPE");
                    final Object modernApp = ds.getValue("IS_MODERN_APP");
                    final Object isPurchased = ds.getValue("IS_PURCHASED_FROM_PORTAL");
                    final String identifier2 = (String)identifierObj2;
                    if (!addedIdentifier.contains(identifier2)) {
                        final String appName2 = (String)((profileNameObj != null) ? profileNameObj : ((String)groupDisplyNameObj));
                        appData2.put("APP_GROUP_ID", (Object)String.valueOf(appGroupObj));
                        appData2.put("IDENTIFIER", (Object)identifier2);
                        appData2.put("GROUP_DISPLAY_NAME", (Object)appName2);
                        appData2.put("APP_TYPE", appType);
                        Object displayImg = ds.getValue("DISPLAY_IMAGE_LOC");
                        hm.put("path", displayImg);
                        if (displayImg != null) {
                            displayImg = MDMApiFactoryProvider.getMDMAuthTokenUtilAPI().getURLWithAuthToken(hm);
                        }
                        if (platformType == 3) {
                            if (winAUMID1 != null || winAUMID2 != null) {
                                appData2.put("AUMID", (winAUMID1 != null) ? winAUMID1 : winAUMID2);
                            }
                            if (winPID1 != null || winPID2 != null) {
                                appData2.put("PHONE_PRODUCT_ID", (winPID1 != null) ? winPID1 : winPID2);
                            }
                            appData2.put("IsMsi", !(boolean)modernApp);
                            if ((managed != null && ((Integer)managed).equals(2)) || (isPurchased != null && (boolean)isPurchased)) {
                                appData2.put("isManaged", true);
                            }
                            else {
                                appData2.put("isManaged", false);
                            }
                        }
                        appData2.put("DISPLAY_IMAGE_LOC", (displayImg != null) ? displayImg : "");
                        appData2.put("SHOW_APP_ICON", toShowAppIcon);
                        ((Hashtable<String, String>)dataProperty2).put("dataValue", appName2);
                        ((Hashtable<String, String>)dataProperty2).put("dataId", appData2.toString());
                        dataList.add(dataProperty2);
                        addedIdentifier.add(identifier2);
                    }
                }
                if (platformType == 1 && !configName.equalsIgnoreCase("IOS_PER_APP_VPN")) {
                    final SelectQuery systemAppSelectQuery2 = (SelectQuery)new SelectQueryImpl(Table.getTable("IOSSystemApps"));
                    final Criteria sysAppNamecriteria2 = new Criteria(Column.getColumn("IOSSystemApps", "APP_NAME"), (Object)searchString, 12, false);
                    final Criteria sysAppBundleIdentifierCrit2 = new Criteria(Column.getColumn("IOSSystemApps", "IDENTIFIER"), (Object)searchString, 12, false);
                    systemAppSelectQuery2.setCriteria(sysAppNamecriteria2.or(sysAppBundleIdentifierCrit2));
                    systemAppSelectQuery2.addSelectColumn(Column.getColumn("IOSSystemApps", "*"));
                    final DataObject dataObject2 = MDMUtil.getPersistence().get(systemAppSelectQuery2);
                    final List<String> sysAppAddedIdentifier = new ArrayList<String>();
                    if (dataObject2 != null && !dataObject2.isEmpty()) {
                        final Iterator iterator2 = dataObject2.getRows("IOSSystemApps");
                        while (iterator2.hasNext()) {
                            final Row row2 = iterator2.next();
                            final Properties dataProperty3 = new Properties();
                            final JSONObject appData3 = new JSONObject();
                            final Object appId2 = row2.get("APP_ID");
                            final Object appName3 = row2.get("APP_NAME");
                            final Object identifierObj3 = row2.get("IDENTIFIER");
                            final String identifier = (String)identifierObj3;
                            if (!sysAppAddedIdentifier.contains(identifier)) {
                                appData3.put("APP_ID", (Object)appId2);
                                appData3.put("IDENTIFIER", (Object)identifier);
                                appData3.put("GROUP_DISPLAY_NAME", appName3);
                                appData3.put("SHOW_APP_ICON", false);
                                appData3.put("IsiOSSystemApp", true);
                                ((Hashtable<String, Object>)dataProperty3).put("dataValue", appName3);
                                ((Hashtable<String, String>)dataProperty3).put("dataId", appData3.toString());
                                dataList.add(dataProperty3);
                                addedIdentifier.add(identifier);
                            }
                        }
                    }
                }
            }
            catch (final Exception ex2) {
                AppRepositorySearchHandler.logger.log(Level.WARNING, "Exception occurred while executing query on getSearchApps", ex2);
            }
            return (dataList.size() > 0) ? dataList : null;
        }
        catch (final Exception ex3) {
            AppRepositorySearchHandler.logger.log(Level.WARNING, "Exception occurred while searching apps", ex3);
            return null;
        }
    }
    
    private String getKeyValueFromSearchParam(final String data, final String key) {
        if (data != null) {
            final String[] split;
            final String[] subData = split = data.split(",");
            for (final String stringParam : split) {
                if (stringParam.contains("=")) {
                    final String[] param = stringParam.split("=");
                    if (param[0].equalsIgnoreCase(key) && param.length > 1) {
                        return param[1];
                    }
                }
            }
        }
        return null;
    }
    
    private boolean isParamContainsKey(final String data, final String key) {
        return this.getKeyValueFromSearchParam(data, key) != null;
    }
    
    private List getSearchAppsForChromeKiosk(final String searchString, final String paramsData) {
        final List dataList = new ArrayList();
        try {
            final String[] data = paramsData.trim().split(",");
            final Criteria appNamecriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "GROUP_DISPLAY_NAME"), (Object)searchString, 12, false);
            final Criteria appNameProfileCrit = new Criteria(Column.getColumn("Profile", "PROFILE_NAME"), (Object)searchString, 12, false);
            final Criteria bundleIdentifierCrit = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)searchString, 12, false);
            final Criteria chromeKioskCriteria = this.getChromeKioskCriteria();
            Criteria criteria = bundleIdentifierCrit.or(appNamecriteria.or(appNameProfileCrit));
            criteria = criteria.and(chromeKioskCriteria);
            if (data[1] != null && !data[1].equals("")) {
                final Criteria customerCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)Long.valueOf(data[1]), 0);
                criteria = criteria.and(customerCriteria);
            }
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
            final Join appToGroupJoin = new Join("MdAppGroupDetails", "MdAppToGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
            final Join appJoin = new Join("MdAppToGroupRel", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2);
            final Join appToClnJoin = new Join("MdAppDetails", "MdAppToCollection", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 1);
            final Join recProfColnJoin = new Join("MdAppToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 1);
            final Join profileJoin = new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 1);
            final Join recentpkgToAppJoin = new Join("MdAppToCollection", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 1);
            final Join pkgToAppGroupJoin = new Join("AppGroupToCollection", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1);
            final Join pkgToAppDataJoin = new Join("MdPackageToAppGroup", "MdPackageToAppData", new String[] { "PACKAGE_ID", "APP_GROUP_ID" }, new String[] { "PACKAGE_ID", "APP_GROUP_ID" }, 1);
            selectQuery.addJoin(appToGroupJoin);
            selectQuery.addJoin(appJoin);
            selectQuery.addJoin(appToClnJoin);
            selectQuery.addJoin(recProfColnJoin);
            selectQuery.addJoin(profileJoin);
            selectQuery.addJoin(recentpkgToAppJoin);
            selectQuery.addJoin(pkgToAppGroupJoin);
            selectQuery.addJoin(pkgToAppDataJoin);
            final Boolean isallowedApps = Boolean.valueOf(data[2]);
            if (isallowedApps != null && isallowedApps) {
                final Join appControlJoin = new Join("MdAppGroupDetails", "BlacklistAppToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1);
                selectQuery.addJoin(appControlJoin);
                final Criteria whiteListCri = new Criteria(Column.getColumn("BlacklistAppToCollection", "GLOBAL_BLACKLIST"), (Object)true, 1);
                final Criteria whiteListCri2 = new Criteria(Column.getColumn("BlacklistAppToCollection", "GLOBAL_BLACKLIST"), (Object)null, 0);
                criteria = criteria.and(whiteListCri.or(whiteListCri2));
            }
            selectQuery.setCriteria(criteria);
            selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"));
            selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "GROUP_DISPLAY_NAME"));
            selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_NAME"));
            selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "DISPLAY_IMAGE_LOC"));
            selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_TYPE"));
            selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"));
            final SortColumn sortColumn = new SortColumn(Column.getColumn("Profile", "PROFILE_NAME"), true);
            selectQuery.addSortColumn(sortColumn);
            final Range range = new Range(1, 15);
            selectQuery.setRange(range);
            final RelationalAPI relapi = RelationalAPI.getInstance();
            Connection conn = null;
            DataSet ds = null;
            try {
                conn = relapi.getConnection();
                ds = relapi.executeQuery((Query)selectQuery, conn);
                final List<String> addedIdentifier = new ArrayList<String>();
                final HashMap hm = new HashMap();
                hm.put("IS_SERVER", true);
                hm.put("IS_AUTHTOKEN", false);
                while (ds.next()) {
                    final Properties dataProperty = new Properties();
                    final JSONObject appData = new JSONObject();
                    final Object appGroupObj = ds.getValue("APP_GROUP_ID");
                    final Object identifierObj = ds.getValue("IDENTIFIER");
                    final Object groupDisplyNameObj = ds.getValue("GROUP_DISPLAY_NAME");
                    final Object profileNameObj = ds.getValue("PROFILE_NAME");
                    final Object appType = ds.getValue("APP_TYPE");
                    final String identifier = (String)identifierObj;
                    if (!addedIdentifier.contains(identifier)) {
                        final String appName = (String)((profileNameObj != null) ? profileNameObj : ((String)groupDisplyNameObj));
                        appData.put("APP_GROUP_ID", (Object)String.valueOf(appGroupObj));
                        appData.put("IDENTIFIER", (Object)identifier);
                        appData.put("GROUP_DISPLAY_NAME", (Object)appName);
                        appData.put("APP_TYPE", appType);
                        final int platform = (int)ds.getValue("PLATFORM_TYPE");
                        Object displayImg = (platform == 2) ? "/images/androidicon.png" : "/images/chrome.png";
                        hm.put("path", displayImg);
                        displayImg = MDMApiFactoryProvider.getMDMAuthTokenUtilAPI().getURLWithAuthToken(hm);
                        appData.put("DISPLAY_IMAGE_LOC", (displayImg != null) ? displayImg : "");
                        appData.put("SHOW_APP_ICON", true);
                        ((Hashtable<String, String>)dataProperty).put("dataValue", appName);
                        ((Hashtable<String, String>)dataProperty).put("dataId", appData.toString());
                        dataList.add(dataProperty);
                        addedIdentifier.add(identifier);
                    }
                }
            }
            catch (final Exception ex) {
                AppRepositorySearchHandler.logger.log(Level.WARNING, "Exception occurred while executing query on getSearchApps", ex);
            }
            finally {
                CustomGroupUtil.getInstance().closeConnection(conn, ds);
            }
            return (dataList.size() > 0) ? dataList : null;
        }
        catch (final Exception ex2) {
            AppRepositorySearchHandler.logger.log(Level.WARNING, "Exception occurred while searching apps", ex2);
            return null;
        }
    }
    
    private Criteria getChromeKioskCriteria() {
        final Criteria androidCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)2, 0);
        final Criteria chromeCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)4, 0);
        return androidCriteria.or(chromeCriteria);
    }
    
    static {
        AppRepositorySearchHandler.logger = Logger.getLogger("MDMConfigLogger");
    }
}
