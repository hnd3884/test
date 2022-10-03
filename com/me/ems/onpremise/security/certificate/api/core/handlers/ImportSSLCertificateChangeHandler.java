package com.me.ems.onpremise.security.certificate.api.core.handlers;

import java.io.IOException;
import java.io.FileNotFoundException;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessAPI;
import com.me.devicemanagement.framework.webclient.admin.certificate.CertificateUtil;
import java.util.ArrayList;
import java.util.Iterator;
import sun.misc.BASE64Encoder;
import java.util.List;
import java.io.Writer;
import org.bouncycastle.openssl.PEMWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.ByteArrayOutputStream;
import java.security.PrivateKey;
import org.apache.commons.io.FileUtils;
import java.security.cert.Certificate;
import java.util.Arrays;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.json.JSONObject;
import java.util.logging.Level;
import java.security.cert.X509Certificate;
import com.me.ems.onpremise.security.certificate.api.core.utils.SSLCertificateUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.ems.onpremise.security.certificate.api.Exception.CertificateAPIException;
import com.me.ems.onpremise.security.certificate.api.core.events.ImportSSLCertificateChangeEvent;
import com.me.devicemanagement.onpremise.start.util.WebServerUtil;
import java.io.File;
import java.util.Properties;
import com.me.ems.onpremise.security.certificate.api.model.CertificateChainObject;
import java.util.logging.Logger;

public class ImportSSLCertificateChangeHandler
{
    Logger logger;
    String serverHome;
    String webServerConfFolder;
    String certificateBackUpPath;
    String certificateBackUpFolder;
    CertificateChainObject certificateResponseObject;
    Properties userConfigProperties;
    
    public ImportSSLCertificateChangeHandler(final CertificateChainObject certificateResponseObject, final Properties userConfigProperties) throws Exception {
        this.logger = Logger.getLogger("ImportCertificateLogger");
        this.serverHome = System.getProperty("server.home");
        this.webServerConfFolder = this.serverHome + File.separator + WebServerUtil.getWebServerName() + File.separator + "conf" + File.separator;
        this.certificateBackUpPath = this.serverHome + File.separator + "webapps" + File.separator + "DesktopCentral" + File.separator + "server-data" + File.separator + "certificate_backup" + File.separator;
        this.certificateBackUpFolder = null;
        this.certificateResponseObject = null;
        this.userConfigProperties = null;
        this.certificateResponseObject = certificateResponseObject;
        this.userConfigProperties = userConfigProperties;
    }
    
