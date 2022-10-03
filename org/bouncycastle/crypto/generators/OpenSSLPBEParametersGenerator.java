package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.PBEParametersGenerator;

public class OpenSSLPBEParametersGenerator extends PBEParametersGenerator
{
    private Digest digest;
    
    public OpenSSLPBEParametersGenerator() {
        this.digest = DigestFactory.createMD5();
    }
    
    public void init(final byte[] array, final byte[] array2) {
        super.init(array, array2, 1);
    }
    
    private byte[] generateDerivedKey(int n) {
        final byte[] array = new byte[this.digest.getDigestSize()];
        final byte[] array2 = new byte[n];
        int n2 = 0;
        while (true) {
            this.digest.update(this.password, 0, this.password.length);
            this.digest.update(this.salt, 0, this.salt.length);
            this.digest.doFinal(array, 0);
            final int n3 = (n > array.length) ? array.length : n;
            System.arraycopy(array, 0, array2, n2, n3);
            n2 += n3;
            n -= n3;
            if (n == 0) {
                break;
            }
            this.digest.reset();
            this.digest.update(array, 0, array.length);
        }
        return array2;
    }
    
    @Override
    public CipherParameters generateDerivedParameters(int n) {
        n /= 8;
        return new KeyParameter(this.generateDerivedKey(n), 0, n);
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
