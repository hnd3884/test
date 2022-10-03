package com.sun.corba.se.impl.io;

import java.security.NoSuchAlgorithmException;
import java.io.IOException;
import java.lang.reflect.Member;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.io.ByteArrayOutputStream;
import sun.misc.JavaSecurityAccess;
import java.security.AccessControlContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import sun.misc.SharedSecrets;
import java.io.InvalidClassException;
import com.sun.corba.se.impl.util.RepositoryId;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.security.AccessController;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.PrivilegedAction;
import java.util.Set;
import java.util.HashSet;
import java.security.PermissionCollection;
import java.security.CodeSource;
import java.security.Permissions;
import java.lang.reflect.Proxy;
import org.omg.CORBA.ValueMember;
import java.io.Externalizable;
import java.util.Comparator;
import java.security.ProtectionDomain;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import sun.corba.Bridge;
import java.io.Serializable;

public class ObjectStreamClass implements Serializable
{
    private static final boolean DEBUG_SVUID = false;
    public static final long kDefaultUID = -1L;
    private static Object[] noArgsList;
    private static Class<?>[] noTypesList;
    private boolean isEnum;
    private static final Bridge bridge;
    private static final PersistentFieldsValue persistentFieldsValue;
    public static final int CLASS_MASK = 1553;
    public static final int FIELD_MASK = 223;
    public static final int METHOD_MASK = 3391;
    private static ObjectStreamClassEntry[] descriptorFor;
    private String name;
    private ObjectStreamClass superclass;
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
    private boolean initialized;
    private Object lock;
    private boolean hasExternalizableBlockData;
    Method writeObjectMethod;
    Method readObjectMethod;
    private transient Method writeReplaceObjectMethod;
    private transient Method readResolveObjectMethod;
    private Constructor<?> cons;
    private transient ProtectionDomain[] domains;
    private String rmiiiopOptionalDataRepId;
    private ObjectStreamClass localClassDesc;
    private static Method hasStaticInitializerMethod;
    private static final long serialVersionUID = -6120832682080437368L;
    public static final ObjectStreamField[] NO_FIELDS;
    private static Comparator compareClassByName;
    private static final Comparator compareObjStrFieldsByName;
    private static Comparator compareMemberByName;
    
    static final ObjectStreamClass lookup(final Class<?> clazz) {
        final ObjectStreamClass lookupInternal = lookupInternal(clazz);
        if (lookupInternal.isSerializable() || lookupInternal.isExternalizable()) {
            return lookupInternal;
        }
        return null;
    }
    
    static ObjectStreamClass lookupInternal(final Class<?> clazz) {
        ObjectStreamClass descriptor = null;
        synchronized (ObjectStreamClass.descriptorFor) {
            descriptor = findDescriptorFor(clazz);
            if (descriptor == null) {
                boolean assignable = Serializable.class.isAssignableFrom(clazz);
                ObjectStreamClass lookup = null;
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
                descriptor = new ObjectStreamClass(clazz, lookup, assignable, b);
            }
            descriptor.init();
        }
        return descriptor;
    }
    
    public final String getName() {
        return this.name;
    }
    
