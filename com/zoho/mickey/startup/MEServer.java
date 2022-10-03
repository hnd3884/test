package com.zoho.mickey.startup;

import java.util.Hashtable;
import javax.swing.JFrame;
import com.adventnet.mfw.SplashScreen;
import com.zoho.framework.utils.crypto.EncryptionHandler;
import com.zoho.mickey.crypto.DBPasswordProvider;
import com.adventnet.persistence.ConfigurationParser;
import com.zoho.framework.utils.crypto.CryptoUtil;
import com.zoho.framework.utils.crypto.EnDecryptUtil;
import com.adventnet.persistence.StandAlone;
import java.net.URLClassLoader;
import com.zoho.net.handshake.HandShakePacket;
import com.zoho.net.handshake.HandShakeClient;
import com.adventnet.ds.query.DataSet;
import java.sql.Statement;
import java.io.IOException;
import com.adventnet.ds.query.QueryConstructionException;
import com.zoho.net.handshake.HandShakeUtil;
import com.adventnet.ds.query.Query;
import com.zoho.framework.utils.archive.ZipUtils;
import com.zoho.framework.utils.FileUtils;
import java.util.Date;
import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;
import java.awt.GraphicsEnvironment;
import com.adventnet.mfw.service.ServiceUtil;
import com.adventnet.persistence.cache.CacheService;
import java.text.SimpleDateFormat;
import com.adventnet.ds.DSUtil;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.persistence.personality.PersonalityConfigurationUtil;
import com.adventnet.persistence.DataAccessException;
import java.net.UnknownHostException;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import com.adventnet.persistence.SchemaBrowserUtil;
import com.adventnet.mfw.modulestartup.ModuleStartStopProcessorUtil;
import com.adventnet.ds.query.SortColumn;
import com.zoho.framework.utils.OSCheckUtil;
import com.adventnet.mfw.RestoreDB;
import com.adventnet.db.adapter.RestoreResult;
import java.util.Map;
import java.util.HashMap;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.db.adapter.DBAdapter;
import java.sql.Connection;
import java.net.InetAddress;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.persistence.PersistenceUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.persistence.Persistence;
import com.adventnet.db.adapter.BackupHandler;
import java.sql.Timestamp;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.db.adapter.BackupRestoreConfigurations;
import com.adventnet.db.adapter.BackupDBParams;
import com.adventnet.mfw.BackupDB;
import com.adventnet.db.adapter.BackupResult;
import com.zoho.conf.Configuration;
import com.adventnet.mfw.service.ServiceStarter;
import com.zoho.mickey.ha.HAUtil;
import com.adventnet.persistence.fos.FOS;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.xml.Xml2DoConverter;
import com.adventnet.mfw.ServerFailureException;
import com.adventnet.persistence.PersistenceInitializer;
import com.adventnet.persistence.ConcurrentStartupUtil;
import com.adventnet.mfw.ConsoleOut;
import com.adventnet.mfw.Starter;
import java.util.Properties;
import com.adventnet.mfw.message.MessageFilter;
import com.adventnet.mfw.message.Messenger;
import com.adventnet.mfw.message.MessageListener;
import java.util.logging.Level;
import java.io.File;
import com.adventnet.mfw.ServerFailureHandler;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;
import com.adventnet.mfw.ServerInterface;

public class MEServer implements ServerInterface
{
    private static final Logger OUT;
    private boolean cold;
    private static boolean shutDownCompleted;
    private static String serverPath;
    private String confPath;
    int port;
    String mysqlstr;
    private DataObject serverStatusDO;
    private Row serverStatusRow;
    private static ServerFailureHandler serverFailure;
    private DataObject module;
    private boolean isAllModulesPopulated;
    private static String cur_module;
    
