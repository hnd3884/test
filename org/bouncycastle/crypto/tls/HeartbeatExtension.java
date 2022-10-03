package org.bouncycastle.crypto.tls;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

public class HeartbeatExtension
{
    protected short mode;
    
    public HeartbeatExtension(final short mode) {
        if (!HeartbeatMode.isValid(mode)) {
            throw new IllegalArgumentException("'mode' is not a valid HeartbeatMode value");
        }
        this.mode = mode;
    }
    
    public short getMode() {
        return this.mode;
    }
    
    public void encode(final OutputStream outputStream) throws IOException {
        TlsUtils.writeUint8(this.mode, outputStream);
    }
    
    public static HeartbeatExtension parse(final InputStream inputStream) throws IOException {
        final short uint8 = TlsUtils.readUint8(inputStream);
        if (!HeartbeatMode.isValid(uint8)) {
            throw new TlsFatalAlert((short)47);
        }
        return new HeartbeatExtension(uint8);
    }
}
