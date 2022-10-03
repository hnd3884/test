package org.bouncycastle.crypto.macs;

import org.bouncycastle.util.Integers;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.ExtendedDigest;
import java.util.Hashtable;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Mac;

public class HMac implements Mac
{
    private static final byte IPAD = 54;
    private static final byte OPAD = 92;
    private Digest digest;
    private int digestSize;
    private int blockLength;
    private Memoable ipadState;
    private Memoable opadState;
    private byte[] inputPad;
    private byte[] outputBuf;
    private static Hashtable blockLengths;
    
    private static int getByteLength(final Digest digest) {
        if (digest instanceof ExtendedDigest) {
            return ((ExtendedDigest)digest).getByteLength();
        }
        final Integer n = HMac.blockLengths.get(digest.getAlgorithmName());
        if (n == null) {
            throw new IllegalArgumentException("unknown digest passed: " + digest.getAlgorithmName());
        }
        return n;
    }
    
    public HMac(final Digest digest) {
        this(digest, getByteLength(digest));
    }
    
    private HMac(final Digest digest, final int blockLength) {
        this.digest = digest;
        this.digestSize = digest.getDigestSize();
        this.blockLength = blockLength;
        this.inputPad = new byte[this.blockLength];
        this.outputBuf = new byte[this.blockLength + this.digestSize];
    }
    
    public String getAlgorithmName() {
        return this.digest.getAlgorithmName() + "/HMAC";
    }
    
    public Digest getUnderlyingDigest() {
        return this.digest;
    }
    
    public void init(final CipherParameters cipherParameters) {
        this.digest.reset();
        final byte[] key = ((KeyParameter)cipherParameters).getKey();
        int n = key.length;
        if (n > this.blockLength) {
            this.digest.update(key, 0, n);
            this.digest.doFinal(this.inputPad, 0);
            n = this.digestSize;
        }
        else {
            System.arraycopy(key, 0, this.inputPad, 0, n);
        }
        for (int i = n; i < this.inputPad.length; ++i) {
            this.inputPad[i] = 0;
        }
        System.arraycopy(this.inputPad, 0, this.outputBuf, 0, this.blockLength);
        xorPad(this.inputPad, this.blockLength, (byte)54);
        xorPad(this.outputBuf, this.blockLength, (byte)92);
        if (this.digest instanceof Memoable) {
            this.opadState = ((Memoable)this.digest).copy();
            ((Digest)this.opadState).update(this.outputBuf, 0, this.blockLength);
        }
        this.digest.update(this.inputPad, 0, this.inputPad.length);
        if (this.digest instanceof Memoable) {
            this.ipadState = ((Memoable)this.digest).copy();
        }
    }
    
    public int getMacSize() {
        return this.digestSize;
    }
    
    public void update(final byte b) {
        this.digest.update(b);
    }
    
    public void update(final byte[] array, final int n, final int n2) {
        this.digest.update(array, n, n2);
    }
    
    public int doFinal(final byte[] array, final int n) {
        this.digest.doFinal(this.outputBuf, this.blockLength);
        if (this.opadState != null) {
            ((Memoable)this.digest).reset(this.opadState);
            this.digest.update(this.outputBuf, this.blockLength, this.digest.getDigestSize());
        }
        else {
            this.digest.update(this.outputBuf, 0, this.outputBuf.length);
        }
        final int doFinal = this.digest.doFinal(array, n);
        for (int i = this.blockLength; i < this.outputBuf.length; ++i) {
            this.outputBuf[i] = 0;
        }
        if (this.ipadState != null) {
            ((Memoable)this.digest).reset(this.ipadState);
        }
        else {
            this.digest.update(this.inputPad, 0, this.inputPad.length);
        }
        return doFinal;
    }
    
    public void reset() {
        this.digest.reset();
        this.digest.update(this.inputPad, 0, this.inputPad.length);
    }
    
    private static void xorPad(final byte[] array, final int n, final byte b) {
        for (int i = 0; i < n; ++i) {
            final int n2 = i;
            array[n2] ^= b;
        }
    }
    
    static {
        (HMac.blockLengths = new Hashtable()).put("GOST3411", Integers.valueOf(32));
        HMac.blockLengths.put("MD2", Integers.valueOf(16));
        HMac.blockLengths.put("MD4", Integers.valueOf(64));
        HMac.blockLengths.put("MD5", Integers.valueOf(64));
        HMac.blockLengths.put("RIPEMD128", Integers.valueOf(64));
        HMac.blockLengths.put("RIPEMD160", Integers.valueOf(64));
        HMac.blockLengths.put("SHA-1", Integers.valueOf(64));
        HMac.blockLengths.put("SHA-224", Integers.valueOf(64));
        HMac.blockLengths.put("SHA-256", Integers.valueOf(64));
        HMac.blockLengths.put("SHA-384", Integers.valueOf(128));
        HMac.blockLengths.put("SHA-512", Integers.valueOf(128));
        HMac.blockLengths.put("Tiger", Integers.valueOf(64));
        HMac.blockLengths.put("Whirlpool", Integers.valueOf(64));
    }
}
