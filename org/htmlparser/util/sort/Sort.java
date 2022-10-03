package org.htmlparser.util.sort;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class Sort
{
    private Sort() {
    }
    
    public static void QuickSort(final Vector v) throws ClassCastException {
        QuickSort(v, 0, v.size() - 1);
    }
    
    public static void QuickSort(final Vector v, final int lo0, final int hi0) throws ClassCastException {
        int lo = lo0;
        int hi = hi0;
        if (hi0 > lo0) {
            final Ordered mid = v.elementAt((lo0 + hi0) / 2);
            while (lo <= hi) {
                while (lo < hi0 && 0 > v.elementAt(lo).compare(mid)) {
                    ++lo;
                }
                while (hi > lo0 && 0 < v.elementAt(hi).compare(mid)) {
                    --hi;
                }
                if (lo <= hi) {
                    swap(v, lo++, hi--);
                }
            }
            if (lo0 < hi) {
                QuickSort(v, lo0, hi);
            }
            if (lo < hi0) {
                QuickSort(v, lo, hi0);
            }
        }
    }
    
    private static void swap(final Vector v, final int i, final int j) {
        final Object o = v.elementAt(i);
        v.setElementAt(v.elementAt(j), i);
        v.setElementAt(o, j);
    }
    
    public static void QuickSort(final Ordered[] a) {
        QuickSort(a, 0, a.length - 1);
    }
    
    public static void QuickSort(final Ordered[] a, final int lo0, final int hi0) {
        int lo = lo0;
        int hi = hi0;
        if (hi0 > lo0) {
            final Ordered mid = a[(lo0 + hi0) / 2];
            while (lo <= hi) {
                while (lo < hi0 && 0 > a[lo].compare(mid)) {
                    ++lo;
                }
                while (hi > lo0 && 0 < a[hi].compare(mid)) {
                    --hi;
                }
                if (lo <= hi) {
                    swap(a, lo++, hi--);
                }
            }
            if (lo0 < hi) {
                QuickSort(a, lo0, hi);
            }
            if (lo < hi0) {
                QuickSort(a, lo, hi0);
            }
        }
    }
    
    private static void swap(final Object[] a, final int i, final int j) {
        final Object o = a[i];
        a[i] = a[j];
        a[j] = o;
    }
    
    public static void QuickSort(final String[] a) {
        QuickSort(a, 0, a.length - 1);
    }
    
    public static void QuickSort(final String[] a, final int lo0, final int hi0) {
        int lo = lo0;
        int hi = hi0;
        if (hi0 > lo0) {
            final String mid = a[(lo0 + hi0) / 2];
            while (lo <= hi) {
                while (lo < hi0 && 0 > a[lo].compareTo(mid)) {
                    ++lo;
                }
                while (hi > lo0 && 0 < a[hi].compareTo(mid)) {
                    --hi;
                }
                if (lo <= hi) {
                    swap(a, lo++, hi--);
                }
            }
            if (lo0 < hi) {
                QuickSort(a, lo0, hi);
            }
            if (lo < hi0) {
                QuickSort(a, lo, hi0);
            }
        }
    }
    
    public static void QuickSort(final Sortable sortable, final int lo0, final int hi0) {
        int lo = lo0;
        int hi = hi0;
        if (hi0 > lo0) {
            final Ordered mid = sortable.fetch((lo0 + hi0) / 2, null);
            Ordered test = null;
            while (lo <= hi) {
                while (lo < hi0 && 0 > (test = sortable.fetch(lo, test)).compare(mid)) {
                    ++lo;
                }
                while (hi > lo0 && 0 < (test = sortable.fetch(hi, test)).compare(mid)) {
                    --hi;
                }
                if (lo <= hi) {
                    sortable.swap(lo++, hi--);
                }
            }
            if (lo0 < hi) {
                QuickSort(sortable, lo0, hi);
            }
            if (lo < hi0) {
                QuickSort(sortable, lo, hi0);
            }
        }
    }
    
    public static void QuickSort(final Sortable sortable) {
        QuickSort(sortable, sortable.first(), sortable.last());
    }
    
    public static Object[] QuickSort(final Hashtable h) throws ClassCastException {
        final Object[] ret = new Ordered[h.size()];
        final Enumeration e = h.keys();
        boolean are_strings = true;
        for (int i = 0; i < ret.length; ++i) {
            ret[i] = e.nextElement();
            if (are_strings && !(ret[i] instanceof String)) {
                are_strings = false;
            }
        }
        if (are_strings) {
            QuickSort((String[])ret);
        }
        else {
            QuickSort((Ordered[])ret);
        }
        return ret;
    }
    
    public static int bsearch(final Sortable set, final Ordered ref, int lo, int hi) {
        int ret = -1;
        int num = hi - lo + 1;
        Ordered ordered = null;
        while (-1 == ret && lo <= hi) {
            final int half = num / 2;
            final int mid = lo + ((0x0 != (num & 0x1)) ? half : (half - 1));
            ordered = set.fetch(mid, ordered);
            final int result = ref.compare(ordered);
            if (0 == result) {
                ret = mid;
            }
            else if (0 > result) {
                hi = mid - 1;
                num = ((0x0 != (num & 0x1)) ? half : (half - 1));
            }
            else {
                lo = mid + 1;
                num = half;
            }
        }
        if (-1 == ret) {
            ret = lo;
        }
        return ret;
    }
    
    public static int bsearch(final Sortable set, final Ordered ref) {
        return bsearch(set, ref, set.first(), set.last());
    }
    
    public static int bsearch(final Vector vector, final Ordered ref, int lo, int hi) {
        int ret = -1;
        int num = hi - lo + 1;
        while (-1 == ret && lo <= hi) {
            final int half = num / 2;
            final int mid = lo + ((0x0 != (num & 0x1)) ? half : (half - 1));
            final int result = ref.compare(vector.elementAt(mid));
            if (0 == result) {
                ret = mid;
            }
            else if (0 > result) {
                hi = mid - 1;
                num = ((0x0 != (num & 0x1)) ? half : (half - 1));
            }
            else {
                lo = mid + 1;
                num = half;
            }
        }
        if (-1 == ret) {
            ret = lo;
        }
        return ret;
    }
    
    public static int bsearch(final Vector vector, final Ordered ref) {
        return bsearch(vector, ref, 0, vector.size() - 1);
    }
    
    public static int bsearch(final Ordered[] array, final Ordered ref, int lo, int hi) {
        int ret = -1;
        int num = hi - lo + 1;
        while (-1 == ret && lo <= hi) {
            final int half = num / 2;
            final int mid = lo + ((0x0 != (num & 0x1)) ? half : (half - 1));
            final int result = ref.compare(array[mid]);
            if (0 == result) {
                ret = mid;
            }
            else if (0 > result) {
                hi = mid - 1;
                num = ((0x0 != (num & 0x1)) ? half : (half - 1));
            }
            else {
                lo = mid + 1;
                num = half;
            }
        }
        if (-1 == ret) {
            ret = lo;
        }
        return ret;
    }
    
    public static int bsearch(final Ordered[] array, final Ordered ref) {
        return bsearch(array, ref, 0, array.length - 1);
    }
}
