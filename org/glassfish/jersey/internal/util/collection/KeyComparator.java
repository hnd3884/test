package org.glassfish.jersey.internal.util.collection;

import java.io.Serializable;

public interface KeyComparator<K> extends Serializable
{
    boolean equals(final K p0, final K p1);
    
    int hash(final K p0);
}
