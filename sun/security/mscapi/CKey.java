package sun.security.mscapi;

import java.math.BigInteger;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.ECPrivateKey;
import sun.security.util.KeyUtil;
import sun.security.util.Length;
import java.security.Key;

abstract class CKey implements Key, Length
{
    private static final long serialVersionUID = -1088859394025049194L;
    protected final NativeHandles handles;
    protected final int keyLength;
    protected final String algorithm;
    
    protected CKey(final String algorithm, final NativeHandles handles, final int keyLength) {
        this.algorithm = algorithm;
        this.handles = handles;
        this.keyLength = keyLength;
    }
    
    private static native void cleanUp(final long p0, final long p1);
    
    @Override
    public int length() {
        return this.keyLength;
    }
    
    public long getHCryptKey() {
        return this.handles.hCryptKey;
    }
    
    public long getHCryptProvider() {
        return this.handles.hCryptProv;
    }
    
    @Override
    public String getAlgorithm() {
        return this.algorithm;
    }
    
    protected static native String getContainerName(final long p0);
    
    protected static native String getKeyType(final long p0);
    
    static byte[] generateECBlob(final Key key) {
        final int keySize = KeyUtil.getKeySize(key);
        final int n = (keySize + 7) / 8;
        final boolean b = key instanceof ECPrivateKey;
        final byte[] array = new byte[8 + n * (b ? 3 : 2)];
        array[0] = 69;
        array[1] = 67;
        array[2] = 83;
        if (b) {
            array[3] = (byte)((keySize == 256) ? 50 : ((keySize == 384) ? 52 : 54));
        }
        else {
            array[3] = (byte)((keySize == 256) ? 49 : ((keySize == 384) ? 51 : 53));
        }
        if (b) {
            final byte[] byteArray = ((ECPrivateKey)key).getS().toByteArray();
            System.arraycopy(byteArray, 0, array, 8 + n + n + n - byteArray.length, byteArray.length);
        }
        else {
            final ECPublicKey ecPublicKey = (ECPublicKey)key;
            final BigInteger affineX = ecPublicKey.getW().getAffineX();
            final byte[] byteArray2 = ecPublicKey.getW().getAffineY().toByteArray();
            System.arraycopy(byteArray2, 0, array, 8 + n + n - byteArray2.length, byteArray2.length);
            final byte[] byteArray3 = affineX.toByteArray();
            System.arraycopy(byteArray3, 0, array, 8 + n - byteArray3.length, byteArray3.length);
        }
        array[4] = (byte)n;
        final byte[] array2 = array;
        final int n2 = 5;
        final byte[] array3 = array;
        final int n3 = 6;
        final byte[] array4 = array;
        final int n4 = 7;
        final byte b2 = 0;
        array4[n4] = b2;
        array2[n2] = (array3[n3] = b2);
        return array;
    }
    
    static class NativeHandles
    {
        long hCryptProv;
        long hCryptKey;
        
        public NativeHandles(final long hCryptProv, final long hCryptKey) {
            this.hCryptProv = 0L;
            this.hCryptKey = 0L;
            this.hCryptProv = hCryptProv;
            this.hCryptKey = hCryptKey;
        }
        
        @Override
        protected void finalize() throws Throwable {
            try {
                synchronized (this) {
                    cleanUp(this.hCryptProv, this.hCryptKey);
                    this.hCryptProv = 0L;
                    this.hCryptKey = 0L;
                }
            }
            finally {
                super.finalize();
            }
        }
    }
}
