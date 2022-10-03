package org.glassfish.jersey.server;

import java.io.OutputStream;
import org.glassfish.jersey.server.internal.process.MappableException;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.io.IOException;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import javax.inject.Provider;
import java.lang.reflect.Type;
import java.util.concurrent.LinkedBlockingDeque;
import javax.ws.rs.container.ConnectionCallback;
import org.glassfish.jersey.process.internal.RequestContext;
import org.glassfish.jersey.process.internal.RequestScope;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.BlockingDeque;
import java.io.Closeable;
import javax.ws.rs.core.GenericType;

public class ChunkedOutput<T> extends GenericType<T> implements Closeable
{
    private static final byte[] ZERO_LENGTH_DELIMITER;
    private final BlockingDeque<T> queue;
    private final byte[] chunkDelimiter;
    private final AtomicBoolean resumed;
    private boolean flushing;
    private volatile boolean closed;
    private volatile AsyncContext asyncContext;
    private volatile RequestScope requestScope;
    private volatile RequestContext requestScopeContext;
    private volatile ContainerRequest requestContext;
    private volatile ContainerResponse responseContext;
    private volatile ConnectionCallback connectionCallback;
    
    protected ChunkedOutput() {
        this.queue = new LinkedBlockingDeque<T>();
        this.resumed = new AtomicBoolean(false);
        this.flushing = false;
        this.closed = false;
        this.chunkDelimiter = ChunkedOutput.ZERO_LENGTH_DELIMITER;
    }
    
    public ChunkedOutput(final Type chunkType) {
        super(chunkType);
        this.queue = new LinkedBlockingDeque<T>();
        this.resumed = new AtomicBoolean(false);
        this.flushing = false;
        this.closed = false;
        this.chunkDelimiter = ChunkedOutput.ZERO_LENGTH_DELIMITER;
    }
    
    protected ChunkedOutput(final byte[] chunkDelimiter) {
        this.queue = new LinkedBlockingDeque<T>();
        this.resumed = new AtomicBoolean(false);
        this.flushing = false;
        this.closed = false;
        if (chunkDelimiter.length > 0) {
            System.arraycopy(chunkDelimiter, 0, this.chunkDelimiter = new byte[chunkDelimiter.length], 0, chunkDelimiter.length);
        }
        else {
            this.chunkDelimiter = ChunkedOutput.ZERO_LENGTH_DELIMITER;
        }
    }
    
    protected ChunkedOutput(final byte[] chunkDelimiter, final Provider<AsyncContext> asyncContextProvider) {
        this.queue = new LinkedBlockingDeque<T>();
        this.resumed = new AtomicBoolean(false);
        this.flushing = false;
        this.closed = false;
        if (chunkDelimiter.length > 0) {
            System.arraycopy(chunkDelimiter, 0, this.chunkDelimiter = new byte[chunkDelimiter.length], 0, chunkDelimiter.length);
        }
        else {
            this.chunkDelimiter = ChunkedOutput.ZERO_LENGTH_DELIMITER;
        }
        this.asyncContext = ((asyncContextProvider == null) ? null : ((AsyncContext)asyncContextProvider.get()));
    }
    
    public ChunkedOutput(final Type chunkType, final byte[] chunkDelimiter) {
        super(chunkType);
        this.queue = new LinkedBlockingDeque<T>();
        this.resumed = new AtomicBoolean(false);
        this.flushing = false;
        this.closed = false;
        if (chunkDelimiter.length > 0) {
            System.arraycopy(chunkDelimiter, 0, this.chunkDelimiter = new byte[chunkDelimiter.length], 0, chunkDelimiter.length);
        }
        else {
            this.chunkDelimiter = ChunkedOutput.ZERO_LENGTH_DELIMITER;
        }
    }
    
    protected ChunkedOutput(final String chunkDelimiter) {
        this.queue = new LinkedBlockingDeque<T>();
        this.resumed = new AtomicBoolean(false);
        this.flushing = false;
        this.closed = false;
        if (chunkDelimiter.isEmpty()) {
            this.chunkDelimiter = ChunkedOutput.ZERO_LENGTH_DELIMITER;
        }
        else {
            this.chunkDelimiter = chunkDelimiter.getBytes();
        }
    }
    
    public ChunkedOutput(final Type chunkType, final String chunkDelimiter) {
        super(chunkType);
        this.queue = new LinkedBlockingDeque<T>();
        this.resumed = new AtomicBoolean(false);
        this.flushing = false;
        this.closed = false;
        if (chunkDelimiter.isEmpty()) {
            this.chunkDelimiter = ChunkedOutput.ZERO_LENGTH_DELIMITER;
        }
        else {
            this.chunkDelimiter = chunkDelimiter.getBytes();
        }
    }
    
