package com.google.api.client.http;

import java.io.IOException;
import com.google.api.client.util.BackOffUtils;
import com.google.api.client.util.Preconditions;
import com.google.api.client.util.Sleeper;
import com.google.api.client.util.BackOff;
import com.google.api.client.util.Beta;

@Beta
public class HttpBackOffIOExceptionHandler implements HttpIOExceptionHandler
{
    private final BackOff backOff;
    private Sleeper sleeper;
    
    public HttpBackOffIOExceptionHandler(final BackOff backOff) {
        this.sleeper = Sleeper.DEFAULT;
        this.backOff = Preconditions.checkNotNull(backOff);
    }
    
    public final BackOff getBackOff() {
        return this.backOff;
    }
    
    public final Sleeper getSleeper() {
        return this.sleeper;
    }
    
    public HttpBackOffIOExceptionHandler setSleeper(final Sleeper sleeper) {
        this.sleeper = Preconditions.checkNotNull(sleeper);
        return this;
    }
    
    @Override
    public boolean handleIOException(final HttpRequest request, final boolean supportsRetry) throws IOException {
        if (!supportsRetry) {
            return false;
        }
        try {
            return BackOffUtils.next(this.sleeper, this.backOff);
        }
        catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
