package org.glassfish.jersey.model.internal;

import javax.annotation.PreDestroy;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;
import org.glassfish.jersey.internal.inject.InjectionManager;
import javax.inject.Singleton;

@Singleton
public class ManagedObjectsFinalizer
{
    private final InjectionManager injectionManager;
    private final Set<Object> managedObjects;
    
    public ManagedObjectsFinalizer(final InjectionManager injectionManager) {
        this.managedObjects = new HashSet<Object>();
        this.injectionManager = injectionManager;
    }
    
    public void registerForPreDestroyCall(final Object object) {
        this.managedObjects.add(object);
    }
    
    @PreDestroy
    public void preDestroy() {
        try {
            for (final Object o : this.managedObjects) {
                this.injectionManager.preDestroy(o);
            }
        }
        finally {
            this.managedObjects.clear();
        }
    }
}
