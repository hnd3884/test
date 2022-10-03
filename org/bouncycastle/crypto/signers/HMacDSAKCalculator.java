package org.bouncycastle.crypto.signers;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Arrays;
import java.security.SecureRandom;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.macs.HMac;
import java.math.BigInteger;

public class HMacDSAKCalculator implements DSAKCalculator
{
    private static final BigInteger ZERO;
    private final HMac hMac;
    private final byte[] K;
    private final byte[] V;
    private BigInteger n;
    
    public HMacDSAKCalculator(final Digest digest) {
        this.hMac = new HMac(digest);
        this.V = new byte[this.hMac.getMacSize()];
        this.K = new byte[this.hMac.getMacSize()];
    }
    
    public boolean isDeterministic() {
        return true;
    }
    
    public void init(final BigInteger bigInteger, final SecureRandom secureRandom) {
        throw new IllegalStateException("Operation not supported");
    }
    
    public void init(final BigInteger n, final BigInteger bigInteger, final byte[] array) {
        this.n = n;
        Arrays.fill(this.V, (byte)1);
        Arrays.fill(this.K, (byte)0);
        final byte[] array2 = new byte[(n.bitLength() + 7) / 8];
        final byte[] unsignedByteArray = BigIntegers.asUnsignedByteArray(bigInteger);
        System.arraycopy(unsignedByteArray, 0, array2, array2.length - unsignedByteArray.length, unsignedByteArray.length);
        final byte[] array3 = new byte[(n.bitLength() + 7) / 8];
        BigInteger bigInteger2 = this.bitsToInt(array);
        if (bigInteger2.compareTo(n) >= 0) {
            bigInteger2 = bigInteger2.subtract(n);
        }
        final byte[] unsignedByteArray2 = BigIntegers.asUnsignedByteArray(bigInteger2);
        System.arraycopy(unsignedByteArray2, 0, array3, array3.length - unsignedByteArray2.length, unsignedByteArray2.length);
        this.hMac.init(new KeyParameter(this.K));
        this.hMac.update(this.V, 0, this.V.length);
        this.hMac.update((byte)0);
        this.hMac.update(array2, 0, array2.length);
        this.hMac.update(array3, 0, array3.length);
        this.hMac.doFinal(this.K, 0);
        this.hMac.init(new KeyParameter(this.K));
        this.hMac.update(this.V, 0, this.V.length);
        this.hMac.doFinal(this.V, 0);
        this.hMac.update(this.V, 0, this.V.length);
        this.hMac.update((byte)1);
        this.hMac.update(array2, 0, array2.length);
        this.hMac.update(array3, 0, array3.length);
        this.hMac.doFinal(this.K, 0);
        this.hMac.init(new KeyParameter(this.K));
        this.hMac.update(this.V, 0, this.V.length);
        this.hMac.doFinal(this.V, 0);
    }
    
    public BigInteger nextK() {
        final byte[] array = new byte[(this.n.bitLength() + 7) / 8];
        BigInteger bitsToInt;
        while (true) {
            int min;
            for (int i = 0; i < array.length; i += min) {
                this.hMac.update(this.V, 0, this.V.length);
                this.hMac.doFinal(this.V, 0);
                min = Math.min(array.length - i, this.V.length);
                System.arraycopy(this.V, 0, array, i, min);
            }
            bitsToInt = this.bitsToInt(array);
            if (bitsToInt.compareTo(HMacDSAKCalculator.ZERO) > 0 && bitsToInt.compareTo(this.n) < 0) {
                break;
            }
            this.hMac.update(this.V, 0, this.V.length);
            this.hMac.update((byte)0);
            this.hMac.doFinal(this.K, 0);
            this.hMac.init(new KeyParameter(this.K));
            this.hMac.update(this.V, 0, this.V.length);
            this.hMac.doFinal(this.V, 0);
        }
        return bitsToInt;
    }
    
    private BigInteger bitsToInt(final byte[] array) {
        BigInteger shiftRight = new BigInteger(1, array);
        if (array.length * 8 > this.n.bitLength()) {
            shiftRight = shiftRight.shiftRight(array.length * 8 - this.n.bitLength());
        }
        return shiftRight;
    }
    
    static {
        ZERO = BigInteger.valueOf(0L);
    }
}
