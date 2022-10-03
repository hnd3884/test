package com.sun.xml.internal.ws.api.server;

import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;

public enum LazyMOMProvider
{
    INSTANCE;
    
    private final Set<WSEndpointScopeChangeListener> endpointsWaitingForMOM;
    private final Set<DefaultScopeChangeListener> listeners;
    private volatile Scope scope;
    
    private LazyMOMProvider() {
        this.endpointsWaitingForMOM = new HashSet<WSEndpointScopeChangeListener>();
        this.listeners = new HashSet<DefaultScopeChangeListener>();
        this.scope = Scope.STANDALONE;
    }
    
    public void initMOMForScope(final Scope scope) {
        if (this.scope == Scope.GLASSFISH_JMX || (scope == Scope.STANDALONE && (this.scope == Scope.GLASSFISH_JMX || this.scope == Scope.GLASSFISH_NO_JMX)) || this.scope == scope) {
            return;
        }
        this.scope = scope;
        this.fireScopeChanged();
    }
    
    private void fireScopeChanged() {
        for (final ScopeChangeListener wsEndpoint : this.endpointsWaitingForMOM) {
            wsEndpoint.scopeChanged(this.scope);
        }
        for (final ScopeChangeListener listener : this.listeners) {
            listener.scopeChanged(this.scope);
        }
    }
    
    public void registerListener(final DefaultScopeChangeListener listener) {
        this.listeners.add(listener);
        if (!this.isProviderInDefaultScope()) {
            listener.scopeChanged(this.scope);
        }
    }
    
    private boolean isProviderInDefaultScope() {
        return this.scope == Scope.STANDALONE;
    }
    
    public Scope getScope() {
        return this.scope;
    }
    
    public void registerEndpoint(final WSEndpointScopeChangeListener wsEndpoint) {
        this.endpointsWaitingForMOM.add(wsEndpoint);
        if (!this.isProviderInDefaultScope()) {
            wsEndpoint.scopeChanged(this.scope);
        }
    }
    
    public void unregisterEndpoint(final WSEndpointScopeChangeListener wsEndpoint) {
        this.endpointsWaitingForMOM.remove(wsEndpoint);
    }
    
    public enum Scope
    {
        STANDALONE, 
        GLASSFISH_NO_JMX, 
        GLASSFISH_JMX;
    }
    
    public interface WSEndpointScopeChangeListener extends ScopeChangeListener
    {
    }
    
    public interface ScopeChangeListener
    {
        void scopeChanged(final Scope p0);
    }
    
    public interface DefaultScopeChangeListener extends ScopeChangeListener
    {
    }
}
