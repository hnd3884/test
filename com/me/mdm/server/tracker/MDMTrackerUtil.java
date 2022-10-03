package com.me.mdm.server.tracker;

import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import java.util.Iterator;
import java.util.Properties;
import com.adventnet.ds.query.CaseExpression;
import java.util.Arrays;
import com.adventnet.ds.query.Criteria;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.LinkedHashMap;
import com.adventnet.sym.server.mdm.util.MDMUtil;

public class MDMTrackerUtil extends MDMUtil
{
    public static LinkedHashMap<String, String> getPayloadMap() {
        DMDataSetWrapper ds = null;
        LinkedHashMap<String, String> payloadMap = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Configuration"));
            selectQuery.addSelectColumn(new Column("Configuration", "CONFIG_NAME"));
            selectQuery.addSelectColumn(new Column("Configuration", "CONFIG_ID"));
            selectQuery.addSortColumn(new SortColumn("Configuration", "CONFIG_ID", true));
            ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
            payloadMap = new LinkedHashMap<String, String>();
            while (ds.next()) {
                payloadMap.put(ds.getValue("CONFIG_NAME").toString(), ds.getValue("CONFIG_ID").toString());
            }
            return payloadMap;
        }
        catch (final Exception ex) {
            Logger.getLogger(MDMTrackerUtil.class.getName()).log(Level.SEVERE, " Exception occurred ", ex);
            return payloadMap;
        }
    }
    
    public static LinkedHashMap<String, String> getVpnTypeMap() {
        final LinkedHashMap<String, String> vpnTypesMap = new LinkedHashMap<String, String>();
        vpnTypesMap.put("VPNL2TP", "0");
        vpnTypesMap.put("VPNIPSEC", "1");
        vpnTypesMap.put("VPNPPTP", "2");
        vpnTypesMap.put("VPNCISCOLEGACY", "3");
        vpnTypesMap.put("VPNJUNIPERSSL", "4");
        vpnTypesMap.put("VPNF5SSL", "5");
        vpnTypesMap.put("VPNCUSTOMSSL", "6");
        vpnTypesMap.put("VPNPULSESECURE", "7");
        vpnTypesMap.put("VPNIKEV2", "8");
        vpnTypesMap.put("VPNCISCO", "9");
        vpnTypesMap.put("VPNSONICWALL", "10");
        vpnTypesMap.put("VPNARUBAVIA", "11");
        vpnTypesMap.put("VPNCHECKPOINT", "12");
        return vpnTypesMap;
    }
    
    public static List<String> getVPNPluginIds() {
        DMDataSetWrapper ds = null;
        List<String> vpnIds = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("VpnCustomSSL"));
            final Column identifierColumn = new Column("VpnCustomSSL", "IDENTIFIER").distinct();
            identifierColumn.setColumnAlias("Identifier");
            selectQuery.addSelectColumn(identifierColumn);
            selectQuery.setCriteria(knownVPNCriteria());
            ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
            vpnIds = new ArrayList<String>();
            while (ds.next()) {
                final String identifier = (String)ds.getValue("Identifier");
                vpnIds.add(identifier);
            }
            return vpnIds;
        }
        catch (final Exception ex) {
            MDMTrackerUtil.logger.log(Level.SEVERE, "Exception in vpnplugin", ex);
            return vpnIds;
        }
    }
    
    public static Criteria knownVPNCriteria() {
        final List<String> knownIdentifier = new ArrayList<String>();
        knownIdentifier.add("com.citrix.NetScalerGateway.ios.app");
        knownIdentifier.add("com.citrix.NetScalerGateway.ios.app");
        knownIdentifier.add("net.openvpn.connect.app");
        knownIdentifier.add("com.f5.access.ios");
        knownIdentifier.add("com.paloaltonetworks.globalprotect.vpn");
        knownIdentifier.add("com.paloaltonetworks.GlobalProtect.vpnplugin");
        return new Criteria(new Column("VpnCustomSSL", "IDENTIFIER"), (Object)knownIdentifier.toArray(), 9);
    }
    
    public static SelectQuery addAppleVersionSummaryCriteriaFromConf(final SelectQuery selectQuery) {
        try {
            final Properties confProperties = MDMUtil.getInstance().getMDMApplicationProperties();
            final String iOSVersion = confProperties.getProperty("IOS_OS_VERSIONS");
            final String macOSVersion = confProperties.getProperty("MAC_OS_VERSIONS");
            final String tvOSVersion = confProperties.getProperty("TV_OS_VERSIONS");
            final MDMTrackerUtil trackerUtil = new MDMTrackerUtil();
            final String[] iOSVersionString = iOSVersion.split(",");
            final String[] macVersionString = macOSVersion.split(",");
            final String[] tvVersionString = tvOSVersion.split(",");
            final List<String> iOSVersionList = Arrays.asList(iOSVersionString);
            final List<String> macVersionList = Arrays.asList(macVersionString);
            final List<String> tvVersionList = Arrays.asList(tvVersionString);
            final Criteria platformTypeCriteria = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)1, 0);
            final Criteria iOSModelCriteria = new Criteria(new Column("MdModelInfo", "MODEL_TYPE"), (Object)new Integer[] { 1, 2, 0 }, 8);
            final Criteria macModelCriteria = new Criteria(new Column("MdModelInfo", "MODEL_TYPE"), (Object)new Integer[] { 3, 4 }, 8);
            final Criteria tvModelCriteria = new Criteria(new Column("MdModelInfo", "MODEL_TYPE"), (Object)5, 0);
            final Criteria iOSBetaProfileCriteria = new Criteria(new Column("IOSConfigPayload", "PAYLOAD_IDENTIFIER"), (Object)"com.apple.appleseedsoftware", 2).or(new Criteria(new Column("IOSConfigPayload", "PAYLOAD_IDENTIFIER"), (Object)"com.apple.applebetasoftware", 2));
            for (final String osVersion : iOSVersionList) {
                final String columnName = "IOS_" + osVersion;
                final Criteria osVersionCriteria = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)("*" + osVersion + "*"), 2);
                final CaseExpression caseExpression = new CaseExpression(columnName);
                caseExpression.addWhen(platformTypeCriteria.and(osVersionCriteria).and(iOSModelCriteria), (Object)new Column("Resource", "RESOURCE_ID"));
                selectQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)caseExpression));
            }
            final CaseExpression betaProfileCaseExpression = new CaseExpression("IOS_BETA_OS");
            betaProfileCaseExpression.addWhen(platformTypeCriteria.and(iOSBetaProfileCriteria).and(iOSModelCriteria), (Object)new Column("Resource", "RESOURCE_ID"));
            selectQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)betaProfileCaseExpression));
            for (final String osVersion2 : macVersionList) {
                final String columnName2 = "MAC_" + osVersion2;
                final Criteria osVersionCriteria2 = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)("*" + osVersion2 + "*"), 2);
                final CaseExpression caseExpression2 = new CaseExpression(columnName2);
                caseExpression2.addWhen(platformTypeCriteria.and(osVersionCriteria2).and(macModelCriteria), (Object)new Column("Resource", "RESOURCE_ID"));
                selectQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)caseExpression2));
            }
            for (final String osVersion2 : tvVersionList) {
                final String columnName2 = "TV_" + osVersion2;
                final Criteria osVersionCriteria2 = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)("*" + osVersion2 + "*"), 2);
                final CaseExpression caseExpression2 = new CaseExpression(columnName2);
                caseExpression2.addWhen(platformTypeCriteria.and(osVersionCriteria2).and(tvModelCriteria), (Object)new Column("Resource", "RESOURCE_ID"));
                selectQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)caseExpression2));
            }
        }
        catch (final Exception e) {
            MDMTrackerUtil.logger.log(Level.SEVERE, "Exception in osversion tracker", e);
        }
        return selectQuery;
    }
    
    public static List<String> getIOSSystemAppList() {
        final List<String> systemAppList = new ArrayList<String>();
        try {
            final DataObject iosSystemAppsObject = AppsUtil.getInstance().getIOSSystemApps(null);
            if (!iosSystemAppsObject.isEmpty()) {
                final Iterator sysAppIterator = iosSystemAppsObject.getRows("IOSSystemApps");
                while (sysAppIterator.hasNext()) {
                    final Row sysAppRow = sysAppIterator.next();
                    final String bundleIdentifier = (String)sysAppRow.get("IDENTIFIER");
                    systemAppList.add(bundleIdentifier);
                }
            }
        }
        catch (final DataAccessException e) {
            MDMTrackerUtil.logger.log(Level.SEVERE, "Exception in getIOS system app list", (Throwable)e);
        }
        return systemAppList;
    }
    
    public static List<String> getWindowsSystemAppList() {
        final List<String> systemAppList = new ArrayList<String>();
        try {
            final DataObject windowsSystemAppsObject = AppsUtil.getInstance().getWindowSystemApps(null);
            if (!windowsSystemAppsObject.isEmpty()) {
                final Iterator sysAppIterator = windowsSystemAppsObject.getRows("WindowsSystemApps");
                while (sysAppIterator.hasNext()) {
                    final Row sysAppRow = sysAppIterator.next();
                    final String bundleIdentifier = (String)sysAppRow.get("PACKAGE_FAMILY_NAME");
                    systemAppList.add(bundleIdentifier);
                }
            }
        }
        catch (final DataAccessException e) {
            MDMTrackerUtil.logger.log(Level.SEVERE, "Exception in get win system app list", (Throwable)e);
        }
        return systemAppList;
    }
    
    public static DerivedTable getIOSScreenLayoutMoreAppsTable() {
        final SelectQuery appQuery = (SelectQuery)new SelectQueryImpl(new Table("AppLockPolicyApps"));
        final Column appCountColumn = new Column("AppLockPolicyApps", "APP_GROUP_ID").count();
        appCountColumn.setColumnAlias("APP_GROUP_ID");
        appQuery.addSelectColumn(appCountColumn);
        appQuery.addSelectColumn(new Column("AppLockPolicyApps", "CONFIG_DATA_ITEM_ID"));
        final Criteria havingiOSMoreAppCriteria = new Criteria(appCountColumn, (Object)10, 4);
        final List groupByColumn = new ArrayList();
        groupByColumn.add(new Column("AppLockPolicyApps", "CONFIG_DATA_ITEM_ID"));
        final GroupByClause groupByClause = new GroupByClause(groupByColumn, havingiOSMoreAppCriteria);
        appQuery.setGroupByClause(groupByClause);
        final DerivedTable appGroupCountTable = new DerivedTable("iOSAppMoreCountTable", (Query)appQuery);
        return appGroupCountTable;
    }
    
    public static DerivedTable getAndroidScreenLayoutMoreAppTable() {
        final SelectQuery appQuery = (SelectQuery)new SelectQueryImpl(new Table("AndroidKioskPolicyApps"));
        final Column appCountColumn = new Column("AndroidKioskPolicyApps", "APP_GROUP_ID").count();
        appCountColumn.setColumnAlias("APP_GROUP_ID");
        appQuery.addSelectColumn(appCountColumn);
        appQuery.addSelectColumn(new Column("AndroidKioskPolicyApps", "CONFIG_DATA_ITEM_ID"));
        final Criteria havingAndroidMoreAppCriteria = new Criteria(appCountColumn, (Object)10, 4);
        final List groupByColumn = new ArrayList();
        groupByColumn.add(new Column("AndroidKioskPolicyApps", "CONFIG_DATA_ITEM_ID"));
        final GroupByClause groupByClause = new GroupByClause(groupByColumn, havingAndroidMoreAppCriteria);
        appQuery.setGroupByClause(groupByClause);
        final DerivedTable appGroupCountTable = new DerivedTable("androidAppMoreCountTable", (Query)appQuery);
        return appGroupCountTable;
    }
    
    public static DerivedTable getAutonomousKioskAppTable() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AppLockPolicyApps"));
        selectQuery.addSelectColumn(new Column("AppLockPolicyApps", "CONFIG_DATA_ITEM_ID"));
        selectQuery.addSelectColumn(new Column("AppLockPolicyApps", "IS_AUTO_KIOSK_ALLOWED"));
        final Criteria autonomousKioskCriteria = new Criteria(new Column("AppLockPolicyApps", "IS_AUTO_KIOSK_ALLOWED"), (Object)true, 0);
        final List groupByColumn = new ArrayList();
        groupByColumn.add(new Column("AppLockPolicyApps", "CONFIG_DATA_ITEM_ID"));
        groupByColumn.add(new Column("AppLockPolicyApps", "IS_AUTO_KIOSK_ALLOWED"));
        final GroupByClause groupByClause = new GroupByClause(groupByColumn, autonomousKioskCriteria);
        selectQuery.setGroupByClause(groupByClause);
        final DerivedTable autoKioskTable = new DerivedTable("autonomousKioskTable", (Query)selectQuery);
        return autoKioskTable;
    }
}
