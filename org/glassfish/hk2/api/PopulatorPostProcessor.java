package org.glassfish.hk2.api;

import org.glassfish.hk2.utilities.DescriptorImpl;

public interface PopulatorPostProcessor
{
    DescriptorImpl process(final ServiceLocator p0, final DescriptorImpl p1);
}
