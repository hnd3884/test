package sun.util.calendar;

public class CalendarUtils
{
    public static final boolean isGregorianLeapYear(final int n) {
        return n % 4 == 0 && (n % 100 != 0 || n % 400 == 0);
    }
    
    public static final boolean isJulianLeapYear(final int n) {
        return n % 4 == 0;
    }
    
    public static final long floorDivide(final long n, final long n2) {
        return (n >= 0L) ? (n / n2) : ((n + 1L) / n2 - 1L);
    }
    
    public static final int floorDivide(final int n, final int n2) {
        return (n >= 0) ? (n / n2) : ((n + 1) / n2 - 1);
    }
    
    public static final int floorDivide(final int n, final int n2, final int[] array) {
        if (n >= 0) {
            array[0] = n % n2;
            return n / n2;
        }
        final int n3 = (n + 1) / n2 - 1;
        array[0] = n - n3 * n2;
        return n3;
    }
    
    public static final int floorDivide(final long n, final int n2, final int[] array) {
        if (n >= 0L) {
            array[0] = (int)(n % n2);
            return (int)(n / n2);
        }
        final int n3 = (int)((n + 1L) / n2 - 1L);
        array[0] = (int)(n - n3 * n2);
        return n3;
    }
    
    public static final long mod(final long n, final long n2) {
        return n - n2 * floorDivide(n, n2);
    }
    
    public static final int mod(final int n, final int n2) {
        return n - n2 * floorDivide(n, n2);
    }
    
    public static final int amod(final int n, final int n2) {
        final int mod = mod(n, n2);
        return (mod == 0) ? n2 : mod;
    }
    
    public static final long amod(final long n, final long n2) {
        final long mod = mod(n, n2);
        return (mod == 0L) ? n2 : mod;
    }
    
    public static final StringBuilder sprintf0d(final StringBuilder sb, final int n, int n2) {
        long n3 = n;
        if (n3 < 0L) {
            sb.append('-');
            n3 = -n3;
            --n2;
        }
        int n4 = 10;
        for (int i = 2; i < n2; ++i) {
            n4 *= 10;
        }
        for (int n5 = 1; n5 < n2 && n3 < n4; n4 /= 10, ++n5) {
            sb.append('0');
        }
        sb.append(n3);
        return sb;
    }
    
    public static final StringBuffer sprintf0d(final StringBuffer sb, final int n, int n2) {
        long n3 = n;
        if (n3 < 0L) {
            sb.append('-');
            n3 = -n3;
            --n2;
        }
        int n4 = 10;
        for (int i = 2; i < n2; ++i) {
            n4 *= 10;
        }
        for (int n5 = 1; n5 < n2 && n3 < n4; n4 /= 10, ++n5) {
            sb.append('0');
        }
        sb.append(n3);
        return sb;
    }
}
