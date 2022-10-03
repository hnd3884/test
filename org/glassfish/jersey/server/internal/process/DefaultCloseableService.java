package org.glassfish.jersey.server.internal.process;

import java.util.Iterator;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import java.util.logging.Level;
import java.util.Map;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.io.Closeable;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import org.glassfish.jersey.server.CloseableService;

class DefaultCloseableService implements CloseableService
{
    private static final Logger LOGGER;
    private final AtomicBoolean closed;
    private final Set<Closeable> closeables;
    
    DefaultCloseableService() {
        this.closed = new AtomicBoolean(false);
        this.closeables = Collections.newSetFromMap(new IdentityHashMap<Closeable, Boolean>());
    }
    
    @Override
    public boolean add(final Closeable closeable) {
        return !this.closed.get() && this.closeables.add(closeable);
    }
    
    @Override
    public void close() {
        if (this.closed.compareAndSet(false, true)) {
            for (final Closeable closeable : this.closeables) {
                try {
                    closeable.close();
                }
                catch (final Exception ex) {
                    DefaultCloseableService.LOGGER.log(Level.WARNING, LocalizationMessages.CLOSEABLE_UNABLE_TO_CLOSE(closeable.getClass().getName()), ex);
                }
            }
        }
    }
    
    static {
        LOGGER = Logger.getLogger(DefaultCloseableService.class.getName());
    }
}
