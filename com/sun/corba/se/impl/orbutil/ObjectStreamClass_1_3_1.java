package com.sun.corba.se.impl.orbutil;

import java.lang.reflect.Member;
import java.security.NoSuchAlgorithmException;
import java.io.IOException;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Array;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import com.sun.corba.se.impl.io.ObjectStreamClass;
import java.util.Arrays;
import java.security.AccessController;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.PrivilegedAction;
import java.lang.reflect.Proxy;
import com.sun.corba.se.impl.io.ValueUtility;
import org.omg.CORBA.ValueMember;
import java.io.Externalizable;
import java.util.Comparator;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.io.Serializable;

public class ObjectStreamClass_1_3_1 implements Serializable
{
    public static final long kDefaultUID = -1L;
    private static Object[] noArgsList;
    private static Class<?>[] noTypesList;
    private static Hashtable translatedFields;
    private static ObjectStreamClassEntry[] descriptorFor;
    private String name;
    private ObjectStreamClass_1_3_1 superclass;
    private boolean serializable;
    private boolean externalizable;
    private ObjectStreamField[] fields;
    private Class<?> ofClass;
    boolean forProxyClass;
    private long suid;
    private String suidStr;
    private long actualSuid;
    private String actualSuidStr;
    int primBytes;
    int objFields;
    private Object lock;
    private boolean hasWriteObjectMethod;
    private boolean hasExternalizableBlockData;
    Method writeObjectMethod;
    Method readObjectMethod;
    private transient Method writeReplaceObjectMethod;
    private transient Method readResolveObjectMethod;
    private ObjectStreamClass_1_3_1 localClassDesc;
    private static final long serialVersionUID = -6120832682080437368L;
    public static final ObjectStreamField[] NO_FIELDS;
    private static Comparator compareClassByName;
    private static Comparator compareMemberByName;
    
    static final ObjectStreamClass_1_3_1 lookup(final Class<?> clazz) {
        final ObjectStreamClass_1_3_1 lookupInternal = lookupInternal(clazz);
        if (lookupInternal.isSerializable() || lookupInternal.isExternalizable()) {
            return lookupInternal;
        }
        return null;
    }
    
    static ObjectStreamClass_1_3_1 lookupInternal(final Class<?> clazz) {
        ObjectStreamClass_1_3_1 descriptor = null;
        synchronized (ObjectStreamClass_1_3_1.descriptorFor) {
            descriptor = findDescriptorFor(clazz);
            if (descriptor != null) {
                return descriptor;
            }
            boolean assignable = Serializable.class.isAssignableFrom(clazz);
            ObjectStreamClass_1_3_1 lookup = null;
            if (assignable) {
                final Class<?> superclass = clazz.getSuperclass();
                if (superclass != null) {
                    lookup = lookup(superclass);
                }
            }
            boolean b = false;
            if (assignable) {
                b = ((lookup != null && lookup.isExternalizable()) || Externalizable.class.isAssignableFrom(clazz));
                if (b) {
                    assignable = false;
                }
            }
            descriptor = new ObjectStreamClass_1_3_1(clazz, lookup, assignable, b);
        }
        descriptor.init();
        return descriptor;
    }
    
    public final String getName() {
        return this.name;
    }
    
    public static final long getSerialVersionUID(final Class<?> clazz) {
        final ObjectStreamClass_1_3_1 lookup = lookup(clazz);
        if (lookup != null) {
            return lookup.getSerialVersionUID();
        }
        return 0L;
    }
    
    public final long getSerialVersionUID() {
        return this.suid;
    }
    
    public final String getSerialVersionUIDStr() {
        if (this.suidStr == null) {
            this.suidStr = Long.toHexString(this.suid).toUpperCase();
        }
        return this.suidStr;
    }
    
    public static final long getActualSerialVersionUID(final Class<?> clazz) {
        final ObjectStreamClass_1_3_1 lookup = lookup(clazz);
        if (lookup != null) {
            return lookup.getActualSerialVersionUID();
        }
        return 0L;
    }
    
    public final long getActualSerialVersionUID() {
        return this.actualSuid;
    }
    
    public final String getActualSerialVersionUIDStr() {
        if (this.actualSuidStr == null) {
            this.actualSuidStr = Long.toHexString(this.actualSuid).toUpperCase();
        }
        return this.actualSuidStr;
    }
    
    public final Class<?> forClass() {
        return this.ofClass;
    }
    
    public ObjectStreamField[] getFields() {
        if (this.fields.length > 0) {
            final ObjectStreamField[] array = new ObjectStreamField[this.fields.length];
            System.arraycopy(this.fields, 0, array, 0, this.fields.length);
            return array;
        }
        return this.fields;
    }
    
