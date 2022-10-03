package org.bouncycastle.cert.crmf.bc;

import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.params.MGFParameters;
import org.bouncycastle.crypto.generators.MGF1BytesGenerator;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.Digest;
import java.security.SecureRandom;
import org.bouncycastle.cert.crmf.EncryptedValuePadder;

public class BcFixedLengthMGF1Padder implements EncryptedValuePadder
{
    private int length;
    private SecureRandom random;
    private Digest dig;
    
    public BcFixedLengthMGF1Padder(final int n) {
        this(n, null);
    }
    
    public BcFixedLengthMGF1Padder(final int length, final SecureRandom random) {
        this.dig = (Digest)new SHA1Digest();
        this.length = length;
        this.random = random;
    }
    
    public byte[] getPaddedData(final byte[] array) {
        final byte[] array2 = new byte[this.length];
        final byte[] array3 = new byte[this.dig.getDigestSize()];
        final byte[] array4 = new byte[this.length - this.dig.getDigestSize()];
        if (this.random == null) {
            this.random = new SecureRandom();
        }
        this.random.nextBytes(array3);
        final MGF1BytesGenerator mgf1BytesGenerator = new MGF1BytesGenerator(this.dig);
        mgf1BytesGenerator.init((DerivationParameters)new MGFParameters(array3));
        mgf1BytesGenerator.generateBytes(array4, 0, array4.length);
        System.arraycopy(array3, 0, array2, 0, array3.length);
        System.arraycopy(array, 0, array2, array3.length, array.length);
        for (int i = array3.length + array.length + 1; i != array2.length; ++i) {
            array2[i] = (byte)(1 + this.random.nextInt(255));
        }
        for (int j = 0; j != array4.length; ++j) {
            final byte[] array5 = array2;
            final int n = j + array3.length;
            array5[n] ^= array4[j];
        }
        return array2;
    }
    
    public byte[] getUnpaddedData(final byte[] array) {
        final byte[] array2 = new byte[this.dig.getDigestSize()];
        final byte[] array3 = new byte[this.length - this.dig.getDigestSize()];
        System.arraycopy(array, 0, array2, 0, array2.length);
        final MGF1BytesGenerator mgf1BytesGenerator = new MGF1BytesGenerator(this.dig);
        mgf1BytesGenerator.init((DerivationParameters)new MGFParameters(array2));
        mgf1BytesGenerator.generateBytes(array3, 0, array3.length);
        for (int i = 0; i != array3.length; ++i) {
            final int n = i + array2.length;
            array[n] ^= array3[i];
        }
        int n2 = 0;
        for (int j = array.length - 1; j != array2.length; --j) {
            if (array[j] == 0) {
                n2 = j;
                break;
            }
        }
        if (n2 == 0) {
            throw new IllegalStateException("bad padding in encoding");
        }
        final byte[] array4 = new byte[n2 - array2.length];
        System.arraycopy(array, array2.length, array4, 0, array4.length);
        return array4;
    }
}
