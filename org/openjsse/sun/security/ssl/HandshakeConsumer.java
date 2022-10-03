package org.openjsse.sun.security.ssl;

import java.io.IOException;

interface HandshakeConsumer
{
    void consume(final ConnectionContext p0, final SSLHandshake.HandshakeMessage p1) throws IOException;
}
