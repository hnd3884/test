package com.me.devicemanagement.onpremise.server.certificate;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.CopyOption;
import java.nio.file.Paths;
import com.me.devicemanagement.framework.server.util.DCMetaDataUtil;
import java.util.Properties;
import com.me.devicemanagement.onpremise.server.fileaccess.FileAccessImpl;
import com.zoho.framework.utils.FileUtils;
import com.me.devicemanagement.onpremise.start.util.WebServerUtil;
import java.security.PrivateKey;
import com.me.devicemanagement.framework.server.certificate.CertificateUtils;
import java.io.File;
import com.me.devicemanagement.onpremise.start.util.CertificateGenerator;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.security.cert.CertificateException;
import java.util.Iterator;
import java.util.Collection;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.util.Calendar;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SecurityUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class SSLCertificateUtil extends com.me.devicemanagement.framework.server.certificate.SSLCertificateUtil
{
    private static SSLCertificateUtil sslCertUtil;
    public String commonName;
    public static final String SAN_CERT_APPLICABLE = "SAN_CERT_APPLICABLE";
    private Logger logger;
    
    public SSLCertificateUtil() {
        this.commonName = "ManageEngine";
        this.logger = Logger.getLogger("ImportCertificateLogger");
    }
    
    public static SSLCertificateUtil getInstance() {
        if (SSLCertificateUtil.sslCertUtil == null) {
            SSLCertificateUtil.sslCertUtil = new SSLCertificateUtil();
        }
        return SSLCertificateUtil.sslCertUtil;
    }
    
    public Boolean isCertRegenerationRequired(final List serverInfo) throws Exception {
        Boolean isRegenerationReqd = false;
        final X509Certificate serverCertificate = com.me.devicemanagement.framework.server.certificate.SSLCertificateUtil.getCertificate(this.getServerCertificateFilePath());
        final Collection subjectAlternativeNamesFromCert = serverCertificate.getSubjectAlternativeNames();
        final List subjectAlternativeNames = new ArrayList();
        final String isCertRegenRequired = SecurityUtil.getSecurityParameter("REGEN_LEAF_CERT");
        if (isCertRegenRequired != null && isCertRegenRequired.equalsIgnoreCase("true")) {
            isRegenerationReqd = true;
        }
        if (null != subjectAlternativeNamesFromCert) {
            if (0 != subjectAlternativeNamesFromCert.size()) {
                this.logger.log(Level.INFO, "Subject Alternative Names from Certificate is Not Null and size is not zero. Size of SAN is " + subjectAlternativeNamesFromCert.size());
                for (final List eachSANdetail : subjectAlternativeNamesFromCert) {
                    subjectAlternativeNames.add(eachSANdetail.get(1).toString());
                }
                final Boolean isServerInfoChanged = this.isServerInfoChanged(subjectAlternativeNames, serverInfo);
                this.logger.log(Level.INFO, "Is Server Info Different in Certificate and DB " + isServerInfoChanged);
                if (isServerInfoChanged) {
                    this.logger.log(Level.INFO, "Server Info Changed. Regenerating Server Certificate");
                    isRegenerationReqd = true;
                }
            }
            else {
                this.logger.log(Level.INFO, "No SAN found in the server certificate. Generating SAN certificate for the first time");
                isRegenerationReqd = true;
            }
        }
        else {
            this.logger.log(Level.INFO, "No SAN found in the server certificate. Generating SAN certificate for the first time");
            isRegenerationReqd = true;
        }
        if (!isRegenerationReqd) {
            try {
                final Calendar calendar = Calendar.getInstance();
                calendar.add(2, 1);
                serverCertificate.checkValidity(calendar.getTime());
            }
            catch (final CertificateNotYetValidException | CertificateExpiredException exception) {
                this.logger.log(Level.WARNING, "Server Certificate going to expire in next 30 days.", exception);
                isRegenerationReqd = true;
            }
        }
        this.logger.info("isRegenerationReqd " + isRegenerationReqd);
        return isRegenerationReqd;
    }
    
    Boolean isServerInfoChanged(final List sanFromCertificate, final List serverInfoFromDB) {
        if (!sanFromCertificate.containsAll(serverInfoFromDB)) {
            return true;
        }
        return false;
    }
    
    public Boolean isCertificateRegenerationAllowed() {
        try {
            if (com.me.devicemanagement.framework.server.certificate.SSLCertificateUtil.getInstance().isThirdPartySSLInstalled()) {
                return false;
            }
            final Criteria sanCertApplicableCriteria = new Criteria(new Column("ServerParams", "PARAM_NAME"), (Object)"SAN_CERT_APPLICABLE", 0);
            final DataObject serverParamsDO = DataAccess.get("ServerParams", sanCertApplicableCriteria);
            if (null == serverParamsDO || serverParamsDO.isEmpty()) {
                return true;
            }
            final Row serverParamsRow = serverParamsDO.getFirstRow("ServerParams");
            if (serverParamsRow.get("PARAM_VALUE").toString().equalsIgnoreCase("false")) {
                return false;
            }
            return true;
        }
        catch (final DataAccessException ex) {
            this.logger.log(Level.INFO, "No rows found for SAN Applicable Status from ServerParams" + ex);
            return true;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.INFO, "Exception in isCertificateRegenerateAllowed()" + ex2);
            return true;
        }
    }
    
    public List<String> getServerInfoFromDB() throws DataAccessException, SyMException {
        final List<String> serverInfo = new ArrayList<String>();
        final DataObject serverInfoDO = DataAccess.get("DCServerInfo", (Criteria)null);
        if (!serverInfoDO.isEmpty()) {
            final Row serverInfoRow = serverInfoDO.getFirstRow("DCServerInfo");
            serverInfo.add(serverInfoRow.get("SERVER_MAC_IPADDR").toString());
            final String serverSecIPAddr = serverInfoRow.get("SERVER_SEC_IPADDR").toString();
            if (!serverSecIPAddr.equalsIgnoreCase("--") && !"".equalsIgnoreCase(serverSecIPAddr)) {
                serverInfo.add(serverInfoRow.get("SERVER_SEC_IPADDR").toString());
            }
            serverInfo.add(serverInfoRow.get("SERVER_MAC_NAME").toString());
            serverInfo.add(serverInfoRow.get("SERVER_FQDN").toString());
        }
        final Boolean isFOSInDifferentNetwork = Boolean.parseBoolean(SyMUtil.getServerParameter("isFOSInDifferentNetwork"));
        if (isFOSInDifferentNetwork) {
            final DataObject fosServerInfoDO = SyMUtil.getFosServerInfoDO();
            if (!fosServerInfoDO.isEmpty() && fosServerInfoDO.containsTable("FOSServerDetails")) {
                serverInfo.add((String)fosServerInfoDO.getFirstValue("FOSServerDetails", "SERVER_MAC_NAME"));
                serverInfo.add((String)fosServerInfoDO.getFirstValue("FOSServerDetails", "SERVER_FQDN"));
            }
        }
        return serverInfo;
    }
    
    public boolean checkAndGenerateServerCertificate(final String natAddress) throws Exception {
        boolean isCertificateGenerated = false;
        if (!com.me.devicemanagement.framework.server.certificate.SSLCertificateUtil.getInstance().isThirdPartySSLInstalled()) {
            if (!this.isCertificateRegenerationAllowed()) {
                CertificateGenerator.getInstance().generateSelfSignedServerCACertificate(this.getServerCertificateFilePath(), this.getServerPrivateKeyFilePath(), natAddress, new String[0]);
            }
            else {
                final String rootCertPath = this.getSelfSignedRootCertificatePath();
                final String rootKeyPath = this.getSelfSignedRootKeyPath();
                final List serverInfo = this.getServerInfoFromDB();
                if (serverInfo != null) {
                    serverInfo.add(natAddress);
                    final X509Certificate rootCACert = CertificateUtils.loadX509CertificateFromFile(new File(rootCertPath));
                    final PrivateKey rootCAKey = CertificateUtils.loadPrivateKeyFromFile(new File(rootKeyPath));
                    CertificateUtils.generateServerSANCertificateFromRoot(rootCACert, rootCAKey, this.getServerCertificateFilePath(), this.getServerPrivateKeyFilePath(), this.commonName, serverInfo);
                    this.doPostCertificateGeneration();
                }
            }
            isCertificateGenerated = true;
        }
        return isCertificateGenerated;
    }
    
    public void doPostCertificateGeneration() throws Exception {
        final Properties webServerProps = WebServerUtil.getWebServerSettings();
        final CertificatePropertiesDAO certificatePropertiesDAO = CertificatePropertiesDAO.getInstance();
        SyMUtil.updateSyMParameter("SAN_CERTIFICATE_GENERATED", "true");
        final String webServerName = WebServerUtil.getWebServerName();
        if ("apache".equalsIgnoreCase(webServerName)) {
            this.copyCertsToWebServer("nginx");
        }
        else {
            this.copyCertsToWebServer("apache");
        }
        this.copyCertsToClientDataDir();
        if (SSLCertificateUtil.webServerName.equalsIgnoreCase("apache")) {
            webServerProps.setProperty("apache.ssl.intermediate.ca.file", "DMRootCA.crt");
            webServerProps.setProperty("apache.ssl.root.ca.file", "DMRootCA.crt");
        }
        else {
            File serverCrtTempFile = new File(WebServerUtil.getWebServerConfDir() + File.separator + "server-temp.crt");
            FileUtils.copyFile(new File(certificatePropertiesDAO.getServerCertificatePath()), serverCrtTempFile);
            final File appendedServerCrtFile = new File(certificatePropertiesDAO.getServerCertificatePath());
            if (!appendedServerCrtFile.exists()) {
                appendedServerCrtFile.createNewFile();
            }
            serverCrtTempFile = FileAccessImpl.appendMultipleFiles(serverCrtTempFile.getAbsolutePath(), certificatePropertiesDAO.getRootCertificatePath());
            FileUtils.copyFile(serverCrtTempFile, appendedServerCrtFile);
            serverCrtTempFile.delete();
        }
        WebServerUtil.storeProperWebServerSettings(webServerProps);
        WebServerUtil.refreshWebServerSettings();
        ServerSSLCertificateHandler.getInstance().invokeServerSSLCertificateChangeListeners();
    }
    
    public void copyCertsToClientDataDir() throws Exception {
        final String clientDataPath = DCMetaDataUtil.getInstance().getClientDataDir();
        final String rootCertificatePath = CertificatePropertiesDAO.getInstance().getRootCertificatePath();
        final File clientDataDir = new File(clientDataPath);
        if (clientDataDir.exists()) {
            final File serverCertificatesDir = new File(clientDataPath + File.separator + "server-certificates");
            if (!serverCertificatesDir.exists()) {
                serverCertificatesDir.mkdir();
            }
            final String rootCertClientData = serverCertificatesDir + File.separator + "DMRootCA.crt";
            Files.copy(Paths.get(rootCertificatePath, new String[0]), Paths.get(rootCertClientData, new String[0]), StandardCopyOption.REPLACE_EXISTING);
        }
    }
    
    private void copyCertsToWebServer(final String destWebServer) throws Exception {
        final CertificatePropertiesDAO certificatePropertiesDAO = CertificatePropertiesDAO.getInstance();
        final String serverCertFilePath = certificatePropertiesDAO.getServerCertificatePath();
        final String serverKeyPath = certificatePropertiesDAO.getServerKeyPath();
        final String destServerConfFolder = System.getProperty("server.home") + File.separator + destWebServer + File.separator + "conf";
        final String destServerCertPath = destServerConfFolder + File.separator + "server.crt";
        final String destServerKeyPath = destServerConfFolder + File.separator + "server.key";
        FileUtils.copyFile(new File(serverCertFilePath), new File(destServerCertPath));
        FileUtils.copyFile(new File(serverKeyPath), new File(destServerKeyPath));
        if (destWebServer.equalsIgnoreCase("nginx")) {
            File temporaryCertFile = new File(WebServerUtil.getWebServerConfDir() + File.separator + "server-temp.crt");
            FileUtils.copyFile(new File(certificatePropertiesDAO.getServerCertificatePath()), temporaryCertFile);
            final File nginxServerCertificateFile = new File(destServerCertPath);
            temporaryCertFile = FileAccessImpl.appendMultipleFiles(temporaryCertFile.getAbsolutePath(), certificatePropertiesDAO.getRootCertificatePath());
            FileUtils.copyFile(temporaryCertFile, nginxServerCertificateFile);
            temporaryCertFile.delete();
        }
        this.logger.log(Level.INFO, "Copied certificates successfully to " + destWebServer);
    }
    
    public String getSelfSignedRootCertificatePath() throws Exception {
        final String serverHome = System.getProperty("server.home");
        final String webServerName = this.getWebServerName();
        return serverHome + File.separator + webServerName + File.separator + "conf" + File.separator + "DMRootCA.crt";
    }
    
    public String getSelfSignedRootKeyPath() throws Exception {
        final String serverHome = System.getProperty("server.home");
        final String webServerName = this.getWebServerName();
        return serverHome + File.separator + webServerName + File.separator + "conf" + File.separator + "DMRootCA.key";
    }
    
    static {
        SSLCertificateUtil.sslCertUtil = null;
    }
}
