package org.glassfish.jersey.client.filter;

import java.util.Collections;
import java.util.HashSet;
import java.io.IOException;
import javax.ws.rs.client.ClientRequestContext;
import java.util.Set;
import javax.ws.rs.client.ClientRequestFilter;

public class CsrfProtectionFilter implements ClientRequestFilter
{
    public static final String HEADER_NAME = "X-Requested-By";
    private static final Set<String> METHODS_TO_IGNORE;
    private final String requestedBy;
    
    public CsrfProtectionFilter() {
        this("");
    }
    
    public CsrfProtectionFilter(final String requestedBy) {
        this.requestedBy = requestedBy;
    }
    
    public void filter(final ClientRequestContext rc) throws IOException {
        if (!CsrfProtectionFilter.METHODS_TO_IGNORE.contains(rc.getMethod()) && !rc.getHeaders().containsKey((Object)"X-Requested-By")) {
            rc.getHeaders().add((Object)"X-Requested-By", (Object)this.requestedBy);
        }
    }
    
    static {
        final HashSet<String> mti = new HashSet<String>();
        mti.add("GET");
        mti.add("OPTIONS");
        mti.add("HEAD");
        METHODS_TO_IGNORE = Collections.unmodifiableSet((Set<? extends String>)mti);
    }
}
