package com.adventnet.db.adapter.mysql;

import java.util.Hashtable;
import java.util.Collection;
import com.adventnet.mfw.ConsoleOut;
import com.adventnet.db.adapter.BackupRestoreUtil;
import com.zoho.conf.AppResources;
import com.adventnet.persistence.PersistenceInitializer;
import java.util.Iterator;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import com.adventnet.db.adapter.RestoreStatus;
import java.util.Arrays;
import com.zoho.framework.utils.FileUtils;
import java.io.IOException;
import com.adventnet.db.adapter.BackupRestoreException;
import com.adventnet.db.adapter.RestoreErrors;
import com.zoho.framework.utils.OSCheckUtil;
import com.adventnet.db.adapter.BackupRestoreConfigurations;
import java.io.File;
import com.zoho.conf.Configuration;
import java.util.logging.Level;
import com.adventnet.db.adapter.RestoreResult;
import com.adventnet.db.adapter.RestoreDBParams;
import java.util.logging.Logger;
import com.adventnet.db.adapter.AbstractRestoreHandler;

public class MysqlRestoreHandler extends AbstractRestoreHandler
{
    private static final Logger LOGGER;
    public static final int OS;
    protected boolean endsWithEZIP;
    
    public MysqlRestoreHandler() {
        this.endsWithEZIP = true;
    }
    
