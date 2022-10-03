package com.adventnet.persistence.fos;

import com.zoho.conf.Configuration;
import java.util.List;
import java.util.logging.Level;
import java.io.File;
import com.adventnet.persistence.PersistenceInitializer;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Logger;
import com.adventnet.mfw.VersionHandler;

public class FOSConfig
{
    private String ipaddr;
    private String mode;
    private String peerIP;
    private int updateCounterInterval;
    private int dbFailureRetryCount;
    private int dbRetryInterval;
    private int ratioOfPollIntervaltoUpdateCounter;
    private int pollInterval;
    private int pollFailureRetryCount;
    private boolean isPreferredMasterEnabled;
    private String takeOverHandler;
    private String fileReplicationHandler;
    private String replConf;
    private boolean replicationEnabled;
    private String masterHealthDetectors;
    private boolean publicIPEnabled;
    private String publicIP;
    private String publicIPIfName;
    private String publicIPNetMask;
    private FOSErrorHandler errorHandler;
    private int ipCheckIntervalInSecs;
    private int ipCheckRetryCount;
    private boolean httpPollEnabled;
    private String hostnameVerifierClassName;
    private int httpPollConnTimeout;
    private int httpPollReadTimeout;
    private int httpPollIntervalInSecs;
    private int httpPollRetryCount;
    private String httpPollURI;
    private String connectorPort;
    private String httpProtocol;
    private String downgradeURI;
    private String replaceIP;
    private String replaceHost;
    private String toolsDir;
    private static String server_home;
    private VersionHandler versionHandler;
    private static final Logger OUT;
    
    public FOSConfig() {
        this.peerIP = null;
        this.publicIP = "";
        this.replaceIP = null;
        this.replaceHost = null;
        this.versionHandler = null;
    }
    
    public void FOSconfig() {
    }
    
    protected void initialize(final Properties props) throws Exception {
        this.mode = props.getProperty("mode", "NEUTRAL").toUpperCase(Locale.ENGLISH);
        this.isPreferredMasterEnabled = !this.mode.equals("NEUTRAL");
        this.replaceIP = props.getProperty("replace.ipaddr");
        if (this.replaceIP != null) {
            this.replaceHost = FOSUtil.getValue(props, "replace.hostname");
        }
        this.dbFailureRetryCount = Integer.parseInt(props.getProperty("dbfailure.retrycount", "2"));
        this.dbRetryInterval = Integer.parseInt(props.getProperty("dbfailure.retryinterval", "10"));
        this.updateCounterInterval = Integer.parseInt(props.getProperty("updatecounter.interval", "5"));
        this.ratioOfPollIntervaltoUpdateCounter = Integer.parseInt(props.getProperty("ratioOfPollIntervaltoUpdateCounter", "2"));
        this.pollFailureRetryCount = Integer.parseInt(props.getProperty("updatecounter.failureRetryCount", "3"));
        this.toolsDir = props.getProperty("tools.dir", "./");
        this.errorHandler = (FOSErrorHandler)Class.forName(props.getProperty("fos.errorhandler", "com.adventnet.persistence.fos.DefaultErrorHandler")).newInstance();
        this.replicationEnabled = Boolean.valueOf(props.getProperty("repl.enabled", "false"));
        if (this.replicationEnabled) {
            this.fileReplicationHandler = props.getProperty("repl.handler", "com.adventnet.persistence.fos.filereplication.DefaultFileReplicationHandler");
            final String fosConf = (PersistenceInitializer.getConfigurationValue("fos.conf.file") != null) ? PersistenceInitializer.getConfigurationValue("fos.conf.file") : ("conf" + File.separator + "fos.conf");
            this.replConf = FOSConfig.server_home + File.separator + props.getProperty("repl.conf", fosConf);
        }
        this.takeOverHandler = props.getProperty("takeover.handler");
        this.masterHealthDetectors = props.getProperty("masterhealth.detectorlist", "");
        this.publicIPEnabled = Boolean.valueOf(props.getProperty("publicIP.enabled", "false"));
        if (this.publicIPEnabled) {
            this.publicIP = FOSUtil.getValue(props, "publicIP.ipaddr");
            this.publicIPIfName = props.getProperty("publicIP.Ifname");
            this.publicIPNetMask = props.getProperty("publicIP.netmask");
            this.ipCheckIntervalInSecs = Integer.parseInt(props.getProperty("publicIP.retryinterval", "30"));
            this.ipCheckRetryCount = Integer.parseInt(props.getProperty("publicIP.retryCount", "3"));
        }
        this.httpPollEnabled = Boolean.valueOf(props.getProperty("http.poll.enabled", "true"));
        if (this.httpPollEnabled) {
            final String httpDetector = "com.adventnet.persistence.fos.slave.HttpBasedMasterHealthDetector";
            if (this.masterHealthDetectors.trim().length() > 0) {
                this.masterHealthDetectors = httpDetector + "," + this.masterHealthDetectors;
            }
            else {
                this.masterHealthDetectors = httpDetector;
            }
            this.httpPollURI = FOSUtil.getValue(props, "http.poll.URI");
            this.httpPollConnTimeout = Integer.parseInt(props.getProperty("http.poll.conntimeout", "2000"));
            this.httpPollReadTimeout = Integer.parseInt(props.getProperty("http.poll.readtimeout", "2000"));
            this.httpPollIntervalInSecs = Integer.parseInt(props.getProperty("http.poll.interval", "90"));
            this.httpPollRetryCount = Integer.parseInt(props.getProperty("http.poll.retrycount", "3"));
            this.hostnameVerifierClassName = props.getProperty("http.poll.hostnameverifierclass");
            this.connectorPort = props.getProperty("http.poll.connectorport", "8080");
            this.httpProtocol = props.getProperty("http.poll.protocol", "http");
        }
        if (this.isPreferredMasterEnabled) {
            if (!this.httpPollEnabled) {
                throw new IllegalArgumentException("Preferred master will not be enabled unless a httpPoll is activated");
            }
            this.downgradeURI = FOSUtil.getValue(props, "downgradeURI");
        }
        this.ipaddr = props.getProperty("ipaddr", FOSUtil.getIPAddr(this.publicIP));
        this.validateParams();
        FOSConfig.OUT.log(Level.FINER, this.toString());
    }
    
