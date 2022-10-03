package org.msgpack.template.builder.beans;

import org.apache.harmony.beans.BeansUtils;
import org.apache.harmony.beans.internal.nls.Messages;
import java.lang.reflect.Method;

public class IndexedPropertyDescriptor extends PropertyDescriptor
{
    private Class<?> indexedPropertyType;
    private Method indexedGetter;
    private Method indexedSetter;
    
    public IndexedPropertyDescriptor(final String propertyName, final Class<?> beanClass, final String getterName, final String setterName, final String indexedGetterName, final String indexedSetterName) throws IntrospectionException {
        super(propertyName, beanClass, getterName, setterName);
        this.setIndexedByName(beanClass, indexedGetterName, indexedSetterName);
    }
    
    private void setIndexedByName(final Class<?> beanClass, final String indexedGetterName, final String indexedSetterName) throws IntrospectionException {
        String theIndexedGetterName = indexedGetterName;
        if (theIndexedGetterName == null) {
            if (indexedSetterName != null) {
                this.setIndexedWriteMethod(beanClass, indexedSetterName);
            }
        }
        else {
            if (theIndexedGetterName.length() == 0) {
                theIndexedGetterName = "get" + this.name;
            }
            this.setIndexedReadMethod(beanClass, theIndexedGetterName);
            if (indexedSetterName != null) {
                this.setIndexedWriteMethod(beanClass, indexedSetterName, this.indexedPropertyType);
            }
        }
        if (!this.isCompatible()) {
            throw new IntrospectionException(Messages.getString("custom.beans.57"));
        }
    }
    
    private boolean isCompatible() {
        final Class<?> propertyType = this.getPropertyType();
        if (propertyType == null) {
            return true;
        }
        final Class<?> componentTypeOfProperty = propertyType.getComponentType();
        return componentTypeOfProperty != null && this.indexedPropertyType != null && componentTypeOfProperty.getName().equals(this.indexedPropertyType.getName());
    }
    
    public IndexedPropertyDescriptor(final String propertyName, final Method getter, final Method setter, final Method indexedGetter, final Method indexedSetter) throws IntrospectionException {
        super(propertyName, getter, setter);
        if (indexedGetter != null) {
            this.internalSetIndexedReadMethod(indexedGetter);
            this.internalSetIndexedWriteMethod(indexedSetter, true);
        }
        else {
            this.internalSetIndexedWriteMethod(indexedSetter, true);
            this.internalSetIndexedReadMethod(indexedGetter);
        }
        if (!this.isCompatible()) {
            throw new IntrospectionException(Messages.getString("custom.beans.57"));
        }
    }
    
    public IndexedPropertyDescriptor(final String propertyName, final Class<?> beanClass) throws IntrospectionException {
        super(propertyName, beanClass);
        this.setIndexedByName(beanClass, "get".concat(initialUpperCase(propertyName)), "set".concat(initialUpperCase(propertyName)));
    }
    
    public void setIndexedReadMethod(final Method indexedGetter) throws IntrospectionException {
        this.internalSetIndexedReadMethod(indexedGetter);
    }
    
    public void setIndexedWriteMethod(final Method indexedSetter) throws IntrospectionException {
        this.internalSetIndexedWriteMethod(indexedSetter, false);
    }
    
    public Method getIndexedWriteMethod() {
        return this.indexedSetter;
    }
    
