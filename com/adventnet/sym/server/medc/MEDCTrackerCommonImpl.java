package com.adventnet.sym.server.medc;

import java.util.Set;
import com.me.devicemanagement.framework.utils.JsonUtils;
import com.me.devicemanagement.onpremise.server.util.UpdatesParamUtil;
import com.adventnet.ds.query.Range;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.devicemanagement.framework.server.util.DCPluginUtil;
import com.me.ems.onpremise.server.util.NotifyUpdatesUtil;
import com.adventnet.ds.query.QueryConstructionException;
import java.sql.SQLException;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import com.adventnet.persistence.DataAccess;
import java.util.Locale;
import com.adventnet.ds.query.Join;
import java.util.Date;
import java.sql.Timestamp;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Hashtable;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.onpremise.server.task.UploadDebugFileTask;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import com.me.devicemanagement.onpremise.start.StartupUtil;
import java.io.File;
import com.me.devicemanagement.framework.server.deletionfw.DeletionTaskMetrack;
import com.adventnet.ds.query.Column;
import java.util.Calendar;
import com.me.devicemanagement.onpremise.server.dbtuning.DBPostgresOptimizationUtil;
import java.util.HashMap;
import com.me.devicemanagement.onpremise.server.metrack.METrackerUtil;
import org.json.JSONObject;
import java.util.LinkedHashMap;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.onpremise.server.util.DBUtil;
import com.me.devicemanagement.onpremise.start.util.WebServerUtil;
import com.adventnet.sym.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;
import java.util.Properties;
import com.me.devicemanagement.onpremise.server.metrack.MEDMTracker;

public class MEDCTrackerCommonImpl implements MEDMTracker
{
    private Properties commonTrackerProperties;
    private Logger logger;
    private String sourceClass;
    private static final String SDP_SETTINGS = "SDP_Settings";
    private static final String MDM_SETTINGS = "MDM_Settings";
    private static final String IS_PLUGIN = "Is_Plugin";
    private static final String PRODUCT_CODE = "Pro_Code";
    private static final String DC_MIGRATION_DIR = "dcarchmigration";
    private static final String DC_32BIT_TO_64BIT = "32bitTo64bit";
    private static final String DC_64BIT_TO_32BIT = "64bitTo32bit";
    private static final String DC_MIGRATION_INFO_FILE = "dcarchmigrationinfo.props";
    public static final String KILLED_OPEN_TRANSACTIONS_MSSQL = "open_transaction_killed_in_mssql";
    public static final String SNAPSHOTENABLED = "is_read_committed_snapshot_on";
    public static final String INSTALLER_SELECTED_LANG = "InstallerSelectedLang";
    public static final String PPM_FAILURE_DETAILS = "ppm_failure_details";
    public static final String PROXY_TYPE = "Proxy_Type";
    public static final String REVIEW_BANNER_DETAILS = "Review_Banner_Details";
    public static final String REVIEW_BANNER_DO_NOT_SHOW = "doNotShow";
    public static final String REVIEW_BANNER_FIRST_REMIND_LATER = "firstRemindLater";
    public static final String REVIEW_BANNER_REMIND_LATER = "remindLater";
    public static final String REVIEW_BANNER_ALREADY_REVIEWED = "alreadyReviewed";
    public static final String REVIEW_BANNER_CLICK_COUNT = "bannerClickCount";
    
    public MEDCTrackerCommonImpl() {
        this.commonTrackerProperties = new Properties();
        this.logger = Logger.getLogger("METrackLog");
        this.sourceClass = "MEDCCommonImpl";
    }
    
    public Properties getTrackerProperties() {
        SyMLogger.info(this.logger, this.sourceClass, "getProperties", "Common params implementation starts...");
        this.updateCommonProperties();
        return this.getCommonTrackerProperties();
    }
    
