package sun.tracing.dtrace;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.lang.reflect.Method;

class JVM
{
    static long activate(final String s, final DTraceProvider[] array) {
        return activate0(s, array);
    }
    
    static void dispose(final long n) {
        dispose0(n);
    }
    
    static boolean isEnabled(final Method method) {
        return isEnabled0(method);
    }
    
    static boolean isSupported() {
        return isSupported0();
    }
    
    static Class<?> defineClass(final ClassLoader classLoader, final String s, final byte[] array, final int n, final int n2) {
        return defineClass0(classLoader, s, array, n, n2);
    }
    
    private static native long activate0(final String p0, final DTraceProvider[] p1);
    
    private static native void dispose0(final long p0);
    
    private static native boolean isEnabled0(final Method p0);
    
    private static native boolean isSupported0();
    
    private static native Class<?> defineClass0(final ClassLoader p0, final String p1, final byte[] p2, final int p3, final int p4);
    
    static {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                System.loadLibrary("jsdt");
                return null;
            }
        });
    }
}
