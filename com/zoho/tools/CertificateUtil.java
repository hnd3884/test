package com.zoho.tools;

import java.security.GeneralSecurityException;
import java.util.Iterator;
import com.zoho.tools.util.FileUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.Arrays;
import sun.security.pkcs.PKCS7;
import sun.security.util.SignatureFileVerifier;
import java.util.zip.ZipEntry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.security.cert.CertificateFactory;
import sun.security.x509.X509CertImpl;
import sun.security.x509.BasicConstraintsExtension;
import java.security.KeyStoreException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Enumeration;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.security.cert.Certificate;
import java.util.UUID;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.io.File;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;

public class CertificateUtil
{
    public static final Logger LOGGER;
    public static final String KEYSTORE = "manageengine.keystore";
    public static final String BLACKLISTED_IDENTIFIER = "blacklisted_";
    
    public static void importCertificate(final String certificateFilePath, final String keyStoreDir, final String keyStorePass) throws Exception {
        if (certificateFilePath == null) {
            throw new IllegalArgumentException("Certificate path cannot be null");
        }
        if (!certificateFilePath.endsWith(".cer") && !certificateFilePath.endsWith(".crt")) {
            throw new IllegalArgumentException("Invalid Certificate File is specified");
        }
        importCertificate(getCertificate(certificateFilePath), keyStoreDir, keyStorePass);
    }
    
    public static void importCertificate(final X509Certificate newCertificate, final String keyStoreDir, final String keyStorePass) throws Exception {
        if (newCertificate == null) {
            throw new IllegalArgumentException("Invalid X.509 certificate specified");
        }
        if (keyStorePass == null || keyStorePass.isEmpty()) {
            throw new IllegalArgumentException("Invalid keystore password is specified");
        }
        if (keyStoreDir == null || keyStoreDir.isEmpty()) {
            throw new IllegalArgumentException("Invalid keystore directory is specified");
        }
        final KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        final char[] password = keyStorePass.toCharArray();
        final File keystoreFile = new File(keyStoreDir + File.separator + "manageengine.keystore");
        if (!keystoreFile.exists()) {
            CertificateUtil.LOGGER.log(Level.INFO, "Creating new Keystore at {0} :: {1}", new Object[] { keystoreFile.getAbsolutePath(), keystoreFile.createNewFile() });
            keystore.load(null, password);
        }
        else {
            try (final FileInputStream kis = new FileInputStream(keystoreFile)) {
                keystore.load(kis, password);
            }
        }
        final Enumeration<String> aliases = keystore.aliases();
        while (aliases.hasMoreElements()) {
            final String alias = aliases.nextElement();
            final X509Certificate certificate = (X509Certificate)keystore.getCertificate(alias);
            if (certificate.equals(newCertificate)) {
                CertificateUtil.LOGGER.log(Level.INFO, "Certificate already exists");
                return;
            }
        }
        keystore.setCertificateEntry(String.valueOf(UUID.randomUUID()), newCertificate);
        try (final FileOutputStream out = new FileOutputStream(keystoreFile)) {
            keystore.store(out, password);
        }
        CertificateUtil.LOGGER.log(Level.INFO, "Certificate identified by alias {0} imported successfully", getAlias(newCertificate, keyStoreDir, keyStorePass));
    }
    
