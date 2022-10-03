package org.apache.tika.config;

import java.util.HashMap;
import java.util.Locale;
import java.lang.reflect.InvocationTargetException;
import org.apache.tika.exception.TikaConfigException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.Map;

public class ParamField
{
    public static final String DEFAULT = "#default";
    private static final Map<Class<?>, Class<?>> PRIMITIVE_MAP;
    private final String name;
    private final Class<?> type;
    private final boolean required;
    private Field field;
    private Method setter;
    
    public ParamField(final AccessibleObject member) throws TikaConfigException {
        if (member instanceof Field) {
            this.field = (Field)member;
        }
        else {
            this.setter = (Method)member;
        }
        final org.apache.tika.config.Field annotation = member.getAnnotation(org.apache.tika.config.Field.class);
        this.required = annotation.required();
        this.name = this.retrieveParamName(annotation);
        this.type = this.retrieveType();
    }
    
    public Field getField() {
        return this.field;
    }
    
    public Method getSetter() {
        return this.setter;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Class<?> getType() {
        return this.type;
    }
    
    public boolean isRequired() {
        return this.required;
    }
    
    public void assignValue(final Object bean, final Object value) throws IllegalAccessException, InvocationTargetException {
        if (this.field != null) {
            this.field.set(bean, value);
        }
        else {
            this.setter.invoke(bean, value);
        }
    }
    
    private Class retrieveType() throws TikaConfigException {
        Class type;
        if (this.field != null) {
            type = this.field.getType();
        }
        else {
            final Class[] params = this.setter.getParameterTypes();
            if (params.length != 1) {
                String msg = "Invalid setter method. Must have one and only one parameter. ";
                if (this.setter.getName().startsWith("get")) {
                    msg = msg + "Perhaps the annotation is misplaced on " + this.setter.getName() + " while a set'X' is expected?";
                }
                throw new TikaConfigException(msg);
            }
            type = params[0];
        }
        if (type.isPrimitive() && ParamField.PRIMITIVE_MAP.containsKey(type)) {
            type = ParamField.PRIMITIVE_MAP.get(type);
        }
        return type;
    }
    
    private String retrieveParamName(final org.apache.tika.config.Field annotation) {
        String name;
        if (annotation.name().equals("#default")) {
            if (this.field != null) {
                name = this.field.getName();
            }
            else {
                final String setterName = this.setter.getName();
                if (setterName.startsWith("set") && setterName.length() > 3) {
                    name = setterName.substring(3, 4).toLowerCase(Locale.ROOT) + setterName.substring(4);
                }
                else {
                    name = this.setter.getName();
                }
            }
        }
        else {
            name = annotation.name();
        }
        return name;
    }
    
    @Override
    public String toString() {
        return "ParamField{name='" + this.name + '\'' + ", type=" + this.type + ", required=" + this.required + '}';
    }
    
    static {
        PRIMITIVE_MAP = new HashMap<Class<?>, Class<?>>() {
            {
                ((HashMap<Class<Integer>, Class<Integer>>)this).put(Integer.TYPE, Integer.class);
                ((HashMap<Class<Short>, Class<Short>>)this).put(Short.TYPE, Short.class);
                ((HashMap<Class<Boolean>, Class<Boolean>>)this).put(Boolean.TYPE, Boolean.class);
                ((HashMap<Class<Long>, Class<Long>>)this).put(Long.TYPE, Long.class);
                ((HashMap<Class<Float>, Class<Float>>)this).put(Float.TYPE, Float.class);
                ((HashMap<Class<Double>, Class<Double>>)this).put(Double.TYPE, Double.class);
            }
        };
    }
}
