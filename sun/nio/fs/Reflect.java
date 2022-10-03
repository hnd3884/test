package sun.nio.fs;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.lang.reflect.AccessibleObject;

class Reflect
{
    private Reflect() {
    }
    
    private static void setAccessible(final AccessibleObject accessibleObject) {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                accessibleObject.setAccessible(true);
                return null;
            }
        });
    }
    
    static Field lookupField(final String s, final String s2) {
        try {
            final Field declaredField = Class.forName(s).getDeclaredField(s2);
            setAccessible(declaredField);
            return declaredField;
        }
        catch (final ClassNotFoundException ex) {
            throw new AssertionError((Object)ex);
        }
        catch (final NoSuchFieldException ex2) {
            throw new AssertionError((Object)ex2);
        }
    }
}
