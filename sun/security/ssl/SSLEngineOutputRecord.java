package sun.security.ssl;

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
    void encodeAlert(final byte b, final byte b2) throws IOException {
        if (this.isClosed()) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.warning("outbound has closed, ignore outbound alert message: " + Alert.nameOf(b2), new Object[0]);
            }
            return;
        }
        if (this.fragmenter == null) {
            this.fragmenter = new HandshakeFragment();
        }
        this.fragmenter.queueUpAlert(b, b2);
    }
    
    @Override
    void encodeHandshake(final byte[] array, final int n, final int n2) throws IOException {
        if (this.isClosed()) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.warning("outbound has closed, ignore outbound handshake message", ByteBuffer.wrap(array, n, n2));
            }
            return;
        }
        if (this.fragmenter == null) {
            this.fragmenter = new HandshakeFragment();
        }
        if (this.firstMessage) {
            this.firstMessage = false;
            if (this.helloVersion == ProtocolVersion.SSL20Hello && array[n] == SSLHandshake.CLIENT_HELLO.id && array[n + 4 + 2 + 32] == 0) {
                (this.v2ClientHello = OutputRecord.encodeV2ClientHello(array, n + 4, n2 - 4)).position(2);
                this.handshakeHash.deliver(this.v2ClientHello);
                this.v2ClientHello.position(0);
                return;
            }
        }
        if (this.handshakeHash.isHashable(array[n])) {
            this.handshakeHash.deliver(array, n, n2);
        }
        this.fragmenter.queueUpFragment(array, n, n2);
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
    Ciphertext encode(ByteBuffer[] array, final int n, final int n2, final ByteBuffer[] array2, final int n3, final int n4) throws IOException {
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
            array = null;
        }
        return this.encode(array, n, n2, array2[0]);
    }
    
    private Ciphertext encode(final ByteBuffer[] array, int n, int n2, final ByteBuffer byteBuffer) throws IOException {
        if (this.writeCipher.authenticator.seqNumOverflow()) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.fine("sequence number extremely close to overflow (2^64-1 packets). Closing connection.", new Object[0]);
            }
            throw new SSLHandshakeException("sequence number overflow");
        }
        final Ciphertext acquireCiphertext = this.acquireCiphertext(byteBuffer);
        if (acquireCiphertext != null) {
            return acquireCiphertext;
        }
        if (array == null || array.length == 0) {
            return null;
        }
        int n3 = 0;
        for (int i = n; i < n + n2; ++i) {
            n3 += array[i].remaining();
        }
        if (n3 == 0) {
            return null;
        }
        final int limit = byteBuffer.limit();
        int n4 = 1;
        int min = Math.min(16709, this.packetSize);
        int j = 1;
        long encrypt = 0L;
        while (j != 0) {
            int calculateFragmentSize;
            if (n4 != 0 && this.needToSplitPayload()) {
                j = 1;
                calculateFragmentSize = 1;
                n4 = 0;
            }
            else {
                j = 0;
                int min2;
                if (min > 0) {
                    min2 = Math.min(this.writeCipher.calculateFragmentSize(min, 5), 16384);
                }
                else {
                    min2 = 16384;
                }
                calculateFragmentSize = this.calculateFragmentSize(min2);
            }
            final int position = byteBuffer.position();
            final int n5 = position + 5 + this.writeCipher.getExplicitNonceSize();
            byteBuffer.position(n5);
            int min3 = Math.min(calculateFragmentSize, byteBuffer.remaining());
            int n6 = 0;
            for (int n7 = n + n2, n8 = n; n8 < n7 && min3 > 0; ++n8) {
                final int min4 = Math.min(array[n8].remaining(), min3);
                final int limit2 = array[n8].limit();
                array[n8].limit(array[n8].position() + min4);
                byteBuffer.put(array[n8]);
                array[n8].limit(limit2);
                min3 -= min4;
                n6 += min4;
                if (min3 > 0) {
                    ++n;
                    --n2;
                }
            }
            byteBuffer.limit(byteBuffer.position());
            byteBuffer.position(n5);
            if (SSLLogger.isOn && SSLLogger.isOn("record")) {
                SSLLogger.fine("WRITE: " + this.protocolVersion + " " + ContentType.APPLICATION_DATA.name + ", length = " + byteBuffer.remaining(), new Object[0]);
            }
            encrypt = OutputRecord.encrypt(this.writeCipher, ContentType.APPLICATION_DATA.id, byteBuffer, position, limit, 5, this.protocolVersion);
            if (SSLLogger.isOn && SSLLogger.isOn("packet")) {
                final ByteBuffer duplicate = byteBuffer.duplicate();
                duplicate.limit(duplicate.position());
                duplicate.position(position);
                SSLLogger.fine("Raw write", duplicate);
            }
            min -= byteBuffer.position() - position;
            byteBuffer.limit(limit);
            if (this.isFirstAppOutputRecord) {
                this.isFirstAppOutputRecord = false;
            }
        }
        return new Ciphertext(ContentType.APPLICATION_DATA.id, SSLHandshake.NOT_APPLICABLE.id, encrypt);
    }
    
    private Ciphertext acquireCiphertext(final ByteBuffer byteBuffer) throws IOException {
        if (this.isTalkingToV2) {
            byteBuffer.put(SSLRecord.v2NoCipher);
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
            byteBuffer.put(this.v2ClientHello);
            this.v2ClientHello = null;
            return new Ciphertext(ContentType.HANDSHAKE.id, SSLHandshake.CLIENT_HELLO.id, -1L);
        }
        if (this.fragmenter != null) {
            return this.fragmenter.acquireCiphertext(byteBuffer);
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
        
        void queueUpFragment(final byte[] array, final int n, final int n2) throws IOException {
            final HandshakeMemo handshakeMemo = new HandshakeMemo();
            handshakeMemo.contentType = ContentType.HANDSHAKE.id;
            handshakeMemo.majorVersion = SSLEngineOutputRecord.this.protocolVersion.major;
            handshakeMemo.minorVersion = SSLEngineOutputRecord.this.protocolVersion.minor;
            handshakeMemo.encodeCipher = SSLEngineOutputRecord.this.writeCipher;
            handshakeMemo.handshakeType = array[n];
            handshakeMemo.acquireOffset = 0;
            System.arraycopy(array, n + 4, handshakeMemo.fragment = new byte[n2 - 4], 0, n2 - 4);
            this.handshakeMemos.add(handshakeMemo);
        }
        
        void queueUpChangeCipherSpec() {
            final RecordMemo recordMemo = new RecordMemo();
            recordMemo.contentType = ContentType.CHANGE_CIPHER_SPEC.id;
            recordMemo.majorVersion = SSLEngineOutputRecord.this.protocolVersion.major;
            recordMemo.minorVersion = SSLEngineOutputRecord.this.protocolVersion.minor;
            recordMemo.encodeCipher = SSLEngineOutputRecord.this.writeCipher;
            (recordMemo.fragment = new byte[1])[0] = 1;
            this.handshakeMemos.add(recordMemo);
        }
        
        void queueUpAlert(final byte b, final byte b2) {
            final RecordMemo recordMemo = new RecordMemo();
            recordMemo.contentType = ContentType.ALERT.id;
            recordMemo.majorVersion = SSLEngineOutputRecord.this.protocolVersion.major;
            recordMemo.minorVersion = SSLEngineOutputRecord.this.protocolVersion.minor;
            recordMemo.encodeCipher = SSLEngineOutputRecord.this.writeCipher;
            (recordMemo.fragment = new byte[2])[0] = b;
            recordMemo.fragment[1] = b2;
            this.handshakeMemos.add(recordMemo);
        }
        
        Ciphertext acquireCiphertext(final ByteBuffer byteBuffer) throws IOException {
            if (this.isEmpty()) {
                return null;
            }
            final RecordMemo recordMemo = this.handshakeMemos.getFirst();
            HandshakeMemo handshakeMemo = null;
            if (recordMemo.contentType == ContentType.HANDSHAKE.id) {
                handshakeMemo = (HandshakeMemo)recordMemo;
            }
            int calculateFragmentSize;
            if (SSLEngineOutputRecord.this.packetSize > 0) {
                calculateFragmentSize = recordMemo.encodeCipher.calculateFragmentSize(Math.min(16709, SSLEngineOutputRecord.this.packetSize), 5);
            }
            else {
                calculateFragmentSize = 16384;
            }
            final int calculateFragmentSize2 = SSLEngineOutputRecord.this.calculateFragmentSize(calculateFragmentSize);
            final int position = byteBuffer.position();
            final int limit = byteBuffer.limit();
            final int n = position + 5 + recordMemo.encodeCipher.getExplicitNonceSize();
            byteBuffer.position(n);
            if (handshakeMemo != null) {
                int min;
                for (int n2 = calculateFragmentSize2; n2 > 0 && !this.handshakeMemos.isEmpty(); n2 -= min) {
                    final int length = handshakeMemo.fragment.length;
                    if (handshakeMemo.acquireOffset == 0) {
                        if (n2 <= 4) {
                            break;
                        }
                        byteBuffer.put(handshakeMemo.handshakeType);
                        byteBuffer.put((byte)(length >> 16 & 0xFF));
                        byteBuffer.put((byte)(length >> 8 & 0xFF));
                        byteBuffer.put((byte)(length & 0xFF));
                        n2 -= 4;
                    }
                    min = Math.min(n2, length - handshakeMemo.acquireOffset);
                    byteBuffer.put(handshakeMemo.fragment, handshakeMemo.acquireOffset, min);
                    final HandshakeMemo handshakeMemo2 = handshakeMemo;
                    handshakeMemo2.acquireOffset += min;
                    if (handshakeMemo.acquireOffset == length) {
                        this.handshakeMemos.removeFirst();
                        if (n2 > min && !this.handshakeMemos.isEmpty()) {
                            final RecordMemo recordMemo2 = this.handshakeMemos.getFirst();
                            if (recordMemo2.contentType != ContentType.HANDSHAKE.id || recordMemo2.encodeCipher != handshakeMemo.encodeCipher) {
                                break;
                            }
                            handshakeMemo = (HandshakeMemo)recordMemo2;
                        }
                    }
                }
            }
            else {
                byteBuffer.put(recordMemo.fragment, 0, Math.min(calculateFragmentSize2, recordMemo.fragment.length));
                this.handshakeMemos.removeFirst();
            }
            byteBuffer.limit(byteBuffer.position());
            byteBuffer.position(n);
            if (SSLLogger.isOn && SSLLogger.isOn("record")) {
                SSLLogger.fine("WRITE: " + SSLEngineOutputRecord.this.protocolVersion + " " + ContentType.nameOf(recordMemo.contentType) + ", length = " + byteBuffer.remaining(), new Object[0]);
            }
            final long encrypt = OutputRecord.encrypt(recordMemo.encodeCipher, recordMemo.contentType, byteBuffer, position, limit, 5, ProtocolVersion.valueOf(recordMemo.majorVersion, recordMemo.minorVersion));
            if (SSLLogger.isOn && SSLLogger.isOn("packet")) {
                final ByteBuffer duplicate = byteBuffer.duplicate();
                duplicate.limit(duplicate.position());
                duplicate.position(position);
                SSLLogger.fine("Raw write", duplicate);
            }
            byteBuffer.limit(limit);
            if (handshakeMemo != null) {
                return new Ciphertext(handshakeMemo.contentType, handshakeMemo.handshakeType, encrypt);
            }
            if (SSLEngineOutputRecord.this.isCloseWaiting && recordMemo.contentType == ContentType.ALERT.id) {
                SSLEngineOutputRecord.this.close();
            }
            return new Ciphertext(recordMemo.contentType, SSLHandshake.NOT_APPLICABLE.id, encrypt);
        }
        
        boolean isEmpty() {
            return this.handshakeMemos.isEmpty();
        }
        
        boolean hasAlert() {
            final Iterator<Object> iterator = this.handshakeMemos.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().contentType == ContentType.ALERT.id) {
                    return true;
                }
            }
            return false;
        }
    }
}
