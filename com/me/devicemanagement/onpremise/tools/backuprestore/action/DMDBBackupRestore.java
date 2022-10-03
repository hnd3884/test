package com.me.devicemanagement.onpremise.tools.backuprestore.action;

import java.util.Hashtable;
import org.json.JSONObject;
import com.me.devicemanagement.framework.utils.FrameworkConfigurations;
import org.w3c.dom.NodeList;
import java.io.InputStream;
import com.adventnet.persistence.PersistenceUtil;
import com.zoho.framework.utils.crypto.EnDecryptAES256Impl;
import org.w3c.dom.Element;
import java.io.FileInputStream;
import org.apache.commons.io.FileUtils;
import com.adventnet.db.adapter.RestoreResult;
import com.zoho.mickey.startup.MEServer;
import com.adventnet.db.adapter.mssql.DCMssqlDBAdapter;
import com.zoho.conf.AppResources;
import com.adventnet.mfw.RestoreDB;
import java.io.FilenameFilter;
import java.util.List;
import com.zoho.framework.utils.archive.SevenZipUtils;
import java.util.ArrayList;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.CompressUtil;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.DMBackupPasswordHandler;
import java.util.HashMap;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.DCBackupRestoreException;
import com.adventnet.db.adapter.BackupHandler;
import com.adventnet.db.adapter.BackupRestoreException;
import com.adventnet.db.adapter.mssql.DCMssqlBackupHandler;
import java.net.InetAddress;
import com.adventnet.db.adapter.BackupResult;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.nio.file.CopyOption;
import java.nio.file.Paths;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.FileUtil;
import com.adventnet.db.adapter.BackupDBParams;
import com.adventnet.persistence.PersistenceInitializer;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.db.adapter.BackupRestoreConfigurations;
import com.adventnet.mfw.BackupDB;
import com.me.devicemanagement.onpremise.server.util.DBUtil;
import com.adventnet.taskengine.backup.DbBackupTask;
import java.io.File;
import java.util.logging.Level;
import java.util.Properties;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.BackupRestoreUtil;
import com.me.devicemanagement.onpremise.tools.backuprestore.handler.Informable;
import java.util.logging.Logger;

public class DMDBBackupRestore
{
    private static final Logger LOGGER;
    private Informable informable;
    private String destFolder;
    private String serverHome;
    private static final String PGSQL_FOLDER_NAME = "pgsql";
    private static final String DATA_FOLDER_NAME = "data";
    private final String backupType = "backup.content.type";
    private static final String PGSQL_FOLDER = "pgsql";
    private static final String DATA_NEW_FOLDER;
    private static final String DATA_FOLDER;
    private static final String CURRENT_BACKUP = "CurrentBackup";
    public static String tempFolder;
    public static boolean isBakFormatEnabled;
    private static int backupContentType;
    
    public DMDBBackupRestore(final Informable informable) {
        this(null, informable);
    }
    
    public DMDBBackupRestore(final String dest, final Informable inf) {
        this.informable = null;
        this.serverHome = null;
        this.informable = inf;
        this.destFolder = dest;
        this.serverHome = System.getProperty("server.home");
        BackupRestoreUtil.setSevenZipLoc();
    }
    
    public int backupDB() throws Exception {
        DMDBBackupRestore.backupContentType = this.getBackupContentType();
        if (BackupRestoreUtil.isScheduleDBBackup()) {
            this.doOnlineBackup(BackupRestoreUtil.getInstance().getMickeyBackupConfigProperties());
        }
        else if (DMDBBackupRestore.backupContentType != 3) {
            this.doOfflineBackup(DMDBBackupRestore.backupContentType);
        }
        else {
            this.doCopyBackup();
        }
        return DMDBBackupRestore.backupContentType;
    }
    
    private void doOnlineBackup(final Properties dbBackupProps) throws Exception {
        DMDBBackupRestore.LOGGER.log(Level.INFO, "########## Starting Online backup ##########");
        Row currentBackupDetailsRow = null;
        try {
            final String onlineBackupLoc = this.destFolder + File.separator + this.getDBBackupLoc();
            if (BackupRestoreUtil.getDBType() == 2) {
                final DbBackupTask dbBackupTask = new DbBackupTask();
                dbBackupProps.setProperty("backup.directory", onlineBackupLoc);
                if (DBUtil.isRemoteDB()) {
                    DMDBBackupRestore.LOGGER.log(Level.INFO, "remote database : adding the dump properties");
                    BackupDB.BACKUP_DB_USING_SCRIPTS = true;
                    dbBackupProps.setProperty("backup.content.type", "dump");
                    DMDBBackupRestore.backupContentType = 2;
                }
                DMDBBackupRestore.LOGGER.log(Level.INFO, "backup properties from mickey :: {0}", dbBackupProps);
                DbBackupTask.setBackupConfigurations(dbBackupProps);
                currentBackupDetailsRow = dbBackupTask.doBackup(dbBackupProps);
                DMDBBackupRestore.LOGGER.log(Level.INFO, "currentBackupDetailsRow :: {0}", currentBackupDetailsRow);
                if (Boolean.parseBoolean(dbBackupProps.getProperty("first.backup.after.ppm", "false"))) {
                    final int status = (int)currentBackupDetailsRow.get("BACKUP_STATUS");
                    if (status == BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_SUCCESSFULLY_COMPLETED.getValue()) {
                        final Criteria c = new Criteria(Column.getColumn("Default_Task_Input", "VARIABLE_NAME"), (Object)"first.backup.after.ppm", 0);
                        DataAccess.delete(c);
                    }
                }
            }
            else {
                final String backupStatus = this.doMssqlBackup();
                if (backupStatus.equals(BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_PROCESS_FAILED.toString())) {
                    throw new Exception("MS SQL Backup failed");
                }
            }
        }
        catch (final Exception ex) {
            DMDBBackupRestore.LOGGER.log(Level.SEVERE, "Online backup failed with error : ", ex);
            throw ex;
        }
        finally {
            BackupDB.BACKUP_DB_USING_SCRIPTS = false;
        }
        DMDBBackupRestore.LOGGER.log(Level.INFO, "########## Online backup completed ##########");
    }
    
