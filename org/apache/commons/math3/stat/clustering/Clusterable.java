package org.apache.commons.math3.stat.clustering;

import java.util.Collection;

@Deprecated
public interface Clusterable<T>
{
    double distanceFrom(final T p0);
    
    T centroidOf(final Collection<T> p0);
}
