package org.apache.commons.beanutils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Modifier;
import java.lang.ref.SoftReference;
import java.beans.IntrospectionException;
import java.lang.reflect.Method;
import java.lang.ref.Reference;
import java.beans.PropertyDescriptor;

public class MappedPropertyDescriptor extends PropertyDescriptor
{
    private Reference<Class<?>> mappedPropertyTypeRef;
    private MappedMethodReference mappedReadMethodRef;
    private MappedMethodReference mappedWriteMethodRef;
    private static final Class<?>[] STRING_CLASS_PARAMETER;
    
    public MappedPropertyDescriptor(final String propertyName, final Class<?> beanClass) throws IntrospectionException {
        super(propertyName, null, null);
        if (propertyName == null || propertyName.length() == 0) {
            throw new IntrospectionException("bad property name: " + propertyName + " on class: " + beanClass.getClass().getName());
        }
        this.setName(propertyName);
        final String base = capitalizePropertyName(propertyName);
        Method mappedReadMethod = null;
        Method mappedWriteMethod = null;
        try {
            try {
                mappedReadMethod = getMethod(beanClass, "get" + base, MappedPropertyDescriptor.STRING_CLASS_PARAMETER);
            }
            catch (final IntrospectionException e) {
                mappedReadMethod = getMethod(beanClass, "is" + base, MappedPropertyDescriptor.STRING_CLASS_PARAMETER);
            }
            final Class<?>[] params = { String.class, mappedReadMethod.getReturnType() };
            mappedWriteMethod = getMethod(beanClass, "set" + base, params);
        }
        catch (final IntrospectionException ex) {}
        if (mappedReadMethod == null) {
            mappedWriteMethod = getMethod(beanClass, "set" + base, 2);
        }
        if (mappedReadMethod == null && mappedWriteMethod == null) {
            throw new IntrospectionException("Property '" + propertyName + "' not found on " + beanClass.getName());
        }
        this.mappedReadMethodRef = new MappedMethodReference(mappedReadMethod);
        this.mappedWriteMethodRef = new MappedMethodReference(mappedWriteMethod);
        this.findMappedPropertyType();
    }
    
    public MappedPropertyDescriptor(final String propertyName, final Class<?> beanClass, final String mappedGetterName, final String mappedSetterName) throws IntrospectionException {
        super(propertyName, null, null);
        if (propertyName == null || propertyName.length() == 0) {
            throw new IntrospectionException("bad property name: " + propertyName);
        }
        this.setName(propertyName);
        Method mappedReadMethod = null;
        Method mappedWriteMethod = null;
        mappedReadMethod = getMethod(beanClass, mappedGetterName, MappedPropertyDescriptor.STRING_CLASS_PARAMETER);
        if (mappedReadMethod != null) {
            final Class<?>[] params = { String.class, mappedReadMethod.getReturnType() };
            mappedWriteMethod = getMethod(beanClass, mappedSetterName, params);
        }
        else {
            mappedWriteMethod = getMethod(beanClass, mappedSetterName, 2);
        }
        this.mappedReadMethodRef = new MappedMethodReference(mappedReadMethod);
        this.mappedWriteMethodRef = new MappedMethodReference(mappedWriteMethod);
        this.findMappedPropertyType();
    }
    
    public MappedPropertyDescriptor(final String propertyName, final Method mappedGetter, final Method mappedSetter) throws IntrospectionException {
        super(propertyName, mappedGetter, mappedSetter);
        if (propertyName == null || propertyName.length() == 0) {
            throw new IntrospectionException("bad property name: " + propertyName);
        }
        this.setName(propertyName);
        this.mappedReadMethodRef = new MappedMethodReference(mappedGetter);
        this.mappedWriteMethodRef = new MappedMethodReference(mappedSetter);
        this.findMappedPropertyType();
    }
    
    public Class<?> getMappedPropertyType() {
        return this.mappedPropertyTypeRef.get();
    }
    
    public Method getMappedReadMethod() {
        return this.mappedReadMethodRef.get();
    }
    
    public void setMappedReadMethod(final Method mappedGetter) throws IntrospectionException {
        this.mappedReadMethodRef = new MappedMethodReference(mappedGetter);
        this.findMappedPropertyType();
    }
    
    public Method getMappedWriteMethod() {
        return this.mappedWriteMethodRef.get();
    }
    
    public void setMappedWriteMethod(final Method mappedSetter) throws IntrospectionException {
        this.mappedWriteMethodRef = new MappedMethodReference(mappedSetter);
        this.findMappedPropertyType();
    }
    
    private void findMappedPropertyType() throws IntrospectionException {
        try {
            final Method mappedReadMethod = this.getMappedReadMethod();
            final Method mappedWriteMethod = this.getMappedWriteMethod();
            Class<?> mappedPropertyType = null;
            if (mappedReadMethod != null) {
                if (mappedReadMethod.getParameterTypes().length != 1) {
                    throw new IntrospectionException("bad mapped read method arg count");
                }
                mappedPropertyType = mappedReadMethod.getReturnType();
                if (mappedPropertyType == Void.TYPE) {
                    throw new IntrospectionException("mapped read method " + mappedReadMethod.getName() + " returns void");
                }
            }
            if (mappedWriteMethod != null) {
                final Class<?>[] params = mappedWriteMethod.getParameterTypes();
                if (params.length != 2) {
                    throw new IntrospectionException("bad mapped write method arg count");
                }
                if (mappedPropertyType != null && mappedPropertyType != params[1]) {
                    throw new IntrospectionException("type mismatch between mapped read and write methods");
                }
                mappedPropertyType = params[1];
            }
            this.mappedPropertyTypeRef = new SoftReference<Class<?>>(mappedPropertyType);
        }
        catch (final IntrospectionException ex) {
            throw ex;
        }
    }
    
