package io.netty.buffer;

import io.netty.util.ReferenceCounted;

public interface ByteBufHolder extends ReferenceCounted
{
    ByteBuf content();
    
    ByteBufHolder copy();
    
    ByteBufHolder duplicate();
    
    ByteBufHolder retainedDuplicate();
    
    ByteBufHolder replace(final ByteBuf p0);
    
    ByteBufHolder retain();
    
    ByteBufHolder retain(final int p0);
    
    ByteBufHolder touch();
    
    ByteBufHolder touch(final Object p0);
}
