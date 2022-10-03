package com.sun.corba.se.impl.presentation.rmi;

import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.HashSet;
import java.io.IOException;
import java.io.Externalizable;
import org.omg.CORBA.portable.IDLEntity;
import java.rmi.RemoteException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.rmi.Remote;

public final class IDLTypesUtil
{
    private static final String GET_PROPERTY_PREFIX = "get";
    private static final String SET_PROPERTY_PREFIX = "set";
    private static final String IS_PROPERTY_PREFIX = "is";
    public static final int VALID_TYPE = 0;
    public static final int INVALID_TYPE = 1;
    public static final boolean FOLLOW_RMIC = true;
    
    public void validateRemoteInterface(final Class clazz) throws IDLTypeException {
        if (clazz == null) {
            throw new IllegalArgumentException();
        }
        if (!clazz.isInterface()) {
            throw new IDLTypeException("Class " + clazz + " must be a java interface.");
        }
        if (!Remote.class.isAssignableFrom(clazz)) {
            throw new IDLTypeException("Class " + clazz + " must extend java.rmi.Remote, either directly or indirectly.");
        }
        final Method[] methods = clazz.getMethods();
        for (int i = 0; i < methods.length; ++i) {
            this.validateExceptions(methods[i]);
        }
        this.validateConstants(clazz);
    }
    
    public boolean isRemoteInterface(final Class clazz) {
        boolean b = true;
        try {
            this.validateRemoteInterface(clazz);
        }
        catch (final IDLTypeException ex) {
            b = false;
        }
        return b;
    }
    
