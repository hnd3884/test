package org.glassfish.hk2.utilities;

import org.glassfish.hk2.api.DynamicConfiguration;
import org.jvnet.hk2.annotations.Contract;

@Contract
public interface Binder
{
    void bind(final DynamicConfiguration p0);
}
