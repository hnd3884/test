package com.microsoft.sqlserver.jdbc;

import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.security.SignatureException;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.security.GeneralSecurityException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import javax.crypto.Cipher;
import java.text.MessageFormat;

class KeyStoreProviderCommon
{
    static final String rsaEncryptionAlgorithmWithOAEP = "RSA_OAEP";
    static byte[] version;
    
    static void validateEncryptionAlgorithm(final String encryptionAlgorithm, final boolean isEncrypt) throws SQLServerException {
        String errString = isEncrypt ? "R_NullKeyEncryptionAlgorithm" : "R_NullKeyEncryptionAlgorithmInternal";
        if (null == encryptionAlgorithm) {
            throw new SQLServerException(null, SQLServerException.getErrString(errString), null, 0, false);
        }
        errString = (isEncrypt ? "R_InvalidKeyEncryptionAlgorithm" : "R_InvalidKeyEncryptionAlgorithmInternal");
        if (!"RSA_OAEP".equalsIgnoreCase(encryptionAlgorithm.trim())) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString(errString));
            final Object[] msgArgs = { encryptionAlgorithm, "RSA_OAEP" };
            throw new SQLServerException(form.format(msgArgs), (Throwable)null);
        }
    }
    
    static void validateNonEmptyMasterKeyPath(final String masterKeyPath) throws SQLServerException {
        if (null == masterKeyPath || masterKeyPath.trim().length() == 0) {
            throw new SQLServerException(null, SQLServerException.getErrString("R_InvalidMasterKeyDetails"), null, 0, false);
        }
    }
    
    static byte[] decryptColumnEncryptionKey(final String masterKeyPath, final String encryptionAlgorithm, final byte[] encryptedColumnEncryptionKey, final CertificateDetails certificateDetails) throws SQLServerException {
        if (null == encryptedColumnEncryptionKey) {
            throw new SQLServerException(null, SQLServerException.getErrString("R_NullEncryptedColumnEncryptionKey"), null, 0, false);
        }
        if (0 == encryptedColumnEncryptionKey.length) {
            throw new SQLServerException(null, SQLServerException.getErrString("R_EmptyEncryptedColumnEncryptionKey"), null, 0, false);
        }
        validateEncryptionAlgorithm(encryptionAlgorithm, false);
        int currentIndex = KeyStoreProviderCommon.version.length;
        final int keyPathLength = convertTwoBytesToShort(encryptedColumnEncryptionKey, currentIndex);
        currentIndex += 2;
        final int cipherTextLength = convertTwoBytesToShort(encryptedColumnEncryptionKey, currentIndex);
        currentIndex += 2;
        currentIndex += keyPathLength;
        final int signatureLength = encryptedColumnEncryptionKey.length - currentIndex - cipherTextLength;
        final byte[] cipherText = new byte[cipherTextLength];
        System.arraycopy(encryptedColumnEncryptionKey, currentIndex, cipherText, 0, cipherTextLength);
        currentIndex += cipherTextLength;
        final byte[] signature = new byte[signatureLength];
        System.arraycopy(encryptedColumnEncryptionKey, currentIndex, signature, 0, signatureLength);
        final byte[] hash = new byte[encryptedColumnEncryptionKey.length - signature.length];
        System.arraycopy(encryptedColumnEncryptionKey, 0, hash, 0, encryptedColumnEncryptionKey.length - signature.length);
        if (!verifyRSASignature(hash, signature, certificateDetails.certificate, masterKeyPath)) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidCertificateSignature"));
            final Object[] msgArgs = { masterKeyPath };
            throw new SQLServerException(form.format(msgArgs), (Throwable)null);
        }
        final byte[] plainCEK = decryptRSAOAEP(cipherText, certificateDetails);
        return plainCEK;
    }
    
    private static byte[] decryptRSAOAEP(final byte[] cipherText, final CertificateDetails certificateDetails) throws SQLServerException {
        byte[] plainCEK = null;
        try {
            final Cipher rsa = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
            rsa.init(2, certificateDetails.privateKey);
            rsa.update(cipherText);
            plainCEK = rsa.doFinal();
        }
        catch (final InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_CEKDecryptionFailed"));
            final Object[] msgArgs = { e.getMessage() };
            throw new SQLServerException(form.format(msgArgs), e);
        }
        return plainCEK;
    }
    
    static boolean verifyRSASignature(final byte[] hash, final byte[] signature, final X509Certificate certificate, final String masterKeyPath) throws SQLServerException {
        boolean verificationSuccess = false;
        try {
            final Signature signVerify = Signature.getInstance("SHA256withRSA");
            signVerify.initVerify(certificate.getPublicKey());
            signVerify.update(hash);
            verificationSuccess = signVerify.verify(signature);
        }
        catch (final InvalidKeyException | NoSuchAlgorithmException | SignatureException e) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidCertificateSignature"));
            final Object[] msgArgs = { masterKeyPath };
            throw new SQLServerException(form.format(msgArgs), e);
        }
        return verificationSuccess;
    }
    
    private static short convertTwoBytesToShort(final byte[] input, final int index) throws SQLServerException {
        if (index + 1 >= input.length) {
            throw new SQLServerException(null, SQLServerException.getErrString("R_ByteToShortConversion"), null, 0, false);
        }
        final ByteBuffer byteBuffer = ByteBuffer.allocate(2);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.put(input[index]);
        byteBuffer.put(input[index + 1]);
        final short shortVal = byteBuffer.getShort(0);
        return shortVal;
    }
    
    static {
        KeyStoreProviderCommon.version = new byte[] { 1 };
    }
}
