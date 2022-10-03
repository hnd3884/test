package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.io.IOException;
import javax.net.ssl.SSLException;
import java.io.InputStream;

public class HandshakeInStream extends InputStream
{
    InputRecord r;
    
    HandshakeInStream(final HandshakeHash handshakeHash) {
        (this.r = new InputRecord()).setHandshakeHash(handshakeHash);
    }
    
    @Override
    public int available() {
        return this.r.available();
    }
    
    @Override
    public int read() throws IOException {
        final int n = this.r.read();
        if (n == -1) {
            throw new SSLException("Unexpected end of handshake data");
        }
        return n;
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        final int n = this.r.read(b, off, len);
        if (n != len) {
            throw new SSLException("Unexpected end of handshake data");
        }
        return n;
    }
    
    @Override
    public long skip(final long n) throws IOException {
        return this.r.skip(n);
    }
    
    @Override
    public void mark(final int readlimit) {
        this.r.mark(readlimit);
    }
    
    @Override
    public void reset() throws IOException {
        this.r.reset();
    }
    
    @Override
    public boolean markSupported() {
        return true;
    }
    
    void incomingRecord(final InputRecord in) throws IOException {
        this.r.queueHandshake(in);
    }
    
    void digestNow() {
        this.r.doHashes();
    }
    
    void ignore(final int n) {
        this.r.ignore(n);
    }
    
    int getInt8() throws IOException {
        return this.read();
    }
    
    int getInt16() throws IOException {
        return this.getInt8() << 8 | this.getInt8();
    }
    
    int getInt24() throws IOException {
        return this.getInt8() << 16 | this.getInt8() << 8 | this.getInt8();
    }
    
    int getInt32() throws IOException {
        return this.getInt8() << 24 | this.getInt8() << 16 | this.getInt8() << 8 | this.getInt8();
    }
    
    byte[] getBytes8() throws IOException {
        final int len = this.getInt8();
        this.verifyLength(len);
        final byte[] b = new byte[len];
        this.read(b, 0, len);
        return b;
    }
    
    public byte[] getBytes16() throws IOException {
        final int len = this.getInt16();
        this.verifyLength(len);
        final byte[] b = new byte[len];
        this.read(b, 0, len);
        return b;
    }
    
    byte[] getBytes24() throws IOException {
        final int len = this.getInt24();
        this.verifyLength(len);
        final byte[] b = new byte[len];
        this.read(b, 0, len);
        return b;
    }
    
    private void verifyLength(final int len) throws SSLException {
        if (len > this.available()) {
            throw new SSLException("Not enough data to fill declared vector size");
        }
    }
}
