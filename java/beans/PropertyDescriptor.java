package java.beans;

import java.lang.reflect.Constructor;
import sun.reflect.misc.ReflectUtil;
import java.lang.reflect.Method;
import java.lang.ref.Reference;

public class PropertyDescriptor extends FeatureDescriptor
{
    private Reference<? extends Class<?>> propertyTypeRef;
    private final MethodRef readMethodRef;
    private final MethodRef writeMethodRef;
    private Reference<? extends Class<?>> propertyEditorClassRef;
    private boolean bound;
    private boolean constrained;
    private String baseName;
    private String writeMethodName;
    private String readMethodName;
    
    public PropertyDescriptor(final String s, final Class<?> clazz) throws IntrospectionException {
        this(s, clazz, "is" + NameGenerator.capitalize(s), "set" + NameGenerator.capitalize(s));
    }
    
    public PropertyDescriptor(final String name, final Class<?> class0, final String readMethodName, final String writeMethodName) throws IntrospectionException {
        this.readMethodRef = new MethodRef();
        this.writeMethodRef = new MethodRef();
        if (class0 == null) {
            throw new IntrospectionException("Target Bean class is null");
        }
        if (name == null || name.length() == 0) {
            throw new IntrospectionException("bad property name");
        }
        if ("".equals(readMethodName) || "".equals(writeMethodName)) {
            throw new IntrospectionException("read or write method name should not be the empty string");
        }
        this.setName(name);
        this.setClass0(class0);
        this.readMethodName = readMethodName;
        if (readMethodName != null && this.getReadMethod() == null) {
            throw new IntrospectionException("Method not found: " + readMethodName);
        }
        if ((this.writeMethodName = writeMethodName) != null && this.getWriteMethod() == null) {
            throw new IntrospectionException("Method not found: " + writeMethodName);
        }
        final Class[] array = { PropertyChangeListener.class };
        this.bound = (null != Introspector.findMethod(class0, "addPropertyChangeListener", array.length, array));
    }
    
    public PropertyDescriptor(final String name, final Method readMethod, final Method writeMethod) throws IntrospectionException {
        this.readMethodRef = new MethodRef();
        this.writeMethodRef = new MethodRef();
        if (name == null || name.length() == 0) {
            throw new IntrospectionException("bad property name");
        }
        this.setName(name);
        this.setReadMethod(readMethod);
        this.setWriteMethod(writeMethod);
    }
    
    PropertyDescriptor(final Class<?> class0, final String baseName, final Method readMethod, final Method writeMethod) throws IntrospectionException {
        this.readMethodRef = new MethodRef();
        this.writeMethodRef = new MethodRef();
        if (class0 == null) {
            throw new IntrospectionException("Target Bean class is null");
        }
        this.setClass0(class0);
        this.setName(Introspector.decapitalize(baseName));
        this.setReadMethod(readMethod);
        this.setWriteMethod(writeMethod);
        this.baseName = baseName;
    }
    
    public synchronized Class<?> getPropertyType() {
        Class<?> propertyType = this.getPropertyType0();
        if (propertyType == null) {
            try {
                propertyType = this.findPropertyType(this.getReadMethod(), this.getWriteMethod());
                this.setPropertyType(propertyType);
            }
            catch (final IntrospectionException ex) {}
        }
        return propertyType;
    }
    
    private void setPropertyType(final Class<?> clazz) {
        this.propertyTypeRef = FeatureDescriptor.getWeakReference(clazz);
    }
    
    private Class<?> getPropertyType0() {
        return (this.propertyTypeRef != null) ? ((Class)this.propertyTypeRef.get()) : null;
    }
    
    public synchronized Method getReadMethod() {
        Method readMethod = this.readMethodRef.get();
        if (readMethod == null) {
            final Class<?> class0 = this.getClass0();
            if (class0 == null || (this.readMethodName == null && !this.readMethodRef.isSet())) {
                return null;
            }
            final String string = "get" + this.getBaseName();
            if (this.readMethodName == null) {
                final Class<?> propertyType0 = this.getPropertyType0();
                if (propertyType0 == Boolean.TYPE || propertyType0 == null) {
                    this.readMethodName = "is" + this.getBaseName();
                }
                else {
                    this.readMethodName = string;
                }
            }
            readMethod = Introspector.findMethod(class0, this.readMethodName, 0);
            if (readMethod == null && !this.readMethodName.equals(string)) {
                this.readMethodName = string;
                readMethod = Introspector.findMethod(class0, this.readMethodName, 0);
            }
            try {
                this.setReadMethod(readMethod);
            }
            catch (final IntrospectionException ex) {}
        }
        return readMethod;
    }
    
