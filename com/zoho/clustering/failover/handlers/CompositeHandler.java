package com.zoho.clustering.failover.handlers;

import com.zoho.clustering.failover.ErrorCode;
import java.util.Iterator;
import com.zoho.clustering.failover.FOS;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import com.zoho.clustering.failover.FOSHandler;

public class CompositeHandler implements FOSHandler
{
    private List<FOSHandler> handlers;
    private boolean mutable;
    
    public CompositeHandler() {
        this.handlers = new ArrayList<FOSHandler>();
        this.mutable = true;
    }
    
    public void addHandler(final FOSHandler handler) {
        if (!this.mutable) {
            throw new IllegalStateException("This CompositeHandler object is not mutable");
        }
        this.handlers.add(handler);
    }
    
    public void makeImmutable() {
        this.mutable = false;
        this.handlers = Collections.unmodifiableList((List<? extends FOSHandler>)this.handlers);
    }
    
    public List<FOSHandler> getHandlers() {
        return this.handlers;
    }
    
    @Override
    public void onStart(final FOS.Mode mode) {
        for (final FOSHandler handler : this.handlers) {
            handler.onStart(mode);
        }
    }
    
    @Override
    public void onStop(final FOS.Mode mode, final ErrorCode errorCode) {
        final int size = this.handlers.size();
        for (int i = size - 1; i >= 0; --i) {
            this.handlers.get(i).onStop(mode, errorCode);
        }
    }
    
    @Override
    public void onSlaveTakeover() {
        final int size = this.handlers.size();
        for (int i = size - 1; i >= 0; --i) {
            this.handlers.get(i).onSlaveTakeover();
        }
    }
}
