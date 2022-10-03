package sun.security.ssl;

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
    int estimateFragmentSize(final int n) {
        if (n > 0) {
            return this.readCipher.estimateFragmentSize(n, 5);
        }
        return 16384;
    }
    
    @Override
    int bytesInCompletePacket(final ByteBuffer[] array, final int n, final int n2) throws IOException {
        return this.bytesInCompletePacket(array[n]);
    }
    
    private int bytesInCompletePacket(final ByteBuffer byteBuffer) throws SSLException {
        if (byteBuffer.remaining() < 5) {
            return -1;
        }
        final int position = byteBuffer.position();
        final byte value = byteBuffer.get(position);
        int n;
        if (this.formatVerified || value == ContentType.HANDSHAKE.id || value == ContentType.ALERT.id) {
            final byte value2 = byteBuffer.get(position + 1);
            final byte value3 = byteBuffer.get(position + 2);
            if (!ProtocolVersion.isNegotiable(value2, value3, false)) {
                throw new SSLException("Unrecognized record version " + ProtocolVersion.nameOf(value2, value3) + " , plaintext connection?");
            }
            this.formatVerified = true;
            n = ((byteBuffer.get(position + 3) & 0xFF) << 8) + (byteBuffer.get(position + 4) & 0xFF) + 5;
        }
        else {
            final boolean b = (value & 0x80) != 0x0;
            if (!b || (byteBuffer.get(position + 2) != 1 && byteBuffer.get(position + 2) != 4)) {
                throw new SSLException("Unrecognized SSL message, plaintext connection?");
            }
            final byte value4 = byteBuffer.get(position + 3);
            final byte value5 = byteBuffer.get(position + 4);
            if (!ProtocolVersion.isNegotiable(value4, value5, false)) {
                throw new SSLException("Unrecognized record version " + ProtocolVersion.nameOf(value4, value5) + " , plaintext connection?");
            }
            n = ((value & (b ? 127 : 63)) << 8) + (byteBuffer.get(position + 1) & 0xFF) + (b ? 2 : 3);
        }
        return n;
    }
    
    @Override
    Plaintext[] decode(final ByteBuffer[] array, final int n, final int n2) throws IOException, BadPaddingException {
        if (array == null || array.length == 0 || n2 == 0) {
            return new Plaintext[0];
        }
        if (n2 == 1) {
            return this.decode(array[n]);
        }
        return this.decode(InputRecord.extract(array, n, n2, 5));
    }
    
    private Plaintext[] decode(final ByteBuffer byteBuffer) throws IOException, BadPaddingException {
        if (this.isClosed) {
            return null;
        }
        if (SSLLogger.isOn && SSLLogger.isOn("packet")) {
            SSLLogger.fine("Raw read", byteBuffer);
        }
        if (!this.formatVerified) {
            this.formatVerified = true;
            final byte value = byteBuffer.get(byteBuffer.position());
            if (value != ContentType.HANDSHAKE.id && value != ContentType.ALERT.id) {
                return this.handleUnknownRecord(byteBuffer);
            }
        }
        return this.decodeInputRecord(byteBuffer);
    }
    
    private Plaintext[] decodeInputRecord(final ByteBuffer byteBuffer) throws IOException, BadPaddingException {
        final int position = byteBuffer.position();
        final int limit = byteBuffer.limit();
        byte b = byteBuffer.get();
        final byte value = byteBuffer.get();
        final byte value2 = byteBuffer.get();
        final int int16 = Record.getInt16(byteBuffer);
        if (SSLLogger.isOn && SSLLogger.isOn("record")) {
            SSLLogger.fine("READ: " + ProtocolVersion.nameOf(value, value2) + " " + ContentType.nameOf(b) + ", length = " + int16, new Object[0]);
        }
        if (int16 < 0 || int16 > 33088) {
            throw new SSLProtocolException("Bad input record size, TLSCiphertext.length = " + int16);
        }
        final int n = position + 5 + int16;
        byteBuffer.limit(n);
        byteBuffer.position(position + 5);
        ByteBuffer fragment;
        try {
            final Plaintext decrypt = this.readCipher.decrypt(b, byteBuffer, null);
            fragment = decrypt.fragment;
            b = decrypt.contentType;
        }
        catch (final BadPaddingException ex) {
            throw ex;
        }
        catch (final GeneralSecurityException ex2) {
            throw (SSLProtocolException)new SSLProtocolException("Unexpected exception").initCause(ex2);
        }
        finally {
            byteBuffer.limit(limit);
            byteBuffer.position(n);
        }
        if (b != ContentType.HANDSHAKE.id && this.handshakeBuffer != null && this.handshakeBuffer.hasRemaining()) {
            throw new SSLProtocolException("Expecting a handshake fragment, but received " + ContentType.nameOf(b));
        }
        if (b == ContentType.HANDSHAKE.id) {
            ByteBuffer byteBuffer2 = fragment;
            if (this.handshakeBuffer != null && this.handshakeBuffer.remaining() != 0) {
                final ByteBuffer wrap = ByteBuffer.wrap(new byte[this.handshakeBuffer.remaining() + fragment.remaining()]);
                wrap.put(this.handshakeBuffer);
                wrap.put(fragment);
                byteBuffer2 = (ByteBuffer)wrap.rewind();
                this.handshakeBuffer = null;
            }
            final ArrayList<Plaintext> list = new ArrayList<Plaintext>(5);
            while (byteBuffer2.hasRemaining()) {
                final int remaining = byteBuffer2.remaining();
                if (remaining < 4) {
                    (this.handshakeBuffer = ByteBuffer.wrap(new byte[remaining])).put(byteBuffer2);
                    this.handshakeBuffer.rewind();
                    break;
                }
                byteBuffer2.mark();
                final byte value3 = byteBuffer2.get();
                if (!SSLHandshake.isKnown(value3)) {
                    throw new SSLProtocolException("Unknown handshake type size, Handshake.msg_type = " + (value3 & 0xFF));
                }
                final int int17 = Record.getInt24(byteBuffer2);
                if (int17 > SSLConfiguration.maxHandshakeMessageSize) {
                    throw new SSLProtocolException("The size of the handshake message (" + int17 + ") exceeds the maximum allowed size (" + SSLConfiguration.maxHandshakeMessageSize + ")");
                }
                byteBuffer2.reset();
                final int n2 = 4 + int17;
                if (remaining < n2) {
                    (this.handshakeBuffer = ByteBuffer.wrap(new byte[remaining])).put(byteBuffer2);
                    this.handshakeBuffer.rewind();
                    break;
                }
                if (remaining == n2) {
                    if (this.handshakeHash.isHashable(value3)) {
                        this.handshakeHash.receive(byteBuffer2);
                    }
                    list.add(new Plaintext(b, value, value2, -1, -1L, byteBuffer2));
                    break;
                }
                final int position2 = byteBuffer2.position();
                final int limit2 = byteBuffer2.limit();
                final int n3 = position2 + n2;
                byteBuffer2.limit(n3);
                if (this.handshakeHash.isHashable(value3)) {
                    this.handshakeHash.receive(byteBuffer2);
                }
                list.add(new Plaintext(b, value, value2, -1, -1L, byteBuffer2.slice()));
                byteBuffer2.position(n3);
                byteBuffer2.limit(limit2);
            }
            return list.toArray(new Plaintext[0]);
        }
        return new Plaintext[] { new Plaintext(b, value, value2, -1, -1L, fragment) };
    }
    
    private Plaintext[] handleUnknownRecord(final ByteBuffer byteBuffer) throws IOException, BadPaddingException {
        final int position = byteBuffer.position();
        byteBuffer.limit();
        final byte value = byteBuffer.get(position);
        final byte value2 = byteBuffer.get(position + 2);
        if ((value & 0x80) != 0x0 && value2 == 1) {
            if (this.helloVersion != ProtocolVersion.SSL20Hello) {
                throw new SSLHandshakeException("SSLv2Hello is not enabled");
            }
            final byte value3 = byteBuffer.get(position + 3);
            final byte value4 = byteBuffer.get(position + 4);
            if (value3 == ProtocolVersion.SSL20Hello.major && value4 == ProtocolVersion.SSL20Hello.minor) {
                if (SSLLogger.isOn && SSLLogger.isOn("record")) {
                    SSLLogger.fine("Requested to negotiate unsupported SSLv2!", new Object[0]);
                }
                throw new UnsupportedOperationException("Unsupported SSL v2.0 ClientHello");
            }
            byteBuffer.position(position + 2);
            this.handshakeHash.receive(byteBuffer);
            byteBuffer.position(position);
            final ByteBuffer convertToClientHello = InputRecord.convertToClientHello(byteBuffer);
            if (SSLLogger.isOn && SSLLogger.isOn("packet")) {
                SSLLogger.fine("[Converted] ClientHello", convertToClientHello);
            }
            return new Plaintext[] { new Plaintext(ContentType.HANDSHAKE.id, value3, value4, -1, -1L, convertToClientHello) };
        }
        else {
            if ((value & 0x80) != 0x0 && value2 == 4) {
                throw new SSLException("SSL V2.0 servers are not supported.");
            }
            throw new SSLException("Unsupported or unrecognized SSL message");
        }
    }
}