    public Method getIndexedReadMethod() {
        return this.indexedGetter;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof IndexedPropertyDescriptor)) {
            return false;
        }
        final IndexedPropertyDescriptor other = (IndexedPropertyDescriptor)obj;
        if (super.equals(other)) {
            if (this.indexedPropertyType == null) {
                if (other.indexedPropertyType != null) {
                    return false;
                }
            }
            else if (!this.indexedPropertyType.equals(other.indexedPropertyType)) {
                return false;
            }
            if (this.indexedGetter == null) {
                if (other.indexedGetter != null) {
                    return false;
                }
            }
            else if (!this.indexedGetter.equals(other.indexedGetter)) {
                return false;
            }
            if ((this.indexedSetter != null) ? this.indexedSetter.equals(other.indexedSetter) : (other.indexedSetter == null)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() + BeansUtils.getHashCode(this.indexedPropertyType) + BeansUtils.getHashCode(this.indexedGetter) + BeansUtils.getHashCode(this.indexedSetter);
    }
    
    public Class<?> getIndexedPropertyType() {
        return this.indexedPropertyType;
    }
    
    private void setIndexedReadMethod(final Class<?> beanClass, final String indexedGetterName) throws IntrospectionException {
        Method getter;
        try {
            getter = beanClass.getMethod(indexedGetterName, Integer.TYPE);
        }
        catch (final NoSuchMethodException exception) {
            throw new IntrospectionException(Messages.getString("custom.beans.58"));
        }
        catch (final SecurityException exception2) {
            throw new IntrospectionException(Messages.getString("custom.beans.59"));
        }
        this.internalSetIndexedReadMethod(getter);
    }
    
    private void internalSetIndexedReadMethod(final Method indexGetter) throws IntrospectionException {
        if (indexGetter == null) {
            if (this.indexedSetter == null) {
                if (this.getPropertyType() != null) {
                    throw new IntrospectionException(Messages.getString("custom.beans.5A"));
                }
                this.indexedPropertyType = null;
            }
            this.indexedGetter = null;
            return;
        }
        if (indexGetter.getParameterTypes().length != 1 || indexGetter.getParameterTypes()[0] != Integer.TYPE) {
            throw new IntrospectionException(Messages.getString("custom.beans.5B"));
        }
        final Class<?> indexedReadType = indexGetter.getReturnType();
        if (indexedReadType == Void.TYPE) {
            throw new IntrospectionException(Messages.getString("custom.beans.5B"));
        }
        if (this.indexedSetter != null && indexGetter.getReturnType() != this.indexedSetter.getParameterTypes()[1]) {
            throw new IntrospectionException(Messages.getString("custom.beans.5A"));
        }
        if (this.indexedGetter == null) {
            this.indexedPropertyType = indexedReadType;
        }
        else if (this.indexedPropertyType != indexedReadType) {
            throw new IntrospectionException(Messages.getString("custom.beans.5A"));
        }
        this.indexedGetter = indexGetter;
    }
    
    private void setIndexedWriteMethod(final Class<?> beanClass, final String indexedSetterName) throws IntrospectionException {
        Method setter = null;
        try {
            setter = beanClass.getMethod(indexedSetterName, Integer.TYPE, this.getPropertyType().getComponentType());
        }
        catch (final SecurityException e) {
            throw new IntrospectionException(Messages.getString("custom.beans.5C"));
        }
        catch (final NoSuchMethodException e2) {
            throw new IntrospectionException(Messages.getString("custom.beans.5D"));
        }
        this.internalSetIndexedWriteMethod(setter, true);
    }
    
    private void setIndexedWriteMethod(final Class<?> beanClass, final String indexedSetterName, final Class<?> argType) throws IntrospectionException {
        try {
            final Method setter = beanClass.getMethod(indexedSetterName, Integer.TYPE, argType);
            this.internalSetIndexedWriteMethod(setter, true);
        }
        catch (final NoSuchMethodException exception) {
            throw new IntrospectionException(Messages.getString("custom.beans.5D"));
        }
        catch (final SecurityException exception2) {
            throw new IntrospectionException(Messages.getString("custom.beans.5C"));
        }
    }
    
    private void internalSetIndexedWriteMethod(final Method indexSetter, final boolean initialize) throws IntrospectionException {
        if (indexSetter == null) {
            if (this.indexedGetter == null) {
                if (this.getPropertyType() != null) {
                    throw new IntrospectionException(Messages.getString("custom.beans.5E"));
                }
                this.indexedPropertyType = null;
            }
            this.indexedSetter = null;
            return;
        }
        final Class<?>[] indexedSetterArgs = indexSetter.getParameterTypes();
        if (indexedSetterArgs.length != 2) {
            throw new IntrospectionException(Messages.getString("custom.beans.5F"));
        }
        if (indexedSetterArgs[0] != Integer.TYPE) {
            throw new IntrospectionException(Messages.getString("custom.beans.60"));
        }
        final Class<?> indexedWriteType = indexedSetterArgs[1];
        if (initialize && this.indexedGetter == null) {
            this.indexedPropertyType = indexedWriteType;
        }
        else if (this.indexedPropertyType != indexedWriteType) {
            throw new IntrospectionException(Messages.getString("custom.beans.61"));
        }
        this.indexedSetter = indexSetter;
    }
    
    private static String initialUpperCase(final String string) {
        if (Character.isUpperCase(string.charAt(0))) {
            return string;
        }
        final String initial = string.substring(0, 1).toUpperCase();
        return initial.concat(string.substring(1));
    }
}
