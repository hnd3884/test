package io.netty.util.internal;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

public abstract class TypeParameterMatcher
{
    private static final TypeParameterMatcher NOOP;
    
    public static TypeParameterMatcher get(final Class<?> parameterType) {
        final Map<Class<?>, TypeParameterMatcher> getCache = InternalThreadLocalMap.get().typeParameterMatcherGetCache();
        TypeParameterMatcher matcher = getCache.get(parameterType);
        if (matcher == null) {
            if (parameterType == Object.class) {
                matcher = TypeParameterMatcher.NOOP;
            }
            else {
                matcher = new ReflectiveMatcher(parameterType);
            }
            getCache.put(parameterType, matcher);
        }
        return matcher;
    }
    
    public static TypeParameterMatcher find(final Object object, final Class<?> parametrizedSuperclass, final String typeParamName) {
        final Map<Class<?>, Map<String, TypeParameterMatcher>> findCache = InternalThreadLocalMap.get().typeParameterMatcherFindCache();
        final Class<?> thisClass = object.getClass();
        Map<String, TypeParameterMatcher> map = findCache.get(thisClass);
        if (map == null) {
            map = new HashMap<String, TypeParameterMatcher>();
            findCache.put(thisClass, map);
        }
        TypeParameterMatcher matcher = map.get(typeParamName);
        if (matcher == null) {
            matcher = get(find0(object, parametrizedSuperclass, typeParamName));
            map.put(typeParamName, matcher);
        }
        return matcher;
    }
    
    private static Class<?> find0(final Object object, Class<?> parametrizedSuperclass, String typeParamName) {
        Class<?> currentClass;
        final Class<?> thisClass = currentClass = object.getClass();
        while (true) {
            if (currentClass.getSuperclass() == parametrizedSuperclass) {
                int typeParamIndex = -1;
                final TypeVariable<?>[] typeParams = currentClass.getSuperclass().getTypeParameters();
                for (int i = 0; i < typeParams.length; ++i) {
                    if (typeParamName.equals(typeParams[i].getName())) {
                        typeParamIndex = i;
                        break;
                    }
                }
                if (typeParamIndex < 0) {
                    throw new IllegalStateException("unknown type parameter '" + typeParamName + "': " + parametrizedSuperclass);
                }
                final Type genericSuperType = currentClass.getGenericSuperclass();
                if (!(genericSuperType instanceof ParameterizedType)) {
                    return Object.class;
                }
                final Type[] actualTypeParams = ((ParameterizedType)genericSuperType).getActualTypeArguments();
                Type actualTypeParam = actualTypeParams[typeParamIndex];
                if (actualTypeParam instanceof ParameterizedType) {
                    actualTypeParam = ((ParameterizedType)actualTypeParam).getRawType();
                }
                if (actualTypeParam instanceof Class) {
                    return (Class)actualTypeParam;
                }
                if (actualTypeParam instanceof GenericArrayType) {
                    Type componentType = ((GenericArrayType)actualTypeParam).getGenericComponentType();
                    if (componentType instanceof ParameterizedType) {
                        componentType = ((ParameterizedType)componentType).getRawType();
                    }
                    if (componentType instanceof Class) {
                        return Array.newInstance((Class<?>)componentType, 0).getClass();
                    }
                }
                if (!(actualTypeParam instanceof TypeVariable)) {
                    return fail(thisClass, typeParamName);
                }
                final TypeVariable<?> v = (TypeVariable<?>)actualTypeParam;
                if (!(v.getGenericDeclaration() instanceof Class)) {
                    return Object.class;
                }
                currentClass = thisClass;
                parametrizedSuperclass = (Class)v.getGenericDeclaration();
                typeParamName = v.getName();
                if (parametrizedSuperclass.isAssignableFrom(thisClass)) {
                    continue;
                }
                return Object.class;
            }
            else {
                currentClass = currentClass.getSuperclass();
                if (currentClass == null) {
                    return fail(thisClass, typeParamName);
                }
                continue;
            }
        }
    }
    
    private static Class<?> fail(final Class<?> type, final String typeParamName) {
        throw new IllegalStateException("cannot determine the type of the type parameter '" + typeParamName + "': " + type);
    }
    
    public abstract boolean match(final Object p0);
    
    TypeParameterMatcher() {
    }
    
    static {
        NOOP = new TypeParameterMatcher() {
            @Override
            public boolean match(final Object msg) {
                return true;
            }
        };
    }
    
    private static final class ReflectiveMatcher extends TypeParameterMatcher
    {
        private final Class<?> type;
        
        ReflectiveMatcher(final Class<?> type) {
            this.type = type;
        }
        
        @Override
        public boolean match(final Object msg) {
            return this.type.isInstance(msg);
        }
    }
}
