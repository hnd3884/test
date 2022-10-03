package com.me.mdm.certificate;

import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.certificate.SSLCertificateUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.FileNotFoundException;
import org.apache.commons.codec.binary.Base64;
import java.io.FileOutputStream;
import java.security.cert.CertificateEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.KeyFactory;
import java.io.DataInputStream;
import java.security.PrivateKey;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.security.cert.X509Certificate;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMiOSEntrollmentUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Logger;

public class CertificateHandler
{
    public Logger logger;
    private static CertificateHandler certHandler;
    private static String fs;
    
    public CertificateHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public static CertificateHandler getInstance() {
        if (CertificateHandler.certHandler == null) {
            CertificateHandler.certHandler = new CertificateHandler();
        }
        return CertificateHandler.certHandler;
    }
    
    public void generateCertificateAuthority() {
        try {
            final String SERVER_HOME = MDMUtil.getInstallationDir();
            final String SERVER_FQDN = MDMiOSEntrollmentUtil.getInstance().getServerBaseURL();
            final String CA_DIR = SERVER_HOME + CertificateHandler.fs + "mdm" + CertificateHandler.fs + "CA";
            final String sOpenSSLPath = SERVER_HOME + CertificateHandler.fs + "apache" + CertificateHandler.fs + "bin" + CertificateHandler.fs + "openssl.exe";
            final String sOPenSSLConfigPath = SERVER_HOME + CertificateHandler.fs + "apache" + CertificateHandler.fs + "conf" + CertificateHandler.fs + "openssl.cnf";
            final String sCA_Key = CA_DIR + CertificateHandler.fs + "cakey.pem";
            final String sCA_Req = CA_DIR + CertificateHandler.fs + "careq.pem";
            final String sCA_Cert = CA_DIR + CertificateHandler.fs + "cacert.pem";
            final String subjStr = "\"/OU=ManageEngine/C=US/ST=CA/L=Pleasanton/O=Zoho Corporation/CN=" + SERVER_FQDN + "\"";
            final String sKeyPairCommand = sOpenSSLPath + " req -nodes -days 3650 -newkey rsa:2048 -keyout " + sCA_Key + " -out " + sCA_Req + " -config " + sOPenSSLConfigPath + " -subj " + subjStr;
            this.logger.log(Level.INFO, "sKeyPairCommand :{0}", sKeyPairCommand);
            final Process processKeyPair = Runtime.getRuntime().exec(sKeyPairCommand);
            final String sSelfSignCommand = sOpenSSLPath + " ca -create_serial -out " + sCA_Cert + " -days 3650 -keyfile " + sCA_Key + " -selfsign -extensions v3_ca " + "-config " + sOPenSSLConfigPath + " -infiles " + sCA_Req;
            this.logger.log(Level.INFO, "sSelfSignCommand : {0}", sSelfSignCommand);
            Runtime.getRuntime().exec(sKeyPairCommand);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void generateServerCertificate() {
        try {
            final String SERVER_HOME = SyMUtil.getInstallationDir();
            final String CA_DIR = SERVER_HOME + CertificateHandler.fs + "mdm" + CertificateHandler.fs + "certificate" + CertificateHandler.fs + "CA";
            final String SERVER_CERT_DIR = SERVER_HOME + CertificateHandler.fs + "mdm" + CertificateHandler.fs + "certificate" + CertificateHandler.fs + "ServerCert";
            final String sKeyStoreFile = SERVER_CERT_DIR + CertificateHandler.fs + "serverKeyStore";
            final String sCsrFile = SERVER_CERT_DIR + CertificateHandler.fs + "server.csr";
            final String serverCerPath = SERVER_CERT_DIR + CertificateHandler.fs + "server.cer";
            final String sCA_Key = CA_DIR + CertificateHandler.fs + "cakey.pem";
            final String sCA_Cert = CA_DIR + CertificateHandler.fs + "cacert.pem";
            final String sOpenSSLPath = SERVER_HOME + CertificateHandler.fs + "apache" + CertificateHandler.fs + "bin" + CertificateHandler.fs + "openssl.exe";
            final String serverKeyStore = SERVER_HOME + CertificateHandler.fs + "jre" + CertificateHandler.fs + "bin" + CertificateHandler.fs + "keytool.exe -genkey -alias server -keyalg RSA -keystore " + sKeyStoreFile + " -keysize 2048 -storepass Vembu123 -keypass Vembu123 -sigalg SHA1withRSA -dname \"CN=dc-w8-e6420.csez.zohocorpin.com, OU=ME, O=ZOHOCORP, L=CHENNAI, S=IN, C=IN\"";
            this.logger.log(Level.INFO, "serverKeyStore : {0}", serverKeyStore);
            final Process process = Runtime.getRuntime().exec(serverKeyStore);
            Thread.sleep(3000L);
            final String generateCSR = SERVER_HOME + CertificateHandler.fs + "jre" + CertificateHandler.fs + "bin" + CertificateHandler.fs + "keytool.exe" + " -keystore " + sKeyStoreFile + " -certreq -alias server -keyalg RSA -file " + sCsrFile + " -storepass Vembu123 -keysize 2048 -sigalg SHA1withRSA";
            this.logger.log(Level.INFO, "generateCSR : {0}", generateCSR);
            final Process processCSR = Runtime.getRuntime().exec(generateCSR);
            Thread.sleep(3000L);
            final String geenrateServerCert = sOpenSSLPath + " x509 -req -CA " + sCA_Cert + " -CAkey " + sCA_Key + " -in " + sCsrFile + " -out " + serverCerPath + " -days 3650 -CAcreateserial";
            this.logger.log(Level.INFO, "geenrateServerCert : {0}", geenrateServerCert);
            final Process processServerCert = Runtime.getRuntime().exec(geenrateServerCert);
            Thread.sleep(3000L);
            final String importCAToServerKeyStore = SERVER_HOME + CertificateHandler.fs + "jre" + CertificateHandler.fs + "bin" + CertificateHandler.fs + "keytool.exe" + " -import -keystore " + sKeyStoreFile + " -file " + sCA_Cert + " -storepass Vembu123 -alias theCARoot";
            this.logger.log(Level.INFO, "importCAToServerKeyStore : {0}", importCAToServerKeyStore);
            final Process processImportCA = Runtime.getRuntime().exec(importCAToServerKeyStore);
            Thread.sleep(3000L);
            final String importSignedCertToKeyStore = SERVER_HOME + CertificateHandler.fs + "jre" + CertificateHandler.fs + "bin" + CertificateHandler.fs + "keytool.exe" + " -import -keystore " + sKeyStoreFile + " -file " + serverCerPath + " -storepass Vembu123 -alias server";
            this.logger.log(Level.INFO, "importSignedCertToKeyStore : {0}", importSignedCertToKeyStore);
            Runtime.getRuntime().exec(importSignedCertToKeyStore);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public X509Certificate getRootCACertificate(final String sFilePath) throws Exception {
        X509Certificate rootCertificate = null;
        FileInputStream fis = null;
        ByteArrayInputStream bais = null;
        CertificateFactory certFactory = null;
        try {
            fis = new FileInputStream(new File(sFilePath));
            if (fis != null) {
                final byte[] value = new byte[fis.available()];
                fis.read(value);
                bais = new ByteArrayInputStream(value);
                certFactory = CertificateFactory.getInstance("X.509");
            }
            rootCertificate = (X509Certificate)certFactory.generateCertificate(bais);
        }
        finally {
            fis.close();
            bais.close();
        }
        return rootCertificate;
    }
    
    public PrivateKey getPrivateKey(final String filename) throws Exception {
        final File f = new File(filename);
        final FileInputStream fis = new FileInputStream(f);
        final DataInputStream dis = new DataInputStream(fis);
        final KeyFactory kf = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec spec = null;
        try {
            final byte[] keyBytes = new byte[(int)f.length()];
            dis.readFully(keyBytes);
            final String temp = new String(keyBytes);
            String privKeyPEM = temp.replace("-----BEGIN PRIVATE KEY-----\n", "");
            privKeyPEM = privKeyPEM.replace("-----END PRIVATE KEY-----", "");
            spec = new PKCS8EncodedKeySpec(keyBytes);
        }
        finally {
            fis.close();
            dis.close();
        }
        return kf.generatePrivate(spec);
    }
    
    public String getFingerPrint(final X509Certificate cert) throws NoSuchAlgorithmException, CertificateEncodingException {
        final MessageDigest md = MessageDigest.getInstance("SHA-256");
        final byte[] der = cert.getEncoded();
        md.update(der);
        final byte[] digest = md.digest();
        return this.hexify(digest);
    }
    
    private String hexify(final byte[] bytes) {
        final char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        final StringBuffer buf = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; ++i) {
            buf.append(hexDigits[(bytes[i] & 0xF0) >> 4]);
            buf.append(hexDigits[bytes[i] & 0xF]);
        }
        return buf.toString();
    }
    
    public void writeCertificateFile(final String certFileName, final X509Certificate signedCert) throws FileNotFoundException, UnsupportedEncodingException, IOException, CertificateEncodingException {
        final FileOutputStream os = new FileOutputStream(certFileName);
        try {
            os.write("-----BEGIN CERTIFICATE-----\n".getBytes("US-ASCII"));
            os.write(Base64.encodeBase64(signedCert.getEncoded(), true));
            os.write("-----END CERTIFICATE-----\n".getBytes("US-ASCII"));
        }
        finally {
            os.close();
        }
    }
    
    public Boolean isSANOrEnterpriseCACertificate() throws Exception {
        final int certificateType = ApiFactoryProvider.getServerSettingsAPI().getCertificateType();
        if (certificateType == 4 || certificateType == 3) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    
    public X509Certificate getAppropriateCertificate() {
        X509Certificate certificate = null;
        try {
            if (ApiFactoryProvider.getServerSettingsAPI().getCertificateType() == 1) {
                final String CertificateFilePath = SSLCertificateUtil.getInstance().getServerCertificateFilePath();
                certificate = SSLCertificateUtil.getCertificate(CertificateFilePath);
            }
            else if (this.isSANOrEnterpriseCACertificate()) {
                final String CertificateFilePath = SSLCertificateUtil.getInstance().getServerCACertificateFilePath();
                certificate = SSLCertificateUtil.getCertificate(CertificateFilePath);
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(CertificateHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return certificate;
    }
    
    public HashMap<String, X509Certificate> getAllCertificates() {
        final HashMap<String, X509Certificate> certificateMap = new HashMap<String, X509Certificate>();
        certificateMap.put("ServerCertificate", this.getServerCertificate());
        certificateMap.put("RootCertificate", this.getServerCACertificate());
        final X509Certificate serverCert = this.getIntermediateCertificate();
        if (serverCert != null) {
            certificateMap.put("IntermediateCertificate", serverCert);
        }
        return certificateMap;
    }
    
    public X509Certificate getServerCACertificate() {
        X509Certificate certificate = null;
        try {
            final String certificateFilePath = SSLCertificateUtil.getInstance().getServerCACertificateFilePath();
            certificate = SSLCertificateUtil.getCertificate(certificateFilePath);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Cannot fetch server certificate {0}", exp.toString());
        }
        return certificate;
    }
    
    public X509Certificate getServerCertificate() {
        X509Certificate certificate = null;
        try {
            final String certificateFilePath = SSLCertificateUtil.getInstance().getServerCertificateFilePath();
            certificate = SSLCertificateUtil.getCertificate(certificateFilePath);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Cannot fetch server certificate {0}", exp.toString());
        }
        return certificate;
    }
    
    public X509Certificate getIntermediateCertificate() {
        X509Certificate certificate = null;
        try {
            final String certificateFilePath = SSLCertificateUtil.getInstance().getIntermediateCertificateFilePath();
            certificate = SSLCertificateUtil.getCertificate(certificateFilePath);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Cannot fetch server certificate {0}", exp.toString());
        }
        return certificate;
    }
    
    public ArrayList<X509Certificate> splitIntermediateCertificate(final X509Certificate intermediateCert) {
        final ArrayList<X509Certificate> certificateChain = new ArrayList<X509Certificate>();
        if (intermediateCert != null) {
            certificateChain.addAll(Arrays.asList(intermediateCert));
        }
        return certificateChain;
    }
    
    static {
        CertificateHandler.certHandler = null;
        CertificateHandler.fs = File.separator;
    }
}
