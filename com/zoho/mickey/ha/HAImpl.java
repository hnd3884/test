package com.zoho.mickey.ha;

import com.zoho.mickey.ha.slave.CoreMasterHealthDetector;
import com.adventnet.mfw.Shutdown;
import com.adventnet.mfw.Starter;
import java.io.FileNotFoundException;
import java.util.Iterator;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.db.persistence.metadata.DataTypeManager;
import java.io.IOException;
import java.nio.file.OpenOption;
import com.zoho.framework.utils.crypto.CryptoUtil;
import java.util.UUID;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import com.adventnet.persistence.DataAccessException;
import java.util.ArrayList;
import com.zoho.conf.Configuration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.Properties;
import com.zoho.framework.utils.FileUtils;
import java.util.logging.Level;
import com.adventnet.persistence.PersistenceInitializer;
import java.io.File;
import com.zoho.clustering.failover.FOSUtil;
import com.zoho.mickey.ha.slave.MasterHealthDetector;
import java.util.List;
import com.zoho.mickey.ha.filereplication.FileReplicationHandler;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

public class HAImpl implements HA
{
    private static final String RESTART_MESSAGE = "MLITE_HA_RESTART";
    private static final Logger LOG;
    protected Mode mode;
    protected static Mode currentMode;
    protected HAConfig config;
    protected ScheduledFuture<?> future;
    protected String peerIP;
    protected String lastSeenStatus;
    protected FileReplicationHandler replHandler;
    private List<MasterHealthDetector> masterHealthDetectorList;
    private boolean ipBinded;
    private static FOSUtil clusteringFOSUtil;
    protected Thread replCheckThread;
    protected boolean replStarted;
    public static final File UNIQUE_ID_FILE;
    private static String server_home;
    
    public HAImpl() {
        this.config = new HAConfig();
        this.peerIP = null;
        this.masterHealthDetectorList = null;
        this.ipBinded = false;
        this.replStarted = false;
    }
    
    public void initialize() throws Exception {
        final String confFilePath = (PersistenceInitializer.getConfigurationValue("ha.conf.file") != null) ? (HAImpl.server_home + File.separator + PersistenceInitializer.getConfigurationValue("ha.conf.file")) : (HAImpl.server_home + File.separator + "conf" + File.separator + "ha.conf");
        HAImpl.LOG.log(Level.FINER, "Loading HA properties from file {0}", new Object[] { confFilePath });
        final Properties props = FileUtils.readPropertyFile(new File(confFilePath));
        this.config.initialize(props);
        this.mode = Mode.valueOf(this.config.mode().toUpperCase());
        HAImpl.currentMode = this.mode;
        DBUtil.initialize(this.config);
        if (HAImpl.clusteringFOSUtil == null) {
            FOSUtil.initialize(this.config.toolsDir());
            HAImpl.clusteringFOSUtil = FOSUtil.getInst();
        }
        if (this.config.httpPollEnabled()) {
            HttpPollUtil.initialize(this.config);
        }
    }
    
    public void postStartProcess() throws Exception {
        this.config.initializeVersionHandler();
        this.syncPatchDir();
        DBUtil.assertBuildNumber();
        if (this.config.replaceIP() != null) {
            DBUtil.removeEntry(this.config.replaceIP(), this.config.replaceHost());
        }
        DBUtil.addEntry(this.config.ipaddr());
        final TimerTask task = new updateTask();
        final ScheduledExecutorService counter = Executors.newSingleThreadScheduledExecutor(r -> {
            final Thread t = new Thread(r);
            t.setName("counter");
            return t;
        });
        this.future = counter.scheduleAtFixedRate(task, 0L, this.config.updateCounterInterval(), TimeUnit.SECONDS);
        if (this.mode == Mode.NEUTRAL) {
            this.mode = this.getMode();
        }
        HAImpl.LOG.log(Level.INFO, " Node starting in [ {0} ] mode", new Object[] { this.mode });
        if (this.mode == Mode.MASTER) {
            if (this.masterModePrecheck()) {
                this.runInMasterMode();
            }
            else {
                this.runInSlaveMode();
            }
        }
        if (this.mode == Mode.SLAVE) {
            this.runInSlaveMode();
        }
    }
    
    public void stopProcess() throws Exception {
        HAImpl.LOG.log(Level.INFO, "Stopping HA....");
        this.setStatusDown();
        if (this.ipBinded) {
            IPHandler.deleteIP(this.config);
        }
        this.future.cancel(false);
    }
    
    public void preStartProcess() throws Exception {
    }
    
