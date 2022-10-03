package org.jscep.transport;

import java.net.MalformedURLException;
import org.jscep.transport.request.Operation;
import java.net.URL;

public abstract class AbstractTransport implements Transport
{
    private final URL url;
    
    public AbstractTransport(final URL url) {
        this.url = url;
    }
    
    public final URL getUrl(final Operation op) throws TransportException {
        try {
            return new URL(this.url.toExternalForm() + "?operation=" + op.getName());
        }
        catch (final MalformedURLException e) {
            throw new TransportException(e);
        }
    }
    
    protected final Object[] varargs(final Object... objects) {
        return objects;
    }
}
