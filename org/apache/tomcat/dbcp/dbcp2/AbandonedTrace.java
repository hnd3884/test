package org.apache.tomcat.dbcp.dbcp2;

import java.util.Iterator;
import java.util.Collections;
import java.util.ArrayList;
import java.lang.ref.WeakReference;
import java.util.List;
import org.apache.tomcat.dbcp.pool2.TrackedUse;

public class AbandonedTrace implements TrackedUse
{
    private final List<WeakReference<AbandonedTrace>> traceList;
    private volatile long lastUsedMillis;
    
    public AbandonedTrace() {
        this.traceList = new ArrayList<WeakReference<AbandonedTrace>>();
        this.init(null);
    }
    
    public AbandonedTrace(final AbandonedTrace parent) {
        this.traceList = new ArrayList<WeakReference<AbandonedTrace>>();
        this.init(parent);
    }
    
    protected void addTrace(final AbandonedTrace trace) {
        synchronized (this.traceList) {
            this.traceList.add(new WeakReference<AbandonedTrace>(trace));
        }
        this.setLastUsed();
    }
    
    protected void clearTrace() {
        synchronized (this.traceList) {
            this.traceList.clear();
        }
    }
    
    @Override
    public long getLastUsed() {
        return this.lastUsedMillis;
    }
    
    protected List<AbandonedTrace> getTrace() {
        final int size = this.traceList.size();
        if (size == 0) {
            return Collections.emptyList();
        }
        final ArrayList<AbandonedTrace> result = new ArrayList<AbandonedTrace>(size);
        synchronized (this.traceList) {
            final Iterator<WeakReference<AbandonedTrace>> iter = this.traceList.iterator();
            while (iter.hasNext()) {
                final AbandonedTrace trace = iter.next().get();
                if (trace == null) {
                    iter.remove();
                }
                else {
                    result.add(trace);
                }
            }
        }
        return result;
    }
    
    private void init(final AbandonedTrace parent) {
        if (parent != null) {
            parent.addTrace(this);
        }
    }
    
    protected void removeThisTrace(final Object source) {
        if (source instanceof AbandonedTrace) {
            AbandonedTrace.class.cast(source).removeTrace(this);
        }
    }
    
    protected void removeTrace(final AbandonedTrace trace) {
        synchronized (this.traceList) {
            final Iterator<WeakReference<AbandonedTrace>> iter = this.traceList.iterator();
            while (iter.hasNext()) {
                final AbandonedTrace traceInList = iter.next().get();
                if (trace != null && trace.equals(traceInList)) {
                    iter.remove();
                    break;
                }
                if (traceInList != null) {
                    continue;
                }
                iter.remove();
            }
        }
    }
    
    protected void setLastUsed() {
        this.lastUsedMillis = System.currentTimeMillis();
    }
    
    protected void setLastUsed(final long lastUsedMillis) {
        this.lastUsedMillis = lastUsedMillis;
    }
}
