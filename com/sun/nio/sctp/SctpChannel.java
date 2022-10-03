package com.sun.nio.sctp;

import java.nio.ByteBuffer;
import java.util.Set;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.io.IOException;
import sun.nio.ch.sctp.SctpChannelImpl;
import java.nio.channels.spi.SelectorProvider;
import jdk.Exported;
import java.nio.channels.spi.AbstractSelectableChannel;

@Exported
public abstract class SctpChannel extends AbstractSelectableChannel
{
    protected SctpChannel(final SelectorProvider selectorProvider) {
        super(selectorProvider);
    }
    
    public static SctpChannel open() throws IOException {
        return new SctpChannelImpl(null);
    }
    
    public static SctpChannel open(final SocketAddress socketAddress, final int n, final int n2) throws IOException {
        final SctpChannel open = open();
        open.connect(socketAddress, n, n2);
        return open;
    }
    
    public abstract Association association() throws IOException;
    
    public abstract SctpChannel bind(final SocketAddress p0) throws IOException;
    
    public abstract SctpChannel bindAddress(final InetAddress p0) throws IOException;
    
    public abstract SctpChannel unbindAddress(final InetAddress p0) throws IOException;
    
    public abstract boolean connect(final SocketAddress p0) throws IOException;
    
    public abstract boolean connect(final SocketAddress p0, final int p1, final int p2) throws IOException;
    
    public abstract boolean isConnectionPending();
    
    public abstract boolean finishConnect() throws IOException;
    
    public abstract Set<SocketAddress> getAllLocalAddresses() throws IOException;
    
    public abstract Set<SocketAddress> getRemoteAddresses() throws IOException;
    
    public abstract SctpChannel shutdown() throws IOException;
    
    public abstract <T> T getOption(final SctpSocketOption<T> p0) throws IOException;
    
    public abstract <T> SctpChannel setOption(final SctpSocketOption<T> p0, final T p1) throws IOException;
    
    public abstract Set<SctpSocketOption<?>> supportedOptions();
    
    @Override
    public final int validOps() {
        return 13;
    }
    
    public abstract <T> MessageInfo receive(final ByteBuffer p0, final T p1, final NotificationHandler<T> p2) throws IOException;
    
    public abstract int send(final ByteBuffer p0, final MessageInfo p1) throws IOException;
}
