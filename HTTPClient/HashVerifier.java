package HTTPClient;

import java.io.IOException;

interface HashVerifier
{
    void verifyHash(final byte[] p0, final long p1) throws IOException;
}
