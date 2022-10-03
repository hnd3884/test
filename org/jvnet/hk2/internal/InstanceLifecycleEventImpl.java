package org.jvnet.hk2.internal;

import java.util.Collections;
import org.glassfish.hk2.api.Injectee;
import java.util.Map;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.InstanceLifecycleEventType;
import org.glassfish.hk2.api.InstanceLifecycleEvent;

public class InstanceLifecycleEventImpl implements InstanceLifecycleEvent
{
    private final InstanceLifecycleEventType eventType;
    private final ActiveDescriptor<?> descriptor;
    private final Object lifecycleObject;
    private final Map<Injectee, Object> knownInjectees;
    
    InstanceLifecycleEventImpl(final InstanceLifecycleEventType eventType, final Object lifecycleObject, final Map<Injectee, Object> knownInjectees, final ActiveDescriptor<?> descriptor) {
        this.eventType = eventType;
        this.lifecycleObject = lifecycleObject;
        if (knownInjectees == null) {
            this.knownInjectees = null;
        }
        else {
            this.knownInjectees = Collections.unmodifiableMap((Map<? extends Injectee, ?>)knownInjectees);
        }
        this.descriptor = descriptor;
    }
    
    InstanceLifecycleEventImpl(final InstanceLifecycleEventType eventType, final Object lifecycleObject, final ActiveDescriptor<?> descriptor) {
        this(eventType, lifecycleObject, null, descriptor);
    }
    
    public InstanceLifecycleEventType getEventType() {
        return this.eventType;
    }
    
    public ActiveDescriptor<?> getActiveDescriptor() {
        return this.descriptor;
    }
    
    public Object getLifecycleObject() {
        return this.lifecycleObject;
    }
    
    public Map<Injectee, Object> getKnownInjectees() {
        return this.knownInjectees;
    }
    
    @Override
    public String toString() {
        final String descName = (this.descriptor == null) ? "null" : this.descriptor.getImplementation();
        return "InstanceLifecycleEventImpl(" + this.eventType + "," + descName + "," + System.identityHashCode(this) + ")";
    }
}
