package com.sun.xml.internal.bind.v2.model.nav;

import java.lang.reflect.WildcardType;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.GenericDeclaration;
import com.sun.xml.internal.bind.v2.runtime.Location;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

final class ReflectionNavigator implements Navigator<Type, Class, Field, Method>
{
    private static final ReflectionNavigator INSTANCE;
    private static final TypeVisitor<Type, Class> baseClassFinder;
    private static final TypeVisitor<Type, BinderArg> binder;
    private static final TypeVisitor<Class, Void> eraser;
    
    static ReflectionNavigator getInstance() {
        return ReflectionNavigator.INSTANCE;
    }
    
    private ReflectionNavigator() {
    }
    
    @Override
    public Class getSuperClass(final Class clazz) {
        if (clazz == Object.class) {
            return null;
        }
        Class sc = clazz.getSuperclass();
        if (sc == null) {
            sc = Object.class;
        }
        return sc;
    }
    
    @Override
    public Type getBaseClass(final Type t, final Class sup) {
        return ReflectionNavigator.baseClassFinder.visit(t, sup);
    }
    
    @Override
    public String getClassName(final Class clazz) {
        return clazz.getName();
    }
    
    @Override
    public String getTypeName(final Type type) {
        if (!(type instanceof Class)) {
            return type.toString();
        }
        final Class c = (Class)type;
        if (c.isArray()) {
            return this.getTypeName((Type)c.getComponentType()) + "[]";
        }
        return c.getName();
    }
    
    @Override
    public String getClassShortName(final Class clazz) {
        return clazz.getSimpleName();
    }
    
    @Override
    public Collection<? extends Field> getDeclaredFields(final Class clazz) {
        return Arrays.asList(clazz.getDeclaredFields());
    }
    