    public MEServer() {
        this.cold = false;
        this.confPath = MEServer.serverPath + File.separator + "conf" + File.separator;
        this.port = 33306;
        this.mysqlstr = "NA";
        this.serverStatusDO = null;
        this.serverStatusRow = null;
        this.module = null;
        this.isAllModulesPopulated = true;
        MEServer.OUT.log(Level.INFO, "Creating new Server instance");
        try {
            final String startupListener = System.getProperty("startup.listener");
            if (startupListener != null) {
                final MessageListener startupListenerInstance = (MessageListener)Thread.currentThread().getContextClassLoader().loadClass(startupListener).newInstance();
                Messenger.subscribe("startupNotification", startupListenerInstance, true, null);
            }
        }
        catch (final Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    public void setSplashMessage(final String message, final int progress) {
        if (!this.checkForSplashScreen()) {
            return;
        }
        try {
            final Properties props = new Properties();
            props.setProperty("TopicName", "splashNotification");
            props.setProperty("message", message);
            props.setProperty("progress", Integer.toString(progress));
            Messenger.publish("splashNotification", props);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public void startServer(final Properties startupNotifyProps) throws Throwable {
        this.validateStartup();
        startupNotifyProps.setProperty("message", "Going to start Server");
        this.sendStartUpNotification(startupNotifyProps);
        final long startTime = System.currentTimeMillis();
        MEServer.serverFailure = Starter.getServerFailureHandler();
        try {
            if (this.checkForSplashScreen()) {
                new Thread() {
                    @Override
                    public void run() {
                        final SplashScreenThread ss = new SplashScreenThread();
                        ss.run();
                    }
                }.start();
            }
            if (Boolean.getBoolean("development.mode")) {
                ConsoleOut.println("\n\t\t\t\t\t\t**********************\n\t\t\t\t\t\t   DEVELOPMENT MODE\n\t\t\t\t\t\t**********************\n\n");
            }
            this.setSplashMessage("Initializing the Database ...", 0);
            int serverStatus = -1;
            final Properties properties = new Properties();
            try {
                this.handleEncryptionOfConfValuesAndSensitiveData();
                ServerFailureException sfe = null;
                properties.setProperty("IsDBInitialized", "true");
                properties.setProperty("IsPersistenceModuleLoaded", "true");
                try {
                    if (ConcurrentStartupUtil.isConcurrentTableCreationEnabled()) {
                        ConcurrentStartupUtil.initializeConcurrentStartupUtil();
                    }
                    PersistenceInitializer.initialize(this.confPath);
                }
                catch (final Exception e) {
                    if (e.getMessage().equals("Exception while initializing DB.")) {
                        properties.setProperty("IsDBInitialized", "false");
                        properties.setProperty("IsPersistenceModuleLoaded", "false");
                        sfe = new ServerFailureException(10001, (Throwable)e);
                    }
                    else if (e.getMessage().equals("Exception while initializing Persistence Module.")) {
                        properties.setProperty("IsPersistenceModuleLoaded", "false");
                        sfe = new ServerFailureException(10008, (Throwable)e);
                    }
                    if (!Starter.isSafeStart()) {
                        throw sfe;
                    }
                    e.printStackTrace();
                }
                this.checkForDBMStatus();
                DataObject module_DO = null;
                try {
                    this.serverStatusDO = this.getServerStatusDO();
                    module_DO = this.getDO("Module", null);
                }
                catch (final Exception e2) {
                    if (!Starter.isSafeStart()) {
                        throw e2;
                    }
                    e2.printStackTrace();
                }
                if (this.serverStatusDO != null && this.serverStatusDO.isEmpty() && module_DO != null && module_DO.isEmpty()) {
                    if (Starter.isSafeStart()) {
                        ConsoleOut.println("Safe mode is not possible during cold start...");
                        throw new ServerFailureException(10003, "Safe mode is not possible during cold start...");
                    }
                    this.cold = true;
                    this.isAllModulesPopulated = false;
                    MEServer.OUT.log(Level.INFO, "Server startup mode is COLD");
                    this.updateStatus(1, true);
                    this.module = Xml2DoConverter.transform(new File(this.confPath + "module.xml").toURI().toURL());
                    try {
                        DataAccess.getTransactionManager().begin();
                        DataAccess.add(this.module);
                        DataAccess.getTransactionManager().commit();
                    }
                    catch (final Exception e2) {
                        DataAccess.getTransactionManager().rollback();
                        throw new ServerFailureException(10007, (Throwable)e2);
                    }
                    ConsoleOut.println("Loading Modules\n");
                }
                else {
                    boolean addNode = false;
                    if (this.serverStatusDO != null && this.serverStatusDO.isEmpty()) {
                        this.serverStatusDO = this.getDO("ServerStatus", null);
                        if (FOS.isEnabled() || HAUtil.isHAEnabled() || HAUtil.isDataBaseHAEnabled()) {
                            MEServer.OUT.log(Level.INFO, "DO taken for processing current node {0}", new Object[] { this.serverStatusDO });
                            addNode = true;
                        }
                        else {
                            MEServer.OUT.log(Level.INFO, "Updating the server name in serverstatus table with current machine name");
                            this.updateServerName();
                        }
                    }
                    if (!Starter.isSafeStart() || (Starter.isSafeStart() && this.serverStatusDO != null && !this.serverStatusDO.isEmpty())) {
                        final int starting = serverStatus = (int)this.serverStatusDO.getFirstValue("ServerStatus", "STATUS");
                        if (serverStatus == 5) {
                            ConsoleOut.println("Previous start failed. Please, Reinitialize DB and restart server");
                            throw new ServerFailureException(10004, "Previous start failed. Please, Reinitialize DB and restart server");
                        }
                        if (serverStatus == 6) {
                            ConsoleOut.println("Previous Patch not applied properly, hence contact support team");
                            throw new ServerFailureException(10005, "Previous Patch not applied properly, hence contact support team");
                        }
                        if (serverStatus == 4) {
                            this.isAllModulesPopulated = false;
                            MEServer.OUT.log(Level.INFO, "Server startup mode is COLD");
                        }
                        else {
                            MEServer.OUT.log(Level.INFO, "Server startup mode is WARM");
                            if (addNode) {
                                MEServer.OUT.log(Level.INFO, "new node is adding in FOS system. Previous node's serverstatus DO {0}", new Object[] { this.serverStatusDO });
                                this.updateStatus(1, true);
                            }
                        }
                        MEServer.OUT.log(Level.INFO, "status :: " + serverStatus);
                        this.updateStatus(1, false);
                    }
                    try {
                        this.module = this.getDO("Module", "MODULEORDER");
                    }
                    catch (final Exception e3) {
                        if (!Starter.isSafeStart()) {
                            throw e3;
                        }
                        e3.printStackTrace();
                    }
                    ConsoleOut.println("Modules already Populated\n");
                }
                this.checkForPPMErrors(this.serverStatusDO.getRow("ServerStatus"));
                if (this.module != null) {
                    this.populateModules(this.module, serverStatus, properties);
                }
                else {
                    ConsoleOut.println("UNABLE TO LOAD MODULES..");
                }
                this.isAllModulesPopulated = true;
            }
            catch (final Throwable e4) {
                e4.printStackTrace();
                if (this.cold || serverStatus == 4) {
                    this.updateStatus(4, false);
                }
                throw e4;
            }
            try {
                ServiceStarter.initServices();
            }
            catch (final Throwable e4) {
                throw new ServerFailureException(10006, e4);
            }
            if (!Starter.isSafeStart()) {
                this.enableCacheing();
            }
            startupNotifyProps.setProperty("TopicName", "startupNotification");
            final long currentTime = System.currentTimeMillis();
            final long startupTime = currentTime - startTime;
            Configuration.setString("serverstarttime", String.valueOf(startupTime));
            startupNotifyProps.setProperty("start.type", this.cold ? "cold" : "warm");
            startupNotifyProps.setProperty("message", "Server Started");
            MEServer.OUT.log(Level.INFO, "Startup Notification Props :: " + startupNotifyProps);
            this.sendStartUpNotification(startupNotifyProps);
            if (!Starter.isSafeStart() || (Starter.isSafeStart() && this.serverStatusDO != null && !this.serverStatusDO.isEmpty())) {
                this.updateStatus(2, false);
            }
            this.setSplashMessage("Server Started.", 100);
            if (Starter.isSafeStart()) {
                MEServer.OUT.log(Level.INFO, "Safe Start Properties :: {0}", properties);
                Messenger.publish("SafeStartTopic", properties);
            }
        }
        catch (final Throwable e5) {
            ConsoleOut.println("");
            MEServer.OUT.log(Level.SEVERE, "STACK TRACE", e5);
            if (e5 instanceof ServerFailureException) {
                MEServer.serverFailure.handle((ServerFailureException)e5);
            }
            this.shutDown(false);
            throw e5;
        }
        finally {
            if (ConcurrentStartupUtil.isConcurrentTableCreationEnabled()) {
                ConcurrentStartupUtil.cleanup();
            }
        }
    }
    
    protected void validateStartup() throws Exception {
        if ((HAUtil.isHAEnabled() || FOS.isEnabled()) && HAUtil.isDataBaseHAEnabled()) {
            throw new ServerFailureException((Throwable)new Exception("Both HA and DataBase HA are enabled at the same time. Either of them should be enabled at a given times"));
        }
    }
    
    private void checkForPPMErrors(final Row serverStatusRow) throws Exception {
        final int ppmStatus = (int)serverStatusRow.get(3);
        if (ppmStatus == 6) {
            throw new ServerFailureException(10005, "PPM was not installed/reverted improperly, hence server cannot be started unless error rectified. Please contact support team.");
        }
    }
    
    public int backupDB(final String backupDir, final String backupFile, final int backupContentType, final String archivePassword) throws Exception {
        return this.backupDatabase(backupDir, backupFile, backupContentType, archivePassword).getBackupStatus().getValue();
    }
    
    protected BackupResult backupDatabase(final String backupDir, String backupFile, final int backupContentType, final String archivePassword) throws Exception {
        try {
            BackupResult backupResult = null;
            BackupDB.BACKUP_DB_USING_SCRIPTS = true;
            BackupDB.SHOW_STATUS = true;
            backupFile = this.getBackUpFileName("OfflineBackup_", backupFile);
            if (new File(backupDir + File.separator + backupFile).exists()) {
                throw new IllegalArgumentException("Backup File [" + backupFile + "] already exists at [" + backupDir + "]");
            }
            ConsoleOut.println("");
            ConsoleOut.println("Backup Directory : " + backupDir);
            ConsoleOut.println("Backup File Name : " + backupFile);
            final String confFileDir = MEServer.serverPath + "/conf/";
            PersistenceInitializer.initializeDB(confFileDir);
            PersistenceInitializer.initializeMickey(true);
            final BackupDBParams params = new BackupDBParams();
            params.incrementalBackupEnabled = false;
            params.backupStartTime = System.currentTimeMillis();
            params.backupContentType = BackupRestoreConfigurations.BACKUP_CONTENT_TYPE.values()[backupContentType - 1];
            params.backupType = BackupRestoreConfigurations.BACKUP_TYPE.FULL_BACKUP;
            params.backupFolder = new File(backupDir);
            params.backupLabelWaitDuration = 20;
            params.backupMode = BackupRestoreConfigurations.BACKUP_MODE.OFFLINE_BACKUP;
            params.zipFileName = backupFile;
            params.archivePassword = archivePassword;
            final BackupHandler backupHandler = RelationalAPI.getInstance().getDBAdapter().getBackupHandler();
            MEServer.OUT.log(Level.INFO, "backupParams :: {0}", params);
            final Properties backupProps = new Properties();
            backupProps.setProperty("backup.content.type", params.backupContentType.name().toLowerCase());
            if (!backupHandler.isValid(backupProps)) {
                throw new Exception("Backup failed");
            }
            backupResult = backupHandler.doBackup(params);
            backupHandler.doCleanup(backupResult.getFilesToBeCleaned());
            if (backupResult.getBackupStatus() == BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_SUCCESSFULLY_COMPLETED) {
                ConsoleOut.println("Backup Start Time : " + new Timestamp(backupResult.getStartTime()));
                ConsoleOut.println("Backup End Time   : " + new Timestamp(backupResult.getEndTime()));
                ConsoleOut.println("Backup Duration   : " + backupResult.getDuration() + " ms");
                ConsoleOut.println("");
                MEServer.OUT.log(Level.INFO, "BackedUp Successfully");
                return backupResult;
            }
            throw new Exception("Backup failed");
        }
        catch (final Exception e) {
            this.printBackupStatus(BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_PROCESS_FAILED.getValue());
            throw e;
        }
        finally {
            BackupDB.SHOW_STATUS = false;
            BackupDB.BACKUP_DB_USING_SCRIPTS = false;
            try {
                PersistenceInitializer.stopDB();
            }
            catch (final Exception e2) {
                ConsoleOut.println(e2.getMessage());
                MEServer.OUT.log(Level.INFO, "Problem while Stopping DB.", e2);
            }
        }
    }
    
    private void printBackupStatus(final int backUpStatus) {
        if (backUpStatus == BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_SUCCESSFULLY_COMPLETED.getValue()) {
            ConsoleOut.println("\nBackedUp Successfully.");
        }
        else if (backUpStatus == BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_PROCESS_FAILED.getValue()) {
            ConsoleOut.println("\nProblem while taking backUp");
        }
        else if (backUpStatus == BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_PROCESS_IN_PROGRESS.getValue()) {
            ConsoleOut.println("\nAlready a backup / restore process is started, so please try again after some time.");
        }
    }
    
    public int reinitialize(final boolean allTables) {
        this.deleteRestoreTouchFiles();
        Connection conn = null;
        try {
            if (!Starter.checkShutdownListenerPort()) {
                ConsoleOut.println("\nServer seems to be running. Please shutdown server and then reinitialize\n");
                return 1;
            }
            final String confFileDir = MEServer.serverPath + File.separator + "conf" + File.separator;
            PersistenceInitializer.initializeDB(confFileDir);
            final RelationalAPI relAPI = RelationalAPI.getInstance();
            conn = relAPI.getConnection();
            final DBAdapter adapter = relAPI.getDBAdapter();
            if (!RelationalAPI.getInstance().getDBAdapter().isTablePresentInDB(conn, (String)null, "SeqGenState")) {
                ConsoleOut.println("\nNo Tables found in the specified DB.");
                return 0;
            }
            PersistenceInitializer.initializeMickey(false, false);
            if (PersistenceInitializer.getConfigurationValue("DBName").equals("postgres") && adapter.isBundledDB()) {
                final Persistence persistence = (Persistence)BeanUtil.lookup("Persistence");
                final DataObject DBAuditDO = persistence.get("DBCredentialsAudit", new Criteria(Column.getColumn("DBCredentialsAudit", "USERNAME"), (Object)"postgres", 0));
                try {
                    final Row userCred = DBAuditDO.getFirstRow("DBCredentialsAudit");
                    final String superUserPass = (String)userCred.get("PASSWORD");
                    if (!PersistenceUtil.addKeyInDBConf("superuser_pass", PersistenceUtil.getDBPasswordProvider("postgres").getEncryptedPassword(superUserPass))) {
                        ConsoleOut.println("DB Credential write to database_params.conf failed, should be added manually");
                        MEServer.OUT.log(Level.WARNING, "DB Credential write to database_params.conf failed. Required Credentials should be added manually");
                    }
                }
                catch (final Exception e) {
                    ConsoleOut.println("DB Credential write to database_params.conf failed");
                    MEServer.OUT.log(Level.WARNING, "There are no matching rows present in Audit table hence write to database_params.conf failed. Required Credentials should be added manually");
                }
            }
            try {
                final UpdateQuery uq = (UpdateQuery)new UpdateQueryImpl("ServerStatus");
                uq.setUpdateColumn("STATUS", (Object)5);
                final Criteria c = new Criteria(new Column("ServerStatus", "SERVERNAME"), (Object)InetAddress.getLocalHost().getHostName(), 0);
                uq.setCriteria(c);
                DataAccess.update(uq);
            }
            catch (final Exception ex) {}
            final boolean onlyProductTables = !allTables;
            adapter.dropAllTables(conn, onlyProductTables);
        }
        catch (final Exception e2) {
            ConsoleOut.println("Problem while reinitializing the DB");
            if (e2.getMessage() == null || !e2.getMessage().startsWith("Already Server seems to be running")) {
                e2.printStackTrace();
            }
            final int n = 1;
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception e3) {
                e3.printStackTrace();
                MEServer.OUT.log(Level.SEVERE, "Problem while closing the Database Connection..\n");
            }
            finally {
                try {
                    PersistenceInitializer.stopDB();
                }
                catch (final Exception ex2) {}
            }
            return n;
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception e4) {
                e4.printStackTrace();
                MEServer.OUT.log(Level.SEVERE, "Problem while closing the Database Connection..\n");
                try {
                    PersistenceInitializer.stopDB();
                }
                catch (final Exception ex3) {}
            }
            finally {
                try {
                    PersistenceInitializer.stopDB();
                }
                catch (final Exception ex4) {}
            }
        }
        return 0;
    }
    
    private void deleteRestoreTouchFiles() {
        final Map<Integer, String> restoreStatus = new HashMap<Integer, String>();
        restoreStatus.put(1, "restore_params_generated");
        restoreStatus.put(2, "restore_valid");
        restoreStatus.put(3, "restore_started");
        restoreStatus.put(4, "restore_db_completed");
        restoreStatus.put(5, "restore_status_updated");
        File checkStatusFile = null;
        for (int restoreLevel = 1; restoreLevel <= 5; ++restoreLevel) {
            checkStatusFile = new File(MEServer.serverPath + File.separator + "bin" + File.separator + restoreStatus.get(restoreLevel));
            if (checkStatusFile != null & checkStatusFile.exists()) {
                checkStatusFile.delete();
            }
        }
    }
    
    public int restoreDB(final String src, final String password) throws Exception {
        return this.restoreDatabase(src, password).getRestoreStatus().getValue();
    }
    
    protected RestoreResult restoreDatabase(String src, final String password) throws Exception {
        try {
            RestoreResult restoreResult = null;
            RestoreDB.RESTORING_DB_USING_SCRIPTS = true;
            final String confFileDir = MEServer.serverPath + "/conf/";
            PersistenceInitializer.initializeRelationalAPI(confFileDir);
            final DBAdapter adapter = RelationalAPI.getInstance().getDBAdapter();
            if (OSCheckUtil.isWindows(OSCheckUtil.getOS())) {
                src = src.replace("/", "\\");
            }
            ConsoleOut.println("Restore DB Started");
            ConsoleOut.println("");
            ConsoleOut.println("File Name          : " + src);
            restoreResult = adapter.getRestoreHandler().restoreBackup(src, password);
            final String msg = (restoreResult.getBackupMode() == BackupRestoreConfigurations.BACKUP_MODE.FILE_BACKUP) ? "Files" : "DB";
            if (restoreResult.getRestoreStatus() == BackupRestoreConfigurations.RESTORE_STATUS.RESTORE_SUCCESSFULLY_COMPLETED) {
                ConsoleOut.println("Restore Start Time : " + new Timestamp(restoreResult.getStartTime()));
                ConsoleOut.println("Restore End Time   : " + new Timestamp(restoreResult.getEndTime()));
                ConsoleOut.println("Restore Duration   : " + restoreResult.getDuration() + " ms");
                MEServer.OUT.log(Level.INFO, "{0} Restored Successfully", msg);
                ConsoleOut.println("\n" + msg + " Restored Successfully");
            }
            else if (restoreResult.getRestoreStatus() == BackupRestoreConfigurations.RESTORE_STATUS.RESTORE_PROCESS_FAILED) {
                ConsoleOut.println("\nProblem while restoring db");
            }
            return restoreResult;
        }
        catch (final Exception e) {
            ConsoleOut.println("\nProblem while restoring db");
            MEServer.OUT.log(Level.SEVERE, "Problem while restoring db");
            throw e;
        }
        finally {
            RestoreDB.RESTORING_DB_USING_SCRIPTS = false;
        }
    }
    
    private void populateModules(final DataObject module, final int serverStatus, final Properties properties) throws Exception {
        final SortColumn sc = new SortColumn("Module", "MODULEORDER", true);
        module.sortRows("Module", new SortColumn[] { sc });
        final Iterator it = module.getRows("Module");
        final int size = module.size("Module");
        final int progressRate = 60 / size;
        int progress = 0;
        boolean isLoadModule = true;
        boolean isModulesPopulated = false;
        final long totalPopulationTime = 0L;
        if (this.cold && ConcurrentStartupUtil.isConcurrentTableCreation()) {
            MEServer.OUT.log(Level.INFO, "Concurrent server startup ");
            isModulesPopulated = ConcurrentStartupUtil.concurrentModuleCreation();
        }
        else {
            MEServer.OUT.log(Level.INFO, "Normal startup");
        }
        while (!isModulesPopulated && it.hasNext()) {
            final Row modRow = it.next();
            final String modName = (String)modRow.get("MODULENAME");
            properties.setProperty("Is" + modName + "ModuleLoaded", "true");
            final boolean isPopulated = (boolean)((modRow.get("ISPOPULATED") == null) ? isLoadModule : modRow.get("ISPOPULATED"));
            try {
                ModuleStartStopProcessorUtil.execute_preStartProcesses(modName);
            }
            catch (final Exception e) {
                final ServerFailureException newException = new ServerFailureException(10014, "Exception while executing Module Pre invokation process in module" + modName);
                newException.initCause((Throwable)e);
                throw newException;
            }
            ConsoleOut.print(modName);
            this.setSplashMessage("Initializing the " + modName + " Module ...", progress);
            MEServer.cur_module = modName;
            Label_0524: {
                if (!this.cold) {
                    if (serverStatus != 4 || isPopulated) {
                        try {
                            PersistenceInitializer.loadModule(modName);
                            print(modName, " LOADED ");
                        }
                        catch (final Exception e) {
                            if (!Starter.isSafeStart()) {
                                throw new ServerFailureException(10002, (Throwable)new Exception("Module Loading Failed : " + modName, e));
                            }
                            print(modName, " FAILED ");
                            properties.setProperty("Is" + modName + "ModuleLoaded", "false");
                            e.printStackTrace();
                        }
                        break Label_0524;
                    }
                }
                try {
                    isLoadModule = false;
                    PersistenceInitializer.addModule(modName);
                    print(modName, "POPULATED");
                }
                catch (final Exception e) {
                    if (!Starter.isSafeStart()) {
                        throw new ServerFailureException(10002, "Module Loading Failed : " + modName, (Throwable)e);
                    }
                    e.printStackTrace();
                }
            }
            MEServer.cur_module = null;
            if (this.cold || serverStatus == 4) {
                modRow.set("ISPOPULATED", (Object)true);
                module.updateRow(modRow);
            }
            if (PersistenceInitializer.onSAS()) {
                PersistenceInitializer.loadBeans(this.confPath + modName);
                PersistenceInitializer.loadServices(this.confPath + modName);
            }
            try {
                ModuleStartStopProcessorUtil.execute_postStartProcesses(modName);
            }
            catch (final Exception e) {
                final ServerFailureException newException = new ServerFailureException(10015, "Exception while executing Module Post invokation process in module" + modName);
                newException.initCause((Throwable)e);
                throw newException;
            }
            progress += progressRate;
        }
        SchemaBrowserUtil.setReady(true);
    }
    
    private DataObject getDO(final String tableName, final String colName) throws Exception {
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable(tableName));
        sq.addSelectColumn(Column.getColumn(tableName, "*"));
        if (colName != null) {
            sq.addSortColumn(new SortColumn(Column.getColumn(tableName, colName), true));
        }
        final DataObject dobj = DataAccess.get(sq);
        return dobj;
    }
    
    private DataObject getServerStatusDO() throws DataAccessException {
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("ServerStatus"));
        sq.addSelectColumn(Column.getColumn("ServerStatus", "*"));
        String serverName;
        try {
            serverName = InetAddress.getLocalHost().getHostName();
        }
        catch (final UnknownHostException e) {
            MEServer.OUT.info("Error in getting Cannot get InetAddress localhost name");
            serverName = "localhost";
            e.printStackTrace();
        }
        final Criteria c = new Criteria(new Column("ServerStatus", "SERVERNAME"), (Object)serverName, 0);
        sq.setCriteria(c);
        final DataObject dobj = DataAccess.get(sq);
        MEServer.OUT.finer("server status DO:" + dobj);
        return dobj;
    }
    
