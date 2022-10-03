package org.bouncycastle.crypto.tls;

import java.io.IOException;

public class AbstractTlsCipherFactory implements TlsCipherFactory
{
    public TlsCipher createCipher(final TlsContext tlsContext, final int n, final int n2) throws IOException {
        throw new TlsFatalAlert((short)80);
    }
}
