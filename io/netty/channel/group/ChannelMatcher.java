package io.netty.channel.group;

import io.netty.channel.Channel;

public interface ChannelMatcher
{
    boolean matches(final Channel p0);
}
