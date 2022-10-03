package org.openjsse.com.sun.crypto.provider;

import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.ProviderException;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.InvalidParameterException;
import java.security.SecureRandom;
import java.util.Arrays;
import org.openjsse.sun.security.internal.spec.TlsPrfParameterSpec;
import javax.crypto.KeyGeneratorSpi;

abstract class TlsPrfGenerator extends KeyGeneratorSpi
{
    private static final byte[] B0;
    static final byte[] LABEL_MASTER_SECRET;
    static final byte[] LABEL_EXTENDED_MASTER_SECRET;
    static final byte[] LABEL_KEY_EXPANSION;
    static final byte[] LABEL_CLIENT_WRITE_KEY;
    static final byte[] LABEL_SERVER_WRITE_KEY;
    static final byte[] LABEL_IV_BLOCK;
    private static final byte[] HMAC_ipad64;
    private static final byte[] HMAC_ipad128;
    private static final byte[] HMAC_opad64;
    private static final byte[] HMAC_opad128;
    static final byte[][] SSL3_CONST;
    private static final String MSG = "TlsPrfGenerator must be initialized using a TlsPrfParameterSpec";
    private TlsPrfParameterSpec spec;
    
    static byte[] genPad(final byte b, final int count) {
        final byte[] padding = new byte[count];
        Arrays.fill(padding, b);
        return padding;
    }
    
    static byte[] concat(final byte[] b1, final byte[] b2) {
        final int n1 = b1.length;
        final int n2 = b2.length;
        final byte[] b3 = new byte[n1 + n2];
        System.arraycopy(b1, 0, b3, 0, n1);
        System.arraycopy(b2, 0, b3, n1, n2);
        return b3;
    }
    
    private static byte[][] genConst() {
        final int n = 10;
        final byte[][] arr = new byte[n][];
        for (int i = 0; i < n; ++i) {
            final byte[] b = new byte[i + 1];
            Arrays.fill(b, (byte)(65 + i));
            arr[i] = b;
        }
        return arr;
    }
    
    public TlsPrfGenerator() {
    }
    
    @Override
    protected void engineInit(final SecureRandom random) {
        throw new InvalidParameterException("TlsPrfGenerator must be initialized using a TlsPrfParameterSpec");
    }
    
    @Override
    protected void engineInit(final AlgorithmParameterSpec params, final SecureRandom random) throws InvalidAlgorithmParameterException {
        if (!(params instanceof TlsPrfParameterSpec)) {
            throw new InvalidAlgorithmParameterException("TlsPrfGenerator must be initialized using a TlsPrfParameterSpec");
        }
        this.spec = (TlsPrfParameterSpec)params;
        final SecretKey key = this.spec.getSecret();
        if (key != null && !"RAW".equals(key.getFormat())) {
            throw new InvalidAlgorithmParameterException("Key encoding format must be RAW");
        }
    }
    
    @Override
    protected void engineInit(final int keysize, final SecureRandom random) {
        throw new InvalidParameterException("TlsPrfGenerator must be initialized using a TlsPrfParameterSpec");
    }
    
    SecretKey engineGenerateKey0(final boolean tls12) {
        if (this.spec == null) {
            throw new IllegalStateException("TlsPrfGenerator must be initialized");
        }
        final SecretKey key = this.spec.getSecret();
        final byte[] secret = (byte[])((key == null) ? null : key.getEncoded());
        try {
            final byte[] labelBytes = this.spec.getLabel().getBytes("UTF8");
            final int n = this.spec.getOutputLength();
            final byte[] prfBytes = tls12 ? doTLS12PRF(secret, labelBytes, this.spec.getSeed(), n, this.spec.getPRFHashAlg(), this.spec.getPRFHashLength(), this.spec.getPRFBlockSize()) : doTLS10PRF(secret, labelBytes, this.spec.getSeed(), n);
            return new SecretKeySpec(prfBytes, "TlsPrf");
        }
        catch (final GeneralSecurityException e) {
            throw new ProviderException("Could not generate PRF", e);
        }
        catch (final UnsupportedEncodingException e2) {
            throw new ProviderException("Could not generate PRF", e2);
        }
    }
    
    static byte[] doTLS12PRF(final byte[] secret, final byte[] labelBytes, final byte[] seed, final int outputLength, final String prfHash, final int prfHashLength, final int prfBlockSize) throws NoSuchAlgorithmException, DigestException {
        if (prfHash == null) {
            throw new NoSuchAlgorithmException("Unspecified PRF algorithm");
        }
        final MessageDigest prfMD = MessageDigest.getInstance(prfHash);
        return doTLS12PRF(secret, labelBytes, seed, outputLength, prfMD, prfHashLength, prfBlockSize);
    }
    
    static byte[] doTLS12PRF(byte[] secret, final byte[] labelBytes, final byte[] seed, final int outputLength, final MessageDigest mdPRF, final int mdPRFLen, final int mdPRFBlockSize) throws DigestException {
        if (secret == null) {
            secret = TlsPrfGenerator.B0;
        }
        if (secret.length > mdPRFBlockSize) {
            secret = mdPRF.digest(secret);
        }
        final byte[] output = new byte[outputLength];
        byte[] ipad = null;
        byte[] opad = null;
        switch (mdPRFBlockSize) {
            case 64: {
                ipad = TlsPrfGenerator.HMAC_ipad64.clone();
                opad = TlsPrfGenerator.HMAC_opad64.clone();
                break;
            }
            case 128: {
                ipad = TlsPrfGenerator.HMAC_ipad128.clone();
                opad = TlsPrfGenerator.HMAC_opad128.clone();
                break;
            }
            default: {
                throw new DigestException("Unexpected block size.");
            }
        }
        expand(mdPRF, mdPRFLen, secret, 0, secret.length, labelBytes, seed, output, ipad, opad);
        return output;
    }
    
