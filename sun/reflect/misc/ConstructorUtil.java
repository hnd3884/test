package sun.reflect.misc;

import java.lang.reflect.Constructor;

public final class ConstructorUtil
{
    private ConstructorUtil() {
    }
    
    public static Constructor<?> getConstructor(final Class<?> clazz, final Class<?>[] array) throws NoSuchMethodException {
        ReflectUtil.checkPackageAccess(clazz);
        return clazz.getConstructor(array);
    }
    
    public static Constructor<?>[] getConstructors(final Class<?> clazz) {
        ReflectUtil.checkPackageAccess(clazz);
        return clazz.getConstructors();
    }
}
