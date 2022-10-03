package com.sun.corba.se.impl.orbutil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.security.NoSuchAlgorithmException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.io.ObjectOutputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.io.Externalizable;
import java.io.Serializable;
import java.io.ByteArrayOutputStream;
import java.security.AccessController;
import java.lang.reflect.Modifier;
import java.security.PrivilegedAction;
import com.sun.corba.se.impl.io.ObjectStreamClass;
import java.lang.reflect.Method;
import java.util.Comparator;

public final class ObjectStreamClassUtil_1_3
{
    private static Comparator compareClassByName;
    private static Comparator compareMemberByName;
    private static Method hasStaticInitializerMethod;
    
    public static long computeSerialVersionUID(final Class clazz) {
        final long serialVersionUID = ObjectStreamClass.getSerialVersionUID(clazz);
        if (serialVersionUID == 0L) {
            return serialVersionUID;
        }
        return getSerialVersion(serialVersionUID, clazz);
    }
    
    private static Long getSerialVersion(final long n, final Class clazz) {
        return AccessController.doPrivileged((PrivilegedAction<Long>)new PrivilegedAction() {
            @Override
            public Object run() {
                long n;
                try {
                    final int modifiers = clazz.getDeclaredField("serialVersionUID").getModifiers();
                    if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers) && Modifier.isPrivate(modifiers)) {
                        n = n;
                    }
                    else {
                        n = _computeSerialVersionUID(clazz);
                    }
                }
                catch (final NoSuchFieldException ex) {
                    n = _computeSerialVersionUID(clazz);
                }
                return new Long(n);
            }
        });
    }
    
    public static long computeStructuralUID(final boolean b, final Class<?> clazz) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(512);
        long n = 0L;
        try {
            if (!Serializable.class.isAssignableFrom(clazz) || clazz.isInterface()) {
                return 0L;
            }
            if (Externalizable.class.isAssignableFrom(clazz)) {
                return 1L;
            }
            final MessageDigest instance = MessageDigest.getInstance("SHA");
            final DataOutputStream dataOutputStream = new DataOutputStream(new DigestOutputStream(byteArrayOutputStream, instance));
            final Class superclass = clazz.getSuperclass();
            if (superclass != null && superclass != Object.class) {
                boolean b2 = false;
                if (getDeclaredMethod(superclass, "writeObject", new Class[] { ObjectOutputStream.class }, 2, 8) != null) {
                    b2 = true;
                }
                dataOutputStream.writeLong(computeStructuralUID(b2, superclass));
            }
            if (b) {
                dataOutputStream.writeInt(2);
            }
            else {
                dataOutputStream.writeInt(1);
            }
            final Field[] declaredFields = getDeclaredFields(clazz);
            Arrays.sort(declaredFields, ObjectStreamClassUtil_1_3.compareMemberByName);
            for (int i = 0; i < declaredFields.length; ++i) {
                final Field field = declaredFields[i];
                final int modifiers = field.getModifiers();
                if (!Modifier.isTransient(modifiers)) {
                    if (!Modifier.isStatic(modifiers)) {
                        dataOutputStream.writeUTF(field.getName());
                        dataOutputStream.writeUTF(getSignature(field.getType()));
                    }
                }
            }
            dataOutputStream.flush();
            final byte[] digest = instance.digest();
            for (int j = Math.min(8, digest.length); j > 0; --j) {
                n += (long)(digest[j] & 0xFF) << j * 8;
            }
        }
        catch (final IOException ex) {
            n = -1L;
        }
        catch (final NoSuchAlgorithmException ex2) {
            throw new SecurityException(ex2.getMessage());
        }
        return n;
    }
    
    private static long _computeSerialVersionUID(final Class clazz) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(512);
        long n = 0L;
        try {
            final MessageDigest instance = MessageDigest.getInstance("SHA");
            final DataOutputStream dataOutputStream = new DataOutputStream(new DigestOutputStream(byteArrayOutputStream, instance));
            dataOutputStream.writeUTF(clazz.getName());
            int n2 = clazz.getModifiers() & 0x611;
            final Method[] declaredMethods = clazz.getDeclaredMethods();
            if ((n2 & 0x200) != 0x0) {
                n2 &= 0xFFFFFBFF;
                if (declaredMethods.length > 0) {
                    n2 |= 0x400;
                }
            }
            dataOutputStream.writeInt(n2);
            if (!clazz.isArray()) {
                final Class[] interfaces = clazz.getInterfaces();
                Arrays.sort(interfaces, ObjectStreamClassUtil_1_3.compareClassByName);
                for (int i = 0; i < interfaces.length; ++i) {
                    dataOutputStream.writeUTF(interfaces[i].getName());
                }
            }
            final Field[] declaredFields = clazz.getDeclaredFields();
            Arrays.sort(declaredFields, ObjectStreamClassUtil_1_3.compareMemberByName);
            for (int j = 0; j < declaredFields.length; ++j) {
                final Field field = declaredFields[j];
                final int modifiers = field.getModifiers();
                if (Modifier.isPrivate(modifiers)) {
                    if (Modifier.isTransient(modifiers)) {
                        continue;
                    }
                    if (Modifier.isStatic(modifiers)) {
                        continue;
                    }
                }
                dataOutputStream.writeUTF(field.getName());
                dataOutputStream.writeInt(modifiers);
                dataOutputStream.writeUTF(getSignature(field.getType()));
            }
            if (hasStaticInitializer(clazz)) {
                dataOutputStream.writeUTF("<clinit>");
                dataOutputStream.writeInt(8);
                dataOutputStream.writeUTF("()V");
            }
            final MethodSignature[] removePrivateAndSort = MethodSignature.removePrivateAndSort(clazz.getDeclaredConstructors());
            for (int k = 0; k < removePrivateAndSort.length; ++k) {
                final MethodSignature methodSignature = removePrivateAndSort[k];
                final String s = "<init>";
                final String replace = methodSignature.signature.replace('/', '.');
                dataOutputStream.writeUTF(s);
                dataOutputStream.writeInt(methodSignature.member.getModifiers());
                dataOutputStream.writeUTF(replace);
            }
            final MethodSignature[] removePrivateAndSort2 = MethodSignature.removePrivateAndSort(declaredMethods);
            for (int l = 0; l < removePrivateAndSort2.length; ++l) {
                final MethodSignature methodSignature2 = removePrivateAndSort2[l];
                final String replace2 = methodSignature2.signature.replace('/', '.');
                dataOutputStream.writeUTF(methodSignature2.member.getName());
                dataOutputStream.writeInt(methodSignature2.member.getModifiers());
                dataOutputStream.writeUTF(replace2);
            }
            dataOutputStream.flush();
            final byte[] digest = instance.digest();
            for (int n3 = 0; n3 < Math.min(8, digest.length); ++n3) {
                n += (long)(digest[n3] & 0xFF) << n3 * 8;
            }
        }
        catch (final IOException ex) {
            n = -1L;
        }
        catch (final NoSuchAlgorithmException ex2) {
            throw new SecurityException(ex2.getMessage());
        }
        return n;
    }
    
    private static String getSignature(final Class clazz) {
        String s = null;
        if (clazz.isArray()) {
            Class componentType = clazz;
            int n = 0;
            while (componentType.isArray()) {
                ++n;
                componentType = componentType.getComponentType();
            }
            final StringBuffer sb = new StringBuffer();
            for (int i = 0; i < n; ++i) {
                sb.append("[");
            }
            sb.append(getSignature(componentType));
            s = sb.toString();
        }
        else if (clazz.isPrimitive()) {
            if (clazz == Integer.TYPE) {
                s = "I";
            }
            else if (clazz == Byte.TYPE) {
                s = "B";
            }
            else if (clazz == Long.TYPE) {
                s = "J";
            }
            else if (clazz == Float.TYPE) {
                s = "F";
            }
            else if (clazz == Double.TYPE) {
                s = "D";
            }
            else if (clazz == Short.TYPE) {
                s = "S";
            }
            else if (clazz == Character.TYPE) {
                s = "C";
            }
            else if (clazz == Boolean.TYPE) {
                s = "Z";
            }
            else if (clazz == Void.TYPE) {
                s = "V";
            }
        }
        else {
            s = "L" + clazz.getName().replace('.', '/') + ";";
        }
        return s;
    }
    
    private static String getSignature(final Method method) {
        final StringBuffer sb = new StringBuffer();
        sb.append("(");
        final Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; ++i) {
            sb.append(getSignature(parameterTypes[i]));
        }
        sb.append(")");
        sb.append(getSignature(method.getReturnType()));
        return sb.toString();
    }
    
    private static String getSignature(final Constructor constructor) {
        final StringBuffer sb = new StringBuffer();
        sb.append("(");
        final Class[] parameterTypes = constructor.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; ++i) {
            sb.append(getSignature(parameterTypes[i]));
        }
        sb.append(")V");
        return sb.toString();
    }
    
    private static Field[] getDeclaredFields(final Class clazz) {
        return AccessController.doPrivileged((PrivilegedAction<Field[]>)new PrivilegedAction() {
            @Override
            public Object run() {
                return clazz.getDeclaredFields();
            }
        });
    }
    
    private static boolean hasStaticInitializer(final Class clazz) {
        if (ObjectStreamClassUtil_1_3.hasStaticInitializerMethod == null) {
            Class<java.io.ObjectStreamClass> clazz2 = null;
            try {
                if (clazz2 == null) {
                    clazz2 = java.io.ObjectStreamClass.class;
                }
                ObjectStreamClassUtil_1_3.hasStaticInitializerMethod = clazz2.getDeclaredMethod("hasStaticInitializer", Class.class);
            }
            catch (final NoSuchMethodException ex) {}
            if (ObjectStreamClassUtil_1_3.hasStaticInitializerMethod == null) {
                throw new InternalError("Can't find hasStaticInitializer method on " + clazz2.getName());
            }
            ObjectStreamClassUtil_1_3.hasStaticInitializerMethod.setAccessible(true);
        }
        try {
            return (boolean)ObjectStreamClassUtil_1_3.hasStaticInitializerMethod.invoke(null, clazz);
        }
        catch (final Exception ex2) {
            throw new InternalError("Error invoking hasStaticInitializer: " + ex2);
        }
    }
    
    private static Method getDeclaredMethod(final Class clazz, final String s, final Class[] array, final int n, final int n2) {
        return AccessController.doPrivileged((PrivilegedAction<Method>)new PrivilegedAction() {
            @Override
            public Object run() {
                Method declaredMethod = null;
                try {
                    declaredMethod = clazz.getDeclaredMethod(s, (Class[])array);
                    final int modifiers = declaredMethod.getModifiers();
                    if ((modifiers & n2) != 0x0 || (modifiers & n) != n) {
                        declaredMethod = null;
                    }
                }
                catch (final NoSuchMethodException ex) {}
                return declaredMethod;
            }
        });
    }
    
    static {
        ObjectStreamClassUtil_1_3.compareClassByName = new CompareClassByName();
        ObjectStreamClassUtil_1_3.compareMemberByName = new CompareMemberByName();
        ObjectStreamClassUtil_1_3.hasStaticInitializerMethod = null;
    }
    
    private static class CompareClassByName implements Comparator
    {
        @Override
        public int compare(final Object o, final Object o2) {
            return ((Class)o).getName().compareTo(((Class)o2).getName());
        }
    }
    
    private static class CompareMemberByName implements Comparator
    {
        @Override
        public int compare(final Object o, final Object o2) {
            String s = ((Member)o).getName();
            String s2 = ((Member)o2).getName();
            if (o instanceof Method) {
                s += getSignature((Method)o);
                s2 += getSignature((Method)o2);
            }
            else if (o instanceof Constructor) {
                s += getSignature((Constructor)o);
                s2 += getSignature((Constructor)o2);
            }
            return s.compareTo(s2);
        }
    }
    
    private static class MethodSignature implements Comparator
    {
        Member member;
        String signature;
        
        static MethodSignature[] removePrivateAndSort(final Member[] array) {
            int n = 0;
            for (int i = 0; i < array.length; ++i) {
                if (!Modifier.isPrivate(array[i].getModifiers())) {
                    ++n;
                }
            }
            final MethodSignature[] array2 = new MethodSignature[n];
            int n2 = 0;
            for (int j = 0; j < array.length; ++j) {
                if (!Modifier.isPrivate(array[j].getModifiers())) {
                    array2[n2] = new MethodSignature(array[j]);
                    ++n2;
                }
            }
            if (n2 > 0) {
                Arrays.sort(array2, array2[0]);
            }
            return array2;
        }
        
        @Override
        public int compare(final Object o, final Object o2) {
            if (o == o2) {
                return 0;
            }
            final MethodSignature methodSignature = (MethodSignature)o;
            final MethodSignature methodSignature2 = (MethodSignature)o2;
            int n;
            if (this.isConstructor()) {
                n = methodSignature.signature.compareTo(methodSignature2.signature);
            }
            else {
                n = methodSignature.member.getName().compareTo(methodSignature2.member.getName());
                if (n == 0) {
                    n = methodSignature.signature.compareTo(methodSignature2.signature);
                }
            }
            return n;
        }
        
        private final boolean isConstructor() {
            return this.member instanceof Constructor;
        }
        
        private MethodSignature(final Member member) {
            this.member = member;
            if (this.isConstructor()) {
                this.signature = getSignature((Constructor)member);
            }
            else {
                this.signature = getSignature((Method)member);
            }
        }
    }
}
