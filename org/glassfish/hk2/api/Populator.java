package org.glassfish.hk2.api;

import java.io.IOException;
import java.util.List;

public interface Populator
{
    List<ActiveDescriptor<?>> populate(final DescriptorFileFinder p0, final PopulatorPostProcessor... p1) throws IOException, MultiException;
    
    List<ActiveDescriptor<?>> populate() throws IOException, MultiException;
}
