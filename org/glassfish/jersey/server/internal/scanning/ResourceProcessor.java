package org.glassfish.jersey.server.internal.scanning;

import java.io.IOException;
import java.io.InputStream;

public interface ResourceProcessor
{
    boolean accept(final String p0);
    
    void process(final String p0, final InputStream p1) throws IOException;
}
