package io.netty.channel.socket;

import java.io.IOException;

public final class ChannelOutputShutdownException extends IOException
{
    private static final long serialVersionUID = 6712549938359321378L;
    
    public ChannelOutputShutdownException(final String msg) {
        super(msg);
    }
    
    public ChannelOutputShutdownException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
