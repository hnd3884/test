package sun.security.ssl;

import javax.net.ssl.SSLHandshakeException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;

final class SSLSocketOutputRecord extends OutputRecord implements SSLRecord
{
    private OutputStream deliverStream;
    
    SSLSocketOutputRecord(final HandshakeHash handshakeHash) {
        this(handshakeHash, (TransportContext)null);
    }
    
    SSLSocketOutputRecord(final HandshakeHash handshakeHash, final TransportContext tc) {
        super(handshakeHash, SSLCipher.SSLWriteCipher.nullTlsWriteCipher());
        this.deliverStream = null;
        this.tc = tc;
        this.packetSize = 16709;
        this.protocolVersion = ProtocolVersion.NONE;
    }
    
    @Override
    synchronized void encodeAlert(final byte b, final byte b2) throws IOException {
        if (this.isClosed()) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.warning("outbound has closed, ignore outbound alert message: " + Alert.nameOf(b2), new Object[0]);
            }
            return;
        }
        this.count = 5 + this.writeCipher.getExplicitNonceSize();
        this.write(b);
        this.write(b2);
        if (SSLLogger.isOn && SSLLogger.isOn("record")) {
            SSLLogger.fine("WRITE: " + this.protocolVersion + " " + ContentType.ALERT.name + "(" + Alert.nameOf(b2) + "), length = " + (this.count - 5), new Object[0]);
        }
        this.encrypt(this.writeCipher, ContentType.ALERT.id, 5);
        this.deliverStream.write(this.buf, 0, this.count);
        this.deliverStream.flush();
        if (SSLLogger.isOn && SSLLogger.isOn("packet")) {
            SSLLogger.fine("Raw write", new ByteArrayInputStream(this.buf, 0, this.count));
        }
        this.count = 0;
    }
    
    @Override
    synchronized void encodeHandshake(final byte[] array, int i, final int n) throws IOException {
        if (this.isClosed()) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.warning("outbound has closed, ignore outbound handshake message", ByteBuffer.wrap(array, i, n));
            }
            return;
        }
        if (this.firstMessage) {
            this.firstMessage = false;
            if (this.helloVersion == ProtocolVersion.SSL20Hello && array[i] == SSLHandshake.CLIENT_HELLO.id && array[i + 4 + 2 + 32] == 0) {
                final ByteBuffer encodeV2ClientHello = OutputRecord.encodeV2ClientHello(array, i + 4, n - 4);
                final byte[] array2 = encodeV2ClientHello.array();
                final int limit = encodeV2ClientHello.limit();
                this.handshakeHash.deliver(array2, 2, limit - 2);
                if (SSLLogger.isOn && SSLLogger.isOn("record")) {
                    SSLLogger.fine("WRITE: SSLv2 ClientHello message, length = " + limit, new Object[0]);
                }
                this.deliverStream.write(array2, 0, limit);
                this.deliverStream.flush();
                if (SSLLogger.isOn && SSLLogger.isOn("packet")) {
                    SSLLogger.fine("Raw write", new ByteArrayInputStream(array2, 0, limit));
                }
                return;
            }
        }
        if (this.handshakeHash.isHashable(array[0])) {
            this.handshakeHash.deliver(array, i, n);
        }
        final int fragLimit = this.getFragLimit();
        final int n2 = 5 + this.writeCipher.getExplicitNonceSize();
        if (this.count == 0) {
            this.count = n2;
        }
        if (this.count - n2 < fragLimit - n) {
            this.write(array, i, n);
            return;
        }
        int min;
        for (int n3 = i + n; i < n3; i += min, this.count = n2) {
            final int n4 = n3 - i + (this.count - n2);
            min = Math.min(fragLimit, n4);
            this.write(array, i, min);
            if (n4 < fragLimit) {
                return;
            }
            if (SSLLogger.isOn && SSLLogger.isOn("record")) {
                SSLLogger.fine("WRITE: " + this.protocolVersion + " " + ContentType.HANDSHAKE.name + ", length = " + (this.count - 5), new Object[0]);
            }
            this.encrypt(this.writeCipher, ContentType.HANDSHAKE.id, 5);
            this.deliverStream.write(this.buf, 0, this.count);
            this.deliverStream.flush();
            if (SSLLogger.isOn && SSLLogger.isOn("packet")) {
                SSLLogger.fine("Raw write", new ByteArrayInputStream(this.buf, 0, this.count));
            }
        }
    }
    
    @Override
    synchronized void encodeChangeCipherSpec() throws IOException {
        if (this.isClosed()) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.warning("outbound has closed, ignore outbound change_cipher_spec message", new Object[0]);
            }
            return;
        }
        this.count = 5 + this.writeCipher.getExplicitNonceSize();
        this.write(1);
        this.encrypt(this.writeCipher, ContentType.CHANGE_CIPHER_SPEC.id, 5);
        this.deliverStream.write(this.buf, 0, this.count);
        if (SSLLogger.isOn && SSLLogger.isOn("packet")) {
            SSLLogger.fine("Raw write", new ByteArrayInputStream(this.buf, 0, this.count));
        }
        this.count = 0;
    }
    
    @Override
    public synchronized void flush() throws IOException {
        if (this.count <= 5 + this.writeCipher.getExplicitNonceSize()) {
            return;
        }
        if (SSLLogger.isOn && SSLLogger.isOn("record")) {
            SSLLogger.fine("WRITE: " + this.protocolVersion + " " + ContentType.HANDSHAKE.name + ", length = " + (this.count - 5), new Object[0]);
        }
        this.encrypt(this.writeCipher, ContentType.HANDSHAKE.id, 5);
        this.deliverStream.write(this.buf, 0, this.count);
        this.deliverStream.flush();
        if (SSLLogger.isOn && SSLLogger.isOn("packet")) {
            SSLLogger.fine("Raw write", new ByteArrayInputStream(this.buf, 0, this.count));
        }
        this.count = 0;
    }
    
    @Override
    synchronized void deliver(final byte[] array, int i, final int n) throws IOException {
        if (this.isClosed()) {
            throw new SocketException("Connection or outbound has been closed");
        }
        if (this.writeCipher.authenticator.seqNumOverflow()) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.fine("sequence number extremely close to overflow (2^64-1 packets). Closing connection.", new Object[0]);
            }
            throw new SSLHandshakeException("sequence number overflow");
        }
        int n2 = 1;
        int min2;
        for (int n3 = i + n; i < n3; i += min2) {
            int min;
            if (this.packetSize > 0) {
                min = Math.min(this.writeCipher.calculateFragmentSize(Math.min(16709, this.packetSize), 5), 16384);
            }
            else {
                min = 16384;
            }
            final int calculateFragmentSize = this.calculateFragmentSize(min);
            if (n2 != 0 && this.needToSplitPayload()) {
                min2 = 1;
                n2 = 0;
            }
            else {
                min2 = Math.min(calculateFragmentSize, n3 - i);
            }
            final int count = 5 + this.writeCipher.getExplicitNonceSize();
            this.count = count;
            this.write(array, i, min2);
            if (SSLLogger.isOn && SSLLogger.isOn("record")) {
                SSLLogger.fine("WRITE: " + this.protocolVersion + " " + ContentType.APPLICATION_DATA.name + ", length = " + (this.count - count), new Object[0]);
            }
            this.encrypt(this.writeCipher, ContentType.APPLICATION_DATA.id, 5);
            this.deliverStream.write(this.buf, 0, this.count);
            this.deliverStream.flush();
            if (SSLLogger.isOn && SSLLogger.isOn("packet")) {
                SSLLogger.fine("Raw write", new ByteArrayInputStream(this.buf, 0, this.count));
            }
            this.count = 0;
            if (this.isFirstAppOutputRecord) {
                this.isFirstAppOutputRecord = false;
            }
        }
    }
    
    @Override
    synchronized void setDeliverStream(final OutputStream deliverStream) {
        this.deliverStream = deliverStream;
    }
    
    private boolean needToSplitPayload() {
        return !this.protocolVersion.useTLS11PlusSpec() && this.writeCipher.isCBCMode() && !this.isFirstAppOutputRecord && Record.enableCBCProtection;
    }
    
    private int getFragLimit() {
        int min;
        if (this.packetSize > 0) {
            min = Math.min(this.writeCipher.calculateFragmentSize(Math.min(16709, this.packetSize), 5), 16384);
        }
        else {
            min = 16384;
        }
        return this.calculateFragmentSize(min);
    }
}
