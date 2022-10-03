package java.beans;

import java.awt.Image;
import java.lang.ref.SoftReference;
import java.lang.ref.Reference;

class GenericBeanInfo extends SimpleBeanInfo
{
    private BeanDescriptor beanDescriptor;
    private EventSetDescriptor[] events;
    private int defaultEvent;
    private PropertyDescriptor[] properties;
    private int defaultProperty;
    private MethodDescriptor[] methods;
    private Reference<BeanInfo> targetBeanInfoRef;
    
    public GenericBeanInfo(final BeanDescriptor beanDescriptor, final EventSetDescriptor[] events, final int defaultEvent, final PropertyDescriptor[] properties, final int defaultProperty, final MethodDescriptor[] methods, final BeanInfo beanInfo) {
        this.beanDescriptor = beanDescriptor;
        this.events = events;
        this.defaultEvent = defaultEvent;
        this.properties = properties;
        this.defaultProperty = defaultProperty;
        this.methods = methods;
        this.targetBeanInfoRef = ((beanInfo != null) ? new SoftReference<BeanInfo>(beanInfo) : null);
    }
    
    GenericBeanInfo(final GenericBeanInfo genericBeanInfo) {
        this.beanDescriptor = new BeanDescriptor(genericBeanInfo.beanDescriptor);
        if (genericBeanInfo.events != null) {
            final int length = genericBeanInfo.events.length;
            this.events = new EventSetDescriptor[length];
            for (int i = 0; i < length; ++i) {
                this.events[i] = new EventSetDescriptor(genericBeanInfo.events[i]);
            }
        }
        this.defaultEvent = genericBeanInfo.defaultEvent;
        if (genericBeanInfo.properties != null) {
            final int length2 = genericBeanInfo.properties.length;
            this.properties = new PropertyDescriptor[length2];
            for (int j = 0; j < length2; ++j) {
                final PropertyDescriptor propertyDescriptor = genericBeanInfo.properties[j];
                if (propertyDescriptor instanceof IndexedPropertyDescriptor) {
                    this.properties[j] = new IndexedPropertyDescriptor((IndexedPropertyDescriptor)propertyDescriptor);
                }
                else {
                    this.properties[j] = new PropertyDescriptor(propertyDescriptor);
                }
            }
        }
        this.defaultProperty = genericBeanInfo.defaultProperty;
        if (genericBeanInfo.methods != null) {
            final int length3 = genericBeanInfo.methods.length;
            this.methods = new MethodDescriptor[length3];
            for (int k = 0; k < length3; ++k) {
                this.methods[k] = new MethodDescriptor(genericBeanInfo.methods[k]);
            }
        }
        this.targetBeanInfoRef = genericBeanInfo.targetBeanInfoRef;
    }
    
    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        return this.properties;
    }
    
    @Override
    public int getDefaultPropertyIndex() {
        return this.defaultProperty;
    }
    
    @Override
    public EventSetDescriptor[] getEventSetDescriptors() {
        return this.events;
    }
    
    @Override
    public int getDefaultEventIndex() {
        return this.defaultEvent;
    }
    
    @Override
    public MethodDescriptor[] getMethodDescriptors() {
        return this.methods;
    }
    
    @Override
    public BeanDescriptor getBeanDescriptor() {
        return this.beanDescriptor;
    }
    
    @Override
    public Image getIcon(final int n) {
        final BeanInfo targetBeanInfo = this.getTargetBeanInfo();
        if (targetBeanInfo != null) {
            return targetBeanInfo.getIcon(n);
        }
        return super.getIcon(n);
    }
    
    private BeanInfo getTargetBeanInfo() {
        if (this.targetBeanInfoRef == null) {
            return null;
        }
        BeanInfo beanInfo = this.targetBeanInfoRef.get();
        if (beanInfo == null) {
            beanInfo = ThreadGroupContext.getContext().getBeanInfoFinder().find(this.beanDescriptor.getBeanClass());
            if (beanInfo != null) {
                this.targetBeanInfoRef = new SoftReference<BeanInfo>(beanInfo);
            }
        }
        return beanInfo;
    }
}
