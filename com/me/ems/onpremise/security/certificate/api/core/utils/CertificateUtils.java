package com.me.ems.onpremise.security.certificate.api.core.utils;

import java.security.Provider;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.security.Security;
import javax.security.auth.x500.X500Principal;
import java.security.Principal;
import java.util.StringTokenizer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import com.me.devicemanagement.onpremise.start.util.WebServerUtil;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Collection;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.nio.file.Files;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.security.InvalidAlgorithmParameterException;
import java.security.cert.TrustAnchor;
import java.security.cert.PKIXParameters;
import java.util.ArrayList;
import java.math.BigInteger;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import java.util.Enumeration;
import java.util.Iterator;
import java.security.KeyStore;
import java.io.FileNotFoundException;
import java.security.KeyStoreException;
import java.util.logging.Level;
import java.util.List;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import org.json.JSONObject;
import java.io.ByteArrayInputStream;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.bouncycastle.util.encoders.Hex;
import java.security.MessageDigest;
import com.me.devicemanagement.framework.server.util.ChecksumProvider;
import org.apache.commons.io.IOUtils;
import java.io.FileReader;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.security.spec.KeySpec;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.io.Reader;
import org.bouncycastle.util.io.pem.PemReader;
import java.security.PrivateKey;
import java.io.InputStreamReader;
import java.security.cert.Certificate;
import java.io.InputStream;
import java.io.FileInputStream;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateFactory;
import java.nio.file.Path;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.io.File;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessAPI;
import java.util.logging.Logger;

public class CertificateUtils
{
    private static Logger logger;
    private static final String IDENTIFY = "IsIdentity";
    private static final String CERTIFICATE_NAME = "CommonName";
    private static final String JAVA_KEY_STORE_PASSWORD = "changeit";
    private static final String JAVA_KEY_STORE_PATH;
    private static final String USER_KEY_STORE_PATH;
    private static final String USER_KEY_STORE_PASSWORD = "changeit-user";
    private static String webServerLocation;
    private static FileAccessAPI fileAccessAPI;
    
    public static X509Certificate loadX509CertificateFromFile(final File x509CertificateFile) throws IOException, CertificateNotYetValidException, CertificateExpiredException, CertificateException, Exception {
        final X509Certificate x509Certificate = null;
        return loadX509CertificateFromFile(x509CertificateFile, Boolean.TRUE);
    }
    
    public static X509Certificate loadX509CertificateFromFile(final Path x509CertificateFile) throws IOException, CertificateNotYetValidException, CertificateExpiredException, CertificateException, Exception {
        final X509Certificate x509Certificate = null;
        return loadX509CertificateFromFile(x509CertificateFile.toFile(), Boolean.TRUE);
    }
    
    public static X509Certificate loadX509CertificateFromFileWithoutValidation(final File x509CertificateFile) throws IOException, CertificateNotYetValidException, CertificateExpiredException, CertificateException, Exception {
        return loadX509CertificateFromFile(x509CertificateFile, Boolean.FALSE);
    }
    
    private static X509Certificate loadX509CertificateFromFile(final File x509CertificateFile, final Boolean performValidation) throws IOException, CertificateNotYetValidException, CertificateExpiredException, CertificateException, Exception {
        X509Certificate x509Certificate = null;
        final InputStream in = null;
        try {
            final String fileName = x509CertificateFile.getPath();
            if (!x509CertificateFile.exists()) {
                final String message = "The given file \"" + x509CertificateFile + "\" does not exist.";
                throw new IOException(message);
            }
            if (!x509CertificateFile.canRead()) {
                final String message = "The given file \"" + x509CertificateFile + "\" cannot be read.";
                throw new IOException(message);
            }
            CertificateFactory certificateFactory = null;
            try {
                certificateFactory = CertificateFactory.getInstance("X.509", "BC");
            }
            catch (final NoSuchProviderException e) {
                throw new RuntimeException("Certificate provider not found.", e);
            }
            final Certificate certificate = certificateFactory.generateCertificate(new FileInputStream(x509CertificateFile));
            if (certificate == null) {
                final String message2 = "The given file \"" + x509CertificateFile + "\" does not contain a X.509 certificate.";
                throw new CertificateException(message2);
            }
            if (!certificate.getType().equalsIgnoreCase("x.509")) {
                final String message2 = "The certificate contained in the given file \"" + x509CertificateFile + "\" is not a X.509 certificate.";
                throw new CertificateException(message2);
            }
            x509Certificate = (X509Certificate)certificate;
            if (performValidation) {
                x509Certificate.checkValidity();
            }
        }
        finally {
            if (in != null) {
                in.close();
            }
        }
        return x509Certificate;
    }
    