    @Override
    protected RestoreResult restoreDBBackup(final RestoreDBParams rdbp) throws BackupRestoreException {
        this.endsWithEZIP = rdbp.getSourceFile().endsWith(".ezip");
        MysqlRestoreHandler.LOGGER.log(Level.INFO, "Entered restoreBackup for [{0}]", rdbp.toString());
        final String myCnf = Configuration.getString("db.home") + File.separator + "bin" + File.separator + "my.cnf";
        final boolean isLoopBackAddress = ((MysqlDBAdapter)this.dbAdapter).isLoopbackAddress();
        final boolean isBundledDB = this.dbAdapter.isBundledDB();
        final RestoreResult restoreResult = new RestoreResult(rdbp.getSourceFile());
        final RestoreStatus restoreStatus = AbstractRestoreHandler.getRestoreStatus(rdbp);
        restoreResult.setBackupContentType(BackupRestoreConfigurations.BACKUP_CONTENT_TYPE.BINARY);
        restoreStatus.setStatus(BackupRestoreConfigurations.RESTORE_STATUS.RESTORE_PROCESS_STARTED);
        restoreResult.setRestoreStatus(restoreStatus.getStatus());
        this.sendRestoreNotification(restoreStatus);
        try {
            if (!isBundledDB && !OSCheckUtil.isWindows(MysqlRestoreHandler.OS) && isLoopBackAddress) {
                final File f = new File(myCnf);
                if (!f.exists()) {
                    MysqlRestoreHandler.LOGGER.log(Level.SEVERE, "Restore failed for Installed DB :: Required file my.cnf not found at :: [{0}]", myCnf);
                    throw new BackupRestoreException(RestoreErrors.INSTALLED_DB_CONF_FILE_NOT_FOUND);
                }
            }
            boolean isServerStarted = false;
            try {
                isServerStarted = this.dbInitializer.isServerStarted();
            }
            catch (final IOException e) {
                throw new BackupRestoreException(RestoreErrors.PROBLEM_CHECKING_DB_SERVER_STATUS, e);
            }
            if (!isServerStarted) {
                MysqlRestoreHandler.LOGGER.log(Level.SEVERE, "Mysql DB Server is not running, hence please start the Database and then try restoring ...");
                throw new BackupRestoreException(RestoreErrors.DATABASE_NOT_RUNNING);
            }
            final Properties properties = this.dbAdapter.getDBProps();
            final String username = properties.getProperty("username");
            final String password = properties.getProperty("password", "");
            String url = properties.getProperty("url");
            if (url.contains("?")) {
                url = url.substring(0, url.indexOf("?"));
            }
            final String hostName = ((Hashtable<K, String>)properties).get("Server");
            final String sockPath = Configuration.getString("db.home") + File.separator + "tmp" + File.separator + "mysql.sock";
            final Integer port = ((Hashtable<K, Integer>)properties).get("Port");
            final String database = url.substring(url.lastIndexOf("/") + 1, url.length());
            final File tempDirectory = this.getTempBackupDirectory();
            final File fullIndexFile = new File(tempDirectory, "full_index.props");
            final File incrementalIndexFile = new File(tempDirectory, "incremental_index.props");
            final boolean isIncrementalBackup = incrementalIndexFile.exists();
            final boolean isFullBackup = fullIndexFile.exists();
            final String backupZipFolder = new File(rdbp.getSourceFile()).getParent();
            String mysqlPath = Configuration.getString("db.home") + File.separator + "bin" + File.separator + "mysql";
            if (OSCheckUtil.isWindows(MysqlRestoreHandler.OS)) {
                mysqlPath += ".exe";
            }
            final File file = new File(mysqlPath);
            if (!file.exists()) {
                MysqlRestoreHandler.LOGGER.log(Level.SEVERE, "mysql not found at :: {0}", mysqlPath);
                throw new BackupRestoreException(RestoreErrors.RESTORE_BINARY_NOT_FOUND);
            }
            if (!isFullBackup && !isIncrementalBackup) {
                MysqlRestoreHandler.LOGGER.log(Level.SEVERE, "This is neither a Full nor a Incremental Backup file, hence it cannot be restored using MysqlBackupHandler");
                throw new BackupRestoreException(RestoreErrors.UNSUPPORTED_RESTORE_TYPE);
            }
            final File propFile = isFullBackup ? fullIndexFile : incrementalIndexFile;
            Properties indexProps;
            try {
                indexProps = FileUtils.readPropertyFile(propFile);
            }
            catch (final IOException ioe) {
                throw new BackupRestoreException(RestoreErrors.PROBLEM_WHILE_CHECKING_FILE_IN_ZIP, ioe);
            }
            final String newDBName = database + "_old";
            restoreStatus.setRestoreStartTime(System.currentTimeMillis());
            restoreResult.setStartTime(restoreStatus.getRestoreStartTime());
            if (isFullBackup) {
                MysqlRestoreHandler.LOGGER.log(Level.INFO, "This is a FullBackupZip");
                if (!this.isBackupCompatible(rdbp.getSourceFile(), rdbp.getResetMickeyType(), rdbp.getArchivePassword())) {
                    MysqlRestoreHandler.LOGGER.log(Level.SEVERE, "This backup is not compatible to this version of the product and hence it cannot be restored");
                    throw new BackupRestoreException(RestoreErrors.INCOMPATIBLE_BACKUP);
                }
                if (isBundledDB) {
                    MysqlRestoreHandler.LOGGER.log(Level.INFO, "Preparing RestoreDB");
                    MysqlRestoreHandler.LOGGER.log(Level.INFO, "Moving database :: " + database + " to " + newDBName + " for safety reasons. Can be deleted manually after successful restore");
                    final boolean renameDBResult = this.renameDatabase(port, hostName, username, password, database, newDBName);
                    restoreStatus.setStatus(BackupRestoreConfigurations.RESTORE_STATUS.MOVING_DATABASE);
                    restoreResult.setRestoreStatus(restoreStatus.getStatus());
                    this.sendRestoreNotification(restoreStatus);
                    if (renameDBResult) {
                        MysqlRestoreHandler.LOGGER.log(Level.INFO, "Previous working copy of database moved to {0}", newDBName);
                    }
                    else {
                        MysqlRestoreHandler.LOGGER.log(Level.SEVERE, "Previous working copy of database will not be found after restore. Data will be lost it restore fails");
                    }
                }
                MysqlRestoreHandler.LOGGER.log(Level.INFO, "Starting RestoreDB");
                this.replaceUDTFiles(rdbp.getSourceFile(), rdbp.getArchivePassword());
                restoreStatus.setStatus(BackupRestoreConfigurations.RESTORE_STATUS.GOING_TO_UNZIP_BACKUPZIP);
                restoreResult.setRestoreStatus(restoreStatus.getStatus());
                this.sendRestoreNotification(restoreStatus);
                this.restoreFile(username, password, hostName, sockPath, port, database, new File(rdbp.getSourceFile()).getAbsolutePath(), rdbp.getArchivePassword());
                restoreStatus.setStatus(BackupRestoreConfigurations.RESTORE_STATUS.UNZIP_BACKUPZIP_COMPLETED);
                restoreResult.setRestoreStatus(restoreStatus.getStatus());
                this.sendRestoreNotification(restoreStatus);
            }
            else {
                MysqlRestoreHandler.LOGGER.log(Level.INFO, "This is an Incremental BackupZip");
                final File fullBackupZipFile = new File(backupZipFolder, indexProps.getProperty("fullbackup_zipname"));
                MysqlRestoreHandler.LOGGER.log(Level.INFO, "fullBackupZipFile :: [{0}], exists :: [{1}]", new Object[] { fullBackupZipFile, fullBackupZipFile.exists() });
                String[] previousIncrBackupZipFileNames = null;
                if (!indexProps.getProperty("previous_incr_backup_zipnames", "").equals("")) {
                    previousIncrBackupZipFileNames = indexProps.getProperty("previous_incr_backup_zipnames", "").split(",");
                    MysqlRestoreHandler.LOGGER.log(Level.INFO, "previousIncrBackupZipFileNames :: {0}", Arrays.toString(previousIncrBackupZipFileNames));
                }
                File[] previousIncrBackupZipFiles = null;
                if (previousIncrBackupZipFileNames != null) {
                    previousIncrBackupZipFiles = new File[previousIncrBackupZipFileNames.length];
                    boolean allDependentZipsAvailable = true;
                    allDependentZipsAvailable = (allDependentZipsAvailable && fullBackupZipFile.exists());
                    MysqlRestoreHandler.LOGGER.log(Level.INFO, "allDependentZipsAvailable :: {0}", allDependentZipsAvailable);
                    MysqlRestoreHandler.LOGGER.log(Level.INFO, "length :: {0}", previousIncrBackupZipFileNames.length);
                    MysqlRestoreHandler.LOGGER.log(Level.INFO, "backupZipFolder :: {0}", backupZipFolder);
                    for (int index = 0; allDependentZipsAvailable && index < previousIncrBackupZipFileNames.length; allDependentZipsAvailable = (allDependentZipsAvailable && previousIncrBackupZipFiles[index].exists()), ++index) {
                        final String prevIncrBackupFileName = previousIncrBackupZipFileNames[index];
                        previousIncrBackupZipFiles[index] = new File(backupZipFolder, prevIncrBackupFileName);
                        MysqlRestoreHandler.LOGGER.log(Level.INFO, "index :: {0}, zipFile :: [{1}] exists() :: {2}", new Object[] { index, previousIncrBackupZipFileNames[index], previousIncrBackupZipFiles[index].exists() });
                    }
                    if (!allDependentZipsAvailable) {
                        MysqlRestoreHandler.LOGGER.log(Level.SEVERE, "Some/All of the dependent backup zips [" + fullBackupZipFile + ", " + Arrays.toString(previousIncrBackupZipFileNames) + "] are not found in the parent folder of this backupZipFileName :: [" + rdbp.getSourceFile() + "], hence it cannot be restored.");
                        throw new BackupRestoreException(RestoreErrors.MISSING_DEPENDENT_BACKUPS);
                    }
                }
                else if (!fullBackupZipFile.exists()) {
                    MysqlRestoreHandler.LOGGER.log(Level.SEVERE, "Full backup zip [" + fullBackupZipFile + "] is not found in the parent folder of this backupZipFileName :: [" + rdbp.getSourceFile() + "], hence it cannot be restored.");
                    throw new BackupRestoreException(RestoreErrors.MISSING_DEPENDENT_BACKUPS);
                }
                if (!this.isBackupCompatible(fullBackupZipFile.toString(), rdbp.getResetMickeyType(), rdbp.getArchivePassword())) {
                    MysqlRestoreHandler.LOGGER.log(Level.SEVERE, "This backup is not compatible to this version of the product and hence it cannot be restored");
                    throw new BackupRestoreException(RestoreErrors.INCOMPATIBLE_BACKUP);
                }
                MysqlRestoreHandler.LOGGER.log(Level.INFO, "Preparing RestoreDB");
                MysqlRestoreHandler.LOGGER.log(Level.INFO, "Moving database :: " + database + " to " + newDBName + " for safety reasons. Can be deleted manually after successful restore");
                final boolean renameDBResult2 = this.renameDatabase(port, hostName, username, password, database, newDBName);
                restoreStatus.setStatus(BackupRestoreConfigurations.RESTORE_STATUS.MOVING_DATABASE);
                restoreResult.setRestoreStatus(restoreStatus.getStatus());
                this.sendRestoreNotification(restoreStatus);
                if (renameDBResult2) {
                    MysqlRestoreHandler.LOGGER.log(Level.INFO, "Previous working copy of database moved to {0}", newDBName);
                }
                else {
                    MysqlRestoreHandler.LOGGER.log(Level.SEVERE, "Previous working copy of database will not be found after restore. Data will be lost it restore fails");
                }
                MysqlRestoreHandler.LOGGER.log(Level.INFO, "Starting RestoreDB");
                this.replaceUDTFiles(fullBackupZipFile.getAbsolutePath(), rdbp.getArchivePassword());
                restoreStatus.setStatus(BackupRestoreConfigurations.RESTORE_STATUS.GOING_TO_UNZIP_BACKUPZIP);
                restoreResult.setRestoreStatus(restoreStatus.getStatus());
                this.sendRestoreNotification(restoreStatus);
                this.restoreFile(username, password, hostName, sockPath, port, database, new File(backupZipFolder, fullBackupZipFile.getName()).getAbsolutePath(), rdbp.getArchivePassword());
                if (previousIncrBackupZipFiles != null) {
                    for (final File prevIncrBackupZipFile : previousIncrBackupZipFiles) {
                        this.restoreFile(username, password, hostName, sockPath, port, database, new File(backupZipFolder, prevIncrBackupZipFile.getName()).getAbsolutePath(), rdbp.getArchivePassword());
                    }
                }
                this.restoreFile(username, password, hostName, sockPath, port, database, rdbp.getSourceFile(), rdbp.getArchivePassword());
                restoreStatus.setStatus(BackupRestoreConfigurations.RESTORE_STATUS.UNZIP_BACKUPZIP_COMPLETED);
                restoreResult.setRestoreStatus(restoreStatus.getStatus());
                this.sendRestoreNotification(restoreStatus);
            }
            MysqlRestoreHandler.LOGGER.log(Level.INFO, "Finishing RestoreDB");
            restoreStatus.setStatus(BackupRestoreConfigurations.RESTORE_STATUS.RESTORE_SUCCESSFULLY_COMPLETED);
            restoreResult.setRestoreStatus(restoreStatus.getStatus());
            this.sendRestoreNotification(restoreStatus);
            restoreStatus.setRestoreEndTime(System.currentTimeMillis());
            restoreResult.setEndTime(restoreStatus.getRestoreEndTime());
            restoreResult.calculateDuration();
            restoreResult.setDataDirectory(new File(Configuration.getString("db.home") + File.separator + "data"));
        }
        catch (final BackupRestoreException e2) {
            restoreStatus.setStatus(BackupRestoreConfigurations.RESTORE_STATUS.RESTORE_PROCESS_FAILED);
            restoreResult.setRestoreStatus(restoreStatus.getStatus());
            this.sendRestoreNotification(restoreStatus);
            throw e2;
        }
        MysqlRestoreHandler.LOGGER.log(Level.INFO, "Exiting restoreBackup with restoreResult :: {0}", restoreResult.toString());
        return restoreResult;
    }
    
