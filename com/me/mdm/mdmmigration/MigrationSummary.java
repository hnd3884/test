package com.me.mdm.mdmmigration;

import com.adventnet.ds.query.Join;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.simple.JSONArray;
import java.util.List;
import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.server.enrollment.admin.migration.IOSMigrationEnrollmentHandler;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.GroupByClause;
import java.util.ArrayList;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class MigrationSummary
{
    private static Logger logger;
    
    public JSONObject devicesSummary(final JSONObject request) {
        final Long config_id = APIUtil.getResourceID(request, "summar_id");
        final Criteria configIdCriteria = new Criteria(new Column("MigrationDevices", "CONFIG_ID"), (Object)config_id, 0);
        JSONObject response;
        try {
            SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MigrationDevices"));
            selectQuery.setCriteria(configIdCriteria);
            final Column countColumn = new Column("MigrationDevices", "OS").count();
            countColumn.setColumnAlias("Count");
            selectQuery.addSelectColumn(countColumn);
            selectQuery.addSelectColumn(Column.getColumn("MigrationDevices", "OS"));
            selectQuery.addSelectColumn(Column.getColumn("MigrationDevices", "MANUFACTURER"));
            List groupList = new ArrayList();
            groupList.add(new Column("MigrationDevices", "MANUFACTURER"));
            groupList.add(new Column("MigrationDevices", "OS"));
            final GroupByClause groupByClause = new GroupByClause(groupList);
            selectQuery.setGroupByClause(groupByClause);
            final DMDataSetWrapper dataSet = DMDataSetWrapper.executeQuery((Object)selectQuery);
            int iOSDevices = 0;
            int samsungDevices = 0;
            int androidDevices = 0;
            int windowsDevices = 0;
            while (dataSet.next()) {
                final String osName = dataSet.getValue("OS").toString();
                final String manufacturer = "";
                if (dataSet.getValue("MANUFACTURER") != null) {
                    String.valueOf(dataSet.getValue("MANUFACTURER"));
                }
                if (osName.startsWith("iOS")) {
                    iOSDevices += (int)dataSet.getValue(1);
                }
                else if (osName.startsWith("Android")) {
                    if (manufacturer.contains("Samsung") || manufacturer.contains("SM")) {
                        samsungDevices += (int)dataSet.getValue(1);
                    }
                    else {
                        androidDevices += (int)dataSet.getValue(1);
                    }
                }
                else {
                    if (!osName.startsWith("Windows") && !osName.startsWith("Microsoft")) {
                        continue;
                    }
                    windowsDevices += (int)dataSet.getValue(1);
                }
            }
            final JSONObject devicesCount = new JSONObject();
            selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MigrationDevices"));
            selectQuery.setCriteria(configIdCriteria);
            groupList = new ArrayList();
            groupList.add(new Column("MigrationDevices", "DEVICE_ID"));
            groupList.add(new Column("MigrationDevices", "MIGRATION_SERVER_DEVICE_ID"));
            groupList.add(new Column("MigrationDevices", "CONFIG_ID"));
            groupList.add(new Column("MigrationDevices", "UDID"));
            groupList.add(new Column("MigrationDevices", "EAS_ID"));
            groupList.add(new Column("MigrationDevices", "IMEI"));
            groupList.add(new Column("MigrationDevices", "DEVICE_NAME"));
            groupList.add(new Column("MigrationDevices", "OS"));
            groupList.add(new Column("MigrationDevices", "MODEL"));
            groupList.add(new Column("MigrationDevices", "MIGRATION_SERIAL_ID"));
            groupList.add(new Column("MigrationDevices", "MANUFACTURER"));
            groupList.add(new Column("MigrationDevices", "DEVICE_TYPE"));
            selectQuery.setGroupByClause(new GroupByClause(groupList));
            final int numberOfDevices = DBUtil.getRecordCount(selectQuery, "MigrationDevices", "DEVICE_ID");
            devicesCount.put("totalDevices", numberOfDevices);
            final JSONObject fetchedDevices = new JSONObject();
            final JSONObject adminEnrollmentDetails = new IOSMigrationEnrollmentHandler(50).getEnrollmentDetails(request);
            final JSONObject summaryObject = new JSONObject();
            summaryObject.put("totalDevices", numberOfDevices);
            final JSONObject deviceDetails = new JSONObject();
            deviceDetails.put("iOS devices", iOSDevices);
            final JSONObject androidDevicesDetails = new JSONObject();
            androidDevicesDetails.put("Samsung", samsungDevices);
            androidDevicesDetails.put("Others", androidDevices);
            deviceDetails.put("Android", (Object)androidDevicesDetails);
            deviceDetails.put("Windows", windowsDevices);
            fetchedDevices.put("devices", (Object)deviceDetails);
            summaryObject.put("fetched", (Object)fetchedDevices);
            final JSONObject migratedDetails = new JSONObject();
            migratedDetails.put("enrolledDevicesCount", adminEnrollmentDetails.get("adminEnrolledDeviceCount"));
            summaryObject.put("migrated", (Object)migratedDetails);
            response = summaryObject;
        }
        catch (final Exception e) {
            MigrationSummary.logger.log(Level.SEVERE, "Error while getting migration devices from DB", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return response;
    }
    
    public JSONObject usersSummary(final JSONObject request) {
        final Long config_id = APIUtil.getResourceID(request, "summar_id");
        final Criteria configIdCriteria = new Criteria(new Column("MigrationUsers", "CONFIG_ID"), (Object)config_id, 0);
        final JSONObject response = new JSONObject();
        try {
            final int count = DBUtil.getRecordCount("MigrationUsers", "USER_ID", configIdCriteria);
            response.put("total_users", count);
        }
        catch (final Exception e) {
            MigrationSummary.logger.log(Level.SEVERE, "Error while getting migration users from DB");
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return response;
    }
    
    public JSONObject groupsSummary(final JSONObject request) {
        final Long config_id = APIUtil.getResourceID(request, "summar_id");
        final Criteria configIdCriteria = new Criteria(new Column("MigrationGroups", "CONFIG_ID"), (Object)config_id, 0);
        final JSONObject response = new JSONObject();
        try {
            final int count = DBUtil.getRecordCount("MigrationGroups", "GROUP_ID", configIdCriteria);
            response.put("total_groups", count);
        }
        catch (final Exception e) {
            MigrationSummary.logger.log(Level.SEVERE, "Error while getting migration users from DB");
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return response;
    }
    
    public JSONArray profilesSummary(final JSONObject request) {
        final Long config_id = APIUtil.getResourceID(request, "summar_id");
        final JSONArray profileSummaryArray = new JSONArray();
        try {
            final DataObject dobj = MDMUtil.getPersistence().get("ProfileMigrationSummary", new Criteria(new Column("ProfileMigrationSummary", "CONFIG_ID"), (Object)config_id, 0));
            if (dobj != null && !dobj.isEmpty()) {
                final Iterator profileItr = dobj.getRows("ProfileMigrationSummary");
                while (profileItr != null && profileItr.hasNext()) {
                    final Row profileRow = profileItr.next();
                    final JSONObject profile = new JSONObject();
                    profile.put("profile_name", profileRow.get("PROFILE_NAME"));
                    profile.put("isMigrated", profileRow.get("IS_MIGRATED"));
                    profile.put("remarks", profileRow.get("REMARKS"));
                    profile.put("profile_id", profileRow.get("PROFILE_ID"));
                    profile.put("old_profile_id", profileRow.get("SERVER_PROFILE_ID"));
                    profile.put("config_id", profileRow.get("CONFIG_ID"));
                    profileSummaryArray.add((Object)profile);
                }
            }
        }
        catch (final Exception e) {
            MigrationSummary.logger.log(Level.SEVERE, "Error while getting migration profiles from DB");
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return profileSummaryArray;
    }
    
    public JSONArray appsSummary(final JSONObject request) {
        final Long config_id = APIUtil.getResourceID(request, "summar_id");
        final JSONArray appSummaryArray = new JSONArray();
        try {
            final DataObject dobj = MDMUtil.getPersistence().get("AppMigrationSummary", new Criteria(new Column("AppMigrationSummary", "CONFIG_ID"), (Object)config_id, 0));
            if (dobj != null && !dobj.isEmpty()) {
                final Iterator appItr = dobj.getRows("AppMigrationSummary");
                while (appItr != null && appItr.hasNext()) {
                    final Row appRow = appItr.next();
                    final JSONObject profile = new JSONObject();
                    profile.put("app_title", appRow.get("APP_TITLE"));
                    profile.put("bundle_identifier", appRow.get("BUNDLE_IDENTIFIER"));
                    profile.put("platform_type", appRow.get("PLATFORM_TYPE"));
                    profile.put("release_labels", appRow.get("RELEASE_LABEL"));
                    profile.put("isMigrated", appRow.get("IS_MIGRATED"));
                    profile.put("remarks", appRow.get("REMARKS"));
                    profile.put("app_id", appRow.get("APP_ID"));
                    profile.put("old_app_id", appRow.get("SERVER_APP_ID"));
                    profile.put("config_id", appRow.get("CONFIG_ID"));
                    appSummaryArray.add((Object)profile);
                }
            }
        }
        catch (final Exception e) {
            MigrationSummary.logger.log(Level.SEVERE, "Error while getting migration apps from DB");
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return appSummaryArray;
    }
    
    public JSONObject migrationCountSummary(final Long config_id, final Long customer_id) {
        final JSONObject response = new JSONObject();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MDMServerMigrationStatus"));
            selectQuery.addJoin(new Join("MDMServerMigrationStatus", "CustomerAPIServiceConfigAssociation", new String[] { "CONFIG_ID" }, new String[] { "CONFIG_ID" }, 1));
            selectQuery.setCriteria(new Criteria(new Column("CustomerAPIServiceConfigAssociation", "CUSTOMER_ID"), (Object)customer_id, 0).and(new Criteria(new Column("MDMServerMigrationStatus", "CONFIG_ID"), (Object)config_id, 0)));
            selectQuery.addSelectColumn(new Column("MDMServerMigrationStatus", "*"));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            final Row row = dataObject.getFirstRow("MDMServerMigrationStatus");
            response.put("total_devices", row.get("DEVICES_COUNT"));
            response.put("total_users", row.get("USERS_COUNT"));
            response.put("total_groups", row.get("GROUPS_COUNT"));
            response.put("total_profiles", row.get("PROFILES_COUNT"));
            response.put("total_apps", row.get("APPS_COUNT"));
            response.put("migrated_devices", row.get("MIGRATED_DEVICES_COUNT"));
            response.put("migrated_users", row.get("MIGRATED_USERS_COUNT"));
            response.put("migrated_groups", row.get("MIGRATED_GROUPS_COUNT"));
            response.put("migrated_profiles", row.get("MIGRATED_PROFILES_COUNT"));
            response.put("migrated_apps", row.get("MIGRATED_APPS_COUNT"));
        }
        catch (final Exception e) {
            MigrationSummary.logger.log(Level.SEVERE, "Error while getting migration count from DB");
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return response;
    }
    
    static {
        MigrationSummary.logger = Logger.getLogger("MDMMigrationLogger");
    }
}
