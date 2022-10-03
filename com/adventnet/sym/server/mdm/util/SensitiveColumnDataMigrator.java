package com.adventnet.sym.server.mdm.util;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import java.util.List;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.time.temporal.TemporalAmount;
import java.time.Duration;
import java.time.Instant;
import com.adventnet.persistence.DataAccess;
import java.util.Map;
import com.adventnet.persistence.WritableDataObject;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.logging.Logger;

public class SensitiveColumnDataMigrator
{
    public static Logger logger;
    
    private static ArrayList<HashMap> addColumnsForScharMigration() {
        final ArrayList<HashMap> data = new ArrayList<HashMap>();
        final HashMap<String, ArrayList<String>> sensitiveColumn = new HashMap<String, ArrayList<String>>();
        addSensitiveColumns("DeviceToken", "TOKEN", sensitiveColumn);
        addSensitiveColumns("AndroidAgentSettings", "RECOVERY_PASSWORD", sensitiveColumn);
        addSensitiveColumns("NotificationDetails", "NOTIFICATION_TOKEN", sensitiveColumn);
        addSensitiveColumns("IosNotificationDetailsExtn", "PUSH_MAGIC", sensitiveColumn);
        addSensitiveColumns("IosNotificationDetailsExtn", "UNLOCK_TOKEN", sensitiveColumn);
        addSensitiveColumns("DEPTokenDetails", "CUSTOMER_SECRET", sensitiveColumn);
        addSensitiveColumns("DEPTokenDetails", "ACCESS_TOKEN", sensitiveColumn);
        addSensitiveColumns("DEPTokenDetails", "ACCESS_SECRET", sensitiveColumn);
        addSensitiveColumns("IOSEnrollmentTemp", "DEVICE_TOKEN", sensitiveColumn);
        addSensitiveColumns("CustomHeadersAuthInfo", "CUSTOM_VALUE", sensitiveColumn);
        addSensitiveColumns("AgentSecretTokens", "TOKEN", sensitiveColumn);
        data.add(sensitiveColumn);
        final HashMap<String, String> migratedColumn = new HashMap<String, String>();
        addMigratedColumns("TOKEN", "TOKEN_ENCRYPTED", migratedColumn);
        addMigratedColumns("RECOVERY_PASSWORD", "RECOVERY_PASSWORD_ENCRYPTED", migratedColumn);
        addMigratedColumns("NOTIFICATION_TOKEN", "NOTIFICATION_TOKEN_ENCRYPTED", migratedColumn);
        addMigratedColumns("PUSH_MAGIC", "PUSH_MAGIC_ENCRYPTED", migratedColumn);
        addMigratedColumns("UNLOCK_TOKEN", "UNLOCK_TOKEN_ENCRYPTED", migratedColumn);
        addMigratedColumns("CUSTOMER_SECRET", "CUSTOMER_SECRET_ENCRYPTED", migratedColumn);
        addMigratedColumns("ACCESS_TOKEN", "ACCESS_TOKEN_ENCRYPTED", migratedColumn);
        addMigratedColumns("ACCESS_SECRET", "ACCESS_SECRET_ENCRYPTED", migratedColumn);
        addMigratedColumns("DEVICE_TOKEN", "DEVICE_TOKEN_ENCRYPTED", migratedColumn);
        addMigratedColumns("CUSTOM_VALUE", "CUSTOM_VALUE_ENCRYPTED", migratedColumn);
        addMigratedColumns("TOKEN", "TOKEN_ENCRYPTED", migratedColumn);
        data.add(migratedColumn);
        final HashMap<String, ArrayList<String>> PKColumn = new HashMap<String, ArrayList<String>>();
        addPKColumns("DeviceToken", "ENROLLMENT_REQUEST_ID", PKColumn);
        addPKColumns("AndroidAgentSettings", "SETTINGS_ID", PKColumn);
        addPKColumns("NotificationDetails", "NOTIFICATION_DETAILS_ID", PKColumn);
        addPKColumns("IosNotificationDetailsExtn", "NOTIFICATION_DETAILS_ID", PKColumn);
        addPKColumns("DEPTokenDetails", "DEP_TOKEN_ID", PKColumn);
        addPKColumns("IOSEnrollmentTemp", "ENROLLMENT_ID", PKColumn);
        addPKColumns("CustomHeadersAuthInfo", "AUTH_ID", PKColumn);
        addPKColumns("CustomHeadersAuthInfo", "CUSTOM_KEY", PKColumn);
        addPKColumns("AgentSecretTokens", "AGENTSECRET_ID", PKColumn);
        data.add(PKColumn);
        return data;
    }
    
