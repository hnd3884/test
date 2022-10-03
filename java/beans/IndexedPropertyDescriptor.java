package java.beans;

import java.lang.reflect.Method;
import java.lang.ref.Reference;

public class IndexedPropertyDescriptor extends PropertyDescriptor
{
    private Reference<? extends Class<?>> indexedPropertyTypeRef;
    private final MethodRef indexedReadMethodRef;
    private final MethodRef indexedWriteMethodRef;
    private String indexedReadMethodName;
    private String indexedWriteMethodName;
    
    public IndexedPropertyDescriptor(final String s, final Class<?> clazz) throws IntrospectionException {
        this(s, clazz, "get" + NameGenerator.capitalize(s), "set" + NameGenerator.capitalize(s), "get" + NameGenerator.capitalize(s), "set" + NameGenerator.capitalize(s));
    }
    
    public IndexedPropertyDescriptor(final String s, final Class<?> clazz, final String s2, final String s3, final String indexedReadMethodName, final String indexedWriteMethodName) throws IntrospectionException {
        super(s, clazz, s2, s3);
        this.indexedReadMethodRef = new MethodRef();
        this.indexedWriteMethodRef = new MethodRef();
        this.indexedReadMethodName = indexedReadMethodName;
        if (indexedReadMethodName != null && this.getIndexedReadMethod() == null) {
            throw new IntrospectionException("Method not found: " + indexedReadMethodName);
        }
        if ((this.indexedWriteMethodName = indexedWriteMethodName) != null && this.getIndexedWriteMethod() == null) {
            throw new IntrospectionException("Method not found: " + indexedWriteMethodName);
        }
        this.findIndexedPropertyType(this.getIndexedReadMethod(), this.getIndexedWriteMethod());
    }
    
    public IndexedPropertyDescriptor(final String s, final Method method, final Method method2, final Method indexedReadMethod0, final Method indexedWriteMethod0) throws IntrospectionException {
        super(s, method, method2);
        this.indexedReadMethodRef = new MethodRef();
        this.indexedWriteMethodRef = new MethodRef();
        this.setIndexedReadMethod0(indexedReadMethod0);
        this.setIndexedWriteMethod0(indexedWriteMethod0);
        this.setIndexedPropertyType(this.findIndexedPropertyType(indexedReadMethod0, indexedWriteMethod0));
    }
    
    IndexedPropertyDescriptor(final Class<?> clazz, final String s, final Method method, final Method method2, final Method indexedReadMethod0, final Method indexedWriteMethod0) throws IntrospectionException {
        super(clazz, s, method, method2);
        this.indexedReadMethodRef = new MethodRef();
        this.indexedWriteMethodRef = new MethodRef();
        this.setIndexedReadMethod0(indexedReadMethod0);
        this.setIndexedWriteMethod0(indexedWriteMethod0);
        this.setIndexedPropertyType(this.findIndexedPropertyType(indexedReadMethod0, indexedWriteMethod0));
    }
    
    public synchronized Method getIndexedReadMethod() {
        Method indexedReadMethod0 = this.indexedReadMethodRef.get();
        if (indexedReadMethod0 == null) {
            final Class<?> class0 = this.getClass0();
            if (class0 == null || (this.indexedReadMethodName == null && !this.indexedReadMethodRef.isSet())) {
                return null;
            }
            final String string = "get" + this.getBaseName();
            if (this.indexedReadMethodName == null) {
                final Class<?> indexedPropertyType0 = this.getIndexedPropertyType0();
                if (indexedPropertyType0 == Boolean.TYPE || indexedPropertyType0 == null) {
                    this.indexedReadMethodName = "is" + this.getBaseName();
                }
                else {
                    this.indexedReadMethodName = string;
                }
            }
            final Class[] array = { Integer.TYPE };
            indexedReadMethod0 = Introspector.findMethod(class0, this.indexedReadMethodName, 1, array);
            if (indexedReadMethod0 == null && !this.indexedReadMethodName.equals(string)) {
                this.indexedReadMethodName = string;
                indexedReadMethod0 = Introspector.findMethod(class0, this.indexedReadMethodName, 1, array);
            }
            this.setIndexedReadMethod0(indexedReadMethod0);
        }
        return indexedReadMethod0;
    }
    
    public synchronized void setIndexedReadMethod(final Method indexedReadMethod0) throws IntrospectionException {
        this.setIndexedPropertyType(this.findIndexedPropertyType(indexedReadMethod0, this.indexedWriteMethodRef.get()));
        this.setIndexedReadMethod0(indexedReadMethod0);
    }
    
    private void setIndexedReadMethod0(final Method method) {
        this.indexedReadMethodRef.set(method);
        if (method == null) {
            this.indexedReadMethodName = null;
            return;
        }
        this.setClass0(method.getDeclaringClass());
        this.indexedReadMethodName = method.getName();
        this.setTransient(method.getAnnotation(Transient.class));
    }
    
