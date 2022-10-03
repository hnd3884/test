package org.glassfish.hk2.internal;

import org.glassfish.hk2.utilities.reflection.Logger;
import java.util.HashMap;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ActiveDescriptor;
import java.lang.annotation.Annotation;
import org.glassfish.hk2.utilities.general.Hk2ThreadLocal;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.Visibility;
import javax.inject.Singleton;
import org.glassfish.hk2.api.PerThread;
import org.glassfish.hk2.api.Context;

@Singleton
@Visibility(DescriptorVisibility.LOCAL)
public class PerThreadContext implements Context<PerThread>
{
    private static final boolean LOG_THREAD_DESTRUCTION;
    private final Hk2ThreadLocal<PerContextThreadWrapper> threadMap;
    
    public PerThreadContext() {
        this.threadMap = new Hk2ThreadLocal<PerContextThreadWrapper>() {
            public PerContextThreadWrapper initialValue() {
                return new PerContextThreadWrapper();
            }
        };
    }
    
    @Override
    public Class<? extends Annotation> getScope() {
        return PerThread.class;
    }
    
    @Override
    public <U> U findOrCreate(final ActiveDescriptor<U> activeDescriptor, final ServiceHandle<?> root) {
        U retVal = (U)((PerContextThreadWrapper)this.threadMap.get()).get(activeDescriptor);
        if (retVal == null) {
            retVal = activeDescriptor.create(root);
            ((PerContextThreadWrapper)this.threadMap.get()).put(activeDescriptor, retVal);
        }
        return retVal;
    }
    
    @Override
    public boolean containsKey(final ActiveDescriptor<?> descriptor) {
        return ((PerContextThreadWrapper)this.threadMap.get()).has(descriptor);
    }
    
    @Override
    public boolean isActive() {
        return true;
    }
    
    @Override
    public boolean supportsNullCreation() {
        return false;
    }
    
    @Override
    public void shutdown() {
        this.threadMap.removeAll();
    }
    
    @Override
    public void destroyOne(final ActiveDescriptor<?> descriptor) {
    }
    
    static {
        LOG_THREAD_DESTRUCTION = AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return Boolean.parseBoolean(System.getProperty("org.hk2.debug.perthreadcontext.log", "false"));
            }
        });
    }
    
    private static class PerContextThreadWrapper
    {
        private final HashMap<ActiveDescriptor<?>, Object> instances;
        private final long id;
        
        private PerContextThreadWrapper() {
            this.instances = new HashMap<ActiveDescriptor<?>, Object>();
            this.id = Thread.currentThread().getId();
        }
        
        public boolean has(final ActiveDescriptor<?> d) {
            return this.instances.containsKey(d);
        }
        
        public Object get(final ActiveDescriptor<?> d) {
            return this.instances.get(d);
        }
        
        public void put(final ActiveDescriptor<?> d, final Object v) {
            this.instances.put(d, v);
        }
        
        public void finalize() throws Throwable {
            this.instances.clear();
            if (PerThreadContext.LOG_THREAD_DESTRUCTION) {
                Logger.getLogger().debug("Removing PerThreadContext data for thread " + this.id);
            }
        }
    }
}
