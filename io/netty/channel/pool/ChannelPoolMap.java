package io.netty.channel.pool;

public interface ChannelPoolMap<K, P extends ChannelPool>
{
    P get(final K p0);
    
    boolean contains(final K p0);
}
