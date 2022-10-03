package com.me.devicemanagement.onpremise.server.certificate;

import java.util.Hashtable;
import java.security.interfaces.RSAPublicKey;
import java.util.concurrent.TimeUnit;
import java.util.Date;
import com.me.devicemanagement.framework.server.certificate.verifier.CertificateVerificationException;
import java.util.Set;
import com.me.devicemanagement.framework.server.certificate.verifier.CertificateVerifier;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Collection;
import java.util.Arrays;
import com.me.devicemanagement.framework.server.certificate.SSLCertificateUtil;
import java.security.cert.CertificateException;
import java.io.IOException;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadStatus;
import com.me.devicemanagement.framework.server.certificate.CertificateUtils;
import com.me.devicemanagement.onpremise.webclient.admin.certificate.IntermediateManager;
import com.me.devicemanagement.onpremise.webclient.admin.certificate.CertificateAttributeManager;
import java.security.cert.X509Certificate;
import java.util.List;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.security.cert.Certificate;
import java.security.PrivateKey;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.io.File;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Properties;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessAPI;
import com.me.devicemanagement.framework.webclient.admin.certificate.CertificateUtil;
import java.util.logging.Logger;

public abstract class CertificateChainBuilder
{
    Logger logger;
    CertificateUtil certUtil;
    FileAccessAPI fileAccessApi;
    String uploadedCertificateFilePath;
    String certIssuedDate;
    String certificateBackUpFolder;
    String uploadFolder;
    Properties userConfigProperties;
    CertificateChainObject certificateResponseObject;
    
    public CertificateChainBuilder(final String uploadedCertificateFilePath, final Properties userConfigProperties) {
        this.logger = Logger.getLogger("ImportCertificateLogger");
        this.certUtil = null;
        this.fileAccessApi = null;
        this.uploadedCertificateFilePath = null;
        this.certIssuedDate = "07/01/19";
        this.certificateBackUpFolder = null;
        this.uploadFolder = null;
        this.userConfigProperties = null;
        this.certificateResponseObject = null;
        this.certUtil = CertificateUtil.getInstance();
        this.fileAccessApi = ApiFactoryProvider.getFileAccessAPI();
        this.uploadFolder = uploadedCertificateFilePath.substring(0, uploadedCertificateFilePath.lastIndexOf(File.separator) + 1);
        this.uploadedCertificateFilePath = uploadedCertificateFilePath;
        this.userConfigProperties = userConfigProperties;
    }
    
    public CertificateChainObject processRequest() throws SyMException {
        final Boolean isBackupSuccessful = Boolean.FALSE;
        final Boolean isFileMoveSuccessful = Boolean.FALSE;
        final Boolean isConfFileUpdationSuccessful = Boolean.FALSE;
        try {
            this.validateUploadedFiles();
            final Boolean isInitialized = this.initialize();
            if (!isInitialized) {
                throw new SyMException(80000, "Initialize Failed", (Throwable)new Exception());
            }
            this.processCertificate();
            this.validateCertificates();
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in import certificate ", ex);
            int errorCode = 80025;
            if (ex instanceof CertificateNotYetValidException || ex instanceof CertificateExpiredException) {
                errorCode = 80001;
            }
            if (ex instanceof NoSuchAlgorithmException || ex instanceof NoSuchProviderException) {
                errorCode = 80002;
            }
            try {
                this.cleanAllTempFolders();
            }
            catch (final Exception exp) {
                throw new SyMException(errorCode, (Throwable)exp);
            }
            if (ex instanceof SyMException) {
                throw (SyMException)ex;
            }
            throw new SyMException(errorCode, (Throwable)ex);
        }
        return this.certificateResponseObject;
    }
    
    protected void validateUploadedFiles() throws Exception {
        if (this.uploadedCertificateFilePath == null) {
            throw new SyMException(80004, "UploadedCertificateFilePath is null", (Throwable)new Exception());
        }
    }
    
    protected abstract PrivateKey obtainPrivateKey() throws Exception;
    
    protected abstract Certificate obtainServerCertificate() throws Exception;
    
    protected abstract Certificate[] obtainCertificateChain() throws Exception;
    
    protected abstract Certificate obtainRootCACertificate() throws Exception;
    
    protected Boolean initialize() throws SyMException {
        return Boolean.TRUE;
    }
    
