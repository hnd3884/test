package org.apache.commons.compress.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class Sets
{
    private Sets() {
    }
    
    @SafeVarargs
    public static <E> HashSet<E> newHashSet(final E... elements) {
        final HashSet<E> set = new HashSet<E>(elements.length);
        Collections.addAll(set, elements);
        return set;
    }
}
