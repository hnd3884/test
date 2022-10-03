package com.sun.xml.internal.ws.client;

import java.util.Map;
import com.sun.xml.internal.ws.util.CompletedFuture;
import javax.xml.ws.WebServiceException;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.Cancelable;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import java.util.concurrent.FutureTask;

public final class AsyncResponseImpl<T> extends FutureTask<T> implements Response<T>, ResponseContextReceiver
{
    private final AsyncHandler<T> handler;
    private ResponseContext responseContext;
    private final Runnable callable;
    private Cancelable cancelable;
    
    public AsyncResponseImpl(final Runnable runnable, @Nullable final AsyncHandler<T> handler) {
        super(runnable, null);
        this.callable = runnable;
        this.handler = handler;
    }
    
    @Override
    public void run() {
        try {
            this.callable.run();
        }
        catch (final WebServiceException e) {
            this.set(null, e);
        }
        catch (final Throwable e2) {
            this.set(null, new WebServiceException(e2));
        }
    }
    
    @Override
    public ResponseContext getContext() {
        return this.responseContext;
    }
    
    @Override
    public void setResponseContext(final ResponseContext rc) {
        this.responseContext = rc;
    }
    
    public void set(final T v, final Throwable t) {
        if (this.handler != null) {
            try {
                class CallbackFuture<T> extends CompletedFuture<T> implements Response<T>
                {
                    public CallbackFuture(final T v, final Throwable t) {
                        super(v, t);
                    }
                    
                    @Override
                    public Map<String, Object> getContext() {
                        return AsyncResponseImpl.this.getContext();
                    }
                }
                this.handler.handleResponse(new CallbackFuture<T>(v, t));
            }
            catch (final Throwable e) {
                super.setException(e);
                return;
            }
        }
        if (t != null) {
            super.setException(t);
        }
        else {
            super.set(v);
        }
    }
    
    public void setCancelable(final Cancelable cancelable) {
        this.cancelable = cancelable;
    }
    
    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        if (this.cancelable != null) {
            this.cancelable.cancel(mayInterruptIfRunning);
        }
        return super.cancel(mayInterruptIfRunning);
    }
}
