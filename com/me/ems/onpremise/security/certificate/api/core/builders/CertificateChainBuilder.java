package com.me.ems.onpremise.security.certificate.api.core.builders;

import java.util.Hashtable;
import java.security.interfaces.RSAPublicKey;
import java.util.concurrent.TimeUnit;
import java.util.Date;
import com.me.devicemanagement.framework.server.certificate.verifier.CertificateVerificationException;
import com.adventnet.i18n.I18N;
import java.util.Set;
import com.me.devicemanagement.framework.server.certificate.verifier.CertificateVerifier;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Collection;
import java.util.Arrays;
import com.me.ems.onpremise.security.certificate.api.constants.CertificateChainBuilderConstants;
import com.me.devicemanagement.framework.server.util.SecurityUtil;
import com.me.devicemanagement.framework.server.certificate.SSLCertificateUtil;
import java.security.cert.CertificateException;
import java.io.IOException;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadStatus;
import com.me.ems.onpremise.security.certificate.api.core.utils.CertificateUtils;
import com.me.devicemanagement.onpremise.webclient.admin.certificate.CertificateUtil;
import com.me.devicemanagement.onpremise.webclient.admin.certificate.IntermediateManager;
import java.io.File;
import com.me.devicemanagement.onpremise.webclient.admin.certificate.CertificateAttributeManager;
import java.security.cert.X509Certificate;
import java.security.UnrecoverableKeyException;
import java.util.List;
import java.util.ArrayList;
import com.me.ems.framework.common.api.utils.APIException;
import java.security.cert.Certificate;
import java.security.PrivateKey;
import com.me.ems.onpremise.security.certificate.api.Exception.PromptException;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.util.logging.Level;
import com.me.ems.onpremise.security.certificate.api.Exception.CertificateAPIException;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.ems.onpremise.security.certificate.api.model.CertificateChainObject;
import java.util.Properties;
import java.nio.file.Path;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessAPI;
import java.util.logging.Logger;

public abstract class CertificateChainBuilder
{
    Logger logger;
    FileAccessAPI fileAccessApi;
    Path uploadedCertificateFilePath;
    String certIssuedDate;
    String certIssuedDate2020;
    String certificateBackUpFolder;
    Properties userConfigProperties;
    CertificateChainObject certificateResponseObject;
    
    public CertificateChainBuilder(final Path uploadedCertificateFilePath, final Properties userConfigProperties) {
        this.logger = Logger.getLogger("ImportCertificateLogger");
        this.fileAccessApi = null;
        this.uploadedCertificateFilePath = null;
        this.certIssuedDate = "07/01/19";
        this.certIssuedDate2020 = "09/01/2020";
        this.certificateBackUpFolder = null;
        this.userConfigProperties = null;
        this.certificateResponseObject = null;
        this.fileAccessApi = ApiFactoryProvider.getFileAccessAPI();
        this.uploadedCertificateFilePath = uploadedCertificateFilePath;
        this.userConfigProperties = userConfigProperties;
    }
    
    public CertificateChainObject processRequest() throws CertificateAPIException, PromptException {
        final Boolean isBackupSuccessful = Boolean.FALSE;
        final Boolean isFileMoveSuccessful = Boolean.FALSE;
        final Boolean isConfFileUpdationSuccessful = Boolean.FALSE;
        try {
            this.validateUploadedFiles();
            final Boolean isInitialized = this.initialize();
            if (!isInitialized) {
                throw new CertificateAPIException("CERT002", "Initialize Failed");
            }
            this.processCertificate();
            this.validateCertificates();
        }
        catch (final CertificateAPIException certificateAPIException) {
            throw certificateAPIException;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in import certificate ", ex);
            if (ex instanceof CertificateNotYetValidException || ex instanceof CertificateExpiredException) {
                throw new CertificateAPIException("CERT003", "Certificate Expired", ex);
            }
            if (ex instanceof NoSuchAlgorithmException || ex instanceof NoSuchProviderException) {
                throw new CertificateAPIException("CERT004", "Certificate Algorithm Not supported", ex, new String[] { this.userConfigProperties.getProperty("productDisplayName") });
            }
            try {
                this.cleanAllTempFolders();
            }
            catch (final Exception exp) {
                throw new CertificateAPIException("CERT001", ex);
            }
            if (ex instanceof CertificateAPIException) {
                throw (CertificateAPIException)ex;
            }
            if (ex instanceof PromptException) {
                throw (PromptException)ex;
            }
            throw new CertificateAPIException("CERT001", ex);
        }
        return this.certificateResponseObject;
    }
    
