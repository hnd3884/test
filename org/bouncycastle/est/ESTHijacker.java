package org.bouncycastle.est;

import java.io.IOException;

public interface ESTHijacker
{
    ESTResponse hijack(final ESTRequest p0, final Source p1) throws IOException;
}