    private void restoreFile(final String username, final String password, final String hostName, final String sockPath, final int port, final String database, final String zipFile, final String archivePassword) throws BackupRestoreException {
        final String mysqlPath = Configuration.getString("db.home") + File.separator + "bin" + File.separator + "mysql";
        final String sqlFileName = new File(zipFile).getName().substring(0, new File(zipFile).getName().length() - (this.endsWithEZIP ? 4 : 3)) + "sql";
        final boolean isLoopBackAddress = ((MysqlDBAdapter)this.dbAdapter).isLoopbackAddress();
        final String myCnf = Configuration.getString("db.home") + File.separator + "bin" + File.separator + "my.cnf";
        final boolean isBundledDB = this.dbAdapter.isBundledDB();
        final List<String> includeFileList = new ArrayList<String>();
        includeFileList.add(sqlFileName);
        this.unZip(new File(zipFile), new File(zipFile).getParentFile(), includeFileList, null, archivePassword);
        String sqlFilePath = zipFile.substring(0, zipFile.length() - (this.endsWithEZIP ? 4 : 3)) + "sql";
        final List<String> commandList = new ArrayList<String>();
        commandList.add(mysqlPath);
        if (isLoopBackAddress && !isBundledDB && !OSCheckUtil.isWindows(MysqlRestoreHandler.OS)) {
            commandList.add("--defaults-file=" + myCnf);
        }
        else {
            commandList.add("--no-defaults");
        }
        commandList.add("--quick");
        commandList.add("--user=" + username);
        commandList.add("--password=" + password);
        commandList.add("--port=" + port);
        commandList.add("--host=" + hostName);
        commandList.add("--force");
        if (isBundledDB && !OSCheckUtil.isWindows(MysqlRestoreHandler.OS)) {
            commandList.add("-S");
            commandList.add(sockPath);
        }
        commandList.add(database);
        commandList.add("-e");
        if (OSCheckUtil.isWindows(MysqlRestoreHandler.OS)) {
            sqlFilePath = sqlFilePath.replaceAll("\\\\", "/");
            commandList.add("\"SET AUTOCOMMIT = 0;SET FOREIGN_KEY_CHECKS=0;SOURCE " + sqlFilePath + ";SET FOREIGN_KEY_CHECKS=1;COMMIT;SET AUTOCOMMIT = 1;\"");
        }
        else {
            commandList.add("SET AUTOCOMMIT = 0;SET FOREIGN_KEY_CHECKS=0;SOURCE " + sqlFilePath + ";SET FOREIGN_KEY_CHECKS=1;COMMIT;SET AUTOCOMMIT = 1;");
        }
        try {
            this.executeCommand(commandList);
        }
        catch (final Exception e) {
            throw new BackupRestoreException(RestoreErrors.PROBLEM_WHILE_EXECUTING_COMMAND, e);
        }
        FileUtils.deleteFile(sqlFilePath);
        MysqlRestoreHandler.LOGGER.log(Level.INFO, "Restored :: [{0}]", sqlFilePath);
    }
    
