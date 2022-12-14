package io.netty.channel;

import io.netty.util.IntSupplier;

public interface SelectStrategy
{
    public static final int SELECT = -1;
    public static final int CONTINUE = -2;
    public static final int BUSY_WAIT = -3;
    
    int calculateStrategy(final IntSupplier p0, final boolean p1) throws Exception;
}
