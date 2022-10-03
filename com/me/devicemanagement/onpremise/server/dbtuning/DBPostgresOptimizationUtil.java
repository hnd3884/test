package com.me.devicemanagement.onpremise.server.dbtuning;

import java.util.Hashtable;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.util.Map;
import com.adventnet.db.api.RelationalAPI;
import com.me.devicemanagement.onpremise.start.StartupUtil;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import java.util.List;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import com.me.devicemanagement.onpremise.winaccess.WmiAccessProvider;
import com.adventnet.i18n.I18N;
import java.io.File;
import java.util.Properties;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import java.util.HashMap;
import java.util.logging.Logger;

public class DBPostgresOptimizationUtil
{
    private static Logger dbPostgresOptimizelogger;
    public static final String POSTGRES_EXT_FILE;
    public static final String CONF_POSTGRES_EXT_FILE;
    private static DBPostgresOptimizationUtil dbPgSQLOptimizeUtilhandler;
    
    public static DBPostgresOptimizationUtil getInstance() {
        if (DBPostgresOptimizationUtil.dbPgSQLOptimizeUtilhandler == null) {
            DBPostgresOptimizationUtil.dbPgSQLOptimizeUtilhandler = new DBPostgresOptimizationUtil();
        }
        return DBPostgresOptimizationUtil.dbPgSQLOptimizeUtilhandler;
    }
    
