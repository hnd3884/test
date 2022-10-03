package com.sun.xml.internal.ws.api;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.ws.Service;

public abstract class WSDLLocator
{
    public abstract URL locateWSDL(final Class<Service> p0, final String p1) throws MalformedURLException;
}
