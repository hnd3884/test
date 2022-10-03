package sun.security.jgss;

import java.io.OutputStream;
import sun.security.util.DerValue;
import org.ietf.jgss.GSSException;
import java.io.InputStream;
import java.io.IOException;
import sun.security.util.DerOutputStream;
import sun.security.util.ObjectIdentifier;

public class GSSHeader
{
    private ObjectIdentifier mechOid;
    private byte[] mechOidBytes;
    private int mechTokenLength;
    public static final int TOKEN_ID = 96;
    
    public GSSHeader(final ObjectIdentifier mechOid, final int mechTokenLength) throws IOException {
        this.mechOid = null;
        this.mechOidBytes = null;
        this.mechTokenLength = 0;
        this.mechOid = mechOid;
        final DerOutputStream derOutputStream = new DerOutputStream();
        derOutputStream.putOID(mechOid);
        this.mechOidBytes = derOutputStream.toByteArray();
        this.mechTokenLength = mechTokenLength;
    }
    
    public GSSHeader(final InputStream inputStream) throws IOException, GSSException {
        this.mechOid = null;
        this.mechOidBytes = null;
        this.mechTokenLength = 0;
        if (inputStream.read() != 96) {
            throw new GSSException(10, -1, "GSSHeader did not find the right tag");
        }
        final int length = this.getLength(inputStream);
        final DerValue derValue = new DerValue(inputStream);
        this.mechOidBytes = derValue.toByteArray();
        this.mechOid = derValue.getOID();
        this.mechTokenLength = length - this.mechOidBytes.length;
    }
    
    public ObjectIdentifier getOid() {
        return this.mechOid;
    }
    
    public int getMechTokenLength() {
        return this.mechTokenLength;
    }
    
    public int getLength() {
        return 1 + this.getLenFieldSize(this.mechOidBytes.length + this.mechTokenLength) + this.mechOidBytes.length;
    }
    
    public static int getMaxMechTokenSize(final ObjectIdentifier objectIdentifier, int n) {
        int length = 0;
        try {
            final DerOutputStream derOutputStream = new DerOutputStream();
            derOutputStream.putOID(objectIdentifier);
            length = derOutputStream.toByteArray().length;
        }
        catch (final IOException ex) {}
        n -= 1 + length;
        n -= 5;
        return n;
    }
    
    private int getLenFieldSize(final int n) {
        int n2;
        if (n < 128) {
            n2 = 1;
        }
        else if (n < 256) {
            n2 = 2;
        }
        else if (n < 65536) {
            n2 = 3;
        }
        else if (n < 16777216) {
            n2 = 4;
        }
        else {
            n2 = 5;
        }
        return n2;
    }
    
    public int encode(final OutputStream outputStream) throws IOException {
        final int n = 1 + this.mechOidBytes.length;
        outputStream.write(96);
        final int n2 = n + this.putLength(this.mechOidBytes.length + this.mechTokenLength, outputStream);
        outputStream.write(this.mechOidBytes);
        return n2;
    }
    
    private int getLength(final InputStream inputStream) throws IOException {
        return this.getLength(inputStream.read(), inputStream);
    }
    
    private int getLength(final int n, final InputStream inputStream) throws IOException {
        int n2;
        if ((n & 0x80) == 0x0) {
            n2 = n;
        }
        else {
            int i = n & 0x7F;
            if (i == 0) {
                return -1;
            }
            if (i < 0 || i > 4) {
                throw new IOException("DerInputStream.getLength(): lengthTag=" + i + ", " + ((i < 0) ? "incorrect DER encoding." : "too big."));
            }
            n2 = 0;
            while (i > 0) {
                n2 = (n2 << 8) + (0xFF & inputStream.read());
                --i;
            }
            if (n2 < 0) {
                throw new IOException("Invalid length bytes");
            }
        }
        return n2;
    }
    
    private int putLength(final int n, final OutputStream outputStream) throws IOException {
        int n2;
        if (n < 128) {
            outputStream.write((byte)n);
            n2 = 1;
        }
        else if (n < 256) {
            outputStream.write(-127);
            outputStream.write((byte)n);
            n2 = 2;
        }
        else if (n < 65536) {
            outputStream.write(-126);
            outputStream.write((byte)(n >> 8));
            outputStream.write((byte)n);
            n2 = 3;
        }
        else if (n < 16777216) {
            outputStream.write(-125);
            outputStream.write((byte)(n >> 16));
            outputStream.write((byte)(n >> 8));
            outputStream.write((byte)n);
            n2 = 4;
        }
        else {
            outputStream.write(-124);
            outputStream.write((byte)(n >> 24));
            outputStream.write((byte)(n >> 16));
            outputStream.write((byte)(n >> 8));
            outputStream.write((byte)n);
            n2 = 5;
        }
        return n2;
    }
    
    private void debug(final String s) {
        System.err.print(s);
    }
    
    private String getHexBytes(final byte[] array, final int n) throws IOException {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < n; ++i) {
            final int n2 = array[i] >> 4 & 0xF;
            final int n3 = array[i] & 0xF;
            sb.append(Integer.toHexString(n2));
            sb.append(Integer.toHexString(n3));
            sb.append(' ');
        }
        return sb.toString();
    }
}
