package org.postgresql.util;

public interface Gettable<K, V>
{
    V get(final K p0);
}
