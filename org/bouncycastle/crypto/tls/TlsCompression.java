package org.bouncycastle.crypto.tls;

import java.io.OutputStream;

public interface TlsCompression
{
    OutputStream compress(final OutputStream p0);
    
    OutputStream decompress(final OutputStream p0);
}
