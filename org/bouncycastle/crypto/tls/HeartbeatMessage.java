package org.bouncycastle.crypto.tls;

import org.bouncycastle.util.Arrays;
import java.io.ByteArrayOutputStream;
import org.bouncycastle.util.io.Streams;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

public class HeartbeatMessage
{
    protected short type;
    protected byte[] payload;
    protected int paddingLength;
    
    public HeartbeatMessage(final short type, final byte[] payload, final int paddingLength) {
        if (!HeartbeatMessageType.isValid(type)) {
            throw new IllegalArgumentException("'type' is not a valid HeartbeatMessageType value");
        }
        if (payload == null || payload.length >= 65536) {
            throw new IllegalArgumentException("'payload' must have length < 2^16");
        }
        if (paddingLength < 16) {
            throw new IllegalArgumentException("'paddingLength' must be at least 16");
        }
        this.type = type;
        this.payload = payload;
        this.paddingLength = paddingLength;
    }
    
    public void encode(final TlsContext tlsContext, final OutputStream outputStream) throws IOException {
        TlsUtils.writeUint8(this.type, outputStream);
        TlsUtils.checkUint16(this.payload.length);
        TlsUtils.writeUint16(this.payload.length, outputStream);
        outputStream.write(this.payload);
        final byte[] array = new byte[this.paddingLength];
        tlsContext.getNonceRandomGenerator().nextBytes(array);
        outputStream.write(array);
    }
    
    public static HeartbeatMessage parse(final InputStream inputStream) throws IOException {
        final short uint8 = TlsUtils.readUint8(inputStream);
        if (!HeartbeatMessageType.isValid(uint8)) {
            throw new TlsFatalAlert((short)47);
        }
        final int uint9 = TlsUtils.readUint16(inputStream);
        final PayloadBuffer payloadBuffer = new PayloadBuffer();
        Streams.pipeAll(inputStream, payloadBuffer);
        final byte[] truncatedByteArray = payloadBuffer.toTruncatedByteArray(uint9);
        if (truncatedByteArray == null) {
            return null;
        }
        return new HeartbeatMessage(uint8, truncatedByteArray, payloadBuffer.size() - truncatedByteArray.length);
    }
    
    static class PayloadBuffer extends ByteArrayOutputStream
    {
        byte[] toTruncatedByteArray(final int n) {
            if (this.count < n + 16) {
                return null;
            }
            return Arrays.copyOf(this.buf, n);
        }
    }
}
