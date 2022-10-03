package com.sun.xml.internal.ws.transport.http.server;

import java.io.IOException;
import com.sun.xml.internal.ws.transport.http.WSHTTPConnection;
import com.sun.xml.internal.ws.resources.HttpserverMessages;
import java.util.logging.Level;
import javax.xml.ws.spi.http.HttpExchange;
import com.sun.istack.internal.Nullable;
import com.sun.istack.internal.NotNull;
import java.util.concurrent.Executor;
import com.sun.xml.internal.ws.transport.http.HttpAdapter;
import java.util.logging.Logger;
import javax.xml.ws.spi.http.HttpHandler;

final class PortableHttpHandler extends HttpHandler
{
    private static final String GET_METHOD = "GET";
    private static final String POST_METHOD = "POST";
    private static final String HEAD_METHOD = "HEAD";
    private static final String PUT_METHOD = "PUT";
    private static final String DELETE_METHOD = "DELETE";
    private static final Logger logger;
    private final HttpAdapter adapter;
    private final Executor executor;
    
    public PortableHttpHandler(@NotNull final HttpAdapter adapter, @Nullable final Executor executor) {
        assert adapter != null;
        this.adapter = adapter;
        this.executor = executor;
    }
    
    @Override
    public void handle(final HttpExchange msg) {
        try {
            if (PortableHttpHandler.logger.isLoggable(Level.FINE)) {
                PortableHttpHandler.logger.log(Level.FINE, "Received HTTP request:{0}", msg.getRequestURI());
            }
            if (this.executor != null) {
                this.executor.execute(new HttpHandlerRunnable(msg));
            }
            else {
                this.handleExchange(msg);
            }
        }
        catch (final Throwable e) {
            PortableHttpHandler.logger.log(Level.SEVERE, null, e);
        }
    }
    
    public void handleExchange(final HttpExchange msg) throws IOException {
        final WSHTTPConnection con = new PortableConnectionImpl(this.adapter, msg);
        try {
            if (PortableHttpHandler.logger.isLoggable(Level.FINE)) {
                PortableHttpHandler.logger.log(Level.FINE, "Received HTTP request:{0}", msg.getRequestURI());
            }
            final String method = msg.getRequestMethod();
            if (method.equals("GET") || method.equals("POST") || method.equals("HEAD") || method.equals("PUT") || method.equals("DELETE")) {
                this.adapter.handle(con);
            }
            else {
                PortableHttpHandler.logger.warning(HttpserverMessages.UNEXPECTED_HTTP_METHOD(method));
            }
        }
        finally {
            msg.close();
        }
    }
    
    static {
        logger = Logger.getLogger("com.sun.xml.internal.ws.server.http");
    }
    
    class HttpHandlerRunnable implements Runnable
    {
        final HttpExchange msg;
        
        HttpHandlerRunnable(final HttpExchange msg) {
            this.msg = msg;
        }
        
        @Override
        public void run() {
            try {
                PortableHttpHandler.this.handleExchange(this.msg);
            }
            catch (final Throwable e) {
                e.printStackTrace();
            }
        }
    }
}
