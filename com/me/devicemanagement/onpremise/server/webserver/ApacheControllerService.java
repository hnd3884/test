package com.me.devicemanagement.onpremise.server.webserver;

import java.util.Hashtable;
import java.util.Properties;
import org.bouncycastle.jcajce.provider.asymmetric.util.ExtendedInvalidKeySpecException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.devicemanagement.framework.server.certificate.verifier.CertificateVerificationException;
import java.security.cert.CertPathBuilderException;
import java.util.Set;
import com.me.devicemanagement.framework.server.certificate.verifier.CertificateVerifier;
import java.util.Collection;
import java.security.cert.Certificate;
import java.util.HashSet;
import java.util.List;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.certificate.CertificateUtils;
import com.me.devicemanagement.onpremise.start.servertroubleshooter.util.ServerTroubleshooterUtil;
import java.io.File;
import com.me.devicemanagement.framework.server.certificate.SSLCertificateUtil;
import com.me.devicemanagement.onpremise.server.settings.nat.NATHandler;
import java.util.HashMap;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.server.dbtuning.DBPostgresOptimizationUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.devicemanagement.onpremise.start.util.WebServerUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;

public class ApacheControllerService
{
    private Logger logger;
    private Logger troubleShootLog;
    private Logger dbPostgresOptimizelogger;
    private String sourceClass;
    private ApacheMonitorTask apacheMonitor;
    private static ApacheControllerService apacheControllerService;
    
    public ApacheControllerService() {
        this.logger = Logger.getLogger("WebServerControllerLogger");
        this.troubleShootLog = Logger.getLogger("ServerTroubleshooterLogger");
        this.dbPostgresOptimizelogger = Logger.getLogger("DbOptimizationLog");
        this.sourceClass = "ApacheControllerService";
        this.apacheMonitor = null;
    }
    
    public static ApacheControllerService getInstance() {
        if (ApacheControllerService.apacheControllerService == null) {
            ApacheControllerService.apacheControllerService = new ApacheControllerService();
        }
        return ApacheControllerService.apacheControllerService;
    }
    
