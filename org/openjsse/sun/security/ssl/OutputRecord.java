package org.openjsse.sun.security.ssl;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.Closeable;
import java.io.ByteArrayOutputStream;

abstract class OutputRecord extends ByteArrayOutputStream implements Record, Closeable
{
    SSLCipher.SSLWriteCipher writeCipher;
    TransportContext tc;
    final HandshakeHash handshakeHash;
    boolean firstMessage;
    ProtocolVersion protocolVersion;
    ProtocolVersion helloVersion;
    boolean isFirstAppOutputRecord;
    int packetSize;
    private int fragmentSize;
    volatile boolean isClosed;
    private static final int[] V3toV2CipherMap1;
    private static final int[] V3toV2CipherMap3;
    private static final byte[] HANDSHAKE_MESSAGE_KEY_UPDATE;
    
    OutputRecord(final HandshakeHash handshakeHash, final SSLCipher.SSLWriteCipher writeCipher) {
        this.isFirstAppOutputRecord = true;
        this.writeCipher = writeCipher;
        this.firstMessage = true;
        this.fragmentSize = 16384;
        this.handshakeHash = handshakeHash;
    }
    
    synchronized void setVersion(final ProtocolVersion protocolVersion) {
        this.protocolVersion = protocolVersion;
    }
    
    synchronized void setHelloVersion(final ProtocolVersion helloVersion) {
        this.helloVersion = helloVersion;
    }
    
    boolean isEmpty() {
        return false;
    }
    
    synchronized boolean seqNumIsHuge() {
        return this.writeCipher.authenticator != null && this.writeCipher.authenticator.seqNumIsHuge();
    }
    
    abstract void encodeAlert(final byte p0, final byte p1) throws IOException;
    
    abstract void encodeHandshake(final byte[] p0, final int p1, final int p2) throws IOException;
    
    abstract void encodeChangeCipherSpec() throws IOException;
    
    Ciphertext encode(final ByteBuffer[] srcs, final int srcsOffset, final int srcsLength, final ByteBuffer[] dsts, final int dstsOffset, final int dstsLength) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    void encodeV2NoCipher() throws IOException {
        throw new UnsupportedOperationException();
    }
    
    void deliver(final byte[] source, final int offset, final int length) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    void setDeliverStream(final OutputStream outputStream) {
        throw new UnsupportedOperationException();
    }
    
