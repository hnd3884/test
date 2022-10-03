package com.sun.nio.sctp;

import java.util.Set;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.io.IOException;
import sun.nio.ch.sctp.SctpServerChannelImpl;
import java.nio.channels.spi.SelectorProvider;
import jdk.Exported;
import java.nio.channels.spi.AbstractSelectableChannel;

@Exported
public abstract class SctpServerChannel extends AbstractSelectableChannel
{
    protected SctpServerChannel(final SelectorProvider selectorProvider) {
        super(selectorProvider);
    }
    
    public static SctpServerChannel open() throws IOException {
        return new SctpServerChannelImpl(null);
    }
    
    public abstract SctpChannel accept() throws IOException;
    
    public final SctpServerChannel bind(final SocketAddress socketAddress) throws IOException {
        return this.bind(socketAddress, 0);
    }
    
    public abstract SctpServerChannel bind(final SocketAddress p0, final int p1) throws IOException;
    
    public abstract SctpServerChannel bindAddress(final InetAddress p0) throws IOException;
    
    public abstract SctpServerChannel unbindAddress(final InetAddress p0) throws IOException;
    
    public abstract Set<SocketAddress> getAllLocalAddresses() throws IOException;
    
    public abstract <T> T getOption(final SctpSocketOption<T> p0) throws IOException;
    
    public abstract <T> SctpServerChannel setOption(final SctpSocketOption<T> p0, final T p1) throws IOException;
    
    public abstract Set<SctpSocketOption<?>> supportedOptions();
    
    @Override
    public final int validOps() {
        return 16;
    }
}
