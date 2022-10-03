package sun.security.ssl;

import javax.net.ssl.SSLHandshakeException;
import java.util.ArrayList;
import java.security.GeneralSecurityException;
import javax.net.ssl.SSLProtocolException;
import javax.crypto.BadPaddingException;
import java.io.InterruptedIOException;
import java.io.IOException;
import javax.net.ssl.SSLException;
import java.io.EOFException;
import java.nio.ByteBuffer;
import java.io.OutputStream;
import java.io.InputStream;

final class SSLSocketInputRecord extends InputRecord implements SSLRecord
{
    private InputStream is;
    private OutputStream os;
    private final byte[] header;
    private int headerOff;
    private ByteBuffer recordBody;
    private boolean formatVerified;
    private ByteBuffer handshakeBuffer;
    
    SSLSocketInputRecord(final HandshakeHash handshakeHash) {
        super(handshakeHash, SSLCipher.SSLReadCipher.nullTlsReadCipher());
        this.is = null;
        this.os = null;
        this.header = new byte[5];
        this.headerOff = 0;
        this.recordBody = ByteBuffer.allocate(1024);
        this.formatVerified = false;
        this.handshakeBuffer = null;
    }
    
    @Override
    int bytesInCompletePacket() throws IOException {
        try {
            this.readHeader();
        }
        catch (final EOFException ex) {
            return -1;
        }
        final byte b = this.header[0];
        int n;
        if (this.formatVerified || b == ContentType.HANDSHAKE.id || b == ContentType.ALERT.id) {
            if (!ProtocolVersion.isNegotiable(this.header[1], this.header[2], false)) {
                throw new SSLException("Unrecognized record version " + ProtocolVersion.nameOf(this.header[1], this.header[2]) + " , plaintext connection?");
            }
            this.formatVerified = true;
            n = ((this.header[3] & 0xFF) << 8) + (this.header[4] & 0xFF) + 5;
        }
        else {
            if ((b & 0x80) == 0x0 || (this.header[2] != 1 && this.header[2] != 4)) {
                throw new SSLException("Unrecognized SSL message, plaintext connection?");
            }
            if (!ProtocolVersion.isNegotiable(this.header[3], this.header[4], false)) {
                throw new SSLException("Unrecognized record version " + ProtocolVersion.nameOf(this.header[3], this.header[4]) + " , plaintext connection?");
            }
            n = ((b & 0x7F) << 8) + (this.header[1] & 0xFF) + 2;
        }
        return n;
    }
    
    @Override
    Plaintext[] decode(final ByteBuffer[] array, final int n, final int n2) throws IOException, BadPaddingException {
        if (this.isClosed) {
            return null;
        }
        this.readHeader();
        Plaintext[] array2 = null;
        boolean b = true;
        try {
            if (!this.formatVerified) {
                this.formatVerified = true;
                if (this.header[0] != ContentType.HANDSHAKE.id && this.header[0] != ContentType.ALERT.id) {
                    array2 = this.handleUnknownRecord();
                }
            }
            if (array2 == null) {
                array2 = this.decodeInputRecord();
            }
        }
        catch (final InterruptedIOException ex) {
            b = false;
            throw ex;
        }
        finally {
            if (b) {
                this.headerOff = 0;
                this.recordBody.clear();
            }
        }
        return array2;
    }
    
    @Override
    void setReceiverStream(final InputStream is) {
        this.is = is;
    }
    
    @Override
    void setDeliverStream(final OutputStream os) {
        this.os = os;
    }
    
