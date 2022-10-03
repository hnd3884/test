package org.openjsse.sun.security.ssl;

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
    public void write(final byte[] b, final int off, final int len) {
        checkOverflow(len, 16777216);
        super.write(b, off, len);
    }
    
    @Override
    public void flush() throws IOException {
        if (this.outputRecord != null) {
            this.outputRecord.flush();
        }
    }
    
    void putInt8(final int i) throws IOException {
        checkOverflow(i, 256);
        super.write(i);
    }
    
    void putInt16(final int i) throws IOException {
        checkOverflow(i, 65536);
        super.write(i >> 8);
        super.write(i);
    }
    
    void putInt24(final int i) throws IOException {
        checkOverflow(i, 16777216);
        super.write(i >> 16);
        super.write(i >> 8);
        super.write(i);
    }
    
    void putInt32(final int i) throws IOException {
        super.write(i >> 24);
        super.write(i >> 16);
        super.write(i >> 8);
        super.write(i);
    }
    
    void putBytes8(final byte[] b) throws IOException {
        if (b == null) {
            this.putInt8(0);
        }
        else {
            this.putInt8(b.length);
            super.write(b, 0, b.length);
        }
    }
    
    public void putBytes16(final byte[] b) throws IOException {
        if (b == null) {
            this.putInt16(0);
        }
        else {
            this.putInt16(b.length);
            super.write(b, 0, b.length);
        }
    }
    
    void putBytes24(final byte[] b) throws IOException {
        if (b == null) {
            this.putInt24(0);
        }
        else {
            this.putInt24(b.length);
            super.write(b, 0, b.length);
        }
    }
    
    private static void checkOverflow(final int length, final int limit) {
        if (length >= limit) {
            throw new RuntimeException("Field length overflow, the field length (" + length + ") should be less than " + limit);
        }
    }
}