    private boolean renameDatabase(final int port, final String host, final String userName, final String passwd, final String oldDBName, final String newDBName) throws BackupRestoreException {
        List<?> tableList = null;
        Connection c = null;
        Statement s = null;
        boolean result = false;
        try {
            c = this.dataSource.getConnection();
            MysqlRestoreHandler.LOGGER.log(Level.INFO, "Attempting to create database :: {0}", newDBName);
            final boolean isDBExists = this.dbAdapter.getAllDatabaseNames(c).contains(newDBName);
            s = c.createStatement();
            if (isDBExists) {
                MysqlRestoreHandler.LOGGER.log(Level.INFO, "{0} already found. Dropping it", newDBName);
                s.execute("DROP DATABASE " + newDBName);
            }
            ((MySqlDBInitializer)this.dbInitializer).createDB(port, host, userName, passwd, newDBName);
            MysqlRestoreHandler.LOGGER.log(Level.INFO, "{0} created successfully", newDBName);
            tableList = this.dbAdapter.getTables(c, oldDBName);
            for (final String tableName : tableList) {
                s.execute("RENAME TABLE " + oldDBName + "." + tableName + " TO " + newDBName + "." + tableName);
                MysqlRestoreHandler.LOGGER.log(Level.INFO, "Renamed table :: {0}", tableName);
            }
            result = true;
        }
        catch (final SQLException e) {
            throw new BackupRestoreException(RestoreErrors.PROBLEM_WHILE_EXECUTING_COMMAND, e);
        }
        finally {
            if (s != null) {
                try {
                    s.close();
                }
                catch (final SQLException e2) {
                    e2.printStackTrace();
                }
            }
            if (c != null) {
                try {
                    c.close();
                }
                catch (final SQLException e2) {
                    e2.printStackTrace();
                }
            }
        }
        return result;
    }
    
