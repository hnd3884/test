package com.me.devicemanagement.onpremise.server.certificate;

import java.security.cert.CertificateParsingException;
import java.util.Iterator;
import java.util.ArrayList;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.spec.InvalidKeySpecException;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import java.io.IOException;
import java.security.PrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.ExtendedInvalidKeySpecException;
import java.security.cert.Certificate;
import com.me.devicemanagement.framework.server.certificate.CertificateUtils;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.io.File;
import java.util.Properties;
import java.util.List;

public class DEROrCRTCertificateChainBuilder extends CertificateChainBuilder
{
    String serverHome;
    String apacheConfFolder;
    String baseDownloadFolder;
    String uploadedCertificateKeyFile;
    List<String> uploadedIntermediateCertificateFiles;
    
    public DEROrCRTCertificateChainBuilder(final String uploadedCertificateFilePath, final String uploadedCertificateKeyFile, final List<String> uploadedIntermediateCertificatesFiles, final Properties userConfigProperties) {
        super(uploadedCertificateFilePath, userConfigProperties);
        this.serverHome = System.getProperty("server.home");
        this.apacheConfFolder = this.serverHome + File.separator + SSLCertificateUtil.webServerName + File.separator + "conf" + File.separator;
        this.baseDownloadFolder = this.apacheConfFolder + "downloaded_files";
        this.uploadedCertificateKeyFile = null;
        this.uploadedIntermediateCertificateFiles = null;
        this.uploadedCertificateKeyFile = uploadedCertificateKeyFile;
        this.uploadedIntermediateCertificateFiles = uploadedIntermediateCertificatesFiles;
    }
    
    public DEROrCRTCertificateChainBuilder(final String uploadedCertificateFilePath, final String uploadedCertificateKeyFile, final Properties userConfigProperties) {
        super(uploadedCertificateFilePath, userConfigProperties);
        this.serverHome = System.getProperty("server.home");
        this.apacheConfFolder = this.serverHome + File.separator + SSLCertificateUtil.webServerName + File.separator + "conf" + File.separator;
        this.baseDownloadFolder = this.apacheConfFolder + "downloaded_files";
        this.uploadedCertificateKeyFile = null;
        this.uploadedIntermediateCertificateFiles = null;
        this.uploadedCertificateKeyFile = uploadedCertificateKeyFile;
        this.uploadedIntermediateCertificateFiles = null;
    }
    
    @Override
    protected void validateUploadedFiles() throws Exception {
        super.validateUploadedFiles();
        if (!this.certUtil.getExtension(this.uploadedCertificateFilePath).equalsIgnoreCase(".crt") && !this.certUtil.getExtension(this.uploadedCertificateFilePath).equalsIgnoreCase(".cer") && !this.certUtil.getExtension(this.uploadedCertificateFilePath).equalsIgnoreCase(".der")) {
            throw new SyMException(80014, "Uploaded certificate is not CRT or CER format", (Throwable)new Exception());
        }
        if (!this.certUtil.getExtension(this.uploadedCertificateKeyFile).equalsIgnoreCase(".key")) {
            throw new SyMException(80015, "Uploaded key file is invalid or has an invalid extension", (Throwable)new Exception());
        }
        Boolean isVerified = false;
        try {
            isVerified = CertificateUtils.isValidCertificateAndPrivateKey((Certificate)CertificateUtils.loadX509CertificateFromFile(new File(this.uploadedCertificateFilePath)), CertificateUtils.loadPrivateKeyFromFile(new File(this.uploadedCertificateKeyFile)));
        }
        catch (final ExtendedInvalidKeySpecException e) {
            throw new SyMException(80030, "Encrypted key file uploaded. Decrypt it and upload it again", (Throwable)new Exception());
        }
        if (!isVerified) {
            throw new SyMException(80013, "The signature of the certificate " + this.uploadedCertificateFilePath + " could not be verified using the given keyFile ", (Throwable)new Exception());
        }
    }
    
    @Override
    protected PrivateKey obtainPrivateKey() throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, Exception {
        final PrivateKey privateKey = CertificateUtils.loadPrivateKeyFromFile(new File(this.uploadedCertificateKeyFile));
        return privateKey;
    }
    
    @Override
    protected Certificate obtainServerCertificate() throws IOException, CertificateNotYetValidException, CertificateExpiredException, CertificateException, Exception {
        return CertificateUtils.loadX509CertificateFromFile(new File(this.uploadedCertificateFilePath));
    }
    
    @Override
    protected Certificate[] obtainCertificateChain() throws IOException, CertificateNotYetValidException, CertificateExpiredException, CertificateException, Exception {
        List<Certificate> certificateChainList = new ArrayList<Certificate>();
        final Boolean isAutoDownloadIntermediateCerts = Boolean.valueOf(this.userConfigProperties.getProperty("isAutoDownloadIntermediateCerts", Boolean.TRUE.toString()));
        if (isAutoDownloadIntermediateCerts) {
            this.certificateResponseObject.tempFoldersListToDeleteFinally.add(this.baseDownloadFolder);
            certificateChainList = this.autoDownloadIntermediateCerts(this.baseDownloadFolder);
        }
        else {
            for (final String currentIntermediateFileName : this.uploadedIntermediateCertificateFiles) {
                certificateChainList.add(CertificateUtils.loadX509CertificateFromFile(new File(currentIntermediateFileName)));
            }
        }
        if (certificateChainList.isEmpty()) {
            return null;
        }
        return certificateChainList.toArray(new Certificate[1]);
    }
    
    @Override
    protected Certificate obtainRootCACertificate() throws CertificateParsingException, CertificateException, NoSuchAlgorithmException, NoSuchProviderException {
        Certificate rootCACert = null;
        if (this.certificateResponseObject.intermediateCertificate != null) {
            rootCACert = CertificateUtils.getRootCACertificateFromCertificates(this.certificateResponseObject.intermediateCertificate);
        }
        return rootCACert;
    }
}
