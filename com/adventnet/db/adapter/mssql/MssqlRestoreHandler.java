package com.adventnet.db.adapter.mssql;

import com.adventnet.ds.query.DataSet;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.QueryConstructionException;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.sql.Connection;
import com.adventnet.db.adapter.RestoreStatus;
import java.util.HashMap;
import java.sql.SQLException;
import java.util.ArrayList;
import java.io.IOException;
import com.zoho.framework.utils.FileUtils;
import com.adventnet.db.adapter.RestoreResult;
import com.adventnet.db.adapter.BackupRestoreUtil;
import com.zoho.conf.Configuration;
import com.adventnet.persistence.PersistenceInitializer;
import com.zoho.conf.AppResources;
import com.adventnet.db.adapter.BackupRestoreException;
import com.adventnet.db.adapter.RestoreErrors;
import java.util.logging.Level;
import com.adventnet.db.adapter.BackupRestoreConfigurations;
import java.io.File;
import com.adventnet.db.adapter.RestoreDBParams;
import java.util.logging.Logger;
import com.adventnet.db.adapter.AbstractRestoreHandler;

public class MssqlRestoreHandler extends AbstractRestoreHandler
{
    private static final Logger LOGGER;
    private String database;
    
    public MssqlRestoreHandler() {
        this.database = null;
    }
    
    @Override
    protected RestoreDBParams preRestoreDB(final String src, final String password) throws BackupRestoreException {
        final RestoreDBParams rdbp = super.preRestoreDB(src, password);
        try {
            this.database = ((MssqlDBInitializer)this.dbInitializer).getDBName();
            final File tempDirectory = this.getTempBackupDirectory();
            final File fullIndexPropFile = new File(tempDirectory, "full_index.props");
            final File incrementalIndexPropFile = new File(tempDirectory, "incremental_index.props");
            if (fullIndexPropFile.exists()) {
                rdbp.setRestoreBackupContentType(BackupRestoreConfigurations.BACKUP_CONTENT_TYPE.BINARY);
                rdbp.setRestoreBackupMode(null);
                this.setOldCryptTag(fullIndexPropFile, rdbp);
            }
            else {
                if (!incrementalIndexPropFile.exists()) {
                    MssqlRestoreHandler.LOGGER.log(Level.SEVERE, "The given file dosent contain both full_index.props or incremental_index.props, so it is not a valid zip to restore from");
                    throw new BackupRestoreException(RestoreErrors.UNSUPPORTED_RESTORE_TYPE);
                }
                rdbp.setRestoreBackupContentType(BackupRestoreConfigurations.BACKUP_CONTENT_TYPE.BINARY);
                rdbp.setRestoreBackupMode(BackupRestoreConfigurations.BACKUP_MODE.ONLINE_BACKUP);
                this.setOldCryptTag(incrementalIndexPropFile, rdbp);
            }
            if (!AppResources.getBoolean("force.restore", Boolean.valueOf(false))) {
                rdbp.setResetMickeyType(BackupRestoreConfigurations.RESET_MICKEY.RESET);
                PersistenceInitializer.initializeMickey(true);
                try {
                    final String newDCInfoLocation = BackupRestoreUtil.getDynamicColumnsInfoFileLocation(Configuration.getString("server.home") + File.separator + "bin");
                    MssqlRestoreHandler.LOGGER.log(Level.INFO, "New DC File Location :: {0}", newDCInfoLocation);
                    return rdbp;
                }
                catch (final Exception e) {
                    throw new BackupRestoreException(RestoreErrors.PROBLEM_GENERATING_DYNAMIC_COLUMN_FILE, e);
                }
            }
            rdbp.setResetMickeyType(BackupRestoreConfigurations.RESET_MICKEY.NOT_APPLICABLE);
        }
        catch (final BackupRestoreException e2) {
            throw e2;
        }
        catch (final Exception e3) {
            throw new BackupRestoreException(RestoreErrors.PROBLEM_PRE_RESTORE_DATABASE, e3);
        }
        return rdbp;
    }
    
