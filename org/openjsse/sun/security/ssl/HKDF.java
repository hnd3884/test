package org.openjsse.sun.security.ssl;

import javax.crypto.ShortBufferException;
import java.security.InvalidKeyException;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import javax.crypto.Mac;

final class HKDF
{
    private final String hmacAlg;
    private final Mac hmacObj;
    private final int hmacLen;
    
    HKDF(final String hashAlg) throws NoSuchAlgorithmException {
        Objects.requireNonNull(hashAlg, "Must provide underlying HKDF Digest algorithm.");
        this.hmacAlg = "Hmac" + hashAlg.replace("-", "");
        this.hmacObj = JsseJce.getMac(this.hmacAlg);
        this.hmacLen = this.hmacObj.getMacLength();
    }
    
    SecretKey extract(SecretKey salt, final SecretKey inputKey, final String keyAlg) throws InvalidKeyException {
        if (salt == null) {
            salt = new SecretKeySpec(new byte[this.hmacLen], "HKDF-Salt");
        }
        this.hmacObj.init(salt);
        return new SecretKeySpec(this.hmacObj.doFinal(inputKey.getEncoded()), keyAlg);
    }
    
    SecretKey extract(byte[] salt, final SecretKey inputKey, final String keyAlg) throws InvalidKeyException {
        if (salt == null) {
            salt = new byte[this.hmacLen];
        }
        return this.extract(new SecretKeySpec(salt, "HKDF-Salt"), inputKey, keyAlg);
    }
    
    SecretKey expand(final SecretKey pseudoRandKey, byte[] info, final int outLen, final String keyAlg) throws InvalidKeyException {
        Objects.requireNonNull(pseudoRandKey, "A null PRK is not allowed.");
        if (outLen > 255 * this.hmacLen) {
            throw new IllegalArgumentException("Requested output length exceeds maximum length allowed for HKDF expansion");
        }
        this.hmacObj.init(pseudoRandKey);
        if (info == null) {
            info = new byte[0];
        }
        final int rounds = (outLen + this.hmacLen - 1) / this.hmacLen;
        final byte[] kdfOutput = new byte[rounds * this.hmacLen];
        int offset = 0;
        int tLength = 0;
        for (int i = 0; i < rounds; ++i) {
            try {
                this.hmacObj.update(kdfOutput, Math.max(0, offset - this.hmacLen), tLength);
                this.hmacObj.update(info);
                this.hmacObj.update((byte)(i + 1));
                this.hmacObj.doFinal(kdfOutput, offset);
                tLength = this.hmacLen;
                offset += this.hmacLen;
            }
            catch (final ShortBufferException sbe) {
                throw new RuntimeException(sbe);
            }
        }
        return new SecretKeySpec(kdfOutput, 0, outLen, keyAlg);
    }
}
