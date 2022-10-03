package com.google.api.client.util;

import java.util.Collection;

@Deprecated
public final class Collections2
{
    static <T> Collection<T> cast(final Iterable<T> iterable) {
        return (Collection)iterable;
    }
    
    private Collections2() {
    }
}
