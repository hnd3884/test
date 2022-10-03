package com.me.mdm.server.profiles.config;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.List;
import java.util.Iterator;
import org.json.JSONObject;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import org.json.JSONArray;
import com.adventnet.persistence.DataObject;

public class WindowsKioskConfigHandler extends DefaultConfigHandler
{
    @Override
    protected JSONArray DOToAPIJSON(final DataObject dataObject, final String configName, final String tableName) throws APIHTTPException {
        try {
            if (!dataObject.isEmpty() && tableName != null && !tableName.isEmpty()) {
                final JSONArray result = super.DOToAPIJSON(dataObject, configName, tableName);
                if (result != null && result.length() != 0) {
                    final JSONObject jsonObject = result.getJSONObject(0);
                    Iterator iterator = dataObject.getRows("WindowsKioskPolicyApps");
                    final List<Long> appGroupIDList = new ArrayList<Long>();
                    while (iterator.hasNext()) {
                        final Row row = iterator.next();
                        appGroupIDList.add((Long)row.get("APP_GROUP_ID"));
                    }
                    iterator = dataObject.getRows("WindowsKioskPolicySystemApps");
                    final List<Long> systemAppGroupIDList = new ArrayList<Long>();
                    while (iterator.hasNext()) {
                        final Row row2 = iterator.next();
                        systemAppGroupIDList.add((Long)row2.get("APP_ID"));
                    }
                    jsonObject.put("allowed_apps", (Object)this.getAppInformation(appGroupIDList, systemAppGroupIDList).toString());
                }
                return result;
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "exception occurred in DOtoAPIJSON WindowsKioskConfigHandler");
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return null;
    }
    
    public JSONArray getAppInformation(final List<Long> appGroupID, final List<Long> systemAppGroupIDList) {
        final JSONArray containerApps = new JSONArray();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
            final Join appToGroupJoin = new Join("MdAppGroupDetails", "MdAppToGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
            final Join appJoin = new Join("MdAppToGroupRel", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2);
            final Join appToClnJoin = new Join("MdAppDetails", "WindowsAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2);
            final Join pkgJoin = new Join("MdAppGroupDetails", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1);
            selectQuery.addJoin(appToGroupJoin);
            selectQuery.addJoin(appJoin);
            selectQuery.addJoin(appToClnJoin);
            selectQuery.addJoin(pkgJoin);
            selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"));
            selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "GROUP_DISPLAY_NAME"));
            selectQuery.addSelectColumn(Column.getColumn("WindowsAppDetails", "AUMID"));
            selectQuery.addSelectColumn(Column.getColumn("WindowsAppDetails", "PHONE_PRODUCT_ID"));
            selectQuery.addSelectColumn(Column.getColumn("WindowsAppDetails", "PRODUCT_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "PACKAGE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "APP_GROUP_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"));
            final Criteria criteria = new Criteria(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroupID.toArray(), 8);
            selectQuery.setCriteria(criteria);
            try {
                final org.json.simple.JSONArray apps = MDMUtil.executeSelectQuery(selectQuery);
                final List<String> addedIdentifier = new ArrayList<String>();
                for (final org.json.simple.JSONObject app : apps) {
                    final JSONObject appData = new JSONObject();
                    final Object appGroupObj = app.get((Object)"APP_GROUP_ID");
                    final Object identifierObj = app.get((Object)"IDENTIFIER");
                    final Object groupDisplyNameObj = app.get((Object)"GROUP_DISPLAY_NAME");
                    final Object aumid = app.get((Object)"AUMID");
                    final Object phoneProductID = app.get((Object)"PHONE_PRODUCT_ID");
                    final Object packageID = app.get((Object)"PACKAGE_ID");
                    final Object isPurchasedFromProtal = app.get((Object)"IS_PURCHASED_FROM_PORTAL");
                    final Object storeID = app.get((Object)"PRODUCT_ID");
                    final String identifier = (String)identifierObj;
                    if (!addedIdentifier.contains(identifier)) {
                        final String appName = (String)groupDisplyNameObj;
                        appData.put("app_id", (Object)appGroupObj);
                        appData.put("IDENTIFIER".toLowerCase(), (Object)identifier);
                        appData.put("GROUP_DISPLAY_NAME".toLowerCase(), (Object)appName);
                        appData.put("aum_id", aumid);
                        if (packageID != null) {
                            appData.put("app_scope", (Object)"managed");
                            appData.put("enterprise_app", !(boolean)isPurchasedFromProtal);
                        }
                        appData.put("product_id", phoneProductID);
                        if (storeID != null) {
                            appData.put("store_id", storeID);
                        }
                        containerApps.put((Object)appData);
                        addedIdentifier.add(identifier);
                    }
                }
                final SelectQuery selectQuery2 = (SelectQuery)new SelectQueryImpl(new Table("WindowsSystemApps"));
                selectQuery2.addSelectColumn(Column.getColumn("WindowsSystemApps", "*"));
                selectQuery2.setCriteria(new Criteria(Column.getColumn("WindowsSystemApps", "APP_ID"), (Object)systemAppGroupIDList.toArray(), 8));
                final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery2);
                final Iterator iterator = dataObject.getRows("WindowsSystemApps");
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final JSONObject appData2 = new JSONObject();
                    final Long appGroupObj2 = (Long)row.get("APP_ID");
                    final String identifierObj2 = (String)row.get("PACKAGE_FAMILY_NAME");
                    final String groupDisplyNameObj2 = (String)row.get("APP_NAME");
                    final String aumid2 = (String)row.get("AUMID");
                    final String phoneProductID2 = (String)row.get("PHONE_PRODUCT_ID");
                    appData2.put("app_id", (Object)appGroupObj2);
                    appData2.put("IDENTIFIER".toLowerCase(), (Object)identifierObj2);
                    appData2.put("GROUP_DISPLAY_NAME".toLowerCase(), (Object)groupDisplyNameObj2);
                    appData2.put("aum_id", (Object)aumid2);
                    appData2.put("product_id", (Object)phoneProductID2);
                    appData2.put("is_system_app", true);
                    containerApps.put((Object)appData2);
                }
            }
            catch (final Exception ex) {
                this.logger.log(Level.WARNING, "Exception occurred while executing query on getSearchApps", ex);
            }
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception in getAppInformation ", ex2);
        }
        return containerApps;
    }
}