    public void processRequest() throws CertificateAPIException {
        Boolean isBackupSuccessful = Boolean.FALSE;
        Boolean isFileMoveSuccessful = Boolean.FALSE;
        Boolean isConfFileUpdationSuccessful = Boolean.FALSE;
        final String uploadedCertificateFilePath = this.userConfigProperties.getProperty("uploadedCertificateFilePath");
        final String uploadFolder = uploadedCertificateFilePath.substring(0, uploadedCertificateFilePath.lastIndexOf(File.separator) + 1);
        final ImportSSLCertificateChangeEvent event = this.certificateResponseObject.getIsSelfSignedCA() ? new ImportSSLCertificateChangeEvent(3, this.certificateResponseObject) : new ImportSSLCertificateChangeEvent(2, this.certificateResponseObject);
        try {
            final Boolean allowCertificateUpload = this.allowEnterpriseCASignedCertificateUpload();
            final JSONObject remarkJSON = ImportSSLCertificateHandler.getInstance().canUploadCertificateListener(event);
            if (remarkJSON != null && !remarkJSON.optBoolean("status")) {
                final int errorCode = remarkJSON.optInt("errorCode");
                if (errorCode == 80027 && !allowCertificateUpload) {
                    throw new CertificateAPIException("CERT039");
                }
                if (errorCode == 80029) {
                    throw new CertificateAPIException("CERT038");
                }
                if (errorCode == 80039) {
                    throw new CertificateAPIException("CERT040");
                }
                if (errorCode == 80040) {
                    throw new CertificateAPIException("CERT041");
                }
            }
            this.checkWritePermissionForFolders();
            if (allowCertificateUpload || (remarkJSON != null && remarkJSON.optBoolean("status"))) {
                this.writeFilesToDestination(uploadFolder, this.certificateResponseObject);
                isBackupSuccessful = this.backUpServerCertificateAndDetails();
                if (!isBackupSuccessful) {
                    throw new CertificateAPIException("CERT035", "Backup Failed - Failed to create backup of existing certificate");
                }
                isFileMoveSuccessful = this.moveFilesToWebServerFolder(uploadFolder);
                if (!isFileMoveSuccessful) {
                    throw new CertificateAPIException("CERT036", "Writing to webserver/conf folder failed - Failed while moving files from upload folder to webserver/conf folder");
                }
                isConfFileUpdationSuccessful = this.updateConfFiles();
                if (!isConfFileUpdationSuccessful) {
                    throw new CertificateAPIException("CERT037", "Websettings.conf file Updation Failed");
                }
                SyMUtil.updateSyMParameter("IsEnterpriseCASignedServerCert", this.certificateResponseObject.getIsSelfSignedCA().toString());
                SyMUtil.updateSyMParameter("server_certificate_thumbprint", SSLCertificateUtil.getInstance().getThumbPrint((X509Certificate)this.certificateResponseObject.getServerCertificate()));
                if (this.certificateResponseObject.getRootCACertificate() != null) {
                    SyMUtil.updateSyMParameter("root_certificate_thumbprint", SSLCertificateUtil.getInstance().getThumbPrint((X509Certificate)this.certificateResponseObject.getRootCACertificate()));
                }
                ImportSSLCertificateHandler.getInstance().invokeImportSSLCertificateChangeListeners();
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in import certificate ", ex);
            Label_0473: {
                if (isBackupSuccessful) {
                    if (isFileMoveSuccessful) {
                        if (isConfFileUpdationSuccessful) {
                            break Label_0473;
                        }
                    }
                    try {
                        this.revertAllChanges();
                    }
                    catch (final Exception exp) {
                        this.logger.log(Level.SEVERE, "Fatal error: Exception while reverting changes", exp);
                    }
                }
            }
            if (ex instanceof CertificateAPIException) {
                throw (CertificateAPIException)ex;
            }
            throw new CertificateAPIException("CERT001", "Error in SSLCertificateChangeHandler", ex);
        }
        finally {
            try {
                this.cleanAllTempFolders();
            }
            catch (final Exception ex2) {
                this.logger.log(Level.SEVERE, "Exception in deleting backup folders", ex2);
            }
        }
    }
    
    private Boolean allowEnterpriseCASignedCertificateUpload() {
        if (this.certificateResponseObject.getIsSelfSignedCA()) {
            return Boolean.valueOf(this.userConfigProperties.getProperty("confirmedSelfSignedCA", Boolean.FALSE.toString()));
        }
        return true;
    }
    
    private void checkWritePermissionForFolders() throws Exception {
        try {
            this.checkPermissionForFolderOrParentRecursively(this.webServerConfFolder);
        }
        catch (final Exception ex) {
            throw new CertificateAPIException("CERT023", "Write permission not given for the following folder ", ex, new String[] { new File(this.webServerConfFolder).getCanonicalPath() });
        }
        try {
            this.checkPermissionForFolderOrParentRecursively(this.certificateBackUpPath);
        }
        catch (final Exception ex) {
            throw new CertificateAPIException("CERT023", "Write permission not given for the following folder ", ex, new String[] { new File(this.certificateBackUpPath).getCanonicalPath() });
        }
    }
    
    private void checkPermissionForFolderOrParentRecursively(String folderPath) throws Exception {
        while (!ApiFactoryProvider.getFileAccessAPI().isDirectory(folderPath)) {
            folderPath = ApiFactoryProvider.getFileAccessAPI().getParent(folderPath);
        }
        if (!ApiFactoryProvider.getFileAccessAPI().createNewFile(folderPath + File.separator + "dummy-file")) {
            throw new Exception("ServerSSLCertificateChangeHandler.checkPermissionForFolderOrParentRecursively - Permission denied for the folder " + folderPath);
        }
        ApiFactoryProvider.getFileAccessAPI().deleteFile(folderPath + File.separator + "dummy-file");
    }
    
    private void writeFilesToDestination(final String destinationPath, final CertificateChainObject certificateResponse) throws Exception {
        this.writePrivateKeyToFile(destinationPath + File.separator + "server.key", certificateResponse.getServerKey());
        final StringBuilder allCertsValue = new StringBuilder();
        String allCertsFromServerCert = this.getBase64EncodedValueFromCert(certificateResponse.getServerCertificate());
        final int indexOfEndCertificate = allCertsFromServerCert.indexOf("-----END CERTIFICATE-----");
        allCertsFromServerCert = allCertsFromServerCert.substring(0, indexOfEndCertificate + "-----END CERTIFICATE-----".length());
        allCertsValue.append(allCertsFromServerCert + "\n");
        final Certificate[] intermediateCertificate;
        final Certificate[] intermediateCerts = intermediateCertificate = certificateResponse.getIntermediateCertificate();
        for (final Certificate certificate : intermediateCertificate) {
            allCertsValue.append(this.getBase64EncodedValueFromCert(certificate));
        }
        this.writeCertificateListToFile(destinationPath + File.separator + "intermediate.crt", Arrays.asList(certificateResponse.getIntermediateCertificate()));
        if (certificateResponse.getRootCACertificate() != null) {
            allCertsValue.append(this.getBase64EncodedValueFromCert(certificateResponse.getRootCACertificate()));
            this.writeCertificateListToFile(destinationPath + File.separator + "caCert.crt", Arrays.asList(certificateResponse.getRootCACertificate()));
        }
        FileUtils.writeStringToFile(new File(destinationPath + File.separator + "server.crt"), allCertsValue.toString(), "UTF-8");
    }
    
    private void writePrivateKeyToFile(final String privateKeyPath, final PrivateKey serverKey) throws Exception {
        final ByteArrayOutputStream pemArrayOutput = new ByteArrayOutputStream();
        final PEMWriter writer = new PEMWriter((Writer)new OutputStreamWriter(pemArrayOutput));
        writer.writeObject((Object)serverKey);
        writer.flush();
        writer.close();
        ApiFactoryProvider.getFileAccessAPI().writeFile(privateKeyPath, pemArrayOutput.toByteArray());
        pemArrayOutput.close();
    }
    
    private void writeCertificateListToFile(final String certificatePath, final List<Certificate> certificateList) throws Exception {
        final ByteArrayOutputStream certificateStream = new ByteArrayOutputStream();
        final BASE64Encoder b64Encoder = new BASE64Encoder();
        for (final Certificate currentCertificate : certificateList) {
            certificateStream.write(("-----BEGIN CERTIFICATE-----" + System.getProperty("line.separator")).getBytes());
            final String certCode = b64Encoder.encode(currentCertificate.getEncoded()).trim();
            certificateStream.write(certCode.getBytes());
            certificateStream.write((System.getProperty("line.separator") + "-----END CERTIFICATE-----" + System.getProperty("line.separator")).getBytes());
        }
        certificateStream.flush();
        certificateStream.close();
        final byte[] intermediateCertByteArray = certificateStream.toByteArray();
        certificateStream.close();
        ApiFactoryProvider.getFileAccessAPI().writeFile(certificatePath, intermediateCertByteArray);
    }
    
    private String getBase64EncodedValueFromCert(final Certificate certificate) throws Exception {
        final ByteArrayOutputStream certificateStream = new ByteArrayOutputStream();
        final BASE64Encoder b64Encoder = new BASE64Encoder();
        certificateStream.write(("-----BEGIN CERTIFICATE-----" + System.getProperty("line.separator")).getBytes());
        final String certCode = b64Encoder.encode(certificate.getEncoded()).trim();
        certificateStream.write(certCode.getBytes());
        certificateStream.write((System.getProperty("line.separator") + "-----END CERTIFICATE-----" + System.getProperty("line.separator")).getBytes());
        certificateStream.flush();
        certificateStream.close();
        final byte[] intermediateCertByteArray = certificateStream.toByteArray();
        certificateStream.close();
        return new String(intermediateCertByteArray);
    }
    
    private Boolean backUpServerCertificateAndDetails() throws Exception {
        final String sourceMethod = "backUpServerCertificateAndDetails";
        final Properties webServerProps = WebServerUtil.getWebServerSettings();
        final ArrayList<String> filesToBackUp = new ArrayList<String>();
        String serverCrtFilePath = webServerProps.getProperty("server.crt.loc");
        if (null == serverCrtFilePath || "".equals(serverCrtFilePath)) {
            serverCrtFilePath = webServerProps.getProperty("apache.crt.loc");
        }
        String serverKeyFilePath = webServerProps.getProperty("server.key.loc");
        if (null == serverKeyFilePath || "".equals(serverKeyFilePath)) {
            serverKeyFilePath = webServerProps.getProperty("apache.serverKey.loc");
        }
        filesToBackUp.add(this.webServerConfFolder + serverCrtFilePath);
        filesToBackUp.add(this.webServerConfFolder + serverKeyFilePath);
        if (!webServerProps.getProperty("apache.ssl.intermediate.ca.file", "").isEmpty()) {
            if (ApiFactoryProvider.getFileAccessAPI().isFileExists(this.webServerConfFolder + webServerProps.getProperty("apache.ssl.intermediate.ca.file"))) {
                filesToBackUp.add(this.webServerConfFolder + webServerProps.getProperty("apache.ssl.intermediate.ca.file"));
            }
            else {
                this.logger.log(Level.SEVERE, sourceMethod + "  the file " + this.webServerConfFolder + webServerProps.getProperty("apache.ssl.intermediate.ca.file") + " is not found...");
            }
        }
        if (!webServerProps.getProperty("apache.ssl.root.ca.file", "").isEmpty() && !webServerProps.getProperty("apache.ssl.intermediate.ca.file").equalsIgnoreCase(webServerProps.getProperty("apache.ssl.root.ca.file"))) {
            if (ApiFactoryProvider.getFileAccessAPI().isFileExists(this.webServerConfFolder + webServerProps.getProperty("apache.ssl.root.ca.file"))) {
                filesToBackUp.add(this.webServerConfFolder + webServerProps.getProperty("apache.ssl.root.ca.file"));
            }
            else {
                this.logger.log(Level.SEVERE, sourceMethod + "  the file " + this.webServerConfFolder + webServerProps.getProperty("apache.ssl.root.ca.file") + " is not found...");
            }
        }
        final String websettingsFile = this.serverHome + File.separator + "conf" + File.separator + "websettings.conf";
        filesToBackUp.add(websettingsFile);
        this.certificateBackUpFolder = this.certificateBackUpPath + "freshcertificate";
        final FileAccessAPI fileAccessApi = ApiFactoryProvider.getFileAccessAPI();
        if (fileAccessApi.isFileExists(this.certificateBackUpFolder)) {
            this.certificateBackUpFolder = CertificateUtil.getInstance().getSubDirectory(this.certificateBackUpPath);
        }
        if (!fileAccessApi.isFileExists(this.certificateBackUpFolder)) {
            fileAccessApi.createDirectory(this.certificateBackUpFolder);
        }
        final Boolean isSuccess = CertificateUtil.getInstance().copyFileListToDestinationFolder((List)filesToBackUp, this.certificateBackUpFolder);
        return isSuccess;
    }
    
    private Boolean moveFilesToWebServerFolder(final String uploadFolder) {
        this.logger.log(Level.INFO, "Entered method moveFilesToWebServerFolder with arg :" + uploadFolder);
        final List<String> newCertificateFilesToBeCopied = new ArrayList<String>();
        newCertificateFilesToBeCopied.add(uploadFolder + File.separator + "server.key");
        newCertificateFilesToBeCopied.add(uploadFolder + File.separator + "server.crt");
        if (this.certificateResponseObject.getIntermediateCertificate() != null) {
            newCertificateFilesToBeCopied.add(uploadFolder + File.separator + "intermediate.crt");
        }
        if (this.certificateResponseObject.getRootCACertificate() != null) {
            newCertificateFilesToBeCopied.add(uploadFolder + File.separator + "caCert.crt");
        }
        final String apacheConfFolder = this.serverHome + File.separator + "apache" + File.separator + "conf";
        final String nginxConfFolder = this.serverHome + File.separator + "nginx" + File.separator + "conf";
        boolean apacheCertCopoied = true;
        boolean nginxCertCopied = true;
        this.logger.log(Level.INFO, "newCertificateFilesToBeCopied" + newCertificateFilesToBeCopied);
        final boolean apacheConfExists = new File(apacheConfFolder).exists();
        final boolean nginxConfExists = new File(nginxConfFolder).exists();
        this.logger.log(Level.INFO, "is File '" + apacheConfFolder + "' exists :" + apacheConfExists);
        if (apacheConfExists) {
            apacheCertCopoied = CertificateUtil.getInstance().copyFileListToDestinationFolder((List)newCertificateFilesToBeCopied, apacheConfFolder);
        }
        this.logger.log(Level.INFO, "is File '" + nginxConfFolder + "' exists :" + nginxConfExists);
        if (nginxConfExists) {
            nginxCertCopied = CertificateUtil.getInstance().copyFileListToDestinationFolder((List)newCertificateFilesToBeCopied, nginxConfFolder);
        }
        return apacheCertCopoied && nginxCertCopied;
    }
    
    private Boolean updateConfFiles() throws Exception {
        final Boolean isSuccess = Boolean.TRUE;
        final Properties websettingsProps = new Properties();
        if (websettingsProps.containsKey("server.crt.loc")) {
            websettingsProps.setProperty("server.crt.loc", "server.crt");
        }
        else {
            websettingsProps.setProperty("apache.crt.loc", "server.crt");
        }
        if (websettingsProps.containsKey("server.key.loc")) {
            websettingsProps.setProperty("server.key.loc", "server.key");
        }
        else {
            websettingsProps.setProperty("apache.serverKey.loc", "server.key");
        }
        String rootCAFileName = "";
        if (this.certificateResponseObject.getRootCACertificate() != null) {
            rootCAFileName = "caCert.crt";
        }
        websettingsProps.setProperty("apache.ssl.root.ca.file", rootCAFileName);
        if (this.certificateResponseObject.getIntermediateCertificate() != null) {
            websettingsProps.setProperty("apache.ssl.intermediate.ca.file", "intermediate.crt");
        }
        WebServerUtil.storeProperWebServerSettings(websettingsProps);
        return isSuccess;
    }
    
    private Boolean revertAllChanges() throws FileNotFoundException, IOException, Exception {
        Boolean isSuccess = Boolean.FALSE;
        try {
            Properties oldWebServerSettings = new Properties();
            final String backupWebSettingsPath = this.certificateBackUpFolder + File.separator + "websettings.conf";
            oldWebServerSettings = WebServerUtil.getWebServerSettings(Boolean.TRUE, backupWebSettingsPath);
            final List<String> filesToBeRestoredFromBackUp = new ArrayList<String>();
            String oldServerCrtFilepath = "";
            String oldServerKeyFilepath = "";
            if (oldWebServerSettings.containsKey("server.crt.loc")) {
                oldServerCrtFilepath = oldWebServerSettings.getProperty("server.crt.loc");
            }
            else {
                oldServerCrtFilepath = oldWebServerSettings.getProperty("apache.crt.loc");
            }
            if (oldWebServerSettings.containsKey("server.key.loc")) {
                oldServerKeyFilepath = oldWebServerSettings.getProperty("server.key.loc");
            }
            else {
                oldServerKeyFilepath = oldWebServerSettings.getProperty("apache.serverKey.loc");
            }
            filesToBeRestoredFromBackUp.add(this.certificateBackUpFolder + File.separator + oldServerCrtFilepath);
            filesToBeRestoredFromBackUp.add(this.certificateBackUpFolder + File.separator + oldServerKeyFilepath);
            if (!oldWebServerSettings.getProperty("apache.ssl.intermediate.ca.file", "").isEmpty()) {
                filesToBeRestoredFromBackUp.add(this.certificateBackUpFolder + File.separator + oldWebServerSettings.getProperty("apache.ssl.intermediate.ca.file"));
            }
            if (!oldWebServerSettings.getProperty("apache.ssl.root.ca.file", "").isEmpty()) {
                filesToBeRestoredFromBackUp.add(this.certificateBackUpFolder + File.separator + oldWebServerSettings.getProperty("apache.ssl.root.ca.file"));
            }
            isSuccess = CertificateUtil.getInstance().copyFileListToDestinationFolder((List)filesToBeRestoredFromBackUp, this.webServerConfFolder);
            if (!isSuccess) {
                return isSuccess;
            }
            filesToBeRestoredFromBackUp.clear();
            filesToBeRestoredFromBackUp.add(backupWebSettingsPath);
            isSuccess = CertificateUtil.getInstance().copyFileListToDestinationFolder((List)filesToBeRestoredFromBackUp, this.serverHome + File.separator + "conf");
        }
        catch (final Exception ex) {
            throw ex;
        }
        return isSuccess;
    }
    
    protected void cleanAllTempFolders() throws Exception {
        if (this.certificateResponseObject.getTempFoldersListToDeleteFinally() != null) {
            final FileAccessAPI fileAccessApi = ApiFactoryProvider.getFileAccessAPI();
            for (final String folderPath : this.certificateResponseObject.getTempFoldersListToDeleteFinally()) {
                fileAccessApi.deleteDirectory(folderPath);
            }
        }
    }
}