    private void removeModule(final Row row) {
        try {
            if (new Integer(row.get(3).toString()) == 4) {
                if (Configuration.getString("development.mode", "false").equalsIgnoreCase("false")) {
                    row.set(3, (Object)new Integer(5));
                    return;
                }
                if (ConcurrentStartupUtil.isConcurrentModulePopulation()) {
                    MEServer.OUT.log(Level.SEVERE, "Removing module under development mode is not supported when 'Hierarchical Module Population' is enabled");
                    row.set(3, (Object)new Integer(5));
                    return;
                }
                if (MEServer.cur_module != null) {
                    final DataObject dataObject = DataAccess.get("PersonalityConfiguration", new Criteria(Column.getColumn("PersonalityConfiguration", "MODULENAME"), (Object)MEServer.cur_module, 0));
                    if (!dataObject.isEmpty()) {
                        PersonalityConfigurationUtil.removePersonalityConfiguration(MEServer.cur_module);
                    }
                    if (MetaDataUtil.getDataDictionary(MEServer.cur_module) != null) {
                        DataAccess.dropTables(MEServer.cur_module);
                    }
                    DataAccess.delete(new Criteria(Column.getColumn("SB_Applications", "APPL_NAME"), (Object)MEServer.cur_module, 0));
                    final Row modRow = this.module.getRow("Module", new Criteria(Column.getColumn("Module", "MODULENAME"), (Object)MEServer.cur_module, 0));
                    if (modRow != null) {
                        modRow.set("ISPOPULATED", (Object)false);
                        this.module.updateRow(modRow);
                    }
                    DataAccess.update(this.module);
                }
            }
        }
        catch (final Exception e) {
            MEServer.OUT.log(Level.INFO, "Exception while removing the module " + MEServer.cur_module, e.getCause());
            e.printStackTrace();
            row.set(3, (Object)new Integer(5));
        }
    }
    