    private void executeCommand(final List<String> commandList) throws Exception {
        try {
            this.executeCommand(commandList, null, null);
        }
        catch (final BackupRestoreException e) {
            int index = -1;
            for (final String str : commandList) {
                if (str.startsWith("--password")) {
                    index = commandList.indexOf(str);
                    break;
                }
            }
            if (index != -1) {
                commandList.set(index, "--password=********");
            }
            MysqlRestoreHandler.LOGGER.log(Level.SEVERE, "Unable to execute command ", commandList);
            throw e;
        }
    }
    
    @Override
    protected RestoreDBParams preRestoreDB(final String backupZipName, final String password) throws BackupRestoreException {
        final RestoreDBParams rdbp = super.preRestoreDB(backupZipName, password);
        final String dbParamsFileName = PersistenceInitializer.getDBParamsFilePath();
        Properties dbProps = null;
        try {
            dbProps = PersistenceInitializer.getDBProps(dbParamsFileName);
        }
        catch (final Exception e1) {
            e1.printStackTrace();
        }
        try {
            final boolean startDB = Boolean.parseBoolean(PersistenceInitializer.getConfigurationValue("StartDBServer"));
            final File tempDirectory = this.getTempBackupDirectory();
            final File fullIndexPropFile = new File(tempDirectory, "full_index.props");
            final File incrementalIndexPropFile = new File(tempDirectory, "incremental_index.props");
            final File backuprestoreFile = new File(tempDirectory, "backuprestore.conf");
            if (fullIndexPropFile.exists() || incrementalIndexPropFile.exists()) {
                rdbp.setRestoreBackupContentType(BackupRestoreConfigurations.BACKUP_CONTENT_TYPE.BINARY);
                rdbp.setStartDBMode(BackupRestoreConfigurations.DB_START_MODE.PRE_START_DB);
                rdbp.setRestoreBackupMode(null);
                this.setOldCryptTag(fullIndexPropFile.exists() ? fullIndexPropFile : incrementalIndexPropFile, rdbp);
                rdbp.setInitializePersistenceType(BackupRestoreConfigurations.INIT_PERSISTENCE.BEFORE_RESTORE);
                if (startDB) {
                    final boolean isDBStarted = this.dbAdapter.startDB(dbProps.getProperty("url"), dbProps.getProperty("username"), dbProps.getProperty("password"));
                    if (isDBStarted) {
                        this.dbAdapter.createDB(dbProps.getProperty("url"), dbProps.getProperty("username"), dbProps.getProperty("password"));
                    }
                    rdbp.requiresDBStop(true);
                }
                if (!AppResources.getBoolean("force.restore", Boolean.valueOf(false))) {
                    rdbp.setResetMickeyType(BackupRestoreConfigurations.RESET_MICKEY.RESET);
                    PersistenceInitializer.checkAndPrepareDatabase(this.dbAdapter, this.dataSource);
                    PersistenceInitializer.initializeMickey(true);
                    try {
                        final String newDCInfoLocation = BackupRestoreUtil.getDynamicColumnsInfoFileLocation(Configuration.getString("server.home") + File.separator + "bin");
                        MysqlRestoreHandler.LOGGER.log(Level.INFO, "New DC File Location :: {0}", newDCInfoLocation);
                        return rdbp;
                    }
                    catch (final Exception e2) {
                        throw new BackupRestoreException(RestoreErrors.PROBLEM_GENERATING_DYNAMIC_COLUMN_FILE, e2);
                    }
                }
                rdbp.setResetMickeyType(BackupRestoreConfigurations.RESET_MICKEY.NOT_APPLICABLE);
            }
            else if (!backuprestoreFile.exists()) {
                rdbp.setRestoreBackupMode(BackupRestoreConfigurations.BACKUP_MODE.FILE_BACKUP);
            }
            else {
                rdbp.setRestoreBackupContentType(BackupRestoreConfigurations.BACKUP_CONTENT_TYPE.DUMP);
                rdbp.setStartDBMode(BackupRestoreConfigurations.DB_START_MODE.PRE_START_DB);
                rdbp.setRestoreBackupMode(null);
                rdbp.setInitializePersistenceType(BackupRestoreConfigurations.INIT_PERSISTENCE.BEFORE_RESTORE);
                rdbp.setResetMickeyType(BackupRestoreConfigurations.RESET_MICKEY.NOT_APPLICABLE);
                this.setOldCryptTag(backuprestoreFile, rdbp);
                if (startDB) {
                    final boolean isDBStarted = this.dbAdapter.startDB(dbProps.getProperty("url"), dbProps.getProperty("username"), dbProps.getProperty("password"));
                    if (isDBStarted) {
                        this.dbAdapter.createDB(dbProps.getProperty("url"), dbProps.getProperty("username"), dbProps.getProperty("password"));
                    }
                    rdbp.requiresDBStop(true);
                }
            }
        }
        catch (final Exception e3) {
            e3.printStackTrace();
        }
        return rdbp;
    }
    
