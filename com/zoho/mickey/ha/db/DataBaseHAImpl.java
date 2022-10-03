package com.zoho.mickey.ha.db;

import com.zoho.mickey.ha.HAConfig;
import java.sql.SQLException;
import com.adventnet.ds.query.DataSet;
import com.adventnet.persistence.PersistenceInitializer;
import com.zoho.mickey.ha.filereplication.FileReplicationHandler;
import java.io.FileNotFoundException;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.mfw.ConsoleOut;
import com.zoho.conf.Configuration;
import java.net.InetAddress;
import java.util.concurrent.ScheduledExecutorService;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.io.IOException;
import com.adventnet.db.adapter.postgres.DefaultPostgresDBInitializer;
import com.zoho.mickey.ha.DBUtil;
import com.zoho.mickey.ha.HAException;
import com.zoho.mickey.ha.HAErrorCode;
import com.zoho.mickey.ha.HA;
import com.adventnet.persistence.PersistenceException;
import com.zoho.mickey.exception.PasswordException;
import java.util.Properties;
import java.sql.Connection;
import com.adventnet.db.adapter.DBAdapter;
import com.adventnet.persistence.PersistenceUtil;
import com.zoho.mickey.tools.CreateDBUser;
import com.zoho.mickey.tools.postgres.CreatePostgresDBUser;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.QueryConstructor;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import com.adventnet.db.adapter.postgres.PostgresDBAdapter;
import com.adventnet.mfw.message.MessageFilter;
import com.adventnet.mfw.message.MessageListener;
import com.adventnet.mfw.message.Messenger;
import com.zoho.mickey.ha.HttpStatusUtil;
import com.adventnet.db.api.RelationalAPI;
import java.io.File;
import java.util.logging.Logger;
import com.zoho.mickey.ha.HAImpl;

public class DataBaseHAImpl extends HAImpl
{
    private static final Logger LOGGER;
    private static String serverName;
    public static final File HA_FILE;
    protected static final String TAKEOVER_FAILED = "TAKEOVER_FAILED";
    public static final String UPDATE_SERVER_STATUS = "updateServerStatus";
    public static final String UPDATE_HA_STATUS = "updateHAStatus";
    public static final String UPDATE_REPL_STATE = "updateReplState";
    
    @Override
    public void initialize() throws Exception {
        if (!RelationalAPI.getInstance().getDBAdapter().isBundledDB()) {
            throw new Exception("HA is supported for bundled DB only");
        }
        super.initialize();
        HttpStatusUtil.initialize(this.config);
        if (!DataBaseHAImpl.HA_FILE.exists()) {
            DataBaseHAImpl.HA_FILE.getParentFile().mkdirs();
            DataBaseHAImpl.HA_FILE.createNewFile();
        }
        DataBaseHAImpl.LOGGER.info("going to subscribe to startup notif");
        Messenger.subscribe("startupNotification", (MessageListener)new StartUpListener(), true, (MessageFilter)null);
    }
    
    private void checkAndCreateReplRole() throws PasswordException, PersistenceException, Exception {
        try (final Connection connection = RelationalAPI.getInstance().getConnection()) {
            final Properties dbProps = RelationalAPI.getInstance().getDBAdapter().getDBProps();
            final String replUserName = dbProps.getProperty("repl.username");
            final PostgresDBAdapter dbAdapter = (PostgresDBAdapter)RelationalAPI.getInstance().getDBAdapter();
            if (dbAdapter.isRole(connection, "rolcanlogin", replUserName)) {
                DataBaseHAImpl.LOGGER.log(Level.SEVERE, "Role {0} already exists", replUserName);
            }
            else {
                final String superUserPassword = (String)DataAccess.get(QueryConstructor.get("DBCredentialsAudit", new Criteria(Column.getColumn("DBCredentialsAudit", "USERNAME"), (Object)"postgres", 0), new String[] { "USERNAME", "PASSWORD" })).getFirstValue("DBCredentialsAudit", "PASSWORD");
                String dbUserCreatorClassName = dbProps.getProperty("dbusercreator");
                if (dbUserCreatorClassName == null) {
                    dbUserCreatorClassName = CreatePostgresDBUser.class.getName();
                }
                final CreateDBUser createDBUser = (CreateDBUser)Thread.currentThread().getContextClassLoader().loadClass(dbUserCreatorClassName).newInstance();
                createDBUser.createUser((DBAdapter)dbAdapter, dbProps.getProperty("url"), "postgres", superUserPassword, replUserName, PersistenceUtil.getDBPasswordProvider("postgres").getPassword((Object)dbProps.getProperty("repl.password")), "replication");
                PersistenceUtil.removeKeyInDBConf("repl.password");
                PersistenceUtil.addKeyInDBConf("repl.password", PersistenceUtil.getDBPasswordProvider("postgres").getEncryptedPassword(dbProps.getProperty("repl.password")));
            }
        }
    }
    