    synchronized void changeWriteCiphers(final SSLCipher.SSLWriteCipher writeCipher, final boolean useChangeCipherSpec) throws IOException {
        if (this.isClosed()) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.warning("outbound has closed, ignore outbound change_cipher_spec message", new Object[0]);
            }
            return;
        }
        if (useChangeCipherSpec) {
            this.encodeChangeCipherSpec();
        }
        writeCipher.dispose();
        this.writeCipher = writeCipher;
        this.isFirstAppOutputRecord = true;
    }
    
    synchronized void changeWriteCiphers(final SSLCipher.SSLWriteCipher writeCipher, final byte keyUpdateRequest) throws IOException {
        if (this.isClosed()) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.warning("outbound has closed, ignore outbound key_update handshake message", new Object[0]);
            }
            return;
        }
        final byte[] hm = OutputRecord.HANDSHAKE_MESSAGE_KEY_UPDATE.clone();
        hm[hm.length - 1] = keyUpdateRequest;
        this.encodeHandshake(hm, 0, hm.length);
        this.flush();
        writeCipher.dispose();
        this.writeCipher = writeCipher;
        this.isFirstAppOutputRecord = true;
    }
    
    synchronized void changePacketSize(final int packetSize) {
        this.packetSize = packetSize;
    }
    
    synchronized void changeFragmentSize(final int fragmentSize) {
        this.fragmentSize = fragmentSize;
    }
    
    synchronized int getMaxPacketSize() {
        return this.packetSize;
    }
    
    void initHandshaker() {
    }
    
    void finishHandshake() {
    }
    
    void launchRetransmission() {
    }
    
    @Override
    public synchronized void close() throws IOException {
        if (this.isClosed) {
            return;
        }
        this.isClosed = true;
        this.writeCipher.dispose();
    }
    
    boolean isClosed() {
        return this.isClosed;
    }
    
    int calculateFragmentSize(int fragmentLimit) {
        if (this.fragmentSize > 0) {
            fragmentLimit = Math.min(fragmentLimit, this.fragmentSize);
        }
        if (this.protocolVersion.useTLS13PlusSpec()) {
            return fragmentLimit - T13PaddingHolder.zeros.length - 1;
        }
        return fragmentLimit;
    }
    
    static long encrypt(final SSLCipher.SSLWriteCipher encCipher, final byte contentType, final ByteBuffer destination, final int headerOffset, final int dstLim, final int headerSize, final ProtocolVersion protocolVersion) {
        final boolean isDTLS = protocolVersion.isDTLS;
        if (isDTLS) {
            if (protocolVersion.useTLS13PlusSpec()) {
                return d13Encrypt(encCipher, contentType, destination, headerOffset, dstLim, headerSize, protocolVersion);
            }
            return d10Encrypt(encCipher, contentType, destination, headerOffset, dstLim, headerSize, protocolVersion);
        }
        else {
            if (protocolVersion.useTLS13PlusSpec()) {
                return t13Encrypt(encCipher, contentType, destination, headerOffset, dstLim, headerSize, protocolVersion);
            }
            return t10Encrypt(encCipher, contentType, destination, headerOffset, dstLim, headerSize, protocolVersion);
        }
    }
    
    private static long d13Encrypt(final SSLCipher.SSLWriteCipher encCipher, final byte contentType, final ByteBuffer destination, final int headerOffset, final int dstLim, final int headerSize, final ProtocolVersion protocolVersion) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private static long d10Encrypt(final SSLCipher.SSLWriteCipher encCipher, final byte contentType, final ByteBuffer destination, final int headerOffset, final int dstLim, final int headerSize, final ProtocolVersion protocolVersion) {
        final byte[] sequenceNumber = encCipher.authenticator.sequenceNumber();
        encCipher.encrypt(contentType, destination);
        final int fragLen = destination.limit() - headerOffset - headerSize;
        destination.put(headerOffset, contentType);
        destination.put(headerOffset + 1, protocolVersion.major);
        destination.put(headerOffset + 2, protocolVersion.minor);
        destination.put(headerOffset + 3, sequenceNumber[0]);
        destination.put(headerOffset + 4, sequenceNumber[1]);
        destination.put(headerOffset + 5, sequenceNumber[2]);
        destination.put(headerOffset + 6, sequenceNumber[3]);
        destination.put(headerOffset + 7, sequenceNumber[4]);
        destination.put(headerOffset + 8, sequenceNumber[5]);
        destination.put(headerOffset + 9, sequenceNumber[6]);
        destination.put(headerOffset + 10, sequenceNumber[7]);
        destination.put(headerOffset + 11, (byte)(fragLen >> 8));
        destination.put(headerOffset + 12, (byte)fragLen);
        destination.position(destination.limit());
        return Authenticator.toLong(sequenceNumber);
    }
    
    private static long t13Encrypt(final SSLCipher.SSLWriteCipher encCipher, byte contentType, final ByteBuffer destination, final int headerOffset, final int dstLim, final int headerSize, final ProtocolVersion protocolVersion) {
        if (!encCipher.isNullCipher()) {
            final int endOfPt = destination.limit();
            final int startOfPt = destination.position();
            destination.position(endOfPt);
            destination.limit(endOfPt + 1 + T13PaddingHolder.zeros.length);
            destination.put(contentType);
            destination.put(T13PaddingHolder.zeros);
            destination.position(startOfPt);
        }
        ProtocolVersion pv = protocolVersion;
        if (!encCipher.isNullCipher()) {
            pv = ProtocolVersion.TLS12;
            contentType = ContentType.APPLICATION_DATA.id;
        }
        else if (protocolVersion.useTLS13PlusSpec()) {
            pv = ProtocolVersion.TLS12;
        }
        final byte[] sequenceNumber = encCipher.authenticator.sequenceNumber();
        encCipher.encrypt(contentType, destination);
        final int fragLen = destination.limit() - headerOffset - headerSize;
        destination.put(headerOffset, contentType);
        destination.put(headerOffset + 1, pv.major);
        destination.put(headerOffset + 2, pv.minor);
        destination.put(headerOffset + 3, (byte)(fragLen >> 8));
        destination.put(headerOffset + 4, (byte)fragLen);
        destination.position(destination.limit());
        return Authenticator.toLong(sequenceNumber);
    }
    
    private static long t10Encrypt(final SSLCipher.SSLWriteCipher encCipher, final byte contentType, final ByteBuffer destination, final int headerOffset, final int dstLim, final int headerSize, final ProtocolVersion protocolVersion) {
        final byte[] sequenceNumber = encCipher.authenticator.sequenceNumber();
        encCipher.encrypt(contentType, destination);
        final int fragLen = destination.limit() - headerOffset - headerSize;
        destination.put(headerOffset, contentType);
        destination.put(headerOffset + 1, protocolVersion.major);
        destination.put(headerOffset + 2, protocolVersion.minor);
        destination.put(headerOffset + 3, (byte)(fragLen >> 8));
        destination.put(headerOffset + 4, (byte)fragLen);
        destination.position(destination.limit());
        return Authenticator.toLong(sequenceNumber);
    }
    
    long encrypt(final SSLCipher.SSLWriteCipher encCipher, final byte contentType, final int headerSize) {
        if (this.protocolVersion.useTLS13PlusSpec()) {
            return this.t13Encrypt(encCipher, contentType, headerSize);
        }
        return this.t10Encrypt(encCipher, contentType, headerSize);
    }
    
    private long t13Encrypt(final SSLCipher.SSLWriteCipher encCipher, byte contentType, final int headerSize) {
        if (!encCipher.isNullCipher()) {
            this.write(contentType);
            this.write(T13PaddingHolder.zeros, 0, T13PaddingHolder.zeros.length);
        }
        final byte[] sequenceNumber = encCipher.authenticator.sequenceNumber();
        final int position = headerSize;
        final int contentLen = this.count - position;
        final int requiredPacketSize = encCipher.calculatePacketSize(contentLen, headerSize);
        if (requiredPacketSize > this.buf.length) {
            final byte[] newBuf = new byte[requiredPacketSize];
            System.arraycopy(this.buf, 0, newBuf, 0, this.count);
            this.buf = newBuf;
        }
        ProtocolVersion pv = this.protocolVersion;
        if (!encCipher.isNullCipher()) {
            pv = ProtocolVersion.TLS12;
            contentType = ContentType.APPLICATION_DATA.id;
        }
        else {
            pv = ProtocolVersion.TLS12;
        }
        final ByteBuffer destination = ByteBuffer.wrap(this.buf, position, contentLen);
        this.count = headerSize + encCipher.encrypt(contentType, destination);
        final int fragLen = this.count - headerSize;
        this.buf[0] = contentType;
        this.buf[1] = pv.major;
        this.buf[2] = pv.minor;
        this.buf[3] = (byte)(fragLen >> 8 & 0xFF);
        this.buf[4] = (byte)(fragLen & 0xFF);
        return Authenticator.toLong(sequenceNumber);
    }
    
    private long t10Encrypt(final SSLCipher.SSLWriteCipher encCipher, final byte contentType, final int headerSize) {
        final byte[] sequenceNumber = encCipher.authenticator.sequenceNumber();
        final int position = headerSize + this.writeCipher.getExplicitNonceSize();
        final int contentLen = this.count - position;
        final int requiredPacketSize = encCipher.calculatePacketSize(contentLen, headerSize);
        if (requiredPacketSize > this.buf.length) {
            final byte[] newBuf = new byte[requiredPacketSize];
            System.arraycopy(this.buf, 0, newBuf, 0, this.count);
            this.buf = newBuf;
        }
        final ByteBuffer destination = ByteBuffer.wrap(this.buf, position, contentLen);
        this.count = headerSize + encCipher.encrypt(contentType, destination);
        final int fragLen = this.count - headerSize;
        this.buf[0] = contentType;
        this.buf[1] = this.protocolVersion.major;
        this.buf[2] = this.protocolVersion.minor;
        this.buf[3] = (byte)(fragLen >> 8 & 0xFF);
        this.buf[4] = (byte)(fragLen & 0xFF);
        return Authenticator.toLong(sequenceNumber);
    }
    
    static ByteBuffer encodeV2ClientHello(final byte[] fragment, final int offset, final int length) throws IOException {
        final int v3SessIdLenOffset = offset + 34;
        final int v3SessIdLen = fragment[v3SessIdLenOffset];
        final int v3CSLenOffset = v3SessIdLenOffset + 1 + v3SessIdLen;
        final int v3CSLen = ((fragment[v3CSLenOffset] & 0xFF) << 8) + (fragment[v3CSLenOffset + 1] & 0xFF);
        final int cipherSpecs = v3CSLen / 2;
        final int v2MaxMsgLen = 11 + cipherSpecs * 6 + 3 + 32;
        final byte[] dstBytes = new byte[v2MaxMsgLen];
        final ByteBuffer dstBuf = ByteBuffer.wrap(dstBytes);
        int v3CSOffset = v3CSLenOffset + 2;
        int v2CSLen = 0;
        dstBuf.position(11);
        boolean containsRenegoInfoSCSV = false;
        for (int i = 0; i < cipherSpecs; ++i) {
            final byte byte1 = fragment[v3CSOffset++];
            final byte byte2 = fragment[v3CSOffset++];
            v2CSLen += V3toV2CipherSuite(dstBuf, byte1, byte2);
            if (!containsRenegoInfoSCSV && byte1 == 0 && byte2 == -1) {
                containsRenegoInfoSCSV = true;
            }
        }
        if (!containsRenegoInfoSCSV) {
            v2CSLen += V3toV2CipherSuite(dstBuf, (byte)0, (byte)(-1));
        }
        dstBuf.put(fragment, offset + 2, 32);
        final int msgLen = dstBuf.position() - 2;
        dstBuf.position(0);
        dstBuf.put((byte)(0x80 | (msgLen >>> 8 & 0xFF)));
        dstBuf.put((byte)(msgLen & 0xFF));
        dstBuf.put(SSLHandshake.CLIENT_HELLO.id);
        dstBuf.put(fragment[offset]);
        dstBuf.put(fragment[offset + 1]);
        dstBuf.put((byte)(v2CSLen >>> 8));
        dstBuf.put((byte)(v2CSLen & 0xFF));
        dstBuf.put((byte)0);
        dstBuf.put((byte)0);
        dstBuf.put((byte)0);
        dstBuf.put((byte)32);
        dstBuf.position(0);
        dstBuf.limit(msgLen + 2);
        return dstBuf;
    }
    
    private static int V3toV2CipherSuite(final ByteBuffer dstBuf, final byte byte1, final byte byte2) {
        dstBuf.put((byte)0);
        dstBuf.put(byte1);
        dstBuf.put(byte2);
        if ((byte2 & 0xFF) > 10 || OutputRecord.V3toV2CipherMap1[byte2] == -1) {
            return 3;
        }
        dstBuf.put((byte)OutputRecord.V3toV2CipherMap1[byte2]);
        dstBuf.put((byte)0);
        dstBuf.put((byte)OutputRecord.V3toV2CipherMap3[byte2]);
        return 6;
    }
    
    static {
        V3toV2CipherMap1 = new int[] { -1, -1, -1, 2, 1, -1, 4, 5, -1, 6, 7 };
        V3toV2CipherMap3 = new int[] { -1, -1, -1, 128, 128, -1, 128, 128, -1, 64, 192 };
        HANDSHAKE_MESSAGE_KEY_UPDATE = new byte[] { SSLHandshake.KEY_UPDATE.id, 0, 0, 1, 0 };
    }
    
    private static final class T13PaddingHolder
    {
        private static final byte[] zeros;
        
        static {
            zeros = new byte[16];
        }
    }
}