    private static String capitalizePropertyName(final String s) {
        if (s.length() == 0) {
            return s;
        }
        final char[] chars = s.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }
    
    private static Method internalGetMethod(final Class<?> initial, final String methodName, final int parameterCount) {
        for (Class<?> clazz = initial; clazz != null; clazz = clazz.getSuperclass()) {
            final Method[] declaredMethods;
            final Method[] methods = declaredMethods = clazz.getDeclaredMethods();
            for (final Method method : declaredMethods) {
                if (method != null) {
                    final int mods = method.getModifiers();
                    if (Modifier.isPublic(mods)) {
                        if (!Modifier.isStatic(mods)) {
                            if (method.getName().equals(methodName) && method.getParameterTypes().length == parameterCount) {
                                return method;
                            }
                        }
                    }
                }
            }
        }
        final Class<?>[] interfaces2;
        final Class<?>[] interfaces = interfaces2 = initial.getInterfaces();
        for (final Class<?> interface1 : interfaces2) {
            final Method method = internalGetMethod(interface1, methodName, parameterCount);
            if (method != null) {
                return method;
            }
        }
        return null;
    }
    
    private static Method getMethod(final Class<?> clazz, final String methodName, final int parameterCount) throws IntrospectionException {
        if (methodName == null) {
            return null;
        }
        final Method method = internalGetMethod(clazz, methodName, parameterCount);
        if (method != null) {
            return method;
        }
        throw new IntrospectionException("No method \"" + methodName + "\" with " + parameterCount + " parameter(s)");
    }
    
    private static Method getMethod(final Class<?> clazz, final String methodName, final Class<?>[] parameterTypes) throws IntrospectionException {
        if (methodName == null) {
            return null;
        }
        final Method method = MethodUtils.getMatchingAccessibleMethod(clazz, methodName, parameterTypes);
        if (method != null) {
            return method;
        }
        final int parameterCount = (parameterTypes == null) ? 0 : parameterTypes.length;
        throw new IntrospectionException("No method \"" + methodName + "\" with " + parameterCount + " parameter(s) of matching types.");
    }
    
    static {
        STRING_CLASS_PARAMETER = new Class[] { String.class };
    }
    
    private static class MappedMethodReference
    {
        private String className;
        private String methodName;
        private Reference<Method> methodRef;
        private Reference<Class<?>> classRef;
        private Reference<Class<?>> writeParamTypeRef0;
        private Reference<Class<?>> writeParamTypeRef1;
        private String[] writeParamClassNames;
        
        MappedMethodReference(final Method m) {
            if (m != null) {
                this.className = m.getDeclaringClass().getName();
                this.methodName = m.getName();
                this.methodRef = new SoftReference<Method>(m);
                this.classRef = new WeakReference<Class<?>>(m.getDeclaringClass());
                final Class<?>[] types = m.getParameterTypes();
                if (types.length == 2) {
                    this.writeParamTypeRef0 = new WeakReference<Class<?>>(types[0]);
                    this.writeParamTypeRef1 = new WeakReference<Class<?>>(types[1]);
                    (this.writeParamClassNames = new String[2])[0] = types[0].getName();
                    this.writeParamClassNames[1] = types[1].getName();
                }
            }
        }
        
        private Method get() {
            if (this.methodRef == null) {
                return null;
            }
            Method m = this.methodRef.get();
            if (m == null) {
                Class<?> clazz = this.classRef.get();
                if (clazz == null) {
                    clazz = this.reLoadClass();
                    if (clazz != null) {
                        this.classRef = new WeakReference<Class<?>>(clazz);
                    }
                }
                if (clazz == null) {
                    throw new RuntimeException("Method " + this.methodName + " for " + this.className + " could not be reconstructed - class reference has gone");
                }
                Class<?>[] paramTypes = null;
                if (this.writeParamClassNames != null) {
                    paramTypes = new Class[] { this.writeParamTypeRef0.get(), null };
                    if (paramTypes[0] == null) {
                        paramTypes[0] = this.reLoadClass(this.writeParamClassNames[0]);
                        if (paramTypes[0] != null) {
                            this.writeParamTypeRef0 = new WeakReference<Class<?>>(paramTypes[0]);
                        }
                    }
                    paramTypes[1] = this.writeParamTypeRef1.get();
                    if (paramTypes[1] == null) {
                        paramTypes[1] = this.reLoadClass(this.writeParamClassNames[1]);
                        if (paramTypes[1] != null) {
                            this.writeParamTypeRef1 = new WeakReference<Class<?>>(paramTypes[1]);
                        }
                    }
                }
                else {
                    paramTypes = MappedPropertyDescriptor.STRING_CLASS_PARAMETER;
                }
                try {
                    m = clazz.getMethod(this.methodName, paramTypes);
                }
                catch (final NoSuchMethodException e) {
                    throw new RuntimeException("Method " + this.methodName + " for " + this.className + " could not be reconstructed - method not found");
                }
                this.methodRef = new SoftReference<Method>(m);
            }
            return m;
        }
        
        private Class<?> reLoadClass() {
            return this.reLoadClass(this.className);
        }
        
        private Class<?> reLoadClass(final String name) {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader != null) {
                try {
                    return classLoader.loadClass(name);
                }
                catch (final ClassNotFoundException ex) {}
            }
            classLoader = MappedPropertyDescriptor.class.getClassLoader();
            try {
                return classLoader.loadClass(name);
            }
            catch (final ClassNotFoundException e) {
                return null;
            }
        }
    }
}
