package org.msgpack.template;

import java.lang.reflect.GenericArrayType;
import org.msgpack.MessageTypeException;
import org.msgpack.MessagePackable;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.lang.reflect.ParameterizedType;
import org.msgpack.template.builder.TemplateBuilder;
import java.util.Collection;
import java.util.Set;
import java.util.List;
import java.util.Date;
import java.math.BigDecimal;
import org.msgpack.type.Value;
import java.nio.ByteBuffer;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.lang.reflect.Type;
import java.util.Map;
import org.msgpack.template.builder.TemplateBuilderChain;

public class TemplateRegistry
{
    private TemplateRegistry parent;
    private TemplateBuilderChain chain;
    Map<Type, Template<Type>> cache;
    private Map<Type, GenericTemplate> genericCache;
    
    private TemplateRegistry() {
        this.parent = null;
        this.parent = null;
        this.chain = this.createTemplateBuilderChain();
        this.genericCache = new HashMap<Type, GenericTemplate>();
        this.cache = new HashMap<Type, Template<Type>>();
        this.registerTemplates();
        this.cache = Collections.unmodifiableMap((Map<? extends Type, ? extends Template<Type>>)this.cache);
    }
    
    public TemplateRegistry(final TemplateRegistry registry) {
        this.parent = null;
        if (registry != null) {
            this.parent = registry;
        }
        else {
            this.parent = new TemplateRegistry();
        }
        this.chain = this.createTemplateBuilderChain();
        this.cache = new HashMap<Type, Template<Type>>();
        this.genericCache = new HashMap<Type, GenericTemplate>();
        this.registerTemplatesWhichRefersRegistry();
    }
    
    protected TemplateBuilderChain createTemplateBuilderChain() {
        return new TemplateBuilderChain(this);
    }
    
    public void setClassLoader(final ClassLoader cl) {
        this.chain = new TemplateBuilderChain(this, cl);
    }
    
    private void registerTemplates() {
        this.register(Boolean.TYPE, BooleanTemplate.getInstance());
        this.register(Boolean.class, BooleanTemplate.getInstance());
        this.register(Byte.TYPE, ByteTemplate.getInstance());
        this.register(Byte.class, ByteTemplate.getInstance());
        this.register(Short.TYPE, ShortTemplate.getInstance());
        this.register(Short.class, ShortTemplate.getInstance());
        this.register(Integer.TYPE, IntegerTemplate.getInstance());
        this.register(Integer.class, IntegerTemplate.getInstance());
        this.register(Long.TYPE, LongTemplate.getInstance());
        this.register(Long.class, LongTemplate.getInstance());
        this.register(Float.TYPE, FloatTemplate.getInstance());
        this.register(Float.class, FloatTemplate.getInstance());
        this.register(Double.TYPE, DoubleTemplate.getInstance());
        this.register(Double.class, DoubleTemplate.getInstance());
        this.register(BigInteger.class, BigIntegerTemplate.getInstance());
        this.register(Character.TYPE, CharacterTemplate.getInstance());
        this.register(Character.class, CharacterTemplate.getInstance());
        this.register(boolean[].class, BooleanArrayTemplate.getInstance());
        this.register(short[].class, ShortArrayTemplate.getInstance());
        this.register(int[].class, IntegerArrayTemplate.getInstance());
        this.register(long[].class, LongArrayTemplate.getInstance());
        this.register(float[].class, FloatArrayTemplate.getInstance());
        this.register(double[].class, DoubleArrayTemplate.getInstance());
        this.register(String.class, StringTemplate.getInstance());
        this.register(byte[].class, ByteArrayTemplate.getInstance());
        this.register(ByteBuffer.class, ByteBufferTemplate.getInstance());
        this.register(Value.class, ValueTemplate.getInstance());
        this.register(BigDecimal.class, BigDecimalTemplate.getInstance());
        this.register(Date.class, DateTemplate.getInstance());
        this.registerTemplatesWhichRefersRegistry();
    }
    
    protected void registerTemplatesWhichRefersRegistry() {
        final AnyTemplate anyTemplate = new AnyTemplate(this);
        this.register(List.class, new ListTemplate(anyTemplate));
        this.register(Set.class, new SetTemplate(anyTemplate));
        this.register(Collection.class, new CollectionTemplate(anyTemplate));
        this.register(Map.class, new MapTemplate(anyTemplate, anyTemplate));
        this.registerGeneric(List.class, new GenericCollectionTemplate(this, ListTemplate.class));
        this.registerGeneric(Set.class, new GenericCollectionTemplate(this, SetTemplate.class));
        this.registerGeneric(Collection.class, new GenericCollectionTemplate(this, CollectionTemplate.class));
        this.registerGeneric(Map.class, new GenericMapTemplate(this, MapTemplate.class));
    }
    
