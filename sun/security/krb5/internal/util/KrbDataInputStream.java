package sun.security.krb5.internal.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.io.BufferedInputStream;

public class KrbDataInputStream extends BufferedInputStream
{
    private boolean bigEndian;
    
    public void setNativeByteOrder() {
        if (ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN)) {
            this.bigEndian = true;
        }
        else {
            this.bigEndian = false;
        }
    }
    
    public KrbDataInputStream(final InputStream inputStream) {
        super(inputStream);
        this.bigEndian = true;
    }
    
    public final int readLength4() throws IOException {
        final int read = this.read(4);
        if (read < 0) {
            throw new IOException("Invalid encoding");
        }
        return read;
    }
    
    public int read(final int n) throws IOException {
        final byte[] array = new byte[n];
        if (this.read(array, 0, n) != n) {
            throw new IOException("Premature end of stream reached");
        }
        int n2 = 0;
        for (int i = 0; i < n; ++i) {
            if (this.bigEndian) {
                n2 |= (array[i] & 0xFF) << (n - i - 1) * 8;
            }
            else {
                n2 |= (array[i] & 0xFF) << i * 8;
            }
        }
        return n2;
    }
    
    public int readVersion() throws IOException {
        return (this.read() & 0xFF) << 8 | (this.read() & 0xFF);
    }
}
