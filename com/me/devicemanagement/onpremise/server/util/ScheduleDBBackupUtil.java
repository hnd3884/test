package com.me.devicemanagement.onpremise.server.util;

import java.sql.Statement;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.FileUtil;
import java.net.InetAddress;
import com.adventnet.ds.query.QueryConstructionException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;
import com.adventnet.db.api.RelationalAPI;
import com.me.devicemanagement.onpremise.winaccess.WinAccessProvider;
import com.zoho.framework.utils.crypto.EnDecryptAES256Impl;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import org.apache.commons.lang3.BooleanUtils;
import com.adventnet.i18n.I18N;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.HashMap;
import com.me.devicemanagement.onpremise.server.factory.ApiFactoryProvider;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import java.util.Iterator;
import java.util.Set;
import java.util.Properties;
import org.json.JSONException;
import java.io.File;
import org.json.JSONObject;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.BackupRestoreUtil;
import org.apache.commons.lang3.StringUtils;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.Map;
import java.util.logging.Logger;

public class ScheduleDBBackupUtil
{
    public static Logger logger;
    public static final int ERROR = -1;
    public static final String ERROR_MSG = "ERROR";
    private static final Map<String, Integer> PATH_VALIDATION_MAP;
    public static final int DEFAULT_ENCRYPTION_TYPE = 1;
    public static final int CUSTOMER_ENCRYPTION_TYPE = 2;
    public static final int PPM_BACKUP_ENCRYPTION_TYPE = 3;
    public static Boolean isBakPermissionAllowed;
    public static Boolean isBackupFolderPermissionAllowed;
    
    public static String getBackupLocation() {
        String backupLocation = null;
        try {
            backupLocation = (String)DBUtil.getFirstValueFromDBWithOutCriteria("DBBackupInfo", "BACKUP_DIR");
        }
        catch (final Exception e) {
            ScheduleDBBackupUtil.logger.log(Level.WARNING, "Failed to get backup location from DB", e);
        }
        if (StringUtils.isEmpty((CharSequence)backupLocation) || StringUtils.isBlank((CharSequence)backupLocation) || backupLocation.equals("--")) {
            backupLocation = BackupRestoreUtil.getInstance().getDefaultBackupLocation();
        }
        return backupLocation;
    }
    
    public static int getPathValidationCode(final String pathValidationMsg) {
        int pathValidationCode = -1;
        try {
            final Integer code = ScheduleDBBackupUtil.PATH_VALIDATION_MAP.get(pathValidationMsg);
            if (code != null) {
                pathValidationCode = code;
            }
        }
        catch (final Exception ex) {
            ScheduleDBBackupUtil.logger.log(Level.WARNING, "Caught error while getting path validation code ", ex);
        }
        ScheduleDBBackupUtil.logger.log(Level.INFO, "path validation code " + pathValidationCode);
        return pathValidationCode;
    }
    
