package com.adventnet.sym.server.mdm.certificates;

import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateExpiredException;
import java.security.Key;
import java.security.cert.CertificateException;
import java.security.KeyStoreException;
import java.security.KeyStore;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import java.security.PublicKey;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.interfaces.RSAPrivateKey;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Calendar;
import java.util.Date;
import java.security.NoSuchAlgorithmException;
import java.security.KeyPairGenerator;
import java.security.KeyPair;
import org.bouncycastle.asn1.x500.style.BCStyle;
import java.util.Iterator;
import java.util.Collection;
import java.util.List;
import com.me.mdm.api.error.APIHTTPException;
import java.security.cert.CertificateFactory;
import java.io.ByteArrayInputStream;
import org.apache.commons.io.IOUtils;
import java.util.ArrayList;
import java.util.logging.Level;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import java.security.cert.Certificate;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.io.InputStream;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import com.me.devicemanagement.framework.server.certificate.CertificateUtils;
import java.io.File;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.openssl.PEMParser;
import java.io.Reader;
import org.bouncycastle.util.io.pem.PemReader;
import java.io.InputStreamReader;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.security.SecureRandom;
import java.security.Provider;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.util.logging.Logger;

public class CertificateUtil
{
    private static CertificateUtil certificateUtil;
    public static Logger logger;
    
    public static CertificateUtil getInstance() {
        if (CertificateUtil.certificateUtil == null) {
            CertificateUtil.certificateUtil = new CertificateUtil();
        }
        Security.addProvider((Provider)new BouncyCastleProvider());
        return CertificateUtil.certificateUtil;
    }
    
    public String generateRandomCertPassword(final int length) {
        final String list = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final int mod = list.length();
        String result = "";
        final SecureRandom r = new SecureRandom();
        for (int i = 0; i < length; ++i) {
            result += list.charAt(Math.abs(r.nextInt() % mod));
        }
        return result;
    }
    