    public static final long getSerialVersionUID(final Class<?> clazz) {
        final ObjectStreamClass lookup = lookup(clazz);
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
        final ObjectStreamClass lookup = lookup(clazz);
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
        try {
            for (int i = 0; i < this.fields.length; ++i) {
                if (this.fields[i].getName().equals(valueMember.name) && this.fields[i].getSignature().equals(ValueUtility.getSignature(valueMember))) {
                    return true;
                }
            }
        }
        catch (final Exception ex) {}
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
                return (Serializable)this.writeReplaceObjectMethod.invoke(s, ObjectStreamClass.noArgsList);
            }
            catch (final Throwable t) {
                throw new RuntimeException(t);
            }
        }
        return s;
    }
    
    public Object readResolve(final Object o) {
        if (this.readResolveObjectMethod != null) {
            try {
                return this.readResolveObjectMethod.invoke(o, ObjectStreamClass.noArgsList);
            }
            catch (final Throwable t) {
                throw new RuntimeException(t);
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
    
    private ObjectStreamClass(final Class<?> ofClass, final ObjectStreamClass superclass, final boolean serializable, final boolean externalizable) {
        this.suid = -1L;
        this.suidStr = null;
        this.actualSuid = -1L;
        this.actualSuidStr = null;
        this.initialized = false;
        this.lock = new Object();
        this.rmiiiopOptionalDataRepId = null;
        this.ofClass = ofClass;
        if (Proxy.isProxyClass(ofClass)) {
            this.forProxyClass = true;
        }
        this.name = ofClass.getName();
        this.isEnum = Enum.class.isAssignableFrom(ofClass);
        this.superclass = superclass;
        this.serializable = serializable;
        if (!this.forProxyClass) {
            this.externalizable = externalizable;
        }
        insertDescriptorFor(this);
    }
    
    private ProtectionDomain noPermissionsDomain() {
        final Permissions permissions = new Permissions();
        permissions.setReadOnly();
        return new ProtectionDomain(null, permissions);
    }
    
    private ProtectionDomain[] getProtectionDomains(final Constructor<?> constructor, final Class<?> clazz) {
        ProtectionDomain[] array = null;
        if (constructor != null && clazz.getClassLoader() != null && System.getSecurityManager() != null) {
            Class<?> superclass = clazz;
            final Class<?> declaringClass = constructor.getDeclaringClass();
            Set<ProtectionDomain> set = null;
            while (superclass != declaringClass) {
                final ProtectionDomain protectionDomain = superclass.getProtectionDomain();
                if (protectionDomain != null) {
                    if (set == null) {
                        set = new HashSet<ProtectionDomain>();
                    }
                    set.add(protectionDomain);
                }
                superclass = superclass.getSuperclass();
                if (superclass == null) {
                    if (set == null) {
                        set = new HashSet<ProtectionDomain>();
                    }
                    else {
                        set.clear();
                    }
                    set.add(this.noPermissionsDomain());
                    break;
                }
            }
            if (set != null) {
                array = set.toArray(new ProtectionDomain[0]);
            }
        }
        return array;
    }
    
    private void init() {
        synchronized (this.lock) {
            if (this.initialized) {
                return;
            }
            final Class<?> ofClass = this.ofClass;
            if (!this.serializable || this.externalizable || this.forProxyClass || this.name.equals("java.lang.String")) {
                this.fields = ObjectStreamClass.NO_FIELDS;
            }
            else if (this.serializable) {
                AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                    @Override
                    public Object run() {
                        ObjectStreamClass.this.fields = ObjectStreamClass.persistentFieldsValue.get(ofClass);
                        if (ObjectStreamClass.this.fields == null) {
                            final Field[] declaredFields = ofClass.getDeclaredFields();
                            int n = 0;
                            final ObjectStreamField[] array = new ObjectStreamField[declaredFields.length];
                            for (int i = 0; i < declaredFields.length; ++i) {
                                final Field field = declaredFields[i];
                                final int modifiers = field.getModifiers();
                                if (!Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers)) {
                                    field.setAccessible(true);
                                    array[n++] = new ObjectStreamField(field);
                                }
                            }
                            ObjectStreamClass.this.fields = new ObjectStreamField[n];
                            System.arraycopy(array, 0, ObjectStreamClass.this.fields, 0, n);
                        }
                        else {
                            for (int j = ObjectStreamClass.this.fields.length - 1; j >= 0; --j) {
                                try {
                                    final Field declaredField = ofClass.getDeclaredField(ObjectStreamClass.this.fields[j].getName());
                                    if (ObjectStreamClass.this.fields[j].getType() == declaredField.getType()) {
                                        declaredField.setAccessible(true);
                                        ObjectStreamClass.this.fields[j].setField(declaredField);
                                    }
                                }
                                catch (final NoSuchFieldException ex) {}
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
            if (this.isNonSerializable() || this.isEnum) {
                this.suid = 0L;
            }
            else {
                AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                    @Override
                    public Object run() {
                        if (ObjectStreamClass.this.forProxyClass) {
                            ObjectStreamClass.this.suid = 0L;
                        }
                        else {
                            try {
                                final Field declaredField = ofClass.getDeclaredField("serialVersionUID");
                                final int modifiers = declaredField.getModifiers();
                                if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
                                    declaredField.setAccessible(true);
                                    ObjectStreamClass.this.suid = declaredField.getLong(ofClass);
                                }
                                else {
                                    ObjectStreamClass.this.suid = _computeSerialVersionUID(ofClass);
                                }
                            }
                            catch (final NoSuchFieldException ex) {
                                ObjectStreamClass.this.suid = _computeSerialVersionUID(ofClass);
                            }
                            catch (final IllegalAccessException ex2) {
                                ObjectStreamClass.this.suid = _computeSerialVersionUID(ofClass);
                            }
                        }
                        ObjectStreamClass.this.writeReplaceObjectMethod = getInheritableMethod(ofClass, "writeReplace", ObjectStreamClass.noTypesList, Object.class);
                        ObjectStreamClass.this.readResolveObjectMethod = getInheritableMethod(ofClass, "readResolve", ObjectStreamClass.noTypesList, Object.class);
                        ObjectStreamClass.this.domains = new ProtectionDomain[] { ObjectStreamClass.this.noPermissionsDomain() };
                        if (ObjectStreamClass.this.externalizable) {
                            ObjectStreamClass.this.cons = getExternalizableConstructor(ofClass);
                        }
                        else {
                            ObjectStreamClass.this.cons = getSerializableConstructor(ofClass);
                        }
                        ObjectStreamClass.this.domains = ObjectStreamClass.this.getProtectionDomains(ObjectStreamClass.this.cons, ofClass);
                        if (ObjectStreamClass.this.serializable && !ObjectStreamClass.this.forProxyClass) {
                            ObjectStreamClass.this.writeObjectMethod = getPrivateMethod(ofClass, "writeObject", new Class[] { ObjectOutputStream.class }, Void.TYPE);
                            ObjectStreamClass.this.readObjectMethod = getPrivateMethod(ofClass, "readObject", new Class[] { ObjectInputStream.class }, Void.TYPE);
                        }
                        return null;
                    }
                });
            }
            this.actualSuid = computeStructuralUID(this, ofClass);
            if (this.hasWriteObject()) {
                this.rmiiiopOptionalDataRepId = this.computeRMIIIOPOptionalDataRepId();
            }
            this.initialized = true;
        }
    }
    
    private static Method getPrivateMethod(final Class<?> clazz, final String s, final Class<?>[] array, final Class<?> clazz2) {
        try {
            final Method declaredMethod = clazz.getDeclaredMethod(s, (Class[])array);
            declaredMethod.setAccessible(true);
            final int modifiers = declaredMethod.getModifiers();
            return (declaredMethod.getReturnType() == clazz2 && (modifiers & 0x8) == 0x0 && (modifiers & 0x2) != 0x0) ? declaredMethod : null;
        }
        catch (final NoSuchMethodException ex) {
            return null;
        }
    }
    
    private String computeRMIIIOPOptionalDataRepId() {
        final StringBuffer sb = new StringBuffer("RMI:org.omg.custom.");
        sb.append(RepositoryId.convertToISOLatin1(this.getName()));
        sb.append(':');
        sb.append(this.getActualSerialVersionUIDStr());
        sb.append(':');
        sb.append(this.getSerialVersionUIDStr());
        return sb.toString();
    }
    
    public final String getRMIIIOPOptionalDataRepId() {
        return this.rmiiiopOptionalDataRepId;
    }
    
    ObjectStreamClass(final String name, final long suid) {
        this.suid = -1L;
        this.suidStr = null;
        this.actualSuid = -1L;
        this.actualSuidStr = null;
        this.initialized = false;
        this.lock = new Object();
        this.rmiiiopOptionalDataRepId = null;
        this.name = name;
        this.suid = suid;
        this.superclass = null;
    }
    
    final void setClass(final Class<?> ofClass) throws InvalidClassException {
        if (ofClass == null) {
            this.localClassDesc = null;
            this.ofClass = null;
            this.computeFieldInfo();
            return;
        }
        this.localClassDesc = lookupInternal(ofClass);
        if (this.localClassDesc == null) {
            throw new InvalidClassException(ofClass.getName(), "Local class not compatible");
        }
        if (this.suid != this.localClassDesc.suid) {
            final boolean b = this.isNonSerializable() || this.localClassDesc.isNonSerializable();
            if ((!ofClass.isArray() || ofClass.getName().equals(this.name)) && !b) {
                throw new InvalidClassException(ofClass.getName(), "Local class not compatible: stream classdesc serialVersionUID=" + this.suid + " local class serialVersionUID=" + this.localClassDesc.suid);
            }
        }
        if (!compareClassNames(this.name, ofClass.getName(), '.')) {
            throw new InvalidClassException(ofClass.getName(), "Incompatible local class name. Expected class name compatible with " + this.name);
        }
        if (this.serializable != this.localClassDesc.serializable || this.externalizable != this.localClassDesc.externalizable || (!this.serializable && !this.externalizable)) {
            throw new InvalidClassException(ofClass.getName(), "Serialization incompatible with Externalization");
        }
        final ObjectStreamField[] array = this.localClassDesc.fields;
        final ObjectStreamField[] array2 = this.fields;
        int n = 0;
        for (int i = 0; i < array2.length; ++i) {
            int j = n;
            while (j < array.length) {
                if (array2[i].getName().equals(array[j].getName())) {
                    if (array2[i].isPrimitive() && !array2[i].typeEquals(array[j])) {
                        throw new InvalidClassException(ofClass.getName(), "The type of field " + array2[i].getName() + " of class " + this.name + " is incompatible.");
                    }
                    n = j;
                    array2[i].setField(array[n].getField());
                    break;
                }
                else {
                    ++j;
                }
            }
        }
        this.computeFieldInfo();
        this.ofClass = ofClass;
        this.readObjectMethod = this.localClassDesc.readObjectMethod;
        this.readResolveObjectMethod = this.localClassDesc.readResolveObjectMethod;
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
    
    final boolean typeEquals(final ObjectStreamClass objectStreamClass) {
        return this.suid == objectStreamClass.suid && compareClassNames(this.name, objectStreamClass.name, '.');
    }
    
    final void setSuperclass(final ObjectStreamClass superclass) {
        this.superclass = superclass;
    }
    
    final ObjectStreamClass getSuperclass() {
        return this.superclass;
    }
    
    final boolean hasReadObject() {
        return this.readObjectMethod != null;
    }
    
    final boolean hasWriteObject() {
        return this.writeObjectMethod != null;
    }
    
    final boolean isCustomMarshaled() {
        return this.hasWriteObject() || this.isExternalizable() || (this.superclass != null && this.superclass.isCustomMarshaled());
    }
    
    boolean hasExternalizableBlockDataMode() {
        return this.hasExternalizableBlockData;
    }
    
    Object newInstance() throws InstantiationException, InvocationTargetException, UnsupportedOperationException {
        if (!this.initialized) {
            throw new InternalError("Unexpected call when not initialized");
        }
        if (this.cons != null) {
            try {
                if (this.domains == null || this.domains.length == 0) {
                    return this.cons.newInstance(new Object[0]);
                }
                final JavaSecurityAccess javaSecurityAccess = SharedSecrets.getJavaSecurityAccess();
                final PrivilegedAction privilegedAction = new PrivilegedAction() {
                    @Override
                    public Object run() {
                        try {
                            return ObjectStreamClass.this.cons.newInstance(new Object[0]);
                        }
                        catch (final InstantiationException | InvocationTargetException | IllegalAccessException ex) {
                            throw new UndeclaredThrowableException((Throwable)ex);
                        }
                    }
                };
                try {
                    return javaSecurityAccess.doIntersectionPrivilege((PrivilegedAction<Object>)privilegedAction, AccessController.getContext(), new AccessControlContext(this.domains));
                }
                catch (final UndeclaredThrowableException ex) {
                    final Throwable cause = ex.getCause();
                    if (cause instanceof InstantiationException) {
                        throw (InstantiationException)cause;
                    }
                    if (cause instanceof InvocationTargetException) {
                        throw (InvocationTargetException)cause;
                    }
                    if (cause instanceof IllegalAccessException) {
                        throw (IllegalAccessException)cause;
                    }
                    throw ex;
                }
            }
            catch (final IllegalAccessException ex2) {
                final InternalError internalError = new InternalError();
                internalError.initCause(ex2);
                throw internalError;
            }
        }
        throw new UnsupportedOperationException();
    }
    
    private static Constructor getExternalizableConstructor(final Class<?> clazz) {
        try {
            final Constructor<?> declaredConstructor = clazz.getDeclaredConstructor((Class<?>[])new Class[0]);
            declaredConstructor.setAccessible(true);
            return ((declaredConstructor.getModifiers() & 0x1) != 0x0) ? declaredConstructor : null;
        }
        catch (final NoSuchMethodException ex) {
            return null;
        }
    }
    
    private static Constructor getSerializableConstructor(final Class<?> clazz) {
        Class<?> superclass = clazz;
        while (Serializable.class.isAssignableFrom(superclass)) {
            if ((superclass = superclass.getSuperclass()) == null) {
                return null;
            }
        }
        try {
            final Constructor<?> declaredConstructor = superclass.getDeclaredConstructor((Class<?>[])new Class[0]);
            final int modifiers = declaredConstructor.getModifiers();
            if ((modifiers & 0x2) != 0x0 || ((modifiers & 0x5) == 0x0 && !packageEquals(clazz, superclass))) {
                return null;
            }
            final Constructor constructorForSerialization = ObjectStreamClass.bridge.newConstructorForSerialization(clazz, declaredConstructor);
            constructorForSerialization.setAccessible(true);
            return constructorForSerialization;
        }
        catch (final NoSuchMethodException ex) {
            return null;
        }
    }
    
    final ObjectStreamClass localClassDescriptor() {
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
    
    private static void msg(final String s) {
        System.out.println(s);
    }
    
    private static long _computeSerialVersionUID(final Class<?> clazz) {
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
            dataOutputStream.writeInt(n2 & 0x611);
            if (!clazz.isArray()) {
                final Class[] interfaces = clazz.getInterfaces();
                Arrays.sort(interfaces, ObjectStreamClass.compareClassByName);
                for (int i = 0; i < interfaces.length; ++i) {
                    dataOutputStream.writeUTF(interfaces[i].getName());
                }
            }
            final Field[] declaredFields = clazz.getDeclaredFields();
            Arrays.sort(declaredFields, ObjectStreamClass.compareMemberByName);
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
                dataOutputStream.writeInt(modifiers & 0xDF);
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
                dataOutputStream.writeInt(methodSignature.member.getModifiers() & 0xD3F);
                dataOutputStream.writeUTF(replace);
            }
            final MethodSignature[] removePrivateAndSort2 = MethodSignature.removePrivateAndSort(declaredMethods);
            for (int l = 0; l < removePrivateAndSort2.length; ++l) {
                final MethodSignature methodSignature2 = removePrivateAndSort2[l];
                final String replace2 = methodSignature2.signature.replace('/', '.');
                dataOutputStream.writeUTF(methodSignature2.member.getName());
                dataOutputStream.writeInt(methodSignature2.member.getModifiers() & 0xD3F);
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
            final SecurityException ex3 = new SecurityException();
            ex3.initCause(ex2);
            throw ex3;
        }
        return n;
    }
    
    private static long computeStructuralUID(final ObjectStreamClass objectStreamClass, final Class<?> clazz) {
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
            if (objectStreamClass.hasWriteObject()) {
                dataOutputStream.writeInt(2);
            }
            else {
                dataOutputStream.writeInt(1);
            }
            final ObjectStreamField[] fields = objectStreamClass.getFields();
            if (fields.length > 1) {
                Arrays.sort(fields, ObjectStreamClass.compareObjStrFieldsByName);
            }
            for (int i = 0; i < fields.length; ++i) {
                dataOutputStream.writeUTF(fields[i].getName());
                dataOutputStream.writeUTF(fields[i].getSignature());
            }
            dataOutputStream.flush();
            final byte[] digest = instance.digest();
            for (int j = 0; j < Math.min(8, digest.length); ++j) {
                n += (long)(digest[j] & 0xFF) << j * 8;
            }
        }
        catch (final IOException ex) {
            n = -1L;
        }
        catch (final NoSuchAlgorithmException ex2) {
            final SecurityException ex3 = new SecurityException();
            ex3.initCause(ex2);
            throw ex3;
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
    
    private static ObjectStreamClass findDescriptorFor(final Class<?> clazz) {
        final int n = (clazz.hashCode() & Integer.MAX_VALUE) % ObjectStreamClass.descriptorFor.length;
        ObjectStreamClassEntry next;
        while ((next = ObjectStreamClass.descriptorFor[n]) != null && next.get() == null) {
            ObjectStreamClass.descriptorFor[n] = next.next;
        }
        ObjectStreamClassEntry objectStreamClassEntry = next;
        while (next != null) {
            final ObjectStreamClass objectStreamClass = (ObjectStreamClass)next.get();
            if (objectStreamClass == null) {
                objectStreamClassEntry.next = next.next;
            }
            else {
                if (objectStreamClass.ofClass == clazz) {
                    return objectStreamClass;
                }
                objectStreamClassEntry = next;
            }
            next = next.next;
        }
        return null;
    }
    
    private static void insertDescriptorFor(final ObjectStreamClass objectStreamClass) {
        if (findDescriptorFor(objectStreamClass.ofClass) != null) {
            return;
        }
        final int n = (objectStreamClass.ofClass.hashCode() & Integer.MAX_VALUE) % ObjectStreamClass.descriptorFor.length;
        final ObjectStreamClassEntry objectStreamClassEntry = new ObjectStreamClassEntry(objectStreamClass);
        objectStreamClassEntry.next = ObjectStreamClass.descriptorFor[n];
        ObjectStreamClass.descriptorFor[n] = objectStreamClassEntry;
    }
    
    private static Field[] getDeclaredFields(final Class<?> clazz) {
        return AccessController.doPrivileged((PrivilegedAction<Field[]>)new PrivilegedAction() {
            @Override
            public Object run() {
                return clazz.getDeclaredFields();
            }
        });
    }
    
    private static boolean hasStaticInitializer(final Class<?> clazz) {
        if (ObjectStreamClass.hasStaticInitializerMethod == null) {
            Class<java.io.ObjectStreamClass> clazz2 = null;
            try {
                if (clazz2 == null) {
                    clazz2 = java.io.ObjectStreamClass.class;
                }
                ObjectStreamClass.hasStaticInitializerMethod = clazz2.getDeclaredMethod("hasStaticInitializer", Class.class);
            }
            catch (final NoSuchMethodException ex) {}
            if (ObjectStreamClass.hasStaticInitializerMethod == null) {
                throw new InternalError("Can't find hasStaticInitializer method on " + clazz2.getName());
            }
            ObjectStreamClass.hasStaticInitializerMethod.setAccessible(true);
        }
        try {
            return (boolean)ObjectStreamClass.hasStaticInitializerMethod.invoke(null, clazz);
        }
        catch (final Exception ex2) {
            final InternalError internalError = new InternalError("Error invoking hasStaticInitializer");
            internalError.initCause(ex2);
            throw internalError;
        }
    }
    
    private static Method getInheritableMethod(final Class<?> clazz, final String s, final Class<?>[] array, final Class<?> clazz2) {
        Method declaredMethod = null;
        Class<?> superclass = clazz;
        while (superclass != null) {
            try {
                declaredMethod = superclass.getDeclaredMethod(s, (Class[])array);
            }
            catch (final NoSuchMethodException ex) {
                superclass = superclass.getSuperclass();
                continue;
            }
            break;
        }
        if (declaredMethod == null || declaredMethod.getReturnType() != clazz2) {
            return null;
        }
        declaredMethod.setAccessible(true);
        final int modifiers = declaredMethod.getModifiers();
        if ((modifiers & 0x408) != 0x0) {
            return null;
        }
        if ((modifiers & 0x5) != 0x0) {
            return declaredMethod;
        }
        if ((modifiers & 0x2) != 0x0) {
            return (clazz == superclass) ? declaredMethod : null;
        }
        return packageEquals(clazz, superclass) ? declaredMethod : null;
    }
    
    private static boolean packageEquals(final Class<?> clazz, final Class<?> clazz2) {
        final Package package1 = clazz.getPackage();
        final Package package2 = clazz2.getPackage();
        return package1 == package2 || (package1 != null && package1.equals(package2));
    }
    
    static {
        ObjectStreamClass.noArgsList = new Object[0];
        ObjectStreamClass.noTypesList = new Class[0];
        bridge = AccessController.doPrivileged((PrivilegedAction<Bridge>)new PrivilegedAction<Bridge>() {
            @Override
            public Bridge run() {
                return Bridge.get();
            }
        });
        persistentFieldsValue = new PersistentFieldsValue();
        ObjectStreamClass.descriptorFor = new ObjectStreamClassEntry[61];
        ObjectStreamClass.hasStaticInitializerMethod = null;
        NO_FIELDS = new ObjectStreamField[0];
        ObjectStreamClass.compareClassByName = new CompareClassByName();
        compareObjStrFieldsByName = new CompareObjStrFieldsByName();
        ObjectStreamClass.compareMemberByName = new CompareMemberByName();
    }
    
    private static final class PersistentFieldsValue extends ClassValue<ObjectStreamField[]>
    {
        PersistentFieldsValue() {
        }
        
        @Override
        protected ObjectStreamField[] computeValue(final Class<?> clazz) {
            try {
                final Field declaredField = clazz.getDeclaredField("serialPersistentFields");
                final int modifiers = declaredField.getModifiers();
                if (Modifier.isPrivate(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
                    declaredField.setAccessible(true);
                    return translateFields((java.io.ObjectStreamField[])declaredField.get(clazz));
                }
            }
            catch (final NoSuchFieldException | IllegalAccessException | IllegalArgumentException | ClassCastException ex) {}
            return null;
        }
        
        private static ObjectStreamField[] translateFields(final java.io.ObjectStreamField[] array) {
            final ObjectStreamField[] array2 = new ObjectStreamField[array.length];
            for (int i = 0; i < array.length; ++i) {
                array2[i] = new ObjectStreamField(array[i].getName(), array[i].getType());
            }
            return array2;
        }
    }
    
    private static class ObjectStreamClassEntry
    {
        ObjectStreamClassEntry next;
        private ObjectStreamClass c;
        
        ObjectStreamClassEntry(final ObjectStreamClass c) {
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
    
    private static class CompareObjStrFieldsByName implements Comparator
    {
        @Override
        public int compare(final Object o, final Object o2) {
            return ((ObjectStreamField)o).getName().compareTo(((ObjectStreamField)o2).getName());
        }
    }
    
    private static class CompareMemberByName implements Comparator
    {
        @Override
        public int compare(final Object o, final Object o2) {
            String s = ((Member)o).getName();
            String s2 = ((Member)o2).getName();
            if (o instanceof Method) {
                s += ObjectStreamClass.getSignature((Method)o);
                s2 += ObjectStreamClass.getSignature((Method)o2);
            }
            else if (o instanceof Constructor) {
                s += ObjectStreamClass.getSignature((Constructor)o);
                s2 += ObjectStreamClass.getSignature((Constructor)o2);
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
                this.signature = ObjectStreamClass.getSignature((Constructor)member);
            }
            else {
                this.signature = ObjectStreamClass.getSignature((Method)member);
            }
        }
    }
}
