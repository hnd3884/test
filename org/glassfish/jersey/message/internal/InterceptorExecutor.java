package org.glassfish.jersey.message.internal;

import java.util.Collection;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Type;
import java.lang.annotation.Annotation;
import org.glassfish.jersey.internal.PropertiesDelegate;
import javax.ws.rs.ext.InterceptorContext;

abstract class InterceptorExecutor<T> implements InterceptorContext, PropertiesDelegate
{
    private final PropertiesDelegate propertiesDelegate;
    private Annotation[] annotations;
    private Class<?> type;
    private Type genericType;
    private MediaType mediaType;
    private final TracingLogger tracingLogger;
    private InterceptorTimestampPair<T> lastTracedInterceptor;
    
    public InterceptorExecutor(final Class<?> rawType, final Type type, final Annotation[] annotations, final MediaType mediaType, final PropertiesDelegate propertiesDelegate) {
        this.type = rawType;
        this.genericType = type;
        this.annotations = annotations;
        this.mediaType = mediaType;
        this.propertiesDelegate = propertiesDelegate;
        this.tracingLogger = TracingLogger.getInstance(propertiesDelegate);
    }
    
    public Object getProperty(final String name) {
        return this.propertiesDelegate.getProperty(name);
    }
    
    public Collection<String> getPropertyNames() {
        return this.propertiesDelegate.getPropertyNames();
    }
    
    public void setProperty(final String name, final Object object) {
        this.propertiesDelegate.setProperty(name, object);
    }
    
    public void removeProperty(final String name) {
        this.propertiesDelegate.removeProperty(name);
    }
    
    protected final TracingLogger getTracingLogger() {
        return this.tracingLogger;
    }
    
    protected final void traceBefore(final T interceptor, final TracingLogger.Event event) {
        if (this.tracingLogger.isLogEnabled(event)) {
            if (this.lastTracedInterceptor != null && interceptor != null) {
                this.tracingLogger.logDuration(event, ((InterceptorTimestampPair<Object>)this.lastTracedInterceptor).getTimestamp(), ((InterceptorTimestampPair<Object>)this.lastTracedInterceptor).getInterceptor());
            }
            this.lastTracedInterceptor = new InterceptorTimestampPair<T>((Object)interceptor, System.nanoTime());
        }
    }
    
    protected final void traceAfter(final T interceptor, final TracingLogger.Event event) {
        if (this.tracingLogger.isLogEnabled(event)) {
            if (this.lastTracedInterceptor != null && ((InterceptorTimestampPair<Object>)this.lastTracedInterceptor).getInterceptor() != null) {
                this.tracingLogger.logDuration(event, ((InterceptorTimestampPair<Object>)this.lastTracedInterceptor).getTimestamp(), interceptor);
            }
            this.lastTracedInterceptor = new InterceptorTimestampPair<T>((Object)interceptor, System.nanoTime());
        }
    }
    
    protected final void clearLastTracedInterceptor() {
        this.lastTracedInterceptor = null;
    }
    
    public Annotation[] getAnnotations() {
        return this.annotations;
    }
    
    public void setAnnotations(final Annotation[] annotations) {
        if (annotations == null) {
            throw new NullPointerException("Annotations must not be null.");
        }
        this.annotations = annotations;
    }
    
    public Class getType() {
        return this.type;
    }
    
    public void setType(final Class type) {
        this.type = type;
    }
    
    public Type getGenericType() {
        return this.genericType;
    }
    
    public void setGenericType(final Type genericType) {
        this.genericType = genericType;
    }
    
    public MediaType getMediaType() {
        return this.mediaType;
    }
    
    public void setMediaType(final MediaType mediaType) {
        this.mediaType = mediaType;
    }
    
    private static class InterceptorTimestampPair<T>
    {
        private final T interceptor;
        private final long timestamp;
        
        private InterceptorTimestampPair(final T interceptor, final long timestamp) {
            this.interceptor = interceptor;
            this.timestamp = timestamp;
        }
        
        private T getInterceptor() {
            return this.interceptor;
        }
        
        private long getTimestamp() {
            return this.timestamp;
        }
    }
}
