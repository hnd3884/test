package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.io.OutputStream;
import java.io.IOException;

final class EngineOutputRecord extends OutputRecord
{
    private SSLEngineImpl engine;
    private EngineWriter writer;
    private boolean finishedMsg;
    
    EngineOutputRecord(final byte type, final SSLEngineImpl engine) {
        super(type, recordSize(type));
        this.finishedMsg = false;
        this.engine = engine;
        this.writer = engine.writer;
    }
    
    private static int recordSize(final byte type) {
        switch (type) {
            case 20:
            case 21: {
                return 539;
            }
            case 22: {
                return 16921;
            }
            case 23: {
                return 0;
            }
            default: {
                throw new RuntimeException("Unknown record type: " + type);
            }
        }
    }
    
    void setFinishedMsg() {
        this.finishedMsg = true;
    }
    
    @Override
    public void flush() throws IOException {
        this.finishedMsg = false;
    }
    
    boolean isFinishedMsg() {
        return this.finishedMsg;
    }
    
    @Override
    void writeBuffer(final OutputStream s, final byte[] buf, final int off, final int len, final int debugOffset) throws IOException {
        final ByteBuffer netBB = (ByteBuffer)ByteBuffer.allocate(len).put(buf, off, len).flip();
        this.writer.putOutboundData(netBB);
    }
    
    void write(final Authenticator authenticator, final CipherBox writeCipher) throws IOException {
        switch (this.contentType()) {
            case 20:
            case 21:
            case 22: {
                if (!this.isEmpty()) {
                    this.encrypt(authenticator, writeCipher);
                    this.write(null, false, null);
                }
                return;
            }
            default: {
                throw new RuntimeException("unexpected byte buffers");
            }
        }
    }
    
    void write(final EngineArgs ea, final Authenticator authenticator, final CipherBox writeCipher) throws IOException {
        assert this.contentType() == 23;
        if (authenticator == MAC.NULL) {
            return;
        }
        if (ea.getAppRemaining() == 0) {
            return;
        }
        int length;
        if (this.engine.needToSplitPayload(writeCipher, this.protocolVersion)) {
            this.write(ea, authenticator, writeCipher, 1);
            ea.resetLim();
            length = Math.min(ea.getAppRemaining(), 15846);
        }
        else {
            length = Math.min(ea.getAppRemaining(), 16384);
        }
        if (length > 0) {
            this.write(ea, authenticator, writeCipher, length);
        }
    }
    
    void write(final EngineArgs ea, final Authenticator authenticator, final CipherBox writeCipher, final int length) throws IOException {
        final ByteBuffer dstBB = ea.netData;
        final int dstPos = dstBB.position();
        final int dstLim = dstBB.limit();
        final int dstData = dstPos + 5 + writeCipher.getExplicitNonceSize();
        dstBB.position(dstData);
        ea.gather(length);
        dstBB.limit(dstBB.position());
        dstBB.position(dstData);
        if (authenticator instanceof MAC) {
            final MAC signer = (MAC)authenticator;
            if (signer.MAClen() != 0) {
                final byte[] hash = signer.compute(this.contentType(), dstBB, false);
                dstBB.limit(dstBB.limit() + hash.length);
                dstBB.put(hash);
                dstBB.limit(dstBB.position());
                dstBB.position(dstData);
            }
        }
        if (!writeCipher.isNullCipher()) {
            if (this.protocolVersion.v >= ProtocolVersion.TLS11.v && (writeCipher.isCBCMode() || writeCipher.isAEADMode())) {
                final byte[] nonce = writeCipher.createExplicitNonce(authenticator, this.contentType(), dstBB.remaining());
                dstBB.position(dstPos + 5);
                dstBB.put(nonce);
                if (!writeCipher.isAEADMode()) {
                    dstBB.position(dstPos + 5);
                }
            }
            writeCipher.encrypt(dstBB, dstLim);
            if (EngineOutputRecord.debug != null && (Debug.isOn("record") || (Debug.isOn("handshake") && this.contentType() == 20))) {
                System.out.println(Thread.currentThread().getName() + ", WRITE: " + this.protocolVersion + " " + InputRecord.contentName(this.contentType()) + ", length = " + length);
            }
        }
        else {
            dstBB.position(dstBB.limit());
        }
        final int packetLength = dstBB.limit() - dstPos - 5;
        dstBB.put(dstPos, this.contentType());
        dstBB.put(dstPos + 1, this.protocolVersion.major);
        dstBB.put(dstPos + 2, this.protocolVersion.minor);
        dstBB.put(dstPos + 3, (byte)(packetLength >> 8));
        dstBB.put(dstPos + 4, (byte)packetLength);
        dstBB.limit(dstLim);
    }
}