    protected void syncPatchDir() {
        HAImpl.LOG.log(Level.FINER, "Checking for sync patch dir process");
        final File syncPatch = new File(Configuration.getString("server.home") + File.separator + "sync_patch_dir");
        if (syncPatch.exists()) {
            HAImpl.LOG.log(Level.INFO, "Syncing patch directory from peer during first start up after ppm applying on other machine");
            try {
                final FileReplicationHandler replHandler = (FileReplicationHandler)Class.forName(this.config.ReplicationHandler()).newInstance();
                final List<String> files = new ArrayList<String>();
                files.add("patch");
                replHandler.replicateDirs(this.config, files, "MIRROR");
            }
            catch (final Exception exp) {
                exp.printStackTrace();
                throw new RuntimeException("ERROR_IN_SYNCING_PATCH_AFTER_PPM " + exp.getMessage());
            }
            HAImpl.LOG.log(Level.FINER, "deleting sync_patch_dir");
            syncPatch.delete();
        }
    }
    
    @Override
    public boolean masterModePrecheck() throws InterruptedException, DataAccessException {
        if (this.IsOtherNodeRunning()) {
            if (this.config.isPreferredMasterEnabled()) {
                HAImpl.LOG.log(Level.INFO, "Preferred master not supported yet");
            }
            HAImpl.LOG.log(Level.SEVERE, " Starting as Master failed..Trying to start as Slave");
            return false;
        }
        return true;
    }
    
    @Override
    public void runInMasterMode() throws Exception {
        HAImpl.currentMode = Mode.MASTER;
        this.createUniqueID();
        DBUtil.updateStatus(this.config.ipaddr(), "serving");
        if (this.config.replicationEnabled() && !this.isLastServingNode()) {
            HAImpl.LOG.log(Level.FINER, "completing pending replication process begin");
            (this.replHandler = (FileReplicationHandler)Class.forName(this.config.ReplicationHandler()).newInstance()).initialize(this.config, this.config.replConf());
            DBUtil.updateReplState(this.config.ipaddr(), "progress");
            if (!this.replHandler.completePendingReplication()) {
                throw new HAException(HAErrorCode.ERROR_IN_COMPLETE_PENDING_REPLICATION, "Cannot PULL latest changes from peer node. Either the peer node isnot available or due to replication failure. Refer logs for more details.");
            }
            DBUtil.updateReplState(this.config.ipaddr(), "ready");
        }
        if (this.config.publicIPEnabled()) {
            HAImpl.LOG.log(Level.FINER, " Binding public IP");
            this.ipBinded = IPHandler.addIP(this.config);
        }
        DBUtil.updateServingTime(this.config.ipaddr());
    }
    
    private void createUniqueID() throws IOException {
        if (!HAImpl.UNIQUE_ID_FILE.exists()) {
            HAImpl.UNIQUE_ID_FILE.getParentFile().mkdirs();
            Files.createFile(HAImpl.UNIQUE_ID_FILE.toPath(), (FileAttribute<?>[])new FileAttribute[0]);
        }
        Files.write(HAImpl.UNIQUE_ID_FILE.toPath(), CryptoUtil.encrypt(UUID.randomUUID().toString()).getBytes(), new OpenOption[0]);
    }
    
    @Override
    public void runInSlaveMode() throws Exception {
        HAImpl.currentMode = Mode.SLAVE;
        if (this.slaveModePreCheck()) {
            this.initializeMasterHealthDetectors();
            this.initializeSlaveReplication();
            this.waitForMaster();
            if (this.lastSeenStatus.equals("serving")) {
                DBUtil.updateStatus(this.peerIP, "down");
            }
            DBUtil.updateStatus(this.config.ipaddr(), "serving");
            this.stopSlaveReplication();
        }
        this.slaveTakeOver();
        this.createUniqueID();
        this.postSlaveTakeOver();
    }
    
    protected void postSlaveTakeOver() throws Exception {
        HAImpl.LOG.log(Level.SEVERE, "This node has taken over as Master..");
        HAImpl.currentMode = Mode.MASTER;
        HAImpl.LOG.log(Level.INFO, "Loading EDT DataTypes");
        DataTypeManager.reload();
        HAImpl.LOG.log(Level.INFO, "Loading Custom Attributes");
        MetaDataUtil.loadCustomAttributes((List)null, false, true);
        if (this.config.publicIPEnabled()) {
            this.ipBinded = IPHandler.addIP(this.config);
        }
        DBUtil.updateServingTime(this.config.ipaddr());
    }
    
