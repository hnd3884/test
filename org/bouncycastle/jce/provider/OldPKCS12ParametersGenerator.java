package org.bouncycastle.jce.provider;

import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.PBEParametersGenerator;

class OldPKCS12ParametersGenerator extends PBEParametersGenerator
{
    public static final int KEY_MATERIAL = 1;
    public static final int IV_MATERIAL = 2;
    public static final int MAC_MATERIAL = 3;
    private Digest digest;
    private int u;
    private int v;
    
    public OldPKCS12ParametersGenerator(final Digest digest) {
        this.digest = digest;
        if (digest instanceof MD5Digest) {
            this.u = 16;
            this.v = 64;
        }
        else if (digest instanceof SHA1Digest) {
            this.u = 20;
            this.v = 64;
        }
        else {
            if (!(digest instanceof RIPEMD160Digest)) {
                throw new IllegalArgumentException("Digest " + digest.getAlgorithmName() + " unsupported");
            }
            this.u = 20;
            this.v = 64;
        }
    }
    
    private void adjust(final byte[] array, final int n, final byte[] array2) {
        final int n2 = (array2[array2.length - 1] & 0xFF) + (array[n + array2.length - 1] & 0xFF) + 1;
        array[n + array2.length - 1] = (byte)n2;
        int n3 = n2 >>> 8;
        for (int i = array2.length - 2; i >= 0; --i) {
            final int n4 = n3 + ((array2[i] & 0xFF) + (array[n + i] & 0xFF));
            array[n + i] = (byte)n4;
            n3 = n4 >>> 8;
        }
    }
    
    private byte[] generateDerivedKey(final int n, final int n2) {
        final byte[] array = new byte[this.v];
        final byte[] array2 = new byte[n2];
        for (int i = 0; i != array.length; ++i) {
            array[i] = (byte)n;
        }
        byte[] array3;
        if (this.salt != null && this.salt.length != 0) {
            array3 = new byte[this.v * ((this.salt.length + this.v - 1) / this.v)];
            for (int j = 0; j != array3.length; ++j) {
                array3[j] = this.salt[j % this.salt.length];
            }
        }
        else {
            array3 = new byte[0];
        }
        byte[] array4;
        if (this.password != null && this.password.length != 0) {
            array4 = new byte[this.v * ((this.password.length + this.v - 1) / this.v)];
            for (int k = 0; k != array4.length; ++k) {
                array4[k] = this.password[k % this.password.length];
            }
        }
        else {
            array4 = new byte[0];
        }
        final byte[] array5 = new byte[array3.length + array4.length];
        System.arraycopy(array3, 0, array5, 0, array3.length);
        System.arraycopy(array4, 0, array5, array3.length, array4.length);
        final byte[] array6 = new byte[this.v];
        for (int n3 = (n2 + this.u - 1) / this.u, l = 1; l <= n3; ++l) {
            final byte[] array7 = new byte[this.u];
            this.digest.update(array, 0, array.length);
            this.digest.update(array5, 0, array5.length);
            this.digest.doFinal(array7, 0);
            for (int n4 = 1; n4 != this.iterationCount; ++n4) {
                this.digest.update(array7, 0, array7.length);
                this.digest.doFinal(array7, 0);
            }
            for (int n5 = 0; n5 != array6.length; ++n5) {
                array6[l] = array7[n5 % array7.length];
            }
            for (int n6 = 0; n6 != array5.length / this.v; ++n6) {
                this.adjust(array5, n6 * this.v, array6);
            }
            if (l == n3) {
                System.arraycopy(array7, 0, array2, (l - 1) * this.u, array2.length - (l - 1) * this.u);
            }
            else {
                System.arraycopy(array7, 0, array2, (l - 1) * this.u, array7.length);
            }
        }
        return array2;
    }
    
    @Override
    public CipherParameters generateDerivedParameters(int n) {
        n /= 8;
        return new KeyParameter(this.generateDerivedKey(1, n), 0, n);
    }
    
    @Override
    public CipherParameters generateDerivedParameters(int n, int n2) {
        n /= 8;
        n2 /= 8;
        return new ParametersWithIV(new KeyParameter(this.generateDerivedKey(1, n), 0, n), this.generateDerivedKey(2, n2), 0, n2);
    }
    
    @Override
    public CipherParameters generateDerivedMacParameters(int n) {
        n /= 8;
        return new KeyParameter(this.generateDerivedKey(3, n), 0, n);
    }
}
