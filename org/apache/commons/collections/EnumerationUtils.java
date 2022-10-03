package org.apache.commons.collections;

import java.util.Iterator;
import org.apache.commons.collections.iterators.EnumerationIterator;
import java.util.List;
import java.util.Enumeration;

public class EnumerationUtils
{
    public static List toList(final Enumeration enumeration) {
        return IteratorUtils.toList(new EnumerationIterator(enumeration));
    }
}
