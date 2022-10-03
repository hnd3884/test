package com.btr.proxy.selector.whitelist;

import java.net.URI;
import com.btr.proxy.util.UriFilter;

public class HostnameFilter implements UriFilter
{
    private String matchTo;
    private Mode mode;
    
    public HostnameFilter(final Mode mode, final String matchTo) {
        this.mode = mode;
        this.matchTo = matchTo.toLowerCase();
    }
    
    public boolean accept(final URI uri) {
        if (uri == null || uri.getAuthority() == null) {
            return false;
        }
        String host = uri.getAuthority();
        final int index = host.indexOf(58);
        if (index >= 0) {
            host = host.substring(0, index);
        }
        switch (this.mode) {
            case BEGINS_WITH: {
                return host.toLowerCase().startsWith(this.matchTo);
            }
            case ENDS_WITH: {
                return host.toLowerCase().endsWith(this.matchTo);
            }
            case REGEX: {
                return host.toLowerCase().matches(this.matchTo);
            }
            default: {
                return false;
            }
        }
    }
    
    public enum Mode
    {
        BEGINS_WITH, 
        ENDS_WITH, 
        REGEX;
    }
}
