package com.sun.net.httpserver;

import com.sun.net.httpserver.spi.HttpServerProvider;
import java.io.IOException;
import java.net.InetSocketAddress;
import jdk.Exported;

@Exported
public abstract class HttpsServer extends HttpServer
{
    protected HttpsServer() {
    }
    
    public static HttpsServer create() throws IOException {
        return create(null, 0);
    }
    
    public static HttpsServer create(final InetSocketAddress inetSocketAddress, final int n) throws IOException {
        return HttpServerProvider.provider().createHttpsServer(inetSocketAddress, n);
    }
    
    public abstract void setHttpsConfigurator(final HttpsConfigurator p0);
    
    public abstract HttpsConfigurator getHttpsConfigurator();
}
