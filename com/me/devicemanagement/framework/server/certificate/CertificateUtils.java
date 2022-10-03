package com.me.devicemanagement.framework.server.certificate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.Writer;
import java.io.OutputStream;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.operator.ContentSigner;
import java.security.KeyPair;
import org.bouncycastle.openssl.PEMWriter;
import java.io.OutputStreamWriter;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import java.util.Calendar;
import java.security.SecureRandom;
import java.security.KeyPairGenerator;
import java.security.Provider;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Collection;
import com.me.devicemanagement.framework.server.util.SyMUtil;
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
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.io.File;
import java.util.logging.Logger;

public class CertificateUtils
{
    private static Logger out;
    private static final String IDENTIFY = "IsIdentity";
    private static final String CERTIFICATE_NAME = "CommonName";
    private static final String JAVA_KEY_STORE_PASSWORD = "changeit";
    private static final String JAVA_KEY_STORE_PATH;
    private static final String USER_KEY_STORE_PATH;
    private static final String USER_KEY_STORE_PASSWORD = "changeit-user";
    
    public static X509Certificate loadX509CertificateFromFile(final File x509CertificateFile) throws IOException, CertificateNotYetValidException, CertificateExpiredException, CertificateException, Exception {
        final X509Certificate x509Certificate = null;
        return loadX509CertificateFromFile(x509CertificateFile, Boolean.TRUE);
    }
    
    public static X509Certificate loadX509CertificateFromFileWithoutValidation(final File x509CertificateFile) throws IOException, CertificateNotYetValidException, CertificateExpiredException, CertificateException, Exception {
        return loadX509CertificateFromFile(x509CertificateFile, Boolean.FALSE);
    }
    
