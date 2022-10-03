package sun.security.ssl;

import java.io.IOException;

interface SSLKeyAgreementGenerator
{
    SSLKeyDerivation createKeyDerivation(final HandshakeContext p0) throws IOException;
}
