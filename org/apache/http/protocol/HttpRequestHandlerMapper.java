package org.apache.http.protocol;

import org.apache.http.HttpRequest;

public interface HttpRequestHandlerMapper
{
    HttpRequestHandler lookup(final HttpRequest p0);
}
