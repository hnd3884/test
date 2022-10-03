package org.openjsse.com.sun.crypto.provider;

import javax.crypto.spec.IvParameterSpec;
import org.openjsse.sun.security.internal.spec.TlsKeyMaterialSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.GeneralSecurityException;
import java.security.ProviderException;
import javax.crypto.SecretKey;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.InvalidParameterException;
import java.security.SecureRandom;
import org.openjsse.sun.security.internal.spec.TlsKeyMaterialParameterSpec;
import javax.crypto.KeyGeneratorSpi;

public final class TlsKeyMaterialGenerator extends KeyGeneratorSpi
{
    private static final String MSG = "TlsKeyMaterialGenerator must be initialized using a TlsKeyMaterialParameterSpec";
    private TlsKeyMaterialParameterSpec spec;
    private int protocolVersion;
    
    @Override
    protected void engineInit(final SecureRandom random) {
        throw new InvalidParameterException("TlsKeyMaterialGenerator must be initialized using a TlsKeyMaterialParameterSpec");
    }
    
    @Override
    protected void engineInit(final AlgorithmParameterSpec params, final SecureRandom random) throws InvalidAlgorithmParameterException {
        if (!(params instanceof TlsKeyMaterialParameterSpec)) {
            throw new InvalidAlgorithmParameterException("TlsKeyMaterialGenerator must be initialized using a TlsKeyMaterialParameterSpec");
        }
        this.spec = (TlsKeyMaterialParameterSpec)params;
        if (!"RAW".equals(this.spec.getMasterSecret().getFormat())) {
            throw new InvalidAlgorithmParameterException("Key format must be RAW");
        }
        this.protocolVersion = (this.spec.getMajorVersion() << 8 | this.spec.getMinorVersion());
        if (this.protocolVersion < 768 || this.protocolVersion > 771) {
            throw new InvalidAlgorithmParameterException("Only SSL 3.0, TLS 1.0/1.1/1.2 supported");
        }
    }
    
    @Override
    protected void engineInit(final int keysize, final SecureRandom random) {
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
        catch (final GeneralSecurityException e) {
            throw new ProviderException(e);
        }
    }
    