    static byte[] doTLS10PRF(final byte[] secret, final byte[] labelBytes, final byte[] seed, final int outputLength) throws NoSuchAlgorithmException, DigestException {
        final MessageDigest md5 = MessageDigest.getInstance("MD5");
        final MessageDigest sha = MessageDigest.getInstance("SHA1");
        return doTLS10PRF(secret, labelBytes, seed, outputLength, md5, sha);
    }
    
    static byte[] doTLS10PRF(byte[] secret, final byte[] labelBytes, final byte[] seed, final int outputLength, final MessageDigest md5, final MessageDigest sha) throws DigestException {
        if (secret == null) {
            secret = TlsPrfGenerator.B0;
        }
        int off = secret.length >> 1;
        final int seclen = off + (secret.length & 0x1);
        byte[] secKey = secret;
        int keyLen = seclen;
        final byte[] output = new byte[outputLength];
        if (seclen > 64) {
            md5.update(secret, 0, seclen);
            secKey = md5.digest();
            keyLen = secKey.length;
        }
        expand(md5, 16, secKey, 0, keyLen, labelBytes, seed, output, TlsPrfGenerator.HMAC_ipad64.clone(), TlsPrfGenerator.HMAC_opad64.clone());
        if (seclen > 64) {
            sha.update(secret, off, seclen);
            secKey = sha.digest();
            keyLen = secKey.length;
            off = 0;
        }
        expand(sha, 20, secKey, off, keyLen, labelBytes, seed, output, TlsPrfGenerator.HMAC_ipad64.clone(), TlsPrfGenerator.HMAC_opad64.clone());
        return output;
    }
    
    private static void expand(final MessageDigest digest, final int hmacSize, final byte[] secret, final int secOff, final int secLen, final byte[] label, final byte[] seed, final byte[] output, final byte[] pad1, final byte[] pad2) throws DigestException {
        for (int i = 0; i < secLen; ++i) {
            final int n = i;
            pad1[n] ^= secret[i + secOff];
            final int n2 = i;
            pad2[n2] ^= secret[i + secOff];
        }
        final byte[] tmp = new byte[hmacSize];
        byte[] aBytes = null;
        int remaining = output.length;
        int ofs = 0;
        while (remaining > 0) {
            digest.update(pad1);
            if (aBytes == null) {
                digest.update(label);
                digest.update(seed);
            }
            else {
                digest.update(aBytes);
            }
            digest.digest(tmp, 0, hmacSize);
            digest.update(pad2);
            digest.update(tmp);
            if (aBytes == null) {
                aBytes = new byte[hmacSize];
            }
            digest.digest(aBytes, 0, hmacSize);
            digest.update(pad1);
            digest.update(aBytes);
            digest.update(label);
            digest.update(seed);
            digest.digest(tmp, 0, hmacSize);
            digest.update(pad2);
            digest.update(tmp);
            digest.digest(tmp, 0, hmacSize);
            final int k = Math.min(hmacSize, remaining);
            for (int j = 0; j < k; ++j) {
                final int n3 = ofs++;
                output[n3] ^= tmp[j];
            }
            remaining -= k;
        }
    }
    
    static {
        B0 = new byte[0];
        LABEL_MASTER_SECRET = new byte[] { 109, 97, 115, 116, 101, 114, 32, 115, 101, 99, 114, 101, 116 };
        LABEL_EXTENDED_MASTER_SECRET = new byte[] { 101, 120, 116, 101, 110, 100, 101, 100, 32, 109, 97, 115, 116, 101, 114, 32, 115, 101, 99, 114, 101, 116 };
        LABEL_KEY_EXPANSION = new byte[] { 107, 101, 121, 32, 101, 120, 112, 97, 110, 115, 105, 111, 110 };
        LABEL_CLIENT_WRITE_KEY = new byte[] { 99, 108, 105, 101, 110, 116, 32, 119, 114, 105, 116, 101, 32, 107, 101, 121 };
        LABEL_SERVER_WRITE_KEY = new byte[] { 115, 101, 114, 118, 101, 114, 32, 119, 114, 105, 116, 101, 32, 107, 101, 121 };
        LABEL_IV_BLOCK = new byte[] { 73, 86, 32, 98, 108, 111, 99, 107 };
        HMAC_ipad64 = genPad((byte)54, 64);
        HMAC_ipad128 = genPad((byte)54, 128);
        HMAC_opad64 = genPad((byte)92, 64);
        HMAC_opad128 = genPad((byte)92, 128);
        SSL3_CONST = genConst();
    }
    
    public static class V12 extends TlsPrfGenerator
    {
        @Override
        protected SecretKey engineGenerateKey() {
            return this.engineGenerateKey0(true);
        }
    }
    
    public static class V10 extends TlsPrfGenerator
    {
        @Override
        protected SecretKey engineGenerateKey() {
            return this.engineGenerateKey0(false);
        }
    }
}
