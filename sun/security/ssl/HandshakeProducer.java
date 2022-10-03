package sun.security.ssl;

import java.io.IOException;

interface HandshakeProducer
{
    byte[] produce(final ConnectionContext p0, final SSLHandshake.HandshakeMessage p1) throws IOException;
}
