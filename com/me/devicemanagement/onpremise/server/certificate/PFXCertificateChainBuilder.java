package com.me.devicemanagement.onpremise.server.certificate;

import java.security.cert.X509Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.certificate.CertificateUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.security.cert.Certificate;
import java.security.UnrecoverableKeyException;
import java.security.PrivateKey;
import java.util.Enumeration;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.NoSuchAlgorithmException;
import java.io.IOException;
import java.security.NoSuchProviderException;
import java.security.KeyStoreException;
import java.io.EOFException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import java.security.Provider;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.io.File;
import java.util.Properties;
import java.util.List;
import java.security.KeyStore;

public class PFXCertificateChainBuilder extends CertificateChainBuilder
{
    KeyStore keystore;
    String pfxPassword;
    String certAliasInPFX;
    String serverHome;
    String webServerConfFolder;
    String baseDownloadFolder;
    List<String> uploadedIntermediateCertificateFiles;
    
    public PFXCertificateChainBuilder(final String uploadedCertificateFilePath, final String pfxPassword, final Properties userConfigProperties) {
        super(uploadedCertificateFilePath, userConfigProperties);
        this.keystore = null;
        this.pfxPassword = null;
        this.certAliasInPFX = null;
        this.serverHome = System.getProperty("server.home");
        this.webServerConfFolder = this.serverHome + File.separator + SSLCertificateUtil.webServerName + File.separator + "conf" + File.separator;
        this.baseDownloadFolder = this.webServerConfFolder + "downloaded_files";
        this.uploadedIntermediateCertificateFiles = null;
        this.pfxPassword = pfxPassword;
    }
    
    public PFXCertificateChainBuilder(final String uploadedCertificateFilePath, final String pfxPassword, final List<String> uploadedIntermediateCertificatesFiles, final Properties userConfigProperties) {
        super(uploadedCertificateFilePath, userConfigProperties);
        this.keystore = null;
        this.pfxPassword = null;
        this.certAliasInPFX = null;
        this.serverHome = System.getProperty("server.home");
        this.webServerConfFolder = this.serverHome + File.separator + SSLCertificateUtil.webServerName + File.separator + "conf" + File.separator;
        this.baseDownloadFolder = this.webServerConfFolder + "downloaded_files";
        this.uploadedIntermediateCertificateFiles = null;
        this.pfxPassword = pfxPassword;
        this.uploadedIntermediateCertificateFiles = uploadedIntermediateCertificatesFiles;
    }
    
    @Override
    protected Boolean initialize() throws SyMException {
        Boolean isInitialized = Boolean.FALSE;
        Boolean isJKSUpload = false;
        Boolean isKeystoreUpload = false;
        isJKSUpload = Boolean.valueOf(this.userConfigProperties.getProperty("jksUpload"));
        isKeystoreUpload = Boolean.valueOf(this.userConfigProperties.getProperty("keystoreUpload"));
        try {
            Security.addProvider((Provider)new BouncyCastleProvider());
            if (!isJKSUpload && !isKeystoreUpload) {
                this.keystore = KeyStore.getInstance("PKCS12", "BC");
                this.logger.log(Level.INFO, "PFX Upload");
            }
            else if (isJKSUpload) {
                this.keystore = KeyStore.getInstance("JKS");
                this.logger.log(Level.INFO, "JKS Upload");
            }
            else if (isKeystoreUpload) {
                this.keystore = KeyStore.getInstance(KeyStore.getDefaultType());
                this.logger.log(Level.INFO, "Keystore Upload");
            }
            final InputStream pfxInputStream = this.fileAccessApi.readFile(this.uploadedCertificateFilePath);
            this.keystore.load(pfxInputStream, this.pfxPassword.toCharArray());
            pfxInputStream.close();
            final Enumeration<String> e = this.keystore.aliases();
            while (e.hasMoreElements()) {
                this.certAliasInPFX = e.nextElement();
                if (this.keystore.isKeyEntry(this.certAliasInPFX) && !isJKSUpload && !isKeystoreUpload) {
                    break;
                }
            }
            isInitialized = Boolean.TRUE;
        }
        catch (final EOFException ex) {
            throw new SyMException(80023, "Given PFX or JKS or Keystore certificate is corrupted", (Throwable)ex);
        }
        catch (final KeyStoreException ex2) {
            this.logger.log(Level.SEVERE, "Key store not instantiated.. PFX or JKS or Keystore may be in a different format", ex2);
        }
        catch (final NoSuchProviderException ex3) {
            this.logger.log(Level.SEVERE, "BC Provider not supported..send the logs..", ex3);
        }
        catch (final IOException ex4) {
            throw new SyMException(80022, "PFX or JKS or Keystore Password given is wrong", (Throwable)ex4);
        }
        catch (final NoSuchAlgorithmException ex5) {
            this.logger.log(Level.SEVERE, "Algorithm used was not supported by the tool.. send the logs", ex5);
        }
        catch (final CertificateException ex6) {
            this.logger.log(Level.SEVERE, "Certificate failed to initialize..send the logs", ex6);
        }
        catch (final Exception ex7) {
            this.logger.log(Level.SEVERE, "Exception in handling the file", ex7);
        }
        return isInitialized;
    }
    
