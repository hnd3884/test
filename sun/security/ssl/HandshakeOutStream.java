package sun.security.ssl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.io.ByteArrayOutputStream;

public class HandshakeOutStream extends ByteArrayOutputStream
{
    OutputRecord outputRecord;
    
    HandshakeOutStream(final OutputRecord outputRecord) {
        this.outputRecord = outputRecord;
    }
    
    void complete() throws IOException {
        if (this.size() < 4) {
            throw new RuntimeException("handshake message is not available");
        }
        if (this.outputRecord != null) {
            if (!this.outputRecord.isClosed()) {
                this.outputRecord.encodeHandshake(this.buf, 0, this.count);
            }
            else if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.warning("outbound has closed, ignore outbound handshake messages", ByteBuffer.wrap(this.buf, 0, this.count));
            }
            this.reset();
        }
    }
    
    @Override
    public void write(final byte[] array, final int n, final int n2) {
        checkOverflow(n2, 16777216);
        super.write(array, n, n2);
    }
    
    @Override
    public void flush() throws IOException {
        if (this.outputRecord != null) {
            this.outputRecord.flush();
        }
    }
    
    void putInt8(final int n) throws IOException {
        checkOverflow(n, 256);
        super.write(n);
    }
    
    void putInt16(final int n) throws IOException {
        checkOverflow(n, 65536);
        super.write(n >> 8);
        super.write(n);
    }
    
    void putInt24(final int n) throws IOException {
        checkOverflow(n, 16777216);
        super.write(n >> 16);
        super.write(n >> 8);
        super.write(n);
    }
    
    void putInt32(final int n) throws IOException {
        super.write(n >> 24);
        super.write(n >> 16);
        super.write(n >> 8);
        super.write(n);
    }
    
    void putBytes8(final byte[] array) throws IOException {
        if (array == null) {
            this.putInt8(0);
        }
        else {
            this.putInt8(array.length);
            super.write(array, 0, array.length);
        }
    }
    
    public void putBytes16(final byte[] array) throws IOException {
        if (array == null) {
            this.putInt16(0);
        }
        else {
            this.putInt16(array.length);
            super.write(array, 0, array.length);
        }
    }
    
    void putBytes24(final byte[] array) throws IOException {
        if (array == null) {
            this.putInt24(0);
        }
        else {
            this.putInt24(array.length);
            super.write(array, 0, array.length);
        }
    }
    
    private static void checkOverflow(final int n, final int n2) {
        if (n >= n2) {
            throw new RuntimeException("Field length overflow, the field length (" + n + ") should be less than " + n2);
        }
    }
}
