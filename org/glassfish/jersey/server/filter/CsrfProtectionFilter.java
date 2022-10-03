package org.glassfish.jersey.server.filter;

import java.util.Collections;
import java.util.HashSet;
import java.io.IOException;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.container.ContainerRequestContext;
import java.util.Set;
import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestFilter;

@Priority(1000)
public class CsrfProtectionFilter implements ContainerRequestFilter
{
    public static final String HEADER_NAME = "X-Requested-By";
    private static final Set<String> METHODS_TO_IGNORE;
    
    public void filter(final ContainerRequestContext rc) throws IOException {
        if (!CsrfProtectionFilter.METHODS_TO_IGNORE.contains(rc.getMethod()) && !rc.getHeaders().containsKey((Object)"X-Requested-By")) {
            throw new BadRequestException();
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
