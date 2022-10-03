package com.me.devicemanagement.onpremise.webclient.admin.certificate;

import java.util.Iterator;
import com.me.devicemanagement.framework.server.certificate.SSLCertificateUtil;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.io.InputStream;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import javax.security.auth.x500.X500Principal;
import java.security.Principal;
import java.util.Date;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import com.me.devicemanagement.onpremise.start.util.WebServerUtil;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.security.cert.CertificateException;
import java.security.cert.CertificateEncodingException;
import java.security.UnrecoverableKeyException;
import java.security.NoSuchAlgorithmException;
import java.io.IOException;
import java.security.NoSuchProviderException;
import java.security.KeyStoreException;
import java.util.logging.Level;
import java.io.Writer;
import org.bouncycastle.openssl.PEMWriter;
import java.io.OutputStreamWriter;
import java.io.File;
import org.apache.commons.codec.binary.Base64;
import java.security.PrivateKey;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessAPI;

public class CertificateUtil extends com.me.devicemanagement.framework.webclient.admin.certificate.CertificateUtil
{
    private static String sourceClass;
    private static FileAccessAPI fileAccessAPI;
    private static CertificateUtil certificateUtil;
    private static Logger logger;
    private static String webServerLocation;
    
    private CertificateUtil() {
    }
    
    public static CertificateUtil getInstance() {
        if (CertificateUtil.certificateUtil == null) {
            CertificateUtil.certificateUtil = new CertificateUtil();
        }
        return CertificateUtil.certificateUtil;
    }
    
