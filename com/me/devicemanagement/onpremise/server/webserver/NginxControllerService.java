package com.me.devicemanagement.onpremise.server.webserver;

import java.util.Hashtable;
import org.json.JSONObject;
import com.zoho.framework.utils.FileUtils;
import java.util.Properties;
import com.me.devicemanagement.framework.utils.FrameworkConfigurations;
import com.me.devicemanagement.onpremise.start.util.NginxServerUtils;
import org.bouncycastle.jcajce.provider.asymmetric.util.ExtendedInvalidKeySpecException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.security.cert.Certificate;
import com.me.devicemanagement.framework.server.certificate.CertificateUtils;
import com.me.devicemanagement.onpremise.start.servertroubleshooter.util.ServerTroubleshooterUtil;
import java.io.File;
import com.me.devicemanagement.framework.server.certificate.SSLCertificateUtil;
import com.me.devicemanagement.onpremise.server.settings.nat.NATHandler;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.HashMap;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.server.dbtuning.DBPostgresOptimizationUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.devicemanagement.onpremise.start.util.WebServerUtil;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;

public class NginxControllerService
{
    private Logger logger;
    private Logger troubleShootLog;
    private Logger dbPostgresOptimizelogger;
    private String sourceClass;
    private static NginxControllerService nginxControllerService;
    private static final String IS_REPOSITORIES_ACCESS_CHECK_REQUIRED = "is_repositories_access_check_required";
    
    public NginxControllerService() {
        this.logger = Logger.getLogger("WebServerControllerLogger");
        this.troubleShootLog = Logger.getLogger("ServerTroubleshooterLogger");
        this.dbPostgresOptimizelogger = Logger.getLogger("DbOptimizationLog");
        this.sourceClass = "NginxControllerService";
    }
    
    public static NginxControllerService getInstance() {
        if (NginxControllerService.nginxControllerService == null) {
            NginxControllerService.nginxControllerService = new NginxControllerService();
        }
        return NginxControllerService.nginxControllerService;
    }
    
