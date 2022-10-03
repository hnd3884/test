package com.me.devicemanagement.onpremise.server.service;

import java.util.Hashtable;
import com.zoho.mickey.exception.PasswordException;
import com.adventnet.persistence.PersistenceException;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileReader;
import java.sql.DriverManager;
import com.adventnet.persistence.PersistenceUtil;
import java.io.InputStream;
import java.io.FileInputStream;
import com.me.devicemanagement.onpremise.server.fos.FosUtil;
import com.me.devicemanagement.onpremise.start.DCStarter;
import java.util.Enumeration;
import java.util.Locale;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import com.adventnet.db.api.RelationalAPI;
import java.util.Iterator;
import com.adventnet.i18n.I18N;
import com.adventnet.persistence.fos.FOS;
import java.util.Map;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadStatus;
import com.me.devicemanagement.framework.server.downloadmgr.SSLValidationType;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import com.me.devicemanagement.onpremise.properties.util.GeneralPropertiesLoader;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.devicemanagement.onpremise.webclient.common.SYMClientUtil;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.nio.channels.FileLock;
import java.nio.channels.FileChannel;
import java.io.RandomAccessFile;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import com.me.tools.zcutil.METrack;
import com.me.devicemanagement.onpremise.server.metrack.METrackerHandler;
import com.me.devicemanagement.onpremise.server.metrack.EvaluatorTrackerUtil;
import com.me.devicemanagement.onpremise.server.status.SysStatusHandler;
import com.me.devicemanagement.onpremise.start.util.InstallUtil;
import java.util.Properties;
import com.me.devicemanagement.framework.server.dms.DMSDownloadHandler;
import com.me.devicemanagement.framework.server.license.LicenseListener;
import com.me.devicemanagement.onpremise.server.license.OnpremiseLicenseListenerImpl;
import com.me.devicemanagement.framework.server.license.LicenseListenerHandler;
import com.me.ems.framework.common.api.utils.FileAccess;
import com.me.devicemanagement.framework.server.search.AdvSearchUtil;
import com.me.devicemanagement.framework.server.search.AdvSearchCommonUtil;
import com.me.devicemanagement.onpremise.server.search.AdvSearchIndexUpdater;
import com.me.devicemanagement.framework.server.search.AdvSearchLogger;
import com.me.devicemanagement.framework.server.search.SearchConfiguration;
import com.me.devicemanagement.framework.server.common.DMApplicationHandler;
import com.adventnet.persistence.PersistenceInitializer;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.devicemanagement.onpremise.start.StartupUtil;
import java.io.File;
import com.me.devicemanagement.onpremise.start.servertroubleshooter.util.ServerTroubleshooterUtil;
import com.me.devicemanagement.onpremise.server.util.DBUtil;
import com.me.devicemanagement.onpremise.server.queue.QueueDataMETracking;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import com.me.devicemanagement.onpremise.server.redis.RedisServerUtil;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import java.util.logging.Level;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;
import com.adventnet.mfw.service.Service;

public class DMOnPremiseService implements Service
{
    private static Logger logger;
    private static final String SERVER_LOCK_FILE_NAME = "server.lock";
    public static boolean isAllowToStartServer;
    public static String server_migration_temp;
    
    public void create(final DataObject d) throws Exception {
        DMOnPremiseService.logger.log(Level.INFO, "___________________________________________");
        DMOnPremiseService.logger.log(Level.INFO, "Creating DM OnPremise Service...");
        DMOnPremiseService.logger.log(Level.INFO, "___________________________________________");
    }
    
