package sun.reflect;

import java.security.ProtectionDomain;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.misc.Unsafe;

class ClassDefiner
{
    static final Unsafe unsafe;
    
    static Class<?> defineClass(final String s, final byte[] array, final int n, final int n2, final ClassLoader classLoader) {
        return ClassDefiner.unsafe.defineClass(s, array, n, n2, AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction<ClassLoader>() {
            @Override
            public ClassLoader run() {
                return new DelegatingClassLoader(classLoader);
            }
        }), null);
    }
    
    static {
        unsafe = Unsafe.getUnsafe();
    }
}
