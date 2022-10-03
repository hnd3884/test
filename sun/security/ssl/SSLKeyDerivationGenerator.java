package sun.security.ssl;

import java.io.IOException;
import javax.crypto.SecretKey;

interface SSLKeyDerivationGenerator
{
    SSLKeyDerivation createKeyDerivation(final HandshakeContext p0, final SecretKey p1) throws IOException;
}