    public void start() throws Exception {
        this.fosLicenseHandling();
        SyMUtil.findSystemHWType();
        this.amazonConfig();
        RedisServerUtil.configureRedisServer();
        final boolean isRedisEnabled = Boolean.parseBoolean(com.me.devicemanagement.framework.server.util.SyMUtil.getSyMParameter("enableRedis"));
        if (isRedisEnabled) {
            RedisServerUtil.configureQueue();
            RedisServerUtil.configureRedisPeriodicBackup();
        }
        else {
            RedisServerUtil.disableRedisPeriodicBackupScheduler();
        }
        DCQueueHandler.createAndStartQueuesFromDB();
        QueueDataMETracking.initializeTrackingMap();
        this.logProductDetails();
        this.updateRegistrationDetails();
        SyMUtil.loadPortValues();
        SyMUtil.renewLogLevel();
        if (DBUtil.getActiveDBName().equalsIgnoreCase("postgres")) {
            this.changeMEDCUserPassword();
        }
        if (this.isBuildNumberIncompatibilityFound()) {
            DMOnPremiseService.logger.log(Level.WARNING, "########################################################################");
            DMOnPremiseService.logger.log(Level.WARNING, "###################     N E E D   A T T E N T I O N    #################");
            DMOnPremiseService.logger.log(Level.WARNING, "Found build number compatibility issues. Going to shutdown the server.....");
            DMOnPremiseService.logger.log(Level.WARNING, "########################################################################");
            DMOnPremiseService.isAllowToStartServer = false;
            try {
                ServerTroubleshooterUtil.getInstance().serverStartupFailure("build_number_incompatible");
            }
            catch (final Exception e) {
                DMOnPremiseService.logger.log(Level.SEVERE, "Exception while calling serverStartupFailure for build number incompatibility found", e);
            }
            SyMUtil.triggerServerShutdown("Build number from db and file is incompatible.");
        }
        if (this.isIncompleteRevertFound()) {
            DMOnPremiseService.logger.log(Level.WARNING, "########################################################################");
            DMOnPremiseService.logger.log(Level.WARNING, "###################     N E E D   A T T E N T I O N    #################");
            DMOnPremiseService.logger.log(Level.WARNING, "Found Revert LOCK file. Going to shutdown the server to avoid any data corruption.....");
            DMOnPremiseService.logger.log(Level.WARNING, "Revert while previous restore might not have been successful. Check \"backuplog.txt\" for more info..");
            DMOnPremiseService.logger.log(Level.WARNING, "########################################################################");
            DMOnPremiseService.isAllowToStartServer = false;
            try {
                ServerTroubleshooterUtil.getInstance().serverStartupFailure("revert_lock");
            }
            catch (final Exception e) {
                DMOnPremiseService.logger.log(Level.SEVERE, "Exception while calling serverStartupFailure for incomplete previous restore found", e);
            }
            SyMUtil.triggerServerShutdown("Revert is incomplete. The revert.lock file still exists.");
        }
        final File flock = new File("migration.lock");
        if (this.ismigrationRevertFound() && this.isFileLocked(flock)) {
            DMOnPremiseService.logger.log(Level.WARNING, "########################################################################");
            DMOnPremiseService.logger.log(Level.WARNING, "###################     N E E D   A T T E N T I O N    #################");
            DMOnPremiseService.logger.log(Level.WARNING, "Found migration LOCK file. Going to shutdown the server to avoid any data corruption.....");
            DMOnPremiseService.logger.log(Level.WARNING, "Previous DB migration might not have been successful. Check \"DBMigration_log.txt\" for more info..");
            DMOnPremiseService.logger.log(Level.WARNING, "########################################################################");
            DMOnPremiseService.isAllowToStartServer = false;
            try {
                ServerTroubleshooterUtil.getInstance().serverStartupFailure("migration_lock");
            }
            catch (final Exception e2) {
                DMOnPremiseService.logger.log(Level.SEVERE, "Exception while calling serverStartupFailure for improper previous DB Migration found", e2);
            }
            SyMUtil.triggerServerShutdown("Server migration is incomplete. The migration.lock file still exists.");
        }
        final String dbName = DBUtil.getActiveDBName();
        DMOnPremiseService.logger.log(Level.INFO, "DB Name : " + dbName);
        final String fileSystemLockFile = SyMUtil.getInstallationDir() + File.separator + "bin" + File.separator + "filesystem.lock";
        if ((dbName.equalsIgnoreCase("mssql") || (dbName.equalsIgnoreCase("postgres") && DBUtil.isRemoteDB())) && !SyMUtil.isFosReplicationPending()) {
            boolean isfileSystemLockExist = false;
            isfileSystemLockExist = this.isFileSystemLockExists();
            if (isfileSystemLockExist) {
                DMOnPremiseService.logger.log(Level.INFO, " FileSystem Lock Exists : " + isfileSystemLockExist);
                DMOnPremiseService.logger.log(Level.INFO, " FileSystem Lock Path : " + fileSystemLockFile);
                final Properties fileSystemProps = StartupUtil.getProperties(fileSystemLockFile);
                final String needToHandleinServerStartup = fileSystemProps.getProperty("isAllowServerStartup");
                if (needToHandleinServerStartup.equalsIgnoreCase("false")) {
                    if (!SyMUtil.isDBMatchWithFileSystem()) {
                        DMOnPremiseService.logger.log(Level.WARNING, "###################     N E E D   A T T E N T I O N    #################");
                        DMOnPremiseService.logger.log(Level.WARNING, "Found File System LOCK file already exists .");
                        DMOnPremiseService.logger.log(Level.WARNING, "DB and File System was mismatched. Going to shutdown the server to avoid any data corruption.....");
                        DMOnPremiseService.logger.log(Level.INFO, " FileSystem properties are " + fileSystemProps);
                        DMOnPremiseService.isAllowToStartServer = false;
                        try {
                            ServerTroubleshooterUtil.getInstance().serverStartupFailure("file_db_mismatch");
                        }
                        catch (final Exception e3) {
                            DMOnPremiseService.logger.log(Level.SEVERE, "Exception while calling serverStartupFailure for mismatch of database and filesystem found", e3);
                        }
                        SyMUtil.triggerServerShutdown("File system and db mismatch.");
                    }
                }
                else {
                    DMOnPremiseService.logger.log(Level.INFO, " No need to compare DB and file system ");
                }
            }
            if (!isfileSystemLockExist && !SyMUtil.isDBMatchWithFileSystem()) {
                DMOnPremiseService.logger.log(Level.WARNING, "########################################################################");
                DMOnPremiseService.logger.log(Level.WARNING, "###################     N E E D   A T T E N T I O N    #################");
                DMOnPremiseService.logger.log(Level.WARNING, "File System LOCK file Exists. Going to shutdown the server to avoid any data corruption.....");
                DMOnPremiseService.logger.log(Level.WARNING, "DB and File System was mismatched");
                DMOnPremiseService.logger.log(Level.WARNING, "########################################################################");
                final Properties fileSystemProps = StartupUtil.getProperties(fileSystemLockFile);
                DMOnPremiseService.logger.log(Level.INFO, " FileSystem properties are " + fileSystemProps);
                DMOnPremiseService.isAllowToStartServer = false;
                try {
                    ServerTroubleshooterUtil.getInstance().serverStartupFailure("file_db_mismatch");
                }
                catch (final Exception e4) {
                    DMOnPremiseService.logger.log(Level.SEVERE, "Exception while calling serverStartupFailure for mismatch of database and filesystem found", e4);
                }
                SyMUtil.triggerServerShutdown("File system and db mismatch.");
            }
        }
        if (DMOnPremiseService.isAllowToStartServer) {
            DMOnPremiseService.logger.log(Level.INFO, "System Details: " + System.getProperties());
            MessageProvider.getInstance().hideMessage("SQL_DB_FILE_SYSTEM_MISMATCH");
            if (dbName != null && dbName.equalsIgnoreCase("postgres")) {
                final String postgresVersion = this.getPostgresVersion(System.getProperty("db.home")).substring(22);
                final int majorVersion = Integer.parseInt(postgresVersion.split("\\.")[0]);
                if (majorVersion < 10) {
                    DMOnPremiseService.logger.log(Level.INFO, "Postgres major version : {0} , Going to update FSM Corruption Info", majorVersion);
                    this.updateFSMCorruptionInfo();
                }
            }
            if (dbName != null && dbName.equalsIgnoreCase("mssql")) {
                this.updateSnapshotInfo();
            }
            DMOnPremisetHandler.initiate();
            this.checkAndSendFosTakeoverMail();
            DMOnPremiseService.logger.log(Level.INFO, "Demo mode :" + ApiFactoryProvider.getDemoUtilAPI().isDemoMode());
            if (ApiFactoryProvider.getDemoUtilAPI().isDemoMode()) {
                SyMUtil.sendCrashLog();
            }
            if (new File(fileSystemLockFile).exists()) {
                DMOnPremiseService.logger.log(Level.INFO, "filesystem.lock file delete status " + new File(fileSystemLockFile).delete());
            }
            if (PersistenceInitializer.isColdStart()) {
                DMApplicationHandler.setDCModulesInExtn();
            }
            this.startMETracking();
        }
        if (new File(System.getProperty("server.home") + File.separator + "bin" + File.separator + "port_in_use_restart.lock").exists()) {
            DMOnPremiseService.logger.log(Level.INFO, "########################################################################");
            DMOnPremiseService.logger.log(Level.INFO, "###################     N E E D   A T T E N T I O N    #################");
            DMOnPremiseService.logger.log(Level.INFO, "port_in_use_restart.lock exists , In startup, DB Port Number may changed.");
            DMOnPremiseService.logger.log(Level.INFO, "Port is use Restart Lock File Delete status : " + new File(System.getProperty("server.home") + File.separator + "bin" + File.separator + "port_in_use_restart.lock").delete());
            DMOnPremiseService.logger.log(Level.INFO, "########################################################################");
        }
        try {
            if (SearchConfiguration.getConfiguration().isSearchEnabled()) {
                final AdvSearchLogger advSearchLogger = AdvSearchLogger.getInstance();
                advSearchLogger.printProductInfo("StartUp");
                final AdvSearchIndexUpdater indexUpdater = AdvSearchIndexUpdater.getInstance();
                final boolean isSettingsUpdate = indexUpdater.checkSearchIndexDir("staticMainIndex", AdvSearchCommonUtil.static_action_index_dir);
                indexUpdater.checkSearchIndexDir("docMainIndex", AdvSearchCommonUtil.doc_index_dir).booleanValue();
            }
            else {
                DMOnPremiseService.logger.log(Level.WARNING, "Skipped Updating Index for Searching due to Disable of AdvSearch");
            }
            if (SearchConfiguration.getConfiguration().isSearchEnabled()) {
                AdvSearchUtil.getInstance().getSearchProductSpecificHandler().disableAdvSearchForCertainEdition();
            }
            this.deleteDBEntryonServerMirationDisable();
            FileAccess.createFilesTableCleanupScheduler();
        }
        catch (final Exception e5) {
            DMOnPremiseService.logger.log(Level.SEVERE, "Exception while Updating Index for Searching");
        }
        try {
            LicenseListenerHandler.getInstance().addLicenseListener((LicenseListener)new OnpremiseLicenseListenerImpl());
        }
        catch (final Exception ex) {
            DMOnPremiseService.logger.log(Level.SEVERE, "Exception while adding on-premise license listener for product banner");
        }
        DMSDownloadHandler.startupHandling();
    }
    
