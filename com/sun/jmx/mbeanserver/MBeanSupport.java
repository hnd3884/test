package com.sun.jmx.mbeanserver;

import java.util.Iterator;
import javax.management.InvalidAttributeValueException;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.ReflectionException;
import javax.management.MBeanException;
import javax.management.AttributeNotFoundException;
import javax.management.ObjectName;
import javax.management.MBeanServer;
import sun.reflect.misc.ReflectUtil;
import javax.management.NotCompliantMBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanRegistration;

public abstract class MBeanSupport<M> implements DynamicMBean2, MBeanRegistration
{
    private final MBeanInfo mbeanInfo;
    private final Object resource;
    private final PerInterface<M> perInterface;
    
     <T> MBeanSupport(final T resource, final Class<T> clazz) throws NotCompliantMBeanException {
        if (clazz == null) {
            throw new NotCompliantMBeanException("Null MBean interface");
        }
        if (!clazz.isInstance(resource)) {
            throw new NotCompliantMBeanException("Resource class " + resource.getClass().getName() + " is not an instance of " + clazz.getName());
        }
        ReflectUtil.checkPackageAccess(clazz);
        this.resource = resource;
        final MBeanIntrospector<M> mBeanIntrospector = this.getMBeanIntrospector();
        this.perInterface = mBeanIntrospector.getPerInterface(clazz);
        this.mbeanInfo = mBeanIntrospector.getMBeanInfo(resource, this.perInterface);
    }
    
    abstract MBeanIntrospector<M> getMBeanIntrospector();
    
    abstract Object getCookie();
    
    public final boolean isMXBean() {
        return this.perInterface.isMXBean();
    }
    
    public abstract void register(final MBeanServer p0, final ObjectName p1) throws Exception;
    
    public abstract void unregister();
    
    @Override
    public final ObjectName preRegister(final MBeanServer mBeanServer, ObjectName preRegister) throws Exception {
        if (this.resource instanceof MBeanRegistration) {
            preRegister = ((MBeanRegistration)this.resource).preRegister(mBeanServer, preRegister);
        }
        return preRegister;
    }
    
    @Override
    public final void preRegister2(final MBeanServer mBeanServer, final ObjectName objectName) throws Exception {
        this.register(mBeanServer, objectName);
    }
    
    @Override
    public final void registerFailed() {
        this.unregister();
    }
    
    @Override
    public final void postRegister(final Boolean b) {
        if (this.resource instanceof MBeanRegistration) {
            ((MBeanRegistration)this.resource).postRegister(b);
        }
    }
    
    @Override
    public final void preDeregister() throws Exception {
        if (this.resource instanceof MBeanRegistration) {
            ((MBeanRegistration)this.resource).preDeregister();
        }
    }
    
    @Override
    public final void postDeregister() {
        try {
            this.unregister();
        }
        finally {
            if (this.resource instanceof MBeanRegistration) {
                ((MBeanRegistration)this.resource).postDeregister();
            }
        }
    }
    
    @Override
    public final Object getAttribute(final String s) throws AttributeNotFoundException, MBeanException, ReflectionException {
        return this.perInterface.getAttribute(this.resource, s, this.getCookie());
    }
    
    @Override
    public final AttributeList getAttributes(final String[] array) {
        final AttributeList list = new AttributeList(array.length);
        for (final String s : array) {
            try {
                list.add(new Attribute(s, this.getAttribute(s)));
            }
            catch (final Exception ex) {}
        }
        return list;
    }
    
    @Override
    public final void setAttribute(final Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        this.perInterface.setAttribute(this.resource, attribute.getName(), attribute.getValue(), this.getCookie());
    }
    
    @Override
    public final AttributeList setAttributes(final AttributeList list) {
        final AttributeList list2 = new AttributeList(list.size());
        for (final Attribute attribute : list) {
            try {
                this.setAttribute(attribute);
                list2.add(new Attribute(attribute.getName(), attribute.getValue()));
            }
            catch (final Exception ex) {}
        }
        return list2;
    }
    
    @Override
    public final Object invoke(final String s, final Object[] array, final String[] array2) throws MBeanException, ReflectionException {
        return this.perInterface.invoke(this.resource, s, array, array2, this.getCookie());
    }
    
    @Override
    public MBeanInfo getMBeanInfo() {
        return this.mbeanInfo;
    }
    
    @Override
    public final String getClassName() {
        return this.resource.getClass().getName();
    }
    
    @Override
    public final Object getResource() {
        return this.resource;
    }
    
    public final Class<?> getMBeanInterface() {
        return this.perInterface.getMBeanInterface();
    }
}