    private static void addSensitiveColumns(final String table, final String column, final HashMap<String, ArrayList<String>> sensitiveColumn) {
        if (sensitiveColumn.containsKey(table)) {
            final ArrayList<String> sensitiveColumnTemp = sensitiveColumn.get(table);
            sensitiveColumnTemp.add(column);
            sensitiveColumn.put(table, sensitiveColumnTemp);
        }
        else {
            sensitiveColumn.put(table, new ArrayList<String>() {
                {
                    this.add(column);
                }
            });
        }
    }
    
    private static void addMigratedColumns(final String sensitiveColumn, final String migrateColumn, final HashMap<String, String> migratedColumn) {
        migratedColumn.put(sensitiveColumn, migrateColumn);
    }
    
    private static void addPKColumns(final String table, final String column, final HashMap<String, ArrayList<String>> PKColumn) {
        if (PKColumn.containsKey(table)) {
            final ArrayList<String> PKColumnTemp = PKColumn.get(table);
            PKColumnTemp.add(column);
            PKColumn.put(table, PKColumnTemp);
        }
        else {
            PKColumn.put(table, new ArrayList<String>() {
                {
                    this.add(column);
                }
            });
        }
    }
    
    public static void migrateSensitiveColumns() throws Exception {
        try {
            SensitiveColumnDataMigrator.logger.log(Level.INFO, "----Inside SCHAR MIGRATE SENSITIVE COLUMNS METHOD------");
            final DataObject migrateColumnDataDO = (DataObject)new WritableDataObject();
            final List<HashMap> data = addColumnsForScharMigration();
            final HashMap<String, ArrayList<String>> sensitiveColumn = data.get(0);
            final HashMap<String, String> migratedColumn = data.get(1);
            final HashMap<String, ArrayList<String>> PKColumn = data.get(2);
            for (final Map.Entry<String, ArrayList<String>> sensitiveColumns : sensitiveColumn.entrySet()) {
                final String table = sensitiveColumns.getKey();
                final ArrayList<String> columns = sensitiveColumns.getValue();
                final ArrayList<String> PKColumns = PKColumn.get(table);
                SensitiveColumnDataMigrator.logger.log(Level.INFO, "Table ready for migration is {0}", table);
                migrationHandler(table, columns, migratedColumn, migrateColumnDataDO, PKColumns);
            }
            DataAccess.update(migrateColumnDataDO);
            SensitiveColumnDataMigrator.logger.log(Level.INFO, "Data Accessed Successfully");
        }
        catch (final Exception e) {
            SensitiveColumnDataMigrator.logger.log(Level.SEVERE, "ERROR IN MIGRATING TO SCHAR");
            throw e;
        }
        SensitiveColumnDataMigrator.logger.log(Level.INFO, "Migration To SCHAR completed Successfully");
        final Instant ppmAppliedTime = Instant.now();
        final Duration days = Duration.ofDays(60L);
        final Instant start = Instant.ofEpochSecond(ppmAppliedTime.getEpochSecond());
        final Instant sensitiveDataDeleteScheduledTime = start.plus((TemporalAmount)days);
        SyMUtil.updateSyMParameter("sensitiveDataDeleteScheduledTime", String.valueOf(sensitiveDataDeleteScheduledTime));
        SyMUtil.updateSyMParameter("isSensitiveDataDeleted", "false");
        SensitiveColumnDataMigrator.logger.log(Level.INFO, "SYSTEM PARAM ADDED FOR DELETION OF DATA --SCHAR");
    }
    
