package org.apache.http.impl.conn;

import org.apache.http.HttpException;
import org.apache.http.protocol.HttpContext;
import org.apache.http.HttpRequest;
import org.apache.http.util.Args;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.HttpHost;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class DefaultProxyRoutePlanner extends DefaultRoutePlanner
{
    private final HttpHost proxy;
    
    public DefaultProxyRoutePlanner(final HttpHost proxy, final SchemePortResolver schemePortResolver) {
        super(schemePortResolver);
        this.proxy = (HttpHost)Args.notNull((Object)proxy, "Proxy host");
    }
    
    public DefaultProxyRoutePlanner(final HttpHost proxy) {
        this(proxy, null);
    }
    
    @Override
    protected HttpHost determineProxy(final HttpHost target, final HttpRequest request, final HttpContext context) throws HttpException {
        return this.proxy;
    }
}
