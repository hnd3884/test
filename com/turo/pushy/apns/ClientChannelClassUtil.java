package com.turo.pushy.apns;

import java.util.HashMap;
import io.netty.channel.socket.DatagramChannel;
import java.util.Objects;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.EventLoopGroup;
import java.util.Map;

class ClientChannelClassUtil
{
    private static final Map<String, String> SOCKET_CHANNEL_CLASSES;
    private static final Map<String, String> DATAGRAM_CHANNEL_CLASSES;
    
    static Class<? extends SocketChannel> getSocketChannelClass(final EventLoopGroup eventLoopGroup) {
        Objects.requireNonNull(eventLoopGroup);
        final String socketChannelClassName = ClientChannelClassUtil.SOCKET_CHANNEL_CLASSES.get(eventLoopGroup.getClass().getName());
        if (socketChannelClassName == null) {
            throw new IllegalArgumentException("No socket channel class found for event loop group type: " + eventLoopGroup.getClass().getName());
        }
        try {
            return Class.forName(socketChannelClassName).asSubclass(SocketChannel.class);
        }
        catch (final ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    static Class<? extends DatagramChannel> getDatagramChannelClass(final EventLoopGroup eventLoopGroup) {
        Objects.requireNonNull(eventLoopGroup);
        final String datagramChannelClassName = ClientChannelClassUtil.DATAGRAM_CHANNEL_CLASSES.get(eventLoopGroup.getClass().getName());
        if (datagramChannelClassName == null) {
            throw new IllegalArgumentException("No datagram channel class found for event loop group type: " + eventLoopGroup.getClass().getName());
        }
        try {
            return Class.forName(datagramChannelClassName).asSubclass(DatagramChannel.class);
        }
        catch (final ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    static {
        SOCKET_CHANNEL_CLASSES = new HashMap<String, String>();
        DATAGRAM_CHANNEL_CLASSES = new HashMap<String, String>();
        ClientChannelClassUtil.SOCKET_CHANNEL_CLASSES.put("io.netty.channel.nio.NioEventLoopGroup", "io.netty.channel.socket.nio.NioSocketChannel");
        ClientChannelClassUtil.SOCKET_CHANNEL_CLASSES.put("io.netty.channel.oio.OioEventLoopGroup", "io.netty.channel.socket.oio.OioSocketChannel");
        ClientChannelClassUtil.SOCKET_CHANNEL_CLASSES.put("io.netty.channel.epoll.EpollEventLoopGroup", "io.netty.channel.epoll.EpollSocketChannel");
        ClientChannelClassUtil.SOCKET_CHANNEL_CLASSES.put("io.netty.channel.kqueue.KQueueEventLoopGroup", "io.netty.channel.kqueue.KQueueSocketChannel");
        ClientChannelClassUtil.DATAGRAM_CHANNEL_CLASSES.put("io.netty.channel.nio.NioEventLoopGroup", "io.netty.channel.socket.nio.NioDatagramChannel");
        ClientChannelClassUtil.DATAGRAM_CHANNEL_CLASSES.put("io.netty.channel.oio.OioEventLoopGroup", "io.netty.channel.socket.oio.OioDatagramChannel");
        ClientChannelClassUtil.DATAGRAM_CHANNEL_CLASSES.put("io.netty.channel.epoll.EpollEventLoopGroup", "io.netty.channel.epoll.EpollDatagramChannel");
        ClientChannelClassUtil.DATAGRAM_CHANNEL_CLASSES.put("io.netty.channel.kqueue.KQueueEventLoopGroup", "io.netty.channel.kqueue.KQueueDatagramChannel");
    }
}
