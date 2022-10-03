package com.sun.xml.internal.ws.transport.http.server;

import java.io.IOException;
import com.sun.xml.internal.ws.transport.http.WSHTTPConnection;
import com.sun.xml.internal.ws.resources.HttpserverMessages;
import java.util.logging.Level;
import com.sun.net.httpserver.HttpExchange;
import com.sun.istack.internal.Nullable;
import com.sun.istack.internal.NotNull;
import java.util.concurrent.Executor;
import com.sun.xml.internal.ws.transport.http.HttpAdapter;
import java.util.logging.Logger;
import com.sun.net.httpserver.HttpHandler;

final class WSHttpHandler implements HttpHandler
{
    private static final String GET_METHOD = "GET";
    private static final String POST_METHOD = "POST";
    private static final String HEAD_METHOD = "HEAD";
    private static final String PUT_METHOD = "PUT";
    private static final String DELETE_METHOD = "DELETE";
    private static final Logger LOGGER;
    private static final boolean fineTraceEnabled;
    private final HttpAdapter adapter;
    private final Executor executor;
    
    public WSHttpHandler(@NotNull final HttpAdapter adapter, @Nullable final Executor executor) {
        assert adapter != null;
        this.adapter = adapter;
        this.executor = executor;
    }
    
    @Override
    public void handle(final HttpExchange msg) {
        try {
            if (WSHttpHandler.fineTraceEnabled) {
                WSHttpHandler.LOGGER.log(Level.FINE, "Received HTTP request:{0}", msg.getRequestURI());
            }
            if (this.executor != null) {
                this.executor.execute(new HttpHandlerRunnable(msg));
            }
            else {
                this.handleExchange(msg);
            }
        }
        catch (final Throwable t) {}
    }
    
    private void handleExchange(final HttpExchange msg) throws IOException {
        final WSHTTPConnection con = new ServerConnectionImpl(this.adapter, msg);
        try {
            if (WSHttpHandler.fineTraceEnabled) {
                WSHttpHandler.LOGGER.log(Level.FINE, "Received HTTP request:{0}", msg.getRequestURI());
            }
            final String method = msg.getRequestMethod();
            if (method.equals("GET") || method.equals("POST") || method.equals("HEAD") || method.equals("PUT") || method.equals("DELETE")) {
                this.adapter.handle(con);
            }
            else if (WSHttpHandler.LOGGER.isLoggable(Level.WARNING)) {
                WSHttpHandler.LOGGER.warning(HttpserverMessages.UNEXPECTED_HTTP_METHOD(method));
            }
        }
        finally {
            msg.close();
        }
    }
    
    static {
        LOGGER = Logger.getLogger("com.sun.xml.internal.ws.server.http");
        fineTraceEnabled = WSHttpHandler.LOGGER.isLoggable(Level.FINE);
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
                WSHttpHandler.this.handleExchange(this.msg);
            }
            catch (final Throwable e) {
                e.printStackTrace();
            }
        }
    }
}
