package org.bouncycastle.crypto.tls;

import java.io.IOException;

public interface TlsCipherFactory
{
    TlsCipher createCipher(final TlsContext p0, final int p1, final int p2) throws IOException;
}
