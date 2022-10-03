package org.apache.tomcat.websocket;

import java.io.IOException;
import java.net.SocketAddress;
import javax.net.ssl.SSLException;
import java.util.concurrent.TimeUnit;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Future;
import java.nio.ByteBuffer;

public interface AsyncChannelWrapper
{
    Future<Integer> read(final ByteBuffer p0);
    
     <B, A extends B> void read(final ByteBuffer p0, final A p1, final CompletionHandler<Integer, B> p2);
    
    Future<Integer> write(final ByteBuffer p0);
    
     <B, A extends B> void write(final ByteBuffer[] p0, final int p1, final int p2, final long p3, final TimeUnit p4, final A p5, final CompletionHandler<Long, B> p6);
    
    void close();
    
    Future<Void> handshake() throws SSLException;
    
    SocketAddress getLocalAddress() throws IOException;
}