    private String doMssqlBackup() throws Exception {
        final RelationalAPI relAPI = RelationalAPI.getInstance();
        if (relAPI == null) {
            final String confFileDir = System.getProperty("server.home") + File.separator + "conf";
            PersistenceInitializer.initializeDB(confFileDir);
            PersistenceInitializer.initializeMickey(true);
        }
        final BackupDBParams backupProps = new BackupDBParams();
        BackupResult backupResult = null;
        backupProps.zipFileName = getDBbackupFileName();
        backupProps.incrementalBackupEnabled = false;
        backupProps.backupType = BackupRestoreConfigurations.BACKUP_TYPE.FULL_BACKUP;
        try {
            if (DMDBBackupRestore.isBakFormatEnabled) {
                backupResult = this.doMssqlBakBackup(backupProps);
                if (backupResult.getBackupStatus().equals((Object)BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_PROCESS_FAILED)) {
                    throw new Exception("MS SQL BAK Backup failed");
                }
                System.setProperty("isBakTaken", "true");
            }
            else {
                backupResult = this.doMssqlDumpBackup(backupProps);
            }
        }
        catch (final Exception ex) {
            System.setProperty("isBakTaken", "false");
            DMDBBackupRestore.LOGGER.log(Level.WARNING, "Exception ", ex);
            if (!DMDBBackupRestore.isBakFormatEnabled) {
                DMDBBackupRestore.LOGGER.log(Level.INFO, "Exception ", ex);
                throw new Exception("Exception occurred while taking dump Backup. Status : ", ex);
            }
            DMDBBackupRestore.LOGGER.log(Level.INFO, "*************************************************** ");
            DMDBBackupRestore.LOGGER.log(Level.INFO, "UNABLE TO TAKE BAK FILE BACKUP");
            DMDBBackupRestore.LOGGER.log(Level.INFO, "*************************************************** ");
            backupResult = this.doMssqlDumpBackup(backupProps);
        }
        finally {
            if (DMDBBackupRestore.backupContentType == 1) {
                final String junkFolderLocation = backupProps.backupFolder.getCanonicalPath() + File.separator + backupProps.zipFileName;
                final File junkFileLocation = new File(junkFolderLocation);
                if (junkFileLocation.exists()) {
                    final File cleanupFile = new File(junkFolderLocation.substring(0, junkFolderLocation.lastIndexOf(".")));
                    if (cleanupFile.isDirectory()) {
                        FileUtil.deleteFileOrFolder(cleanupFile);
                        DMDBBackupRestore.LOGGER.log(Level.INFO, "Junk Folder {0} cleanup status : {1}", new Object[] { cleanupFile.getCanonicalPath(), cleanupFile.exists() });
                    }
                }
                FileAccessUtil.moveFolder(backupProps.backupFolder.toPath(), Paths.get(this.destFolder + File.separator + "mssql", new String[0]), new CopyOption[0]);
                FileUtil.deleteFileOrFolder(backupProps.backupFolder);
            }
        }
        return backupResult.getBackupStatus().toString();
    }
    
    private BackupResult doMssqlBakBackup(final BackupDBParams backupProps) throws Exception {
        backupProps.backupContentType = BackupRestoreConfigurations.BACKUP_CONTENT_TYPE.BINARY;
        DMDBBackupRestore.backupContentType = 1;
        backupProps.backupMode = BackupRestoreConfigurations.BACKUP_MODE.OFFLINE_BACKUP;
        DMDBBackupRestore.LOGGER.log(Level.INFO, "*************************************************** ");
        DMDBBackupRestore.LOGGER.log(Level.INFO, "PROCEED BACKUP WITH BAK FILE FORMAT");
        DMDBBackupRestore.LOGGER.log(Level.INFO, "*************************************************** ");
        System.setProperty("isBakTaken", "true");
        final String backupLocation = "\\\\" + InetAddress.getLocalHost().getHostName() + File.separator + BackupRestoreUtil.getInstance().getMickeyBackupConfigProperties().getProperty("default.backup.directory", "ScheduledDBBackup") + File.separator + "mssql";
        backupProps.backupFolder = new File(backupLocation);
        backupProps.remoteBackupDir = backupLocation;
        BackupResult backupResult = null;
        backupResult = RelationalAPI.getInstance().getDBAdapter().getBackupHandler().doBackup(backupProps);
        DMDBBackupRestore.LOGGER.log(Level.INFO, "BACKUP RESULT : " + backupResult);
        return backupResult;
    }
    
