package org.apache.http.impl.auth;

import java.io.IOException;

@Deprecated
public interface SpnegoTokenGenerator
{
    byte[] generateSpnegoDERObject(final byte[] p0) throws IOException;
}
