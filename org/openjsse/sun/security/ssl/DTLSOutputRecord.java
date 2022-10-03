package org.openjsse.sun.security.ssl;

import java.util.Iterator;
import java.util.LinkedList;
import javax.net.ssl.SSLHandshakeException;
import java.nio.ByteBuffer;
import java.io.IOException;

final class DTLSOutputRecord extends OutputRecord implements DTLSRecord
{
    private DTLSFragmenter fragmenter;
    int writeEpoch;
    int prevWriteEpoch;
    Authenticator prevWriteAuthenticator;
    SSLCipher.SSLWriteCipher prevWriteCipher;
    private volatile boolean isCloseWaiting;
    
    DTLSOutputRecord(final HandshakeHash handshakeHash) {
        super(handshakeHash, SSLCipher.SSLWriteCipher.nullDTlsWriteCipher());
        this.fragmenter = null;
        this.isCloseWaiting = false;
        this.writeEpoch = 0;
        this.prevWriteEpoch = 0;
        this.prevWriteCipher = SSLCipher.SSLWriteCipher.nullDTlsWriteCipher();
        this.packetSize = 16717;
        this.protocolVersion = ProtocolVersion.NONE;
    }
    
    @Override
    public synchronized void close() throws IOException {
        if (!this.isClosed) {
            if (this.fragmenter != null && this.fragmenter.hasAlert()) {
                this.isCloseWaiting = true;
            }
            else {
                super.close();
            }
        }
    }
    
    @Override
    boolean isClosed() {
        return this.isClosed || this.isCloseWaiting;
    }
    
    @Override
    void initHandshaker() {
        this.fragmenter = null;
    }
    
    @Override
    void finishHandshake() {
    }
    