    @Deprecated
    @Override
    protected RestoreResult restoreTableBackup(final RestoreDBParams rdbp) throws BackupRestoreException {
        MysqlRestoreHandler.LOGGER.log(Level.INFO, "Entered restoreBackup for [{0}]", rdbp.toString());
        final String myCnf = Configuration.getString("db.home") + File.separator + "bin" + File.separator + "my.cnf";
        final boolean isLoopBackAddress = ((MysqlDBAdapter)this.dbAdapter).isLoopbackAddress();
        final boolean isBundledDB = this.dbAdapter.isBundledDB();
        final RestoreResult restoreResult = new RestoreResult(rdbp.getSourceFile());
        final RestoreStatus restoreStatus = AbstractRestoreHandler.getRestoreStatus(rdbp);
        restoreResult.setBackupMode(rdbp.getRestoreBackupMode());
        restoreResult.setBackupContentType(BackupRestoreConfigurations.BACKUP_CONTENT_TYPE.DUMP);
        restoreStatus.setStatus(BackupRestoreConfigurations.RESTORE_STATUS.RESTORE_PROCESS_STARTED);
        restoreResult.setRestoreStatus(restoreStatus.getStatus());
        this.sendRestoreNotification(restoreStatus);
        try {
            if (!isBundledDB && !OSCheckUtil.isWindows(MysqlRestoreHandler.OS) && isLoopBackAddress) {
                final File f = new File(myCnf);
                if (!f.exists()) {
                    MysqlRestoreHandler.LOGGER.log(Level.SEVERE, "my.cnf not found at " + myCnf);
                    throw new BackupRestoreException(RestoreErrors.INSTALLED_DB_CONF_FILE_NOT_FOUND);
                }
            }
            final Properties props = new Properties();
            final String zipFile = rdbp.getSourceFile();
            if (!this.isCompatibleVersion(zipFile, props, rdbp.getArchivePassword())) {
                MysqlRestoreHandler.LOGGER.log(Level.SEVERE, "Incompatible zip file version :: {0}", zipFile);
                throw new BackupRestoreException(RestoreErrors.INCOMPATIBLE_BACKUP);
            }
            final String path = zipFile.substring(0, zipFile.length() - (this.endsWithEZIP ? 4 : 3));
            final File file = new File(path);
            if (!file.exists() && !file.isDirectory()) {
                file.mkdirs();
            }
            restoreStatus.setStatus(BackupRestoreConfigurations.RESTORE_STATUS.GOING_TO_UNZIP_BACKUPZIP);
            restoreResult.setRestoreStatus(restoreStatus.getStatus());
            this.sendRestoreNotification(restoreStatus);
            this.unZip(new File(zipFile), new File(path), null, null, rdbp.getArchivePassword());
            restoreStatus.setStatus(BackupRestoreConfigurations.RESTORE_STATUS.UNZIP_BACKUPZIP_COMPLETED);
            restoreResult.setRestoreStatus(restoreStatus.getStatus());
            this.sendRestoreNotification(restoreStatus);
            ConsoleOut.println("Starting to restore the DB");
            MysqlRestoreHandler.LOGGER.log(Level.INFO, "Restore DB Started");
            String mysqlPath = Configuration.getString("db.home") + "/bin/mysql";
            final String os = AppResources.getString("os.name").toLowerCase();
            if (os.indexOf("window") >= 0) {
                mysqlPath = Configuration.getString("db.home") + "/bin/mysql.exe";
            }
            final Properties properties = this.dbAdapter.getDBProps();
            final String username = properties.getProperty("username");
            final String password = properties.getProperty("password", "");
            String url = properties.getProperty("url");
            if (url.contains("?")) {
                url = url.substring(0, url.indexOf("?"));
            }
            final String hostName = ((Hashtable<K, String>)properties).get("Server");
            final String sockPath = Configuration.getString("db.home") + "/tmp/mysql.sock";
            final Integer port = ((Hashtable<K, Integer>)properties).get("Port");
            final String database = url.substring(url.lastIndexOf("/") + 1, url.length());
            Connection con = null;
            Statement stmt = null;
            try {
                ConsoleOut.print("Dropping Tables ...");
                con = this.dataSource.getConnection();
                stmt = con.createStatement();
                ((MysqlDBAdapter)this.dbAdapter).disableForeignKeyChecks(stmt);
                for (int i = 0; i < props.size(); ++i) {
                    final String tableName = props.getProperty("table" + i);
                    if (tableName != null) {
                        String dropSQL = null;
                        try {
                            dropSQL = this.sqlGenerator.getSQLForDrop(tableName, true);
                            this.dbAdapter.execute(stmt, dropSQL);
                            ConsoleOut.print(".");
                            MysqlRestoreHandler.LOGGER.log(Level.INFO, "Dropped table " + tableName);
                        }
                        catch (final SQLException sqle) {
                            MysqlRestoreHandler.LOGGER.log(Level.INFO, "tableName " + tableName);
                            sqle.printStackTrace();
                        }
                    }
                }
            }
            catch (final Exception e) {
                MysqlRestoreHandler.LOGGER.log(Level.INFO, "Problem while Reinitializing the DB.");
                throw new BackupRestoreException(RestoreErrors.PROBLEM_REINITIALIZING_DB_SERVER, e);
            }
            finally {
                if (stmt != null) {
                    try {
                        stmt.close();
                    }
                    catch (final SQLException e2) {
                        e2.printStackTrace();
                    }
                }
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (final SQLException e2) {
                        e2.printStackTrace();
                    }
                }
            }
            ConsoleOut.println("");
            restoreStatus.setStatus(BackupRestoreConfigurations.RESTORE_STATUS.DROPPED_TABLES);
            restoreResult.setRestoreStatus(restoreStatus.getStatus());
            this.sendRestoreNotification(restoreStatus);
            ConsoleOut.print("Restoring ...");
            restoreStatus.setRestoreStartTime(System.currentTimeMillis());
            restoreResult.setStartTime(restoreStatus.getRestoreStartTime());
            restoreStatus.setStatus(BackupRestoreConfigurations.RESTORE_STATUS.RESTORE_PROCESS_IN_PROGRESS);
            restoreResult.setRestoreStatus(restoreStatus.getStatus());
            this.sendRestoreNotification(restoreStatus);
            final List<String> commandList = new ArrayList<String>();
            commandList.add(mysqlPath);
            if (isLoopBackAddress && !isBundledDB && !OSCheckUtil.isWindows(MysqlRestoreHandler.OS)) {
                commandList.add("--defaults-file=" + myCnf);
            }
            else {
                commandList.add("--no-defaults");
            }
            commandList.add("--user=" + username);
            commandList.add("--password=" + password);
            commandList.add("--port=" + port);
            commandList.add("--host=" + hostName);
            if (isBundledDB && !OSCheckUtil.isWindows(MysqlRestoreHandler.OS)) {
                commandList.add("-S");
                commandList.add(sockPath);
            }
            commandList.add("--force");
            commandList.add(database);
            for (int j = props.size() - 1; j >= 0; --j) {
                final String tableName2 = props.getProperty("table" + j);
                MysqlRestoreHandler.LOGGER.log(Level.INFO, "Restoring Table :: {0}", tableName2);
                if (tableName2 != null) {
                    String backupFile = path + "/" + tableName2 + ".sql";
                    final List<String> command = new ArrayList<String>();
                    command.addAll(commandList);
                    command.add("-e");
                    if (OSCheckUtil.isWindows(MysqlRestoreHandler.OS)) {
                        backupFile = backupFile.replaceAll("\\\\", "/");
                        command.add("\"SET AUTOCOMMIT = 0;SET FOREIGN_KEY_CHECKS=0;source " + backupFile + ";SET FOREIGN_KEY_CHECKS=1;COMMIT;SET AUTOCOMMIT = 1;\"");
                    }
                    else {
                        command.add("SET AUTOCOMMIT = 0;SET FOREIGN_KEY_CHECKS=0;source " + backupFile + ";SET FOREIGN_KEY_CHECKS=1;COMMIT;SET AUTOCOMMIT = 1;");
                    }
                    ConsoleOut.print(".");
                    try {
                        this.executeCommand(command);
                    }
                    catch (final Exception e3) {
                        throw new BackupRestoreException(RestoreErrors.PROBLEM_WHILE_EXECUTING_COMMAND, e3);
                    }
                    restoreResult.addTable(tableName2);
                }
            }
            ConsoleOut.println("");
            restoreStatus.setRestoreEndTime(System.currentTimeMillis());
            restoreResult.setEndTime(restoreStatus.getRestoreEndTime());
            restoreResult.calculateDuration();
            FileUtils.deleteDir(path);
            restoreStatus.setStatus(BackupRestoreConfigurations.RESTORE_STATUS.RESTORE_SUCCESSFULLY_COMPLETED);
            restoreResult.setRestoreStatus(restoreStatus.getStatus());
            this.sendRestoreNotification(restoreStatus);
        }
        catch (final BackupRestoreException e4) {
            restoreStatus.setStatus(BackupRestoreConfigurations.RESTORE_STATUS.RESTORE_PROCESS_FAILED);
            restoreResult.setRestoreStatus(restoreStatus.getStatus());
            this.sendRestoreNotification(restoreStatus);
            throw e4;
        }
        MysqlRestoreHandler.LOGGER.log(Level.INFO, "Exiting restoreBackup with restoreResult :: {0}", restoreResult.toString());
        return restoreResult;
    }
    
    @Override
    protected void postRestoreDB(final RestoreDBParams rdbp) {
    }
    
    static {
        LOGGER = Logger.getLogger(MysqlRestoreHandler.class.getName());
        OS = OSCheckUtil.getOS();
    }
}
