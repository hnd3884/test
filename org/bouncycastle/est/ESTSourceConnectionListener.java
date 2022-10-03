package org.bouncycastle.est;

import java.io.IOException;

public interface ESTSourceConnectionListener<T, I>
{
    ESTRequest onConnection(final Source<T> p0, final ESTRequest p1) throws IOException;
}
