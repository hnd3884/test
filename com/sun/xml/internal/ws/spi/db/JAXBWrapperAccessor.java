package com.sun.xml.internal.ws.spi.db;

import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.lang.reflect.Method;
import java.util.HashMap;

public class JAXBWrapperAccessor extends WrapperAccessor
{
    protected Class<?> contentClass;
    protected HashMap<Object, Class> elementDeclaredTypes;
    
    public JAXBWrapperAccessor(final Class<?> wrapperBean) {
        this.contentClass = wrapperBean;
        final HashMap<Object, PropertySetter> setByQName = new HashMap<Object, PropertySetter>();
        final HashMap<Object, PropertySetter> setByLocalpart = new HashMap<Object, PropertySetter>();
        final HashMap<String, Method> publicSetters = new HashMap<String, Method>();
        final HashMap<Object, PropertyGetter> getByQName = new HashMap<Object, PropertyGetter>();
        final HashMap<Object, PropertyGetter> getByLocalpart = new HashMap<Object, PropertyGetter>();
        final HashMap<String, Method> publicGetters = new HashMap<String, Method>();
        final HashMap<Object, Class> elementDeclaredTypesByQName = new HashMap<Object, Class>();
        final HashMap<Object, Class> elementDeclaredTypesByLocalpart = new HashMap<Object, Class>();
        for (final Method method : this.contentClass.getMethods()) {
            if (PropertySetterBase.setterPattern(method)) {
                final String key = method.getName().substring(3, method.getName().length()).toLowerCase();
                publicSetters.put(key, method);
            }
            if (PropertyGetterBase.getterPattern(method)) {
                final String methodName = method.getName();
                final String key2 = methodName.startsWith("is") ? methodName.substring(2, method.getName().length()).toLowerCase() : methodName.substring(3, method.getName().length()).toLowerCase();
                publicGetters.put(key2, method);
            }
        }
        final HashSet<String> elementLocalNames = new HashSet<String>();
        for (final Field field : getAllFields(this.contentClass)) {
            final XmlElementWrapper xmlElemWrapper = field.getAnnotation(XmlElementWrapper.class);
            final XmlElement xmlElem = field.getAnnotation(XmlElement.class);
            final XmlElementRef xmlElemRef = field.getAnnotation(XmlElementRef.class);
            String fieldName = field.getName().toLowerCase();
            String namespace = "";
            String localName = field.getName();
            if (xmlElemWrapper != null) {
                namespace = xmlElemWrapper.namespace();
                if (xmlElemWrapper.name() != null && !xmlElemWrapper.name().equals("") && !xmlElemWrapper.name().equals("##default")) {
                    localName = xmlElemWrapper.name();
                }
            }
            else if (xmlElem != null) {
                namespace = xmlElem.namespace();
                if (xmlElem.name() != null && !xmlElem.name().equals("") && !xmlElem.name().equals("##default")) {
                    localName = xmlElem.name();
                }
            }
            else if (xmlElemRef != null) {
                namespace = xmlElemRef.namespace();
                if (xmlElemRef.name() != null && !xmlElemRef.name().equals("") && !xmlElemRef.name().equals("##default")) {
                    localName = xmlElemRef.name();
                }
            }
            if (elementLocalNames.contains(localName)) {
                this.elementLocalNameCollision = true;
            }
            else {
                elementLocalNames.add(localName);
            }
            final QName qname = new QName(namespace, localName);
            if (field.getType().equals(JAXBElement.class) && field.getGenericType() instanceof ParameterizedType) {
                final Type arg = ((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
                if (arg instanceof Class) {
                    elementDeclaredTypesByQName.put(qname, (Class)arg);
                    elementDeclaredTypesByLocalpart.put(localName, (Class)arg);
                }
                else if (arg instanceof GenericArrayType) {
                    final Type componentType = ((GenericArrayType)arg).getGenericComponentType();
                    if (componentType instanceof Class) {
                        final Class arrayClass = Array.newInstance((Class<?>)componentType, 0).getClass();
                        elementDeclaredTypesByQName.put(qname, arrayClass);
                        elementDeclaredTypesByLocalpart.put(localName, arrayClass);
                    }
                }
            }
            if (fieldName.startsWith("_") && !localName.startsWith("_")) {
                fieldName = fieldName.substring(1);
            }
            final Method setMethod = publicSetters.get(fieldName);
            final Method getMethod = publicGetters.get(fieldName);
            final PropertySetter setter = createPropertySetter(field, setMethod);
            final PropertyGetter getter = createPropertyGetter(field, getMethod);
            setByQName.put(qname, setter);
            setByLocalpart.put(localName, setter);
            getByQName.put(qname, getter);
            getByLocalpart.put(localName, getter);
        }
        if (this.elementLocalNameCollision) {
            this.propertySetters = setByQName;
            this.propertyGetters = getByQName;
            this.elementDeclaredTypes = elementDeclaredTypesByQName;
        }
        else {
            this.propertySetters = setByLocalpart;
            this.propertyGetters = getByLocalpart;
            this.elementDeclaredTypes = elementDeclaredTypesByLocalpart;
        }
    }
    
    protected static List<Field> getAllFields(Class<?> clz) {
        final List<Field> list = new ArrayList<Field>();
        while (!Object.class.equals(clz)) {
            list.addAll(Arrays.asList(getDeclaredFields(clz)));
            clz = clz.getSuperclass();
        }
        return list;
    }
    
    protected static Field[] getDeclaredFields(final Class<?> clz) {
        try {
            return (System.getSecurityManager() == null) ? clz.getDeclaredFields() : AccessController.doPrivileged((PrivilegedExceptionAction<Field[]>)new PrivilegedExceptionAction<Field[]>() {
                @Override
                public Field[] run() throws IllegalAccessException {
                    return clz.getDeclaredFields();
                }
            });
        }
        catch (final PrivilegedActionException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    protected static PropertyGetter createPropertyGetter(final Field field, final Method getMethod) {
        if (!field.isAccessible() && getMethod != null) {
            final MethodGetter methodGetter = new MethodGetter(getMethod);
            if (methodGetter.getType().toString().equals(field.getType().toString())) {
                return methodGetter;
            }
        }
        return new FieldGetter(field);
    }
    
    protected static PropertySetter createPropertySetter(final Field field, final Method setter) {
        if (!field.isAccessible() && setter != null) {
            final MethodSetter injection = new MethodSetter(setter);
            if (injection.getType().toString().equals(field.getType().toString())) {
                return injection;
            }
        }
        return new FieldSetter(field);
    }
    
    private Class getElementDeclaredType(final QName name) {
        final Object key = this.elementLocalNameCollision ? name : name.getLocalPart();
        return this.elementDeclaredTypes.get(key);
    }
    
    @Override
    public PropertyAccessor getPropertyAccessor(final String ns, final String name) {
        final QName n = new QName(ns, name);
        final PropertySetter setter = this.getPropertySetter(n);
        final PropertyGetter getter = this.getPropertyGetter(n);
        final boolean isJAXBElement = setter.getType().equals(JAXBElement.class);
        final boolean isListType = List.class.isAssignableFrom(setter.getType());
        final Class elementDeclaredType = isJAXBElement ? this.getElementDeclaredType(n) : null;
        return new PropertyAccessor() {
            @Override
            public Object get(final Object bean) throws DatabindingException {
                Object val;
                if (isJAXBElement) {
                    final JAXBElement<Object> jaxbElement = (JAXBElement<Object>)getter.get(bean);
                    val = ((jaxbElement == null) ? null : jaxbElement.getValue());
                }
                else {
                    val = getter.get(bean);
                }
                if (val == null && isListType) {
                    val = new ArrayList();
                    this.set(bean, val);
                }
                return val;
            }
            
            @Override
            public void set(final Object bean, final Object value) throws DatabindingException {
                if (isJAXBElement) {
                    final JAXBElement<Object> jaxbElement = new JAXBElement<Object>(n, elementDeclaredType, JAXBWrapperAccessor.this.contentClass, value);
                    setter.set(bean, jaxbElement);
                }
                else {
                    setter.set(bean, value);
                }
            }
        };
    }
}
