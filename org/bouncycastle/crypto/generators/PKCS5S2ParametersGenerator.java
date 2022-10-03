package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.PBEParametersGenerator;

public class PKCS5S2ParametersGenerator extends PBEParametersGenerator
{
    private Mac hMac;
    private byte[] state;
    
    public PKCS5S2ParametersGenerator() {
        this(DigestFactory.createSHA1());
    }
    
    public PKCS5S2ParametersGenerator(final Digest digest) {
        this.hMac = new HMac(digest);
        this.state = new byte[this.hMac.getMacSize()];
    }
    
    private void F(final byte[] array, final int n, final byte[] array2, final byte[] array3, final int n2) {
        if (n == 0) {
            throw new IllegalArgumentException("iteration count must be at least 1.");
        }
        if (array != null) {
            this.hMac.update(array, 0, array.length);
        }
        this.hMac.update(array2, 0, array2.length);
        this.hMac.doFinal(this.state, 0);
        System.arraycopy(this.state, 0, array3, n2, this.state.length);
        for (int i = 1; i < n; ++i) {
            this.hMac.update(this.state, 0, this.state.length);
            this.hMac.doFinal(this.state, 0);
            for (int j = 0; j != this.state.length; ++j) {
                final int n3 = n2 + j;
                array3[n3] ^= this.state[j];
            }
        }
    }
    
    private byte[] generateDerivedKey(final int n) {
        final int macSize = this.hMac.getMacSize();
        final int n2 = (n + macSize - 1) / macSize;
        final byte[] array = new byte[4];
        final byte[] array2 = new byte[n2 * macSize];
        int n3 = 0;
        this.hMac.init(new KeyParameter(this.password));
        for (int i = 1; i <= n2; ++i) {
            int n4 = 3;
            while (true) {
                final byte[] array3 = array;
                final int n5 = n4;
                final byte b = (byte)(array3[n5] + 1);
                array3[n5] = b;
                if (b != 0) {
                    break;
                }
                --n4;
            }
            this.F(this.salt, this.iterationCount, array, array2, n3);
            n3 += macSize;
        }
        return array2;
    }
    
    @Override
    public CipherParameters generateDerivedParameters(int n) {
        n /= 8;
        return new KeyParameter(Arrays.copyOfRange(this.generateDerivedKey(n), 0, n), 0, n);
    }
    
    @Override
    public CipherParameters generateDerivedParameters(int n, int n2) {
        n /= 8;
        n2 /= 8;
        final byte[] generateDerivedKey = this.generateDerivedKey(n + n2);
        return new ParametersWithIV(new KeyParameter(generateDerivedKey, 0, n), generateDerivedKey, n, n2);
    }
    
    @Override
    public CipherParameters generateDerivedMacParameters(final int n) {
        return this.generateDerivedParameters(n);
    }
}
