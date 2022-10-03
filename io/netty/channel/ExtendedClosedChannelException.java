package io.netty.channel;

import java.nio.channels.ClosedChannelException;

final class ExtendedClosedChannelException extends ClosedChannelException
{
    ExtendedClosedChannelException(final Throwable cause) {
        if (cause != null) {
            this.initCause(cause);
        }
    }
    
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
