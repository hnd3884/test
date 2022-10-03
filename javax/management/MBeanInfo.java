package javax.management;

import java.lang.reflect.Method;
import java.util.WeakHashMap;
import java.io.StreamCorruptedException;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.PrivilegedAction;
import java.security.AccessController;
import java.util.Objects;
import java.util.Arrays;
import java.util.Map;
import java.io.Serializable;

public class MBeanInfo implements Cloneable, Serializable, DescriptorRead
{
    static final long serialVersionUID = -6451021435135161911L;
    private transient Descriptor descriptor;
    private final String description;
    private final String className;
    private final MBeanAttributeInfo[] attributes;
    private final MBeanOperationInfo[] operations;
    private final MBeanConstructorInfo[] constructors;
    private final MBeanNotificationInfo[] notifications;
    private transient int hashCode;
    private final transient boolean arrayGettersSafe;
    private static final Map<Class<?>, Boolean> arrayGettersSafeMap;
    
    public MBeanInfo(final String s, final String s2, final MBeanAttributeInfo[] array, final MBeanConstructorInfo[] array2, final MBeanOperationInfo[] array3, final MBeanNotificationInfo[] array4) throws IllegalArgumentException {
        this(s, s2, array, array2, array3, array4, null);
    }
    
    public MBeanInfo(final String className, final String description, MBeanAttributeInfo[] no_ATTRIBUTES, MBeanConstructorInfo[] no_CONSTRUCTORS, MBeanOperationInfo[] no_OPERATIONS, MBeanNotificationInfo[] no_NOTIFICATIONS, Descriptor empty_DESCRIPTOR) throws IllegalArgumentException {
        this.className = className;
        this.description = description;
        if (no_ATTRIBUTES == null) {
            no_ATTRIBUTES = MBeanAttributeInfo.NO_ATTRIBUTES;
        }
        this.attributes = no_ATTRIBUTES;
        if (no_OPERATIONS == null) {
            no_OPERATIONS = MBeanOperationInfo.NO_OPERATIONS;
        }
        this.operations = no_OPERATIONS;
        if (no_CONSTRUCTORS == null) {
            no_CONSTRUCTORS = MBeanConstructorInfo.NO_CONSTRUCTORS;
        }
        this.constructors = no_CONSTRUCTORS;
        if (no_NOTIFICATIONS == null) {
            no_NOTIFICATIONS = MBeanNotificationInfo.NO_NOTIFICATIONS;
        }
        this.notifications = no_NOTIFICATIONS;
        if (empty_DESCRIPTOR == null) {
            empty_DESCRIPTOR = ImmutableDescriptor.EMPTY_DESCRIPTOR;
        }
        this.descriptor = empty_DESCRIPTOR;
        this.arrayGettersSafe = arrayGettersSafe(this.getClass(), MBeanInfo.class);
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (final CloneNotSupportedException ex) {
            return null;
        }
    }
    
