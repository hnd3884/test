package sun.security.ssl;

import java.io.IOException;
import java.nio.ByteBuffer;

interface SSLConsumer
{
    void consume(final ConnectionContext p0, final ByteBuffer p1) throws IOException;
}
