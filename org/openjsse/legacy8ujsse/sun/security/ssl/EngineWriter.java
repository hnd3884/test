package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.io.OutputStream;
import sun.misc.HexDumpEncoder;
import java.io.IOException;
import javax.net.ssl.SSLEngineResult;
import java.nio.ByteBuffer;
import java.util.LinkedList;

final class EngineWriter
{
    private LinkedList<Object> outboundList;
    private boolean outboundClosed;
    private static final Debug debug;
    
    EngineWriter() {
        this.outboundClosed = false;
        this.outboundList = new LinkedList<Object>();
    }
    
    private SSLEngineResult.HandshakeStatus getOutboundData(final ByteBuffer dstBB) {
        Object msg = this.outboundList.removeFirst();
        assert msg instanceof ByteBuffer;
        final ByteBuffer bbIn = (ByteBuffer)msg;
        assert dstBB.remaining() >= bbIn.remaining();
        dstBB.put(bbIn);
        if (!this.hasOutboundDataInternal()) {
            return null;
        }
        msg = this.outboundList.getFirst();
        if (msg == SSLEngineResult.HandshakeStatus.FINISHED) {
            this.outboundList.removeFirst();
            return SSLEngineResult.HandshakeStatus.FINISHED;
        }
        return SSLEngineResult.HandshakeStatus.NEED_WRAP;
    }
    
    synchronized void writeRecord(final EngineOutputRecord outputRecord, final Authenticator authenticator, final CipherBox writeCipher) throws IOException {
        if (this.outboundClosed) {
            throw new IOException("writer side was already closed.");
        }
        outputRecord.write(authenticator, writeCipher);
        if (outputRecord.isFinishedMsg()) {
            this.outboundList.addLast(SSLEngineResult.HandshakeStatus.FINISHED);
        }
    }
    
    private void dumpPacket(final EngineArgs ea, final boolean hsData) {
        try {
            final HexDumpEncoder hd = new HexDumpEncoder();
            final ByteBuffer bb = ea.netData.duplicate();
            final int pos = bb.position();
            bb.position(pos - ea.deltaNet());
            bb.limit(pos);
            System.out.println("[Raw write" + (hsData ? "" : " (bb)") + "]: length = " + bb.remaining());
            hd.encodeBuffer(bb, System.out);
        }
        catch (final IOException ex) {}
    }
    
    synchronized SSLEngineResult.HandshakeStatus writeRecord(final EngineOutputRecord outputRecord, final EngineArgs ea, final Authenticator authenticator, final CipherBox writeCipher) throws IOException {
        if (this.hasOutboundDataInternal()) {
            final SSLEngineResult.HandshakeStatus hss = this.getOutboundData(ea.netData);
            if (EngineWriter.debug != null && Debug.isOn("packet")) {
                this.dumpPacket(ea, true);
            }
            return hss;
        }
        if (this.outboundClosed) {
            throw new IOException("The write side was already closed");
        }
        outputRecord.write(ea, authenticator, writeCipher);
        if (EngineWriter.debug != null && Debug.isOn("packet")) {
            this.dumpPacket(ea, false);
        }
        return null;
    }
    
    void putOutboundData(final ByteBuffer bytes) {
        this.outboundList.addLast(bytes);
    }
    
    synchronized void putOutboundDataSync(final ByteBuffer bytes) throws IOException {
        if (this.outboundClosed) {
            throw new IOException("Write side already closed");
        }
        this.outboundList.addLast(bytes);
    }
    
    private boolean hasOutboundDataInternal() {
        return this.outboundList.size() != 0;
    }
    
    synchronized boolean hasOutboundData() {
        return this.hasOutboundDataInternal();
    }
    
    synchronized boolean isOutboundDone() {
        return this.outboundClosed && !this.hasOutboundDataInternal();
    }
    
    synchronized void closeOutbound() {
        this.outboundClosed = true;
    }
    
    static {
        debug = Debug.getInstance("ssl");
    }
}
