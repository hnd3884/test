package org.apache.commons.httpclient;

import java.io.IOException;

public interface HttpMethodRetryHandler
{
    boolean retryMethod(final HttpMethod p0, final IOException p1, final int p2);
}
