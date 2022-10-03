package com.adventnet.db.adapter.postgres;

import java.util.Hashtable;
import com.adventnet.ds.query.DataSet;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.db.api.RelationalAPI;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Date;
import java.util.Calendar;
import java.util.Locale;
import java.io.BufferedReader;
import java.io.FilenameFilter;
import com.zoho.framework.utils.FileNameFilter;
import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.io.FileInputStream;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import com.zoho.cp.LogicalConnection;
import com.adventnet.cp.WrappedConnection;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import com.adventnet.mfw.ConsoleOut;
import com.adventnet.db.adapter.BackupRestoreUtil;
import com.zoho.conf.AppResources;
import com.zoho.mickey.ha.HAUtil;
import com.adventnet.db.adapter.RestoreStatus;
import com.adventnet.persistence.PersistenceInitializer;
import com.adventnet.persistence.PersistenceException;
import com.zoho.mickey.exception.PasswordException;
import com.adventnet.persistence.PersistenceUtil;
import com.zoho.framework.utils.crypto.EnDecryptUtil;
import java.util.Arrays;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import com.zoho.conf.Configuration;
import java.io.IOException;
import java.util.Map;
import com.zoho.framework.utils.FileUtils;
import java.util.Properties;
import java.io.File;
import com.zoho.framework.utils.OSCheckUtil;
import com.adventnet.db.adapter.BackupRestoreException;
import com.adventnet.db.adapter.RestoreErrors;
import com.adventnet.db.adapter.BackupRestoreConfigurations;
import java.util.logging.Level;
import com.adventnet.db.adapter.RestoreResult;
import com.adventnet.db.adapter.RestoreDBParams;
import java.util.logging.Logger;
import com.adventnet.db.adapter.AbstractRestoreHandler;

public class PostgresRestoreHandler extends AbstractRestoreHandler
{
    private static final Logger LOGGER;
    public static final int OS;
    private static String configPassword;
    private boolean isPasswordModified;
    private static String server_home;
    
    public PostgresRestoreHandler() {
        this.isPasswordModified = false;
    }
    
