package com.adventnet.db.adapter.mssql;

import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.ds.query.DataSet;
import java.util.Properties;
import com.zoho.framework.utils.FileUtils;
import java.sql.Connection;
import com.adventnet.db.adapter.BackupStatus;
import java.util.List;
import com.adventnet.db.adapter.BackupRestoreUtil;
import java.util.ArrayList;
import java.io.File;
import java.sql.SQLException;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.db.adapter.BackupRestoreConfigurations;
import java.io.IOException;
import com.adventnet.db.adapter.BackupErrors;
import com.adventnet.db.adapter.BackupResult;
import com.adventnet.db.adapter.BackupDBParams;
import com.adventnet.db.adapter.BackupRestoreException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.mfw.message.MessageListener;
import com.adventnet.db.adapter.AbstractBackupHandler;

public class MssqlBackupHandler extends AbstractBackupHandler implements MessageListener
{
    private static final Logger LOGGER;
    
    public void onMessage(final Object msg) {
        MssqlBackupHandler.LOGGER.log(Level.INFO, "Entered into the onMessage of MssqlBackupHandler");
        MssqlBackupHandler.LOGGER.log(Level.INFO, "No handling required for improper cleanup in MSSQL Backup");
        MssqlBackupHandler.LOGGER.log(Level.INFO, "Returning from the onMessage of MssqlBackupHandler");
    }
    
    @Override
    protected void flushBuffers(final Statement s) throws BackupRestoreException {
    }
    
    @Deprecated
    @Override
    protected BackupResult doTableBackup(final BackupDBParams params) throws BackupRestoreException {
        MssqlBackupHandler.LOGGER.log(Level.SEVERE, "Backup Operation not Supported");
        throw new BackupRestoreException(BackupErrors.UNSUPPORTED_BACKUP_TYPE);
    }
    
    @Override
    protected BackupResult doIncrementalBackup(final BackupDBParams params) throws BackupRestoreException {
        MssqlBackupHandler.LOGGER.log(Level.INFO, "Entered doIncrementalBackup :: [{0}]", params);
        return this.doCommonBackup(params);
    }
    
    @Override
    protected BackupResult doFullBackup(final BackupDBParams params) throws BackupRestoreException {
        MssqlBackupHandler.LOGGER.log(Level.INFO, "Entered doFullBackup :: [{0}]", params);
        return this.doCommonBackup(params);
    }
    
