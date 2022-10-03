package com.me.ems.onpremise.security.certificate.api.core.builders;

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
import com.me.ems.onpremise.security.certificate.api.Exception.CertificateAPIException;
import com.me.ems.onpremise.security.certificate.api.core.utils.CertificateUtils;
import com.me.ems.onpremise.security.certificate.api.core.utils.SSLCertificateUtil;
import java.io.File;
import java.util.Properties;
import java.util.List;
import java.nio.file.Path;

public class DEROrCRTCertificateChainBuilder extends CertificateChainBuilder
{
    String serverHome;
    String apacheConfFolder;
    String baseDownloadFolder;
    Path uploadedCertificateKeyFilePath;
    List<Path> uploadedIntermediateCertificateFiles;
    
    public DEROrCRTCertificateChainBuilder(final Path uploadedCertificateFilePath, final Path uploadedCertificateKeyFilePath, final List<Path> uploadedIntermediateCertificatesFiles, final Properties userConfigProperties) {
        super(uploadedCertificateFilePath, userConfigProperties);
        this.serverHome = System.getProperty("server.home");
        this.apacheConfFolder = this.serverHome + File.separator + SSLCertificateUtil.webServerName + File.separator + "conf" + File.separator;
        this.baseDownloadFolder = this.apacheConfFolder + "downloaded_files";
        this.uploadedCertificateKeyFilePath = null;
        this.uploadedIntermediateCertificateFiles = null;
        this.uploadedCertificateKeyFilePath = uploadedCertificateKeyFilePath;
        this.uploadedIntermediateCertificateFiles = uploadedIntermediateCertificatesFiles;
    }
    
    public DEROrCRTCertificateChainBuilder(final Path uploadedCertificateFilePath, final Path uploadedCertificateKeyFilePath, final Properties userConfigProperties) {
        super(uploadedCertificateFilePath, userConfigProperties);
        this.serverHome = System.getProperty("server.home");
        this.apacheConfFolder = this.serverHome + File.separator + SSLCertificateUtil.webServerName + File.separator + "conf" + File.separator;
        this.baseDownloadFolder = this.apacheConfFolder + "downloaded_files";
        this.uploadedCertificateKeyFilePath = null;
        this.uploadedIntermediateCertificateFiles = null;
        this.uploadedCertificateKeyFilePath = uploadedCertificateKeyFilePath;
        this.uploadedIntermediateCertificateFiles = null;
    }
    
    @Override
    protected void validateUploadedFiles() throws Exception {
        super.validateUploadedFiles();
        if (!CertificateUtils.getExtension(this.uploadedCertificateFilePath.toString()).equalsIgnoreCase(".crt") && !CertificateUtils.getExtension(this.uploadedCertificateFilePath.toString()).equalsIgnoreCase(".cer") && !CertificateUtils.getExtension(this.uploadedCertificateFilePath.toString()).equalsIgnoreCase(".der")) {
            throw new CertificateAPIException("CERT015", "Uploaded certificate is not CRT or CER format");
        }
        if (!CertificateUtils.getExtension(this.uploadedCertificateKeyFilePath.toString()).equalsIgnoreCase(".key")) {
            throw new CertificateAPIException("CERT016", "Uploaded key file is invalid or has an invalid extension");
        }
        Boolean isVerified = false;
        try {
            isVerified = CertificateUtils.isValidCertificateAndPrivateKey(CertificateUtils.loadX509CertificateFromFile(this.uploadedCertificateFilePath), CertificateUtils.loadPrivateKeyFromFilePath(this.uploadedCertificateKeyFilePath));
        }
        catch (final ExtendedInvalidKeySpecException e) {
            throw new CertificateAPIException("CERT014", "Encrypted key file uploaded. Decrypt it and upload it again", (Throwable)e);
        }
        if (!isVerified) {
            throw new CertificateAPIException("CERT013", "The signature of the certificate " + this.uploadedCertificateFilePath + " could not be verified using the given keyFile ", new String[] { this.uploadedCertificateFilePath.getFileName().toString() });
        }
    }
    
    @Override
    protected PrivateKey obtainPrivateKey() throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, Exception {
        final PrivateKey privateKey = CertificateUtils.loadPrivateKeyFromFilePath(this.uploadedCertificateKeyFilePath);
        return privateKey;
    }
    
    @Override
    protected Certificate obtainServerCertificate() throws IOException, CertificateNotYetValidException, CertificateExpiredException, CertificateException, Exception {
        return CertificateUtils.loadX509CertificateFromFile(this.uploadedCertificateFilePath);
    }
    
    @Override
    protected Certificate[] obtainCertificateChain() throws IOException, CertificateNotYetValidException, CertificateExpiredException, CertificateException, Exception {
        List<Certificate> certificateChainList = new ArrayList<Certificate>();
        final Boolean isAutoDownloadIntermediateCerts = Boolean.valueOf(this.userConfigProperties.getProperty("isAutoDownloadIntermediateCerts", Boolean.TRUE.toString()));
        if (isAutoDownloadIntermediateCerts) {
            this.certificateResponseObject.getTempFoldersListToDeleteFinally().add(this.baseDownloadFolder);
            certificateChainList = this.autoDownloadIntermediateCerts(this.baseDownloadFolder);
        }
        else {
            for (final Path currentIntermediateFilePath : this.uploadedIntermediateCertificateFiles) {
                certificateChainList.add(CertificateUtils.loadX509CertificateFromFile(currentIntermediateFilePath));
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
        if (this.certificateResponseObject.getIntermediateCertificate() != null) {
            rootCACert = CertificateUtils.getRootCACertificateFromCertificates(this.certificateResponseObject.getIntermediateCertificate());
        }
        return rootCACert;
    }
}
