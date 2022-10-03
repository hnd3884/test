package org.jvnet.hk2.internal;

import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.LinkedList;
import org.glassfish.hk2.api.ActiveDescriptor;
import java.util.List;
import org.glassfish.hk2.api.TwoPhaseTransactionData;

public class TwoPhaseTransactionDataImpl implements TwoPhaseTransactionData
{
    private final List<ActiveDescriptor<?>> added;
    private final List<ActiveDescriptor<?>> removed;
    
    public TwoPhaseTransactionDataImpl() {
        this.added = new LinkedList<ActiveDescriptor<?>>();
        this.removed = new LinkedList<ActiveDescriptor<?>>();
    }
    
    public List<ActiveDescriptor<?>> getAllAddedDescriptors() {
        return Collections.unmodifiableList((List<? extends ActiveDescriptor<?>>)new ArrayList<ActiveDescriptor<?>>(this.added));
    }
    
    public List<ActiveDescriptor<?>> getAllRemovedDescriptors() {
        return Collections.unmodifiableList((List<? extends ActiveDescriptor<?>>)new ArrayList<ActiveDescriptor<?>>(this.removed));
    }
    
    void toAdd(final ActiveDescriptor<?> addMe) {
        this.added.add(addMe);
    }
    
    void toRemove(final ActiveDescriptor<?> removeMe) {
        this.removed.add(removeMe);
    }
    
    @Override
    public String toString() {
        return "TwoPhaseTransactionalDataImpl(" + System.identityHashCode(this) + ")";
    }
}
