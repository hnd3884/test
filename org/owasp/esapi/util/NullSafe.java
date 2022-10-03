package org.owasp.esapi.util;

public class NullSafe
{
    private NullSafe() {
    }
    
    public static boolean equals(final Object a, final Object b) {
        if (a == b) {
            return true;
        }
        if (a == null) {
            return b == null;
        }
        return b != null && a.equals(b);
    }
    
    public static int hashCode(final Object o) {
        if (o == null) {
            return 0;
        }
        return o.hashCode();
    }
    
    public static String toString(final Object o) {
        if (o == null) {
            return "(null)";
        }
        return o.toString();
    }
}
