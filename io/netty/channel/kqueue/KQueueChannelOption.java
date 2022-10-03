package io.netty.channel.kqueue;

import io.netty.channel.ChannelOption;
import io.netty.channel.unix.UnixChannelOption;

public final class KQueueChannelOption<T> extends UnixChannelOption<T>
{
    public static final ChannelOption<Integer> SO_SNDLOWAT;
    public static final ChannelOption<Boolean> TCP_NOPUSH;
    public static final ChannelOption<AcceptFilter> SO_ACCEPTFILTER;
    public static final ChannelOption<Boolean> RCV_ALLOC_TRANSPORT_PROVIDES_GUESS;
    
    private KQueueChannelOption() {
    }
    
    static {
        SO_SNDLOWAT = ChannelOption.valueOf(KQueueChannelOption.class, "SO_SNDLOWAT");
        TCP_NOPUSH = ChannelOption.valueOf(KQueueChannelOption.class, "TCP_NOPUSH");
        SO_ACCEPTFILTER = ChannelOption.valueOf(KQueueChannelOption.class, "SO_ACCEPTFILTER");
        RCV_ALLOC_TRANSPORT_PROVIDES_GUESS = ChannelOption.valueOf(KQueueChannelOption.class, "RCV_ALLOC_TRANSPORT_PROVIDES_GUESS");
    }
}
