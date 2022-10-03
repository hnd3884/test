package org.bouncycastle.crypto.tls;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Mac;

public class SSL3Mac implements Mac
{
    private static final byte IPAD_BYTE = 54;
    private static final byte OPAD_BYTE = 92;
    static final byte[] IPAD;
    static final byte[] OPAD;
    private Digest digest;
    private int padLength;
    private byte[] secret;
    
    public SSL3Mac(final Digest digest) {
        this.digest = digest;
        if (digest.getDigestSize() == 20) {
            this.padLength = 40;
        }
        else {
            this.padLength = 48;
        }
    }
    
    public String getAlgorithmName() {
        return this.digest.getAlgorithmName() + "/SSL3MAC";
    }
    
    public Digest getUnderlyingDigest() {
        return this.digest;
    }
    
    public void init(final CipherParameters cipherParameters) {
        this.secret = Arrays.clone(((KeyParameter)cipherParameters).getKey());
        this.reset();
    }
    
    public int getMacSize() {
        return this.digest.getDigestSize();
    }
    
    public void update(final byte b) {
        this.digest.update(b);
    }
    
    public void update(final byte[] array, final int n, final int n2) {
        this.digest.update(array, n, n2);
    }
    
    public int doFinal(final byte[] array, final int n) {
        final byte[] array2 = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(array2, 0);
        this.digest.update(this.secret, 0, this.secret.length);
        this.digest.update(SSL3Mac.OPAD, 0, this.padLength);
        this.digest.update(array2, 0, array2.length);
        final int doFinal = this.digest.doFinal(array, n);
        this.reset();
        return doFinal;
    }
    
    public void reset() {
        this.digest.reset();
        this.digest.update(this.secret, 0, this.secret.length);
        this.digest.update(SSL3Mac.IPAD, 0, this.padLength);
    }
    
    private static byte[] genPad(final byte b, final int n) {
        final byte[] array = new byte[n];
        Arrays.fill(array, b);
        return array;
    }
    
    static {
        IPAD = genPad((byte)54, 48);
        OPAD = genPad((byte)92, 48);
    }
}
