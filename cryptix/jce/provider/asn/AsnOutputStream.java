package cryptix.jce.provider.asn;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class AsnOutputStream
{
    private final OutputStream os;
    
    public void close() throws IOException {
        this.os.flush();
        this.os.close();
    }
    
    public void flush() throws IOException {
        this.os.flush();
    }
    
    public void write(final AsnObject obj) throws IOException {
        obj.encode(this);
    }
    
    public byte[] toByteArray() {
        if (!(this.os instanceof ByteArrayOutputStream)) {
            throw new IllegalStateException("Underlying stream is not instanceof ByteArrayOutputStream.");
        }
        final ByteArrayOutputStream baos = (ByteArrayOutputStream)this.os;
        return baos.toByteArray();
    }
    
    int getLengthOfLength(final int len) {
        if (len <= 127) {
            return 1;
        }
        if (len <= 255) {
            return 2;
        }
        if (len <= 65535) {
            return 3;
        }
        if (len <= 16777215) {
            return 4;
        }
        return 5;
    }
    
    void writeByte(final byte b) throws IOException {
        this.os.write(b & 0xFF);
    }
    
    void writeBytes(final byte[] data) throws IOException {
        this.os.write(data);
    }
    
    void writeLength(final int len) throws IOException {
        if (len < 0) {
            throw new IllegalArgumentException("len: < 0");
        }
        if (len <= 127) {
            this.os.write((char)len);
            return;
        }
        int lenOfLenData = this.getLengthOfLength(len) - 1;
        this.os.write((byte)(lenOfLenData | 0x80));
        while (lenOfLenData-- > 0) {
            this.os.write((byte)(len >>> lenOfLenData * 8));
        }
    }
    
    void writeType(final byte type) throws IOException {
        this.os.write(type);
    }
    
    public AsnOutputStream() {
        this.os = new ByteArrayOutputStream();
    }
    
    public AsnOutputStream(final OutputStream os) {
        this.os = os;
    }
}
