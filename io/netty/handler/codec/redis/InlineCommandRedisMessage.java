package io.netty.handler.codec.redis;

public final class InlineCommandRedisMessage extends AbstractStringRedisMessage
{
    public InlineCommandRedisMessage(final String content) {
        super(content);
    }
}