    private static X509Certificate loadX509CertificateFromFile(final File x509CertificateFile, final Boolean performValidation) throws IOException, CertificateNotYetValidException, CertificateExpiredException, CertificateException, Exception {
        X509Certificate x509Certificate = null;
        final InputStream in = null;
        FileInputStream fin = null;
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
            fin = new FileInputStream(x509CertificateFile);
            final Certificate certificate = certificateFactory.generateCertificate(fin);
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
            if (fin != null) {
                fin.close();
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
    
    public static PrivateKey loadPrivateKeyFromFile(final File privateKeyFile) throws Exception {
        final FileReader file = new FileReader(privateKeyFile);
        return loadPrivateKey(file);
    }
    
    public static X509Certificate loadX509CertificateFromApiFactory(final File certificateFile) throws Exception {
        String certificate = IOUtils.toString(ApiFactoryProvider.getFileAccessAPI().readFile(certificateFile.getPath()));
        if (certificate != null) {
            certificate = certificate.substring(0, certificate.indexOf("-----END CERTIFICATE-----"));
            certificate = certificate.replace("-----BEGIN CERTIFICATE-----", "");
        }
        return loadX509CertificateFromBuffer(certificate);
    }
    
    public static String getCertificateFingerPrint(final File x509CertificateFile) throws Exception {
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
    
    public static List<String> splitMultipleCertificatesInEachFileToCertificateFileList(final List<String> certificateFilePaths, final String uploadFilePath) throws Exception {
        final List<String> tempUploadedIntermediateCertificate = new ArrayList<String>();
        for (int i = 0; i < certificateFilePaths.size(); ++i) {
            final String intermediatePath = certificateFilePaths.get(i);
            final byte[] contents = ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(intermediatePath);
            String contentString = new String(contents);
            if (contentString.indexOf("-----END CERTIFICATE-----") != contentString.lastIndexOf("-----END CERTIFICATE-----")) {
                contentString = contentString.trim();
                final String[] split;
                final String[] contentArr = split = contentString.split("-----END CERTIFICATE-----");
                for (String content : split) {
                    final String newFileName = uploadFilePath + File.separator + "intermediate" + SyMUtil.getCurrentTimeInMillis() + ".crt";
                    final String[] splitBeginCert = content.split("-----BEGIN CERTIFICATE-----");
                    content = ((splitBeginCert.length > 1) ? splitBeginCert[1] : splitBeginCert[0]);
                    content = "-----BEGIN CERTIFICATE-----" + System.getProperty("line.separator") + content.trim() + System.getProperty("line.separator");
                    content += "-----END CERTIFICATE-----";
                    ApiFactoryProvider.getFileAccessAPI().writeFile(newFileName, content.getBytes());
                    tempUploadedIntermediateCertificate.add(newFileName);
                }
                certificateFilePaths.remove(i);
                --i;
            }
        }
        certificateFilePaths.addAll(tempUploadedIntermediateCertificate);
        return certificateFilePaths;
    }
    
    public static Long getThirdPartyCertificateExpiryDaysRemaining() {
        CertificateUtils.out.log(Level.INFO, "CERTIFICATE expiry check!!!");
        X509Certificate x509Certificate = null;
        Long noOfDays = -2L;
        try {
            if (SSLCertificateUtil.getInstance().isThirdPartySSLInstalled()) {
                x509Certificate = loadX509CertificateFromFile(new File(SSLCertificateUtil.getInstance().getServerCertificateFilePath()));
                Date expiryDate = x509Certificate.getNotAfter();
                Date todaysDate = new Date();
                CertificateUtils.out.log(Level.INFO, "creation date!!!" + x509Certificate.getNotBefore());
                CertificateUtils.out.log(Level.INFO, "expiry date!!!" + expiryDate);
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
            CertificateUtils.out.log(Level.WARNING, "Exception in parsing date!!!");
        }
        return noOfDays;
    }
    
    public static Long getDaysToExpiryOfCertificate(final String certificatePath) {
        CertificateUtils.out.log(Level.INFO, "Method daysToExpiryOfCertificate() starts");
        X509Certificate x509Certificate = null;
        Long noOfDays = -2L;
        try {
            x509Certificate = loadX509CertificateFromApiFactory(new File(certificatePath));
            Date expiryDate = x509Certificate.getNotAfter();
            Date todaysDate = new Date();
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyy");
            expiryDate = simpleDateFormat.parse(simpleDateFormat.format(expiryDate));
            todaysDate = simpleDateFormat.parse(simpleDateFormat.format(todaysDate));
            if (expiryDate != null) {
                noOfDays = expiryDate.getTime() - todaysDate.getTime();
                noOfDays /= 86400000L;
            }
        }
        catch (final CertificateExpiredException ex) {
            CertificateUtils.out.log(Level.WARNING, "Certificate Expired Exception in daysToExpiryOfCertificate", ex);
            return -1L;
        }
        catch (final Exception ex2) {
            CertificateUtils.out.log(Level.WARNING, "Exception in parsing date in daysToExpiryOfCertificate", ex2);
        }
        return noOfDays;
    }
    
    public static void generateServerSANCertificateFromRoot(final X509Certificate rootCA, final PrivateKey rootCAKey, final String serverCertificateFile, final String serverKeyFile, final String commonName, final List subjectAlternativeNames) throws Exception {
        Security.addProvider((Provider)new BouncyCastleProvider());
        X509Certificate serverSANCertificate = null;
        final KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", "BC");
        kpGen.initialize(2048, new SecureRandom());
        final KeyPair keyPair = kpGen.generateKeyPair();
        final BigInteger serialNumber = BigInteger.valueOf(new SecureRandom().nextLong()).abs();
        final Calendar cal = Calendar.getInstance();
        final Date notBefore = cal.getTime();
        cal.add(5, 365);
        final Date notAfter = cal.getTime();
        try {
            final X500NameBuilder builderex = new X500NameBuilder(BCStyle.INSTANCE);
            builderex.addRDN(BCStyle.C, "US");
            builderex.addRDN(BCStyle.ST, "CA");
            builderex.addRDN(BCStyle.OU, "ManageEngine");
            builderex.addRDN(BCStyle.O, "Zoho Corporation");
            builderex.addRDN(BCStyle.CN, commonName);
            JcaX509v3CertificateBuilder builder = null;
            builder = new JcaX509v3CertificateBuilder(new JcaX509CertificateHolder(rootCA).getSubject(), serialNumber, notBefore, notAfter, builderex.build(), keyPair.getPublic());
            final ContentSigner contentSigner = new JcaContentSignerBuilder("SHA256WithRSAEncryption").setProvider("BC").build(rootCAKey);
            builder.addExtension(Extension.basicConstraints, false, (ASN1Encodable)new BasicConstraints(false));
            final SubjectKeyIdentifier subjectKeyIdentifier = new JcaX509ExtensionUtils().createSubjectKeyIdentifier(keyPair.getPublic());
            builder.addExtension(Extension.subjectKeyIdentifier, false, (ASN1Encodable)subjectKeyIdentifier);
            final AuthorityKeyIdentifier authorityKeyIdentifier = new JcaX509ExtensionUtils().createAuthorityKeyIdentifier(rootCA.getPublicKey());
            builder.addExtension(Extension.authorityKeyIdentifier, false, (ASN1Encodable)authorityKeyIdentifier);
            final KeyUsage keyUsage = new KeyUsage(160);
            builder.addExtension(Extension.keyUsage, true, (ASN1Encodable)keyUsage);
            final KeyPurposeId[] extendedUsages = { KeyPurposeId.id_kp_serverAuth, KeyPurposeId.id_kp_clientAuth };
            builder.addExtension(Extension.extendedKeyUsage, false, (ASN1Encodable)new ExtendedKeyUsage(extendedUsages));
            int numberOfValidSAN;
            for (int numberOfSAN = numberOfValidSAN = subjectAlternativeNames.size(), i = 0; i < numberOfValidSAN; ++i) {
                final String subjectAlternativeName = subjectAlternativeNames.get(i).toString();
                if (null == subjectAlternativeName || "".equalsIgnoreCase(subjectAlternativeName.trim()) || "--".equalsIgnoreCase(subjectAlternativeName.trim())) {
                    subjectAlternativeNames.remove(i);
                    --numberOfValidSAN;
                }
            }
            Logger.getLogger("ImportCertificateLogger").log(Level.INFO, "Going to generate certificate with SAN Values " + subjectAlternativeNames);
            final GeneralName[] names = new GeneralName[numberOfValidSAN];
            for (int j = 0; j < numberOfValidSAN; ++j) {
                final String subjectAlternativeName2 = subjectAlternativeNames.get(j).toString();
                names[j] = new GeneralName(getGeneralNameType(subjectAlternativeName2), subjectAlternativeName2);
            }
            final GeneralNames subjectAltName = new GeneralNames(names);
            builder.addExtension(Extension.subjectAlternativeName, false, (ASN1Encodable)subjectAltName);
            final X509CertificateHolder holder = builder.build(contentSigner);
            serverSANCertificate = (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(holder.getEncoded()));
            final OutputStream certificateOutputStream = ApiFactoryProvider.getFileAccessAPI().writeFile(serverCertificateFile);
            final Writer certWriter = new OutputStreamWriter(certificateOutputStream);
            final PEMWriter certPemWriter = new PEMWriter(certWriter);
            certPemWriter.writeObject((Object)serverSANCertificate);
            certPemWriter.flush();
            certPemWriter.close();
            final OutputStream keyOutputStream = ApiFactoryProvider.getFileAccessAPI().writeFile(serverKeyFile);
            final Writer keyWriter = new OutputStreamWriter(keyOutputStream);
            final PEMWriter writer = new PEMWriter(keyWriter);
            writer.writeObject((Object)keyPair.getPrivate());
            writer.flush();
            writer.close();
        }
        catch (final Exception ex) {
            Logger.getLogger("ImportCertificateLogger").log(Level.SEVERE, "Error in generating SAN certificate " + ex);
            throw new Exception(ex);
        }
    }
    
    protected static int getGeneralNameType(final String subjectAlternativeName) {
        final String ipAddressRegex = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
        final Pattern ipAddressPattern = Pattern.compile(ipAddressRegex);
        final Matcher ipAddressMatcher = ipAddressPattern.matcher(subjectAlternativeName);
        if (ipAddressMatcher.matches()) {
            return 7;
        }
        return 2;
    }
    
    static {
        CertificateUtils.out = Logger.getLogger(CertificateUtils.class.getName());
        JAVA_KEY_STORE_PATH = System.getProperty("java.home") + File.separator + "lib" + File.separator + "security" + File.separator + "cacerts";
        USER_KEY_STORE_PATH = System.getProperty("server.home") + File.separator + "conf" + File.separator + "cacerts" + File.separator + "cacerts-user";
        if (Security.getProvider("BC") == null) {
            Security.addProvider((Provider)new BouncyCastleProvider());
        }
    }
}
