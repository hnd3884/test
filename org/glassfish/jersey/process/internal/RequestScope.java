package org.glassfish.jersey.process.internal;

import org.glassfish.jersey.internal.BootstrapBag;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.BootstrapConfigurator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.jersey.internal.util.Producer;
import java.util.concurrent.Callable;
import org.glassfish.jersey.internal.Errors;
import org.glassfish.jersey.internal.guava.Preconditions;
import org.glassfish.jersey.internal.util.ExtendedLogger;

public abstract class RequestScope
{
    private static final ExtendedLogger logger;
    private final ThreadLocal<RequestContext> currentRequestContext;
    private volatile boolean isActive;
    
    public RequestScope() {
        this.currentRequestContext = new ThreadLocal<RequestContext>();
        this.isActive = true;
    }
    
    public boolean isActive() {
        return this.isActive;
    }
    
    public void shutdown() {
        this.isActive = false;
    }
    
    public RequestContext referenceCurrent() throws IllegalStateException {
        return this.current().getReference();
    }
    
    public RequestContext current() {
        Preconditions.checkState(this.isActive, (Object)"Request scope has been already shut down.");
        final RequestContext scopeInstance = this.currentRequestContext.get();
        Preconditions.checkState(scopeInstance != null, (Object)"Not inside a request scope.");
        return scopeInstance;
    }
    
    private RequestContext retrieveCurrent() {
        Preconditions.checkState(this.isActive, (Object)"Request scope has been already shut down.");
        return this.currentRequestContext.get();
    }
    
    public RequestContext suspendCurrent() {
        final RequestContext context = this.retrieveCurrent();
        if (context == null) {
            return null;
        }
        try {
            final RequestContext referencedContext = context.getReference();
            this.suspend(referencedContext);
            return referencedContext;
        }
        finally {
            RequestScope.logger.debugLog("Returned a new reference of the request scope context {0}", context);
        }
    }
    
    protected void suspend(final RequestContext context) {
    }
    
    public abstract RequestContext createContext();
    
    protected void activate(final RequestContext context, final RequestContext oldContext) {
        Preconditions.checkState(this.isActive, (Object)"Request scope has been already shut down.");
        this.currentRequestContext.set(context);
    }
    
    protected void resume(final RequestContext context) {
        this.currentRequestContext.set(context);
    }
    
    protected void release(final RequestContext context) {
        context.release();
    }
    
    public void runInScope(final RequestContext context, final Runnable task) {
        final RequestContext oldContext = this.retrieveCurrent();
        try {
            this.activate(context.getReference(), oldContext);
            Errors.process(task);
        }
        finally {
            this.release(context);
            this.resume(oldContext);
        }
    }
    
    public void runInScope(final Runnable task) {
        final RequestContext oldContext = this.retrieveCurrent();
        final RequestContext context = this.createContext();
        try {
            this.activate(context, oldContext);
            Errors.process(task);
        }
        finally {
            this.release(context);
            this.resume(oldContext);
        }
    }
    
    public <T> T runInScope(final RequestContext context, final Callable<T> task) throws Exception {
        final RequestContext oldContext = this.retrieveCurrent();
        try {
            this.activate(context.getReference(), oldContext);
            return Errors.process(task);
        }
        finally {
            this.release(context);
            this.resume(oldContext);
        }
    }
    
    public <T> T runInScope(final Callable<T> task) throws Exception {
        final RequestContext oldContext = this.retrieveCurrent();
        final RequestContext context = this.createContext();
        try {
            this.activate(context, oldContext);
            return Errors.process(task);
        }
        finally {
            this.release(context);
            this.resume(oldContext);
        }
    }
    
    public <T> T runInScope(final RequestContext context, final Producer<T> task) {
        final RequestContext oldContext = this.retrieveCurrent();
        try {
            this.activate(context.getReference(), oldContext);
            return Errors.process(task);
        }
        finally {
            this.release(context);
            this.resume(oldContext);
        }
    }
    
    public <T> T runInScope(final Producer<T> task) {
        final RequestContext oldContext = this.retrieveCurrent();
        final RequestContext context = this.createContext();
        try {
            this.activate(context, oldContext);
            return Errors.process(task);
        }
        finally {
            this.release(context);
            this.resume(oldContext);
        }
    }
    
    static {
        logger = new ExtendedLogger(Logger.getLogger(RequestScope.class.getName()), Level.FINEST);
    }
    
    public static class RequestScopeConfigurator implements BootstrapConfigurator
    {
        @Override
        public void init(final InjectionManager injectionManagerFactory, final BootstrapBag bootstrapBag) {
        }
        
        @Override
        public void postInit(final InjectionManager injectionManager, final BootstrapBag bootstrapBag) {
            final RequestScope requestScope = injectionManager.getInstance(RequestScope.class);
            bootstrapBag.setRequestScope(requestScope);
        }
    }
}
