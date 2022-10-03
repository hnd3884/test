package org.msgpack.template.builder.beans;

import java.lang.reflect.Constructor;
import org.apache.harmony.beans.BeansUtils;
import java.lang.reflect.Modifier;
import org.apache.harmony.beans.internal.nls.Messages;
import java.lang.reflect.Method;

public class PropertyDescriptor extends FeatureDescriptor
{
    private Method getter;
    private Method setter;
    private Class<?> propertyEditorClass;
    boolean constrained;
    boolean bound;
    
    public PropertyDescriptor(final String propertyName, final Class<?> beanClass, final String getterName, final String setterName) throws IntrospectionException {
        if (beanClass == null) {
            throw new IntrospectionException(Messages.getString("custom.beans.03"));
        }
        if (propertyName == null || propertyName.length() == 0) {
            throw new IntrospectionException(Messages.getString("custom.beans.04"));
        }
        this.setName(propertyName);
        if (getterName != null) {
            if (getterName.length() == 0) {
                throw new IntrospectionException("read or write method cannot be empty.");
            }
            try {
                this.setReadMethod(beanClass, getterName);
            }
            catch (final IntrospectionException e) {
                this.setReadMethod(beanClass, this.createDefaultMethodName(propertyName, "get"));
            }
        }
        if (setterName != null) {
            if (setterName.length() == 0) {
                throw new IntrospectionException("read or write method cannot be empty.");
            }
            this.setWriteMethod(beanClass, setterName);
        }
    }
    
    public PropertyDescriptor(final String propertyName, final Method getter, final Method setter) throws IntrospectionException {
        if (propertyName == null || propertyName.length() == 0) {
            throw new IntrospectionException(Messages.getString("custom.beans.04"));
        }
        this.setName(propertyName);
        this.setReadMethod(getter);
        this.setWriteMethod(setter);
    }
    
    public PropertyDescriptor(final String propertyName, final Class<?> beanClass) throws IntrospectionException {
        if (beanClass == null) {
            throw new IntrospectionException(Messages.getString("custom.beans.03"));
        }
        if (propertyName == null || propertyName.length() == 0) {
            throw new IntrospectionException(Messages.getString("custom.beans.04"));
        }
        this.setName(propertyName);
        try {
            this.setReadMethod(beanClass, this.createDefaultMethodName(propertyName, "is"));
        }
        catch (final Exception e) {
            this.setReadMethod(beanClass, this.createDefaultMethodName(propertyName, "get"));
        }
        this.setWriteMethod(beanClass, this.createDefaultMethodName(propertyName, "set"));
    }
    
    public void setWriteMethod(final Method setter) throws IntrospectionException {
        if (setter != null) {
            final int modifiers = setter.getModifiers();
            if (!Modifier.isPublic(modifiers)) {
                throw new IntrospectionException(Messages.getString("custom.beans.05"));
            }
            final Class<?>[] parameterTypes = setter.getParameterTypes();
            if (parameterTypes.length != 1) {
                throw new IntrospectionException(Messages.getString("custom.beans.06"));
            }
            final Class<?> parameterType = parameterTypes[0];
            final Class<?> propertyType = this.getPropertyType();
            if (propertyType != null && !propertyType.equals(parameterType)) {
                throw new IntrospectionException(Messages.getString("custom.beans.07"));
            }
        }
        this.setter = setter;
    }
    
    public void setReadMethod(final Method getter) throws IntrospectionException {
        if (getter != null) {
            final int modifiers = getter.getModifiers();
            if (!Modifier.isPublic(modifiers)) {
                throw new IntrospectionException(Messages.getString("custom.beans.0A"));
            }
            final Class<?>[] parameterTypes = getter.getParameterTypes();
            if (parameterTypes.length != 0) {
                throw new IntrospectionException(Messages.getString("custom.beans.08"));
            }
            final Class<?> returnType = getter.getReturnType();
            if (returnType.equals(Void.TYPE)) {
                throw new IntrospectionException(Messages.getString("custom.beans.33"));
            }
            final Class<?> propertyType = this.getPropertyType();
            if (propertyType != null && !returnType.equals(propertyType)) {
                throw new IntrospectionException(Messages.getString("custom.beans.09"));
            }
        }
        this.getter = getter;
    }
    
    public Method getWriteMethod() {
        return this.setter;
    }
    
    public Method getReadMethod() {
        return this.getter;
    }
    
