package org.bouncycastle.util;

import java.util.Collection;

public interface Store<T>
{
    Collection<T> getMatches(final Selector<T> p0) throws StoreException;
}
