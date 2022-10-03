package org.apache.commons.pool2.proxy;

import org.apache.commons.pool2.UsageTracking;

interface ProxySource<T>
{
    T createProxy(final T p0, final UsageTracking<T> p1);
    
    T resolveProxy(final T p0);
}