    private SecretKey engineGenerateKey0() throws GeneralSecurityException {
        final byte[] masterSecret = this.spec.getMasterSecret().getEncoded();
        final byte[] clientRandom = this.spec.getClientRandom();
        final byte[] serverRandom = this.spec.getServerRandom();
        SecretKey clientMacKey = null;
        SecretKey serverMacKey = null;
        SecretKey clientCipherKey = null;
        SecretKey serverCipherKey = null;
        IvParameterSpec clientIv = null;
        IvParameterSpec serverIv = null;
        final int macLength = this.spec.getMacKeyLength();
        final int expandedKeyLength = this.spec.getExpandedCipherKeyLength();
        final boolean isExportable = expandedKeyLength != 0;
        final int keyLength = this.spec.getCipherKeyLength();
        final int ivLength = this.spec.getIvLength();
        int keyBlockLen = macLength + keyLength + (isExportable ? 0 : ivLength);
        keyBlockLen <<= 1;
        byte[] keyBlock = new byte[keyBlockLen];
        MessageDigest md5 = null;
        MessageDigest sha = null;
        if (this.protocolVersion >= 771) {
            final byte[] seed = TlsPrfGenerator.concat(serverRandom, clientRandom);
            keyBlock = TlsPrfGenerator.doTLS12PRF(masterSecret, TlsPrfGenerator.LABEL_KEY_EXPANSION, seed, keyBlockLen, this.spec.getPRFHashAlg(), this.spec.getPRFHashLength(), this.spec.getPRFBlockSize());
        }
        else if (this.protocolVersion >= 769) {
            md5 = MessageDigest.getInstance("MD5");
            sha = MessageDigest.getInstance("SHA1");
            final byte[] seed = TlsPrfGenerator.concat(serverRandom, clientRandom);
            keyBlock = TlsPrfGenerator.doTLS10PRF(masterSecret, TlsPrfGenerator.LABEL_KEY_EXPANSION, seed, keyBlockLen, md5, sha);
        }
        else {
            md5 = MessageDigest.getInstance("MD5");
            sha = MessageDigest.getInstance("SHA1");
            keyBlock = new byte[keyBlockLen];
            final byte[] tmp = new byte[20];
            int i = 0;
            for (int remaining = keyBlockLen; remaining > 0; remaining -= 16) {
                sha.update(TlsPrfGenerator.SSL3_CONST[i]);
                sha.update(masterSecret);
                sha.update(serverRandom);
                sha.update(clientRandom);
                sha.digest(tmp, 0, 20);
                md5.update(masterSecret);
                md5.update(tmp);
                if (remaining >= 16) {
                    md5.digest(keyBlock, i << 4, 16);
                }
                else {
                    md5.digest(tmp, 0, 16);
                    System.arraycopy(tmp, 0, keyBlock, i << 4, remaining);
                }
                ++i;
            }
        }
        int ofs = 0;
        if (macLength != 0) {
            final byte[] tmp2 = new byte[macLength];
            System.arraycopy(keyBlock, ofs, tmp2, 0, macLength);
            ofs += macLength;
            clientMacKey = new SecretKeySpec(tmp2, "Mac");
            System.arraycopy(keyBlock, ofs, tmp2, 0, macLength);
            ofs += macLength;
            serverMacKey = new SecretKeySpec(tmp2, "Mac");
        }
        if (keyLength == 0) {
            return new TlsKeyMaterialSpec(clientMacKey, serverMacKey);
        }
        final String alg = this.spec.getCipherAlgorithm();
        final byte[] clientKeyBytes = new byte[keyLength];
        System.arraycopy(keyBlock, ofs, clientKeyBytes, 0, keyLength);
        ofs += keyLength;
        final byte[] serverKeyBytes = new byte[keyLength];
        System.arraycopy(keyBlock, ofs, serverKeyBytes, 0, keyLength);
        ofs += keyLength;
        if (!isExportable) {
            clientCipherKey = new SecretKeySpec(clientKeyBytes, alg);
            serverCipherKey = new SecretKeySpec(serverKeyBytes, alg);
            if (ivLength != 0) {
                final byte[] tmp3 = new byte[ivLength];
                System.arraycopy(keyBlock, ofs, tmp3, 0, ivLength);
                ofs += ivLength;
                clientIv = new IvParameterSpec(tmp3);
                System.arraycopy(keyBlock, ofs, tmp3, 0, ivLength);
                ofs += ivLength;
                serverIv = new IvParameterSpec(tmp3);
            }
        }
        else {
            if (this.protocolVersion >= 770) {
                throw new RuntimeException("Internal Error:  TLS 1.1+ should not be negotiatingexportable ciphersuites");
            }
            if (this.protocolVersion == 769) {
                final byte[] seed2 = TlsPrfGenerator.concat(clientRandom, serverRandom);
                byte[] tmp4 = TlsPrfGenerator.doTLS10PRF(clientKeyBytes, TlsPrfGenerator.LABEL_CLIENT_WRITE_KEY, seed2, expandedKeyLength, md5, sha);
                clientCipherKey = new SecretKeySpec(tmp4, alg);
                tmp4 = TlsPrfGenerator.doTLS10PRF(serverKeyBytes, TlsPrfGenerator.LABEL_SERVER_WRITE_KEY, seed2, expandedKeyLength, md5, sha);
                serverCipherKey = new SecretKeySpec(tmp4, alg);
                if (ivLength != 0) {
                    tmp4 = new byte[ivLength];
                    final byte[] block = TlsPrfGenerator.doTLS10PRF(null, TlsPrfGenerator.LABEL_IV_BLOCK, seed2, ivLength << 1, md5, sha);
                    System.arraycopy(block, 0, tmp4, 0, ivLength);
                    clientIv = new IvParameterSpec(tmp4);
                    System.arraycopy(block, ivLength, tmp4, 0, ivLength);
                    serverIv = new IvParameterSpec(tmp4);
                }
            }
            else {
                byte[] tmp3 = new byte[expandedKeyLength];
                md5.update(clientKeyBytes);
                md5.update(clientRandom);
                md5.update(serverRandom);
                System.arraycopy(md5.digest(), 0, tmp3, 0, expandedKeyLength);
                clientCipherKey = new SecretKeySpec(tmp3, alg);
                md5.update(serverKeyBytes);
                md5.update(serverRandom);
                md5.update(clientRandom);
                System.arraycopy(md5.digest(), 0, tmp3, 0, expandedKeyLength);
                serverCipherKey = new SecretKeySpec(tmp3, alg);
                if (ivLength != 0) {
                    tmp3 = new byte[ivLength];
                    md5.update(clientRandom);
                    md5.update(serverRandom);
                    System.arraycopy(md5.digest(), 0, tmp3, 0, ivLength);
                    clientIv = new IvParameterSpec(tmp3);
                    md5.update(serverRandom);
                    md5.update(clientRandom);
                    System.arraycopy(md5.digest(), 0, tmp3, 0, ivLength);
                    serverIv = new IvParameterSpec(tmp3);
                }
            }
        }
        return new TlsKeyMaterialSpec(clientMacKey, serverMacKey, clientCipherKey, clientIv, serverCipherKey, serverIv);
    }
}