    public static PrivateKey loadPrivateKey(final InputStreamReader privateKeyFile) throws Exception {
        PemReader pemReader = null;
        PrivateKey caKey = null;
        try {
            pemReader = new PemReader((Reader)privateKeyFile);
            final PKCS8EncodedKeySpec caKeySpec = new PKCS8EncodedKeySpec(pemReader.readPemObject().getContent());
            final KeyFactory kf = KeyFactory.getInstance("RSA", "BC");
            caKey = kf.generatePrivate(caKeySpec);
        }
        finally {
            if (pemReader != null) {
                pemReader.close();
            }
        }
        return caKey;
    }
    
    public static PrivateKey loadPrivateKeyFromApiFactory(final File privateKeyFile) throws Exception {
        final InputStreamReader inputStreamReader = new InputStreamReader(ApiFactoryProvider.getFileAccessAPI().getInputStream(privateKeyFile.getPath()));
        return loadPrivateKey(inputStreamReader);
    }
    
    public static PrivateKey loadPrivateKeyFromFilePath(final Path privateKeyFilePath) throws Exception {
        final FileReader file = new FileReader(privateKeyFilePath.toFile());
        return loadPrivateKey(file);
    }
    
    public static X509Certificate loadX509CertificateFromApiFactory(final File certificateFile) throws Exception {
        String certificate = IOUtils.toString(ApiFactoryProvider.getFileAccessAPI().readFile(certificateFile.getPath()));
        if (certificate != null) {
            certificate = certificate.replace("-----BEGIN CERTIFICATE-----", "").replace("-----END CERTIFICATE-----", "");
        }
        return loadX509CertificateFromBuffer(certificate);
    }
    
    public static String getCertificateFingerPrint(final Path x509CertificateFile) throws Exception {
        final X509Certificate certificate = loadX509CertificateFromFile(x509CertificateFile);
        return getCertificateFingerPrint(certificate);
    }
    
    public static String getCertificateFingerPrint(final X509Certificate certificate) throws Exception {
        String fingerPrint = null;
        final MessageDigest md = MessageDigest.getInstance(ChecksumProvider.getSecurityAlgorithm2());
        final byte[] digest = md.digest(certificate.getEncoded());
        fingerPrint = new String(Hex.encode(digest));
        return fingerPrint;
    }
    
    public static X509Certificate loadX509CertificateFromBuffer(final String encodedCertificate) throws CertificateException {
        final CertificateFactory x509Factory = CertificateFactory.getInstance("X.509");
        final X509Certificate x509 = (X509Certificate)x509Factory.generateCertificate(new ByteArrayInputStream(Base64.decode(encodedCertificate)));
        return x509;
    }
    
    public static JSONObject parseX509Certificate(final String encodedCertificate) throws Exception {
        final JSONObject certificateDetails = new JSONObject();
        final X509Certificate x509 = loadX509CertificateFromBuffer(encodedCertificate);
        final String certName = x509.getSubjectDN().getName().split("=")[1];
        final int lastIndexOfComma = certName.lastIndexOf(",");
        certificateDetails.put("CommonName", (Object)((lastIndexOfComma != -1) ? certName.substring(0, lastIndexOfComma) : certName));
        certificateDetails.put("IsIdentity", (Object)String.valueOf(!isCertificateSelfSigned(x509)));
        certificateDetails.put("CERTIFICATE_EXPIRE", (Object)String.valueOf(x509.getNotAfter().getTime()));
        certificateDetails.put("CERTIFICATE_ISSUER_DN", (Object)x509.getIssuerDN().getName());
        certificateDetails.put("CERTIFICATE_SERIAL_NUMBER", (Object)x509.getSerialNumber().toString());
        certificateDetails.put("CERTIFICATE_SIGNATURE", (Object)x509.getSignature().toString());
        certificateDetails.put("CERTIFICATE_SUBJECT_DN", (Object)x509.getSubjectDN().getName());
        certificateDetails.put("CERTIFICATE_TYPE", (Object)"X.509");
        certificateDetails.put("CERTIFICATE_VERSION", (Object)String.valueOf(x509.getVersion()));
        certificateDetails.put("SIGNATURE_ALGORITHM_NAME", (Object)x509.getSigAlgName());
        certificateDetails.put("SIGNATURE_ALGORITHM_OID", (Object)x509.getSigAlgOID());
        return certificateDetails;
    }
    
