package sun.invoke.util;

import java.lang.invoke.MethodType;
import sun.invoke.empty.Empty;

public class VerifyType
{
    private VerifyType() {
    }
    
    public static boolean isNullConversion(Class<?> clazz, Class<?> clazz2, final boolean b) {
        if (clazz == clazz2) {
            return true;
        }
        if (!b) {
            if (clazz2.isInterface()) {
                clazz2 = Object.class;
            }
            if (clazz.isInterface()) {
                clazz = Object.class;
            }
            if (clazz == clazz2) {
                return true;
            }
        }
        if (isNullType(clazz)) {
            return !clazz2.isPrimitive();
        }
        if (!clazz.isPrimitive()) {
            return clazz2.isAssignableFrom(clazz);
        }
        if (!clazz2.isPrimitive()) {
            return false;
        }
        final Wrapper forPrimitiveType = Wrapper.forPrimitiveType(clazz);
        if (clazz2 == Integer.TYPE) {
            return forPrimitiveType.isSubwordOrInt();
        }
        final Wrapper forPrimitiveType2 = Wrapper.forPrimitiveType(clazz2);
        return forPrimitiveType.isSubwordOrInt() && forPrimitiveType2.isSubwordOrInt() && (forPrimitiveType2.isSigned() || !forPrimitiveType.isSigned()) && forPrimitiveType2.bitWidth() > forPrimitiveType.bitWidth();
    }
    
    public static boolean isNullReferenceConversion(final Class<?> clazz, final Class<?> clazz2) {
        assert !clazz2.isPrimitive();
        return clazz2.isInterface() || isNullType(clazz) || clazz2.isAssignableFrom(clazz);
    }
    
    public static boolean isNullType(final Class<?> clazz) {
        return clazz == Void.class || clazz == Empty.class;
    }
    
    public static boolean isNullConversion(final MethodType methodType, final MethodType methodType2, final boolean b) {
        if (methodType == methodType2) {
            return true;
        }
        final int parameterCount = methodType.parameterCount();
        if (parameterCount != methodType2.parameterCount()) {
            return false;
        }
        for (int i = 0; i < parameterCount; ++i) {
            if (!isNullConversion(methodType.parameterType(i), methodType2.parameterType(i), b)) {
                return false;
            }
        }
        return isNullConversion(methodType2.returnType(), methodType.returnType(), b);
    }
    
    public static int canPassUnchecked(final Class<?> clazz, final Class<?> clazz2) {
        if (clazz == clazz2) {
            return 1;
        }
        if (clazz2.isPrimitive()) {
            if (clazz2 == Void.TYPE) {
                return 1;
            }
            if (clazz == Void.TYPE) {
                return 0;
            }
            if (!clazz.isPrimitive()) {
                return 0;
            }
            final Wrapper forPrimitiveType = Wrapper.forPrimitiveType(clazz);
            final Wrapper forPrimitiveType2 = Wrapper.forPrimitiveType(clazz2);
            if (forPrimitiveType.isSubwordOrInt() && forPrimitiveType2.isSubwordOrInt()) {
                if (forPrimitiveType.bitWidth() >= forPrimitiveType2.bitWidth()) {
                    return -1;
                }
                if (!forPrimitiveType2.isSigned() && forPrimitiveType.isSigned()) {
                    return -1;
                }
                return 1;
            }
            else {
                if (clazz != Float.TYPE && clazz2 != Float.TYPE) {
                    return 0;
                }
                if (clazz == Double.TYPE || clazz2 == Double.TYPE) {
                    return -1;
                }
                return 0;
            }
        }
        else {
            if (clazz.isPrimitive()) {
                return 0;
            }
            if (isNullReferenceConversion(clazz, clazz2)) {
                return 1;
            }
            return -1;
        }
    }
    
    public static boolean isSpreadArgType(final Class<?> clazz) {
        return clazz.isArray();
    }
    
    public static Class<?> spreadArgElementType(final Class<?> clazz, final int n) {
        return clazz.getComponentType();
    }
}