    private BackupResult doMssqlDumpBackup(final BackupDBParams backupProps) throws BackupRestoreException {
        DMDBBackupRestore.LOGGER.log(Level.INFO, "*************************************************** ");
        DMDBBackupRestore.LOGGER.log(Level.INFO, "PROCEED DUMP BACKUP  ");
        DMDBBackupRestore.LOGGER.log(Level.INFO, "*************************************************** ");
        backupProps.backupContentType = BackupRestoreConfigurations.BACKUP_CONTENT_TYPE.DUMP;
        DMDBBackupRestore.backupContentType = 2;
        final String backupLocation = this.destFolder + File.separator + this.getDBBackupLoc();
        backupProps.backupFolder = new File(backupLocation);
        final BackupResult backupResult = ((DCMssqlBackupHandler)RelationalAPI.getInstance().getDBAdapter().getBackupHandler()).doTableBackup(backupProps);
        DMDBBackupRestore.LOGGER.log(Level.INFO, "BACKUP RESULT : " + backupResult);
        return backupResult;
    }
    
    private void doCopyBackup() {
        DMDBBackupRestore.LOGGER.log(Level.INFO, "########## Starting Copy backup ##########");
        includeDataFolder(DMBackupAction.backupList);
        DMDBBackupRestore.LOGGER.log(Level.INFO, "########## Copy backup completed. data folder entry is added in compression list ##########");
    }
    
    private void doOfflineBackup(final int backupContentType) throws Exception {
        DMDBBackupRestore.LOGGER.log(Level.INFO, "########## Starting DB backup from scripts ##########");
        try {
            final String backupDir = this.destFolder + File.separator + this.getDBBackupLoc();
            if (BackupRestoreUtil.getDBType() == 2) {
                BackupResult backupResult = null;
                BackupDB.BACKUP_DB_USING_SCRIPTS = true;
                if (RelationalAPI.getInstance() == null) {
                    final String confFileDir = System.getProperty("server.home") + File.separator + "conf";
                    PersistenceInitializer.initializeDB(confFileDir);
                    PersistenceInitializer.initializeMickey(true);
                }
                BackupDB.SHOW_STATUS = true;
                final BackupDBParams params = new BackupDBParams();
                params.incrementalBackupEnabled = false;
                params.backupStartTime = System.currentTimeMillis();
                params.backupContentType = BackupRestoreConfigurations.BACKUP_CONTENT_TYPE.values()[backupContentType - 1];
                params.backupType = BackupRestoreConfigurations.BACKUP_TYPE.FULL_BACKUP;
                params.backupFolder = new File(backupDir);
                params.backupLabelWaitDuration = 20;
                params.backupMode = BackupRestoreConfigurations.BACKUP_MODE.OFFLINE_BACKUP;
                params.zipFileName = getDBbackupFileName();
                final BackupHandler backupHandler = RelationalAPI.getInstance().getDBAdapter().getBackupHandler();
                DMDBBackupRestore.LOGGER.log(Level.INFO, "backupParams :: {0}", params);
                final Properties backupProps = new Properties();
                if (!DBUtil.isRemoteDB()) {
                    backupProps.setProperty("backup.content.type", params.backupContentType.name().toLowerCase());
                }
                else {
                    backupProps.setProperty("backup.content.type", "dump");
                }
                if (!backupHandler.isValid(backupProps)) {
                    throw new Exception("Backup failed");
                }
                backupResult = backupHandler.doBackup(params);
                backupHandler.doCleanup(backupResult.getFilesToBeCleaned());
                if (backupResult.getBackupStatus() != BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_SUCCESSFULLY_COMPLETED) {
                    throw new Exception("Backup failed");
                }
            }
            else {
                final String backupStatus = this.doMssqlBackup();
                if (backupStatus.equals(BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_PROCESS_FAILED.toString())) {
                    throw new Exception("MS SQL DUMP Backup failed");
                }
            }
        }
        finally {
            BackupDB.SHOW_STATUS = false;
            BackupDB.BACKUP_DB_USING_SCRIPTS = false;
            try {
                PersistenceInitializer.stopDB();
            }
            catch (final Exception e) {
                DMDBBackupRestore.LOGGER.log(Level.INFO, "Problem while Stopping DB.", e);
            }
        }
        DMDBBackupRestore.LOGGER.log(Level.INFO, "########## DB backup from scripts completed ##########");
    }
    
    private String getDBBackupLoc() {
        String dbBackupLoc;
        if (BackupRestoreUtil.getDBType() == 2) {
            dbBackupLoc = getDataFolderLoc();
        }
        else {
            dbBackupLoc = "mssql";
        }
        return dbBackupLoc;
    }
    
    private int getBackupContentType() throws DCBackupRestoreException {
        try {
            if (!BackupRestoreUtil.isDBRunning() && !DBUtil.isRemoteDB()) {
                return 3;
            }
            final String backupTypeFromConfFile = BackupRestoreUtil.getInstance().getBackupType();
            if (backupTypeFromConfFile.equalsIgnoreCase("dump") || (BackupRestoreUtil.getDBType() == 3 && !DMDBBackupRestore.isBakFormatEnabled) || DBUtil.isRemoteDB()) {
                return 2;
            }
            return 1;
        }
        catch (final Exception e) {
            DMDBBackupRestore.LOGGER.log(Level.SEVERE, "Exception while trying to get backup type", e);
            throw new DCBackupRestoreException(e.getMessage());
        }
    }
    