    public void startNginxServer() throws Exception {
        final String sourceMethod = "startNginxService";
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Entered into startNginxServie Method.");
        final Properties wsProps = WebServerUtil.getWebServerSettings();
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "WebServer settings configured is: " + wsProps);
        this.checkAndGenerateLogsAndTempFolders();
        MessageProvider.getInstance().hideMessage("REQUIRED_SERVICE_RESTART");
        MessageProvider.getInstance().hideMessage("REQUIRED_SERVICE_RESTART_POSTGRES");
        final String activeDb = DBUtil.getActiveDBName();
        if (activeDb.equalsIgnoreCase("postgres")) {
            final Boolean isPgConfFileReset = DBPostgresOptimizationUtil.getInstance().resetPgSQLConfAtStartup();
            if (isPgConfFileReset) {
                MessageProvider.getInstance().unhideMessage("RESTART_SERVER_PGCONF_RESET");
            }
            else {
                MessageProvider.getInstance().hideMessage("RESTART_SERVER_PGCONF_RESET");
            }
            this.logger.log(Level.INFO, "Replaced postgres_ext.conf File from 'conf' folder: " + isPgConfFileReset);
            HashMap postgresExtMap = new HashMap();
            postgresExtMap = DBPostgresOptimizationUtil.getInstance().getComputedRAMDetails();
            if (Boolean.valueOf(String.valueOf(postgresExtMap.get("appliedRAMExceedsSys")))) {
                MessageProvider.getInstance().unhideMessage("PGCONF_RAM_EXCEEDS_SYSRAM");
            }
            else {
                MessageProvider.getInstance().hideMessage("PGCONF_RAM_EXCEEDS_SYSRAM");
            }
            if (Boolean.valueOf(String.valueOf(postgresExtMap.get("ispgExtFileMissing")))) {
                MessageProvider.getInstance().unhideMessage("PGCONF_FILE_MISSING");
            }
            else {
                MessageProvider.getInstance().hideMessage("PGCONF_FILE_MISSING");
            }
            this.logger.log(Level.INFO, "check for PGCONF_RAM_EXCEEDS_SYSRAM is done: ");
            this.dbPostgresOptimizelogger.log(Level.INFO, "Replaced postgres_ext.conf File from 'conf' folder (since the Server RAM is not more than 4 GB): " + isPgConfFileReset);
        }
        else {
            MessageProvider.getInstance().hideMessage("PGCONF_RAM_EXCEEDS_SYSRAM");
        }
        SyMUtil.deleteSyMParameter("SERVICE_RESTARTED");
        try {
            final Properties natProps = NATHandler.getNATConfigurationProperties();
            String givenNATAddress = null;
            if (!natProps.isEmpty()) {
                givenNATAddress = ((Hashtable<K, String>)natProps).get("NAT_ADDRESS");
            }
            SSLCertificateUtil.getInstance().verifyCertificate(givenNATAddress);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Error in certificate verification", ex);
        }
        try {
            this.troubleShootLog.log(Level.INFO, "Going to check Nginx server certificates and key");
            final String certificateFilePath = SSLCertificateUtil.getInstance().getServerCertificateFilePath();
            Label_0614: {
                if (!new File(certificateFilePath).exists()) {
                    this.troubleShootLog.log(Level.INFO, "Certificate missing");
                    ServerTroubleshooterUtil.getInstance().serverStartupFailure("ssl_certificate_missing");
                }
                else {
                    if (SSLCertificateUtil.getInstance().isThirdPartySSLInstalled()) {
                        this.troubleShootLog.log(Level.INFO, "Third Party Certificate installed for nginx");
                        try {
                            this.troubleShootLog.log(Level.INFO, "Going to check server crt and key match");
                            final String certificateKeyFile = SSLCertificateUtil.getInstance().getServerPrivateKeyFilePath();
                            final boolean isValid = CertificateUtils.isValidCertificateAndPrivateKey((Certificate)CertificateUtils.loadX509CertificateFromFile(new File(certificateFilePath)), CertificateUtils.loadPrivateKeyFromFile(new File(certificateKeyFile)));
                            if (isValid == Boolean.FALSE) {
                                this.troubleShootLog.log(Level.INFO, "server crt and key mismatch found");
                                ServerTroubleshooterUtil.getInstance().serverStartupFailure("ssl_certificate_key_mismatch");
                            }
                            this.troubleShootLog.log(Level.INFO, "server crt and key match found");
                            break Label_0614;
                        }
                        catch (final ExtendedInvalidKeySpecException e) {
                            this.troubleShootLog.log(Level.INFO, "server key is encrypted");
                            ServerTroubleshooterUtil.getInstance().serverStartupFailure("ssl_certificate_key_encrypted");
                            throw new SyMException(80030, "Encrypted key file uploaded. Decrypt it and upload it again", (Throwable)new Exception());
                        }
                        catch (final Exception e2) {
                            this.logger.log(Level.SEVERE, "Exception while calling serverStartupFailure for certificate and key verification");
                            break Label_0614;
                        }
                    }
                    SyMUtil.updateSyMParameter("CERT_CHAIN_NOT_VERIFIED", "false");
                    MessageProvider.getInstance().hideMessage("CERT_CHAIN_NOT_VERIFIED");
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Error in certificate verification", ex);
        }
        final String serverHome = System.getProperty("server.home");
        final String stopResult = NginxServerUtils.stopNginxServer(serverHome);
        this.logger.log(Level.INFO, "Result of Stop Nginx  Server: " + stopResult);
        WebServerUtil.deleteMaintenanceHtmlFile();
        final Properties webServerProps = WebServerUtil.getWebServerSettings();
        final JSONObject frameworkconfigurations = FrameworkConfigurations.getFrameworkConfigurations();
        if (!frameworkconfigurations.has("is_repositories_access_check_required") || frameworkconfigurations.getBoolean("is_repositories_access_check_required") != Boolean.FALSE) {
            final Properties accessStatusProps = WebServerUtil.getRepositoriesAccessStatus();
            this.logger.log(Level.INFO, "Result of Start Nginx  Service :Share Access Status " + accessStatusProps);
            final Boolean storeStatus = Boolean.valueOf(accessStatusProps.getProperty("store_access_status"));
            final Boolean swStatus = Boolean.valueOf(accessStatusProps.getProperty("swrepository_access_status"));
            if (!storeStatus || !swStatus) {
                WebServerUtil.createShareAccessPropsFile(accessStatusProps);
                WebServerUtil.storeProperWebServerSettings(webServerProps);
                try {
                    this.troubleShootLog.log(Level.INFO, "Going to check Software or patch store not reachable");
                    ServerTroubleshooterUtil.getInstance().serverStartupFailure("software_or_patch_store_not_reachable");
                }
                catch (final Exception e3) {
                    this.logger.log(Level.SEVERE, "Exception while calling serverStartupFailure for patch store or software location not reachable");
                    this.troubleShootLog.log(Level.SEVERE, "Exception while calling serverStartupFailure for patch store or software location not reachable");
                }
            }
        }
        else {
            final String startResult = NginxServerUtils.startNginxServer(serverHome);
            this.logger.log(Level.INFO, "Result of start Nginx Server " + startResult);
            if (!NginxServerUtils.isNginxServerRunning()) {
                if (startResult.contains("bind") && startResult.contains("failed")) {
                    final String httpPort = webServerProps.getProperty("http.port");
                    final String httpsPort = webServerProps.getProperty("https.port");
                    final Properties portProps = new Properties();
                    if (startResult.contains(httpPort)) {
                        portProps.setProperty("http.port", httpPort);
                    }
                    if (startResult.contains(httpsPort)) {
                        portProps.setProperty("https.port", httpsPort);
                    }
                    WebServerUtil.createShareAccessPropsFile(portProps);
                    try {
                        this.troubleShootLog.log(Level.INFO, "Going to check nginx port is in use");
                        ServerTroubleshooterUtil.getInstance().serverStartupFailure("webserver_port_in_use");
                    }
                    catch (final Exception e4) {
                        this.logger.log(Level.SEVERE, "Exception while calling serverStartupFailure for nginx port is in use");
                        this.troubleShootLog.log(Level.SEVERE, "Exception while calling serverStartupFailure for nginx port is in use");
                    }
                    this.logger.log(Level.INFO, " Going to delete ws.modtime file ");
                    final String wsModifiedTime = serverHome + File.separator + "conf" + File.separator + "ws.modtime";
                    final File wsModifiedTimeFile = new File(wsModifiedTime);
                    if (wsModifiedTimeFile.exists()) {
                        this.logger.log(Level.INFO, "ws.modtime file deleted status : " + wsModifiedTimeFile.delete());
                    }
                }
                else {
                    this.logger.log(Level.INFO, "Result of Start Nginx Service : Stopped due to some Reason..." + startResult);
                    this.logger.log(Level.INFO, "Nginx Startup Failed. Going to start Apache Service");
                    NginxServerUtils.trackNginxStartupError(startResult);
                    WebServerUtil.setWebserverName("apache");
                    SSLCertificateUtil.setWebServerName("apache");
                    new ApacheControllerService().startApacheService();
                }
            }
            else {
                final String nginxErrorJsonFilePath = System.getProperty("server.home") + File.separator + "logs" + File.separator + "NginxErrorFile.json";
                if (new File(nginxErrorJsonFilePath).exists()) {
                    FileUtils.deleteFile(nginxErrorJsonFilePath);
                }
                WebServerUtil.deleteShareAccessPropsFile();
                WebServerControllerService.serverStartupTrack();
            }
        }
    }
    
    public void stopNginxServer() throws Exception {
        this.logger.log(Level.INFO, "Enterd into method stopNginxService().");
        final String stopResult = NginxServerUtils.stopNginxServer(System.getProperty("server.home"));
        this.logger.log(Level.INFO, "Result of stop Nginx Server " + stopResult);
    }
    
    public void reloadNginxServer() throws Exception {
        this.logger.log(Level.INFO, "Entered into method reloadNginxServer().");
        final String reloadResult = NginxServerUtils.reloadNginxServer();
        this.logger.log(Level.INFO, "Result of reload Nginx Server " + reloadResult);
    }
    
    public void checkAndGenerateLogsAndTempFolders() {
        final String nginxDir = System.getProperty("server.home") + File.separator + "nginx";
        final File logsFolder = new File(nginxDir + File.separator + "logs");
        final File tempFolder = new File(nginxDir + File.separator + "temp");
        if (!logsFolder.exists()) {
            logsFolder.mkdirs();
        }
        if (!tempFolder.exists()) {
            tempFolder.mkdirs();
        }
    }
    
    static {
        NginxControllerService.nginxControllerService = null;
    }
}