    public static List<X509Certificate> getCertificates(final String keyStoreDir, final String keyStorePass) {
        final File keystoreFile = new File(keyStoreDir + File.separator + "manageengine.keystore");
        if (!keystoreFile.exists()) {
            return Collections.emptyList();
        }
        final List<X509Certificate> certificateList = new ArrayList<X509Certificate>();
        try {
            final char[] password = keyStorePass.toCharArray();
            final KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            try (final FileInputStream kis = new FileInputStream(keystoreFile)) {
                keystore.load(kis, password);
            }
            final Enumeration<String> aliases = keystore.aliases();
            while (aliases.hasMoreElements()) {
                final String alias = aliases.nextElement();
                if (alias.startsWith("blacklisted_")) {
                    continue;
                }
                final X509Certificate certificate = (X509Certificate)keystore.getCertificate(alias);
                certificateList.add(certificate);
            }
        }
        catch (final CertificateException | NoSuchAlgorithmException | IOException | KeyStoreException e) {
            CertificateUtil.LOGGER.log(Level.SEVERE, "Exception occurred while fetching certificates");
            CertificateUtil.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return certificateList;
    }
    
    public static X509Certificate getClientCertificate(final X509Certificate[] certificates) {
        for (final X509Certificate certificate : certificates) {
            try {
                final Object bc = ((BasicConstraintsExtension)((X509CertImpl)certificate).get("x509.info.extensions.BasicConstraints")).get("is_ca");
                if (!Boolean.parseBoolean(String.valueOf(bc))) {
                    return certificate;
                }
            }
            catch (final CertificateException | IOException e) {
                return certificate;
            }
        }
        return certificates[0];
    }
    
    private static X509Certificate getCertificate(final String certificateFilePath) throws IOException, CertificateException {
        final CertificateFactory fac = CertificateFactory.getInstance("X509");
        try (final FileInputStream is = new FileInputStream(certificateFilePath)) {
            return (X509Certificate)fac.generateCertificate(is);
        }
    }
    
    public static boolean isSelfSigned(final String certificateFilePath) throws Exception {
        return isSelfSigned(getCertificate(certificateFilePath));
    }
    
    public static boolean isSelfSigned(final X509Certificate cert) throws CertificateException, NoSuchAlgorithmException, NoSuchProviderException {
        try {
            final PublicKey key = cert.getPublicKey();
            cert.verify(key);
            return true;
        }
        catch (final SignatureException | InvalidKeyException sigEx) {
            return false;
        }
    }
    
    public static boolean removeCertificate(final String certificateFilePath, final String keyStoreDir, final String keyStorePass) throws Exception {
        final KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        final File keystoreFile = new File(keyStoreDir + File.separator + "manageengine.keystore");
        try (final FileInputStream fis = new FileInputStream(keystoreFile)) {
            keystore.load(fis, keyStorePass.toCharArray());
            final X509Certificate certificate = getCertificate(certificateFilePath);
            final Enumeration<String> aliases = keystore.aliases();
            while (aliases.hasMoreElements()) {
                final String alias = aliases.nextElement();
                if (keystore.isCertificateEntry(alias)) {
                    final X509Certificate certFromKeystore = (X509Certificate)keystore.getCertificate(alias);
                    if (certFromKeystore.equals(certificate)) {
                        keystore.deleteEntry(alias);
                        CertificateUtil.LOGGER.log(Level.INFO, "removed a certificate {0} from the keystore", alias);
                        keystore.store(new FileOutputStream(keystoreFile), keyStorePass.toCharArray());
                        return true;
                    }
                    continue;
                }
            }
        }
        catch (final Exception exception) {
            CertificateUtil.LOGGER.log(Level.SEVERE, "Could not remove a certificate from the keystore : {0}", "manageengine.keystore");
            throw exception;
        }
        return false;
    }
    
    public static boolean blackListCertificate(final String certificateFilePath, final String keyStoreDir, final String keyStorePass) throws Exception {
        if (certificateFilePath == null) {
            throw new IllegalArgumentException("Certificate path cannot be null");
        }
        if (!certificateFilePath.endsWith(".cer") && !certificateFilePath.endsWith(".crt")) {
            throw new IllegalArgumentException("Invalid Certificate File is specified");
        }
        return blackListCertificate(getCertificate(certificateFilePath), keyStoreDir, keyStorePass);
    }
    
    public static boolean blackListCertificate(final X509Certificate certificate, final String keyStoreDir, final String keyStorePass) throws Exception {
        final KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        final File keystoreFile = new File(keyStoreDir + File.separator + "manageengine.keystore");
        try (final FileInputStream fis = new FileInputStream(keystoreFile)) {
            keystore.load(fis, keyStorePass.toCharArray());
            final Enumeration<String> aliases = keystore.aliases();
            while (aliases.hasMoreElements()) {
                final String alias = aliases.nextElement();
                if (keystore.isCertificateEntry(alias)) {
                    final X509Certificate certFromKeystore = (X509Certificate)keystore.getCertificate(alias);
                    if (certFromKeystore.equals(certificate)) {
                        keystore.deleteEntry(alias);
                        final String newAliasName = "blacklisted_" + alias;
                        keystore.setCertificateEntry(newAliasName, certFromKeystore);
                        keystore.store(new FileOutputStream(keystoreFile), keyStorePass.toCharArray());
                        CertificateUtil.LOGGER.log(Level.INFO, "blacklisted a certificate {0} from the keystore", alias);
                        return true;
                    }
                    continue;
                }
            }
        }
        catch (final Exception exception) {
            CertificateUtil.LOGGER.log(Level.SEVERE, "could not blacklist a certificate from the keystore : {0}", "manageengine.keystore");
            throw exception;
        }
        return false;
    }
    
    public static boolean isBlackListed(final String certificateFilePath, final String keyStoreDir, final String keyStorePass) throws Exception {
        final X509Certificate certificate = getCertificate(certificateFilePath);
        return isBlackListed(certificate, keyStoreDir, keyStorePass);
    }
    
    public static boolean isBlackListed(final X509Certificate certificate, final String keyStoreDir, final String keyStorePass) throws Exception {
        final KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        final File keystoreFile = new File(keyStoreDir + File.separator + "manageengine.keystore");
        try (final FileInputStream fis = new FileInputStream(keystoreFile)) {
            keystore.load(fis, keyStorePass.toCharArray());
            final Enumeration<String> aliases = keystore.aliases();
            while (aliases.hasMoreElements()) {
                final String alias = aliases.nextElement();
                if (keystore.isCertificateEntry(alias)) {
                    final X509Certificate certFromKeystore = (X509Certificate)keystore.getCertificate(alias);
                    if (certFromKeystore.equals(certificate)) {
                        return alias.startsWith("blacklisted_");
                    }
                    continue;
                }
            }
        }
        catch (final Exception exception) {
            CertificateUtil.LOGGER.log(Level.SEVERE, "Exception occurred while getting a certificate from the keystore : {0}", "manageengine.keystore");
            throw exception;
        }
        return false;
    }
    
    public static boolean isKeyStoreExists(final String keyStoreDir) {
        return new File(keyStoreDir + File.separator + "manageengine.keystore").exists();
    }
    
    public static String getAlias(final String certificateFilePath, final String keyStoreDir, final String keyStorePass) throws Exception {
        if (certificateFilePath == null) {
            throw new IllegalArgumentException("Certificate path cannot be null");
        }
        if (!certificateFilePath.endsWith(".cer") && !certificateFilePath.endsWith(".crt")) {
            throw new IllegalArgumentException("Invalid Certificate File is specified");
        }
        return getAlias(getCertificate(certificateFilePath), keyStoreDir, keyStorePass);
    }
    
    public static String getAlias(final X509Certificate newCertificate, final String keyStoreDir, final String keyStorePass) throws Exception {
        final KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        final char[] password = keyStorePass.toCharArray();
        final File keystoreFile = new File(keyStoreDir + File.separator + "manageengine.keystore");
        if (!keystoreFile.exists()) {
            return null;
        }
        try (final FileInputStream kis = new FileInputStream(keystoreFile)) {
            keystore.load(kis, password);
        }
        final Enumeration<String> aliases = keystore.aliases();
        while (aliases.hasMoreElements()) {
            final String alias = aliases.nextElement();
            final X509Certificate certificate = (X509Certificate)keystore.getCertificate(alias);
            if (certificate.equals(newCertificate)) {
                return alias;
            }
        }
        return null;
    }
    
    public static boolean isCertificateExists(final String certificateFilePath, final String keyStoreDir, final String keyStorePass) throws Exception {
        final X509Certificate newCertificate = getCertificate(certificateFilePath);
        return isCertificateExists(newCertificate, keyStoreDir, keyStorePass);
    }
    
    public static boolean isCertificateExists(final X509Certificate newCertificate, final String keyStoreDir, final String keyStorePass) throws Exception {
        final KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        final char[] password = keyStorePass.toCharArray();
        final File keystoreFile = new File(keyStoreDir + File.separator + "manageengine.keystore");
        if (!keystoreFile.exists()) {
            return false;
        }
        try (final FileInputStream kis = new FileInputStream(keystoreFile)) {
            keystore.load(kis, password);
        }
        final Enumeration<String> aliases = keystore.aliases();
        while (aliases.hasMoreElements()) {
            final String alias = aliases.nextElement();
            final X509Certificate certificate = (X509Certificate)keystore.getCertificate(alias);
            if (certificate.equals(newCertificate)) {
                return true;
            }
        }
        return false;
    }
    
    @Deprecated
    public static List<X509Certificate> getCertificates(final String ppmFilePath) {
        final List<X509Certificate> signatures = new ArrayList<X509Certificate>();
        try (final JarFile jarFile = new JarFile(ppmFilePath, true)) {
            boolean hasSignature = false;
            final Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                final JarEntry jarEntry = entries.nextElement();
                try (final InputStream inputStream = jarFile.getInputStream(jarEntry)) {
                    final String name = jarEntry.getName();
                    if (SignatureFileVerifier.isSigningRelated(name) && SignatureFileVerifier.isBlockOrSF(name)) {
                        hasSignature = (hasSignature || SignatureFileVerifier.isBlockOrSF(name));
                        try {
                            if (!name.endsWith(".SF")) {
                                final PKCS7 pkcs7 = new PKCS7(inputStream);
                                signatures.addAll(Arrays.asList(pkcs7.getCertificates()));
                            }
                        }
                        catch (final IOException ioe) {
                            CertificateUtil.LOGGER.log(Level.SEVERE, "Unable to parse signature file {0}", name);
                            CertificateUtil.LOGGER.log(Level.SEVERE, ioe.getMessage(), ioe);
                        }
                    }
                }
            }
        }
        catch (final IOException ioe2) {
            CertificateUtil.LOGGER.log(Level.SEVERE, ioe2.getMessage(), ioe2);
        }
        return signatures;
    }
    