    private void updateCommonProperties() {
        try {
            if (!this.commonTrackerProperties.isEmpty()) {
                this.commonTrackerProperties = new Properties();
            }
            try {
                this.commonTrackerProperties.setProperty("Language", SyMUtil.getInstallationProperty("lang"));
                this.commonTrackerProperties.setProperty("Login_Count", SyMUtil.getInstallationProperty("lc"));
                this.commonTrackerProperties.setProperty("SDP_Settings", SyMUtil.getInstallationProperty("sdp"));
                this.commonTrackerProperties.setProperty("MDM_Settings", SyMUtil.getInstallationProperty("mdm"));
                this.commonTrackerProperties.setProperty("Last_Login", this.getTimeFromString(SyMUtil.getInstallationProperty("ll")));
                this.commonTrackerProperties.setProperty("Build_Number", SyMUtil.getProductProperty("buildnumber"));
                this.commonTrackerProperties.setProperty("Installation_Time", this.getTimeFromString(SyMUtil.getInstallationProperty("it")));
                this.commonTrackerProperties.setProperty("Installation_Timestamp", SyMUtil.getInstallationProperty("it"));
                this.commonTrackerProperties.setProperty("OS_Name", SyMUtil.getValueFromSystemLogFile("OS"));
                this.commonTrackerProperties.setProperty("InstallerSelectedLang", SyMUtil.getValueFromSystemLogFile("InstallerSelectedLang"));
                this.commonTrackerProperties.setProperty("JRE_Version", System.getProperty("java.runtime.version"));
                this.commonTrackerProperties.setProperty("Apache_Version", WebServerUtil.getApacheVersion());
            }
            catch (final Exception e) {
                SyMLogger.info(this.logger, this.sourceClass, "updateCommonProperties", "Exception e is :" + e);
            }
            try {
                Object install_build_num = DBUtil.getMinOfValue("DCServerBuildHistory", "BUILD_NUMBER", (Criteria)null);
                install_build_num = ((install_build_num == null) ? "0" : install_build_num);
                this.commonTrackerProperties.setProperty("Install_Build_Num", install_build_num.toString());
            }
            catch (final Exception e2) {
                SyMLogger.info(this.logger, this.sourceClass, "updateCommonProperties", "Exception e1 is :" + e2);
            }
            try {
                final LinkedHashMap dbProbs = (LinkedHashMap)DBUtil.getDBServerProperties();
                String dbVersion = "-";
                String dbArch = "-";
                if (dbProbs.get("db.version") != null) {
                    dbVersion = String.valueOf(dbProbs.get("db.version"));
                }
                if (dbProbs.get("db.arch") != null) {
                    dbArch = String.valueOf(dbProbs.get("db.arch")).trim();
                }
                final JSONObject dbDetailsjsonObj = new JSONObject();
                dbDetailsjsonObj.put("DBVersion", (Object)dbVersion);
                dbDetailsjsonObj.put("DBArch", (Object)dbArch);
                final Properties DBSizeProps = METrackerUtil.getMETrackParams("DBSizeInMB");
                if (DBSizeProps != null) {
                    Integer dbSize = 0;
                    if (DBSizeProps.containsKey("DBSizeInMB")) {
                        dbSize = Integer.parseInt(DBSizeProps.getProperty("DBSizeInMB"));
                    }
                    dbDetailsjsonObj.put("DBSizeInMB", (Object)dbSize);
                }
                final Properties liveConnectionProps = METrackerUtil.getMETrackParams("NumberOfLiveConnections");
                if (liveConnectionProps != null) {
                    int numberOfLiveConnections = 0;
                    if (liveConnectionProps.containsKey("NumberOfLiveConnections") && liveConnectionProps.getProperty("NumberOfLiveConnections") != null) {
                        numberOfLiveConnections = Integer.parseInt(liveConnectionProps.getProperty("NumberOfLiveConnections"));
                    }
                    dbDetailsjsonObj.put("NumberOfLiveConnections", numberOfLiveConnections);
                }
                final String activeDb = DBUtil.getActiveDBName();
                final boolean isRemoteDB = DBUtil.isRemoteDB();
                dbDetailsjsonObj.put("isRemoteDatabase", (Object)Boolean.toString(isRemoteDB));
                if (activeDb.equalsIgnoreCase("postgres") && !isRemoteDB) {
                    HashMap postgresExtMap = new HashMap();
                    postgresExtMap = DBPostgresOptimizationUtil.getInstance().getComputedRAMDetails();
                    final Float pgSQLMaxMem = Float.valueOf(String.valueOf(postgresExtMap.get("currtMaxMemoryFile2DeciPt")));
                    if (pgSQLMaxMem <= 0.0f) {
                        dbDetailsjsonObj.put("PgSQLMaxMem", (Object)"-1.0");
                    }
                    else {
                        dbDetailsjsonObj.put("PgSQLMaxMem", (Object)pgSQLMaxMem);
                    }
                    Long pgSQLMemLastModifiedTime = DBPostgresOptimizationUtil.getInstance().getLastModifiedTimePgExtConf();
                    if (pgSQLMemLastModifiedTime <= 0L) {
                        pgSQLMemLastModifiedTime = -1L;
                    }
                    dbDetailsjsonObj.put("PgSQLMemModTime", (Object)pgSQLMemLastModifiedTime);
                    Boolean pgconfFileMissing = false;
                    pgconfFileMissing = Boolean.valueOf(String.valueOf(postgresExtMap.get("ispgExtFileMissing")));
                    final Boolean isDefaultPgConf = DBPostgresOptimizationUtil.getInstance().isDefaultPgExtConfFile();
                    String pgSQLOptimized = "N";
                    if (!isDefaultPgConf && !pgconfFileMissing) {
                        pgSQLOptimized = "Y";
                    }
                    dbDetailsjsonObj.put("PgSQLDbTuned", (Object)pgSQLOptimized);
                }
                if (activeDb.equalsIgnoreCase("mssql")) {
                    final JSONObject jsonObject = dbDetailsjsonObj;
                    final String s = "is_read_committed_snapshot_on";
                    SyMUtil.getInstance();
                    jsonObject.put(s, (Object)com.me.devicemanagement.framework.server.util.SyMUtil.getServerParameter("is_read_committed_snapshot_on"));
                    SyMUtil.getInstance();
                    String killedCount = com.me.devicemanagement.framework.server.util.SyMUtil.getServerParameter("open_transaction_killed_in_mssql");
                    killedCount = ((killedCount == null) ? "None" : killedCount);
                    dbDetailsjsonObj.put("open_transaction_killed_in_mssql", (Object)killedCount);
                }
                final String dbmDetails = this.getDBMigrationDetails();
                dbDetailsjsonObj.put("DBMigrationDtls", (Object)dbmDetails);
                final Long number_of_locks = (Long)DBUtil.getRecordCount("DbLockInfo", "DBLOCKINFO_ID", (Criteria)null);
                JSONObject buildwiseLocksJsonObj = new JSONObject();
                if (number_of_locks > 0L) {
                    dbDetailsjsonObj.put("TotalDBLock", (Object)number_of_locks);
                    final Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(System.currentTimeMillis());
                    cal.add(2, -1);
                    final long deletionTime = cal.getTime().getTime();
                    final Criteria criteria = new Criteria(new Column("DbLockInfo", "CREATED_TIME"), (Object)deletionTime, 4);
                    final Long locks_in_lastmonth = (Long)DBUtil.getRecordCount("DbLockInfo", "DBLOCKINFO_ID", criteria);
                    dbDetailsjsonObj.put("DBLockInLastMonth", (Object)locks_in_lastmonth);
                    final Hashtable MaxValues = DBUtil.getMaxOfValues("DbLockInfo", new String[] { "CREATED_TIME", "LOCKED_QUERY_MAX_TIME", "NO_OF_QUERIES_LOCKED", "THREADS_BLOCKED" }, (Criteria)null);
                    dbDetailsjsonObj.put("DBLockLastDetectedTime", (Object)new Long(MaxValues.get("CREATED_TIME").toString()));
                    dbDetailsjsonObj.put("MaxDBLockedTimeinms", (Object)new Long(MaxValues.get("LOCKED_QUERY_MAX_TIME").toString()));
                    dbDetailsjsonObj.put("MaxDBLockedQueryCount", (Object)new Long(MaxValues.get("NO_OF_QUERIES_LOCKED").toString()));
                    dbDetailsjsonObj.put("MaxThreadsBlockedCount", (Object)new Long(MaxValues.get("THREADS_BLOCKED").toString()));
                    buildwiseLocksJsonObj = this.getBuildwiseLockDetails();
                }
                this.commonTrackerProperties.setProperty("DBLocksBuildwise", buildwiseLocksJsonObj.toString());
                this.commonTrackerProperties.setProperty("DataBase_Details", dbDetailsjsonObj.toString());
            }
            catch (final Exception e3) {
                SyMLogger.info(this.logger, this.sourceClass, "updateCommonProperties", "Exception e2 is :" + e3);
            }
            final String dbDataDeletionJson = DeletionTaskMetrack.getDeletionMeTrackData();
            if (dbDataDeletionJson != null && !dbDataDeletionJson.isEmpty()) {
                this.commonTrackerProperties.setProperty("Db_Background_Data_Deletion", dbDataDeletionJson);
            }
            try {
                final JSONObject ppmDetailsjsonObj = new JSONObject();
                final String amsPropertyFileName = System.getProperty("server.home") + File.separator + "conf" + File.separator + "amsexpiry.props";
                final File amsPropertyFile = new File(amsPropertyFileName);
                if (amsPropertyFile.exists()) {
                    final Properties amsProps = StartupUtil.getProperties(amsPropertyFileName);
                    final String applyPPMwithAMSExpiry = amsProps.getProperty("AMSExpiredPPMCount");
                    ppmDetailsjsonObj.put("AMSExpiryStatusForPPM", (Object)"PPM Upgrade Denied");
                    ppmDetailsjsonObj.put("AMSExpiredPPMCount", (Object)"0");
                    if (applyPPMwithAMSExpiry != null && applyPPMwithAMSExpiry != "") {
                        final int amsExpiredPPMCount = Integer.parseInt(applyPPMwithAMSExpiry);
                        if (amsExpiredPPMCount > 0) {
                            ppmDetailsjsonObj.put("AMSExpiredPPMCount", (Object)applyPPMwithAMSExpiry);
                        }
                    }
                }
                else {
                    ppmDetailsjsonObj.put("AMSExpiryStatusForPPM", (Object)"---");
                    ppmDetailsjsonObj.put("AMSExpiredPPMCount", (Object)"---");
                }
                this.commonTrackerProperties.setProperty("PPM_Details", ppmDetailsjsonObj.toString());
            }
            catch (final Exception e4) {
                SyMLogger.info(this.logger, this.sourceClass, "updateCommonProperties", "Exception e3 is :" + e4);
            }
            final String ppmfailureDetails = this.loadPPMFailureDetails();
            this.commonTrackerProperties.setProperty("ppm_failure_details", ppmfailureDetails);
            try {
                final JSONObject serverDetailsjsonObj = new JSONObject();
                Long sysRAMMemValue = -1L;
                final String serverRamMem = DBPostgresOptimizationUtil.getInstance().getServerRAMDetails();
                if (serverRamMem != null && !serverRamMem.isEmpty()) {
                    sysRAMMemValue = Long.valueOf(serverRamMem);
                }
                if (sysRAMMemValue <= 0L) {
                    sysRAMMemValue = -1L;
                }
                serverDetailsjsonObj.put("ServerRAMMem", (Object)sysRAMMemValue);
                final String dcProductArchitecture = StartupUtil.dcProductArch();
                if (dcProductArchitecture != null) {
                    serverDetailsjsonObj.put("JREArch", (Object)dcProductArchitecture);
                }
                else {
                    serverDetailsjsonObj.put("JREArch", (Object)"-");
                }
                final String osArchitecture = com.me.devicemanagement.onpremise.server.util.SyMUtil.getDCOSArchitecture();
                serverDetailsjsonObj.put("OSArch", (Object)osArchitecture);
                final String dcArchMigratedInfo = this.getDCArchMigratedInfo();
                serverDetailsjsonObj.put("64bitArchMigration", (Object)dcArchMigratedInfo);
                if (WebServerUtil.server_ip_props != null && !WebServerUtil.server_ip_props.trim().equals("") && !WebServerUtil.server_ip_props.equals("IP_NOT_REACHABLE") && !WebServerUtil.server_ip_props.equals("BIND_IP_IS_NOT_SPECIFIED")) {
                    serverDetailsjsonObj.put("IsSpecificIPEnabled", (Object)"true");
                }
                else {
                    serverDetailsjsonObj.put("IsSpecificIPEnabled", (Object)"false");
                }
                try {
                    final String confFileName = SyMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "User-Conf" + File.separator + "proxy-details.props";
                    final Properties prop = FileAccessUtil.readProperties(confFileName);
                    serverDetailsjsonObj.put("Proxy_Type", (Object)String.valueOf(prop.getProperty("proxyType")));
                }
                catch (final Exception ex) {
                    this.logger.log(Level.SEVERE, "Exception while getting proxy type:", ex);
                }
                try {
                    final String confFileName = SyMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "install.conf";
                    final String webservername = WebServerUtil.getWebServerName();
                    this.logger.log(Level.INFO, "Webserver name is {0}", webservername);
                    final boolean isNginxFailed = webservername.equalsIgnoreCase("apache");
                    final Properties prop2 = FileAccessUtil.readProperties(confFileName);
                    if (prop2.containsKey("apache_start_count") && prop2.containsKey("nginx_start_count")) {
                        serverDetailsjsonObj.put("apache_start_count", (Object)String.valueOf(prop2.getProperty("apache_start_count")));
                        serverDetailsjsonObj.put("isnginxfailed", (Object)String.valueOf(isNginxFailed));
                        serverDetailsjsonObj.put("nginx_start_count", (Object)String.valueOf(prop2.getProperty("nginx_start_count")));
                    }
                }
                catch (final Exception ex) {
                    this.logger.log(Level.SEVERE, "Exception while getting nginx failure status ", ex);
                }
                this.commonTrackerProperties.setProperty("Server_Details", serverDetailsjsonObj.toString());
                this.commonTrackerProperties.setProperty("Product_Mode", SyMUtil.getInstance().getProductMode());
            }
            catch (final Exception e5) {
                SyMLogger.info(this.logger, this.sourceClass, "updateCommonProperties", "Exception e4 is :" + e5);
            }
            try {
                this.commonTrackerProperties = METrackerUtil.getMETrackParam(this.commonTrackerProperties, "Hprof_Last_Modified_Time", (String)null);
                this.commonTrackerProperties = this.setHprofCountProps(this.commonTrackerProperties);
                SyMLogger.info(this.logger, this.sourceClass, "Setting details HRPOF", "Details Summary : DONE");
                this.commonTrackerProperties = METrackerUtil.getMETrackParam(this.commonTrackerProperties, "PID_Count", (String)null);
                this.commonTrackerProperties = METrackerUtil.getMETrackParam(this.commonTrackerProperties, "PID_Last_Modified_Time", (String)null);
            }
            catch (final Exception e6) {
                SyMLogger.info(this.logger, this.sourceClass, "updateCommonProperties", "Exception e6 is :" + e6);
            }
            this.addPluginInfoProperties();
            final JSONObject ppmInstallationJsonObj = new JSONObject();
            final Object ppmInstalledDate = this.getPpmInstalledDate();
            ppmInstallationJsonObj.put("PPM_Installed_Date_In_Long", ppmInstalledDate);
            this.commonTrackerProperties.setProperty("PPM_Installation_Details", ppmInstallationJsonObj.toString());
            try {
                final boolean isEnable = UploadDebugFileTask.isAutomaticUploadEnable();
                String debugLogUploadStatus = "";
                final JSONObject debugDetailsJsonObj = new JSONObject();
                if (isEnable == Boolean.TRUE) {
                    debugLogUploadStatus = com.me.devicemanagement.framework.server.util.SyMUtil.getServerParameter("DebugLogUploadStatus");
                    debugLogUploadStatus = ((debugLogUploadStatus != null && !debugLogUploadStatus.isEmpty()) ? debugLogUploadStatus : "No issues found");
                }
                else {
                    debugLogUploadStatus = "disabled";
                }
                debugDetailsJsonObj.put("IsDebugLogUploadEnabled", (Object)String.valueOf(isEnable));
                debugDetailsJsonObj.put("LatestDebugLogUploadStatus", (Object)debugLogUploadStatus);
                this.commonTrackerProperties.setProperty("Debug_Details", debugDetailsJsonObj.toString());
            }
            catch (final Exception e7) {
                SyMLogger.info(this.logger, this.sourceClass, "updateCommonProperties", "Exception e7 is :" + e7);
            }
            this.addUpdatesLastCheckedTime();
            this.checkforupdateMETrackingDetails();
            if ("R".equalsIgnoreCase(LicenseProvider.getInstance().getLicenseType())) {
                ((Hashtable<String, JSONObject>)this.commonTrackerProperties).put("Review_Banner_Details", this.getProductBannerTrackData());
            }
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "Details Summary : " + this.getCommonTrackerProperties());
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "Common implementation ends...");
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "updateCommonProperties", "Exception : ", (Throwable)e);
        }
    }
    
    private Properties setHprofCountProps(final Properties commonTrackerProperties) {
        int totalCount = 0;
        final JSONObject buildwiseHprofJsonObj = new JSONObject();
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ServerDebuggingFileSummary"));
        query.addSelectColumn(new Column("ServerDebuggingFileSummary", "*"));
        query.addSortColumn(new SortColumn("ServerDebuggingFileSummary", "BUILD_NUMBER", true));
        final Column column = Column.getColumn("ServerDebuggingFileSummary", "FILE_TYPE");
        final Criteria fileCriteria = new Criteria(column, (Object)1, 0);
        query.setCriteria(fileCriteria);
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection conn = null;
        try {
            conn = relapi.getConnection();
            final DataSet ds = relapi.executeQuery((Query)query, conn);
            while (ds.next()) {
                totalCount += ds.getInt("COUNT");
                buildwiseHprofJsonObj.put(ds.getAsString("BUILD_NUMBER"), (Object)ds.getAsString("COUNT"));
            }
            ds.close();
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "getBuildwiseHprofDetails", "Exception while getting buildwise hprof information ", (Throwable)ex);
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception ex2) {
                SyMLogger.error(this.logger, this.sourceClass, "getBuildwiseHprofDetails", "Exception while closing connection ", (Throwable)ex2);
            }
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception ex3) {
                SyMLogger.error(this.logger, this.sourceClass, "getBuildwiseHprofDetails", "Exception while closing connection ", (Throwable)ex3);
            }
        }
        commonTrackerProperties.setProperty("Hprof_Count", String.valueOf(totalCount));
        commonTrackerProperties.setProperty("HprofCountBuildWise", buildwiseHprofJsonObj.toString());
        return commonTrackerProperties;
    }
    
    private String getTimeFromString(final String timeStr) {
        Date date = null;
        try {
            long timeStamp = 0L;
            if (timeStr != null && !timeStr.equalsIgnoreCase("")) {
                timeStamp = Long.valueOf(timeStr);
            }
            date = new Date(new Timestamp(timeStamp).getTime());
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "getLastLoginTime", "Exception : ", (Throwable)e);
            return "-";
        }
        return date.toString();
    }
    
    private Properties getCommonTrackerProperties() {
        return this.commonTrackerProperties;
    }
    
    private int getLocaleCount(final String language) {
        int count = 0;
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("AaaUserProfile"));
            query.addSelectColumn(new Column("AaaUserProfile", "USER_ID").count());
            final Join userJoin = new Join("AaaUserProfile", "AaaUser", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2);
            final Join loginJoin = new Join("AaaUser", "AaaLogin", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2);
            final Criteria criteria = new Criteria(Column.getColumn("AaaUserProfile", "LANGUAGE_CODE"), (Object)language, 0);
            query.addJoin(userJoin);
            query.addJoin(loginJoin);
            query.setCriteria(criteria);
            count = this.getProperty(query);
            if (language.equalsIgnoreCase(Locale.ENGLISH.getLanguage())) {
                count += this.getDefaultLocaleCount();
            }
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "getLocaleCount", "Exception : ", (Throwable)e);
        }
        return count;
    }
    
    private int getDefaultLocaleCount() {
        int defaultCnt = 0;
        try {
            defaultCnt = 0;
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("AaaUserProfile"));
            query.addSelectColumn(new Column("AaaUserProfile", "USER_ID"));
            DataObject dobj = DataAccess.get(query);
            if (!dobj.isEmpty()) {
                final ArrayList al = new ArrayList();
                final Iterator iterator = dobj.getRows("AaaUserProfile");
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    al.add(row.get("USER_ID"));
                }
                final Criteria criteria = new Criteria(Column.getColumn("AaaLogin", "USER_ID"), (Object)al.toArray(), 9);
                dobj = DataAccess.get("AaaLogin", criteria);
                if (!dobj.isEmpty()) {
                    defaultCnt = dobj.size("AaaLogin");
                }
            }
        }
        catch (final DataAccessException e) {
            SyMLogger.error(this.logger, this.sourceClass, "getDefaultLocaleCount", "DataAccessException : ", (Throwable)e);
        }
        return defaultCnt;
    }
    
    private int getProperty(final SelectQuery query) {
        RelationalAPI relationalAPI = null;
        Connection conn = null;
        DataSet ds = null;
        int count = 0;
        try {
            relationalAPI = RelationalAPI.getInstance();
            conn = relationalAPI.getConnection();
            ds = relationalAPI.executeQuery((Query)query, conn);
            while (ds.next()) {
                final Object value = ds.getValue(1);
                if (value != null) {
                    count = Integer.valueOf(ds.getValue(1).toString());
                }
            }
        }
        catch (final SQLException e) {
            SyMLogger.error(this.logger, this.sourceClass, "getProperty", "SQLException : ", (Throwable)e);
        }
        catch (final QueryConstructionException e2) {
            SyMLogger.error(this.logger, this.sourceClass, "getProperty", "QueryConstructionException : ", (Throwable)e2);
        }
        finally {
            try {
                if (ds != null) {
                    ds.close();
                }
                if (!conn.isClosed()) {
                    conn.close();
                }
            }
            catch (final Exception e3) {
                SyMLogger.error(this.logger, this.sourceClass, "getProperty", "Exception when closing streams : ", (Throwable)e3);
            }
        }
        return count;
    }
    
    public void checkforupdateMETrackingDetails() {
        final JSONObject checkforUpdatesJsonObj = new JSONObject();
        final JSONObject userClicks = new JSONObject();
        final JSONObject doItLaterClicks = new JSONObject();
        final JSONObject statusClicks = new JSONObject();
        Long userID = 0L;
        Long noOfBuildNoClicks = 0L;
        Long noOfViews = 0L;
        Long noOfDoItLaterClicks = 0L;
        String msg_status = new String();
        try {
            DataObject dataObject = DataAccess.get("CheckforUpdatesUserClicks", (Criteria)null);
            Iterator updatesIterator = dataObject.getRows("CheckforUpdatesUserClicks");
            while (updatesIterator.hasNext()) {
                final Row updatesRow = updatesIterator.next();
                userID = (Long)updatesRow.get("USER_ID");
                noOfBuildNoClicks = (Long)updatesRow.get("NO_OF_CLICKS");
                noOfDoItLaterClicks = (Long)updatesRow.get("NO_OF_DO_IT_LATER_CLICKS");
                userClicks.put(userID.toString(), (Object)noOfBuildNoClicks.toString());
                doItLaterClicks.put(userID.toString(), (Object)noOfDoItLaterClicks.toString());
            }
            dataObject = DataAccess.get("CheckforUpdatesStatusClicks", (Criteria)null);
            updatesIterator = dataObject.getRows("CheckforUpdatesStatusClicks");
            while (updatesIterator.hasNext()) {
                final Row updatesRow = updatesIterator.next();
                msg_status = (String)updatesRow.get("STATUS");
                noOfViews = (Long)updatesRow.get("COUNT");
                statusClicks.put(msg_status, (Object)noOfViews.toString());
            }
            checkforUpdatesJsonObj.put("is_version_notification_enabled", NotifyUpdatesUtil.getProductUpdatesNotificationSettings());
            checkforUpdatesJsonObj.put("is_flashmsg_notification_enabled", NotifyUpdatesUtil.getFlashMsgNotificationSettings());
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "checkforupdateMETrackingDetails", "Error in Check For update METracking details : ", (Throwable)e);
        }
        checkforUpdatesJsonObj.put("User_Clicks", (Object)userClicks);
        checkforUpdatesJsonObj.put("Status_Shown", (Object)statusClicks);
        checkforUpdatesJsonObj.put("Do_It_Later_Clicks", (Object)doItLaterClicks);
        this.commonTrackerProperties.setProperty("CheckforUpdatesInfo", checkforUpdatesJsonObj.toString());
    }
    
    public void addPluginInfoProperties() {
        final String sourceMethod = "addPluginInfoProperties";
        try {
            final Properties pluginInfoProperties = new Properties();
            String isPluginEnabled = "-";
            final boolean isPluginMode = DCPluginUtil.getInstance().isPlugin();
            if (isPluginMode) {
                isPluginEnabled = "Y";
            }
            else {
                isPluginEnabled = "N";
            }
            final String productCode = ProductUrlLoader.getInstance().getValue("productcode");
            pluginInfoProperties.setProperty("Is_Plugin", isPluginEnabled);
            pluginInfoProperties.setProperty("Pro_Code", productCode);
            JSONObject addPluginJSONobj = new JSONObject();
            addPluginJSONobj = METrackerUtil.createJSONObject(pluginInfoProperties);
            this.commonTrackerProperties.setProperty("Is_Addon", addPluginJSONobj.toString());
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Error: ", (Throwable)e);
        }
    }
    
    private JSONObject getBuildwiseLockDetails() {
        final JSONObject buildwiseLocksjsonObj = new JSONObject();
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("DCServerBuildHistory"));
        query.addSelectColumn(new Column("DCServerBuildHistory", "*"));
        query.addSortColumn(new SortColumn("DCServerBuildHistory", "BUILD_NUMBER", false));
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection conn = null;
        try {
            conn = relapi.getConnection();
            final DataSet ds = relapi.executeQuery((Query)query, conn);
            Long locks_in_previous_builds = 0L;
            int return_count = 0;
            while (ds.next() && return_count < 2) {
                final Criteria criteria1 = new Criteria(new Column("DbLockInfo", "CREATED_TIME"), (Object)ds.getAsLong("BUILD_DETECTED_AT"), 4);
                final Long total_locks = (Long)DBUtil.getRecordCount("DbLockInfo", "DBLOCKINFO_ID", criteria1);
                final Long locks_in_current_build = total_locks - locks_in_previous_builds;
                if (locks_in_current_build > 0L) {
                    buildwiseLocksjsonObj.put(ds.getAsString("BUILD_NUMBER"), (Object)locks_in_current_build);
                    ++return_count;
                }
                locks_in_previous_builds = total_locks;
            }
            ds.close();
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "updateCommonProperties", "Exception while getting buildwise database lock information ", (Throwable)ex);
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception ex2) {
                SyMLogger.error(this.logger, this.sourceClass, "updateCommonProperties", "Exception while closing connection ", (Throwable)ex2);
            }
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception ex3) {
                SyMLogger.error(this.logger, this.sourceClass, "updateCommonProperties", "Exception while closing connection ", (Throwable)ex3);
            }
        }
        return buildwiseLocksjsonObj;
    }
    
    private Criteria getSoftwaNameCri(final ArrayList list) {
        Criteria criteria = new Criteria(new Column("InvSW", "SOFTWARE_NAME"), (Object)("*" + list.get(0).toString() + "*"), 2);
        for (int i = 1; i < list.size(); ++i) {
            criteria = criteria.or(new Criteria(new Column("InvSW", "SOFTWARE_NAME"), (Object)("*" + list.get(i).toString() + "*"), 2));
        }
        return criteria;
    }
    
    private ArrayList getAntivirusList() {
        final ArrayList antiVirusList = new ArrayList();
        antiVirusList.add("avast");
        antiVirusList.add("Norton");
        antiVirusList.add("McAfee");
        antiVirusList.add("Kaspersky");
        antiVirusList.add("Webroot");
        antiVirusList.add("Avira");
        antiVirusList.add("Bitdefender");
        antiVirusList.add("k7");
        antiVirusList.add("G Data");
        antiVirusList.add("NisSrv MsMpSvc");
        antiVirusList.add("WinDefend");
        antiVirusList.add("Emsisoft");
        antiVirusList.add("F-Secure");
        antiVirusList.add("Malwarbytes");
        antiVirusList.add("Panda");
        antiVirusList.add("Trend Micro");
        antiVirusList.add("Blue Ridge");
        antiVirusList.add("AVG");
        antiVirusList.add("ESET");
        antiVirusList.add("eScan");
        return antiVirusList;
    }
    
    private boolean checkIfDataCanBePosted(final String postingStatus, final String lastPostedTime) {
        if (postingStatus == null || "false".equalsIgnoreCase(postingStatus)) {
            return true;
        }
        if (lastPostedTime == null || "0".equalsIgnoreCase(lastPostedTime)) {
            return true;
        }
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(4, calendar.get(4) - 1);
        final Long timeBeforeWeek = calendar.getTimeInMillis();
        return Long.parseLong(lastPostedTime) > timeBeforeWeek;
    }
    
    private Object getPpmInstalledDate() {
        Object ppmInstalledDateInLong = null;
        Connection conn = null;
        final RelationalAPI relapi = RelationalAPI.getInstance();
        DataSet ds = null;
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("DCServerBuildHistory"));
        final Column ppmInstalledDateColumn = new Column("DCServerBuildHistory", "BUILD_DETECTED_AT");
        query.addSelectColumn(ppmInstalledDateColumn);
        query.addSortColumn(new SortColumn("DCServerBuildHistory", "BUILD_NUMBER", false));
        query.setRange(new Range(0, 1));
        SyMLogger.info(this.logger, this.sourceClass, "getPpmInstalledDate", "Query is " + query.toString());
        try {
            conn = relapi.getConnection();
            ds = relapi.executeQuery((Query)query, conn);
            if (ds.next()) {
                ppmInstalledDateInLong = ds.getValue("BUILD_DETECTED_AT");
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "getPpmInstalledDate", "Exception while fetching data from DB in getPpmInstalledType() function", (Throwable)ex);
            try {
                if (ds != null) {
                    ds.close();
                }
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception ex2) {
                SyMLogger.error(this.logger, this.sourceClass, "getPpmInstalledDate", "Exception while closing connection in getPpmInstalledType() function", (Throwable)ex2);
            }
        }
        finally {
            try {
                if (ds != null) {
                    ds.close();
                }
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception ex3) {
                SyMLogger.error(this.logger, this.sourceClass, "getPpmInstalledDate", "Exception while closing connection in getPpmInstalledType() function", (Throwable)ex3);
            }
        }
        return ppmInstalledDateInLong;
    }
    
    public String getDCArchMigratedInfo() {
        String dcArchMigratedInfo = "-";
        try {
            final String homeDir = System.getProperty("server.home");
            final String dcmigrationdir = homeDir + File.separator + "dcarchmigration";
            final File dcmigrationdirFile = new File(dcmigrationdir);
            if (dcmigrationdirFile.exists()) {
                final String dcmigrationInfoFilePath = dcmigrationdir + File.separator + "dcarchmigrationinfo.props";
                final File dcmigrationInfoFile = new File(dcmigrationInfoFilePath);
                if (dcmigrationInfoFile.exists()) {
                    final Properties dcmigrationInfoProps = FileAccessUtil.readProperties(dcmigrationInfoFilePath);
                    if (dcmigrationInfoProps.containsKey("32bitTo64bit")) {
                        dcArchMigratedInfo = dcmigrationInfoProps.getProperty("32bitTo64bit");
                    }
                    else if (dcmigrationInfoProps.containsKey("64bitTo32bit")) {
                        dcArchMigratedInfo = "R-" + dcmigrationInfoProps.getProperty("64bitTo32bit");
                    }
                }
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "Exception in getDCArchMigratedInfo() method called by updateCommonProperties()", "Exception : ", (Throwable)ex);
        }
        return dcArchMigratedInfo;
    }
    
    private String getDBMigrationDetails() {
        String dbmDetails = "Not yet";
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ServerParams"));
            final Column allCol = Column.getColumn((String)null, "*");
            query.addSelectColumn(allCol);
            final Column paramName = Column.getColumn("ServerParams", "PARAM_NAME");
            final Criteria dbmCri = new Criteria(paramName, (Object)"dbmigration", 0);
            query.setCriteria(dbmCri);
            final SortColumn sortColumn = new SortColumn(Column.getColumn("ServerParams", "SERVER_PARAM_ID"), (boolean)Boolean.FALSE);
            query.addSortColumn(sortColumn);
            query.setRange(new Range(0, 1));
            final DataObject data = DataAccess.get(query);
            if (!data.isEmpty()) {
                final Row paramValueRow = data.getRow("ServerParams");
                dbmDetails = (String)paramValueRow.get("PARAM_VALUE");
                SyMLogger.info(this.logger, this.sourceClass, "getDBMigrationDetails", "DBMigration is found, details set in ME tracking. Value to be sent :  " + dbmDetails);
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "getDBMigrationDetails", "Exception occurred while setting flag for dbmigration", (Throwable)ex);
        }
        return dbmDetails;
    }
    
    private void addUpdatesLastCheckedTime() {
        try {
            final String lastCheckedAt = UpdatesParamUtil.getUpdParameter("updatesLastModifiedAt");
            ((Hashtable<String, String>)this.commonTrackerProperties).put("FlashUpdsLastChkdAt", (lastCheckedAt == null) ? "" : lastCheckedAt);
        }
        catch (final Exception e) {
            SyMLogger.info(this.logger, this.sourceClass, "addUpdatesLastCheckedTime", "Exception while addUpdatesLastCheckedTime :" + e);
        }
    }
    
    private String loadPPMFailureDetails() {
        JSONObject ppmFailureDetailsjsonObj = new JSONObject();
        try {
            final String PPMDetailsFilePath = System.getProperty("server.home") + File.separator + "logs" + File.separator + "PPMUninstallDetails.json";
            final File PPMDetailsFile = new File(PPMDetailsFilePath);
            if (PPMDetailsFile.exists()) {
                ppmFailureDetailsjsonObj = JsonUtils.loadJsonFile(PPMDetailsFile);
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "Exception in loadPPMFailureDetails() method called by updateCommonProperties()", "Exception : ", (Throwable)ex);
        }
        return ppmFailureDetailsjsonObj.toString();
    }
    
    private JSONObject getProductBannerTrackData() {
        final JSONObject finalResponseData = new JSONObject();
        try {
            finalResponseData.put("remindLater", (Object)this.removeStartsWithUniqueKey(METrackerUtil.getMETrackParamsStartsWith("REMIND_LATER".concat("."))));
            finalResponseData.put("firstRemindLater", (Object)this.removeStartsWithUniqueKey(METrackerUtil.getMETrackParamsStartsWith("FIRST_CLOSE".concat("."))));
            finalResponseData.put("alreadyReviewed", (Object)this.removeStartsWithUniqueKey(METrackerUtil.getMETrackParamsStartsWith("REVIEWED".concat("."))));
            finalResponseData.put("doNotShow", (Object)this.removeStartsWithUniqueKey(METrackerUtil.getMETrackParamsStartsWith("DO_NOT_SHOW".concat("."))));
            finalResponseData.put("bannerClickCount", (Object)this.removeStartsWithUniqueKey(METrackerUtil.getMETrackParamsStartsWith("BANNER_CLICK_COUNT".concat("."))));
            this.logger.log(Level.INFO, "Data to be posted : {0}", finalResponseData.toString(4));
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while posting metrack data for Banner For Reviews", ex);
        }
        return finalResponseData;
    }
    
    private JSONObject removeStartsWithUniqueKey(final JSONObject inputJson) throws Exception {
        final JSONObject outputJson = new JSONObject();
        final Set<String> keySet = inputJson.keySet();
        for (final String oldKey : keySet) {
            outputJson.put(oldKey.substring(oldKey.indexOf(".") + 1), (Object)inputJson.optString(oldKey, "0"));
        }
        return outputJson;
    }
}
