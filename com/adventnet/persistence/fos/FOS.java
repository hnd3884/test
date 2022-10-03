package com.adventnet.persistence.fos;

import com.zoho.conf.Configuration;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Element;
import com.adventnet.iam.security.SecurityUtil;
import com.adventnet.persistence.fos.slave.CoreMasterHealthDetector;
import com.adventnet.mfw.Shutdown;
import com.adventnet.mfw.Starter;
import java.util.Iterator;
import java.net.URL;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.db.persistence.metadata.DataTypeManager;
import com.adventnet.persistence.DataAccessException;
import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.Properties;
import com.zoho.framework.utils.FileUtils;
import java.util.logging.Level;
import java.io.File;
import com.adventnet.persistence.PersistenceInitializer;
import com.zoho.clustering.failover.FOSUtil;
import com.adventnet.persistence.fos.slave.MasterHealthDetector;
import java.util.List;
import com.adventnet.persistence.fos.filereplication.FileReplicationHandler;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;
import com.adventnet.mfw.modulestartup.ModuleStartStopProcessor;

public class FOS implements ModuleStartStopProcessor
{
    public static final String NODE_ALIVE = "alive";
    public static final String NODE_SERVING = "serving";
    public static final String NODE_DOWN = "down";
    public static final String REPL_NO_OP = "none";
    public static final String REPL_READY = "ready";
    public static final String REPL_IN_PROGRESS = "progress";
    private static final String RESTART_MESSAGE = "MLITE_FOS_RESTART";
    private static final Logger LOG;
    private Mode mode;
    private static Mode currentMode;
    private FOSConfig config;
    private ScheduledFuture<?> future;
    private String peerIP;
    private long lastCounterValueofMaster;
    private String lastSeenStatus;
    private FileReplicationHandler replHandler;
    private List<MasterHealthDetector> masterHealthDetectorList;
    private boolean ipBinded;
    private static FOSUtil clusteringFOSUtil;
    private Thread replCheckThread;
    private boolean replStarted;
    private static String server_home;
    
    public FOS() {
        this.config = new FOSConfig();
        this.peerIP = null;
        this.lastCounterValueofMaster = 0L;
        this.masterHealthDetectorList = null;
        this.ipBinded = false;
        this.replStarted = false;
    }
    
