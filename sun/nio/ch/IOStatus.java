package sun.nio.ch;

public final class IOStatus
{
    public static final int EOF = -1;
    public static final int UNAVAILABLE = -2;
    public static final int INTERRUPTED = -3;
    public static final int UNSUPPORTED = -4;
    public static final int THROWN = -5;
    public static final int UNSUPPORTED_CASE = -6;
    
    private IOStatus() {
    }
    
    public static int normalize(final int n) {
        if (n == -2) {
            return 0;
        }
        return n;
    }
    
    public static boolean check(final int n) {
        return n >= -2;
    }
    
    public static long normalize(final long n) {
        if (n == -2L) {
            return 0L;
        }
        return n;
    }
    
    public static boolean check(final long n) {
        return n >= -2L;
    }
    
    public static boolean checkAll(final long n) {
        return n > -1L || n < -6L;
    }
}