    protected void processCertificate() throws Exception {
        this.certificateResponseObject = new CertificateChainObject();
        this.certificateResponseObject.tempFoldersListToDeleteFinally = new ArrayList<String>();
        try {
            this.certificateResponseObject.serverKey = this.obtainPrivateKey();
        }
        catch (final UnrecoverableKeyException e) {
            throw new SyMException(80032, "Keystore password does not match with the private key password", (Throwable)e);
        }
        this.certificateResponseObject.serverCertificate = this.obtainServerCertificate();
        this.certificateResponseObject.intermediateCertificate = this.obtainCertificateChain();
        this.certificateResponseObject.rootCACertificate = this.obtainRootCACertificate();
        this.certificateResponseObject.tempFoldersListToDeleteFinally.add(this.uploadFolder);
        this.certificateResponseObject.isSelfSignedCA = this.verifyIfSelfSignedCA();
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
            downloadSuccess = resultMap.get("isSuccess");
            if (downloadSuccess) {
                tempCertObject = CertificateUtils.loadX509CertificateFromFile(new File(downloadPath));
                certificateChainList.add(tempCertObject);
            }
            else {
                downloadStatus = resultMap.get("downloadStatus");
                final String downloadLink = resultMap.get("downloadLink").toString();
                if (resultMap.containsKey("ldap")) {
                    throw new SyMException(80034, "LDAP URL for Intermediate Certificate Present", downloadLink, (Throwable)new Exception());
                }
                if (downloadStatus.getErrorMessage().contains("Unsupported protocol")) {
                    throw new SyMException(80006, "Unsupported DownloadProtocol", downloadLink, (Throwable)new Exception());
                }
                if (downloadStatus.getStatus() == 503) {
                    throw new SyMException(80020, "The url for intermediate certificate download is not reachable from server", downloadLink, (Throwable)new Exception());
                }
                if (downloadStatus.getErrorMessage().contains("Bad Set-Cookie header")) {
                    throw new SyMException(80033, "Unsupported Header in response", downloadLink, (Throwable)new Exception());
                }
                throw new SyMException(80007, "Internet Connection Needed for AutoDownload", (Throwable)new Exception());
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
            natSettingsValid = SSLCertificateUtil.getInstance().checkHostNameValidWithCertificate(natAddress, (X509Certificate)this.certificateResponseObject.serverCertificate);
        }
        return natSettingsValid;
    }
    
    protected void validateCertificates() throws Exception {
        final String certificateFileName = this.uploadedCertificateFilePath.substring(this.uploadedCertificateFilePath.lastIndexOf("/") + 1);
        if (this.certificateResponseObject.serverCertificate == null) {
            throw new SyMException(80008, "Certificate processed but ServerCertificateObject is null", (Throwable)new Exception());
        }
        if (this.certificateResponseObject.serverKey == null) {
            throw new SyMException(80009, "Certificate processed but ServerKeyObject is null", (Throwable)new Exception());
        }
        if (CertificateUtils.isCertificateSelfSigned((X509Certificate)this.certificateResponseObject.serverCertificate)) {
            throw new SyMException(80024, "Self signed certificate cannot be imported. Please import an enterprise or a third party certificate.", (Throwable)new Exception());
        }
        if (this.certificateResponseObject.intermediateCertificate == null || this.certificateResponseObject.intermediateCertificate.length == 0) {
            throw new SyMException(80010, "Certificate processed but intermediate chain is null", (Throwable)new Exception());
        }
        if (!CertificateUtils.isCertificateSelfSigned((X509Certificate)this.certificateResponseObject.serverCertificate)) {
            this.verifyCertificatePath();
        }
        if (this.certificateResponseObject.rootCACertificate == null && this.certificateResponseObject.isSelfSignedCA) {
            throw new SyMException(80011, "Certificate processed but RootCA is null", (Throwable)new Exception());
        }
        if (!this.isValidRSAKey()) {
            throw new SyMException(80035, "RSA keys must have size greater than 2048 bits", (Throwable)new Exception());
        }
        if (!this.isValidHashAlgorithm()) {
            throw new SyMException(80037, "SHA-1 hash algorithm are not supported", (Throwable)new Exception());
        }
        if (!this.checkValidityOfCertificate(this.certificateResponseObject.serverCertificate)) {
            throw new SyMException(80036, "Certificate expiry date cannot be greater than 825 days", (Throwable)new Exception());
        }
        if (((X509Certificate)this.certificateResponseObject.serverCertificate).getBasicConstraints() != -1) {
            throw new SyMException(80012, "Given " + this.uploadedCertificateFilePath + "  is not a End entity Certificate", (Throwable)new Exception());
        }
        final Boolean isVerified = CertificateUtils.isValidCertificateAndPrivateKey(this.certificateResponseObject.serverCertificate, this.certificateResponseObject.serverKey);
        if (!isVerified) {
            throw new SyMException(80013, "The signature of the certificate " + certificateFileName + " could not be verified using the given keyFile ", (Throwable)new Exception());
        }
        final Boolean natSettingsValid = this.checkNatSettingsValidWithCertificate();
        if (!natSettingsValid && !Boolean.valueOf(this.userConfigProperties.getProperty("confirmedChangeInNatSettings", Boolean.FALSE.toString()))) {
            throw new SyMException(80005, "Nat Settings change required.. Sending confirmation message", (Throwable)new Exception());
        }
    }
    
