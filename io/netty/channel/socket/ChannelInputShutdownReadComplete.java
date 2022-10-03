package io.netty.channel.socket;

public final class ChannelInputShutdownReadComplete
{
    public static final ChannelInputShutdownReadComplete INSTANCE;
    
    private ChannelInputShutdownReadComplete() {
    }
    
    static {
        INSTANCE = new ChannelInputShutdownReadComplete();
    }
}
