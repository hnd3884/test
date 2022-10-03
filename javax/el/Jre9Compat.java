package javax.el;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

class Jre9Compat extends JreCompat
{
    private static final Method canAccessMethod;
    private static final Method getModuleMethod;
    private static final Method isExportedMethod;
    
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
    
    @Override
    public boolean isExported(final Class<?> type) {
        try {
            final String packageName = type.getPackage().getName();
            final Object module = Jre9Compat.getModuleMethod.invoke(type, new Object[0]);
            return (boolean)Jre9Compat.isExportedMethod.invoke(module, packageName);
        }
        catch (final ReflectiveOperationException e) {
            return false;
        }
    }
    
    static {
        Method m1 = null;
        Method m2 = null;
        Method m3 = null;
        try {
            m1 = AccessibleObject.class.getMethod("canAccess", Object.class);
            m2 = Class.class.getMethod("getModule", (Class<?>[])new Class[0]);
            final Class<?> moduleClass = Class.forName("java.lang.Module");
            m3 = moduleClass.getMethod("isExported", String.class);
        }
        catch (final NoSuchMethodException ex) {}
        catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        canAccessMethod = m1;
        getModuleMethod = m2;
        isExportedMethod = m3;
    }
}
