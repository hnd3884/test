package org.openjsse.sun.security.ssl;

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
        catch (final EOFException eofe) {
            return -1;
        }
        final byte byteZero = this.header[0];
        int len = 0;
        if (this.formatVerified || byteZero == ContentType.HANDSHAKE.id || byteZero == ContentType.ALERT.id) {
            if (!ProtocolVersion.isNegotiable(this.header[1], this.header[2], false, false)) {
                throw new SSLException("Unrecognized record version " + ProtocolVersion.nameOf(this.header[1], this.header[2]) + " , plaintext connection?");
            }
            this.formatVerified = true;
            len = ((this.header[3] & 0xFF) << 8) + (this.header[4] & 0xFF) + 5;
        }
        else {
            final boolean isShort = (byteZero & 0x80) != 0x0;
            if (!isShort || (this.header[2] != 1 && this.header[2] != 4)) {
                throw new SSLException("Unrecognized SSL message, plaintext connection?");
            }
            if (!ProtocolVersion.isNegotiable(this.header[3], this.header[4], false, false)) {
                throw new SSLException("Unrecognized record version " + ProtocolVersion.nameOf(this.header[3], this.header[4]) + " , plaintext connection?");
            }
            len = ((byteZero & 0x7F) << 8) + (this.header[1] & 0xFF) + 2;
        }
        return len;
    }
    
    @Override
    Plaintext[] decode(final ByteBuffer[] srcs, final int srcsOffset, final int srcsLength) throws IOException, BadPaddingException {
        if (this.isClosed) {
            return null;
        }
        this.readHeader();
        Plaintext[] plaintext = null;
        boolean cleanInBuffer = true;
        try {
            if (!this.formatVerified) {
                this.formatVerified = true;
                if (this.header[0] != ContentType.HANDSHAKE.id && this.header[0] != ContentType.ALERT.id) {
                    plaintext = this.handleUnknownRecord();
                }
            }
            if (plaintext == null) {
                plaintext = this.decodeInputRecord();
            }
        }
        catch (final InterruptedIOException e) {
            cleanInBuffer = false;
            throw e;
        }
        finally {
            if (cleanInBuffer) {
                this.headerOff = 0;
                this.recordBody.clear();
            }
        }
        return plaintext;
    }
    
    @Override
    void setReceiverStream(final InputStream inputStream) {
        this.is = inputStream;
    }
    
    @Override
    void setDeliverStream(final OutputStream outputStream) {
        this.os = outputStream;
    }
    
    private Plaintext[] decodeInputRecord() throws IOException, BadPaddingException {
        byte contentType = this.header[0];
        final byte majorVersion = this.header[1];
        final byte minorVersion = this.header[2];
        int contentLen = ((this.header[3] & 0xFF) << 8) + (this.header[4] & 0xFF);
        if (SSLLogger.isOn && SSLLogger.isOn("record")) {
            SSLLogger.fine("READ: " + ProtocolVersion.nameOf(majorVersion, minorVersion) + " " + ContentType.nameOf(contentType) + ", length = " + contentLen, new Object[0]);
        }
        if (contentLen < 0 || contentLen > 33088) {
            throw new SSLProtocolException("Bad input record size, TLSCiphertext.length = " + contentLen);
        }
        if (this.recordBody.position() == 0) {
            if (this.recordBody.capacity() < contentLen) {
                this.recordBody = ByteBuffer.allocate(contentLen);
            }
            this.recordBody.limit(contentLen);
        }
        else {
            contentLen = this.recordBody.remaining();
        }
        this.readFully(contentLen);
        this.recordBody.flip();
        if (SSLLogger.isOn && SSLLogger.isOn("record")) {
            SSLLogger.fine("READ: " + ProtocolVersion.nameOf(majorVersion, minorVersion) + " " + ContentType.nameOf(contentType) + ", length = " + this.recordBody.remaining(), new Object[0]);
        }
        ByteBuffer fragment;
        try {
            final Plaintext plaintext = this.readCipher.decrypt(contentType, this.recordBody, null);
            fragment = plaintext.fragment;
            contentType = plaintext.contentType;
        }
        catch (final BadPaddingException bpe) {
            throw bpe;
        }
        catch (final GeneralSecurityException gse) {
            throw (SSLProtocolException)new SSLProtocolException("Unexpected exception").initCause(gse);
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
    
    private Plaintext[] handleUnknownRecord() throws IOException, BadPaddingException {
        final byte firstByte = this.header[0];
        final byte thirdByte = this.header[2];
        if ((firstByte & 0x80) != 0x0 && thirdByte == 1) {
            if (this.helloVersion != ProtocolVersion.SSL20Hello) {
                throw new SSLHandshakeException("SSLv2Hello is not enabled");
            }
            final byte majorVersion = this.header[3];
            final byte minorVersion = this.header[4];
            if (majorVersion == ProtocolVersion.SSL20Hello.major && minorVersion == ProtocolVersion.SSL20Hello.minor) {
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
            int msgLen = (this.header[0] & 0x7F) << 8 | (this.header[1] & 0xFF);
            if (this.recordBody.position() == 0) {
                if (this.recordBody.capacity() < 5 + msgLen) {
                    this.recordBody = ByteBuffer.allocate(5 + msgLen);
                }
                this.recordBody.limit(5 + msgLen);
                this.recordBody.put(this.header, 0, 5);
            }
            else {
                msgLen = this.recordBody.remaining();
            }
            msgLen -= 3;
            this.readFully(msgLen);
            this.recordBody.flip();
            this.recordBody.position(2);
            this.handshakeHash.receive(this.recordBody);
            this.recordBody.position(0);
            final ByteBuffer converted = InputRecord.convertToClientHello(this.recordBody);
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
    
    private int readFully(final int len) throws IOException {
        final int end = len + this.recordBody.position();
        int off = this.recordBody.position();
        try {
            while (off < end) {
                off += read(this.is, this.recordBody.array(), off, end - off);
            }
        }
        finally {
            this.recordBody.position(off);
        }
        return len;
    }
    
    private int readHeader() throws IOException {
        while (this.headerOff < 5) {
            this.headerOff += read(this.is, this.header, this.headerOff, 5 - this.headerOff);
        }
        return 5;
    }
    
    private static int read(final InputStream is, final byte[] buf, final int off, final int len) throws IOException {
        final int readLen = is.read(buf, off, len);
        if (readLen < 0) {
            if (SSLLogger.isOn && SSLLogger.isOn("packet")) {
                SSLLogger.fine("Raw read: EOF", new Object[0]);
            }
            throw new EOFException("SSL peer shut down incorrectly");
        }
        if (SSLLogger.isOn && SSLLogger.isOn("packet")) {
            final ByteBuffer bb = ByteBuffer.wrap(buf, off, readLen);
            SSLLogger.fine("Raw read", bb);
        }
        return readLen;
    }
    
    void deplete(final boolean tryToRead) throws IOException {
        int remaining = this.is.available();
        if (tryToRead && remaining == 0) {
            this.is.read();
        }
        while ((remaining = this.is.available()) != 0) {
            this.is.skip(remaining);
        }
    }
}
