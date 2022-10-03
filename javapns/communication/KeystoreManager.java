package javapns.communication;

import javapns.communication.exceptions.InvalidKeystoreReferenceException;
import java.io.ByteArrayInputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.File;
import javapns.communication.exceptions.InvalidKeystoreFormatException;
import javapns.communication.exceptions.InvalidKeystorePasswordException;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.X509Certificate;
import java.io.InputStream;
import javapns.communication.exceptions.KeystoreException;
import java.security.KeyStore;

public class KeystoreManager
{
    private static final String REVIEW_MESSAGE = " Please review the procedure for generating a keystore for JavaPNS.";
    
    static KeyStore loadKeystore(final AppleServer server) throws KeystoreException {
        return loadKeystore(server, server.getKeystoreStream());
    }
    
    private static KeyStore loadKeystore(final AppleServer server, final Object keystore) throws KeystoreException {
        return loadKeystore(server, keystore, false);
    }
    
    private static synchronized KeyStore loadKeystore(final AppleServer server, final Object keystore, final boolean verifyKeystore) throws KeystoreException {
        if (keystore instanceof KeyStore) {
            return (KeyStore)keystore;
        }
        try (final InputStream keystoreStream = streamKeystore(keystore)) {
            if (keystoreStream instanceof WrappedKeystore) {
                return ((WrappedKeystore)keystoreStream).getKeystore();
            }
            final KeyStore keyStore = KeyStore.getInstance(server.getKeystoreType());
            final char[] password = getKeystorePasswordForSSL(server);
            keyStore.load(keystoreStream, password);
            return keyStore;
        }
        catch (final Exception e) {
            throw wrapKeystoreException(e);
        }
    }
    
    static Object ensureReusableKeystore(final AppleServer server, Object keystore) throws KeystoreException {
        if (keystore instanceof InputStream) {
            keystore = loadKeystore(server, keystore, false);
        }
        return keystore;
    }
    
    public static void verifyKeystoreContent(final AppleServer server, final Object keystore) throws KeystoreException {
        KeyStore keystoreToValidate;
        if (keystore instanceof KeyStore) {
            keystoreToValidate = (KeyStore)keystore;
        }
        else {
            keystoreToValidate = loadKeystore(server, keystore);
        }
        verifyKeystoreContent(keystoreToValidate);
    }
    
    private static void verifyKeystoreContent(final KeyStore keystore) throws KeystoreException {
        try {
            int numberOfCertificates = 0;
            final Enumeration<String> aliases = keystore.aliases();
            while (aliases.hasMoreElements()) {
                final String alias = aliases.nextElement();
                final Certificate certificate = keystore.getCertificate(alias);
                if (certificate instanceof X509Certificate) {
                    final X509Certificate xcert = (X509Certificate)certificate;
                    ++numberOfCertificates;
                    xcert.checkValidity();
                    final boolean issuerIsApple = xcert.getIssuerDN().toString().contains("Apple");
                    if (!issuerIsApple) {
                        throw new KeystoreException("Certificate was not issued by Apple. Please review the procedure for generating a keystore for JavaPNS.");
                    }
                    final boolean[] keyUsage = xcert.getKeyUsage();
                    if (!keyUsage[0]) {
                        throw new KeystoreException("Certificate usage is incorrect. Please review the procedure for generating a keystore for JavaPNS.");
                    }
                    continue;
                }
            }
            if (numberOfCertificates == 0) {
                throw new KeystoreException("Keystore does not contain any valid certificate. Please review the procedure for generating a keystore for JavaPNS.");
            }
            if (numberOfCertificates > 1) {
                throw new KeystoreException("Keystore contains too many certificates. Please review the procedure for generating a keystore for JavaPNS.");
            }
        }
        catch (final KeystoreException e) {
            throw e;
        }
        catch (final CertificateExpiredException e2) {
            throw new KeystoreException("Certificate is expired. A new one must be issued.", e2);
        }
        catch (final CertificateNotYetValidException e3) {
            throw new KeystoreException("Certificate is not yet valid. Wait until the validity period is reached or issue a new certificate.", e3);
        }
        catch (final Exception ex) {}
    }
    
    static char[] getKeystorePasswordForSSL(final AppleServer server) {
        String password = server.getKeystorePassword();
        if (password == null) {
            password = "";
        }
        return password.toCharArray();
    }
    
    static KeystoreException wrapKeystoreException(final Exception e) {
        if (e != null) {
            final String msg = e.toString();
            if (msg.contains("javax.crypto.BadPaddingException")) {
                return new InvalidKeystorePasswordException();
            }
            if (msg.contains("DerInputStream.getLength(): lengthTag=127, too big")) {
                return new InvalidKeystoreFormatException();
            }
            if (msg.contains("java.lang.ArithmeticException: / by zero") || msg.contains("java.security.UnrecoverableKeyException: Get Key failed: / by zero")) {
                return new InvalidKeystorePasswordException("Blank passwords not supported (#38).  You must create your keystore with a non-empty password.");
            }
        }
        return new KeystoreException("Keystore exception: " + ((e != null) ? e.getMessage() : null), e);
    }
    
    static InputStream streamKeystore(final Object keystore) throws InvalidKeystoreReferenceException {
        validateKeystoreParameter(keystore);
        try {
            if (keystore instanceof InputStream) {
                return (InputStream)keystore;
            }
            if (keystore instanceof KeyStore) {
                return new WrappedKeystore((KeyStore)keystore);
            }
            if (keystore instanceof File) {
                return new BufferedInputStream(new FileInputStream((File)keystore));
            }
            if (keystore instanceof String) {
                return new BufferedInputStream(new FileInputStream((String)keystore));
            }
            if (keystore instanceof byte[]) {
                return new ByteArrayInputStream((byte[])keystore);
            }
            return null;
        }
        catch (final Exception e) {
            throw new InvalidKeystoreReferenceException("Invalid keystore reference: " + e.getMessage());
        }
    }
    
    public static void validateKeystoreParameter(Object keystore) throws InvalidKeystoreReferenceException {
        if (keystore == null) {
            throw new InvalidKeystoreReferenceException((Object)null);
        }
        if (keystore instanceof KeyStore) {
            return;
        }
        if (keystore instanceof InputStream) {
            return;
        }
        if (keystore instanceof String) {
            keystore = new File((String)keystore);
        }
        if (keystore instanceof File) {
            final File file = (File)keystore;
            if (!file.exists()) {
                throw new InvalidKeystoreReferenceException("Invalid keystore reference.  File does not exist: " + file.getAbsolutePath());
            }
            if (!file.isFile()) {
                throw new InvalidKeystoreReferenceException("Invalid keystore reference.  Path does not refer to a valid file: " + file.getAbsolutePath());
            }
            if (file.length() <= 0L) {
                throw new InvalidKeystoreReferenceException("Invalid keystore reference.  File is empty: " + file.getAbsolutePath());
            }
        }
        else {
            if (!(keystore instanceof byte[])) {
                throw new InvalidKeystoreReferenceException(keystore);
            }
            final byte[] bytes = (byte[])keystore;
            if (bytes.length == 0) {
                throw new InvalidKeystoreReferenceException("Invalid keystore reference. Byte array is empty");
            }
        }
    }
}
