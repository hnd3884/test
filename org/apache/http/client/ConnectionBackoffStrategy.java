package org.apache.http.client;

import org.apache.http.HttpResponse;

public interface ConnectionBackoffStrategy
{
    boolean shouldBackoff(final Throwable p0);
    
    boolean shouldBackoff(final HttpResponse p0);
}
