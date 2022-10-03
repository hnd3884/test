package com.adventnet.taskengine.backup;

import java.util.Hashtable;
import com.adventnet.taskengine.Scheduler;
import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.persistence.Persistence;
import java.sql.Connection;
import com.adventnet.ds.query.DataSet;
import java.sql.SQLException;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.mfw.ConsoleOut;
import com.adventnet.db.adapter.BackupRestoreException;
import java.util.List;
import com.adventnet.ds.query.DerivedColumn;
import java.util.ArrayList;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.db.adapter.BackupStatus;
import com.adventnet.db.adapter.BackupResult;
import com.adventnet.mfw.message.MessageFilter;
import javax.transaction.SystemException;
import com.zoho.framework.utils.FileUtils;
import com.adventnet.taskengine.TaskExecutionException;
import com.adventnet.mfw.message.Messenger;
import com.adventnet.taskengine.TaskContext;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import java.net.InetAddress;
import com.adventnet.ds.query.Column;
import com.adventnet.db.adapter.BackupRestoreConfigurations;
import java.io.File;
import java.util.logging.Level;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.Row;
import com.adventnet.db.adapter.BackupDBParams;
import com.adventnet.db.api.RelationalAPI;
import java.util.Properties;
import com.adventnet.persistence.DataObject;
import com.adventnet.db.adapter.BackupHandler;
import java.util.logging.Logger;
import com.adventnet.mfw.message.MessageListener;
import com.adventnet.taskengine.Task;

public class DbBackupTask implements Task, MessageListener
{
    private static final Logger LOGGER;
    private static BackupHandler backupHandler;
    private DataObject backupDetailsDO;
    private static Properties defaultProps;
    private static Properties backupProps;
    private static String backupNameSuffix;
    private static boolean isBackupScheduleEnabled;
    private static int backupFailurePolicy;
    private static int backupFailed;
    
    public DbBackupTask() {
        this.backupDetailsDO = null;
        DbBackupTask.backupHandler = RelationalAPI.getInstance().getDBAdapter().getBackupHandler();
    }
    
    private Row init(final Properties backupProps, final BackupDBParams params) throws Exception {
        final SelectQuery sq = this.getBackupDetailsSQ();
        this.backupDetailsDO = DataAccess.get(sq);
        DbBackupTask.LOGGER.log(Level.FINE, "backupDetailsDO :: {0}", this.backupDetailsDO);
        params.backupStartTime = System.currentTimeMillis();
        params.backupType = this.getCurrentBackupType(this.backupDetailsDO, backupProps, params);
        params.backupFolder = new File(backupProps.getProperty("backup.directory", "Backup"));
        params.backupLabelWaitDuration = Integer.parseInt(backupProps.getProperty("backuplabel.waitduration", "20"));
        params.remoteBackupDir = backupProps.getProperty("mssql.backup.directory");
        params.backupMode = BackupRestoreConfigurations.BACKUP_MODE.ONLINE_BACKUP;
        final String backupContentType = (backupProps.getProperty("backup.content.type") != null) ? backupProps.getProperty("backup.content.type") : "binary";
        if (backupContentType.equals("dump")) {
            params.backupContentType = BackupRestoreConfigurations.BACKUP_CONTENT_TYPE.DUMP;
        }
        else {
            params.backupContentType = BackupRestoreConfigurations.BACKUP_CONTENT_TYPE.BINARY;
        }
        final Criteria c = new Criteria(new Column("ServerStatus", "SERVERNAME"), (Object)InetAddress.getLocalHost().getHostName(), 0);
        final DataObject serverStatusDO = DataAccess.get("ServerStatus", c);
        final Row serverStatusRow = serverStatusDO.getRow("ServerStatus");
        final Integer updatePPMStatus = (Integer)serverStatusRow.get(3);
        if (updatePPMStatus != null) {
            final int ppmStatus = (int)serverStatusRow.get(3);
            if (ppmStatus == 6) {
                throw new RuntimeException("PPM not properly installed/reverted, hence backup is not possible.");
            }
        }
        params.zipFileName = backupProps.getProperty("zipFileName", this.getBackupZipFileName(params.backupType));
        final Row newBackupDetailsRow = new Row("BackupDetails");
        newBackupDetailsRow.set("BACKUP_TYPE", (Object)params.backupType.getValue());
        newBackupDetailsRow.set("BACKUP_STARTTIME", (Object)params.backupStartTime);
        newBackupDetailsRow.set("BACKUP_STATUS", (Object)BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_PROCESS_STARTED.getValue());
        newBackupDetailsRow.set("BACKUP_ZIPNAME", (Object)params.zipFileName);
        newBackupDetailsRow.set("BACKUP_ZIP_CLEANED", (Object)false);
        this.backupDetailsDO.addRow(newBackupDetailsRow);
        DataAccess.update(this.backupDetailsDO);
        params.backupID = (long)newBackupDetailsRow.get("BACKUP_ID");
        return newBackupDetailsRow;
    }
    
