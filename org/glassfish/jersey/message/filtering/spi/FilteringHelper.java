package org.glassfish.jersey.message.filtering.spi;

import org.glassfish.jersey.internal.util.collection.DataStructures;
import java.util.Collections;
import java.util.Set;
import java.util.Iterator;
import java.security.PrivilegedAction;
import java.security.AccessController;
import java.util.Collection;
import java.util.HashMap;
import java.lang.reflect.Method;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Map;
import java.lang.reflect.ParameterizedType;
import javax.xml.bind.JAXBElement;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentMap;
import java.lang.annotation.Annotation;

public final class FilteringHelper
{
    public static final Annotation[] EMPTY_ANNOTATIONS;
    private static final ConcurrentMap<Type, Class<?>> ENTITY_CLASSES;
    
    public static boolean filterableEntityClass(final Class<?> clazz) {
        return !ReflectionHelper.isPrimitive((Type)clazz) && !clazz.getPackage().getName().startsWith("java.");
    }
    
    public static Class<?> getEntityClass(final Type genericType) {
        if (!FilteringHelper.ENTITY_CLASSES.containsKey(genericType)) {
            FilteringHelper.ENTITY_CLASSES.putIfAbsent(genericType, _getEntityClass(genericType));
        }
        return FilteringHelper.ENTITY_CLASSES.get(genericType);
    }
    
    private static Class<?> _getEntityClass(final Type genericType) {
        if (null == genericType) {
            return Object.class;
        }
        if (genericType instanceof Class && genericType != JAXBElement.class) {
            final Class<?> clazz = (Class<?>)genericType;
            if (clazz.isArray()) {
                return _getEntityClass(clazz.getComponentType());
            }
            return clazz;
        }
        else {
            if (genericType instanceof ParameterizedType) {
                final ParameterizedType parameterizedType = (ParameterizedType)genericType;
                final Type[] arguments = parameterizedType.getActualTypeArguments();
                final Type type = (parameterizedType.getRawType() == Map.class) ? arguments[1] : arguments[0];
                if (type instanceof ParameterizedType) {
                    final Type rawType = ((ParameterizedType)type).getRawType();
                    if (rawType == JAXBElement.class) {
                        return _getEntityClass(type);
                    }
                }
                else if (type instanceof WildcardType) {
                    final Type[] upperTypes = ((WildcardType)type).getUpperBounds();
                    if (upperTypes.length > 0) {
                        final Type upperType = upperTypes[0];
                        if (upperType instanceof Class) {
                            return (Class)upperType;
                        }
                    }
                }
                else if (JAXBElement.class == type || type instanceof TypeVariable) {
                    return Object.class;
                }
                return (Class)type;
            }
            if (genericType instanceof GenericArrayType) {
                final GenericArrayType genericArrayType = (GenericArrayType)genericType;
                return _getEntityClass(genericArrayType.getGenericComponentType());
            }
            return Object.class;
        }
    }
    
    public static Map<String, Method> getPropertyMethods(final Class<?> clazz, final boolean isGetter) {
        final Map<String, Method> methods = new HashMap<String, Method>();
        for (final Method method : AccessController.doPrivileged((PrivilegedAction<Collection>)ReflectionHelper.getDeclaredMethodsPA((Class)clazz))) {
            if ((isGetter && ReflectionHelper.isGetter(method)) || (!isGetter && ReflectionHelper.isSetter(method))) {
                methods.put(ReflectionHelper.getPropertyName(method), method);
            }
        }
        final Class<?> parent = clazz.getSuperclass();
        if (parent != null && !parent.getPackage().getName().startsWith("java.lang")) {
            methods.putAll(getPropertyMethods(parent, isGetter));
        }
        return methods;
    }
    
    public static Set<String> getDefaultFilteringScope() {
        return Collections.singleton(ScopeProvider.DEFAULT_SCOPE);
    }
    
    private FilteringHelper() {
    }
    
    static {
        EMPTY_ANNOTATIONS = new Annotation[0];
        ENTITY_CLASSES = DataStructures.createConcurrentMap();
    }
}