    private static String getDataFolderLoc() {
        final String dataFolderLoc = "pgsql" + File.separator + "data";
        return dataFolderLoc;
    }
    
    public static void includeDataFolder(final HashMap<Integer, Properties> backupList) {
        if (BackupRestoreUtil.getDBType() == 2) {
            final Properties dbProps = new Properties();
            dbProps.setProperty("backup_type", "copy");
            dbProps.setProperty("file_path", getDataFolderLoc());
            backupList.put(1001, dbProps);
            DMDBBackupRestore.LOGGER.log(Level.INFO, "Pgsql data folder is added in PreCheck list");
        }
    }
    
    public void restore(final int dbBackupContentType) throws Exception {
        DMDBBackupRestore.LOGGER.log(Level.INFO, "content type : " + dbBackupContentType);
        DMDBBackupRestore.backupContentType = dbBackupContentType;
        if (dbBackupContentType == 3) {
            this.doCopyRestore();
        }
        else {
            this.doOnlineRestore(dbBackupContentType);
        }
    }
    
    private void doOnlineRestore(final int dbBackupContentType) throws Exception {
        final CompressUtil compressUtil = new CompressUtil(this.informable, DMBackupPasswordHandler.getInstance().getPassword(Boolean.FALSE));
        String tempFolderFullPath = this.serverHome + File.separator + BackupRestoreUtil.getInstance().getTempFolderName();
        String fullBackupZipName = null;
        try {
            if (dbBackupContentType == 2 || BackupRestoreUtil.getDBType() == 3) {
                this.setOldDBProps();
                this.doCurrentDBBackup();
                if (DMRestoreAction.dbContenttypeFromBackup == 1 && new File(DMRestoreAction.sourceFile).exists()) {
                    tempFolderFullPath = DMRestoreAction.sourceFile.substring(0, DMRestoreAction.sourceFile.lastIndexOf("."));
                    new File(tempFolderFullPath).mkdirs();
                }
            }
            final boolean extractSuccess = compressUtil.decompress(DMRestoreAction.sourceFile, this.getDBBackupLoc(), tempFolderFullPath);
            if (!extractSuccess) {
                throw new Exception("Unable to extract data folder");
            }
            DMDBBackupRestore.LOGGER.log(Level.INFO, "Full backup zip file extracted successfully");
            fullBackupZipName = this.getZipFileLoc(tempFolderFullPath);
            if (BackupRestoreUtil.getDBType() == 3 && dbBackupContentType == 1) {
                fullBackupZipName = this.moveBakToScheduledDBBackup(tempFolderFullPath, fullBackupZipName);
            }
            if (fullBackupZipName == null) {
                throw new Exception("Unable to find DB backup zip file");
            }
            int restoreStatus;
            try {
                if (BackupRestoreUtil.isRemoteDB() && !BackupRestoreUtil.isRemoteDBRunning()) {
                    DMDBBackupRestore.LOGGER.log(Level.WARNING, "Remote DB Configured is not running.");
                    throw new Exception("Remote DB is not running");
                }
                restoreStatus = (this.restoreDB(fullBackupZipName) ? 1 : -1);
            }
            catch (final Exception e) {
                DMDBBackupRestore.LOGGER.log(Level.WARNING, "Exception while restoring database.", e);
                restoreStatus = -6;
            }
            if (restoreStatus != 1) {
                throw new Exception("Unable to restore DB Backup");
            }
            DMDBBackupRestore.LOGGER.log(Level.INFO, "DB Restore completed successfully");
        }
        catch (final Exception ex) {
            if (dbBackupContentType == 2) {
                if (!ex.getMessage().contains("files might be in use")) {
                    DMDBBackupRestore.LOGGER.log(Level.WARNING, "DB Restore was failed. Going to revert...");
                    this.revertDBBackup();
                }
                else {
                    DMDBBackupRestore.LOGGER.log(Level.WARNING, "Current DB backup was failed, so DB revert not called");
                }
            }
            throw ex;
        }
        finally {
            final String dataFolderCopyLoc = this.serverHome + File.separator + DMDBBackupRestore.DATA_FOLDER + "_copy";
            if (FileUtil.isFileExists(dataFolderCopyLoc)) {
                DMDBBackupRestore.LOGGER.log(Level.INFO, "Deleting folder {0}, status : {1}", new Object[] { dataFolderCopyLoc, FileUtil.deleteFileOrFolder(new File(dataFolderCopyLoc)) });
            }
            FileUtil.deleteFileOrFolder(new File(BackupRestoreUtil.getInstance().getDefaultBackupLocation() + File.separator + BackupRestoreUtil.getInstance().getMickeyBackupConfigProperties().getProperty("default.backup.directory", "ScheduledDBBackup") + File.separator + "restore" + File.separator + "mssql"));
        }
    }
    
