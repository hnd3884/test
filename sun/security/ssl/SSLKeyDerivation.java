package sun.security.ssl;

import java.io.IOException;
import javax.crypto.SecretKey;
import java.security.spec.AlgorithmParameterSpec;

interface SSLKeyDerivation
{
    SecretKey deriveKey(final String p0, final AlgorithmParameterSpec p1) throws IOException;
}
