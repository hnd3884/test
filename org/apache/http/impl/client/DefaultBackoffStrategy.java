package org.apache.http.impl.client;

import org.apache.http.HttpResponse;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import org.apache.http.client.ConnectionBackoffStrategy;

public class DefaultBackoffStrategy implements ConnectionBackoffStrategy
{
    @Override
    public boolean shouldBackoff(final Throwable t) {
        return t instanceof SocketTimeoutException || t instanceof ConnectException;
    }
    
    @Override
    public boolean shouldBackoff(final HttpResponse resp) {
        return resp.getStatusLine().getStatusCode() == 429 || resp.getStatusLine().getStatusCode() == 503;
    }
}