    private Plaintext[] decodeInputRecord() throws IOException, BadPaddingException {
        final byte b = this.header[0];
        final byte b2 = this.header[1];
        final byte b3 = this.header[2];
        int remaining = ((this.header[3] & 0xFF) << 8) + (this.header[4] & 0xFF);
        if (SSLLogger.isOn && SSLLogger.isOn("record")) {
            SSLLogger.fine("READ: " + ProtocolVersion.nameOf(b2, b3) + " " + ContentType.nameOf(b) + ", length = " + remaining, new Object[0]);
        }
        if (remaining < 0 || remaining > 33088) {
            throw new SSLProtocolException("Bad input record size, TLSCiphertext.length = " + remaining);
        }
        if (this.recordBody.position() == 0) {
            if (this.recordBody.capacity() < remaining) {
                this.recordBody = ByteBuffer.allocate(remaining);
            }
            this.recordBody.limit(remaining);
        }
        else {
            remaining = this.recordBody.remaining();
        }
        this.readFully(remaining);
        this.recordBody.flip();
        if (SSLLogger.isOn && SSLLogger.isOn("record")) {
            SSLLogger.fine("READ: " + ProtocolVersion.nameOf(b2, b3) + " " + ContentType.nameOf(b) + ", length = " + this.recordBody.remaining(), new Object[0]);
        }
        ByteBuffer fragment;
        byte contentType;
        try {
            final Plaintext decrypt = this.readCipher.decrypt(b, this.recordBody, null);
            fragment = decrypt.fragment;
            contentType = decrypt.contentType;
        }
        catch (final BadPaddingException ex) {
            throw ex;
        }
        catch (final GeneralSecurityException ex2) {
            throw (SSLProtocolException)new SSLProtocolException("Unexpected exception").initCause(ex2);
        }
        if (contentType != ContentType.HANDSHAKE.id && this.handshakeBuffer != null && this.handshakeBuffer.hasRemaining()) {
            throw new SSLProtocolException("Expecting a handshake fragment, but received " + ContentType.nameOf(contentType));
        }
        if (contentType == ContentType.HANDSHAKE.id) {
            ByteBuffer byteBuffer = fragment;
            if (this.handshakeBuffer != null && this.handshakeBuffer.remaining() != 0) {
                final ByteBuffer wrap = ByteBuffer.wrap(new byte[this.handshakeBuffer.remaining() + fragment.remaining()]);
                wrap.put(this.handshakeBuffer);
                wrap.put(fragment);
                byteBuffer = (ByteBuffer)wrap.rewind();
                this.handshakeBuffer = null;
            }
            final ArrayList<Plaintext> list = new ArrayList<Plaintext>(5);
            while (byteBuffer.hasRemaining()) {
                final int remaining2 = byteBuffer.remaining();
                if (remaining2 < 4) {
                    (this.handshakeBuffer = ByteBuffer.wrap(new byte[remaining2])).put(byteBuffer);
                    this.handshakeBuffer.rewind();
                    break;
                }
                byteBuffer.mark();
                final byte value = byteBuffer.get();
                if (!SSLHandshake.isKnown(value)) {
                    throw new SSLProtocolException("Unknown handshake type size, Handshake.msg_type = " + (value & 0xFF));
                }
                final int int24 = Record.getInt24(byteBuffer);
                if (int24 > SSLConfiguration.maxHandshakeMessageSize) {
                    throw new SSLProtocolException("The size of the handshake message (" + int24 + ") exceeds the maximum allowed size (" + SSLConfiguration.maxHandshakeMessageSize + ")");
                }
                byteBuffer.reset();
                final int n = 4 + int24;
                if (remaining2 < n) {
                    (this.handshakeBuffer = ByteBuffer.wrap(new byte[remaining2])).put(byteBuffer);
                    this.handshakeBuffer.rewind();
                    break;
                }
                if (remaining2 == n) {
                    if (this.handshakeHash.isHashable(value)) {
                        this.handshakeHash.receive(byteBuffer);
                    }
                    list.add(new Plaintext(contentType, b2, b3, -1, -1L, byteBuffer));
                    break;
                }
                final int position = byteBuffer.position();
                final int limit = byteBuffer.limit();
                final int n2 = position + n;
                byteBuffer.limit(n2);
                if (this.handshakeHash.isHashable(value)) {
                    this.handshakeHash.receive(byteBuffer);
                }
                list.add(new Plaintext(contentType, b2, b3, -1, -1L, byteBuffer.slice()));
                byteBuffer.position(n2);
                byteBuffer.limit(limit);
            }
            return list.toArray(new Plaintext[0]);
        }
        return new Plaintext[] { new Plaintext(contentType, b2, b3, -1, -1L, fragment) };
    }
    
