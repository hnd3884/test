package com.sun.xml.internal.ws.api;

import java.net.MalformedURLException;
import java.net.URL;

public abstract class ResourceLoader
{
    public abstract URL getResource(final String p0) throws MalformedURLException;
}
