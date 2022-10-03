package com.microsoft.sqlserver.jdbc;

import java.security.MessageDigest;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.security.SignatureException;
import java.security.PrivateKey;
import java.security.Signature;
import javax.crypto.BadPaddingException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidKeyException;
import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.logging.Level;
import java.text.MessageFormat;
import java.util.logging.Logger;

public class SQLServerColumnEncryptionJavaKeyStoreProvider extends SQLServerColumnEncryptionKeyStoreProvider
{
    String name;
    String keyStorePath;
    char[] keyStorePwd;
    private static final Logger javaKeyStoreLogger;
    
    @Override
    public void setName(final String name) {
        this.name = name;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    public SQLServerColumnEncryptionJavaKeyStoreProvider(final String keyStoreLocation, char[] keyStoreSecret) throws SQLServerException {
        this.name = "MSSQL_JAVA_KEYSTORE";
        this.keyStorePath = null;
        this.keyStorePwd = null;
        SQLServerColumnEncryptionJavaKeyStoreProvider.javaKeyStoreLogger.entering(SQLServerColumnEncryptionJavaKeyStoreProvider.class.getName(), "SQLServerColumnEncryptionJavaKeyStoreProvider");
        if (null == keyStoreLocation || 0 == keyStoreLocation.length()) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidConnectionSetting"));
            final Object[] msgArgs = { "keyStoreLocation", keyStoreLocation };
            throw new SQLServerException(form.format(msgArgs), (Throwable)null);
        }
        this.keyStorePath = keyStoreLocation;
        if (SQLServerColumnEncryptionJavaKeyStoreProvider.javaKeyStoreLogger.isLoggable(Level.FINE)) {
            SQLServerColumnEncryptionJavaKeyStoreProvider.javaKeyStoreLogger.fine("Path of key store provider is set.");
        }
        if (null == keyStoreSecret) {
            keyStoreSecret = "".toCharArray();
        }
        System.arraycopy(keyStoreSecret, 0, this.keyStorePwd = new char[keyStoreSecret.length], 0, keyStoreSecret.length);
        if (SQLServerColumnEncryptionJavaKeyStoreProvider.javaKeyStoreLogger.isLoggable(Level.FINE)) {
            SQLServerColumnEncryptionJavaKeyStoreProvider.javaKeyStoreLogger.fine("Password for key store provider is set.");
        }
        SQLServerColumnEncryptionJavaKeyStoreProvider.javaKeyStoreLogger.exiting(SQLServerColumnEncryptionJavaKeyStoreProvider.class.getName(), "SQLServerColumnEncryptionJavaKeyStoreProvider");
    }
    
    @Override
    public byte[] decryptColumnEncryptionKey(final String masterKeyPath, final String encryptionAlgorithm, final byte[] encryptedColumnEncryptionKey) throws SQLServerException {
        SQLServerColumnEncryptionJavaKeyStoreProvider.javaKeyStoreLogger.entering(SQLServerColumnEncryptionJavaKeyStoreProvider.class.getName(), "decryptColumnEncryptionKey", "Decrypting Column Encryption Key.");
        KeyStoreProviderCommon.validateNonEmptyMasterKeyPath(masterKeyPath);
        final CertificateDetails certificateDetails = this.getCertificateDetails(masterKeyPath);
        final byte[] plainCEK = KeyStoreProviderCommon.decryptColumnEncryptionKey(masterKeyPath, encryptionAlgorithm, encryptedColumnEncryptionKey, certificateDetails);
        SQLServerColumnEncryptionJavaKeyStoreProvider.javaKeyStoreLogger.exiting(SQLServerColumnEncryptionJavaKeyStoreProvider.class.getName(), "decryptColumnEncryptionKey", "Finished decrypting Column Encryption Key.");
        return plainCEK;
    }
    