    @Override
    protected RestoreResult restoreDBBackup(final RestoreDBParams rdbp) throws BackupRestoreException {
        PostgresRestoreHandler.LOGGER.log(Level.INFO, "Entered restoreBackup for [{0}]", rdbp.toString());
        final RestoreResult restoreResult = new RestoreResult(rdbp.getSourceFile());
        final RestoreStatus restoreStatus = AbstractRestoreHandler.getRestoreStatus(rdbp);
        restoreResult.setBackupContentType(BackupRestoreConfigurations.BACKUP_CONTENT_TYPE.BINARY);
        restoreStatus.setStatus(BackupRestoreConfigurations.RESTORE_STATUS.RESTORE_PROCESS_STARTED);
        restoreResult.setRestoreStatus(restoreStatus.getStatus());
        this.sendRestoreNotification(restoreStatus);
        Label_2506: {
            try {
                boolean isServerStarted = false;
                try {
                    isServerStarted = this.dbInitializer.isServerStarted();
                }
                catch (final Exception e) {
                    throw new BackupRestoreException(RestoreErrors.PROBLEM_CHECKING_DB_SERVER_STATUS, e);
                }
                if (isServerStarted) {
                    PostgresRestoreHandler.LOGGER.log(Level.SEVERE, "Postgres DB Server is already running, hence please stop the Database and then try restoring ...");
                    throw new BackupRestoreException(RestoreErrors.DATABASE_ALREADY_RUNNING);
                }
                final String serverHome = PostgresRestoreHandler.server_home;
                File f = null;
                if (OSCheckUtil.isWindows(PostgresRestoreHandler.OS)) {
                    f = new File(serverHome + File.separator + "bin" + File.separator + "change_datadir_perm.bat");
                }
                else {
                    f = new File(serverHome + File.separator + "bin" + File.separator + "change_datadir_perm.sh");
                }
                if (!f.exists()) {
                    PostgresRestoreHandler.LOGGER.log(Level.INFO, "change_datadir_perm script not exists at " + serverHome + File.separator + "bin");
                    throw new BackupRestoreException(RestoreErrors.CHANGE_PERMISSION_SCRIPT_NOT_FOUND);
                }
                final File tempDirectory = this.getTempBackupDirectory();
                final File postgresConfFile = new File(tempDirectory, "postgresql.conf");
                final File indexPropFile = new File(tempDirectory, "index.props");
                final boolean isIncrementalBackup = indexPropFile.exists();
                final boolean isFullBackup = postgresConfFile.exists();
                final String backupZipFolder = new File(rdbp.getSourceFile()).getParent();
                final Properties indexProps = new Properties();
                Label_0404: {
                    if (!isFullBackup) {
                        if (!isIncrementalBackup) {
                            break Label_0404;
                        }
                    }
                    Label_0426: {
                        try {
                            if (indexPropFile.exists()) {
                                indexProps.putAll(FileUtils.readPropertyFile(indexPropFile));
                            }
                            break Label_0426;
                        }
                        catch (final IOException ioe) {
                            throw new BackupRestoreException(RestoreErrors.PROBLEM_WHILE_CHECKING_FILE_IN_ZIP, ioe);
                        }
                        break Label_0404;
                    }
                    restoreStatus.setRestoreStartTime(System.currentTimeMillis());
                    restoreResult.setStartTime(restoreStatus.getRestoreStartTime());
                    final File newDataDir = new File(Configuration.getString("db.home") + File.separator + "data_new");
                    final List<String> excludeFiles = new ArrayList<String>();
                    excludeFiles.add("conf");
                    excludeFiles.add("PG_ARCH");
                    excludeFiles.add("dcinfo.json");
                    excludeFiles.add("index.props");
                    if (indexProps.getProperty("osname") != null) {
                        final String backupOS = indexProps.getProperty("osname");
                        final String currentOS = OSCheckUtil.getOSName();
                        PostgresRestoreHandler.LOGGER.log(Level.INFO, "Backup OS :: {0}", backupOS);
                        PostgresRestoreHandler.LOGGER.log(Level.INFO, "Restore OS :: {0}", currentOS);
                        if (!OSCheckUtil.getSimpleOSName(backupOS).equals(OSCheckUtil.getSimpleOSName(currentOS))) {
                            PostgresRestoreHandler.LOGGER.log(Level.SEVERE, "Backup is taken from a different operating system");
                            throw new BackupRestoreException(RestoreErrors.INCOMPATIBLE_BACKUP);
                        }
                    }
                    else {
                        PostgresRestoreHandler.LOGGER.log(Level.WARNING, "Backedup OS unknown");
                        PostgresRestoreHandler.LOGGER.log(Level.WARNING, "Restoring at risk");
                    }
                    if (indexProps.getProperty("dbversion") != null) {
                        final String backupVersion = indexProps.getProperty("dbversion");
                        PostgresRestoreHandler.LOGGER.log(Level.INFO, "Backup DB Version :: {0}", backupVersion);
                        String restoreVersion = null;
                        try {
                            restoreVersion = this.dbAdapter.getDBInitializer().getVersion();
                        }
                        catch (final Exception e2) {
                            throw new BackupRestoreException(RestoreErrors.PROBLEM_FETCHING_VERSION, e2);
                        }
                        PostgresRestoreHandler.LOGGER.log(Level.INFO, "Restore DB Version :: {0}", restoreVersion);
                        final String[] backupVersionSplit = backupVersion.split("\\.");
                        final String[] currentVersionSplit = restoreVersion.split("\\.");
                        if (Integer.parseInt(currentVersionSplit[0]) >= 10) {
                            if (Integer.parseInt(currentVersionSplit[0]) != Integer.parseInt(backupVersionSplit[0])) {
                                PostgresRestoreHandler.LOGGER.log(Level.SEVERE, "Backup is taken from a different database version");
                                throw new BackupRestoreException(RestoreErrors.INCOMPATIBLE_BACKUP);
                            }
                        }
                        else if (!new Float(backupVersionSplit[0] + "." + backupVersionSplit[1]).equals(new Float(currentVersionSplit[0] + "." + currentVersionSplit[1]))) {
                            PostgresRestoreHandler.LOGGER.log(Level.SEVERE, "Backup is taken from a different database version");
                            throw new BackupRestoreException(RestoreErrors.INCOMPATIBLE_BACKUP);
                        }
                    }
                    else {
                        PostgresRestoreHandler.LOGGER.log(Level.WARNING, "Backedup Version unknown");
                        PostgresRestoreHandler.LOGGER.log(Level.WARNING, "Restoring at risk");
                    }
                    final File archFile = new File(Configuration.getString("db.home") + File.separator + "bin" + File.separator + "PG_ARCH");
                    if (indexProps.getProperty("arch") != null && archFile.exists()) {
                        String arch = null;
                        try {
                            arch = new String(Files.readAllBytes(Paths.get(Configuration.getString("db.home") + File.separator + "bin" + File.separator + "PG_ARCH", new String[0]))).trim();
                        }
                        catch (final IOException e3) {
                            throw new BackupRestoreException(RestoreErrors.PROBLEM_READING_ARCHITECTURE_FILE, e3);
                        }
                        final String archProp = indexProps.getProperty("arch").trim();
                        PostgresRestoreHandler.LOGGER.log(Level.INFO, "Backup Architecture :: {0}", archProp);
                        PostgresRestoreHandler.LOGGER.log(Level.INFO, "Restore Architecture :: {0}", arch);
                        if (!arch.equals(archProp)) {
                            PostgresRestoreHandler.LOGGER.log(Level.SEVERE, "Backup is taken from a different architecture");
                            throw new BackupRestoreException(RestoreErrors.INCOMPATIBLE_BACKUP);
                        }
                    }
                    else if (!archFile.exists()) {
                        PostgresRestoreHandler.LOGGER.log(Level.WARNING, "PG_ARCH doesnot exist at :: {0}", archFile.getAbsolutePath());
                        throw new BackupRestoreException(RestoreErrors.ARCHITECTURE_FILE_NOT_FOUND);
                    }
                    if (isFullBackup) {
                        PostgresRestoreHandler.LOGGER.log(Level.INFO, "This is a FullBackupZip");
                        newDataDir.mkdir();
                        if (!this.isBackupCompatible(rdbp.getSourceFile(), rdbp.getResetMickeyType(), rdbp.getArchivePassword())) {
                            PostgresRestoreHandler.LOGGER.log(Level.SEVERE, "This backup is not compatible to this version of the product and hence it cannot be restored");
                            throw new BackupRestoreException(RestoreErrors.INCOMPATIBLE_BACKUP);
                        }
                        this.replaceUDTFiles(rdbp.getSourceFile(), rdbp.getArchivePassword());
                        restoreStatus.setStatus(BackupRestoreConfigurations.RESTORE_STATUS.GOING_TO_UNZIP_BACKUPZIP);
                        restoreResult.setRestoreStatus(restoreStatus.getStatus());
                        this.sendRestoreNotification(restoreStatus);
                        this.unZip(new File(rdbp.getSourceFile()), newDataDir, null, excludeFiles, rdbp.getArchivePassword());
                        restoreStatus.setStatus(BackupRestoreConfigurations.RESTORE_STATUS.UNZIP_BACKUPZIP_COMPLETED);
                        restoreResult.setRestoreStatus(restoreStatus.getStatus());
                        this.sendRestoreNotification(restoreStatus);
                    }
                    else {
                        PostgresRestoreHandler.LOGGER.log(Level.INFO, "This is an Incremental BackupZip");
                        final File fullBackupZipFile = new File(backupZipFolder, indexProps.getProperty("fullbackup_zipname"));
                        PostgresRestoreHandler.LOGGER.log(Level.INFO, "fullBackupZipFile :: [{0}], exists :: [{1}]", new Object[] { fullBackupZipFile, fullBackupZipFile.exists() });
                        String[] previousIncrBackupZipFileNames = null;
                        if (!indexProps.getProperty("previous_incr_backup_zipnames", "").equals("")) {
                            previousIncrBackupZipFileNames = indexProps.getProperty("previous_incr_backup_zipnames", "").split(",");
                            PostgresRestoreHandler.LOGGER.log(Level.INFO, "previousIncrBackupZipFileNames :: {0}", Arrays.toString(previousIncrBackupZipFileNames));
                        }
                        File[] previousIncrBackupZipFiles = null;
                        if (previousIncrBackupZipFileNames != null) {
                            previousIncrBackupZipFiles = new File[previousIncrBackupZipFileNames.length];
                            boolean allDependentZipsAvailable = true;
                            allDependentZipsAvailable = (allDependentZipsAvailable && fullBackupZipFile.exists());
                            PostgresRestoreHandler.LOGGER.log(Level.INFO, "allDependentZipsAvailable :: {0}", allDependentZipsAvailable);
                            PostgresRestoreHandler.LOGGER.log(Level.INFO, "length :: {0}", previousIncrBackupZipFileNames.length);
                            PostgresRestoreHandler.LOGGER.log(Level.INFO, "backupZipFolder :: {0}", backupZipFolder);
                            for (int index = 0; allDependentZipsAvailable && index < previousIncrBackupZipFileNames.length; allDependentZipsAvailable = (allDependentZipsAvailable && previousIncrBackupZipFiles[index].exists()), ++index) {
                                final String prevIncrBackupFileName = previousIncrBackupZipFileNames[index];
                                previousIncrBackupZipFiles[index] = new File(backupZipFolder, prevIncrBackupFileName);
                                PostgresRestoreHandler.LOGGER.log(Level.INFO, "index :: {0}, zipFile :: [{1}] exists() :: {2}", new Object[] { index, previousIncrBackupZipFileNames[index], previousIncrBackupZipFiles[index].exists() });
                            }
                            if (!allDependentZipsAvailable) {
                                PostgresRestoreHandler.LOGGER.log(Level.SEVERE, "Some/All of the dependent backup zips [" + fullBackupZipFile + ", " + Arrays.toString(previousIncrBackupZipFileNames) + "] are not found in the parent folder of this backupZipFileName :: [" + rdbp.getSourceFile() + "], hence it cannot be restored.");
                                throw new BackupRestoreException(RestoreErrors.MISSING_DEPENDENT_BACKUPS);
                            }
                        }
                        else if (!fullBackupZipFile.exists()) {
                            PostgresRestoreHandler.LOGGER.log(Level.SEVERE, "Full backup zip [" + fullBackupZipFile + "] is not found in the parent folder of this backupZipFileName :: [" + rdbp.getSourceFile() + "], hence it cannot be restored.");
                            throw new BackupRestoreException(RestoreErrors.MISSING_DEPENDENT_BACKUPS);
                        }
                        String version;
                        try {
                            version = this.dbInitializer.getVersion();
                            version = version.substring(0, version.lastIndexOf(46));
                        }
                        catch (final Exception e4) {
                            throw new BackupRestoreException(RestoreErrors.PROBLEM_FETCHING_VERSION, e4);
                        }
                        String walDir;
                        if (Float.valueOf(version) < 10.0f) {
                            walDir = "pg_xlog";
                        }
                        else {
                            walDir = "pg_wal";
                        }
                        final File newPgxlogDir = new File(System.getProperty("db.home") + File.separator + "data_new" + File.separator + walDir);
                        newDataDir.mkdirs();
                        if (!this.isBackupCompatible(fullBackupZipFile.toString(), rdbp.getResetMickeyType(), rdbp.getArchivePassword())) {
                            PostgresRestoreHandler.LOGGER.log(Level.SEVERE, "This backup is not compatible to this version of the product and hence it cannot be restored");
                            throw new BackupRestoreException(RestoreErrors.INCOMPATIBLE_BACKUP);
                        }
                        this.replaceUDTFiles(fullBackupZipFile.getAbsolutePath(), rdbp.getArchivePassword());
                        restoreStatus.setStatus(BackupRestoreConfigurations.RESTORE_STATUS.GOING_TO_UNZIP_BACKUPZIP);
                        restoreResult.setRestoreStatus(restoreStatus.getStatus());
                        this.sendRestoreNotification(restoreStatus);
                        this.unZip(new File(backupZipFolder, fullBackupZipFile.getName()), newDataDir, null, excludeFiles, rdbp.getArchivePassword());
                        final List<String> excludePropsFile = new ArrayList<String>();
                        excludePropsFile.add("index.props");
                        if (previousIncrBackupZipFiles != null) {
                            if (!newPgxlogDir.exists()) {
                                PostgresRestoreHandler.LOGGER.log(Level.SEVERE, "WAL directory is expected in [{0}]", newPgxlogDir);
                                throw new BackupRestoreException(RestoreErrors.WAL_DIRECTORY_MISSING);
                            }
                            for (final File prevIncrBackupZipFile : previousIncrBackupZipFiles) {
                                this.unZip(new File(backupZipFolder, prevIncrBackupZipFile.getName()), newPgxlogDir, null, excludePropsFile, rdbp.getArchivePassword());
                            }
                        }
                        this.unZip(new File(rdbp.getSourceFile()), newPgxlogDir, null, excludePropsFile, rdbp.getArchivePassword());
                        restoreStatus.setStatus(BackupRestoreConfigurations.RESTORE_STATUS.UNZIP_BACKUPZIP_COMPLETED);
                        restoreResult.setRestoreStatus(restoreStatus.getStatus());
                        this.sendRestoreNotification(restoreStatus);
                    }
                    try {
                        this.assignPermissionsForDataDir(newDataDir);
                    }
                    catch (final Exception e5) {
                        PostgresRestoreHandler.LOGGER.log(Level.SEVERE, "Problem assigning permission to data directory :: [{0}]", newDataDir);
                        throw new BackupRestoreException(RestoreErrors.PROBLEM_ASSIGNING_PERMISSION_TO_DATA_DIRECTORY, e5);
                    }
                    restoreStatus.setStatus(BackupRestoreConfigurations.RESTORE_STATUS.RENAMING_DATA_DIRECTORY);
                    restoreResult.setRestoreStatus(restoreStatus.getStatus());
                    this.sendRestoreNotification(restoreStatus);
                    final File dataDirRenamedTo = this.renameDataDir();
                    final File dataDir = new File(Configuration.getString("db.home") + File.separator + "data");
                    if (!newDataDir.renameTo(dataDir)) {
                        dataDirRenamedTo.renameTo(dataDir);
                    }
                    restoreStatus.setStatus(BackupRestoreConfigurations.RESTORE_STATUS.RESTORE_SUCCESSFULLY_COMPLETED);
                    restoreResult.setRestoreStatus(restoreStatus.getStatus());
                    this.sendRestoreNotification(restoreStatus);
                    restoreResult.setDataDirectory(dataDirRenamedTo);
                    restoreStatus.setRestoreEndTime(System.currentTimeMillis());
                    restoreResult.setEndTime(restoreStatus.getRestoreEndTime());
                    restoreResult.calculateDuration();
                    final Properties configProps = this.dbAdapter.getDBProps();
                    final String configPasswordinDBProps = configProps.getProperty("password", "");
                    String url = configProps.getProperty("url");
                    if (url.contains("?")) {
                        url = url.substring(0, url.indexOf("?"));
                    }
                    String backupPassword;
                    try {
                        EnDecryptUtil.setCryptTag(rdbp.getOldCryptTag());
                        backupPassword = PersistenceUtil.getDBPasswordProvider().getPassword(indexProps.getProperty("uid"));
                    }
                    catch (final PasswordException | PersistenceException e6) {
                        e6.printStackTrace();
                        throw new BackupRestoreException(RestoreErrors.PASSWORD_REQUIRED_FOR_ENCRYPTED_ZIP);
                    }
                    finally {
                        EnDecryptUtil.setCryptTag(PersistenceInitializer.getConfigurationValue("CryptTag"));
                    }
                    if (!backupPassword.equals(configPasswordinDBProps)) {
                        PostgresRestoreHandler.configPassword = configPasswordinDBProps;
                        configProps.setProperty("password", backupPassword);
                        this.dbAdapter.initialize(configProps);
                        this.isPasswordModified = true;
                    }
                    break Label_2506;
                }
                PostgresRestoreHandler.LOGGER.log(Level.SEVERE, "This is neither a Full nor a Incremental WAL Backup file, hence it cannot be restored using PostgresRestoreHandler");
                throw new BackupRestoreException(RestoreErrors.UNSUPPORTED_RESTORE_TYPE);
            }
            catch (final BackupRestoreException e7) {
                restoreStatus.setStatus(BackupRestoreConfigurations.RESTORE_STATUS.RESTORE_PROCESS_FAILED);
                restoreResult.setRestoreStatus(restoreStatus.getStatus());
                this.sendRestoreNotification(restoreStatus);
                throw e7;
            }
        }
        PostgresRestoreHandler.LOGGER.log(Level.INFO, "Exiting restoreBackup with restoreResult :: {0}", restoreResult.toString());
        return restoreResult;
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
            ((DefaultPostgresDBInitializer)this.dbInitializer).checkForPgIsReadybinary();
        }
        catch (final IOException e2) {
            e2.printStackTrace();
        }
        try {
            final File tempDirectory = this.getTempBackupDirectory();
            final File indexPropFile = new File(tempDirectory, "index.props");
            final File postgresConfFile = new File(tempDirectory, "postgresql.props");
            final File backuprestoreFile = new File(tempDirectory, "backuprestore.conf");
            if (indexPropFile.exists() || postgresConfFile.exists()) {
                if (HAUtil.isDataBaseHAEnabled()) {
                    throw new UnsupportedOperationException("Restore is not supported when HA is enabled");
                }
                rdbp.setRestoreBackupContentType(BackupRestoreConfigurations.BACKUP_CONTENT_TYPE.BINARY);
                rdbp.setStartDBMode(BackupRestoreConfigurations.DB_START_MODE.POST_START_DB);
                rdbp.setRestoreBackupMode(null);
                this.setOldCryptTag(indexPropFile, rdbp);
                rdbp.setInitializePersistenceType(BackupRestoreConfigurations.INIT_PERSISTENCE.AFTER_RESTORE);
                if (!Boolean.valueOf(AppResources.getString("force.restore", "false"))) {
                    rdbp.setResetMickeyType(BackupRestoreConfigurations.RESET_MICKEY.RESET);
                    try {
                        final boolean isDBStarted = this.dbAdapter.startDB(dbProps.getProperty("url"), dbProps.getProperty("username"), dbProps.getProperty("password"));
                        final Map<?, ?> urlProps = this.dbAdapter.splitConnectionURL(dbProps.getProperty("url"));
                        final int port = (int)urlProps.get("Port");
                        final String host = (String)urlProps.get("Server");
                        final boolean isDBReady = ((DefaultPostgresDBInitializer)this.dbInitializer).isDBReadyToAcceptConnection(port, host, dbProps.getProperty("username"), dbProps.getProperty("password"));
                        if (isDBStarted && isDBReady) {
                            PersistenceInitializer.checkAndPrepareDatabase(this.dbAdapter, this.dataSource);
                            PersistenceInitializer.initializeMickey(true);
                            try {
                                final String newDCInfoLocation = BackupRestoreUtil.getDynamicColumnsInfoFileLocation(Configuration.getString("server.home") + File.separator + "bin");
                                PostgresRestoreHandler.LOGGER.log(Level.INFO, "New DC File Location :: {0}", newDCInfoLocation);
                            }
                            catch (final Exception e3) {
                                throw new BackupRestoreException(RestoreErrors.PROBLEM_GENERATING_DYNAMIC_COLUMN_FILE, e3);
                            }
                        }
                    }
                    finally {
                        this.dbAdapter.stopDB(dbProps.getProperty("url"), dbProps.getProperty("username"), dbProps.getProperty("password"));
                        this.resetDataSource();
                    }
                }
                else {
                    rdbp.setResetMickeyType(BackupRestoreConfigurations.RESET_MICKEY.NOT_APPLICABLE);
                }
            }
            else if (!backuprestoreFile.exists()) {
                rdbp.setRestoreBackupMode(BackupRestoreConfigurations.BACKUP_MODE.FILE_BACKUP);
            }
            else {
                rdbp.setRestoreBackupContentType(BackupRestoreConfigurations.BACKUP_CONTENT_TYPE.DUMP);
                rdbp.setRestoreBackupMode(null);
                rdbp.setStartDBMode(BackupRestoreConfigurations.DB_START_MODE.PRE_START_DB);
                rdbp.setResetMickeyType(BackupRestoreConfigurations.RESET_MICKEY.NOT_APPLICABLE);
                this.setOldCryptTag(backuprestoreFile, rdbp);
                rdbp.setInitializePersistenceType(BackupRestoreConfigurations.INIT_PERSISTENCE.BEFORE_RESTORE);
                final boolean startDB = Boolean.parseBoolean(PersistenceInitializer.getConfigurationValue("StartDBServer"));
                if (startDB) {
                    final boolean isDBStarted2 = this.dbAdapter.startDB(dbProps.getProperty("url"), dbProps.getProperty("username"), dbProps.getProperty("password"));
                    final Map<?, ?> urlProps2 = this.dbAdapter.splitConnectionURL(dbProps.getProperty("url"));
                    final int port2 = (int)urlProps2.get("Port");
                    final String host2 = (String)urlProps2.get("Server");
                    final boolean isDBReady2 = ((DefaultPostgresDBInitializer)this.dbInitializer).isDBReadyToAcceptConnection(port2, host2, dbProps.getProperty("username"), dbProps.getProperty("password"));
                    if (isDBStarted2 && isDBReady2) {
                        this.dbAdapter.createDB(dbProps.getProperty("url"), dbProps.getProperty("username"), dbProps.getProperty("password"));
                    }
                    rdbp.requiresDBStop(true);
                }
                else if (this.dbInitializer.isServerStarted()) {
                    this.dbAdapter.createDB(dbProps.getProperty("url"), dbProps.getProperty("username"), dbProps.getProperty("password"));
                }
            }
        }
        catch (final Exception e4) {
            e4.printStackTrace();
            throw new BackupRestoreException(RestoreErrors.PROBLEM_PRE_RESTORE_DATABASE, e4);
        }
        return rdbp;
    }
    
    @Deprecated
    @Override
    protected RestoreResult restoreTableBackup(final RestoreDBParams rdbp) throws BackupRestoreException {
        PostgresRestoreHandler.LOGGER.log(Level.INFO, "Entered restoreBackup for [{0}]", rdbp.toString());
        final RestoreResult restoreResult = new RestoreResult(rdbp.getSourceFile());
        final RestoreStatus restoreStatus = AbstractRestoreHandler.getRestoreStatus(rdbp);
        restoreResult.setBackupMode(rdbp.getRestoreBackupMode());
        restoreResult.setBackupContentType(BackupRestoreConfigurations.BACKUP_CONTENT_TYPE.DUMP);
        restoreStatus.setStatus(BackupRestoreConfigurations.RESTORE_STATUS.RESTORE_PROCESS_STARTED);
        restoreResult.setRestoreStatus(restoreStatus.getStatus());
        this.sendRestoreNotification(restoreStatus);
        final Properties restoreProperties = new Properties();
        final String zipFile = rdbp.getSourceFile();
        final String path = zipFile.substring(0, zipFile.length() - 4);
        try {
            if (!this.isCompatibleVersion(zipFile, restoreProperties, rdbp.getArchivePassword())) {
                PostgresRestoreHandler.LOGGER.log(Level.SEVERE, "Incompatible zip file version :: {0}", zipFile);
                throw new BackupRestoreException(RestoreErrors.INCOMPATIBLE_BACKUP);
            }
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
            PostgresRestoreHandler.LOGGER.log(Level.INFO, "Restore DB Started in RestoreHandler");
            final boolean isWindows = OSCheckUtil.isWindows(PostgresRestoreHandler.OS);
            final Properties getDBProperties = this.dbAdapter.getDBProps();
            final String username = getDBProperties.getProperty("username");
            final String password = getDBProperties.getProperty("password", "");
            String url = getDBProperties.getProperty("url");
            if (url.contains("?")) {
                url = url.substring(0, url.indexOf("?"));
            }
            final String hostName = ((Hashtable<K, String>)getDBProperties).get("Server");
            final Integer port = ((Hashtable<K, Integer>)getDBProperties).get("Port");
            final String database = url.substring(url.lastIndexOf("/") + 1, url.length());
            Connection con = null;
            Statement stmt = null;
            try {
                ConsoleOut.print("Dropping all tables :: ");
                con = this.dataSource.getConnection();
                stmt = con.createStatement();
                ((PostgresDBAdapter)this.dbAdapter).disableForeignKeyChecks(stmt);
                for (int i = 0; i < restoreProperties.size(); ++i) {
                    final String tableName = restoreProperties.getProperty("table" + i);
                    if (tableName != null) {
                        try {
                            final String dropSQL = this.sqlGenerator.getSQLForDrop(tableName, true);
                            this.dbAdapter.execute(stmt, dropSQL);
                            ConsoleOut.print(".");
                            PostgresRestoreHandler.LOGGER.log(Level.INFO, "Dropped table " + tableName);
                        }
                        catch (final SQLException sqle) {
                            PostgresRestoreHandler.LOGGER.warning("Error while droping table " + tableName);
                            sqle.printStackTrace();
                        }
                    }
                }
            }
            catch (final Exception e) {
                PostgresRestoreHandler.LOGGER.log(Level.INFO, "Problem while Reinitializing the DB.");
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
            ConsoleOut.println("Restoring ...");
            restoreStatus.setRestoreStartTime(System.currentTimeMillis());
            restoreResult.setStartTime(restoreStatus.getRestoreStartTime());
            restoreStatus.setStatus(BackupRestoreConfigurations.RESTORE_STATUS.RESTORE_PROCESS_IN_PROGRESS);
            restoreResult.setRestoreStatus(restoreStatus.getStatus());
            this.sendRestoreNotification(restoreStatus);
            if (new File(file.getAbsolutePath() + File.separator + "table_create.sql").exists()) {
                restoreResult.setTables(this.restoreUsingCopyManager(restoreProperties, file, isWindows, port, hostName, username, password, database));
            }
            else {
                restoreResult.setTables(this.restoreUsingPgDump(restoreProperties, isWindows, file, port, hostName, username, password, database));
            }
            restoreStatus.setRestoreEndTime(System.currentTimeMillis());
            restoreResult.setEndTime(restoreStatus.getRestoreEndTime());
            restoreResult.calculateDuration();
            restoreStatus.setStatus(BackupRestoreConfigurations.RESTORE_STATUS.RESTORE_SUCCESSFULLY_COMPLETED);
            restoreResult.setRestoreStatus(restoreStatus.getStatus());
            this.sendRestoreNotification(restoreStatus);
        }
        catch (final BackupRestoreException e3) {
            restoreStatus.setStatus(BackupRestoreConfigurations.RESTORE_STATUS.RESTORE_PROCESS_FAILED);
            restoreResult.setRestoreStatus(restoreStatus.getStatus());
            this.sendRestoreNotification(restoreStatus);
            throw e3;
        }
        finally {
            FileUtils.deleteDir(path);
        }
        PostgresRestoreHandler.LOGGER.log(Level.INFO, "Exiting restoreBackup with restoreResult :: {0}", restoreResult.toString());
        return restoreResult;
    }
    
    private List<String> restoreUsingCopyManager(final Properties restoreProperties, final File file, final boolean isWindows, final int port, final String hostName, final String username, final String password, final String database) throws BackupRestoreException {
        final List<String> tablesRestored = new ArrayList<String>();
        Connection conn = null;
        try {
            conn = this.dataSource.getConnection();
            BaseConnection baseConnection = null;
            if (conn instanceof WrappedConnection) {
                final LogicalConnection logicalConnection = (LogicalConnection)((WrappedConnection)conn).getConnection(0);
                baseConnection = (BaseConnection)logicalConnection.getPhysicalConnection();
            }
            else {
                final LogicalConnection logicalConnection = (LogicalConnection)conn;
                baseConnection = (BaseConnection)logicalConnection.getPhysicalConnection();
            }
            final CopyManager copyManager = new CopyManager(baseConnection);
            ConsoleOut.println("Creating Tables : table_create.sql");
            PostgresRestoreHandler.LOGGER.log(Level.INFO, "Creating Tables :: {0}", "table_create.sql");
            this.processSQLForRestore(file, isWindows, port, hostName, username, password, database, "table_create", true);
            for (int i = 0; i < restoreProperties.size(); ++i) {
                final String tableName = restoreProperties.getProperty("table" + i);
                if (tableName == null) {
                    PostgresRestoreHandler.LOGGER.severe("TableName cannot be null");
                }
                else {
                    ConsoleOut.println("Restoring table : " + tableName);
                    PostgresRestoreHandler.LOGGER.log(Level.INFO, "Restoring table :: {0}", tableName);
                    tablesRestored.add(tableName);
                    InputStreamReader freader = null;
                    try {
                        String dataFileName = file.getCanonicalPath() + File.separator + tableName + ".txt";
                        dataFileName = dataFileName.replace("\"", "");
                        freader = new InputStreamReader(new FileInputStream(new File(dataFileName)), StandardCharsets.UTF_8);
                        copyManager.copyIn("COPY " + tableName + " FROM STDIN;", (Reader)freader);
                    }
                    catch (final Exception e) {
                        throw new BackupRestoreException(RestoreErrors.PROBLEM_WHILE_EXECUTING_COMMAND, e);
                    }
                    finally {
                        try {
                            if (freader != null) {
                                freader.close();
                            }
                        }
                        catch (final Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
            ConsoleOut.println("Adding Constrains : table_constrains.sql");
            PostgresRestoreHandler.LOGGER.log(Level.INFO, "Adding Constrains :: {0}", "table_constrains.sql");
            this.processSQLForRestore(file, isWindows, port, hostName, username, password, database, "table_constrains", true);
            final File[] sequenceFiles = file.listFiles((FilenameFilter)new FileNameFilter("sequences", ".sql"));
            PostgresRestoreHandler.LOGGER.log(Level.INFO, "Sequence Files :: {0}", sequenceFiles);
            if (sequenceFiles != null) {
                for (final File sequenceFile : sequenceFiles) {
                    ConsoleOut.println("Updating Sequences : sequences.sql");
                    PostgresRestoreHandler.LOGGER.log(Level.INFO, "Updating Sequences :: {0}", "sequences.sql");
                    this.processSQLForRestore(file, isWindows, port, hostName, username, password, database, sequenceFile.getName().substring(0, sequenceFile.getName().lastIndexOf(".sql")), true);
                }
            }
        }
        catch (final SQLException e2) {
            throw new BackupRestoreException(RestoreErrors.PROBLEM_FETCHING_CONNECTION, e2);
        }
        finally {
            if (conn != null) {
                try {
                    conn.close();
                }
                catch (final SQLException e3) {
                    e3.printStackTrace();
                }
            }
        }
        PostgresRestoreHandler.LOGGER.log(Level.INFO, "Exiting restoreBackup");
        return tablesRestored;
    }
    
    @Deprecated
    private List<String> restoreUsingPgDump(final Properties restoreProperties, final boolean isWindows, final File file, final int port, final String hostName, final String username, final String password, final String database) throws BackupRestoreException {
        final List<String> tablesRestored = new ArrayList<String>();
        try {
            BufferedReader errStreamBuff = null;
            for (int i = 0; i < restoreProperties.size(); ++i) {
                final String tableName = restoreProperties.getProperty("table" + i);
                if (tableName == null) {
                    PostgresRestoreHandler.LOGGER.severe("TableName cannot be null");
                }
                else {
                    ConsoleOut.println("Restoring table : " + tableName);
                    PostgresRestoreHandler.LOGGER.log(Level.INFO, "Restoring table :: {0}", tableName);
                    tablesRestored.add(tableName);
                    String tableFileName = file.getCanonicalPath() + File.separator + tableName + ".sql";
                    if (isWindows) {
                        tableFileName = tableFileName.replace(File.separator, "/");
                    }
                    final Process process = ((DefaultPostgresDBInitializer)this.dbInitializer).executeCommand(port, hostName, username, password, database, "\\i '" + tableFileName + "'");
                    errStreamBuff = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    String errStreamString = null;
                    while ((errStreamString = errStreamBuff.readLine()) != null) {
                        PostgresRestoreHandler.LOGGER.warning(errStreamString);
                        errStreamString = errStreamString.toLowerCase(Locale.ENGLISH);
                        if (errStreamString != null && !errStreamString.contains(" ignored.")) {
                            PostgresRestoreHandler.LOGGER.log(Level.SEVERE, "Error occured while restoring " + tableName + " table :: ErrorStream message ::: " + errStreamString);
                            throw new BackupRestoreException(RestoreErrors.PROBLEM_WHILE_EXECUTING_COMMAND);
                        }
                    }
                }
            }
        }
        catch (final IOException e) {
            throw new BackupRestoreException(RestoreErrors.PROBLEM_WHILE_EXECUTING_COMMAND, e);
        }
        return tablesRestored;
    }
    
    @Override
    protected void postRestoreDB(final RestoreDBParams rdbp) {
        final String dbParamsFileName = PersistenceInitializer.getDBParamsFilePath();
        Properties dbProps = null;
        try {
            dbProps = PersistenceInitializer.getDBProps(dbParamsFileName);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        if (rdbp.getRestoreBackupContentType() == BackupRestoreConfigurations.BACKUP_CONTENT_TYPE.BINARY) {
            try {
                final boolean startDB = Boolean.parseBoolean(PersistenceInitializer.getConfigurationValue("StartDBServer"));
                if (startDB) {
                    this.dbAdapter.startDB(dbProps.getProperty("url"), dbProps.getProperty("username"), dbProps.getProperty("password"));
                    rdbp.requiresDBStop(true);
                    this.resetDataSource();
                }
            }
            catch (final Exception e) {
                PostgresRestoreHandler.LOGGER.log(Level.SEVERE, "Problem while starting DB");
                e.printStackTrace();
            }
        }
    }
    
    private void assignPermissionsForDataDir(final File newDataDir) throws IOException, InterruptedException, BackupRestoreException {
        final List<String> commandList = new ArrayList<String>();
        final String serverHome = PostgresRestoreHandler.server_home;
        if (OSCheckUtil.isWindows(PostgresRestoreHandler.OS)) {
            commandList.add(serverHome + File.separator + "bin" + File.separator + "change_datadir_perm.bat");
        }
        else {
            commandList.add("/bin/sh");
            final String fileName = serverHome + File.separator + "bin" + File.separator + "change_datadir_perm.sh";
            FileUtils.changePermissionForFile(fileName);
            final File changePermScriptFile = new File(fileName);
            commandList.add(changePermScriptFile.getAbsolutePath());
        }
        commandList.add(newDataDir.getAbsolutePath());
        PostgresRestoreHandler.LOGGER.log(Level.INFO, "{0}", commandList);
        this.executeCommand(commandList, null, null);
    }
    
    private File renameDataDir() throws BackupRestoreException {
        final File dataDir = new File(Configuration.getString("db.home") + File.separator + "data");
        if (!dataDir.exists()) {
            PostgresRestoreHandler.LOGGER.log(Level.SEVERE, "old data folder not found. Try calling initPgsql script and try restoring again");
            throw new BackupRestoreException(RestoreErrors.DATA_DIRECTORY_DOES_NOT_EXIST);
        }
        final Date currDate = Calendar.getInstance().getTime();
        final String currentTime = String.valueOf(currDate.getYear() + 1900) + String.valueOf(currDate.getMonth() + 1) + String.valueOf(currDate.getDate()) + String.valueOf(currDate.getHours()) + String.valueOf(currDate.getMinutes()) + String.valueOf(currDate.getSeconds());
        final File renameToFile = new File(dataDir + "_" + currentTime);
        final boolean renameSuccess = FileUtils.moveDirectoryWithRetry(dataDir, renameToFile);
        if (!renameSuccess) {
            PostgresRestoreHandler.LOGGER.log(Level.SEVERE, "Renaming operation failed. Please check whether the current data folder is being used anywhere and if yes please close and try the restoration again.");
            throw new BackupRestoreException(RestoreErrors.PROBLEM_RENAMING_DATA_DIRECTORY);
        }
        PostgresRestoreHandler.LOGGER.log(Level.INFO, "dataDir renamed to {0}", renameToFile);
        return renameToFile;
    }
    
    protected void processSQLForRestore(final File file, final boolean isWindows, final Integer port, final String hostName, final String username, final String password, final String database, final String fileName, final boolean waitFor) throws BackupRestoreException {
        final AtomicBoolean errflag = new AtomicBoolean();
        final List<String> errMsg = new ArrayList<String>();
        errMsg.add(" ignored.");
        errMsg.add("error:");
        errMsg.add("fatal:");
        try {
            String sqlFileName = file.getCanonicalPath() + File.separator + fileName + ".sql";
            PostgresRestoreHandler.LOGGER.log(Level.INFO, "sqlFileName ::: {0}", sqlFileName);
            if (isWindows) {
                sqlFileName = sqlFileName.replace(File.separator, "/");
            }
            final Process process = ((DefaultPostgresDBInitializer)this.dbInitializer).executeCommand(port, hostName, username, password, database, "\\i '" + sqlFileName + "'");
            if (waitFor) {
                this.dbInitializer.dump(process, Logger.getLogger("PSQL OUTPUT"), errMsg, errflag);
                process.waitFor();
                if (errflag.get()) {
                    PostgresRestoreHandler.LOGGER.log(Level.SEVERE, "Error occured while Restoring " + fileName + " schema");
                    throw new BackupRestoreException(RestoreErrors.PROBLEM_WHILE_EXECUTING_COMMAND);
                }
            }
        }
        catch (final IOException e) {
            throw new BackupRestoreException(RestoreErrors.PROBLEM_WHILE_EXECUTING_COMMAND, e);
        }
        catch (final InterruptedException e2) {
            throw new BackupRestoreException(RestoreErrors.PROBLEM_WHILE_EXECUTING_COMMAND, e2);
        }
    }
    
    @Override
    protected void resetPassword() throws BackupRestoreException {
        if (PostgresRestoreHandler.configPassword == null || !this.isPasswordModified) {
            PostgresRestoreHandler.LOGGER.log(Level.INFO, "Password change not required");
            return;
        }
        String version;
        try {
            version = this.dbInitializer.getVersion();
            version = version.substring(0, version.lastIndexOf(46));
        }
        catch (final Exception e) {
            throw new BackupRestoreException(RestoreErrors.PROBLEM_FETCHING_VERSION, e);
        }
        try (final Connection conn = this.dataSource.getConnection()) {
            if (Float.valueOf(version) < 10.0f) {
                final String query = "ALTER USER " + this.dbAdapter.getDBProps().getProperty("username") + " WITH ENCRYPTED PASSWORD '" + PostgresRestoreHandler.configPassword + "'";
                PostgresRestoreHandler.LOGGER.log(Level.FINE, "Going to execute statement :: [{0}]", new Object[] { query.replace(PostgresRestoreHandler.configPassword, "**********") });
                try (final Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(query);
                }
                PostgresRestoreHandler.LOGGER.log(Level.FINE, "Executed statement :: [{0}]", new Object[] { query.replace(PostgresRestoreHandler.configPassword, "**********") });
            }
            else {
                final String query = "SELECT change_password('" + PostgresRestoreHandler.configPassword + "'::TEXT)";
                PostgresRestoreHandler.LOGGER.log(Level.FINE, "Going to execute statement :: [{0}]", new Object[] { query.replace(PostgresRestoreHandler.configPassword, "**********") });
                try (final DataSet dataSet = RelationalAPI.getInstance().executeQuery(query, conn)) {
                    dataSet.next();
                    final boolean isPasswordChanged = dataSet.getAsBoolean(1);
                    if (!isPasswordChanged) {
                        throw new BackupRestoreException(RestoreErrors.PROBLEM_RESETTING_DB_SERVER_PASSWORD);
                    }
                }
                PostgresRestoreHandler.LOGGER.log(Level.FINE, "Executed statement :: [{0}]", new Object[] { query.replace(PostgresRestoreHandler.configPassword, "**********") });
            }
            final Properties dbProps = this.dbAdapter.getDBProps();
            dbProps.setProperty("password", PostgresRestoreHandler.configPassword);
            this.dbAdapter.initialize(dbProps);
            this.resetDataSource();
        }
        catch (final QueryConstructionException | SQLException e) {
            PostgresRestoreHandler.LOGGER.log(Level.SEVERE, "Problem while setting new password. Restoring old password");
            throw new BackupRestoreException(RestoreErrors.PROBLEM_RESETTING_DB_SERVER_PASSWORD, e);
        }
    }
    
    static {
        LOGGER = Logger.getLogger(PostgresRestoreHandler.class.getName());
        OS = OSCheckUtil.getOS();
        PostgresRestoreHandler.configPassword = null;
        PostgresRestoreHandler.server_home = ((Configuration.getString("server.home", "..") != null) ? Configuration.getString("server.home", "..") : Configuration.getString("app.home", ".."));
    }
}