    @Override
    public void postStartProcess() throws Exception {
        if (RelationalAPI.getInstance().getDBAdapter().getDBType().equalsIgnoreCase("postgres")) {
            this.checkAndCreateReplRole();
        }
        this.config.initializeVersionHandler();
        this.syncPatchDir();
        DataBaseHAUtil.assertBuildNumber(this.config);
        if (this.mode == HA.Mode.NEUTRAL) {
            this.mode = this.getMode();
        }
        DataBaseHAImpl.LOGGER.log(Level.INFO, " Node starting in [ {0} ] mode", new Object[] { this.mode });
        if (this.mode == HA.Mode.MASTER) {
            if (this.masterModePrecheck()) {
                this.runInMasterMode();
            }
            else {
                this.runInSlaveMode();
            }
        }
        if (this.mode == HA.Mode.SLAVE) {
            this.runInSlaveMode();
        }
    }
    
    @Override
    public void runInMasterMode() throws Exception {
        try (final Connection connection = RelationalAPI.getInstance().getConnection()) {
            final boolean isPeerDBRunning = this.isPeerDataBaseRunning();
            if (RelationalAPI.getInstance().getDBAdapter().isReadOnly(connection)) {
                if (isPeerDBRunning) {
                    throw new HAException(HAErrorCode.PEER_DATABASE_RUNNING, "Current Database has started in read only mode. Peer database might be still running. Please stop the peer database and try again");
                }
                DataBaseHAImpl.LOGGER.log(Level.INFO, "During startup,peer database was running and current database started as slave but peer database is down now. Hence, promoting as master. ");
                DataBaseHAUtil.promoteDB(this.config);
            }
        }
        if (this.config.replaceIP() != null) {
            DataBaseHAUtil.removeEntry(this.config.replaceIP(), this.config.replaceHost());
        }
        DataBaseHAUtil.addEntries(this.config.ipaddr(), this.config.getPeerIP());
        DataBaseHAUtil.updateServingTime(this.config.ipaddr());
        this.startUpdateCounter();
        super.runInMasterMode();
    }
    
    @Override
    public void runInSlaveMode() throws Exception {
        DataBaseHAImpl.currentMode = HA.Mode.SLAVE;
        if (this.slaveModePreCheck()) {
            this.initializeMasterHealthDetectors();
            this.initializeSlaveReplication();
            this.waitForMaster();
            if (this.isPeerDataBaseRunning()) {
                throw new HAException(HAErrorCode.PEER_DATABASE_RUNNING, "Peer Server is down but peer database is running. This might be due to foreceful termination of peer server. Please stop the peer database and try again");
            }
            this.slaveTakeOver();
            if (this.lastSeenStatus.equals("serving")) {
                DBUtil.updateStatus(this.peerIP, "down");
            }
            DBUtil.updateStatus(this.config.ipaddr(), "serving");
            this.stopSlaveReplication();
        }
        this.postSlaveTakeOver();
        this.startUpdateCounter();
    }
    
    private boolean isPeerDataBaseRunning() throws NumberFormatException, IOException {
        return ((DefaultPostgresDBInitializer)RelationalAPI.getInstance().getDBAdapter().getDBInitializer()).checkServerStatus(this.config.getPeerIP(), Integer.parseInt(this.config.getPeerDBPort()), "postgres");
    }
    
    @Override
    protected void syncConfigFiles() {
    }
    
    private void startUpdateCounter() {
        final TimerTask task = new updateTask();
        final ScheduledExecutorService counter = Executors.newSingleThreadScheduledExecutor(r -> {
            final Thread t = new Thread(r);
            t.setName("counter");
            return t;
        });
        this.future = counter.scheduleAtFixedRate(task, 0L, this.config.updateCounterInterval(), TimeUnit.SECONDS);
    }
    
    @Override
    public boolean slaveModePreCheck() throws Exception {
        if (super.slaveModePreCheck()) {
            this.syncUniqueIdFile();
            try {
                Configuration.setString("server.name", DataBaseHAImpl.serverName = InetAddress.getLocalHost().getHostName());
            }
            catch (final Exception e) {
                DataBaseHAImpl.serverName = "localhost";
                DataBaseHAImpl.LOGGER.log(Level.WARNING, "Cannot get InetAddress localhost name");
                if (Configuration.getString("resolve.servername", "false").equalsIgnoreCase("true")) {
                    ConsoleOut.println("");
                    ConsoleOut.println("Cannot get InetAddress for localhost.");
                    ConsoleOut.println("");
                    throw e;
                }
            }
            HttpStatusUtil.getInstance().sendStatus("updateServerStatus", DataBaseHAImpl.serverName, 1);
            HttpStatusUtil.getInstance().sendStatus("updateHAStatus", this.config.ipaddr(), "alive");
            return true;
        }
        return false;
    }
    
    @Override
    protected void slaveTakeOver() throws HAException, InstantiationException, IllegalAccessException, ClassNotFoundException, NumberFormatException, DataAccessException, FileNotFoundException, IOException {
        try {
            super.slaveTakeOver();
        }
        catch (final Exception e) {
            DataBaseHAUtil.updateTakeOverFailedStatus(this.config.ipaddr());
            throw e;
        }
        DBUtil.updateBuildNumber(this.config.ipaddr(), this.config.versionHandler().getCurrentBuildNumber());
    }
    
