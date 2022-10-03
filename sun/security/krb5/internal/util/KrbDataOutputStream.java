package sun.security.krb5.internal.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedOutputStream;

public class KrbDataOutputStream extends BufferedOutputStream
{
    public KrbDataOutputStream(final OutputStream outputStream) {
        super(outputStream);
    }
    
    public void write32(final int n) throws IOException {
        this.write(new byte[] { (byte)((n & 0xFF000000) >> 24 & 0xFF), (byte)((n & 0xFF0000) >> 16 & 0xFF), (byte)((n & 0xFF00) >> 8 & 0xFF), (byte)(n & 0xFF) }, 0, 4);
    }
    
    public void write16(final int n) throws IOException {
        this.write(new byte[] { (byte)((n & 0xFF00) >> 8 & 0xFF), (byte)(n & 0xFF) }, 0, 2);
    }
    
    public void write8(final int n) throws IOException {
        this.write(n & 0xFF);
    }
}
