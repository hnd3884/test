package org.apache.http.client.protocol;

import java.util.Iterator;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.Cookie;
import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HeaderIterator;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.util.Args;
import org.apache.http.protocol.HttpContext;
import org.apache.http.HttpResponse;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;
import org.apache.http.HttpResponseInterceptor;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class ResponseProcessCookies implements HttpResponseInterceptor
{
    private final Log log;
    
    public ResponseProcessCookies() {
        this.log = LogFactory.getLog((Class)this.getClass());
    }
    
    public void process(final HttpResponse response, final HttpContext context) throws HttpException, IOException {
        Args.notNull((Object)response, "HTTP request");
        Args.notNull((Object)context, "HTTP context");
        final HttpClientContext clientContext = HttpClientContext.adapt(context);
        final CookieSpec cookieSpec = clientContext.getCookieSpec();
        if (cookieSpec == null) {
            this.log.debug((Object)"Cookie spec not specified in HTTP context");
            return;
        }
        final CookieStore cookieStore = clientContext.getCookieStore();
        if (cookieStore == null) {
            this.log.debug((Object)"Cookie store not specified in HTTP context");
            return;
        }
        final CookieOrigin cookieOrigin = clientContext.getCookieOrigin();
        if (cookieOrigin == null) {
            this.log.debug((Object)"Cookie origin not specified in HTTP context");
            return;
        }
        HeaderIterator it = response.headerIterator("Set-Cookie");
        this.processCookies(it, cookieSpec, cookieOrigin, cookieStore);
        if (cookieSpec.getVersion() > 0) {
            it = response.headerIterator("Set-Cookie2");
            this.processCookies(it, cookieSpec, cookieOrigin, cookieStore);
        }
    }
    
    private void processCookies(final HeaderIterator iterator, final CookieSpec cookieSpec, final CookieOrigin cookieOrigin, final CookieStore cookieStore) {
        while (iterator.hasNext()) {
            final Header header = iterator.nextHeader();
            try {
                final List<Cookie> cookies = cookieSpec.parse(header, cookieOrigin);
                for (final Cookie cookie : cookies) {
                    try {
                        cookieSpec.validate(cookie, cookieOrigin);
                        cookieStore.addCookie(cookie);
                        if (!this.log.isDebugEnabled()) {
                            continue;
                        }
                        this.log.debug((Object)("Cookie accepted [" + formatCooke(cookie) + "]"));
                    }
                    catch (final MalformedCookieException ex) {
                        if (!this.log.isWarnEnabled()) {
                            continue;
                        }
                        this.log.warn((Object)("Cookie rejected [" + formatCooke(cookie) + "] " + ex.getMessage()));
                    }
                }
            }
            catch (final MalformedCookieException ex2) {
                if (!this.log.isWarnEnabled()) {
                    continue;
                }
                this.log.warn((Object)("Invalid cookie header: \"" + header + "\". " + ex2.getMessage()));
            }
        }
    }
    
    private static String formatCooke(final Cookie cookie) {
        final StringBuilder buf = new StringBuilder();
        buf.append(cookie.getName());
        buf.append("=\"");
        String v = cookie.getValue();
        if (v != null) {
            if (v.length() > 100) {
                v = v.substring(0, 100) + "...";
            }
            buf.append(v);
        }
        buf.append("\"");
        buf.append(", version:");
        buf.append(Integer.toString(cookie.getVersion()));
        buf.append(", domain:");
        buf.append(cookie.getDomain());
        buf.append(", path:");
        buf.append(cookie.getPath());
        buf.append(", expiry:");
        buf.append(cookie.getExpiryDate());
        return buf.toString();
    }
}
