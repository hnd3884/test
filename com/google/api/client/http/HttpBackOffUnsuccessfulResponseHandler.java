package com.google.api.client.http;

import java.io.IOException;
import com.google.api.client.util.BackOffUtils;
import com.google.api.client.util.Preconditions;
import com.google.api.client.util.Sleeper;
import com.google.api.client.util.BackOff;
import com.google.api.client.util.Beta;

@Beta
public class HttpBackOffUnsuccessfulResponseHandler implements HttpUnsuccessfulResponseHandler
{
    private final BackOff backOff;
    private BackOffRequired backOffRequired;
    private Sleeper sleeper;
    
    public HttpBackOffUnsuccessfulResponseHandler(final BackOff backOff) {
        this.backOffRequired = BackOffRequired.ON_SERVER_ERROR;
        this.sleeper = Sleeper.DEFAULT;
        this.backOff = Preconditions.checkNotNull(backOff);
    }
    
    public final BackOff getBackOff() {
        return this.backOff;
    }
    
    public final BackOffRequired getBackOffRequired() {
        return this.backOffRequired;
    }
    
    public HttpBackOffUnsuccessfulResponseHandler setBackOffRequired(final BackOffRequired backOffRequired) {
        this.backOffRequired = Preconditions.checkNotNull(backOffRequired);
        return this;
    }
    
    public final Sleeper getSleeper() {
        return this.sleeper;
    }
    
    public HttpBackOffUnsuccessfulResponseHandler setSleeper(final Sleeper sleeper) {
        this.sleeper = Preconditions.checkNotNull(sleeper);
        return this;
    }
    
    @Override
    public boolean handleResponse(final HttpRequest request, final HttpResponse response, final boolean supportsRetry) throws IOException {
        if (!supportsRetry) {
            return false;
        }
        if (this.backOffRequired.isRequired(response)) {
            try {
                return BackOffUtils.next(this.sleeper, this.backOff);
            }
            catch (final InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
        }
        return false;
    }
    
    @Beta
    public interface BackOffRequired
    {
        public static final BackOffRequired ALWAYS = new BackOffRequired() {
            @Override
            public boolean isRequired(final HttpResponse response) {
                return true;
            }
        };
        public static final BackOffRequired ON_SERVER_ERROR = new BackOffRequired() {
            @Override
            public boolean isRequired(final HttpResponse response) {
                return response.getStatusCode() / 100 == 5;
            }
        };
        
        boolean isRequired(final HttpResponse p0);
    }
}
