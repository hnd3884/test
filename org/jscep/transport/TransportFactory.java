package org.jscep.transport;

import java.net.URL;

public interface TransportFactory
{
    Transport forMethod(final Method p0, final URL p1);
    
    public enum Method
    {
        GET, 
        POST;
    }
}