    public void register(final Class<?> targetClass) {
        this.buildAndRegister(null, targetClass, false, null);
    }
    
    public void register(final Class<?> targetClass, final FieldList flist) {
        if (flist == null) {
            throw new NullPointerException("FieldList object is null");
        }
        this.buildAndRegister(null, targetClass, false, flist);
    }
    
    public synchronized void register(final Type targetType, final Template tmpl) {
        if (tmpl == null) {
            throw new NullPointerException("Template object is null");
        }
        if (targetType instanceof ParameterizedType) {
            this.cache.put(((ParameterizedType)targetType).getRawType(), tmpl);
        }
        else {
            this.cache.put(targetType, tmpl);
        }
    }
    
    public synchronized void registerGeneric(final Type targetType, final GenericTemplate tmpl) {
        if (targetType instanceof ParameterizedType) {
            this.genericCache.put(((ParameterizedType)targetType).getRawType(), tmpl);
        }
        else {
            this.genericCache.put(targetType, tmpl);
        }
    }
    
    public synchronized boolean unregister(final Type targetType) {
        final Template<Type> tmpl = this.cache.remove(targetType);
        return tmpl != null;
    }
    
    public synchronized void unregister() {
        this.cache.clear();
    }
    
    public synchronized Template lookup(Type targetType) {
        if (targetType instanceof ParameterizedType) {
            final ParameterizedType paramedType = (ParameterizedType)targetType;
            final Template tmpl = this.lookupGenericType(paramedType);
            if (tmpl != null) {
                return tmpl;
            }
            targetType = paramedType.getRawType();
        }
        Template tmpl = this.lookupGenericArrayType(targetType);
        if (tmpl != null) {
            return tmpl;
        }
        tmpl = this.lookupCache(targetType);
        if (tmpl != null) {
            return tmpl;
        }
        if (targetType instanceof WildcardType || targetType instanceof TypeVariable) {
            tmpl = new AnyTemplate(this);
            this.register(targetType, tmpl);
            return tmpl;
        }
        final Class<?> targetClass = (Class<?>)targetType;
        if (MessagePackable.class.isAssignableFrom(targetClass)) {
            tmpl = new MessagePackableTemplate(targetClass);
            this.register(targetClass, tmpl);
            return tmpl;
        }
        if (targetClass.isInterface()) {
            tmpl = new AnyTemplate(this);
            this.register(targetType, tmpl);
            return tmpl;
        }
        tmpl = this.lookupAfterBuilding(targetClass);
        if (tmpl != null) {
            return tmpl;
        }
        tmpl = this.lookupInterfaceTypes(targetClass);
        if (tmpl != null) {
            return tmpl;
        }
        tmpl = this.lookupSuperclasses(targetClass);
        if (tmpl != null) {
            return tmpl;
        }
        tmpl = this.lookupSuperclassInterfaceTypes(targetClass);
        if (tmpl != null) {
            return tmpl;
        }
        throw new MessageTypeException("Cannot find template for " + targetClass + " class.  " + "Try to add @Message annotation to the class or call MessagePack.register(Type).");
    }
    
    private Template<Type> lookupGenericType(final ParameterizedType paramedType) {
        Template<Type> tmpl = this.lookupGenericTypeImpl(paramedType);
        if (tmpl != null) {
            return tmpl;
        }
        try {
            tmpl = this.parent.lookupGenericTypeImpl(paramedType);
            if (tmpl != null) {
                return tmpl;
            }
        }
        catch (final NullPointerException ex) {}
        tmpl = this.lookupGenericInterfaceTypes(paramedType);
        if (tmpl != null) {
            return tmpl;
        }
        tmpl = this.lookupGenericSuperclasses(paramedType);
        if (tmpl != null) {
            return tmpl;
        }
        return null;
    }
    
    private Template lookupGenericTypeImpl(final ParameterizedType targetType) {
        final Type rawType = targetType.getRawType();
        return this.lookupGenericTypeImpl0(targetType, rawType);
    }
    
    private Template lookupGenericTypeImpl0(final ParameterizedType targetType, final Type rawType) {
        final GenericTemplate gtmpl = this.genericCache.get(rawType);
        if (gtmpl == null) {
            return null;
        }
        final Type[] types = targetType.getActualTypeArguments();
        final Template[] tmpls = new Template[types.length];
        for (int i = 0; i < types.length; ++i) {
            tmpls[i] = this.lookup(types[i]);
        }
        return gtmpl.build(tmpls);
    }
    
