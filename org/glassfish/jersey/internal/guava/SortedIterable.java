package org.glassfish.jersey.internal.guava;

import java.util.Iterator;
import java.util.Comparator;

interface SortedIterable<T> extends Iterable<T>
{
    Comparator<? super T> comparator();
    
    Iterator<T> iterator();
}
