package io.opencensus.contrib.http;

import javax.annotation.Nullable;

public abstract class HttpExtractor<Q, P>
{
    @Nullable
    public abstract String getRoute(final Q p0);
    
    @Nullable
    public abstract String getUrl(final Q p0);
    
    @Nullable
    public abstract String getHost(final Q p0);
    
    @Nullable
    public abstract String getMethod(final Q p0);
    
    @Nullable
    public abstract String getPath(final Q p0);
    
    @Nullable
    public abstract String getUserAgent(final Q p0);
    
    public abstract int getStatusCode(@Nullable final P p0);
}