    public String getClassName() {
        return this.className;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public MBeanAttributeInfo[] getAttributes() {
        final MBeanAttributeInfo[] nonNullAttributes = this.nonNullAttributes();
        if (nonNullAttributes.length == 0) {
            return nonNullAttributes;
        }
        return nonNullAttributes.clone();
    }
    
    private MBeanAttributeInfo[] fastGetAttributes() {
        if (this.arrayGettersSafe) {
            return this.nonNullAttributes();
        }
        return this.getAttributes();
    }
    
    private MBeanAttributeInfo[] nonNullAttributes() {
        return (this.attributes == null) ? MBeanAttributeInfo.NO_ATTRIBUTES : this.attributes;
    }
    
    public MBeanOperationInfo[] getOperations() {
        final MBeanOperationInfo[] nonNullOperations = this.nonNullOperations();
        if (nonNullOperations.length == 0) {
            return nonNullOperations;
        }
        return nonNullOperations.clone();
    }
    
    private MBeanOperationInfo[] fastGetOperations() {
        if (this.arrayGettersSafe) {
            return this.nonNullOperations();
        }
        return this.getOperations();
    }
    
    private MBeanOperationInfo[] nonNullOperations() {
        return (this.operations == null) ? MBeanOperationInfo.NO_OPERATIONS : this.operations;
    }
    
    public MBeanConstructorInfo[] getConstructors() {
        final MBeanConstructorInfo[] nonNullConstructors = this.nonNullConstructors();
        if (nonNullConstructors.length == 0) {
            return nonNullConstructors;
        }
        return nonNullConstructors.clone();
    }
    
    private MBeanConstructorInfo[] fastGetConstructors() {
        if (this.arrayGettersSafe) {
            return this.nonNullConstructors();
        }
        return this.getConstructors();
    }
    
    private MBeanConstructorInfo[] nonNullConstructors() {
        return (this.constructors == null) ? MBeanConstructorInfo.NO_CONSTRUCTORS : this.constructors;
    }
    
    public MBeanNotificationInfo[] getNotifications() {
        final MBeanNotificationInfo[] nonNullNotifications = this.nonNullNotifications();
        if (nonNullNotifications.length == 0) {
            return nonNullNotifications;
        }
        return nonNullNotifications.clone();
    }
    
    private MBeanNotificationInfo[] fastGetNotifications() {
        if (this.arrayGettersSafe) {
            return this.nonNullNotifications();
        }
        return this.getNotifications();
    }
    
    private MBeanNotificationInfo[] nonNullNotifications() {
        return (this.notifications == null) ? MBeanNotificationInfo.NO_NOTIFICATIONS : this.notifications;
    }
    
    @Override
    public Descriptor getDescriptor() {
        return (Descriptor)ImmutableDescriptor.nonNullDescriptor(this.descriptor).clone();
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "[description=" + this.getDescription() + ", attributes=" + Arrays.asList(this.fastGetAttributes()) + ", constructors=" + Arrays.asList(this.fastGetConstructors()) + ", operations=" + Arrays.asList(this.fastGetOperations()) + ", notifications=" + Arrays.asList(this.fastGetNotifications()) + ", descriptor=" + this.getDescriptor() + "]";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof MBeanInfo)) {
            return false;
        }
        final MBeanInfo mBeanInfo = (MBeanInfo)o;
        return isEqual(this.getClassName(), mBeanInfo.getClassName()) && isEqual(this.getDescription(), mBeanInfo.getDescription()) && this.getDescriptor().equals(mBeanInfo.getDescriptor()) && Arrays.equals(mBeanInfo.fastGetAttributes(), this.fastGetAttributes()) && Arrays.equals(mBeanInfo.fastGetOperations(), this.fastGetOperations()) && Arrays.equals(mBeanInfo.fastGetConstructors(), this.fastGetConstructors()) && Arrays.equals(mBeanInfo.fastGetNotifications(), this.fastGetNotifications());
    }
    
    @Override
    public int hashCode() {
        if (this.hashCode != 0) {
            return this.hashCode;
        }
        return this.hashCode = (Objects.hash(this.getClassName(), this.getDescriptor()) ^ Arrays.hashCode(this.fastGetAttributes()) ^ Arrays.hashCode(this.fastGetOperations()) ^ Arrays.hashCode(this.fastGetConstructors()) ^ Arrays.hashCode(this.fastGetNotifications()));
    }
    
    static boolean arrayGettersSafe(final Class<?> clazz, final Class<?> clazz2) {
        if (clazz == clazz2) {
            return true;
        }
        synchronized (MBeanInfo.arrayGettersSafeMap) {
            Boolean value = MBeanInfo.arrayGettersSafeMap.get(clazz);
            if (value == null) {
                try {
                    value = AccessController.doPrivileged((PrivilegedAction<Boolean>)new ArrayGettersSafeAction(clazz, clazz2));
                }
                catch (final Exception ex) {
                    value = false;
                }
                MBeanInfo.arrayGettersSafeMap.put(clazz, value);
            }
            return value;
        }
    }
    
    private static boolean isEqual(final String s, final String s2) {
        boolean equals;
        if (s == null) {
            equals = (s2 == null);
        }
        else {
            equals = s.equals(s2);
        }
        return equals;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.descriptor.getClass() == ImmutableDescriptor.class) {
            objectOutputStream.write(1);
            final String[] fieldNames = this.descriptor.getFieldNames();
            objectOutputStream.writeObject(fieldNames);
            objectOutputStream.writeObject(this.descriptor.getFieldValues(fieldNames));
        }
        else {
            objectOutputStream.write(0);
            objectOutputStream.writeObject(this.descriptor);
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        switch (objectInputStream.read()) {
            case 1: {
                final String[] array = (String[])objectInputStream.readObject();
                final Object[] array2 = (Object[])objectInputStream.readObject();
                this.descriptor = ((array.length == 0) ? ImmutableDescriptor.EMPTY_DESCRIPTOR : new ImmutableDescriptor(array, array2));
                break;
            }
            case 0: {
                this.descriptor = (Descriptor)objectInputStream.readObject();
                if (this.descriptor == null) {
                    this.descriptor = ImmutableDescriptor.EMPTY_DESCRIPTOR;
                    break;
                }
                break;
            }
            case -1: {
                this.descriptor = ImmutableDescriptor.EMPTY_DESCRIPTOR;
                break;
            }
            default: {
                throw new StreamCorruptedException("Got unexpected byte.");
            }
        }
    }
    
    static {
        arrayGettersSafeMap = new WeakHashMap<Class<?>, Boolean>();
    }
    
    private static class ArrayGettersSafeAction implements PrivilegedAction<Boolean>
    {
        private final Class<?> subclass;
        private final Class<?> immutableClass;
        
        ArrayGettersSafeAction(final Class<?> subclass, final Class<?> immutableClass) {
            this.subclass = subclass;
            this.immutableClass = immutableClass;
        }
        
        @Override
        public Boolean run() {
            final Method[] methods = this.immutableClass.getMethods();
            for (int i = 0; i < methods.length; ++i) {
                final Method method = methods[i];
                final String name = method.getName();
                if (name.startsWith("get") && method.getParameterTypes().length == 0 && method.getReturnType().isArray()) {
                    try {
                        if (!this.subclass.getMethod(name, (Class<?>[])new Class[0]).equals(method)) {
                            return false;
                        }
                    }
                    catch (final NoSuchMethodException ex) {
                        return false;
                    }
                }
            }
            return true;
        }
    }
}
