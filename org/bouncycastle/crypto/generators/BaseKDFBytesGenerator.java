package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.util.Pack;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.ISO18033KDFParameters;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.DigestDerivationFunction;

public class BaseKDFBytesGenerator implements DigestDerivationFunction
{
    private int counterStart;
    private Digest digest;
    private byte[] shared;
    private byte[] iv;
    
    protected BaseKDFBytesGenerator(final int counterStart, final Digest digest) {
        this.counterStart = counterStart;
        this.digest = digest;
    }
    
    public void init(final DerivationParameters derivationParameters) {
        if (derivationParameters instanceof KDFParameters) {
            final KDFParameters kdfParameters = (KDFParameters)derivationParameters;
            this.shared = kdfParameters.getSharedSecret();
            this.iv = kdfParameters.getIV();
        }
        else {
            if (!(derivationParameters instanceof ISO18033KDFParameters)) {
                throw new IllegalArgumentException("KDF parameters required for generator");
            }
            this.shared = ((ISO18033KDFParameters)derivationParameters).getSeed();
            this.iv = null;
        }
    }
    
    public Digest getDigest() {
        return this.digest;
    }
    
    public int generateBytes(final byte[] array, int n, int n2) throws DataLengthException, IllegalArgumentException {
        if (array.length - n2 < n) {
            throw new OutputLengthException("output buffer too small");
        }
        final long n3 = n2;
        final int digestSize = this.digest.getDigestSize();
        if (n3 > 8589934591L) {
            throw new IllegalArgumentException("Output length too large");
        }
        final int n4 = (int)((n3 + digestSize - 1L) / digestSize);
        final byte[] array2 = new byte[this.digest.getDigestSize()];
        final byte[] array3 = new byte[4];
        Pack.intToBigEndian(this.counterStart, array3, 0);
        int n5 = this.counterStart & 0xFFFFFF00;
        for (int i = 0; i < n4; ++i) {
            this.digest.update(this.shared, 0, this.shared.length);
            this.digest.update(array3, 0, array3.length);
            if (this.iv != null) {
                this.digest.update(this.iv, 0, this.iv.length);
            }
            this.digest.doFinal(array2, 0);
            if (n2 > digestSize) {
                System.arraycopy(array2, 0, array, n, digestSize);
                n += digestSize;
                n2 -= digestSize;
            }
            else {
                System.arraycopy(array2, 0, array, n, n2);
            }
            final byte[] array4 = array3;
            final int n6 = 3;
            final byte b = (byte)(array4[n6] + 1);
            array4[n6] = b;
            if (b == 0) {
                n5 += 256;
                Pack.intToBigEndian(n5, array3, 0);
            }
        }
        this.digest.reset();
        return (int)n3;
    }
}
