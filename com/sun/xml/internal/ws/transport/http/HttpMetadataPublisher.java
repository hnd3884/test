package com.sun.xml.internal.ws.transport.http;

import java.io.IOException;
import com.sun.istack.internal.NotNull;

public abstract class HttpMetadataPublisher
{
    public abstract boolean handleMetadataRequest(@NotNull final HttpAdapter p0, @NotNull final WSHTTPConnection p1) throws IOException;
}
