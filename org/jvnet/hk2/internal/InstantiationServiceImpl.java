package org.jvnet.hk2.internal;

import org.glassfish.hk2.api.InstantiationData;
import org.glassfish.hk2.api.Injectee;
import java.util.LinkedList;
import java.util.HashMap;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.Visibility;
import org.glassfish.hk2.api.InstantiationService;

@Visibility(DescriptorVisibility.LOCAL)
public class InstantiationServiceImpl implements InstantiationService
{
    private final HashMap<Long, LinkedList<Injectee>> injecteeStack;
    
    public InstantiationServiceImpl() {
        this.injecteeStack = new HashMap<Long, LinkedList<Injectee>>();
    }
    
    public synchronized InstantiationData getInstantiationData() {
        final long tid = Thread.currentThread().getId();
        final LinkedList<Injectee> threadStack = this.injecteeStack.get(tid);
        if (threadStack == null) {
            return null;
        }
        if (threadStack.isEmpty()) {
            return null;
        }
        final Injectee head = threadStack.getLast();
        return (InstantiationData)new InstantiationData() {
            public Injectee getParentInjectee() {
                return head;
            }
            
            @Override
            public String toString() {
                return "InstantiationData(" + head + "," + System.identityHashCode(this) + ")";
            }
        };
    }
    
    public synchronized void pushInjecteeParent(final Injectee injectee) {
        final long tid = Thread.currentThread().getId();
        LinkedList<Injectee> threadStack = this.injecteeStack.get(tid);
        if (threadStack == null) {
            threadStack = new LinkedList<Injectee>();
            this.injecteeStack.put(tid, threadStack);
        }
        threadStack.addLast(injectee);
    }
    
    public synchronized void popInjecteeParent() {
        final long tid = Thread.currentThread().getId();
        final LinkedList<Injectee> threadStack = this.injecteeStack.get(tid);
        if (threadStack == null) {
            return;
        }
        threadStack.removeLast();
        if (threadStack.isEmpty()) {
            this.injecteeStack.remove(tid);
        }
    }
    
    @Override
    public String toString() {
        return "InstantiationServiceImpl(" + this.injecteeStack.keySet() + "," + System.identityHashCode(this) + ")";
    }
}
