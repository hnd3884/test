package sun.security.ssl;

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
    
    Ciphertext encode(final ByteBuffer[] array, final int n, final int n2, final ByteBuffer[] array2, final int n3, final int n4) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    void encodeV2NoCipher() throws IOException {
        throw new UnsupportedOperationException();
    }
    
    void deliver(final byte[] array, final int n, final int n2) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    void setDeliverStream(final OutputStream outputStream) {
        throw new UnsupportedOperationException();
    }
    
    synchronized void changeWriteCiphers(final SSLCipher.SSLWriteCipher writeCipher, final boolean b) throws IOException {
        if (this.isClosed()) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.warning("outbound has closed, ignore outbound change_cipher_spec message", new Object[0]);
            }
            return;
        }
        if (b) {
            this.encodeChangeCipherSpec();
        }
        writeCipher.dispose();
        this.writeCipher = writeCipher;
        this.isFirstAppOutputRecord = true;
    }
    
    synchronized void changeWriteCiphers(final SSLCipher.SSLWriteCipher writeCipher, final byte b) throws IOException {
        if (this.isClosed()) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.warning("outbound has closed, ignore outbound key_update handshake message", new Object[0]);
            }
            return;
        }
        final byte[] array = OutputRecord.HANDSHAKE_MESSAGE_KEY_UPDATE.clone();
        array[array.length - 1] = b;
        this.encodeHandshake(array, 0, array.length);
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
    
    int calculateFragmentSize(int min) {
        if (this.fragmentSize > 0) {
            min = Math.min(min, this.fragmentSize);
        }
        if (this.protocolVersion.useTLS13PlusSpec()) {
            return min - T13PaddingHolder.zeros.length - 1;
        }
        return min;
    }
    
    static long encrypt(final SSLCipher.SSLWriteCipher sslWriteCipher, final byte b, final ByteBuffer byteBuffer, final int n, final int n2, final int n3, final ProtocolVersion protocolVersion) {
        if (protocolVersion.useTLS13PlusSpec()) {
            return t13Encrypt(sslWriteCipher, b, byteBuffer, n, n2, n3, protocolVersion);
        }
        return t10Encrypt(sslWriteCipher, b, byteBuffer, n, n2, n3, protocolVersion);
    }
    
    private static long t13Encrypt(final SSLCipher.SSLWriteCipher sslWriteCipher, byte id, final ByteBuffer byteBuffer, final int n, final int n2, final int n3, final ProtocolVersion protocolVersion) {
        if (!sslWriteCipher.isNullCipher()) {
            final int limit = byteBuffer.limit();
            final int position = byteBuffer.position();
            byteBuffer.position(limit);
            byteBuffer.limit(limit + 1 + T13PaddingHolder.zeros.length);
            byteBuffer.put(id);
            byteBuffer.put(T13PaddingHolder.zeros);
            byteBuffer.position(position);
        }
        ProtocolVersion protocolVersion2 = protocolVersion;
        if (!sslWriteCipher.isNullCipher()) {
            protocolVersion2 = ProtocolVersion.TLS12;
            id = ContentType.APPLICATION_DATA.id;
        }
        else if (protocolVersion.useTLS13PlusSpec()) {
            protocolVersion2 = ProtocolVersion.TLS12;
        }
        final byte[] sequenceNumber = sslWriteCipher.authenticator.sequenceNumber();
        sslWriteCipher.encrypt(id, byteBuffer);
        final int n4 = byteBuffer.limit() - n - n3;
        byteBuffer.put(n, id);
        byteBuffer.put(n + 1, protocolVersion2.major);
        byteBuffer.put(n + 2, protocolVersion2.minor);
        byteBuffer.put(n + 3, (byte)(n4 >> 8));
        byteBuffer.put(n + 4, (byte)n4);
        byteBuffer.position(byteBuffer.limit());
        return Authenticator.toLong(sequenceNumber);
    }
    
    private static long t10Encrypt(final SSLCipher.SSLWriteCipher sslWriteCipher, final byte b, final ByteBuffer byteBuffer, final int n, final int n2, final int n3, final ProtocolVersion protocolVersion) {
        final byte[] sequenceNumber = sslWriteCipher.authenticator.sequenceNumber();
        sslWriteCipher.encrypt(b, byteBuffer);
        final int n4 = byteBuffer.limit() - n - n3;
        byteBuffer.put(n, b);
        byteBuffer.put(n + 1, protocolVersion.major);
        byteBuffer.put(n + 2, protocolVersion.minor);
        byteBuffer.put(n + 3, (byte)(n4 >> 8));
        byteBuffer.put(n + 4, (byte)n4);
        byteBuffer.position(byteBuffer.limit());
        return Authenticator.toLong(sequenceNumber);
    }
    
    long encrypt(final SSLCipher.SSLWriteCipher sslWriteCipher, final byte b, final int n) {
        if (this.protocolVersion.useTLS13PlusSpec()) {
            return this.t13Encrypt(sslWriteCipher, b, n);
        }
        return this.t10Encrypt(sslWriteCipher, b, n);
    }
    
    private long t13Encrypt(final SSLCipher.SSLWriteCipher sslWriteCipher, byte id, final int n) {
        if (!sslWriteCipher.isNullCipher()) {
            this.write(id);
            this.write(T13PaddingHolder.zeros, 0, T13PaddingHolder.zeros.length);
        }
        final byte[] sequenceNumber = sslWriteCipher.authenticator.sequenceNumber();
        final int n2 = this.count - n;
        final int calculatePacketSize = sslWriteCipher.calculatePacketSize(n2, n);
        if (calculatePacketSize > this.buf.length) {
            final byte[] buf = new byte[calculatePacketSize];
            System.arraycopy(this.buf, 0, buf, 0, this.count);
            this.buf = buf;
        }
        final ProtocolVersion protocolVersion = this.protocolVersion;
        ProtocolVersion protocolVersion2;
        if (!sslWriteCipher.isNullCipher()) {
            protocolVersion2 = ProtocolVersion.TLS12;
            id = ContentType.APPLICATION_DATA.id;
        }
        else {
            protocolVersion2 = ProtocolVersion.TLS12;
        }
        this.count = n + sslWriteCipher.encrypt(id, ByteBuffer.wrap(this.buf, n, n2));
        final int n3 = this.count - n;
        this.buf[0] = id;
        this.buf[1] = protocolVersion2.major;
        this.buf[2] = protocolVersion2.minor;
        this.buf[3] = (byte)(n3 >> 8 & 0xFF);
        this.buf[4] = (byte)(n3 & 0xFF);
        return Authenticator.toLong(sequenceNumber);
    }
    
    private long t10Encrypt(final SSLCipher.SSLWriteCipher sslWriteCipher, final byte b, final int n) {
        final byte[] sequenceNumber = sslWriteCipher.authenticator.sequenceNumber();
        final int n2 = n + this.writeCipher.getExplicitNonceSize();
        final int n3 = this.count - n2;
        final int calculatePacketSize = sslWriteCipher.calculatePacketSize(n3, n);
        if (calculatePacketSize > this.buf.length) {
            final byte[] buf = new byte[calculatePacketSize];
            System.arraycopy(this.buf, 0, buf, 0, this.count);
            this.buf = buf;
        }
        this.count = n + sslWriteCipher.encrypt(b, ByteBuffer.wrap(this.buf, n2, n3));
        final int n4 = this.count - n;
        this.buf[0] = b;
        this.buf[1] = this.protocolVersion.major;
        this.buf[2] = this.protocolVersion.minor;
        this.buf[3] = (byte)(n4 >> 8 & 0xFF);
        this.buf[4] = (byte)(n4 & 0xFF);
        return Authenticator.toLong(sequenceNumber);
    }
    
    static ByteBuffer encodeV2ClientHello(final byte[] array, final int n, final int n2) throws IOException {
        final int n3 = n + 34;
        final int n4 = n3 + 1 + array[n3];
        final int n5 = (((array[n4] & 0xFF) << 8) + (array[n4 + 1] & 0xFF)) / 2;
        final ByteBuffer wrap = ByteBuffer.wrap(new byte[11 + n5 * 6 + 3 + 32]);
        int n6 = n4 + 2;
        int n7 = 0;
        wrap.position(11);
        int n8 = 0;
        for (int i = 0; i < n5; ++i) {
            final byte b = array[n6++];
            final byte b2 = array[n6++];
            n7 += V3toV2CipherSuite(wrap, b, b2);
            if (n8 == 0 && b == 0 && b2 == -1) {
                n8 = 1;
            }
        }
        if (n8 == 0) {
            n7 += V3toV2CipherSuite(wrap, (byte)0, (byte)(-1));
        }
        wrap.put(array, n + 2, 32);
        final int n9 = wrap.position() - 2;
        wrap.position(0);
        wrap.put((byte)(0x80 | (n9 >>> 8 & 0xFF)));
        wrap.put((byte)(n9 & 0xFF));
        wrap.put(SSLHandshake.CLIENT_HELLO.id);
        wrap.put(array[n]);
        wrap.put(array[n + 1]);
        wrap.put((byte)(n7 >>> 8));
        wrap.put((byte)(n7 & 0xFF));
        wrap.put((byte)0);
        wrap.put((byte)0);
        wrap.put((byte)0);
        wrap.put((byte)32);
        wrap.position(0);
        wrap.limit(n9 + 2);
        return wrap;
    }
    
    private static int V3toV2CipherSuite(final ByteBuffer byteBuffer, final byte b, final byte b2) {
        byteBuffer.put((byte)0);
        byteBuffer.put(b);
        byteBuffer.put(b2);
        if ((b2 & 0xFF) > 10 || OutputRecord.V3toV2CipherMap1[b2] == -1) {
            return 3;
        }
        byteBuffer.put((byte)OutputRecord.V3toV2CipherMap1[b2]);
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)OutputRecord.V3toV2CipherMap3[b2]);
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