    private String moveBakToScheduledDBBackup(final String bakFolderPath, String ezipPath) throws Exception {
        final String restoreLocation = "\\\\" + InetAddress.getLocalHost().getHostName() + File.separator + BackupRestoreUtil.getInstance().getMickeyBackupConfigProperties().getProperty("default.backup.directory", "ScheduledDBBackup") + File.separator + "restore" + File.separator + "mssql";
        FileUtil.deleteFileOrFolder(new File(restoreLocation));
        FileAccessUtil.moveFolder(Paths.get(bakFolderPath + File.separator + "mssql", new String[0]), Paths.get(restoreLocation, new String[0]), new CopyOption[0]);
        ezipPath = restoreLocation + File.separator + Paths.get(ezipPath, new String[0]).getFileName().toString();
        List<String> filesToModify = new ArrayList<String>();
        filesToModify.add("full_index.props");
        SevenZipUtils.unZip(new File(ezipPath), new File(restoreLocation), (List)filesToModify, (List)null, "Password123");
        final Properties properties = BackupRestoreUtil.readProperties(restoreLocation + File.separator + "full_index.props");
        properties.setProperty("remoteBackupFolder", restoreLocation);
        properties.setProperty("location", ezipPath.replace(".ezip", ".bak"));
        BackupRestoreUtil.storeProperties(properties, restoreLocation + File.separator + "full_index.props", null);
        filesToModify = new ArrayList<String>();
        filesToModify.add(restoreLocation + File.separator + "full_index.props");
        SevenZipUtils.appendInZip(ezipPath, (List)filesToModify, "Password123", (String)null);
        return ezipPath;
    }
    
    private String getZipFileLoc(final String tempFolderFullPath) throws Exception {
        String fullBackupZipLoc = null;
        final FilenameFilter zipFileFilter = new FilenameFilter() {
            @Override
            public boolean accept(final File directory, final String fileName) {
                return fileName.endsWith(".zip") || fileName.endsWith(".ezip");
            }
        };
        final File[] zipFileList = new File(tempFolderFullPath + File.separator + this.getDBBackupLoc()).listFiles(zipFileFilter);
        if (zipFileList != null && zipFileList.length > 0) {
            final File zipFile = zipFileList[0];
            fullBackupZipLoc = zipFile.getAbsolutePath();
        }
        return fullBackupZipLoc;
    }
    
    private void doCurrentDBBackup() throws Exception {
        DMDBBackupRestore.LOGGER.log(Level.INFO, " **************************** Going to start current DB Backup **************************** ");
        try {
            if (BackupRestoreUtil.getDBType() == 3 || (BackupRestoreUtil.getDBType() == 2 && BackupRestoreUtil.isRemoteDB())) {
                DMDBBackupRestore.LOGGER.log(Level.INFO, "Dumping current DB");
                final int dbBackupContentType = 1;
                final String currentBackupLoc = this.serverHome + File.separator + BackupRestoreUtil.getInstance().getTempFolderName() + File.separator + "CurrentBackup";
                final DMDBBackupRestore currentBackupObj = new DMDBBackupRestore(currentBackupLoc, this.informable);
                currentBackupObj.doOfflineBackup(dbBackupContentType);
            }
            else {
                DMDBBackupRestore.LOGGER.log(Level.INFO, "Copying current data folder");
                final File dataFolder = new File(this.serverHome, getDataFolderLoc());
                final File dataFolderCopy = new File(this.serverHome, getDataFolderLoc() + "_copy");
                FileUtil.copyFolder(dataFolder, dataFolderCopy);
            }
            DMDBBackupRestore.LOGGER.log(Level.INFO, " **************************** Current DB Backup completed **************************** ");
        }
        catch (final Exception ex) {
            DMDBBackupRestore.LOGGER.log(Level.WARNING, "Current backup failed!!!!!", ex);
            final String displayName = new BackupRestoreUtil().getValueFromGenProps("displayname");
            throw BackupRestoreUtil.createException(-11, new Object[] { displayName }, null);
        }
    }
    
    private void revertDBBackup() throws Exception {
        DMDBBackupRestore.LOGGER.log(Level.INFO, " **************************** Going to revert DB Backup **************************** ");
        try {
            if (BackupRestoreUtil.getDBType() == 2) {
                final FileUtil fileUtil = new FileUtil();
                final File dataFolder = new File(this.serverHome, getDataFolderLoc());
                final File dataFolderCopy = new File(this.serverHome, getDataFolderLoc() + "_copy");
                if (dataFolderCopy.exists()) {
                    DMDBBackupRestore.LOGGER.log(Level.INFO, "Reverting copy data folder");
                    FileUtil.deleteFileOrFolder(dataFolder);
                    final boolean renameSuccess = FileUtil.renameFolder(dataFolderCopy.getAbsolutePath(), dataFolder.getAbsolutePath());
                    DMDBBackupRestore.LOGGER.log(Level.INFO, "Revert operation : {0}", renameSuccess);
                }
            }
            else {
                final String currentBackupLoc = this.serverHome + File.separator + BackupRestoreUtil.getInstance().getTempFolderName() + File.separator + "CurrentBackup";
                String currentBackupZipName = this.getZipFileLoc(currentBackupLoc);
                if (currentBackupZipName == null) {
                    throw new Exception("Unable to find DB backup zip file");
                }
                final boolean isBakTaken = System.getProperty("isBakTaken", "false").equalsIgnoreCase("true");
                if (isBakTaken) {
                    DMRestoreAction.dbContenttypeFromBackup = 1;
                }
                if (BackupRestoreUtil.getDBType() == 3 && DMRestoreAction.dbContenttypeFromBackup == 1) {
                    currentBackupZipName = this.moveBakToScheduledDBBackup(currentBackupLoc, currentBackupZipName);
                }
                int restoreStatus;
                try {
                    if (BackupRestoreUtil.isRemoteDB() && !BackupRestoreUtil.isRemoteDBRunning()) {
                        DMDBBackupRestore.LOGGER.log(Level.WARNING, "Remote DB Configured is not running.");
                        throw new Exception("Remote DB is not running");
                    }
                    restoreStatus = (this.restoreDB(currentBackupZipName) ? 1 : -1);
                }
                catch (final Exception e) {
                    DMDBBackupRestore.LOGGER.log(Level.WARNING, "Exception while reverting database.", e);
                    restoreStatus = -6;
                }
                if (restoreStatus != 1) {
                    throw new Exception("Unable to retore DB Backup");
                }
            }
            DMDBBackupRestore.LOGGER.log(Level.INFO, "DB Revert completed successfully");
            DMDBBackupRestore.LOGGER.log(Level.INFO, " **************************** DB Backup Revert completed successfully**************************** ");
        }
        catch (final Exception ex) {
            DMDBBackupRestore.LOGGER.log(Level.WARNING, "DB Backup revert failed.!!!!");
            BackupRestoreUtil.getInstance().createRevertLockFile();
            throw BackupRestoreUtil.createException(-12);
        }
    }
    
