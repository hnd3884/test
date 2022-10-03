package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.io.IOException;
import java.io.OutputStream;

public class HandshakeOutStream extends OutputStream
{
    private SSLSocketImpl socket;
    private SSLEngineImpl engine;
    OutputRecord r;
    
    HandshakeOutStream(final ProtocolVersion protocolVersion, final ProtocolVersion helloVersion, final HandshakeHash handshakeHash, final SSLSocketImpl socket) {
        this.socket = socket;
        this.r = new OutputRecord((byte)22);
        this.init(protocolVersion, helloVersion, handshakeHash);
    }
    
    HandshakeOutStream(final ProtocolVersion protocolVersion, final ProtocolVersion helloVersion, final HandshakeHash handshakeHash, final SSLEngineImpl engine) {
        this.engine = engine;
        this.r = new EngineOutputRecord((byte)22, engine);
        this.init(protocolVersion, helloVersion, handshakeHash);
    }
    
    private void init(final ProtocolVersion protocolVersion, final ProtocolVersion helloVersion, final HandshakeHash handshakeHash) {
        this.r.setVersion(protocolVersion);
        this.r.setHelloVersion(helloVersion);
        this.r.setHandshakeHash(handshakeHash);
    }
    
    void doHashes() {
        this.r.doHashes();
    }
    
    @Override
    public void write(final byte[] buf, int off, int len) throws IOException {
        while (len > 0) {
            final int howmuch = Math.min(len, this.r.availableDataBytes());
            if (howmuch == 0) {
                this.flush();
            }
            else {
                this.r.write(buf, off, howmuch);
                off += howmuch;
                len -= howmuch;
            }
        }
    }
    
    @Override
    public void write(final int i) throws IOException {
        if (this.r.availableDataBytes() < 1) {
            this.flush();
        }
        this.r.write(i);
    }
    
    @Override
    public void flush() throws IOException {
        if (this.socket != null) {
            try {
                this.socket.writeRecord(this.r);
                return;
            }
            catch (final IOException e) {
                this.socket.waitForClose(true);
                throw e;
            }
        }
        this.engine.writeRecord((EngineOutputRecord)this.r);
    }
    
    void setFinishedMsg() {
        assert this.socket == null;
        ((EngineOutputRecord)this.r).setFinishedMsg();
    }
    
    void putInt8(final int i) throws IOException {
        this.checkOverflow(i, 256);
        this.r.write(i);
    }
    
    void putInt16(final int i) throws IOException {
        this.checkOverflow(i, 65536);
        if (this.r.availableDataBytes() < 2) {
            this.flush();
        }
        this.r.write(i >> 8);
        this.r.write(i);
    }
    
    void putInt24(final int i) throws IOException {
        this.checkOverflow(i, 16777216);
        if (this.r.availableDataBytes() < 3) {
            this.flush();
        }
        this.r.write(i >> 16);
        this.r.write(i >> 8);
        this.r.write(i);
    }
    
    void putInt32(final int i) throws IOException {
        if (this.r.availableDataBytes() < 4) {
            this.flush();
        }
        this.r.write(i >> 24);
        this.r.write(i >> 16);
        this.r.write(i >> 8);
        this.r.write(i);
    }
    
    void putBytes8(final byte[] b) throws IOException {
        if (b == null) {
            this.putInt8(0);
            return;
        }
        this.checkOverflow(b.length, 256);
        this.putInt8(b.length);
        this.write(b, 0, b.length);
    }
    
    public void putBytes16(final byte[] b) throws IOException {
        if (b == null) {
            this.putInt16(0);
            return;
        }
        this.checkOverflow(b.length, 65536);
        this.putInt16(b.length);
        this.write(b, 0, b.length);
    }
    
    void putBytes24(final byte[] b) throws IOException {
        if (b == null) {
            this.putInt24(0);
            return;
        }
        this.checkOverflow(b.length, 16777216);
        this.putInt24(b.length);
        this.write(b, 0, b.length);
    }
    
    private void checkOverflow(final int length, final int overflow) {
        if (length >= overflow) {
            throw new RuntimeException("Field length overflow, the field length (" + length + ") should be less than " + overflow);
        }
    }
}