    private BackupResult doCommonBackup(final BackupDBParams params) throws BackupRestoreException {
        BackupResult backupResult = null;
        BackupStatus backupStatus = null;
        try {
            backupResult = new BackupResult(params.zipFileName, params.backupFolder.getCanonicalPath());
        }
        catch (final IOException e) {
            throw new BackupRestoreException(BackupErrors.PROBLEM_FETCHING_BACKUP_DIRECTORY_PATH, e);
        }
        backupResult.setBackupMode(params.backupMode);
        backupResult.setBackupType(params.backupType);
        backupResult.setBackupContentType(params.backupContentType);
        backupStatus = AbstractBackupHandler.getBackupStatus(params);
        try {
            Connection conn = null;
            try {
                conn = this.dataSource.getConnection();
                final String database = ((MssqlDBInitializer)this.dbInitializer).getDBName();
                String backupFile = params.zipFileName.substring(0, params.zipFileName.lastIndexOf(46)) + ".bak";
                if (params.remoteBackupDir != null && !params.remoteBackupDir.trim().isEmpty()) {
                    if (params.remoteBackupDir.endsWith("\\")) {
                        backupFile = params.remoteBackupDir + backupFile;
                    }
                    else {
                        backupFile = params.remoteBackupDir + "\\" + backupFile;
                    }
                }
                final StringBuilder query = new StringBuilder("BACKUP DATABASE " + database + " TO DISK='" + backupFile + "' WITH FORMAT");
                if (this.isCompressionEnabled(conn)) {
                    query.append(", COMPRESSION");
                }
                if (params.backupMode == BackupRestoreConfigurations.BACKUP_MODE.OFFLINE_BACKUP) {
                    query.append(", COPY_ONLY");
                }
                else if (params.backupType == BackupRestoreConfigurations.BACKUP_TYPE.INCREMENTAL_BACKUP) {
                    query.append(", DIFFERENTIAL");
                }
                AbstractBackupHandler.sendBackupStatusNotification(backupStatus, BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_PROCESS_STARTED);
                MssqlBackupHandler.LOGGER.log(Level.FINE, query.toString());
                RelationalAPI.getInstance().execute(conn, query.toString());
                try {
                    ((MssqlDBAdapter)this.dbAdapter).checkSanityOfBackupFile(conn, backupFile);
                }
                catch (final SQLException e2) {
                    MssqlBackupHandler.LOGGER.log(Level.SEVERE, "The backup file which has be obtained has been corrupted for some reason.");
                    throw e2;
                }
            }
            catch (final SQLException e3) {
                MssqlBackupHandler.LOGGER.log(Level.SEVERE, "backup query execution failed");
                if (e3.getMessage().startsWith("Cannot open backup device")) {
                    throw new BackupRestoreException(BackupErrors.REMOTE_BACKUP_PATH_ERROR, e3);
                }
                throw new BackupRestoreException(BackupErrors.PROBLEM_WHILE_EXECUTING_COMMAND, e3);
            }
            finally {
                if (conn != null) {
                    try {
                        conn.close();
                    }
                    catch (final SQLException e4) {
                        e4.printStackTrace();
                    }
                }
            }
            final File tempBackupDir = new File(params.backupFolder + File.separator + params.zipFileName.replace(".ezip", ""));
            MssqlBackupHandler.LOGGER.log(Level.INFO, "Created directory [{0}] :: {1}", new Object[] { tempBackupDir, tempBackupDir.mkdirs() });
            final File indexFile = this.generateIndexFile(params, backupStatus);
            backupResult.setBackupSize(params.backupSize);
            params.backupEndTime = System.currentTimeMillis();
            params.lastFullBackupStartTime = params.backupStartTime;
            AbstractBackupHandler.sendBackupStatusNotification(backupStatus, BackupRestoreConfigurations.BACKUP_STATUS.GOING_TO_CREATE_BACKUPZIP);
            final List<String> zipFileList = new ArrayList<String>();
            zipFileList.add(indexFile.getAbsolutePath());
            if (params.backupType == BackupRestoreConfigurations.BACKUP_TYPE.FULL_BACKUP) {
                zipFileList.add(this.getConfFilesLocation(tempBackupDir));
                try {
                    zipFileList.add(BackupRestoreUtil.getDynamicColumnsInfoFileLocation(tempBackupDir.getCanonicalPath()));
                }
                catch (final Exception e5) {
                    throw new BackupRestoreException(BackupErrors.PROBLEM_GENERATING_DYNAMIC_COLUMN_FILE, e5);
                }
            }
            this.zip(params.backupFolder, params.zipFileName, null, false, false, zipFileList, null, params.archivePassword, params.archiveEncAlgo);
            AbstractBackupHandler.sendBackupStatusNotification(backupStatus, BackupRestoreConfigurations.BACKUP_STATUS.BACKUPZIP_CREATION_COMPLETED);
            zipFileList.add(tempBackupDir.getAbsolutePath());
            params.backupSize += new File(params.backupFolder, params.zipFileName).length();
            backupResult.setFilesToBeCleaned(zipFileList);
            backupResult.setStartTime(params.backupStartTime);
            backupResult.setEndTime(params.backupEndTime);
            backupResult.calculateDuration();
            backupStatus.setBackupEndTime(params.backupEndTime);
            AbstractBackupHandler.sendBackupStatusNotification(backupStatus, BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_SUCCESSFULLY_COMPLETED);
            backupResult.setBackupStatus(backupStatus.getStatus());
            MssqlBackupHandler.LOGGER.log(Level.INFO, "Exiting doFullBackup with backupResult :: {0}", backupResult);
            return backupResult;
        }
        catch (final BackupRestoreException e6) {
            AbstractBackupHandler.sendBackupStatusNotification(backupStatus, BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_PROCESS_FAILED);
            backupResult.setBackupStatus(backupStatus.getStatus());
            throw e6;
        }
    }
    