    private void logProductDetails() {
        DMOnPremiseService.logger.log(Level.INFO, "PRODUCT DETAILS BEGIN");
        try {
            DMOnPremiseService.logger.log(Level.INFO, "Build Number :" + SyMUtil.getProductProperty("buildnumber"));
            final Properties props = InstallUtil.getProductProperties();
            DMOnPremiseService.logger.log(Level.INFO, "Product Details from product.conf: " + props);
        }
        catch (final Exception ex) {
            DMOnPremiseService.logger.log(Level.WARNING, "Exception while logging product Details ", ex);
        }
        DMOnPremiseService.logger.log(Level.INFO, "PRODUCT DETAILS END");
    }
    
    public void stop() throws Exception {
        DMOnPremiseService.logger.log(Level.INFO, "Stopping DesktopCentral Service...");
        if (DMOnPremiseService.isAllowToStartServer) {
            DCServerBuildHistoryProvider.getInstance().updateDCServerShutdown();
            SysStatusHandler.updateServerUptime("DesktopCentralService", null, System.currentTimeMillis());
            EvaluatorTrackerUtil.writeJSONToFile();
        }
        final boolean isRedisEnabled = Boolean.parseBoolean(com.me.devicemanagement.framework.server.util.SyMUtil.getSyMParameter("enableRedis"));
        if (isRedisEnabled) {
            RedisServerUtil.shutdownRedis();
        }
    }
    
    public void destroy() throws Exception {
        DMOnPremiseService.logger.log(Level.INFO, "Destroying DesktopCentral Service...");
        final Properties proxyProps = METrackerHandler.getProxyProps();
        METrack.shutdown(proxyProps);
        DMOnPremiseService.logger.log(Level.INFO, "Shutdown MEDC Tracker...");
    }
    
    private void updateRegistrationDetails() {
        final boolean registeredCustomer = InstallUtil.checkAndUpdateCustomerRegistrationDetails();
        DMOnPremiseService.logger.log(Level.INFO, "checkAndUpdateCustomerRegistrationDetails method returned :" + registeredCustomer);
        final String regCus = SyMUtil.getSyMParameter("REGISTERED_CUSTOMER");
        DMOnPremiseService.logger.log(Level.INFO, "REGISTERED_CUSTOMER parameter value in systemparams :" + regCus);
        if (regCus == null) {
            SyMUtil.updateSyMParameter("REGISTERED_CUSTOMER", String.valueOf(registeredCustomer));
            DMOnPremiseService.logger.log(Level.INFO, "Updated the REGISTERED_CUSTOMER parameter value in systemparams :" + SyMUtil.getSyMParameter("REGISTERED_CUSTOMER"));
        }
    }
    
    private boolean isBuildNumberIncompatibilityFound() throws Exception {
        boolean isIncompatible = false;
        try {
            final String buildNumStrFromConf = SyMUtil.getProductProperty("buildnumber");
            Integer buildNumFromConf = null;
            if (buildNumStrFromConf != null & buildNumStrFromConf.trim().length() > 0) {
                buildNumFromConf = new Integer(buildNumStrFromConf.trim());
            }
            DMOnPremiseService.logger.log(Level.INFO, "Build number retrieved from product.conf is: " + buildNumFromConf);
            final Integer buildNumFromDB = DCServerBuildHistoryProvider.getInstance().getCurrentBuildNumberFromDB();
            DMOnPremiseService.logger.log(Level.INFO, "Build number retrieved from DB is: " + buildNumFromDB);
            if (buildNumFromConf != null && buildNumFromDB != null && buildNumFromConf != (int)buildNumFromDB) {
                isIncompatible = true;
                final Properties pr = new Properties();
                pr.setProperty("error-message", "File system incompatible with DB. Might occur in case Deskop Central installation is overwritten by another incompatible version ?");
                pr.setProperty("buildnumber-conf", String.valueOf(buildNumFromConf));
                pr.setProperty("buildnumber-db", String.valueOf(buildNumFromDB));
                this.createServerLockFile(pr);
            }
            else {
                this.deleteServerLockFile();
            }
        }
        catch (final Exception ex) {
            DMOnPremiseService.logger.log(Level.WARNING, "Caught exception while checking build number compatibility.", ex);
        }
        return isIncompatible;
    }
    
