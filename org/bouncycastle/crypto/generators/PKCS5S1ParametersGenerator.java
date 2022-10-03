package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.PBEParametersGenerator;

public class PKCS5S1ParametersGenerator extends PBEParametersGenerator
{
    private Digest digest;
    
    public PKCS5S1ParametersGenerator(final Digest digest) {
        this.digest = digest;
    }
    
    private byte[] generateDerivedKey() {
        final byte[] array = new byte[this.digest.getDigestSize()];
        this.digest.update(this.password, 0, this.password.length);
        this.digest.update(this.salt, 0, this.salt.length);
        this.digest.doFinal(array, 0);
        for (int i = 1; i < this.iterationCount; ++i) {
            this.digest.update(array, 0, array.length);
            this.digest.doFinal(array, 0);
        }
        return array;
    }
    
    @Override
    public CipherParameters generateDerivedParameters(int n) {
        n /= 8;
        if (n > this.digest.getDigestSize()) {
            throw new IllegalArgumentException("Can't generate a derived key " + n + " bytes long.");
        }
        return new KeyParameter(this.generateDerivedKey(), 0, n);
    }
    
    @Override
    public CipherParameters generateDerivedParameters(int n, int n2) {
        n /= 8;
        n2 /= 8;
        if (n + n2 > this.digest.getDigestSize()) {
            throw new IllegalArgumentException("Can't generate a derived key " + (n + n2) + " bytes long.");
        }
        final byte[] generateDerivedKey = this.generateDerivedKey();
        return new ParametersWithIV(new KeyParameter(generateDerivedKey, 0, n), generateDerivedKey, n, n2);
    }
    
    @Override
    public CipherParameters generateDerivedMacParameters(final int n) {
        return this.generateDerivedParameters(n);
    }
}
