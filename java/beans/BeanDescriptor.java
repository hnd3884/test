package java.beans;

import java.lang.ref.Reference;

public class BeanDescriptor extends FeatureDescriptor
{
    private Reference<? extends Class<?>> beanClassRef;
    private Reference<? extends Class<?>> customizerClassRef;
    
    public BeanDescriptor(final Class<?> clazz) {
        this(clazz, null);
    }
    
    public BeanDescriptor(final Class<?> clazz, final Class<?> clazz2) {
        this.beanClassRef = FeatureDescriptor.getWeakReference(clazz);
        this.customizerClassRef = FeatureDescriptor.getWeakReference(clazz2);
        String name;
        for (name = clazz.getName(); name.indexOf(46) >= 0; name = name.substring(name.indexOf(46) + 1)) {}
        this.setName(name);
    }
    
    public Class<?> getBeanClass() {
        return (this.beanClassRef != null) ? ((Class)this.beanClassRef.get()) : null;
    }
    
    public Class<?> getCustomizerClass() {
        return (this.customizerClassRef != null) ? ((Class)this.customizerClassRef.get()) : null;
    }
    
    BeanDescriptor(final BeanDescriptor beanDescriptor) {
        super(beanDescriptor);
        this.beanClassRef = beanDescriptor.beanClassRef;
        this.customizerClassRef = beanDescriptor.customizerClassRef;
    }
    
    @Override
    void appendTo(final StringBuilder sb) {
        FeatureDescriptor.appendTo(sb, "beanClass", this.beanClassRef);
        FeatureDescriptor.appendTo(sb, "customizerClass", this.customizerClassRef);
    }
}