    private void validateParams() {
        final int MaxtimeforUpdate = this.dbFailureRetryCount() * this.dbRetryInterval();
        final int TotalPollingTime = this.pollFailureRetryCount() * this.pollInterval();
        if (MaxtimeforUpdate >= TotalPollingTime) {
            throw new IllegalArgumentException("Invalid configuration in FOS..TotalPollingtime should be greater than Maximum time for single counter update ");
        }
    }
    
    public void initializeVersionHandler() throws Exception {
        final String versionHandlerClass = PersistenceInitializer.getConfigurationValue("VersionHandler");
        if (versionHandlerClass == null) {
            throw new IllegalArgumentException("FOS will be enabled only if version handler is defined.");
        }
        FOSConfig.OUT.log(Level.INFO, "Instantiating version handler provided in fos.conf: " + versionHandlerClass);
        this.versionHandler = (VersionHandler)Class.forName(versionHandlerClass).newInstance();
    }
    
    public String ipaddr() {
        return this.ipaddr;
    }
    
    public String mode() {
        return this.mode;
    }
    
    public VersionHandler versionHandler() {
        return this.versionHandler;
    }
    
    public String replaceIP() {
        return this.replaceIP;
    }
    
    public String replaceHost() {
        return this.replaceHost;
    }
    
    public int updateCounterInterval() {
        return this.updateCounterInterval;
    }
    
    public int dbFailureRetryCount() {
        return this.dbFailureRetryCount;
    }
    
    public int dbRetryInterval() {
        return this.dbRetryInterval;
    }
    
    public int pollInterval() {
        return this.pollInterval = this.ratioOfPollIntervaltoUpdateCounter * this.updateCounterInterval;
    }
    
    public int pollFailureRetryCount() {
        return this.pollFailureRetryCount;
    }
    
    public boolean isPreferredMasterEnabled() {
        return this.isPreferredMasterEnabled;
    }
    
    public String toolsDir() {
        return this.toolsDir;
    }
    
    public FOSErrorHandler errorHandler() {
        return this.errorHandler;
    }
    
    public int ratioOfPollIntervaltoUpdateCounter() {
        return this.ratioOfPollIntervaltoUpdateCounter;
    }
    
    public boolean replicationEnabled() {
        return this.replicationEnabled;
    }
    
    public String replConf() {
        return this.replConf;
    }
    
    public String ReplicationHandler() {
        return this.fileReplicationHandler;
    }
    
    public String takeOverHandler() {
        return this.takeOverHandler;
    }
    
    public List<String> masterHealthDetectors() {
        return FOSUtil.getList("", this.masterHealthDetectors);
    }
    
    public boolean publicIPEnabled() {
        return this.publicIPEnabled;
    }
    
    public String publicIP() {
        return this.publicIP;
    }
    
    public String publicIPIfName() {
        return this.publicIPIfName;
    }
    
    public String publicIPNetMask() {
        return this.publicIPNetMask;
    }
    
