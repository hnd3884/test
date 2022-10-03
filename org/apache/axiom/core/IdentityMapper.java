package org.apache.axiom.core;

public final class IdentityMapper<T> implements Mapper<T, T>
{
    public T map(final T object) {
        return object;
    }
}