    @Override
    public Field getDeclaredField(final Class clazz, final String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        }
        catch (final NoSuchFieldException e) {
            return null;
        }
    }
    
    @Override
    public Collection<? extends Method> getDeclaredMethods(final Class clazz) {
        return Arrays.asList(clazz.getDeclaredMethods());
    }
    
    @Override
    public Class getDeclaringClassForField(final Field field) {
        return field.getDeclaringClass();
    }
    
    @Override
    public Class getDeclaringClassForMethod(final Method method) {
        return method.getDeclaringClass();
    }
    
    @Override
    public Type getFieldType(final Field field) {
        if (field.getType().isArray()) {
            final Class c = field.getType().getComponentType();
            if (c.isPrimitive()) {
                return Array.newInstance(c, 0).getClass();
            }
        }
        return this.fix(field.getGenericType());
    }
    
    @Override
    public String getFieldName(final Field field) {
        return field.getName();
    }
    
    @Override
    public String getMethodName(final Method method) {
        return method.getName();
    }
    
    @Override
    public Type getReturnType(final Method method) {
        return this.fix(method.getGenericReturnType());
    }
    
    @Override
    public Type[] getMethodParameters(final Method method) {
        return method.getGenericParameterTypes();
    }
    
    @Override
    public boolean isStaticMethod(final Method method) {
        return Modifier.isStatic(method.getModifiers());
    }
    
    @Override
    public boolean isFinalMethod(final Method method) {
        return Modifier.isFinal(method.getModifiers());
    }
    
    @Override
    public boolean isSubClassOf(final Type sub, final Type sup) {
        return this.erasure(sup).isAssignableFrom(this.erasure(sub));
    }
    
    @Override
    public Class ref(final Class c) {
        return c;
    }
    
    @Override
    public Class use(final Class c) {
        return c;
    }
    
    @Override
    public Class asDecl(final Type t) {
        return this.erasure(t);
    }
    
    @Override
    public Class asDecl(final Class c) {
        return c;
    }
    
    @Override
    public <T> Class<T> erasure(final Type t) {
        return ReflectionNavigator.eraser.visit(t, null);
    }
    
    @Override
    public boolean isAbstract(final Class clazz) {
        return Modifier.isAbstract(clazz.getModifiers());
    }
    
    @Override
    public boolean isFinal(final Class clazz) {
        return Modifier.isFinal(clazz.getModifiers());
    }
    
    public Type createParameterizedType(final Class rawType, final Type... arguments) {
        return new ParameterizedTypeImpl(rawType, arguments, null);
    }
    
    @Override
    public boolean isArray(final Type t) {
        if (t instanceof Class) {
            final Class c = (Class)t;
            return c.isArray();
        }
        return t instanceof GenericArrayType;
    }
    
    @Override
    public boolean isArrayButNotByteArray(Type t) {
        if (t instanceof Class) {
            final Class c = (Class)t;
            return c.isArray() && c != byte[].class;
        }
        if (t instanceof GenericArrayType) {
            t = ((GenericArrayType)t).getGenericComponentType();
            return t != Byte.TYPE;
        }
        return false;
    }
    
    @Override
    public Type getComponentType(final Type t) {
        if (t instanceof Class) {
            final Class c = (Class)t;
            return c.getComponentType();
        }
        if (t instanceof GenericArrayType) {
            return ((GenericArrayType)t).getGenericComponentType();
        }
        throw new IllegalArgumentException();
    }
    
    @Override
    public Type getTypeArgument(final Type type, final int i) {
        if (type instanceof ParameterizedType) {
            final ParameterizedType p = (ParameterizedType)type;
            return this.fix(p.getActualTypeArguments()[i]);
        }
        throw new IllegalArgumentException();
    }
    
    @Override
    public boolean isParameterizedType(final Type type) {
        return type instanceof ParameterizedType;
    }
    
    @Override
    public boolean isPrimitive(final Type type) {
        if (type instanceof Class) {
            final Class c = (Class)type;
            return c.isPrimitive();
        }
        return false;
    }
    
    @Override
    public Type getPrimitive(final Class primitiveType) {
        assert primitiveType.isPrimitive();
        return primitiveType;
    }
    
    @Override
    public Location getClassLocation(final Class clazz) {
        return new Location() {
            @Override
            public String toString() {
                return clazz.getName();
            }
        };
    }
    
    @Override
    public Location getFieldLocation(final Field field) {
        return new Location() {
            @Override
            public String toString() {
                return field.toString();
            }
        };
    }
    
    @Override
    public Location getMethodLocation(final Method method) {
        return new Location() {
            @Override
            public String toString() {
                return method.toString();
            }
        };
    }
    
    @Override
    public boolean hasDefaultConstructor(final Class c) {
        try {
            c.getDeclaredConstructor((Class[])new Class[0]);
            return true;
        }
        catch (final NoSuchMethodException e) {
            return false;
        }
    }
    
    @Override
    public boolean isStaticField(final Field field) {
        return Modifier.isStatic(field.getModifiers());
    }
    
    @Override
    public boolean isPublicMethod(final Method method) {
        return Modifier.isPublic(method.getModifiers());
    }
    
    @Override
    public boolean isPublicField(final Field field) {
        return Modifier.isPublic(field.getModifiers());
    }
    
    @Override
    public boolean isEnum(final Class c) {
        return Enum.class.isAssignableFrom(c);
    }
    
    @Override
    public Field[] getEnumConstants(final Class clazz) {
        try {
            final Object[] values = clazz.getEnumConstants();
            final Field[] fields = new Field[values.length];
            for (int i = 0; i < values.length; ++i) {
                fields[i] = clazz.getField(((Enum)values[i]).name());
            }
            return fields;
        }
        catch (final NoSuchFieldException e) {
            throw new NoSuchFieldError(e.getMessage());
        }
    }
    
    @Override
    public Type getVoidType() {
        return Void.class;
    }
    
    @Override
    public String getPackageName(final Class clazz) {
        final String name = clazz.getName();
        final int idx = name.lastIndexOf(46);
        if (idx < 0) {
            return "";
        }
        return name.substring(0, idx);
    }
    
    @Override
    public Class loadObjectFactory(final Class referencePoint, final String pkg) {
        ClassLoader cl = SecureLoader.getClassClassLoader(referencePoint);
        if (cl == null) {
            cl = SecureLoader.getSystemClassLoader();
        }
        try {
            return cl.loadClass(pkg + ".ObjectFactory");
        }
        catch (final ClassNotFoundException e) {
            return null;
        }
    }
    
    @Override
    public boolean isBridgeMethod(final Method method) {
        return method.isBridge();
    }
    
    @Override
    public boolean isOverriding(final Method method, Class base) {
        final String name = method.getName();
        final Class[] params = method.getParameterTypes();
        while (base != null) {
            try {
                if (base.getDeclaredMethod(name, (Class[])params) != null) {
                    return true;
                }
            }
            catch (final NoSuchMethodException ex) {}
            base = base.getSuperclass();
        }
        return false;
    }
    
    @Override
    public boolean isInterface(final Class clazz) {
        return clazz.isInterface();
    }
    
    @Override
    public boolean isTransient(final Field f) {
        return Modifier.isTransient(f.getModifiers());
    }
    
    @Override
    public boolean isInnerClass(final Class clazz) {
        return clazz.getEnclosingClass() != null && !Modifier.isStatic(clazz.getModifiers());
    }
    
    @Override
    public boolean isSameType(final Type t1, final Type t2) {
        return t1.equals(t2);
    }
    
    private Type fix(final Type t) {
        if (!(t instanceof GenericArrayType)) {
            return t;
        }
        final GenericArrayType gat = (GenericArrayType)t;
        if (gat.getGenericComponentType() instanceof Class) {
            final Class c = (Class)gat.getGenericComponentType();
            return Array.newInstance(c, 0).getClass();
        }
        return t;
    }
    
    static {
        INSTANCE = new ReflectionNavigator();
        baseClassFinder = new TypeVisitor<Type, Class>() {
            public Type onClass(final Class c, final Class sup) {
                if (sup == c) {
                    return sup;
                }
                final Type sc = c.getGenericSuperclass();
                if (sc != null) {
                    final Type r = this.visit(sc, sup);
                    if (r != null) {
                        return r;
                    }
                }
                for (final Type i : c.getGenericInterfaces()) {
                    final Type r = this.visit(i, sup);
                    if (r != null) {
                        return r;
                    }
                }
                return null;
            }
            
            public Type onParameterizdType(final ParameterizedType p, final Class sup) {
                final Class raw = (Class)p.getRawType();
                if (raw == sup) {
                    return p;
                }
                Type r = raw.getGenericSuperclass();
                if (r != null) {
                    r = this.visit(this.bind(r, raw, p), sup);
                }
                if (r != null) {
                    return r;
                }
                for (final Type i : raw.getGenericInterfaces()) {
                    r = this.visit(this.bind(i, raw, p), sup);
                    if (r != null) {
                        return r;
                    }
                }
                return null;
            }
            
            public Type onGenericArray(final GenericArrayType g, final Class sup) {
                return null;
            }
            
            public Type onVariable(final TypeVariable v, final Class sup) {
                return this.visit(v.getBounds()[0], sup);
            }
            
            public Type onWildcard(final WildcardType w, final Class sup) {
                return null;
            }
            
            private Type bind(final Type t, final GenericDeclaration decl, final ParameterizedType args) {
                return ReflectionNavigator.binder.visit(t, new BinderArg(decl, args.getActualTypeArguments()));
            }
        };
        binder = new TypeVisitor<Type, BinderArg>() {
            public Type onClass(final Class c, final BinderArg args) {
                return c;
            }
            
            public Type onParameterizdType(final ParameterizedType p, final BinderArg args) {
                final Type[] params = p.getActualTypeArguments();
                boolean different = false;
                for (int i = 0; i < params.length; ++i) {
                    final Type t = params[i];
                    params[i] = this.visit(t, args);
                    different |= (t != params[i]);
                }
                Type newOwner = p.getOwnerType();
                if (newOwner != null) {
                    newOwner = this.visit(newOwner, args);
                }
                different |= (p.getOwnerType() != newOwner);
                if (!different) {
                    return p;
                }
                return new ParameterizedTypeImpl((Class<?>)p.getRawType(), params, newOwner);
            }
            
            public Type onGenericArray(final GenericArrayType g, final BinderArg types) {
                final Type c = this.visit(g.getGenericComponentType(), types);
                if (c == g.getGenericComponentType()) {
                    return g;
                }
                return new GenericArrayTypeImpl(c);
            }
            
            public Type onVariable(final TypeVariable v, final BinderArg types) {
                return types.replace(v);
            }
            
            public Type onWildcard(final WildcardType w, final BinderArg types) {
                final Type[] lb = w.getLowerBounds();
                final Type[] ub = w.getUpperBounds();
                boolean diff = false;
                for (int i = 0; i < lb.length; ++i) {
                    final Type t = lb[i];
                    lb[i] = this.visit(t, types);
                    diff |= (t != lb[i]);
                }
                for (int i = 0; i < ub.length; ++i) {
                    final Type t = ub[i];
                    ub[i] = this.visit(t, types);
                    diff |= (t != ub[i]);
                }
                if (!diff) {
                    return w;
                }
                return new WildcardTypeImpl(lb, ub);
            }
        };
        eraser = new TypeVisitor<Class, Void>() {
            public Class onClass(final Class c, final Void v) {
                return c;
            }
            
            public Class onParameterizdType(final ParameterizedType p, final Void v) {
                return this.visit(p.getRawType(), null);
            }
            
            public Class onGenericArray(final GenericArrayType g, final Void v) {
                return Array.newInstance(((TypeVisitor<Class<?>, Void>)this).visit(g.getGenericComponentType(), null), 0).getClass();
            }
            
            public Class onVariable(final TypeVariable tv, final Void v) {
                return this.visit(tv.getBounds()[0], null);
            }
            
            public Class onWildcard(final WildcardType w, final Void v) {
                return this.visit(w.getUpperBounds()[0], null);
            }
        };
    }
    
    private static class BinderArg
    {
        final TypeVariable[] params;
        final Type[] args;
        
        BinderArg(final TypeVariable[] params, final Type[] args) {
            this.params = params;
            this.args = args;
            assert params.length == args.length;
        }
        
        public BinderArg(final GenericDeclaration decl, final Type[] args) {
            this(decl.getTypeParameters(), args);
        }
        
        Type replace(final TypeVariable v) {
            for (int i = 0; i < this.params.length; ++i) {
                if (this.params[i].equals(v)) {
                    return this.args[i];
                }
            }
            return v;
        }
    }
}