    @Override
    public boolean equals(final Object object) {
        boolean result = object instanceof PropertyDescriptor;
        if (result) {
            final PropertyDescriptor pd = (PropertyDescriptor)object;
            final boolean gettersAreEqual = (this.getter == null && pd.getReadMethod() == null) || (this.getter != null && this.getter.equals(pd.getReadMethod()));
            final boolean settersAreEqual = (this.setter == null && pd.getWriteMethod() == null) || (this.setter != null && this.setter.equals(pd.getWriteMethod()));
            final boolean propertyTypesAreEqual = this.getPropertyType() == pd.getPropertyType();
            final boolean propertyEditorClassesAreEqual = this.getPropertyEditorClass() == pd.getPropertyEditorClass();
            final boolean boundPropertyAreEqual = this.isBound() == pd.isBound();
            final boolean constrainedPropertyAreEqual = this.isConstrained() == pd.isConstrained();
            result = (gettersAreEqual && settersAreEqual && propertyTypesAreEqual && propertyEditorClassesAreEqual && boundPropertyAreEqual && constrainedPropertyAreEqual);
        }
        return result;
    }
    
    @Override
    public int hashCode() {
        return BeansUtils.getHashCode(this.getter) + BeansUtils.getHashCode(this.setter) + BeansUtils.getHashCode(this.getPropertyType()) + BeansUtils.getHashCode(this.getPropertyEditorClass()) + BeansUtils.getHashCode(this.isBound()) + BeansUtils.getHashCode(this.isConstrained());
    }
    
    public void setPropertyEditorClass(final Class<?> propertyEditorClass) {
        this.propertyEditorClass = propertyEditorClass;
    }
    
    public Class<?> getPropertyType() {
        Class<?> result = null;
        if (this.getter != null) {
            result = this.getter.getReturnType();
        }
        else if (this.setter != null) {
            final Class<?>[] parameterTypes = this.setter.getParameterTypes();
            result = parameterTypes[0];
        }
        return result;
    }
    
    public Class<?> getPropertyEditorClass() {
        return this.propertyEditorClass;
    }
    
    public void setConstrained(final boolean constrained) {
        this.constrained = constrained;
    }
    
    public void setBound(final boolean bound) {
        this.bound = bound;
    }
    
    public boolean isConstrained() {
        return this.constrained;
    }
    
    public boolean isBound() {
        return this.bound;
    }
    
    String createDefaultMethodName(final String propertyName, final String prefix) {
        String result = null;
        if (propertyName != null) {
            final String bos = BeansUtils.toASCIIUpperCase(propertyName.substring(0, 1));
            final String eos = propertyName.substring(1, propertyName.length());
            result = prefix + bos + eos;
        }
        return result;
    }
    
    void setReadMethod(final Class<?> beanClass, final String getterName) throws IntrospectionException {
        try {
            final Method readMethod = beanClass.getMethod(getterName, (Class<?>[])new Class[0]);
            this.setReadMethod(readMethod);
        }
        catch (final Exception e) {
            throw new IntrospectionException(e.getLocalizedMessage());
        }
    }
    
    void setWriteMethod(final Class<?> beanClass, final String setterName) throws IntrospectionException {
        Method writeMethod = null;
        try {
            if (this.getter != null) {
                writeMethod = beanClass.getMethod(setterName, this.getter.getReturnType());
            }
            else {
                Class<?> clazz = beanClass;
                Method[] methods = null;
                while (clazz != null && writeMethod == null) {
                    final Method[] arr$;
                    methods = (arr$ = clazz.getDeclaredMethods());
                    for (final Method method : arr$) {
                        if (setterName.equals(method.getName()) && method.getParameterTypes().length == 1) {
                            writeMethod = method;
                            break;
                        }
                    }
                    clazz = clazz.getSuperclass();
                }
            }
        }
        catch (final Exception e) {
            throw new IntrospectionException(e.getLocalizedMessage());
        }
        if (writeMethod == null) {
            throw new IntrospectionException(Messages.getString("custom.beans.64", setterName));
        }
        this.setWriteMethod(writeMethod);
    }
    
    public PropertyEditor createPropertyEditor(final Object bean) {
        if (this.propertyEditorClass == null) {
            return null;
        }
        if (!PropertyEditor.class.isAssignableFrom(this.propertyEditorClass)) {
            throw new ClassCastException(Messages.getString("custom.beans.48"));
        }
        PropertyEditor editor;
        try {
            try {
                final Constructor<?> constr = this.propertyEditorClass.getConstructor(Object.class);
                editor = (PropertyEditor)constr.newInstance(bean);
            }
            catch (final NoSuchMethodException e) {
                final Constructor<?> constr = this.propertyEditorClass.getConstructor((Class<?>[])new Class[0]);
                editor = (PropertyEditor)constr.newInstance(new Object[0]);
            }
        }
        catch (final Exception e2) {
            final RuntimeException re = new RuntimeException(Messages.getString("custom.beans.47"), e2);
            throw re;
        }
        return editor;
    }
}
