package org.glassfish.hk2.utilities.cache;

public interface Computable<K, V>
{
    V compute(final K p0) throws ComputationErrorException;
}
