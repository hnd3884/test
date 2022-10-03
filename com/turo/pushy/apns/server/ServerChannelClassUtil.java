package com.turo.pushy.apns.server;

import java.util.HashMap;
import java.util.Objects;
import io.netty.channel.ServerChannel;
import io.netty.channel.EventLoopGroup;
import java.util.Map;

class ServerChannelClassUtil
{
    private static final Map<String, String> SERVER_SOCKET_CHANNEL_CLASSES;
    
    static Class<? extends ServerChannel> getServerSocketChannelClass(final EventLoopGroup eventLoopGroup) {
        Objects.requireNonNull(eventLoopGroup);
        final String serverSocketChannelClassName = ServerChannelClassUtil.SERVER_SOCKET_CHANNEL_CLASSES.get(eventLoopGroup.getClass().getName());
        if (serverSocketChannelClassName == null) {
            throw new IllegalArgumentException("No server socket channel class found for event loop group type: " + eventLoopGroup.getClass().getName());
        }
        try {
            return Class.forName(serverSocketChannelClassName).asSubclass(ServerChannel.class);
        }
        catch (final ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    static {
        (SERVER_SOCKET_CHANNEL_CLASSES = new HashMap<String, String>()).put("io.netty.channel.nio.NioEventLoopGroup", "io.netty.channel.socket.nio.NioServerSocketChannel");
        ServerChannelClassUtil.SERVER_SOCKET_CHANNEL_CLASSES.put("io.netty.channel.oio.OioEventLoopGroup", "io.netty.channel.socket.oio.OioServerSocketChannel");
        ServerChannelClassUtil.SERVER_SOCKET_CHANNEL_CLASSES.put("io.netty.channel.epoll.EpollEventLoopGroup", "io.netty.channel.epoll.EpollServerSocketChannel");
        ServerChannelClassUtil.SERVER_SOCKET_CHANNEL_CLASSES.put("io.netty.channel.kqueue.KQueueEventLoopGroup", "io.netty.channel.kqueue.KQueueServerSocketChannel");
    }
}
