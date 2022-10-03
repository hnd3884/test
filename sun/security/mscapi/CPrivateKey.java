package sun.security.mscapi;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.security.PrivateKey;

class CPrivateKey extends CKey implements PrivateKey
{
    private static final long serialVersionUID = 8113152807912338063L;
    
    private CPrivateKey(final String s, final NativeHandles nativeHandles, final int n) {
        super(s, nativeHandles, n);
    }
    
    static CPrivateKey of(final String s, final long n, final long n2, final int n3) {
        return of(s, new NativeHandles(n, n2), n3);
    }
    
    public static CPrivateKey of(final String s, final NativeHandles nativeHandles, final int n) {
        return new CPrivateKey(s, nativeHandles, n);
    }
    
    @Override
    public String getFormat() {
        return null;
    }
    
    @Override
    public byte[] getEncoded() {
        return null;
    }
    
    @Override
    public String toString() {
        if (this.handles.hCryptKey != 0L) {
            return this.algorithm + "PrivateKey [size=" + this.keyLength + " bits, type=" + CKey.getKeyType(this.handles.hCryptKey) + ", container=" + CKey.getContainerName(this.handles.hCryptProv) + "]";
        }
        return this.algorithm + "PrivateKey [size=" + this.keyLength + " bits, type=CNG]";
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        throw new NotSerializableException();
    }
}
