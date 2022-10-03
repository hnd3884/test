package org.bouncycastle.openssl;

import java.io.IOException;

interface PEMKeyPairParser
{
    PEMKeyPair parse(final byte[] p0) throws IOException;
}
