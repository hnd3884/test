package sun.rmi.server;

import java.io.ObjectStreamClass;
import java.lang.reflect.Method;

public interface DeserializationChecker
{
    void check(final Method p0, final ObjectStreamClass p1, final int p2, final int p3);
    
    void checkProxyClass(final Method p0, final String[] p1, final int p2, final int p3);
    
    default void end(final int n) {
    }
}
