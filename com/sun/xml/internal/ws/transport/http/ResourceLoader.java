package com.sun.xml.internal.ws.transport.http;

import java.util.Set;
import java.net.MalformedURLException;
import java.net.URL;

public interface ResourceLoader
{
    URL getResource(final String p0) throws MalformedURLException;
    
    URL getCatalogFile() throws MalformedURLException;
    
    Set<String> getResourcePaths(final String p0);
}
