package io.netty.channel.socket;

public final class ChannelOutputShutdownEvent
{
    public static final ChannelOutputShutdownEvent INSTANCE;
    
    private ChannelOutputShutdownEvent() {
    }
    
    static {
        INSTANCE = new ChannelOutputShutdownEvent();
    }
}