    public boolean hasField(final ValueMember valueMember) {
        for (int i = 0; i < this.fields.length; ++i) {
            try {
                if (this.fields[i].getName().equals(valueMember.name) && this.fields[i].getSignature().equals(ValueUtility.getSignature(valueMember))) {
                    return true;
                }
            }
            catch (final Throwable t) {}
        }
        return false;
    }
    
    final ObjectStreamField[] getFieldsNoCopy() {
        return this.fields;
    }
    
    public final ObjectStreamField getField(final String s) {
        for (int i = this.fields.length - 1; i >= 0; --i) {
            if (s.equals(this.fields[i].getName())) {
                return this.fields[i];
            }
        }
        return null;
    }
    
    public Serializable writeReplace(final Serializable s) {
        if (this.writeReplaceObjectMethod != null) {
            try {
                return (Serializable)this.writeReplaceObjectMethod.invoke(s, ObjectStreamClass_1_3_1.noArgsList);
            }
            catch (final Throwable t) {
                throw new RuntimeException(t.getMessage());
            }
        }
        return s;
    }
    
    public Object readResolve(final Object o) {
        if (this.readResolveObjectMethod != null) {
            try {
                return this.readResolveObjectMethod.invoke(o, ObjectStreamClass_1_3_1.noArgsList);
            }
            catch (final Throwable t) {
                throw new RuntimeException(t.getMessage());
            }
        }
        return o;
    }
    