    public void initialize() throws Exception {
        final String confFilePath = (PersistenceInitializer.getConfigurationValue("fos.conf.file") != null) ? (FOS.server_home + File.separator + PersistenceInitializer.getConfigurationValue("fos.conf.file")) : (FOS.server_home + File.separator + "conf" + File.separator + "fos.conf");
        FOS.LOG.log(Level.FINER, "Loading FOS properties from file {0}", new Object[] { confFilePath });
        final Properties props = FileUtils.readPropertyFile(new File(confFilePath));
        this.config.initialize(props);
        this.mode = Mode.valueOf(this.config.mode().toUpperCase());
        FOS.currentMode = this.mode;
        DBUtil.initialize(this.config);
        if (FOS.clusteringFOSUtil == null) {
            FOSUtil.initialize(this.config.toolsDir());
            FOS.clusteringFOSUtil = FOSUtil.getInst();
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
        DBUtil.addentry(this.config.ipaddr());
        final TimerTask task = new updateTask();
        final ScheduledExecutorService counter = Executors.newSingleThreadScheduledExecutor();
        this.future = counter.scheduleAtFixedRate(task, 0L, this.config.updateCounterInterval() * 1000, TimeUnit.MILLISECONDS);
        if (this.mode == Mode.NEUTRAL) {
            this.mode = this.getMode();
        }
        FOS.LOG.log(Level.INFO, " Node starting in [ {0} ] mode", new Object[] { this.mode });
        if (this.mode == Mode.MASTER) {
            if (this.masterModePrecheck()) {
                this.runInMasterMode();
            }
            else {
                this.runInSlaveMode();
            }
        }
        if (this.mode == Mode.SLAVE) {
            if (this.config.publicIPEnabled()) {
                IPHandler.deleteIP(this.config);
            }
            this.runInSlaveMode();
        }
    }
    
    public void stopProcess() throws Exception {
        FOS.LOG.log(Level.INFO, "Stopping FOS....");
        this.setStatusDown();
        if (this.ipBinded) {
            IPHandler.deleteIP(this.config);
        }
        this.future.cancel(false);
    }
    
    public void preStartProcess() throws Exception {
    }
    
    private void syncPatchDir() {
        FOS.LOG.log(Level.FINER, "Checking for sync patch dir process");
        final File syncPatch = new File(System.getProperty("server.home") + File.separator + "sync_patch_dir");
        if (syncPatch.exists()) {
            FOS.LOG.log(Level.INFO, "Syncing patch directory from peer during first start up after ppm applying on other machine");
            try {
                final FileReplicationHandler replHandler = (FileReplicationHandler)Class.forName(this.config.ReplicationHandler()).newInstance();
                final List<String> files = new ArrayList<String>();
                files.add("patch");
                replHandler.replicateOnce(this.config, files);
            }
            catch (final Exception exp) {
                exp.printStackTrace();
                throw new RuntimeException("ERROR_IN_SYNCING_PATCH_AFTER_PPM " + exp.getMessage());
            }
            FOS.LOG.log(Level.FINER, "deleting sync_patch_dir");
            syncPatch.delete();
        }
    }
    
    private boolean masterModePrecheck() throws InterruptedException, DataAccessException {
        if (this.IsOtherNodeRunning()) {
            if (!this.config.isPreferredMasterEnabled()) {
                FOS.LOG.log(Level.SEVERE, " Starting as Master failed..Trying to start as Slave");
                return false;
            }
            FOS.LOG.log(Level.INFO, "Preferred master not supported yet");
        }
        return true;
    }
    
    private void runInMasterMode() throws Exception {
        FOS.currentMode = Mode.MASTER;
        DBUtil.updateStatus(this.config.ipaddr(), "serving");
        if (this.config.replicationEnabled() && !this.isLastServingNode()) {
            FOS.LOG.log(Level.FINER, "completing pending replication process begin");
            (this.replHandler = (FileReplicationHandler)Class.forName(this.config.ReplicationHandler()).newInstance()).initialize(this.config, this.config.replConf());
            DBUtil.updateReplState(this.config.ipaddr(), "progress");
            if (!this.replHandler.completePendingReplication()) {
                throw new FOSException(FOSErrorCode.ERROR_IN_COMPLETE_PENDING_REPLICATION, "Cannot PULL latest changes from peer node. Either the peer node isnot available or due to replication failure. Refer logs for more details.");
            }
            DBUtil.updateReplState(this.config.ipaddr(), "ready");
        }
        if (this.config.publicIPEnabled()) {
            FOS.LOG.log(Level.FINER, " Binding public IP");
            this.ipBinded = IPHandler.addIP(this.config);
        }
        DBUtil.updateServingTime(this.config.ipaddr());
    }
    
    private void runInSlaveMode() throws Exception {
        FOS.currentMode = Mode.SLAVE;
        if (this.IsOtherNodeRunning()) {
            this.initializeMasterHealthDetectors();
            this.initializeSlaveReplication();
            this.waitForMaster();
            if (this.lastSeenStatus.equals("serving")) {
                DBUtil.updateStatus(this.peerIP, "down");
            }
            DBUtil.updateStatus(this.config.ipaddr(), "serving");
            if (this.config.isPreferredMasterEnabled()) {
                FOS.LOG.info("Preferred master not supported yet");
            }
            this.stopSlaveReplication();
        }
        this.slaveTakeOver();
        FOS.LOG.log(Level.SEVERE, "This node has taken over as Master..");
        FOS.currentMode = Mode.MASTER;
        FOS.LOG.log(Level.INFO, "Loading EDT DataTypes");
        DataTypeManager.reload();
        FOS.LOG.log(Level.INFO, "Loading Custom Attributes");
        MetaDataUtil.loadCustomAttributes(null, false, true);
        if (this.config.publicIPEnabled()) {
            this.ipBinded = IPHandler.addIP(this.config);
        }
        DBUtil.updateServingTime(this.config.ipaddr());
    }
    
    public void initializeMasterHealthDetectors() throws FOSException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        (this.masterHealthDetectorList = new ArrayList<MasterHealthDetector>()).add((MasterHealthDetector)Class.forName("com.adventnet.persistence.fos.slave.CoreMasterHealthDetector").newInstance());
        this.masterHealthDetectorList.get(0).initialize(this.config);
        for (final String detectorClass : this.config.masterHealthDetectors()) {
            final MasterHealthDetector detector = (MasterHealthDetector)Class.forName(detectorClass).newInstance();
            detector.initialize(this.config);
            this.masterHealthDetectorList.add(detector);
        }
    }
    