    private BackupRestoreConfigurations.BACKUP_TYPE getCurrentBackupType(final DataObject backupDetailsDO, final Properties backupProps, final BackupDBParams params) throws DataAccessException {
        final int fullBackupInterval = Integer.parseInt(backupProps.getProperty("fullbackup.interval", "-1"));
        if (fullBackupInterval <= 0) {
            params.incrementalBackupEnabled = false;
            return BackupRestoreConfigurations.BACKUP_TYPE.FULL_BACKUP;
        }
        if (Boolean.parseBoolean(backupProps.getProperty("first.backup.after.ppm", "false"))) {
            return BackupRestoreConfigurations.BACKUP_TYPE.FULL_BACKUP;
        }
        final Row lastBackupRow = this.getLastBackupRow();
        if (lastBackupRow == null || lastBackupRow.get(5).toString().equals(BackupRestoreConfigurations.BACKUP_STATUS.RESTORED_BACKUP.toString())) {
            return BackupRestoreConfigurations.BACKUP_TYPE.FULL_BACKUP;
        }
        final Row lastFullBackupRow = this.getLastFullBackupRow(backupDetailsDO);
        if (lastFullBackupRow == null) {
            DbBackupTask.LOGGER.log(Level.INFO, "Immediate Full Backup not present. So taking Full Backup");
            return BackupRestoreConfigurations.BACKUP_TYPE.FULL_BACKUP;
        }
        final long lastFullBackupID = (long)lastFullBackupRow.get("BACKUP_ID");
        params.fullbackup_zipname = (String)lastFullBackupRow.get("BACKUP_ZIPNAME");
        params.previous_incr_backup_zipnames = "";
        final String fullBackupIntervalType = backupProps.getProperty("fullbackup.intervaltype", "days");
        final String backupFolder = backupProps.getProperty("backup.directory", ".." + File.separator + "Backup");
        final File fullBackupFile = new File(backupFolder + File.separator + params.fullbackup_zipname);
        if (!fullBackupFile.exists()) {
            return BackupRestoreConfigurations.BACKUP_TYPE.FULL_BACKUP;
        }
        if (!fullBackupIntervalType.equals("days")) {
            int incrBackupsCompleted = 0;
            final Iterator iterator = backupDetailsDO.getRows("BackupDetails");
            if (!iterator.hasNext()) {
                params.lastIncrementalBackupEndTime = (long)lastFullBackupRow.get("BACKUP_ENDTIME");
                final Object mt = lastFullBackupRow.get("LAST_DATAFILE_MODIFIEDTIME");
                params.prevBackupLastDataFileModifiedTime = (long)((mt == null) ? -1L : mt);
            }
            else {
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final long backupID = (long)row.get("BACKUP_ID");
                    if (backupID > lastFullBackupID) {
                        ++incrBackupsCompleted;
                        params.lastIncrementalBackupEndTime = (long)row.get("BACKUP_ENDTIME");
                        final Object mt2 = row.get("LAST_DATAFILE_MODIFIEDTIME");
                        params.prevBackupLastDataFileModifiedTime = (long)((mt2 == null) ? -1L : mt2);
                        params.previous_incr_backup_zipnames += (params.previous_incr_backup_zipnames.equals("") ? "" : ",");
                        params.previous_incr_backup_zipnames += (String)row.get("BACKUP_ZIPNAME");
                    }
                }
            }
            return (incrBackupsCompleted < fullBackupInterval) ? BackupRestoreConfigurations.BACKUP_TYPE.INCREMENTAL_BACKUP : BackupRestoreConfigurations.BACKUP_TYPE.FULL_BACKUP;
        }
        params.lastFullBackupStartTime = (long)lastFullBackupRow.get("BACKUP_STARTTIME");
        Object mt3 = lastFullBackupRow.get("LAST_DATAFILE_MODIFIEDTIME");
        params.prevBackupLastDataFileModifiedTime = (long)((mt3 == null) ? -1L : mt3);
        if (params.lastFullBackupStartTime + 86400000 * fullBackupInterval <= params.backupStartTime) {
            return BackupRestoreConfigurations.BACKUP_TYPE.FULL_BACKUP;
        }
        final Row lastIncrementalBackupRow = this.getPrevSuccessfulBackupRow(backupDetailsDO);
        DbBackupTask.LOGGER.log(Level.FINE, "lastIncrementalBackupRow :: [{0}]", lastIncrementalBackupRow);
        if (lastIncrementalBackupRow != null) {
            params.lastIncrementalBackupEndTime = (long)lastIncrementalBackupRow.get("BACKUP_ENDTIME");
            mt3 = lastIncrementalBackupRow.get("LAST_DATAFILE_MODIFIEDTIME");
            params.prevBackupLastDataFileModifiedTime = (long)((mt3 == null) ? -1L : mt3);
        }
        final Iterator iterator2 = backupDetailsDO.getRows("BackupDetails");
        while (iterator2.hasNext()) {
            final Row row2 = iterator2.next();
            final long backupID2 = (long)row2.get("BACKUP_ID");
            if (backupID2 > lastFullBackupID) {
                params.lastIncrementalBackupEndTime = (long)row2.get("BACKUP_ENDTIME");
                mt3 = row2.get("LAST_DATAFILE_MODIFIEDTIME");
                params.prevBackupLastDataFileModifiedTime = (long)((mt3 == null) ? -1L : mt3);
                params.previous_incr_backup_zipnames += (params.previous_incr_backup_zipnames.equals("") ? "" : ",");
                params.previous_incr_backup_zipnames += (String)row2.get("BACKUP_ZIPNAME");
            }
        }
        return BackupRestoreConfigurations.BACKUP_TYPE.INCREMENTAL_BACKUP;
    }
    
    private Row getPrevSuccessfulBackupRow(final DataObject backupDetailsDO) throws DataAccessException {
        Row lastIncrBackupRow = null;
        final Iterator iterator = backupDetailsDO.getRows("BackupDetails");
        while (iterator.hasNext()) {
            lastIncrBackupRow = iterator.next();
        }
        return lastIncrBackupRow;
    }
    
    private Row getLastBackupRow() throws DataAccessException {
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("BackupDetails"));
        sq.addSelectColumn(Column.getColumn("BackupDetails", "*"));
        sq.setRange(new Range(1, 1));
        sq.addSortColumn(new SortColumn(Column.getColumn("BackupDetails", "BACKUP_ID"), false));
        final int[] status = { BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_SUCCESSFULLY_COMPLETED.getValue(), BackupRestoreConfigurations.BACKUP_STATUS.RESTORED_BACKUP.getValue() };
        final Criteria criteria = new Criteria(Column.getColumn("BackupDetails", "BACKUP_STATUS"), (Object)status, 8);
        sq.setCriteria(criteria);
        final DataObject lastBackupDO = DataAccess.get(sq);
        DbBackupTask.LOGGER.log(Level.INFO, "lastBackupRow :: {0}", lastBackupDO.getRow("BackupDetails"));
        return lastBackupDO.getRow("BackupDetails");
    }
    
    private Row getLastFullBackupRow(final DataObject backupDetailsDO) throws DataAccessException {
        Row lastFullBackupRow = null;
        long lastFullBackupStartTime = -1L;
        final Iterator iterator = backupDetailsDO.getRows("BackupDetails");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            if ((int)row.get("BACKUP_TYPE") == BackupRestoreConfigurations.BACKUP_TYPE.FULL_BACKUP.getValue() && (long)row.get("BACKUP_STARTTIME") > lastFullBackupStartTime) {
                lastFullBackupRow = row;
                lastFullBackupStartTime = (long)row.get("BACKUP_STARTTIME");
            }
        }
        return lastFullBackupRow;
    }
    
    protected String getBackupZipFileName(final BackupRestoreConfigurations.BACKUP_TYPE backupType) {
        final Calendar c = Calendar.getInstance();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String retVal = ((backupType == BackupRestoreConfigurations.BACKUP_TYPE.INCREMENTAL_BACKUP) ? "IncrementalBackup_" : "FullBackup_") + sdf.format(c.getTime());
        if (DbBackupTask.backupNameSuffix != null) {
            retVal = retVal + "_" + DbBackupTask.backupNameSuffix;
        }
        retVal += ".ezip";
        DbBackupTask.LOGGER.log(Level.FINE, "Returning getBackupZipFileName :: [{0}]", retVal);
        return retVal;
    }
    
    private SelectQuery getBackupDetailsSQ() {
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("BackupDetails"));
        sq.addSelectColumn(Column.getColumn("BackupDetails", "*"));
        final int[] status = { BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_SUCCESSFULLY_COMPLETED.getValue(), BackupRestoreConfigurations.BACKUP_STATUS.RESTORED_BACKUP.getValue() };
        Criteria criteria = new Criteria(Column.getColumn("BackupDetails", "BACKUP_STATUS"), (Object)status, 8);
        criteria = criteria.and(new Criteria(Column.getColumn("BackupDetails", "BACKUP_ZIP_CLEANED"), (Object)false, 0));
        sq.setCriteria(criteria);
        sq.addSortColumn(new SortColumn(Column.getColumn("BackupDetails", "BACKUP_ID"), true));
        return sq;
    }
    
    @Override
    public void executeTask(final TaskContext taskContext) throws TaskExecutionException {
        DbBackupTask.LOGGER.log(Level.INFO, "Entering into the executeTask");
        Row currentBackupDetailsRow = null;
        try {
            if (DbBackupTask.backupProps == null) {
                taskContext.flushConfigurations();
                DbBackupTask.backupProps = getBackupPropsFromDB(taskContext.getDefaultTaskInputs());
                DbBackupTask.LOGGER.log(Level.INFO, "executeTask :: backupProps :: {0}", DbBackupTask.backupProps);
            }
            final String value = DbBackupTask.backupProps.getProperty("framework.online.backup");
            if (value == null) {
                DbBackupTask.LOGGER.log(Level.INFO, "Disabling the scheduler as the framework.online.backup configuration is not found");
                disableSchedule(taskContext.getTaskID());
                DbBackupTask.backupHandler.cleanBackupConfigFiles();
            }
            else {
                currentBackupDetailsRow = this.doBackup(DbBackupTask.backupProps);
                DbBackupTask.LOGGER.log(Level.INFO, "currentBackupDetailsRow :: {0}", currentBackupDetailsRow);
                if (Boolean.parseBoolean(DbBackupTask.backupProps.getProperty("first.backup.after.ppm", "false"))) {
                    final int status = (int)currentBackupDetailsRow.get("BACKUP_STATUS");
                    if (status == BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_SUCCESSFULLY_COMPLETED.getValue()) {
                        final Criteria c = new Criteria(Column.getColumn("Default_Task_Input", "VARIABLE_NAME"), (Object)"first.backup.after.ppm", 0);
                        DataAccess.delete(c);
                        DbBackupTask.backupProps = null;
                    }
                }
            }
            DbBackupTask.backupFailed = 0;
        }
        catch (final Throwable t) {
            ++DbBackupTask.backupFailed;
            final Properties p = new Properties();
            p.setProperty("backupFailureCount", "" + DbBackupTask.backupFailed);
            p.setProperty("backupFailurePolicy", "" + DbBackupTask.backupFailurePolicy);
            if (DbBackupTask.backupFailurePolicy <= 0) {
                p.setProperty("failureMessage", "Backup has failed. Backup failures will not be handled as backupFailurePolicy is " + DbBackupTask.backupFailurePolicy);
            }
            else {
                p.setProperty("failureMessage", "Backup has failed due to " + t.getMessage());
                try {
                    if (DbBackupTask.backupFailed >= DbBackupTask.backupFailurePolicy) {
                        p.setProperty("failureMessage", "Backup Failed more than the failure policy. Disabling backup scheduler.");
                        DbBackupTask.LOGGER.log(Level.SEVERE, "Failure Message Published :: {0}", p);
                        Messenger.publish("BackupStatusTopic", (Object)p);
                        disableDbBackupTask();
                    }
                    else {
                        DbBackupTask.LOGGER.log(Level.SEVERE, "Failure Message Published :: {0}", p);
                        Messenger.publish("BackupStatusTopic", (Object)p);
                    }
                }
                catch (final Exception e) {
                    throw new TaskExecutionException(e);
                }
            }
            throw new TaskExecutionException(t.getMessage(), t);
        }
        finally {
            if (currentBackupDetailsRow != null && currentBackupDetailsRow.get(2).equals(BackupRestoreConfigurations.BACKUP_TYPE.FULL_BACKUP.getValue())) {
                if (DbBackupTask.backupProps == null) {
                    taskContext.flushConfigurations();
                    try {
                        DbBackupTask.backupProps = getBackupPropsFromDB(getTaskInputDO().getRows("Default_Task_Input"));
                    }
                    catch (final DataAccessException e2) {
                        e2.printStackTrace();
                    }
                }
                doCleanup(DbBackupTask.backupProps);
            }
        }
        DbBackupTask.LOGGER.log(Level.INFO, "Coming out of the DbBackupTask.executeTask");
    }
    
    public static void doCleanup() {
        DataObject taskInputDO = null;
        try {
            taskInputDO = getTaskInputDO();
            if (taskInputDO.isEmpty()) {
                DbBackupTask.LOGGER.log(Level.SEVERE, "Configuration Incomplete. Hence Online Backup is skipped");
                return;
            }
            if (DbBackupTask.backupProps == null) {
                DbBackupTask.backupProps = getBackupPropsFromDB(taskInputDO.getRows("Default_Task_Input"));
            }
            doCleanup(DbBackupTask.backupProps);
        }
        catch (final DataAccessException e) {
            e.printStackTrace();
        }
    }
    
    private static void doCleanup(final Properties props) {
        try {
            DbBackupTask.LOGGER.log(Level.INFO, "Entered doCleanup(props) :: {0}", props);
            final int retainCount = Integer.parseInt(props.getProperty("fullbackup.retaincount"));
            DbBackupTask.LOGGER.log(Level.INFO, "retainCount :: {0}", retainCount);
            if (retainCount == -1) {
                DbBackupTask.LOGGER.log(Level.INFO, "backupzip cleanup ignored since its configured to retain all");
                return;
            }
            Criteria c = new Criteria(Column.getColumn("BackupDetails", "BACKUP_TYPE"), (Object)BackupRestoreConfigurations.BACKUP_TYPE.FULL_BACKUP.getValue(), 0);
            final int[] status = { BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_SUCCESSFULLY_COMPLETED.getValue(), BackupRestoreConfigurations.BACKUP_STATUS.RESTORED_BACKUP.getValue() };
            c = c.and(new Criteria(Column.getColumn("BackupDetails", "BACKUP_STATUS"), (Object)status, 8));
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("BackupDetails"));
            sq.addSelectColumn(Column.getColumn((String)null, "*"));
            sq.setCriteria(c);
            sq.setRange(new Range(0, retainCount));
            sq.addSortColumn(new SortColumn(Column.getColumn("BackupDetails", "BACKUP_ID"), false));
            long firstBackupIDToBeRetained = -1L;
            final DataObject fullBackupDetailsDO = DataAccess.get(sq);
            DbBackupTask.LOGGER.log(Level.FINE, "fullBackupDetailsDO :: {0}", fullBackupDetailsDO);
            Iterator iterator = fullBackupDetailsDO.getRows("BackupDetails");
            Row lastRow = null;
            if (!iterator.hasNext()) {
                DbBackupTask.LOGGER.log(Level.INFO, "No zipFiles present for cleanup, hence returning");
                return;
            }
            int prevFullBackupCount = 0;
            while (iterator.hasNext()) {
                lastRow = iterator.next();
                firstBackupIDToBeRetained = (long)lastRow.get(1);
                if (++prevFullBackupCount == retainCount) {
                    break;
                }
            }
            if (prevFullBackupCount < retainCount) {
                DbBackupTask.LOGGER.log(Level.INFO, "previousFullBackupCount is [{0}] and retainCount is [{1}] hence there is nothing for cleanup", new Object[] { prevFullBackupCount, retainCount });
                return;
            }
            DbBackupTask.LOGGER.log(Level.FINE, "lastRow :: {0}", lastRow);
            c = new Criteria(Column.getColumn("BackupDetails", "BACKUP_ID"), (Object)firstBackupIDToBeRetained, 7);
            c = c.and(new Criteria(Column.getColumn("BackupDetails", "BACKUP_ZIP_CLEANED"), (Object)false, 0));
            final DataObject backupDetailsDO_ToBeCleaned = DataAccess.get("BackupDetails", c);
            DbBackupTask.LOGGER.log(Level.INFO, "backupDetailsDO_ToBeCleaned :: {0}", backupDetailsDO_ToBeCleaned);
            iterator = backupDetailsDO_ToBeCleaned.getRows("BackupDetails");
            if (!iterator.hasNext()) {
                DbBackupTask.LOGGER.log(Level.INFO, "No zipFiles present for cleanup, hence returning");
                return;
            }
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                DbBackupTask.LOGGER.log(Level.INFO, "Going to process the BackupDetails row :: {0}", row);
                final String zipName = (String)row.get(6);
                final File zipFile = new File(props.getProperty("backup.directory") + File.separator + zipName);
                DbBackupTask.LOGGER.log(Level.INFO, "Going to cleanup the zip :: [{0}]", zipFile.getAbsolutePath());
                if (zipFile.exists()) {
                    final boolean deleted = FileUtils.deleteFile(zipFile);
                    DbBackupTask.LOGGER.log(Level.INFO, "ZipFile :: [{0}], Deleted :: [{1}]", new Object[] { zipFile, deleted });
                    row.set("BACKUP_ZIP_CLEANED", (Object)Boolean.TRUE);
                    backupDetailsDO_ToBeCleaned.updateRow(row);
                }
                else {
                    DbBackupTask.LOGGER.log(Level.INFO, "zipFile :: [{0}] not found, probably deleted already - hence ignoring", zipFile);
                }
            }
            DataAccess.update(backupDetailsDO_ToBeCleaned);
        }
        catch (final Exception e) {
            DbBackupTask.LOGGER.log(Level.INFO, "Exception occurred while doing cleanup :: {0}", e);
            e.printStackTrace();
        }
        finally {
            DbBackupTask.LOGGER.log(Level.INFO, "Exiting doCleanup(props) :: {0}", props);
        }
    }
    
    public Row doBackup(final Properties props) throws TaskExecutionException {
        DbBackupTask.LOGGER.log(Level.INFO, "Entering into doBackup(backupProps) :: {0}", props);
        try {
            if (DataAccess.getTransactionManager().getTransaction() != null) {
                throw new TaskExecutionException("Online DB Backup should not be invoked within a transaction context");
            }
        }
        catch (final SystemException se) {
            throw new TaskExecutionException((Throwable)se);
        }
        final BackupDBParams params = new BackupDBParams();
        Row currentBackupDetailsRow = null;
        Row serverStatusRow = null;
        try {
            final DataObject serverStatusDO = DataAccess.get("ServerStatus", (Criteria)null);
            serverStatusRow = serverStatusDO.getRow("ServerStatus");
        }
        catch (final Exception e) {
            throw new TaskExecutionException("Exception occurred while fetching the ServerStatus rows from DB");
        }
        try {
            currentBackupDetailsRow = this.init(props, params);
            Messenger.subscribe("BackupStatusTopic", (MessageListener)this, true, (MessageFilter)null);
            final BackupResult backupResult = DbBackupTask.backupHandler.doBackup(params);
            DbBackupTask.backupHandler.doCleanup(backupResult.getFilesToBeCleaned());
            currentBackupDetailsRow.set("DATABASE_SIZE", (Object)params.databaseSize);
            currentBackupDetailsRow.set(4, (Object)params.backupEndTime);
            currentBackupDetailsRow.set(5, (Object)BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_SUCCESSFULLY_COMPLETED.getValue());
            currentBackupDetailsRow.set("FIRST_BACKUP_AFTER_PPM", (Object)Boolean.parseBoolean(props.getProperty("first.backup.after.ppm", "false")));
            currentBackupDetailsRow.set("BACKUP_ZIPSIZE", (Object)backupResult.getBackupSize());
            serverStatusRow.set(3, (Object)8);
            this.backupDetailsDO.updateBlindly(serverStatusRow);
            return currentBackupDetailsRow;
        }
        catch (final Exception e) {
            e.printStackTrace();
            if (currentBackupDetailsRow != null) {
                currentBackupDetailsRow.set(4, (Object)params.backupEndTime);
                currentBackupDetailsRow.set(5, (Object)BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_PROCESS_FAILED.getValue());
            }
            throw new TaskExecutionException(e);
        }
        finally {
            try {
                if (currentBackupDetailsRow != null) {
                    this.backupDetailsDO.updateRow(currentBackupDetailsRow);
                    DataAccess.update(this.backupDetailsDO);
                }
                Messenger.unsubscribe("BackupStatusTopic", (MessageListener)this);
            }
            catch (final DataAccessException dae) {
                DbBackupTask.LOGGER.log(Level.INFO, "backupDetailsDO :: {0}", this.backupDetailsDO);
                throw new TaskExecutionException((Throwable)dae);
            }
            catch (final Exception e2) {
                DbBackupTask.LOGGER.log(Level.WARNING, "Exception occurred while unsubscribing from the Messenger");
                e2.printStackTrace();
            }
        }
    }
    
    @Override
    public void stopTask() throws TaskExecutionException {
    }
    
    private UpdateQuery getUpdateQuery(final BackupStatus backupStatus) {
        final UpdateQuery uq = (UpdateQuery)new UpdateQueryImpl("BackupDetails");
        uq.setUpdateColumn("BACKUP_STATUS", (Object)Integer.parseInt(backupStatus.getStatus().toString()));
        if (backupStatus.getBackupEndTime() > backupStatus.getBackupStartTime()) {
            uq.setUpdateColumn("BACKUP_ENDTIME", (Object)backupStatus.getBackupEndTime());
        }
        if (backupStatus.getDataFileCount() > 0) {
            uq.setUpdateColumn("DATAFILE_COUNT", (Object)backupStatus.getDataFileCount());
        }
        if (backupStatus.getLastDataFileName() != null) {
            uq.setUpdateColumn("LAST_DATAFILE_NAME", (Object)backupStatus.getLastDataFileName());
        }
        if (backupStatus.getLastDataFileModifiedTime() > 0L) {
            uq.setUpdateColumn("LAST_DATAFILE_MODIFIEDTIME", (Object)backupStatus.getLastDataFileModifiedTime());
        }
        uq.setCriteria(new Criteria(Column.getColumn("BackupDetails", "BACKUP_ID"), (Object)backupStatus.getBackupID(), 0));
        return uq;
    }
    
    private static DataObject getTaskInputDO() throws DataAccessException {
        final List<String> list = new ArrayList<String>();
        list.add("TaskEngine_Task");
        list.add("Scheduled_Task");
        list.add("Task_Input");
        list.add("Default_Task_Input");
        final SelectQuery sqWhere = (SelectQuery)new SelectQueryImpl(Table.getTable("Default_Task_Input"));
        sqWhere.addSelectColumn(Column.getColumn("Default_Task_Input", "INSTANCE_ID"));
        sqWhere.setCriteria(new Criteria(Column.getColumn("Default_Task_Input", "VARIABLE_NAME"), (Object)"framework.online.backup", 0));
        final Criteria c = new Criteria(new Column("Task_Input", "INSTANCE_ID"), (Object)new DerivedColumn("INSTANCE_ID", sqWhere), 0);
        final DataObject taskInputDO = DataAccess.get((List)list, c);
        DbBackupTask.LOGGER.log(Level.FINE, "taskInputDO :: {0}", taskInputDO);
        if (taskInputDO.size("Task_Input") > 1) {
            throw new IllegalStateException("There cannot be more than 1 schedule for DbBackupTask :: " + taskInputDO);
        }
        return taskInputDO;
    }
    
    public void onMessage(final Object msg) {
        if (msg instanceof BackupStatus) {
            BackupStatus backupStatus = null;
            backupStatus = (BackupStatus)msg;
            final UpdateQuery uq = this.getUpdateQuery(backupStatus);
            try {
                this.getPersistence().update(uq);
                DbBackupTask.LOGGER.log(Level.INFO, "BackupStatus updated in DB :: {0}", backupStatus);
            }
            catch (final Exception e) {
                e.printStackTrace();
                DbBackupTask.LOGGER.log(Level.INFO, "Exception occurred while updating the status :: [{0}] in the BackupDetails table", backupStatus);
            }
        }
        else if (msg instanceof Properties) {
            DataSet ds = null;
            Connection conn = null;
            try {
                final DataObject taskInputDO = getTaskInputDO();
                if (taskInputDO.isEmpty()) {
                    DbBackupTask.LOGGER.log(Level.SEVERE, "Configuration Incomplete. Hence Online Backup is skipped");
                    return;
                }
                if (DbBackupTask.backupProps == null) {
                    DbBackupTask.backupProps = getBackupPropsFromDB(taskInputDO.getRows("Default_Task_Input"));
                }
                final Row stRow = taskInputDO.getRow("Scheduled_Task");
                final Row tiRow = taskInputDO.getRow("Task_Input");
                DbBackupTask.isBackupScheduleEnabled = ((int)stRow.get(5) == 3 && (int)tiRow.get("ADMIN_STATUS") == 3);
                if (DbBackupTask.isBackupScheduleEnabled) {
                    if (DbBackupTask.backupHandler == null) {
                        throw new BackupRestoreException("BackupHandler not initialized");
                    }
                    DbBackupTask.LOGGER.log(Level.INFO, "serverStartupNotification :: onMessage :: backupProps :: {0}", DbBackupTask.backupProps);
                    if (!validateProps(DbBackupTask.backupProps)) {
                        ConsoleOut.println("Validation for DbBackupTask failed. Refer Logs.");
                        throw new BackupRestoreException("Validation for DbBackupTask failed");
                    }
                    if (!DbBackupTask.backupHandler.isValid(DbBackupTask.backupProps)) {
                        ConsoleOut.println("DB Specific Validation for DbBackupTask failed. Refer Logs.");
                        throw new BackupRestoreException("DB Specific Validation for DbBackupTask failed");
                    }
                    this.updateInconsistentData(DbBackupTask.backupProps.getProperty("backup.directory"));
                    DbBackupTask.backupFailurePolicy = Integer.parseInt(DbBackupTask.backupProps.getProperty("backup.failure.policy", "5"));
                    final SelectQuery backupFailureCountSubQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("BackupDetails"));
                    final Column max = new Column("BackupDetails", "BACKUP_ID");
                    final Column fn = (Column)Column.createFunction("COALESCE", new Object[] { Column.createFunction("MAX", new Object[] { max }), 0 });
                    fn.setDataType("BIGINT");
                    fn.setColumnAlias("BACKUP_ID");
                    backupFailureCountSubQuery.addSelectColumn(fn);
                    backupFailureCountSubQuery.setCriteria(new Criteria(Column.getColumn("BackupDetails", "BACKUP_STATUS"), (Object)new int[] { 12, 16 }, 8));
                    final DerivedTable dt = new DerivedTable("BACKUP_SUCCESS_MAX_ID", (Query)backupFailureCountSubQuery);
                    final SelectQuery backupFailureCount = (SelectQuery)new SelectQueryImpl(Table.getTable("BackupDetails"));
                    backupFailureCount.addSelectColumn(Column.getColumn("BackupDetails", "BACKUP_ID").count());
                    backupFailureCount.addJoin(new Join(Table.getTable("BackupDetails"), (Table)dt, new Criteria(Column.getColumn("BackupDetails", "BACKUP_ID"), (Object)new Column("BACKUP_SUCCESS_MAX_ID", "BACKUP_ID"), 5), 2));
                    conn = RelationalAPI.getInstance().getConnection();
                    ds = RelationalAPI.getInstance().executeQuery((Query)backupFailureCount, conn);
                    ds.next();
                    DbBackupTask.backupFailed = Integer.parseInt(ds.getAsString(1));
                    DbBackupTask.LOGGER.log(Level.INFO, "Previous Backup Failures :: {0}", DbBackupTask.backupFailed);
                }
                else if (DbBackupTask.backupHandler != null) {
                    DbBackupTask.backupHandler.cleanBackupConfigFiles();
                }
            }
            catch (final Exception e) {
                throw new RuntimeException(e);
            }
            finally {
                if (ds != null) {
                    try {
                        ds.close();
                    }
                    catch (final SQLException e2) {
                        e2.printStackTrace();
                    }
                }
                if (conn != null) {
                    try {
                        conn.close();
                    }
                    catch (final SQLException e2) {
                        e2.printStackTrace();
                    }
                }
            }
        }
    }
    
    private static Properties getBackupPropsFromDB(final Iterator iterator) {
        final Properties newProps = new Properties();
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final String key = (String)row.get(2);
            final String value = (String)row.get(4);
            newProps.setProperty(key, value);
        }
        final Iterator defaultPropsIterator = ((Hashtable<Object, V>)DbBackupTask.defaultProps).keySet().iterator();
        while (iterator.hasNext()) {
            final String key = defaultPropsIterator.next();
            if (!newProps.containsKey(key)) {
                newProps.setProperty(key, DbBackupTask.defaultProps.getProperty(key));
            }
        }
        DbBackupTask.backupFailurePolicy = Integer.parseInt(newProps.getProperty("backup.failure.policy", "5"));
        return newProps;
    }
    
    private Persistence getPersistence() throws Exception {
        return (Persistence)BeanUtil.lookup("Persistence");
    }
    
    private void updateInconsistentData(final String backupFolder) {
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("BackupDetails"));
            sq.addSelectColumn(Column.getColumn((String)null, "*"));
            sq.setCriteria(new Criteria(new Column("BackupDetails", "BACKUP_ZIP_CLEANED"), (Object)"false", 0));
            sq.addSortColumn(new SortColumn(Column.getColumn("BackupDetails", "BACKUP_ID"), false));
            final DataObject fullBackupDetailsDO = DataAccess.get(sq);
            final Iterator i = fullBackupDetailsDO.getRows("BackupDetails");
            File f = null;
            int rowsChanged = 0;
            while (i.hasNext()) {
                final Row row = i.next();
                final String zipName = (String)row.get("BACKUP_ZIPNAME");
                f = new File(backupFolder + File.separator + zipName);
                if (!f.exists()) {
                    row.set("BACKUP_ZIP_CLEANED", (Object)Boolean.TRUE);
                    fullBackupDetailsDO.updateRow(row);
                    DbBackupTask.LOGGER.log(Level.INFO, "fullBackupDetailsDO :: {0}", fullBackupDetailsDO);
                    ++rowsChanged;
                }
            }
            if (rowsChanged != 0) {
                DataAccess.update(fullBackupDetailsDO);
            }
        }
        catch (final DataAccessException e) {
            e.printStackTrace();
        }
    }
    
    public static Properties getBackupConfigurations() throws BackupRestoreException {
        if (DbBackupTask.backupProps == null) {
            try {
                DbBackupTask.backupProps = getBackupPropsFromDB(getTaskInputDO().getRows("Default_Task_Input"));
            }
            catch (final Exception e) {
                throw new BackupRestoreException(e.getMessage(), (Throwable)e);
            }
        }
        return (Properties)DbBackupTask.backupProps.clone();
    }
    
    public static void setBackupConfigurations(final Properties newBackupProps) throws BackupRestoreException {
        DataObject backupDO = null;
        Row r = null;
        try {
            backupDO = getTaskInputDO();
            if (DbBackupTask.backupProps == null) {
                DbBackupTask.backupProps = getBackupPropsFromDB(backupDO.getRows("Default_Task_Input"));
            }
        }
        catch (final DataAccessException e) {
            throw new BackupRestoreException(e.getMessage(), (Throwable)e);
        }
        if (backupDO.isEmpty()) {
            DbBackupTask.LOGGER.log(Level.SEVERE, "backupDO :: {0}", backupDO);
            throw new BackupRestoreException("Compulsory Configuration missing");
        }
        if (!validateProps(newBackupProps)) {
            DbBackupTask.LOGGER.log(Level.WARNING, "Configuration not updated. Old configuration persists.");
            throw new BackupRestoreException("Backup Configuration is invalid");
        }
        if (!DbBackupTask.backupHandler.isValid(newBackupProps)) {
            DbBackupTask.LOGGER.log(Level.WARNING, "Configuration not updated. Old configuration persists.");
            throw new BackupRestoreException("Backup Configuration for this database is invalid");
        }
        try {
            if (newBackupProps.getProperty("backup.directory") != null) {
                r = backupDO.getRow("Default_Task_Input", new Criteria(Column.getColumn("Default_Task_Input", "VARIABLE_NAME"), (Object)"backup.directory", 0));
                if (r != null) {
                    r.set("VARIABLE_VALUE", (Object)newBackupProps.getProperty("backup.directory"));
                    backupDO.updateRow(r);
                }
            }
            if (newBackupProps.getProperty("fullbackup.interval") != null) {
                r = backupDO.getRow("Default_Task_Input", new Criteria(Column.getColumn("Default_Task_Input", "VARIABLE_NAME"), (Object)"fullbackup.interval", 0));
                if (r != null) {
                    r.set("VARIABLE_VALUE", (Object)newBackupProps.getProperty("fullbackup.interval"));
                    backupDO.updateRow(r);
                }
            }
            if (newBackupProps.getProperty("fullbackup.intervaltype") != null) {
                r = backupDO.getRow("Default_Task_Input", new Criteria(Column.getColumn("Default_Task_Input", "VARIABLE_NAME"), (Object)"fullbackup.intervaltype", 0));
                if (r != null) {
                    r.set("VARIABLE_VALUE", (Object)newBackupProps.getProperty("fullbackup.intervaltype"));
                    backupDO.updateRow(r);
                }
            }
            if (newBackupProps.getProperty("fullbackup.retaincount") != null) {
                r = backupDO.getRow("Default_Task_Input", new Criteria(Column.getColumn("Default_Task_Input", "VARIABLE_NAME"), (Object)"fullbackup.retaincount", 0));
                if (r != null) {
                    r.set("VARIABLE_VALUE", (Object)newBackupProps.getProperty("fullbackup.retaincount"));
                    backupDO.updateRow(r);
                }
            }
            if (newBackupProps.getProperty("backup.content.type") != null) {
                r = backupDO.getRow("Default_Task_Input", new Criteria(Column.getColumn("Default_Task_Input", "VARIABLE_NAME"), (Object)"backup.content.type", 0));
                if (r != null) {
                    r.set("VARIABLE_VALUE", (Object)newBackupProps.getProperty("backup.content.type"));
                    backupDO.updateRow(r);
                }
            }
            if (newBackupProps.getProperty("backuplabel.waitduration") != null) {
                r = backupDO.getRow("Default_Task_Input", new Criteria(Column.getColumn("Default_Task_Input", "VARIABLE_NAME"), (Object)"backuplabel.waitduration", 0));
                if (r != null) {
                    r.set("VARIABLE_VALUE", (Object)newBackupProps.getProperty("backuplabel.waitduration"));
                    backupDO.updateRow(r);
                }
            }
            if (newBackupProps.getProperty("mssql.backup.directory") != null) {
                if (newBackupProps.getProperty("mssql.backup.directory").isEmpty()) {
                    r = backupDO.getRow("Default_Task_Input", new Criteria(Column.getColumn("Default_Task_Input", "VARIABLE_NAME"), (Object)"mssql.backup.directory", 0));
                    if (r != null) {
                        backupDO.deleteRow(r);
                    }
                }
                else {
                    r = backupDO.getRow("Default_Task_Input", new Criteria(Column.getColumn("Default_Task_Input", "VARIABLE_NAME"), (Object)"mssql.backup.directory", 0));
                    if (r != null) {
                        r.set("VARIABLE_VALUE", (Object)newBackupProps.getProperty("mssql.backup.directory"));
                        backupDO.updateRow(r);
                    }
                    else {
                        r = backupDO.getRow("Default_Task_Input", new Criteria(Column.getColumn("Default_Task_Input", "VARIABLE_NAME"), (Object)"framework.online.backup", 0));
                        final Row newRow = new Row("Default_Task_Input");
                        newRow.set("VARIABLE_NAME", (Object)"mssql.backup.directory");
                        newRow.set("VARIABLE_VALUE", (Object)newBackupProps.getProperty("mssql.backup.directory"));
                        newRow.set("INSTANCE_ID", r.get("INSTANCE_ID"));
                        backupDO.addRow(newRow);
                    }
                }
            }
            if (newBackupProps.getProperty("backup.failure.policy") != null) {
                r = backupDO.getRow("Default_Task_Input", new Criteria(Column.getColumn("Default_Task_Input", "VARIABLE_NAME"), (Object)"backup.failure.policy", 0));
                if (r != null) {
                    r.set("VARIABLE_VALUE", (Object)newBackupProps.getProperty("backup.failure.policy"));
                    backupDO.updateRow(r);
                }
                else {
                    r = backupDO.getRow("Default_Task_Input", new Criteria(Column.getColumn("Default_Task_Input", "VARIABLE_NAME"), (Object)"framework.online.backup", 0));
                    final Row newRow = new Row("Default_Task_Input");
                    newRow.set("VARIABLE_NAME", (Object)"backup.failure.policy");
                    newRow.set("VARIABLE_VALUE", (Object)newBackupProps.getProperty("backup.failure.policy"));
                    newRow.set("INSTANCE_ID", r.get("INSTANCE_ID"));
                    backupDO.addRow(newRow);
                }
            }
            DbBackupTask.LOGGER.log(Level.INFO, "newBackupPropsDO :: {0}", backupDO);
            DataAccess.update(backupDO);
        }
        catch (final Exception e2) {
            throw new BackupRestoreException(e2.getMessage(), (Throwable)e2);
        }
        DbBackupTask.backupProps = null;
    }
    
    static boolean validateProps(final Properties newBackupProps) {
        boolean isValid = true;
        if (newBackupProps.getProperty("fullbackup.interval") != null) {
            try {
                if (Integer.parseInt(newBackupProps.getProperty("fullbackup.interval")) > 0) {
                    if (DbBackupTask.backupHandler.isIncrementalBackupValid()) {
                        DbBackupTask.backupHandler.enableIncrementalBackup();
                    }
                    else {
                        DbBackupTask.LOGGER.log(Level.SEVERE, "Backup Configuration mismatch :: fullbackup.interval is invalid for installed db. Specify 0 or -1");
                        isValid = false;
                    }
                }
                else {
                    DbBackupTask.backupHandler.disableIncrementalBackup();
                }
            }
            catch (final BackupRestoreException e) {
                e.printStackTrace();
                isValid = false;
            }
        }
        else {
            newBackupProps.setProperty("fullbackup.interval", DbBackupTask.backupProps.getProperty("fullbackup.interval"));
        }
        if (newBackupProps.getProperty("fullbackup.intervaltype") != null) {
            final String fullBackupIntervalType = newBackupProps.getProperty("fullbackup.intervaltype");
            if (!fullBackupIntervalType.equals("backups") && !fullBackupIntervalType.equals("days")) {
                DbBackupTask.LOGGER.log(Level.SEVERE, "Backup Configuration :: fullbackup.intervaltype is invalid. Specify backups or days");
                isValid = false;
            }
        }
        else {
            newBackupProps.setProperty("fullbackup.intervaltype", DbBackupTask.backupProps.getProperty("fullbackup.intervaltype"));
        }
        if (newBackupProps.getProperty("fullbackup.retaincount") != null) {
            final int fullBackupRetainCount = Integer.parseInt(newBackupProps.getProperty("fullbackup.retaincount"));
            if (fullBackupRetainCount <= 0) {
                DbBackupTask.LOGGER.log(Level.FINEST, "Backup Configuration :: fullbackup.retaincount disabled");
            }
            else {
                DbBackupTask.LOGGER.log(Level.FINEST, "Backup Configuration :: fullbackup.retaincount is {0}", fullBackupRetainCount);
            }
        }
        else {
            newBackupProps.setProperty("fullbackup.retaincount", DbBackupTask.backupProps.getProperty("fullbackup.retaincount"));
        }
        if (newBackupProps.getProperty("backup.content.type") != null) {
            final String backupContentType = newBackupProps.getProperty("backup.content.type");
            if (!backupContentType.equals("dump") && !backupContentType.equals("binary")) {
                DbBackupTask.LOGGER.log(Level.SEVERE, "Backup Configuration :: backup.content.type is invalid. Specify dump or binary");
                isValid = false;
            }
            if (backupContentType.equals("dump")) {
                int fullBackupInterval = 0;
                if (newBackupProps.getProperty("fullbackup.interval") != null) {
                    fullBackupInterval = Integer.parseInt(newBackupProps.getProperty("fullbackup.interval"));
                }
                else {
                    fullBackupInterval = Integer.parseInt(DbBackupTask.backupProps.getProperty("fullbackup.interval"));
                }
                if (fullBackupInterval > 0) {
                    DbBackupTask.LOGGER.log(Level.SEVERE, "Backup Configuration mismatch :: fullbackup.interval is invalid for dump backup. Specify 0 or -1");
                    isValid = false;
                }
            }
        }
        else {
            newBackupProps.setProperty("backup.content.type", DbBackupTask.backupProps.getProperty("backup.content.type"));
        }
        if (newBackupProps.getProperty("backuplabel.waitduration") != null) {
            final int backupLabelWaitDuration = Integer.parseInt(newBackupProps.getProperty("backuplabel.waitduration"));
            if (backupLabelWaitDuration < 20) {
                DbBackupTask.LOGGER.log(Level.SEVERE, "Backup Configuration :: backuplabel.waitduration is invalid. Specify 20 and above");
                isValid = false;
            }
        }
        else {
            newBackupProps.setProperty("backuplabel.waitduration", DbBackupTask.backupProps.getProperty("backuplabel.waitduration"));
        }
        if (newBackupProps.getProperty("backupname.suffix") != null) {
            String tempSuffix = newBackupProps.getProperty("backupname.suffix");
            tempSuffix = tempSuffix.trim();
            if (!tempSuffix.equals("") && !tempSuffix.contains(" ")) {
                DbBackupTask.backupNameSuffix = tempSuffix;
            }
            else {
                DbBackupTask.LOGGER.log(Level.SEVERE, "Backup Configuration :: backupname.suffix is not used. Check whether it contains space or is null");
                isValid = false;
            }
        }
        if (newBackupProps.getProperty("backup.failure.policy") != null) {
            final int backupFailurePolicyCount = Integer.parseInt(newBackupProps.getProperty("backup.failure.policy"));
            if (backupFailurePolicyCount <= 0) {
                DbBackupTask.LOGGER.log(Level.INFO, "Backup Configuration :: backup.failure.policy is {0}. Backup Failure cannot be notified.", backupFailurePolicyCount);
            }
            else {
                DbBackupTask.LOGGER.log(Level.FINEST, "Backup Configuration :: backup.failure.policy is {0}", backupFailurePolicyCount);
            }
        }
        else {
            String backupPropFailurePolicy = DbBackupTask.backupProps.getProperty("backup.failure.policy");
            if (backupPropFailurePolicy == null) {
                backupPropFailurePolicy = DbBackupTask.defaultProps.getProperty("backup.failure.policy");
            }
            newBackupProps.setProperty("backup.failure.policy", backupPropFailurePolicy);
        }
        DbBackupTask.LOGGER.log(Level.INFO, "newBackupProps :: {0}", newBackupProps);
        return isValid;
    }
    
    public static boolean isEnabled() {
        return DbBackupTask.isBackupScheduleEnabled;
    }
    
    public static void disableDbBackupTask() throws Exception {
        final DataObject dataObject = getTaskInputDO();
        if (dataObject == null) {
            throw new Exception("Task cannot be disabled using this API. framework.online.backup configuration not found");
        }
        final Criteria c = new Criteria(Column.getColumn("Default_Task_Input", "VARIABLE_NAME"), (Object)"framework.online.backup", 0);
        final Long instanceId = (Long)dataObject.getRow("Default_Task_Input", c).get("INSTANCE_ID");
        disableSchedule(instanceId);
        DbBackupTask.backupHandler.cleanBackupConfigFiles();
        DbBackupTask.isBackupScheduleEnabled = false;
    }
    
    public static void enableDbBackupTask() throws Exception {
        final DataObject dataObject = getTaskInputDO();
        if (dataObject == null) {
            throw new Exception("Task cannot be enabled using this API. framework.online.backup configuration not found");
        }
        final Criteria c = new Criteria(Column.getColumn("Default_Task_Input", "VARIABLE_NAME"), (Object)"framework.online.backup", 0);
        final Long instanceId = (Long)dataObject.getRow("Default_Task_Input", c).get("INSTANCE_ID");
        DbBackupTask.backupProps = getBackupConfigurations();
        if (!validateProps(DbBackupTask.backupProps)) {
            ConsoleOut.println("Validation for DbBackupTask failed. Refer Logs.");
            throw new BackupRestoreException("Validation for DbBackupTask failed");
        }
        if (!DbBackupTask.backupHandler.isValid(DbBackupTask.backupProps)) {
            ConsoleOut.println("DB Specific Validation for DbBackupTask failed. Refer Logs.");
            throw new BackupRestoreException("DB Specific Validation for DbBackupTask failed");
        }
        DbBackupTask.LOGGER.log(Level.INFO, "Enabling the DbBackupTask");
        final Scheduler s = (Scheduler)BeanUtil.lookup("Scheduler");
        s.setTaskInputAdminStatus(instanceId, 3);
        DbBackupTask.LOGGER.log(Level.INFO, "DbBackupTask enabled successfully");
        DbBackupTask.isBackupScheduleEnabled = true;
    }
    
    private static void disableSchedule(final Long instanceId) throws Exception {
        DbBackupTask.LOGGER.log(Level.INFO, "Disabling the DbBackupTask");
        final Scheduler s = (Scheduler)BeanUtil.lookup("Scheduler");
        s.setTaskInputAdminStatus(instanceId, 4);
        DbBackupTask.LOGGER.log(Level.INFO, "DbBackupTask disabled successfully");
    }
    
    static {
        LOGGER = Logger.getLogger(DbBackupTask.class.getName());
        DbBackupTask.defaultProps = new Properties();
        DbBackupTask.backupProps = null;
        DbBackupTask.backupNameSuffix = null;
        DbBackupTask.isBackupScheduleEnabled = false;
        DbBackupTask.backupFailurePolicy = 0;
        DbBackupTask.backupFailed = 0;
        DbBackupTask.defaultProps.setProperty("backup.directory", ".." + File.separator + "Backup");
        DbBackupTask.defaultProps.setProperty("fullbackup.interval", "6");
        DbBackupTask.defaultProps.setProperty("fullbackup.intervaltype", "days");
        DbBackupTask.defaultProps.setProperty("fullbackup.retaincount", "5");
        DbBackupTask.defaultProps.setProperty("backup.content.type", "binary");
        DbBackupTask.defaultProps.setProperty("backuplabel.waitduration", "20");
        DbBackupTask.defaultProps.setProperty("backup.failure.policy", "5");
    }
}