    public Boolean resetPgSQLConfAtStartup() {
        Float defaultMemPercent2DeciPt = 0.0f;
        Float currtMaxMemoryFile2DeciPt = 0.0f;
        Float currtMemoryFilePct2DeciPt = 0.0f;
        Float maxMemoryPct0Dcpt = 0.0f;
        Boolean resetDBSuccess = null;
        Boolean resetFileSuccess = null;
        Boolean isDefaultPgExt = false;
        Boolean ispgExtFileMissing = false;
        Boolean ispgExtFileCorrupted = false;
        Boolean isWmiFailure = false;
        try {
            HashMap postgresExtMap = new HashMap();
            postgresExtMap = this.getComputedRAMDetails();
            defaultMemPercent2DeciPt = Float.valueOf(String.valueOf(postgresExtMap.get("defaultMemPercent2DcPt")));
            maxMemoryPct0Dcpt = Float.valueOf(String.valueOf(postgresExtMap.get("maxMemoryPct0Dcpt")));
            currtMaxMemoryFile2DeciPt = Float.valueOf(String.valueOf(postgresExtMap.get("currtMaxMemoryFile2DeciPt")));
            currtMemoryFilePct2DeciPt = Float.valueOf(String.valueOf(postgresExtMap.get("currentMemFilePct2DcPt")));
            ispgExtFileMissing = Boolean.valueOf(String.valueOf(postgresExtMap.get("ispgExtFileMissing")));
            ispgExtFileCorrupted = Boolean.valueOf(String.valueOf(postgresExtMap.get("ispgExtFileCorrupted")));
            isWmiFailure = Boolean.valueOf(String.valueOf(postgresExtMap.get("isWmiFailure")));
            if (ispgExtFileMissing) {
                return false;
            }
            if (isWmiFailure) {
                return false;
            }
            isDefaultPgExt = this.isDefaultPgExtConfFile();
            if (defaultMemPercent2DeciPt < maxMemoryPct0Dcpt || isDefaultPgExt) {
                SyMUtil.deleteSyMParameter("postgresql_conf_reset");
                DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.INFO, "CheckPgSQLConfAtStartup: Removed the system param: postgresql_conf_reset");
                DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.INFO, "CheckPgSQLConfAtStartup: Resetting the postgres_ext.conf File is not Needed ");
                return false;
            }
            resetDBSuccess = this.deletePostgresServer("postgres", currtMaxMemoryFile2DeciPt, currtMemoryFilePct2DeciPt, DBPostgresOptimizationConstants.DEFAULT_MEMORY_VALUE, defaultMemPercent2DeciPt);
            if (!resetDBSuccess) {
                DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.INFO, "CheckPgSQLConfAtStartup:  Cannot delete the entry in the DbOptimization Table, so unable to Reset the File and Update the System Parameters Accordingly");
                return false;
            }
            resetFileSuccess = this.resetdbPostgresServerProps();
            if (resetFileSuccess) {
                SyMUtil.updateSyMParameter("postgresql_conf_reset", "true");
                DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.INFO, "CheckPgSQLConfAtStartup: Successfully Reset the Postgres_ext.conf File & Updated the system param: postgresql_conf_reset , to: true ");
                return true;
            }
            DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.INFO, "CheckPgSQLConfAtStartup:  Cannot Reset the Postgres_ext.conf File and unable to Update the System Parameters Accordingly");
            return false;
        }
        catch (final Exception e) {
            DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.WARNING, "Problem while Checking the Postgres_ext.conf File during startup", e);
            return false;
        }
    }
    
    public HashMap getComputedRAMDetails() {
        Properties dbPostgresPropDB = new Properties();
        final HashMap postgresExtMap = new HashMap();
        String sysRAMMemory = "";
        Float sysRAMMemFromDB = 0.0f;
        Float sysRAMMemValue = 0.0f;
        Float sysRAMMemInGB = 0.0f;
        Float currtMaxMemoryFromFile = 0.0f;
        Float currentMemoryFilePercent = 0.0f;
        Float currentMemoryValueDB = 0.0f;
        Float currentMemoryPercent = 0.0f;
        final Float currentMemoryPercent2DecimalPt = 0.0f;
        Float currentMemoryPercent0DecimalPt = 0.0f;
        Float currtMaxMemoryFile2DeciPt = 0.0f;
        Float currtMemoryFilePct2DeciPt = 0.0f;
        Float currtMemoryFilePct0DeciPt = 0.0f;
        Float memoryPercent = 0.0f;
        Float defaultMemoryPercent = 0.0f;
        Float maxMemoryValue = 0.0f;
        Float maxMemoryTempPct = 0.0f;
        Float maxMemoryTempPct0DecimalPt = 0.0f;
        Float maxMemoryPct0Dcpt = 0.0f;
        Float maxMemoryValue2DecimalPt = 0.0f;
        Float sysRAMMemInGB2DecimalPt = 0.0f;
        Float memoryPercent2DecimalPt = 0.0f;
        Float defaultMemoryPercent0DecimalPt = 0.0f;
        Float maintenanceMemFile = 0.0f;
        final Float diffFileDBPercent = 0.0f;
        Float maintenanceMemDB = 0.0f;
        String featureErrorMsg = "";
        Boolean ispgExtFileMissing = false;
        Boolean ispgExtFileCorrupted = false;
        Boolean isWmiFailure = false;
        Boolean appliedRAMExceedsSys = false;
        Boolean isDBFileMisMatch = false;
        Boolean resetOption = false;
        Boolean requiredRAMFailure = false;
        try {
            final File pgExtConfFile = new File(DBPostgresOptimizationUtil.POSTGRES_EXT_FILE);
            if (pgExtConfFile.exists()) {
                sysRAMMemory = this.getServerRAMDetails();
                if (sysRAMMemory != null && !sysRAMMemory.isEmpty()) {
                    sysRAMMemValue = Float.valueOf(sysRAMMemory);
                    sysRAMMemInGB = sysRAMMemValue / 1.07374182E9f;
                    sysRAMMemInGB2DecimalPt = this.roundUpTo2DecimalPlaces(sysRAMMemInGB, 2);
                    memoryPercent = sysRAMMemInGB / 100.0f;
                    memoryPercent2DecimalPt = this.roundUpTo2DecimalPlaces(memoryPercent, 2);
                    defaultMemoryPercent = DBPostgresOptimizationConstants.DEFAULT_MEMORY_VALUE / sysRAMMemInGB * 100.0f;
                    final String profileMinMemory = SyMUtil.getServerParameter("postgresMinValue");
                    if (profileMinMemory != null && !profileMinMemory.equals("0")) {
                        defaultMemoryPercent = Float.parseFloat(profileMinMemory) / sysRAMMemInGB * 100.0f;
                    }
                    defaultMemoryPercent0DecimalPt = this.roundUpTo2DecimalPlaces(defaultMemoryPercent, 0);
                    maxMemoryTempPct = DBPostgresOptimizationConstants.POSTGRES_MAX_MEMORY_VALUE / sysRAMMemInGB * 100.0f;
                    maxMemoryTempPct0DecimalPt = this.roundUpTo2DecimalPlaces(maxMemoryTempPct, 0);
                    maxMemoryPct0Dcpt = ((DBPostgresOptimizationConstants.MAX_MEMORY_PERCENT < maxMemoryTempPct0DecimalPt) ? DBPostgresOptimizationConstants.MAX_MEMORY_PERCENT : maxMemoryTempPct0DecimalPt);
                    maxMemoryValue = maxMemoryPct0Dcpt / 100.0f * sysRAMMemInGB;
                    maxMemoryValue2DecimalPt = this.roundUpTo2DecimalPlaces(maxMemoryValue, 2);
                    maintenanceMemFile = this.getParamFromPgExtConf("maintenance_work_mem");
                    currtMaxMemoryFromFile = maintenanceMemFile * 16.0f / 1024.0f;
                    if (currtMaxMemoryFromFile > 0.0) {
                        currtMaxMemoryFile2DeciPt = this.roundUpTo2DecimalPlaces(currtMaxMemoryFromFile, 2);
                        currentMemoryFilePercent = currtMaxMemoryFromFile / sysRAMMemInGB * 100.0f;
                        currtMemoryFilePct0DeciPt = this.roundUpTo2DecimalPlaces(currentMemoryFilePercent, 0);
                        currtMemoryFilePct2DeciPt = this.roundUpTo2DecimalPlaces(currentMemoryFilePercent, 2);
                        if (defaultMemoryPercent0DecimalPt < maxMemoryPct0Dcpt) {
                            if (currtMaxMemoryFile2DeciPt >= sysRAMMemInGB2DecimalPt) {
                                appliedRAMExceedsSys = true;
                                featureErrorMsg = I18N.getMsg("dc.common.dboptimize.exceed_sys_memory", new Object[0]);
                                DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.INFO, featureErrorMsg);
                            }
                            dbPostgresPropDB = this.getRAMDetailsFromDB("postgres");
                            sysRAMMemFromDB = this.getValueFromProps(dbPostgresPropDB, "SYSTEM_TOTAL_MEMORY");
                            currentMemoryValueDB = this.getValueFromProps(dbPostgresPropDB, "MEMORY_ALLOCATED");
                            maintenanceMemDB = Float.valueOf(String.valueOf(Math.round(currentMemoryValueDB * 1024.0f / 16.0)));
                            if (currentMemoryValueDB > 0.0 && sysRAMMemFromDB > 0.0) {
                                resetOption = true;
                                currentMemoryPercent = currentMemoryValueDB / memoryPercent2DecimalPt;
                                currentMemoryPercent0DecimalPt = this.roundUpTo2DecimalPlaces(currentMemoryPercent, 0);
                                if (!maintenanceMemFile.equals(maintenanceMemDB)) {
                                    isDBFileMisMatch = true;
                                    featureErrorMsg += I18N.getMsg("dc.admin.dboptimize.file_db_mismatch", new Object[0]);
                                    DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.INFO, featureErrorMsg);
                                }
                            }
                        }
                        else {
                            requiredRAMFailure = true;
                            featureErrorMsg = I18N.getMsg("dc.admin.dboptimize.RAM_insufficient", new Object[0]);
                            DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.INFO, featureErrorMsg);
                        }
                    }
                    else {
                        ispgExtFileCorrupted = true;
                        featureErrorMsg = I18N.getMsg("dc.admin.dboptimize.file_corrupted", new Object[0]);
                        DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.INFO, featureErrorMsg);
                    }
                }
                else {
                    isWmiFailure = true;
                    featureErrorMsg = I18N.getMsg("dc.admin.dboptimize.WMI_failure", new Object[0]);
                    DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.INFO, featureErrorMsg);
                }
            }
            else {
                ispgExtFileMissing = true;
                featureErrorMsg = I18N.getMsg("dc.common.dboptimize.file_missing", new Object[0]);
                DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.INFO, featureErrorMsg);
            }
            DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.FINE, "System RAM Memory is : " + sysRAMMemValue + "  Bytes");
            DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.FINE, "System RAM Memory is : " + sysRAMMemInGB2DecimalPt + "  GB");
            DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.FINE, "System RAM Memory Percent is : " + memoryPercent2DecimalPt + "  %");
            DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.FINE, "Default RAM Consumed by Postgres is : " + defaultMemoryPercent0DecimalPt + "  %");
            DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.FINE, "Current RAM Consumed by Postgres is : " + currtMemoryFilePct0DeciPt + "  %");
            DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.FINE, "MAX RAM Memory Percent that can be Consumed is : " + maxMemoryPct0Dcpt + "  %");
            DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.FINE, "MAX RAM Memory that can be Consumed is : " + maxMemoryValue + "  GB");
            postgresExtMap.put("systemRamMemInGB", String.valueOf(sysRAMMemInGB2DecimalPt));
            postgresExtMap.put("memoryPercent2DcPt", String.valueOf(memoryPercent2DecimalPt));
            postgresExtMap.put("defaultMemPercent2DcPt", String.valueOf(defaultMemoryPercent0DecimalPt));
            postgresExtMap.put("maxMemoryPct0Dcpt", String.valueOf(maxMemoryPct0Dcpt));
            postgresExtMap.put("maxMemoryValue2DcPt", String.valueOf(maxMemoryValue2DecimalPt));
            postgresExtMap.put("currtMaxMemoryFile2DeciPt", String.valueOf(currtMaxMemoryFile2DeciPt));
            postgresExtMap.put("currentMemFilePct2DcPt", String.valueOf(currtMemoryFilePct0DeciPt));
            postgresExtMap.put("sysRAMMemFromDB", String.valueOf(sysRAMMemFromDB));
            postgresExtMap.put("currentMemoryValueDB", String.valueOf(currentMemoryValueDB));
            postgresExtMap.put("currentMemDBPct2DcPt", String.valueOf(currentMemoryPercent0DecimalPt));
            postgresExtMap.put("ispgExtFileMissing", String.valueOf(ispgExtFileMissing));
            postgresExtMap.put("ispgExtFileCorrupted", String.valueOf(ispgExtFileCorrupted));
            postgresExtMap.put("isWmiFailure", String.valueOf(isWmiFailure));
            postgresExtMap.put("requiredRAMFailure", String.valueOf(requiredRAMFailure));
            postgresExtMap.put("appliedRAMExceedsSys", String.valueOf(appliedRAMExceedsSys));
            postgresExtMap.put("isDBFileMisMatch", String.valueOf(isDBFileMisMatch));
            postgresExtMap.put("resetOption", String.valueOf(resetOption));
        }
        catch (final Exception e) {
            DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.WARNING, "Problem while showing DBPostgresOptimization page", e);
        }
        return postgresExtMap;
    }
    
    public String getServerRAMDetails() {
        String serverSysRAMMemory = null;
        try {
            serverSysRAMMemory = WmiAccessProvider.getInstance().getRAMDetails();
        }
        catch (final Exception e) {
            DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.WARNING, "Problem while Obtaining the System RAM Memory Deatils through WMI/Kernel API in DBPostgresOptimization page", e);
        }
        return serverSysRAMMemory;
    }
    
    public Float getValueFromProps(final Properties dbPostgresPropDB, final String propName) {
        Float valueFromDB = 0.0f;
        try {
            if (dbPostgresPropDB != null && dbPostgresPropDB.get(propName) != null && !((Hashtable<K, Object>)dbPostgresPropDB).get(propName).equals(0.0)) {
                valueFromDB = Float.valueOf(String.valueOf(((Hashtable<K, Object>)dbPostgresPropDB).get(propName)));
            }
        }
        catch (final Exception e) {
            DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.WARNING, "Problem while converting Property Value to Float Type ", e);
        }
        return valueFromDB;
    }
    
    public Float roundUpTo2DecimalPlaces(final Float val, final Integer places) {
        DecimalFormat deciFormatValue2 = null;
        try {
            final DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
            otherSymbols.setDecimalSeparator('.');
            otherSymbols.setGroupingSeparator(',');
            if (places == 0) {
                deciFormatValue2 = new DecimalFormat("###", otherSymbols);
            }
            if (places == 1) {
                deciFormatValue2 = new DecimalFormat("###.#", otherSymbols);
            }
            if (places == 2) {
                deciFormatValue2 = new DecimalFormat("###.##", otherSymbols);
            }
            return Float.valueOf(deciFormatValue2.format(val));
        }
        catch (final Exception e) {
            DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.WARNING, "Problem while Rounding to 2 Decimal Places, the Float Value: " + val + " After Decimal Format the value is:  " + deciFormatValue2 + "  ", e);
            return 0.0f;
        }
    }
    
    public void addOrUpdateDbPostgresOptmize(final String dbName, final Properties props, final Float oldMemoryValueDB, final Float oldMemoryDBPercent, final Float currentMemoryValue, final Float currentMemoryPercent, final Float defaultMemPercent) {
        final String sourceMethod = "addOrUpdateDbPostgresOptmize";
        String remarksText = "dc.admin.dboptimize.enabled_success";
        String userName = "";
        try {
            userName = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
            final DataObject dataObject = this.getDbPostgresOptimizeDO(dbName);
            Row DbPostgresOptmizeRow = null;
            if (dataObject.isEmpty()) {
                DbPostgresOptmizeRow = new Row("DbOptimization");
                DbPostgresOptmizeRow = this.constructDbPostgresOptimizeRow(DbPostgresOptmizeRow, dbName, props);
                dataObject.addRow(DbPostgresOptmizeRow);
                SyMUtil.getPersistence().add(dataObject);
                remarksText = I18N.getMsg(remarksText, new Object[] { defaultMemPercent, DBPostgresOptimizationConstants.DEFAULT_MEMORY_VALUE, currentMemoryPercent, currentMemoryValue, userName });
            }
            else {
                DbPostgresOptmizeRow = dataObject.getFirstRow("DbOptimization");
                DbPostgresOptmizeRow = this.constructDbPostgresOptimizeRow(DbPostgresOptmizeRow, dbName, props);
                dataObject.updateRow(DbPostgresOptmizeRow);
                SyMUtil.getPersistence().update(dataObject);
                remarksText = I18N.getMsg(remarksText, new Object[] { oldMemoryDBPercent, oldMemoryValueDB, currentMemoryPercent, currentMemoryValue, userName });
            }
            DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.INFO, "addOrUpdateDbPostgresOptmize  DbOptimization row updated successfully for the given props: " + props);
            DCEventLogUtil.getInstance().addEvent(121, userName, (HashMap)null, remarksText, (Object)null, true);
            DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.INFO, "addOrUpdateDbPostgresOptmize: An Entry has been made in the Action Log Viewer ");
        }
        catch (final Exception ex) {
            DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.SEVERE, "Caught exception while add/update of DbOptimization. Given props: " + props, ex);
        }
    }
    
    public Properties getRAMDetailsFromDB(final String dbName) {
        Properties dbPostgresOptimizeProp = null;
        try {
            final DataObject dataObject = this.getDbPostgresOptimizeDO(dbName);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("DbOptimization");
                dbPostgresOptimizeProp = new Properties();
                if (row != null) {
                    final List colList = row.getColumns();
                    for (final String colName : colList) {
                        final Object colValue = row.get(colName);
                        if (colValue != null) {
                            ((Hashtable<String, Object>)dbPostgresOptimizeProp).put(colName, colValue);
                        }
                    }
                }
            }
        }
        catch (final Exception ex) {
            DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.WARNING, "Exception while getting Postgres DB optimization Settings from DbOptimization table:", ex);
        }
        return dbPostgresOptimizeProp;
    }
    
    public DataObject getDbPostgresOptimizeDO(final String dbName) throws Exception {
        final SelectQueryImpl query = new SelectQueryImpl(new Table("DbOptimization"));
        query.addSelectColumn(new Column("DbOptimization", "DBOPTIMIZATION_ID"));
        query.addSelectColumn(new Column("DbOptimization", "DB_NAME"));
        query.addSelectColumn(new Column("DbOptimization", "DC_USER_ID"));
        query.addSelectColumn(new Column("DbOptimization", "MEMORY_ALLOCATED"));
        query.addSelectColumn(new Column("DbOptimization", "MEMORY_ALLOCATED_TIME"));
        query.addSelectColumn(new Column("DbOptimization", "SYSTEM_TOTAL_MEMORY"));
        final Criteria criteria = new Criteria(new Column("DbOptimization", "DB_NAME"), (Object)dbName, 0, false);
        query.setCriteria(criteria);
        final DataObject dataObject = SyMUtil.getPersistence().get((SelectQuery)query);
        return dataObject;
    }
    
    public Row constructDbPostgresOptimizeRow(final Row dbPostgresOptmizeRow, final String dbName, final Properties props) throws Exception {
        if (dbName != null) {
            dbPostgresOptmizeRow.set("DB_NAME", (Object)dbName);
        }
        if (props.getProperty("SYSTEM_TOTAL_MEMORY") != null) {
            dbPostgresOptmizeRow.set("SYSTEM_TOTAL_MEMORY", (Object)props.getProperty("SYSTEM_TOTAL_MEMORY"));
        }
        if (props.getProperty("MEMORY_ALLOCATED") != null) {
            dbPostgresOptmizeRow.set("MEMORY_ALLOCATED", (Object)props.getProperty("MEMORY_ALLOCATED"));
        }
        if (props.getProperty("MEMORY_ALLOCATED_TIME") != null) {
            dbPostgresOptmizeRow.set("MEMORY_ALLOCATED_TIME", (Object)props.getProperty("MEMORY_ALLOCATED_TIME"));
        }
        if (props.getProperty("DC_USER_ID") != null) {
            dbPostgresOptmizeRow.set("DC_USER_ID", (Object)props.getProperty("DC_USER_ID"));
        }
        return dbPostgresOptmizeRow;
    }
    
    public Boolean deletePostgresServer(final String dbName, final Float oldMemoryValueDB, final Float oldMemoryDBPercent, final Float defaultMemoryValue, final Float defaultMemoryPercent) {
        String remarksText = "dc.admin.dboptimize.reset_success";
        String userName = "";
        try {
            userName = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
            final Criteria criteria = new Criteria(new Column("DbOptimization", "DB_NAME"), (Object)dbName, 0, false);
            DataAccess.delete(criteria);
            DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.INFO, "Successfully deleted the Postgres DB optimization Settings Row from DbOptimization table:");
            remarksText = I18N.getMsg(remarksText, new Object[] { oldMemoryDBPercent, oldMemoryValueDB, defaultMemoryPercent, defaultMemoryValue, userName });
            DCEventLogUtil.getInstance().addEvent(121, userName, (HashMap)null, remarksText, (Object)null, true);
            DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.INFO, "deletePostgresServer: DB Optimization Settings Reset Entry has been made in the Action Log Viewer ");
            return true;
        }
        catch (final Exception ex) {
            DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.WARNING, "Exception while deleting the Postgres DB optimization Settings Row from DbOptimization table:", ex);
            return false;
        }
    }
    
    public Float getParamFromPgExtConf(final String paramName) {
        try {
            final File pgExtconfFile = new File(DBPostgresOptimizationUtil.POSTGRES_EXT_FILE);
            Properties pgExtConfProp = new Properties();
            String paramText = "";
            String paramValueNumeric = "";
            Float paramValueValue = 0.0f;
            if (!pgExtconfFile.exists() || !pgExtconfFile.canRead()) {
                DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.INFO, "getParamFromPgExtConf: postgres_ext.conf file does not exists/ cannot be Read to get the Param value of " + paramName);
                return 0.0f;
            }
            pgExtConfProp = StartupUtil.getProperties(DBPostgresOptimizationUtil.POSTGRES_EXT_FILE);
            if (!pgExtConfProp.isEmpty()) {
                paramText = pgExtConfProp.getProperty(paramName);
                final Integer indexofHash = paramText.indexOf(35);
                if (indexofHash > -1) {
                    paramText = paramText.substring(0, indexofHash);
                }
                paramValueNumeric = paramText.replaceAll("[^0-9]", "");
                paramValueNumeric = paramValueNumeric.trim();
                paramValueValue = Float.valueOf(paramValueNumeric);
                DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.INFO, "getParamFromPgExtConf: Param Name is : " + paramName + "&  Param value is: " + paramValueValue + " in the postgres_ext.conf file ");
                return paramValueValue;
            }
            DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.INFO, "getParamFromPgExtConf: postgres_ext.conf file does not exists/ cannot be Read to get the Param value of " + paramName);
            return 0.0f;
        }
        catch (final Exception ex) {
            DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.WARNING, "Exception while Reading the postgres_ext.conf file to get the Param Value", ex);
            return 0.0f;
        }
    }
    
    public Boolean isDefaultPgExtConfFile() {
        Float effectiveCacheSize = 0.0f;
        try {
            effectiveCacheSize = this.getParamFromPgExtConf("effective_cache_size");
            if (!effectiveCacheSize.equals(0.0) && effectiveCacheSize.equals(DBPostgresOptimizationConstants.EFFECTIVE_CACHE_SIZE_VALUE)) {
                DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.INFO, "isDefaultPgExtConfFile: postgres_ext.conf file is default one");
                return true;
            }
            DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.INFO, "isDefaultPgExtConfFile: postgres_ext.conf file is not a default one");
            return false;
        }
        catch (final Exception ex) {
            DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.WARNING, "Exception while Reading the postgres_ext.conf file to Check whether it is Default or not", ex);
            return false;
        }
    }
    
    public Long getLastModifiedTimePgExtConf() {
        try {
            final File pgExtconfFile = new File(DBPostgresOptimizationUtil.POSTGRES_EXT_FILE);
            Long pgSQLExtFileLMT = 0L;
            pgSQLExtFileLMT = pgExtconfFile.lastModified();
            return pgSQLExtFileLMT;
        }
        catch (final Exception ex) {
            DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.WARNING, "Exception while Reading the postgres_ext.conf file to get the it's Last Modified Time ", ex);
            return 0L;
        }
    }
    
    public Boolean updatedbPostgresServerProps(final Float currentMemoryValue) {
        try {
            Properties newDbPostgresProps = new Properties();
            final Boolean fileExistsWritable = this.fileExistsOrCreated(DBPostgresOptimizationUtil.POSTGRES_EXT_FILE);
            final String dbPostgresParamComments = "  \n#Shared Buffers - 1/4 of RAM MEMORY IN MB, Maximum 512MB for Windows  \n#Temp Buffers - RAM MEMORY IN MB /32*4  \n#Work Memory - (RAM MEMORY IN MB-Shared_Buffers) /(2*Max_Used_Connections*3)  \n#Effective Cache Size - RAM MEMORY IN MB * 0.75  \n#Maintenance Work Memory - RAM MEMORY IN MB/16   \n#Wal_buffers - 1/32 of the size of shared_buffers, with an upper limit of 16MB  \n#log_line_prefix -  %m - Time stamp with milliseconds  %p - Process ID   %e - SQLSTATE error code  \n#wal_sync_method - Will ensure that the data sent from WAL buffers to Disk are actually persisted";
            if (!fileExistsWritable) {
                DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.INFO, "updatedbPostgresServerProps: postgres_ext.conf file does not exists/ cannot be Written to update the new Parameter values");
                return false;
            }
            newDbPostgresProps = this.setNewDbPostgresParameters(currentMemoryValue);
            if (!newDbPostgresProps.isEmpty()) {
                DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.INFO, "updatedbPostgresServerProps:   postgres_ext.conf file exists to update the new Parameter values:  " + newDbPostgresProps);
                StartupUtil.storeProperties(newDbPostgresProps, DBPostgresOptimizationUtil.POSTGRES_EXT_FILE, dbPostgresParamComments);
                return true;
            }
            return false;
        }
        catch (final Exception ex) {
            DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.WARNING, "Exception while Updating the postgres_ext.conf file for the Max Memory provided", ex);
            return false;
        }
    }
    
    public Boolean resetdbPostgresServerProps() {
        try {
            final File confPostgresExt = new File(DBPostgresOptimizationUtil.CONF_POSTGRES_EXT_FILE);
            final File pgdataPostgresExt = new File(DBPostgresOptimizationUtil.POSTGRES_EXT_FILE);
            if (!pgdataPostgresExt.exists()) {
                pgdataPostgresExt.createNewFile();
            }
            if (confPostgresExt.exists() && pgdataPostgresExt.exists()) {
                SyMUtil.copyFile(confPostgresExt, pgdataPostgresExt);
                return true;
            }
            final Boolean fileExistsWritable = this.fileExistsOrCreated(DBPostgresOptimizationUtil.POSTGRES_EXT_FILE);
            final String defaultDBPostgresParamComments = "  \n#Shared Buffers - 1/4 of RAM MEMORY IN MB, Maximum 512MB for Windows  \n#Temp Buffers - RAM MEMORY IN MB /32*4  \n#Work Memory - (RAM MEMORY IN MB-Shared_Buffers) /(2*Max_Used_Connections*3)  \n#Effective Cache Size - RAM MEMORY IN MB * 0.75  \n#Maintenance Work Memory - RAM MEMORY IN MB/16   \n#Wal_buffers - 1/32 of the size of shared_buffers, with an upper limit of 16MB \n#log_line_prefix -  %t - Time stamp without milliseconds  %p - Process ID  \n#wal_sync_method - Will ensure that the data sent from WAL buffers to Disk are actually persisted";
            Properties defaultDbPostgresProps = new Properties();
            if (fileExistsWritable) {
                defaultDbPostgresProps = this.defaultPostgresParameters(defaultDbPostgresProps);
                DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.INFO, "resetdbPostgresServerProps:   postgres_ext.conf file exists to update the Default Parameter values:  " + defaultDbPostgresProps);
                StartupUtil.storeProperties(defaultDbPostgresProps, DBPostgresOptimizationUtil.POSTGRES_EXT_FILE, defaultDBPostgresParamComments);
                DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.INFO, "resetdbPostgresServerProps:   Successfully reset the postgres_ext.conf to the Default Parameter values:  " + defaultDbPostgresProps);
                return true;
            }
            DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.INFO, "resetdbPostgresServerProps:   postgres_ext.conf file does not exists/ cannot be Written to update to the Default Parameter values");
            return false;
        }
        catch (final Exception ex) {
            DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.WARNING, "Exception while Updating the postgres_ext.conf file with the Default Properties", ex);
            return false;
        }
    }
    
    public Boolean fileExistsOrCreated(final String fileName) {
        try {
            final File configFile = new File(fileName);
            Boolean newFileCreated = null;
            Boolean isFileWriteable = null;
            if (configFile.exists()) {
                isFileWriteable = configFile.canWrite();
                if (isFileWriteable) {
                    DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.INFO, "fileExistsOrCreated: postgres_ext.conf file exists & can be Written to update the new Parameter values");
                    return true;
                }
                DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.INFO, "fileExistsOrCreated: postgres_ext.conf file exists & cannot be Written to update the new Parameter values");
                return false;
            }
            else {
                newFileCreated = configFile.createNewFile();
                isFileWriteable = configFile.canWrite();
                if (newFileCreated && isFileWriteable) {
                    DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.INFO, "fileExistsOrCreated: postgres_ext.conf file does not exists, so creating file to update the new Parameter values");
                    return true;
                }
                return false;
            }
        }
        catch (final Exception ex) {
            DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.WARNING, "Exception while Checking wheterh the postgres_ext.conf file exists or not", ex);
            return false;
        }
    }
    
    public Properties setNewDbPostgresParameters(final Float currentMemoryValue) {
        Properties dbPostgresProps = new Properties();
        try {
            final Long tempBuffers = Math.round(currentMemoryValue * 1024.0f / 128.0);
            final Long tempBuffersValue = (DBPostgresOptimizationConstants.MAX_TEMP_BUFFERS_VALUE < tempBuffers) ? DBPostgresOptimizationConstants.MAX_TEMP_BUFFERS_VALUE : tempBuffers;
            final Long workMem = Math.round((currentMemoryValue * 1024.0f - 512.0f) / 300.0);
            final Long workMemValue = (DBPostgresOptimizationConstants.MAX_WORK_MEM_VALUE < workMem) ? DBPostgresOptimizationConstants.MAX_WORK_MEM_VALUE : workMem;
            final Long effectiveCacheSize = Math.round(currentMemoryValue * 1024.0f * 0.75);
            final Long effectiveCacheSizeValue = (DBPostgresOptimizationConstants.MAX_EFFECTIVE_CACHE_SIZE_VALUE < effectiveCacheSize) ? DBPostgresOptimizationConstants.MAX_EFFECTIVE_CACHE_SIZE_VALUE : effectiveCacheSize;
            final Long maintenanceWorkMem = Math.round(currentMemoryValue * 1024.0f / 16.0);
            final Long maintenanceWorkMemValue = (DBPostgresOptimizationConstants.MAX_MAINTENANCE_WORK_MEM_VALUE < maintenanceWorkMem) ? DBPostgresOptimizationConstants.MAX_MAINTENANCE_WORK_MEM_VALUE : maintenanceWorkMem;
            final Integer minWalSize = 256;
            Integer maxWalSize = 256;
            if (currentMemoryValue <= 4.0) {
                maxWalSize = 768;
            }
            else if (currentMemoryValue > 4.0 && currentMemoryValue <= 8.0) {
                maxWalSize = 960;
            }
            else if (currentMemoryValue > 8.0 && currentMemoryValue <= 12.0) {
                maxWalSize = 1152;
            }
            else if (currentMemoryValue > 12.0 && currentMemoryValue <= 32.0) {
                maxWalSize = 1536;
            }
            else if (currentMemoryValue > 32.0 && currentMemoryValue <= 128.0) {
                maxWalSize = 3072;
            }
            else if (currentMemoryValue > 128.0) {
                maxWalSize = 6144;
            }
            dbPostgresProps.setProperty("min_wal_size", String.valueOf(minWalSize) + "MB");
            if (maxWalSize > 1024) {
                dbPostgresProps.setProperty("max_wal_size", String.valueOf(maxWalSize) + "MB");
            }
            dbPostgresProps.setProperty("temp_buffers", String.valueOf(tempBuffersValue) + "MB");
            dbPostgresProps.setProperty("work_mem", String.valueOf(workMemValue) + "MB");
            dbPostgresProps.setProperty("effective_cache_size", String.valueOf(effectiveCacheSizeValue) + "MB");
            dbPostgresProps.setProperty("maintenance_work_mem", String.valueOf(maintenanceWorkMemValue) + "MB");
            dbPostgresProps = this.newDefaultPostgresParameters(dbPostgresProps);
        }
        catch (final Exception ex) {
            DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.WARNING, "Exception while Constructing Parameters for the Max Memory provided", ex);
        }
        return dbPostgresProps;
    }
    
    public Properties newDefaultPostgresParameters(final Properties newDefaultPostgresProps) {
        try {
            newDefaultPostgresProps.setProperty("listen_addresses", "'*'");
            newDefaultPostgresProps.setProperty("shared_buffers", "512MB");
            newDefaultPostgresProps.setProperty("wal_buffers", "16MB");
            newDefaultPostgresProps.setProperty("checkpoint_completion_target", "0.9");
            newDefaultPostgresProps.setProperty("checkpoint_timeout", "10min");
            newDefaultPostgresProps.setProperty("max_files_per_process", "1000");
            newDefaultPostgresProps.setProperty("logging_collector", "on");
            newDefaultPostgresProps.setProperty("log_directory", "'pg_log'");
            newDefaultPostgresProps.setProperty("log_filename", "'postgresql-%d.log'");
            newDefaultPostgresProps.setProperty("log_truncate_on_rotation", "on");
            newDefaultPostgresProps.setProperty("client_min_messages", "notice");
            newDefaultPostgresProps.setProperty("log_min_messages", "info");
            newDefaultPostgresProps.setProperty("log_min_error_statement", "error");
            newDefaultPostgresProps.setProperty("log_min_duration_statement", "5000");
            newDefaultPostgresProps.setProperty("log_checkpoints", "on");
            newDefaultPostgresProps.setProperty("log_lock_waits", "on");
            newDefaultPostgresProps.setProperty("log_connections", "on");
            newDefaultPostgresProps.setProperty("log_disconnections", "on");
            newDefaultPostgresProps.setProperty("log_hostname", "on");
            newDefaultPostgresProps.setProperty("log_line_prefix", "'%m [%p] [%e] '");
            newDefaultPostgresProps.setProperty("log_statement", "'ddl'");
            newDefaultPostgresProps.setProperty("log_temp_files", "1024");
            newDefaultPostgresProps.setProperty("deadlock_timeout", "50s");
            newDefaultPostgresProps.setProperty("random_page_cost", "2.0");
            newDefaultPostgresProps.setProperty("synchronous_commit", "off");
            newDefaultPostgresProps.setProperty("vacuum_freeze_min_age", "10000000");
            newDefaultPostgresProps.setProperty("wal_sync_method", "fsync_writethrough");
        }
        catch (final Exception ex) {
            DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.WARNING, "Exception while Constructing Parameters for the Max Memory provided", ex);
        }
        return newDefaultPostgresProps;
    }
    
    public Properties defaultPostgresParameters(final Properties defaultPostgresProps) {
        try {
            defaultPostgresProps.setProperty("listen_addresses", "'*'");
            defaultPostgresProps.setProperty("shared_buffers", "512MB");
            defaultPostgresProps.setProperty("temp_buffers", "32MB");
            defaultPostgresProps.setProperty("work_mem", "10MB");
            defaultPostgresProps.setProperty("effective_cache_size", "1338MB");
            defaultPostgresProps.setProperty("maintenance_work_mem", "256MB");
            defaultPostgresProps.setProperty("wal_buffers", "16MB");
            defaultPostgresProps.setProperty("min_wal_size", "256min");
            defaultPostgresProps.setProperty("checkpoint_completion_target", "0.9");
            defaultPostgresProps.setProperty("checkpoint_timeout", "10min");
            defaultPostgresProps.setProperty("max_files_per_process", "1000");
            defaultPostgresProps.setProperty("logging_collector", "on");
            defaultPostgresProps.setProperty("log_directory", "'pg_log'");
            defaultPostgresProps.setProperty("log_filename", "'postgresql-%d.log'");
            defaultPostgresProps.setProperty("log_truncate_on_rotation", "on");
            defaultPostgresProps.setProperty("client_min_messages", "notice");
            defaultPostgresProps.setProperty("log_min_messages", "info");
            defaultPostgresProps.setProperty("log_min_error_statement", "error");
            defaultPostgresProps.setProperty("log_min_duration_statement", "5000");
            defaultPostgresProps.setProperty("log_checkpoints", "on");
            defaultPostgresProps.setProperty("log_lock_waits", "on");
            defaultPostgresProps.setProperty("log_connections", "on");
            defaultPostgresProps.setProperty("log_disconnections", "on");
            defaultPostgresProps.setProperty("log_hostname", "on");
            defaultPostgresProps.setProperty("log_line_prefix", "'%m [%p] [%e] '");
            defaultPostgresProps.setProperty("log_statement", "'ddl'");
            defaultPostgresProps.setProperty("log_temp_files", "1024");
            defaultPostgresProps.setProperty("deadlock_timeout", "50s");
            defaultPostgresProps.setProperty("random_page_cost", "2.0");
            defaultPostgresProps.setProperty("synchronous_commit", "off");
            defaultPostgresProps.setProperty("vacuum_freeze_min_age", "10000000");
            defaultPostgresProps.setProperty("wal_sync_method", "fsync_writethrough");
        }
        catch (final Exception ex) {
            DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.WARNING, "Exception while Constructing Parameters for the Default Memory Value", ex);
        }
        return defaultPostgresProps;
    }
    
    public float tableBloatCalculator() {
        Connection conn = null;
        Statement statement = null;
        ResultSet rs = null;
        try {
            conn = RelationalAPI.getInstance().getConnection();
            statement = conn.createStatement();
            rs = RelationalAPI.getInstance().executeQueryForSQL("-- new table bloat query\n-- still needs work; is often off by +/- 20%\nWITH constants AS (\n    -- define some constants for sizes of things\n    -- for reference down the query and easy maintenance\n    SELECT current_setting('block_size')::numeric AS bs, 23 AS hdr, 8 AS ma\n),\nno_stats AS (\n    -- screen out table who have attributes\n    -- which dont have stats, such as JSON\n    SELECT table_schema, table_name, \n        n_live_tup::numeric as est_rows,\n        pg_table_size(relid)::numeric as table_size\n    FROM information_schema.columns\n        JOIN pg_stat_user_tables as psut\n           ON table_schema = psut.schemaname\n           AND table_name = psut.relname\n        LEFT OUTER JOIN pg_stats\n        ON table_schema = pg_stats.schemaname\n            AND table_name = pg_stats.tablename\n            AND column_name = attname \n    WHERE attname IS NULL\n        AND table_schema NOT IN ('pg_catalog', 'information_schema')\n    GROUP BY table_schema, table_name, relid, n_live_tup\n),\nnull_headers AS (\n    -- calculate null header sizes\n    -- omitting tables which dont have complete stats\n    -- and attributes which aren't visible\n    SELECT\n        hdr+1+(sum(case when null_frac <> 0 THEN 1 else 0 END)/8) as nullhdr,\n        SUM((1-null_frac)*avg_width) as datawidth,\n        MAX(null_frac) as maxfracsum,\n        schemaname,\n        tablename,\n        hdr, ma, bs\n    FROM pg_stats CROSS JOIN constants\n        LEFT OUTER JOIN no_stats\n            ON schemaname = no_stats.table_schema\n            AND tablename = no_stats.table_name\n    WHERE schemaname NOT IN ('pg_catalog', 'information_schema')\n        AND no_stats.table_name IS NULL\n        AND EXISTS ( SELECT 1\n            FROM information_schema.columns\n                WHERE schemaname = columns.table_schema\n                    AND tablename = columns.table_name )\n    GROUP BY schemaname, tablename, hdr, ma, bs\n),\ndata_headers AS (\n    -- estimate header and row size\n    SELECT\n        ma, bs, hdr, schemaname, tablename,\n        (datawidth+(hdr+ma-(case when hdr%ma=0 THEN ma ELSE hdr%ma END)))::numeric AS datahdr,\n        (maxfracsum*(nullhdr+ma-(case when nullhdr%ma=0 THEN ma ELSE nullhdr%ma END))) AS nullhdr2\n    FROM null_headers\n),\ntable_estimates AS (\n    -- make estimates of how large the table should be\n    -- based on row and page size\n    SELECT schemaname, tablename, bs,\n        reltuples::numeric as est_rows, relpages * bs as table_bytes,\n    CEIL((reltuples*\n            (datahdr + nullhdr2 + 4 + ma -\n                (CASE WHEN datahdr%ma=0\n                    THEN ma ELSE datahdr%ma END)\n                )/(bs-20))) * bs AS expected_bytes,\n        reltoastrelid\n    FROM data_headers\n        JOIN pg_class ON tablename = relname\n        JOIN pg_namespace ON relnamespace = pg_namespace.oid\n            AND schemaname = nspname\n    WHERE pg_class.relkind = 'r'\n),\nestimates_with_toast AS (\n    -- add in estimated TOAST table sizes\n    -- estimate based on 4 toast tuples per page because we dont have \n    -- anything better.  also append the no_data tables\n    SELECT schemaname, tablename, \n        TRUE as can_estimate,\n        est_rows,\n        table_bytes + ( coalesce(toast.relpages, 0) * bs ) as table_bytes,\n        expected_bytes + ( ceil( coalesce(toast.reltuples, 0) / 4 ) * bs ) as expected_bytes\n    FROM table_estimates LEFT OUTER JOIN pg_class as toast\n        ON table_estimates.reltoastrelid = toast.oid\n            AND toast.relkind = 't'\n),\ntable_estimates_plus AS (\n-- add some extra metadata to the table data\n-- and calculations to be reused\n-- including whether we cant estimate it\n-- or whether we think it might be compressed\n    SELECT current_database() as databasename,\n            schemaname, tablename, can_estimate, \n            est_rows,\n            CASE WHEN table_bytes > 0\n                THEN table_bytes::NUMERIC\n                ELSE NULL::NUMERIC END\n                AS table_bytes,\n            CASE WHEN expected_bytes > 0 \n                THEN expected_bytes::NUMERIC\n                ELSE NULL::NUMERIC END\n                    AS expected_bytes,\n            CASE WHEN expected_bytes > 0 AND table_bytes > 0\n                AND expected_bytes <= table_bytes\n                THEN (table_bytes - expected_bytes)::NUMERIC\n                ELSE 0::NUMERIC END AS bloat_bytes\n    FROM estimates_with_toast\n    UNION ALL\n    SELECT current_database() as databasename, \n        table_schema, table_name, FALSE, \n        est_rows, table_size,\n        NULL::NUMERIC, NULL::NUMERIC\n    FROM no_stats\n),\nbloat_data AS (\n    -- do final math calculations and formatting\n    select current_database() as databasename,\n        schemaname, tablename, can_estimate, \n        table_bytes, round(table_bytes/(1024^2)::NUMERIC,3) as table_mb,\n        expected_bytes, round(expected_bytes/(1024^2)::NUMERIC,3) as expected_mb,\n        round(bloat_bytes*100/table_bytes) as pct_bloat,\n        round(bloat_bytes/(1024::NUMERIC^2),2) as mb_bloat,\n        table_bytes, expected_bytes, est_rows\n    FROM table_estimates_plus\n)\n-- filter output for bloated tables\nSELECT sum(mb_bloat) FROM bloat_data group by databasename\n-- this where clause defines which tables actually appear\n-- in the bloat chart\n-- example below filters for tables which are either 50%\n-- bloated and more than 20mb in size, or more than 25%\n-- bloated and more than 4GB in size\n", (Map)null, statement);
            if (rs.next()) {
                final Float bloatSize = rs.getFloat("sum");
                return bloatSize;
            }
        }
        catch (final Exception e) {
            DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.INFO, "Error in table bloat calculator " + e.getMessage());
            try {
                if (conn != null) {
                    conn.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (rs != null) {
                    rs.close();
                }
            }
            catch (final Exception e) {
                DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.INFO, "Error in finally block of table bloat calculator " + e.getMessage());
            }
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (rs != null) {
                    rs.close();
                }
            }
            catch (final Exception e2) {
                DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.INFO, "Error in finally block of table bloat calculator " + e2.getMessage());
            }
        }
        return 0.0f;
    }
    
    public float indexBloatCalculator() {
        Connection conn = null;
        Statement statement = null;
        ResultSet rs = null;
        try {
            conn = RelationalAPI.getInstance().getConnection();
            statement = conn.createStatement();
            rs = RelationalAPI.getInstance().executeQueryForSQL("-- btree index stats query\n-- estimates bloat for btree indexes\nWITH btree_index_atts AS (\n    SELECT nspname, \n        indexclass.relname as index_name, \n        indexclass.reltuples, \n        indexclass.relpages, \n        indrelid, indexrelid,\n        indexclass.relam,\n        tableclass.relname as tablename,\n        regexp_split_to_table(indkey::text, ' ')::smallint AS attnum,\n        indexrelid as index_oid\n    FROM pg_index\n    JOIN pg_class AS indexclass ON pg_index.indexrelid = indexclass.oid\n    JOIN pg_class AS tableclass ON pg_index.indrelid = tableclass.oid\n    JOIN pg_namespace ON pg_namespace.oid = indexclass.relnamespace\n    JOIN pg_am ON indexclass.relam = pg_am.oid\n    WHERE pg_am.amname = 'btree' and indexclass.relpages > 0\n         AND nspname NOT IN ('pg_catalog','information_schema')\n    ),\nindex_item_sizes AS (\n    SELECT\n    ind_atts.nspname, ind_atts.index_name, \n    ind_atts.reltuples, ind_atts.relpages, ind_atts.relam,\n    indrelid AS table_oid, index_oid,\n    current_setting('block_size')::numeric AS bs,\n    8 AS maxalign,\n    24 AS pagehdr,\n    CASE WHEN max(coalesce(pg_stats.null_frac,0)) = 0\n        THEN 2\n        ELSE 6\n    END AS index_tuple_hdr,\n    sum( (1-coalesce(pg_stats.null_frac, 0)) * coalesce(pg_stats.avg_width, 1024) ) AS nulldatawidth\n    FROM pg_attribute\n    JOIN btree_index_atts AS ind_atts ON pg_attribute.attrelid = ind_atts.indexrelid AND pg_attribute.attnum = ind_atts.attnum\n    JOIN pg_stats ON pg_stats.schemaname = ind_atts.nspname\n          -- stats for regular index columns\n          AND ( (pg_stats.tablename = ind_atts.tablename AND pg_stats.attname = pg_catalog.pg_get_indexdef(pg_attribute.attrelid, pg_attribute.attnum, TRUE)) \n          -- stats for functional indexes\n          OR   (pg_stats.tablename = ind_atts.index_name AND pg_stats.attname = pg_attribute.attname))\n    WHERE pg_attribute.attnum > 0\n    GROUP BY 1, 2, 3, 4, 5, 6, 7, 8, 9\n),\nindex_aligned_est AS (\n    SELECT maxalign, bs, nspname, index_name, reltuples,\n        relpages, relam, table_oid, index_oid,\n        coalesce (\n            ceil (\n                reltuples * ( 6 \n                    + maxalign \n                    - CASE\n                        WHEN index_tuple_hdr%maxalign = 0 THEN maxalign\n                        ELSE index_tuple_hdr%maxalign\n                      END\n                    + nulldatawidth \n                    + maxalign \n                    - CASE /* Add padding to the data to align on MAXALIGN */\n                        WHEN nulldatawidth::integer%maxalign = 0 THEN maxalign\n                        ELSE nulldatawidth::integer%maxalign\n                      END\n                )::numeric \n              / ( bs - pagehdr::NUMERIC )\n              +1 )\n         , 0 )\n      as expected\n    FROM index_item_sizes\n),\nraw_bloat AS (\n    SELECT current_database() as dbname, nspname, pg_class.relname AS table_name, index_name,\n        bs*(index_aligned_est.relpages)::bigint AS totalbytes, expected,\n        CASE\n            WHEN index_aligned_est.relpages <= expected \n                THEN 0\n                ELSE bs*(index_aligned_est.relpages-expected)::bigint \n            END AS wastedbytes,\n        CASE\n            WHEN index_aligned_est.relpages <= expected\n                THEN 0 \n                ELSE bs*(index_aligned_est.relpages-expected)::bigint * 100 / (bs*(index_aligned_est.relpages)::bigint) \n            END AS realbloat,\n        pg_relation_size(index_aligned_est.table_oid) as table_bytes,\n        stat.idx_scan as index_scans\n    FROM index_aligned_est\n    JOIN pg_class ON pg_class.oid=index_aligned_est.table_oid\n    JOIN pg_stat_user_indexes AS stat ON index_aligned_est.index_oid = stat.indexrelid\n),\nformat_bloat AS (\nSELECT dbname as database_name, nspname as schema_name, table_name, index_name,\n        round(realbloat) as bloat_pct, round(wastedbytes/(1024^2)::NUMERIC) as bloat_mb,\n        round(totalbytes/(1024^2)::NUMERIC,3) as index_mb,\n        round(table_bytes/(1024^2)::NUMERIC,3) as table_mb,\n        index_scans\nFROM raw_bloat\n)\n-- final query outputting the bloated indexes\n-- change the where and order by to change\n-- what shows up as bloated\nSELECT sum(bloat_mb)\nFROM format_bloat group by database_name;", (Map)null, statement);
            if (rs.next()) {
                final Float bloatSize = rs.getFloat("sum");
                return bloatSize;
            }
        }
        catch (final Exception e) {
            DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.INFO, "Error in index bloat calculator " + e.getMessage());
            try {
                if (conn != null) {
                    conn.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (rs != null) {
                    rs.close();
                }
            }
            catch (final Exception e) {
                DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.INFO, "Error in finally block of index bloat calculator " + e.getMessage());
            }
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (rs != null) {
                    rs.close();
                }
            }
            catch (final Exception e2) {
                DBPostgresOptimizationUtil.dbPostgresOptimizelogger.log(Level.INFO, "Error in finally block of index bloat calculator " + e2.getMessage());
            }
        }
        return 0.0f;
    }
    
    static {
        DBPostgresOptimizationUtil.dbPostgresOptimizelogger = Logger.getLogger("DbOptimizationLog");
        POSTGRES_EXT_FILE = System.getProperty("db.home") + File.separator + "ext_conf" + File.separator + "postgres_ext.conf";
        CONF_POSTGRES_EXT_FILE = System.getProperty("server.home") + File.separator + "conf" + File.separator + "postgres_ext.conf";
        DBPostgresOptimizationUtil.dbPgSQLOptimizeUtilhandler = null;
    }
}
