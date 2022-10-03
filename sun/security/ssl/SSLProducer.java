package sun.security.ssl;

import java.io.IOException;

interface SSLProducer
{
    byte[] produce(final ConnectionContext p0) throws IOException;
}
