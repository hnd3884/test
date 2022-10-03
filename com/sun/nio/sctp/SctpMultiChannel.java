package com.sun.nio.sctp;

import java.nio.ByteBuffer;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Set;
import java.io.IOException;
import sun.nio.ch.sctp.SctpMultiChannelImpl;
import java.nio.channels.spi.SelectorProvider;
import jdk.Exported;
import java.nio.channels.spi.AbstractSelectableChannel;

@Exported
public abstract class SctpMultiChannel extends AbstractSelectableChannel
{
    protected SctpMultiChannel(final SelectorProvider selectorProvider) {
        super(selectorProvider);
    }
    
    public static SctpMultiChannel open() throws IOException {
        return new SctpMultiChannelImpl(null);
    }
    
    public abstract Set<Association> associations() throws IOException;
    
    public abstract SctpMultiChannel bind(final SocketAddress p0, final int p1) throws IOException;
    
    public final SctpMultiChannel bind(final SocketAddress socketAddress) throws IOException {
        return this.bind(socketAddress, 0);
    }
    
    public abstract SctpMultiChannel bindAddress(final InetAddress p0) throws IOException;
    
    public abstract SctpMultiChannel unbindAddress(final InetAddress p0) throws IOException;
    
    public abstract Set<SocketAddress> getAllLocalAddresses() throws IOException;
    
    public abstract Set<SocketAddress> getRemoteAddresses(final Association p0) throws IOException;
    
    public abstract SctpMultiChannel shutdown(final Association p0) throws IOException;
    
    public abstract <T> T getOption(final SctpSocketOption<T> p0, final Association p1) throws IOException;
    
    public abstract <T> SctpMultiChannel setOption(final SctpSocketOption<T> p0, final T p1, final Association p2) throws IOException;
    
    public abstract Set<SctpSocketOption<?>> supportedOptions();
    
    @Override
    public final int validOps() {
        return 5;
    }
    
    public abstract <T> MessageInfo receive(final ByteBuffer p0, final T p1, final NotificationHandler<T> p2) throws IOException;
    
    public abstract int send(final ByteBuffer p0, final MessageInfo p1) throws IOException;
    
    public abstract SctpChannel branch(final Association p0) throws IOException;
}
