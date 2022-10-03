package com.azul.crs.com.fasterxml.jackson.databind.introspect;

import java.lang.reflect.WildcardType;
import java.lang.reflect.Type;
import java.util.List;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Objects;
import java.lang.reflect.ParameterizedType;
import com.azul.crs.com.fasterxml.jackson.databind.type.TypeBindings;
import com.azul.crs.com.fasterxml.jackson.databind.type.TypeFactory;
import com.azul.crs.com.fasterxml.jackson.databind.JavaType;
import java.lang.reflect.Method;

final class MethodGenericTypeResolver
{
    public static TypeResolutionContext narrowMethodTypeParameters(final Method candidate, final JavaType requestedType, final TypeFactory typeFactory, final TypeResolutionContext emptyTypeResCtxt) {
        final TypeBindings newTypeBindings = bindMethodTypeParameters(candidate, requestedType, emptyTypeResCtxt);
        return (newTypeBindings == null) ? emptyTypeResCtxt : new TypeResolutionContext.Basic(typeFactory, newTypeBindings);
    }
    
    static TypeBindings bindMethodTypeParameters(final Method candidate, final JavaType requestedType, final TypeResolutionContext emptyTypeResCtxt) {
        final TypeVariable<Method>[] methodTypeParameters = candidate.getTypeParameters();
        if (methodTypeParameters.length == 0 || requestedType.getBindings().isEmpty()) {
            return null;
        }
        final Type genericReturnType = candidate.getGenericReturnType();
        if (!(genericReturnType instanceof ParameterizedType)) {
            return null;
        }
        final ParameterizedType parameterizedGenericReturnType = (ParameterizedType)genericReturnType;
        if (!Objects.equals(requestedType.getRawClass(), parameterizedGenericReturnType.getRawType())) {
            return null;
        }
        final Type[] methodReturnTypeArguments = parameterizedGenericReturnType.getActualTypeArguments();
        final ArrayList<String> names = new ArrayList<String>(methodTypeParameters.length);
        final ArrayList<JavaType> types = new ArrayList<JavaType>(methodTypeParameters.length);
        for (int i = 0; i < methodReturnTypeArguments.length; ++i) {
            final Type methodReturnTypeArgument = methodReturnTypeArguments[i];
            final TypeVariable<?> typeVar = maybeGetTypeVariable(methodReturnTypeArgument);
            if (typeVar != null) {
                final String typeParameterName = typeVar.getName();
                if (typeParameterName == null) {
                    return null;
                }
                final JavaType bindTarget = requestedType.getBindings().getBoundType(i);
                if (bindTarget == null) {
                    return null;
                }
                final TypeVariable<?> methodTypeVariable = findByName(methodTypeParameters, typeParameterName);
                if (methodTypeVariable == null) {
                    return null;
                }
                if (pessimisticallyValidateBounds(emptyTypeResCtxt, bindTarget, methodTypeVariable.getBounds())) {
                    final int existingIndex = names.indexOf(typeParameterName);
                    if (existingIndex != -1) {
                        final JavaType existingBindTarget = types.get(existingIndex);
                        if (!bindTarget.equals(existingBindTarget)) {
                            final boolean existingIsSubtype = existingBindTarget.isTypeOrSubTypeOf(bindTarget.getRawClass());
                            final boolean newIsSubtype = bindTarget.isTypeOrSubTypeOf(existingBindTarget.getRawClass());
                            if (!existingIsSubtype && !newIsSubtype) {
                                return null;
                            }
                            if ((existingIsSubtype ^ newIsSubtype) && newIsSubtype) {
                                types.set(existingIndex, bindTarget);
                            }
                        }
                    }
                    else {
                        names.add(typeParameterName);
                        types.add(bindTarget);
                    }
                }
            }
        }
        if (names.isEmpty()) {
            return null;
        }
        return TypeBindings.create(names, types);
    }
    
    private static TypeVariable<?> maybeGetTypeVariable(final Type type) {
        if (type instanceof TypeVariable) {
            return (TypeVariable)type;
        }
        if (type instanceof WildcardType) {
            final WildcardType wildcardType = (WildcardType)type;
            if (wildcardType.getLowerBounds().length != 0) {
                return null;
            }
            final Type[] upperBounds = wildcardType.getUpperBounds();
            if (upperBounds.length == 1) {
                return maybeGetTypeVariable(upperBounds[0]);
            }
        }
        return null;
    }
    
    private static ParameterizedType maybeGetParameterizedType(final Type type) {
        if (type instanceof ParameterizedType) {
            return (ParameterizedType)type;
        }
        if (type instanceof WildcardType) {
            final WildcardType wildcardType = (WildcardType)type;
            if (wildcardType.getLowerBounds().length != 0) {
                return null;
            }
            final Type[] upperBounds = wildcardType.getUpperBounds();
            if (upperBounds.length == 1) {
                return maybeGetParameterizedType(upperBounds[0]);
            }
        }
        return null;
    }
    
    private static boolean pessimisticallyValidateBounds(final TypeResolutionContext context, final JavaType boundType, final Type[] upperBound) {
        for (final Type type : upperBound) {
            if (!pessimisticallyValidateBound(context, boundType, type)) {
                return false;
            }
        }
        return true;
    }
    
    private static boolean pessimisticallyValidateBound(final TypeResolutionContext context, final JavaType boundType, final Type type) {
        if (!boundType.isTypeOrSubTypeOf(context.resolveType(type).getRawClass())) {
            return false;
        }
        final ParameterizedType parameterized = maybeGetParameterizedType(type);
        if (parameterized != null && Objects.equals(boundType.getRawClass(), parameterized.getRawType())) {
            final Type[] typeArguments = parameterized.getActualTypeArguments();
            final TypeBindings bindings = boundType.getBindings();
            if (bindings.size() != typeArguments.length) {
                return false;
            }
            for (int i = 0; i < bindings.size(); ++i) {
                final JavaType boundTypeBound = bindings.getBoundType(i);
                final Type typeArg = typeArguments[i];
                if (!pessimisticallyValidateBound(context, boundTypeBound, typeArg)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private static TypeVariable<?> findByName(final TypeVariable<?>[] typeVariables, final String name) {
        if (typeVariables == null || name == null) {
            return null;
        }
        for (final TypeVariable<?> typeVariable : typeVariables) {
            if (name.equals(typeVariable.getName())) {
                return typeVariable;
            }
        }
        return null;
    }
}
