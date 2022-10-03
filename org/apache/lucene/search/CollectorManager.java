package org.apache.lucene.search;

import java.util.Collection;
import java.io.IOException;

public interface CollectorManager<C extends Collector, T>
{
    C newCollector() throws IOException;
    
    T reduce(final Collection<C> p0) throws IOException;
}
