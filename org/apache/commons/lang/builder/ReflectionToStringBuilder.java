package org.apache.commons.lang.builder;

import java.util.HashSet;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Modifier;
import java.lang.reflect.Field;
import java.util.Set;

public class ReflectionToStringBuilder extends ToStringBuilder
{
    private static ThreadLocal registry;
    private boolean appendTransients;
    private Class upToClass;
    
    static Set getRegistry() {
        return ReflectionToStringBuilder.registry.get();
    }
    
    static boolean isRegistered(final Object value) {
        return getRegistry().contains(value);
    }
    
    static void register(final Object value) {
        getRegistry().add(value);
    }
    
    public static String toString(final Object object) {
        return toString(object, null, false, null);
    }
    
    public static String toString(final Object object, final ToStringStyle style) {
        return toString(object, style, false, null);
    }
    
    public static String toString(final Object object, final ToStringStyle style, final boolean outputTransients) {
        return toString(object, style, outputTransients, null);
    }
    
    public static String toString(final Object object, final ToStringStyle style, final boolean outputTransients, final Class reflectUpToClass) {
        return new ReflectionToStringBuilder(object, style, null, reflectUpToClass, outputTransients).toString();
    }
    
    static void unregister(final Object value) {
        getRegistry().remove(value);
    }
    
    public ReflectionToStringBuilder(final Object object) {
        super(object);
        this.appendTransients = false;
        this.upToClass = null;
    }
    
    public ReflectionToStringBuilder(final Object object, final ToStringStyle style) {
        super(object, style);
        this.appendTransients = false;
        this.upToClass = null;
    }
    
    public ReflectionToStringBuilder(final Object object, final ToStringStyle style, final StringBuffer buffer) {
        super(object, style, buffer);
        this.appendTransients = false;
        this.upToClass = null;
    }
    
    public ReflectionToStringBuilder(final Object object, final ToStringStyle style, final StringBuffer buffer, final Class reflectUpToClass, final boolean outputTransients) {
        super(object, style, buffer);
        this.appendTransients = false;
        this.upToClass = null;
        this.setUpToClass(reflectUpToClass);
        this.setAppendTransients(outputTransients);
    }
    
    protected boolean accept(final Field field) {
        final String fieldName = field.getName();
        return fieldName.indexOf(36) == -1 && (this.isAppendTransients() || !Modifier.isTransient(field.getModifiers())) && !Modifier.isStatic(field.getModifiers());
    }
    
    protected void appendFieldsIn(final Class clazz) {
        if (isRegistered(this.getObject())) {
            this.appendAsObjectToString(this.getObject());
            return;
        }
        try {
            this.registerObject();
            if (clazz.isArray()) {
                this.reflectionAppendArray(this.getObject());
                return;
            }
            final Field[] fields = clazz.getDeclaredFields();
            AccessibleObject.setAccessible(fields, true);
            for (int i = 0; i < fields.length; ++i) {
                final Field field = fields[i];
                final String fieldName = field.getName();
                if (this.accept(field)) {
                    try {
                        final Object fieldValue = this.getValue(field);
                        if (isRegistered(fieldValue) && !field.getType().isPrimitive()) {
                            this.getStyle().appendFieldStart(this.getStringBuffer(), fieldName);
                            this.appendAsObjectToString(fieldValue);
                        }
                        else {
                            try {
                                this.registerObject();
                                this.append(fieldName, fieldValue);
                            }
                            finally {
                                this.unregisterObject();
                            }
                        }
                    }
                    catch (final IllegalAccessException ex) {
                        throw new InternalError("Unexpected IllegalAccessException: " + ex.getMessage());
                    }
                }
            }
        }
        finally {
            this.unregisterObject();
        }
    }
    
    public Class getUpToClass() {
        return this.upToClass;
    }
    
    protected Object getValue(final Field field) throws IllegalArgumentException, IllegalAccessException {
        return field.get(this.getObject());
    }
    
    public boolean isAppendTransients() {
        return this.appendTransients;
    }
    
    public ToStringBuilder reflectionAppendArray(final Object array) {
        this.getStyle().reflectionAppendArrayDetail(this.getStringBuffer(), null, array);
        return this;
    }
    
    void registerObject() {
        register(this.getObject());
    }
    
    public void setAppendTransients(final boolean appendTransients) {
        this.appendTransients = appendTransients;
    }
    
    public void setUpToClass(final Class clazz) {
        this.upToClass = clazz;
    }
    
    public String toString() {
        if (this.getObject() == null) {
            return this.getStyle().getNullText();
        }
        Class clazz = this.getObject().getClass();
        this.appendFieldsIn(clazz);
        while (clazz.getSuperclass() != null && clazz != this.getUpToClass()) {
            clazz = clazz.getSuperclass();
            this.appendFieldsIn(clazz);
        }
        return super.toString();
    }
    
    void unregisterObject() {
        unregister(this.getObject());
    }
    
    static {
        ReflectionToStringBuilder.registry = new ThreadLocal() {
            protected synchronized Object initialValue() {
                return new HashSet();
            }
        };
    }
}
