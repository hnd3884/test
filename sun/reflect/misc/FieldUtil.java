package sun.reflect.misc;

import java.lang.reflect.Field;

public final class FieldUtil
{
    private FieldUtil() {
    }
    
    public static Field getField(final Class<?> clazz, final String s) throws NoSuchFieldException {
        ReflectUtil.checkPackageAccess(clazz);
        return clazz.getField(s);
    }
    
    public static Field[] getFields(final Class<?> clazz) {
        ReflectUtil.checkPackageAccess(clazz);
        return clazz.getFields();
    }
    
    public static Field[] getDeclaredFields(final Class<?> clazz) {
        ReflectUtil.checkPackageAccess(clazz);
        return clazz.getDeclaredFields();
    }
}