    private boolean restoreDB(final String src) throws Exception {
        boolean isRestored = false;
        FileUtil fileUtil = null;
        try {
            fileUtil = new FileUtil();
            BackupRestoreUtil.setDBHome();
            RestoreDB.RESTORING_DB_USING_SCRIPTS = true;
            int restoreStatus = 0;
            if (BackupRestoreUtil.getDBType() == 3) {
                if (DMRestoreAction.dbContenttypeFromBackup == 1) {
                    PersistenceInitializer.resetMickey(true);
                    AppResources.setProperty("force.restore", "true");
                    final RestoreResult status = RelationalAPI.getInstance().getDBAdapter().getRestoreHandler().restoreBackup(src, "Password123");
                    isRestored = this.isRestoreSucceeded(status.getRestoreStatus().getValue());
                }
                else {
                    restoreStatus = DCMssqlDBAdapter.class.newInstance().restoreDB(src);
                    isRestored = this.isRestoreSucceeded(restoreStatus);
                }
            }
            else {
                if (!BackupRestoreUtil.isRemoteDB()) {
                    this.setOldDBPassword();
                    final boolean isDBStartupRequired = BackupRestoreUtil.validateDBStartupRequired(System.getProperty("forceRestoreRequired", "false"));
                    if (isDBStartupRequired) {
                        final boolean isDBStarted = BackupRestoreUtil.startDB();
                        if (!isDBStarted) {
                            DMDBBackupRestore.LOGGER.log(Level.INFO, "Setting force.restore property to be true. Initiating force restore....");
                            System.setProperty("force.restore", "true");
                            AppResources.setProperty("force.restore", "true");
                        }
                        BackupRestoreUtil.stopDB();
                    }
                    restoreStatus = new MEServer().restoreDB(src, DMBackupPasswordHandler.getInstance().getPassword(Boolean.FALSE));
                }
                else {
                    RelationalAPI.getInstance().getDBAdapter().getRestoreHandler().restoreBackup(src, DMBackupPasswordHandler.getInstance().getPassword(Boolean.FALSE));
                }
                DMDBBackupRestore.LOGGER.log(Level.INFO, "Restore status : " + restoreStatus);
                if (restoreStatus == BackupRestoreConfigurations.RESTORE_STATUS.RESTORE_SUCCESSFULLY_COMPLETED.getValue()) {
                    DMDBBackupRestore.LOGGER.log(Level.INFO, "DB Restored Successfully");
                    isRestored = true;
                }
                else if (restoreStatus == BackupRestoreConfigurations.RESTORE_STATUS.RESTORE_PROCESS_FAILED.getValue()) {
                    DMDBBackupRestore.LOGGER.log(Level.WARNING, "Problem while restoring database. DB type : {0}", BackupRestoreUtil.getDBType());
                    isRestored = false;
                }
            }
        }
        catch (final Exception ex) {
            DMDBBackupRestore.LOGGER.log(Level.WARNING, "Problem while restoring db. ", ex);
            isRestored = false;
            final String dataFolderLoc = this.serverHome + File.separator + DMDBBackupRestore.DATA_FOLDER;
            final String dataNewFolderLoc = this.serverHome + File.separator + DMDBBackupRestore.DATA_NEW_FOLDER;
            if (FileUtil.isFileExists(dataFolderLoc) && FileUtil.isFileExists(dataNewFolderLoc)) {
                DMDBBackupRestore.LOGGER.log(Level.INFO, "{0} folder delete status : {1}", new Object[] { dataNewFolderLoc, FileUtil.deleteFileOrFolder(new File(dataNewFolderLoc)) });
            }
            else {
                DMDBBackupRestore.LOGGER.log(Level.INFO, "{0} folder presence: {1}, {2} folder presence: {3}. Not going to delete {2} folder.", new Object[] { dataFolderLoc, FileUtil.isFileExists(dataFolderLoc), dataNewFolderLoc, FileUtil.isFileExists(dataNewFolderLoc) });
            }
        }
        finally {
            RestoreDB.RESTORING_DB_USING_SCRIPTS = false;
            try {
                PersistenceInitializer.stopDB();
            }
            catch (final Exception e) {
                DMDBBackupRestore.LOGGER.log(Level.INFO, "Problem while Stopping DB.", e);
            }
            final String dataFolderLoc2 = this.serverHome + File.separator + DMDBBackupRestore.DATA_FOLDER;
            if (FileUtil.isFileExists(dataFolderLoc2)) {
                DMDBBackupRestore.LOGGER.log(Level.INFO, "{0} folder exists. Going to delete old data folder", dataFolderLoc2);
                final File[] listFiles;
                final File[] oldDataFolders = listFiles = new File(this.serverHome, "pgsql").listFiles(this.getOldDataFolderFilter());
                for (final File oldDataFolder : listFiles) {
                    if (!oldDataFolder.getAbsolutePath().endsWith("data_copy")) {
                        DMDBBackupRestore.LOGGER.log(Level.INFO, "Going to delete {0} folder. Status : {1}", new Object[] { oldDataFolder.getAbsolutePath(), FileUtil.deleteFileOrFolder(oldDataFolder) });
                    }
                }
                DMDBBackupRestore.LOGGER.log(Level.INFO, "{0} old data folders deleted", oldDataFolders.length);
            }
            else {
                DMDBBackupRestore.LOGGER.log(Level.INFO, "{0} folder not exists. Not going to delete old data folders", dataFolderLoc2);
            }
        }
        return isRestored;
    }
    
