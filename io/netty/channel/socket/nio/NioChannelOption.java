package io.netty.channel.socket.nio;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.io.IOException;
import io.netty.channel.ChannelException;
import java.net.StandardSocketOptions;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.NetworkChannel;
import java.nio.channels.Channel;
import java.net.SocketOption;
import io.netty.util.internal.SuppressJava6Requirement;
import io.netty.channel.ChannelOption;

@SuppressJava6Requirement(reason = "Usage explicit by the user")
public final class NioChannelOption<T> extends ChannelOption<T>
{
    private final SocketOption<T> option;
    
    private NioChannelOption(final SocketOption<T> option) {
        super(option.name());
        this.option = option;
    }
    
    public static <T> ChannelOption<T> of(final SocketOption<T> option) {
        return new NioChannelOption<T>(option);
    }
    
    @SuppressJava6Requirement(reason = "Usage guarded by java version check")
    static <T> boolean setOption(final Channel jdkChannel, final NioChannelOption<T> option, final T value) {
        final NetworkChannel channel = (NetworkChannel)jdkChannel;
        if (!channel.supportedOptions().contains(option.option)) {
            return false;
        }
        if (channel instanceof ServerSocketChannel && option.option == StandardSocketOptions.IP_TOS) {
            return false;
        }
        try {
            channel.setOption(option.option, value);
            return true;
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @SuppressJava6Requirement(reason = "Usage guarded by java version check")
    static <T> T getOption(final Channel jdkChannel, final NioChannelOption<T> option) {
        final NetworkChannel channel = (NetworkChannel)jdkChannel;
        if (!channel.supportedOptions().contains(option.option)) {
            return null;
        }
        if (channel instanceof ServerSocketChannel && option.option == StandardSocketOptions.IP_TOS) {
            return null;
        }
        try {
            return channel.getOption(option.option);
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @SuppressJava6Requirement(reason = "Usage guarded by java version check")
    static ChannelOption[] getOptions(final Channel jdkChannel) {
        final NetworkChannel channel = (NetworkChannel)jdkChannel;
        final Set<SocketOption<?>> supportedOpts = channel.supportedOptions();
        if (channel instanceof ServerSocketChannel) {
            final List<ChannelOption<?>> extraOpts = new ArrayList<ChannelOption<?>>(supportedOpts.size());
            for (final SocketOption<?> opt : supportedOpts) {
                if (opt == StandardSocketOptions.IP_TOS) {
                    continue;
                }
                extraOpts.add(new NioChannelOption<Object>((SocketOption<Object>)opt));
            }
            return extraOpts.toArray(new ChannelOption[0]);
        }
        final ChannelOption<?>[] extraOpts2 = new ChannelOption[supportedOpts.size()];
        int i = 0;
        for (final SocketOption<?> opt2 : supportedOpts) {
            extraOpts2[i++] = new NioChannelOption<Object>(opt2);
        }
        return extraOpts2;
    }
}