    public static Map<String, X509Certificate> getCertificatesWithAlias(final String keyStoreDir, final String keyStorePass) {
        final File keystoreFile = new File(keyStoreDir + File.separator + "manageengine.keystore");
        if (!keystoreFile.exists()) {
            return Collections.emptyMap();
        }
        final Map<String, X509Certificate> certificateList = new HashMap<String, X509Certificate>();
        try {
            final char[] password = keyStorePass.toCharArray();
            final KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            try (final FileInputStream kis = new FileInputStream(keystoreFile)) {
                keystore.load(kis, password);
            }
            final Enumeration<String> aliases = keystore.aliases();
            while (aliases.hasMoreElements()) {
                final String alias = aliases.nextElement();
                final X509Certificate certificate = (X509Certificate)keystore.getCertificate(alias);
                certificateList.put(alias, certificate);
            }
        }
        catch (final CertificateException | NoSuchAlgorithmException | IOException | KeyStoreException e) {
            CertificateUtil.LOGGER.log(Level.SEVERE, "Exception occurred while fetching certificates");
            CertificateUtil.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return certificateList;
    }
    
    public static boolean rotateKeyStorePassword(final String keyStoreDir, final String oldPassword, final String newPassword) {
        final File keystoreFile = new File(keyStoreDir + File.separator + "manageengine.keystore");
        final File oldKeystoreFile = new File(keyStoreDir + File.separator + "manageengine.keystore" + ".old");
        try {
            final Map<String, X509Certificate> certificates = getCertificatesWithAlias(keyStoreDir, oldPassword);
            FileUtil.moveDirectory(keystoreFile, oldKeystoreFile);
            final KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            CertificateUtil.LOGGER.log(Level.INFO, "Creating new Keystore at {0} :: {1}", new Object[] { keystoreFile.getAbsolutePath(), keystoreFile.createNewFile() });
            keystore.load(null, newPassword.toCharArray());
            for (final String alias : certificates.keySet()) {
                keystore.setCertificateEntry(alias, certificates.get(alias));
            }
            try (final FileOutputStream out = new FileOutputStream(keystoreFile)) {
                keystore.store(out, newPassword.toCharArray());
            }
            CertificateUtil.LOGGER.log(Level.INFO, "Keystore password rotated successfully");
            return true;
        }
        catch (final Exception e) {
            CertificateUtil.LOGGER.log(Level.SEVERE, "Exception occurred while rotating password :: " + e.getMessage(), e);
            if (keystoreFile.exists() && oldKeystoreFile.exists()) {
                try {
                    CertificateUtil.LOGGER.log(Level.INFO, "Deleting keystore file :: {0}", keystoreFile.delete());
                    FileUtil.moveDirectory(oldKeystoreFile, keystoreFile);
                }
                catch (final IOException ioException) {
                    CertificateUtil.LOGGER.log(Level.INFO, "Exception occurred while reverting to old keystore file :: " + ioException.getMessage(), ioException);
                }
            }
            return false;
        }
        finally {
            if (oldKeystoreFile.exists()) {
                CertificateUtil.LOGGER.log(Level.INFO, "Deleting old keystore file :: {0}", oldKeystoreFile.delete());
            }
        }
    }
    
    static {
        LOGGER = Logger.getLogger(CertificateUtil.class.getName());
    }
}