    private static void migrationHandler(final String table, final ArrayList<String> sensitiveColumns, final HashMap<String, String> migratedColumn, final DataObject migrateColumnDataDO, final ArrayList<String> PK) throws Exception {
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable(table));
            for (final String sensitiveColumn : sensitiveColumns) {
                final Column column1 = new Column(table, sensitiveColumn);
                sq.addSelectColumn(column1);
            }
            for (final String PKColumn : PK) {
                final Column column2 = new Column(table, PKColumn);
                sq.addSelectColumn(column2);
            }
            final DataObject tableDataObjectDO = MDMUtil.getPersistence().get(sq);
            if (!tableDataObjectDO.isEmpty()) {
                final Iterator i = tableDataObjectDO.getRows(table, (Criteria)null);
                while (i.hasNext()) {
                    SensitiveColumnDataMigrator.logger.log(Level.INFO, "--Inside iterator---");
                    final Row r = i.next();
                    for (final String sensitiveColumn2 : sensitiveColumns) {
                        final String data = (String)r.get(sensitiveColumn2);
                        final String migrateColumn = migratedColumn.get(sensitiveColumn2);
                        r.set(migrateColumn, (Object)data);
                    }
                    tableDataObjectDO.updateRow(r);
                    SensitiveColumnDataMigrator.logger.log(Level.INFO, "----row added to data object ");
                }
            }
            migrateColumnDataDO.merge(tableDataObjectDO);
            SensitiveColumnDataMigrator.logger.log(Level.INFO, "Table added to Data Object is {0}", table);
        }
        catch (final Exception E) {
            SensitiveColumnDataMigrator.logger.log(Level.SEVERE, "---Exception at Migration Handler---Table affected is {0}", table);
            throw E;
        }
    }
    
    public static void sensitiveDataDeleteInDB() throws DataAccessException {
        SensitiveColumnDataMigrator.logger.log(Level.INFO, "Inside sensitiveDataDeleteInDB method");
        final Instant current_time = Instant.now();
        final String sensitiveDataDeleteStatus = SyMUtil.getSyMParameter("isSensitiveDataDeleted");
        if (current_time.isAfter(Instant.parse(SyMUtil.getSyMParameter("sensitiveDataDeleteScheduledTime"))) && sensitiveDataDeleteStatus.equals("false")) {
            try {
                final List<UpdateQuery> updateQueryList = new ArrayList<UpdateQuery>();
                final List<HashMap> data = addColumnsForScharMigration();
                final HashMap<String, ArrayList<String>> sensitiveColumn = data.get(0);
                for (final Map.Entry<String, ArrayList<String>> sensitiveColumns : sensitiveColumn.entrySet()) {
                    final String table = sensitiveColumns.getKey();
                    final ArrayList<String> columns = sensitiveColumns.getValue();
                    final UpdateQuery up = (UpdateQuery)new UpdateQueryImpl(table);
                    for (final String sensitiveColumnData : columns) {
                        final Criteria c = new Criteria(new Column(table, sensitiveColumnData), (Object)null, 1);
                        up.setCriteria(c);
                        up.setUpdateColumn(sensitiveColumnData, (Object)null);
                        updateQueryList.add(up);
                    }
                }
                DataAccess.update((List)updateQueryList);
                SensitiveColumnDataMigrator.logger.log(Level.INFO, "Sensitive data in sensitive columns are deleted successfully");
                SyMUtil.updateSyMParameter("isSensitiveDataDeleted", "true");
            }
            catch (final Exception E) {
                SensitiveColumnDataMigrator.logger.log(Level.SEVERE, "Error in sesnitive data deletion handler");
            }
        }
    }
    
    static {
        SensitiveColumnDataMigrator.logger = Logger.getLogger(SensitiveColumnDataMigrator.class.getName());
    }
}