    private void updateStatus(final int status, final boolean add) throws Exception {
        try (final Connection connection = RelationalAPI.getInstance().getConnection()) {
            if (RelationalAPI.getInstance().getDBAdapter().isReadOnly(connection)) {
                return;
            }
        }
        final String readOnly = PersistenceInitializer.getConfigurationValue("ReadOnly");
        if (readOnly != null && readOnly.equalsIgnoreCase("true")) {
            return;
        }
        try {
            String serverName = null;
            if (add) {
                this.serverStatusDO = (DataObject)new WritableDataObject();
                final Row state = new Row("ServerStatus");
                try {
                    serverName = InetAddress.getLocalHost().getHostName();
                    Configuration.setString("server.name", serverName);
                }
                catch (final Exception e) {
                    serverName = "localhost";
                    MEServer.OUT.log(Level.WARNING, "Cannot get InetAddress localhost name");
                    if (Configuration.getString("resolve.servername", "false").equalsIgnoreCase("true")) {
                        ConsoleOut.println("");
                        ConsoleOut.println("Cannot get InetAddress for localhost.");
                        ConsoleOut.println("");
                        throw e;
                    }
                }
                state.set(2, (Object)serverName);
                state.set(3, (Object)new Integer(status));
                this.removeModule(state);
                this.serverStatusDO.addRow(state);
                this.serverStatusRow = state;
            }
            else {
                if (this.serverStatusDO == null || this.serverStatusDO.isEmpty()) {
                    throw new RuntimeException("There are no rows in ServerStatus table!!");
                }
                final Row row = this.serverStatusDO.getFirstRow("ServerStatus");
                final int server_status = (int)row.get(3);
                MEServer.OUT.log(Level.INFO, "DB Server status number :: " + server_status);
                MEServer.OUT.log(Level.INFO, "Server status number :: " + status);
                if (server_status != 7) {
                    if (!this.isAllModulesPopulated && status == 3) {
                        row.set(3, (Object)new Integer(5));
                    }
                    else {
                        MEServer.OUT.log(Level.INFO, "Updateing status ::::: " + status);
                        row.set(3, (Object)new Integer(status));
                    }
                }
                else {
                    MEServer.OUT.log(Level.INFO, "else part :: " + status);
                    if (status == 2) {
                        row.set(3, (Object)new Integer(2));
                    }
                }
                this.removeModule(row);
                this.serverStatusDO.updateRow(row);
            }
            try {
                DataAccess.getTransactionManager().begin();
                DataAccess.update(this.serverStatusDO);
                DataAccess.getTransactionManager().commit();
            }
            catch (final Exception e2) {
                DataAccess.getTransactionManager().rollback();
                throw e2;
            }
        }
        catch (final Exception e3) {
            MEServer.OUT.log(Level.WARNING, "Exception while updating ServerStatus table");
            MEServer.OUT.log(Level.FINE, "Exception while updating ServerStatus table", e3.getCause());
            throw e3;
        }
    }
    
