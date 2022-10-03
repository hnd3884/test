package org.apache.tika.utils;

public class CompareUtils
{
    public static int compareClassName(final Object o1, final Object o2) {
        final String n1 = o1.getClass().getName();
        final String n2 = o2.getClass().getName();
        final boolean tika1 = n1.startsWith("org.apache.tika.");
        final boolean tika2 = n2.startsWith("org.apache.tika.");
        if (tika1 == tika2) {
            return n1.compareTo(n2);
        }
        return tika1 ? 1 : -1;
    }
}
