package org.apache.http.impl.client;

import org.apache.http.TokenIterator;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.message.BasicTokenIterator;
import org.apache.http.message.BasicHeaderIterator;
import org.apache.http.HttpRequest;
import org.apache.http.protocol.HttpContext;
import org.apache.http.HttpResponse;
import org.apache.http.impl.DefaultConnectionReuseStrategy;

public class DefaultClientConnectionReuseStrategy extends DefaultConnectionReuseStrategy
{
    public static final DefaultClientConnectionReuseStrategy INSTANCE;
    
    public boolean keepAlive(final HttpResponse response, final HttpContext context) {
        final HttpRequest request = (HttpRequest)context.getAttribute("http.request");
        if (request != null) {
            final Header[] connHeaders = request.getHeaders("Connection");
            if (connHeaders.length != 0) {
                final TokenIterator ti = (TokenIterator)new BasicTokenIterator((HeaderIterator)new BasicHeaderIterator(connHeaders, (String)null));
                while (ti.hasNext()) {
                    final String token = ti.nextToken();
                    if ("Close".equalsIgnoreCase(token)) {
                        return false;
                    }
                }
            }
        }
        return super.keepAlive(response, context);
    }
    
    static {
        INSTANCE = new DefaultClientConnectionReuseStrategy();
    }
}