    @Override
    void changeWriteCiphers(final SSLCipher.SSLWriteCipher writeCipher, final boolean useChangeCipherSpec) throws IOException {
        if (this.isClosed()) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.warning("outbound has closed, ignore outbound change_cipher_spec message", new Object[0]);
            }
            return;
        }
        if (useChangeCipherSpec) {
            this.encodeChangeCipherSpec();
        }
        this.prevWriteCipher.dispose();
        this.prevWriteCipher = this.writeCipher;
        this.prevWriteEpoch = this.writeEpoch;
        this.writeCipher = writeCipher;
        ++this.writeEpoch;
        this.isFirstAppOutputRecord = true;
        this.writeCipher.authenticator.setEpochNumber(this.writeEpoch);
    }
    
    @Override
    void encodeAlert(final byte level, final byte description) throws IOException {
        if (this.isClosed()) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.warning("outbound has closed, ignore outbound alert message: " + Alert.nameOf(description), new Object[0]);
            }
            return;
        }
        if (this.fragmenter == null) {
            this.fragmenter = new DTLSFragmenter();
        }
        this.fragmenter.queueUpAlert(level, description);
    }
    
    @Override
    void encodeChangeCipherSpec() throws IOException {
        if (this.isClosed()) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.warning("outbound has closed, ignore outbound change_cipher_spec message", new Object[0]);
            }
            return;
        }
        if (this.fragmenter == null) {
            this.fragmenter = new DTLSFragmenter();
        }
        this.fragmenter.queueUpChangeCipherSpec();
    }
    
    @Override
    void encodeHandshake(final byte[] source, final int offset, final int length) throws IOException {
        if (this.isClosed()) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.warning("outbound has closed, ignore outbound handshake message", ByteBuffer.wrap(source, offset, length));
            }
            return;
        }
        if (this.firstMessage) {
            this.firstMessage = false;
        }
        if (this.fragmenter == null) {
            this.fragmenter = new DTLSFragmenter();
        }
        this.fragmenter.queueUpHandshake(source, offset, length);
    }
    
    @Override
    Ciphertext encode(ByteBuffer[] srcs, final int srcsOffset, final int srcsLength, final ByteBuffer[] dsts, final int dstsOffset, final int dstsLength) throws IOException {
        if (this.isClosed) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.warning("outbound has closed, ignore outbound application data or cached messages", new Object[0]);
            }
            return null;
        }
        if (this.isCloseWaiting) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.warning("outbound has closed, ignore outbound application data", new Object[0]);
            }
            srcs = null;
        }
        return this.encode(srcs, srcsOffset, srcsLength, dsts[0]);
    }
    
    private Ciphertext encode(final ByteBuffer[] sources, final int offset, final int length, final ByteBuffer destination) throws IOException {
        if (this.writeCipher.authenticator.seqNumOverflow()) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.fine("sequence number extremely close to overflow (2^64-1 packets). Closing connection.", new Object[0]);
            }
            throw new SSLHandshakeException("sequence number overflow");
        }
        if (!this.isEmpty() || sources == null || sources.length == 0) {
            final Ciphertext ct = this.acquireCiphertext(destination);
            if (ct != null) {
                return ct;
            }
        }
        if (sources == null || sources.length == 0) {
            return null;
        }
        int srcsRemains = 0;
        for (int i = offset; i < offset + length; ++i) {
            srcsRemains += sources[i].remaining();
        }
        if (srcsRemains == 0) {
            return null;
        }
        int fragLen;
        if (this.packetSize > 0) {
            fragLen = Math.min(16717, this.packetSize);
            fragLen = this.writeCipher.calculateFragmentSize(fragLen, 13);
            fragLen = Math.min(fragLen, 16384);
        }
        else {
            fragLen = 16384;
        }
        fragLen = this.calculateFragmentSize(fragLen);
        final int dstPos = destination.position();
        final int dstLim = destination.limit();
        final int dstContent = dstPos + 13 + this.writeCipher.getExplicitNonceSize();
        destination.position(dstContent);
        int remains = Math.min(fragLen, destination.remaining());
        fragLen = 0;
        int amount;
        for (int srcsLen = offset + length, j = offset; j < srcsLen && remains > 0; remains -= amount, fragLen += amount, ++j) {
            amount = Math.min(sources[j].remaining(), remains);
            final int srcLimit = sources[j].limit();
            sources[j].limit(sources[j].position() + amount);
            destination.put(sources[j]);
            sources[j].limit(srcLimit);
        }
        destination.limit(destination.position());
        destination.position(dstContent);
        if (SSLLogger.isOn && SSLLogger.isOn("record")) {
            SSLLogger.fine("WRITE: " + this.protocolVersion + " " + ContentType.APPLICATION_DATA.name + ", length = " + destination.remaining(), new Object[0]);
        }
        final long recordSN = OutputRecord.encrypt(this.writeCipher, ContentType.APPLICATION_DATA.id, destination, dstPos, dstLim, 13, this.protocolVersion);
        if (SSLLogger.isOn && SSLLogger.isOn("packet")) {
            final ByteBuffer temporary = destination.duplicate();
            temporary.limit(temporary.position());
            temporary.position(dstPos);
            SSLLogger.fine("Raw write", temporary);
        }
        destination.limit(dstLim);
        return new Ciphertext(ContentType.APPLICATION_DATA.id, SSLHandshake.NOT_APPLICABLE.id, recordSN);
    }
    
    private Ciphertext acquireCiphertext(final ByteBuffer destination) throws IOException {
        if (this.fragmenter != null) {
            return this.fragmenter.acquireCiphertext(destination);
        }
        return null;
    }
    
    @Override
    boolean isEmpty() {
        return this.fragmenter == null || this.fragmenter.isEmpty();
    }
    
    @Override
    void launchRetransmission() {
        if (this.fragmenter != null && this.fragmenter.isRetransmittable()) {
            this.fragmenter.setRetransmission();
        }
    }
    
    private static class RecordMemo
    {
        byte contentType;
        byte majorVersion;
        byte minorVersion;
        int encodeEpoch;
        SSLCipher.SSLWriteCipher encodeCipher;
        byte[] fragment;
    }
    
    private static class HandshakeMemo extends RecordMemo
    {
        byte handshakeType;
        int messageSequence;
        int acquireOffset;
    }
    
    private final class DTLSFragmenter
    {
        private final LinkedList<RecordMemo> handshakeMemos;
        private int acquireIndex;
        private int messageSequence;
        private boolean flightIsReady;
        private int retransmits;
        
        private DTLSFragmenter() {
            this.handshakeMemos = new LinkedList<RecordMemo>();
            this.acquireIndex = 0;
            this.messageSequence = 0;
            this.flightIsReady = false;
            this.retransmits = 2;
        }
        
        void queueUpHandshake(final byte[] buf, final int offset, final int length) throws IOException {
            if (this.flightIsReady) {
                this.handshakeMemos.clear();
                this.acquireIndex = 0;
                this.flightIsReady = false;
            }
            final HandshakeMemo memo = new HandshakeMemo();
            memo.contentType = ContentType.HANDSHAKE.id;
            memo.majorVersion = DTLSOutputRecord.this.protocolVersion.major;
            memo.minorVersion = DTLSOutputRecord.this.protocolVersion.minor;
            memo.encodeEpoch = DTLSOutputRecord.this.writeEpoch;
            memo.encodeCipher = DTLSOutputRecord.this.writeCipher;
            memo.handshakeType = buf[offset];
            memo.messageSequence = this.messageSequence++;
            memo.acquireOffset = 0;
            System.arraycopy(buf, offset + 4, memo.fragment = new byte[length - 4], 0, length - 4);
            this.handshakeHashing(memo, memo.fragment);
            this.handshakeMemos.add(memo);
            if (memo.handshakeType == SSLHandshake.CLIENT_HELLO.id || memo.handshakeType == SSLHandshake.HELLO_REQUEST.id || memo.handshakeType == SSLHandshake.HELLO_VERIFY_REQUEST.id || memo.handshakeType == SSLHandshake.SERVER_HELLO_DONE.id || memo.handshakeType == SSLHandshake.FINISHED.id) {
                this.flightIsReady = true;
            }
        }
        
        void queueUpChangeCipherSpec() {
            if (this.flightIsReady) {
                this.handshakeMemos.clear();
                this.acquireIndex = 0;
                this.flightIsReady = false;
            }
            final RecordMemo memo = new RecordMemo();
            memo.contentType = ContentType.CHANGE_CIPHER_SPEC.id;
            memo.majorVersion = DTLSOutputRecord.this.protocolVersion.major;
            memo.minorVersion = DTLSOutputRecord.this.protocolVersion.minor;
            memo.encodeEpoch = DTLSOutputRecord.this.writeEpoch;
            memo.encodeCipher = DTLSOutputRecord.this.writeCipher;
            (memo.fragment = new byte[1])[0] = 1;
            this.handshakeMemos.add(memo);
        }
        
        void queueUpAlert(final byte level, final byte description) throws IOException {
            final RecordMemo memo = new RecordMemo();
            memo.contentType = ContentType.ALERT.id;
            memo.majorVersion = DTLSOutputRecord.this.protocolVersion.major;
            memo.minorVersion = DTLSOutputRecord.this.protocolVersion.minor;
            memo.encodeEpoch = DTLSOutputRecord.this.writeEpoch;
            memo.encodeCipher = DTLSOutputRecord.this.writeCipher;
            (memo.fragment = new byte[2])[0] = level;
            memo.fragment[1] = description;
            this.handshakeMemos.add(memo);
        }
        
        Ciphertext acquireCiphertext(final ByteBuffer dstBuf) throws IOException {
            if (this.isEmpty()) {
                if (!this.isRetransmittable()) {
                    return null;
                }
                this.setRetransmission();
            }
            final RecordMemo memo = this.handshakeMemos.get(this.acquireIndex);
            HandshakeMemo hsMemo = null;
            if (memo.contentType == ContentType.HANDSHAKE.id) {
                hsMemo = (HandshakeMemo)memo;
            }
            int fragLen;
            if (DTLSOutputRecord.this.packetSize > 0) {
                fragLen = Math.min(16717, DTLSOutputRecord.this.packetSize);
                fragLen = memo.encodeCipher.calculateFragmentSize(fragLen, 25);
                fragLen = Math.min(fragLen, 16384);
            }
            else {
                fragLen = 16384;
            }
            fragLen = DTLSOutputRecord.this.calculateFragmentSize(fragLen);
            final int dstPos = dstBuf.position();
            final int dstLim = dstBuf.limit();
            final int dstContent = dstPos + 13 + memo.encodeCipher.getExplicitNonceSize();
            dstBuf.position(dstContent);
            if (hsMemo != null) {
                fragLen = Math.min(fragLen, hsMemo.fragment.length - hsMemo.acquireOffset);
                dstBuf.put(hsMemo.handshakeType);
                dstBuf.put((byte)(hsMemo.fragment.length >> 16 & 0xFF));
                dstBuf.put((byte)(hsMemo.fragment.length >> 8 & 0xFF));
                dstBuf.put((byte)(hsMemo.fragment.length & 0xFF));
                dstBuf.put((byte)(hsMemo.messageSequence >> 8 & 0xFF));
                dstBuf.put((byte)(hsMemo.messageSequence & 0xFF));
                dstBuf.put((byte)(hsMemo.acquireOffset >> 16 & 0xFF));
                dstBuf.put((byte)(hsMemo.acquireOffset >> 8 & 0xFF));
                dstBuf.put((byte)(hsMemo.acquireOffset & 0xFF));
                dstBuf.put((byte)(fragLen >> 16 & 0xFF));
                dstBuf.put((byte)(fragLen >> 8 & 0xFF));
                dstBuf.put((byte)(fragLen & 0xFF));
                dstBuf.put(hsMemo.fragment, hsMemo.acquireOffset, fragLen);
            }
            else {
                fragLen = Math.min(fragLen, memo.fragment.length);
                dstBuf.put(memo.fragment, 0, fragLen);
            }
            dstBuf.limit(dstBuf.position());
            dstBuf.position(dstContent);
            if (SSLLogger.isOn && SSLLogger.isOn("record")) {
                SSLLogger.fine("WRITE: " + DTLSOutputRecord.this.protocolVersion + " " + ContentType.nameOf(memo.contentType) + ", length = " + dstBuf.remaining(), new Object[0]);
            }
            final long recordSN = OutputRecord.encrypt(memo.encodeCipher, memo.contentType, dstBuf, dstPos, dstLim, 13, ProtocolVersion.valueOf(memo.majorVersion, memo.minorVersion));
            if (SSLLogger.isOn && SSLLogger.isOn("packet")) {
                final ByteBuffer temporary = dstBuf.duplicate();
                temporary.limit(temporary.position());
                temporary.position(dstPos);
                SSLLogger.fine("Raw write (" + temporary.remaining() + ")", temporary);
            }
            dstBuf.limit(dstLim);
            if (hsMemo != null) {
                final HandshakeMemo handshakeMemo = hsMemo;
                handshakeMemo.acquireOffset += fragLen;
                if (hsMemo.acquireOffset == hsMemo.fragment.length) {
                    ++this.acquireIndex;
                }
                return new Ciphertext(hsMemo.contentType, hsMemo.handshakeType, recordSN);
            }
            if (DTLSOutputRecord.this.isCloseWaiting && memo.contentType == ContentType.ALERT.id) {
                DTLSOutputRecord.this.close();
            }
            ++this.acquireIndex;
            return new Ciphertext(memo.contentType, SSLHandshake.NOT_APPLICABLE.id, recordSN);
        }
        
        private void handshakeHashing(final HandshakeMemo hsFrag, final byte[] hsBody) {
            final byte hsType = hsFrag.handshakeType;
            if (!DTLSOutputRecord.this.handshakeHash.isHashable(hsType)) {
                return;
            }
            final byte[] temporary = { hsFrag.handshakeType, (byte)(hsBody.length >> 16 & 0xFF), (byte)(hsBody.length >> 8 & 0xFF), (byte)(hsBody.length & 0xFF), (byte)(hsFrag.messageSequence >> 8 & 0xFF), (byte)(hsFrag.messageSequence & 0xFF), 0, 0, 0, 0, 0, 0 };
            temporary[9] = temporary[1];
            temporary[10] = temporary[2];
            temporary[11] = temporary[3];
            DTLSOutputRecord.this.handshakeHash.deliver(temporary, 0, 12);
            DTLSOutputRecord.this.handshakeHash.deliver(hsBody, 0, hsBody.length);
        }
        
        boolean isEmpty() {
            return !this.flightIsReady || this.handshakeMemos.isEmpty() || this.acquireIndex >= this.handshakeMemos.size();
        }
        
        boolean hasAlert() {
            for (final RecordMemo memo : this.handshakeMemos) {
                if (memo.contentType == ContentType.ALERT.id) {
                    return true;
                }
            }
            return false;
        }
        
        boolean isRetransmittable() {
            return this.flightIsReady && !this.handshakeMemos.isEmpty() && this.acquireIndex >= this.handshakeMemos.size();
        }
        
        private void setRetransmission() {
            this.acquireIndex = 0;
            for (final RecordMemo memo : this.handshakeMemos) {
                if (memo instanceof HandshakeMemo) {
                    final HandshakeMemo hmemo = (HandshakeMemo)memo;
                    hmemo.acquireOffset = 0;
                }
            }
            if (DTLSOutputRecord.this.packetSize <= 16717 && DTLSOutputRecord.this.packetSize > 256 && this.retransmits-- <= 0) {
                this.shrinkPacketSize();
                this.retransmits = 2;
            }
        }
        
        private void shrinkPacketSize() {
            DTLSOutputRecord.this.packetSize = Math.max(256, DTLSOutputRecord.this.packetSize / 2);
        }
    }
}