    protected void validateUploadedFiles() throws Exception {
        if (this.uploadedCertificateFilePath == null) {
            throw new CertificateAPIException("CERT034", "UploadedCertificateFilePath is null");
        }
    }
    
    protected abstract PrivateKey obtainPrivateKey() throws Exception;
    
    protected abstract Certificate obtainServerCertificate() throws Exception;
    
    protected abstract Certificate[] obtainCertificateChain() throws Exception;
    
    protected abstract Certificate obtainRootCACertificate() throws Exception;
    
    protected Boolean initialize() throws APIException {
        return Boolean.TRUE;
    }
    
    protected void processCertificate() throws Exception {
        (this.certificateResponseObject = new CertificateChainObject()).setTempFoldersListToDeleteFinally(new ArrayList<String>());
        try {
            this.certificateResponseObject.setServerKey(this.obtainPrivateKey());
        }
        catch (final UnrecoverableKeyException e) {
            throw new CertificateAPIException("CERT025", "Keystore password does not match with the private key password", e);
        }
        this.certificateResponseObject.setServerCertificate(this.obtainServerCertificate());
        this.certificateResponseObject.setIntermediateCertificate(this.obtainCertificateChain());
        this.certificateResponseObject.setRootCACertificate(this.obtainRootCACertificate());
        this.certificateResponseObject.setIsSelfSignedCA(this.verifyIfSelfSignedCA());
    }
    
    protected List<Certificate> autoDownloadIntermediateCerts(final String baseDownloadFolder) throws IOException, CertificateNotYetValidException, CertificateExpiredException, CertificateException, Exception {
        final List<Certificate> certificateChainList = new ArrayList<Certificate>();
        X509Certificate tempCertObject = (X509Certificate)this.obtainServerCertificate();
        final int i = 0;
        while (!CertificateAttributeManager.getInstance().isCertificateChainEnd(tempCertObject)) {
            final String downloadPath = baseDownloadFolder + File.separator + "intemediate" + (i + 1) + ".crt";
            DownloadStatus downloadStatus = null;
            Boolean downloadSuccess = Boolean.FALSE;
            final HashMap resultMap = IntermediateManager.getInstance().downloadIntermediateFromServerCertificate(tempCertObject, downloadPath);
            final String serverCertificateFile = this.userConfigProperties.getProperty("uploadedCertificateFilePath");
            final String certificateExtension = CertificateUtil.getInstance().getExtension(serverCertificateFile);
            downloadSuccess = resultMap.get("isSuccess");
            if (downloadSuccess) {
                tempCertObject = CertificateUtils.loadX509CertificateFromFile(new File(downloadPath));
                certificateChainList.add(tempCertObject);
            }
            else {
                downloadStatus = resultMap.get("downloadStatus");
                final String downloadLink = String.valueOf(resultMap.get("downloadLink"));
                if (resultMap.containsKey("ldap")) {
                    Logger.getLogger("ImportCertificateLogger").log(Level.INFO, "LDAP ERROR FOUND");
                    throw new CertificateAPIException("CERT028", true, downloadLink);
                }
                if (downloadStatus.getErrorMessage().contains("Unsupported protocol")) {
                    if (certificateExtension.equals(".pfx") || certificateExtension.equals(".jks") || certificateExtension.equals(".keystore")) {
                        throw new CertificateAPIException("CERT006", true, downloadLink);
                    }
                    throw new CertificateAPIException("CERT005", true, downloadLink);
                }
                else if (downloadStatus.getStatus() == 503) {
                    if (certificateExtension.equals(".pfx") || certificateExtension.equals(".jks") || certificateExtension.equals(".keystore")) {
                        throw new CertificateAPIException("CERT018", true, downloadLink);
                    }
                    throw new CertificateAPIException("CERT019", true, downloadLink);
                }
                else {
                    if (!downloadStatus.getErrorMessage().contains("Bad Set-Cookie header")) {
                        throw new CertificateAPIException("CERT007", "Internet Connection Needed for AutoDownload", new String[] { this.userConfigProperties.getProperty("productDisplayName") });
                    }
                    if (downloadLink.startsWith("http://") || downloadLink.startsWith("https://")) {
                        throw new CertificateAPIException("CERT026", true, downloadLink);
                    }
                    throw new CertificateAPIException("CERT027", true, downloadLink);
                }
            }
        }
        return certificateChainList;
    }
    
