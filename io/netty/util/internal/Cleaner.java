package io.netty.util.internal;

import java.nio.ByteBuffer;

interface Cleaner
{
    void freeDirectBuffer(final ByteBuffer p0);
}
