package org.glassfish.jersey.server;

import org.glassfish.jersey.server.internal.LocalizationMessages;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Iterator;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class Broadcaster<T> implements BroadcasterListener<T>
{
    private final CopyOnWriteArrayList<BroadcasterListener<T>> listeners;
    private final ConcurrentLinkedQueue<ChunkedOutput<T>> chunkedOutputs;
    
    public Broadcaster() {
        this(Broadcaster.class);
    }
    
    protected Broadcaster(final Class<? extends Broadcaster> subclass) {
        this.listeners = new CopyOnWriteArrayList<BroadcasterListener<T>>();
        this.chunkedOutputs = new ConcurrentLinkedQueue<ChunkedOutput<T>>();
        if (subclass != this.getClass()) {
            this.listeners.add(this);
        }
    }
    
    public <OUT extends ChunkedOutput<T>> boolean add(final OUT chunkedOutput) {
        return this.chunkedOutputs.offer(chunkedOutput);
    }
    
    public <OUT extends ChunkedOutput<T>> boolean remove(final OUT chunkedOutput) {
        return this.chunkedOutputs.remove(chunkedOutput);
    }
    
    public boolean add(final BroadcasterListener<T> listener) {
        return this.listeners.add(listener);
    }
    
    public boolean remove(final BroadcasterListener<T> listener) {
        return this.listeners.remove(listener);
    }
    
    public void broadcast(final T chunk) {
        this.forEachOutput(new Task<ChunkedOutput<T>>() {
            @Override
            public void run(final ChunkedOutput<T> cr) throws IOException {
                cr.write(chunk);
            }
        });
    }
    
    public void closeAll() {
        this.forEachOutput(new Task<ChunkedOutput<T>>() {
            @Override
            public void run(final ChunkedOutput<T> cr) throws IOException {
                cr.close();
            }
        });
    }
    
    @Override
    public void onException(final ChunkedOutput<T> chunkedOutput, final Exception exception) {
    }
    
    @Override
    public void onClose(final ChunkedOutput<T> chunkedOutput) {
    }
    
    private void forEachOutput(final Task<ChunkedOutput<T>> t) {
        final Iterator<ChunkedOutput<T>> iterator = this.chunkedOutputs.iterator();
        while (iterator.hasNext()) {
            final ChunkedOutput<T> chunkedOutput = iterator.next();
            if (!chunkedOutput.isClosed()) {
                try {
                    t.run(chunkedOutput);
                }
                catch (final Exception e) {
                    this.fireOnException(chunkedOutput, e);
                }
            }
            if (chunkedOutput.isClosed()) {
                iterator.remove();
                this.fireOnClose(chunkedOutput);
            }
        }
    }
    
    private void forEachListener(final Task<BroadcasterListener<T>> t) {
        for (final BroadcasterListener<T> listener : this.listeners) {
            try {
                t.run(listener);
            }
            catch (final Exception e) {
                Logger.getLogger(Broadcaster.class.getName()).log(Level.WARNING, LocalizationMessages.BROADCASTER_LISTENER_EXCEPTION(e.getClass().getSimpleName()), e);
            }
        }
    }
    
    private void fireOnException(final ChunkedOutput<T> chunkedOutput, final Exception exception) {
        this.forEachListener(new Task<BroadcasterListener<T>>() {
            @Override
            public void run(final BroadcasterListener<T> parameter) throws IOException {
                parameter.onException(chunkedOutput, exception);
            }
        });
    }
    
    private void fireOnClose(final ChunkedOutput<T> chunkedOutput) {
        this.forEachListener(new Task<BroadcasterListener<T>>() {
            @Override
            public void run(final BroadcasterListener<T> parameter) throws IOException {
                parameter.onClose(chunkedOutput);
            }
        });
    }
    
    private interface Task<T>
    {
        void run(final T p0) throws IOException;
    }
}
