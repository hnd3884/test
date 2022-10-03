package org.openjsse.sun.security.ssl;

import javax.net.ssl.SSLHandshakeException;
import java.util.ArrayList;
import java.security.GeneralSecurityException;
import javax.net.ssl.SSLProtocolException;
import javax.crypto.BadPaddingException;
import javax.net.ssl.SSLException;
import java.io.IOException;
import java.nio.ByteBuffer;

final class SSLEngineInputRecord extends InputRecord implements SSLRecord
{
    private boolean formatVerified;
    private ByteBuffer handshakeBuffer;
    
    SSLEngineInputRecord(final HandshakeHash handshakeHash) {
        super(handshakeHash, SSLCipher.SSLReadCipher.nullTlsReadCipher());
        this.formatVerified = false;
        this.handshakeBuffer = null;
    }
    
    @Override
    int estimateFragmentSize(final int packetSize) {
        if (packetSize > 0) {
            return this.readCipher.estimateFragmentSize(packetSize, 5);
        }
        return 16384;
    }
    
    @Override
    int bytesInCompletePacket(final ByteBuffer[] srcs, final int srcsOffset, final int srcsLength) throws IOException {
        return this.bytesInCompletePacket(srcs[srcsOffset]);
    }
    
    private int bytesInCompletePacket(final ByteBuffer packet) throws SSLException {
        if (packet.remaining() < 5) {
            return -1;
        }
        final int pos = packet.position();
        final byte byteZero = packet.get(pos);
        int len = 0;
        if (this.formatVerified || byteZero == ContentType.HANDSHAKE.id || byteZero == ContentType.ALERT.id) {
            final byte majorVersion = packet.get(pos + 1);
            final byte minorVersion = packet.get(pos + 2);
            if (!ProtocolVersion.isNegotiable(majorVersion, minorVersion, false, false)) {
                throw new SSLException("Unrecognized record version " + ProtocolVersion.nameOf(majorVersion, minorVersion) + " , plaintext connection?");
            }
            this.formatVerified = true;
            len = ((packet.get(pos + 3) & 0xFF) << 8) + (packet.get(pos + 4) & 0xFF) + 5;
        }
        else {
            final boolean isShort = (byteZero & 0x80) != 0x0;
            if (!isShort || (packet.get(pos + 2) != 1 && packet.get(pos + 2) != 4)) {
                throw new SSLException("Unrecognized SSL message, plaintext connection?");
            }
            final byte majorVersion2 = packet.get(pos + 3);
            final byte minorVersion2 = packet.get(pos + 4);
            if (!ProtocolVersion.isNegotiable(majorVersion2, minorVersion2, false, false)) {
                throw new SSLException("Unrecognized record version " + ProtocolVersion.nameOf(majorVersion2, minorVersion2) + " , plaintext connection?");
            }
            final int mask = isShort ? 127 : 63;
            len = ((byteZero & mask) << 8) + (packet.get(pos + 1) & 0xFF) + (isShort ? 2 : 3);
        }
        return len;
    }
    
    @Override
    Plaintext[] decode(final ByteBuffer[] srcs, final int srcsOffset, final int srcsLength) throws IOException, BadPaddingException {
        if (srcs == null || srcs.length == 0 || srcsLength == 0) {
            return new Plaintext[0];
        }
        if (srcsLength == 1) {
            return this.decode(srcs[srcsOffset]);
        }
        final ByteBuffer packet = InputRecord.extract(srcs, srcsOffset, srcsLength, 5);
        return this.decode(packet);
    }
    
    private Plaintext[] decode(final ByteBuffer packet) throws IOException, BadPaddingException {
        if (this.isClosed) {
            return null;
        }
        if (SSLLogger.isOn && SSLLogger.isOn("packet")) {
            SSLLogger.fine("Raw read", packet);
        }
        if (!this.formatVerified) {
            this.formatVerified = true;
            final int pos = packet.position();
            final byte byteZero = packet.get(pos);
            if (byteZero != ContentType.HANDSHAKE.id && byteZero != ContentType.ALERT.id) {
                return this.handleUnknownRecord(packet);
            }
        }
        return this.decodeInputRecord(packet);
    }
    
