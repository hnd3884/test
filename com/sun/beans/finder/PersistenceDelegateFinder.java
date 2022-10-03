package com.sun.beans.finder;

import java.util.HashMap;
import java.util.Map;
import java.beans.PersistenceDelegate;

public final class PersistenceDelegateFinder extends InstanceFinder<PersistenceDelegate>
{
    private final Map<Class<?>, PersistenceDelegate> registry;
    
    public PersistenceDelegateFinder() {
        super(PersistenceDelegate.class, true, "PersistenceDelegate", new String[0]);
        this.registry = new HashMap<Class<?>, PersistenceDelegate>();
    }
    
    public void register(final Class<?> clazz, final PersistenceDelegate persistenceDelegate) {
        synchronized (this.registry) {
            if (persistenceDelegate != null) {
                this.registry.put(clazz, persistenceDelegate);
            }
            else {
                this.registry.remove(clazz);
            }
        }
    }
    
    @Override
    public PersistenceDelegate find(final Class<?> clazz) {
        final PersistenceDelegate persistenceDelegate;
        synchronized (this.registry) {
            persistenceDelegate = this.registry.get(clazz);
        }
        return (persistenceDelegate != null) ? persistenceDelegate : super.find(clazz);
    }
}
