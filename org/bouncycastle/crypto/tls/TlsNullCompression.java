package org.bouncycastle.crypto.tls;

import java.io.OutputStream;

public class TlsNullCompression implements TlsCompression
{
    public OutputStream compress(final OutputStream outputStream) {
        return outputStream;
    }
    
    public OutputStream decompress(final OutputStream outputStream) {
        return outputStream;
    }
}
