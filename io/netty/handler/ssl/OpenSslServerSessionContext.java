package io.netty.handler.ssl;

import java.util.concurrent.locks.Lock;
import io.netty.internal.tcnative.SSLContext;
import io.netty.internal.tcnative.SSL;

public final class OpenSslServerSessionContext extends OpenSslSessionContext
{
    OpenSslServerSessionContext(final ReferenceCountedOpenSslContext context, final OpenSslKeyMaterialProvider provider) {
        super(context, provider, SSL.SSL_SESS_CACHE_SERVER, new OpenSslSessionCache(context.engineMap));
    }
    
    public boolean setSessionIdContext(final byte[] sidCtx) {
        final Lock writerLock = this.context.ctxLock.writeLock();
        writerLock.lock();
        try {
            return SSLContext.setSessionIdContext(this.context.ctx, sidCtx);
        }
        finally {
            writerLock.unlock();
        }
    }
}
