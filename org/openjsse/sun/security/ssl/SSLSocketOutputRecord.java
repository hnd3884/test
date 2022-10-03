package org.openjsse.sun.security.ssl;

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
    synchronized void encodeAlert(final byte level, final byte description) throws IOException {
        if (this.isClosed()) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.warning("outbound has closed, ignore outbound alert message: " + Alert.nameOf(description), new Object[0]);
            }
            return;
        }
        final int position = 5 + this.writeCipher.getExplicitNonceSize();
        this.count = position;
        this.write(level);
        this.write(description);
        if (SSLLogger.isOn && SSLLogger.isOn("record")) {
            SSLLogger.fine("WRITE: " + this.protocolVersion + " " + ContentType.ALERT.name + "(" + Alert.nameOf(description) + "), length = " + (this.count - 5), new Object[0]);
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
    synchronized void encodeHandshake(final byte[] source, int offset, final int length) throws IOException {
        if (this.isClosed()) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.warning("outbound has closed, ignore outbound handshake message", ByteBuffer.wrap(source, offset, length));
            }
            return;
        }
        if (this.firstMessage) {
            this.firstMessage = false;
            if (this.helloVersion == ProtocolVersion.SSL20Hello && source[offset] == SSLHandshake.CLIENT_HELLO.id && source[offset + 4 + 2 + 32] == 0) {
                final ByteBuffer v2ClientHello = OutputRecord.encodeV2ClientHello(source, offset + 4, length - 4);
                final byte[] record = v2ClientHello.array();
                final int limit = v2ClientHello.limit();
                this.handshakeHash.deliver(record, 2, limit - 2);
                if (SSLLogger.isOn && SSLLogger.isOn("record")) {
                    SSLLogger.fine("WRITE: SSLv2 ClientHello message, length = " + limit, new Object[0]);
                }
                this.deliverStream.write(record, 0, limit);
                this.deliverStream.flush();
                if (SSLLogger.isOn && SSLLogger.isOn("packet")) {
                    SSLLogger.fine("Raw write", new ByteArrayInputStream(record, 0, limit));
                }
                return;
            }
        }
        final byte handshakeType = source[0];
        if (this.handshakeHash.isHashable(handshakeType)) {
            this.handshakeHash.deliver(source, offset, length);
        }
        final int fragLimit = this.getFragLimit();
        final int position = 5 + this.writeCipher.getExplicitNonceSize();
        if (this.count == 0) {
            this.count = position;
        }
        if (this.count - position < fragLimit - length) {
            this.write(source, offset, length);
            return;
        }
        int fragLen;
        for (int limit2 = offset + length; offset < limit2; offset += fragLen, this.count = position) {
            final int remains = limit2 - offset + (this.count - position);
            fragLen = Math.min(fragLimit, remains);
            this.write(source, offset, fragLen);
            if (remains < fragLimit) {
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
        final int position = 5 + this.writeCipher.getExplicitNonceSize();
        this.count = position;
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
        final int position = 5 + this.writeCipher.getExplicitNonceSize();
        if (this.count <= position) {
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
    synchronized void deliver(final byte[] source, int offset, final int length) throws IOException {
        if (this.isClosed()) {
            throw new SocketException("Connection or outbound has been closed");
        }
        if (this.writeCipher.authenticator.seqNumOverflow()) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.fine("sequence number extremely close to overflow (2^64-1 packets). Closing connection.", new Object[0]);
            }
            throw new SSLHandshakeException("sequence number overflow");
        }
        boolean isFirstRecordOfThePayload = true;
        int fragLen;
        for (int limit = offset + length; offset < limit; offset += fragLen) {
            if (this.packetSize > 0) {
                fragLen = Math.min(16709, this.packetSize);
                fragLen = this.writeCipher.calculateFragmentSize(fragLen, 5);
                fragLen = Math.min(fragLen, 16384);
            }
            else {
                fragLen = 16384;
            }
            fragLen = this.calculateFragmentSize(fragLen);
            if (isFirstRecordOfThePayload && this.needToSplitPayload()) {
                fragLen = 1;
                isFirstRecordOfThePayload = false;
            }
            else {
                fragLen = Math.min(fragLen, limit - offset);
            }
            final int position = 5 + this.writeCipher.getExplicitNonceSize();
            this.count = position;
            this.write(source, offset, fragLen);
            if (SSLLogger.isOn && SSLLogger.isOn("record")) {
                SSLLogger.fine("WRITE: " + this.protocolVersion + " " + ContentType.APPLICATION_DATA.name + ", length = " + (this.count - position), new Object[0]);
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
    synchronized void setDeliverStream(final OutputStream outputStream) {
        this.deliverStream = outputStream;
    }
    
    private boolean needToSplitPayload() {
        return !this.protocolVersion.useTLS11PlusSpec() && this.writeCipher.isCBCMode() && !this.isFirstAppOutputRecord && Record.enableCBCProtection;
    }
    
    private int getFragLimit() {
        int fragLimit;
        if (this.packetSize > 0) {
            fragLimit = Math.min(16709, this.packetSize);
            fragLimit = this.writeCipher.calculateFragmentSize(fragLimit, 5);
            fragLimit = Math.min(fragLimit, 16384);
        }
        else {
            fragLimit = 16384;
        }
        fragLimit = this.calculateFragmentSize(fragLimit);
        return fragLimit;
    }
}
