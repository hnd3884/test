package org.jvnet.hk2.internal;

import java.util.Iterator;
import java.util.ListIterator;
import org.glassfish.hk2.api.Descriptor;
import java.util.Comparator;
import java.util.List;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;

public class IndexedListData
{
    private final ArrayList<SystemDescriptor<?>> unsortedList;
    private volatile boolean sorted;
    
    public IndexedListData() {
        this.unsortedList = new ArrayList<SystemDescriptor<?>>();
        this.sorted = true;
    }
    
    public Collection<SystemDescriptor<?>> getSortedList() {
        if (this.sorted) {
            return this.unsortedList;
        }
        synchronized (this) {
            if (this.sorted) {
                return this.unsortedList;
            }
            if (this.unsortedList.size() <= 1) {
                this.sorted = true;
                return this.unsortedList;
            }
            Collections.sort(this.unsortedList, (Comparator<? super SystemDescriptor<?>>)ServiceLocatorImpl.DESCRIPTOR_COMPARATOR);
            this.sorted = true;
            return this.unsortedList;
        }
    }
    
    public synchronized void addDescriptor(final SystemDescriptor<?> descriptor) {
        this.unsortedList.add(descriptor);
        if (this.unsortedList.size() > 1) {
            this.sorted = false;
        }
        else {
            this.sorted = true;
        }
        descriptor.addList(this);
    }
    
    public synchronized void removeDescriptor(final SystemDescriptor<?> descriptor) {
        final ListIterator<SystemDescriptor<?>> iterator = this.unsortedList.listIterator();
        while (iterator.hasNext()) {
            final SystemDescriptor<?> candidate = iterator.next();
            if (ServiceLocatorImpl.DESCRIPTOR_COMPARATOR.compare((Descriptor)descriptor, (Descriptor)candidate) == 0) {
                iterator.remove();
                break;
            }
        }
        if (this.unsortedList.size() > 1) {
            this.sorted = false;
        }
        else {
            this.sorted = true;
        }
        descriptor.removeList(this);
    }
    
    public synchronized boolean isEmpty() {
        return this.unsortedList.isEmpty();
    }
    
    public synchronized void unSort() {
        if (this.unsortedList.size() > 1) {
            this.sorted = false;
        }
    }
    
    public synchronized void clear() {
        for (final SystemDescriptor<?> descriptor : this.unsortedList) {
            descriptor.removeList(this);
        }
        this.unsortedList.clear();
    }
    
    public synchronized int size() {
        return this.unsortedList.size();
    }
}
