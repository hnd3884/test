package sun.security.ssl;

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
    
    HKDF(final String s) throws NoSuchAlgorithmException {
        Objects.requireNonNull(s, "Must provide underlying HKDF Digest algorithm.");
        this.hmacAlg = "Hmac" + s.replace("-", "");
        this.hmacObj = JsseJce.getMac(this.hmacAlg);
        this.hmacLen = this.hmacObj.getMacLength();
    }
    
    SecretKey extract(SecretKey secretKey, final SecretKey secretKey2, final String s) throws InvalidKeyException {
        if (secretKey == null) {
            secretKey = new SecretKeySpec(new byte[this.hmacLen], "HKDF-Salt");
        }
        this.hmacObj.init(secretKey);
        return new SecretKeySpec(this.hmacObj.doFinal(secretKey2.getEncoded()), s);
    }
    
    SecretKey extract(byte[] array, final SecretKey secretKey, final String s) throws InvalidKeyException {
        if (array == null) {
            array = new byte[this.hmacLen];
        }
        return this.extract(new SecretKeySpec(array, "HKDF-Salt"), secretKey, s);
    }
    
    SecretKey expand(final SecretKey secretKey, byte[] array, final int n, final String s) throws InvalidKeyException {
        Objects.requireNonNull(secretKey, "A null PRK is not allowed.");
        if (n > 255 * this.hmacLen) {
            throw new IllegalArgumentException("Requested output length exceeds maximum length allowed for HKDF expansion");
        }
        this.hmacObj.init(secretKey);
        if (array == null) {
            array = new byte[0];
        }
        final int n2 = (n + this.hmacLen - 1) / this.hmacLen;
        final byte[] array2 = new byte[n2 * this.hmacLen];
        int n3 = 0;
        int hmacLen = 0;
        for (int i = 0; i < n2; ++i) {
            try {
                this.hmacObj.update(array2, Math.max(0, n3 - this.hmacLen), hmacLen);
                this.hmacObj.update(array);
                this.hmacObj.update((byte)(i + 1));
                this.hmacObj.doFinal(array2, n3);
                hmacLen = this.hmacLen;
                n3 += this.hmacLen;
            }
            catch (final ShortBufferException ex) {
                throw new RuntimeException(ex);
            }
        }
        return new SecretKeySpec(array2, 0, n, s);
    }
}
