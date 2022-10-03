package org.apache.commons.compress.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;

public class Lists
{
    public static <E> ArrayList<E> newArrayList() {
        return new ArrayList<E>();
    }
    
    public static <E> ArrayList<E> newArrayList(final Iterator<? extends E> iterator) {
        final ArrayList<E> list = newArrayList();
        Iterators.addAll(list, iterator);
        return list;
    }
    
    private Lists() {
    }
}
