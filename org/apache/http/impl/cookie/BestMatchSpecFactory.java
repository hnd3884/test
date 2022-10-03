package org.apache.http.impl.cookie;

import org.apache.http.protocol.HttpContext;
import java.util.Collection;
import org.apache.http.params.HttpParams;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.cookie.CookieSpecFactory;

@Deprecated
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class BestMatchSpecFactory implements CookieSpecFactory, CookieSpecProvider
{
    private final CookieSpec cookieSpec;
    
    public BestMatchSpecFactory(final String[] datepatterns, final boolean oneHeader) {
        this.cookieSpec = new BestMatchSpec(datepatterns, oneHeader);
    }
    
    public BestMatchSpecFactory() {
        this(null, false);
    }
    
    @Override
    public CookieSpec newInstance(final HttpParams params) {
        if (params != null) {
            String[] patterns = null;
            final Collection<?> param = (Collection<?>)params.getParameter("http.protocol.cookie-datepatterns");
            if (param != null) {
                patterns = new String[param.size()];
                patterns = param.toArray(patterns);
            }
            final boolean singleHeader = params.getBooleanParameter("http.protocol.single-cookie-header", false);
            return new BestMatchSpec(patterns, singleHeader);
        }
        return new BestMatchSpec();
    }
    
    @Override
    public CookieSpec create(final HttpContext context) {
        return this.cookieSpec;
    }
}