    private <T> Template<T> lookupGenericInterfaceTypes(final ParameterizedType targetType) {
        final Type rawType = targetType.getRawType();
        Template<T> tmpl = null;
        try {
            final Class[] arr$;
            final Class<?>[] infTypes = arr$ = ((Class)rawType).getInterfaces();
            for (final Class<?> infType : arr$) {
                tmpl = this.lookupGenericTypeImpl0(targetType, infType);
                if (tmpl != null) {
                    return tmpl;
                }
            }
        }
        catch (final ClassCastException ex) {}
        return tmpl;
    }
    
    private <T> Template<T> lookupGenericSuperclasses(final ParameterizedType targetType) {
        final Type rawType = targetType.getRawType();
        Template<T> tmpl = null;
        try {
            Class<?> superClass = ((Class)rawType).getSuperclass();
            if (superClass == null) {
                return null;
            }
            while (superClass != Object.class) {
                tmpl = this.lookupGenericTypeImpl0(targetType, superClass);
                if (tmpl != null) {
                    this.register(targetType, tmpl);
                    return tmpl;
                }
                superClass = superClass.getSuperclass();
            }
        }
        catch (final ClassCastException ex) {}
        return tmpl;
    }
    
    private Template<Type> lookupGenericArrayType(final Type targetType) {
        if (!(targetType instanceof GenericArrayType)) {
            return null;
        }
        final GenericArrayType genericArrayType = (GenericArrayType)targetType;
        Template<Type> tmpl = this.lookupGenericArrayTypeImpl(genericArrayType);
        if (tmpl != null) {
            return tmpl;
        }
        try {
            tmpl = this.parent.lookupGenericArrayTypeImpl(genericArrayType);
            if (tmpl != null) {
                return tmpl;
            }
        }
        catch (final NullPointerException ex) {}
        return null;
    }
    