    public int ipCheckIntervalInSecs() {
        return this.ipCheckIntervalInSecs;
    }
    
    public int ipCheckRetryCount() {
        return this.ipCheckRetryCount;
    }
    
    protected void setPeerIP(final String ip) {
        this.peerIP = ip;
    }
    
    public String getPeerIP() {
        if (this.peerIP == null) {
            throw new IllegalStateException("Peer ip has not been initialized");
        }
        return this.peerIP;
    }
    
    public boolean httpPollEnabled() {
        return this.httpPollEnabled;
    }
    
    public String hostnameVerifierClassName() {
        return this.hostnameVerifierClassName;
    }
    
    public int httpPollConnTimeout() {
        return this.httpPollConnTimeout;
    }
    
    public int httpPollReadTimeout() {
        return this.httpPollReadTimeout;
    }
    
    public int httpPollIntervalInSecs() {
        return this.httpPollIntervalInSecs;
    }
    
    public int httpPollRetryCount() {
        return this.httpPollRetryCount;
    }
    
    public String httpPollURI() {
        return this.httpPollURI;
    }
    
    public String downgradeURI() {
        return this.downgradeURI;
    }
    
    public String httpProtocol() {
        return this.httpProtocol;
    }
    
    public String connectorPort() {
        return this.connectorPort;
    }
    
    @Override
    public String toString() {
        final StringBuffer buff = new StringBuffer();
        buff.append("FOS CONFIGURATION \n");
        buff.append("ipaddr :[" + this.ipaddr + "] \n");
        buff.append("mode :[" + this.mode + "] \n");
        buff.append("replace IP :[" + this.replaceIP + "] \n");
        buff.append("peerIP :[" + this.peerIP + "] \n");
        buff.append("updateCounterInterval :[" + this.updateCounterInterval + "] \n");
        buff.append("dbFailureRetryCount :[" + this.dbFailureRetryCount + "] \n");
        buff.append("dbRetryInterval :[" + this.dbRetryInterval + "] \n");
        buff.append("ratioOfPollIntervaltoUpdateCounter:[" + this.ratioOfPollIntervaltoUpdateCounter + "] \n");
        buff.append("pollInterval\t:[" + this.pollInterval + "] \n");
        buff.append("pollFailureRetryCount\t:[" + this.pollFailureRetryCount + "] \n");
        buff.append("isPreferredMasterEnabled:[" + this.isPreferredMasterEnabled + "] \n");
        buff.append("takeOverHandler :[" + this.takeOverHandler + "] \n");
        buff.append("fileReplicationHandler :[" + this.fileReplicationHandler + "] \n");
        buff.append("replConf:[" + this.replConf + "] \n");
        buff.append("replicationEnabled:[" + this.replicationEnabled + "] \n");
        buff.append("masterHealthDetectors\t:[" + this.masterHealthDetectors + "] \n");
        buff.append("publicIPEnabled :[" + this.publicIPEnabled + "] \n");
        buff.append("publicIP :[" + this.publicIP + "] \n");
        buff.append("publicIPIfName :[" + this.publicIPIfName + "] \n");
        buff.append("publicIPNetMask :[" + this.publicIPNetMask + "] \n");
        buff.append("errorHandler :[" + this.errorHandler + "] \n");
        buff.append("ipCheckIntervalInSecs :[" + this.ipCheckIntervalInSecs + "] \n");
        buff.append("ipCheckRetryCount :[" + this.ipCheckRetryCount + "] \n");
        buff.append("toolsDir:[" + this.toolsDir + "] \n");
        buff.append("httpPollEnabled:[" + this.httpPollEnabled + "] \n");
        buff.append("hostnameVerifierClassName:[" + this.hostnameVerifierClassName + "] \n");
        buff.append("httpPollConnTimeout:[" + this.httpPollConnTimeout + "] \n");
        buff.append("httpPollReadTimeout:[" + this.httpPollReadTimeout + "] \n");
        buff.append("httpPollIntervalInSecs:[" + this.httpPollIntervalInSecs + "] \n");
        buff.append("httpPollRetryCount:[" + this.httpPollRetryCount + "] \n");
        buff.append("httpPollURI:[" + this.httpPollURI + "] \n");
        buff.append("downgradeURI:[" + this.downgradeURI + "] \n");
        buff.append("VersionHandler:[" + this.versionHandler + "] \n");
        return buff.toString();
    }
    
    static {
        FOSConfig.server_home = ((Configuration.getString("server.home") != null) ? Configuration.getString("server.home") : Configuration.getString("app.home"));
        OUT = Logger.getLogger(FOSConfig.class.getName());
    }
}
