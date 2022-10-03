package org.jvnet.hk2.internal;

import org.glassfish.hk2.api.Descriptor;
import java.io.Serializable;
import org.glassfish.hk2.api.ServiceHandle;
import java.util.Comparator;

public class ServiceHandleComparator implements Comparator<ServiceHandle<?>>, Serializable
{
    private static final long serialVersionUID = -3475592779302344427L;
    private final DescriptorComparator baseComparator;
    
    public ServiceHandleComparator() {
        this.baseComparator = new DescriptorComparator();
    }
    
    @Override
    public int compare(final ServiceHandle<?> o1, final ServiceHandle<?> o2) {
        return this.baseComparator.compare((Descriptor)o1.getActiveDescriptor(), (Descriptor)o2.getActiveDescriptor());
    }
}
