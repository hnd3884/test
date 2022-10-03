package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.HKDFParameters;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.DerivationFunction;

public class HKDFBytesGenerator implements DerivationFunction
{
    private HMac hMacHash;
    private int hashLen;
    private byte[] info;
    private byte[] currentT;
    private int generatedBytes;
    
    public HKDFBytesGenerator(final Digest digest) {
        this.hMacHash = new HMac(digest);
        this.hashLen = digest.getDigestSize();
    }
    
    public void init(final DerivationParameters derivationParameters) {
        if (!(derivationParameters instanceof HKDFParameters)) {
            throw new IllegalArgumentException("HKDF parameters required for HKDFBytesGenerator");
        }
        final HKDFParameters hkdfParameters = (HKDFParameters)derivationParameters;
        if (hkdfParameters.skipExtract()) {
            this.hMacHash.init(new KeyParameter(hkdfParameters.getIKM()));
        }
        else {
            this.hMacHash.init(this.extract(hkdfParameters.getSalt(), hkdfParameters.getIKM()));
        }
        this.info = hkdfParameters.getInfo();
        this.generatedBytes = 0;
        this.currentT = new byte[this.hashLen];
    }
    
    private KeyParameter extract(final byte[] array, final byte[] array2) {
        if (array == null) {
            this.hMacHash.init(new KeyParameter(new byte[this.hashLen]));
        }
        else {
            this.hMacHash.init(new KeyParameter(array));
        }
        this.hMacHash.update(array2, 0, array2.length);
        final byte[] array3 = new byte[this.hashLen];
        this.hMacHash.doFinal(array3, 0);
        return new KeyParameter(array3);
    }
    
    private void expandNext() throws DataLengthException {
        final int n = this.generatedBytes / this.hashLen + 1;
        if (n >= 256) {
            throw new DataLengthException("HKDF cannot generate more than 255 blocks of HashLen size");
        }
        if (this.generatedBytes != 0) {
            this.hMacHash.update(this.currentT, 0, this.hashLen);
        }
        this.hMacHash.update(this.info, 0, this.info.length);
        this.hMacHash.update((byte)n);
        this.hMacHash.doFinal(this.currentT, 0);
    }
    
    public Digest getDigest() {
        return this.hMacHash.getUnderlyingDigest();
    }
    
    public int generateBytes(final byte[] array, int n, final int n2) throws DataLengthException, IllegalArgumentException {
        if (this.generatedBytes + n2 > 255 * this.hashLen) {
            throw new DataLengthException("HKDF may only be used for 255 * HashLen bytes of output");
        }
        if (this.generatedBytes % this.hashLen == 0) {
            this.expandNext();
        }
        final int n3 = this.generatedBytes % this.hashLen;
        final int min = Math.min(this.hashLen - this.generatedBytes % this.hashLen, n2);
        System.arraycopy(this.currentT, n3, array, n, min);
        this.generatedBytes += min;
        int i;
        int min2;
        for (i = n2 - min, n += min; i > 0; i -= min2, n += min2) {
            this.expandNext();
            min2 = Math.min(this.hashLen, i);
            System.arraycopy(this.currentT, 0, array, n, min2);
            this.generatedBytes += min2;
        }
        return n2;
    }
}