    private Plaintext[] decodeInputRecord(final ByteBuffer packet) throws IOException, BadPaddingException {
        final int srcPos = packet.position();
        final int srcLim = packet.limit();
        byte contentType = packet.get();
        final byte majorVersion = packet.get();
        final byte minorVersion = packet.get();
        final int contentLen = Record.getInt16(packet);
        if (SSLLogger.isOn && SSLLogger.isOn("record")) {
            SSLLogger.fine("READ: " + ProtocolVersion.nameOf(majorVersion, minorVersion) + " " + ContentType.nameOf(contentType) + ", length = " + contentLen, new Object[0]);
        }
        if (contentLen < 0 || contentLen > 33088) {
            throw new SSLProtocolException("Bad input record size, TLSCiphertext.length = " + contentLen);
        }
        final int recLim = srcPos + 5 + contentLen;
        packet.limit(recLim);
        packet.position(srcPos + 5);
        ByteBuffer fragment;
        try {
            final Plaintext plaintext = this.readCipher.decrypt(contentType, packet, null);
            fragment = plaintext.fragment;
            contentType = plaintext.contentType;
        }
        catch (final BadPaddingException bpe) {
            throw bpe;
        }
        catch (final GeneralSecurityException gse) {
            throw (SSLProtocolException)new SSLProtocolException("Unexpected exception").initCause(gse);
        }
        finally {
            packet.limit(srcLim);
            packet.position(recLim);
        }
        if (contentType != ContentType.HANDSHAKE.id && this.handshakeBuffer != null && this.handshakeBuffer.hasRemaining()) {
            throw new SSLProtocolException("Expecting a handshake fragment, but received " + ContentType.nameOf(contentType));
        }
        if (contentType == ContentType.HANDSHAKE.id) {
            ByteBuffer handshakeFrag = fragment;
            if (this.handshakeBuffer != null && this.handshakeBuffer.remaining() != 0) {
                final ByteBuffer bb = ByteBuffer.wrap(new byte[this.handshakeBuffer.remaining() + fragment.remaining()]);
                bb.put(this.handshakeBuffer);
                bb.put(fragment);
                handshakeFrag = (ByteBuffer)bb.rewind();
                this.handshakeBuffer = null;
            }
            final ArrayList<Plaintext> plaintexts = new ArrayList<Plaintext>(5);
            while (handshakeFrag.hasRemaining()) {
                final int remaining = handshakeFrag.remaining();
                if (remaining < 4) {
                    (this.handshakeBuffer = ByteBuffer.wrap(new byte[remaining])).put(handshakeFrag);
                    this.handshakeBuffer.rewind();
                    break;
                }
                handshakeFrag.mark();
                final byte handshakeType = handshakeFrag.get();
                if (!SSLHandshake.isKnown(handshakeType)) {
                    throw new SSLProtocolException("Unknown handshake type size, Handshake.msg_type = " + (handshakeType & 0xFF));
                }
                final int handshakeBodyLen = Record.getInt24(handshakeFrag);
                if (handshakeBodyLen > SSLConfiguration.maxHandshakeMessageSize) {
                    throw new SSLProtocolException("The size of the handshake message (" + handshakeBodyLen + ") exceeds the maximum allowed size (" + SSLConfiguration.maxHandshakeMessageSize + ")");
                }
                handshakeFrag.reset();
                final int handshakeMessageLen = 4 + handshakeBodyLen;
                if (remaining < handshakeMessageLen) {
                    (this.handshakeBuffer = ByteBuffer.wrap(new byte[remaining])).put(handshakeFrag);
                    this.handshakeBuffer.rewind();
                    break;
                }
                if (remaining == handshakeMessageLen) {
                    if (this.handshakeHash.isHashable(handshakeType)) {
                        this.handshakeHash.receive(handshakeFrag);
                    }
                    plaintexts.add(new Plaintext(contentType, majorVersion, minorVersion, -1, -1L, handshakeFrag));
                    break;
                }
                final int fragPos = handshakeFrag.position();
                final int fragLim = handshakeFrag.limit();
                final int nextPos = fragPos + handshakeMessageLen;
                handshakeFrag.limit(nextPos);
                if (this.handshakeHash.isHashable(handshakeType)) {
                    this.handshakeHash.receive(handshakeFrag);
                }
                plaintexts.add(new Plaintext(contentType, majorVersion, minorVersion, -1, -1L, handshakeFrag.slice()));
                handshakeFrag.position(nextPos);
                handshakeFrag.limit(fragLim);
            }
            return plaintexts.toArray(new Plaintext[0]);
        }
        return new Plaintext[] { new Plaintext(contentType, majorVersion, minorVersion, -1, -1L, fragment) };
    }
    
    private Plaintext[] handleUnknownRecord(final ByteBuffer packet) throws IOException, BadPaddingException {
        final int srcPos = packet.position();
        final int srcLim = packet.limit();
        final byte firstByte = packet.get(srcPos);
        final byte thirdByte = packet.get(srcPos + 2);
        if ((firstByte & 0x80) != 0x0 && thirdByte == 1) {
            if (this.helloVersion != ProtocolVersion.SSL20Hello) {
                throw new SSLHandshakeException("SSLv2Hello is not enabled");
            }
            final byte majorVersion = packet.get(srcPos + 3);
            final byte minorVersion = packet.get(srcPos + 4);
            if (majorVersion == ProtocolVersion.SSL20Hello.major && minorVersion == ProtocolVersion.SSL20Hello.minor) {
                if (SSLLogger.isOn && SSLLogger.isOn("record")) {
                    SSLLogger.fine("Requested to negotiate unsupported SSLv2!", new Object[0]);
                }
                throw new UnsupportedOperationException("Unsupported SSL v2.0 ClientHello");
            }
            packet.position(srcPos + 2);
            this.handshakeHash.receive(packet);
            packet.position(srcPos);
            final ByteBuffer converted = InputRecord.convertToClientHello(packet);
            if (SSLLogger.isOn && SSLLogger.isOn("packet")) {
                SSLLogger.fine("[Converted] ClientHello", converted);
            }
            return new Plaintext[] { new Plaintext(ContentType.HANDSHAKE.id, majorVersion, minorVersion, -1, -1L, converted) };
        }
        else {
            if ((firstByte & 0x80) != 0x0 && thirdByte == 4) {
                throw new SSLException("SSL V2.0 servers are not supported.");
            }
            throw new SSLException("Unsupported or unrecognized SSL message");
        }
    }
}