    public synchronized Method getIndexedWriteMethod() {
        Method indexedWriteMethod0 = this.indexedWriteMethodRef.get();
        if (indexedWriteMethod0 == null) {
            final Class<?> class0 = this.getClass0();
            if (class0 == null || (this.indexedWriteMethodName == null && !this.indexedWriteMethodRef.isSet())) {
                return null;
            }
            Class<?> indexedPropertyType = this.getIndexedPropertyType0();
            if (indexedPropertyType == null) {
                try {
                    indexedPropertyType = this.findIndexedPropertyType(this.getIndexedReadMethod(), null);
                    this.setIndexedPropertyType(indexedPropertyType);
                }
                catch (final IntrospectionException ex) {
                    final Class<?> propertyType = this.getPropertyType();
                    if (propertyType.isArray()) {
                        indexedPropertyType = propertyType.getComponentType();
                    }
                }
            }
            if (this.indexedWriteMethodName == null) {
                this.indexedWriteMethodName = "set" + this.getBaseName();
            }
            indexedWriteMethod0 = Introspector.findMethod(class0, this.indexedWriteMethodName, 2, (Class[])((indexedPropertyType == null) ? null : new Class[] { Integer.TYPE, indexedPropertyType }));
            if (indexedWriteMethod0 != null && !indexedWriteMethod0.getReturnType().equals(Void.TYPE)) {
                indexedWriteMethod0 = null;
            }
            this.setIndexedWriteMethod0(indexedWriteMethod0);
        }
        return indexedWriteMethod0;
    }
    
    public synchronized void setIndexedWriteMethod(final Method indexedWriteMethod0) throws IntrospectionException {
        this.setIndexedPropertyType(this.findIndexedPropertyType(this.getIndexedReadMethod(), indexedWriteMethod0));
        this.setIndexedWriteMethod0(indexedWriteMethod0);
    }
    
    private void setIndexedWriteMethod0(final Method method) {
        this.indexedWriteMethodRef.set(method);
        if (method == null) {
            this.indexedWriteMethodName = null;
            return;
        }
        this.setClass0(method.getDeclaringClass());
        this.indexedWriteMethodName = method.getName();
        this.setTransient(method.getAnnotation(Transient.class));
    }
    
    public synchronized Class<?> getIndexedPropertyType() {
        Class<?> indexedPropertyType = this.getIndexedPropertyType0();
        if (indexedPropertyType == null) {
            try {
                indexedPropertyType = this.findIndexedPropertyType(this.getIndexedReadMethod(), this.getIndexedWriteMethod());
                this.setIndexedPropertyType(indexedPropertyType);
            }
            catch (final IntrospectionException ex) {}
        }
        return indexedPropertyType;
    }
    
    private void setIndexedPropertyType(final Class<?> clazz) {
        this.indexedPropertyTypeRef = FeatureDescriptor.getWeakReference(clazz);
    }
    
    private Class<?> getIndexedPropertyType0() {
        return (this.indexedPropertyTypeRef != null) ? ((Class)this.indexedPropertyTypeRef.get()) : null;
    }
    
