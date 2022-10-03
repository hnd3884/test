package org.jscep.transport;

import java.net.URL;
import javax.net.ssl.SSLSocketFactory;

public class UrlConnectionTransportFactory implements TransportFactory
{
    private SSLSocketFactory sslSocketFactory;
    
    public UrlConnectionTransportFactory() {
    }
    
    public UrlConnectionTransportFactory(final SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
    }
    
    @Override
    public Transport forMethod(final Method method, final URL url) {
        if (method == Method.GET) {
            return new UrlConnectionGetTransport(url, this.sslSocketFactory);
        }
        return new UrlConnectionPostTransport(url, this.sslSocketFactory);
    }
}
