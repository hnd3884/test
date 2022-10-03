package sun.net;

import java.security.AccessController;
import java.security.PrivilegedAction;

public final class PortConfig
{
    private static int defaultUpper;
    private static int defaultLower;
    private static final int upper;
    private static final int lower;
    
    static native int getLower0();
    
    static native int getUpper0();
    
    public static int getLower() {
        return PortConfig.lower;
    }
    
    public static int getUpper() {
        return PortConfig.upper;
    }
    
    static {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                System.loadLibrary("net");
                return null;
            }
        });
        int lower2 = getLower0();
        if (lower2 == -1) {
            lower2 = PortConfig.defaultLower;
        }
        lower = lower2;
        int upper2 = getUpper0();
        if (upper2 == -1) {
            upper2 = PortConfig.defaultUpper;
        }
        upper = upper2;
    }
}