    private static void print(final String moduleName, final String status) {
        for (int i = moduleName.length(); i < 50; ++i) {
            ConsoleOut.print(" ");
        }
        ConsoleOut.println("[" + status + "]");
    }
    
    public void shutDown(final boolean normal) {
        if (this.isDebugMode()) {
            DSUtil.dumpInUseConnections();
        }
        boolean serviceStopped = true;
        MEServer.shutDownCompleted = true;
        try {
            final RelationalAPI relAPI = RelationalAPI.getInstance();
            if (relAPI != null) {
                try {
                    relAPI.getDBAdapter().abortBackup();
                }
                catch (final Exception e) {
                    MEServer.OUT.warning(e.getMessage());
                }
            }
            Label_0123: {
                if (normal) {
                    ConsoleOut.println("Shutting down the JVM now!");
                    if (Starter.isSafeStart()) {
                        if (!Starter.isSafeStart() || this.serverStatusDO == null || this.serverStatusDO.isEmpty()) {
                            break Label_0123;
                        }
                    }
                    try {
                        this.updateStatus(3, false);
                    }
                    catch (final Exception e) {
                        MEServer.shutDownCompleted = false;
                        MEServer.OUT.log(Level.WARNING, "Exception occurred while updating Server Status");
                        e.printStackTrace();
                    }
                }
            }
            Starter.shutDownDiskSpaceMonitor();
            final Properties shutdownProps = new Properties();
            shutdownProps.setProperty("TopicName", "SERVER_SHUTDOWN_NOTIFICATION");
            shutdownProps.setProperty("ShutDownInvocationTime", new Timestamp(System.currentTimeMillis()).toString());
            try {
                Messenger.publish("SERVER_SHUTDOWN_NOTIFICATION", shutdownProps);
            }
            catch (final Exception e2) {
                MEServer.shutDownCompleted = false;
                MEServer.OUT.log(Level.WARNING, "Exception occurred while notifying Server Shutdown Listener");
                e2.printStackTrace();
            }
            serviceStopped = ServiceStarter.destroyServices();
            if (!serviceStopped) {
                final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                ConsoleOut.println("Exception thrown while stopping/destroying the services. Please check the logs for more details.");
            }
            ModuleStartStopProcessorUtil.execute_stopProcesses();
            DSUtil.abortAllConnections(true);
            PersistenceInitializer.stopDB();
        }
        catch (final Exception e3) {
            MEServer.shutDownCompleted = false;
            MEServer.OUT.log(Level.WARNING, "Exception occurred during shutdown");
            e3.printStackTrace();
        }
        finally {
            final File lock = new File(".lock");
            if (lock.exists()) {
                lock.delete();
            }
            Starter.extshutdown = true;
            new Thread() {
                @Override
                public void run() {
                    final int status = normal ? 0 : -1;
                    if (MEServer.shutDownCompleted && Starter.restart) {
                        MEServer.OUT.log(Level.SEVERE, "Going to halt JVM for restarting. Requested from FOS");
                        Runtime.getRuntime().halt(1992);
                    }
                    else {
                        Runtime.getRuntime().exit(status);
                    }
                }
            }.start();
        }
    }
    
