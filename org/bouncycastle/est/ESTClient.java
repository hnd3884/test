package org.bouncycastle.est;

import java.io.IOException;

public interface ESTClient
{
    ESTResponse doRequest(final ESTRequest p0) throws IOException;
}
