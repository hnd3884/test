package com.sun.jmx.mbeanserver;

import javax.management.Descriptor;
import javax.management.JMX;
import java.lang.ref.WeakReference;
import javax.management.ImmutableDescriptor;
import java.util.WeakHashMap;

public class DescriptorCache
{
    private static final DescriptorCache instance;
    private final WeakHashMap<ImmutableDescriptor, WeakReference<ImmutableDescriptor>> map;
    
    private DescriptorCache() {
        this.map = new WeakHashMap<ImmutableDescriptor, WeakReference<ImmutableDescriptor>>();
    }
    
    static DescriptorCache getInstance() {
        return DescriptorCache.instance;
    }
    
    public static DescriptorCache getInstance(final JMX jmx) {
        if (jmx != null) {
            return DescriptorCache.instance;
        }
        return null;
    }
    
    public ImmutableDescriptor get(final ImmutableDescriptor immutableDescriptor) {
        final WeakReference weakReference = this.map.get(immutableDescriptor);
        final ImmutableDescriptor immutableDescriptor2 = (weakReference == null) ? null : ((ImmutableDescriptor)weakReference.get());
        if (immutableDescriptor2 != null) {
            return immutableDescriptor2;
        }
        this.map.put(immutableDescriptor, new WeakReference<ImmutableDescriptor>(immutableDescriptor));
        return immutableDescriptor;
    }
    
    public ImmutableDescriptor union(final Descriptor... array) {
        return this.get(ImmutableDescriptor.union(array));
    }
    
    static {
        instance = new DescriptorCache();
    }
}