    private CertificateDetails getCertificateDetails(final String masterKeyPath) throws SQLServerException {
        FileInputStream fis = null;
        KeyStore keyStore = null;
        CertificateDetails certificateDetails = null;
        try {
            if (null == masterKeyPath || 0 == masterKeyPath.length()) {
                throw new SQLServerException(null, SQLServerException.getErrString("R_InvalidMasterKeyDetails"), null, 0, false);
            }
            try {
                keyStore = KeyStore.getInstance("JKS");
                fis = new FileInputStream(this.keyStorePath);
                keyStore.load(fis, this.keyStorePwd);
            }
            catch (final IOException e) {
                if (null != fis) {
                    fis.close();
                }
                keyStore = KeyStore.getInstance("PKCS12");
                fis = new FileInputStream(this.keyStorePath);
                keyStore.load(fis, this.keyStorePwd);
            }
            certificateDetails = this.getCertificateDetailsByAlias(keyStore, masterKeyPath);
        }
        catch (final FileNotFoundException fileNotFound) {
            throw new SQLServerException(this, SQLServerException.getErrString("R_KeyStoreNotFound"), null, 0, false);
        }
        catch (final IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException e2) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidKeyStoreFile"));
            final Object[] msgArgs = { this.keyStorePath };
            throw new SQLServerException(form.format(msgArgs), e2);
        }
        finally {
            try {
                if (null != fis) {
                    fis.close();
                }
            }
            catch (final IOException ex) {}
        }
        return certificateDetails;
    }
    
    private CertificateDetails getCertificateDetailsByAlias(final KeyStore keyStore, final String alias) throws SQLServerException {
        try {
            final X509Certificate publicCertificate = (X509Certificate)keyStore.getCertificate(alias);
            final Key keyPrivate = keyStore.getKey(alias, this.keyStorePwd);
            if (null == publicCertificate) {
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_CertificateNotFoundForAlias"));
                final Object[] msgArgs = { alias, "MSSQL_JAVA_KEYSTORE" };
                throw new SQLServerException(this, form.format(msgArgs), null, 0, false);
            }
            if (null == keyPrivate) {
                throw new UnrecoverableKeyException();
            }
            return new CertificateDetails(publicCertificate, keyPrivate);
        }
        catch (final UnrecoverableKeyException unrecoverableKeyException) {
            final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_UnrecoverableKeyAE"));
            final Object[] msgArgs2 = { alias };
            throw new SQLServerException(this, form2.format(msgArgs2), null, 0, false);
        }
        catch (final NoSuchAlgorithmException | KeyStoreException e) {
            final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_CertificateError"));
            final Object[] msgArgs2 = { alias, this.name };
            throw new SQLServerException(form2.format(msgArgs2), e);
        }
    }
    
    @Override
    public byte[] encryptColumnEncryptionKey(final String masterKeyPath, final String encryptionAlgorithm, final byte[] plainTextColumnEncryptionKey) throws SQLServerException {
        SQLServerColumnEncryptionJavaKeyStoreProvider.javaKeyStoreLogger.entering(SQLServerColumnEncryptionJavaKeyStoreProvider.class.getName(), "encryptColumnEncryptionKey", "Encrypting Column Encryption Key.");
        final byte[] version = KeyStoreProviderCommon.version;
        KeyStoreProviderCommon.validateNonEmptyMasterKeyPath(masterKeyPath);
        if (null == plainTextColumnEncryptionKey) {
            throw new SQLServerException(null, SQLServerException.getErrString("R_NullColumnEncryptionKey"), null, 0, false);
        }
        if (0 == plainTextColumnEncryptionKey.length) {
            throw new SQLServerException(null, SQLServerException.getErrString("R_EmptyColumnEncryptionKey"), null, 0, false);
        }
        KeyStoreProviderCommon.validateEncryptionAlgorithm(encryptionAlgorithm, true);
        final CertificateDetails certificateDetails = this.getCertificateDetails(masterKeyPath);
        final byte[] cipherText = this.encryptRSAOAEP(plainTextColumnEncryptionKey, certificateDetails);
        final byte[] cipherTextLength = this.getLittleEndianBytesFromShort((short)cipherText.length);
        final byte[] masterKeyPathBytes = masterKeyPath.toLowerCase().getBytes(StandardCharsets.UTF_16LE);
        final byte[] keyPathLength = this.getLittleEndianBytesFromShort((short)masterKeyPathBytes.length);
        final byte[] dataToSign = new byte[version.length + keyPathLength.length + cipherTextLength.length + masterKeyPathBytes.length + cipherText.length];
        int destinationPosition = version.length;
        System.arraycopy(version, 0, dataToSign, 0, version.length);
        System.arraycopy(keyPathLength, 0, dataToSign, destinationPosition, keyPathLength.length);
        destinationPosition += keyPathLength.length;
        System.arraycopy(cipherTextLength, 0, dataToSign, destinationPosition, cipherTextLength.length);
        destinationPosition += cipherTextLength.length;
        System.arraycopy(masterKeyPathBytes, 0, dataToSign, destinationPosition, masterKeyPathBytes.length);
        destinationPosition += masterKeyPathBytes.length;
        System.arraycopy(cipherText, 0, dataToSign, destinationPosition, cipherText.length);
        final byte[] signedHash = this.rsaSignHashedData(dataToSign, certificateDetails);
        final int encryptedColumnEncryptionKeyLength = version.length + cipherTextLength.length + keyPathLength.length + cipherText.length + masterKeyPathBytes.length + signedHash.length;
        final byte[] encryptedColumnEncryptionKey = new byte[encryptedColumnEncryptionKeyLength];
        int currentIndex = 0;
        System.arraycopy(version, 0, encryptedColumnEncryptionKey, currentIndex, version.length);
        currentIndex += version.length;
        System.arraycopy(keyPathLength, 0, encryptedColumnEncryptionKey, currentIndex, keyPathLength.length);
        currentIndex += keyPathLength.length;
        System.arraycopy(cipherTextLength, 0, encryptedColumnEncryptionKey, currentIndex, cipherTextLength.length);
        currentIndex += cipherTextLength.length;
        System.arraycopy(masterKeyPathBytes, 0, encryptedColumnEncryptionKey, currentIndex, masterKeyPathBytes.length);
        currentIndex += masterKeyPathBytes.length;
        System.arraycopy(cipherText, 0, encryptedColumnEncryptionKey, currentIndex, cipherText.length);
        currentIndex += cipherText.length;
        System.arraycopy(signedHash, 0, encryptedColumnEncryptionKey, currentIndex, signedHash.length);
        SQLServerColumnEncryptionJavaKeyStoreProvider.javaKeyStoreLogger.exiting(SQLServerColumnEncryptionJavaKeyStoreProvider.class.getName(), "encryptColumnEncryptionKey", "Finished encrypting Column Encryption Key.");
        return encryptedColumnEncryptionKey;
    }
    
    private byte[] encryptRSAOAEP(final byte[] plainText, final CertificateDetails certificateDetails) throws SQLServerException {
        byte[] cipherText = null;
        try {
            final Cipher rsa = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
            rsa.init(1, certificateDetails.certificate.getPublicKey());
            rsa.update(plainText);
            cipherText = rsa.doFinal();
        }
        catch (final InvalidKeyException | NoSuchAlgorithmException | IllegalBlockSizeException | NoSuchPaddingException | BadPaddingException e) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_EncryptionFailed"));
            final Object[] msgArgs = { e.getMessage() };
            throw new SQLServerException(this, form.format(msgArgs), null, 0, false);
        }
        return cipherText;
    }
    
    private byte[] rsaSignHashedData(final byte[] dataToSign, final CertificateDetails certificateDetails) throws SQLServerException {
        byte[] signedHash = null;
        try {
            final Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign((PrivateKey)certificateDetails.privateKey);
            signature.update(dataToSign);
            signedHash = signature.sign();
        }
        catch (final InvalidKeyException | NoSuchAlgorithmException | SignatureException e) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_EncryptionFailed"));
            final Object[] msgArgs = { e.getMessage() };
            throw new SQLServerException(this, form.format(msgArgs), null, 0, false);
        }
        return signedHash;
    }
    
    private byte[] getLittleEndianBytesFromShort(final short value) {
        final ByteBuffer byteBuffer = ByteBuffer.allocate(2);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        final byte[] byteValue = byteBuffer.putShort(value).array();
        return byteValue;
    }
    
    private boolean rsaVerifySignature(final byte[] dataToVerify, final byte[] signature, final CertificateDetails certificateDetails) throws SQLServerException {
        try {
            final Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initSign((PrivateKey)certificateDetails.privateKey);
            sig.update(dataToVerify);
            final byte[] signedHash = sig.sign();
            sig.initVerify(certificateDetails.certificate.getPublicKey());
            sig.update(dataToVerify);
            return sig.verify(signedHash);
        }
        catch (final InvalidKeyException | NoSuchAlgorithmException | SignatureException e) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_VerifySignatureFailed"));
            final Object[] msgArgs = { e.getMessage() };
            throw new SQLServerException(this, form.format(msgArgs), null, 0, false);
        }
    }
    
    @Override
    public boolean verifyColumnMasterKeyMetadata(final String masterKeyPath, final boolean allowEnclaveComputations, final byte[] signature) throws SQLServerException {
        if (!allowEnclaveComputations) {
            return false;
        }
        KeyStoreProviderCommon.validateNonEmptyMasterKeyPath(masterKeyPath);
        final CertificateDetails certificateDetails = this.getCertificateDetails(masterKeyPath);
        if (null == certificateDetails) {
            return false;
        }
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(this.name.toLowerCase().getBytes(StandardCharsets.UTF_16LE));
            md.update(masterKeyPath.toLowerCase().getBytes(StandardCharsets.UTF_16LE));
            md.update("true".getBytes(StandardCharsets.UTF_16LE));
            return this.rsaVerifySignature(md.digest(), signature, certificateDetails);
        }
        catch (final NoSuchAlgorithmException e) {
            throw new SQLServerException(SQLServerException.getErrString("R_NoSHA256Algorithm"), e);
        }
    }
    
    static {
        javaKeyStoreLogger = Logger.getLogger("com.microsoft.sqlserver.jdbc.SQLServerColumnEncryptionJavaKeyStoreProvider");
    }
}
