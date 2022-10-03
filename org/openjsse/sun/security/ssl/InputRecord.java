package org.openjsse.sun.security.ssl;

import java.nio.BufferUnderflowException;
import java.io.OutputStream;
import javax.crypto.BadPaddingException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.Closeable;

abstract class InputRecord implements Record, Closeable
{
    SSLCipher.SSLReadCipher readCipher;
    TransportContext tc;
    final HandshakeHash handshakeHash;
    boolean isClosed;
    ProtocolVersion helloVersion;
    int fragmentSize;
    
    InputRecord(final HandshakeHash handshakeHash, final SSLCipher.SSLReadCipher readCipher) {
        this.readCipher = readCipher;
        this.helloVersion = ProtocolVersion.TLS10;
        this.handshakeHash = handshakeHash;
        this.isClosed = false;
        this.fragmentSize = 16384;
    }
    
    void setHelloVersion(final ProtocolVersion helloVersion) {
        this.helloVersion = helloVersion;
    }
    
    boolean seqNumIsHuge() {
        return this.readCipher.authenticator != null && this.readCipher.authenticator.seqNumIsHuge();
    }
    
    boolean isEmpty() {
        return false;
    }
    
    void expectingFinishFlight() {
    }
    
    void finishHandshake() {
    }
    
    @Override
    public synchronized void close() throws IOException {
        if (!this.isClosed) {
            this.isClosed = true;
            this.readCipher.dispose();
        }
    }
    
    synchronized boolean isClosed() {
        return this.isClosed;
    }
    
    void changeReadCiphers(final SSLCipher.SSLReadCipher readCipher) {
        readCipher.dispose();
        this.readCipher = readCipher;
    }
    
    void changeFragmentSize(final int fragmentSize) {
        this.fragmentSize = fragmentSize;
    }
    
    int bytesInCompletePacket(final ByteBuffer[] srcs, final int srcsOffset, final int srcsLength) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    int bytesInCompletePacket() throws IOException {
        throw new UnsupportedOperationException();
    }
    
    void setReceiverStream(final InputStream inputStream) {
        throw new UnsupportedOperationException();
    }
    
    Plaintext acquirePlaintext() throws IOException, BadPaddingException {
        throw new UnsupportedOperationException();
    }
    
    abstract Plaintext[] decode(final ByteBuffer[] p0, final int p1, final int p2) throws IOException, BadPaddingException;
    
    void setDeliverStream(final OutputStream outputStream) {
        throw new UnsupportedOperationException();
    }
    
    int estimateFragmentSize(final int packetSize) {
        throw new UnsupportedOperationException();
    }
    
    static ByteBuffer convertToClientHello(final ByteBuffer packet) {
        final int srcPos = packet.position();
        final byte firstByte = packet.get();
        final byte secondByte = packet.get();
        final int recordLen = ((firstByte & 0x7F) << 8 | (secondByte & 0xFF)) + 2;
        packet.position(srcPos + 3);
        final byte majorVersion = packet.get();
        final byte minorVersion = packet.get();
        final int cipherSpecLen = ((packet.get() & 0xFF) << 8) + (packet.get() & 0xFF);
        final int sessionIdLen = ((packet.get() & 0xFF) << 8) + (packet.get() & 0xFF);
        final int nonceLen = ((packet.get() & 0xFF) << 8) + (packet.get() & 0xFF);
        final int requiredSize = 48 + sessionIdLen + cipherSpecLen * 2 / 3;
        final byte[] converted = new byte[requiredSize];
        converted[0] = ContentType.HANDSHAKE.id;
        converted[1] = majorVersion;
        converted[2] = minorVersion;
        converted[5] = 1;
        converted[9] = majorVersion;
        converted[10] = minorVersion;
        int pointer = 11;
        int offset = srcPos + 11 + cipherSpecLen + sessionIdLen;
        if (nonceLen < 32) {
            for (int i = 0; i < 32 - nonceLen; ++i) {
                converted[pointer++] = 0;
            }
            packet.position(offset);
            packet.get(converted, pointer, nonceLen);
            pointer += nonceLen;
        }
        else {
            packet.position(offset + nonceLen - 32);
            packet.get(converted, pointer, 32);
            pointer += 32;
        }
        offset -= sessionIdLen;
        converted[pointer++] = (byte)(sessionIdLen & 0xFF);
        packet.position(offset);
        packet.get(converted, pointer, sessionIdLen);
        offset -= cipherSpecLen;
        packet.position(offset);
        int j = pointer + 2;
        for (int k = 0; k < cipherSpecLen; k += 3) {
            if (packet.get() != 0) {
                packet.get();
                packet.get();
            }
            else {
                converted[j++] = packet.get();
                converted[j++] = packet.get();
            }
        }
        j -= pointer + 2;
        converted[pointer++] = (byte)(j >>> 8 & 0xFF);
        converted[pointer++] = (byte)(j & 0xFF);
        pointer += j;
        converted[pointer++] = 1;
        converted[pointer++] = 0;
        int fragLen = pointer - 5;
        converted[3] = (byte)(fragLen >>> 8 & 0xFF);
        converted[4] = (byte)(fragLen & 0xFF);
        fragLen = pointer - 9;
        converted[6] = (byte)(fragLen >>> 16 & 0xFF);
        converted[7] = (byte)(fragLen >>> 8 & 0xFF);
        converted[8] = (byte)(fragLen & 0xFF);
        packet.position(srcPos + recordLen);
        return ByteBuffer.wrap(converted, 5, pointer - 5);
    }
    
    static ByteBuffer extract(final ByteBuffer[] buffers, final int offset, final int length, final int headerSize) {
        boolean hasFullHeader = false;
        int contentLen = -1;
        for (int i = offset, j = 0; i < offset + length && j < headerSize; ++i) {
            final int remains = buffers[i].remaining();
            final int pos = buffers[i].position();
            for (int k = 0; k < remains && j < headerSize; ++j, ++k) {
                final byte b = buffers[i].get(pos + k);
                if (j == headerSize - 2) {
                    contentLen = (b & 0xFF) << 8;
                }
                else if (j == headerSize - 1) {
                    contentLen |= (b & 0xFF);
                    hasFullHeader = true;
                    break;
                }
            }
        }
        if (!hasFullHeader) {
            throw new BufferUnderflowException();
        }
        final int packetLen = headerSize + contentLen;
        int remains2 = 0;
        for (int l = offset; l < offset + length; ++l) {
            remains2 += buffers[l].remaining();
            if (remains2 >= packetLen) {
                break;
            }
        }
        if (remains2 < packetLen) {
            throw new BufferUnderflowException();
        }
        final byte[] packet = new byte[packetLen];
        int packetOffset = 0;
        int packetSpaces = packetLen;
        for (int m = offset; m < offset + length; ++m) {
            if (buffers[m].hasRemaining()) {
                final int len = Math.min(packetSpaces, buffers[m].remaining());
                buffers[m].get(packet, packetOffset, len);
                packetOffset += len;
                packetSpaces -= len;
            }
            if (packetSpaces <= 0) {
                break;
            }
        }
        return ByteBuffer.wrap(packet);
    }
}