    private boolean isRestoreSucceeded(final int restoreStatus) {
        boolean isRestored = false;
        if (restoreStatus == BackupRestoreConfigurations.RESTORE_STATUS.RESTORE_SUCCESSFULLY_COMPLETED.getValue()) {
            DMDBBackupRestore.LOGGER.log(Level.INFO, "Mssql DB Restored Successfully");
            isRestored = true;
        }
        else if (restoreStatus == BackupRestoreConfigurations.RESTORE_STATUS.RESTORE_PROCESS_FAILED.getValue()) {
            DMDBBackupRestore.LOGGER.log(Level.WARNING, "Problem while restoring Mssql database");
        }
        return isRestored;
    }
    
    private void setOldDBProps() throws Exception {
        DMDBBackupRestore.LOGGER.log(Level.INFO, "Changing database_params.conf file");
        final String confFile = this.serverHome + File.separator + "conf" + File.separator + "database_params.conf";
        final String tempConfFolderName = "conf-" + DMDBBackupRestore.tempFolder;
        final String confFromTemp = this.serverHome + File.separator + tempConfFolderName + File.separator + "database_params.conf";
        if (!new File(confFromTemp).exists()) {
            DMDBBackupRestore.LOGGER.log(Level.INFO, "during restore the current conf folder is not backed up. Hence not restoring existing database_params.conf");
        }
        else {
            FileUtils.copyFile(new File(confFromTemp), new File(confFile));
            this.reEncryptDataBaseParamsWithNewCryptTag();
            BackupRestoreUtil.setDBProps();
        }
    }
    
    private void reEncryptDataBaseParamsWithNewCryptTag() throws Exception {
        final String tempConfFolderName = "conf-" + DMDBBackupRestore.tempFolder;
        final File oldCustomerConfigXml = new File(this.serverHome + File.separator + tempConfFolderName + File.separator + "customer-config.xml");
        final InputStream oldCustomerConfigXmlInputStream = new FileInputStream(oldCustomerConfigXml);
        String oldEncryptionString = null;
        NodeList nodeList = BackupRestoreUtil.getInstance().parseXML(oldCustomerConfigXmlInputStream).getElementsByTagName("configuration");
        for (int i = 0; i < nodeList.getLength(); ++i) {
            final Element element = (Element)nodeList.item(i);
            if (element.getAttribute("name").equals("CryptTag")) {
                oldEncryptionString = element.getAttribute("value");
                break;
            }
        }
        if (oldEncryptionString == null) {
            oldEncryptionString = "MLITE_ENCRYPT_DECRYPT";
        }
        final File newCustomerConfigXml = new File(this.serverHome + File.separator + "conf" + File.separator + "customer-config.xml");
        final InputStream newCustomerConfigXmlInputStream = new FileInputStream(newCustomerConfigXml);
        String newEncryptionString = null;
        nodeList = BackupRestoreUtil.getInstance().parseXML(newCustomerConfigXmlInputStream).getElementsByTagName("configuration");
        for (int j = 0; j < nodeList.getLength(); ++j) {
            final Element element2 = (Element)nodeList.item(j);
            if (element2.getAttribute("name").equals("CryptTag")) {
                newEncryptionString = element2.getAttribute("value");
                break;
            }
        }
        final String newEncryptedPassword = new EnDecryptAES256Impl().encrypt(new EnDecryptAES256Impl().decrypt(BackupRestoreUtil.readProperties(this.serverHome + File.separator + "conf" + File.separator + "database_params.conf").getProperty("password"), oldEncryptionString), newEncryptionString);
        PersistenceUtil.updatePasswordInDBConf(newEncryptedPassword);
    }
    