    public synchronized void setReadMethod(final Method method) throws IntrospectionException {
        this.readMethodRef.set(method);
        if (method == null) {
            this.readMethodName = null;
            return;
        }
        this.setPropertyType(this.findPropertyType(method, this.writeMethodRef.get()));
        this.setClass0(method.getDeclaringClass());
        this.readMethodName = method.getName();
        this.setTransient(method.getAnnotation(Transient.class));
    }
    
    public synchronized Method getWriteMethod() {
        Method writeMethod = this.writeMethodRef.get();
        if (writeMethod == null) {
            final Class<?> class0 = this.getClass0();
            if (class0 == null || (this.writeMethodName == null && !this.writeMethodRef.isSet())) {
                return null;
            }
            Class<?> propertyType = this.getPropertyType0();
            if (propertyType == null) {
                try {
                    propertyType = this.findPropertyType(this.getReadMethod(), null);
                    this.setPropertyType(propertyType);
                }
                catch (final IntrospectionException ex) {
                    return null;
                }
            }
            if (this.writeMethodName == null) {
                this.writeMethodName = "set" + this.getBaseName();
            }
            writeMethod = Introspector.findMethod(class0, this.writeMethodName, 1, (Class[])((propertyType == null) ? null : new Class[] { propertyType }));
            if (writeMethod != null && !writeMethod.getReturnType().equals(Void.TYPE)) {
                writeMethod = null;
            }
            try {
                this.setWriteMethod(writeMethod);
            }
            catch (final IntrospectionException ex2) {}
        }
        return writeMethod;
    }
    
    public synchronized void setWriteMethod(final Method method) throws IntrospectionException {
        this.writeMethodRef.set(method);
        if (method == null) {
            this.writeMethodName = null;
            return;
        }
        this.setPropertyType(this.findPropertyType(this.getReadMethod(), method));
        this.setClass0(method.getDeclaringClass());
        this.writeMethodName = method.getName();
        this.setTransient(method.getAnnotation(Transient.class));
    }
    
    @Override
    void setClass0(final Class<?> class0) {
        if (this.getClass0() != null && class0.isAssignableFrom(this.getClass0())) {
            return;
        }
        super.setClass0(class0);
    }
    
    public boolean isBound() {
        return this.bound;
    }
    
    public void setBound(final boolean bound) {
        this.bound = bound;
    }
    
    public boolean isConstrained() {
        return this.constrained;
    }
    
    public void setConstrained(final boolean constrained) {
        this.constrained = constrained;
    }
    
    public void setPropertyEditorClass(final Class<?> clazz) {
        this.propertyEditorClassRef = FeatureDescriptor.getWeakReference(clazz);
    }
    
    public Class<?> getPropertyEditorClass() {
        return (this.propertyEditorClassRef != null) ? ((Class)this.propertyEditorClassRef.get()) : null;
    }
    