    @Override
    protected void postRestoreDB(final RestoreDBParams rdbp) {
        try {
            this.resetDataSource();
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    protected RestoreResult restoreDBBackup(final RestoreDBParams rdbp) throws BackupRestoreException {
        MssqlRestoreHandler.LOGGER.log(Level.INFO, "Entered restoreBackup for [{0}]", rdbp.toString());
        boolean isMultiUserMode = false;
        final String zipFile = rdbp.getSourceFile();
        final RestoreResult restoreResult = new RestoreResult(zipFile);
        final RestoreStatus restoreStatus = AbstractRestoreHandler.getRestoreStatus(rdbp);
        restoreResult.setBackupContentType(BackupRestoreConfigurations.BACKUP_CONTENT_TYPE.BINARY);
        restoreStatus.setStatus(BackupRestoreConfigurations.RESTORE_STATUS.RESTORE_PROCESS_STARTED);
        restoreResult.setRestoreStatus(restoreStatus.getStatus());
        this.sendRestoreNotification(restoreStatus);
        Connection conn = null;
        try {
            try {
                conn = this.dataSource.getConnection();
                final String backupZipFolder = zipFile.substring(0, zipFile.lastIndexOf(File.separator));
                restoreStatus.setRestoreStartTime(System.currentTimeMillis());
                restoreResult.setStartTime(restoreStatus.getRestoreStartTime());
                restoreStatus.setStatus(BackupRestoreConfigurations.RESTORE_STATUS.GOING_TO_UNZIP_BACKUPZIP);
                restoreResult.setRestoreStatus(restoreStatus.getStatus());
                this.sendRestoreNotification(restoreStatus);
                String restoreFile = null;
                boolean isFullBackup = true;
                File fullBackupZipFile = new File(zipFile);
                Properties prop;
                try {
                    final File tempDirectory = this.getTempBackupDirectory();
                    final File fullIndexFile = new File(tempDirectory, "full_index.props");
                    isFullBackup = fullIndexFile.exists();
                    prop = FileUtils.readPropertyFile(isFullBackup ? fullIndexFile : new File(tempDirectory, "incremental_index.props"));
                }
                catch (final IOException e) {
                    throw new BackupRestoreException(RestoreErrors.PROBLEM_WHILE_CHECKING_FILE_IN_ZIP, e);
                }
                restoreFile = this.getBAKFile(zipFile, prop, rdbp);
                final List<String> restoreFiles = new ArrayList<String>(2);
                restoreFiles.add(restoreFile);
                if (!isFullBackup) {
                    MssqlRestoreHandler.LOGGER.log(Level.INFO, "This is incremental backup.");
                    final String lastFullBackupZip = prop.getProperty("fullbackup_zipname");
                    fullBackupZipFile = new File(backupZipFolder, lastFullBackupZip);
                    if (!fullBackupZipFile.exists()) {
                        MssqlRestoreHandler.LOGGER.log(Level.SEVERE, "Full Backup Zip Cant be found in the location " + fullBackupZipFile.getAbsolutePath());
                        throw new BackupRestoreException(RestoreErrors.MISSING_DEPENDENT_BACKUPS);
                    }
                    final Properties fullBakProps = this.readProperty(fullBackupZipFile.getAbsolutePath(), "full_index.props", rdbp.getArchivePassword());
                    restoreFiles.add(0, this.getBAKFile(lastFullBackupZip, fullBakProps, rdbp));
                }
                else {
                    MssqlRestoreHandler.LOGGER.log(Level.INFO, "This is full backup.");
                }
                restoreStatus.setStatus(BackupRestoreConfigurations.RESTORE_STATUS.UNZIP_BACKUPZIP_COMPLETED);
                restoreResult.setRestoreStatus(restoreStatus.getStatus());
                this.sendRestoreNotification(restoreStatus);
                this.checkSanityOfTheBackupFiles(conn, restoreFiles);
                String version = null;
                try {
                    version = this.dbInitializer.getVersion();
                }
                catch (final Exception e2) {
                    throw new BackupRestoreException(RestoreErrors.PROBLEM_FETCHING_VERSION, e2);
                }
                if (!this.isDBVersionCompatable(version, prop.getProperty("dbversion", null))) {
                    MssqlRestoreHandler.LOGGER.log(Level.SEVERE, "DB Version incompatable");
                    throw new BackupRestoreException(RestoreErrors.INCOMPATIBLE_BACKUP);
                }
                prop.clear();
                if (!this.isBackupCompatible(fullBackupZipFile.getAbsolutePath(), rdbp.getResetMickeyType(), rdbp.getArchivePassword())) {
                    MssqlRestoreHandler.LOGGER.log(Level.SEVERE, "This backup is not compatible to this version of the product and hence it cannot be restored");
                    throw new BackupRestoreException(RestoreErrors.INCOMPATIBLE_BACKUP);
                }
                this.replaceUDTFiles(fullBackupZipFile.getAbsolutePath(), rdbp.getArchivePassword());
                final String[] locToStoreFileTemp = this.getLocationOfDatabaseFiles(conn, version);
                MssqlRestoreHandler.LOGGER.log(Level.INFO, "Preparing for restore");
                try {
                    if (((MssqlDBAdapter)this.dbAdapter).hasPermissionForAlterDatabase(conn)) {
                        isMultiUserMode = this.isDatabaseInMultiUserMode(conn);
                    }
                    else {
                        MssqlRestoreHandler.LOGGER.log(Level.INFO, "No permission for DDL command on database.");
                    }
                }
                catch (final Exception e3) {
                    throw new BackupRestoreException(RestoreErrors.PROBLEM_SWITCHING_DB_MODE, e3);
                }
                try {
                    this.useMasterDB(conn);
                }
                catch (final SQLException e4) {
                    MssqlRestoreHandler.LOGGER.log(Level.SEVERE, "Cant switch to master database");
                    throw new BackupRestoreException(RestoreErrors.PROBLEM_SWITCHING_DB_MODE, e4);
                }
                if (isMultiUserMode) {
                    try {
                        this.setSingleUserMode(conn);
                    }
                    catch (final SQLException e4) {
                        MssqlRestoreHandler.LOGGER.log(Level.SEVERE, "Cant switch to single user mode :: " + e4);
                    }
                }
                else {
                    MssqlRestoreHandler.LOGGER.log(Level.INFO, "There is no need to switch to single user mode");
                }
                restoreStatus.setStatus(BackupRestoreConfigurations.RESTORE_STATUS.RESTORE_PROCESS_IN_PROGRESS);
                restoreResult.setRestoreStatus(restoreStatus.getStatus());
                this.sendRestoreNotification(restoreStatus);
                byte restoreQueryType = 0;
                final String errorPattern1 = ".*The file .* cannot be overwritten.*It is being used by database.*";
                final String errorPattern2 = ".*Directory lookup for the file.*failed.*";
                HashMap<String, String> fileNameRelation = null;
                String[] locToStoreFile = null;
                if (!isFullBackup) {
                    ++restoreQueryType;
                    while (true) {
                        try {
                            this.executeRestore(restoreFiles.get(0), restoreQueryType, locToStoreFile, fileNameRelation, conn);
                            ++restoreQueryType;
                        }
                        catch (final SQLException e5) {
                            final String errorMessage = e5.getMessage();
                            MssqlRestoreHandler.LOGGER.log(Level.SEVERE, errorMessage);
                            final boolean matchesErrorPattern1 = errorMessage.matches(".*The file .* cannot be overwritten.*It is being used by database.*");
                            final boolean matchesErrorPattern2 = errorMessage.matches(".*Directory lookup for the file.*failed.*");
                            if (matchesErrorPattern1 || matchesErrorPattern2) {
                                if (matchesErrorPattern1) {
                                    MssqlRestoreHandler.LOGGER.log(Level.WARNING, "The executution failed because on the MS SQL Server side there might be a data or log file with the same name.");
                                }
                                else {
                                    MssqlRestoreHandler.LOGGER.log(Level.WARNING, "The executution failed because expected backup directory dosent exists.");
                                }
                                if (locToStoreFileTemp != null) {
                                    locToStoreFile = locToStoreFileTemp;
                                    MssqlRestoreHandler.LOGGER.log(Level.INFO, "Trying the restoration with MOVE");
                                    fileNameRelation = new HashMap<String, String>();
                                    continue;
                                }
                                MssqlRestoreHandler.LOGGER.log(Level.WARNING, "Data of log files location cannot be obtained. So the restoration failed.");
                            }
                            throw new BackupRestoreException(RestoreErrors.PROBLEM_WHILE_EXECUTING_COMMAND, e5);
                        }
                        break;
                    }
                    MssqlRestoreHandler.LOGGER.log(Level.INFO, "Restoring the incremental backup file " + restoreFile);
                }
                else {
                    MssqlRestoreHandler.LOGGER.log(Level.INFO, "Restoring the full backup file " + restoreFile);
                }
                while (true) {
                    try {
                        this.executeRestore(restoreFile, restoreQueryType, locToStoreFile, fileNameRelation, conn);
                    }
                    catch (final SQLException e5) {
                        final String errorMessage = e5.getMessage();
                        MssqlRestoreHandler.LOGGER.log(Level.SEVERE, errorMessage);
                        final boolean matchesErrorPattern1 = errorMessage.matches(".*The file .* cannot be overwritten.*It is being used by database.*");
                        final boolean matchesErrorPattern2 = errorMessage.matches(".*Directory lookup for the file.*failed.*");
                        if (locToStoreFile == null && (matchesErrorPattern1 || matchesErrorPattern2)) {
                            if (matchesErrorPattern1) {
                                MssqlRestoreHandler.LOGGER.log(Level.WARNING, "The executution failed because on the MS SQL Server side there might be a data or log file with the same name.");
                            }
                            else {
                                MssqlRestoreHandler.LOGGER.log(Level.WARNING, "The executution failed because expected backup directory dosent exists.");
                            }
                            if (locToStoreFileTemp != null) {
                                locToStoreFile = locToStoreFileTemp;
                                MssqlRestoreHandler.LOGGER.log(Level.INFO, "Trying the restoration with MOVE");
                                continue;
                            }
                            MssqlRestoreHandler.LOGGER.log(Level.WARNING, "Data of log files location cannot be obtained. So the restoration failed.");
                        }
                        throw new BackupRestoreException(RestoreErrors.PROBLEM_WHILE_EXECUTING_COMMAND, e5);
                    }
                    break;
                }
                restoreStatus.setStatus(BackupRestoreConfigurations.RESTORE_STATUS.RESTORE_SUCCESSFULLY_COMPLETED);
                restoreResult.setRestoreStatus(restoreStatus.getStatus());
                this.sendRestoreNotification(restoreStatus);
                restoreStatus.setRestoreEndTime(System.currentTimeMillis());
                restoreResult.setEndTime(restoreStatus.getRestoreEndTime());
                restoreResult.calculateDuration();
                return restoreResult;
            }
            catch (final SQLException e6) {
                throw new BackupRestoreException(RestoreErrors.PROBLEM_FETCHING_CONNECTION, e6);
            }
            finally {
                try {
                    if (isMultiUserMode) {
                        this.setMultiUserMode(conn);
                    }
                    this.useGivenDB(conn);
                }
                catch (final Exception e7) {
                    throw new BackupRestoreException(RestoreErrors.PROBLEM_SWITCHING_DB_MODE, e7);
                }
                finally {
                    try {
                        if (conn != null) {
                            conn.close();
                        }
                    }
                    catch (final SQLException e8) {
                        e8.printStackTrace();
                    }
                }
            }
        }
        catch (final BackupRestoreException e9) {
            restoreStatus.setStatus(BackupRestoreConfigurations.RESTORE_STATUS.RESTORE_PROCESS_FAILED);
            restoreResult.setRestoreStatus(restoreStatus.getStatus());
            this.sendRestoreNotification(restoreStatus);
            throw e9;
        }
    }
    
    private void checkSanityOfTheBackupFiles(final Connection conn, final List<String> restoreFiles) throws BackupRestoreException {
        for (final String restoreFile : restoreFiles) {
            try {
                ((MssqlDBAdapter)this.dbAdapter).checkSanityOfBackupFile(conn, restoreFile);
            }
            catch (final Exception e) {
                MssqlRestoreHandler.LOGGER.log(Level.SEVERE, "The backup file '" + restoreFile + "' is corrupted.");
                throw new BackupRestoreException(RestoreErrors.FW_SANITY_TEST_FAILED, e);
            }
        }
        MssqlRestoreHandler.LOGGER.log(Level.INFO, "None of the backup file(s) are corrupted");
    }
    
    private String[] getLocationOfDatabaseFiles(final Connection conn, final String version) {
        String[] folders = new String[2];
        try {
            if (version != null && Integer.parseInt(version.substring(0, version.indexOf(46))) >= 11) {
                folders[0] = this.getDefaultDataFilesLocation(conn);
                folders[1] = this.getDefaultLogFilesLocation(conn);
            }
            else {
                boolean isDataFileSet = false;
                boolean isLogFileSet = false;
                final Map<String, String> map = ((MssqlDBInitializer)this.dbInitializer).getDBFilesLocation();
                for (final String key : map.keySet()) {
                    if (isLogFileSet && isDataFileSet) {
                        break;
                    }
                    final String type = map.get(key);
                    if (type.equals("ROWS")) {
                        folders[0] = this.getDataFileFolder(key);
                        isDataFileSet = true;
                    }
                    else {
                        if (!type.equals("LOG")) {
                            continue;
                        }
                        folders[1] = this.getDataFileFolder(key);
                        isLogFileSet = true;
                    }
                }
            }
            if (folders[0] == null || folders[1] == null) {
                MssqlRestoreHandler.LOGGER.log(Level.INFO, "The default location couldnt be obtained.");
                folders = null;
            }
            else {
                MssqlRestoreHandler.LOGGER.log(Level.INFO, "The default location of data-files is : " + folders[0] + " and log files is " + folders[1]);
            }
        }
        catch (final Exception e) {
            MssqlRestoreHandler.LOGGER.log(Level.SEVERE, "Couldn't get the default location of log & data files. {0}", e);
            folders = null;
        }
        return folders;
    }
    
    private String getDataFileFolder(final String file) {
        return file.substring(0, file.lastIndexOf("\\"));
    }
    
    private String getDefaultLogFilesLocation(final Connection conn) throws SQLException, QueryConstructionException {
        return this.dbAdapter.getDBSystemProperty(conn, "INSTANCEDEFAULTLOGPATH");
    }
    
    private String getDefaultDataFilesLocation(final Connection conn) throws SQLException, QueryConstructionException {
        return this.dbAdapter.getDBSystemProperty(conn, "INSTANCEDEFAULTDATAPATH");
    }
    
    private HashMap<String, String> getRestoreFiles(final Connection conn, final String restoreFile) {
        DataSet ds = null;
        HashMap<String, String> fileAndType = new HashMap<String, String>();
        try {
            final String query = "RESTORE FILELISTONLY FROM DISK =?";
            MssqlRestoreHandler.LOGGER.log(Level.FINE, query);
            ds = RelationalAPI.getInstance().executeQuery(conn, query, restoreFile);
            while (ds.next()) {
                fileAndType.put(ds.getAsString(1), ds.getAsString(3));
            }
            if (fileAndType.size() == 0) {
                fileAndType = null;
            }
        }
        catch (final Exception e) {
            fileAndType = null;
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
        return fileAndType;
    }
    
    private boolean isDatabaseInMultiUserMode(final Connection conn) {
        boolean isMultiUserMode = false;
        String result = null;
        try {
            result = ((MssqlDBAdapter)this.dbAdapter).getMssqlDBProperty(conn, "UserAccess");
            if (result != null && !result.equals("")) {
                isMultiUserMode = result.equalsIgnoreCase("MULTI_USER");
            }
            else {
                MssqlRestoreHandler.LOGGER.log(Level.INFO, "The useraccess cannot be null or empty.");
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return isMultiUserMode;
    }
    
    private void executeRestore(final String fileLocation, final byte restoreQyeryType, final String[] locToStoreFile, final HashMap<String, String> fileNameRelation, final Connection conn) throws SQLException {
        final StringBuilder query = new StringBuilder("RESTORE DATABASE " + this.database + " FROM DISK=? WITH REPLACE");
        if (restoreQyeryType == 1) {
            query.append(", NORECOVERY");
        }
        else if (restoreQyeryType == 2) {
            query.append(", RECOVERY");
        }
        if (locToStoreFile != null) {
            final HashMap<String, String> fileAndType = this.getRestoreFiles(conn, fileLocation);
            if (fileAndType == null) {
                MssqlRestoreHandler.LOGGER.log(Level.SEVERE, "No records where found while executing restore FILELISTONLY query. So now trying to restore without MOVE command. This might throw execption while running.");
            }
            else {
                MssqlRestoreHandler.LOGGER.log(Level.WARNING, "MOVE command is used only when there exists another database with the same set of file names as that of the file which are being restored from thisd backup.");
                MssqlRestoreHandler.LOGGER.log(Level.WARNING, "Since MOVE command is used, the file name which is being used by the database might vary.");
                int mdfCount = 0;
                int ldfCount = 0;
                if (fileNameRelation != null) {
                    if (fileNameRelation.containsKey("dataFileCount")) {
                        mdfCount = Integer.parseInt(fileNameRelation.get("dataFileCount"));
                    }
                    if (fileNameRelation.containsKey("logFileCount")) {
                        ldfCount = Integer.parseInt(fileNameRelation.get("logFileCount"));
                    }
                }
                for (final String logicalName : fileAndType.keySet()) {
                    query.append(", MOVE '" + logicalName + "' TO '");
                    if (fileNameRelation != null && fileNameRelation.containsKey(logicalName)) {
                        query.append(fileNameRelation.get(logicalName) + "' ");
                    }
                    else {
                        String moveName = null;
                        final char type = fileAndType.get(logicalName).charAt(0);
                        if (type == 'D') {
                            moveName = locToStoreFile[0] + "\\" + this.database + "_" + mdfCount++ + ".mdf";
                        }
                        else if (type == 'L') {
                            moveName = locToStoreFile[1] + "\\" + this.database + "_" + ldfCount++ + ".ldf";
                        }
                        query.append(moveName + "' ");
                        if (fileNameRelation == null) {
                            continue;
                        }
                        fileNameRelation.put(logicalName, moveName);
                    }
                }
                if (fileNameRelation != null) {
                    fileNameRelation.put("logFileCount", mdfCount + "");
                    fileNameRelation.put("dataFileCount", ldfCount + "");
                }
            }
        }
        MssqlRestoreHandler.LOGGER.log(Level.FINE, query.toString());
        RelationalAPI.getInstance().execute(conn, query.toString(), fileLocation);
    }
    
    private boolean isDBVersionCompatable(final String version, final String prevVersion) {
        if (version == null || prevVersion == null) {
            return true;
        }
        final String[] propVersion = prevVersion.split("\\.");
        final String[] currVersion = version.split("\\.");
        return Float.parseFloat(propVersion[0] + "." + propVersion[1]) == Float.parseFloat(currVersion[0] + "." + currVersion[1]);
    }
    
    private void setSingleUserMode(final Connection conn) throws SQLException {
        MssqlRestoreHandler.LOGGER.log(Level.INFO, "Changing the database to single user mode");
        ((MssqlDBAdapter)this.dbAdapter).alterDatabase(conn, "SINGLE_USER WITH ROLLBACK IMMEDIATE");
    }
    
    private void setMultiUserMode(final Connection conn) throws SQLException {
        MssqlRestoreHandler.LOGGER.log(Level.INFO, "Changing the database to multi user mode");
        ((MssqlDBAdapter)this.dbAdapter).alterDatabase(conn, "MULTI_USER");
    }
    
    private void useMasterDB(final Connection conn) throws SQLException {
        this.useDB(conn, "master");
    }
    
    private void useGivenDB(final Connection conn) throws SQLException {
        this.useDB(conn, this.database);
    }
    
    private void useDB(final Connection conn, final String database) throws SQLException {
        MssqlRestoreHandler.LOGGER.log(Level.INFO, "Connecting to the " + database + " database");
        final String query = "USE " + database;
        MssqlRestoreHandler.LOGGER.log(Level.FINE, query);
        RelationalAPI.getInstance().execute(conn, query);
    }
    
    protected String getBAKFile(String file, final Properties prop, final RestoreDBParams rdbp) {
        int pos = file.lastIndexOf(File.separator);
        if (pos != -1) {
            file = file.substring(pos + 1);
        }
        pos = file.lastIndexOf(46);
        if (pos != -1) {
            file = file.substring(0, pos);
        }
        file += ".bak";
        final String backupFolder = prop.getProperty("remoteBackupFolder");
        if (backupFolder != null && !backupFolder.trim().isEmpty()) {
            if (backupFolder.endsWith("\\")) {
                file = backupFolder + file;
            }
            else {
                file = backupFolder + "\\" + file;
            }
        }
        return file;
    }
    
    @Deprecated
    @Override
    protected RestoreResult restoreTableBackup(final RestoreDBParams rdbp) throws BackupRestoreException {
        MssqlRestoreHandler.LOGGER.log(Level.SEVERE, "Restore Operation not Supported");
        throw new BackupRestoreException(RestoreErrors.UNSUPPORTED_RESTORE_TYPE);
    }
    
    static {
        LOGGER = Logger.getLogger(MssqlRestoreHandler.class.getName());
    }
}
