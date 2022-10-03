package com.btr.proxy.search.browser.ie;

import java.net.URI;
import com.btr.proxy.util.UriFilter;

public class IELocalByPassFilter implements UriFilter
{
    public boolean accept(final URI uri) {
        if (uri == null) {
            return false;
        }
        final String host = uri.getAuthority();
        return host != null && !host.contains(".");
    }
}