    public boolean isValidCSRAndPrivateKey(final String csrLocation, final String privateKeyLocation) throws Exception {
        PemReader pemreader = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        try {
            inputStream = ApiFactoryProvider.getFileAccessAPI().getInputStream(csrLocation);
            inputStreamReader = new InputStreamReader(inputStream);
            pemreader = new PemReader((Reader)inputStreamReader);
            final PEMParser pemParser = new PEMParser((Reader)pemreader);
            final PKCS10CertificationRequest csr = (PKCS10CertificationRequest)pemParser.readObject();
            final PrivateKey privateKey = CertificateUtils.loadPrivateKeyFromApiFactory(new File(privateKeyLocation));
            final RSAKeyParameters publicKeyParams = (RSAKeyParameters)PublicKeyFactory.createKey(csr.getSubjectPublicKeyInfo());
            final BigInteger publicKeyModulus = publicKeyParams.getModulus();
            final RSAKeyParameters privKeyParams = (RSAKeyParameters)PrivateKeyFactory.createKey(privateKey.getEncoded());
            final BigInteger privateKeyModulus = privKeyParams.getModulus();
            return this.compareKeys(publicKeyModulus, privateKeyModulus);
        }
        finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (pemreader != null) {
                pemreader.close();
            }
            if (inputStreamReader != null) {
                inputStreamReader.close();
            }
        }
    }
    
    public boolean isValidCertificateAndPrivateKey(final X509Certificate certificate, final PrivateKey privateKey) throws IOException {
        final RSAKeyParameters publicKeyParams = (RSAKeyParameters)PublicKeyFactory.createKey(certificate.getPublicKey().getEncoded());
        final BigInteger publicKeyModulus = publicKeyParams.getModulus();
        final RSAKeyParameters privKeyParams = (RSAKeyParameters)PrivateKeyFactory.createKey(privateKey.getEncoded());
        final BigInteger privateKeyModulus = privKeyParams.getModulus();
        return this.compareKeys(publicKeyModulus, privateKeyModulus);
    }
    
    public boolean compareKeys(final BigInteger publicKeyModulus, final BigInteger privateKeyModulus) {
        return publicKeyModulus.equals(privateKeyModulus);
    }
    
    public static String getCertificateNameFromCertificateSubject(final X509Certificate x509Certificate) throws Exception {
        String subject = getInstance().getDNFromCertificate(x509Certificate, SubjectDN.CN);
        if (subject == null || subject.isEmpty()) {
            subject = getInstance().getDNFromCertificate(x509Certificate, SubjectDN.OU);
        }
        if (subject == null || subject.isEmpty()) {
            subject = getInstance().getDNFromCertificate(x509Certificate, SubjectDN.O);
        }
        if (subject == null || subject.isEmpty()) {
            subject = getInstance().getDNFromCertificate(x509Certificate, SubjectDN.L);
        }
        if (subject == null || subject.isEmpty()) {
            subject = getInstance().getDNFromCertificate(x509Certificate, SubjectDN.ST);
        }
        if (subject == null || subject.isEmpty()) {
            subject = getInstance().getDNFromCertificate(x509Certificate, SubjectDN.C);
        }
        return subject;
    }
    
    public String getDNFromCertificate(final Certificate certificate, final SubjectDN subjectDN) throws Exception {
        final X509Certificate x509Certificate = (X509Certificate)certificate;
        final X500Name x500name = new JcaX509CertificateHolder(x509Certificate).getSubject();
        final RDN[] rdns = x500name.getRDNs(subjectDN.getAsn1ObjectIdentifier());
        if (rdns.length > 0) {
            return IETFUtils.valueToString(rdns[0].getFirst().getValue());
        }
        return "";
    }
    
    public static Certificate[] convertInputStreamToX509CertificateChain(final InputStream inputStream) throws Exception {
        try {
            CertificateUtil.logger.log(Level.INFO, "Reading p7b certificate");
            final List<Certificate> certList = new ArrayList<Certificate>();
            final byte[] bytes = IOUtils.toByteArray(inputStream);
            final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            final CertificateFactory cf = CertificateFactory.getInstance("X.509");
            final Collection<?> c = cf.generateCertificates(bais);
            if (!c.isEmpty()) {
                CertificateUtil.logger.log(Level.INFO, "Certificates available");
                final Iterator<?> i = c.iterator();
                while (i.hasNext()) {
                    certList.add((Certificate)i.next());
                }
            }
            final Certificate[] certArr = new Certificate[certList.size()];
            return certList.toArray(certArr);
        }
        catch (final Exception e) {
            CertificateUtil.logger.log(Level.SEVERE, "Unable to read RA certificate");
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
    
    public static String getCommonNameFromCertificateSubject(final Certificate certificate) throws Exception {
        final X500Name x500Name = new JcaX509CertificateHolder((X509Certificate)certificate).getSubject();
        final RDN cn = x500Name.getRDNs(BCStyle.CN)[0];
        return IETFUtils.valueToString(cn.getFirst().getValue());
    }
    
    public static KeyPair createRsaKeyPair(final int keySize) throws NoSuchAlgorithmException {
        final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize);
        final KeyPair keyPair = keyPairGenerator.generateKeyPair();
        return keyPair;
    }
    
    public static Date getCertificateValidityEndDate(final int numberOfYears) {
        final Calendar calendar = Calendar.getInstance();
        calendar.add(1, numberOfYears);
        final Date certificateValidityEndDate = calendar.getTime();
        return certificateValidityEndDate;
    }
    
    public static PrivateKey convertInputStreamToRsaPrivateKey(final InputStream privateKeyInputStream) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        final byte[] bytes = IOUtils.toByteArray(privateKeyInputStream);
        final PKCS8EncodedKeySpec x509EncodedKeySpec = new PKCS8EncodedKeySpec(bytes);
        final KeyFactory kf = KeyFactory.getInstance("RSA");
        final RSAPrivateKey privateKey = (RSAPrivateKey)kf.generatePrivate(x509EncodedKeySpec);
        return privateKey;
    }
    
    public static BigInteger getRandomSerialNumber() {
        return BigInteger.valueOf(new SecureRandom().nextLong()).abs();
    }
    
    public static String getSignatureFromX509Certificate(final X509Certificate certificate) {
        return String.valueOf(new BigInteger(certificate.getSignature()));
    }
    
    public static void addSubjectKeyIdentifier(final JcaX509v3CertificateBuilder certificateBuilder, final PublicKey publicKey) throws CertIOException, NoSuchAlgorithmException {
        final SubjectKeyIdentifier subjectKeyIdentifier = new JcaX509ExtensionUtils().createSubjectKeyIdentifier(publicKey);
        certificateBuilder.addExtension(Extension.subjectKeyIdentifier, false, (ASN1Encodable)subjectKeyIdentifier);
    }
    
    public static void addAuthorityKeyIdentifier(final JcaX509v3CertificateBuilder certificateBuilder, final PublicKey publicKey) throws CertIOException, NoSuchAlgorithmException {
        final AuthorityKeyIdentifier authorityKeyIdentifier = new JcaX509ExtensionUtils().createAuthorityKeyIdentifier(publicKey);
        certificateBuilder.addExtension(Extension.authorityKeyIdentifier, false, (ASN1Encodable)authorityKeyIdentifier);
    }
    
    public static void addTypicalCAKeyUsages(final JcaX509v3CertificateBuilder certificateBuilder) throws CertIOException {
        final KeyUsage keyUsage = new KeyUsage(166);
        certificateBuilder.addExtension(Extension.keyUsage, true, (ASN1Encodable)keyUsage);
    }
    
    public static void addTypicalClientKeyUsages(final JcaX509v3CertificateBuilder certificateBuilder) throws CertIOException {
        final KeyUsage keyUsage = new KeyUsage(160);
        certificateBuilder.addExtension(Extension.keyUsage, true, (ASN1Encodable)keyUsage);
    }
    
    public static void addClientAuthExtendedKeyUsage(final JcaX509v3CertificateBuilder certificateBuilder) throws CertIOException {
        final ExtendedKeyUsage clientAuthExtendedKeyUsage = new ExtendedKeyUsage(KeyPurposeId.id_kp_clientAuth);
        certificateBuilder.addExtension(Extension.extendedKeyUsage, true, (ASN1Encodable)clientAuthExtendedKeyUsage);
    }
    
    public static KeyStore createTrustStore(final Certificate[] certificates) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        final KeyStore pkcs12Keystore = KeyStore.getInstance("PKCS12");
        pkcs12Keystore.load(null, null);
        int i = 0;
        for (final Certificate certificate : certificates) {
            pkcs12Keystore.setCertificateEntry("ca_" + ++i, certificate);
        }
        return pkcs12Keystore;
    }
    
    public static KeyStore createPkcs12KeyStore(final String alias, final Certificate[] clientCerts, final PrivateKey privateKey, final char[] password) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        final KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(null, null);
        keyStore.setKeyEntry(alias, privateKey, password, clientCerts);
        return keyStore;
    }
    
    public static boolean isX509CertificateValid(final Certificate[] raCertificate) {
        boolean isValidCertificate = false;
        for (final Certificate cert : raCertificate) {
            if (cert instanceof X509Certificate) {
                CertificateUtil.logger.log(Level.INFO, "Checking validity for certificate: {0}", ((X509Certificate)cert).getSubjectDN());
                try {
                    ((X509Certificate)cert).checkValidity();
                    CertificateUtil.logger.log(Level.INFO, "Certificate is valid: {0}", ((X509Certificate)cert).getSubjectDN());
                    isValidCertificate = true;
                }
                catch (final CertificateExpiredException | CertificateNotYetValidException e) {
                    final String eMessage = "Certificate not valid: " + ((X509Certificate)cert).getSubjectDN();
                    CertificateUtil.logger.log(Level.SEVERE, eMessage, e);
                    isValidCertificate = false;
                    break;
                }
            }
        }
        return isValidCertificate;
    }
    
    static {
        CertificateUtil.logger = Logger.getLogger("MdmCertificateIntegLogger");
    }
}
