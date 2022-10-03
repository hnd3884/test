package org.apache.lucene.search;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.io.Closeable;

public abstract class LiveFieldValues<S, T> implements ReferenceManager.RefreshListener, Closeable
{
    private volatile Map<String, T> current;
    private volatile Map<String, T> old;
    private final ReferenceManager<S> mgr;
    private final T missingValue;
    
    public LiveFieldValues(final ReferenceManager<S> mgr, final T missingValue) {
        this.current = new ConcurrentHashMap<String, T>();
        this.old = new ConcurrentHashMap<String, T>();
        this.missingValue = missingValue;
        (this.mgr = mgr).addListener(this);
    }
    
    @Override
    public void close() {
        this.mgr.removeListener(this);
    }
    
    @Override
    public void beforeRefresh() throws IOException {
        this.old = this.current;
        this.current = new ConcurrentHashMap<String, T>();
    }
    
    @Override
    public void afterRefresh(final boolean didRefresh) throws IOException {
        this.old = new ConcurrentHashMap<String, T>();
    }
    
    public void add(final String id, final T value) {
        this.current.put(id, value);
    }
    
    public void delete(final String id) {
        this.current.put(id, this.missingValue);
    }
    
    public int size() {
        return this.current.size() + this.old.size();
    }
    
    public T get(final String id) throws IOException {
        T value = this.current.get(id);
        if (value == this.missingValue) {
            return null;
        }
        if (value != null) {
            return value;
        }
        value = this.old.get(id);
        if (value == this.missingValue) {
            return null;
        }
        if (value != null) {
            return value;
        }
        final S s = this.mgr.acquire();
        try {
            return this.lookupFromSearcher(s, id);
        }
        finally {
            this.mgr.release(s);
        }
    }
    
    protected abstract T lookupFromSearcher(final S p0, final String p1) throws IOException;
}