    private void enableCacheing() throws Exception {
        final CacheService cacheService = (CacheService)ServiceUtil.lookup("CacheService");
        cacheService.setCacheingStatus(true);
    }
    
    public void sendStartUpNotification(final Properties startupNotifyProps) throws Exception {
        final Properties clonedProperties = (Properties)startupNotifyProps.clone();
        Messenger.publish("startupNotification", clonedProperties);
    }
    
    private boolean checkForSplashScreen() {
        if (GraphicsEnvironment.isHeadless()) {
            return false;
        }
        final String splash = System.getProperty("splash.filename");
        return splash != null && new File(splash).exists();
    }
    
    public int fileBackup(final String backupDir, String backupFileName) throws Exception {
        final Properties props = loadBackupConfFile();
        MEServer.OUT.log(Level.INFO, "properties from conf File :: {0}", props);
        ConsoleOut.println("\nBackup Directory  : " + backupDir);
        backupFileName = this.getBackUpFileName("FileBackup_", backupFileName);
        ConsoleOut.println("Backup File Name  : " + backupFileName);
        final long startTime = System.currentTimeMillis();
        if (props == null) {
            ConsoleOut.println("There is no File/Directory specified in the backup_files.conf");
            return BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_PROCESS_FAILED.getValue();
        }
        final Properties modifiedProps = new Properties();
        final List<String> fileToBeBackup = new ArrayList<String>();
        for (final Map.Entry entry : props.entrySet()) {
            final String canonicalPath = new File(MEServer.serverPath + File.separator + entry.getKey()).getCanonicalPath();
            fileToBeBackup.add(canonicalPath);
            modifiedProps.setProperty(canonicalPath, entry.getValue().toString());
        }
        if (!fileToBeBackup.isEmpty()) {
            final String confFileDir = Configuration.getString("server.home") + "/conf/";
            PersistenceInitializer.initializeRelationalAPI(confFileDir);
            PersistenceInitializer.loadPersistenceConfigurations();
            final String adapterType = PersistenceInitializer.getConfigurationValue("DSAdapter");
            final Properties adapterProps = PersistenceInitializer.getConfigurationProps(adapterType);
            final DBAdapter adapter = PersistenceInitializer.createDBAdapter(adapterProps);
            final int fileBackupStatus = adapter.fileBackup(backupDir, backupFileName, (List)fileToBeBackup, adapterProps.getProperty("VersionHandler"), modifiedProps);
            final long endTime = System.currentTimeMillis();
            if (fileBackupStatus == BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_SUCCESSFULLY_COMPLETED.getValue()) {
                ConsoleOut.println("Backup Start Time : " + new Timestamp(startTime));
                ConsoleOut.println("Backup End Time   : " + new Timestamp(endTime));
                ConsoleOut.println("Backup Duration   : " + (endTime - startTime) + " ms");
            }
            this.printBackupStatus(fileBackupStatus);
            return fileBackupStatus;
        }
        MEServer.OUT.warning("backup_file.conf doesn't contain any entries, hence File backup process ignored");
        ConsoleOut.println("backup_file.conf doesn't contain any entries, hence File backup process ignored");
        return BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_PROCESS_FAILED.getValue();
    }
    
    private String getBackUpFileName(final String prefix, final String backupFile) {
        if (backupFile == null) {
            final Date today = Calendar.getInstance().getTime();
            final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            return prefix + formatter.format(today) + ".ezip";
        }
        if (!backupFile.endsWith(".ezip")) {
            return backupFile + ".ezip";
        }
        return backupFile;
    }
    
    public static Properties loadBackupConfFile() throws Exception {
        final String fileName = "backup_files.conf";
        final File identifierFile = new File(MEServer.serverPath + File.separator + "conf" + File.separator + fileName);
        Properties props = null;
        if (identifierFile.exists()) {
            props = FileUtils.readPropertyFile(identifierFile);
        }
        else {
            MEServer.OUT.log(Level.WARNING, "Identifier [{0}] file not exist for this database", fileName);
        }
        return props;
    }
    