    private boolean isIncompleteRevertFound() {
        boolean found = false;
        try {
            final String revertLock = SyMUtil.getInstallationDir() + File.separator + "bin" + File.separator + "revert.lock";
            final File revertLockFile = new File(revertLock);
            found = revertLockFile.exists();
        }
        catch (final Exception e) {
            DMOnPremiseService.logger.log(Level.INFO, "Exception while checking revert.lock file", e);
        }
        return found;
    }
    
    private boolean ismigrationRevertFound() {
        boolean found = false;
        try {
            final String migrateLock = SyMUtil.getInstallationDir() + File.separator + "bin" + File.separator + "migration.lock";
            final File migrateLockFile = new File(migrateLock);
            found = migrateLockFile.exists();
        }
        catch (final Exception e) {
            DMOnPremiseService.logger.log(Level.INFO, "Exception while checking revert.lock file", e);
        }
        return found;
    }
    
    private void createServerLockFile(final Properties pr) {
        String serverLockFileNameFull = null;
        try {
            serverLockFileNameFull = SyMUtil.getInstallationDir() + File.separator + "bin" + File.separator + "server.lock";
            DMOnPremiseService.logger.log(Level.WARNING, "Going to create a server Lock file: " + serverLockFileNameFull);
            if (ApiFactoryProvider.getFileAccessAPI().isFileExists(serverLockFileNameFull)) {
                final Properties serverLockProps = FileAccessUtil.readProperties(serverLockFileNameFull);
                DMOnPremiseService.logger.log(Level.WARNING, "Contents of existing server lock file: " + serverLockProps);
            }
            FileAccessUtil.storeProperties(pr, serverLockFileNameFull, false);
        }
        catch (final Exception ex) {
            DMOnPremiseService.logger.log(Level.WARNING, "Caught exception while creating server lock file: " + serverLockFileNameFull, ex);
        }
    }
    
    private void deleteServerLockFile() {
        String serverLockFileNameFull = null;
        try {
            serverLockFileNameFull = SyMUtil.getInstallationDir() + File.separator + "bin" + File.separator + "server.lock";
            if (ApiFactoryProvider.getFileAccessAPI().isFileExists(serverLockFileNameFull)) {
                DMOnPremiseService.logger.log(Level.WARNING, "########################################################################");
                DMOnPremiseService.logger.log(Level.WARNING, "###################     N E E D   A T T E N T I O N    #################");
                DMOnPremiseService.logger.log(Level.WARNING, "Going to delete the old server lock file...");
                DMOnPremiseService.logger.log(Level.WARNING, "deleteServerLockFile(): Server Lock file with fullpath: " + serverLockFileNameFull);
                final Properties serverLockProps = FileAccessUtil.readProperties(serverLockFileNameFull);
                DMOnPremiseService.logger.log(Level.WARNING, "deleteServerLockFile(): Contents of existing server lock file: " + serverLockProps);
                final boolean result = ApiFactoryProvider.getFileAccessAPI().deleteFile(serverLockFileNameFull);
                DMOnPremiseService.logger.log(Level.WARNING, "deleteServerLockFile(): Result of deletion of server lock file: " + result);
                DMOnPremiseService.logger.log(Level.WARNING, "########################################################################");
            }
        }
        catch (final Exception ex) {
            DMOnPremiseService.logger.log(Level.WARNING, "Caught exception while deleting the server lock file: " + serverLockFileNameFull, ex);
        }
    }
    
    public void startMETracking() {
        try {
            DMOnPremiseService.logger.log(Level.INFO, "Initiating ME Tracking");
            METrackerHandler.startTracking();
            METrackerHandler.checkAndUpdateTrackingProps();
        }
        catch (final Exception e) {
            DMOnPremiseService.logger.log(Level.WARNING, "Exception while starting METracking.." + e);
            e.printStackTrace();
        }
    }
    
    public boolean isFileLocked(final File flock) {
        boolean isLocked = false;
        try {
            final FileChannel f1 = new RandomAccessFile(flock, "rw").getChannel();
            FileLock trylock = null;
            for (int i = 0; i < 3; ++i) {
                if ((trylock = f1.tryLock()) == null) {
                    isLocked = true;
                    break;
                }
                isLocked = false;
                Thread.sleep(2000L);
            }
        }
        catch (final Exception ex) {
            DMOnPremiseService.logger.log(Level.WARNING, "Exception while checking file lock :: ", ex);
        }
        return isLocked;
    }
    
    private boolean isFileSystemLockExists() {
        boolean found = false;
        try {
            final String fileSystemLock = SyMUtil.getInstallationDir() + File.separator + "bin" + File.separator + "filesystem.lock";
            final File fileSystemLockFile = new File(fileSystemLock);
            found = fileSystemLockFile.exists();
        }
        catch (final Exception e) {
            DMOnPremiseService.logger.log(Level.INFO, "Exception while checking filesystem.lock file exists status ", e);
        }
        return found;
    }
    
    private void amazonConfig() {
        final boolean firstStartUp = this.isFirstStartUp();
        DMOnPremiseService.logger.log(Level.INFO, "First StartUp :" + firstStartUp);
        final String systemHWType = SyMUtil.getServerParameter("SYSTEM_HW_TYPE");
        DMOnPremiseService.logger.log(Level.INFO, "systemHWTYpe :" + systemHWType);
        if (systemHWType != null && systemHWType.equalsIgnoreCase("amazon_virtual") && firstStartUp) {
            this.changeDefaultPassword();
        }
    }
    
    public boolean isFirstStartUp() {
        try {
            final SelectQueryImpl query = new SelectQueryImpl(Table.getTable("DCServerBuildHistory"));
            query.addSelectColumn(Column.getColumn("DCServerBuildHistory", "*"));
            final DataObject buildHistoryDO = DataAccess.get((SelectQuery)query);
            if (!buildHistoryDO.isEmpty()) {
                final int buildHistoryRowCount = buildHistoryDO.size("DCServerBuildHistory");
                DMOnPremiseService.logger.log(Level.INFO, "Build DO Count" + buildHistoryRowCount);
                if (buildHistoryRowCount == 1) {
                    final Row buildHistoryDOFirstRow = buildHistoryDO.getFirstRow("DCServerBuildHistory");
                    final Long build_detected_at = (Long)buildHistoryDOFirstRow.get("BUILD_DETECTED_AT");
                    final Integer build_no = (Integer)buildHistoryDOFirstRow.get("BUILD_NUMBER");
                    final int buildUpTimeHistoryRowCount = DBUtil.getRecordCount("DCServerUptimeHistory", "DC_UPTIME_RECORD_ID", (Criteria)null);
                    DMOnPremiseService.logger.log(Level.INFO, "Build Detected at :" + build_detected_at);
                    DMOnPremiseService.logger.log(Level.INFO, "Build up time count" + buildUpTimeHistoryRowCount);
                    if (build_no != null && build_detected_at == -1L && buildUpTimeHistoryRowCount == 0) {
                        return Boolean.TRUE;
                    }
                }
            }
        }
        catch (final Exception e) {
            DMOnPremiseService.logger.log(Level.INFO, "Exception in checking first startup" + e);
            return Boolean.FALSE;
        }
        return Boolean.FALSE;
    }
    