    private void slaveTakeOver() throws FOSException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        if (this.config.takeOverHandler() != null) {
            final TakeOverHandler handler = (TakeOverHandler)Class.forName(this.config.takeOverHandler()).newInstance();
            handler.onTakeover();
        }
    }
    
    private void initializeSlaveReplication() throws FOSException, InstantiationException, IllegalAccessException, ClassNotFoundException, InterruptedException, DataAccessException {
        if (this.config.replicationEnabled()) {
            (this.replHandler = (FileReplicationHandler)Class.forName(this.config.ReplicationHandler()).newInstance()).initialize(this.config, this.config.replConf());
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
    
    private void stopSlaveReplication() throws FOSException, DataAccessException {
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
    
    private void waitForMaster() throws FOSException {
        int index = 0;
        while (index < this.masterHealthDetectorList.size()) {
            if (!this.masterHealthDetectorList.get(index).isMasterDown(this.peerIP)) {
                FOS.LOG.log(Level.INFO, "Master Health Detector: [ " + this.masterHealthDetectorList.get(index).getName() + " ] isMasterDown:[ false ]");
                index = 0;
            }
            else {
                FOS.LOG.log(Level.INFO, "Master Health Detector: [ " + this.masterHealthDetectorList.get(index).getName() + " ] isMasterDown:[ true ]");
                ++index;
            }
        }
        final List<String> healthStatus = DBUtil.getHealthStatus(this.peerIP);
        this.lastSeenStatus = healthStatus.get(0);
        this.lastCounterValueofMaster = Long.parseLong(healthStatus.get(1));
    }
    
    private void startReplWhenReady() {
        (this.replCheckThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        FOS.LOG.log(Level.FINER, "Waiting for Replication since peer is in REPL_PROGRESS state");
                        if (DBUtil.getReplicationState(FOS.this.peerIP).equals("ready")) {
                            break;
                        }
                        Thread.sleep(60000L);
                    }
                    DBUtil.updateReplState(FOS.this.config.ipaddr(), "progress");
                    FOS.this.replHandler.startReplication();
                    FOS.this.replStarted = true;
                }
                catch (final Throwable e) {
                    FOS.LOG.log(Level.SEVERE, "Exception in start Replication when peer node ready" + e.getMessage());
                }
            }
        }).start();
    }
    
    public FOSConfig getFOSConfig() {
        return this.config;
    }
    
    public List getMasterHealthDetectors() {
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
    
    public String getOtherAliveNode() throws InterruptedException, DataAccessException, FOSException {
        this.peerIP = DBUtil.getOtherNodeIP(true);
        if (this.peerIP != null) {
            this.config.setPeerIP(this.peerIP);
        }
        return this.peerIP;
    }
    
    public String getOtherNode() throws InterruptedException, DataAccessException, FOSException {
        this.peerIP = DBUtil.getOtherNodeIP(false);
        if (this.peerIP != null) {
            this.config.setPeerIP(this.peerIP);
        }
        return this.peerIP;
    }
    
    private Mode getMode() throws InterruptedException, DataAccessException {
        if (this.IsOtherNodeRunning()) {
            return Mode.SLAVE;
        }
        return Mode.MASTER;
    }
    
    public static String getCurrentMode() {
        if (FOS.currentMode == null) {
            throw new IllegalStateException("FOS has not been initialized yet.");
        }
        return FOS.currentMode.toString();
    }
    
    private void setStatusDown() throws DataAccessException {
        DBUtil.updateStatus(this.config.ipaddr(), "down");
    }
    
    public static void restart(final String message) {
        try {
            if (!Starter.extshutdown) {
                if (message.equals("MLITE_FOS_RESTART")) {
                    Starter.restart = true;
                    final String[] arg = { "localhost", "2" };
                    new Shutdown(arg);
                }
                else {
                    FOS.LOG.log(Level.SEVERE, "Restart message is incorrect.");
                }
            }
            else {
                FOS.LOG.log(Level.SEVERE, "ShutDown Already Initiated...");
            }
        }
        catch (final Exception e) {
            FOS.LOG.log(Level.SEVERE, "Exception while shutting down requested by http request");
        }
    }
    
    public static void restart() {
        restart("MLITE_FOS_RESTART");
    }
    
    public static boolean standAloneMasterHealthCheck() {
        FOS.LOG.info("Testing peer node status in standalone mode");
        try {
            final FOS fos = new FOS();
            fos.initialize();
            fos.initializeMasterHealthDetectors();
            final String peerIP = fos.getOtherAliveNode();
            if (peerIP == null) {
                FOS.LOG.info("Standalonecheck : No other live node present in the system");
                return false;
            }
            final List<MasterHealthDetector> detectorList = fos.getMasterHealthDetectors();
            final CoreMasterHealthDetector detector = detectorList.get(0);
            if (!detector.checkMasterStatus(peerIP, true)) {
                FOS.LOG.log(Level.INFO, "Standalonecheck : Other node : [{0}] is alive.", new Object[] { peerIP });
                return true;
            }
            for (int index = 1; index < detectorList.size(); ++index) {
                if (!detectorList.get(index).isMasterDown(peerIP)) {
                    FOS.LOG.log(Level.INFO, "Standalonecheck : Master Health Detector: [ {0} ] isOherNodeDown:[ false ]", new Object[] { detectorList.get(index).getName() });
                    FOS.LOG.log(Level.INFO, "Standalonecheck :Other node : [{0}] is alive.", new Object[] { peerIP });
                    return true;
                }
            }
            FOS.LOG.log(Level.INFO, "Standalonecheck :Other node : [{0}] is down.", new Object[] { peerIP });
            return false;
        }
        catch (final Exception exp) {
            exp.printStackTrace();
            throw new RuntimeException("Error in getting health status of peer node from standalone mode");
        }
    }
    
    public static boolean isEnabled() throws Exception {
        final File file = new File(FOS.server_home + File.separator + "conf" + File.separator + "Persistence" + File.separator + "module-startstop-processors.xml");
        if (!file.exists()) {
            return false;
        }
        try {
            final DocumentBuilder docBuilder = SecurityUtil.createDocumentBuilder(true, false, (Properties)null);
            final Document doc = docBuilder.parse(file);
            final Element root = doc.getDocumentElement();
            final NodeList connList = root.getElementsByTagName("ModuleStartStopProcessor");
            final int length = connList.getLength();
            if (length > 0) {
                for (int i = 0; i < length; ++i) {
                    final Element connectorEl = (Element)connList.item(i);
                    if (connectorEl.getAttribute("PROCESSOR_NAME").equals("FOS")) {
                        return true;
                    }
                }
            }
            return false;
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    static {
        LOG = Logger.getLogger(FOS.class.getName());
        FOS.currentMode = null;
        FOS.clusteringFOSUtil = null;
        FOS.server_home = ((Configuration.getString("server.home") != null) ? Configuration.getString("server.home") : Configuration.getString("app.home"));
    }
    
    public enum Mode
    {
        MASTER, 
        SLAVE, 
        NEUTRAL;
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
                DBUtil.updateCounter(FOS.this.config.ipaddr());
            }
            catch (final Throwable e) {
                e.printStackTrace();
                FOS.restart();
            }
        }
    }
}