    @Override
    protected File generateIndexFile(final BackupDBParams params, final BackupStatus status) throws BackupRestoreException {
        final Properties p = this.generateIndexProperties(params, status);
        final String[] details = this.getLastBackupDetails();
        params.backupSize = Long.parseLong(details[0]);
        p.setProperty("location", details[1]);
        p.setProperty("bakSize", params.backupSize + " Bytes");
        if (params.remoteBackupDir != null && !params.remoteBackupDir.trim().isEmpty()) {
            p.setProperty("remoteBackupFolder", params.remoteBackupDir);
        }
        try {
            p.setProperty("dbversion", this.dbInitializer.getVersion());
            p.setProperty("arch", this.dbInitializer.getDBArchitecture() + "");
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        File indexFile;
        if (params.backupType == BackupRestoreConfigurations.BACKUP_TYPE.FULL_BACKUP) {
            indexFile = new File(params.backupFolder + File.separator + params.zipFileName.replace(".ezip", ""), "full_index.props");
        }
        else {
            indexFile = new File(params.backupFolder + File.separator + params.zipFileName.replace(".ezip", ""), "incremental_index.props");
        }
        try {
            FileUtils.writeToFile(indexFile, p, "backup_index_props");
            MssqlBackupHandler.LOGGER.log(Level.INFO, indexFile.getName() + " file generated for the backup");
        }
        catch (final IOException e2) {
            MssqlBackupHandler.LOGGER.log(Level.SEVERE, "Error while writing into property file");
            throw new BackupRestoreException(BackupErrors.PROBLEM_GENERATING_BACKUP_INDEX_PROPS, e2);
        }
        AbstractBackupHandler.sendBackupStatusNotification(status, BackupRestoreConfigurations.BACKUP_STATUS.INDEXFILE_GENERATED_FOR_BACKUP);
        return indexFile;
    }
    
    private boolean isCompressionEnabled(final Connection conn) {
        DataSet ds = null;
        boolean isCompressionEnabled = false;
        try {
            final String query = "SELECT CONVERT(VARCHAR, value) FROM sys.configurations WHERE name = 'backup compression default'";
            MssqlBackupHandler.LOGGER.log(Level.FINE, query);
            ds = RelationalAPI.getInstance().executeQuery(query, conn);
            if (ds.next()) {
                isCompressionEnabled = true;
            }
        }
        catch (final Exception e) {
            MssqlBackupHandler.LOGGER.log(Level.SEVERE, "Exception while obtaining 'backup compression default' from sys.configurations. So assuming compression is not supported. {0}", e);
            if (ds != null) {
                try {
                    ds.close();
                }
                catch (final SQLException e2) {
                    e2.printStackTrace();
                }
            }
        }
        finally {
            if (ds != null) {
                try {
                    ds.close();
                }
                catch (final SQLException e3) {
                    e3.printStackTrace();
                }
            }
        }
        return isCompressionEnabled;
    }
    
    private String[] getLastBackupDetails() {
        final String[] details = { "-1", "" };
        try (final Connection conn = RelationalAPI.getInstance().getConnection()) {
            final boolean isCompressionEnabled = this.isCompressionEnabled(conn);
            String query = "SELECT TOP 1 ";
            if (isCompressionEnabled) {
                query += "msdb.dbo.backupset.compressed_backup_size,";
            }
            else {
                query += "msdb.dbo.backupset.backup_size,";
            }
            query = query + " msdb.dbo.backupmediafamily.physical_device_name FROM msdb.dbo.backupmediafamily INNER JOIN msdb.dbo.backupset ON msdb.dbo.backupmediafamily.media_set_id = msdb.dbo.backupset.media_set_id WHERE msdb.dbo.backupset.database_name  = '" + ((MssqlDBInitializer)this.dbInitializer).getDBName() + "' ORDER BY msdb.dbo.backupset.backup_finish_date DESC";
            MssqlBackupHandler.LOGGER.log(Level.FINE, query);
            try (final DataSet ds = RelationalAPI.getInstance().executeQuery(query, conn)) {
                if (ds.next()) {
                    details[0] = ds.getAsString(1);
                    details[1] = ds.getAsString(2);
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return details;
    }
    
    public boolean isValid(final Properties backupProps) throws BackupRestoreException {
        return backupProps.getProperty("backup.content.type").equalsIgnoreCase("binary");
    }
    
    public void checkForSpace(final BackupDBParams params) throws BackupRestoreException {
        MssqlBackupHandler.LOGGER.log(Level.INFO, "Assuming there is enough space for backup in the machine where MS-SQL Database Service is running");
    }
    
    @Override
    public boolean isIncrementalBackupValid() throws BackupRestoreException {
        Connection conn = null;
        boolean isValid = true;
        try {
            conn = this.dataSource.getConnection();
            isValid = this.isRecoveryModelFull(conn);
            if (!isValid) {
                MssqlBackupHandler.LOGGER.log(Level.WARNING, "The RECOVERY model of the database is not FULL. Incremental backup is possible only when recovery is FULL");
                if (((MssqlDBAdapter)this.dbAdapter).hasPermissionForAlterDatabase(conn)) {
                    MssqlBackupHandler.LOGGER.log(Level.INFO, "While executing enableIncrementalBackup, the recovery mode of the database will be changed to FULL.");
                    isValid = true;
                }
                else {
                    MssqlBackupHandler.LOGGER.log(Level.SEVERE, "There is no permission for executing ALTER DATABASE query. So Recovery Mode switch may fail.");
                }
            }
        }
        catch (final Exception e) {
            throw new BackupRestoreException(BackupErrors.DATABASE_BACKUP_MISCONFIGURED, e);
        }
        finally {
            if (conn != null) {
                try {
                    conn.close();
                }
                catch (final SQLException e2) {
                    e2.printStackTrace();
                }
            }
        }
        return isValid;
    }
    
    @Override
    public void enableIncrementalBackup() throws BackupRestoreException {
        Connection conn = null;
        try {
            conn = this.dataSource.getConnection();
            if (!this.isRecoveryModelFull(conn)) {
                MssqlBackupHandler.LOGGER.log(Level.INFO, "Switching the recovery model of the database to FULL");
                ((MssqlDBAdapter)this.dbAdapter).alterDatabase(conn, "RECOVERY FULL");
            }
        }
        catch (final Exception e) {
            throw new BackupRestoreException(BackupErrors.PROBLEM_ENABLING_BACKUP, e);
        }
        finally {
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
    
    private boolean isRecoveryModelFull(final Connection conn) throws SQLException, QueryConstructionException {
        boolean isFullRecovery = false;
        final String result = ((MssqlDBAdapter)this.dbAdapter).getMssqlDBProperty(conn, "RECOVERY");
        if (result != null && !result.equals("")) {
            isFullRecovery = result.equalsIgnoreCase("FULL");
        }
        else {
            MssqlBackupHandler.LOGGER.log(Level.SEVERE, "The recovery cannot be null or empty. So assuming incremental backup is not possible.");
        }
        return isFullRecovery;
    }
    
    static {
        LOGGER = Logger.getLogger(MssqlBackupHandler.class.getName());
    }
}