    private Plaintext[] handleUnknownRecord() throws IOException, BadPaddingException {
        final byte b = this.header[0];
        final byte b2 = this.header[2];
        if ((b & 0x80) != 0x0 && b2 == 1) {
            if (this.helloVersion != ProtocolVersion.SSL20Hello) {
                throw new SSLHandshakeException("SSLv2Hello is not enabled");
            }
            final byte b3 = this.header[3];
            final byte b4 = this.header[4];
            if (b3 == ProtocolVersion.SSL20Hello.major && b4 == ProtocolVersion.SSL20Hello.minor) {
                this.os.write(SSLRecord.v2NoCipher);
                if (SSLLogger.isOn) {
                    if (SSLLogger.isOn("record")) {
                        SSLLogger.fine("Requested to negotiate unsupported SSLv2!", new Object[0]);
                    }
                    if (SSLLogger.isOn("packet")) {
                        SSLLogger.fine("Raw write", SSLRecord.v2NoCipher);
                    }
                }
                throw new SSLException("Unsupported SSL v2.0 ClientHello");
            }
            int remaining = (this.header[0] & 0x7F) << 8 | (this.header[1] & 0xFF);
            if (this.recordBody.position() == 0) {
                if (this.recordBody.capacity() < 5 + remaining) {
                    this.recordBody = ByteBuffer.allocate(5 + remaining);
                }
                this.recordBody.limit(5 + remaining);
                this.recordBody.put(this.header, 0, 5);
            }
            else {
                remaining = this.recordBody.remaining();
            }
            remaining -= 3;
            this.readFully(remaining);
            this.recordBody.flip();
            this.recordBody.position(2);
            this.handshakeHash.receive(this.recordBody);
            this.recordBody.position(0);
            final ByteBuffer convertToClientHello = InputRecord.convertToClientHello(this.recordBody);
            if (SSLLogger.isOn && SSLLogger.isOn("packet")) {
                SSLLogger.fine("[Converted] ClientHello", convertToClientHello);
            }
            return new Plaintext[] { new Plaintext(ContentType.HANDSHAKE.id, b3, b4, -1, -1L, convertToClientHello) };
        }
        else {
            if ((b & 0x80) != 0x0 && b2 == 4) {
                throw new SSLException("SSL V2.0 servers are not supported.");
            }
            throw new SSLException("Unsupported or unrecognized SSL message");
        }
    }
    
    private int readFully(final int n) throws IOException {
        final int n2 = n + this.recordBody.position();
        int i = this.recordBody.position();
        try {
            while (i < n2) {
                i += read(this.is, this.recordBody.array(), i, n2 - i);
            }
        }
        finally {
            this.recordBody.position(i);
        }
        return n;
    }
    
    private int readHeader() throws IOException {
        while (this.headerOff < 5) {
            this.headerOff += read(this.is, this.header, this.headerOff, 5 - this.headerOff);
        }
        return 5;
    }
    
    private static int read(final InputStream inputStream, final byte[] array, final int n, final int n2) throws IOException {
        final int read = inputStream.read(array, n, n2);
        if (read < 0) {
            if (SSLLogger.isOn && SSLLogger.isOn("packet")) {
                SSLLogger.fine("Raw read: EOF", new Object[0]);
            }
            throw new EOFException("SSL peer shut down incorrectly");
        }
        if (SSLLogger.isOn && SSLLogger.isOn("packet")) {
            SSLLogger.fine("Raw read", ByteBuffer.wrap(array, n, read));
        }
        return read;
    }
    
    void deplete(final boolean b) throws IOException {
        final int available = this.is.available();
        if (b && available == 0) {
            this.is.read();
        }
        int available2;
        while ((available2 = this.is.available()) != 0) {
            this.is.skip(available2);
        }
    }
}
