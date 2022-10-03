package com.me.devicemanagement.onpremise.server.webserver;

import java.util.Hashtable;
import java.security.cert.Certificate;
import java.util.HashMap;
import com.me.devicemanagement.onpremise.server.certificate.ServerSSLCertificateHandler;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.security.cert.X509Certificate;
import com.me.devicemanagement.framework.server.certificate.CertificateUtils;
import java.io.File;
import com.me.devicemanagement.framework.server.certificate.CertificateCacheHandler;
import java.util.logging.Level;
import java.util.Properties;
import com.me.devicemanagement.onpremise.start.servertroubleshooter.util.ServerTroubleshooterUtil;
import com.me.devicemanagement.framework.server.certificate.SSLCertificateUtil;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;
import com.adventnet.mfw.service.Service;

public class WebServerControllerService implements Service
{
    private static Logger logger;
    private Logger troubleShootLog;
    private Logger dbPostgresOptimizelogger;
    private String sourceClass;
    private ApacheMonitorTask apacheMonitor;
    
    public WebServerControllerService() {
        this.troubleShootLog = Logger.getLogger("ServerTroubleshooterLogger");
        this.dbPostgresOptimizelogger = Logger.getLogger("DbOptimizationLog");
        this.sourceClass = "WebServerControllerService";
        this.apacheMonitor = null;
    }
    
    public void create(final DataObject arg0) throws Exception {
        final String sourceMethod = "create";
        SyMLogger.info(WebServerControllerService.logger, this.sourceClass, sourceMethod, "\n\n\n");
        SyMLogger.info(WebServerControllerService.logger, this.sourceClass, sourceMethod, "____________________________________");
        SyMLogger.info(WebServerControllerService.logger, this.sourceClass, sourceMethod, "Creating WebServerController Service...");
        SyMLogger.info(WebServerControllerService.logger, this.sourceClass, sourceMethod, "____________________________________");
    }
    
    public void start() throws Exception {
        final String sourceMethod = "start";
        SyMLogger.info(WebServerControllerService.logger, this.sourceClass, sourceMethod, "Entered into Start method of WebServerControllerService");
        if ("apache".equalsIgnoreCase(SSLCertificateUtil.webServerName)) {
            ApacheControllerService.getInstance().startApacheService();
        }
        else if ("nginx".equalsIgnoreCase(SSLCertificateUtil.webServerName)) {
            NginxControllerService.getInstance().startNginxServer();
        }
        if (SSLCertificateUtil.getInstance().isThirdPartySSLInstalled()) {
            this.validateCertAndInvokeListeners();
        }
        SyMLogger.info(WebServerControllerService.logger, this.sourceClass, sourceMethod, "WebServerControllerService started successfully");
    }
    
    public void stop() throws Exception {
        final String sourceMethod = "stop()";
        SyMLogger.info(WebServerControllerService.logger, this.sourceClass, sourceMethod, "Stopping WebServerController Service...");
        ApacheControllerService.getInstance().stopApacheService();
        NginxControllerService.getInstance().stopNginxServer();
        SyMLogger.info(WebServerControllerService.logger, this.sourceClass, sourceMethod, "WebServerControllerService stopped successfully");
    }
    
    public void destroy() throws Exception {
        final String sourceMethod = "destroy";
        SyMLogger.info(WebServerControllerService.logger, this.sourceClass, sourceMethod, "Destroying WebServerController Service...");
    }
    
    public static void serverStartupTrack() throws Exception {
        final boolean isFileExist = ServerTroubleshooterUtil.getInstance().isStartupConfFileAvailable();
        final Properties props = new Properties();
        ((Hashtable<String, String>)props).put("LST_StartupStatus", "Success");
        ((Hashtable<String, String>)props).put("LST_FailureReason", "--");
        ((Hashtable<String, String>)props).put("ServerSuccStartedAtleastOnce", "Yes");
        ((Hashtable<String, String>)props).put("IsDataNeedToPost", "No");
        if (isFileExist) {
            final String existFSTstartupStatus = ServerTroubleshooterUtil.getInstance().getValueFromStartupInfoConf("FST_StartupStatus");
            if (existFSTstartupStatus == null) {
                ((Hashtable<String, String>)props).put("FST_StartupStatus", "Success");
                ((Hashtable<String, String>)props).put("FST_FailureReason", "--");
                WebServerControllerService.logger.log(Level.INFO, "First Startup Details Keys Added in Properties when the key not available.[In WebServerContrllerService]");
            }
        }
        else {
            ((Hashtable<String, String>)props).put("WebconsoleOpened", "YetToOpen");
            ((Hashtable<String, String>)props).put("FST_StartupStatus", "Success");
            ((Hashtable<String, String>)props).put("FST_FailureReason", "--");
            WebServerControllerService.logger.log(Level.INFO, "First Startup Details Keys Added in Properties when the file not exist.[In WebServerContrllerService]");
        }
        WebServerControllerService.logger.log(Level.INFO, "Going to write file with properties from WebServerControllerService  ");
        ServerTroubleshooterUtil.getInstance().writeStartupFailureInfoToConfFile(props);
    }
    
    private void validateCertAndInvokeListeners() {
        try {
            final HashMap certCache = CertificateCacheHandler.getInstance().getAll();
            final boolean isSSLHostNameMisMatch = certCache.containsKey("SSL_HOST_NAME_MISMATCH") && certCache.get("SSL_HOST_NAME_MISMATCH");
            final boolean isCertChainNotVerified = certCache.containsKey("CERT_CHAIN_NOT_VERIFIED") && certCache.get("CERT_CHAIN_NOT_VERIFIED");
            final boolean isCertExpired = certCache.containsKey("SSL_CERTIFICATE_EXPIRED") && certCache.get("SSL_CERTIFICATE_EXPIRED");
            final Certificate serverCertificate = CertificateUtils.loadX509CertificateFromFile(new File(SSLCertificateUtil.getInstance().getServerCertificateFilePath()));
            final String serverCertThumbPrint = SSLCertificateUtil.getInstance().getThumbPrint((X509Certificate)serverCertificate);
            final String CertThumbPrint = SyMUtil.getSyMParameterFromDB("server_certificate_thumbprint");
            final boolean isCertThumbPrintMissMatch = CertThumbPrint == null || !CertThumbPrint.equals(serverCertThumbPrint);
            if (isCertThumbPrintMissMatch && !isCertChainNotVerified && !isSSLHostNameMisMatch && !isCertExpired) {
                WebServerControllerService.logger.log(Level.INFO, "Certificate found to be not imported through UI and is valid, Hence invoking listeners.");
                ServerSSLCertificateHandler.getInstance().invokeServerSSLCertificateChangeListeners();
                SyMUtil.updateSyMParameter("server_certificate_thumbprint", serverCertThumbPrint);
            }
        }
        catch (final Exception ex) {
            WebServerControllerService.logger.log(Level.SEVERE, "Exception in validateCertAndInvokeListeners method", ex);
        }
    }
    
    static {
        WebServerControllerService.logger = Logger.getLogger("WebServerControllerLogger");
    }
}