    public void startApacheService() throws Exception {
        final String sourceMethod = "start";
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Starting WebServerController Service...");
        final String serverHome = SyMUtil.getInstallationDir();
        final Properties wsProps = WebServerUtil.getWebServerSettings();
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "WebServer settings configured is: " + wsProps);
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
            this.troubleShootLog.log(Level.INFO, "Going to check apache server certificates and key");
            final String certificateFilePath = SSLCertificateUtil.getInstance().getServerCertificateFilePath();
            if (!new File(certificateFilePath).exists()) {
                this.troubleShootLog.log(Level.INFO, "Certificate missing");
                ServerTroubleshooterUtil.getInstance().serverStartupFailure("ssl_certificate_missing");
            }
            else if (SSLCertificateUtil.getInstance().isThirdPartySSLInstalled()) {
                this.troubleShootLog.log(Level.INFO, "Third Party Certificate installed for apache");
                final Certificate serverCertificate = CertificateUtils.loadX509CertificateFromFile(new File(SSLCertificateUtil.getInstance().getServerCertificateFilePath()));
                final List<String> filePaths = new ArrayList<String>();
                final String intermediatePath = SSLCertificateUtil.getInstance().getIntermediateCertificateFilePath();
                if (intermediatePath != null && ApiFactoryProvider.getFileAccessAPI().isFileExists(intermediatePath)) {
                    filePaths.add(intermediatePath);
                }
                final String caPath = SSLCertificateUtil.getInstance().getServerCACertificateFilePath();
                if (caPath != null && ApiFactoryProvider.getFileAccessAPI().isFileExists(caPath)) {
                    filePaths.add(caPath);
                }
                final Set<Certificate> intermediateCertificateList = new HashSet<Certificate>(CertificateUtils.splitMultipleCertificatesInEachFileToCertificateList((List)filePaths));
                intermediateCertificateList.addAll(CertificateUtils.getTrustedRootCACertificatesFromCACerts());
                try {
                    CertificateVerifier.verifyCertificate(serverCertificate, (Set)intermediateCertificateList);
                    this.troubleShootLog.log(Level.INFO, "Chain certificate missing");
                    SyMUtil.updateSyMParameter("CERT_CHAIN_NOT_VERIFIED", "false");
                    MessageProvider.getInstance().hideMessage("CERT_CHAIN_NOT_VERIFIED");
                }
                catch (final CertificateVerificationException exp) {
                    final Throwable rootCause = exp.getCause();
                    if (rootCause instanceof CertPathBuilderException) {
                        SyMUtil.updateSyMParameter("CERT_CHAIN_NOT_VERIFIED", "true");
                        MessageProvider.getInstance().unhideMessage("CERT_CHAIN_NOT_VERIFIED");
                        ServerTroubleshooterUtil.getInstance().serverStartupFailure("ssl_certificate_chain_issue");
                        this.logger.log(Level.SEVERE, "Certificate Chain is not verified. Need to re upload certificate with complete chain.");
                        this.troubleShootLog.log(Level.SEVERE, "Certificate Chain is not verified. Need to re upload certificate with complete chain.");
                    }
                    else {
                        this.logger.log(Level.SEVERE, "Exception in verifyCertificateChain", (Throwable)exp);
                        this.troubleShootLog.log(Level.SEVERE, "Exception in verifyCertificateChain", (Throwable)exp);
                    }
                }
                try {
                    this.troubleShootLog.log(Level.INFO, "Going to check server crt and key match");
                    final String certificateKeyFile = SSLCertificateUtil.getInstance().getServerPrivateKeyFilePath();
                    final boolean isValid = CertificateUtils.isValidCertificateAndPrivateKey((Certificate)CertificateUtils.loadX509CertificateFromFile(new File(certificateFilePath)), CertificateUtils.loadPrivateKeyFromFile(new File(certificateKeyFile)));
                    if (isValid == Boolean.FALSE) {
                        this.troubleShootLog.log(Level.INFO, "server crt and key mismatch found");
                        ServerTroubleshooterUtil.getInstance().serverStartupFailure("ssl_certificate_key_mismatch");
                    }
                    this.troubleShootLog.log(Level.INFO, "server crt and key match found");
                }
                catch (final ExtendedInvalidKeySpecException e) {
                    this.troubleShootLog.log(Level.INFO, "server key is encrypted");
                    ServerTroubleshooterUtil.getInstance().serverStartupFailure("ssl_certificate_key_encrypted");
                    throw new SyMException(80030, "Encrypted key file uploaded. Decrypt it and upload it again", (Throwable)new Exception());
                }
                catch (final Exception e2) {
                    this.logger.log(Level.SEVERE, "Exception while calling serverStartupFailure for certificate and key verification");
                }
            }
            else {
                SyMUtil.updateSyMParameter("CERT_CHAIN_NOT_VERIFIED", "false");
                MessageProvider.getInstance().hideMessage("CERT_CHAIN_NOT_VERIFIED");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Error in certificate verification", ex);
        }
        if (SSLCertificateUtil.webServerName.equalsIgnoreCase("apache")) {
            final String stopResult = WebServerUtil.apacheHttpdInvoke(serverHome, "stop");
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Result of Stop Apache Service: " + stopResult);
            WebServerUtil.deleteMaintenanceHtmlFile();
            final String startResult = WebServerUtil.apacheHttpdInvoke(serverHome, "start");
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Result of Start Apache Service: " + startResult);
            final int waitingForApacheStartupInSeconds = 0;
            if (this.ApacheCurrentRunningStatus(waitingForApacheStartupInSeconds, false)) {
                final String checkResults = WebServerUtil.checkApacheService(serverHome);
                SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Result of Current Apache Service status : " + checkResults);
            }
            if (WebServerUtil.isApacheServiceStopped()) {
                SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Result of Start Apache Service : Stopped");
                final Properties accessStatusProps = WebServerUtil.getRepositoriesAccessStatus();
                SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Result of Start Apache Service :Share Access Status " + accessStatusProps);
                final Boolean storeStatus = Boolean.valueOf(accessStatusProps.getProperty("store_access_status"));
                final Boolean swStatus = Boolean.valueOf(accessStatusProps.getProperty("swrepository_access_status"));
                final Boolean osdStatus = Boolean.valueOf(accessStatusProps.getProperty("osd_repository_access_status"));
                final Properties webServerProps = WebServerUtil.getWebServerSettings();
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
                else if (!osdStatus) {
                    this.logger.log(Level.INFO, "Server stopped due to OSD repository path not reachable...");
                }
                else if (startResult.contains("no listening sockets available")) {
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
                        this.troubleShootLog.log(Level.INFO, "Going to check apache port is in use");
                        ServerTroubleshooterUtil.getInstance().serverStartupFailure("webserver_port_in_use");
                    }
                    catch (final Exception e4) {
                        this.logger.log(Level.SEVERE, "Exception while calling serverStartupFailure for apache port is in use");
                        this.troubleShootLog.log(Level.SEVERE, "Exception while calling serverStartupFailure for apache port is in use");
                    }
                    this.logger.log(Level.INFO, " Going to delete ws.modtime file ");
                    final String wsModifiedTime = serverHome + File.separator + "conf" + File.separator + "ws.modtime";
                    final File wsModifiedTimeFile = new File(wsModifiedTime);
                    if (wsModifiedTimeFile.exists()) {
                        this.logger.log(Level.INFO, "ws.modtime file deleted status : " + wsModifiedTimeFile.delete());
                    }
                }
                else {
                    SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Result of Start Apache Service : Stopped due to some Reason...");
                }
                if (!osdStatus) {
                    SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Going to restart Desktopcentral service...");
                    com.me.devicemanagement.onpremise.server.util.SyMUtil.triggerServerRestart("OSD repository path not reachable.");
                }
                else {
                    SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Going to stop Desktopcentral service...");
                    com.me.devicemanagement.onpremise.server.util.SyMUtil.triggerServerShutdown("Cannot start Apache service.");
                }
            }
            else {
                WebServerUtil.deleteShareAccessPropsFile();
                WebServerControllerService.serverStartupTrack();
            }
            final String checkResult = WebServerUtil.checkApacheService(serverHome);
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Result of Check Apache Service: " + checkResult);
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Apache Monitor Task is getting created...");
            (this.apacheMonitor = new ApacheMonitorTask()).schedule();
        }
        else {
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Apache is not enabled. No need to do anything here for Tomcat...");
        }
    }
    
    public void stopApacheService() throws Exception {
        final String sourceMethod = "stop";
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Stopping WebServerController Service...");
        if (this.apacheMonitor != null) {
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Stopping Apache Monitor Task...");
            this.apacheMonitor.suspend();
        }
        WebServerUtil.stopApacheService();
    }
    
    public void destroy() throws Exception {
        final String sourceMethod = "destroy";
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Destroying WebServerController Service...");
    }
    
    private boolean ApacheCurrentRunningStatus(int seconds, boolean isExceptionTraceCaughtInLogs) {
        final String sourceMethod = "start";
        try {
            final String serverHome = SyMUtil.getInstallationDir();
            if (!WebServerUtil.isApacheServiceRunning() && !WebServerUtil.isApacheServiceStopped()) {
                final String checkResult = WebServerUtil.checkApacheService(serverHome);
                if (!checkResult.equalsIgnoreCase("Syntax OK") && !isExceptionTraceCaughtInLogs) {
                    SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Apache server starting troubled due to : " + checkResult);
                    isExceptionTraceCaughtInLogs = true;
                }
                if (seconds > 5) {
                    return true;
                }
                ++seconds;
                Thread.sleep(1000L);
                this.ApacheCurrentRunningStatus(seconds, isExceptionTraceCaughtInLogs);
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception occurred while checking Apache service current status : ", (Throwable)ex);
        }
        return true;
    }
    
    static {
        ApacheControllerService.apacheControllerService = null;
    }
}
