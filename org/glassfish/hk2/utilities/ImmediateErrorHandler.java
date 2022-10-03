package org.glassfish.hk2.utilities;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.jvnet.hk2.annotations.Contract;

@Contract
public interface ImmediateErrorHandler
{
    void postConstructFailed(final ActiveDescriptor<?> p0, final Throwable p1);
    
    void preDestroyFailed(final ActiveDescriptor<?> p0, final Throwable p1);
}