    public boolean exportPrivateKey(final String thirdPartyPFXCertificateFile, final String pfxPassword) {
        final String sourceMethod = "exportPrivateKey";
        CertificateUtil.logger.info("Exporting private key from certificate began..");
        Security.addProvider((Provider)new BouncyCastleProvider());
        OutputStreamWriter out = null;
        KeyStore keystore = null;
        PEMWriter writer = null;
        try {
            keystore = KeyStore.getInstance("PKCS12", "BC");
            keystore.load(CertificateUtil.fileAccessAPI.readFile(thirdPartyPFXCertificateFile), pfxPassword.toCharArray());
            final Enumeration<String> e = keystore.aliases();
            String alias = null;
            while (e.hasMoreElements()) {
                alias = e.nextElement();
                if (keystore.isKeyEntry(alias)) {
                    break;
                }
            }
            final Certificate certificate = keystore.getCertificate(alias);
            final PrivateKey privateKey = (PrivateKey)keystore.getKey(alias, pfxPassword.toCharArray());
            final byte[] bytes = certificate.getEncoded();
            final String convPem = new String(Base64.encodeBase64(bytes, true));
            final String certpem = "-----BEGIN CERTIFICATE-----\n" + convPem + "-----END CERTIFICATE-----" + "\n";
            out = new OutputStreamWriter(CertificateUtil.fileAccessAPI.writeFile(this.getParentDirectory(thirdPartyPFXCertificateFile) + File.separator + "server.crt"));
            out.write(certpem);
            out.flush();
            out.close();
            final Certificate[] certificateChain = keystore.getCertificateChain(alias);
            IntermediateManager.getInstance().retrieveIntermediateFromPFX(certificateChain);
            if (privateKey != null) {
                writer = new PEMWriter((Writer)new OutputStreamWriter(CertificateUtil.fileAccessAPI.writeFile(this.getParentDirectory(thirdPartyPFXCertificateFile) + File.separator + "server.key")));
                writer.writeObject((Object)privateKey);
                writer.flush();
                writer.close();
                return true;
            }
            CertificateUtil.logger.severe("Key not extracated from pfx file.. Please send the logs to dc-support@zohocorp.com");
            return false;
        }
        catch (final KeyStoreException ex) {
            CertificateUtil.logger.logp(Level.SEVERE, CertificateUtil.sourceClass, sourceMethod, "Key store not instantiated.. PFX may be in a different format", ex);
        }
        catch (final NoSuchProviderException ex2) {
            CertificateUtil.logger.logp(Level.SEVERE, CertificateUtil.sourceClass, sourceMethod, "BC Provider not supported..send the logs..");
        }
        catch (final IOException ex3) {
            CertificateUtil.logger.logp(Level.SEVERE, CertificateUtil.sourceClass, sourceMethod, thirdPartyPFXCertificateFile + " File may be in use/not available", ex3);
        }
        catch (final NoSuchAlgorithmException ex4) {
            CertificateUtil.logger.logp(Level.SEVERE, CertificateUtil.sourceClass, sourceMethod, thirdPartyPFXCertificateFile + " Algorithm used was not supported by the tool.. send the logs");
        }
        catch (final UnrecoverableKeyException ex5) {
            CertificateUtil.logger.logp(Level.SEVERE, CertificateUtil.sourceClass, sourceMethod, " Key generation failed for " + thirdPartyPFXCertificateFile + " .. Send the logs", ex5);
        }
        catch (final CertificateEncodingException ex6) {
            CertificateUtil.logger.logp(Level.SEVERE, CertificateUtil.sourceClass, sourceMethod, "Failed in converting certificate to pem..send the logs");
        }
        catch (final CertificateException ex7) {
            CertificateUtil.logger.logp(Level.SEVERE, CertificateUtil.sourceClass, sourceMethod, "Certificate failed to initialise..send the logs");
        }
        catch (final Exception ex8) {
            CertificateUtil.logger.logp(Level.SEVERE, CertificateUtil.sourceClass, sourceMethod, "Exception in handlingthe file", ex8);
        }
        finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (out != null) {
                    out.close();
                }
            }
            catch (final IOException ex9) {
                CertificateUtil.logger.logp(Level.SEVERE, CertificateUtil.sourceClass, sourceMethod, "IO Exception in closingthe stream");
            }
            catch (final Exception ex10) {
                CertificateUtil.logger.logp(Level.SEVERE, CertificateUtil.sourceClass, sourceMethod, "Exception in closingthe stream");
            }
        }
        return false;
    }
    
    boolean moveExtractedPFXFilesToConfDirectory(final String uploadedDirectory) {
        final String sourceMethod = "moveExtractedPFXFilesToConfDirectory";
        try {
            final String confDirectory = CertificateUtil.webServerLocation + File.separator + "conf";
            this.checkAndMoveFileToConfDir(uploadedDirectory + File.separator + "server.crt", confDirectory);
            this.checkAndMoveFileToConfDir(uploadedDirectory + File.separator + "server.key", confDirectory);
            this.checkAndMoveFileToConfDir(uploadedDirectory + File.separator + "intermediate.crt", confDirectory);
            return true;
        }
        catch (final Exception ex) {
            CertificateUtil.logger.logp(Level.SEVERE, CertificateUtil.sourceClass, sourceMethod, " PFX server.crt server.key copying failed..", ex);
            return false;
        }
    }
    
    public String getServerCertificateWebSettingsFilePath() {
        String oldServerCrtFileFromProperty = null;
        try {
            final Properties webSettingsProp = WebServerUtil.getWebServerSettings();
            final String confDirectory = CertificateUtil.webServerLocation + File.separator + "conf";
            final String serverCrtFileLoc = webSettingsProp.getProperty("server.crt.loc");
            if (serverCrtFileLoc != null && !"".equals(serverCrtFileLoc)) {
                oldServerCrtFileFromProperty = confDirectory + File.separator + serverCrtFileLoc;
            }
            else {
                oldServerCrtFileFromProperty = confDirectory + File.separator + webSettingsProp.getProperty("apache.crt.loc");
            }
            if (this.checkExistenceOfFile(oldServerCrtFileFromProperty)) {
                return oldServerCrtFileFromProperty;
            }
            return null;
        }
        catch (final Exception ex) {
            CertificateUtil.logger.log(Level.SEVERE, "Error while getting the Properties from the websettings.conf file", ex);
            return oldServerCrtFileFromProperty;
        }
    }
    
    public Map getCertificateDetails(final String certificateFilePath) {
        final Map certificateDetails = new HashMap();
        final X509Certificate cert = this.generateCertificateFromFile(certificateFilePath);
        final Date notAfter = cert.getNotAfter();
        final Date notBefore = cert.getNotBefore();
        final Principal subjectPrincipal = cert.getSubjectDN();
        final String subjectName = subjectPrincipal.getName();
        if (cert.getBasicConstraints() != -1) {
            return null;
        }
        if (subjectName != null) {
            final StringTokenizer tokenizer = new StringTokenizer(subjectName, ", ");
            while (tokenizer.hasMoreElements()) {
                final String token = (String)tokenizer.nextElement();
                final String[] strArray = token.split("=");
                if (token.startsWith("CN=")) {
                    certificateDetails.put("CertificateName", strArray[1]);
                }
                else {
                    if (!token.startsWith("UID=")) {
                        continue;
                    }
                    certificateDetails.put("Topic", strArray[1]);
                }
            }
        }
        final X500Principal issuerPrincipal = cert.getIssuerX500Principal();
        final String issuerDistinguishedName = issuerPrincipal.getName();
        if (issuerDistinguishedName != null) {
            final String[] strIssuerNameArray = issuerDistinguishedName.split(",");
            for (int issuerNameIndex = 0; issuerNameIndex < strIssuerNameArray.length; ++issuerNameIndex) {
                final String issuerName = strIssuerNameArray[issuerNameIndex];
                final String[] strArray2 = issuerName.split("=");
                if (issuerName.startsWith("CN=")) {
                    certificateDetails.put("IssuerName", strArray2[1]);
                }
                else if (issuerName.startsWith("OU=")) {
                    certificateDetails.put("IssuerOrganizationalUnitName", strArray2[1]);
                }
                else if (issuerName.startsWith("O=")) {
                    certificateDetails.put("IssuerOrganizationName", strArray2[1]);
                }
            }
        }
        final SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
        certificateDetails.put("CreationDate", sdf.format(notBefore));
        certificateDetails.put("ExpiryDate", sdf.format(notAfter));
        return certificateDetails;
    }
    
    private void checkAndMoveFileToConfDir(final String fileToCopy, final String confDirectory) throws Exception {
        if (ApiFactoryProvider.getFileAccessAPI().isFileExists(fileToCopy)) {
            CertificateUtil.fileAccessAPI.copyFile(fileToCopy, confDirectory + File.separator + this.getNameOfTheFile(fileToCopy));
        }
    }
    
    public KeyStore getKeyStoreObject(final String certFile, final String intermediateCertFile, final String rootCertFile, final String keyFile, final String password, final String keyStoreAlias) {
        final String sourceMethod = "getKeyStoreObject - ";
        final List<X509Certificate> certList = new ArrayList<X509Certificate>();
        X509Certificate serverCrt = null;
        KeyStore serverKeyStore = null;
        try {
            PrivateKey privateKey = null;
            if (certFile != null) {
                serverCrt = this.generateCertificateFromFile(certFile);
                if (serverCrt != null) {
                    certList.add(serverCrt);
                }
            }
            if (intermediateCertFile != null && intermediateCertFile != "") {
                final X509Certificate interCrt = this.generateCertificateFromFile(intermediateCertFile);
                if (interCrt != null) {
                    certList.add(interCrt);
                }
            }
            if (rootCertFile != null && rootCertFile != "") {
                final X509Certificate rootCert = this.generateCertificateFromFile(rootCertFile);
                if (rootCert != null) {
                    certList.add(rootCert);
                }
            }
            if (keyFile != null) {
                Security.addProvider((Provider)new BouncyCastleProvider());
                final CertificateAlgorithm algorithmObj = CertificateAlgorithmIdentifier.getAlgorithmFromCertificate(serverCrt);
                privateKey = algorithmObj.loadPrivateKeyFromFile(keyFile);
            }
            X509Certificate[] certArray = null;
            if (certList.size() > 0) {
                certArray = new X509Certificate[certList.size()];
                certList.toArray(certArray);
            }
            if (privateKey != null && certArray != null && keyStoreAlias != null) {
                final KeyStore.PrivateKeyEntry serverPrivateKey = new KeyStore.PrivateKeyEntry(privateKey, certArray);
                serverKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                serverKeyStore.load(null, password.toCharArray());
                serverKeyStore.setEntry(keyStoreAlias, serverPrivateKey, new KeyStore.PasswordProtection(password.toCharArray()));
                CertificateUtil.logger.log(Level.INFO, sourceMethod + "KeyStore for alias " + keyStoreAlias + " has been created successfully !!");
            }
            else {
                CertificateUtil.logger.warning(sourceMethod + "Entries needed to create Key Entry is Null - " + keyStoreAlias);
            }
        }
        catch (final Exception excep) {
            CertificateUtil.logger.log(Level.SEVERE, sourceMethod + "Following exception occurred while generating keystore from Server crt and key - ", excep);
        }
        return serverKeyStore;
    }
    
    public boolean saveKeyStoreObjectToFile(final KeyStore keyStoreObj, final String filePath, final boolean fileCreateMode, final String password) {
        final String sourceMethod = "saveKeyStoreObjectToFile - ";
        boolean saveStatus = false;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath, fileCreateMode);
            keyStoreObj.store(fos, password.toCharArray());
            saveStatus = true;
        }
        catch (final FileNotFoundException fileExcep) {
            CertificateUtil.logger.log(Level.SEVERE, sourceMethod + "FileNotFoundException while opening - " + filePath, fileExcep);
        }
        catch (final KeyStoreException keyStoreExcep) {
            CertificateUtil.logger.log(Level.SEVERE, sourceMethod + "KeyStoreException while storing keystore - " + filePath, keyStoreExcep);
        }
        catch (final Exception excep) {
            CertificateUtil.logger.log(Level.SEVERE, sourceMethod + "Exception while storing keystore - " + filePath, excep);
        }
        finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            }
            catch (final Exception excep2) {
                CertificateUtil.logger.log(Level.SEVERE, sourceMethod + "Exception while closing File Stream - " + filePath, excep2);
            }
        }
        return saveStatus;
    }
    
    public boolean generateServerKeystoreFile(final String keyStorePath, final String aliasName, final String password) {
        final String sourceMethod = "generateServerKeystoreFile - ";
        final String serverHome = System.getProperty("server.home");
        boolean createStatus = false;
        final InputStream inputStream = null;
        try {
            final Properties webSettingsProp = WebServerUtil.getWebServerSettings();
            final String webServerConfDir = WebServerUtil.getWebServerConfDir();
            final String serverCrtFromWebSettings = webSettingsProp.getProperty("server.crt.loc");
            final String serverKeyFromWebSettings = webSettingsProp.getProperty("server.key.loc");
            String serverCrtFileName;
            if (serverCrtFromWebSettings != null && !"".equals(serverCrtFromWebSettings)) {
                serverCrtFileName = webServerConfDir + File.separator + serverCrtFromWebSettings;
            }
            else {
                serverCrtFileName = webServerConfDir + File.separator + webSettingsProp.getProperty("apache.crt.loc");
            }
            String serverKeyFileName;
            if (serverKeyFromWebSettings != null && !"".equals(serverKeyFromWebSettings)) {
                serverKeyFileName = webServerConfDir + File.separator + serverKeyFromWebSettings;
            }
            else {
                serverKeyFileName = webServerConfDir + File.separator + webSettingsProp.getProperty("apache.serverKey.loc");
            }
            final String interCertTempName = webSettingsProp.getProperty("apache.ssl.intermediate.ca.file");
            String interCertFileName = "";
            if (!interCertTempName.equals("")) {
                interCertFileName = webServerConfDir + File.separator + interCertTempName;
            }
            String rootCertFileName = "";
            if (!SSLCertificateUtil.getInstance().isThirdPartySSLInstalled() && new File(webServerConfDir + File.separator + "DMRootCA.crt").exists()) {
                rootCertFileName = webServerConfDir + File.separator + "DMRootCA.crt";
            }
            CertificateUtil.logger.log(Level.INFO, sourceMethod + "Generating Keystore for Server certificates and key.");
            KeyStore serverKeyStoreObj = null;
            if (password != null && aliasName != null) {
                serverKeyStoreObj = getInstance().getKeyStoreObject(serverCrtFileName, interCertFileName, rootCertFileName, serverKeyFileName, password, aliasName);
            }
            else {
                CertificateUtil.logger.log(Level.SEVERE, sourceMethod + "Password or Alias passed for Keystore is null !!");
            }
            if (serverKeyStoreObj != null) {
                CertificateUtil.logger.log(Level.INFO, sourceMethod + "KeyStore object created ... Saving keystore object to file.");
                createStatus = getInstance().saveKeyStoreObjectToFile(serverKeyStoreObj, keyStorePath, false, password);
                if (!createStatus) {
                    CertificateUtil.logger.log(Level.SEVERE, sourceMethod + "Saving Keystore to file Failed !!");
                }
            }
            else {
                CertificateUtil.logger.log(Level.SEVERE, sourceMethod + "KeyStore Object Creation failed.");
            }
        }
        catch (final Exception excep) {
            CertificateUtil.logger.log(Level.SEVERE, sourceMethod + "Exception while creating keystore for AWS Gateway - ", excep);
        }
        return createStatus;
    }
    
    public Boolean copyFileListToDestinationFolder(final List<String> filesToBeCopied, final String destPath) {
        Boolean copiedSuccessfully = Boolean.TRUE;
        try {
            for (final String fileName : filesToBeCopied) {
                final String backUpFileName = destPath + File.separator + fileName.substring(fileName.lastIndexOf(File.separator) + 1);
                copiedSuccessfully = CertificateUtil.fileAccessAPI.copyFile(fileName, backUpFileName);
                if (!copiedSuccessfully) {
                    return copiedSuccessfully;
                }
            }
        }
        catch (final Exception exp) {
            CertificateUtil.logger.log(Level.SEVERE, "Error in CertificateUtil.copyFileListToDestinationFolder {0}", exp);
            copiedSuccessfully = Boolean.FALSE;
        }
        return copiedSuccessfully;
    }
    
    static {
        CertificateUtil.sourceClass = "CertificateUtil";
        CertificateUtil.fileAccessAPI = ApiFactoryProvider.getFileAccessAPI();
        CertificateUtil.certificateUtil = null;
        CertificateUtil.logger = Logger.getLogger("ImportCertificateLogger");
        CertificateUtil.webServerLocation = System.getProperty("server.home") + File.separator + SSLCertificateUtil.webServerName;
    }
}