    protected boolean isFileExistsInZip(final String zipNameWithFullPath, final String entryNameWithPackage) throws Exception {
        return ZipUtils.isFileExistsInZip(zipNameWithFullPath, entryNameWithPackage);
    }
    
    public void startDB() throws Exception {
        PersistenceInitializer.loadPersistenceConfigurations();
        final Properties props = PersistenceInitializer.getDBProps(PersistenceInitializer.getDBParamsFilePath());
        final String url = props.getProperty("url");
        PersistenceInitializer.initializeRelationalAPI(MEServer.serverPath + File.separator + "conf");
        RelationalAPI.getInstance().getDBAdapter().setUpDB(url, props.getProperty("username"), props.getProperty("password"));
    }
    
    protected DBAdapter getDBAdapter() throws Exception {
        PersistenceInitializer.loadPersistenceConfigurations();
        final String adapterType = PersistenceInitializer.getConfigurationValue("DSAdapter");
        final Properties configurationProps = PersistenceInitializer.getConfigurationProps(adapterType);
        final DBAdapter adapter = PersistenceInitializer.createDBAdapter(configurationProps);
        adapter.initialize(configurationProps);
        return adapter;
    }
    
    public void stopDB() throws Exception {
        PersistenceInitializer.loadPersistenceConfigurations();
        final Properties dbProps = PersistenceInitializer.getDBProps(PersistenceInitializer.getDBParamsFilePath());
        final String url = dbProps.getProperty("url");
        PersistenceInitializer.initializeRelationalAPI(MEServer.serverPath + File.separator + "conf");
        RelationalAPI.getInstance().getDBAdapter().stopDB(url, dbProps.getProperty("username"), dbProps.getProperty("password"));
    }
    
