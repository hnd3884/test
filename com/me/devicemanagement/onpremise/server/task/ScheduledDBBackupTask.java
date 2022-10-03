package com.me.devicemanagement.onpremise.server.task;

import java.util.Map;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;
import java.util.AbstractMap;
import com.me.devicemanagement.onpremise.tools.backuprestore.action.DMBackupAction;
import java.io.Writer;
import java.io.FileWriter;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.FileUtil;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Range;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.common.DMApplicationHandler;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.pushnotification.PushNotificationUtil;
import org.json.JSONObject;
import java.util.Hashtable;
import com.me.devicemanagement.framework.server.mailmanager.MailDetails;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.me.devicemanagement.onpremise.server.util.ZipUtil;
import java.util.ArrayList;
import com.me.devicemanagement.onpremise.tools.backuprestore.handler.DefaultBackupPasswordProvider;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.DMBackupPasswordProvider;
import com.me.devicemanagement.onpremise.server.backup.ScheduledDBBackupPasswordProvider;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.DMBackupPasswordHandler;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.onpremise.tools.backuprestore.action.DMDBBackupRestore;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.i18n.I18N;
import java.io.IOException;
import java.io.Reader;
import java.io.FileReader;
import com.me.devicemanagement.onpremise.server.troubleshooter.postgres.PostgresCorruptionDetectionUtil;
import com.me.devicemanagement.onpremise.server.troubleshooter.postgres.PostgresCorruptionConstant;
import java.io.File;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.devicemanagement.onpremise.server.util.DBUtil;
import com.me.devicemanagement.onpremise.server.util.ScheduleDBBackupUtil;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.BackupRestoreUtil;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class ScheduledDBBackupTask implements SchedulerExecutionInterface
{
    public static final String BACKUP_CONTENT_TYPE = "backup.content.type";
    private static Logger logger;
    Integer defalutCountMaintain;
    int moreThanFailed;
    boolean isFileSystemDBMismatch;
    
    public ScheduledDBBackupTask() {
        this.defalutCountMaintain = new Integer(7);
        this.moreThanFailed = 2;
        this.isFileSystemDBMismatch = false;
    }
    
    public void executeTask(final Properties taskProps) {
        this.cleanupScheduledBackupJunks();
        boolean sendMailtoCustomer = false;
        Integer noBackupMaintain = new Integer(7);
        final Long currTime = new Long(System.currentTimeMillis());
        ScheduledDBBackupTask.logger.log(Level.INFO, "**************************************************************************");
        ScheduledDBBackupTask.logger.log(Level.INFO, "ScheduledDBBackupTask Task is invoked at " + SyMUtil.getDate((long)currTime));
        try {
            BackupRestoreUtil.getInstance().addBackupTrackingDetails(7, String.valueOf(BackupRestoreUtil.getInstance().useNativeForExecution()));
            final DataObject data = ScheduleDBBackupUtil.getDbBackupInfoDO();
            final Row dcBackupInfoRow = data.getRow("DBBackupInfo");
            noBackupMaintain = (Integer)dcBackupInfoRow.get("NO_OF_BACKUP");
            final Boolean isBackupPWDEnabled = ScheduleDBBackupUtil.isPasswordEnabled(dcBackupInfoRow);
            ScheduledDBBackupTask.logger.log(Level.WARNING, "DB Backup Started ");
            String pushNotifyRemoteBkReason = "";
            String dbfilesDEST = ScheduleDBBackupUtil.getBackupLocation();
            final boolean isRemotePath = ScheduleDBBackupUtil.isRemoteBackupPath(dbfilesDEST);
            boolean takeBackupInRemotePath = true;
            String remoteBackupFailedReason = null;
            if (isRemotePath) {
                final Properties preCheckRemoteBackup = this.preCheckRemoteBackup(dbfilesDEST);
                ScheduledDBBackupTask.logger.log(Level.INFO, "Pre-Check for Remote Backup :" + preCheckRemoteBackup);
                takeBackupInRemotePath = Boolean.parseBoolean(preCheckRemoteBackup.getProperty("isValid"));
                if (!takeBackupInRemotePath) {
                    ScheduledDBBackupTask.logger.log(Level.WARNING, "Since preCheckRemoteBackup has been failed, Reset path to default location");
                    dbfilesDEST = BackupRestoreUtil.getInstance().getDefaultBackupLocation();
                    remoteBackupFailedReason = preCheckRemoteBackup.getProperty("message");
                    pushNotifyRemoteBkReason = preCheckRemoteBackup.getProperty("pushNotificationReason");
                }
            }
            ScheduledDBBackupTask.logger.log(Level.INFO, "Backup location : " + dbfilesDEST);
            this.setBackupPWDProvider(isBackupPWDEnabled);
            boolean backupStatus = true;
            String reason = null;
            String pushNotificationReason = "";
            final String dbName = DBUtil.getActiveDBName();
            if (dbName.equalsIgnoreCase("mssql") && !SyMUtil.isFosReplicationPending()) {
                if (!SyMUtil.isDBMatchWithFileSystem()) {
                    ScheduledDBBackupTask.logger.log(Level.WARNING, "DB and File System was mismatch, Do not proceed Scheduled DB backup.");
                    this.isFileSystemDBMismatch = true;
                }
                else {
                    MessageProvider.getInstance().hideMessage("SQL_DB_FILE_SYSTEM_MISMATCH");
                    if (new File(System.getProperty("server.home") + File.separator + "bin" + File.separator + "filesystem.lock").exists()) {
                        ScheduledDBBackupTask.logger.log(Level.INFO, "File System lock file exists. No need it. Delete status : " + new File(System.getProperty("server.home") + File.separator + "bin" + File.separator + "filesystem.lock").delete());
                    }
                }
            }
            boolean takeBinaryBackup = true;
            final String dumpBackupPath = BackupRestoreUtil.getInstance().getDefaultBackupLocation() + File.separator + "DumpBackup";
            final File corruptionLockFile = new File(PostgresCorruptionConstant.CORRUPTION_LOCK_FILE);
            final boolean isCorruptionPresent = corruptionLockFile.exists();
            if (!this.isFileSystemDBMismatch) {
                FileReader fileReader = null;
                try {
                    ScheduledDBBackupTask.logger.log(Level.INFO, "Going to take db backup..");
                    ScheduledDBBackupTask.logger.log(Level.INFO, "isCorruptionPresent: " + isCorruptionPresent);
                    this.takePeriodicDumpBackup(dumpBackupPath, isCorruptionPresent);
                    if (isCorruptionPresent) {
                        ScheduledDBBackupTask.logger.log(Level.INFO, "Going to take dump backup since corruption is present ..");
                        final boolean dumpBackup = this.takeDumpBackup(dbfilesDEST);
                        PostgresCorruptionDetectionUtil.getInstance().updateBackupTypeStatus("dump", dumpBackup);
                        takeBinaryBackup = !dumpBackup;
                        final Properties corruptionProp = new Properties();
                        fileReader = new FileReader(corruptionLockFile);
                        corruptionProp.load(fileReader);
                        sendMailtoCustomer = Boolean.parseBoolean(((Hashtable<K, String>)corruptionProp).get("notify.cust.thru.mail"));
                        ScheduledDBBackupTask.logger.log(Level.INFO, "Reading corruption properties : " + corruptionProp);
                        if (takeBinaryBackup) {
                            ScheduledDBBackupTask.logger.log(Level.INFO, "binary backup is taken since dump backup failed in file corruption block.");
                            this.takeBinaryBackup(dbfilesDEST);
                        }
                    }
                    else if (BackupRestoreUtil.getInstance().getBackupType().equalsIgnoreCase("dump")) {
                        final boolean status = this.takeDumpBackup(dbfilesDEST);
                        ScheduledDBBackupTask.logger.log(Level.INFO, "Dump Backup status" + status);
                    }
                    else {
                        final boolean status = this.takeBinaryBackup(dbfilesDEST);
                        ScheduledDBBackupTask.logger.log(Level.INFO, "Binary Backup status:" + status);
                    }
                }
                catch (final Exception e) {
                    backupStatus = false;
                    ScheduledDBBackupTask.logger.log(Level.INFO, "Exception while performing backup", e);
                    reason = e.getMessage();
                    pushNotificationReason = "An unknown error has occurred.";
                    if (fileReader != null) {
                        try {
                            fileReader.close();
                        }
                        catch (final IOException e2) {
                            ScheduledDBBackupTask.logger.log(Level.WARNING, "Failed Close the corruption lock File Reader:", e2);
                        }
                    }
                }
                finally {
                    if (fileReader != null) {
                        try {
                            fileReader.close();
                        }
                        catch (final IOException e3) {
                            ScheduledDBBackupTask.logger.log(Level.WARNING, "Failed Close the corruption lock File Reader:", e3);
                        }
                    }
                }
            }
            else {
                backupStatus = false;
                reason = I18N.getMsg("dc.db.backup.failure.filedbmismatch.mailcontent", new Object[0]);
                pushNotificationReason = "Database and file system mismatch.";
                ScheduledDBBackupTask.logger.log(Level.INFO, "Reason for backup failure: " + reason);
            }
            final long sysCurrenttime = System.currentTimeMillis();
            final long timeTaken = sysCurrenttime - currTime;
            final double timeTakenMin = (double)(timeTaken / 60000L);
            if (backupStatus) {
                ScheduledDBBackupTask.logger.log(Level.INFO, "DB Backup Completed and Time Taken for complete the process  " + timeTakenMin);
                if (isRemotePath && !takeBackupInRemotePath) {
                    final String fileName = this.getLastModifiedFile(dbfilesDEST).getName();
                    reason = I18N.getMsg("dc.admin.scheduleDBBackup.remote_access_denied", new Object[] { BackupRestoreUtil.getInstance().getDefaultBackupLocation(), remoteBackupFailedReason });
                    pushNotificationReason = "Your database is temporarily backed up at " + BackupRestoreUtil.getInstance().getDefaultBackupLocation() + " due to " + pushNotifyRemoteBkReason;
                    this.sendEmail(reason, false, true, pushNotificationReason);
                }
            }
            else {
                ScheduledDBBackupTask.logger.log(Level.WARNING, "DB Backup Failed ");
                this.sendEmail(reason, false, false, pushNotificationReason);
            }
            ScheduledDBBackupTask.logger.log(Level.WARNING, "DB Backup End and status " + backupStatus);
            dcBackupInfoRow.set("SHOW_MESSAGE_STATUS", (Object)backupStatus);
            final Row backupFileInfoRow = new Row("DBBackupFilesInfo");
            if (backupStatus) {
                final String fileName2 = this.getLastModifiedFile(dbfilesDEST).getName();
                ScheduledDBBackupTask.logger.log(Level.INFO, "DB Backup File name " + fileName2);
                backupFileInfoRow.set("FILE_NAME", (Object)fileName2);
                this.deleteOldBackup(noBackupMaintain, dbfilesDEST);
                final Properties pr = new Properties();
                pr.setProperty("LastSuccessfullScheduledBackup", fileName2);
                pr.setProperty("ScheduledBackupLocation", dbfilesDEST);
                updateBackupDetailsInFile(pr);
            }
            final String type = BackupRestoreUtil.getInstance().getMickeyBackupConfigProperties().getProperty("backup.content.type", "binary");
            final int backupType = type.equals("binary") ? 1 : 2;
            backupFileInfoRow.set("BACKUP_STATUS", (Object)backupStatus);
            backupFileInfoRow.set("GENRATED_TIME", (Object)currTime);
            backupFileInfoRow.set("BACKUP_TYPE", (Object)backupType);
            data.addRow(backupFileInfoRow);
            data.updateRow(dcBackupInfoRow);
            this.updateMessageStatus(data, backupStatus);
            SyMUtil.getPersistence().update(data);
            int backupLocationType;
            if (ScheduleDBBackupUtil.isRemoteBackupPath(dbfilesDEST)) {
                backupLocationType = 3;
            }
            else if (!dbfilesDEST.equalsIgnoreCase(BackupRestoreUtil.getInstance().getDefaultBackupLocation())) {
                backupLocationType = 2;
            }
            else {
                backupLocationType = 1;
            }
            BackupRestoreUtil.getInstance().addBackupTrackingDetails(1, String.valueOf(backupLocationType));
            BackupRestoreUtil.getInstance().addBackupTrackingDetails(8, String.valueOf(ScheduleDBBackupUtil.isPasswordEnabled(dcBackupInfoRow)));
            BackupRestoreUtil.getInstance().addBackupTrackingDetails(2, String.valueOf(this.getEMailAddress() != null));
            final String backupStatusString = backupStatus ? "success" : reason;
            BackupRestoreUtil.getInstance().addBackupTrackingDetails(3, backupStatusString);
            BackupRestoreUtil.getInstance().addBackupTrackingDetails(5, String.valueOf(noBackupMaintain));
            final String isBakBackup = System.getProperty("isBakTaken", "false").equalsIgnoreCase("true") ? "BAK" : "DUMP";
            BackupRestoreUtil.getInstance().addBackupTrackingDetails(9, isBakBackup);
            BackupRestoreUtil.getInstance().addBackupTrackingDetails(11, String.valueOf(ScheduleDBBackupUtil.isBackupFolderPermissionAllowed));
            BackupRestoreUtil.getInstance().addBackupTrackingDetails(12, String.valueOf(ScheduleDBBackupUtil.isBakPermissionAllowed));
            if (backupStatus && isCorruptionPresent && sendMailtoCustomer) {
                ScheduledDBBackupTask.logger.log(Level.INFO, "Since sendMailtoCustomer is true, going to get name of dump backup file for restore.. ");
                final Properties lastDumpBackupProp = this.getLastDumpBackupProp(dumpBackupPath);
                final String dumpFileName = lastDumpBackupProp.getProperty("dumpFileName");
                if (dumpFileName != null && dumpFileName.length() > 0) {
                    reason = I18N.getMsg("dc.db.backup.failure.dbcorruption.mailcontent", new Object[] { lastDumpBackupProp.getProperty("restoreFilePath"), dumpFileName });
                    pushNotificationReason = "Database is corrupted.";
                    ScheduledDBBackupTask.logger.log(Level.INFO, "customer mail content: " + reason);
                    this.sendEmail(reason, true, false, pushNotificationReason);
                }
                else {
                    ScheduledDBBackupTask.logger.log(Level.WARNING, "Suggest Dump File not available. Hence we skipped corruption email");
                }
            }
            if (isCorruptionPresent && takeBinaryBackup) {
                PostgresCorruptionDetectionUtil.getInstance().updateBackupTypeStatus("binary", backupStatus);
            }
        }
        catch (final Exception e4) {
            ScheduledDBBackupTask.logger.log(Level.SEVERE, "Exception while backup db operation ", e4);
        }
        this.cleanupScheduledBackupJunks();
        this.storeBackupDetailInFile();
        this.updateMsslBackupMessages();
        ScheduledDBBackupTask.logger.log(Level.WARNING, "**************************************************************************");
    }
    
    private void updateMsslBackupMessages() {
        try {
            final String dbName = com.me.devicemanagement.framework.server.util.DBUtil.getActiveDBName();
            if (dbName.equalsIgnoreCase("mssql")) {
                final boolean isBakTaken = System.getProperty("isBakTaken", "false").equalsIgnoreCase("true");
                if (isBakTaken) {
                    ScheduleDBBackupUtil.isBackupFolderPermissionAllowed = true;
                    ScheduleDBBackupUtil.isBakPermissionAllowed = true;
                }
                else if (DMDBBackupRestore.isBakFormatEnabled) {
                    ScheduleDBBackupUtil.isMssqlDBPermissionsAvailableToTakeBakBackup(true);
                    ScheduleDBBackupUtil.isMssqlServiceUserHasPrevilegeForScheduleDBLocation(true);
                }
            }
        }
        catch (final Exception e) {
            ScheduledDBBackupTask.logger.log(Level.INFO, "Exception while updating mssql bak permission messages.", e);
        }
    }
    
    public static void updateBackupDetailsInFile(final Properties pr) {
        String backupFile = null;
        try {
            backupFile = SyMUtil.getInstallationDir() + File.separator + "bin" + File.separator + "BackupDetails.txt";
            ScheduledDBBackupTask.logger.log(Level.WARNING, "Going to create backupdetails file: " + backupFile);
            if (ApiFactoryProvider.getFileAccessAPI().isFileExists(backupFile)) {
                final Properties backupProps = FileAccessUtil.readProperties(backupFile);
                ScheduledDBBackupTask.logger.log(Level.WARNING, "Contents of existing backupdetails file: " + backupProps);
            }
            FileAccessUtil.storeProperties(pr, backupFile, false);
        }
        catch (final Exception ex) {
            ScheduledDBBackupTask.logger.log(Level.WARNING, "Caught exception while creating backupdetails file: ", ex);
        }
    }
    
    private Properties preCheckRemoteBackup(final String dbfilesDEST) {
        ScheduledDBBackupTask.logger.log(Level.INFO, "Going to Check backup Remote location permissions ..");
        final Properties properties = new Properties();
        try {
            final boolean canWrite = ScheduleDBBackupUtil.isBackupLocationReachable(dbfilesDEST);
            if (!canWrite) {
                properties.setProperty("isValid", "false");
                properties.setProperty("message", I18N.getMsg("dc.admin.scheduleDBBackup.remote_denied_precheck", new Object[0]));
                properties.setProperty("pushNotificationReason", I18N.getMsg("dc.admin.scheduleDBBackup.remote_denied_precheck", new Object[0]));
                ScheduledDBBackupTask.logger.log(Level.SEVERE, "Remote backup location is Access Denied");
                return properties;
            }
            BackupRestoreUtil.getInstance().preCheck(dbfilesDEST);
            properties.setProperty("isValid", "true");
            ScheduledDBBackupTask.logger.log(Level.INFO, "Pre-Check are verified successfully going to take backup on remote location");
        }
        catch (final Exception ex) {
            properties.setProperty("isValid", "false");
            if (ex instanceof NullPointerException) {
                properties.setProperty("message", "NullPointerException");
            }
            else {
                properties.setProperty("message", ex.getMessage());
            }
            properties.setProperty("pushNotificationReason", "an unknown error has occurred.");
            ScheduledDBBackupTask.logger.log(Level.SEVERE, "Pre-Check backup Remote location failed", ex);
        }
        return properties;
    }
    
    private void takePeriodicDumpBackup(final String dumpBackupPath, final boolean isCorruptionPresent) {
        final boolean isDumpBackupApplicable = this.isDumpNecessary(dumpBackupPath);
        ScheduledDBBackupTask.logger.log(Level.INFO, "Is Dump Applicable ? " + isDumpBackupApplicable);
        if (!isCorruptionPresent && isDumpBackupApplicable) {
            ScheduledDBBackupTask.logger.log(Level.INFO, "Dump is applicable.. taking dump backup");
            final String backupType = BackupRestoreUtil.getInstance().getBackupType();
            final boolean periodicDumpBackupStatus = this.takeDumpBackup(dumpBackupPath);
            BackupRestoreUtil.getInstance().updateBackupType(backupType);
            ScheduledDBBackupTask.logger.log(Level.INFO, "Periodic Dump Backup status: " + periodicDumpBackupStatus);
            this.deleteOldDumpBackup(dumpBackupPath);
        }
    }
    
    private void setBackupPWDProvider(final Boolean isBackupPWDEnabled) {
        ScheduledDBBackupTask.logger.log(Level.INFO, "Is backup password protected : " + isBackupPWDEnabled);
        if (isBackupPWDEnabled) {
            DMBackupPasswordHandler.getInstance().setEncryptionType(2);
            DMBackupPasswordHandler.getInstance().setPasswordProvider((DMBackupPasswordProvider)new ScheduledDBBackupPasswordProvider());
        }
        else {
            DMBackupPasswordHandler.getInstance().setEncryptionType(1);
            DMBackupPasswordHandler.getInstance().setPasswordProvider((DMBackupPasswordProvider)new DefaultBackupPasswordProvider());
        }
    }
    
    private boolean DBBackup7ZipCompression(final String fromFile, final String zipFileName) {
        Process process = null;
        boolean status = false;
        try {
            final List<String> command = new ArrayList<String>();
            command.add(System.getProperty("server.home") + File.separator + "bin" + File.separator + "7za.exe");
            command.add("a");
            command.add(zipFileName);
            command.add(fromFile);
            command.add("-mmt=" + ZipUtil.get7ZipCoreCount());
            ScheduledDBBackupTask.logger.log(Level.WARNING, "cmd {0} ", command.toArray());
            final ProcessBuilder builder = new ProcessBuilder(command);
            process = builder.start();
            ScheduledDBBackupTask.logger.log(Level.WARNING, "process {0} ", process);
            final BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            final StringBuffer resultBuffer = new StringBuffer();
            String s = null;
            while ((s = in.readLine()) != null) {
                if (s != null && s.trim().equalsIgnoreCase("Everything is Ok")) {
                    status = true;
                }
                resultBuffer.append(s);
            }
            process.destroy();
            ScheduledDBBackupTask.logger.log(Level.WARNING, "Result {0} ", resultBuffer.toString());
        }
        catch (final Exception e) {
            ScheduledDBBackupTask.logger.log(Level.WARNING, "Exception while take7zipBackup operation ", e);
            if (process != null) {
                process.destroy();
            }
            status = false;
        }
        return status;
    }
    
    private void sendEmail(final String reason, final boolean isCorruption, final boolean isException, final String pushNotificationReason) {
        try {
            final String strToAddress = this.getEMailAddress();
            final DataObject dobj = SyMUtil.getEmailAddDO("DBBackup");
            if (dobj.isEmpty()) {
                ScheduledDBBackupTask.logger.log(Level.WARNING, "E-Mail Alert Not configured !!!");
                return;
            }
            final Row emailAlertRow = dobj.getRow("EMailAddr");
            if (emailAlertRow == null) {
                ScheduledDBBackupTask.logger.log(Level.WARNING, "E-Mail Alert Not configured !!!");
                return;
            }
            final Boolean isEnable = (Boolean)emailAlertRow.get("SEND_MAIL");
            if (!isEnable) {
                ScheduledDBBackupTask.logger.log(Level.WARNING, "E-Mail Alerts is Stopped !!!");
                return;
            }
            if (strToAddress == null) {
                ScheduledDBBackupTask.logger.log(Level.WARNING, "E-Mail address is null.  Cant Proceed!!!");
            }
            final Hashtable<String, String> mailSenderDetails = ApiFactoryProvider.getMailSettingAPI().getMailSenderDetails();
            final String frAdd = mailSenderDetails.get("mail.fromAddress");
            final String subject = I18N.getMsg("dc.admin.backup_failed.mail.subject", new Object[0]);
            final MailDetails maildetails = new MailDetails(frAdd, strToAddress);
            maildetails.senderDisplayName = mailSenderDetails.get("mail.fromName");
            final String mailContent = this.getMailContent(reason);
            maildetails.bodyContent = mailContent;
            maildetails.ccAddress = null;
            maildetails.subject = subject;
            maildetails.attachment = null;
            if (isCorruption) {
                maildetails.subject = I18N.getMsg("dc.db.backup.failure.dbcorruption.mailSubject", new Object[0]);
                maildetails.bodyContent = reason;
            }
            this.sendMobileNotification(pushNotificationReason, isException);
            ApiFactoryProvider.getMailSettingAPI().addToMailQueue(maildetails, 0);
        }
        catch (final Exception ex) {
            ScheduledDBBackupTask.logger.log(Level.WARNING, "Exception while sending alert mail", ex);
        }
    }
    
    public void sendMobileNotification(final String reason, final boolean isException) {
        final JSONObject messageObject = new JSONObject();
        final JSONObject customPayLoad = new JSONObject();
        try {
            ScheduledDBBackupTask.logger.log(Level.INFO, "ScheduledDBBackupTask-Entered sendMobileNotification");
            final String pushNotifyUsers = this.getPushNotifyUsersByModuleName("DBBackup");
            if (pushNotifyUsers != null && pushNotifyUsers.length() > 0) {
                final List<Long> userIdList = new ArrayList<Long>();
                final String[] split;
                final String[] userIds = split = pushNotifyUsers.split(",");
                for (final String userId : split) {
                    userIdList.add(Long.parseLong(userId));
                }
                messageObject.put("title", (Object)"Database backup failure");
                messageObject.put("message", (Object)reason);
                customPayLoad.put("n_type", (Object)"prod");
                customPayLoad.put("moduleId", 11);
                customPayLoad.put("is_known", !isException);
                PushNotificationUtil.getInstance().sendPushNotification((List)userIdList, messageObject, customPayLoad);
            }
            else {
                ScheduledDBBackupTask.logger.log(Level.INFO, "ScheduledDBBackupTask-Empty PushNotify Users");
            }
        }
        catch (final Exception e) {
            ScheduledDBBackupTask.logger.log(Level.WARNING, "ScheduledDBBackupTask - Exception in sending mobile Notification ", e);
        }
    }
    
    public String getPushNotifyUsersByModuleName(final String moduleName) {
        String pushNotifyUsers = "";
        try {
            final SelectQuery selectQuery = this.getModuleCriteriaQuery(moduleName);
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Row row = dataObject.getRow("PushNotificationDetails");
                if (row.get("PUSH_NOTIFICATION_USERS") != null) {
                    pushNotifyUsers = (String)row.get("PUSH_NOTIFICATION_USERS");
                }
            }
        }
        catch (final Exception e) {
            ScheduledDBBackupTask.logger.log(Level.WARNING, "Exception in getPushNotifyUsersByModuleName", e);
        }
        return pushNotifyUsers;
    }
    
    private SelectQuery getModuleCriteriaQuery(final String moduleName) {
        SelectQuery selectQuery = null;
        try {
            selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("PushNotificationDetails"));
            selectQuery.addSelectColumn(Column.getColumn("PushNotificationDetails", "PUSH_NOTIFICATION_ID"));
            selectQuery.addSelectColumn(Column.getColumn("PushNotificationDetails", "PUSH_NOTIFICATION_USERS"));
            final Criteria moduleCriteria = new Criteria(Column.getColumn("PushNotificationDetails", "MODULE_NAME"), (Object)moduleName, 0);
            selectQuery.setCriteria(moduleCriteria);
        }
        catch (final Exception e) {
            ScheduledDBBackupTask.logger.log(Level.WARNING, "Exception in getModuleCriteriaQuery", e);
        }
        return selectQuery;
    }
    
    private String getMailContent(final String reason) throws Exception {
        final String serverURL = ApiFactoryProvider.getUtilAccessAPI().getServerURL();
        final StringBuilder spaceSeparator = new StringBuilder("<tr><td><br></br></td> </tr>");
        final StringBuilder tableStartTag = new StringBuilder("<table width='100%' style='font-family:Lato,Roboto;font-size:13px;'> <tbody>");
        final String rowStartTag = "<tr><td>";
        final String rowEndTag = "</td></tr>";
        final String titleTag = rowStartTag + "<b> " + I18N.getMsg("dc.admin.backup_failed.mail.message", new Object[0]) + " </b> " + rowEndTag;
        final String reasonTag = rowStartTag + I18N.getMsg("dc.common.REASON", new Object[0]) + " : " + reason + rowEndTag;
        String readKBTag = rowStartTag + I18N.getMsg("dc.common.READ_KB", new Object[0]) + " : <a target='_blank' href='" + ProductUrlLoader.getInstance().getValue("dcUrl") + "/backup-creation-failed.html?" + ProductUrlLoader.getInstance().getValue("trackingcode") + "'>" + ProductUrlLoader.getInstance().getValue("dcUrl") + "/backup-creation-failed.html</a> " + rowEndTag;
        if (DMApplicationHandler.isMdmProduct()) {
            readKBTag = rowStartTag + I18N.getMsg("dc.common.READ_KB", new Object[0]) + " : <a target='_blank' href='" + ProductUrlLoader.getInstance().getValue("mdmUrl") + "/kb/backup-creation-failed.html?" + ProductUrlLoader.getInstance().getValue("trackingcode") + "'>" + ProductUrlLoader.getInstance().getValue("mdmUrl") + "/kb/backup-creation-failed.html</a> " + rowEndTag;
        }
        if (this.isFileSystemDBMismatch) {
            readKBTag = rowStartTag + SyMUtil.setSQLDBFileSystemMessageContentForBackup(I18N.getMsg("dc.common.homePage.scbackup.dbmismatch.content1", new Object[0])) + rowEndTag;
        }
        final String footerTag = " <tr> <td style='color:#888888;'>" + I18N.getMsg("dc.admin.backup_failed.mail.footer", new Object[] { serverURL, ProductUrlLoader.getInstance().getValue("displayname") }) + " </td> </tr>";
        final StringBuilder mailContent = tableStartTag.append(titleTag).append((CharSequence)spaceSeparator).append(reasonTag).append((CharSequence)spaceSeparator).append(readKBTag).append((CharSequence)spaceSeparator).append(footerTag).append("</table>");
        return mailContent.toString();
    }
    
    private String getEMailAddress() throws Exception {
        final String sourceMethod = "getEMailAddress";
        String strEMailAddr = null;
        final DataObject dobj = SyMUtil.getEmailAddDO("DBBackup");
        if (!dobj.isEmpty()) {
            final StringBuffer buffer = new StringBuffer();
            final Iterator iter = dobj.getRows("EMailAddr");
            while (iter.hasNext()) {
                final Row row = iter.next();
                buffer.append((String)row.get("EMAIL_ADDR"));
                buffer.append(",");
            }
            strEMailAddr = buffer.toString();
        }
        else {
            ScheduledDBBackupTask.logger.log(Level.WARNING, sourceMethod + "DB Backup To Email address configuration is Empty ");
        }
        return strEMailAddr;
    }
    
    private void updateMessageStatus(final DataObject dbInfoDO, final boolean status) throws Exception {
        final Criteria criteria = new Criteria(Column.getColumn("DBBackupFilesInfo", "BACKUP_STATUS"), (Object)false, 0);
        final DataObject dbFilesInfoDO = this.getDBBackupInfoDataObject(criteria, 1);
        ScheduledDBBackupTask.logger.log(Level.WARNING, "updateMessageStatus data object : " + dbFilesInfoDO);
        final Row dbBackupInfoRow = dbInfoDO.getRow("DBBackupInfo");
        if (!status) {
            if (this.isFileSystemDBMismatch) {
                dbBackupInfoRow.set("SHOW_MESSAGE_STATUS", (Object)true);
                MessageProvider.getInstance().unhideMessage("SQL_DB_FILE_SYSTEM_MISMATCH");
                MessageProvider.getInstance().hideMessage("SCHEDULED_BACKUP_FAILED");
            }
            else if (dbFilesInfoDO.size("DBBackupFilesInfo") >= this.moreThanFailed) {
                dbBackupInfoRow.set("SHOW_MESSAGE_STATUS", (Object)true);
                MessageProvider.getInstance().unhideMessage("SCHEDULED_BACKUP_FAILED");
            }
            else {
                dbBackupInfoRow.set("SHOW_MESSAGE_STATUS", (Object)false);
                MessageProvider.getInstance().hideMessage("SCHEDULED_BACKUP_FAILED");
            }
        }
        else {
            dbBackupInfoRow.set("SHOW_MESSAGE_STATUS", (Object)false);
            MessageProvider.getInstance().hideMessage("SCHEDULED_BACKUP_FAILED");
            final Iterator itr = dbFilesInfoDO.getRows("DBBackupFilesInfo");
            while (itr.hasNext()) {
                final Row dbFilesInfoRow = itr.next();
                SyMUtil.getPersistence().delete(dbFilesInfoRow);
            }
        }
        dbInfoDO.updateRow(dbBackupInfoRow);
    }
    
    private DataObject getDBBackupInfoDataObject(final Criteria criteria, final int range) throws Exception {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("DBBackupFilesInfo"));
        query.addSelectColumn(new Column((String)null, "*"));
        final Range rangeLimit = new Range(range, 0);
        query.setRange(rangeLimit);
        final SortColumn sortcolumn = new SortColumn(Column.getColumn("DBBackupFilesInfo", "GENRATED_TIME"), false);
        query.addSortColumn(sortcolumn);
        query.setCriteria(criteria);
        final DataObject data = SyMUtil.getPersistence().get(query);
        return data;
    }
    
    private void deleteOldBackup(final int noofbackmaintain, final String dir) throws Exception {
        try {
            final Criteria criteria = new Criteria(Column.getColumn("DBBackupFilesInfo", "BACKUP_STATUS"), (Object)true, 0);
            final DataObject data = this.getDBBackupInfoDataObject(criteria, noofbackmaintain);
            ScheduledDBBackupTask.logger.log(Level.WARNING, "To be delete backup files data object : " + data);
            final Iterator itr = data.getRows("DBBackupFilesInfo");
            while (itr.hasNext()) {
                final Row dbBackFilesRow = itr.next();
                final String fileName = (String)dbBackFilesRow.get("FILE_NAME");
                final String filePath = dir + File.separator + fileName;
                try {
                    final File file = new File(filePath);
                    if (file.exists()) {
                        file.delete();
                        ScheduledDBBackupTask.logger.log(Level.INFO, "Backup file {0} deleted sucessfully  : ", filePath);
                    }
                }
                catch (final Exception e) {
                    ScheduledDBBackupTask.logger.log(Level.WARNING, "Exception while deleting file : ", e);
                }
                final File file2 = new File(filePath);
                if (!file2.exists()) {
                    SyMUtil.getPersistence().delete(dbBackFilesRow);
                }
            }
        }
        catch (final Exception e2) {
            ScheduledDBBackupTask.logger.log(Level.WARNING, "deleteOldBackup Exception while deleting file : ", e2);
        }
    }
    
    private File getLastModifiedFile(final String rootDirectory) {
        File lastModifiedFile = null;
        final File logDir = new File(rootDirectory);
        if (!logDir.isDirectory()) {
            logDir.mkdirs();
        }
        final File[] fileList = logDir.listFiles();
        if (((fileList != null) ? fileList.length : 0) > 0) {
            lastModifiedFile = fileList[0];
            for (final File file : fileList) {
                if (file.isFile() && file.lastModified() > lastModifiedFile.lastModified()) {
                    lastModifiedFile = file;
                }
            }
        }
        return lastModifiedFile;
    }
    
    public void cleanupScheduledBackupJunks() {
        try {
            final String scheduledBackupFolderStr = BackupRestoreUtil.getInstance().getDefaultBackupLocation();
            final File[] scheduledBackupZipFolders = new File(scheduledBackupFolderStr).listFiles();
            final List<String> scheduledBackupFoldersInDB = this.getScheduledBackupFolderNameFromDB();
            if (scheduledBackupFoldersInDB != null && !scheduledBackupFoldersInDB.isEmpty()) {
                for (int i = 0; i < scheduledBackupZipFolders.length; ++i) {
                    final String fileName = scheduledBackupZipFolders[i].getName();
                    if (!scheduledBackupFoldersInDB.contains(fileName) && !fileName.equalsIgnoreCase("DumpBackup")) {
                        FileUtil.deleteFileOrFolder(scheduledBackupZipFolders[i]);
                        ScheduledDBBackupTask.logger.log(Level.INFO, "Scheduled DB Backup deleted folder : " + scheduledBackupZipFolders[i].getName());
                    }
                }
            }
            this.deleteOldDumpBackup(BackupRestoreUtil.getInstance().getDefaultBackupLocation() + File.separator + "DumpBackup");
        }
        catch (final Exception e) {
            ScheduledDBBackupTask.logger.log(Level.SEVERE, "Exception occurred while cleaning scheduled backup junks..", e);
        }
    }
    
    public void storeBackupDetailInFile() {
        final Properties backupDetails = SyMUtil.getLastBackupDetails();
        if (backupDetails.size() > 0) {
            final String backupDetailsFilePath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "User-Conf" + File.separator + "server.props";
            try (final FileWriter fileWriter = new FileWriter(new File(backupDetailsFilePath))) {
                backupDetails.store(fileWriter, null);
            }
            catch (final Exception e) {
                ScheduledDBBackupTask.logger.log(Level.SEVERE, "Exception occurred while storing the scheduled backup details in File..", e);
            }
        }
    }
    
    private List<String> getScheduledBackupFolderNameFromDB() {
        List<String> folderName = null;
        try {
            folderName = new ArrayList<String>();
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("DBBackupFilesInfo"));
            query.addSelectColumn(new Column((String)null, "*"));
            final Criteria criteria = new Criteria(Column.getColumn("DBBackupFilesInfo", "BACKUP_STATUS"), (Object)true, 0);
            query.setCriteria(criteria);
            final DataObject dataObject = SyMUtil.getPersistence().get(query);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("DBBackupFilesInfo");
                while (iterator.hasNext()) {
                    final Row dbBackFilesRow = iterator.next();
                    final String folder = (String)dbBackFilesRow.get("FILE_NAME");
                    folderName.add(folder);
                }
            }
        }
        catch (final Exception e) {
            ScheduledDBBackupTask.logger.log(Level.SEVERE, "Exception occurred while getting ScheduledBackup Folder Name from DB..", e);
        }
        return folderName;
    }
    
    private boolean takeDumpBackup(final String dumpBackupPath) {
        boolean status = false;
        ScheduledDBBackupTask.logger.log(Level.INFO, "Going to take DUMP backup..");
        try {
            final DMBackupAction backupObj = new DMBackupAction(dumpBackupPath);
            BackupRestoreUtil.getInstance().updateBackupType("dump");
            this.takeBackup(backupObj);
            ScheduledDBBackupTask.logger.log(Level.WARNING, "DUMP Backup has been completed");
            status = true;
        }
        catch (final Exception e) {
            ScheduledDBBackupTask.logger.log(Level.SEVERE, "Exception while taking DUMP backup", e);
        }
        return status;
    }
    
    private boolean takeBinaryBackup(final String dbfilesDEST) throws Exception {
        ScheduledDBBackupTask.logger.log(Level.INFO, "Going to take Binary backup..");
        final DMBackupAction backupObj = new DMBackupAction(dbfilesDEST);
        BackupRestoreUtil.getInstance().updateBackupType("binary");
        this.takeBackup(backupObj);
        return true;
    }
    
    public Properties getLastDumpBackupProp(final String dumpBackupPath) {
        String dumpFileName = "";
        String fileNameFromDB = "";
        String restoreFilePath = "";
        long lastModifiedFromDB = 0L;
        final Properties properties = new Properties();
        try {
            final Criteria c1 = new Criteria(Column.getColumn("DBBackupFilesInfo", "BACKUP_STATUS"), (Object)true, 0);
            final Criteria c2 = new Criteria(Column.getColumn("DBBackupFilesInfo", "BACKUP_TYPE"), (Object)2, 0);
            final Criteria c3 = c1.and(c2);
            final DataObject data = this.getDBBackupInfoDataObject(c3, 1);
            final Iterator itr = data.getRows("DBBackupFilesInfo");
            final Row dbBackFilesRow = itr.next();
            lastModifiedFromDB = (long)dbBackFilesRow.get("GENRATED_TIME");
            fileNameFromDB = (String)dbBackFilesRow.get("FILE_NAME");
            final File lastModifiedFile = this.getLastModifiedFile(dumpBackupPath);
            if (lastModifiedFile == null || lastModifiedFromDB > lastModifiedFile.lastModified()) {
                dumpFileName = fileNameFromDB;
                restoreFilePath = new File(dumpBackupPath).getParentFile().getAbsolutePath();
            }
            else {
                dumpFileName = lastModifiedFile.getName();
                restoreFilePath = dumpBackupPath;
            }
            properties.setProperty("dumpFileName", dumpFileName);
            properties.setProperty("restoreFilePath", restoreFilePath);
            ScheduledDBBackupTask.logger.log(Level.INFO, " Last DumpBackup Properties : " + properties);
        }
        catch (final Exception e) {
            ScheduledDBBackupTask.logger.log(Level.SEVERE, "Exception caught while getting dump file name: ", e);
        }
        return properties;
    }
    
    private boolean isDumpNecessary(final String dumpBackupPath) {
        boolean isDumpBackupApplicable = false;
        ScheduledDBBackupTask.logger.log(Level.INFO, "Going to check if dump backup is applicable today..");
        try {
            if (!DBUtil.getActiveDBName().equalsIgnoreCase("postgres")) {
                ScheduledDBBackupTask.logger.log(Level.INFO, "Server is not running in Postgres DB. Weekly dump backup is not applicable");
                return false;
            }
            final Properties backupProps = BackupRestoreUtil.getInstance().getMickeyBackupConfigProperties();
            final long dumpbackup_interval = Long.parseLong(backupProps.getProperty("dump.backup.interval", "7"));
            ScheduledDBBackupTask.logger.log(Level.INFO, "Dump Backup Interval: " + dumpbackup_interval);
            final File lastModifiedFile = this.getLastModifiedFile(dumpBackupPath);
            if (lastModifiedFile == null) {
                return true;
            }
            final long latestDumpCreation = lastModifiedFile.lastModified();
            ScheduledDBBackupTask.logger.log(Level.INFO, "Latest Dump Backup Creation: " + latestDumpCreation);
            final long diffInDays = SyMUtil.getDateDiff(latestDumpCreation, System.currentTimeMillis());
            ScheduledDBBackupTask.logger.log(Level.INFO, "Difference in days: " + diffInDays);
            if (diffInDays >= dumpbackup_interval) {
                isDumpBackupApplicable = true;
            }
        }
        catch (final Exception e) {
            ScheduledDBBackupTask.logger.log(Level.SEVERE, "Exception caught while checking if dump backup should be taken ", e);
        }
        return isDumpBackupApplicable;
    }
    
    private void deleteOldDumpBackup(final String dumpBackupPath) {
        ScheduledDBBackupTask.logger.log(Level.INFO, "Going to delete Old dump Backup file.");
        try {
            final int maintainCount = Integer.parseInt(BackupRestoreUtil.getInstance().getMickeyBackupConfigProperties().getProperty("dump.backup.retain.count", "1"));
            ScheduledDBBackupTask.logger.log(Level.INFO, "Retain Count : " + maintainCount);
            final File folder = new File(dumpBackupPath);
            final File[] listOfFiles = folder.listFiles();
            if (listOfFiles != null && listOfFiles.length > maintainCount) {
                ScheduledDBBackupTask.logger.log(Level.INFO, "Since there are more files than the retention count :" + maintainCount + ", going to delete oldest dump file..");
                final List<File> filesTobeDeleted = new ArrayList<File>();
                Map.Entry<File, Date> latest = new AbstractMap.SimpleEntry<File, Date>(null, null);
                for (final File file : listOfFiles) {
                    if (file.isDirectory()) {
                        filesTobeDeleted.add(file);
                    }
                    else {
                        Date currentFileDate = null;
                        try {
                            currentFileDate = new SimpleDateFormat("MMM-dd-yyyy-HH-mm", Locale.ENGLISH).parse(file.getName().replace(BackupRestoreUtil.getInstance().getBuildNumber() + "-", "").replace(".zip", ""));
                        }
                        catch (final ParseException ex) {
                            ScheduledDBBackupTask.logger.log(Level.INFO, ex.getMessage(), ex);
                        }
                        if (currentFileDate == null) {
                            filesTobeDeleted.add(file);
                        }
                        else if (latest.getValue() == null) {
                            latest = new AbstractMap.SimpleEntry<File, Date>(file, currentFileDate);
                        }
                        else if (latest.getValue().before(currentFileDate)) {
                            filesTobeDeleted.add(latest.getKey());
                            latest = new AbstractMap.SimpleEntry<File, Date>(file, currentFileDate);
                        }
                        else {
                            filesTobeDeleted.add(file);
                        }
                    }
                }
                int removeFileCount = listOfFiles.length - maintainCount;
                for (Iterator<File> iterator = filesTobeDeleted.iterator(); iterator.hasNext() && removeFileCount > 0; --removeFileCount) {
                    final File file2 = iterator.next();
                    ScheduledDBBackupTask.logger.log(Level.INFO, "Backup file {0} deleted successfully  :  {1}", new Object[] { file2.getAbsolutePath(), FileUtil.deleteFileOrFolder(file2) });
                }
            }
        }
        catch (final Exception e) {
            ScheduledDBBackupTask.logger.log(Level.SEVERE, "Exception caught while cleaning up previous dump backup: ", e);
        }
    }
    
    private void backupWithDLL(final DMBackupAction backupObj) throws Exception {
        try {
            System.setProperty("use.native.execution", "true");
            ScheduledDBBackupTask.logger.log(Level.INFO, "Retrying backup with DLL execution...");
            BackupRestoreUtil.printOneLineLog(Level.INFO, "Calling backup from ScheduledDBBackupTask - NativeCall");
            backupObj.backup();
            BackupRestoreUtil.getInstance().useDLLForBackupExe();
        }
        catch (final Exception e) {
            System.setProperty("use.native.execution", "false");
            throw e;
        }
    }
    
    private void takeBackup(final DMBackupAction backupObj) throws Exception {
        try {
            BackupRestoreUtil.printOneLineLog(Level.INFO, "Calling backup from ScheduledDBBackupTask");
            backupObj.backup();
        }
        catch (final Exception ex) {
            if (BackupRestoreUtil.getInstance().useNativeForExecution() || !ex.getMessage().contains("Problem while executing command")) {
                throw ex;
            }
            this.backupWithDLL(backupObj);
        }
    }
    
    static {
        ScheduledDBBackupTask.logger = Logger.getLogger("ScheduleDBBackup");
    }
}
