package org.apache.http.client.methods;

import org.apache.http.params.HttpParams;
import org.apache.http.client.utils.CloneUtils;
import org.apache.http.message.HeaderGroup;
import java.io.IOException;
import org.apache.http.conn.ConnectionReleaseTrigger;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.concurrent.Cancellable;
import java.util.concurrent.atomic.AtomicMarkableReference;
import org.apache.http.HttpRequest;
import org.apache.http.message.AbstractHttpMessage;

public abstract class AbstractExecutionAwareRequest extends AbstractHttpMessage implements HttpExecutionAware, AbortableHttpRequest, Cloneable, HttpRequest
{
    private final AtomicMarkableReference<Cancellable> cancellableRef;
    
    protected AbstractExecutionAwareRequest() {
        this.cancellableRef = new AtomicMarkableReference<Cancellable>(null, false);
    }
    
    @Deprecated
    public void setConnectionRequest(final ClientConnectionRequest connRequest) {
        this.setCancellable((Cancellable)new Cancellable() {
            public boolean cancel() {
                connRequest.abortRequest();
                return true;
            }
        });
    }
    
    @Deprecated
    public void setReleaseTrigger(final ConnectionReleaseTrigger releaseTrigger) {
        this.setCancellable((Cancellable)new Cancellable() {
            public boolean cancel() {
                try {
                    releaseTrigger.abortConnection();
                    return true;
                }
                catch (final IOException ex) {
                    return false;
                }
            }
        });
    }
    
    public void abort() {
        while (!this.cancellableRef.isMarked()) {
            final Cancellable actualCancellable = this.cancellableRef.getReference();
            if (this.cancellableRef.compareAndSet(actualCancellable, actualCancellable, false, true) && actualCancellable != null) {
                actualCancellable.cancel();
            }
        }
    }
    
    public boolean isAborted() {
        return this.cancellableRef.isMarked();
    }
    
    public void setCancellable(final Cancellable cancellable) {
        final Cancellable actualCancellable = this.cancellableRef.getReference();
        if (!this.cancellableRef.compareAndSet(actualCancellable, cancellable, false, false)) {
            cancellable.cancel();
        }
    }
    
    public Object clone() throws CloneNotSupportedException {
        final AbstractExecutionAwareRequest clone = (AbstractExecutionAwareRequest)super.clone();
        clone.headergroup = CloneUtils.cloneObject(this.headergroup);
        clone.params = CloneUtils.cloneObject(this.params);
        return clone;
    }
    
    @Deprecated
    public void completed() {
        this.cancellableRef.set(null, false);
    }
    
    public void reset() {
        boolean marked;
        Cancellable actualCancellable;
        do {
            marked = this.cancellableRef.isMarked();
            actualCancellable = this.cancellableRef.getReference();
            if (actualCancellable != null) {
                actualCancellable.cancel();
            }
        } while (!this.cancellableRef.compareAndSet(actualCancellable, null, marked, false));
    }
}
