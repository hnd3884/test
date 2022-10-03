package sun.net.httpserver;

import java.io.IOException;
import java.util.logging.Logger;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.io.OutputStream;
import java.io.InputStream;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

class HttpConnection
{
    HttpContextImpl context;
    SSLEngine engine;
    SSLContext sslContext;
    SSLStreams sslStreams;
    InputStream i;
    InputStream raw;
    OutputStream rawout;
    SocketChannel chan;
    SelectionKey selectionKey;
    String protocol;
    long time;
    volatile long creationTime;
    volatile long rspStartedTime;
    int remaining;
    boolean closed;
    Logger logger;
    volatile State state;
    
    @Override
    public String toString() {
        String string = null;
        if (this.chan != null) {
            string = this.chan.toString();
        }
        return string;
    }
    
    HttpConnection() {
        this.closed = false;
    }
    
    void setChannel(final SocketChannel chan) {
        this.chan = chan;
    }
    
    void setContext(final HttpContextImpl context) {
        this.context = context;
    }
    
    State getState() {
        return this.state;
    }
    
    void setState(final State state) {
        this.state = state;
    }
    
    void setParameters(final InputStream i, final OutputStream rawout, final SocketChannel chan, final SSLEngine engine, final SSLStreams sslStreams, final SSLContext sslContext, final String protocol, final HttpContextImpl context, final InputStream raw) {
        this.context = context;
        this.i = i;
        this.rawout = rawout;
        this.raw = raw;
        this.protocol = protocol;
        this.engine = engine;
        this.chan = chan;
        this.sslContext = sslContext;
        this.sslStreams = sslStreams;
        this.logger = context.getLogger();
    }
    
    SocketChannel getChannel() {
        return this.chan;
    }
    
    synchronized void close() {
        if (this.closed) {
            return;
        }
        this.closed = true;
        if (this.logger != null && this.chan != null) {
            this.logger.finest("Closing connection: " + this.chan.toString());
        }
        if (!this.chan.isOpen()) {
            ServerImpl.dprint("Channel already closed");
            return;
        }
        try {
            if (this.raw != null) {
                this.raw.close();
            }
        }
        catch (final IOException ex) {
            ServerImpl.dprint(ex);
        }
        try {
            if (this.rawout != null) {
                this.rawout.close();
            }
        }
        catch (final IOException ex2) {
            ServerImpl.dprint(ex2);
        }
        try {
            if (this.sslStreams != null) {
                this.sslStreams.close();
            }
        }
        catch (final IOException ex3) {
            ServerImpl.dprint(ex3);
        }
        try {
            this.chan.close();
        }
        catch (final IOException ex4) {
            ServerImpl.dprint(ex4);
        }
    }
    
    void setRemaining(final int remaining) {
        this.remaining = remaining;
    }
    
    int getRemaining() {
        return this.remaining;
    }
    
    SelectionKey getSelectionKey() {
        return this.selectionKey;
    }
    
    InputStream getInputStream() {
        return this.i;
    }
    
    OutputStream getRawOutputStream() {
        return this.rawout;
    }
    
    String getProtocol() {
        return this.protocol;
    }
    
    SSLEngine getSSLEngine() {
        return this.engine;
    }
    
    SSLContext getSSLContext() {
        return this.sslContext;
    }
    
    HttpContextImpl getHttpContext() {
        return this.context;
    }
    
    public enum State
    {
        IDLE, 
        REQUEST, 
        RESPONSE;
    }
}