    public PropertyEditor createPropertyEditor(final Object o) {
        Object o2 = null;
        final Class<?> propertyEditorClass = this.getPropertyEditorClass();
        if (propertyEditorClass != null && PropertyEditor.class.isAssignableFrom(propertyEditorClass) && ReflectUtil.isPackageAccessible(propertyEditorClass)) {
            Constructor<?> constructor = null;
            if (o != null) {
                try {
                    constructor = propertyEditorClass.getConstructor(Object.class);
                }
                catch (final Exception ex) {}
            }
            try {
                if (constructor == null) {
                    o2 = propertyEditorClass.newInstance();
                }
                else {
                    o2 = constructor.newInstance(o);
                }
            }
            catch (final Exception ex2) {}
        }
        return (PropertyEditor)o2;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o != null && o instanceof PropertyDescriptor) {
            final PropertyDescriptor propertyDescriptor = (PropertyDescriptor)o;
            final Method readMethod = propertyDescriptor.getReadMethod();
            final Method writeMethod = propertyDescriptor.getWriteMethod();
            if (!this.compareMethods(this.getReadMethod(), readMethod)) {
                return false;
            }
            if (!this.compareMethods(this.getWriteMethod(), writeMethod)) {
                return false;
            }
            if (this.getPropertyType() == propertyDescriptor.getPropertyType() && this.getPropertyEditorClass() == propertyDescriptor.getPropertyEditorClass() && this.bound == propertyDescriptor.isBound() && this.constrained == propertyDescriptor.isConstrained() && this.writeMethodName == propertyDescriptor.writeMethodName && this.readMethodName == propertyDescriptor.readMethodName) {
                return true;
            }
        }
        return false;
    }
    
    boolean compareMethods(final Method method, final Method method2) {
        return method == null == (method2 == null) && (method == null || method2 == null || method.equals(method2));
    }
    
    PropertyDescriptor(final PropertyDescriptor propertyDescriptor, final PropertyDescriptor propertyDescriptor2) {
        super(propertyDescriptor, propertyDescriptor2);
        this.readMethodRef = new MethodRef();
        this.writeMethodRef = new MethodRef();
        if (propertyDescriptor2.baseName != null) {
            this.baseName = propertyDescriptor2.baseName;
        }
        else {
            this.baseName = propertyDescriptor.baseName;
        }
        if (propertyDescriptor2.readMethodName != null) {
            this.readMethodName = propertyDescriptor2.readMethodName;
        }
        else {
            this.readMethodName = propertyDescriptor.readMethodName;
        }
        if (propertyDescriptor2.writeMethodName != null) {
            this.writeMethodName = propertyDescriptor2.writeMethodName;
        }
        else {
            this.writeMethodName = propertyDescriptor.writeMethodName;
        }
        if (propertyDescriptor2.propertyTypeRef != null) {
            this.propertyTypeRef = propertyDescriptor2.propertyTypeRef;
        }
        else {
            this.propertyTypeRef = propertyDescriptor.propertyTypeRef;
        }
        final Method readMethod = propertyDescriptor.getReadMethod();
        final Method readMethod2 = propertyDescriptor2.getReadMethod();
        try {
            if (this.isAssignable(readMethod, readMethod2)) {
                this.setReadMethod(readMethod2);
            }
            else {
                this.setReadMethod(readMethod);
            }
        }
        catch (final IntrospectionException ex) {}
        if (readMethod != null && readMethod2 != null && readMethod.getDeclaringClass() == readMethod2.getDeclaringClass() && FeatureDescriptor.getReturnType(this.getClass0(), readMethod) == Boolean.TYPE && FeatureDescriptor.getReturnType(this.getClass0(), readMethod2) == Boolean.TYPE && readMethod.getName().indexOf("is") == 0 && readMethod2.getName().indexOf("get") == 0) {
            try {
                this.setReadMethod(readMethod);
            }
            catch (final IntrospectionException ex2) {}
        }
        final Method writeMethod = propertyDescriptor.getWriteMethod();
        final Method writeMethod2 = propertyDescriptor2.getWriteMethod();
        try {
            if (writeMethod2 != null) {
                this.setWriteMethod(writeMethod2);
            }
            else {
                this.setWriteMethod(writeMethod);
            }
        }
        catch (final IntrospectionException ex3) {}
        if (propertyDescriptor2.getPropertyEditorClass() != null) {
            this.setPropertyEditorClass(propertyDescriptor2.getPropertyEditorClass());
        }
        else {
            this.setPropertyEditorClass(propertyDescriptor.getPropertyEditorClass());
        }
        this.bound = (propertyDescriptor.bound | propertyDescriptor2.bound);
        this.constrained = (propertyDescriptor.constrained | propertyDescriptor2.constrained);
    }
    
    PropertyDescriptor(final PropertyDescriptor propertyDescriptor) {
        super(propertyDescriptor);
        this.readMethodRef = new MethodRef();
        this.writeMethodRef = new MethodRef();
        this.propertyTypeRef = propertyDescriptor.propertyTypeRef;
        this.readMethodRef.set(propertyDescriptor.readMethodRef.get());
        this.writeMethodRef.set(propertyDescriptor.writeMethodRef.get());
        this.propertyEditorClassRef = propertyDescriptor.propertyEditorClassRef;
        this.writeMethodName = propertyDescriptor.writeMethodName;
        this.readMethodName = propertyDescriptor.readMethodName;
        this.baseName = propertyDescriptor.baseName;
        this.bound = propertyDescriptor.bound;
        this.constrained = propertyDescriptor.constrained;
    }
    
    void updateGenericsFor(final Class<?> class0) {
        this.setClass0(class0);
        try {
            this.setPropertyType(this.findPropertyType(this.readMethodRef.get(), this.writeMethodRef.get()));
        }
        catch (final IntrospectionException ex) {
            this.setPropertyType(null);
        }
    }
    
    private Class<?> findPropertyType(final Method method, final Method method2) throws IntrospectionException {
        Class<?> returnType = null;
        try {
            if (method != null) {
                if (FeatureDescriptor.getParameterTypes(this.getClass0(), method).length != 0) {
                    throw new IntrospectionException("bad read method arg count: " + method);
                }
                returnType = FeatureDescriptor.getReturnType(this.getClass0(), method);
                if (returnType == Void.TYPE) {
                    throw new IntrospectionException("read method " + method.getName() + " returns void");
                }
            }
            if (method2 != null) {
                final Class<?>[] parameterTypes = FeatureDescriptor.getParameterTypes(this.getClass0(), method2);
                if (parameterTypes.length != 1) {
                    throw new IntrospectionException("bad write method arg count: " + method2);
                }
                if (returnType != null && !parameterTypes[0].isAssignableFrom(returnType)) {
                    throw new IntrospectionException("type mismatch between read and write methods");
                }
                returnType = parameterTypes[0];
            }
        }
        catch (final IntrospectionException ex) {
            throw ex;
        }
        return returnType;
    }
    
    @Override
    public int hashCode() {
        return 37 * (37 * (37 * (37 * (37 * (37 * (37 * (37 * (37 * 7 + ((this.getPropertyType() == null) ? 0 : this.getPropertyType().hashCode())) + ((this.getReadMethod() == null) ? 0 : this.getReadMethod().hashCode())) + ((this.getWriteMethod() == null) ? 0 : this.getWriteMethod().hashCode())) + ((this.getPropertyEditorClass() == null) ? 0 : this.getPropertyEditorClass().hashCode())) + ((this.writeMethodName == null) ? 0 : this.writeMethodName.hashCode())) + ((this.readMethodName == null) ? 0 : this.readMethodName.hashCode())) + this.getName().hashCode()) + (this.bound ? 1 : 0)) + (this.constrained ? 1 : 0);
    }
    
    String getBaseName() {
        if (this.baseName == null) {
            this.baseName = NameGenerator.capitalize(this.getName());
        }
        return this.baseName;
    }
    
    @Override
    void appendTo(final StringBuilder sb) {
        FeatureDescriptor.appendTo(sb, "bound", this.bound);
        FeatureDescriptor.appendTo(sb, "constrained", this.constrained);
        FeatureDescriptor.appendTo(sb, "propertyEditorClass", this.propertyEditorClassRef);
        FeatureDescriptor.appendTo(sb, "propertyType", this.propertyTypeRef);
        FeatureDescriptor.appendTo(sb, "readMethod", this.readMethodRef.get());
        FeatureDescriptor.appendTo(sb, "writeMethod", this.writeMethodRef.get());
    }
    
    boolean isAssignable(final Method method, final Method method2) {
        if (method == null) {
            return true;
        }
        if (method2 == null) {
            return false;
        }
        if (!method.getName().equals(method2.getName())) {
            return true;
        }
        if (!method.getDeclaringClass().isAssignableFrom(method2.getDeclaringClass())) {
            return false;
        }
        if (!FeatureDescriptor.getReturnType(this.getClass0(), method).isAssignableFrom(FeatureDescriptor.getReturnType(this.getClass0(), method2))) {
            return false;
        }
        final Class<?>[] parameterTypes = FeatureDescriptor.getParameterTypes(this.getClass0(), method);
        final Class<?>[] parameterTypes2 = FeatureDescriptor.getParameterTypes(this.getClass0(), method2);
        if (parameterTypes.length != parameterTypes2.length) {
            return true;
        }
        for (int i = 0; i < parameterTypes.length; ++i) {
            if (!parameterTypes[i].isAssignableFrom(parameterTypes2[i])) {
                return false;
            }
        }
        return true;
    }
}