    private void checkForDBMStatus() throws Exception {
        Connection connection = null;
        Statement statement = null;
        DataSet statusTable = null;
        try {
            MEServer.OUT.info("Checking DBMigration status.");
            connection = RelationalAPI.getInstance().getConnection();
            if (RelationalAPI.getInstance().getDBAdapter().isTablePresentInDB(connection, (String)null, "DBMStatus")) {
                MEServer.OUT.info("DBMStatus table exists in DB.");
                final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("DBMStatus"));
                sq.addSelectColumn(Column.getColumn((String)null, "*"));
                boolean isEntryFound = Boolean.FALSE;
                try {
                    statement = connection.createStatement();
                    statusTable = RelationalAPI.getInstance().executeQuery(RelationalAPI.getInstance().getDBAdapter().getSQLGenerator().getSQLForSelect((Query)sq), connection);
                    while (statusTable.next()) {
                        final HandShakeClient handShakeClient = HandShakeUtil.getHandShakeClient(statusTable.getAsString("HOST_NAME"), statusTable.getInt("LISTEN_PORT"));
                        if (handShakeClient != null) {
                            isEntryFound = Boolean.TRUE;
                            final HandShakePacket pingMessagePacket = handShakeClient.getPingMessageAndExit("PING");
                            if (pingMessagePacket.getMessage().equals("ALIVE")) {
                                ConsoleOut.println("\nDBMigration process seems to be running.\n");
                                ConsoleOut.println(pingMessagePacket.toString());
                                ConsoleOut.println("\nPlease try again after DB migration process completed.");
                                throw new Exception("DBMigration process seems to be running.");
                            }
                            continue;
                        }
                    }
                }
                finally {
                    if (statusTable != null) {
                        statusTable.close();
                    }
                    if (statement != null) {
                        statement.close();
                    }
                }
                if (!isEntryFound) {
                    MEServer.OUT.info("Deleteing DBMStatus table");
                    RelationalAPI.getInstance().dropTable("DBMStatus", false, (List)null);
                }
            }
        }
        catch (final QueryConstructionException e) {
            e.printStackTrace();
        }
        catch (final IOException e2) {
            e2.printStackTrace();
        }
        catch (final ClassNotFoundException e3) {
            e3.printStackTrace();
        }
        finally {
            if (connection != null) {
                try {
                    connection.close();
                }
                catch (final Exception e4) {
                    e4.printStackTrace();
                }
            }
        }
    }
    
    public void runStandAlone() throws Exception {
        this.runStandAlone(null);
    }
    
    public void runStandAlone(final String[] args) throws Exception {
        final String standAloneClass = System.getProperty("run.standalone.class", "com.adventnet.persistence.StandAlonePersistence");
        final URLClassLoader ucl = (URLClassLoader)Thread.currentThread().getContextClassLoader();
        ((StandAlone)ucl.loadClass(standAloneClass).newInstance()).runStandAlone(args);
    }
    
    private void updateServerName() throws Exception {
        final String readOnly = PersistenceInitializer.getConfigurationValue("ReadOnly");
        if (readOnly != null && readOnly.equalsIgnoreCase("true")) {
            return;
        }
        try {
            String serverName = null;
            if (this.serverStatusDO.isEmpty()) {
                MEServer.OUT.severe("Cannot update servername in empty row");
                return;
            }
            final Row row = this.serverStatusDO.getFirstRow("ServerStatus");
            try {
                serverName = InetAddress.getLocalHost().getHostName();
                Configuration.setString("server.name", serverName);
            }
            catch (final Exception e) {
                serverName = "localhost";
                MEServer.OUT.log(Level.WARNING, "Cannot get InetAddress localhost name");
                if (System.getProperty("resolve.servername", "false").equalsIgnoreCase("true")) {
                    ConsoleOut.println("");
                    ConsoleOut.println("Cannot get InetAddress for localhost.");
                    ConsoleOut.println("");
                    throw e;
                }
            }
            row.set(2, (Object)serverName);
            this.serverStatusDO.updateRow(row);
            try {
                DataAccess.getTransactionManager().begin();
                DataAccess.update(this.serverStatusDO);
                DataAccess.getTransactionManager().commit();
            }
            catch (final Exception e) {
                DataAccess.getTransactionManager().rollback();
                throw e;
            }
        }
        catch (final Exception e2) {
            MEServer.OUT.log(Level.WARNING, "Exception while updating ServerStatus table");
            MEServer.OUT.log(Level.FINE, "Exception while updating ServerStatus table", e2.getCause());
            throw e2;
        }
    }
    
    private String generateCryptTag() throws Exception {
        final String existingCryptTag = PersistenceInitializer.getConfigurationValue("CryptTag");
        if (existingCryptTag != null && !existingCryptTag.equals("MLITE_ENCRYPT_DECRYPT")) {
            return null;
        }
        final String newCryptTag = PersistenceUtil.generateRandomValue(20);
        return newCryptTag;
    }
    
    private void handleEncryptionOfConfValuesAndSensitiveData() throws Exception {
        PersistenceInitializer.loadPersistenceConfigurations();
        if (PersistenceInitializer.getConfigurationValue("ECTag") == null) {
            throw new IllegalArgumentException("ECTag value is mandatory in configuration");
        }
        final String cryptTag = this.generateCryptTag();
        if (cryptTag != null) {
            EnDecryptUtil.setCryptTag(cryptTag);
            reEncryptConfigurationValues(cryptTag);
            reEncryptSensitiveData(cryptTag);
        }
    }
    
    private static void reEncryptConfigurationValues(final String generatedCryptTag) throws Exception {
        final HashMap<String, String> configurationProps = new HashMap<String, String>();
        configurationProps.put("CryptTag", generatedCryptTag);
        final List<String> configurations = PersistenceUtil.getEncryptedConfigurations();
        for (final String confKey : configurations) {
            final String decryptedConfValue = PersistenceInitializer.getConfigurationValue(confKey);
            if (decryptedConfValue != null) {
                final String confValue = CryptoUtil.encrypt(decryptedConfValue, generatedCryptTag);
                configurationProps.put(confKey, confValue);
            }
        }
        final HashMap<String, Properties> encryptedProps = new HashMap<String, Properties>();
        final Properties encryptedSqlProps = new Properties();
        if (PersistenceInitializer.getConfigurationProps("mssql") != null) {
            final Properties decryptedSqlProps = PersistenceInitializer.getConfigurationProps("mssql");
            for (final Object sqlProp : ((Hashtable<Object, V>)decryptedSqlProps).keySet()) {
                if (PersistenceUtil.getEncryptedSqlServerProps().contains(sqlProp)) {
                    final String confValue2 = CryptoUtil.encrypt(decryptedSqlProps.getProperty((String)sqlProp), generatedCryptTag);
                    ((Hashtable<Object, String>)encryptedSqlProps).put(sqlProp, confValue2);
                }
            }
        }
        if (!encryptedSqlProps.isEmpty()) {
            encryptedProps.put("mssql", encryptedSqlProps);
        }
        ConfigurationParser.writeExtendedPersistenceConfFile((HashMap)configurationProps, (HashMap)encryptedProps, (HashMap)null, (List)null);
        final DBPasswordProvider dbPasswordProvider = PersistenceUtil.getDBPasswordProvider();
        final Properties dbProps = PersistenceInitializer.getDBProps(PersistenceInitializer.getDBParamsFilePath());
        final Properties decryptedPasswordProps = new Properties();
        ((Hashtable<String, String>)decryptedPasswordProps).put("CryptTag", "MLITE_ENCRYPT_DECRYPT");
        ((Hashtable<String, String>)decryptedPasswordProps).put("password", dbProps.getProperty("password"));
        final String decryptedPassword = dbPasswordProvider.getPassword((Object)decryptedPasswordProps);
        String decryptedSuperUserPassword = null;
        if (dbProps.getProperty("superuser_pass") != null) {
            final Properties decryptedSUPasswordProps = new Properties();
            ((Hashtable<String, String>)decryptedSUPasswordProps).put("CryptTag", "MLITE_ENCRYPT_DECRYPT");
            ((Hashtable<String, String>)decryptedSUPasswordProps).put("password", dbProps.getProperty("superuser_pass"));
            decryptedSuperUserPassword = dbPasswordProvider.getPassword((Object)decryptedSUPasswordProps);
        }
        PersistenceUtil.updatePasswordInDBConf(dbPasswordProvider.getEncryptedPassword(decryptedPassword));
        if (decryptedSuperUserPassword != null) {
            PersistenceUtil.addKeyInDBConf("superuser_pass", dbPasswordProvider.getEncryptedPassword(decryptedSuperUserPassword));
        }
    }
    
    protected static void reEncryptSensitiveData(final String newCryptTag) throws Exception {
        final EncryptionHandler encryptionHandler = EnDecryptUtil.getEncryptionHandler();
        if (encryptionHandler != null) {
            encryptionHandler.handleData("MLITE_ENCRYPT_DECRYPT", newCryptTag);
        }
    }
    
    public long getServerID() {
        try {
            if (this.serverStatusRow != null) {
                final Long id = Long.parseLong(this.serverStatusRow.get(1).toString());
                return id;
            }
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
        return -1L;
    }
    
    private boolean isDebugMode() {
        final boolean developmentMode = Boolean.getBoolean("development.mode");
        final boolean connectionTrack = Boolean.getBoolean("connection.track");
        final boolean debugMode = developmentMode || connectionTrack;
        return debugMode;
    }
    
    static {
        OUT = Logger.getLogger(MEServer.class.getName());
        MEServer.shutDownCompleted = false;
        MEServer.serverPath = ((Configuration.getString("server.home") != null) ? Configuration.getString("server.home") : Configuration.getString("app.home"));
        MEServer.serverFailure = null;
        MEServer.cur_module = null;
    }
    
    class SplashScreenThread extends Thread implements MessageListener
    {
        boolean start;
        SplashScreen splash;
        
        SplashScreenThread() {
            this.start = true;
            this.splash = null;
        }
        
        @Override
        public void run() {
            try {
                Messenger.subscribe("startupNotification", this, true, null);
                Messenger.subscribe("splashNotification", this, true, null);
            }
            catch (final Exception e) {
                MEServer.OUT.warning("Exception occured while subscribing the splash screen ...");
                e.printStackTrace();
            }
            (this.splash = new SplashScreen(new JFrame(), System.getProperty("splash.filename"), true)).setVisible(true);
            boolean alwaysOnTop = true;
            if (!System.getProperty("splash.alwaysontop", "true").equals("false")) {
                alwaysOnTop = false;
            }
            this.splash.setAlwaysOnTop(alwaysOnTop);
            if (!System.getProperty("splash.percentage", "false").equals("true")) {
                if (Boolean.getBoolean("development.mode")) {
                    this.splash.showString("Starting the server in development mode...");
                }
                else {
                    this.splash.showString("Starting the server...");
                }
            }
        }
        
        @Override
        public void onMessage(final Object message) {
            if (this.splash == null) {
                return;
            }
            final Properties props = (Properties)message;
            if (props.getProperty("TopicName").equals("startupNotification") || props.getProperty("TopicName").equals("SERVER_SHUTDOWN_NOTIFICATION")) {
                this.splash.setVisible(false);
            }
            else {
                if (Configuration.getString("splash.percentage", "false").equals("true")) {
                    this.splash.showString(props.getProperty("progress") + "%");
                }
                else {
                    this.splash.showString(props.getProperty("message"));
                }
                this.splash.showProgress(Integer.valueOf(props.getProperty("progress")));
            }
        }
    }
}
