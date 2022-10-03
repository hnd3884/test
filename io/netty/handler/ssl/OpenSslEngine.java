package io.netty.handler.ssl;

import io.netty.util.ReferenceCounted;
import io.netty.buffer.ByteBufAllocator;

public final class OpenSslEngine extends ReferenceCountedOpenSslEngine
{
    OpenSslEngine(final OpenSslContext context, final ByteBufAllocator alloc, final String peerHost, final int peerPort, final boolean jdkCompatibilityMode) {
        super(context, alloc, peerHost, peerPort, jdkCompatibilityMode, false);
    }
    
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        OpenSsl.releaseIfNeeded(this);
    }
}
