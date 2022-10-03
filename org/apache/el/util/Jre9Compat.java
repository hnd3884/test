package org.apache.el.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

class Jre9Compat extends JreCompat
{
    private static final Method canAccessMethod;
    
    public static boolean isSupported() {
        return Jre9Compat.canAccessMethod != null;
    }
    
    @Override
    public boolean canAccess(final Object base, final AccessibleObject accessibleObject) {
        try {
            return (boolean)Jre9Compat.canAccessMethod.invoke(accessibleObject, base);
        }
        catch (final ReflectiveOperationException | IllegalArgumentException e) {
            return false;
        }
    }
    
    static {
        Method m1 = null;
        try {
            m1 = AccessibleObject.class.getMethod("canAccess", Object.class);
        }
        catch (final NoSuchMethodException ex) {}
        canAccessMethod = m1;
    }
}
