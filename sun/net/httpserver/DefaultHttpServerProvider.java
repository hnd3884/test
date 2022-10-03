package sun.net.httpserver;

import com.sun.net.httpserver.HttpsServer;
import java.io.IOException;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.spi.HttpServerProvider;

public class DefaultHttpServerProvider extends HttpServerProvider
{
    @Override
    public HttpServer createHttpServer(final InetSocketAddress inetSocketAddress, final int n) throws IOException {
        return new HttpServerImpl(inetSocketAddress, n);
    }
    
    @Override
    public HttpsServer createHttpsServer(final InetSocketAddress inetSocketAddress, final int n) throws IOException {
        return new HttpsServerImpl(inetSocketAddress, n);
    }
}
