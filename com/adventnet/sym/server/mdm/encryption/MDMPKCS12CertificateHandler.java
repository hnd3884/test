package com.adventnet.sym.server.mdm.encryption;

import java.security.Provider;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import com.adventnet.sym.server.mdm.config.ProfileCertificateUtil;
import com.me.mdm.files.MDMFileUtil;
import com.me.mdm.server.util.UploadUtil;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.security.KeyStoreException;
import org.json.JSONException;
import java.security.cert.Certificate;
import java.security.Key;
import com.adventnet.sym.server.mdm.certificates.CertificateUtil;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.NoSuchProviderException;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.operator.OperatorCreationException;
import java.security.NoSuchAlgorithmException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.ContentSigner;
import java.security.KeyPair;
import java.util.Date;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import java.security.SecureRandom;
import java.security.KeyPairGenerator;
import java.util.Calendar;
import java.math.BigInteger;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public class MDMPKCS12CertificateHandler
{
    private static MDMPKCS12CertificateHandler mdmPkcs12CertificateHandler;
    public static Logger logger;
    
    private MDMPKCS12CertificateHandler() {
    }
    
    public static MDMPKCS12CertificateHandler getInstance() {
        if (MDMPKCS12CertificateHandler.mdmPkcs12CertificateHandler == null) {
            MDMPKCS12CertificateHandler.mdmPkcs12CertificateHandler = new MDMPKCS12CertificateHandler();
        }
        return MDMPKCS12CertificateHandler.mdmPkcs12CertificateHandler;
    }
    
    private JSONObject generateIdentityCertificate(final JSONObject requestObj) throws NoSuchAlgorithmException, OperatorCreationException, CertIOException, NoSuchProviderException, CertificateException, IOException, Exception {
        MDMPKCS12CertificateHandler.logger.log(Level.INFO, "FileVaultLog: generateIdentityCertificate() Going to Public , Private key pair for personal recovery key.....");
        final String issueToCN = requestObj.optString("ISSUED_TO_CN", "Encryption Certificate");
        final String issuedFromCN = requestObj.optString("ISSUED_FROM_CN", "MDM");
        final X500NameBuilder builderex = new X500NameBuilder(BCStyle.INSTANCE);
        builderex.addRDN(BCStyle.CN, issueToCN);
        final X500NameBuilder issuerBuilder = new X500NameBuilder(BCStyle.INSTANCE);
        issuerBuilder.addRDN(BCStyle.CN, issuedFromCN);
        X509Certificate rootCA = null;
        final BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());
        final Calendar cal = Calendar.getInstance();
        final Date notBefore = cal.getTime();
        cal.add(1, 25);
        final Date notAfter = cal.getTime();
        final KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", "BC");
        kpGen.initialize(2048, new SecureRandom());
        final KeyPair keyPair = kpGen.generateKeyPair();
        JcaX509v3CertificateBuilder builder = null;
        builder = new JcaX509v3CertificateBuilder(issuerBuilder.build(), serialNumber, notBefore, notAfter, builderex.build(), keyPair.getPublic());
        final ContentSigner contentSigner = new JcaContentSignerBuilder("SHA256WithRSA").setProvider("BC").build(keyPair.getPrivate());
        builder.addExtension(Extension.basicConstraints, true, (ASN1Encodable)new BasicConstraints(false));
        final KeyUsage keyUsage = new KeyUsage(180);
        builder.addExtension(Extension.keyUsage, false, (ASN1Encodable)keyUsage);
        final X509CertificateHolder holder = builder.build(contentSigner);
        rootCA = (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(holder.getEncoded()));
        requestObj.put("CERTIFICATE", (Object)rootCA);
        requestObj.put("CERTIFICATE_PRIVATE", (Object)keyPair.getPrivate());
        requestObj.put("CERTIFICATE_PUBLIC", (Object)keyPair.getPublic());
        MDMPKCS12CertificateHandler.logger.log(Level.INFO, "FileVaultLog: generateIdentityCertificate()  Public/Private key par created created successfully.....");
        return requestObj;
    }
    
    private JSONObject generatePKCS12Certificate(JSONObject requestJSON) {
        try {
            MDMPKCS12CertificateHandler.logger.log(Level.INFO, "FileVaultLog: generatePKCS12Certificate()  Going to create keystore for .p12 certificate.....");
            requestJSON = this.generateIdentityCertificate(requestJSON);
            final X509Certificate signedCertificate = (X509Certificate)requestJSON.get("CERTIFICATE");
            final PrivateKey privateKey = (PrivateKey)requestJSON.get("CERTIFICATE_PRIVATE");
            final KeyStore store = KeyStore.getInstance("PKCS12", "BC");
            final String certificateName = requestJSON.optString("CERTIFICATE_NAME", "IdentityCertificate.p12");
            final String exportPassword = CertificateUtil.getInstance().generateRandomCertPassword(10);
            store.load(null, null);
            final X509Certificate[] chain = { signedCertificate };
            store.setKeyEntry(certificateName, privateKey, exportPassword.toCharArray(), chain);
            requestJSON.put("CERTIFICATE_PASSWORD", (Object)exportPassword);
            requestJSON.put("CERTIFICATE_KEYSTORE", (Object)store);
        }
        catch (final JSONException ex) {
            MDMPKCS12CertificateHandler.logger.log(Level.SEVERE, "FileVaultLog:Exception in generatePKCS12Certificate() ", (Throwable)ex);
        }
        catch (final KeyStoreException ex2) {
            MDMPKCS12CertificateHandler.logger.log(Level.SEVERE, "FileVaultLog:Exception in generatePKCS12Certificate() ", ex2);
        }
        catch (final NoSuchProviderException ex3) {
            MDMPKCS12CertificateHandler.logger.log(Level.SEVERE, "FileVaultLog:Exception in generatePKCS12Certificate() ", ex3);
        }
        catch (final IOException ex4) {
            MDMPKCS12CertificateHandler.logger.log(Level.SEVERE, "FileVaultLog:Exception in generatePKCS12Certificate() ", ex4);
        }
        catch (final NoSuchAlgorithmException ex5) {
            MDMPKCS12CertificateHandler.logger.log(Level.SEVERE, "FileVaultLog:Exception in generatePKCS12Certificate() ", ex5);
        }
        catch (final CertificateException ex6) {
            MDMPKCS12CertificateHandler.logger.log(Level.SEVERE, "FileVaultLog:Exception in generatePKCS12Certificate() ", ex6);
        }
        catch (final Exception ex7) {
            MDMPKCS12CertificateHandler.logger.log(Level.SEVERE, "FileVaultLog:Exception in generatePKCS12Certificate() ", ex7);
        }
        MDMPKCS12CertificateHandler.logger.log(Level.INFO, "FileVaultLog: generatePKCS12Certificate() keystore created successfully...");
        return requestJSON;
    }
    
    private Long addCertificateIntoCertificateRepository(JSONObject obj) throws Exception {
        FileOutputStream fout = null;
        File tempFile = null;
        try {
            MDMPKCS12CertificateHandler.logger.log(Level.INFO, "FileVaultLog: addCertificateIntoCertificateRepository()  Going to create identity certificate (.p12) and add in certificate repository.....");
            obj = this.generatePKCS12Certificate(obj);
            tempFile = File.createTempFile("identity", ".p12");
            fout = new FileOutputStream(tempFile);
            final KeyStore keyStore = (KeyStore)obj.get("CERTIFICATE_KEYSTORE");
            final String password = obj.getString("CERTIFICATE_PASSWORD");
            final int certType = obj.getInt("CERTIFICATE_TYPE");
            final Long customerID = obj.getLong("CUSTOMER_ID");
            keyStore.store(fout, password.toCharArray());
            MDMPKCS12CertificateHandler.logger.log(Level.INFO, "FileVaultLog: Keystore created , going to create a temp file to convert as stream....");
            final String folderPath = new UploadUtil().getDestinationPath(tempFile.getName());
            final boolean fileUploaded = MDMFileUtil.uploadFileToDirectory(tempFile, folderPath, tempFile.getName());
            if (!fileUploaded) {
                MDMPKCS12CertificateHandler.logger.log(Level.SEVERE, "Problem in uploading the file");
                throw new Exception("Uploading failed");
            }
            final String filePath = folderPath + File.separator + tempFile.getName();
            final JSONObject addCertJSON = new JSONObject();
            addCertJSON.put("CERTIFICATE_TYPE", certType);
            addCertJSON.put("CUSTOMER_ID", (Object)customerID);
            addCertJSON.put("CERTIFICATE_FILE_UPLOAD", (Object)filePath);
            addCertJSON.put("CERTIFICATE_PASSWORD", (Object)password);
            MDMPKCS12CertificateHandler.logger.log(Level.INFO, "FileVaultLog: Temp file created successfully !");
            final JSONObject certResJSON = ProfileCertificateUtil.addCredentials(addCertJSON);
            if (certResJSON != null && certResJSON.has("CERTIFICATE_ID")) {
                MDMPKCS12CertificateHandler.logger.log(Level.INFO, "FileVaultLog: addCertificateIntoCertificateRepository()  Identity certificate (.p12) created and added in certificate repository successfully.....");
                return certResJSON.getLong("CERTIFICATE_ID");
            }
            return null;
        }
        catch (final IOException ex) {
            MDMPKCS12CertificateHandler.logger.log(Level.SEVERE, "FileVaultLog:Exception in addCertificateIntoCertificateRepository() ", ex);
        }
        catch (final JSONException ex2) {
            MDMPKCS12CertificateHandler.logger.log(Level.SEVERE, "FileVaultLog:Exception in addCertificateIntoCertificateRepository() ", (Throwable)ex2);
        }
        catch (final KeyStoreException ex3) {
            MDMPKCS12CertificateHandler.logger.log(Level.SEVERE, "FileVaultLog:Exception in addCertificateIntoCertificateRepository() ", ex3);
        }
        catch (final NoSuchAlgorithmException ex4) {
            MDMPKCS12CertificateHandler.logger.log(Level.SEVERE, "FileVaultLog:Exception in addCertificateIntoCertificateRepository() ", ex4);
        }
        catch (final CertificateException ex5) {
            MDMPKCS12CertificateHandler.logger.log(Level.SEVERE, "FileVaultLog:Exception in addCertificateIntoCertificateRepository() ", ex5);
        }
        catch (final Exception e) {
            MDMPKCS12CertificateHandler.logger.log(Level.SEVERE, "Exception While saving the temporary path in filevault", e);
            throw e;
        }
        finally {
            if (fout != null) {
                try {
                    fout.close();
                }
                catch (final IOException ex6) {
                    MDMPKCS12CertificateHandler.logger.log(Level.SEVERE, "FileVaultLog:Exception in addCertificateIntoCertificateRepository() ", ex6);
                }
            }
            if (tempFile != null) {
                tempFile.delete();
            }
        }
        return null;
    }
    
    public Long addNewFileVaultPersonalRecoveryKeyToCertificates(final Long customerID) throws Exception {
        try {
            final JSONObject requestJSON = new JSONObject();
            requestJSON.put("CERTIFICATE_TYPE", 2);
            requestJSON.put("CUSTOMER_ID", (Object)customerID);
            requestJSON.put("ISSUED_TO_CN", (Object)"MDM File Vault Escrow Certificate");
            requestJSON.put("ISSUED_FROM_CN", (Object)"Mobile device Manager Plus");
            requestJSON.put("CERTIFICATE_NAME", (Object)"FileVault_Personal_Escrow.p12");
            MDMPKCS12CertificateHandler.logger.log(Level.INFO, "FileVaultLog: Going to create personal key recovery identity certificate for request {0}", requestJSON);
            return this.addCertificateIntoCertificateRepository(requestJSON);
        }
        catch (final JSONException ex) {
            MDMPKCS12CertificateHandler.logger.log(Level.SEVERE, "FileVaultLog:Exception in addNewFileVaultPersonalRecoveryKeyToCertificates() ", (Throwable)ex);
            return null;
        }
    }
    
    public void createSupervisionIdentityCertificate(final Long customerID) throws Exception {
        try {
            final JSONObject requestJSON = new JSONObject();
            requestJSON.put("CERTIFICATE_TYPE", 6);
            requestJSON.put("CUSTOMER_ID", (Object)customerID);
            requestJSON.put("ISSUED_TO_CN", (Object)"MDM");
            requestJSON.put("ISSUED_FROM_CN", (Object)"MDM");
            requestJSON.put("CERTIFICATE_NAME", (Object)"mdm_supervision_identity.p12");
            MDMPKCS12CertificateHandler.logger.log(Level.INFO, "getCertificateIdFromSupervisionIdentity: Going to create certificate for request {0}", requestJSON);
            this.addCertificateIntoCertificateRepository(requestJSON);
        }
        catch (final Exception ex) {
            MDMPKCS12CertificateHandler.logger.log(Level.SEVERE, "getCertificateIdFromSupervisionIdentity:Exception in addNewSupervisionIdentityToCertificates() ", ex);
            throw ex;
        }
    }
    
    static {
        MDMPKCS12CertificateHandler.mdmPkcs12CertificateHandler = null;
        Security.addProvider((Provider)new BouncyCastleProvider());
        MDMPKCS12CertificateHandler.logger = Logger.getLogger("MDMConfigLogger");
    }
}
