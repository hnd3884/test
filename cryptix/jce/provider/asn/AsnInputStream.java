package cryptix.jce.provider.asn;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class AsnInputStream
{
    private final InputStream is;
    
    public AsnObject read() throws IOException {
        final int tagAsInt = this.is.read();
        if (tagAsInt == -1) {
            throw new IOException("End of stream.");
        }
        final byte tag = (byte)tagAsInt;
        switch (tag) {
            case 6: {
                return new AsnObjectId(this);
            }
            case 3: {
                return new AsnBitString(this);
            }
            case 2: {
                return new AsnInteger(this);
            }
            case 5: {
                return new AsnNull(this);
            }
            case 48: {
                return new AsnSequence(this);
            }
            default: {
                return new AsnUnknown(tag, this);
            }
        }
    }
    
    public int available() throws IOException {
        return this.is.available();
    }
    
    int readLength() throws IOException {
        int b = this.is.read();
        if (b == -1) {
            throw new IOException("Unexpected end of stream.");
        }
        if (b <= 127) {
            return b;
        }
        b &= 0x7F;
        if (b > 4) {
            throw new IOException("Length too big.");
        }
        int res = 0;
        while (b-- > 0) {
            final int t;
            if ((t = this.is.read()) == -1) {
                throw new IOException("Unexpected end of stream.");
            }
            res = (res << 8 | t);
        }
        if (res < 0) {
            throw new IOException("Negative length.");
        }
        return res;
    }
    
    byte readByte() throws IOException {
        return this.readBytes(1)[0];
    }
    
    byte[] readBytes(int todo) throws IOException {
        final byte[] res = new byte[todo];
        int done;
        for (int off = 0; todo > 0; todo -= done, off += done) {
            if ((done = this.is.read(res, off, todo)) == -1) {
                throw new IOException("EOF");
            }
        }
        return res;
    }
    
    AsnInputStream getSubStream(final int len) {
        return new AsnInputStream(new SubInputStream(this.is, len));
    }
    
    public AsnInputStream(final byte[] data) {
        this.is = new ByteArrayInputStream(data);
    }
    
    public AsnInputStream(final InputStream is) {
        this.is = is;
    }
}
