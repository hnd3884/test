package org.apache.lucene.util;

import java.util.List;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.Map;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public class Accountables
{
    private Accountables() {
    }
    
    public static String toString(final Accountable a) {
        final StringBuilder sb = new StringBuilder();
        toString(sb, a, 0);
        return sb.toString();
    }
    
    private static StringBuilder toString(final StringBuilder dest, final Accountable a, final int depth) {
        for (int i = 1; i < depth; ++i) {
            dest.append("    ");
        }
        if (depth > 0) {
            dest.append("|-- ");
        }
        dest.append(a.toString());
        dest.append(": ");
        dest.append(RamUsageEstimator.humanReadableUnits(a.ramBytesUsed()));
        dest.append(System.lineSeparator());
        for (final Accountable child : a.getChildResources()) {
            toString(dest, child, depth + 1);
        }
        return dest;
    }
    
    public static Accountable namedAccountable(final String description, final Accountable in) {
        return namedAccountable(description + " [" + in + "]", in.getChildResources(), in.ramBytesUsed());
    }
    
    public static Accountable namedAccountable(final String description, final long bytes) {
        return namedAccountable(description, (Collection<Accountable>)Collections.emptyList(), bytes);
    }
    
    public static Collection<Accountable> namedAccountables(final String prefix, final Map<?, ? extends Accountable> in) {
        final List<Accountable> resources = new ArrayList<Accountable>();
        for (final Map.Entry<?, ? extends Accountable> kv : in.entrySet()) {
            resources.add(namedAccountable(prefix + " '" + kv.getKey() + "'", (Accountable)kv.getValue()));
        }
        Collections.sort(resources, new Comparator<Accountable>() {
            @Override
            public int compare(final Accountable o1, final Accountable o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });
        return (Collection<Accountable>)Collections.unmodifiableList((List<?>)resources);
    }
    
    public static Accountable namedAccountable(final String description, final Collection<Accountable> children, final long bytes) {
        return new Accountable() {
            @Override
            public long ramBytesUsed() {
                return bytes;
            }
            
            @Override
            public Collection<Accountable> getChildResources() {
                return children;
            }
            
            @Override
            public String toString() {
                return description;
            }
        };
    }
}
