package com.me.devicemanagement.onpremise.server.certificate;

import java.util.Hashtable;
import java.nio.file.StandardCopyOption;
import java.nio.file.CopyOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import com.me.devicemanagement.onpremise.start.util.WebServerUtil;
import com.me.devicemanagement.onpremise.server.settings.nat.NATHandler;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessAPI;
import com.me.devicemanagement.onpremise.start.util.CertificateGenerator;
import com.me.ems.onpremise.security.certificate.api.core.handlers.ServerCertificateValidationHandler;
import java.security.cert.Certificate;
import java.io.File;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.util.SecurityUtil;
import com.me.devicemanagement.framework.server.certificate.CertificateUtils;
import java.util.List;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import com.me.devicemanagement.onpremise.server.certificate.client.ClientCertificateGenerator;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class ServerSANCertificateGeneratorTask implements SchedulerExecutionInterface
{
    private static Logger logger;
    private static final String SYSTEM_HOME;
    private static final String ROOT_CERT = "DMRootCA.crt";
    private static final String ROOT_KEY = "DMRootCA.key";
    private static final String NGINX_CONF;
    private static final String APACHE_CONF;
    private static final String CLIENT_DATA;
    
    public void executeTask(final Properties props) {
        ServerSANCertificateGeneratorTask.logger.log(Level.INFO, "Inside the method executeTask() from ServerSANCertificateGeneratorTask Class");
        try {
            this.checkAndGenerateServerRootCertificates();
            this.checkAndGenerateServerLeafCertificates();
            if (!CustomerInfoUtil.getInstance().isMSP()) {
                this.generateDSRootCertificatesIfNotExist();
            }
        }
        catch (final Exception ex) {
            ServerSANCertificateGeneratorTask.logger.log(Level.SEVERE, "Exception in executeTask ServerSANCertificateGeneratorTask ", ex);
        }
        try {
            ClientCertificateGenerator.getInstance().generateClientRootCACertificateAndKey();
        }
        catch (final Exception e) {
            Logger.getLogger("AgentServerAuthLogger").log(Level.SEVERE, "From ServerSANCertificateGeneratorTask Exception in generating client certificate ", e);
        }
    }
    
    private void generateCertificateFiles(final X509Certificate rootCA, final PrivateKey privateKey, final String serverCrt, final String serverKey, final List serverInfo) throws Exception {
        ServerSANCertificateGeneratorTask.logger.log(Level.INFO, "Server Info From DB " + serverInfo);
        CertificateUtils.generateServerSANCertificateFromRoot(rootCA, privateKey, serverCrt, serverKey, SSLCertificateUtil.getInstance().commonName, serverInfo);
        ServerSANCertificateGeneratorTask.logger.log(Level.INFO, "Server Certificate generated successfully");
    }
    
    private void checkAndGenerateServerRootCertificates() {
        try {
            final SSLCertificateUtil certificateUtilInstance = SSLCertificateUtil.getInstance();
            if (certificateUtilInstance.isThirdPartySSLInstalled() || Boolean.parseBoolean(SecurityUtil.getSecurityParameter("SC_VALUE"))) {
                ServerSANCertificateGeneratorTask.logger.log(Level.INFO, "Skipping Root Cert regeneration since third party is installed or TC is Enabled");
                return;
            }
            final FileAccessAPI fileAccessAPI = ApiFactoryProvider.getFileAccessAPI();
            String rootCACrtDB = SecurityUtil.getAdvancedSecurityDetail("Server_DMRootCA.crt");
            String rootCAKeyDB = SecurityUtil.getAdvancedSecurityDetail("Server_DMRootCA.key");
            rootCACrtDB = ((rootCACrtDB != null && !rootCACrtDB.isEmpty()) ? ApiFactoryProvider.getCryptoAPI().decrypt(rootCACrtDB, (String)null, (String)null) : null);
            rootCAKeyDB = ((rootCAKeyDB != null && !rootCAKeyDB.isEmpty()) ? ApiFactoryProvider.getCryptoAPI().decrypt(rootCAKeyDB, (String)null, (String)null) : null);
            final String nginxRootCrt = ServerSANCertificateGeneratorTask.NGINX_CONF + "DMRootCA.crt";
            final String nginxRootKey = ServerSANCertificateGeneratorTask.NGINX_CONF + "DMRootCA.key";
            final String apacheRootCrt = ServerSANCertificateGeneratorTask.APACHE_CONF + "DMRootCA.crt";
            final String apacheRootKey = ServerSANCertificateGeneratorTask.APACHE_CONF + "DMRootCA.key";
            final String clientDataServerRootCA = ServerSANCertificateGeneratorTask.CLIENT_DATA + "server-certificates" + File.separator + "DMRootCA.crt";
            boolean isApacheModValid = false;
            boolean isNginxModValid = false;
            boolean addCertToDB = false;
            boolean regenerateServerRootCert = false;
            boolean isClientRootCertificateAvailableAndValid = false;
            X509Certificate clientDataRootCACertificate = null;
            if (fileAccessAPI.isFileExists(clientDataServerRootCA)) {
                try {
                    clientDataRootCACertificate = CertificateUtils.loadX509CertificateFromFile(new File(clientDataServerRootCA));
                    isClientRootCertificateAvailableAndValid = true;
                }
                catch (final Exception e) {
                    ServerSANCertificateGeneratorTask.logger.log(Level.SEVERE, "Exception while loading ClientData root crt. Exception : ", e);
                }
            }
            if (fileAccessAPI.isFileExists(nginxRootCrt) && fileAccessAPI.isFileExists(nginxRootKey)) {
                try {
                    isNginxModValid = CertificateUtils.isValidCertificateAndPrivateKey((Certificate)CertificateUtils.loadX509CertificateFromFile(new File(nginxRootCrt)), CertificateUtils.loadPrivateKeyFromFile(new File(nginxRootKey)));
                }
                catch (final Exception e) {
                    ServerSANCertificateGeneratorTask.logger.log(Level.SEVERE, "Exception while loading and verifying Nginx root crt and key. Exception : ", e);
                }
            }
            if (fileAccessAPI.isFileExists(apacheRootCrt) && fileAccessAPI.isFileExists(apacheRootKey)) {
                try {
                    isApacheModValid = CertificateUtils.isValidCertificateAndPrivateKey((Certificate)CertificateUtils.loadX509CertificateFromFile(new File(apacheRootCrt)), CertificateUtils.loadPrivateKeyFromFile(new File(apacheRootKey)));
                }
                catch (final Exception e) {
                    ServerSANCertificateGeneratorTask.logger.log(Level.SEVERE, "Exception while loading and verifying Apache root crt and key. Exception : ", e);
                }
            }
            if (isNginxModValid && isApacheModValid) {
                ServerSANCertificateGeneratorTask.logger.log(Level.INFO, "Modulus of Server Root certs in both webservers are valid");
                if (rootCACrtDB == null || rootCAKeyDB == null) {
                    addCertToDB = true;
                }
            }
            else if (isNginxModValid) {
                if (isClientRootCertificateAvailableAndValid && clientDataRootCACertificate != null) {
                    final X509Certificate rootCA = CertificateUtils.loadX509CertificateFromFile(new File(nginxRootCrt));
                    if (rootCA.equals(clientDataRootCACertificate)) {
                        ServerSANCertificateGeneratorTask.logger.log(Level.INFO, "Modulus of Server Root certs in Nginx is valid but Apache is invalid or missing");
                        fileAccessAPI.copyFile(nginxRootCrt, apacheRootCrt);
                        fileAccessAPI.copyFile(nginxRootKey, apacheRootKey);
                        if (rootCACrtDB == null || rootCAKeyDB == null) {
                            addCertToDB = true;
                        }
                    }
                    else {
                        regenerateServerRootCert = true;
                    }
                }
                else {
                    regenerateServerRootCert = true;
                }
            }
            else if (isApacheModValid) {
                if (isClientRootCertificateAvailableAndValid && clientDataRootCACertificate != null) {
                    final X509Certificate rootCA = CertificateUtils.loadX509CertificateFromFile(new File(apacheRootCrt));
                    if (rootCA.equals(clientDataRootCACertificate)) {
                        ServerSANCertificateGeneratorTask.logger.log(Level.INFO, "Modulus of Server Root certs in Apache is valid but Nginx is invalid or missing");
                        fileAccessAPI.copyFile(apacheRootCrt, nginxRootCrt);
                        fileAccessAPI.copyFile(apacheRootKey, nginxRootKey);
                        if (rootCACrtDB == null || rootCAKeyDB == null) {
                            addCertToDB = true;
                        }
                    }
                    else {
                        regenerateServerRootCert = true;
                    }
                }
                else {
                    regenerateServerRootCert = true;
                }
            }
            else {
                ServerSANCertificateGeneratorTask.logger.log(Level.INFO, "Modulus of Server Root certs in both webservers are invalid or missing");
                if (rootCACrtDB != null && !rootCACrtDB.isEmpty() && rootCAKeyDB != null && !rootCAKeyDB.isEmpty()) {
                    boolean canWriteRootCrtsFromDB = true;
                    ServerSANCertificateGeneratorTask.logger.log(Level.INFO, "----SERVER SSL KEYPAIR ALREADY PRESENT IN DB----");
                    if (isClientRootCertificateAvailableAndValid && clientDataRootCACertificate != null) {
                        String rootCACrtDBBuffer = rootCACrtDB.replace("-----BEGIN CERTIFICATE-----", "");
                        rootCACrtDBBuffer = rootCACrtDBBuffer.replace("-----END CERTIFICATE-----", "");
                        final X509Certificate rootCA2 = CertificateUtils.loadX509CertificateFromBuffer(rootCACrtDBBuffer);
                        if (rootCA2.equals(clientDataRootCACertificate)) {
                            ServerSANCertificateGeneratorTask.logger.log(Level.INFO, "Server Root Certificate in DB matches with Root Certificate found in Server Data Folder");
                        }
                        else {
                            ServerSANCertificateGeneratorTask.logger.log(Level.INFO, "Root CA from DB mismatches with Crt in Client-Data location, Unable to replace Root Crts from DB hence regenerating certificate");
                            canWriteRootCrtsFromDB = false;
                            regenerateServerRootCert = true;
                        }
                    }
                    else {
                        ServerSANCertificateGeneratorTask.logger.log(Level.INFO, "No Server Root Certificate found in Server Data Folder");
                    }
                    if (canWriteRootCrtsFromDB) {
                        fileAccessAPI.writeFile(apacheRootCrt, rootCACrtDB.getBytes());
                        fileAccessAPI.writeFile(apacheRootKey, rootCAKeyDB.getBytes());
                        fileAccessAPI.writeFile(nginxRootCrt, rootCACrtDB.getBytes());
                        fileAccessAPI.writeFile(nginxRootKey, rootCAKeyDB.getBytes());
                        ServerSANCertificateGeneratorTask.logger.log(Level.INFO, "Successfully written certs and keys in both webservers from DB");
                        SSLCertificateUtil.getInstance().copyCertsToClientDataDir();
                    }
                }
                else {
                    ServerSANCertificateGeneratorTask.logger.log(Level.INFO, "Server Root Certificate not found in database also, Need to generate new Root Certificate :( ");
                    regenerateServerRootCert = true;
                }
            }
            if (regenerateServerRootCert) {
                ServerSANCertificateGeneratorTask.logger.log(Level.SEVERE, "Root Cert Regeneration required");
                if (ServerCertificateValidationHandler.getInstance().canRegenerateCertificateValidator()) {
                    ServerSANCertificateGeneratorTask.logger.log(Level.INFO, "Generating Server Root Certificates as certificate listener passed");
                    CertificateGenerator.getInstance().generateCARootCertificate(nginxRootCrt, nginxRootKey, new String[] { "ManageEngineCA-Root", "ManageEngineCA-Root" });
                    SecurityUtil.updateSecurityParameter("REGEN_LEAF_CERT", "true");
                    addCertToDB = true;
                    ServerSANCertificateGeneratorTask.logger.log(Level.INFO, "Successfully regenerated Server Root Certificates");
                    fileAccessAPI.copyFile(nginxRootCrt, apacheRootCrt);
                    fileAccessAPI.copyFile(nginxRootKey, apacheRootKey);
                    SSLCertificateUtil.getInstance().copyCertsToClientDataDir();
                }
                else {
                    ServerSANCertificateGeneratorTask.logger.log(Level.SEVERE, "Server Root Certificate was not regenerated due to point product listeners");
                }
            }
            if (addCertToDB) {
                ServerSANCertificateGeneratorTask.logger.log(Level.INFO, "Adding Root Certs to Database");
                SecurityUtil.updateServerRootCertificateToDatabase(nginxRootCrt, nginxRootKey);
            }
        }
        catch (final Exception e2) {
            ServerSANCertificateGeneratorTask.logger.log(Level.SEVERE, "Exception in checkAndGenerateServerRootCertificates of ServerSANCertificateGenerationTask ", e2);
        }
    }
    
    private void checkAndGenerateServerLeafCertificates() {
        try {
            final CertificatePropertiesDAO certificatePropertiesDAO = CertificatePropertiesDAO.getInstance();
            final String serverCrt = certificatePropertiesDAO.getServerCertificatePath();
            final String serverKey = certificatePropertiesDAO.getServerKeyPath();
            final String rootCACert = certificatePropertiesDAO.getRootCertificatePath();
            final String rootCAKey = certificatePropertiesDAO.getRootKeyPath();
            final X509Certificate rootCA = CertificateUtils.loadX509CertificateFromFile(new File(rootCACert));
            final PrivateKey privateKey = CertificateUtils.loadPrivateKeyFromFile(new File(rootCAKey));
            if (CertificateUtils.isValidCertificateAndPrivateKey((Certificate)rootCA, privateKey)) {
                final List<String> serverInfo = SSLCertificateUtil.getInstance().getServerInfoFromDB();
                final String natAddress = ((Hashtable<K, String>)NATHandler.getNATConfigurationProperties()).get("NAT_ADDRESS");
                if (natAddress != null && !natAddress.isEmpty() && serverInfo != null) {
                    ServerSANCertificateGeneratorTask.logger.log(Level.INFO, "NAT Address Configured Already . NAT Address From DB : " + natAddress);
                    serverInfo.add(natAddress);
                }
                else {
                    ServerSANCertificateGeneratorTask.logger.log(Level.INFO, "NAT Address Not Yet Configured .");
                }
                ServerSANCertificateGeneratorTask.logger.log(Level.INFO, "Server Info From DB " + serverInfo);
                ServerSANCertificateGeneratorTask.logger.log(Level.INFO, "Server Certificate File path " + serverCrt);
                ServerSANCertificateGeneratorTask.logger.log(Level.INFO, "Server Key File Path " + serverKey);
                ServerSANCertificateGeneratorTask.logger.log(Level.INFO, "Root certificate file path " + rootCACert);
                ServerSANCertificateGeneratorTask.logger.log(Level.INFO, "Root Key File path " + rootCAKey);
                final File serverCrtFile = new File(serverCrt);
                final File serverKeyFile = new File(serverKey);
                if (serverCrtFile.exists() && serverKeyFile.exists()) {
                    ServerSANCertificateGeneratorTask.logger.log(Level.INFO, "Server Certificate File Exists");
                    if (SSLCertificateUtil.getInstance().isCertificateRegenerationAllowed()) {
                        ServerSANCertificateGeneratorTask.logger.log(Level.INFO, "Certificate Regeneration allowed ");
                        if (SSLCertificateUtil.getInstance().isCertRegenerationRequired(serverInfo)) {
                            this.generateCertificateFiles(rootCA, privateKey, serverCrt, serverKey, serverInfo);
                            SecurityUtil.updateSecurityParameter("REGEN_LEAF_CERT", "false");
                            SSLCertificateUtil.getInstance().doPostCertificateGeneration();
                        }
                    }
                }
                else {
                    ServerSANCertificateGeneratorTask.logger.log(Level.INFO, "Server Certificate File / Server Certificate Key does not exists already. Generating the server certificate file");
                    this.generateCertificateFiles(rootCA, privateKey, serverCrt, serverKey, serverInfo);
                    SecurityUtil.updateSecurityParameter("REGEN_LEAF_CERT", "false");
                    SSLCertificateUtil.getInstance().doPostCertificateGeneration();
                }
            }
            else {
                ServerSANCertificateGeneratorTask.logger.log(Level.SEVERE, "Cannot regenerate the Server certificate as the RootCA and its private key doesn't match.");
            }
        }
        catch (final Exception ex) {
            ServerSANCertificateGeneratorTask.logger.log(Level.SEVERE, "Exception while generating Leaf Certs in Server SAN Generation Task..", ex);
        }
    }
    
    private void generateDSRootCertificatesIfNotExist() {
        try {
            final String directoryToCreateRootCertificate = WebServerUtil.getServerHomeCanonicalPath() + File.separator + "webapps" + File.separator + "DesktopCentral" + File.separator + "server-data" + File.separator + "1" + File.separator + "server-certificates";
            final String rootCertPath = directoryToCreateRootCertificate + File.separator + "DMRootCA.crt";
            final String rootKeyPath = directoryToCreateRootCertificate + File.separator + "DMRootCA.key";
            if (Files.notExists(Paths.get(directoryToCreateRootCertificate, new String[0]), new LinkOption[0])) {
                Files.createDirectories(Paths.get(directoryToCreateRootCertificate, new String[0]), (FileAttribute<?>[])new FileAttribute[0]);
            }
            if (Files.notExists(Paths.get(rootCertPath, new String[0]), new LinkOption[0]) && Files.notExists(Paths.get(rootKeyPath, new String[0]), new LinkOption[0])) {
                CertificateGenerator.getInstance().generateCARootCertificate(rootCertPath, rootKeyPath, new String[] { "ManageEngineCA-DS-CA", "ManageEngine-DS-CA" });
            }
            final String clientDataRoot = ServerSANCertificateGeneratorTask.CLIENT_DATA + "1" + File.separator + "server-certificates";
            if (Files.notExists(Paths.get(clientDataRoot, new String[0]), new LinkOption[0])) {
                Files.createDirectories(Paths.get(clientDataRoot, new String[0]), (FileAttribute<?>[])new FileAttribute[0]);
            }
            if (Files.exists(Paths.get(rootCertPath, new String[0]), new LinkOption[0]) && Files.notExists(Paths.get(new File(clientDataRoot + File.separator + "DMRootCA.crt").getAbsolutePath(), new String[0]), new LinkOption[0])) {
                Files.copy(Paths.get(rootCertPath, new String[0]), Paths.get(clientDataRoot + File.separator + "DMRootCA.crt", new String[0]), StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (final Exception ex) {
            ServerSANCertificateGeneratorTask.logger.log(Level.SEVERE, "Exception while generating DS Root Certificates in ServerSANCertificateGenerationTask", ex);
        }
    }
    
    static {
        ServerSANCertificateGeneratorTask.logger = Logger.getLogger("ImportCertificateLogger");
        SYSTEM_HOME = System.getProperty("server.home");
        NGINX_CONF = ServerSANCertificateGeneratorTask.SYSTEM_HOME + File.separator + "nginx" + File.separator + "conf" + File.separator;
        APACHE_CONF = ServerSANCertificateGeneratorTask.SYSTEM_HOME + File.separator + "apache" + File.separator + "conf" + File.separator;
        CLIENT_DATA = ServerSANCertificateGeneratorTask.SYSTEM_HOME + File.separator + "webapps" + File.separator + "DesktopCentral" + File.separator + "client-data" + File.separator;
    }
}