    @Override
    protected void setStatusDown() throws DataAccessException {
        if (HAImpl.getCurrentMode().equals(HA.Mode.SLAVE.toString())) {
            HttpStatusUtil.getInstance().sendStatus("updateServerStatus", DataBaseHAImpl.serverName, 3);
            HttpStatusUtil.getInstance().sendStatus("updateHAStatus", this.config.ipaddr(), "down");
            return;
        }
        super.setStatusDown();
    }
    
    @Override
    protected void initializeSlaveReplication() throws HAException, InstantiationException, IllegalAccessException, ClassNotFoundException, InterruptedException, DataAccessException {
        if (this.config.replicationEnabled()) {
            (this.replHandler = (FileReplicationHandler)Class.forName(this.config.ReplicationHandler()).newInstance()).initialize(this.config, this.config.replConf());
            if (DBUtil.getReplicationState(this.peerIP).equals("progress")) {
                this.startReplWhenReady();
            }
            else {
                HttpStatusUtil.getInstance().sendStatus("updateReplState", this.config.ipaddr(), "progress");
                this.replHandler.startReplication();
                this.replStarted = true;
            }
        }
    }
    
    @Override
    protected void startReplWhenReady() {
        (this.replCheckThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        DataBaseHAImpl.LOGGER.log(Level.FINER, "Waiting for Replication since peer is in REPL_PROGRESS state");
                        if (DBUtil.getReplicationState(DataBaseHAImpl.this.peerIP).equals("ready")) {
                            break;
                        }
                        Thread.sleep(60000L);
                    }
                    HttpStatusUtil.getInstance().sendStatus("updateReplState", DataBaseHAImpl.this.config.ipaddr(), "progress");
                    DataBaseHAImpl.this.replHandler.startReplication();
                    DataBaseHAImpl.this.replStarted = true;
                }
                catch (final Throwable e) {
                    DataBaseHAImpl.LOGGER.log(Level.SEVERE, "Exception in start Replication when peer node ready" + e.getMessage());
                }
            }
        }).start();
    }
    
    @Override
    public void stopProcess() throws Exception {
        super.stopProcess();
        this.syncStandByWithMasterDB();
    }
    
    private void syncStandByWithMasterDB() throws SQLException {
        if (PersistenceInitializer.getConfigurationValue("DBName").equals("postgres")) {
            final String currentWalLSN_SQL = "select pg_current_wal_lsn()";
            final RelationalAPI relApi = RelationalAPI.getInstance();
            try (final Connection conn = relApi.getConnection()) {
                String monitorSQL = "select pid from pg_stat_replication";
                try (final DataSet ds = relApi.executeQuery(monitorSQL, conn)) {
                    if (!ds.next()) {
                        DataBaseHAImpl.LOGGER.log(Level.INFO, "Stand by database is not started or not accepting streaming replication");
                        return;
                    }
                }
                try (final DataSet ds = relApi.executeQuery(currentWalLSN_SQL, conn)) {
                    ds.next();
                    final String currentWalLSN = ds.getAsString(1);
                    monitorSQL = "select 'synced' from pg_stat_replication where '" + currentWalLSN + "' <= replay_lsn";
                    for (int i = 0; i < 30; ++i) {
                        try (final DataSet dataSet = relApi.executeQuery(currentWalLSN_SQL, conn)) {
                            if (dataSet.next()) {
                                DataBaseHAImpl.LOGGER.log(Level.INFO, "Data is successfully synced with SlaveDB");
                                break;
                            }
                            DataBaseHAImpl.LOGGER.log(Level.INFO, "Waiting for data to sync with standBy server");
                            Thread.sleep(2000L);
                        }
                    }
                }
            }
            catch (final Exception e) {
                DataBaseHAImpl.LOGGER.log(Level.WARNING, "Exception occurred while syncing data with slave database :: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public String getType() {
        return "DataBaseHA";
    }
    
    static {
        LOGGER = Logger.getLogger(DataBaseHAImpl.class.getName());
        DataBaseHAImpl.serverName = null;
        HA_FILE = new File(Configuration.getString("server.home") + File.separator + "conf" + File.separator + "ha" + File.separator + "ha.json");
    }
    
    public class StartUpListener implements MessageListener
    {
        public StartUpListener() {
            DataBaseHAImpl.LOGGER.info("StartUpListener instance created");
        }
        
        public void onMessage(final Object msgArg) {
            DataBaseHAImpl.LOGGER.log(Level.INFO, "Startup notification received. Going to delete takeover status if present");
            try {
                DataBaseHAUtil.deleteFailedTakeOverStatus(DataBaseHAImpl.this.config.ipaddr());
            }
            catch (final IOException e) {
                throw new RuntimeException("Exception occurred while deleting the takeover status in ha.json file", e);
            }
        }
    }
}
