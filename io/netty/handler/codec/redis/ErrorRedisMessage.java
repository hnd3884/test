package io.netty.handler.codec.redis;

public final class ErrorRedisMessage extends AbstractStringRedisMessage
{
    public ErrorRedisMessage(final String content) {
        super(content);
    }
}