    public boolean isPrimitive(final Class clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException();
        }
        return clazz.isPrimitive();
    }
    
    public boolean isValue(final Class clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException();
        }
        return !clazz.isInterface() && Serializable.class.isAssignableFrom(clazz) && !Remote.class.isAssignableFrom(clazz);
    }
    
    public boolean isArray(final Class clazz) {
        boolean b = false;
        if (clazz == null) {
            throw new IllegalArgumentException();
        }
        if (clazz.isArray()) {
            final Class componentType = clazz.getComponentType();
            b = (this.isPrimitive(componentType) || this.isRemoteInterface(componentType) || this.isEntity(componentType) || this.isException(componentType) || this.isValue(componentType) || this.isObjectReference(componentType));
        }
        return b;
    }
    
    public boolean isException(final Class clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException();
        }
        return this.isCheckedException(clazz) && !this.isRemoteException(clazz) && this.isValue(clazz);
    }
    
    public boolean isRemoteException(final Class clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException();
        }
        return RemoteException.class.isAssignableFrom(clazz);
    }
    
    public boolean isCheckedException(final Class clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException();
        }
        return Throwable.class.isAssignableFrom(clazz) && !RuntimeException.class.isAssignableFrom(clazz) && !Error.class.isAssignableFrom(clazz);
    }
    
    public boolean isObjectReference(final Class clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException();
        }
        return clazz.isInterface() && org.omg.CORBA.Object.class.isAssignableFrom(clazz);
    }
    
    public boolean isEntity(final Class clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException();
        }
        final Class superclass = clazz.getSuperclass();
        return !clazz.isInterface() && superclass != null && IDLEntity.class.isAssignableFrom(clazz);
    }
    
    public boolean isPropertyAccessorMethod(final Method method, final Class clazz) {
        final String name = method.getName();
        final Class<?> returnType = method.getReturnType();
        final Class<?>[] parameterTypes = method.getParameterTypes();
        method.getExceptionTypes();
        String s = null;
        if (name.startsWith("get")) {
            if (parameterTypes.length == 0 && returnType != Void.TYPE && !this.readHasCorrespondingIsProperty(method, clazz)) {
                s = "get";
            }
        }
        else if (name.startsWith("set")) {
            if (returnType == Void.TYPE && parameterTypes.length == 1 && (this.hasCorrespondingReadProperty(method, clazz, "get") || this.hasCorrespondingReadProperty(method, clazz, "is"))) {
                s = "set";
            }
        }
        else if (name.startsWith("is") && parameterTypes.length == 0 && returnType == Boolean.TYPE && !this.isHasCorrespondingReadProperty(method, clazz)) {
            s = "is";
        }
        if (s != null && (!this.validPropertyExceptions(method) || name.length() <= s.length())) {
            s = null;
        }
        return s != null;
    }
    
    private boolean hasCorrespondingReadProperty(final Method method, final Class clazz, final String s) {
        final String name = method.getName();
        final Class<?>[] parameterTypes = method.getParameterTypes();
        boolean b = false;
        try {
            final Method method2 = clazz.getMethod(name.replaceFirst("set", s), (Class[])new Class[0]);
            b = (this.isPropertyAccessorMethod(method2, clazz) && method2.getReturnType() == parameterTypes[0]);
        }
        catch (final Exception ex) {}
        return b;
    }
    
    private boolean readHasCorrespondingIsProperty(final Method method, final Class clazz) {
        return false;
    }
    
    private boolean isHasCorrespondingReadProperty(final Method method, final Class clazz) {
        final String name = method.getName();
        boolean propertyAccessorMethod = false;
        try {
            propertyAccessorMethod = this.isPropertyAccessorMethod(clazz.getMethod(name.replaceFirst("is", "get"), (Class[])new Class[0]), clazz);
        }
        catch (final Exception ex) {}
        return propertyAccessorMethod;
    }
    
    public String getAttributeNameForProperty(final String s) {
        String string = null;
        String s2 = null;
        if (s.startsWith("get")) {
            s2 = "get";
        }
        else if (s.startsWith("set")) {
            s2 = "set";
        }
        else if (s.startsWith("is")) {
            s2 = "is";
        }
        if (s2 != null && s2.length() < s.length()) {
            final String substring = s.substring(s2.length());
            if (substring.length() >= 2 && Character.isUpperCase(substring.charAt(0)) && Character.isUpperCase(substring.charAt(1))) {
                string = substring;
            }
            else {
                string = Character.toLowerCase(substring.charAt(0)) + substring.substring(1);
            }
        }
        return string;
    }
    
    public IDLType getPrimitiveIDLTypeMapping(final Class clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException();
        }
        if (clazz.isPrimitive()) {
            if (clazz == Void.TYPE) {
                return new IDLType(clazz, "void");
            }
            if (clazz == Boolean.TYPE) {
                return new IDLType(clazz, "boolean");
            }
            if (clazz == Character.TYPE) {
                return new IDLType(clazz, "wchar");
            }
            if (clazz == Byte.TYPE) {
                return new IDLType(clazz, "octet");
            }
            if (clazz == Short.TYPE) {
                return new IDLType(clazz, "short");
            }
            if (clazz == Integer.TYPE) {
                return new IDLType(clazz, "long");
            }
            if (clazz == Long.TYPE) {
                return new IDLType(clazz, "long_long");
            }
            if (clazz == Float.TYPE) {
                return new IDLType(clazz, "float");
            }
            if (clazz == Double.TYPE) {
                return new IDLType(clazz, "double");
            }
        }
        return null;
    }
    
    public IDLType getSpecialCaseIDLTypeMapping(final Class clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException();
        }
        if (clazz == Object.class) {
            return new IDLType(clazz, new String[] { "java", "lang" }, "Object");
        }
        if (clazz == String.class) {
            return new IDLType(clazz, new String[] { "CORBA" }, "WStringValue");
        }
        if (clazz == Class.class) {
            return new IDLType(clazz, new String[] { "javax", "rmi", "CORBA" }, "ClassDesc");
        }
        if (clazz == Serializable.class) {
            return new IDLType(clazz, new String[] { "java", "io" }, "Serializable");
        }
        if (clazz == Externalizable.class) {
            return new IDLType(clazz, new String[] { "java", "io" }, "Externalizable");
        }
        if (clazz == Remote.class) {
            return new IDLType(clazz, new String[] { "java", "rmi" }, "Remote");
        }
        if (clazz == org.omg.CORBA.Object.class) {
            return new IDLType(clazz, "Object");
        }
        return null;
    }
    
    private void validateExceptions(final Method method) throws IDLTypeException {
        final Class<?>[] exceptionTypes = method.getExceptionTypes();
        boolean b = false;
        for (int i = 0; i < exceptionTypes.length; ++i) {
            if (this.isRemoteExceptionOrSuperClass(exceptionTypes[i])) {
                b = true;
                break;
            }
        }
        if (!b) {
            throw new IDLTypeException("Method '" + method + "' must throw at least one exception of type java.rmi.RemoteException or one of its super-classes");
        }
        for (int j = 0; j < exceptionTypes.length; ++j) {
            final Class<?> clazz = exceptionTypes[j];
            if (this.isCheckedException(clazz) && !this.isValue(clazz) && !this.isRemoteException(clazz)) {
                throw new IDLTypeException("Exception '" + clazz + "' on method '" + method + "' is not a allowed RMI/IIOP exception type");
            }
        }
    }
    
    private boolean validPropertyExceptions(final Method method) {
        final Class<?>[] exceptionTypes = method.getExceptionTypes();
        for (int i = 0; i < exceptionTypes.length; ++i) {
            final Class<?> clazz = exceptionTypes[i];
            if (this.isCheckedException(clazz) && !this.isRemoteException(clazz)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean isRemoteExceptionOrSuperClass(final Class clazz) {
        return clazz == RemoteException.class || clazz == IOException.class || clazz == Exception.class || clazz == Throwable.class;
    }
    
    private void validateDirectInterfaces(final Class clazz) throws IDLTypeException {
        final Class[] interfaces = clazz.getInterfaces();
        if (interfaces.length < 2) {
            return;
        }
        final HashSet set = new HashSet();
        final HashSet set2 = new HashSet();
        for (int i = 0; i < interfaces.length; ++i) {
            final Method[] methods = interfaces[i].getMethods();
            set2.clear();
            for (int j = 0; j < methods.length; ++j) {
                set2.add(methods[j].getName());
            }
            for (final String s : set2) {
                if (set.contains(s)) {
                    throw new IDLTypeException("Class " + clazz + " inherits method " + s + " from multiple direct interfaces.");
                }
                set.add(s);
            }
        }
    }
    
    private void validateConstants(final Class clazz) throws IDLTypeException {
        Field[] array;
        try {
            array = AccessController.doPrivileged((PrivilegedExceptionAction<Field[]>)new PrivilegedExceptionAction() {
                @Override
                public Object run() throws Exception {
                    return clazz.getFields();
                }
            });
        }
        catch (final PrivilegedActionException ex) {
            final IDLTypeException ex2 = new IDLTypeException();
            ex2.initCause(ex);
            throw ex2;
        }
        for (int i = 0; i < array.length; ++i) {
            final Field field = array[i];
            final Class<?> type = field.getType();
            if (type != String.class && !this.isPrimitive(type)) {
                throw new IDLTypeException("Constant field '" + field.getName() + "' in class '" + field.getDeclaringClass().getName() + "' has invalid type' " + field.getType() + "'. Constants in RMI/IIOP interfaces can only have primitive types and java.lang.String types.");
            }
        }
    }
}