    private void changeDefaultPassword() {
        DataObject aaaPasswordRuleDO = null;
        try {
            final String loginName = "admin";
            final String newPasswd = this.getAWSInstanceID();
            if (newPasswd != null) {
                aaaPasswordRuleDO = this.getDefaultPasswordRuleDO("Normal");
                this.changeDefaultPasswordRule((DataObject)aaaPasswordRuleDO.clone());
                SYMClientUtil.changePassword(loginName, newPasswd);
                DMOnPremiseService.logger.log(Level.INFO, "Password changed to Instance ID");
                SyMUtil.updateServerParameter("IS_AWS_LOGIN", "true");
            }
            else {
                SyMUtil.updateServerParameter("IS_AWS_LOGIN", "false");
                DMOnPremiseService.logger.log(Level.INFO, "Default Password is remained");
                DMOnPremiseService.logger.log(Level.INFO, "As instance ID could not be retrieved, changed system HW type to general");
            }
            SyMUtil.updateServerParameter("IS_AMAZON_DEFAULT_PASSWORD_CHANGED", "false");
        }
        catch (final Exception exp) {
            DMOnPremiseService.logger.log(Level.SEVERE, "default password change exception", exp);
        }
        finally {
            if (aaaPasswordRuleDO != null) {
                this.changeToDefaultPasswordRule(aaaPasswordRuleDO, "Normal");
            }
        }
    }
    
    private DataObject getDefaultPasswordRuleDO(final String ruleName) throws Exception {
        final SelectQueryImpl selectQuery = new SelectQueryImpl(Table.getTable("AaaPasswordRule"));
        final Criteria defaultRuleCriteria = new Criteria(Column.getColumn("AaaPasswordRule", "NAME"), (Object)ruleName, 0);
        selectQuery.setCriteria(defaultRuleCriteria);
        selectQuery.addSelectColumn(Column.getColumn("AaaPasswordRule", "PASSWDRULE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaPasswordRule", "MIN_LENGTH"));
        selectQuery.addSelectColumn(Column.getColumn("AaaPasswordRule", "NUMOF_SPLCHAR"));
        selectQuery.addSelectColumn(Column.getColumn("AaaPasswordRule", "REQ_MIXEDCASE"));
        return DataAccess.get((SelectQuery)selectQuery);
    }
    
    private void changeDefaultPasswordRule(final DataObject aaaPasswordRuleDO) throws Exception {
        if (aaaPasswordRuleDO != null) {
            final Row aaaPasswordRuleRow = aaaPasswordRuleDO.getFirstRow("AaaPasswordRule");
            aaaPasswordRuleRow.set("MIN_LENGTH", (Object)5);
            aaaPasswordRuleRow.set("REQ_MIXEDCASE", (Object)false);
            aaaPasswordRuleRow.set("NUMOF_SPLCHAR", (Object)(-1));
            aaaPasswordRuleDO.updateRow(aaaPasswordRuleRow);
            DataAccess.update(aaaPasswordRuleDO);
        }
    }
    
    private void changeToDefaultPasswordRule(final DataObject aaaPasswordRuleDO, final String ruleName) {
        try {
            final Row aaaPasswordRuleRow = aaaPasswordRuleDO.getFirstRow("AaaPasswordRule");
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("AaaPasswordRule");
            updateQuery.setUpdateColumn("MIN_LENGTH", aaaPasswordRuleRow.getOriginalValue("MIN_LENGTH"));
            updateQuery.setUpdateColumn("REQ_MIXEDCASE", aaaPasswordRuleRow.getOriginalValue("REQ_MIXEDCASE"));
            updateQuery.setUpdateColumn("NUMOF_SPLCHAR", aaaPasswordRuleRow.getOriginalValue("NUMOF_SPLCHAR"));
            updateQuery.setCriteria(new Criteria(Column.getColumn("AaaPasswordRule", "NAME"), (Object)ruleName, 0));
            DataAccess.update(updateQuery);
        }
        catch (final Exception exception) {
            DMOnPremiseService.logger.log(Level.SEVERE, "changeToDefaultPasswordRule : exception occurred while change the default passwordRule " + ruleName + ": ", exception);
        }
    }
    
    public String getAWSInstanceID() {
        final String instanaceIDmetaDataUrl = this.getAWSMetaDataURL() + "/instance-id";
        final String instanceID = this.responseForHTTPgetRequest(instanaceIDmetaDataUrl);
        return instanceID;
    }
    
    public String getAWSMetaDataURL() {
        String url = "http://169.254.169.254/latest/meta-data";
        try {
            final Properties props = GeneralPropertiesLoader.getInstance().getProperties();
            url = props.getProperty("AWSMetaDataUrl");
        }
        catch (final Exception e) {
            DMOnPremiseService.logger.log(Level.INFO, "Path of general_properties.conf file: " + e);
        }
        return url;
    }
    
    private String responseForHTTPgetRequest(final String url) {
        String response = null;
        try {
            final DownloadStatus downloadStatus = DownloadManager.getInstance().getURLResponseWithoutCookie(url, (String)null, new SSLValidationType[0]);
            final int responseCode = downloadStatus.getStatus();
            if (responseCode == 0) {
                response = downloadStatus.getUrlDataBuffer();
            }
            else {
                DMOnPremiseService.logger.log(Level.INFO, "Errorenous response code " + String.valueOf(responseCode));
            }
        }
        catch (final Exception ex) {
            DMOnPremiseService.logger.log(Level.SEVERE, null, ex);
        }
        return response;
    }
    