    public void initializeMasterHealthDetectors() throws HAException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        (this.masterHealthDetectorList = new ArrayList<MasterHealthDetector>()).add((MasterHealthDetector)Class.forName("com.zoho.mickey.ha.slave.CoreMasterHealthDetector").newInstance());
        this.masterHealthDetectorList.get(0).initialize(this.config);
        for (final String detectorClass : this.config.masterHealthDetectors()) {
            final MasterHealthDetector detector = (MasterHealthDetector)Class.forName(detectorClass).newInstance();
            detector.initialize(this.config);
            this.masterHealthDetectorList.add(detector);
        }
    }
    
    protected void slaveTakeOver() throws HAException, InstantiationException, IllegalAccessException, ClassNotFoundException, NumberFormatException, DataAccessException, FileNotFoundException, IOException {
        for (final String handlerClass : this.config.takeOverHandlers()) {
            final TakeOverHandler handler = (TakeOverHandler)Class.forName(handlerClass).newInstance();
            handler.initialize(this.config);
            handler.onTakeover();
        }
    }
    
    protected void initializeSlaveReplication() throws HAException, InstantiationException, IllegalAccessException, ClassNotFoundException, InterruptedException, DataAccessException {
        if (this.config.replicationEnabled()) {
            if (this.replHandler == null) {
                this.replHandler = (FileReplicationHandler)Class.forName(this.config.ReplicationHandler()).newInstance();
            }
            this.replHandler.initialize(this.config, this.config.replConf());
            if (DBUtil.getReplicationState(this.peerIP).equals("progress")) {
                this.startReplWhenReady();
            }
            else {
                DBUtil.updateReplState(this.config.ipaddr(), "progress");
                this.replHandler.startReplication();
                this.replStarted = true;
            }
        }
    }
    
    protected void stopSlaveReplication() throws HAException, DataAccessException {
        if (this.config.replicationEnabled()) {
            if (!this.replStarted) {
                this.replCheckThread.interrupt();
            }
            else {
                this.replHandler.stopReplication();
                this.replHandler.completePendingReplication();
                DBUtil.updateReplState(this.config.ipaddr(), "ready");
            }
        }
    }
    
    @Override
    public boolean slaveModePreCheck() throws Exception {
        this.deletePublicIP();
        return this.IsOtherNodeRunning();
    }
    
    private void deletePublicIP() throws Exception {
        if (this.config.publicIPEnabled()) {
            IPHandler.deleteIP(this.config);
        }
    }
    
    protected void waitForMaster() throws HAException {
        int index = 0;
        while (index < this.masterHealthDetectorList.size()) {
            if (!this.masterHealthDetectorList.get(index).isMasterDown(this.peerIP)) {
                HAImpl.LOG.log(Level.INFO, "Master Health Detector: [ " + this.masterHealthDetectorList.get(index).getName() + " ] isMasterDown:[ false ]");
                index = 0;
            }
            else {
                HAImpl.LOG.log(Level.INFO, "Master Health Detector: [ " + this.masterHealthDetectorList.get(index).getName() + " ] isMasterDown:[ true ]");
                ++index;
            }
        }
        this.lastSeenStatus = DBUtil.getHealthStatus(this.peerIP).get(0);
    }
    
    protected void startReplWhenReady() {
        (this.replCheckThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        HAImpl.LOG.log(Level.FINER, "Waiting for Replication since peer is in REPL_PROGRESS state");
                        if (DBUtil.getReplicationState(HAImpl.this.peerIP).equals("ready")) {
                            break;
                        }
                        Thread.sleep(60000L);
                    }
                    DBUtil.updateReplState(HAImpl.this.config.ipaddr(), "progress");
                    HAImpl.this.replHandler.startReplication();
                    HAImpl.this.replStarted = true;
                }
                catch (final Throwable e) {
                    HAImpl.LOG.log(Level.SEVERE, "Exception in start Replication when peer node ready" + e.getMessage());
                }
            }
        }).start();
    }
    
    public HAConfig getHAConfig() {
        return this.config;
    }
    
    public List<MasterHealthDetector> getMasterHealthDetectors() {
        if (this.masterHealthDetectorList == null) {
            throw new IllegalStateException("MasterHealth detectors has not been initialized yet");
        }
        return this.masterHealthDetectorList;
    }
    
    public boolean isLastServingNode() throws DataAccessException {
        final String ip = DBUtil.getLastServingIP();
        if (ip != null && !ip.equals(this.config.ipaddr())) {
            this.peerIP = ip;
            this.config.setPeerIP(this.peerIP);
            return false;
        }
        return true;
    }
    
    private boolean IsOtherNodeRunning() throws InterruptedException, DataAccessException {
        this.peerIP = DBUtil.getServingNodeIP();
        if (this.peerIP == null) {
            return false;
        }
        this.config.setPeerIP(this.peerIP);
        return true;
    }
    
    public String getOtherAliveNode() throws InterruptedException, DataAccessException, HAException {
        this.peerIP = DBUtil.getOtherNodeIP(true);
        if (this.peerIP != null) {
            this.config.setPeerIP(this.peerIP);
        }
        return this.peerIP;
    }
    
    public String getOtherNode() throws InterruptedException, DataAccessException, HAException {
        this.peerIP = DBUtil.getOtherNodeIP(false);
        if (this.peerIP != null) {
            this.config.setPeerIP(this.peerIP);
        }
        return this.peerIP;
    }
    
    protected Mode getMode() throws InterruptedException, DataAccessException {
        if (this.IsOtherNodeRunning()) {
            return Mode.SLAVE;
        }
        return Mode.MASTER;
    }
    
    public static String getCurrentMode() {
        if (HAImpl.currentMode == null) {
            throw new IllegalStateException("HA has not been initialized yet.");
        }
        return HAImpl.currentMode.toString();
    }
    
    protected void setStatusDown() throws DataAccessException {
        DBUtil.updateStatus(this.config.ipaddr(), "down");
    }
    
    public static void restart(final String message) {
        try {
            if (!Starter.extshutdown) {
                if (message.equals("MLITE_HA_RESTART")) {
                    Starter.restart = true;
                    final String[] arg = { "localhost", "2" };
                    new Shutdown(arg);
                }
                else {
                    HAImpl.LOG.log(Level.SEVERE, "Restart message is incorrect.");
                }
            }
            else {
                HAImpl.LOG.log(Level.SEVERE, "ShutDown Already Initiated...");
            }
        }
        catch (final Exception e) {
            HAImpl.LOG.log(Level.SEVERE, "Exception while shutting down requested by http request");
        }
    }
    
    public static void restart() {
        restart("MLITE_HA_RESTART");
    }
    
    public static boolean standAloneMasterHealthCheck() {
        HAImpl.LOG.info("Testing peer node status in standalone mode");
        try {
            final HAImpl ha = new HAImpl();
            ha.initialize();
            ha.initializeMasterHealthDetectors();
            final String peerIP = ha.getOtherAliveNode();
            if (peerIP == null) {
                HAImpl.LOG.info("Standalonecheck : No other live node present in the system");
                return false;
            }
            final List<MasterHealthDetector> detectorList = ha.getMasterHealthDetectors();
            final CoreMasterHealthDetector detector = detectorList.get(0);
            if (!detector.checkMasterStatus(peerIP, true)) {
                HAImpl.LOG.log(Level.INFO, "Standalonecheck : Other node : [{0}] is alive.", new Object[] { peerIP });
                return true;
            }
            for (int index = 1; index < detectorList.size(); ++index) {
                if (!detectorList.get(index).isMasterDown(peerIP)) {
                    HAImpl.LOG.log(Level.INFO, "Standalonecheck : Master Health Detector: [ {0} ] isOherNodeDown:[ false ]", new Object[] { detectorList.get(index).getName() });
                    HAImpl.LOG.log(Level.INFO, "Standalonecheck :Other node : [{0}] is alive.", new Object[] { peerIP });
                    return true;
                }
            }
            HAImpl.LOG.log(Level.INFO, "Standalonecheck :Other node : [{0}] is down.", new Object[] { peerIP });
            return false;
        }
        catch (final Exception exp) {
            exp.printStackTrace();
            throw new RuntimeException("Error in getting health status of peer node from standalone mode");
        }
    }
    
    protected void syncUniqueIdFile() {
        HAImpl.LOG.log(Level.INFO, "deleting the unique id file if exists and syncing with master");
        try {
            if (this.replHandler == null) {
                this.replHandler = (FileReplicationHandler)Class.forName(this.config.ReplicationHandler()).newInstance();
            }
            final List<String> files = new ArrayList<String>();
            files.add("conf" + File.separator + "HA" + File.separator + HAImpl.UNIQUE_ID_FILE.getName());
            this.replHandler.replicateFiles(this.config, files);
        }
        catch (final Exception exp) {
            exp.printStackTrace();
            throw new RuntimeException("ERROR_IN_SYNCING_UNIQUEID_FILE" + exp.getMessage());
        }
        this.syncConfigFiles();
    }
    
    protected void syncConfigFiles() {
        HASyncUtil.syncConfigFiles(this.config);
    }
    
    @Override
    public String getType() {
        return "HA";
    }
    
    static {
        LOG = Logger.getLogger(HAImpl.class.getName());
        HAImpl.currentMode = null;
        HAImpl.clusteringFOSUtil = null;
        UNIQUE_ID_FILE = new File(Configuration.getString("server.home") + File.separator + "conf" + File.separator + "ha" + File.separator + "uniqueID.txt");
        HAImpl.server_home = ((Configuration.getString("server.home") != null) ? Configuration.getString("server.home") : Configuration.getString("app.home"));
    }
    
    public class updateTask extends TimerTask
    {
        @Override
        public void run() {
            try {
                this.update();
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
        
        private void update() {
            try {
                DBUtil.updateCounter(HAImpl.this.config.ipaddr());
            }
            catch (final Throwable e) {
                e.printStackTrace();
                HAImpl.restart();
            }
        }
    }
}