    public void write(final T chunk) throws IOException {
        if (this.closed) {
            throw new IOException(LocalizationMessages.CHUNKED_OUTPUT_CLOSED());
        }
        if (chunk != null) {
            this.queue.add(chunk);
        }
        this.flushQueue();
    }
    
    protected void flushQueue() throws IOException {
        if (this.resumed.compareAndSet(false, true) && this.asyncContext != null) {
            this.asyncContext.resume((Object)this);
        }
        if (this.requestScopeContext == null || this.requestContext == null || this.responseContext == null) {
            return;
        }
        Exception ex = null;
        try {
            this.requestScope.runInScope(this.requestScopeContext, (Callable)new Callable<Void>() {
                @Override
                public Void call() throws IOException {
                    T t;
                    synchronized (ChunkedOutput.this) {
                        if (ChunkedOutput.this.flushing) {
                            return null;
                        }
                        final boolean shouldClose = ChunkedOutput.this.closed;
                        t = ChunkedOutput.this.queue.poll();
                        if (t != null || shouldClose) {
                            ChunkedOutput.this.flushing = true;
                        }
                    }
                    while (t != null) {
                        try {
                            final OutputStream origStream = ChunkedOutput.this.responseContext.getEntityStream();
                            final OutputStream writtenStream = ChunkedOutput.this.requestContext.getWorkers().writeTo((Object)t, (Class)t.getClass(), ChunkedOutput.this.getType(), ChunkedOutput.this.responseContext.getEntityAnnotations(), ChunkedOutput.this.responseContext.getMediaType(), (MultivaluedMap)ChunkedOutput.this.responseContext.getHeaders(), ChunkedOutput.this.requestContext.getPropertiesDelegate(), origStream, (Iterable)Collections.emptyList());
                            if (ChunkedOutput.this.chunkDelimiter != ChunkedOutput.ZERO_LENGTH_DELIMITER) {
                                writtenStream.write(ChunkedOutput.this.chunkDelimiter);
                            }
                            writtenStream.flush();
                            if (origStream != writtenStream) {
                                ChunkedOutput.this.responseContext.setEntityStream(writtenStream);
                            }
                        }
                        catch (final IOException ioe) {
                            ChunkedOutput.this.connectionCallback.onDisconnect((AsyncResponse)ChunkedOutput.this.asyncContext);
                            throw ioe;
                        }
                        catch (final MappableException mpe) {
                            if (mpe.getCause() instanceof IOException) {
                                ChunkedOutput.this.connectionCallback.onDisconnect((AsyncResponse)ChunkedOutput.this.asyncContext);
                            }
                            throw mpe;
                        }
                        t = ChunkedOutput.this.queue.poll();
                        if (t == null) {
                            synchronized (ChunkedOutput.this) {
                                final boolean shouldClose = ChunkedOutput.this.closed;
                                t = ChunkedOutput.this.queue.poll();
                                if (t == null) {
                                    ChunkedOutput.this.responseContext.commitStream();
                                    ChunkedOutput.this.flushing = shouldClose;
                                    break;
                                }
                                continue;
                            }
                        }
                    }
                    return null;
                }
            });
        }
        catch (final Exception e) {
            this.closed = true;
            ex = e;
        }
        finally {
            if (this.closed) {
                try {
                    this.responseContext.close();
                }
                catch (final Exception e2) {
                    ex = ((ex == null) ? e2 : ex);
                }
                this.requestScopeContext.release();
                if (ex instanceof IOException) {
                    throw (IOException)ex;
                }
                if (ex instanceof RuntimeException) {
                    throw (RuntimeException)ex;
                }
            }
        }
    }
    
    public void close() throws IOException {
        this.closed = true;
        this.flushQueue();
    }
    
    public boolean isClosed() {
        return this.closed;
    }
    
    public boolean equals(final Object obj) {
        return this == obj;
    }
    
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.queue.hashCode();
        return result;
    }
    
    public String toString() {
        return "ChunkedOutput<" + this.getType() + ">";
    }
    
    void setContext(final RequestScope requestScope, final RequestContext requestScopeContext, final ContainerRequest requestContext, final ContainerResponse responseContext, final ConnectionCallback connectionCallbackRunner) throws IOException {
        this.requestScope = requestScope;
        this.requestScopeContext = requestScopeContext;
        this.requestContext = requestContext;
        this.responseContext = responseContext;
        this.connectionCallback = connectionCallbackRunner;
        this.flushQueue();
    }
    
    static {
        ZERO_LENGTH_DELIMITER = new byte[0];
    }
}
