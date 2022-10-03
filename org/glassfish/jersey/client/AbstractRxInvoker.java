package org.glassfish.jersey.client;

import javax.ws.rs.core.Response;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import java.util.concurrent.ExecutorService;
import javax.ws.rs.client.SyncInvoker;
import javax.ws.rs.client.RxInvoker;

public abstract class AbstractRxInvoker<T> implements RxInvoker<T>
{
    private final SyncInvoker syncInvoker;
    private final ExecutorService executorService;
    
    public AbstractRxInvoker(final SyncInvoker syncInvoker, final ExecutorService executor) {
        if (syncInvoker == null) {
            throw new IllegalArgumentException("Invocation builder cannot be null.");
        }
        this.syncInvoker = syncInvoker;
        this.executorService = executor;
    }
    
    protected SyncInvoker getSyncInvoker() {
        return this.syncInvoker;
    }
    
    protected ExecutorService getExecutorService() {
        return this.executorService;
    }
    
    public T get() {
        return this.method("GET");
    }
    
    public <R> T get(final Class<R> responseType) {
        return this.method("GET", responseType);
    }
    
    public <R> T get(final GenericType<R> responseType) {
        return this.method("GET", responseType);
    }
    
    public T put(final Entity<?> entity) {
        return this.method("PUT", entity);
    }
    
    public <R> T put(final Entity<?> entity, final Class<R> clazz) {
        return (T)this.method("PUT", (Entity)entity, (Class)clazz);
    }
    
    public <R> T put(final Entity<?> entity, final GenericType<R> type) {
        return (T)this.method("PUT", (Entity)entity, (GenericType)type);
    }
    
    public T post(final Entity<?> entity) {
        return this.method("POST", entity);
    }
    
    public <R> T post(final Entity<?> entity, final Class<R> clazz) {
        return (T)this.method("POST", (Entity)entity, (Class)clazz);
    }
    
    public <R> T post(final Entity<?> entity, final GenericType<R> type) {
        return (T)this.method("POST", (Entity)entity, (GenericType)type);
    }
    
    public T delete() {
        return this.method("DELETE");
    }
    
    public <R> T delete(final Class<R> responseType) {
        return this.method("DELETE", responseType);
    }
    
    public <R> T delete(final GenericType<R> responseType) {
        return this.method("DELETE", responseType);
    }
    
    public T head() {
        return this.method("HEAD");
    }
    
    public T options() {
        return this.method("OPTIONS");
    }
    
    public <R> T options(final Class<R> responseType) {
        return this.method("OPTIONS", responseType);
    }
    
    public <R> T options(final GenericType<R> responseType) {
        return this.method("OPTIONS", responseType);
    }
    
    public T trace() {
        return this.method("TRACE");
    }
    
    public <R> T trace(final Class<R> responseType) {
        return this.method("TRACE", responseType);
    }
    
    public <R> T trace(final GenericType<R> responseType) {
        return this.method("TRACE", responseType);
    }
    
    public T method(final String name) {
        return this.method(name, Response.class);
    }
    
    public <R> T method(final String name, final Class<R> responseType) {
        return (T)this.method(name, (Entity)null, (Class)responseType);
    }
    
    public <R> T method(final String name, final GenericType<R> responseType) {
        return (T)this.method(name, (Entity)null, (GenericType)responseType);
    }
    
    public T method(final String name, final Entity<?> entity) {
        return (T)this.method(name, (Entity)entity, (Class)Response.class);
    }
}
