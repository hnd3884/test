package com.unboundid.ldap.sdk.unboundidds.examples;

import java.util.Iterator;
import java.util.Collection;
import java.util.Arrays;
import java.util.TreeSet;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Validator;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;
import com.unboundid.ldap.sdk.Filter;
import java.util.Comparator;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class FilterComparator implements Comparator<Filter>, Serializable
{
    private static final FilterComparator INSTANCE;
    private static final long serialVersionUID = 7637416445464620770L;
    
    private FilterComparator() {
    }
    
    public static FilterComparator getInstance() {
        return FilterComparator.INSTANCE;
    }
    
    @Override
    public int compare(final Filter f1, final Filter f2) {
        if (f1 == f2) {
            return 0;
        }
        Validator.ensureNotNull(f1, f2);
        final byte type1 = f1.getFilterType();
        final byte type2 = f2.getFilterType();
        if (type1 != type2) {
            return (type1 & 0x1F) - (type2 & 0x1F);
        }
        final String name1 = StaticUtils.toLowerCase(f1.getAttributeName());
        final String name2 = StaticUtils.toLowerCase(f2.getAttributeName());
        if (name1 != null && name2 != null) {
            final int cmpValue = name1.compareTo(name2);
            if (cmpValue != 0) {
                return cmpValue;
            }
        }
        final byte[] value1 = f1.getAssertionValueBytes();
        if (value1 != null) {
            final byte[] value2 = f2.getAssertionValueBytes();
            final int cmpValue2 = compare(value1, value2);
            if (cmpValue2 != 0) {
                return cmpValue2;
            }
        }
        switch (type1) {
            case -96:
            case -95: {
                return compareANDOrOR(f1, f2);
            }
            case -94: {
                return this.compare(f1.getNOTComponent(), f2.getNOTComponent());
            }
            case -121:
            case -93:
            case -91:
            case -90:
            case -88: {
                return 0;
            }
            case -92: {
                return compareSubstring(f1, f2);
            }
            case -87: {
                return compareExtensible(f1, f2);
            }
            default: {
                return 0;
            }
        }
    }
    
    private static int compareANDOrOR(final Filter f1, final Filter f2) {
        final TreeSet<Filter> set1 = new TreeSet<Filter>(FilterComparator.INSTANCE);
        final TreeSet<Filter> set2 = new TreeSet<Filter>(FilterComparator.INSTANCE);
        set1.addAll(Arrays.asList(f1.getComponents()));
        set2.addAll(Arrays.asList(f2.getComponents()));
        final Iterator<Filter> iterator1 = set1.iterator();
        final Iterator<Filter> iterator2 = set2.iterator();
        while (iterator1.hasNext()) {
            final Filter comp1 = iterator1.next();
            if (!iterator2.hasNext()) {
                return 1;
            }
            final Filter comp2 = iterator2.next();
            final int compValue = FilterComparator.INSTANCE.compare(comp1, comp2);
            if (compValue != 0) {
                return compValue;
            }
        }
        if (iterator2.hasNext()) {
            return -1;
        }
        return 0;
    }
    
    private static int compareSubstring(final Filter f1, final Filter f2) {
        final byte[] sI1 = f1.getSubInitialBytes();
        final byte[] sI2 = f2.getSubInitialBytes();
        if (sI1 == null) {
            if (sI2 != null) {
                return -1;
            }
        }
        else {
            if (sI2 == null) {
                return 1;
            }
            final int cmpValue = compare(sI1, sI2);
            if (cmpValue != 0) {
                return cmpValue;
            }
        }
        final byte[][] sA1 = f1.getSubAnyBytes();
        final byte[][] sA2 = f2.getSubAnyBytes();
        if (sA1.length == 0) {
            if (sA2.length > 0) {
                return -1;
            }
        }
        else {
            if (sA2.length == 0) {
                return 1;
            }
            for (int minLength = Math.min(sA1.length, sA2.length), i = 0; i < minLength; ++i) {
                final int cmpValue2 = compare(sA1[i], sA2[i]);
                if (cmpValue2 != 0) {
                    return cmpValue2;
                }
            }
            if (sA1.length < sA2.length) {
                return -1;
            }
            if (sA2.length < sA1.length) {
                return 1;
            }
        }
        final byte[] sF1 = f1.getSubFinalBytes();
        final byte[] sF2 = f2.getSubFinalBytes();
        if (sF1 == null) {
            if (sF2 != null) {
                return -1;
            }
            return 0;
        }
        else {
            if (sF2 == null) {
                return 1;
            }
            return compare(sF1, sF2);
        }
    }
    
    private static int compareExtensible(final Filter f1, final Filter f2) {
        final String name1 = f1.getAttributeName();
        final String name2 = f2.getAttributeName();
        if (name1 == null) {
            if (name2 != null) {
                return -1;
            }
        }
        else if (name2 == null) {
            return 1;
        }
        final String mr1 = f1.getMatchingRuleID();
        final String mr2 = f2.getMatchingRuleID();
        if (mr1 == null) {
            if (mr2 != null) {
                return -1;
            }
        }
        else {
            if (mr2 == null) {
                return 1;
            }
            final int cmpValue = mr1.compareTo(mr2);
            if (cmpValue != 0) {
                return cmpValue;
            }
        }
        if (f1.getDNAttributes()) {
            if (f2.getDNAttributes()) {
                return 0;
            }
            return 1;
        }
        else {
            if (f2.getDNAttributes()) {
                return -1;
            }
            return 0;
        }
    }
    
    private static int compare(final byte[] a1, final byte[] a2) {
        for (int length = Math.min(a1.length, a2.length), i = 0; i < length; ++i) {
            final int b1 = 0xFF & a1[i];
            final int b2 = 0xFF & a2[i];
            if (b1 != b2) {
                return b1 - b2;
            }
        }
        return a1.length - a2.length;
    }
    
    @Override
    public int hashCode() {
        return 0;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && o instanceof FilterComparator;
    }
    
    static {
        INSTANCE = new FilterComparator();
    }
}