    private void setOldDBPassword() throws Exception {
        if (DMRestoreAction.dbContenttypeFromBackup != 2) {
            final String tempConfFolderName = "conf-" + DMDBBackupRestore.tempFolder;
            final String confFromTemp = this.serverHome + File.separator + tempConfFolderName + File.separator + "database_params.conf";
            if (!new File(confFromTemp).exists()) {
                DMDBBackupRestore.LOGGER.log(Level.INFO, "during restore the current conf folder is not backed up. Hence not restoring existing database_params.conf");
            }
            else {
                final Properties oldDBParams = BackupRestoreUtil.readProperties(confFromTemp);
                final Properties passwordProps = new Properties();
                ((Hashtable<String, String>)passwordProps).put("password", oldDBParams.getProperty("password"));
                BackupRestoreUtil.writeInDataBaseParamsConf(passwordProps);
            }
        }
        BackupRestoreUtil.removeInDataBaseParamsConf("superuser_pass");
    }
    
    private FilenameFilter getOldDataFolderFilter() {
        final FilenameFilter oldDataFolderFilter = new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String fileName) {
                return fileName.startsWith("data_");
            }
        };
        return oldDataFolderFilter;
    }
    
    private void doCopyRestore() throws Exception {
        final FileUtil fileUtil = new FileUtil();
        File dataFolder = null;
        String tempDataFolder = null;
        final CompressUtil compressUtil = new CompressUtil(this.informable, DMBackupPasswordHandler.getInstance().getPassword(Boolean.FALSE));
        final String suffix = BackupRestoreUtil.getInstance().getTempFolderName();
        final String pgsqlDataFolderLoc = getDataFolderLoc();
        boolean extractSuccess = false;
        boolean isDataFolderRenamed = false;
        try {
            dataFolder = new File(this.serverHome, pgsqlDataFolderLoc);
            tempDataFolder = dataFolder.getAbsolutePath() + "-" + suffix;
            final boolean copySuccess = FileUtil.renameFolder(dataFolder.getAbsolutePath(), tempDataFolder);
            isDataFolderRenamed = true;
            if (!copySuccess) {
                throw new Exception("Unable to rename data folder");
            }
            extractSuccess = compressUtil.decompress(DMRestoreAction.sourceFile, pgsqlDataFolderLoc, this.serverHome);
            if (!extractSuccess) {
                DMDBBackupRestore.LOGGER.log(Level.WARNING, "Unable to extract Pgsql data folder");
                throw new Exception("Unable to extract pgsql data folder");
            }
            DMDBBackupRestore.LOGGER.log(Level.INFO, "Pgsql data folder extracted successfully");
        }
        catch (final Exception ex) {
            DMDBBackupRestore.LOGGER.log(Level.WARNING, "Exception while restoring DB backup", ex);
            if (isDataFolderRenamed && FileUtil.isFileExists(tempDataFolder)) {
                DMDBBackupRestore.LOGGER.log(Level.INFO, "Reverting data folder");
                FileUtil.deleteFileOrFolder(dataFolder);
                final boolean renameSuccess = FileUtil.renameFolder(tempDataFolder, dataFolder.getAbsolutePath());
                DMDBBackupRestore.LOGGER.log(Level.INFO, "Revert operation : {0}", renameSuccess);
            }
            throw ex;
        }
        finally {
            if (FileUtil.isFileExists(tempDataFolder)) {
                FileUtil.deleteFileOrFolder(new File(tempDataFolder));
            }
        }
    }
    
    private static String getDBFileName() {
        String destinationFolderName = null;
        try {
            final Properties backupAttributes = BackupRestoreUtil.getInstance().getBackupAttributes(DMFileBackup.getInstance().getDoc());
            String fileNameFormat = backupAttributes.getProperty("outfile_name_format");
            if (fileNameFormat == null) {
                fileNameFormat = "$BUILDNUMBER-$TIMESTAMP";
            }
            destinationFolderName = BackupRestoreUtil.getInstance().getFormattedName(fileNameFormat);
            DMDBBackupRestore.LOGGER.log(Level.INFO, "BAK FILE NAME\t::\t{0}", destinationFolderName);
        }
        catch (final Exception ex) {
            DMDBBackupRestore.LOGGER.log(Level.WARNING, "Caught Exception while getting destination backup folder name ", ex);
        }
        return destinationFolderName;
    }
    
    public static String getDBbackupFileName() {
        String backupFile = DMBackupAction.backupFolderName;
        if (backupFile == null || backupFile == "") {
            backupFile = getDBFileName();
        }
        backupFile += ".ezip";
        return backupFile;
    }
    
    static {
        LOGGER = Logger.getLogger("ScheduleDBBackup");
        DATA_NEW_FOLDER = "pgsql" + File.separator + "data_new";
        DATA_FOLDER = "pgsql" + File.separator + "data";
        DMDBBackupRestore.isBakFormatEnabled = false;
        DMDBBackupRestore.backupContentType = 1;
        try {
            final JSONObject frameworkConfigurations = FrameworkConfigurations.getFrameworkConfigurations();
            if (frameworkConfigurations.has("mssql_bak_backuprestore_configurations")) {
                DMDBBackupRestore.isBakFormatEnabled = Boolean.parseBoolean(String.valueOf(((JSONObject)frameworkConfigurations.get("mssql_bak_backuprestore_configurations")).get("isBakFormatEnabled")));
            }
            else {
                DMDBBackupRestore.LOGGER.log(Level.WARNING, "mssql_bak_backuprestore_configurations doesn't exists");
            }
        }
        catch (final Exception ex) {
            DMDBBackupRestore.LOGGER.log(Level.WARNING, "Exception while retrieving data from framework configuration. ", ex);
        }
    }
}