    private Boolean checkNatSettingsValidWithCertificate() throws Exception {
        final Properties natProperties = ApiFactoryProvider.getServerSettingsAPI().getNATConfigurationProperties();
        String natAddress = null;
        Boolean natSettingsValid = Boolean.FALSE;
        if (natProperties != null && natProperties.get("NAT_ADDRESS") != null && !((Hashtable<K, String>)natProperties).get("NAT_ADDRESS").trim().equals("")) {
            natAddress = ((Hashtable<K, String>)natProperties).get("NAT_ADDRESS").trim();
        }
        if (natAddress != null) {
            natSettingsValid = SSLCertificateUtil.getInstance().checkHostNameValidWithCertificate(natAddress, (X509Certificate)this.certificateResponseObject.getServerCertificate());
        }
        return natSettingsValid;
    }
    
    protected void validateCertificates() throws Exception {
        final String certificateFileName = this.uploadedCertificateFilePath.toString().substring(this.uploadedCertificateFilePath.toString().lastIndexOf("/") + 1);
        if (this.certificateResponseObject.getServerCertificate() == null) {
            throw new CertificateAPIException("CERT008", "Certificate processed but ServerCertificateObject is null");
        }
        if (this.certificateResponseObject.getServerKey() == null) {
            throw new CertificateAPIException("CERT009", "Certificate processed but ServerKeyObject is null");
        }
        if (CertificateUtils.isCertificateSelfSigned((X509Certificate)this.certificateResponseObject.getServerCertificate())) {
            throw new CertificateAPIException("CERT024", "Self signed certificate cannot be imported. Please import an enterprise or a third party certificate.");
        }
        if (this.certificateResponseObject.getIntermediateCertificate() == null || this.certificateResponseObject.getIntermediateCertificate().length == 0) {
            throw new CertificateAPIException("CERT010", "Certificate processed but intermediate chain is null");
        }
        if (!CertificateUtils.isCertificateSelfSigned((X509Certificate)this.certificateResponseObject.getServerCertificate())) {
            this.verifyCertificatePath();
        }
        if (this.certificateResponseObject.getRootCACertificate() == null && this.certificateResponseObject.getIsSelfSignedCA()) {
            throw new CertificateAPIException("CERT011", "Certificate processed but RootCA is null");
        }
        if (!this.isValidRSAKey()) {
            throw new CertificateAPIException("CERT029", "RSA keys must have size greater than 2048 bits");
        }
        if (!this.isValidHashAlgorithm()) {
            throw new CertificateAPIException("CERT031", "SHA-1 hash algorithm are not supported");
        }
        if (!this.checkValidityOfCertificate(this.certificateResponseObject.getServerCertificate(), this.certIssuedDate, 825)) {
            throw new CertificateAPIException("CERT030", "Certificate expiry date cannot be greater than 825 days");
        }
        final String ignoreOneYearValidity = SecurityUtil.getSecurityParameter("IGNORE_CERT_ONE_YEAR_VALIDITY");
        if ((ignoreOneYearValidity == null || !ignoreOneYearValidity.equalsIgnoreCase("true")) && !this.checkValidityOfCertificate(this.certificateResponseObject.getServerCertificate(), this.certIssuedDate2020, 397)) {
            throw new CertificateAPIException("CERT042", "Certificate expiry date cannot be greater than 397 days, If the certificate is issued after September 1, 2020.");
        }
        if (((X509Certificate)this.certificateResponseObject.getServerCertificate()).getBasicConstraints() != -1) {
            throw new CertificateAPIException("CERT012", true, certificateFileName);
        }
        final Boolean isVerified = CertificateUtils.isValidCertificateAndPrivateKey(this.certificateResponseObject.getServerCertificate(), this.certificateResponseObject.getServerKey());
        if (!isVerified) {
            throw new CertificateAPIException("CERT013", true, certificateFileName);
        }
        final Boolean natSettingsValid = this.checkNatSettingsValidWithCertificate();
        if (!natSettingsValid && !Boolean.valueOf(this.userConfigProperties.getProperty("confirmedChangeInNatSettings", Boolean.FALSE.toString()))) {
            throw new PromptException(CertificateChainBuilderConstants.CONFIRM_NAT_ADDRESS_MISMATCH_STATUS_CODE, "Nat Settings change required.. Sending confirmation message");
        }
    }
    