    @Override
    protected PrivateKey obtainPrivateKey() throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, SyMException {
        try {
            final PrivateKey privateKey = (PrivateKey)this.keystore.getKey(this.certAliasInPFX, this.pfxPassword.toCharArray());
            return privateKey;
        }
        catch (final UnrecoverableKeyException e) {
            throw new UnrecoverableKeyException("Keystore password does not match with the private key password");
        }
    }
    
    @Override
    protected Certificate[] obtainCertificateChain() throws KeyStoreException, NoSuchProviderException, IOException, NoSuchAlgorithmException, CertificateNotYetValidException, CertificateExpiredException, CertificateException, Exception {
        final Boolean pfxIntermediateManualUpload = Boolean.valueOf(this.userConfigProperties.getProperty("pfxIntermediateManualUplaod", Boolean.FALSE.toString()));
        final Certificate[] certChain = this.keystore.getCertificateChain(this.certAliasInPFX);
        final Certificate[] newCertChain = Arrays.copyOfRange(certChain, 1, certChain.length);
        if (newCertChain != null && newCertChain.length != 0) {
            return newCertChain;
        }
        List<Certificate> certificateChainList = new ArrayList<Certificate>();
        if (!pfxIntermediateManualUpload) {
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
    protected Certificate obtainRootCACertificate() throws CertificateException, NoSuchAlgorithmException, NoSuchProviderException {
        Certificate rootCACert = null;
        if (this.certificateResponseObject.intermediateCertificate != null) {
            rootCACert = CertificateUtils.getRootCACertificateFromCertificates(this.certificateResponseObject.intermediateCertificate);
        }
        return rootCACert;
    }
    
    @Override
    protected Certificate obtainServerCertificate() throws KeyStoreException, CertificateExpiredException, CertificateNotYetValidException {
        final Certificate certificate = this.keystore.getCertificate(this.certAliasInPFX);
        final X509Certificate x509certificate = (X509Certificate)certificate;
        x509certificate.checkValidity();
        return this.keystore.getCertificate(this.certAliasInPFX);
    }
    
    @Override
    protected void validateUploadedFiles() throws Exception {
        super.validateUploadedFiles();
        if (!this.certUtil.getExtension(this.uploadedCertificateFilePath).equalsIgnoreCase(".jks") && !this.certUtil.getExtension(this.uploadedCertificateFilePath).equalsIgnoreCase(".pfx") && !this.certUtil.getExtension(this.uploadedCertificateFilePath).equalsIgnoreCase(".keystore")) {
            final String serverCertName = this.certUtil.getNameOfTheFile(this.uploadedCertificateFilePath);
            throw new SyMException(80003, "PFX or JKS  or KeyStore mode selected but the uploaded file does not have PFX or JKS or Keystore extension", (Throwable)new Exception());
        }
    }
}
