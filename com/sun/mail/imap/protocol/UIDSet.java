package com.sun.mail.imap.protocol;

import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;

public class UIDSet
{
    public long start;
    public long end;
    
    public UIDSet() {
    }
    
    public UIDSet(final long start, final long end) {
        this.start = start;
        this.end = end;
    }
    
    public long size() {
        return this.end - this.start + 1L;
    }
    
    public static UIDSet[] createUIDSets(final long[] uids) {
        if (uids == null) {
            return null;
        }
        final List<UIDSet> v = new ArrayList<UIDSet>();
        int j;
        for (int i = 0; i < uids.length; i = j - 1, ++i) {
            final UIDSet ms = new UIDSet();
            ms.start = uids[i];
            for (j = i + 1; j < uids.length && uids[j] == uids[j - 1] + 1L; ++j) {}
            ms.end = uids[j - 1];
            v.add(ms);
        }
        final UIDSet[] uidset = new UIDSet[v.size()];
        return v.toArray(uidset);
    }
    
    public static UIDSet[] parseUIDSets(final String uids) {
        if (uids == null) {
            return null;
        }
        final List<UIDSet> v = new ArrayList<UIDSet>();
        final StringTokenizer st = new StringTokenizer(uids, ",:", true);
        final long start = -1L;
        UIDSet cur = null;
        try {
            while (st.hasMoreTokens()) {
                final String s = st.nextToken();
                if (s.equals(",")) {
                    if (cur != null) {
                        v.add(cur);
                    }
                    cur = null;
                }
                else {
                    if (s.equals(":")) {
                        continue;
                    }
                    final long n = Long.parseLong(s);
                    if (cur != null) {
                        cur.end = n;
                    }
                    else {
                        cur = new UIDSet(n, n);
                    }
                }
            }
        }
        catch (final NumberFormatException ex) {}
        if (cur != null) {
            v.add(cur);
        }
        final UIDSet[] uidset = new UIDSet[v.size()];
        return v.toArray(uidset);
    }
    
    public static String toString(final UIDSet[] uidset) {
        if (uidset == null) {
            return null;
        }
        if (uidset.length == 0) {
            return "";
        }
        int i = 0;
        final StringBuilder s = new StringBuilder();
        final int size = uidset.length;
        while (true) {
            final long start = uidset[i].start;
            final long end = uidset[i].end;
            if (end > start) {
                s.append(start).append(':').append(end);
            }
            else {
                s.append(start);
            }
            if (++i >= size) {
                break;
            }
            s.append(',');
        }
        return s.toString();
    }
    
    public static long[] toArray(final UIDSet[] uidset) {
        if (uidset == null) {
            return null;
        }
        final long[] uids = new long[(int)size(uidset)];
        int i = 0;
        for (final UIDSet u : uidset) {
            for (long n = u.start; n <= u.end; ++n) {
                uids[i++] = n;
            }
        }
        return uids;
    }
    
    public static long[] toArray(final UIDSet[] uidset, final long uidmax) {
        if (uidset == null) {
            return null;
        }
        final long[] uids = new long[(int)size(uidset, uidmax)];
        int i = 0;
        for (final UIDSet u : uidset) {
            for (long n = u.start; n <= u.end && (uidmax < 0L || n <= uidmax); ++n) {
                uids[i++] = n;
            }
        }
        return uids;
    }
    
    public static long size(final UIDSet[] uidset) {
        long count = 0L;
        if (uidset != null) {
            for (final UIDSet u : uidset) {
                count += u.size();
            }
        }
        return count;
    }
    
    private static long size(final UIDSet[] uidset, final long uidmax) {
        long count = 0L;
        if (uidset != null) {
            for (final UIDSet u : uidset) {
                if (uidmax < 0L) {
                    count += u.size();
                }
                else if (u.start <= uidmax) {
                    if (u.end < uidmax) {
                        count += u.end - u.start + 1L;
                    }
                    else {
                        count += uidmax - u.start + 1L;
                    }
                }
            }
        }
        return count;
    }
}
