package io.netty.handler.codec.http2;

public interface Http2SettingsReceivedConsumer
{
    void consumeReceivedSettings(final Http2Settings p0);
}