    private HashMap getPortMap() {
        final Properties rdsProperties = this.readPropertiesFromConf("conf" + File.separator + "rdssettings.conf");
        final Properties dcnsProperties = this.readPropertiesFromConf("conf" + File.separator + "dcnssettings.conf");
        final Properties customDcnsProperties = this.readPropertiesFromConf("conf" + File.separator + "custom_dcnssettings.conf");
        dcnsProperties.putAll(customDcnsProperties);
        final Properties webProperties = this.readPropertiesFromConf("conf" + File.separator + "websettings.conf");
        final HashMap portMap = new HashMap();
        portMap.put("NAT_RDS_HTTPS_PORT", Integer.valueOf(((Hashtable<K, String>)rdsProperties).get("rds.default.https.port")));
        portMap.put("NAT_FT_HTTPS_PORT", Integer.valueOf(((Hashtable<K, String>)rdsProperties).get("ft.default.https.port")));
        portMap.put("NAT_NS_PORT", Integer.valueOf(((Hashtable<K, String>)dcnsProperties).get("ns.port")));
        portMap.put("NAT_CHAT_PORT", Integer.valueOf(((Hashtable<K, String>)webProperties).get("httpnio.port")));
        portMap.put("NAT_HTTPS_PORT", Integer.valueOf(((Hashtable<K, String>)webProperties).get("https.port")));
        return portMap;
    }
    
