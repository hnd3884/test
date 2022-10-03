package org.apache.commons.collections;

import java.util.Collections;
import org.apache.commons.collections.list.FixedSizeList;
import org.apache.commons.collections.list.LazyList;
import org.apache.commons.collections.list.TransformedList;
import org.apache.commons.collections.list.TypedList;
import org.apache.commons.collections.list.PredicatedList;
import org.apache.commons.collections.list.UnmodifiableList;
import org.apache.commons.collections.list.SynchronizedList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class ListUtils
{
    public static final List EMPTY_LIST;
    
    public static List intersection(final List list1, final List list2) {
        final ArrayList result = new ArrayList();
        final Iterator iterator = list2.iterator();
        while (iterator.hasNext()) {
            final Object o = iterator.next();
            if (list1.contains(o)) {
                result.add(o);
            }
        }
        return result;
    }
    
    public static List subtract(final List list1, final List list2) {
        final ArrayList result = new ArrayList(list1);
        final Iterator iterator = list2.iterator();
        while (iterator.hasNext()) {
            result.remove(iterator.next());
        }
        return result;
    }
    
    public static List sum(final List list1, final List list2) {
        return subtract(union(list1, list2), intersection(list1, list2));
    }
    
    public static List union(final List list1, final List list2) {
        final ArrayList result = new ArrayList(list1);
        result.addAll(list2);
        return result;
    }
    
    public static boolean isEqualList(final Collection list1, final Collection list2) {
        if (list1 == list2) {
            return true;
        }
        if (list1 == null || list2 == null || list1.size() != list2.size()) {
            return false;
        }
        final Iterator it1 = list1.iterator();
        final Iterator it2 = list2.iterator();
        Object obj1 = null;
        Object obj2 = null;
        while (it1.hasNext() && it2.hasNext()) {
            obj1 = it1.next();
            obj2 = it2.next();
            if (obj1 == null) {
                if (obj2 == null) {
                    continue;
                }
            }
            else if (obj1.equals(obj2)) {
                continue;
            }
            return false;
        }
        return !it1.hasNext() && !it2.hasNext();
    }
    
    public static int hashCodeForList(final Collection list) {
        if (list == null) {
            return 0;
        }
        int hashCode = 1;
        final Iterator it = list.iterator();
        Object obj = null;
        while (it.hasNext()) {
            obj = it.next();
            hashCode = 31 * hashCode + ((obj == null) ? 0 : obj.hashCode());
        }
        return hashCode;
    }
    
    public static List retainAll(final Collection collection, final Collection retain) {
        final List list = new ArrayList(Math.min(collection.size(), retain.size()));
        final Iterator iter = collection.iterator();
        while (iter.hasNext()) {
            final Object obj = iter.next();
            if (retain.contains(obj)) {
                list.add(obj);
            }
        }
        return list;
    }
    
    public static List removeAll(final Collection collection, final Collection remove) {
        final List list = new ArrayList();
        final Iterator iter = collection.iterator();
        while (iter.hasNext()) {
            final Object obj = iter.next();
            if (!remove.contains(obj)) {
                list.add(obj);
            }
        }
        return list;
    }
    
    public static List synchronizedList(final List list) {
        return SynchronizedList.decorate(list);
    }
    
    public static List unmodifiableList(final List list) {
        return UnmodifiableList.decorate(list);
    }
    
    public static List predicatedList(final List list, final Predicate predicate) {
        return PredicatedList.decorate(list, predicate);
    }
    
    public static List typedList(final List list, final Class type) {
        return TypedList.decorate(list, type);
    }
    
    public static List transformedList(final List list, final Transformer transformer) {
        return TransformedList.decorate(list, transformer);
    }
    
    public static List lazyList(final List list, final Factory factory) {
        return LazyList.decorate(list, factory);
    }
    
    public static List fixedSizeList(final List list) {
        return FixedSizeList.decorate(list);
    }
    
    static {
        EMPTY_LIST = Collections.EMPTY_LIST;
    }
}
