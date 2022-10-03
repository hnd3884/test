package javax.crypto;

import java.security.spec.AlgorithmParameterSpec;
import java.security.SecureRandom;
import java.security.Key;
import java.security.AlgorithmParameters;

final class NullCipherSpi extends CipherSpi
{
    protected NullCipherSpi() {
    }
    
    public void engineSetMode(final String s) {
    }
    
    public void engineSetPadding(final String s) {
    }
    
    @Override
    protected int engineGetBlockSize() {
        return 1;
    }
    
    @Override
    protected int engineGetOutputSize(final int n) {
        return n;
    }
    
    @Override
    protected byte[] engineGetIV() {
        return new byte[8];
    }
    
    @Override
    protected AlgorithmParameters engineGetParameters() {
        return null;
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final SecureRandom secureRandom) {
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) {
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final AlgorithmParameters algorithmParameters, final SecureRandom secureRandom) {
    }
    
    @Override
    protected byte[] engineUpdate(final byte[] array, final int n, final int n2) {
        if (array == null) {
            return null;
        }
        final byte[] array2 = new byte[n2];
        System.arraycopy(array, n, array2, 0, n2);
        return array2;
    }
    
    @Override
    protected int engineUpdate(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) {
        if (array == null) {
            return 0;
        }
        System.arraycopy(array, n, array2, n3, n2);
        return n2;
    }
    
    @Override
    protected byte[] engineDoFinal(final byte[] array, final int n, final int n2) {
        return this.engineUpdate(array, n, n2);
    }
    
    @Override
    protected int engineDoFinal(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) {
        return this.engineUpdate(array, n, n2, array2, n3);
    }
    
    @Override
    protected int engineGetKeySize(final Key key) {
        return 0;
    }
}