    private Class<?> findIndexedPropertyType(final Method method, final Method method2) throws IntrospectionException {
        Class<?> returnType = null;
        if (method != null) {
            final Class<?>[] parameterTypes = FeatureDescriptor.getParameterTypes(this.getClass0(), method);
            if (parameterTypes.length != 1) {
                throw new IntrospectionException("bad indexed read method arg count");
            }
            if (parameterTypes[0] != Integer.TYPE) {
                throw new IntrospectionException("non int index to indexed read method");
            }
            returnType = FeatureDescriptor.getReturnType(this.getClass0(), method);
            if (returnType == Void.TYPE) {
                throw new IntrospectionException("indexed read method returns void");
            }
        }
        if (method2 != null) {
            final Class<?>[] parameterTypes2 = FeatureDescriptor.getParameterTypes(this.getClass0(), method2);
            if (parameterTypes2.length != 2) {
                throw new IntrospectionException("bad indexed write method arg count");
            }
            if (parameterTypes2[0] != Integer.TYPE) {
                throw new IntrospectionException("non int index to indexed write method");
            }
            if (returnType == null || parameterTypes2[1].isAssignableFrom(returnType)) {
                returnType = parameterTypes2[1];
            }
            else if (!returnType.isAssignableFrom(parameterTypes2[1])) {
                throw new IntrospectionException("type mismatch between indexed read and indexed write methods: " + this.getName());
            }
        }
        final Class<?> propertyType = this.getPropertyType();
        if (propertyType != null && (!propertyType.isArray() || propertyType.getComponentType() != returnType)) {
            throw new IntrospectionException("type mismatch between indexed and non-indexed methods: " + this.getName());
        }
        return returnType;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o != null && o instanceof IndexedPropertyDescriptor) {
            final IndexedPropertyDescriptor indexedPropertyDescriptor = (IndexedPropertyDescriptor)o;
            final Method indexedReadMethod = indexedPropertyDescriptor.getIndexedReadMethod();
            final Method indexedWriteMethod = indexedPropertyDescriptor.getIndexedWriteMethod();
            return this.compareMethods(this.getIndexedReadMethod(), indexedReadMethod) && this.compareMethods(this.getIndexedWriteMethod(), indexedWriteMethod) && this.getIndexedPropertyType() == indexedPropertyDescriptor.getIndexedPropertyType() && super.equals(o);
        }
        return false;
    }
    
    IndexedPropertyDescriptor(final PropertyDescriptor propertyDescriptor, final PropertyDescriptor propertyDescriptor2) {
        super(propertyDescriptor, propertyDescriptor2);
        this.indexedReadMethodRef = new MethodRef();
        this.indexedWriteMethodRef = new MethodRef();
        Method indexedReadMethod = null;
        Method indexedWriteMethod = null;
        if (propertyDescriptor instanceof IndexedPropertyDescriptor) {
            final IndexedPropertyDescriptor indexedPropertyDescriptor = (IndexedPropertyDescriptor)propertyDescriptor;
            indexedReadMethod = indexedPropertyDescriptor.getIndexedReadMethod();
            indexedWriteMethod = indexedPropertyDescriptor.getIndexedWriteMethod();
        }
        if (propertyDescriptor2 instanceof IndexedPropertyDescriptor) {
            final IndexedPropertyDescriptor indexedPropertyDescriptor2 = (IndexedPropertyDescriptor)propertyDescriptor2;
            final Method indexedReadMethod2 = indexedPropertyDescriptor2.getIndexedReadMethod();
            if (this.isAssignable(indexedReadMethod, indexedReadMethod2)) {
                indexedReadMethod = indexedReadMethod2;
            }
            final Method indexedWriteMethod2 = indexedPropertyDescriptor2.getIndexedWriteMethod();
            if (this.isAssignable(indexedWriteMethod, indexedWriteMethod2)) {
                indexedWriteMethod = indexedWriteMethod2;
            }
        }
        try {
            if (indexedReadMethod != null) {
                this.setIndexedReadMethod(indexedReadMethod);
            }
            if (indexedWriteMethod != null) {
                this.setIndexedWriteMethod(indexedWriteMethod);
            }
        }
        catch (final IntrospectionException ex) {
            throw new AssertionError((Object)ex);
        }
    }
    
    IndexedPropertyDescriptor(final IndexedPropertyDescriptor indexedPropertyDescriptor) {
        super(indexedPropertyDescriptor);
        this.indexedReadMethodRef = new MethodRef();
        this.indexedWriteMethodRef = new MethodRef();
        this.indexedReadMethodRef.set(indexedPropertyDescriptor.indexedReadMethodRef.get());
        this.indexedWriteMethodRef.set(indexedPropertyDescriptor.indexedWriteMethodRef.get());
        this.indexedPropertyTypeRef = indexedPropertyDescriptor.indexedPropertyTypeRef;
        this.indexedWriteMethodName = indexedPropertyDescriptor.indexedWriteMethodName;
        this.indexedReadMethodName = indexedPropertyDescriptor.indexedReadMethodName;
    }
    
    @Override
    void updateGenericsFor(final Class<?> clazz) {
        super.updateGenericsFor(clazz);
        try {
            this.setIndexedPropertyType(this.findIndexedPropertyType(this.indexedReadMethodRef.get(), this.indexedWriteMethodRef.get()));
        }
        catch (final IntrospectionException ex) {
            this.setIndexedPropertyType(null);
        }
    }
    
    @Override
    public int hashCode() {
        return 37 * (37 * (37 * super.hashCode() + ((this.indexedWriteMethodName == null) ? 0 : this.indexedWriteMethodName.hashCode())) + ((this.indexedReadMethodName == null) ? 0 : this.indexedReadMethodName.hashCode())) + ((this.getIndexedPropertyType() == null) ? 0 : this.getIndexedPropertyType().hashCode());
    }
    
    @Override
    void appendTo(final StringBuilder sb) {
        super.appendTo(sb);
        FeatureDescriptor.appendTo(sb, "indexedPropertyType", this.indexedPropertyTypeRef);
        FeatureDescriptor.appendTo(sb, "indexedReadMethod", this.indexedReadMethodRef.get());
        FeatureDescriptor.appendTo(sb, "indexedWriteMethod", this.indexedWriteMethodRef.get());
    }
}