    public static JSONObject checkBackupPath(String backupdir) {
        String pathValidationMsg = "ERROR";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("path", (Object)backupdir);
            final File file = new File(backupdir);
            backupdir = file.getCanonicalPath();
            pathValidationMsg = isValidFilePath(backupdir);
            jsonObject.put("path", (Object)backupdir);
            jsonObject.put("responseCode", getPathValidationCode(pathValidationMsg));
            jsonObject.put("responseText", (Object)pathValidationMsg);
            final boolean exists = file.exists();
            jsonObject.put("exists", exists);
            if (exists) {
                jsonObject.put("canRead", file.canRead());
                jsonObject.put("canWrite", file.canWrite());
                jsonObject.put("canExecute", file.canExecute());
            }
        }
        catch (final Exception ex) {
            ScheduleDBBackupUtil.logger.log(Level.SEVERE, "ScheduledDBBackupAction : Exception occurred - checkBackupPath() :  ", ex);
            try {
                jsonObject.put("responseCode", -1);
                jsonObject.put("responseText", (Object)"UNABLE_TO_SET_PATH_DUE_TO_FILE_ACCESS_DENIED");
            }
            catch (final JSONException e) {
                ScheduleDBBackupUtil.logger.log(Level.SEVERE, "ScheduledDBBackupAction : Exception occurred - checkBackupPath() :  ", (Throwable)e);
            }
        }
        return jsonObject;
    }
    
    public static String isValidFilePath(String path) throws Exception {
        final String SERVER_HOME = new File(SyMUtil.getInstallationDir()).getCanonicalPath();
        final String BIN_PATH = SERVER_HOME + File.separator + "bin";
        if (path.equals(BIN_PATH) || StringUtils.isEmpty((CharSequence)path) || StringUtils.isBlank((CharSequence)path)) {
            ScheduleDBBackupUtil.logger.log(Level.WARNING, "Given path cannot be Empty or Blank");
            return "UNABLE_TO_EMPTY_PATH";
        }
        final File file = new File(path);
        path = file.getCanonicalPath();
        final Properties reservedPathProps = BackupRestoreUtil.getInstance().getReservedPathProps();
        final Set<String> reservedPathKeys = reservedPathProps.stringPropertyNames();
        for (final String key : reservedPathKeys) {
            if (path.contains(reservedPathProps.getProperty(key))) {
                ScheduleDBBackupUtil.logger.log(Level.WARNING, "Given path equal to reserved location : " + path + " for more details refer websettings.conf");
                return key;
            }
        }
        if (path.startsWith(SERVER_HOME) && !path.equals(BackupRestoreUtil.getInstance().getDefaultBackupLocation())) {
            ScheduleDBBackupUtil.logger.log(Level.WARNING, "Given path cannot equal DEFAULT_SCHEDULED_PATH : " + path);
            return "UNABLE_TO_SET_PATH_UNDER_SERVER_HOME";
        }
        if (!isBackupLocationReachable(path)) {
            if (file.exists()) {
                ScheduleDBBackupUtil.logger.log(Level.WARNING, "File permissions : canRead : {0} canWrite: {1} canExecute: {2}", new Object[] { file.canRead(), file.canWrite(), file.canExecute() });
            }
            return "UNABLE_TO_SET_PATH_DUE_TO_FILE_ACCESS_DENIED";
        }
        return "VALID_PATH";
    }
    
    public static boolean isBackupLocationReachable(final String backupLocation) {
        final File backupDir = new File(backupLocation);
        boolean isReachable = false;
        if (StringUtils.isEmpty((CharSequence)backupLocation) || StringUtils.isBlank((CharSequence)backupLocation) || backupLocation.equals("--")) {
            return false;
        }
        final File tempFile = new File(backupLocation + File.separator + "temp" + System.currentTimeMillis() + ".txt");
        try {
            if (!backupDir.exists()) {
                backupDir.mkdirs();
            }
            FileUtils.touch(tempFile);
            FileUtils.forceDelete(tempFile);
            isReachable = true;
        }
        catch (final Exception ex) {
            ScheduleDBBackupUtil.logger.log(Level.WARNING, "Error: BackupLocation doesn't have require permissions " + backupLocation, ex);
            isReachable = false;
        }
        return isReachable;
    }
    
    public static boolean isEqualToScheduleBackupDir(String path) throws IOException {
        final String dbBackupPath = getBackupLocation();
        path = new File(path).getCanonicalPath();
        return path.contains(dbBackupPath);
    }
    
    public static boolean isRemoteBackupPath(final String path) {
        try {
            if (path.startsWith("\\\\")) {
                return true;
            }
        }
        catch (final Exception ex) {
            ScheduleDBBackupUtil.logger.log(Level.SEVERE, "Exception while checking Remote path", ex);
        }
        return false;
    }
    
    public static JSONObject getScheduleDBBackupDetails(final JSONObject scheduleDBJson, final String schedulerName) {
        try {
            final HashMap schedule = ApiFactoryProvider.getSchedulerAPI().getScheduledValues(schedulerName);
            ScheduleDBBackupUtil.logger.log(Level.INFO, "schedule" + schedule);
            if (schedule.size() == 0) {
                scheduleDBJson.put("ENABLE_DBBACKUP_SCHEDULER", false);
                scheduleDBJson.put("scheduleType", (Object)"Daily");
                scheduleDBJson.put("dailyTime", (Object)"");
                scheduleDBJson.put("dailyDate", (Object)"");
                scheduleDBJson.put("dailyIntervalType", (Object)"everyDay");
            }
            else {
                final boolean disabled = ApiFactoryProvider.getSchedulerAPI().isSchedulerDisabled(schedulerName);
                if (disabled) {
                    scheduleDBJson.put("ENABLE_DBBACKUP_SCHEDULER", false);
                }
                else {
                    scheduleDBJson.put("ENABLE_DBBACKUP_SCHEDULER", true);
                }
                final int hours = schedule.get("exeHours");
                final int minutes = schedule.get("exeMinutes");
                final int seconds = schedule.get("exeSeconds");
                String updateTime = null;
                if (hours < 10) {
                    updateTime = "0" + hours;
                }
                else {
                    updateTime = String.valueOf(hours);
                }
                if (minutes < 10) {
                    updateTime = updateTime + ":0" + minutes;
                }
                else {
                    updateTime = updateTime + ":" + minutes;
                }
                if (seconds < 10) {
                    updateTime = updateTime + ":0" + seconds;
                }
                else {
                    updateTime = updateTime + ":" + seconds;
                }
                String dateOfExec = "";
                final int day = schedule.get("startDate");
                final int month = schedule.get("startMonth");
                final int year = schedule.get("startYear");
                dateOfExec = ((month < 9) ? dateOfExec.concat("0" + (month + 1) + "/") : dateOfExec.concat("" + (month + 1) + "/"));
                dateOfExec = ((day < 10) ? dateOfExec.concat("0" + day + "/") : dateOfExec.concat("" + day + "/"));
                dateOfExec = dateOfExec.concat("" + year);
                scheduleDBJson.put("scheduleType", (Object)"Daily");
                scheduleDBJson.put("dailyTime", (Object)updateTime);
                scheduleDBJson.put("dailyDate", (Object)dateOfExec);
                ScheduleDBBackupUtil.logger.log(Level.INFO, "Scheduled DB execution time from DB : " + dateOfExec + " " + updateTime);
                if (schedule.containsKey("timeZoneDiff")) {
                    scheduleDBJson.put("taskTimeZone", schedule.get("taskTimeZone"));
                }
                else {
                    scheduleDBJson.put("taskTimeZone", (Object)com.me.devicemanagement.framework.server.factory.ApiFactoryProvider.getAuthUtilAccessAPI().getUserTimeZoneID());
                }
                ScheduleDBBackupUtil.logger.log(Level.FINE, "time zone : " + scheduleDBJson.get("taskTimeZone"));
            }
        }
        catch (final Exception ex) {
            ScheduleDBBackupUtil.logger.log(Level.WARNING, "Exception while getting DBBackupInfo page", ex);
        }
        return scheduleDBJson;
    }
    
    public static JSONObject getEmailAlertConfigInfo(final JSONObject scheduleDBJson) {
        try {
            final DataObject mailDObj = SyMUtil.getEmailAddDO("DBBackup");
            if (!mailDObj.isEmpty()) {
                final Row row = mailDObj.getRow("EMailAddr");
                final Boolean isEnabled = (Boolean)row.get("SEND_MAIL");
                final String emailIDS = (String)row.get("EMAIL_ADDR");
                scheduleDBJson.put("ENABLE_EMAIL_ALERT", (Object)isEnabled);
                scheduleDBJson.put("EMAIL_ID", (Object)emailIDS);
            }
            else {
                scheduleDBJson.put("ENABLE_EMAIL_ALERT", false);
                scheduleDBJson.put("EMAIL_ID", (Object)"");
            }
        }
        catch (final Exception exp) {
            ScheduleDBBackupUtil.logger.log(Level.WARNING, "Exception while getting EmailAlertConfigInfo page", exp);
        }
        return scheduleDBJson;
    }
    
    public static JSONObject getDBBackupInfo(final JSONObject scheduleDBJson) throws Exception {
        final DataObject data = getDbBackupInfoDO();
        final Row dcBackupInfoRow = data.getRow("DBBackupInfo");
        final Integer noBackupMaintain = (Integer)dcBackupInfoRow.get("NO_OF_BACKUP");
        final Boolean isBackupPWDEnabled = isPasswordEnabled(dcBackupInfoRow);
        final String dbfilesDEST = getBackupLocation();
        scheduleDBJson.put("noofbackup", (Object)noBackupMaintain);
        scheduleDBJson.put("backupdir", (Object)dbfilesDEST);
        scheduleDBJson.put("ENABLE_PASSWORD_PROTECTION", (Object)isBackupPWDEnabled);
        return scheduleDBJson;
    }
    
    public static boolean isPasswordEnabled(final Row dcBackupInfoRow) {
        final String passwordString = (String)dcBackupInfoRow.get("BACKUP_PASSWORD");
        return passwordString != null && !passwordString.isEmpty() && !passwordString.equals("");
    }
    
    public static DataObject getDbBackupInfoDO() throws DataAccessException {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("DBBackupInfo"));
        query.addSelectColumn(new Column((String)null, "*"));
        return SyMUtil.getPersistence().get(query);
    }
    
    public static void updateEmailAlertInfo(final JSONObject scheduleBackupDetails) throws Exception {
        final Boolean enable = (Boolean)scheduleBackupDetails.get("ENABLE_EMAIL_ALERT");
        final String email = (String)scheduleBackupDetails.get("EMAIL_ID");
        SyMUtil.addOrUpdateEmailAddr("DBBackup", (boolean)enable, email);
    }
    
    public static String updateScheduledInfo(final JSONObject scheduleBackupDetails) throws Exception {
        try {
            String message = I18N.getMsg("dc.admin.scheduleDBBackup.setting_update", new Object[0]);
            final DataObject data = getDbBackupInfoDO();
            Row dcBackupInfoRow = data.getRow("DBBackupInfo");
            if (dcBackupInfoRow == null) {
                dcBackupInfoRow = new Row("DBBackupInfo");
                data.addRow(dcBackupInfoRow);
            }
            final Integer noBackupMaintain = (Integer)scheduleBackupDetails.get("noofbackup");
            final String dbfilesDEST = (String)scheduleBackupDetails.get("backupdir");
            final Boolean isBackupPWDEnabled = (Boolean)scheduleBackupDetails.get("ENABLE_PASSWORD_PROTECTION");
            final Boolean isPWDChanged = scheduleBackupDetails.optBoolean("isPWDChanged");
            final String disablePwd = (String)scheduleBackupDetails.get("disablePwd");
            final String newPWD = scheduleBackupDetails.optString("NewBackupPassword");
            final String passwordHint = scheduleBackupDetails.optString("BackupPasswordHint");
            final String pathValidationMsg = isValidFilePath(dbfilesDEST);
            if (pathValidationMsg.equalsIgnoreCase("VALID_PATH")) {
                if (isBackupPWDEnabled) {
                    if (isPWDChanged) {
                        if (dcBackupInfoRow.get("BACKUP_PASSWORD") == null) {
                            message = I18N.getMsg("dc.admin.scheduleDBBackup.secure_enable", new Object[0]);
                            ScheduleDBBackupUtil.logger.log(Level.INFO, "Enabled Secure the backup by setting password.");
                        }
                        else {
                            message = I18N.getMsg("dc.admin.scheduleDBBackup.secure_password_changed", new Object[0]);
                            ScheduleDBBackupUtil.logger.log(Level.INFO, "Secure the backup password was changed.");
                        }
                        dcBackupInfoRow.set("BACKUP_PASSWORD", (Object)newPWD);
                        dcBackupInfoRow.set("BACKUP_PASSWORD_HINT", (Object)passwordHint);
                    }
                    else if (dcBackupInfoRow.get("BACKUP_PASSWORD") == null) {
                        message = I18N.getMsg("dc.admin.scheduleDBBackup.secure_enable", new Object[0]);
                        ScheduleDBBackupUtil.logger.log(Level.INFO, "Enabled Secure the backup by setting password.");
                        dcBackupInfoRow.set("BACKUP_PASSWORD", (Object)newPWD);
                        dcBackupInfoRow.set("BACKUP_PASSWORD_HINT", (Object)passwordHint);
                    }
                }
                else if (!isBackupPWDEnabled) {
                    dcBackupInfoRow.set("BACKUP_PASSWORD", (Object)null);
                    dcBackupInfoRow.set("BACKUP_PASSWORD_HINT", (Object)null);
                }
                if (BooleanUtils.toBoolean(disablePwd)) {
                    message = I18N.getMsg("dc.admin.scheduleDBBackup.secure_disable", new Object[0]);
                    ScheduleDBBackupUtil.logger.log(Level.WARNING, "Disabled Secure the backup.");
                }
                dcBackupInfoRow.set("NO_OF_BACKUP", (Object)noBackupMaintain);
                dcBackupInfoRow.set("BACKUP_DIR", (Object)dbfilesDEST);
                data.updateRow(dcBackupInfoRow);
                SyMUtil.getPersistence().update(data);
                String loginName = null;
                loginName = scheduleBackupDetails.optString("loginName");
                if (loginName != null) {
                    DCEventLogUtil.getInstance().addEventLogEntry(503, loginName, message, (Object)null);
                }
            }
            return pathValidationMsg;
        }
        catch (final Exception e) {
            ScheduleDBBackupUtil.logger.log(Level.WARNING, "Failure while updateScheduledInfo() Settings", e);
            throw e;
        }
    }
    
    public static Long saveScheduledValues(final String workEngineId, final String operationType, final String workflowName, final String email, final JSONObject schedForm) throws Exception {
        try {
            Long taskId = 0L;
            final String schType = (String)schedForm.get("scheduleType");
            final Long customerId = (Long)schedForm.get("customerId");
            if (schType.equals("Daily")) {
                final String time = (String)schedForm.get("dailyTime");
                final String date = (String)schedForm.get("dailyDate");
                final String dailyIntervalType = (String)schedForm.get("dailyIntervalType");
                final HashMap schedulerProps = new HashMap();
                schedulerProps.put("workEngineId", workEngineId);
                schedulerProps.put("operationType", operationType);
                schedulerProps.put("workflowName", workflowName);
                schedulerProps.put("schedulerName", "ScheduledDBBackupTaskScheduler");
                schedulerProps.put("taskName", "ScheduledDBBackupTask");
                schedulerProps.put("className", "com.me.devicemanagement.onpremise.server.task.ScheduledDBBackupTask");
                schedulerProps.put("description", "Scheduled db back up configured");
                schedulerProps.put("email", email);
                schedulerProps.put("customerID", customerId);
                schedulerProps.put("owner", ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName());
                schedulerProps.put("schType", "Daily");
                schedulerProps.put("time", time);
                schedulerProps.put("date", date);
                schedulerProps.put("dailyIntervalType", dailyIntervalType);
                schedulerProps.put("timezone", schedForm.get("timezone"));
                taskId = ApiFactoryProvider.getSchedulerAPI().createScheduler(schedulerProps);
            }
            return taskId;
        }
        catch (final Exception e) {
            ScheduleDBBackupUtil.logger.log(Level.WARNING, "Failure while saving DBBackup Schedule Settings", e);
            throw e;
        }
    }
    
    public static String getEncryptedDBBackupPassword(final String pwd) {
        try {
            return new EnDecryptAES256Impl().encrypt(pwd, WinAccessProvider.getInstance().nativeGetDefaultDBBackupPassword());
        }
        catch (final Exception ex) {
            ScheduleDBBackupUtil.logger.log(Level.INFO, "Cannot get default db backup password from native.", ex);
            return null;
        }
    }
    
    public static String getDecryptedDBBackupPassword(final String pwd) {
        try {
            return new EnDecryptAES256Impl().decrypt(pwd, WinAccessProvider.getInstance().nativeGetDefaultDBBackupPassword());
        }
        catch (final Exception ex) {
            ScheduleDBBackupUtil.logger.log(Level.INFO, "Cannot get default db backup password from native.", ex);
            return null;
        }
    }
    
    public static Boolean isMssqlDBPermissionsAvailableToTakeBakBackup(final boolean refresh) throws Exception {
        if (!refresh && ScheduleDBBackupUtil.isBakPermissionAllowed != null) {
            return ScheduleDBBackupUtil.isBakPermissionAllowed;
        }
        try (final Connection connection = RelationalAPI.getInstance().getConnection()) {
            final Boolean res = ScheduleDBBackupUtil.isBakPermissionAllowed = isMssqlDBPermissionsAvailableToTakeBakBackup(BackupRestoreUtil.getDBProps().getProperty("dbname"), connection);
            return res;
        }
        catch (final Exception exception) {
            ScheduleDBBackupUtil.logger.log(Level.INFO, exception.getMessage(), exception);
            throw exception;
        }
    }
    
    public static Boolean isMssqlDBPermissionsAvailableToTakeBakBackup(final String dbName, final Connection connection) throws Exception {
        try {
            return isServerRole(connection, "sysadmin") || ((isMemberRole(connection, "db_backupoperator") || isMemberRole(connection, "db_owner")) && isServerRole(connection, "dbcreator")) || hasPermsByNameInMssqlDB(connection, "CREATE ANY DATABASE");
        }
        catch (final SQLException exception) {
            ScheduleDBBackupUtil.logger.log(Level.INFO, "Exception while checking sql database backup permissions", exception);
            throw exception;
        }
    }
    
    private static boolean isMemberRole(final Connection conn, final String role) throws SQLException, QueryConstructionException {
        boolean result = false;
        final String query = "IF IS_MEMBER ('" + role + "') = 1 SELECT 'true' ELSE SELECT 'false'";
        ScheduleDBBackupUtil.logger.log(Level.FINE, query);
        try (final PreparedStatement ps = conn.prepareStatement(query);
             final ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                result = rs.getBoolean(1);
            }
            else {
                ScheduleDBBackupUtil.logger.log(Level.WARNING, "No information is obtained for " + role);
            }
        }
        ScheduleDBBackupUtil.logger.log(Level.FINE, "Permission for " + role + " is : " + result);
        return result;
    }
    
    private static boolean isServerRole(final Connection conn, final String role) throws SQLException {
        boolean result = false;
        final String query = "IF IS_SRVROLEMEMBER ('" + role + "') = 1 SELECT 'true' ELSE SELECT 'false';";
        ScheduleDBBackupUtil.logger.log(Level.FINE, query);
        try (final PreparedStatement ps = conn.prepareStatement(query);
             final ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                result = rs.getBoolean(1);
            }
            else {
                ScheduleDBBackupUtil.logger.log(Level.WARNING, "No information is obtained for " + role);
            }
        }
        ScheduleDBBackupUtil.logger.log(Level.FINE, "Permission for " + role + " is : " + result);
        return result;
    }
    
    private static boolean hasPermsByNameInMssqlDB(final Connection conn, final String role) throws SQLException {
        boolean result = false;
        final String query = "IF has_perms_by_name(NULL,NULL,'" + role + "') = 1 SELECT 'true' ELSE SELECT 'false';";
        ScheduleDBBackupUtil.logger.log(Level.FINE, query);
        try (final PreparedStatement ps = conn.prepareStatement(query);
             final ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                result = rs.getBoolean(1);
            }
            else {
                ScheduleDBBackupUtil.logger.log(Level.WARNING, "No information is obtained for " + role);
            }
        }
        ScheduleDBBackupUtil.logger.log(Level.FINE, "Permission for " + role + " is : " + result);
        return result;
    }
    
    public static Boolean isMssqlServiceUserHasPrevilegeForScheduleDBLocation(final boolean refresh) throws Exception {
        if (!refresh && ScheduleDBBackupUtil.isBackupFolderPermissionAllowed != null) {
            return ScheduleDBBackupUtil.isBackupFolderPermissionAllowed;
        }
        try (final Connection connection = RelationalAPI.getInstance().getConnection()) {
            final Boolean res = ScheduleDBBackupUtil.isBackupFolderPermissionAllowed = isMssqlServiceUserHasPrevilegeForScheduleDBLocation(BackupRestoreUtil.getDBProps().getProperty("dbname"), connection);
            return res;
        }
        catch (final Exception exception) {
            ScheduleDBBackupUtil.logger.log(Level.INFO, exception.getMessage(), exception);
            throw exception;
        }
    }
    
    public static Boolean isMssqlServiceUserHasPrevilegeForScheduleDBLocation(final String dbName, final Connection connection) throws Exception {
        try {
            final String testBackupPath = BackupRestoreUtil.getInstance().getMickeyBackupConfigProperties().getProperty("default.backup.directory", "ScheduledDBBackup") + File.separator + "test" + File.separator + "mssql" + File.separator + "test.bak";
            final String testLocationCanonicalPath = new File(System.getProperty("server.home")).getCanonicalPath() + File.separator + testBackupPath;
            final String testLocationRemotePath = "\\\\" + InetAddress.getLocalHost().getHostName() + File.separator + testBackupPath;
            final String query = "BACKUP DATABASE " + dbName + " TO DISK =" + " '" + testLocationRemotePath + "' WITH FORMAT, COPY_ONLY;";
            synchronized ("BAK_PERM_CHECK_LOCK") {
                FileUtil.deleteFileOrFolder(new File(testLocationRemotePath));
            }
            new File(testLocationRemotePath).getParentFile().mkdirs();
            final Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try (final Statement statement = connection.createStatement()) {
                        ScheduleDBBackupUtil.logger.log(Level.INFO, "In internal thread");
                        statement.setQueryTimeout(5);
                        statement.execute(query);
                    }
                    catch (final Exception exception) {
                        ScheduleDBBackupUtil.logger.log(Level.INFO, "Exception while executing query", exception);
                        synchronized ("BAK_PERM_CHECK_LOCK") {
                            FileUtil.deleteFileOrFolder(new File(testLocationRemotePath));
                        }
                    }
                    finally {
                        synchronized ("BAK_PERM_CHECK_LOCK") {
                            FileUtil.deleteFileOrFolder(new File(testLocationRemotePath));
                        }
                    }
                }
            });
            thread.start();
            boolean result = false;
            synchronized ("BAK_PERM_CHECK_LOCK") {
                ScheduleDBBackupUtil.logger.log(Level.INFO, "After calling internal thread");
                Thread.sleep(500L);
                result = new File(testLocationCanonicalPath).exists();
                ScheduleDBBackupUtil.logger.log(Level.INFO, result + "");
                FileUtil.deleteFileOrFolder(new File(testLocationRemotePath));
            }
            return result;
        }
        catch (final Exception exception) {
            ScheduleDBBackupUtil.logger.log(Level.INFO, exception.getMessage(), exception);
            throw exception;
        }
    }
    
    static {
        ScheduleDBBackupUtil.logger = Logger.getLogger("ScheduleDBBackup");
        PATH_VALIDATION_MAP = new HashMap<String, Integer>() {
            {
                this.put("VALID_PATH", 5000);
                this.put("UNABLE_TO_EMPTY_PATH", 5001);
                this.put("UNABLE_TO_SET_PATH_UNDER_SERVER_HOME", 5002);
                this.put("UNABLE_TO_SET_RESERVED_PATH", 5003);
                this.put("UNABLE_TO_SET_PATH_DUE_TO_FILE_ACCESS_DENIED", 5004);
                this.put("ERROR", -1);
            }
        };
        ScheduleDBBackupUtil.isBakPermissionAllowed = null;
        ScheduleDBBackupUtil.isBackupFolderPermissionAllowed = null;
    }
}