    private Boolean verifyIfSelfSignedCA() throws Exception {
        final List<Certificate> certificateChain = new ArrayList<Certificate>();
        certificateChain.add(this.certificateResponseObject.getServerCertificate());
        if (this.certificateResponseObject.getIntermediateCertificate() != null && this.certificateResponseObject.getIntermediateCertificate().length > 0) {
            certificateChain.addAll(Arrays.asList(this.certificateResponseObject.getIntermediateCertificate()));
        }
        if (this.certificateResponseObject.getRootCACertificate() != null) {
            certificateChain.add(this.certificateResponseObject.getRootCACertificate());
        }
        final Boolean isCAFoundInCACerts = CertificateUtils.verifyCertificateChainAgainstCACertsFile(certificateChain);
        return !isCAFoundInCACerts;
    }
    
    private void cleanAllTempFolders() throws Exception {
        if (this.certificateResponseObject != null && this.certificateResponseObject.getTempFoldersListToDeleteFinally() != null) {
            for (final String folderPath : this.certificateResponseObject.getTempFoldersListToDeleteFinally()) {
                this.fileAccessApi.deleteDirectory(folderPath);
            }
        }
    }
    
    private void verifyCertificatePath() throws CertificateAPIException {
        try {
            final Set<Certificate> intermediateChain = new HashSet<Certificate>(Arrays.asList(this.certificateResponseObject.getIntermediateCertificate()));
            intermediateChain.addAll(CertificateUtils.getTrustedRootCACertificatesFromCACerts());
            CertificateVerifier.verifyCertificate(this.certificateResponseObject.getServerCertificate(), (Set)intermediateChain);
        }
        catch (final CertificateVerificationException ex) {
            String subMessage = "dc.ssl.server.certificateChain.tryAutoDownload";
            if (Boolean.valueOf(this.userConfigProperties.getProperty("isAutoDownloadIntermediateCerts", Boolean.TRUE.toString()))) {
                subMessage = "dc.ssl.server.certificateChain.tryManualUpload";
            }
            try {
                subMessage = I18N.getMsg(subMessage, new Object[0]);
            }
            catch (final Exception e) {
                this.logger.log(Level.SEVERE, "Excepection while getting i18N keys.", e);
                throw new CertificateAPIException("CERT001", "Excepection while getting i18N keys.", (Throwable)ex);
            }
            throw new CertificateAPIException("CERT020", "Path build failed.", (Throwable)ex, new String[] { subMessage });
        }
        catch (final Exception ex2) {
            throw new CertificateAPIException("CERT001", "Exception in reading trusted root CA certificates from JKS", ex2);
        }
    }
    
    private boolean checkValidityOfCertificate(final Certificate serverCertificate, final String certificateIssuedDate, final int validityPeriod) {
        this.logger.log(Level.INFO, "Inside checkValidityOfCertificate method");
        final X509Certificate certificate = (X509Certificate)serverCertificate;
        final Date notBefore = certificate.getNotBefore();
        final Date notAfter = certificate.getNotAfter();
        final Date issuedDate = new Date(certificateIssuedDate);
        if (notBefore.compareTo(issuedDate) > 0) {
            final long timeDiff = notAfter.getTime() - notBefore.getTime();
            final long days = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);
            if (days > validityPeriod) {
                return false;
            }
        }
        return true;
    }
    
    private boolean isValidRSAKey() {
        this.logger.log(Level.INFO, "Inside isValidRSAKey method");
        if (((RSAPublicKey)this.certificateResponseObject.getServerCertificate().getPublicKey()).getModulus().bitLength() < 2048) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
    
    private boolean isValidHashAlgorithm() {
        this.logger.log(Level.INFO, "Inside isValidHashAlgorithm method");
        try {
            if (((X509Certificate)this.certificateResponseObject.getServerCertificate()).getSigAlgName().contains("SHA1")) {
                return Boolean.FALSE;
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in isValidHashAlgorithm", ex);
        }
        return Boolean.TRUE;
    }
}