    private Boolean verifyIfSelfSignedCA() throws Exception {
        final List<Certificate> certificateChain = new ArrayList<Certificate>();
        certificateChain.add(this.certificateResponseObject.serverCertificate);
        if (this.certificateResponseObject.intermediateCertificate != null && this.certificateResponseObject.intermediateCertificate.length > 0) {
            certificateChain.addAll(Arrays.asList(this.certificateResponseObject.intermediateCertificate));
        }
        if (this.certificateResponseObject.rootCACertificate != null) {
            certificateChain.add(this.certificateResponseObject.rootCACertificate);
        }
        final Boolean isCAFoundInCACerts = CertificateUtils.verifyCertificateChainAgainstCACertsFile((List)certificateChain);
        return !isCAFoundInCACerts;
    }
    
    private void cleanAllTempFolders() throws Exception {
        if (this.certificateResponseObject != null && this.certificateResponseObject.tempFoldersListToDeleteFinally != null) {
            for (final String folderPath : this.certificateResponseObject.tempFoldersListToDeleteFinally) {
                this.fileAccessApi.deleteDirectory(folderPath);
            }
        }
    }
    
    private void verifyCertificatePath() throws SyMException {
        try {
            final Set<Certificate> intermediateChain = new HashSet<Certificate>(Arrays.asList(this.certificateResponseObject.intermediateCertificate));
            intermediateChain.addAll(CertificateUtils.getTrustedRootCACertificatesFromCACerts());
            CertificateVerifier.verifyCertificate(this.certificateResponseObject.serverCertificate, (Set)intermediateChain);
        }
        catch (final CertificateVerificationException ex) {
            throw new SyMException(80021, "Path build failed.", (Throwable)ex);
        }
        catch (final Exception ex2) {
            throw new SyMException(80025, "Exception in reading trusted root CA certificates from JKS", (Throwable)ex2);
        }
    }
    
    private Boolean allowEnterpriseCASignedCertificateUpload() throws Exception {
        Boolean allowSelfSignedCA = Boolean.TRUE;
        if (this.certificateResponseObject.isSelfSignedCA) {
            final Boolean confirmedSelfSignedCA = Boolean.valueOf(this.userConfigProperties.getProperty("confirmedSelfSignedCA", Boolean.FALSE.toString()));
            if (!confirmedSelfSignedCA) {
                allowSelfSignedCA = Boolean.FALSE;
                throw new SyMException(80019, "User confirmation required for EnterpriseCACert", (Throwable)new Exception());
            }
            if (confirmedSelfSignedCA) {
                allowSelfSignedCA = Boolean.TRUE;
            }
        }
        return allowSelfSignedCA;
    }
    
    private boolean checkValidityOfCertificate(final Certificate serverCertificate) {
        this.logger.log(Level.INFO, "Inside checkValidityOfCertificate method");
        final X509Certificate certificate = (X509Certificate)serverCertificate;
        final Date notBefore = certificate.getNotBefore();
        final Date notAfter = certificate.getNotAfter();
        final Date issuedDate = new Date(this.certIssuedDate);
        if (notBefore.compareTo(issuedDate) > 0) {
            final long timeDiff = notAfter.getTime() - notBefore.getTime();
            final long days = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);
            if (days > 825L) {
                return false;
            }
        }
        return true;
    }
    
    private boolean isValidRSAKey() {
        this.logger.log(Level.INFO, "Inside isValidRSAKey method");
        if (((RSAPublicKey)this.certificateResponseObject.serverCertificate.getPublicKey()).getModulus().bitLength() < 2048) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
    
    private boolean isValidHashAlgorithm() {
        this.logger.log(Level.INFO, "Inside isValidHashAlgorithm method");
        try {
            if (((X509Certificate)this.certificateResponseObject.serverCertificate).getSigAlgName().contains("SHA1")) {
                return Boolean.FALSE;
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in isValidHashAlgorithm", ex);
        }
        return Boolean.TRUE;
    }
}
