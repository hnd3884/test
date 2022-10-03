package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.MGFParameters;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.DerivationFunction;

public class MGF1BytesGenerator implements DerivationFunction
{
    private Digest digest;
    private byte[] seed;
    private int hLen;
    
    public MGF1BytesGenerator(final Digest digest) {
        this.digest = digest;
        this.hLen = digest.getDigestSize();
    }
    
    public void init(final DerivationParameters derivationParameters) {
        if (!(derivationParameters instanceof MGFParameters)) {
            throw new IllegalArgumentException("MGF parameters required for MGF1Generator");
        }
        this.seed = ((MGFParameters)derivationParameters).getSeed();
    }
    
    public Digest getDigest() {
        return this.digest;
    }
    
    private void ItoOSP(final int n, final byte[] array) {
        array[0] = (byte)(n >>> 24);
        array[1] = (byte)(n >>> 16);
        array[2] = (byte)(n >>> 8);
        array[3] = (byte)(n >>> 0);
    }
    
    public int generateBytes(final byte[] array, final int n, final int n2) throws DataLengthException, IllegalArgumentException {
        if (array.length - n2 < n) {
            throw new OutputLengthException("output buffer too small");
        }
        final byte[] array2 = new byte[this.hLen];
        final byte[] array3 = new byte[4];
        int n3 = 0;
        this.digest.reset();
        if (n2 > this.hLen) {
            do {
                this.ItoOSP(n3, array3);
                this.digest.update(this.seed, 0, this.seed.length);
                this.digest.update(array3, 0, array3.length);
                this.digest.doFinal(array2, 0);
                System.arraycopy(array2, 0, array, n + n3 * this.hLen, this.hLen);
            } while (++n3 < n2 / this.hLen);
        }
        if (n3 * this.hLen < n2) {
            this.ItoOSP(n3, array3);
            this.digest.update(this.seed, 0, this.seed.length);
            this.digest.update(array3, 0, array3.length);
            this.digest.doFinal(array2, 0);
            System.arraycopy(array2, 0, array, n + n3 * this.hLen, n2 - n3 * this.hLen);
        }
        return n2;
    }
}