    private Properties readPropertiesFromConf(final String fileName) {
        try {
            final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
            final String fname = serverHome + File.separator + fileName;
            return FileAccessUtil.readProperties(fname);
        }
        catch (final Exception ex) {
            DMOnPremiseService.logger.log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    private void checkAndSendFosTakeoverMail() {
        try {
            if (FOS.isEnabled()) {
                final FOS fos = new FOS();
                fos.initialize();
                final String currentIP = fos.getFOSConfig().ipaddr();
                final Column col = Column.getColumn("FosParams", "SERVER_IP");
                final Criteria crit = new Criteria(col, (Object)currentIP, 0, false);
                final DataObject fosDo = SyMUtil.getPersistence().get("FosParams", crit);
                Boolean sendMail = Boolean.FALSE;
                if (fosDo != null && !fosDo.isEmpty()) {
                    final StringBuilder mailContent = new StringBuilder();
                    mailContent.append("<div style=\"height: 40%;\nmargin: 5%;width: 80%;padding: 3%;border: 1px solid #c2bbbb;\nborder-left: 5px solid #F44336;\">Dear Admin,<p style=\"margin-top: 4%;font: 17px Lato;color: red;\">");
                    mailContent.append(I18N.getMsg("dc.admin.fos.mail.takover_notification_content", new Object[] { fos.getOtherNode(), currentIP }));
                    mailContent.append("<p style=\"margin-top: 5%;font: 14px Lato;\">");
                    mailContent.append(I18N.getMsg("dc.admin.fos.sign", new Object[0]));
                    mailContent.append("</p>");
                    mailContent.append("<p style=\"margin-top: -1%;font: 11px Lato;color: #8a6e6e;\">");
                    mailContent.append(I18N.getMsg("dc.admin.fos.automated_mail_warning", new Object[0]));
                    mailContent.append("</p></div>");
                    final String mailSubject = I18N.getMsg("dc.admin.fos.mail.subject", new Object[] { null });
                    sendMail = (Boolean)fosDo.getFirstValue("FosParams", "SEND_TAKEOVER_MAIL");
                    if (sendMail) {
                        final Boolean sent = SYMClientUtil.sendMailForFOS(currentIP, mailContent.toString(), mailSubject);
                        if (sent) {
                            DMOnPremiseService.logger.log(Level.INFO, "mail sent ");
                            final Row fosRow = fosDo.getFirstRow("FosParams");
                            fosRow.set("SEND_TAKEOVER_MAIL", (Object)Boolean.FALSE);
                            fosDo.updateRow(fosRow);
                            SyMUtil.getPersistence().update(fosDo);
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            DMOnPremiseService.logger.log(Level.SEVERE, "Exception while checking and sending takeover mail {0}", e);
        }
    }
    
    private void updateFSMCorruptionInfo() {
        try {
            final HashMap<String, String> fsmCorruptedMap = SyMUtil.getFSMCorruptedTableList();
            if (fsmCorruptedMap.size() > 0) {
                DMOnPremiseService.logger.log(Level.INFO, "Below tables are still have FSM Corruption issue. It May not cleaned-up properly or Corrupted again during startup. ");
                final String serverHome = System.getProperty("server.home");
                for (final String key : fsmCorruptedMap.keySet()) {
                    final String value = fsmCorruptedMap.get(key);
                    final String fullPath = serverHome + File.separator + value;
                    DMOnPremiseService.logger.log(Level.INFO, "Table name : {0} :  path {1}", new Object[] { key, fullPath });
                }
            }
            else {
                DMOnPremiseService.logger.log(Level.INFO, "Currently No FSM Corrupted tables found in customer DB");
            }
            SyMUtil.updateFSMCorruptedFlagInDB(fsmCorruptedMap);
        }
        catch (final Exception ex) {
            DMOnPremiseService.logger.log(Level.WARNING, "Exception occurred while update FSM corruption information. Exception : ", ex);
        }
    }
    
    private void updateSnapshotInfo() {
        Connection conn = null;
        Statement statement = null;
        ResultSet rs = null;
        String snapshot = "";
        try {
            conn = RelationalAPI.getInstance().getConnection();
            statement = conn.createStatement();
            final String dbname = SYMClientUtil.getDataBaseName();
            rs = RelationalAPI.getInstance().executeQueryForSQL("SELECT is_read_committed_snapshot_on FROM sys.databases WHERE name= '" + dbname + "'", (Map)null, statement);
            while (rs.next()) {
                snapshot = "" + rs.getInt("is_read_committed_snapshot_on");
            }
            DMOnPremiseService.logger.info("Read commit snapshot is = " + snapshot);
            SyMUtil.updateServerParameter("is_read_committed_snapshot_on", snapshot);
        }
        catch (final Exception e) {
            DMOnPremiseService.logger.log(Level.WARNING, "Error in updateSnapshotInfo " + e.getMessage());
            try {
                if (conn != null) {
                    conn.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (statement != null) {
                    statement.close();
                }
            }
            catch (final SQLException e2) {
                DMOnPremiseService.logger.log(Level.WARNING, "Error in finally block of updateSnapshotInfo " + e2.getMessage());
            }
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (statement != null) {
                    statement.close();
                }
            }
            catch (final SQLException e3) {
                DMOnPremiseService.logger.log(Level.WARNING, "Error in finally block of updateSnapshotInfo " + e3.getMessage());
            }
        }
    }
    
    private void deleteDBEntryonServerMirationDisable() {
        try {
            if (new File(DMOnPremiseService.server_migration_temp).exists()) {
                final Properties server_migration_props = FileAccessUtil.readProperties(DMOnPremiseService.server_migration_temp);
                final String serverMigrationState = server_migration_props.getProperty("ServerMigration");
                if (serverMigrationState != null && serverMigrationState.equalsIgnoreCase("disabled")) {
                    com.me.devicemanagement.framework.server.util.SyMUtil.deleteServerParameter("ConfigureNewServerasOld");
                    final boolean status = new File(DMOnPremiseService.server_migration_temp).delete();
                    DMOnPremiseService.logger.log(Level.INFO, "server-migration-temp.conf deleted? :: {0}", status);
                }
            }
        }
        catch (final Exception e) {
            DMOnPremiseService.logger.info("Exception while deleting db entry on server migration disable: " + e);
        }
    }
    
    public String getPostgresVersion(final String pg_Home) throws IOException, InterruptedException {
        DMOnPremiseService.logger.info("Getting current version using 'postgres' binary.");
        final boolean isWindows = isWindows();
        final List<String> commandList = new ArrayList<String>();
        final Path postgresPath = Paths.get(pg_Home, "bin", "postgres" + (isWindows ? ".exe" : ""));
        commandList.add(postgresPath.toString());
        commandList.add("-V");
        Process postgresProcess = null;
        BufferedReader br = null;
        try {
            postgresProcess = this.executeCommand(commandList, null, null);
            br = new BufferedReader(new InputStreamReader(postgresProcess.getInputStream()));
            final String ipStream = br.readLine();
            final int waitFor = postgresProcess.waitFor();
            DMOnPremiseService.logger.log(Level.INFO, "postgresProcess waitFor :: {0}", waitFor);
            DMOnPremiseService.logger.log(Level.INFO, "Returning version {0}", ipStream);
            return ipStream;
        }
        finally {
            if (br != null) {
                br.close();
            }
            if (postgresProcess != null) {
                postgresProcess.destroy();
            }
        }
    }
    
    private static boolean isWindows() {
        final String osName = System.getProperty("os.name").trim().toLowerCase(Locale.ENGLISH);
        return osName.indexOf("windows") >= 0;
    }
    
    public Process executeCommand(final List<String> commandList, final Properties envProps, final File directoryPath) throws IOException {
        return this.executeCommand(commandList, envProps, directoryPath, false, true);
    }
    
    public Process executeCommand(final List<String> commandList, final Properties envProps, final File directoryPath, final boolean writeToFile, final boolean executeCmd) throws IOException {
        final boolean isWindows = isWindows();
        if (!writeToFile || isWindows) {
            DMOnPremiseService.logger.log(Level.INFO, "Command to be executed ::: {0}", commandList);
            final ProcessBuilder processBuilder = new ProcessBuilder(commandList);
            if (directoryPath != null) {
                processBuilder.directory(directoryPath);
            }
            this.setEnvProps(processBuilder, envProps);
            return processBuilder.start();
        }
        final File extFile = new File(new File(System.getProperty("server.home")).getAbsolutePath() + File.separator + "ext.sh");
        DMOnPremiseService.logger.log(Level.INFO, "Writing comman to ext.sh file ::: {0}", commandList);
        final RandomAccessFile f = new RandomAccessFile(extFile.getAbsolutePath(), "rw");
        if (extFile.length() != 0L) {
            f.seek(extFile.length());
            f.write(System.getProperty("line.separator").getBytes());
        }
        for (final String cmd : commandList) {
            f.write(cmd.toString().getBytes());
            f.write(" ".getBytes());
        }
        f.close();
        if (executeCmd) {
            DMOnPremiseService.logger.info("Executing all commands in ext.sh ");
            final List<String> extCmdList = new ArrayList<String>();
            extCmdList.add("sh");
            extCmdList.add(extFile.getAbsolutePath());
            DMOnPremiseService.logger.log(Level.INFO, "Command to be executed ::: {0}", extCmdList);
            final ProcessBuilder processBuilder2 = new ProcessBuilder(extCmdList);
            processBuilder2.directory(directoryPath);
            this.setEnvProps(processBuilder2, envProps);
            return processBuilder2.start();
        }
        return null;
    }
    
    public void setEnvProps(final ProcessBuilder processBuilder, final Properties envVariables) {
        if (envVariables != null) {
            final Map<String, String> environment = processBuilder.environment();
            final Enumeration<Object> keys = ((Hashtable<Object, V>)envVariables).keys();
            while (keys.hasMoreElements()) {
                final String key = keys.nextElement();
                environment.put(key, envVariables.getProperty(key));
            }
        }
    }
    
    private void fosLicenseHandling() {
        if (DCStarter.isFosFileConfigured()) {
            if (!FosUtil.checkValidLicenseForFos()) {
                DMOnPremiseService.logger.log(Level.INFO, "FOS is configured, But either the license got expired or Not an valid FOS license");
                final Properties ipProps = FosUtil.getCurrentAndPeerIP();
                final String currentIP = ipProps.getProperty("currentIP");
                final String peerIP = ipProps.getProperty("peerIP");
                final String serverHome = System.getProperty("server.home");
                final String currentServerStatus = com.me.devicemanagement.framework.server.util.SyMUtil.getServerParameter(currentIP);
                if (currentServerStatus == null) {
                    DMOnPremiseService.logger.log(Level.INFO, "Shutting down the other server");
                    try {
                        final UpdateQuery query = (UpdateQuery)new UpdateQueryImpl("FOSNodeDetails");
                        final Criteria c1 = new Criteria(new Column("FOSNodeDetails", "IP"), (Object)peerIP, 0);
                        query.setCriteria(c1);
                        query.setUpdateColumn("STATUS", (Object)"down");
                        DMOnPremiseService.logger.log(Level.INFO, "updating status for ip :[ {0} ] status:[ {1} ]", new Object[] { peerIP, "down" });
                        DataAccess.update(query);
                    }
                    catch (final Exception ex) {
                        DMOnPremiseService.logger.log(Level.SEVERE, "Exception occurred while updating the status of other server as Down in FOSNODEDETAILS table", ex);
                    }
                    DMOnPremiseService.logger.log(Level.INFO, "Updating the staus of peer server to 'doNotStart'.");
                    com.me.devicemanagement.framework.server.util.SyMUtil.updateServerParameter(peerIP, "doNotStart");
                }
                else if (currentServerStatus.equalsIgnoreCase("doNotStart")) {
                    DMOnPremiseService.logger.log(Level.INFO, "Current Server status is 'doNotStart' so shutting down this server. Start this server after applying License in the main server");
                    SyMUtil.triggerServerShutdown("FOS license has expired and the other server is started, so we cannot start this server till license is applied. Use the other server");
                }
                else if (currentServerStatus.equalsIgnoreCase("pullAndRestart")) {
                    DMOnPremiseService.logger.log(Level.INFO, "New License applied in the main server, Replicating License files before starting");
                    if (FosUtil.replicateLicenseFiles(peerIP, serverHome)) {
                        com.me.devicemanagement.framework.server.util.SyMUtil.deleteServerParameter(currentIP);
                        DMOnPremiseService.logger.log(Level.INFO, "License files replicated successfully, Trigerring restart");
                        SyMUtil.triggerServerRestart("License Pulled from other server, Restart required.");
                    }
                    else {
                        DMOnPremiseService.logger.log(Level.SEVERE, "License files replicated failed, Going to down the server");
                        SyMUtil.triggerServerShutdown("License File replicaation failed, Shutting Down the server, Ensure that the other server is UP before starting this server");
                    }
                }
            }
            else {
                DMOnPremiseService.logger.log(Level.INFO, "FOS is configured with a valid License");
                final Properties ipProps = FosUtil.getCurrentAndPeerIP();
                final String currentIP = ipProps.getProperty("currentIP");
                final String currentServerStatus = com.me.devicemanagement.framework.server.util.SyMUtil.getServerParameter(currentIP);
                if (currentServerStatus != null) {
                    DMOnPremiseService.logger.log(Level.INFO, "An entry for this server is present in DB, Deleting this entry.");
                    com.me.devicemanagement.framework.server.util.SyMUtil.deleteServerParameter(currentIP);
                }
            }
        }
    }
    
    private void changeMEDCUserPassword() {
        try {
            final String dbConfFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "database_params.conf";
            final Properties props = new Properties();
            FileInputStream fis = null;
            try {
                if (new File(dbConfFile).exists()) {
                    fis = new FileInputStream(dbConfFile);
                    props.load(fis);
                    fis.close();
                }
            }
            catch (final Exception ex) {
                DMOnPremiseService.logger.log(Level.WARNING, "Caught exception while loading properties from: " + dbConfFile + " Exception: ", ex);
                try {
                    if (fis != null) {
                        fis.close();
                    }
                }
                catch (final Exception ex) {
                    DMOnPremiseService.logger.log(Level.WARNING, "Caught exception while loading properties from: " + dbConfFile + " Exception: ", ex);
                }
            }
            finally {
                try {
                    if (fis != null) {
                        fis.close();
                    }
                }
                catch (final Exception ex2) {
                    DMOnPremiseService.logger.log(Level.WARNING, "Caught exception while loading properties from: " + dbConfFile + " Exception: ", ex2);
                }
            }
            if (props.getProperty("r_password") != null && props.containsKey("r_password")) {
                DMOnPremiseService.logger.log(Level.SEVERE, "medc User Password is already dynamic in DB");
            }
            else {
                final String newPassword = PersistenceUtil.generateRandomPassword();
                if (this.changePassword(newPassword)) {
                    DMOnPremiseService.logger.log(Level.SEVERE, "medc User Password changed in DB");
                    if (this.updateRemoteDBPasswordInDBConf(newPassword)) {
                        SyMUtil.updateServerParameter("is_Remote_DB_password_Random", "true");
                        DMOnPremiseService.logger.log(Level.INFO, "medc user password updated in db params");
                    }
                    else {
                        DMOnPremiseService.logger.log(Level.INFO, "Unable to update RemoteDB password in db params");
                    }
                }
            }
        }
        catch (final Exception ex3) {
            DMOnPremiseService.logger.log(Level.SEVERE, "Error while updating remote access db password", ex3);
        }
    }
    
    public boolean changePassword(final String newPassword) throws Exception {
        Connection connection = null;
        try {
            final Properties dbProps = PersistenceInitializer.getDBProps(PersistenceInitializer.getDBParamsFilePath());
            connection = DriverManager.getConnection(dbProps.getProperty("url"), "medc", "medc");
            RelationalAPI.getInstance().getDBAdapter().changePassword("medc", "medc", newPassword, connection);
        }
        catch (final Exception ex) {
            DMOnPremiseService.logger.log(Level.WARNING, "\u00c3\u008bxception occurred while resetting the password. Exception : ", ex);
            throw ex;
        }
        finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            }
            catch (final Exception c) {
                DMOnPremiseService.logger.log(Level.SEVERE, "Error while closing the connection", c);
            }
        }
        return true;
    }
    
    public boolean updateRemoteDBPasswordInDBConf(String password) throws IOException, PersistenceException, PasswordException {
        final String fileNameWithAbsolutePath = PersistenceInitializer.getDBParamsFilePath();
        final File dbparamsPath = new File(fileNameWithAbsolutePath);
        password = PersistenceUtil.getDBPasswordProvider("postgres").getEncryptedPassword(password);
        final StringBuffer buffer = new StringBuffer();
        BufferedReader br = null;
        BufferedWriter bw = null;
        FileReader fr = null;
        FileWriter fw = null;
        try {
            fr = new FileReader(dbparamsPath);
            br = new BufferedReader(fr);
            String str = br.readLine();
            boolean addedNewPassword = false;
            while (str != null) {
                str = str.trim();
                if (str.matches("(#*)(r_password)(=| ).*")) {
                    if (!addedNewPassword) {
                        buffer.append("r_password=" + password + "\n");
                        addedNewPassword = true;
                    }
                }
                else {
                    buffer.append(str + "\n");
                }
                str = br.readLine();
            }
            if (!addedNewPassword) {
                buffer.append("r_password=" + password + "\n");
                addedNewPassword = true;
            }
            fw = new FileWriter(dbparamsPath);
            bw = new BufferedWriter(fw);
            bw.write(buffer.toString());
        }
        catch (final Exception ex) {
            DMOnPremiseService.logger.log(Level.SEVERE, "Error while updating remote access db password", ex);
            return false;
        }
        finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (bw != null) {
                    bw.close();
                }
                if (fr != null) {
                    fr.close();
                }
                if (fw != null) {
                    fw.close();
                }
            }
            catch (final Exception e) {
                DMOnPremiseService.logger.log(Level.SEVERE, "Error while closing stream", e);
            }
        }
        return true;
    }
    
    static {
        DMOnPremiseService.logger = Logger.getLogger("DCServiceLogger");
        DMOnPremiseService.isAllowToStartServer = true;
        DMOnPremiseService.server_migration_temp = System.getProperty("server.home") + File.separator + "conf" + File.separator + "server-migration-temp.conf";
    }
}