    private Template lookupGenericArrayTypeImpl(final GenericArrayType genericArrayType) {
        final String genericArrayTypeName = "" + genericArrayType;
        final int dim = genericArrayTypeName.split("\\[").length - 1;
        if (dim <= 0) {
            throw new MessageTypeException(String.format("fatal error: type=", genericArrayTypeName));
        }
        if (dim > 1) {
            throw new UnsupportedOperationException(String.format("Not implemented template generation of %s", genericArrayTypeName));
        }
        final String genericCompTypeName = "" + genericArrayType.getGenericComponentType();
        final boolean isPrimitiveType = isPrimitiveType(genericCompTypeName);
        final StringBuffer sbuf = new StringBuffer();
        for (int i = 0; i < dim; ++i) {
            sbuf.append('[');
        }
        if (!isPrimitiveType) {
            sbuf.append('L');
            sbuf.append(toJvmReferenceTypeName(genericCompTypeName));
            sbuf.append(';');
        }
        else {
            sbuf.append(toJvmPrimitiveTypeName(genericCompTypeName));
        }
        final String jvmArrayClassName = sbuf.toString();
        Class jvmArrayClass = null;
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
            if (cl != null) {
                jvmArrayClass = cl.loadClass(jvmArrayClassName);
                if (jvmArrayClass != null) {
                    return this.lookupAfterBuilding((Class<Object>)jvmArrayClass);
                }
            }
        }
        catch (final ClassNotFoundException ex) {}
        try {
            cl = this.getClass().getClassLoader();
            if (cl != null) {
                jvmArrayClass = cl.loadClass(jvmArrayClassName);
                if (jvmArrayClass != null) {
                    return this.lookupAfterBuilding((Class<Object>)jvmArrayClass);
                }
            }
        }
        catch (final ClassNotFoundException ex2) {}
        try {
            jvmArrayClass = Class.forName(jvmArrayClassName);
            if (jvmArrayClass != null) {
                return this.lookupAfterBuilding((Class<Object>)jvmArrayClass);
            }
        }
        catch (final ClassNotFoundException ex3) {}
        throw new MessageTypeException(String.format("cannot find template of %s", jvmArrayClassName));
    }
    
    private Template<Type> lookupCache(final Type targetType) {
        Template<Type> tmpl = this.cache.get(targetType);
        if (tmpl != null) {
            return tmpl;
        }
        try {
            tmpl = this.parent.lookupCache(targetType);
        }
        catch (final NullPointerException ex) {}
        return tmpl;
    }
    
    private <T> Template<T> lookupAfterBuilding(final Class<T> targetClass) {
        final TemplateBuilder builder = this.chain.select(targetClass, true);
        Template<T> tmpl = null;
        if (builder != null) {
            tmpl = this.chain.getForceBuilder().loadTemplate(targetClass);
            if (tmpl != null) {
                this.register(targetClass, tmpl);
                return tmpl;
            }
            tmpl = this.buildAndRegister(builder, targetClass, true, null);
        }
        return tmpl;
    }
    
    private <T> Template<T> lookupInterfaceTypes(final Class<T> targetClass) {
        final Class<?>[] infTypes = targetClass.getInterfaces();
        Template<T> tmpl = null;
        for (final Class<?> infType : infTypes) {
            tmpl = (Template)this.cache.get(infType);
            if (tmpl != null) {
                this.register(targetClass, tmpl);
                return tmpl;
            }
            try {
                tmpl = (Template<T>)this.parent.lookupCache(infType);
                if (tmpl != null) {
                    this.register(targetClass, tmpl);
                    return tmpl;
                }
            }
            catch (final NullPointerException ex) {}
        }
        return tmpl;
    }
    
    private <T> Template<T> lookupSuperclasses(final Class<T> targetClass) {
        Class<?> superClass = targetClass.getSuperclass();
        Template<T> tmpl = null;
        if (superClass != null) {
            while (superClass != Object.class) {
                tmpl = (Template)this.cache.get(superClass);
                if (tmpl != null) {
                    this.register(targetClass, tmpl);
                    return tmpl;
                }
                try {
                    tmpl = (Template<T>)this.parent.lookupCache(superClass);
                    if (tmpl != null) {
                        this.register(targetClass, tmpl);
                        return tmpl;
                    }
                }
                catch (final NullPointerException ex) {}
                superClass = superClass.getSuperclass();
            }
        }
        return tmpl;
    }
    
    private <T> Template<T> lookupSuperclassInterfaceTypes(final Class<T> targetClass) {
        Class<?> superClass = targetClass.getSuperclass();
        Template<T> tmpl = null;
        if (superClass != null) {
            while (superClass != Object.class) {
                tmpl = (Template<T>)this.lookupInterfaceTypes(superClass);
                if (tmpl != null) {
                    this.register(targetClass, tmpl);
                    return tmpl;
                }
                try {
                    tmpl = (Template<T>)this.parent.lookupCache(superClass);
                    if (tmpl != null) {
                        this.register(targetClass, tmpl);
                        return tmpl;
                    }
                }
                catch (final NullPointerException ex) {}
                superClass = superClass.getSuperclass();
            }
        }
        return tmpl;
    }
    
    private synchronized Template buildAndRegister(TemplateBuilder builder, final Class targetClass, final boolean hasAnnotation, final FieldList flist) {
        Template newTmpl = null;
        Template oldTmpl = null;
        try {
            if (this.cache.containsKey(targetClass)) {
                oldTmpl = this.cache.get(targetClass);
            }
            newTmpl = new TemplateReference(this, targetClass);
            this.cache.put(targetClass, newTmpl);
            if (builder == null) {
                builder = this.chain.select(targetClass, hasAnnotation);
            }
            newTmpl = ((flist != null) ? builder.buildTemplate((Class<Object>)targetClass, flist) : builder.buildTemplate(targetClass));
            return newTmpl;
        }
        catch (final Exception e) {
            if (oldTmpl != null) {
                this.cache.put(targetClass, oldTmpl);
            }
            else {
                this.cache.remove(targetClass);
            }
            newTmpl = null;
            if (e instanceof MessageTypeException) {
                throw (MessageTypeException)e;
            }
            throw new MessageTypeException(e);
        }
        finally {
            if (newTmpl != null) {
                this.cache.put(targetClass, newTmpl);
            }
        }
    }
    
    private static boolean isPrimitiveType(final String genericCompTypeName) {
        return genericCompTypeName.equals("byte") || genericCompTypeName.equals("short") || genericCompTypeName.equals("int") || genericCompTypeName.equals("long") || genericCompTypeName.equals("float") || genericCompTypeName.equals("double") || genericCompTypeName.equals("boolean") || genericCompTypeName.equals("char");
    }
    
    private static String toJvmReferenceTypeName(final String typeName) {
        return typeName.substring(6);
    }
    
    private static String toJvmPrimitiveTypeName(final String typeName) {
        if (typeName.equals("byte")) {
            return "B";
        }
        if (typeName.equals("short")) {
            return "S";
        }
        if (typeName.equals("int")) {
            return "I";
        }
        if (typeName.equals("long")) {
            return "J";
        }
        if (typeName.equals("float")) {
            return "F";
        }
        if (typeName.equals("double")) {
            return "D";
        }
        if (typeName.equals("boolean")) {
            return "Z";
        }
        if (typeName.equals("char")) {
            return "C";
        }
        throw new MessageTypeException(String.format("fatal error: type=%s", typeName));
    }
}
