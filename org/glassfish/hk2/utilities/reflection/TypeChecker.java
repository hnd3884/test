package org.glassfish.hk2.utilities.reflection;

import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

public class TypeChecker
{
    public static boolean isRawTypeSafe(final Type requiredType, final Type beanType) {
        Class<?> requiredClass = ReflectionHelper.getRawClass(requiredType);
        if (requiredClass == null) {
            return false;
        }
        requiredClass = ReflectionHelper.translatePrimitiveType(requiredClass);
        Class<?> beanClass = ReflectionHelper.getRawClass(beanType);
        if (beanClass == null) {
            return false;
        }
        beanClass = ReflectionHelper.translatePrimitiveType(beanClass);
        if (!requiredClass.isAssignableFrom(beanClass)) {
            return false;
        }
        if (requiredType instanceof Class || requiredType instanceof GenericArrayType) {
            return true;
        }
        if (!(requiredType instanceof ParameterizedType)) {
            throw new IllegalArgumentException("requiredType " + requiredType + " is of unknown type");
        }
        final ParameterizedType requiredPT = (ParameterizedType)requiredType;
        final Type[] requiredTypeVariables = requiredPT.getActualTypeArguments();
        Type[] beanTypeVariables;
        if (beanType instanceof Class) {
            beanTypeVariables = ((Class)beanType).getTypeParameters();
        }
        else {
            if (!(beanType instanceof ParameterizedType)) {
                throw new IllegalArgumentException("Uknown beanType " + beanType);
            }
            beanTypeVariables = ((ParameterizedType)beanType).getActualTypeArguments();
        }
        if (requiredTypeVariables.length != beanTypeVariables.length) {
            return false;
        }
        for (int lcv = 0; lcv < requiredTypeVariables.length; ++lcv) {
            final Type requiredTypeVariable = requiredTypeVariables[lcv];
            final Type beanTypeVariable = beanTypeVariables[lcv];
            if (isActualType(requiredTypeVariable) && isActualType(beanTypeVariable)) {
                if (!isRawTypeSafe(requiredTypeVariable, beanTypeVariable)) {
                    return false;
                }
            }
            else if (isArrayType(requiredTypeVariable) && isArrayType(beanTypeVariable)) {
                final Type requiredArrayType = getArrayType(requiredTypeVariable);
                final Type beanArrayType = getArrayType(beanTypeVariable);
                if (!isRawTypeSafe(requiredArrayType, beanArrayType)) {
                    return false;
                }
            }
            else if (isWildcard(requiredTypeVariable) && isActualType(beanTypeVariable)) {
                final WildcardType wt = getWildcard(requiredTypeVariable);
                final Class<?> beanActualType = ReflectionHelper.getRawClass(beanTypeVariable);
                if (!isWildcardActualSafe(wt, beanActualType)) {
                    return false;
                }
            }
            else if (isWildcard(requiredTypeVariable) && isTypeVariable(beanTypeVariable)) {
                final WildcardType wt = getWildcard(requiredTypeVariable);
                final TypeVariable<?> tv = getTypeVariable(beanTypeVariable);
                if (!isWildcardTypeVariableSafe(wt, tv)) {
                    return false;
                }
            }
            else if (isActualType(requiredTypeVariable) && isTypeVariable(beanTypeVariable)) {
                final Class<?> requiredActual = ReflectionHelper.getRawClass(requiredTypeVariable);
                final TypeVariable<?> tv = getTypeVariable(beanTypeVariable);
                if (!isActualTypeVariableSafe(requiredActual, tv)) {
                    return false;
                }
            }
            else {
                if (!isTypeVariable(requiredTypeVariable) || !isTypeVariable(beanTypeVariable)) {
                    return false;
                }
                final TypeVariable<?> rtv = getTypeVariable(requiredTypeVariable);
                final TypeVariable<?> btv = getTypeVariable(beanTypeVariable);
                if (!isTypeVariableTypeVariableSafe(rtv, btv)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private static boolean isTypeVariableTypeVariableSafe(final TypeVariable<?> rtv, final TypeVariable<?> btv) {
        final Class<?> rtvBound = getBound(rtv.getBounds());
        if (rtvBound == null) {
            return false;
        }
        final Class<?> btvBound = getBound(btv.getBounds());
        return btvBound != null && btvBound.isAssignableFrom(rtvBound);
    }
    
    private static boolean isActualTypeVariableSafe(final Class<?> actual, final TypeVariable<?> tv) {
        final Class<?> tvBound = getBound(tv.getBounds());
        return tvBound != null && actual.isAssignableFrom(tvBound);
    }
    
    private static boolean isWildcardTypeVariableSafe(final WildcardType wildcard, final TypeVariable<?> tv) {
        final Class<?> tvBound = getBound(tv.getBounds());
        if (tvBound == null) {
            return false;
        }
        final Class<?> upperBound = getBound(wildcard.getUpperBounds());
        if (upperBound == null) {
            return false;
        }
        if (!upperBound.isAssignableFrom(tvBound)) {
            return false;
        }
        final Class<?> lowerBound = getBound(wildcard.getLowerBounds());
        return lowerBound == null || tvBound.isAssignableFrom(lowerBound);
    }
    
    private static Class<?> getBound(final Type[] bounds) {
        if (bounds == null) {
            return null;
        }
        if (bounds.length < 1) {
            return null;
        }
        if (bounds.length > 1) {
            throw new AssertionError((Object)"Do not understand multiple bounds");
        }
        return ReflectionHelper.getRawClass(bounds[0]);
    }
    
    private static boolean isWildcardActualSafe(final WildcardType wildcard, final Class<?> actual) {
        final Class<?> upperBound = getBound(wildcard.getUpperBounds());
        if (upperBound == null) {
            return false;
        }
        if (!upperBound.isAssignableFrom(actual)) {
            return false;
        }
        final Class<?> lowerBound = getBound(wildcard.getLowerBounds());
        return lowerBound == null || actual.isAssignableFrom(lowerBound);
    }
    
    private static WildcardType getWildcard(final Type type) {
        if (type == null) {
            return null;
        }
        if (type instanceof WildcardType) {
            return (WildcardType)type;
        }
        return null;
    }
    
    private static TypeVariable<?> getTypeVariable(final Type type) {
        if (type == null) {
            return null;
        }
        if (type instanceof TypeVariable) {
            return (TypeVariable)type;
        }
        return null;
    }
    
    private static boolean isWildcard(final Type type) {
        return type != null && type instanceof WildcardType;
    }
    
    private static boolean isTypeVariable(final Type type) {
        return type != null && type instanceof TypeVariable;
    }
    
    private static boolean isActualType(final Type type) {
        return type != null && (type instanceof Class || type instanceof ParameterizedType);
    }
    
    private static boolean isArrayType(final Type type) {
        if (type == null) {
            return false;
        }
        if (type instanceof Class) {
            final Class<?> clazz = (Class<?>)type;
            return clazz.isArray();
        }
        return type instanceof GenericArrayType;
    }
    
    private static Type getArrayType(final Type type) {
        if (type == null) {
            return null;
        }
        if (type instanceof Class) {
            final Class<?> clazz = (Class<?>)type;
            return clazz.getComponentType();
        }
        if (type instanceof GenericArrayType) {
            final GenericArrayType gat = (GenericArrayType)type;
            return gat.getGenericComponentType();
        }
        return null;
    }
}
