package sun.security.ssl;

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
    
    int bytesInCompletePacket(final ByteBuffer[] array, final int n, final int n2) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    int bytesInCompletePacket() throws IOException {
        throw new UnsupportedOperationException();
    }
    
    void setReceiverStream(final InputStream inputStream) {
        throw new UnsupportedOperationException();
    }
    
    abstract Plaintext[] decode(final ByteBuffer[] p0, final int p1, final int p2) throws IOException, BadPaddingException;
    
    void setDeliverStream(final OutputStream outputStream) {
        throw new UnsupportedOperationException();
    }
    
    int estimateFragmentSize(final int n) {
        throw new UnsupportedOperationException();
    }
    
    static ByteBuffer convertToClientHello(final ByteBuffer byteBuffer) {
        final int position = byteBuffer.position();
        final int n = ((byteBuffer.get() & 0x7F) << 8 | (byteBuffer.get() & 0xFF)) + 2;
        byteBuffer.position(position + 3);
        final byte value = byteBuffer.get();
        final byte value2 = byteBuffer.get();
        final int n2 = ((byteBuffer.get() & 0xFF) << 8) + (byteBuffer.get() & 0xFF);
        final int n3 = ((byteBuffer.get() & 0xFF) << 8) + (byteBuffer.get() & 0xFF);
        final int n4 = ((byteBuffer.get() & 0xFF) << 8) + (byteBuffer.get() & 0xFF);
        final byte[] array = new byte[48 + n3 + n2 * 2 / 3];
        array[0] = ContentType.HANDSHAKE.id;
        array[1] = value;
        array[2] = value2;
        array[5] = 1;
        array[9] = value;
        array[10] = value2;
        int n5 = 11;
        final int n6 = position + 11 + n2 + n3;
        if (n4 < 32) {
            for (int i = 0; i < 32 - n4; ++i) {
                array[n5++] = 0;
            }
            byteBuffer.position(n6);
            byteBuffer.get(array, n5, n4);
            n5 += n4;
        }
        else {
            byteBuffer.position(n6 + n4 - 32);
            byteBuffer.get(array, n5, 32);
            n5 += 32;
        }
        final int n7 = n6 - n3;
        array[n5++] = (byte)(n3 & 0xFF);
        byteBuffer.position(n7);
        byteBuffer.get(array, n5, n3);
        byteBuffer.position(n7 - n2);
        int n8 = n5 + 2;
        for (int j = 0; j < n2; j += 3) {
            if (byteBuffer.get() != 0) {
                byteBuffer.get();
                byteBuffer.get();
            }
            else {
                array[n8++] = byteBuffer.get();
                array[n8++] = byteBuffer.get();
            }
        }
        final int n9 = n8 - (n5 + 2);
        array[n5++] = (byte)(n9 >>> 8 & 0xFF);
        array[n5++] = (byte)(n9 & 0xFF);
        int n10 = n5 + n9;
        array[n10++] = 1;
        array[n10++] = 0;
        final int n11 = n10 - 5;
        array[3] = (byte)(n11 >>> 8 & 0xFF);
        array[4] = (byte)(n11 & 0xFF);
        final int n12 = n10 - 9;
        array[6] = (byte)(n12 >>> 16 & 0xFF);
        array[7] = (byte)(n12 >>> 8 & 0xFF);
        array[8] = (byte)(n12 & 0xFF);
        byteBuffer.position(position + n);
        return ByteBuffer.wrap(array, 5, n10 - 5);
    }
    
    static ByteBuffer extract(final ByteBuffer[] array, final int n, final int n2, final int n3) {
        boolean b = false;
        int n4 = -1;
        for (int n5 = n, n6 = 0; n5 < n + n2 && n6 < n3; ++n5) {
            final int remaining = array[n5].remaining();
            final int position = array[n5].position();
            for (int n7 = 0; n7 < remaining && n6 < n3; ++n6, ++n7) {
                final byte value = array[n5].get(position + n7);
                if (n6 == n3 - 2) {
                    n4 = (value & 0xFF) << 8;
                }
                else if (n6 == n3 - 1) {
                    n4 |= (value & 0xFF);
                    b = true;
                    break;
                }
            }
        }
        if (!b) {
            throw new BufferUnderflowException();
        }
        final int n8 = n3 + n4;
        int n9 = 0;
        for (int i = n; i < n + n2; ++i) {
            n9 += array[i].remaining();
            if (n9 >= n8) {
                break;
            }
        }
        if (n9 < n8) {
            throw new BufferUnderflowException();
        }
        final byte[] array2 = new byte[n8];
        int n10 = 0;
        int n11 = n8;
        for (int j = n; j < n + n2; ++j) {
            if (array[j].hasRemaining()) {
                final int min = Math.min(n11, array[j].remaining());
                array[j].get(array2, n10, min);
                n10 += min;
                n11 -= min;
            }
            if (n11 <= 0) {
                break;
            }
        }
        return ByteBuffer.wrap(array2);
    }
}
