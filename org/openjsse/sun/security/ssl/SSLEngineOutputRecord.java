package org.openjsse.sun.security.ssl;

import java.util.Iterator;
import java.util.LinkedList;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.nio.ByteBuffer;

final class SSLEngineOutputRecord extends OutputRecord implements SSLRecord
{
    private HandshakeFragment fragmenter;
    private boolean isTalkingToV2;
    private ByteBuffer v2ClientHello;
    private volatile boolean isCloseWaiting;
    
    SSLEngineOutputRecord(final HandshakeHash handshakeHash) {
        super(handshakeHash, SSLCipher.SSLWriteCipher.nullTlsWriteCipher());
        this.fragmenter = null;
        this.isTalkingToV2 = false;
        this.v2ClientHello = null;
        this.isCloseWaiting = false;
        this.packetSize = 16709;
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
    void encodeAlert(final byte level, final byte description) throws IOException {
        if (this.isClosed()) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.warning("outbound has closed, ignore outbound alert message: " + Alert.nameOf(description), new Object[0]);
            }
            return;
        }
        if (this.fragmenter == null) {
            this.fragmenter = new HandshakeFragment();
        }
        this.fragmenter.queueUpAlert(level, description);
    }
    
    @Override
    void encodeHandshake(final byte[] source, final int offset, final int length) throws IOException {
        if (this.isClosed()) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.warning("outbound has closed, ignore outbound handshake message", ByteBuffer.wrap(source, offset, length));
            }
            return;
        }
        if (this.fragmenter == null) {
            this.fragmenter = new HandshakeFragment();
        }
        if (this.firstMessage) {
            this.firstMessage = false;
            if (this.helloVersion == ProtocolVersion.SSL20Hello && source[offset] == SSLHandshake.CLIENT_HELLO.id && source[offset + 4 + 2 + 32] == 0) {
                (this.v2ClientHello = OutputRecord.encodeV2ClientHello(source, offset + 4, length - 4)).position(2);
                this.handshakeHash.deliver(this.v2ClientHello);
                this.v2ClientHello.position(0);
                return;
            }
        }
        final byte handshakeType = source[offset];
        if (this.handshakeHash.isHashable(handshakeType)) {
            this.handshakeHash.deliver(source, offset, length);
        }
        this.fragmenter.queueUpFragment(source, offset, length);
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
            this.fragmenter = new HandshakeFragment();
        }
        this.fragmenter.queueUpChangeCipherSpec();
    }
    
    @Override
    void encodeV2NoCipher() throws IOException {
        this.isTalkingToV2 = true;
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
    
    private Ciphertext encode(final ByteBuffer[] sources, int offset, int length, final ByteBuffer destination) throws IOException {
        if (this.writeCipher.authenticator.seqNumOverflow()) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.fine("sequence number extremely close to overflow (2^64-1 packets). Closing connection.", new Object[0]);
            }
            throw new SSLHandshakeException("sequence number overflow");
        }
        final Ciphertext ct = this.acquireCiphertext(destination);
        if (ct != null) {
            return ct;
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
        final int dstLim = destination.limit();
        boolean isFirstRecordOfThePayload = true;
        int packetLeftSize = Math.min(16709, this.packetSize);
        boolean needMorePayload = true;
        long recordSN = 0L;
        while (needMorePayload) {
            int fragLen;
            if (isFirstRecordOfThePayload && this.needToSplitPayload()) {
                needMorePayload = true;
                fragLen = 1;
                isFirstRecordOfThePayload = false;
            }
            else {
                needMorePayload = false;
                if (packetLeftSize > 0) {
                    fragLen = this.writeCipher.calculateFragmentSize(packetLeftSize, 5);
                    fragLen = Math.min(fragLen, 16384);
                }
                else {
                    fragLen = 16384;
                }
                fragLen = this.calculateFragmentSize(fragLen);
            }
            final int dstPos = destination.position();
            final int dstContent = dstPos + 5 + this.writeCipher.getExplicitNonceSize();
            destination.position(dstContent);
            int remains = Math.min(fragLen, destination.remaining());
            fragLen = 0;
            for (int srcsLen = offset + length, j = offset; j < srcsLen && remains > 0; ++j) {
                final int amount = Math.min(sources[j].remaining(), remains);
                final int srcLimit = sources[j].limit();
                sources[j].limit(sources[j].position() + amount);
                destination.put(sources[j]);
                sources[j].limit(srcLimit);
                remains -= amount;
                fragLen += amount;
                if (remains > 0) {
                    ++offset;
                    --length;
                }
            }
            destination.limit(destination.position());
            destination.position(dstContent);
            if (SSLLogger.isOn && SSLLogger.isOn("record")) {
                SSLLogger.fine("WRITE: " + this.protocolVersion + " " + ContentType.APPLICATION_DATA.name + ", length = " + destination.remaining(), new Object[0]);
            }
            recordSN = OutputRecord.encrypt(this.writeCipher, ContentType.APPLICATION_DATA.id, destination, dstPos, dstLim, 5, this.protocolVersion);
            if (SSLLogger.isOn && SSLLogger.isOn("packet")) {
                final ByteBuffer temporary = destination.duplicate();
                temporary.limit(temporary.position());
                temporary.position(dstPos);
                SSLLogger.fine("Raw write", temporary);
            }
            packetLeftSize -= destination.position() - dstPos;
            destination.limit(dstLim);
            if (this.isFirstAppOutputRecord) {
                this.isFirstAppOutputRecord = false;
            }
        }
        return new Ciphertext(ContentType.APPLICATION_DATA.id, SSLHandshake.NOT_APPLICABLE.id, recordSN);
    }
    
    private Ciphertext acquireCiphertext(final ByteBuffer destination) throws IOException {
        if (this.isTalkingToV2) {
            destination.put(SSLRecord.v2NoCipher);
            if (SSLLogger.isOn && SSLLogger.isOn("packet")) {
                SSLLogger.fine("Raw write", SSLRecord.v2NoCipher);
            }
            this.isTalkingToV2 = false;
            return new Ciphertext(ContentType.ALERT.id, SSLHandshake.NOT_APPLICABLE.id, -1L);
        }
        if (this.v2ClientHello != null) {
            if (SSLLogger.isOn) {
                if (SSLLogger.isOn("record")) {
                    SSLLogger.fine(Thread.currentThread().getName() + ", WRITE: SSLv2 ClientHello message, length = " + this.v2ClientHello.remaining(), new Object[0]);
                }
                if (SSLLogger.isOn("packet")) {
                    SSLLogger.fine("Raw write", this.v2ClientHello);
                }
            }
            destination.put(this.v2ClientHello);
            this.v2ClientHello = null;
            return new Ciphertext(ContentType.HANDSHAKE.id, SSLHandshake.CLIENT_HELLO.id, -1L);
        }
        if (this.fragmenter != null) {
            return this.fragmenter.acquireCiphertext(destination);
        }
        return null;
    }
    
    @Override
    boolean isEmpty() {
        return !this.isTalkingToV2 && this.v2ClientHello == null && (this.fragmenter == null || this.fragmenter.isEmpty());
    }
    
    boolean needToSplitPayload() {
        return !this.protocolVersion.useTLS11PlusSpec() && this.writeCipher.isCBCMode() && !this.isFirstAppOutputRecord && Record.enableCBCProtection;
    }
    
    private static class RecordMemo
    {
        byte contentType;
        byte majorVersion;
        byte minorVersion;
        SSLCipher.SSLWriteCipher encodeCipher;
        byte[] fragment;
    }
    
    private static class HandshakeMemo extends RecordMemo
    {
        byte handshakeType;
        int acquireOffset;
    }
    
    final class HandshakeFragment
    {
        private LinkedList<RecordMemo> handshakeMemos;
        
        HandshakeFragment() {
            this.handshakeMemos = new LinkedList<RecordMemo>();
        }
        
        void queueUpFragment(final byte[] source, final int offset, final int length) throws IOException {
            final HandshakeMemo memo = new HandshakeMemo();
            memo.contentType = ContentType.HANDSHAKE.id;
            memo.majorVersion = SSLEngineOutputRecord.this.protocolVersion.major;
            memo.minorVersion = SSLEngineOutputRecord.this.protocolVersion.minor;
            memo.encodeCipher = SSLEngineOutputRecord.this.writeCipher;
            memo.handshakeType = source[offset];
            memo.acquireOffset = 0;
            System.arraycopy(source, offset + 4, memo.fragment = new byte[length - 4], 0, length - 4);
            this.handshakeMemos.add(memo);
        }
        
        void queueUpChangeCipherSpec() {
            final RecordMemo memo = new RecordMemo();
            memo.contentType = ContentType.CHANGE_CIPHER_SPEC.id;
            memo.majorVersion = SSLEngineOutputRecord.this.protocolVersion.major;
            memo.minorVersion = SSLEngineOutputRecord.this.protocolVersion.minor;
            memo.encodeCipher = SSLEngineOutputRecord.this.writeCipher;
            (memo.fragment = new byte[1])[0] = 1;
            this.handshakeMemos.add(memo);
        }
        
        void queueUpAlert(final byte level, final byte description) {
            final RecordMemo memo = new RecordMemo();
            memo.contentType = ContentType.ALERT.id;
            memo.majorVersion = SSLEngineOutputRecord.this.protocolVersion.major;
            memo.minorVersion = SSLEngineOutputRecord.this.protocolVersion.minor;
            memo.encodeCipher = SSLEngineOutputRecord.this.writeCipher;
            (memo.fragment = new byte[2])[0] = level;
            memo.fragment[1] = description;
            this.handshakeMemos.add(memo);
        }
        
        Ciphertext acquireCiphertext(final ByteBuffer dstBuf) throws IOException {
            if (this.isEmpty()) {
                return null;
            }
            final RecordMemo memo = this.handshakeMemos.getFirst();
            HandshakeMemo hsMemo = null;
            if (memo.contentType == ContentType.HANDSHAKE.id) {
                hsMemo = (HandshakeMemo)memo;
            }
            int fragLen;
            if (SSLEngineOutputRecord.this.packetSize > 0) {
                fragLen = Math.min(16709, SSLEngineOutputRecord.this.packetSize);
                fragLen = memo.encodeCipher.calculateFragmentSize(fragLen, 5);
            }
            else {
                fragLen = 16384;
            }
            fragLen = SSLEngineOutputRecord.this.calculateFragmentSize(fragLen);
            final int dstPos = dstBuf.position();
            final int dstLim = dstBuf.limit();
            final int dstContent = dstPos + 5 + memo.encodeCipher.getExplicitNonceSize();
            dstBuf.position(dstContent);
            if (hsMemo != null) {
                int chipLen;
                for (int remainingFragLen = fragLen; remainingFragLen > 0 && !this.handshakeMemos.isEmpty(); remainingFragLen -= chipLen) {
                    final int memoFragLen = hsMemo.fragment.length;
                    if (hsMemo.acquireOffset == 0) {
                        if (remainingFragLen <= 4) {
                            break;
                        }
                        dstBuf.put(hsMemo.handshakeType);
                        dstBuf.put((byte)(memoFragLen >> 16 & 0xFF));
                        dstBuf.put((byte)(memoFragLen >> 8 & 0xFF));
                        dstBuf.put((byte)(memoFragLen & 0xFF));
                        remainingFragLen -= 4;
                    }
                    chipLen = Math.min(remainingFragLen, memoFragLen - hsMemo.acquireOffset);
                    dstBuf.put(hsMemo.fragment, hsMemo.acquireOffset, chipLen);
                    final HandshakeMemo handshakeMemo = hsMemo;
                    handshakeMemo.acquireOffset += chipLen;
                    if (hsMemo.acquireOffset == memoFragLen) {
                        this.handshakeMemos.removeFirst();
                        if (remainingFragLen > chipLen && !this.handshakeMemos.isEmpty()) {
                            final RecordMemo rm = this.handshakeMemos.getFirst();
                            if (rm.contentType != ContentType.HANDSHAKE.id || rm.encodeCipher != hsMemo.encodeCipher) {
                                break;
                            }
                            hsMemo = (HandshakeMemo)rm;
                        }
                    }
                }
            }
            else {
                fragLen = Math.min(fragLen, memo.fragment.length);
                dstBuf.put(memo.fragment, 0, fragLen);
                this.handshakeMemos.removeFirst();
            }
            dstBuf.limit(dstBuf.position());
            dstBuf.position(dstContent);
            if (SSLLogger.isOn && SSLLogger.isOn("record")) {
                SSLLogger.fine("WRITE: " + SSLEngineOutputRecord.this.protocolVersion + " " + ContentType.nameOf(memo.contentType) + ", length = " + dstBuf.remaining(), new Object[0]);
            }
            final long recordSN = OutputRecord.encrypt(memo.encodeCipher, memo.contentType, dstBuf, dstPos, dstLim, 5, ProtocolVersion.valueOf(memo.majorVersion, memo.minorVersion));
            if (SSLLogger.isOn && SSLLogger.isOn("packet")) {
                final ByteBuffer temporary = dstBuf.duplicate();
                temporary.limit(temporary.position());
                temporary.position(dstPos);
                SSLLogger.fine("Raw write", temporary);
            }
            dstBuf.limit(dstLim);
            if (hsMemo != null) {
                return new Ciphertext(hsMemo.contentType, hsMemo.handshakeType, recordSN);
            }
            if (SSLEngineOutputRecord.this.isCloseWaiting && memo.contentType == ContentType.ALERT.id) {
                SSLEngineOutputRecord.this.close();
            }
            return new Ciphertext(memo.contentType, SSLHandshake.NOT_APPLICABLE.id, recordSN);
        }
        
        boolean isEmpty() {
            return this.handshakeMemos.isEmpty();
        }
        
        boolean hasAlert() {
            for (final RecordMemo memo : this.handshakeMemos) {
                if (memo.contentType == ContentType.ALERT.id) {
                    return true;
                }
            }
            return false;
        }
    }
}
