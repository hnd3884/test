package org.apache.lucene.index;

import java.util.List;

public final class ReaderUtil
{
    private ReaderUtil() {
    }
    
    public static IndexReaderContext getTopLevelContext(IndexReaderContext context) {
        while (context.parent != null) {
            context = context.parent;
        }
        return context;
    }
    
    public static int subIndex(final int n, final int[] docStarts) {
        final int size = docStarts.length;
        int lo = 0;
        int hi = size - 1;
        while (hi >= lo) {
            int mid = lo + hi >>> 1;
            final int midValue = docStarts[mid];
            if (n < midValue) {
                hi = mid - 1;
            }
            else {
                if (n <= midValue) {
                    while (mid + 1 < size && docStarts[mid + 1] == midValue) {
                        ++mid;
                    }
                    return mid;
                }
                lo = mid + 1;
            }
        }
        return hi;
    }
    
    public static int subIndex(final int n, final List<LeafReaderContext> leaves) {
        final int size = leaves.size();
        int lo = 0;
        int hi = size - 1;
        while (hi >= lo) {
            int mid = lo + hi >>> 1;
            final int midValue = leaves.get(mid).docBase;
            if (n < midValue) {
                hi = mid - 1;
            }
            else {
                if (n <= midValue) {
                    while (mid + 1 < size && leaves.get(mid + 1).docBase == midValue) {
                        ++mid;
                    }
                    return mid;
                }
                lo = mid + 1;
            }
        }
        return hi;
    }
}
