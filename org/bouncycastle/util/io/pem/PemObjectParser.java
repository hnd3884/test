package org.bouncycastle.util.io.pem;

import java.io.IOException;

public interface PemObjectParser
{
    Object parseObject(final PemObject p0) throws IOException;
}
