package javax.ws.rs.core;

import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Stack;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class GenericType<T>
{
    private final Type type;
    private final Class<?> rawType;
    
    public static GenericType forInstance(final Object instance) {
        GenericType genericType;
        if (instance instanceof GenericEntity) {
            genericType = new GenericType(((GenericEntity)instance).getType());
        }
        else {
            genericType = ((instance == null) ? null : new GenericType(instance.getClass()));
        }
        return genericType;
    }
    
    protected GenericType() {
        this.type = getTypeArgument(this.getClass(), GenericType.class);
        this.rawType = getClass(this.type);
    }
    
    public GenericType(final Type genericType) {
        if (genericType == null) {
            throw new IllegalArgumentException("Type must not be null");
        }
        this.type = genericType;
        this.rawType = getClass(this.type);
    }
    
    public final Type getType() {
        return this.type;
    }
    
    public final Class<?> getRawType() {
        return this.rawType;
    }
    
    private static Class getClass(final Type type) {
        if (type instanceof Class) {
            return (Class)type;
        }
        if (type instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType)type;
            if (parameterizedType.getRawType() instanceof Class) {
                return (Class)parameterizedType.getRawType();
            }
        }
        else if (type instanceof GenericArrayType) {
            final GenericArrayType array = (GenericArrayType)type;
            final Class<?> componentRawType = getClass(array.getGenericComponentType());
            return getArrayClass(componentRawType);
        }
        throw new IllegalArgumentException("Type parameter " + type.toString() + " not a class or parameterized type whose raw type is a class");
    }
    
    private static Class getArrayClass(final Class c) {
        try {
            final Object o = Array.newInstance(c, 0);
            return o.getClass();
        }
        catch (final Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    static Type getTypeArgument(final Class<?> clazz, final Class<?> baseClass) {
        final Stack<Type> superclasses = new Stack<Type>();
        Class<?> currentClass = clazz;
        Type currentType;
        do {
            currentType = currentClass.getGenericSuperclass();
            superclasses.push(currentType);
            if (currentType instanceof Class) {
                currentClass = (Class)currentType;
            }
            else {
                if (!(currentType instanceof ParameterizedType)) {
                    continue;
                }
                currentClass = (Class)((ParameterizedType)currentType).getRawType();
            }
        } while (!currentClass.equals(baseClass));
        TypeVariable tv = baseClass.getTypeParameters()[0];
        while (!superclasses.isEmpty()) {
            currentType = superclasses.pop();
            if (!(currentType instanceof ParameterizedType)) {
                break;
            }
            final ParameterizedType pt = (ParameterizedType)currentType;
            final Class<?> rawType = (Class<?>)pt.getRawType();
            final int argIndex = Arrays.asList(rawType.getTypeParameters()).indexOf(tv);
            if (argIndex <= -1) {
                break;
            }
            final Type typeArg = pt.getActualTypeArguments()[argIndex];
            if (!(typeArg instanceof TypeVariable)) {
                return typeArg;
            }
            tv = (TypeVariable)typeArg;
        }
        throw new IllegalArgumentException(currentType + " does not specify the type parameter T of GenericType<T>");
    }
    
    @Override
    public boolean equals(final Object obj) {
        final boolean result = this == obj;
        if (!result && obj instanceof GenericType) {
            final GenericType<?> that = (GenericType<?>)obj;
            return this.type.equals(that.type);
        }
        return result;
    }
    
    @Override
    public int hashCode() {
        return this.type.hashCode();
    }
    
    @Override
    public String toString() {
        return "GenericType{" + this.type.toString() + "}";
    }
}
