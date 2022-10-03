package org.msgpack.template.builder.beans;

public class BeanDescriptor extends FeatureDescriptor
{
    private Class<?> beanClass;
    private Class<?> customizerClass;
    
    public BeanDescriptor(final Class<?> beanClass, final Class<?> customizerClass) {
        if (beanClass == null) {
            throw new NullPointerException();
        }
        this.setName(this.getShortClassName(beanClass));
        this.beanClass = beanClass;
        this.customizerClass = customizerClass;
    }
    
    public BeanDescriptor(final Class<?> beanClass) {
        this(beanClass, null);
    }
    
    public Class<?> getCustomizerClass() {
        return this.customizerClass;
    }
    
    public Class<?> getBeanClass() {
        return this.beanClass;
    }
    
    private String getShortClassName(final Class<?> leguminaClass) {
        if (leguminaClass == null) {
            return null;
        }
        final String beanClassName = leguminaClass.getName();
        final int lastIndex = beanClassName.lastIndexOf(".");
        return (lastIndex == -1) ? beanClassName : beanClassName.substring(lastIndex + 1);
    }
}
