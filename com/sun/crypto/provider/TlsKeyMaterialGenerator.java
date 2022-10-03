package com.sun.crypto.provider;

import javax.crypto.spec.IvParameterSpec;
import sun.security.internal.spec.TlsKeyMaterialSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.GeneralSecurityException;
import java.security.ProviderException;
import javax.crypto.SecretKey;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.InvalidParameterException;
import java.security.SecureRandom;
import sun.security.internal.spec.TlsKeyMaterialParameterSpec;
import javax.crypto.KeyGeneratorSpi;

public final class TlsKeyMaterialGenerator extends KeyGeneratorSpi
{
    private static final String MSG = "TlsKeyMaterialGenerator must be initialized using a TlsKeyMaterialParameterSpec";
    private TlsKeyMaterialParameterSpec spec;
    private int protocolVersion;
    
    @Override
    protected void engineInit(final SecureRandom secureRandom) {
        throw new InvalidParameterException("TlsKeyMaterialGenerator must be initialized using a TlsKeyMaterialParameterSpec");
    }
    
    @Override
    protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        if (!(algorithmParameterSpec instanceof TlsKeyMaterialParameterSpec)) {
            throw new InvalidAlgorithmParameterException("TlsKeyMaterialGenerator must be initialized using a TlsKeyMaterialParameterSpec");
        }
        this.spec = (TlsKeyMaterialParameterSpec)algorithmParameterSpec;
        if (!"RAW".equals(this.spec.getMasterSecret().getFormat())) {
            throw new InvalidAlgorithmParameterException("Key format must be RAW");
        }
        this.protocolVersion = (this.spec.getMajorVersion() << 8 | this.spec.getMinorVersion());
        if (this.protocolVersion < 768 || this.protocolVersion > 771) {
            throw new InvalidAlgorithmParameterException("Only SSL 3.0, TLS 1.0/1.1/1.2 supported");
        }
    }
    
    @Override
    protected void engineInit(final int n, final SecureRandom secureRandom) {
        throw new InvalidParameterException("TlsKeyMaterialGenerator must be initialized using a TlsKeyMaterialParameterSpec");
    }
    
    @Override
    protected SecretKey engineGenerateKey() {
        if (this.spec == null) {
            throw new IllegalStateException("TlsKeyMaterialGenerator must be initialized");
        }
        try {
            return this.engineGenerateKey0();
        }
        catch (final GeneralSecurityException ex) {
            throw new ProviderException(ex);
        }
    }
    
    private SecretKey engineGenerateKey0() throws GeneralSecurityException {
        final byte[] encoded = this.spec.getMasterSecret().getEncoded();
        final byte[] clientRandom = this.spec.getClientRandom();
        final byte[] serverRandom = this.spec.getServerRandom();
        SecretKey secretKey = null;
        SecretKey secretKey2 = null;
        IvParameterSpec ivParameterSpec = null;
        IvParameterSpec ivParameterSpec2 = null;
        final int macKeyLength = this.spec.getMacKeyLength();
        final int expandedCipherKeyLength = this.spec.getExpandedCipherKeyLength();
        final boolean b = expandedCipherKeyLength != 0;
        final int cipherKeyLength = this.spec.getCipherKeyLength();
        final int ivLength = this.spec.getIvLength();
        final int n = macKeyLength + cipherKeyLength + (b ? 0 : ivLength) << 1;
        final byte[] array = new byte[n];
        MessageDigest messageDigest = null;
        MessageDigest messageDigest2 = null;
        byte[] array2;
        if (this.protocolVersion >= 771) {
            array2 = TlsPrfGenerator.doTLS12PRF(encoded, TlsPrfGenerator.LABEL_KEY_EXPANSION, TlsPrfGenerator.concat(serverRandom, clientRandom), n, this.spec.getPRFHashAlg(), this.spec.getPRFHashLength(), this.spec.getPRFBlockSize());
        }
        else if (this.protocolVersion >= 769) {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest2 = MessageDigest.getInstance("SHA1");
            array2 = TlsPrfGenerator.doTLS10PRF(encoded, TlsPrfGenerator.LABEL_KEY_EXPANSION, TlsPrfGenerator.concat(serverRandom, clientRandom), n, messageDigest, messageDigest2);
        }
        else {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest2 = MessageDigest.getInstance("SHA1");
            array2 = new byte[n];
            final byte[] array3 = new byte[20];
            int n2 = 0;
            for (int i = n; i > 0; i -= 16) {
                messageDigest2.update(TlsPrfGenerator.SSL3_CONST[n2]);
                messageDigest2.update(encoded);
                messageDigest2.update(serverRandom);
                messageDigest2.update(clientRandom);
                messageDigest2.digest(array3, 0, 20);
                messageDigest.update(encoded);
                messageDigest.update(array3);
                if (i >= 16) {
                    messageDigest.digest(array2, n2 << 4, 16);
                }
                else {
                    messageDigest.digest(array3, 0, 16);
                    System.arraycopy(array3, 0, array2, n2 << 4, i);
                }
                ++n2;
            }
        }
        int n3 = 0;
        if (macKeyLength != 0) {
            final byte[] array4 = new byte[macKeyLength];
            System.arraycopy(array2, n3, array4, 0, macKeyLength);
            final int n4 = n3 + macKeyLength;
            secretKey = new SecretKeySpec(array4, "Mac");
            System.arraycopy(array2, n4, array4, 0, macKeyLength);
            n3 = n4 + macKeyLength;
            secretKey2 = new SecretKeySpec(array4, "Mac");
        }
        if (cipherKeyLength == 0) {
            return new TlsKeyMaterialSpec(secretKey, secretKey2);
        }
        final String cipherAlgorithm = this.spec.getCipherAlgorithm();
        final byte[] array5 = new byte[cipherKeyLength];
        System.arraycopy(array2, n3, array5, 0, cipherKeyLength);
        final int n5 = n3 + cipherKeyLength;
        final byte[] array6 = new byte[cipherKeyLength];
        System.arraycopy(array2, n5, array6, 0, cipherKeyLength);
        final int n6 = n5 + cipherKeyLength;
        SecretKeySpec secretKeySpec;
        SecretKeySpec secretKeySpec2;
        if (!b) {
            secretKeySpec = new SecretKeySpec(array5, cipherAlgorithm);
            secretKeySpec2 = new SecretKeySpec(array6, cipherAlgorithm);
            if (ivLength != 0) {
                final byte[] array7 = new byte[ivLength];
                System.arraycopy(array2, n6, array7, 0, ivLength);
                final int n7 = n6 + ivLength;
                ivParameterSpec = new IvParameterSpec(array7);
                System.arraycopy(array2, n7, array7, 0, ivLength);
                ivParameterSpec2 = new IvParameterSpec(array7);
            }
        }
        else {
            if (this.protocolVersion >= 770) {
                throw new RuntimeException("Internal Error:  TLS 1.1+ should not be negotiatingexportable ciphersuites");
            }
            if (this.protocolVersion == 769) {
                final byte[] concat = TlsPrfGenerator.concat(clientRandom, serverRandom);
                secretKeySpec = new SecretKeySpec(TlsPrfGenerator.doTLS10PRF(array5, TlsPrfGenerator.LABEL_CLIENT_WRITE_KEY, concat, expandedCipherKeyLength, messageDigest, messageDigest2), cipherAlgorithm);
                secretKeySpec2 = new SecretKeySpec(TlsPrfGenerator.doTLS10PRF(array6, TlsPrfGenerator.LABEL_SERVER_WRITE_KEY, concat, expandedCipherKeyLength, messageDigest, messageDigest2), cipherAlgorithm);
                if (ivLength != 0) {
                    final byte[] array8 = new byte[ivLength];
                    final byte[] doTLS10PRF = TlsPrfGenerator.doTLS10PRF(null, TlsPrfGenerator.LABEL_IV_BLOCK, concat, ivLength << 1, messageDigest, messageDigest2);
                    System.arraycopy(doTLS10PRF, 0, array8, 0, ivLength);
                    ivParameterSpec = new IvParameterSpec(array8);
                    System.arraycopy(doTLS10PRF, ivLength, array8, 0, ivLength);
                    ivParameterSpec2 = new IvParameterSpec(array8);
                }
            }
            else {
                final byte[] array9 = new byte[expandedCipherKeyLength];
                messageDigest.update(array5);
                messageDigest.update(clientRandom);
                messageDigest.update(serverRandom);
                System.arraycopy(messageDigest.digest(), 0, array9, 0, expandedCipherKeyLength);
                secretKeySpec = new SecretKeySpec(array9, cipherAlgorithm);
                messageDigest.update(array6);
                messageDigest.update(serverRandom);
                messageDigest.update(clientRandom);
                System.arraycopy(messageDigest.digest(), 0, array9, 0, expandedCipherKeyLength);
                secretKeySpec2 = new SecretKeySpec(array9, cipherAlgorithm);
                if (ivLength != 0) {
                    final byte[] array10 = new byte[ivLength];
                    messageDigest.update(clientRandom);
                    messageDigest.update(serverRandom);
                    System.arraycopy(messageDigest.digest(), 0, array10, 0, ivLength);
                    ivParameterSpec = new IvParameterSpec(array10);
                    messageDigest.update(serverRandom);
                    messageDigest.update(clientRandom);
                    System.arraycopy(messageDigest.digest(), 0, array10, 0, ivLength);
                    ivParameterSpec2 = new IvParameterSpec(array10);
                }
            }
        }
        return new TlsKeyMaterialSpec(secretKey, secretKey2, secretKeySpec, ivParameterSpec, secretKeySpec2, ivParameterSpec2);
    }
}
