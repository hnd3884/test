package org.bouncycastle.crypto.agreement.kdf;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.DerivationFunction;

public class ConcatenationKDFGenerator implements DerivationFunction
{
    private Digest digest;
    private byte[] shared;
    private byte[] otherInfo;
    private int hLen;
    
    public ConcatenationKDFGenerator(final Digest digest) {
        this.digest = digest;
        this.hLen = digest.getDigestSize();
    }
    
    public void init(final DerivationParameters derivationParameters) {
        if (derivationParameters instanceof KDFParameters) {
            final KDFParameters kdfParameters = (KDFParameters)derivationParameters;
            this.shared = kdfParameters.getSharedSecret();
            this.otherInfo = kdfParameters.getIV();
            return;
        }
        throw new IllegalArgumentException("KDF parameters required for generator");
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
        int n3 = 1;
        int n4 = 0;
        this.digest.reset();
        if (n2 > this.hLen) {
            do {
                this.ItoOSP(n3, array3);
                this.digest.update(array3, 0, array3.length);
                this.digest.update(this.shared, 0, this.shared.length);
                this.digest.update(this.otherInfo, 0, this.otherInfo.length);
                this.digest.doFinal(array2, 0);
                System.arraycopy(array2, 0, array, n + n4, this.hLen);
                n4 += this.hLen;
            } while (n3++ < n2 / this.hLen);
        }
        if (n4 < n2) {
            this.ItoOSP(n3, array3);
            this.digest.update(array3, 0, array3.length);
            this.digest.update(this.shared, 0, this.shared.length);
            this.digest.update(this.otherInfo, 0, this.otherInfo.length);
            this.digest.doFinal(array2, 0);
            System.arraycopy(array2, 0, array, n + n4, n2 - n4);
        }
        return n2;
    }
}
