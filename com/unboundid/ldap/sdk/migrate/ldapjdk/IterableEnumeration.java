package com.unboundid.ldap.sdk.migrate.ldapjdk;

import java.util.NoSuchElementException;
import java.util.Iterator;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;
import com.unboundid.util.InternalUseOnly;
import java.util.Enumeration;

@InternalUseOnly
@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
final class IterableEnumeration<T> implements Enumeration<T>
{
    private final Iterator<T> iterator;
    
    IterableEnumeration(final Iterable<T> i) {
        this.iterator = i.iterator();
    }
    
    @Override
    public boolean hasMoreElements() {
        return this.iterator.hasNext();
    }
    
    @Override
    public T nextElement() throws NoSuchElementException {
        return this.iterator.next();
    }
}