    @Override
    public final String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(this.name);
        sb.append(": static final long serialVersionUID = ");
        sb.append(Long.toString(this.suid));
        sb.append("L;");
        return sb.toString();
    }
    
    private ObjectStreamClass_1_3_1(final Class<?> ofClass, final ObjectStreamClass_1_3_1 superclass, final boolean serializable, final boolean externalizable) {
        this.suid = -1L;
        this.suidStr = null;
        this.actualSuid = -1L;
        this.actualSuidStr = null;
        this.lock = new Object();
        this.ofClass = ofClass;
        if (Proxy.isProxyClass(ofClass)) {
            this.forProxyClass = true;
        }
        this.name = ofClass.getName();
        this.superclass = superclass;
        this.serializable = serializable;
        if (!this.forProxyClass) {
            this.externalizable = externalizable;
        }
        insertDescriptorFor(this);
    }
    
    private void init() {
        synchronized (this.lock) {
            final Class<?> ofClass = this.ofClass;
            if (this.fields != null) {
                return;
            }
            if (!this.serializable || this.externalizable || this.forProxyClass || this.name.equals("java.lang.String")) {
                this.fields = ObjectStreamClass_1_3_1.NO_FIELDS;
            }
            else if (this.serializable) {
                AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                    @Override
                    public Object run() {
                        try {
                            final Field declaredField = ofClass.getDeclaredField("serialPersistentFields");
                            declaredField.setAccessible(true);
                            final java.io.ObjectStreamField[] array = (java.io.ObjectStreamField[])declaredField.get(ofClass);
                            final int modifiers = declaredField.getModifiers();
                            if (Modifier.isPrivate(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
                                ObjectStreamClass_1_3_1.this.fields = (ObjectStreamField[])translateFields((Object[])declaredField.get(ofClass));
                            }
                        }
                        catch (final NoSuchFieldException ex) {
                            ObjectStreamClass_1_3_1.this.fields = null;
                        }
                        catch (final IllegalAccessException ex2) {
                            ObjectStreamClass_1_3_1.this.fields = null;
                        }
                        catch (final IllegalArgumentException ex3) {
                            ObjectStreamClass_1_3_1.this.fields = null;
                        }
                        catch (final ClassCastException ex4) {
                            ObjectStreamClass_1_3_1.this.fields = null;
                        }
                        if (ObjectStreamClass_1_3_1.this.fields == null) {
                            final Field[] declaredFields = ofClass.getDeclaredFields();
                            int n = 0;
                            final ObjectStreamField[] array2 = new ObjectStreamField[declaredFields.length];
                            for (int i = 0; i < declaredFields.length; ++i) {
                                final int modifiers2 = declaredFields[i].getModifiers();
                                if (!Modifier.isStatic(modifiers2) && !Modifier.isTransient(modifiers2)) {
                                    array2[n++] = new ObjectStreamField(declaredFields[i]);
                                }
                            }
                            ObjectStreamClass_1_3_1.this.fields = new ObjectStreamField[n];
                            System.arraycopy(array2, 0, ObjectStreamClass_1_3_1.this.fields, 0, n);
                        }
                        else {
                            for (int j = ObjectStreamClass_1_3_1.this.fields.length - 1; j >= 0; --j) {
                                try {
                                    final Field declaredField2 = ofClass.getDeclaredField(ObjectStreamClass_1_3_1.this.fields[j].getName());
                                    if (ObjectStreamClass_1_3_1.this.fields[j].getType() == declaredField2.getType()) {
                                        ObjectStreamClass_1_3_1.this.fields[j].setField(declaredField2);
                                    }
                                }
                                catch (final NoSuchFieldException ex5) {}
                            }
                        }
                        return null;
                    }
                });
                if (this.fields.length > 1) {
                    Arrays.sort(this.fields);
                }
                this.computeFieldInfo();
            }
            if (this.isNonSerializable()) {
                this.suid = 0L;
            }
            else {
                AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                    @Override
                    public Object run() {
                        if (ObjectStreamClass_1_3_1.this.forProxyClass) {
                            ObjectStreamClass_1_3_1.this.suid = 0L;
                        }
                        else {
                            try {
                                final Field declaredField = ofClass.getDeclaredField("serialVersionUID");
                                final int modifiers = declaredField.getModifiers();
                                if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
                                    declaredField.setAccessible(true);
                                    ObjectStreamClass_1_3_1.this.suid = declaredField.getLong(ofClass);
                                }
                                else {
                                    ObjectStreamClass_1_3_1.this.suid = ObjectStreamClass.getSerialVersionUID(ofClass);
                                }
                            }
                            catch (final NoSuchFieldException ex) {
                                ObjectStreamClass_1_3_1.this.suid = ObjectStreamClass.getSerialVersionUID(ofClass);
                            }
                            catch (final IllegalAccessException ex2) {
                                ObjectStreamClass_1_3_1.this.suid = ObjectStreamClass.getSerialVersionUID(ofClass);
                            }
                        }
                        try {
                            ObjectStreamClass_1_3_1.this.writeReplaceObjectMethod = ofClass.getDeclaredMethod("writeReplace", (Class[])ObjectStreamClass_1_3_1.noTypesList);
                            if (Modifier.isStatic(ObjectStreamClass_1_3_1.this.writeReplaceObjectMethod.getModifiers())) {
                                ObjectStreamClass_1_3_1.this.writeReplaceObjectMethod = null;
                            }
                            else {
                                ObjectStreamClass_1_3_1.this.writeReplaceObjectMethod.setAccessible(true);
                            }
                        }
                        catch (final NoSuchMethodException ex3) {}
                        try {
                            ObjectStreamClass_1_3_1.this.readResolveObjectMethod = ofClass.getDeclaredMethod("readResolve", (Class[])ObjectStreamClass_1_3_1.noTypesList);
                            if (Modifier.isStatic(ObjectStreamClass_1_3_1.this.readResolveObjectMethod.getModifiers())) {
                                ObjectStreamClass_1_3_1.this.readResolveObjectMethod = null;
                            }
                            else {
                                ObjectStreamClass_1_3_1.this.readResolveObjectMethod.setAccessible(true);
                            }
                        }
                        catch (final NoSuchMethodException ex4) {}
                        if (ObjectStreamClass_1_3_1.this.serializable && !ObjectStreamClass_1_3_1.this.forProxyClass) {
                            try {
                                ObjectStreamClass_1_3_1.this.writeObjectMethod = ofClass.getDeclaredMethod("writeObject", ObjectOutputStream.class);
                                ObjectStreamClass_1_3_1.this.hasWriteObjectMethod = true;
                                final int modifiers2 = ObjectStreamClass_1_3_1.this.writeObjectMethod.getModifiers();
                                if (!Modifier.isPrivate(modifiers2) || Modifier.isStatic(modifiers2)) {
                                    ObjectStreamClass_1_3_1.this.writeObjectMethod = null;
                                    ObjectStreamClass_1_3_1.this.hasWriteObjectMethod = false;
                                }
                            }
                            catch (final NoSuchMethodException ex5) {}
                            try {
                                ObjectStreamClass_1_3_1.this.readObjectMethod = ofClass.getDeclaredMethod("readObject", ObjectInputStream.class);
                                final int modifiers3 = ObjectStreamClass_1_3_1.this.readObjectMethod.getModifiers();
                                if (!Modifier.isPrivate(modifiers3) || Modifier.isStatic(modifiers3)) {
                                    ObjectStreamClass_1_3_1.this.readObjectMethod = null;
                                }
                            }
                            catch (final NoSuchMethodException ex6) {}
                        }
                        return null;
                    }
                });
            }
            this.actualSuid = computeStructuralUID(this, ofClass);
        }
    }
    
    ObjectStreamClass_1_3_1(final String name, final long suid) {
        this.suid = -1L;
        this.suidStr = null;
        this.actualSuid = -1L;
        this.actualSuidStr = null;
        this.lock = new Object();
        this.name = name;
        this.suid = suid;
        this.superclass = null;
    }
    
    private static Object[] translateFields(final Object[] array) throws NoSuchFieldException {
        try {
            final java.io.ObjectStreamField[] array2 = (java.io.ObjectStreamField[])array;
            if (ObjectStreamClass_1_3_1.translatedFields == null) {
                ObjectStreamClass_1_3_1.translatedFields = new Hashtable();
            }
            final Object[] array3 = ObjectStreamClass_1_3_1.translatedFields.get(array2);
            if (array3 != null) {
                return array3;
            }
            final Class<ObjectStreamField> clazz = ObjectStreamField.class;
            final Object[] array4 = (Object[])Array.newInstance(clazz, array.length);
            final Object[] array5 = new Object[2];
            final Constructor<ObjectStreamField> declaredConstructor = clazz.getDeclaredConstructor(String.class, Class.class);
            for (int i = array2.length - 1; i >= 0; --i) {
                array5[0] = array2[i].getName();
                array5[1] = array2[i].getType();
                array4[i] = declaredConstructor.newInstance(array5);
            }
            ObjectStreamClass_1_3_1.translatedFields.put(array2, array4);
            return array4;
        }
        catch (final Throwable t) {
            throw new NoSuchFieldException();
        }
    }
    
    static boolean compareClassNames(final String s, final String s2, final char c) {
        int lastIndex = s.lastIndexOf(c);
        if (lastIndex < 0) {
            lastIndex = 0;
        }
        int lastIndex2 = s2.lastIndexOf(c);
        if (lastIndex2 < 0) {
            lastIndex2 = 0;
        }
        return s.regionMatches(false, lastIndex, s2, lastIndex2, s.length() - lastIndex);
    }
    
    final boolean typeEquals(final ObjectStreamClass_1_3_1 objectStreamClass_1_3_1) {
        return this.suid == objectStreamClass_1_3_1.suid && compareClassNames(this.name, objectStreamClass_1_3_1.name, '.');
    }
    
    final void setSuperclass(final ObjectStreamClass_1_3_1 superclass) {
        this.superclass = superclass;
    }
    
    final ObjectStreamClass_1_3_1 getSuperclass() {
        return this.superclass;
    }
    
    final boolean hasWriteObject() {
        return this.hasWriteObjectMethod;
    }
    
    final boolean isCustomMarshaled() {
        return this.hasWriteObject() || this.isExternalizable();
    }
    
    boolean hasExternalizableBlockDataMode() {
        return this.hasExternalizableBlockData;
    }
    
    final ObjectStreamClass_1_3_1 localClassDescriptor() {
        return this.localClassDesc;
    }
    
    boolean isSerializable() {
        return this.serializable;
    }
    
    boolean isExternalizable() {
        return this.externalizable;
    }
    
    boolean isNonSerializable() {
        return !this.externalizable && !this.serializable;
    }
    
    private void computeFieldInfo() {
        this.primBytes = 0;
        this.objFields = 0;
        for (int i = 0; i < this.fields.length; ++i) {
            switch (this.fields[i].getTypeCode()) {
                case 'B':
                case 'Z': {
                    ++this.primBytes;
                    break;
                }
                case 'C':
                case 'S': {
                    this.primBytes += 2;
                    break;
                }
                case 'F':
                case 'I': {
                    this.primBytes += 4;
                    break;
                }
                case 'D':
                case 'J': {
                    this.primBytes += 8;
                    break;
                }
                case 'L':
                case '[': {
                    ++this.objFields;
                    break;
                }
            }
        }
    }
    
    private static long computeStructuralUID(final ObjectStreamClass_1_3_1 objectStreamClass_1_3_1, final Class<?> clazz) {
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
            if (superclass != null) {
                dataOutputStream.writeLong(computeStructuralUID(lookup(superclass), superclass));
            }
            if (objectStreamClass_1_3_1.hasWriteObject()) {
                dataOutputStream.writeInt(2);
            }
            else {
                dataOutputStream.writeInt(1);
            }
            final ObjectStreamField[] fields = objectStreamClass_1_3_1.getFields();
            int n2 = 0;
            for (int i = 0; i < fields.length; ++i) {
                if (fields[i].getField() != null) {
                    ++n2;
                }
            }
            final Field[] array = new Field[n2];
            int j = 0;
            int n3 = 0;
            while (j < fields.length) {
                if (fields[j].getField() != null) {
                    array[n3++] = fields[j].getField();
                }
                ++j;
            }
            if (array.length > 1) {
                Arrays.sort(array, ObjectStreamClass_1_3_1.compareMemberByName);
            }
            for (int k = 0; k < array.length; ++k) {
                final Field field = array[k];
                field.getModifiers();
                dataOutputStream.writeUTF(field.getName());
                dataOutputStream.writeUTF(getSignature(field.getType()));
            }
            dataOutputStream.flush();
            final byte[] digest = instance.digest();
            for (int l = 0; l < Math.min(8, digest.length); ++l) {
                n += (long)(digest[l] & 0xFF) << l * 8;
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
    
    static String getSignature(final Class<?> clazz) {
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
    
    static String getSignature(final Method method) {
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
    
    static String getSignature(final Constructor constructor) {
        final StringBuffer sb = new StringBuffer();
        sb.append("(");
        final Class[] parameterTypes = constructor.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; ++i) {
            sb.append(getSignature(parameterTypes[i]));
        }
        sb.append(")V");
        return sb.toString();
    }
    
    private static ObjectStreamClass_1_3_1 findDescriptorFor(final Class<?> clazz) {
        final int n = (clazz.hashCode() & Integer.MAX_VALUE) % ObjectStreamClass_1_3_1.descriptorFor.length;
        ObjectStreamClassEntry next;
        while ((next = ObjectStreamClass_1_3_1.descriptorFor[n]) != null && next.get() == null) {
            ObjectStreamClass_1_3_1.descriptorFor[n] = next.next;
        }
        ObjectStreamClassEntry objectStreamClassEntry = next;
        while (next != null) {
            final ObjectStreamClass_1_3_1 objectStreamClass_1_3_1 = (ObjectStreamClass_1_3_1)next.get();
            if (objectStreamClass_1_3_1 == null) {
                objectStreamClassEntry.next = next.next;
            }
            else {
                if (objectStreamClass_1_3_1.ofClass == clazz) {
                    return objectStreamClass_1_3_1;
                }
                objectStreamClassEntry = next;
            }
            next = next.next;
        }
        return null;
    }
    
    private static void insertDescriptorFor(final ObjectStreamClass_1_3_1 objectStreamClass_1_3_1) {
        if (findDescriptorFor(objectStreamClass_1_3_1.ofClass) != null) {
            return;
        }
        final int n = (objectStreamClass_1_3_1.ofClass.hashCode() & Integer.MAX_VALUE) % ObjectStreamClass_1_3_1.descriptorFor.length;
        final ObjectStreamClassEntry objectStreamClassEntry = new ObjectStreamClassEntry(objectStreamClass_1_3_1);
        objectStreamClassEntry.next = ObjectStreamClass_1_3_1.descriptorFor[n];
        ObjectStreamClass_1_3_1.descriptorFor[n] = objectStreamClassEntry;
    }
    
    private static Field[] getDeclaredFields(final Class clazz) {
        return AccessController.doPrivileged((PrivilegedAction<Field[]>)new PrivilegedAction() {
            @Override
            public Object run() {
                return clazz.getDeclaredFields();
            }
        });
    }
    
    static {
        ObjectStreamClass_1_3_1.noArgsList = new Object[0];
        ObjectStreamClass_1_3_1.noTypesList = new Class[0];
        ObjectStreamClass_1_3_1.descriptorFor = new ObjectStreamClassEntry[61];
        NO_FIELDS = new ObjectStreamField[0];
        ObjectStreamClass_1_3_1.compareClassByName = new CompareClassByName();
        ObjectStreamClass_1_3_1.compareMemberByName = new CompareMemberByName();
    }
    
    private static class ObjectStreamClassEntry
    {
        ObjectStreamClassEntry next;
        private ObjectStreamClass_1_3_1 c;
        
        ObjectStreamClassEntry(final ObjectStreamClass_1_3_1 c) {
            this.c = c;
        }
        
        public Object get() {
            return this.c;
        }
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
                s += ObjectStreamClass_1_3_1.getSignature((Method)o);
                s2 += ObjectStreamClass_1_3_1.getSignature((Method)o2);
            }
            else if (o instanceof Constructor) {
                s += ObjectStreamClass_1_3_1.getSignature((Constructor)o);
                s2 += ObjectStreamClass_1_3_1.getSignature((Constructor)o2);
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
                this.signature = ObjectStreamClass_1_3_1.getSignature((Constructor)member);
            }
            else {
                this.signature = ObjectStreamClass_1_3_1.getSignature((Method)member);
            }
        }
    }
}
