package org.glassfish.jersey.inject.hk2;

import java.util.function.Consumer;
import java.util.Collection;
import java.util.HashSet;
import org.glassfish.jersey.internal.guava.Preconditions;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.atomic.AtomicInteger;
import org.glassfish.jersey.internal.inject.ForeignDescriptor;
import java.util.Map;
import org.glassfish.jersey.internal.util.LazyUid;
import org.glassfish.jersey.internal.util.ExtendedLogger;
import org.glassfish.jersey.process.internal.RequestContext;
import org.glassfish.jersey.process.internal.RequestScope;

public class Hk2RequestScope extends RequestScope
{
    public RequestContext createContext() {
        return (RequestContext)new Instance();
    }
    
    public static final class Instance implements RequestContext
    {
        private final ExtendedLogger logger;
        private final LazyUid id;
        private final Map<ForeignDescriptor, Object> store;
        private final AtomicInteger referenceCounter;
        
        private Instance() {
            this.logger = new ExtendedLogger(Logger.getLogger(Instance.class.getName()), Level.FINEST);
            this.id = new LazyUid();
            this.store = new HashMap<ForeignDescriptor, Object>();
            this.referenceCounter = new AtomicInteger(1);
        }
        
        public Instance getReference() {
            this.referenceCounter.incrementAndGet();
            return this;
        }
        
        public <T> T get(final ForeignDescriptor descriptor) {
            return (T)this.store.get(descriptor);
        }
        
        public <T> T put(final ForeignDescriptor descriptor, final T value) {
            Preconditions.checkState(!this.store.containsKey(descriptor), "An instance for the descriptor %s was already seeded in this scope. Old instance: %s New instance: %s", new Object[] { descriptor, this.store.get(descriptor), value });
            return (T)this.store.put(descriptor, value);
        }
        
        public <T> void remove(final ForeignDescriptor descriptor) {
            final T removed = (T)this.store.remove(descriptor);
            if (removed != null) {
                descriptor.dispose((Object)removed);
            }
        }
        
        public boolean contains(final ForeignDescriptor provider) {
            return this.store.containsKey(provider);
        }
        
        public void release() {
            if (this.referenceCounter.decrementAndGet() < 1) {
                try {
                    new HashSet(this.store.keySet()).forEach((Consumer)this::remove);
                }
                finally {
                    this.logger.debugLog("Released scope instance {0}", new Object[] { this });
                }
            }
        }
        
        @Override
        public String toString() {
            return "Instance{id=" + this.id + ", referenceCounter=" + this.referenceCounter + ", store size=" + this.store.size() + '}';
        }
    }
}
