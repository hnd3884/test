package org.apache.commons.pool2;

public interface UsageTracking<T>
{
    void use(final T p0);
}
