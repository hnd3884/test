package org.bouncycastle.est;

import java.io.IOException;

public interface ESTClientSourceProvider
{
    Source makeSource(final String p0, final int p1) throws IOException;
}