    public static Boolean isCertificateSelfSigned(final X509Certificate cert) throws CertificateException, NoSuchAlgorithmException, NoSuchProviderException {
        try {
            final PublicKey key = cert.getPublicKey();
            cert.verify(key);
            return true;
        }
        catch (final SignatureException sigEx) {
            return false;
        }
        catch (final InvalidKeyException keyEx) {
            return false;
        }
    }
    
    public static Boolean verifyCertificateChainAgainstCACertsFile(final List<Certificate> certificateChain) {
        Boolean isVerified = Boolean.FALSE;
        KeyStore keyStore = null;
        try {
            if (new File(CertificateUtils.JAVA_KEY_STORE_PATH).exists()) {
                keyStore = loadAndReturnKeyStore(CertificateUtils.JAVA_KEY_STORE_PATH, "changeit", "JKS");
                for (final Certificate certificate : certificateChain) {
                    isVerified = verifyCertificateAgainstCACertsFile((X509Certificate)certificate, keyStore);
                    if (isVerified) {
                        break;
                    }
                }
            }
            if (!isVerified && new File(CertificateUtils.USER_KEY_STORE_PATH).exists()) {
                keyStore = loadAndReturnKeyStore(CertificateUtils.USER_KEY_STORE_PATH, "changeit-user", "JKS");
                for (final Certificate certificate : certificateChain) {
                    isVerified = verifyCertificateAgainstCACertsFile((X509Certificate)certificate, keyStore);
                    if (isVerified) {
                        break;
                    }
                }
            }
        }
        catch (final KeyStoreException ex) {
            Logger.getLogger(CertificateUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (final FileNotFoundException ex2) {
            Logger.getLogger(CertificateUtils.class.getName()).log(Level.SEVERE, null, ex2);
        }
        catch (final IOException ex3) {
            Logger.getLogger(CertificateUtils.class.getName()).log(Level.SEVERE, null, ex3);
        }
        catch (final NoSuchAlgorithmException ex4) {
            Logger.getLogger(CertificateUtils.class.getName()).log(Level.SEVERE, null, ex4);
        }
        catch (final CertificateException ex5) {
            Logger.getLogger(CertificateUtils.class.getName()).log(Level.SEVERE, null, ex5);
        }
        return isVerified;
    }
    
    private static KeyStore loadAndReturnKeyStore(final String keyStorePath, final String keyStorePass, final String storeType) throws KeyStoreException, FileNotFoundException, NoSuchAlgorithmException, CertificateException, IOException {
        FileInputStream fis = null;
        KeyStore keyStore = null;
        try {
            final char[] keyStorePassword = keyStorePass.toCharArray();
            keyStore = KeyStore.getInstance(storeType);
            fis = new FileInputStream(keyStorePath);
            keyStore.load(fis, keyStorePassword);
        }
        finally {
            if (fis != null) {
                try {
                    fis.close();
                }
                catch (final IOException ex) {
                    Logger.getLogger(CertificateUtils.class.getName()).log(Level.SEVERE, "Exception while closing file in loadAndReturnKeyStore", ex);
                }
            }
        }
        return keyStore;
    }
    
    private static Boolean verifyCertificateAgainstCACertsFile(final X509Certificate cert, final KeyStore keyStore) throws KeyStoreException {
        Boolean isVerified = Boolean.FALSE;
        final String subjectDN = cert.getSubjectDN().getName();
        final String issuerDN = cert.getIssuerDN().getName();
        final Enumeration en = keyStore.aliases();
        X509Certificate signingcert = null;
        while (en.hasMoreElements()) {
            X509Certificate storecert = null;
            final String ali = en.nextElement();
            if (keyStore.isCertificateEntry(ali)) {
                storecert = (X509Certificate)keyStore.getCertificate(ali);
                try {
                    cert.verify(storecert.getPublicKey());
                    Logger.getLogger(CertificateUtils.class.getName()).log(Level.INFO, "Signature verified on certificate");
                    signingcert = storecert;
                    isVerified = Boolean.TRUE;
                    break;
                }
                catch (final Exception ex) {}
            }
        }
        if (signingcert == null) {
            isVerified = Boolean.FALSE;
            Logger.getLogger(CertificateUtils.class.getName()).log(Level.SEVERE, "!! FAILED to find a signing certificate in keystore which matches or authenticates the certificate file");
        }
        return isVerified;
    }
    
    public static boolean isValidCertificateAndPrivateKey(final Certificate certificate, final PrivateKey privateKey) throws IOException {
        final RSAKeyParameters publicKeyParams = (RSAKeyParameters)PublicKeyFactory.createKey(certificate.getPublicKey().getEncoded());
        final BigInteger publicKeyModulus = publicKeyParams.getModulus();
        final RSAKeyParameters privKeyParams = (RSAKeyParameters)PrivateKeyFactory.createKey(privateKey.getEncoded());
        final BigInteger privateKeyModulus = privKeyParams.getModulus();
        return publicKeyModulus.equals(privateKeyModulus);
    }
    
    public static Certificate getRootCACertificateFromCertificates(final Certificate[] certificates) throws CertificateException, NoSuchAlgorithmException, NoSuchProviderException {
        Certificate rootCACert = null;
        for (final Certificate certificate : certificates) {
            final X509Certificate x509Certificate = (X509Certificate)certificate;
            if (isCertificateSelfSigned(x509Certificate)) {
                rootCACert = certificate;
                break;
            }
        }
        return rootCACert;
    }
    
    public static List<Certificate> getTrustedRootCACertificatesFromCACerts() throws FileNotFoundException, KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, InvalidAlgorithmParameterException {
        final List<Certificate> trustedRootCACertificates = new ArrayList<Certificate>();
        if (new File(CertificateUtils.JAVA_KEY_STORE_PATH).exists()) {
            final KeyStore keystore = loadAndReturnKeyStore(CertificateUtils.JAVA_KEY_STORE_PATH, "changeit", "JKS");
            final PKIXParameters params = new PKIXParameters(keystore);
            for (final TrustAnchor ta : params.getTrustAnchors()) {
                final X509Certificate cert = ta.getTrustedCert();
                trustedRootCACertificates.add(cert);
            }
        }
        if (new File(CertificateUtils.USER_KEY_STORE_PATH).exists()) {
            final KeyStore keystore = loadAndReturnKeyStore(CertificateUtils.USER_KEY_STORE_PATH, "changeit-user", "JKS");
            final PKIXParameters params = new PKIXParameters(keystore);
            for (final TrustAnchor ta : params.getTrustAnchors()) {
                final X509Certificate cert = ta.getTrustedCert();
                trustedRootCACertificates.add(cert);
            }
        }
        return trustedRootCACertificates;
    }
    
    public static List<Certificate> splitMultipleCertificatesInEachFileToCertificateList(final List<String> certificateFilePaths) throws Exception {
        final List<Certificate> certificateList = new ArrayList<Certificate>();
        for (int i = 0; i < certificateFilePaths.size(); ++i) {
            final String intermediatePath = certificateFilePaths.get(i);
            final byte[] contents = FileAccessUtil.getFileAsByteArray(intermediatePath);
            String contentString = new String(contents);
            if (contentString.indexOf("-----END CERTIFICATE-----") != contentString.lastIndexOf("-----END CERTIFICATE-----")) {
                contentString = contentString.trim();
                final String[] split;
                final String[] contentArr = split = contentString.split("-----END CERTIFICATE-----");
                for (String content : split) {
                    final String[] splitBeginCert = content.split("-----BEGIN CERTIFICATE-----");
                    content = ((splitBeginCert.length > 1) ? splitBeginCert[1] : splitBeginCert[0]);
                    content = content.replaceAll("-----END CERTIFICATE-----", "");
                    content = content.replaceAll("-----BEGIN CERTIFICATE-----", "");
                    certificateList.add(loadX509CertificateFromBuffer(content));
                }
            }
            else {
                final String[] splitBeginCert2 = contentString.split("-----BEGIN CERTIFICATE-----");
                contentString = ((splitBeginCert2.length > 1) ? splitBeginCert2[1] : splitBeginCert2[0]);
                contentString = contentString.replaceAll("-----END CERTIFICATE-----", "");
                contentString = contentString.replaceAll("-----BEGIN CERTIFICATE-----", "");
                certificateList.add(loadX509CertificateFromBuffer(contentString));
            }
        }
        return certificateList;
    }
    
    public static List<Path> splitMultipleCertificatesInEachFileToCertificateFileList(final List<Path> certificateFilePaths) throws Exception {
        final List<Path> tempUploadedIntermediateCertificate = new ArrayList<Path>();
        final Iterator intermediatePathItr = certificateFilePaths.iterator();
        while (intermediatePathItr.hasNext()) {
            final Path intermediatePath = intermediatePathItr.next();
            final Path uploadFilePath = intermediatePath.getParent();
            final byte[] contents = Files.readAllBytes(intermediatePath);
            String contentString = new String(contents);
            if (contentString.indexOf("-----END CERTIFICATE-----") != contentString.lastIndexOf("-----END CERTIFICATE-----")) {
                contentString = contentString.trim();
                final String[] split;
                final String[] contentArr = split = contentString.split("-----END CERTIFICATE-----");
                for (String content : split) {
                    final Path newFilePath = Paths.get(uploadFilePath + File.separator + "intermediate" + SyMUtil.getCurrentTimeInMillis() + ".crt", new String[0]);
                    final String[] splitBeginCert = content.split("-----BEGIN CERTIFICATE-----");
                    content = ((splitBeginCert.length > 1) ? splitBeginCert[1] : splitBeginCert[0]);
                    content = "-----BEGIN CERTIFICATE-----" + System.getProperty("line.separator") + content.trim() + System.getProperty("line.separator");
                    content += "-----END CERTIFICATE-----";
                    Files.write(newFilePath, content.getBytes(), new OpenOption[0]);
                    tempUploadedIntermediateCertificate.add(newFilePath);
                }
                intermediatePathItr.remove();
            }
        }
        certificateFilePaths.addAll(tempUploadedIntermediateCertificate);
        return certificateFilePaths;
    }
    
    public static Long getThirdPartyCertificateExpiryDaysRemaining() {
        CertificateUtils.logger.log(Level.INFO, "CERTIFICATE expiry check!!!");
        X509Certificate x509Certificate = null;
        Long noOfDays = -2L;
        try {
            if (SSLCertificateUtil.getInstance().isThirdPartySSLInstalled()) {
                x509Certificate = loadX509CertificateFromFile(new File(SSLCertificateUtil.getInstance().getServerCertificateFilePath()));
                Date expiryDate = x509Certificate.getNotAfter();
                Date todaysDate = new Date();
                CertificateUtils.logger.log(Level.INFO, "creation date!!!" + x509Certificate.getNotBefore());
                CertificateUtils.logger.log(Level.INFO, "expiry date!!!" + expiryDate);
                final SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
                expiryDate = sdf.parse(sdf.format(expiryDate));
                todaysDate = sdf.parse(sdf.format(todaysDate));
                if (expiryDate != null) {
                    noOfDays = expiryDate.getTime() - todaysDate.getTime();
                    noOfDays /= 86400000L;
                }
            }
        }
        catch (final CertificateExpiredException ex) {
            return -1L;
        }
        catch (final Exception e) {
            CertificateUtils.logger.log(Level.WARNING, "Exception in parsing date!!!");
        }
        return noOfDays;
    }
    
    public static String getExtension(final String certificateFile) {
        final String certificateName = getNameOfTheFile(certificateFile);
        if (certificateName.lastIndexOf(".") == -1 && certificateName.length() >= 5) {
            return certificateName.substring(certificateName.length() - 4, certificateName.length());
        }
        if (certificateName.contains(".")) {
            return certificateName.substring(certificateName.lastIndexOf("."));
        }
        return certificateName;
    }
    
    public static String getNameOfTheFile(final String serverCertificateFile) {
        return serverCertificateFile.substring(serverCertificateFile.lastIndexOf(File.separator) + 1);
    }
    
    public static String getServerCertificateWebSettingsFilePath() {
        String oldServerCrtFileFromProperty = null;
        try {
            final Properties webSettingsProp = WebServerUtil.getWebServerSettings();
            final String confDirectory = CertificateUtils.webServerLocation + File.separator + "conf";
            final String serverCrtFileLoc = webSettingsProp.getProperty("server.crt.loc");
            if (serverCrtFileLoc != null && !"".equals(serverCrtFileLoc)) {
                oldServerCrtFileFromProperty = confDirectory + File.separator + serverCrtFileLoc;
            }
            else {
                oldServerCrtFileFromProperty = confDirectory + File.separator + webSettingsProp.getProperty("apache.crt.loc");
            }
            if (checkExistenceOfFile(oldServerCrtFileFromProperty)) {
                return oldServerCrtFileFromProperty;
            }
            return null;
        }
        catch (final Exception ex) {
            Logger.getLogger("ImportCertificateLogger").log(Level.SEVERE, "Error while getting the Properties from the websettings.conf file", ex);
            return oldServerCrtFileFromProperty;
        }
    }
    
    public static boolean checkExistenceOfFile(final String file) {
        final String sourceMethod = "checkExistenceOFFile";
        try {
            return !CertificateUtils.fileAccessAPI.isDirectory(file) && CertificateUtils.fileAccessAPI.isFileExists(file);
        }
        catch (final Exception ex) {
            Logger.getLogger("ImportCertificateLogger").log(Level.SEVERE, "Exception in checking existence of the file " + file + " failed..file may be in use/not available", ex);
            return false;
        }
    }
    
    public static Map getCertificateDetails(final String certificateFilePath) {
        final Map certificateDetails = new HashMap();
        final X509Certificate cert = generateCertificateFromFile(certificateFilePath);
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
    
    public static X509Certificate generateCertificateFromFile(final String serverCertificateFile) {
        final String sourceMethod = "generateCertificateFromFile";
        X509Certificate cert = null;
        InputStream in = null;
        try {
            final byte[] certificateBytes = CertificateUtils.fileAccessAPI.readFileContentAsArray(serverCertificateFile);
            final CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            in = new ByteArrayInputStream(certificateBytes);
            cert = (X509Certificate)certFactory.generateCertificate(in);
        }
        catch (final CertificateException ex) {
            CertificateUtils.logger.log(Level.SEVERE, "Tool doesn't support this certificate..Given certificate is not proper/corrupted..Certificate generation failed..", ex);
        }
        catch (final IOException ex2) {
            CertificateUtils.logger.log(Level.SEVERE, "Reading the file " + serverCertificateFile + " failed", ex2);
        }
        catch (final Exception ex3) {
            CertificateUtils.logger.log(Level.SEVERE, "Couldn't read the file " + serverCertificateFile + ".. it may be in use/not accessible.." + "Certificate generation failed..", ex3);
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (final Exception ex4) {
                CertificateUtils.logger.log(Level.INFO, "Exceptin in closing the stream..you canignore this..");
            }
        }
        return cert;
    }
    
    public static void deleteUploadDirectory() {
        try {
            CertificateUtils.fileAccessAPI.deleteDirectory(CertificateUtils.webServerLocation + File.separator + "conf" + File.separator + "uploaded_files");
        }
        catch (final Exception ex) {
            CertificateUtils.logger.log(Level.SEVERE, "uploaded directory deletion failed..", ex);
        }
    }
    
    static {
        CertificateUtils.logger = Logger.getLogger("ImportCertificateLogger");
        JAVA_KEY_STORE_PATH = System.getProperty("java.home") + File.separator + "lib" + File.separator + "security" + File.separator + "cacerts";
        USER_KEY_STORE_PATH = System.getProperty("server.home") + File.separator + "conf" + File.separator + "cacerts" + File.separator + "cacerts-user";
        try {
            CertificateUtils.webServerLocation = System.getProperty("server.home") + File.separator + WebServerUtil.getWebServerName();
        }
        catch (final Exception ex) {}
        CertificateUtils.fileAccessAPI = ApiFactoryProvider.getFileAccessAPI();
        if (Security.getProvider("BC") == null) {
            Security.addProvider((Provider)new BouncyCastleProvider());
        }
    }
}
