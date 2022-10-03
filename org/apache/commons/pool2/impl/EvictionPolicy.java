package org.apache.commons.pool2.impl;

import org.apache.commons.pool2.PooledObject;

public interface EvictionPolicy<T>
{
    boolean evict(final EvictionConfig p0, final PooledObject<T> p1, final int p2);
}
