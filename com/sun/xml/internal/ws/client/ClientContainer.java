package com.sun.xml.internal.ws.client;

import java.net.MalformedURLException;
import java.net.URL;
import com.sun.xml.internal.ws.api.ResourceLoader;
import com.sun.xml.internal.ws.api.server.Container;

final class ClientContainer extends Container
{
    private final ResourceLoader loader;
    
    ClientContainer() {
        this.loader = new ResourceLoader() {
            @Override
            public URL getResource(final String resource) throws MalformedURLException {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                if (cl == null) {
                    cl = this.getClass().getClassLoader();
                }
                return cl.getResource("META-INF/" + resource);
            }
        };
    }
    
    @Override
    public <T> T getSPI(final Class<T> spiType) {
        final T t = super.getSPI(spiType);
        if (t != null) {
            return t;
        }
        if (spiType == ResourceLoader.class) {
            return spiType.cast(this.loader);
        }
        return null;
    }
}
